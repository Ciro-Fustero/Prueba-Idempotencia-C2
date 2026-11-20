package es.unizar.webeng.lab3.analytics

// Autor: Ciro Fustero Zumeta (NIP: 900506)

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.Instant

/**
 * Definición de un clic tal como viaja desde `links` hacia `analytics`.
 *
 * clickId se crea una sola vez, cuando se sirve la redirección. Una reentrega del
 * mismo clic lleva el mismo id, que es lo que permite al consumidor reconocerlo e
 * ignorarlo.
 */
data class ClickEvent(
    val clickId: String,
    val code: String,
)

/**
 * Ids de los clics ya contabilizados. Se escribe en la misma transacción que la
 * actualización del contador, y su clave primaria rechaza una segunda fila con el mismo id.
 */
@Entity
class ClickSeen(
    @Id
    var clickId: String,
    var seenAt: Instant,
)

/** Número de clics contabilizados para un código corto. */
@Entity
class LinkCounter(
    @Id
    var code: String,
    var clicks: Long,
)
