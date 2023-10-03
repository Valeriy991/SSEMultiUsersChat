package com.valeriygulin.DAO;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;


public class DAO {
    private static Session openedSession = null;

    public static void addObject(Object obj) {
        Session session = HibernateUtil.getSession1();
        try {
            session.beginTransaction();
            session.save(obj);
            session.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            throw new IllegalArgumentException("Entity Already Exists!");
        }
        finally{
            HibernateUtil.closeSession(session);
        }
    }

    public static Object getObjectById(Long id, Class className) {
        Session session = HibernateUtil.getSession1();
        Object obj = session.get(className, id);
        //HibernateUtil.closeSession(session);
    /*Выполняется lazy, используйте closeOpenedSession()
    после каждого вызова этого метода*/
        openedSession = session;
        return obj;
    }

    public static Object getObjectByParam(String prm, Object prmO, Class className) {
        Session session = HibernateUtil.getSession1();
        session.beginTransaction();
        Object obj = session.createCriteria(className).
                add(Restrictions.eq(prm, prmO)).uniqueResult();
    /*Выполняется lazy, используйте closeOpenedSession()
    после каждого вызова этого метода*/
        openedSession = session;
        session.getTransaction().commit();
        return obj;
    }

    public static Object getObjectByParams(String[] prm, Object[] prmO, Class className) {
        Session session = HibernateUtil.getSession1();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(className);
        for (int i = 0; i < prm.length; i++)
            criteria.add(Restrictions.eq(prm[i], prmO[i]));
        Object obj = criteria.uniqueResult();
    /*Выполняется lazy, используйте closeOpenedSession()
    после каждого вызова этого метода*/
        openedSession = session;
        session.getTransaction().commit();
        return obj;
    }

    public static List getObjectsByParams(String[] prm, Object[] prmO, Class className) {
        Session session = HibernateUtil.getSession1();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(className);
        for (int i = 0; i < prm.length; i++)
            criteria.add(Restrictions.eq(prm[i], prmO[i]));
        List obj = criteria.list();
    /*Выполняется lazy, используйте closeOpenedSession()
    после каждого вызова этого метода*/
        openedSession = session;
        session.getTransaction().commit();
        return obj;
    }

    public static List getObjectsByParam(String prm, Object prmO, Class className) {
        Session session = HibernateUtil.getSession1();
        session.beginTransaction();
        List obj = session.createCriteria(className).
                add(Restrictions.eq(prm, prmO)).list();
    /*Выполняется lazy, используйте closeOpenedSession()
    после каждого вызова этого метода*/
        openedSession = session;
        session.getTransaction().commit();
        return obj;
    }

    public static void deleteObjectById(Long id, Class className) {
        Session session = HibernateUtil.getSession1();
        session.beginTransaction();
        Object obj = session.get(className, id);
        session.delete(obj);
        session.getTransaction().commit();
        HibernateUtil.closeSession(session);
    }

    public static List getAllObjects(Class className) {
        Session session = HibernateUtil.getSession1();
        List lst = session.createCriteria(className).list();
    /*Выполняется lazy, используйте closeOpenedSession()
    после каждого вызова этого метода*/
        openedSession = session;
        return lst;
    }

    public static void deleteObject(Object obj) {
        Session session = HibernateUtil.getSession1();
        session.beginTransaction();
        session.delete(obj);
        session.getTransaction().commit();
        HibernateUtil.closeSession(session);
    }

    public static void updateObject(Object obj) {
        Session session = HibernateUtil.getSession1();
        try {
            session.beginTransaction();
            session.update(obj);
            session.getTransaction().commit();
        } catch (ConstraintViolationException e) {
            session.beginTransaction().rollback();
            throw new IllegalArgumentException("Entity doesn't exist");
        }
        HibernateUtil.closeSession(session);
    }

    public static void closeOpenedSession() {
        if (openedSession != null && openedSession.isOpen()) {
            openedSession.close();
            //openedSession.disconnect();
        }
    }
}
