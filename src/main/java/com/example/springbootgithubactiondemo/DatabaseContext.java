package com.example.springbootgithubactiondemo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class DatabaseContext extends AbstractMongoClientConfiguration {

    @Autowired
    private DatabaseConfig databaseProperties;

    @Override
    protected String getDatabaseName(){
        return "sophie";
    }

    @Bean
    public MongoClient mongoClient() {
        MongoClientSettings clientSettings = MongoClientSettings.builder()
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .applyConnectionString(new ConnectionString("mongodb://" + databaseProperties.getHost() + ":" + databaseProperties.getPort()))
            .build();
        return MongoClients.create(clientSettings);
    }
}
