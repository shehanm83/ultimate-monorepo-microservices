package org.smf.healthsystem.patientservice.configuration

import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@EnableReactiveMongoRepositories
class MongoConfig : AbstractReactiveMongoConfiguration() {
    override fun getDatabaseName(): String {
        TODO("Not yet implemented")
    }
}
