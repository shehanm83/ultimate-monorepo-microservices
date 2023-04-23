package org.smf.healthsystem.patientservice.configuration

import org.smf.healthsystem.patientservice.modal.PersistingPatient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaConfig {

    @Bean
    fun kafkaReceiverOptions(
        @Value(value = "\${PATIENT_TOPIC}") topic: String,
        kafkaProperties: KafkaProperties,
    ): ReceiverOptions<String, PersistingPatient> {
        val basicReceiverOptions: ReceiverOptions<String, PersistingPatient> =
            ReceiverOptions.create(kafkaProperties.buildConsumerProperties())
        return basicReceiverOptions.subscription(listOf(topic))
    }

    @Bean
    fun reactiveKafkaConsumerTemplate(kafkaReceiverOptions: ReceiverOptions<String, PersistingPatient>):
        ReactiveKafkaConsumerTemplate<String, PersistingPatient> {
        return ReactiveKafkaConsumerTemplate(kafkaReceiverOptions)
    }

    @Bean
    fun reactiveKafkaProducerTemplate(
        properties: KafkaProperties,
    ): ReactiveKafkaProducerTemplate<String, PersistingPatient> {
        val props = properties.buildProducerProperties()
        return ReactiveKafkaProducerTemplate(SenderOptions.create(props))
    }
}
