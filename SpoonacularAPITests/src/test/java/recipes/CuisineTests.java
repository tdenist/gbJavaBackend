package recipes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import utils.ConfigUtils;

import static io.restassured.RestAssured.given;
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

    @Test
    void cuisineWithoutParametersThenResultsIsPresentTest(){
        given()
                .queryParam("apiKey", ConfigUtils.getApiKey())
                .when()
                .post( ConfigUtils.getBaseUrl() + "/recipes/cuisine")
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(200)
                .body("cuisine", containsString("Mediterranean"))
                .body("cuisines", hasItem(oneOf(supportedCuisinesArray)))
                .body("confidence", equalTo(0.0F));
    }

    @ParameterizedTest
    @CsvSource({
            "pasta, oil, en, Mediterranean, 0.0",
            "burger, butter, de, American, 0.85"
    })
    void cuisineGivenFullParametersThenResultsIsPresentTest(String title,
                                                            String ingredientList,
                                                            String language,
                                                            String expectCuisine,
                                                            float expectConfidence){
        given()
                .queryParam("apiKey", ConfigUtils.getApiKey())
                .queryParam("language", language)
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", title)
                .formParam("ingredientList", ingredientList)
                .when()
                .post( ConfigUtils.getBaseUrl() + "/recipes/cuisine")
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(200)
                .body("cuisine", containsString(expectCuisine))
                .body("cuisines", hasItem(oneOf(supportedCuisinesArray)))
                .body("confidence", equalTo(expectConfidence));
    }
}
