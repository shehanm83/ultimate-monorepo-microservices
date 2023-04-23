package org.smf.healthsystem.patientservice.modal

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class Patient(
    val id: String,
    val firstName: String,
    val lastName: String,
    @JsonFormat(pattern = "yyyy-MM-dd") val dateOfBirth: LocalDate,
    val address: String,
    val phoneNumber: String,
    val email: String,
)
