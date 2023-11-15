package com.example;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Optional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jdk.jfr.ContentType;

public class ArticuloFunction {
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("pu");
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter()).create();

    @FunctionName("getArticulo")
    public HttpResponseMessage getArticulo(
            @HttpTrigger(name = "request", methods = HttpMethod.GET, route = "articulo/{id}", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            ExecutionContext context) {
        context.getLogger().info("Obteniendo el artículo con id " + id);

        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();

        Articulo articulo = em.find(Articulo.class, Long.parseLong(id));

        em.close();

        if (articulo != null) {
            return request.createResponseBuilder(HttpStatus.OK).body(gson.toJson(articulo)).build();
        } else {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Artículo no encontrado").build();
        }
    }

    @FunctionName("createArticulo")
    public HttpResponseMessage createArticulo(
            @HttpTrigger(name = "request", methods = HttpMethod.POST, route = "articulo", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<String> request,
            ExecutionContext context) {
        context.getLogger().info("Creando un nuevo artículo");

        Articulo articulo = gson.fromJson(request.getBody(), Articulo.class);

        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();
        em.getTransaction().begin();

        em.persist(articulo);

        em.getTransaction().commit();
        em.close();

        return request.createResponseBuilder(HttpStatus.OK).body("Artículo creado con éxito").build();
    }

    @FunctionName("searchArticulo")
    public HttpResponseMessage searchArticulo(
            @HttpTrigger(name = "request", methods = HttpMethod.GET, route = "articulo/search/{term}", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("term") String term,
            ExecutionContext context) {
        context.getLogger().info("Buscando artículos con la palabra: " + term);

        EntityManager em = ENTITY_MANAGER_FACTORY.createEntityManager();

        List<Articulo> articulos = em.createQuery("select a from Articulo a where a.nombre like :term or a.descripcion like :term", Articulo.class)
                .setParameter("term", "%" + term + "%")
                .getResultList();

        em.close();

        if (!articulos.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.OK).body(gson.toJson(articulos)).header("Content-type","application/json").build();
        } else {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("No se encontraron artículos").build();
        }
    }
}
