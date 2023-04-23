package org.smf.healthsystem.doctorservice.services

import jakarta.annotation.PostConstruct
import org.smf.healthsystem.doctorservice.modal.PersistingDoctor
import org.smf.healthsystem.doctorservice.repository.DoctorReactiveRepository
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import reactor.util.retry.RetryBackoffSpec
import java.time.Duration

@Service
class DoctorConsumerService(
    val consumerTemplate: ReactiveKafkaConsumerTemplate<String, PersistingDoctor>,
    val repository: DoctorReactiveRepository,
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

    private fun saveToMongo(doctor: PersistingDoctor): Mono<PersistingDoctor> {
        return repository.save(doctor)
    }
}
