package com.Repository.Hibernate;

import com.Domain.Participant;
import com.Domain.ParticipantCompetition;
import com.Repository.Interface.IParticipantRepository;
import com.Util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * ParticipantHibernateRepository is a concrete implementation of the IParticipantRepository interface
 * using Hibernate for ORM.
 */
@Repository
public class ParticipantHibernateRepository implements IParticipantRepository {
    private static final Logger logger = LoggerFactory.getLogger(ParticipantHibernateRepository.class);

    @Override
    public Participant add(Participant p) {
        logger.info("Adding participant: {}", p);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(p);
            tx.commit();
            logger.info("Participant saved with ID: {}", p.getParticipantID());
            return p;
        } catch (Exception e) {
            logger.error("Error adding participant", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int id, Participant p) {
        logger.info("Updating participant ID: {}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Participant participant = session.get(Participant.class, id);
            if (participant != null) {
                participant.setName(p.getName());
                participant.setAge(p.getAge());
                logger.info("Participant updated successfully.");
            } else {
                logger.warn("No participant found with ID: {}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("Error updating participant", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(int id) {
        logger.info("Removing participant with ID: {}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Participant p = session.get(Participant.class, id);
            if (p != null) {
                session.remove(p);
                logger.info("Participant removed successfully.");
            } else {
                logger.warn("No participant found with ID: {}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("Error removing participant", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Participant> getAll() {
        logger.info("Fetching all participants.");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Participant> participants = session.createQuery("from Participant", Participant.class).list();
            logger.info("Successfully retrieved {} participants.", participants.size());
            return participants;
        } catch (Exception e) {
            logger.error("Error fetching participants", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Participant getById(int id) {
        logger.info("Fetching participant with ID: {}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Participant p = session.get(Participant.class, id);
            if (p != null) {
                logger.info("Participant found: {}", p);
            } else {
                logger.warn("No participant found with ID: {}", id);
            }
            return p;
        } catch (Exception e) {
            logger.error("Error fetching participant", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Participant> getParticipantsForCompetition(int compID) {
        logger.info("Fetching participants for competition ID: {}", compID);
        String hql = """
            select p\s
              from Participant p
              join ParticipantCompetition pc\s
                on p.participantID = pc.participantID
             where pc.competitionID = :cid
           \s""";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Participant> q = session.createQuery(hql, Participant.class);
            q.setParameter("cid", compID);
            List<Participant> list = q.list();
            logger.info("Found {} participants", list.size());
            return new ArrayList<>(list);
        } catch (Exception e) {
            logger.error("Error fetching participants", e);
            throw new RuntimeException(e);
        }
    }
}
