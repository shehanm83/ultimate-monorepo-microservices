package org.smf.healthsystem.doctorservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DoctorServiceApplication

fun main(args: Array<String>) {
    runApplication<DoctorServiceApplication>(*args)
}
