package org.smf.healthsystem.patientservice.modal

import java.time.LocalDateTime

data class CreatedPatient(
    val id: String,
    val created: LocalDateTime,
    val savedID: String,

)
