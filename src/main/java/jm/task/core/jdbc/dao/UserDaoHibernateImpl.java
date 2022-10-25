package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.HibernateError;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import java.util.List;

public class UserDaoHibernateImpl extends Util implements UserDao {
    public UserDaoHibernateImpl() {
    }
    Transaction transaction;
    @Override
    public void createUsersTable() {
        try (Session session = getSession()) {
            transaction = session.beginTransaction();
            session.createSQLQuery("create table if not exists users " +
                    "(id integer not null auto_increment primary key, name varchar(15), " +
                    "lastName varchar(25), age integer)")
                    //.addEntity(User.class)
                    .executeUpdate();
            transaction.commit();
        } catch (HibernateError e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void dropUsersTable() {
        try (Session session = getSession()) {
            transaction = session.beginTransaction();
            session.createSQLQuery("drop table if exists users")
                    .executeUpdate();
            transaction.commit();
        } catch (HibernateError e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (Session session = getSession()) {
            transaction = session.beginTransaction();
            session.save(new User(name, lastName, age));
            transaction.commit();
            System.out.println("User с именем – " + name + " добавлен в базу данных");
        } catch (HibernateError e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public void removeUserById(long id) {
        try (Session session = getSession()) {
            transaction = session.beginTransaction();
            session.createQuery("delete User where id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            transaction.commit();
        } catch (HibernateError e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public List<User> getAllUsers() {
        try (Session session = getSession()) {
            transaction = session.beginTransaction();
            TypedQuery<User> query = session.createQuery("from User", User.class);
            List<User> userList = query.getResultList();
            transaction.commit();
            return userList;
        } catch (HibernateError e) {
            transaction.rollback();
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void cleanUsersTable() {
        try (Session session = getSession()) {
            transaction = session.beginTransaction();
            session.createQuery("delete User")
                    .executeUpdate();
            transaction.commit();
        } catch (HibernateError e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }
}
