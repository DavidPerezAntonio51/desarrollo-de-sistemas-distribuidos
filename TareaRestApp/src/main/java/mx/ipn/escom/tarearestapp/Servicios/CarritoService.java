package mx.ipn.escom.tarearestapp.Servicios;

import mx.ipn.escom.tarearestapp.Entidades.Articulo;
import mx.ipn.escom.tarearestapp.Entidades.Carrito;
import mx.ipn.escom.tarearestapp.Utils.EntityManagerUtil;


import javax.persistence.EntityManager;
import java.util.List;

public class CarritoService {

    public List<Carrito> obtenerArticulosCarrito() {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        String query = "SELECT c FROM Carrito c";
        List<Carrito> resultList = entityManager.createQuery(query, Carrito.class).getResultList();
        entityManager.close();
        return resultList;
    }

    public Carrito agregarArticuloCarrito(int articuloId, int cantidad) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        Articulo articulo = entityManager.find(Articulo.class, articuloId);

        if (articulo.getCantidadAlmacen() >= cantidad) {
            entityManager.getTransaction().begin();
            try {
                articulo.setCantidadAlmacen(articulo.getCantidadAlmacen() - cantidad);
                entityManager.merge(articulo);

                Carrito carrito = new Carrito();
                carrito.setArticulo(articulo);
                carrito.setCantidad(cantidad);
                entityManager.persist(carrito);

                entityManager.getTransaction().commit();

                return carrito;
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                throw e;
            }finally {
                entityManager.close();
            }
        } else {
            throw new RuntimeException("No hay suficientes artículos en el almacén.");
        }
    }

    public void eliminarArticuloCarrito(int carritoId) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        Carrito carrito = entityManager.find(Carrito.class, carritoId);
        if (carrito != null) {
            Articulo articulo = carrito.getArticulo();
            entityManager.getTransaction().begin();
            try {
                articulo.setCantidadAlmacen(articulo.getCantidadAlmacen() + carrito.getCantidad());
                entityManager.merge(articulo);

                entityManager.remove(carrito);

                entityManager.getTransaction().commit();
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                throw e;
            }finally {
                entityManager.close();
            }
        } else {
            throw new RuntimeException("No se encontró el artículo en el carrito.");
        }
    }

    public void actualizarCantidadArticuloCarrito(int carritoId, int nuevaCantidad) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        Carrito carrito = entityManager.find(Carrito.class, carritoId);

        if (carrito != null) {
            Articulo articulo = carrito.getArticulo();
            int diferenciaCantidad = nuevaCantidad - carrito.getCantidad();

            if (articulo.getCantidadAlmacen() >= diferenciaCantidad) {
                entityManager.getTransaction().begin();
                try {
                    articulo.setCantidadAlmacen(articulo.getCantidadAlmacen() - diferenciaCantidad);
                    entityManager.merge(articulo);

                    carrito.setCantidad(nuevaCantidad);
                    entityManager.merge(carrito);

                    entityManager.getTransaction().commit();
                } catch (Exception e) {
                    entityManager.getTransaction().rollback();
                    throw e;
                }
            } else {
                throw new RuntimeException("No hay suficientes artículos en el almacén.");
            }
        } else {
            throw new RuntimeException("No se encontró el artículo en el carrito.");
        }
    }

    public void eliminarCarrito() {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        String query = "SELECT c FROM Carrito c";
        List<Carrito> carrito = entityManager.createQuery(query, Carrito.class).getResultList();
        entityManager.getTransaction().begin();
        try {
            for (Carrito item : carrito) {
                Articulo articulo = item.getArticulo();
                articulo.setCantidadAlmacen(articulo.getCantidadAlmacen() + item.getCantidad());
                entityManager.merge(articulo);

                entityManager.remove(item);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw e;
        }finally {
            entityManager.close();
        }
    }
}
