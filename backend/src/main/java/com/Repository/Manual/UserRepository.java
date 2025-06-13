package com.Repository.Manual;

import com.Domain.User;
import com.Repository.Interface.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class UserRepository implements IUserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    @Override
    public User add(User user) {
        logger.info("Adding user: {}", user);
        String sql = "INSERT INTO User(userName, password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPassword());
            ps.executeUpdate();
            logger.info("User added successfully.");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    logger.info("Generated ID for new user: {}", id);
                    user.setUserID(id);
                }
            }
            return user;
        } catch (SQLException e) {
            logger.error("Error adding user", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(int id, User user) {
        logger.info("Updating user ID: {}", id);
        String sql = "UPDATE User SET userName = ?, password = ? WHERE userID = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPassword());
            ps.setInt(3, id);
            ps.executeUpdate();
            logger.info("User updated successfully.");
        } catch (SQLException e) {
            logger.error("Error updating user", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(int id) {
        logger.info("Removing user with ID: {}", id);
        String sql = "DELETE FROM User WHERE userID = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            logger.info("User removed successfully.");
        } catch (SQLException e) {
            logger.error("Error removing user", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<User> getAll() {
        logger.info("Fetching all users.");
        String sql = "SELECT * FROM User";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ArrayList<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(new User(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3)
                ));
            }
            logger.info("Retrieved {} users.", users.size());
            return users;
        } catch (SQLException e) {
            logger.error("Error retrieving users", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getById(int id) {
        logger.info("Fetching user with ID: {}", id);
        String sql = "SELECT * FROM User WHERE userID = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3)
                );
                logger.info("User found: {}", user);
                return user;
            } else {
                logger.warn("No user found with ID: {}", id);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error fetching user", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> authenticate(String userName, String password) {
        logger.info("Authenticating user: {}", userName);
        String sql = "SELECT * FROM User WHERE userName = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userName);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3)
                );
                logger.info("User authenticated successfully.");
                return Optional.of(user);
            }

            logger.info("User authentication failed.");
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error authenticating user", e);
            throw new RuntimeException(e);
        }
    }
}
