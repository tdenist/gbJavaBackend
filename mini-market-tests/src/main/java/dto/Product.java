package dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {

    private Integer id;
    private String title;
    private Integer price;
    private String categoryTitle;

    public Product withId(Integer id) {
        this.id = id;
        return this;
    }
    public Product withTitle(String title) {
        this.title = title;
        return this;
    }
    public Product withPrice(Integer price) {
        this.price = price;
        return this;
    }
    public Product withCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getPrice() {
        return price;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id.equals(product.id) && title.equals(product.title) && price.equals(product.price) && categoryTitle.equals(product.categoryTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, price, categoryTitle);
    }
}
