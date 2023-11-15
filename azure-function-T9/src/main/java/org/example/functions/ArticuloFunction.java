package org.example.functions;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class ArticuloFunction {
    private static SessionFactory sessionFactory;

    static {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @FunctionName("getArticulo")
    public HttpResponseMessage getArticulo(
            @HttpTrigger(name = "request", methods = HttpMethod.GET, route = "articulo/{id}") HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            ExecutionContext context) {
        context.getLogger().info("Obteniendo el artículo con id " + id);

        Session session = sessionFactory.openSession();

        Articulo articulo = session.get(Articulo.class, Long.parseLong(id));

        session.close();

        if (articulo != null) {
            return request.createResponseBuilder(HttpStatus.OK).body(articulo).build();
        } else {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Artículo no encontrado").build();
        }
    }

    @FunctionName("createArticulo")
    public HttpResponseMessage createArticulo(
            @HttpTrigger(name = "request", methods = HttpMethod.POST, route = "articulo") HttpRequestMessage<Optional<Articulo>> request,
            ExecutionContext context) {
        context.getLogger().info("Creando un nuevo artículo");

        Articulo articulo = request.getBody().orElse(new Articulo());

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(articulo);

        tx.commit();
        session.close();

        return request.createResponseBuilder(HttpStatus.OK).body("Artículo creado con éxito").build();
    }

    @FunctionName("searchArticulo")
    public HttpResponseMessage searchArticulo(
            @HttpTrigger(name = "request", methods = HttpMethod.GET, route = "articulo/search/{term}") HttpRequestMessage<Optional<String>> request,
            @BindingName("term") String term,
            ExecutionContext context) {
        context.getLogger().info("Buscando artículos con la palabra: " + term);

        Session session = sessionFactory.openSession();

        List<Articulo> articulos = session.createQuery("select a from Articulo a where a.nombre like :term or a.descripcion like :term", Articulo.class)
                .setParameter("term", "%" + term + "%")
                .list();

        session.close();

        if (!articulos.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.OK).body(articulos).build();
        } else {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("No se encontraron artículos").build();
        }
    }

}
