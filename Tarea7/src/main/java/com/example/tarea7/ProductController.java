package com.example.tarea7;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/productos")
public class ProductController {

    private ProductService productService;

    public ProductController() {
        this.productService = ServiceFactory.getProductService();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> searchProducts(@QueryParam("searchTerm") String searchTerm) {
        return productService.searchProducts(searchTerm);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Product createProduct(Product product) {
        return productService.createProduct(product);
    }
}