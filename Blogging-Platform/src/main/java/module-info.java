module com.amalitech.bloggingplatform {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.mongodb.driver.sync.client;
    requires org.slf4j;
    requires org.mongodb.driver.core;
    requires jbcrypt;
    requires org.mongodb.bson;
    requires javafx.graphics;
    requires com.google.gson;


    opens com.amalitech.bloggingplatform to javafx.fxml, com.google.gson;
    exports com.amalitech.bloggingplatform;
    exports com.amalitech.bloggingplatform.controller;
    opens com.amalitech.bloggingplatform.controller to javafx.fxml;
    opens com.amalitech.bloggingplatform.model.dto.request to com.google.gson;
    opens com.amalitech.bloggingplatform.model.dto.response to com.google.gson;
    opens com.amalitech.bloggingplatform.model.entity to com.google.gson;
}