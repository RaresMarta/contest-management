package com.Service;

import com.Domain.User;
import com.Repository.Interface.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final IUserRepository userRepo;

    // ----- CRUD -----
    public User add(User u) {
        return userRepo.add(u);
    }

    public List<User> getAll() {
        return userRepo.getAll();
    }

    public User getById(int id) {
        return userRepo.getById(id);
    }

    public void update(int id, User u) {
        userRepo.update(id, u);
    }

    public void remove(int id) {
        userRepo.remove(id);
    }

    /** Authenticate user with username and password.
     * @param userName the username of the user
     * @param password the password of the user
     * @return an Optional containing the User if authentication is successful, or empty if not
     */
    public Optional<User> authenticate(String userName, String password) {
        return userRepo.authenticate(userName, password);
    }
}
