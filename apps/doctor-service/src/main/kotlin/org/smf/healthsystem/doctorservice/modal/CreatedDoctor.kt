package org.smf.healthsystem.doctorservice.modal

import java.time.LocalDateTime

data class CreatedDoctor(
    val id: String,
    val created: LocalDateTime,
    val savedID: String,

)
