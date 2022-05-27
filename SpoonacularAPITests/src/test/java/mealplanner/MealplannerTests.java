package mealplanner;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConfigUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MealplannerTests {

    private static String hash;
    private static String username;

    String addItemID;

    @BeforeAll
    static void userConnect(){
        JsonPath response = given()
                .queryParam("apiKey", ConfigUtils.getApiKey())
                .body("{\n" +
                        "    \"username\": \"testuser\",\n" +
                        "    \"firstName\": \"Testov\",\n" +
                        "    \"lastName\": \"Test\",\n" +
                        "    \"email\": \"dent.90@mail.ru\"\n" +
                        "}"
                )
                .log().all()
                .when()
                .post(ConfigUtils.getBaseUrl() + "/users/connect")
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(200)
                .body("status", containsString("success"))
                .extract()
                .jsonPath();

        hash = response.get("hash").toString();
        username = response.get("username").toString();
    }

    @Test
    void shoppinglistItemsAddToShoppingListTest(){
        addItemID = given()
                .pathParam("username", username)
                .queryParam("hash", hash)
                .queryParam("apiKey", ConfigUtils.getApiKey())
                .body("{\n" +
                        "\t\"item\": \"1 package baking powder\",\n" +
                        "\t\"aisle\": \"Baking\",\n" +
                        "\t\"parse\": true\n" +
                        "}"
                )
                .log().all()
                .when()
                .post(ConfigUtils.getBaseUrl() + "/mealplanner/{username}/shopping-list/items")
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", is(notNullValue()))
                .extract()
                .jsonPath()
                .get("id")
                .toString();
    }

    @AfterEach
    void deleteItemFromShoppingList(){
        given()
                .pathParam("username", username)
                .pathParam("id", addItemID )
                .queryParam("hash", hash)
                .queryParam("apiKey", ConfigUtils.getApiKey())
                .log().all()
                .when()
                .delete(ConfigUtils.getBaseUrl() + "/mealplanner/{username}/shopping-list/items/{id}")
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(200)
                .body("status", containsString("success"));
    }
}
