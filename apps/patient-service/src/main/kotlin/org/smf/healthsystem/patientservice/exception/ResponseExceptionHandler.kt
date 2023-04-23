package org.smf.healthsystem.patientservice.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ResponseExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [PatientNotExists::class])
    protected fun handleNotFound(ex: PatientNotExists): ResponseEntity<Any> =
        ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
}
