package com.Repository.Manual;

import com.Domain.Participant;
import com.Repository.Interface.IParticipantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

public class ParticipantRepository implements IParticipantRepository {
    private static final Logger logger = LoggerFactory.getLogger(ParticipantRepository.class);

    @Override
    public Participant add(Participant p) {
        logger.info("Adding participant: {}", p);
        String sql = "INSERT INTO Participant(name, age) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, p.getName());
            ps.setInt(2, p.getAge());
            ps.executeUpdate();
            logger.info("Participant added successfully.");
            try(ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next()) {
                    int id = rs.getInt(1);
                    logger.info("Generated ID for new participant: {}", id);
                    p.setParticipantID(id);
                }
            }
            return p;
        } catch (SQLException e) {
            logger.error("Error adding participant", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int id, Participant p) {
        logger.info("Updating participant ID: {}", id);
        String sql = "UPDATE Participant SET name = ?, age = ? WHERE participantID = ?";

        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, p.getName());
            ps.setInt(2, p.getAge());
            ps.setInt(3, id);
            ps.executeUpdate();
            logger.info("Participant updated successfully.");
        } catch (SQLException e) {
            logger.error("Error updating participant", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(int id) {
        logger.info("Removing participant with ID: {}", id);
        String sqlDeleteParticipant = "DELETE FROM Participant WHERE participantID = ?";
        String sqlDeleteLinks = "DELETE FROM ParticipantCompetition WHERE participantID = ?";

        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement ps1 = conn.prepareStatement(sqlDeleteParticipant);
            PreparedStatement ps2 = conn.prepareStatement(sqlDeleteLinks);
            ps1.setInt(1, id);
            ps2.setInt(1, id);
            ps1.executeUpdate();
            ps2.executeUpdate();
            logger.info("Participant removed successfully.");
        } catch (SQLException e) {
            logger.error("Error removing participant", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Participant> getAll() {
        logger.info("Fetching all participants.");
        String sql = "SELECT * FROM Participant";

        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ArrayList<Participant> participants = new ArrayList<>();
            while (rs.next()) {
                participants.add(new Participant(
                        rs.getInt(1),     // ID
                        rs.getString(2),  // name
                        rs.getInt(3)));   // age
            }
            logger.info("Successfully retrieved {} participants.", participants.size());
            return participants;
        } catch (SQLException e) {
            logger.error("Error fetching participants", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Participant getById(int id) {
        logger.info("Fetching participant with ID: {}", id);
        String sql = "SELECT * FROM Participant WHERE participantID = ?";

        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Participant participant = new Participant(
                        rs.getInt(1),     // ID
                        rs.getString(2),  // name
                        rs.getInt(3));    // age
                logger.info("Participant found: {}", participant);
                return participant;
            } else {
                logger.warn("No participant found with ID: {}", id);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error fetching participant", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Participant> getParticipantsForCompetition(int compID) {
        logger.info("Fetching participants for competition with ID: {}", compID);
        String sql = "SELECT p.* " +
                "FROM Participant p " +
                "INNER JOIN ParticipantCompetition pc " +
                "ON p.participantID = pc.participantID " +
                "WHERE pc.competitionID = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, compID);
            ResultSet rs = ps.executeQuery();
            ArrayList<Participant> participants = new ArrayList<>();
            while (rs.next()) {
                participants.add(new Participant(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3)
                ));
            }
            logger.info("Successfully retrieved {} participants for competition with ID: {}", participants.size(), compID);
            return participants;
        } catch (SQLException e) {
            logger.error("Error fetching participants for competition with ID: {}", compID, e);
            throw new RuntimeException(e);
        }
    }
}
