package org.smf.healthsystem.doctorservice.repository

import org.smf.healthsystem.doctorservice.modal.PersistingDoctor
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface DoctorReactiveRepository : ReactiveMongoRepository<PersistingDoctor, String> {

    fun existsByIdAndStateIn(id: String, state: List<Int>): Mono<Boolean>
}
