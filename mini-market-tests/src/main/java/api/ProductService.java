package api;

import dto.Product;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ProductService {

    @POST("products")
    Call<Product> createProduct (@Body Product createProductRequest);

    @PUT("products")
    Call<Product> modifyProduct (@Body Product modifyProductRequest);

    @DELETE("products/{id}")
    Call<ResponseBody> deleteProduct(@Path("id") int id);

    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") int id);

    @GET("products")
    Call<List<Product>> getProducts();
}
