package es.unizar.webeng.lab3.analytics

// Autor: Ciro Fustero Zumeta (NIP: 900506)

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant

/**
 * Consumidor idempotente de clics.
 *
 * Un bróker con entrega "al menos una vez" (at-least-once) puede entregar el mismo clic dos veces, por
 * ejemplo cuando el consumidor se reinicia antes de confirmar la recepción. El consumidor
 * almacena el id del clic y la actualización del contador en una misma transacción local, de modo que una
 * reentrega encuentra el id y no vuelve a contar.
 *
 * Los ids se olvidan tras la limpieza de los clics para ahorrar espacio. Una reentrega que llegue
 * después de ese tiempo ya no se reconoce y se cuenta una segunda vez.
 */
@Service
class ClickConsumer(
    private val seen: ClickSeenRepository,
    private val counters: LinkCounterRepository,
) {
    /** Devuelve true cuando el clic se contabilizó, false cuando ya existía. */
    @Transactional
    fun handle(event: ClickEvent): Boolean {
        if (seen.existsById(event.clickId)) return false
        seen.save(ClickSeen(event.clickId, Instant.now()))
        if (counters.increment(event.code) == 0) {
            counters.save(LinkCounter(event.code, 1))
        }
        return true
    }

    /** Olvida los ids más antiguos. Devuelve cuántos se eliminaron. */
    @Transactional
    fun purgeExpired(now: Instant): Int = seen.deleteOlderThan(now.minus(RETENTION))

    @Transactional(readOnly = true)
    fun count(code: String): Long = counters.findById(code).map { it.clicks }.orElse(0L)

    companion object {
        val RETENTION: Duration = Duration.ofMinutes(10)
    }
}
