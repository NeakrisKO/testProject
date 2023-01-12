package ru.itb.web.factory;

public class HibSessionFactory {
    private static volatile HibSessionFactory instance;
    private SessionFactory sessionFactory;


    private HibSessionFactory(){
        try{
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }catch (Throwable ex) {
            //TODO add to log "Failed to create sessionFactory object."
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

    public SessionFactory getFactory(){
        return this.sessionFactory;
    }
}
