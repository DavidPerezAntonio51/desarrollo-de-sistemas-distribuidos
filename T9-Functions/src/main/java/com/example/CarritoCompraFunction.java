package com.example;

import com.google.gson.Gson;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.HashMap;
import java.util.List;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.lang.reflect.Type;

public class CarritoCompraFunction {
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("pu");
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter()).create();
    private static final Type typeOfHashMap = new TypeToken<HashMap<String, Integer>>() { }.getType();

    @FunctionName("agregarAlCarrito")
    public HttpResponseMessage agregarAlCarrito(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, route = "carrito/agregar", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<String> request,
            final ExecutionContext context) {
        context.getLogger().info("Agregando al carrito");
        HashMap<String, Integer> body = gson.fromJson(request.getBody(), typeOfHashMap);

        int articuloId = body.get("articuloId");
        int cantidad = body.get("cantidad");

        // Crea un gestor de entidades
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();

        try {
            // Inicia la transacción
            em.getTransaction().begin();

            // Encuentra el artículo
            Articulo articulo = em.find(Articulo.class, articuloId);

            if (articulo == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Articulo no encontrado").build();
            }

            // Verifica si hay suficientes artículos en inventario
            if (articulo.getCantidadAlmacen() < cantidad) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("No hay suficientes articulos en el almacen").build();
            }

            // Crea una nueva entrada en el carrito de compras
            CarritoCompra carritoCompra = new CarritoCompra();
            carritoCompra.setArticulo(articulo);
            carritoCompra.setCantidad(cantidad);

            // Guarda el objeto CarritoCompra
            em.persist(carritoCompra);

            // Actualiza la cantidad de artículos en el inventario
            articulo.setCantidadAlmacen(articulo.getCantidadAlmacen() - cantidad);
            em.merge(articulo);

            // Confirma la transacción
            em.getTransaction().commit();

        } finally {
            // Cierra el gestor de entidades
            em.close();
        }

        return request.createResponseBuilder(HttpStatus.OK).body("Articulo agregado al carrito").build();
    }

    //...
    //...
    @FunctionName("eliminarDelCarrito")
    public HttpResponseMessage eliminarDelCarrito(
            @HttpTrigger(name = "req", methods = {HttpMethod.DELETE}, route = "carrito/eliminar/{id}", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<String> request,
            @BindingName("id") int id,
            final ExecutionContext context) {

        context.getLogger().info("Eliminando del carrito");

        // Crea un gestor de entidades
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();

        try {
            // Inicia la transacción
            em.getTransaction().begin();

            // Encuentra la entrada en el carrito de compras
            CarritoCompra carritoCompra = em.find(CarritoCompra.class, id);

            if (carritoCompra == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Entrada en carrito no encontrada").build();
            }

            // Recupera la cantidad de articulos
            Articulo articulo = carritoCompra.getArticulo();
            int cantidadRecuperada = carritoCompra.getCantidad();

            // Actualiza la cantidad de artículos en el inventario
            articulo.setCantidadAlmacen(articulo.getCantidadAlmacen() + cantidadRecuperada);
            em.merge(articulo);

            // Elimina la entrada en el carrito de compras
            em.remove(carritoCompra);

            // Confirma la transacción
            em.getTransaction().commit();

        } finally {
            // Cierra el gestor de entidades
            em.close();
        }

        return request.createResponseBuilder(HttpStatus.OK).body("Articulo eliminado del carrito").build();
    }

    @FunctionName("ajustarCantidad")
    public HttpResponseMessage ajustarCantidad(
            @HttpTrigger(name = "req", methods = {HttpMethod.PUT}, route = "carrito/ajustar/{id}", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<String> request,
            @BindingName("id") int id,
            final ExecutionContext context) {

        context.getLogger().info("Ajustando cantidad en el carrito");
        HashMap<String, Integer> body = gson.fromJson(request.getBody(), typeOfHashMap);

        int nuevaCantidad = body.get("cantidad");

        // Crea un gestor de entidades
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();

        try {
            // Inicia la transacción
            em.getTransaction().begin();

            // Encuentra la entrada en el carrito de compras
            CarritoCompra carritoCompra = em.find(CarritoCompra.class, id);

            if (carritoCompra == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Entrada en carrito no encontrada").build();
            }

            // Verifica si hay suficientes artículos en inventario
            Articulo articulo = carritoCompra.getArticulo();
            int cantidadActual = carritoCompra.getCantidad();
            int cantidadEnAlmacen = articulo.getCantidadAlmacen();

            if (nuevaCantidad > cantidadActual && cantidadEnAlmacen < (nuevaCantidad - cantidadActual)) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("No hay suficientes articulos en el almacen").build();
            }

            // Actualiza la cantidad de artículos en el inventario
            articulo.setCantidadAlmacen(cantidadEnAlmacen + cantidadActual - nuevaCantidad);
            em.merge(articulo);

            // Actualiza la cantidad en el carrito
            carritoCompra.setCantidad(nuevaCantidad);
            em.merge(carritoCompra);

            // Confirma la transacción
            em.getTransaction().commit();

        } finally {
            // Cierra el gestor de entidades
            em.close();
        }

        return request.createResponseBuilder(HttpStatus.OK).body("Cantidad ajustada").build();
    }

    //...
    //...
    @FunctionName("vaciarCarrito")
    public HttpResponseMessage vaciarCarrito(
            @HttpTrigger(name = "req", methods = {HttpMethod.DELETE}, route = "carrito/vaciar", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<String> request,
            final ExecutionContext context) {

        context.getLogger().info("Vaciando el carrito");

        // Crea un gestor de entidades
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();

        try {
            // Inicia la transacción
            em.getTransaction().begin();

            // Recupera todas las entradas en el carrito de compras
            List<CarritoCompra> carritoCompras = em.createQuery("SELECT c FROM CarritoCompra c", CarritoCompra.class).getResultList();

            // Por cada entrada en el carrito
            for (CarritoCompra carritoCompra : carritoCompras) {
                // Recupera la cantidad de articulos
                Articulo articulo = carritoCompra.getArticulo();
                int cantidadRecuperada = carritoCompra.getCantidad();

                // Actualiza la cantidad de artículos en el inventario
                articulo.setCantidadAlmacen(articulo.getCantidadAlmacen() + cantidadRecuperada);
                em.merge(articulo);

                // Elimina la entrada en el carrito de compras
                em.remove(em.contains(carritoCompra) ? carritoCompra : em.merge(carritoCompra));
            }

            // Confirma la transacción
            em.getTransaction().commit();

        } finally {
            // Cierra el gestor de entidades
            em.close();
        }

        return request.createResponseBuilder(HttpStatus.OK).body("Carrito vaciado").build();
    }

    @FunctionName("obtenerArticulosCarrito")
    public HttpResponseMessage obtenerArticulosCarrito(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, route = "carrito/articulos", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Void> request,
            final ExecutionContext context) {
        context.getLogger().info("Obteniendo articulos en el carrito");

        // Crea un gestor de entidades
        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();

        List<CarritoCompra> carritoCompras;

        try {
            // Consulta para obtener todas las entradas en el carrito
            carritoCompras = em.createQuery("SELECT c FROM CarritoCompra c", CarritoCompra.class).getResultList();
        } finally {
            // Cierra el gestor de entidades
            em.close();
        }

        // Convertir la lista a JSON
        String carritoJson = gson.toJson(carritoCompras);

        // Devuelve la respuesta
        return request.createResponseBuilder(HttpStatus.OK).body(carritoJson).header("Content-type","application/json").build();
    }

}

