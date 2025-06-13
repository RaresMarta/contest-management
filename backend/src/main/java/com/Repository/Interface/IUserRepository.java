package com.Repository.Interface;

import com.Domain.User;
import java.util.Optional;

public interface IUserRepository extends RepositoryInterface<User>{
    /** Authenticate a user based on their username and password */
    Optional<User> authenticate(String userName, String password);
}
