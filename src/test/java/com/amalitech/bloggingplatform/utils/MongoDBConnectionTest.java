package com.amalitech.bloggingplatform.utils;

import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MongoDBConnectionTest {

    @Test
    void getDatabase() {
        // This test will attempt to connect to a MongoDB instance at mongodb://127.0.0.1:27017.
        // If the database is not running, the test will fail, and because of System.exit(1) in the
        // MongoDBConnection class, it might terminate the test process.
        // This is an integration test, not a unit test.
        // For a real-world scenario, consider using an in-memory MongoDB (like de.flapdoodle.embed.mongo)
        // for testing or refactoring the MongoDBConnection class to be more testable.
        MongoDatabase database = MongoDBConnection.getDatabase();
        assertNotNull(database, "The database connection should not be null.");
    }
}
