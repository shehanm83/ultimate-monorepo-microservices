package org.smf.healthsystem.patientservice.services

import org.smf.healthsystem.patientservice.exception.PatientAlreadyExists
import org.smf.healthsystem.patientservice.exception.PatientNotExists
import org.smf.healthsystem.patientservice.modal.CreatedPatient
import org.smf.healthsystem.patientservice.modal.Patient
import org.smf.healthsystem.patientservice.modal.PersistenceState
import org.smf.healthsystem.patientservice.modal.PersistingPatient
import org.smf.healthsystem.patientservice.repository.PatientReactiveRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.util.Assert
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime

class PatientService(
    private val producerTemplate: ReactiveKafkaProducerTemplate<String, PersistingPatient>,
    private val repository: PatientReactiveRepository,
) {

    @Value(value = "\${PATIENT_TOPIC}")
    lateinit var topic: String

    fun createPatient(patient: Patient): Mono<CreatedPatient> {
        return repository.existsById(patient.id).doOnNext {
            if (it) {
                throw PatientAlreadyExists(patient.id)
            }
        }.flatMap {
            val persistingPatient = toPersistingPatient(patient, PersistenceState.SAVE.state)
            sendToKafka(persistingPatient)
        }
    }

    fun getPatientById(id: String): Mono<Patient> {
        return repository.findById(id).map {
            toPatient(it)
        }.switchIfEmpty {
            throw PatientNotExists(id)
        }
    }

    fun updatePatient(id: String, patient: Patient): Mono<CreatedPatient> {
        Assert.state(id == patient.id, "Mismatched Id")
        return repository.existsById(patient.id).doOnNext {
            if (!it) {
                throw PatientNotExists(patient.id)
            }
        }.flatMap {
            val persistingPatient = toPersistingPatient(patient, PersistenceState.UPDATE.state)
            sendToKafka(persistingPatient)
        }
    }

    fun activePatient(id: String): Mono<Void> {
        return repository.findById(id).switchIfEmpty {
            throw PatientNotExists(id)
        }.flatMap {
            val deletePatient = it.copy(state = PersistenceState.UPDATE.state)
            sendToKafka(deletePatient)
        }.map {
            println("Activated ${it.id}")
        }.then()
    }

    fun inactivePatient(id: String): Mono<Void> {
        return repository.findById(id).switchIfEmpty {
            throw PatientNotExists(id)
        }.flatMap {
            val deletePatient = it.copy(state = PersistenceState.DELETE.state)
            sendToKafka(deletePatient)
        }.map {
            println("Inactivated ${it.id}")
        }.then()
    }

    fun getAllPatients(): Flux<Patient> {
        return repository.findAll().map {
            toPatient(it)
        }
    }

    private fun toPatient(it: PersistingPatient) =
        Patient(it.id, it.firstName, it.lastName, it.dateOfBirth, it.address, it.phoneNumber, it.email)

    private fun toPersistingPatient(patient: Patient, status: Int): PersistingPatient {
        val createdTime = LocalDateTime.now()
        return with(patient) {
            PersistingPatient(
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

    private fun sendToKafka(patient: PersistingPatient): Mono<CreatedPatient> {
        return producerTemplate.send(topic, patient.id, patient).map {
            val recordMetadata = it.recordMetadata()
            CreatedPatient(patient.id, patient.created, "${recordMetadata.partition()}#${recordMetadata.offset()}")
        }.onErrorMap {
            println(it)
            it
        }
    }
}
