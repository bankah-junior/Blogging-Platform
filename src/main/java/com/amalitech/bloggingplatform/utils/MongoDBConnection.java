package com.amalitech.bloggingplatform.utils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;

public class MongoDBConnection {
    public static MongoClient connect() {
        String uri = "mongodb://127.0.0.1:27017/?serverSelectionTimeoutMS=5000";
        MongoClient mongoClient;
        Logger logger = org.slf4j.LoggerFactory.getLogger(MongoDBConnection.class);
        try {
            mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyConnectionString(new ConnectionString(uri))
                            .build());
            return mongoClient;
        } catch (Exception e) {
            logger.error("An error occurred while connecting to MongoDB: {}", e.getMessage(), e);
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
