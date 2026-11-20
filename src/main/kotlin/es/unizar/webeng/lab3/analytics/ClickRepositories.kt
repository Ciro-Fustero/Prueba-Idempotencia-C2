package es.unizar.webeng.lab3.analytics

// Autor: Ciro Fustero Zumeta (NIP: 900506)

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ClickSeenRepository : CrudRepository<ClickSeen, String> {
    /** Olvida los ids registrados antes de "cutoff". Devuelve cuántos se eliminaron. */
    @Modifying
    @Query("delete from ClickSeen c where c.seenAt < :cutoff")
    fun deleteOlderThan(
        @Param("cutoff") cutoff: Instant,
    ): Int
}

@Repository
interface LinkCounterRepository : CrudRepository<LinkCounter, String> {
    /** Suma un clic en una sola sentencia. Devuelve 0 cuando el código aún no tiene contador. */
    @Modifying
    @Query("update LinkCounter c set c.clicks = c.clicks + 1 where c.code = :code")
    fun increment(
        @Param("code") code: String,
    ): Int
}
