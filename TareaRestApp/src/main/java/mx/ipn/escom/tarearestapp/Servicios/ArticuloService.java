package mx.ipn.escom.tarearestapp.Servicios;


import mx.ipn.escom.tarearestapp.Entidades.Articulo;
import mx.ipn.escom.tarearestapp.Utils.EntityManagerUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class ArticuloService {

    public Articulo crearArticulo(Articulo articulo) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(articulo);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return articulo;
    }

    public List<Articulo> buscarArticulos(String terminoBusqueda) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        String query = "SELECT a FROM Articulo a WHERE a.nombre LIKE :terminoBusqueda OR a.descripcion LIKE :terminoBusqueda";
        List<Articulo> terminoBusquedaResult = entityManager.createQuery(query, Articulo.class)
                .setParameter("terminoBusqueda", "%" + terminoBusqueda + "%")
                .getResultList();
        entityManager.close();
        return terminoBusquedaResult;
    }

    public Articulo obtenerArticuloPorId(int id) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        Articulo articulo = entityManager.find(Articulo.class, id);
        entityManager.close();
        return articulo;
    }

    public Articulo actualizarArticulo(Articulo articulo) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            Articulo articuloActualizado = entityManager.merge(articulo);
            entityManager.getTransaction().commit();
            return articuloActualizado;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.close();
        }
        return null;
    }
}
