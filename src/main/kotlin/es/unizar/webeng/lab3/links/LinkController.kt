package es.unizar.webeng.lab3.links

// Autor: Ciro Fustero Zumeta (NIP: 900506)

import es.unizar.webeng.lab3.analytics.ClickConsumer
import es.unizar.webeng.lab3.analytics.ClickEvent
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

/** Cómo "links" entrega un clic a "analytics". */
fun interface ClickPublisher {
    fun publish(event: ClickEvent)
}

/**
 * Sustituto del bróker: llama al consumidor directamente, en el mismo proceso.
 * Con RabbitMQ, aquí es donde se publicaría el mensaje y el consumidor
 * lo recibiría con entrega "al menos una vez" (at-least-once).
 */
@Component
class InProcessClickPublisher(
    private val consumer: ClickConsumer,
) : ClickPublisher {
    override fun publish(event: ClickEvent) {
        consumer.handle(event)
    }
}

/** Redirección pública. Cada petición es un clic nuevo con su propio id. */
@RestController
class LinkController(
    private val publisher: ClickPublisher,
) {
    private val targets = mapOf("abc" to "https://example.com/abc")

    @GetMapping("/r/{code}")
    fun redirect(
        @PathVariable code: String,
    ): ResponseEntity<Void> {
        val target = targets[code] ?: return ResponseEntity.notFound().build()
        publisher.publish(ClickEvent(UUID.randomUUID().toString(), code))
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(target)).build()
    }
}
