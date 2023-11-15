package com.example.tarea7;

import javax.persistence.EntityManager;
import java.util.List;

public class ProductService {

    public Product createProduct(Product product) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(product);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return product;
    }

    public List<Product> searchProducts(String searchTerm) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        String query = "SELECT p FROM Product p WHERE p.name LIKE :searchTerm OR p.description LIKE :searchTerm";
        List<Product> searchTermResults = entityManager.createQuery(query, Product.class)
                .setParameter("searchTerm", "%" + searchTerm + "%")
                .getResultList();
        entityManager.close();
        return searchTermResults;
    }

    public Product getProductById(int id) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        Product product = entityManager.find(Product.class, id);
        entityManager.close();
        return product;
    }

    public Product updateProduct(Product product) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            Product updatedProduct = entityManager.merge(product);
            entityManager.getTransaction().commit();
            return updatedProduct;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.close();
        }
        return null;
    }
}

