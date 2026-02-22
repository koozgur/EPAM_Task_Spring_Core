package com.gymcrm.dao;

import com.gymcrm.model.User;

import java.util.Optional;

public interface UserDAO {

    /**
     * Used by UserService for authentication
     */
    Optional<User> findByUsername(String username);
}
