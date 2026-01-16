package com.amalitech.bloggingplatform.utils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDBConnection {

    private static final Logger logger = LoggerFactory.getLogger(MongoDBConnection.class);
    private static final String DATABASE_NAME = "java-demo";
    private static final String URI = "mongodb://127.0.0.1:27017/?serverSelectionTimeoutMS=5000";

    private static final MongoClient mongoClient;
    private static final MongoDatabase database;

    static {
        MongoClient client = null;
        try {
            client = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyConnectionString(new ConnectionString(URI))
                            .build());
            database = client.getDatabase(DATABASE_NAME);
            logger.info("Successfully connected to MongoDB and got database '{}'.", DATABASE_NAME);
        } catch (Exception e) {
            logger.error("Failed to connect to MongoDB: {}", e.getMessage(), e);
            // Exit if we can't connect to the database, as the app is unusable.
            System.exit(1);
            throw new RuntimeException("Failed to initialize MongoDB connection", e); // Will not be reached but good practice
        }
        mongoClient = client;

        // Add shutdown hook to close the connection gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (mongoClient != null) {
                mongoClient.close();
                logger.info("MongoDB connection closed.");
            }
        }));
    }

    /**
     * Gets the singleton instance of the MongoDatabase.
     *
     * @return the application's MongoDatabase instance.
     */
    public static MongoDatabase getDatabase() {
        return database;
    }

    /**
     * This class is a utility class and should not be instantiated.
     */
    private MongoDBConnection() {}
}