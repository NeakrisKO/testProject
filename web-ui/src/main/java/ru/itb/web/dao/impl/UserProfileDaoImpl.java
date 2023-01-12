package ru.itb.web.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.itb.web.dao.intf.UserProfileDao;
import ru.itb.web.factory.HibSessionFactory;
import ru.itb.web.model.security.UserProfile;

import java.util.List;

public class UserProfileDaoImpl implements UserProfileDao {

    public UserProfile getById(Integer id) {

        Transaction tx = null;
        UserProfile userProfile = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            userProfile = session.get(UserProfile.class, id);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }

        return userProfile;
    }

    public UserProfile getByType(String type) {
        Session session = HibSessionFactory.getInstance().getFactory().openSession();
        Criteria crit = session.createCriteria(UserProfile.class);
        crit.add(Restrictions.eq("type", type));
        return (UserProfile) crit.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<UserProfile> getAll(){
        Session session = HibSessionFactory.getInstance().getFactory().openSession();
        Criteria crit =  session.createCriteria(UserProfile.class);
        crit.addOrder(Order.asc("type"));
        return (List<UserProfile>)crit.list();
    }

}
