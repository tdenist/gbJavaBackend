package recipes;

import dto.response.ComplexSearchResponse;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import utils.ConfigUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ComplexSearchTests {

    ResponseSpecification responseSpecification = null;
    RequestSpecification requestSpecification = null;

    @BeforeEach
    void beforeTest(){
        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .expectResponseTime(Matchers.lessThan(5000L))
                .build();

        requestSpecification = new RequestSpecBuilder()
                .addQueryParam("apiKey", ConfigUtils.getApiKey())
                //.log(LogDetail.ALL)
                .build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @ParameterizedTest
    @ValueSource(strings = {"pasta", "burger"})
    void complexSearchGivenQueryThenResultsIsPresentTest(String queryText){
       ComplexSearchResponse response = given()
                .spec(requestSpecification)
                .queryParam("query", queryText)
                .when()
                .get( ConfigUtils.getBaseUrl() + "/recipes/complexSearch")
                .then()
                .assertThat()
                .spec(responseSpecification)
                .extract()
                .response()
                .body()
                .as(ComplexSearchResponse.class);

       assertThat(response.getResults().isEmpty(), is(false));
       assertThat(response.getOffset(),equalTo(0));
       assertThat(response.getTotalResults(), greaterThanOrEqualTo(10));
       assertThat(response.getResults().size(), equalTo(10));
       response.getResults().stream()
                       .forEach(result ->
                               assertThat(result.getTitle(), containsStringIgnoringCase(queryText)));
       //assertThat(response.getResults(), everyItem(hasProperty("title", containsStringIgnoringCase(queryText))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"qweASD123"})
    void complexSearchGivenQueryThenResultsIsEmptyTest(String queryText){
        ComplexSearchResponse response = given()
                .spec(requestSpecification)
                .queryParam("query", queryText)
                .when()
                .get( ConfigUtils.getBaseUrl() + "/recipes/complexSearch")
                .then()
                .assertThat()
                .spec(responseSpecification)
                .extract()
                .response()
                .body()
                .as(ComplexSearchResponse.class);

        assertThat(response.getResults().isEmpty(), is(true));
        assertThat(response.getOffset(),equalTo(0));
        assertThat(response.getNumber(),equalTo(10));
        assertThat(response.getTotalResults(), equalTo(0));
    }

    @ParameterizedTest
    @CsvSource({
            "pasta, 25, 2",
            "burger, 30, 5"
    })
    void complexSearchGivenQueryMaxFatNumberThenResultsIsPresentTest(String queryText, float maxFat, int number){
        ComplexSearchResponse response = given()
                .spec(requestSpecification)
                .queryParam("query", queryText)
                .queryParam("maxFat", maxFat)
                .queryParam("number", number)
                .when()
                .get( ConfigUtils.getBaseUrl() + "/recipes/complexSearch")
                .then()
                .assertThat()
                .spec(responseSpecification)
                .extract()
                .response()
                .body()
                .as(ComplexSearchResponse.class);

        assertThat(response.getResults().isEmpty(), is(false));
        assertThat(response.getOffset(),equalTo(0));
        assertThat(response.getTotalResults(), greaterThanOrEqualTo(number));
        assertThat(response.getResults().size(), equalTo(number));
        response.getResults().stream()
                .forEach(result ->
                        assertThat(result.getTitle(), containsStringIgnoringCase(queryText)));
        response.getResults().stream()
                .forEach(result -> result.getNutrition().getNutrients().stream()
                        .filter(nutrients -> nutrients.getName().equals("Fat"))
                        .forEach(nutrients ->
                                assertThat(nutrients.getAmount(),lessThanOrEqualTo(maxFat))));

    }
}
