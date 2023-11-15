package com.example.tarea7;

import javax.persistence.EntityManager;
import java.util.List;

public class CartService {

    public List<ShoppingCart> getCartItems() {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        String query = "SELECT c FROM ShoppingCart c";
        List<ShoppingCart> resultList = entityManager.createQuery(query, ShoppingCart.class).getResultList();
        entityManager.close();
        return resultList;
    }

    public ShoppingCart addProductToCart(int productId, int quantity) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        Product product = entityManager.find(Product.class, productId);

        if (product.getStockQuantity() >= quantity) {
            entityManager.getTransaction().begin();
            try {
                product.setStockQuantity(product.getStockQuantity() - quantity);
                entityManager.merge(product);

                ShoppingCart cart = new ShoppingCart();
                cart.setProduct(product);
                cart.setQuantity(quantity);
                entityManager.persist(cart);

                entityManager.getTransaction().commit();

                return cart;
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                throw e;
            }finally {
                entityManager.close();
            }
        } else {
            throw new RuntimeException("Insufficient products in stock.");
        }
    }

    public void removeProductFromCart(int cartId) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        ShoppingCart cart = entityManager.find(ShoppingCart.class, cartId);
        if (cart != null) {
            Product product = cart.getProduct();
            entityManager.getTransaction().begin();
            try {
                product.setStockQuantity(product.getStockQuantity() + cart.getQuantity());
                entityManager.merge(product);

                entityManager.remove(cart);

                entityManager.getTransaction().commit();
            } catch (Exception e) {
                entityManager.getTransaction().rollback();
                throw e;
            }finally {
                entityManager.close();
            }
        } else {
            throw new RuntimeException("Product not found in cart.");
        }
    }

    public void updateCartItemQuantity(int cartId, int newQuantity) {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        ShoppingCart cart = entityManager.find(ShoppingCart.class, cartId);

        if (cart != null) {
            Product product = cart.getProduct();
            int quantityDifference = newQuantity - cart.getQuantity();

            if (product.getStockQuantity() >= quantityDifference) {
                entityManager.getTransaction().begin();
                try {
                    product.setStockQuantity(product.getStockQuantity() - quantityDifference);
                    entityManager.merge(product);

                    cart.setQuantity(newQuantity);
                    entityManager.merge(cart);

                    entityManager.getTransaction().commit();
                } catch (Exception e) {
                    entityManager.getTransaction().rollback();
                    throw e;
                }
            } else {
                throw new RuntimeException("Insufficient products in stock.");
            }
        } else {
            throw new RuntimeException("Product not found in cart.");
        }
    }

    public void clearCart() {
        EntityManager entityManager = EntityManagerUtil.getEntityManager();
        String query = "SELECT c FROM ShoppingCart c";
        List<ShoppingCart> cart = entityManager.createQuery(query, ShoppingCart.class).getResultList();
        entityManager.getTransaction().begin();
        try {
            for (ShoppingCart item : cart) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                entityManager.merge(product);

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
