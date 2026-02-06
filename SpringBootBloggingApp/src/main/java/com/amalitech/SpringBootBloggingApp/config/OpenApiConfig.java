package com.amalitech.SpringBootBloggingApp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bloggingAppOpenAPI() {
        Info apiInfo = new Info()
                .title("Spring Boot Blogging Application API")
                .version("1.0.0")
                .description("API for a blogging platform built with Spring Boot and MongoDB")
                .contact(new Contact()
                        .name("Anthony Bekoe Bankah")
                        .email("anthonybekoebankah@gmail.com")
                        .url("https://www.anthonybekoebankah.netlify.app/"));

        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local server");

        List<Tag> tags = List.of(
                new Tag().name("User").description("Operations related to user management"),
                new Tag().name("Tag").description("Operations related to blog tags management"),
                new Tag().name("Post").description("Operations related to blog posts management"),
                new Tag().name("Comment").description("Operations related to blog comments management"),
                new Tag().name("Review").description("Operations related to blog reviews management"),
                new Tag().name("PostTag").description("Operations related to post-tag relationships")
        );

        return new OpenAPI()
                .info(apiInfo)
                .servers(List.of(localServer))
                .tags(tags);
    }
}

