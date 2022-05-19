package recipes;

import dto.response.CuisineResponse;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import utils.ConfigUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CuisineTests {

    private static String[] supportedCuisinesArray = new String[] {
            "African",
            "American",
            "British",
            "Cajun",
            "Caribbean",
            "Chinese",
            "Eastern European",
            "European",
            "French",
            "German",
            "Greek",
            "Indian",
            "Irish",
            "Italian",
            "Japanese",
            "Jewish",
            "Korean",
            "Latin American",
            "Mediterranean",
            "Mexican",
            "Middle Eastern",
            "Nordic",
            "Southern",
            "Spanish",
            "Thai",
            "Vietnamese"
    };

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

    @Test
    void cuisineWithoutParametersThenResultsIsPresentTest(){
        CuisineResponse response = given()
                .spec(requestSpecification)
                .when()
                .post( ConfigUtils.getBaseUrl() + "/recipes/cuisine")
                .prettyPeek()
                .then()
                .assertThat()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(CuisineResponse.class);

        assertThat(response.getCuisine(), oneOf(supportedCuisinesArray));
        response.getCuisines().stream()
                .forEach(cuisine -> assertThat(cuisine, oneOf(supportedCuisinesArray)));
        assertThat(response.getConfidence(), equalTo(0F));
    }

    @ParameterizedTest
    @CsvSource({
            "pasta, oil, en, Italian, 0.0",
            "burger, butter, de, American, 0.85"
    })
    void cuisineGivenFullParametersThenResultsIsPresentTest(String title,
                                                            String ingredientList,
                                                            String language,
                                                            String expectCuisine,
                                                            float expectConfidence){
        CuisineResponse response = given()
                .spec(requestSpecification)
                .queryParam("language", language)
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", title)
                .formParam("ingredientList", ingredientList)
                .when()
                .post( ConfigUtils.getBaseUrl() + "/recipes/cuisine")
                .prettyPeek()
                .then()
                .assertThat()
                .spec(responseSpecification)
                .extract()
                .body()
                .as(CuisineResponse.class);

        assertThat(response.getCuisine(), containsStringIgnoringCase(expectCuisine));
        response.getCuisines().stream()
                .forEach(cuisine -> assertThat(cuisine, oneOf(supportedCuisinesArray)));
        assertThat(response.getConfidence(), equalTo(expectConfidence));
    }
}
