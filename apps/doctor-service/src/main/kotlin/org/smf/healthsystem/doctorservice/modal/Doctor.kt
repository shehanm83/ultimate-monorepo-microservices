package org.smf.healthsystem.doctorservice.modal

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class Doctor(
    val id: String,
    val firstName: String,
    val lastName: String,
    @JsonFormat(pattern = "yyyy-MM-dd") val dateOfBirth: LocalDate,
    val address: String,
    val phoneNumber: String,
    val email: String,
)
