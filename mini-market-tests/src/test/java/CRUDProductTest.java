import api.ProductService;
import com.github.javafaker.Faker;
import db.dao.CategoriesMapper;
import db.dao.ProductsMapper;
import db.model.Categories;
import db.model.Products;
import db.model.ProductsExample;
import dto.Product;
import okhttp3.ResponseBody;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import retrofit2.Response;
import utils.RetrofitUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;

public class CRUDProductTest {

    static ProductService productService = null;
    static SqlSession sqlSession = null;
    static ProductsMapper productsMapper = null;
    static CategoriesMapper categoriesMapper = null;
    Faker faker = new Faker();
    Product product;
    Response<Product> response;
    int idProduct;

    @BeforeAll
    static void setUp() throws IOException {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);

        SqlSessionFactory sqlSessionFactory;
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sqlSessionFactory.openSession();
    }

    @BeforeEach
    void createMappers(){
        productsMapper = sqlSession.getMapper(ProductsMapper.class);
        categoriesMapper = sqlSession.getMapper(CategoriesMapper.class);
    }

    @Test
    void createReadUpdateDeleteProductTest() throws IOException {

        product = new Product()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random()*1000));
        response = productService.createProduct(product).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(201));
        assertThat(response.body().getTitle(), containsStringIgnoringCase(product.getTitle()));
        assertThat(response.body().getCategoryTitle(), containsStringIgnoringCase(product.getCategoryTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        idProduct = response.body().getId();

        Products productInDB = productsMapper.selectByPrimaryKey(Long.valueOf(idProduct));
        Categories productInDBCategory = categoriesMapper.selectByPrimaryKey(productInDB.getCategory_id());
        assertThat(productInDB.getTitle(), containsStringIgnoringCase(product.getTitle()));
        assertThat(productInDBCategory.getTitle(), containsStringIgnoringCase(product.getCategoryTitle()));
        assertThat(productInDB.getPrice(), equalTo(product.getPrice()));
        sqlSession.commit();

        response = productService.getProductById(idProduct).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(200));
        assertThat(response.body().getTitle(), containsStringIgnoringCase(product.getTitle()));
        assertThat(response.body().getCategoryTitle(), containsStringIgnoringCase(product.getCategoryTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));

        product = new Product()
                .withId(idProduct)
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random()*1000));
        response = productService.modifyProduct(product).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(200));
        assertThat(response.body().getId(), equalTo(idProduct));
        assertThat(response.body().getTitle(), containsStringIgnoringCase(product.getTitle()));
        assertThat(response.body().getCategoryTitle(), containsStringIgnoringCase(product.getCategoryTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));

        productInDB = productsMapper.selectByPrimaryKey(Long.valueOf(idProduct));
        productInDBCategory = categoriesMapper.selectByPrimaryKey(productInDB.getCategory_id());
        assertThat(productInDB.getTitle(), containsStringIgnoringCase(product.getTitle()));
        assertThat(productInDBCategory.getTitle(), containsStringIgnoringCase(product.getCategoryTitle()));
        assertThat(productInDB.getPrice(), equalTo(product.getPrice()));
        sqlSession.commit();

        Response<List<Product>> responseProductList = productService.getProducts().execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(200));
        assertThat(
            responseProductList.body().stream()
                .anyMatch(p -> p.equals(product)),
            CoreMatchers.is(true)
        );
    }

    @AfterEach
    void deleteProduct() throws IOException {
        Response<ResponseBody> response = productService.deleteProduct(idProduct).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));

        Response<List<Product>> responseProductList = productService.getProducts().execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.code(), equalTo(200));
        assertThat(
                responseProductList.body().stream()
                        .noneMatch(p -> p.equals(product)),
                CoreMatchers.is(true)
        );

        ProductsExample productExample = new ProductsExample();
        productExample.createCriteria().andIdEqualTo(Long.valueOf(idProduct));
        List<Products> productsList = productsMapper.selectByExample(productExample);
        sqlSession.commit();
        assertThat(productsList.size(), equalTo(0));
    }

    @AfterAll
    static void tearDown(){
        sqlSession.close();
    }
}
