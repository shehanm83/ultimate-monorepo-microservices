package org.smf.healthsystem.doctorservice.rest

import org.smf.healthsystem.doctorservice.modal.CreatedDoctor
import org.smf.healthsystem.doctorservice.modal.Doctor
import org.smf.healthsystem.doctorservice.services.DoctorService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/doctors")
class DoctorController(val doctorService: DoctorService) {

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    fun createDoctor(@RequestBody doctor: Doctor): Mono<CreatedDoctor> {
        return doctorService.createDoctor(doctor)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getDoctorById(@PathVariable("id") id: String): Mono<Doctor> {
        return doctorService.getDoctorById(id)
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateDoctor(@PathVariable("id") id: String, @RequestBody doctor: Doctor): Mono<CreatedDoctor> {
        return doctorService.updateDoctor(id, doctor)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun deleteDoctor(@PathVariable("id") id: String): Mono<Void> {
        return doctorService.inactiveDoctor(id)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllDoctors(): Flux<Doctor> {
        return doctorService.getAllDoctors()
    }

    @GetMapping("activate/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun activateDoctor(@PathVariable("id") id: String): Mono<Void> {
        return doctorService.activeDoctor(id)
    }
}
