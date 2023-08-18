package com.bol.kahala.controller;

import com.bol.kahala.constants.KahalaConstants;
import com.bol.kahala.dto.MoveDTO;
import com.bol.kahala.entity.MatchStatus;
import com.bol.kahala.entity.Pit;
import com.bol.kahala.entity.Player;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.bol.kahala.constants.KahalaConstants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class MatchControllerTest {

    private static final String FIRST_PLAYER_NAME = "First Player";
    private static final String SECOND_PLAYER_NAME = "Second Player";

    @LocalServerPort
    private Integer port;


    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres");

    @BeforeAll
    public static void init(){
        System.out.println(ClassLoader.getSystemClassLoader().getName());
        postgres
                .withDatabaseName("kahala")
                .withUsername("sa_kahala")
                .withPassword("sa_kahala")
                .start();
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
                .body(params)
                .when()
                .post("/match")
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

    @Test
    public void newMatchReturningBadRequest () {
        Map<String, String> params = Map.of("player1", FIRST_PLAYER_NAME, "player2", "");
        given()
                .log()
                .all()
                .contentType("application/json")
                .body(params)
                .when()
                .post("/match")
                .then()
                .log()
                .all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(ERROR_MESSAGE_PLAYER_NAME_EMPTY));

    }

    @Test
    public void moveReturningBadRequestPitIdNull () {
        MoveDTO moveDto = new MoveDTO(null);
        given()
                .log()
                .all()
                .contentType("application/json")
                .body(moveDto)
                .when()
                .put("/match")
                .then()
                .log()
                .all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(ERROR_MESSAGE_PIT_ID_NULL));

    }

    @Test
    public void moveReturningBadRequestPitIdInvalid () {
        MoveDTO moveDto = new MoveDTO(10000L);
        given()
                .log()
                .all()
                .contentType("application/json")
                .body(moveDto)
                .when()
                .put("/match")
                .then()
                .log()
                .all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(KahalaConstants.ERROR_MESSAGE_PIT_ID_INVALID));

    }

    @Test
    @Sql(scripts = "/db/test-move-wrong-player.sql", config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
    public void moveReturningBadRequestWrongPlayer () {
        MoveDTO moveDto = new MoveDTO(5008L);
        given()
                .log()
                .all()
                .contentType("application/json")
                .body(moveDto)
                .when()
                .put("/match")
                .then()
                .log()
                .all()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo(ERROR_MESSAGE_PLAYERS_TURN));

    }

    @Test
    @Sql(scripts = "/db/test-move-change-turn.sql", config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
    public void moveReturningOkChangingTurns () {
        MoveDTO moveDto = new MoveDTO(1002L);
        JsonPath response = given()
                .log()
                .all()
                .contentType("application/json")
                .body(moveDto)
                .when()
                .put("/match")
                .jsonPath();
        Optional<Player> player = response.getList("players", Player.class)
                .stream().filter(p -> p.getId().equals(1001L)).findFirst();
        assertFalse(player.get().isTurn());

    }

    @Test
    @Sql(scripts = "/db/test-move-steal-stones.sql", config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
    public void moveReturningOkStealingStones () {
        MoveDTO moveDto = new MoveDTO(2001L);
        JsonPath response = given()
                .log()
                .all()
                .contentType("application/json")
                .body(moveDto)
                .when()
                .put("/match")
                .jsonPath();
        List<Player> players = response.getList("players", Player.class);
        Player player = players.stream().filter(p -> p.getId().equals(2001L)).findFirst().get();
        assertFalse(player.isTurn());
        Pit bigPit = player.getPits().stream().filter(p -> p.getPitOrder().equals(BIG_PIT_ORDER)).findFirst().get();
        assertEquals(bigPit.getStones(), 14);
        Pit lastSmallPit = player.getPits().stream().filter(p -> p.getPitOrder().equals(6)).findFirst().get();
        assertEquals(lastSmallPit.getStones(), 0);
        Pit oppositePit = players
                .stream()
                .filter(p -> p.getId().equals(2002L))
                .map(Player::getPits)
                .flatMap(Collection<Pit>::stream)
                .filter(pit -> pit.getPitOrder().equals(1))
                .findFirst()
                .get();
        assertEquals(oppositePit.getStones(), 0);
    }

    @Test
    @Sql(scripts = "/db/test-move-get-another-turn.sql", config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
    public void moveReturningOkGettingAnotherTurn () {
        MoveDTO moveDto = new MoveDTO(3001L);
        JsonPath response = given()
                .log()
                .all()
                .contentType("application/json")
                .body(moveDto)
                .when()
                .put("/match")
                .jsonPath();
        List<Player> players = response.getList("players", Player.class);
        Player player = players.stream().filter(p -> p.getId().equals(3001L)).findFirst().get();
        assertTrue(player.isTurn());
        Pit bigPit = player.getPits().stream().filter(p -> p.getPitOrder().equals(BIG_PIT_ORDER)).findFirst().get();
        assertEquals(bigPit.getStones(), 1);
    }

    @Test
    @Sql(scripts = "/db/test-move-game-over.sql", config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
    public void moveReturningOkAndGameOver () {
        MoveDTO moveDto = new MoveDTO(4006L);
        JsonPath response = given()
                .log()
                .all()
                .contentType("application/json")
                .body(moveDto)
                .when()
                .put("/match")
                .jsonPath();
        List<Player> players = response.getList("players", Player.class);
        Player player = players.stream().filter(p -> p.getId().equals(4001L)).findFirst().get();
        assertFalse(player.isTurn());
        Pit bigPit = player.getPits().stream().filter(p -> p.getPitOrder().equals(BIG_PIT_ORDER)).findFirst().get();
        assertEquals(bigPit.getStones(), 37);
        int stones = player.getPits().stream().filter(p -> !p.getPitOrder().equals(BIG_PIT_ORDER)).mapToInt(Pit::getStones).sum();
        assertEquals(stones,0);
        Player oppositePlayer = players.stream().filter(p -> p.getId().equals(4002L)).findFirst().get();
        assertFalse(oppositePlayer.isTurn());
        Pit oppositeBigPit = oppositePlayer.getPits().stream().filter(p -> p.getPitOrder().equals(BIG_PIT_ORDER)).findFirst().get();
        assertEquals(oppositeBigPit.getStones(), 35);
        int oppositeStones = oppositePlayer.getPits().stream().filter(p -> !p.getPitOrder().equals(BIG_PIT_ORDER)).mapToInt(Pit::getStones).sum();
        assertEquals(oppositeStones,0);
        assertEquals(response.getString("status"), MatchStatus.FINISHED.name());
    }

}
