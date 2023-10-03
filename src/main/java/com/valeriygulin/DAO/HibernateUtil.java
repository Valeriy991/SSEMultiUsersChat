package com.valeriygulin.DAO;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory = null;
    static {
        try {
            sessionFactory = new Configuration().configure("hibernatemysql.cfg.xml").buildSessionFactory();
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
    }
    public static SessionFactory getSessionFactory(){
        return sessionFactory;
    }
    public static Session getSession1(){
        Session session1 = null;
        try {
            session1 = getSessionFactory().openSession();
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        return session1;
    }

    public static void closeSession(Session session){
        if(session != null && session.isOpen()){
            session.close();
        }
    }
}