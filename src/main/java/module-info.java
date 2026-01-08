module com.amalitech.bloggingplatform {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.mongodb.driver.sync.client;
    requires org.slf4j;
    requires org.mongodb.driver.core;


    opens com.amalitech.bloggingplatform to javafx.fxml;
    exports com.amalitech.bloggingplatform;
}