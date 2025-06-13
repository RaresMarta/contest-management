package com.Repository.Manual;

import com.Domain.Competition;
import com.Repository.Interface.ICompetitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CompetitionRepository implements ICompetitionRepository {
    private static final Logger logger = LoggerFactory.getLogger(CompetitionRepository.class);

    @Override
    public Competition add(Competition c) {
        logger.info("Adding competition: {}", c);
        String sql = "INSERT INTO Competition(type, ageCategory, nrOfParticipants) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getType());
            ps.setString(2, c.getAgeCategory());
            ps.setInt(3, c.getNrOfParticipants());
            ps.executeUpdate();
            logger.info("Competition added successfully.");
            try(ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    logger.info("Generated ID for new competition: {}", id);
                    c.setCompetitionID(id);
                }
            }
            return c;
        } catch (SQLException e) {
            logger.error("Error adding competition", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int id, Competition c) {
        logger.info("Updating competition ID: {}", id);
        String sql = "UPDATE Competition SET type = ?, ageCategory = ?, nrOfParticipants = ? WHERE competitionID = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getType());
            ps.setString(2, c.getAgeCategory());
            ps.setInt(3, c.getNrOfParticipants());
            ps.setInt(4, id);
            ps.executeUpdate();
            logger.info("Competition updated successfully.");
        } catch (SQLException e) {
            logger.error("Error updating competition", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(int compID) {
        logger.info("Removing competition with ID: {}", compID);
        String sql = "DELETE FROM Competition WHERE competitionID = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, compID);
            ps.executeUpdate();
            logger.info("Competition removed successfully.");
        } catch (SQLException e) {
            logger.error("Error removing competition", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Competition> getAll() {
        logger.info("Fetching all competitions.");
        String sql = "SELECT * FROM Competition";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ArrayList<Competition> competitions = new ArrayList<>();
            while (rs.next()) {
                competitions.add(new Competition(
                        rs.getInt(1),    // ID
                        rs.getString(2), // Type
                        rs.getString(3), // Age Category
                        rs.getInt(4)     // Number of Participants
                ));
            }
            logger.info("Successfully retrieved {} competitions.", competitions.size());
            return competitions;
        } catch (SQLException e) {
            logger.error("Error fetching competitions", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Competition getById(int id) {
        logger.info("Fetching competition with ID: {}", id);
        String sql = "SELECT * FROM Competition WHERE competitionID = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Competition competition = new Competition(
                        rs.getInt(1),    // ID
                        rs.getString(2), // Type
                        rs.getString(3), // Age Category
                        rs.getInt(4)     // Number of Participants
                );
                logger.info("Competition found: {}", competition);
                return competition;
            } else {
                logger.warn("No competition found with ID: {}", id);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error fetching competition", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Competition> getCompetitionByTypeAndAge(String type, String ageCategory) {
        logger.info("Fetching competitions of type: {} and age category: {}", type, ageCategory);
        String sql = "SELECT * FROM Competition WHERE type = ? AND ageCategory = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, type);
            ps.setString(2, ageCategory);
            ResultSet rs = ps.executeQuery();
            ArrayList<Competition> competitions = new ArrayList<>();
            while (rs.next()) {
                competitions.add(new Competition(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4)
                ));
            }
            logger.info("Successfully retrieved {} competitions of type: {} and age category: {}", competitions.size(), type, ageCategory);
            return competitions;
        } catch (SQLException e) {
            logger.error("Error fetching competitions by type and age category", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Competition> getCompetitionsByType(String type) {
        logger.info("Fetching competitions of type: {}", type);
        String sql = "SELECT * FROM Competition WHERE type = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            ArrayList<Competition> competitions = new ArrayList<>();
            while (rs.next()) {
                competitions.add(new Competition(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4)
                ));
            }
            logger.info("Successfully retrieved {} competitions of type: {}", competitions.size(), type);
            return competitions;
        } catch (SQLException e) {
            logger.error("Error fetching competitions by type", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Competition> getCompetitionsByAge(String age) {
        logger.info("Fetching competitions of age category: {}", age);
        String sql = "SELECT * FROM Competition WHERE ageCategory = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, age);
            ResultSet rs = ps.executeQuery();
            ArrayList<Competition> competitions = new ArrayList<>();
            while (rs.next()) {
                competitions.add(new Competition(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4)
                ));
            }
            logger.info("Successfully retrieved {} competitions of age category: {}", competitions.size(), age);
            return competitions;
        } catch (SQLException e) {
            logger.error("Error fetching competitions by age category", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void incrementParticipantCount(int competitionId) {
        logger.info("Incrementing participant count for competition ID: {}", competitionId);
        String sql = "UPDATE Competition SET nrOfParticipants = nrOfParticipants + 1 WHERE competitionID = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, competitionId);
            ps.executeUpdate();
            logger.info("Participant count incremented.");
        } catch (SQLException e) {
            logger.error("Error incrementing participant count", e);
        }
    }

    @Override
    public void enrollParticipant(int participantID, int competitionID) {
        logger.info("Enrolling participant {} in competition {}", participantID, competitionID);
        String sqlCheckCount = "SELECT COUNT(*) FROM ParticipantCompetition WHERE participantID = ?";
        String sqlInsert = "INSERT INTO ParticipantCompetition(participantID, competitionID) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement ps = conn.prepareStatement(sqlCheckCount);
            ps.setInt(1, participantID);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) >= 2) {
                logger.error("Participant {} is already enrolled in 2 competitions", participantID);
                throw new RuntimeException("Participant is already enrolled in 2 competitions");
            }

            PreparedStatement ps2 = conn.prepareStatement(sqlInsert);
            ps2.setInt(1, participantID);
            ps2.setInt(2, competitionID);
            ps2.executeUpdate();
            logger.info("Participant {} enrolled in competition {}", participantID, competitionID);
        } catch (SQLException e) {
            logger.error("Error enrolling participant in competition", e);
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                logger.warn("Participant {} already in competition {}", participantID, competitionID);
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
