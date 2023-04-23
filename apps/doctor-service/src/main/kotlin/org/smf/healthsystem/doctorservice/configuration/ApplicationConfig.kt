package org.smf.healthsystem.doctorservice.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.smf.healthsystem.doctorservice.modal.PersistingDoctor
import org.smf.healthsystem.doctorservice.repository.DoctorReactiveRepository
import org.smf.healthsystem.doctorservice.services.DoctorService
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
    fun doctorService(
        template: ReactiveKafkaProducerTemplate<String, PersistingDoctor>,
        repository: DoctorReactiveRepository,
    ) = DoctorService(template, repository)
}
