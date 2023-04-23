package org.smf.healthsystem.patientservice.modal

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime

@Document
data class PersistingPatient(
    @Id
    val id: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: LocalDate,
    val address: String,
    val phoneNumber: String,
    val email: String,
    val created: LocalDateTime,
    val state: Int,
)

enum class PersistenceState(val state: Int) {
    SAVE(1), UPDATE(2), DELETE(3)
}
