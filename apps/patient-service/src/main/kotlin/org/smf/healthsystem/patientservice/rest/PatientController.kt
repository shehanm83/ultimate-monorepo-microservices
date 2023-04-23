package org.smf.healthsystem.patientservice.rest

import org.smf.healthsystem.patientservice.modal.CreatedPatient
import org.smf.healthsystem.patientservice.modal.Patient
import org.smf.healthsystem.patientservice.services.PatientService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/patients")
class PatientController(val patientService: PatientService) {

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    fun createPatient(@RequestBody patient: Patient): Mono<CreatedPatient> {
        return patientService.createPatient(patient)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getPatientById(@PathVariable("id") id: String): Mono<Patient> {
        return patientService.getPatientById(id)
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updatePatient(@PathVariable("id") id: String, @RequestBody patient: Patient): Mono<CreatedPatient> {
        return patientService.updatePatient(id, patient)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun deletePatient(@PathVariable("id") id: String): Mono<Void> {
        return patientService.inactivePatient(id)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllPatients(): Flux<Patient> {
        return patientService.getAllPatients()
    }

    @GetMapping("activate/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun activatePatient(@PathVariable("id") id: String): Mono<Void> {
        return patientService.activePatient(id)
    }
}
