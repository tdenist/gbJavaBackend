package recipes;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import utils.ConfigUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ComplexSearchTests {

    @ParameterizedTest
    @ValueSource(strings = {"pasta", "burger"})
    void complexSearchGivenQueryThenResultsIsPresentTest(String queryText){
        given()
                .queryParam("apiKey", ConfigUtils.getApiKey())
                .queryParam("query", queryText)
                .when()
                .get( ConfigUtils.getBaseUrl() + "/recipes/complexSearch")
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(200)
                .time(Matchers.lessThan(3000L))
                .body("results", is(notNullValue()))
                .body("results.title", hasItem(containsStringIgnoringCase(queryText)))
                .body("offset", equalTo(0))
                .body("results.size()", equalTo(10))
                .body("totalResults", greaterThanOrEqualTo(10));
    }

    @Test
    void complexSearchGivenQueryThenResultsIsEmptyTest(){
        given()
                .queryParam("apiKey", ConfigUtils.getApiKey())
                .queryParam("query", "qweASD123")
                .when()
                .get( ConfigUtils.getBaseUrl() + "/recipes/complexSearch")
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(200)
                .body("results.size", equalTo(0))
                .body("offset", equalTo(0))
                .body("number", equalTo(10))
                .body("totalResults", equalTo(0));
    }

    @ParameterizedTest
    @CsvSource({
            "pasta, 25, 2",
            "burger, 30, 5"
    })
    void complexSearchGivenQueryMaxFatNumberThenResultsIsPresentTest(String queryText, int maxFat, int number){
        given()
                .queryParam("apiKey", ConfigUtils.getApiKey())
                .queryParam("query", queryText)
                .queryParam("maxFat", maxFat)
                .queryParam("number", number)
                .when()
                .get( ConfigUtils.getBaseUrl() + "/recipes/complexSearch")
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(200)
                .body("results", is(notNullValue()))
                .body("results.title", hasItem(containsStringIgnoringCase(queryText)))
                .body("offset", equalTo(0))
                .body("results.size()", equalTo(number))
                .body("totalResults", greaterThanOrEqualTo(number));
    }
}
