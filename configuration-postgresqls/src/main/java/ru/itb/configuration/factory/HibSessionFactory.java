package ru.itb.configuration.factory;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.itb.configuration.intercepter.AuditInterceptor;

import javax.persistence.EntityManager;

public class HibSessionFactory {
    private static volatile HibSessionFactory instance;
    private SessionFactory sessionFactory;
    private EntityManager entityManager;


    private HibSessionFactory(){
        try{
            sessionFactory = new Configuration().configure().setInterceptor(new AuditInterceptor()).buildSessionFactory();
            entityManager = sessionFactory.createEntityManager();
        }catch (Throwable ex) {
            //TODO add to log "Failed to create sessionFactory object."
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }


    public static HibSessionFactory getInstance() {
        HibSessionFactory localInstance = instance;
        if (localInstance == null) {
            synchronized (HibSessionFactory.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new HibSessionFactory();
                }
            }
        }
        return localInstance;
    }

    public synchronized SessionFactory getFactory(){
        return this.sessionFactory;
//        return new Configuration().configure().buildSessionFactory();
    }

    public synchronized EntityManager getEntityManager(){
        return this.entityManager;
    }
}
