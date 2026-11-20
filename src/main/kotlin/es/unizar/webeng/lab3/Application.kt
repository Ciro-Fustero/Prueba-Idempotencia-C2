package es.unizar.webeng.lab3

// Autor: Ciro Fustero Zumeta (NIP: 900506)

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(vararg args: String) {
    runApplication<Application>(*args)
}
