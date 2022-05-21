import api.ProductService;
import com.github.javafaker.Faker;
import dto.Product;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import utils.RetrofitUtils;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;

public class CRUDProductTest {

    static ProductService productService;
    Faker faker = new Faker();
    Response<Product> response;
    int idProduct;

    @BeforeAll
    static void setUp() throws IOException {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
    }

    @Test
    void createReadUpdateDeleteProductTest() throws IOException {

        Product newProduct = new Product()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random()*1000));
        response = productService.createProduct(newProduct).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(201));
        assertThat(response.body().getTitle(), containsStringIgnoringCase(newProduct.getTitle()));
        assertThat(response.body().getCategoryTitle(), containsStringIgnoringCase(newProduct.getCategoryTitle()));
        assertThat(response.body().getPrice(), equalTo(newProduct.getPrice()));
        idProduct = response.body().getId();

        response = productService.getProductById(idProduct).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(200));
        assertThat(response.body().getTitle(), containsStringIgnoringCase(newProduct.getTitle()));
        assertThat(response.body().getCategoryTitle(), containsStringIgnoringCase(newProduct.getCategoryTitle()));
        assertThat(response.body().getPrice(), equalTo(newProduct.getPrice()));

        Product updateProduct = new Product()
                .withId(idProduct)
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random()*1000));
        response = productService.modifyProduct(updateProduct).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(200));
        assertThat(response.body().getId(), equalTo(idProduct));
        assertThat(response.body().getTitle(), containsStringIgnoringCase(updateProduct.getTitle()));
        assertThat(response.body().getCategoryTitle(), containsStringIgnoringCase(updateProduct.getCategoryTitle()));
        assertThat(response.body().getPrice(), equalTo(updateProduct.getPrice()));

        Response<List<Product>> responseProductList = productService.getProducts().execute();
        assertThat(
            responseProductList.body().stream()
                .anyMatch(product -> product.equals(updateProduct)),
            CoreMatchers.is(true)
        );
    }

    @AfterEach
    void deleteProduct() throws IOException {
        Response<ResponseBody> response = productService.deleteProduct(idProduct).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
    }
}
