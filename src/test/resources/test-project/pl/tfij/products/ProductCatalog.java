import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCatalog {
    private List<Product> products;
    private Map<Category, List<Product>> productsByCategory;
    private Map<String, List<Product>> productsByBrand;

    public ProductCatalog() {
        this.products = new ArrayList<>();
        this.productsByCategory = new HashMap<>();
        this.productsByBrand = new HashMap<>();
    }

    public void addProduct(Product product) {
        products.add(product);
        addToCategoryMap(product);
        addToBrandMap(product);
    }

    private void addToCategoryMap(Product product) {
        List<Product> productList = productsByCategory.computeIfAbsent(product.getCategory(), k -> new ArrayList<>());
        productList.add(product);
    }

    private void addToBrandMap(Product product) {
        List<Product> productList = productsByBrand.computeIfAbsent(product.getBrand(), k -> new ArrayList<>());
        productList.add(product);
    }

    public List<Product> getProductsByCategory(Category category) {
        return productsByCategory.getOrDefault(category, Collections.emptyList());
    }

    public List<Product> getProductsByBrand(String brand) {
        return productsByBrand.getOrDefault(brand, Collections.emptyList());
    }

    public List<Product> searchProducts(String query) {
        List<Product> searchResults = new ArrayList<>();
        for (Product product : products) {
            if (product.getName().toLowerCase().contains(query.toLowerCase()) ||
                    product.getDescription().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(product);
            }
        }
        return searchResults;
    }

    public void removeProduct(Product product) {
        products.remove(product);
        removeFromCategoryMap(product);
        removeFromBrandMap(product);
    }

    private void removeFromCategoryMap(Product product) {
        List<Product> productList = productsByCategory.get(product.getCategory());
        if (productList != null) {
            productList.remove(product);
        }
    }

    private void removeFromBrandMap(Product product) {
        List<Product> productList = productsByBrand.get(product.getBrand());
        if (productList != null) {
            productList.remove(product);
        }
    }

    public int getTotalProductCount() {
        return products.size();
    }

    public void clearCatalog() {
        products.clear();
        productsByCategory.clear();
        productsByBrand.clear();
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public Map<Category, List<Product>> getProductsByCategoryMap() {
        return Collections.unmodifiableMap(productsByCategory);
    }

    public Map<String, List<Product>> getProductsByBrandMap() {
        return Collections.unmodifiableMap(productsByBrand);
    }
}
