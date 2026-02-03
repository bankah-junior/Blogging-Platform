package com.amalitech.SpringBootBloggingApp;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "Spring Boot Blogging Application API",
                version = "1.0.0",
                description = "API for a blogging platform built with Spring Boot and MongoDB",
                contact = @io.swagger.v3.oas.annotations.info.Contact(
                        name = "Anthony Bekoe Bankah",
                        email = "anthonybekoebankah@gmail.com",
                        url = "https://www.anthonybekoebankah.netlify.app/"
                )
        ),
        servers = @Server(url = "http://localhost:8080", description = "Local server"),
        tags = {
                @io.swagger.v3.oas.annotations.tags.Tag(name = "User", description = "Operations related to user management"),
                @io.swagger.v3.oas.annotations.tags.Tag(name = "Tag", description = "Operations related to blog tags management"),
                @io.swagger.v3.oas.annotations.tags.Tag(name = "Post", description = "Operations related to blog posts management"),
                @io.swagger.v3.oas.annotations.tags.Tag(name = "Comment", description = "Operations related to blog comments management"),
                @io.swagger.v3.oas.annotations.tags.Tag(name = "Review", description = "Operations related to blog reviews management")

        }
)
@SpringBootApplication
public class SpringBootBloggingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootBloggingAppApplication.class, args);
	}

}
