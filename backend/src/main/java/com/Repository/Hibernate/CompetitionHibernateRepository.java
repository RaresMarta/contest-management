package com.Repository.Hibernate;

import com.Domain.Competition;
import com.Domain.ParticipantCompetition;
import com.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import com.Repository.Interface.ICompetitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CompetitionHibernateRepository is a concrete implementation of the ICompetitionRepository interface
 * using Hibernate for ORM.
 */
@Repository
public class CompetitionHibernateRepository implements ICompetitionRepository {
    private static final Logger logger = LoggerFactory.getLogger(CompetitionHibernateRepository.class);

    @Override
    public Competition add(Competition c) {
        logger.info("Adding competition: {}", c);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(c);
            tx.commit();
            logger.info("Saved Competition with ID: {}", c.getCompetitionID());
            return c;
        } catch (Exception e) {
            logger.error("Error adding competition", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int id, Competition c) {
        logger.info("Updating competition ID: {}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Competition comp = session.get(Competition.class, id);
            if (comp != null) {
                comp.setType(c.getType());
                comp.setAgeCategory(c.getAgeCategory());
                comp.setNrOfParticipants(c.getNrOfParticipants());
                logger.info("Competition updated");
            } else {
                logger.warn("Competition not found");
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("Error updating competition", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(int id) {
        logger.info("Removing competition ID: {}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Competition comp = session.get(Competition.class, id);
            if (comp != null) {
                session.remove(comp);
                logger.info("Competition removed");
            } else {
                logger.warn("Competition not found");
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("Error removing competition", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Competition> getAll() {
        logger.info("Fetching all competitions");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Competition> list = session.createQuery("from Competition", Competition.class).list();
            logger.info("Retrieved {} competitions", list.size());
            return list;
        } catch (Exception e) {
            logger.error("Error fetching competitions", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Competition getById(int id) {
        logger.info("Fetching competition ID: {}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Competition comp = session.get(Competition.class, id);
            if (comp != null) {
                logger.info("Competition found: {}", comp);
            } else {
                logger.warn("Competition not found");
            }
            return comp;
        } catch (Exception e) {
            logger.error("Error fetching competition", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Competition> getCompetitionByTypeAndAge(String type, String ageCategory) {
        logger.info("Fetching competitions of type '{}' and age '{}'", type, ageCategory);
        String hql = "from Competition c where c.type = :type and c.ageCategory = :age";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Competition> q = session.createQuery(hql, Competition.class);
            q.setParameter("type", type);
            q.setParameter("age", ageCategory);
            List<Competition> results = q.list();
            logger.info("Retrieved {} competitions", results.size());
            return results;
        } catch (Exception e) {
            logger.error("Error fetching competitions by type and age", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Competition> getCompetitionsByType(String type) {
        logger.info("Fetching competitions of type '{}'", type);
        String hql = "from Competition c where c.type = :type";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Competition> q = session.createQuery(hql, Competition.class);
            q.setParameter("type", type);
            List<Competition> results = q.list();
            logger.info("Retrieved {} competitions", results.size());
            return results;
        } catch (Exception e) {
            logger.error("Error fetching competitions by type", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Competition> getCompetitionsByAge(String ageCategory) {
        logger.info("Fetching competitions of age '{}'", ageCategory);
        String hql = "from Competition c where c.ageCategory = :age";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Competition> q = session.createQuery(hql, Competition.class);
            q.setParameter("age", ageCategory);
            List<Competition> results = q.list();
            logger.info("Retrieved {} competitions", results.size());
            return results;
        } catch (Exception e) {
            logger.error("Error fetching competitions by age", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void incrementParticipantCount(int compID) {
        logger.info("Incrementing participant count for competition ID: {}", compID);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Competition comp = session.get(Competition.class, compID);
            if (comp != null) {
                comp.setNrOfParticipants(comp.getNrOfParticipants() + 1);
                logger.info("New participant count: {}", comp.getNrOfParticipants());
            } else {
                logger.warn("Competition not found");
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("Error incrementing count", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void enrollParticipant(int participantID, int competitionID) {
        logger.info("Enrolling participant {} in competition {}", participantID, competitionID);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            // Check if the participant is already enrolled in 2 competitions
            Long count = session.createQuery(
                    "select count(pc) from ParticipantCompetition pc where pc.participantID = :pid",
                    Long.class
            ).setParameter("pid", participantID).uniqueResult();
            if (count != null && count >= 2) {
                logger.error("Participant ID:{} is already enrolled in {} competitions", participantID, count);
                tx.rollback();
                throw new RuntimeException("Participant is already enrolled in 2 competitions");
            }
            // Enroll the participant in the competition(s)
            ParticipantCompetition pc = new ParticipantCompetition(participantID, competitionID);
            session.persist(pc);
            tx.commit();
            logger.info("Participant {} successfully enrolled in competition {}", participantID, competitionID);
        } catch (Exception e) {
            logger.error("Error enrolling participant in competition", e);
            throw new RuntimeException(e);
        }
    }
}
