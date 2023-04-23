package org.smf.healthsystem.patientservice.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.smf.healthsystem.patientservice.modal.PersistingPatient
import org.smf.healthsystem.patientservice.repository.PatientReactiveRepository
import org.smf.healthsystem.patientservice.services.PatientService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate

@Configuration
class ApplicationConfig {

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        val objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        return objectMapper
    }

    @Bean
    fun patientService(
        template: ReactiveKafkaProducerTemplate<String, PersistingPatient>,
        repository: PatientReactiveRepository,
    ) = PatientService(template, repository)
}
