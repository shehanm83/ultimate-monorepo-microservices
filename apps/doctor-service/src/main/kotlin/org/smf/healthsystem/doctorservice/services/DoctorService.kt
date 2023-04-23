package org.smf.healthsystem.doctorservice.services

import org.smf.healthsystem.doctorservice.exception.DoctorAlreadyExists
import org.smf.healthsystem.doctorservice.exception.DoctorNotExists
import org.smf.healthsystem.doctorservice.modal.CreatedDoctor
import org.smf.healthsystem.doctorservice.modal.Doctor
import org.smf.healthsystem.doctorservice.modal.PersistenceState
import org.smf.healthsystem.doctorservice.modal.PersistingDoctor
import org.smf.healthsystem.doctorservice.repository.DoctorReactiveRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.util.Assert
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime

class DoctorService(
    private val producerTemplate: ReactiveKafkaProducerTemplate<String, PersistingDoctor>,
    private val repository: DoctorReactiveRepository,
) {

    @Value(value = "\${DOCTOR_TOPIC}")
    lateinit var topic: String

    fun createDoctor(doctor: Doctor): Mono<CreatedDoctor> {
        return repository.existsById(doctor.id).doOnNext {
            if (it) {
                throw DoctorAlreadyExists(doctor.id)
            }
        }.flatMap {
            val persistingDoctor = toPersistingDoctor(doctor, PersistenceState.SAVE.state)
            sendToKafka(persistingDoctor)
        }
    }

    fun getDoctorById(id: String): Mono<Doctor> {
        return repository.findById(id).map {
            toDoctor(it)
        }.switchIfEmpty {
            throw DoctorNotExists(id)
        }
    }

    fun updateDoctor(id: String, doctor: Doctor): Mono<CreatedDoctor> {
        Assert.state(id == doctor.id, "Mismatched Id")
        return repository.existsById(doctor.id).doOnNext {
            if (!it) {
                throw DoctorNotExists(doctor.id)
            }
        }.flatMap {
            val persistingDoctor = toPersistingDoctor(doctor, PersistenceState.UPDATE.state)
            sendToKafka(persistingDoctor)
        }
    }

    fun activeDoctor(id: String): Mono<Void> {
        return repository.findById(id).switchIfEmpty {
            throw DoctorNotExists(id)
        }.flatMap {
            val deleteDoctor = it.copy(state = PersistenceState.UPDATE.state)
            sendToKafka(deleteDoctor)
        }.map {
            println("Activated ${it.id}")
        }.then()
    }

    fun inactiveDoctor(id: String): Mono<Void> {
        return repository.findById(id).switchIfEmpty {
            throw DoctorNotExists(id)
        }.flatMap {
            val deleteDoctor = it.copy(state = PersistenceState.DELETE.state)
            sendToKafka(deleteDoctor)
        }.map {
            println("Inactivated ${it.id}")
        }.then()
    }

    fun getAllDoctors(): Flux<Doctor> {
        return repository.findAll().map {
            toDoctor(it)
        }
    }

    private fun toDoctor(it: PersistingDoctor) =
        Doctor(it.id, it.firstName, it.lastName, it.dateOfBirth, it.address, it.phoneNumber, it.email)

    private fun toPersistingDoctor(doctor: Doctor, status: Int): PersistingDoctor {
        val createdTime = LocalDateTime.now()
        return with(doctor) {
            PersistingDoctor(
                id,
                firstName,
                lastName,
                dateOfBirth,
                address,
                phoneNumber,
                email,
                createdTime,
                status,
            )
        }
    }

    private fun sendToKafka(doctor: PersistingDoctor): Mono<CreatedDoctor> {
        return producerTemplate.send(topic, doctor.id, doctor).map {
            val recordMetadata = it.recordMetadata()
            CreatedDoctor(doctor.id, doctor.created, "${recordMetadata.partition()}#${recordMetadata.offset()}")
        }.onErrorMap {
            println(it)
            it
        }
    }
}
