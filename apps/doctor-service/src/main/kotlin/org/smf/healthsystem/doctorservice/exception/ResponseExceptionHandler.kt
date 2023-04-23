package org.smf.healthsystem.doctorservice.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ResponseExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [DoctorNotExists::class])
    protected fun handleNotFound(ex: DoctorNotExists): ResponseEntity<Any> =
        ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
}
