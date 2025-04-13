package com.personal.project.jobportal.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.connection.max-size:50}")
    private int maxConnections;

    @Value("${spring.data.mongodb.connection.min-size:10}")
    private int minConnections;

    @Value("${spring.data.mongodb.connection.timeout:3000}")
    private int connectionTimeout;

    @Value("${spring.data.mongodb.connection.wait-queue-timeout:1000}")
    private int waitQueueTimeout;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .applyToConnectionPoolSettings(builder -> 
                    builder
                        .maxSize(maxConnections)
                        .minSize(minConnections)
                        .maxWaitTime(waitQueueTimeout, java.util.concurrent.TimeUnit.MILLISECONDS)
                )
                .applyToSocketSettings(builder -> 
                    builder.connectTimeout(connectionTimeout, java.util.concurrent.TimeUnit.MILLISECONDS)
                )
                .build();

        return MongoClients.create(settings);
    }
}
