package es.unizar.webeng.lab3

// Autor: Ciro Fustero Zumeta (NIP: 900506)

import es.unizar.webeng.lab3.analytics.ClickConsumer
import es.unizar.webeng.lab3.analytics.ClickEvent
import es.unizar.webeng.lab3.analytics.ClickSeenRepository
import es.unizar.webeng.lab3.analytics.LinkCounterRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.Duration
import java.time.Instant

/**
 * Conteo idempotente de clics sobre la base de datos.
 * Una reentrega se simula entregando el mismo ClickEvent al consumidor dos veces.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ClickIdempotencyTests {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var consumer: ClickConsumer

    @Autowired
    private lateinit var seen: ClickSeenRepository

    @Autowired
    private lateinit var counters: LinkCounterRepository

    @BeforeEach
    fun cleanDatabase() {
        seen.deleteAll()
        counters.deleteAll()
    }

    @Test
    fun `una repeticion es ignorada`() {
        val click = ClickEvent("click-1", "abc")

        assertThat(consumer.handle(click)).isTrue() // primera entrega: contabilizada
        assertThat(consumer.handle(click)).isFalse() // reentrega: id encontrado, ignorada

        // Purgar en "now" no elimina nada: el id es más reciente que la ventana de retención.
        assertThat(consumer.purgeExpired(Instant.now())).isZero()
        assertThat(consumer.handle(click)).isFalse()

        // Comprobamos que solo se ha contado un clic, aunque hubo dos entregas.
        assertThat(consumer.count("abc")).isEqualTo(1L)
    }

    @Test
    fun `una reentrega después de que su id sea purgado es contada dos veces`() {
        val click = ClickEvent("click-2", "abc")

        assertThat(consumer.handle(click)).isTrue()

        // 30 minutos después ha pasado la retención de 10 minutos, así que el id se olvida.
        val later = Instant.now().plus(Duration.ofMinutes(30))
        assertThat(consumer.purgeExpired(later)).isEqualTo(1)

        // El mismo clic llega de nuevo, pero ya no se recuerda: se contabiliza una segunda vez.
        assertThat(consumer.handle(click)).isTrue()

        // Comprobamos que se han contado dos clics, aunque solo hubo uno real.
        assertThat(consumer.count("abc")).isEqualTo(2L)
    }

    @Test
    fun `dos clics diferentes en el mismo enlace son contados ambos`() {
        mvc.get("/r/abc").andExpect { status { isFound() } }
        mvc.get("/r/abc").andExpect { status { isFound() } }

        // Cada petición es un clic nuevo con su propio id, por lo que la idempotencia no lo descarta.
        assertThat(consumer.count("abc")).isEqualTo(2L)
    }
}
