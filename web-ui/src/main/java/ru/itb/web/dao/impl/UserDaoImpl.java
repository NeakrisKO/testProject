package ru.itb.web.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.itb.web.dao.intf.UserDao;
import ru.itb.web.factory.HibSessionFactory;
import ru.itb.web.model.security.User;

import java.util.List;

@Repository("userDao")
public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LogManager.getLogger(UserDaoImpl.class);

    public User getById(Integer id) {
        Transaction tx = null;
        User user = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            user = session.get(User.class, id);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        if(user!=null){
            Hibernate.initialize(user.getUserProfiles());
        }
        return user;
    }

    public User getByLogin(String login) {
        Session session = HibSessionFactory.getInstance().getFactory().openSession();
        Criteria crit = session.createCriteria(User.class);
        crit.add(Restrictions.eq("login", login));
        User user = (User)crit.uniqueResult();
        if(user!=null){
            Hibernate.initialize(user.getUserProfiles());
        }
        return user;
    }

    @SuppressWarnings("unchecked")
    public List<User> getAllUsers() {
        Session session = HibSessionFactory.getInstance().getFactory().openSession();
        Criteria criteria = session.createCriteria(User.class).addOrder(Order.asc("firstName"));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return (List<User>) criteria.list();
    }

    public void save(User user) {
        Transaction tx = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(user);
            tx.commit();
        } catch (HibernateException exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't save user: [%s]",user.getLogin()),exc);
        }
    }

    public void update(User user) {
        Transaction tx = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(user);
            tx.commit();
        } catch (HibernateException exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't update user: [%s]",user.getLogin()),exc);
        }
    }

    public void deleteByLogin(String login) {
        Session session = HibSessionFactory.getInstance().getFactory().openSession();
        Criteria crit = session.createCriteria(User.class);
        crit.add(Restrictions.eq("login", login));
        User user = (User)crit.uniqueResult();
        session.delete(user);
    }

}
