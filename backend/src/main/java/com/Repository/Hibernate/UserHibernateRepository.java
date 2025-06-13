package com.Repository.Hibernate;

import com.Domain.User;
import com.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import com.Repository.Interface.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserHibernateRepository is a concrete implementation of the IUserRepository interface
 * using Hibernate for ORM.
 */
@Repository
public class UserHibernateRepository implements IUserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserHibernateRepository.class);

    @Override
    public User add(User user) {
        logger.info("Adding user: {}", user);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            logger.info("Saved User with ID: {}", user.getUserID());
            return user;
        } catch (Exception e) {
            logger.error("Error adding user", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int id, User user) {
        logger.info("Updating user ID: {}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User existingUser = session.get(User.class, id);
            if (existingUser != null) {
                existingUser.setUserName(user.getUserName());
                existingUser.setPassword(user.getPassword());
                logger.info("User updated");
            } else {
                logger.warn("User not found");
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("Error updating user", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(int id) {
        logger.info("Removing user ID: {}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User u = session.get(User.class, id);
            if (u != null) {
                session.remove(u);
                logger.info("User removed");
            } else {
                logger.warn("User not found");
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("Error removing user", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAll() {
        logger.info("Fetching all users");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> list = session.createQuery("from User", User.class).list();
            logger.info("Retrieved {} users", list.size());
            return list;
        } catch (Exception e) {
            logger.error("Error fetching users", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getById(int id) {
        logger.info("Fetching user ID: {}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            if (user != null) {
                logger.info("User found: {}", user);
                return user;
            } else {
                logger.warn("User not found");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error fetching user", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> authenticate(String userName, String password) {
        logger.info("Authenticating user: {}", userName);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> q = session.createQuery(
                    "from User where userName = :u and password = :p", User.class
            );
            q.setParameter("u", userName);
            q.setParameter("p", password);
            User result = q.uniqueResult();
            if (result != null) {
                logger.info("Authentication successful");
                return Optional.of(result);
            } else {
                logger.warn("Authentication failed");
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error during authentication", e);
            throw new RuntimeException(e);
        }
    }
}
