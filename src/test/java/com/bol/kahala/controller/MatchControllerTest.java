package com.bol.kahala.controller;

import io.restassured.RestAssured;
//import org.junit.After;
//import org.junit.Before;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MatchControllerTest {

    private static final String FIRST_PLAYER_NAME = "First Player";
    private static final String SECOND_PLAYER_NAME = "Second Player";

    @LocalServerPort
    private Integer port;


//    @Container
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres");
//            .withDatabaseName("kahala")
//            .withUsername("sa_kahala")
//            .withPassword("sa_kahala")
//            .withExposedPorts(5441);

    @BeforeAll
    public static void init(){
        System.out.println(ClassLoader.getSystemClassLoader().getName());
        postgres
                .withDatabaseName("kahala")
                .withUsername("sa_kahala")
                .withPassword("sa_kahala")
//                .withCommand("CREATE SCHEMA kahala;")
                .start();
//        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
//        System.setProperty("spring.datasource.username", postgres.getUsername());
//        System.setProperty("spring.datasource.password", postgres.getPassword());
//        baseUri = "http://localhost:".concat(port.toString());
    }
    
    @AfterAll
    public static void destroy(){
        postgres.stop();
    }

    @DynamicPropertySource
    protected static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    protected void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    public void newMatchReturningCreatedTest () {
        Map<String, String> params = Map.of("player1", FIRST_PLAYER_NAME, "player2", SECOND_PLAYER_NAME);
        given()
                .log()
                .all()
                .contentType("application/json")
//                .baseUri(baseUri)
                .body(params)
                .when()
                .post("/api/kahala/match")
                .then()
                .log()
                .all()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .and().body("players", hasSize(2))
                .and().body("players", hasItem(hasEntry("name", FIRST_PLAYER_NAME)))
                .and().body("players", hasItem(hasEntry("name", SECOND_PLAYER_NAME)))
                .and().body("players", hasItem(hasEntry("turn", true)))
                .and().body("players", hasItem(hasEntry("turn", false)));
    }



}
