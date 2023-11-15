package com.example.tarea7;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/basket")
public class BasketController {

    private CartService basketService;

    public BasketController() {
        this.basketService = ServiceFactory.getBasketService();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProductToBasket(ProductQuantity productQuantity) {
        if (productQuantity.getQuantity() < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("The quantity must be 1 or more.")
                    .build();
        }

        try {
            ShoppingCart basket = basketService.addProductToCart(productQuantity.getProductId(), productQuantity.getQuantity());
            return Response.ok(basket).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBasketProducts() {
        List<ShoppingCart> basketProducts = basketService.getCartItems();
        if (basketProducts.isEmpty()) {
            return Response.noContent().build();
        } else {
            return Response.ok(basketProducts).build();
        }
    }

    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateBasketProductQuantity(@PathParam("id") int id, ProductQuantity productQuantity) {
        if (productQuantity.getQuantity() < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("The quantity must be 1 or more.")
                    .build();
        }

        try {
            basketService.updateCartItemQuantity(id,productQuantity.getQuantity());
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response removeBasketProduct(@PathParam("id") int id) {
        try {
            basketService.removeProductFromCart(id);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/clear")
    public void clearBasket() {
        basketService.clearCart();
    }
}

