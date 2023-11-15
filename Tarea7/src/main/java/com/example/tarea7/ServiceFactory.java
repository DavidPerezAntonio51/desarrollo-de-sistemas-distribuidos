package com.example.tarea7;

public class ServiceFactory {

    private static ProductService articuloService;
    private static CartService carritoService;

    public static ProductService getProductService() {
        if (articuloService == null) {
            articuloService = new ProductService();
        }
        return articuloService;
    }

    public static CartService getBasketService() {
        if (carritoService == null) {
            carritoService = new CartService();
        }
        return carritoService;
    }
}
