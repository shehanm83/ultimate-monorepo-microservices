package org.smf.healthsystem.doctorservice.configuration

import org.smf.healthsystem.doctorservice.modal.PersistingDoctor
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
        @Value(value = "\${DOCTOR_TOPIC}") topic: String,
        kafkaProperties: KafkaProperties,
    ): ReceiverOptions<String, PersistingDoctor> {
        val basicReceiverOptions: ReceiverOptions<String, PersistingDoctor> =
            ReceiverOptions.create(kafkaProperties.buildConsumerProperties())
        return basicReceiverOptions.subscription(listOf(topic))
    }

    @Bean
    fun reactiveKafkaConsumerTemplate(kafkaReceiverOptions: ReceiverOptions<String, PersistingDoctor>):
        ReactiveKafkaConsumerTemplate<String, PersistingDoctor> {
        return ReactiveKafkaConsumerTemplate(kafkaReceiverOptions)
    }

    @Bean
    fun reactiveKafkaProducerTemplate(
        properties: KafkaProperties,
    ): ReactiveKafkaProducerTemplate<String, PersistingDoctor> {
        val props = properties.buildProducerProperties()
        return ReactiveKafkaProducerTemplate(SenderOptions.create(props))
    }
}
