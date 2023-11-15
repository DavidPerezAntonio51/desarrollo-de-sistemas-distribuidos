package mx.ipn.escom.tarearestapp;

import mx.ipn.escom.tarearestapp.Utils.EntityManagerUtil;
import mx.ipn.escom.tarearestapp.Utils.ServiceFactory;

import javax.persistence.EntityManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Inicializa el EntityManagerFactory para que no tarde en las proximas conexiones
        EntityManager em = null;
        try {
            em = EntityManagerUtil.getEntityManager();
        } finally {
            if (em != null) {
                em.close();
            }
        }
        // Inicializa las instancias de tus clases de servicio
        ServiceFactory.getArticuloService();
        ServiceFactory.getCarritoService();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        EntityManagerUtil.closeEntityManagerFactory();
    }
}
