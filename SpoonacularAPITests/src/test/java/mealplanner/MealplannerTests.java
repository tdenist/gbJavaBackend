package mealplanner;

import dto.request.ShoppinglistItemsRequest;
import dto.request.UsersConnectRequest;
import dto.response.ShoppingListResponse;
import dto.response.ShoppinglistItemsResponse;
import dto.response.UsersConnectResponse;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ConfigUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MealplannerTests {

    ResponseSpecification responseSpecification = null;
    RequestSpecification requestSpecification = null;

    private static String hash;
    private static String username;

    int addItemID;

    @BeforeAll
    static void userConnect(){
        UsersConnectRequest usersConnectRequest = new UsersConnectRequest();
        usersConnectRequest.setEmail("test@test.ru");
        usersConnectRequest.setUsername("testuser");
        usersConnectRequest.setFirstName("Testov");
        usersConnectRequest.setLastName("Test");

        UsersConnectResponse response = given()
                .queryParam("apiKey", ConfigUtils.getApiKey())
                .body(usersConnectRequest)
                .when()
                .post(ConfigUtils.getBaseUrl() + "/users/connect")
                .then()
                .assertThat()
                .statusCode(200)
                .body("status", containsString("success"))
                .extract()
                .body()
                .as(UsersConnectResponse.class);

        hash = response.getHash();
        username = response.getUsername();
    }

    @BeforeEach
    void beforeTest() {
        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .build();

        requestSpecification = new RequestSpecBuilder()
                .addQueryParam("apiKey", ConfigUtils.getApiKey())
                .addQueryParam("hash", hash)
                .addPathParam("username", username)
                //.log(LogDetail.ALL)
                .build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void shoppinglistItemsAddToShoppingListTest(){
        ShoppinglistItemsRequest shoppinglistItemsRequest = new ShoppinglistItemsRequest();
        shoppinglistItemsRequest.setItem("1 package baking powder");
        shoppinglistItemsRequest.setAisle("Baking");
        shoppinglistItemsRequest.setParse(true);

        ShoppinglistItemsResponse shoppinglistItemsResponse = given()
                .spec(requestSpecification)
                .body(shoppinglistItemsRequest)
                .when()
                .post(ConfigUtils.getBaseUrl() + "/mealplanner/{username}/shopping-list/items")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(ShoppinglistItemsResponse.class);

        addItemID = shoppinglistItemsResponse.getId();

        ShoppingListResponse shoppingListResponse = given()
                .spec(requestSpecification)
                .when()
                .get(ConfigUtils.getBaseUrl() + "/mealplanner/{username}/shopping-list")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(ShoppingListResponse.class);

        shoppingListResponse.getAisles().stream()
                .forEach(aisle -> aisle.getItems().stream()
                        .forEach(item ->
                                assertThat(item.getId(), equalTo(addItemID))));
    }

    @AfterEach
    void deleteItemFromShoppingList(){
        given()
                .spec(requestSpecification)
                .pathParam("id", addItemID )
                .when()
                .delete(ConfigUtils.getBaseUrl() + "/mealplanner/{username}/shopping-list/items/{id}")
                .then()
                .assertThat()
                .spec(responseSpecification)
                .body("status", containsString("success"));

        ShoppingListResponse shoppingListResponse = given()
                .spec(requestSpecification)
                .when()
                .get(ConfigUtils.getBaseUrl() + "/mealplanner/{username}/shopping-list")
                .then()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(ShoppingListResponse.class);

        shoppingListResponse.getAisles().stream()
                .forEach(aisle -> aisle.getItems().stream()
                        .forEach(item ->
                                assertThat(item.getId(), not(equalTo(addItemID)))));
    }
}