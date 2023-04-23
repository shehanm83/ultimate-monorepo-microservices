package org.smf.healthsystem.patientservice.services

import jakarta.annotation.PostConstruct
import org.smf.healthsystem.patientservice.modal.PersistingPatient
import org.smf.healthsystem.patientservice.repository.PatientReactiveRepository
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import reactor.util.retry.RetryBackoffSpec
import java.time.Duration

@Service
class PatientConsumerService(
    val consumerTemplate: ReactiveKafkaConsumerTemplate<String, PersistingPatient>,
    val repository: PatientReactiveRepository,
) {

    @PostConstruct
    fun connectKafka() {
        consumerTemplate.receiveAutoAck().flatMap {
            Mono.defer {
                saveToMongo(it.value())
            }.doOnSuccess {
                println("Send to Kafka")
            }.doOnError {
                println(it)
            }.retryWhen(getRetrySpec())
        }.subscribe()
    }

    private fun getRetrySpec(): RetryBackoffSpec {
        return Retry.backoff(Long.MAX_VALUE, Duration.ofMillis(100)).maxBackoff(Duration.ofMillis(10000))
            .doBeforeRetryAsync(this::logRetry)
    }

    private fun logRetry(retrySignal: Retry.RetrySignal): Mono<Void> {
        println("Log error $retrySignal")
        return Mono.empty()
    }

    private fun saveToMongo(patient: PersistingPatient): Mono<PersistingPatient> {
        return repository.save(patient)
    }
}
