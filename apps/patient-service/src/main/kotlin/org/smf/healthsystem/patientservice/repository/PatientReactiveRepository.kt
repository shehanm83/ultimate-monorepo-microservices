package org.smf.healthsystem.patientservice.repository

import org.smf.healthsystem.patientservice.modal.PersistingPatient
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface PatientReactiveRepository : ReactiveMongoRepository<PersistingPatient, String> {

    fun existsByIdAndStateIn(id: String, state: List<Int>): Mono<Boolean>
}
