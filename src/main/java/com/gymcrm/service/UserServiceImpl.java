package com.gymcrm.service;

import com.gymcrm.dao.UserDAO;
import com.gymcrm.exception.AuthenticationException;
import com.gymcrm.exception.NotFoundException;
import com.gymcrm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        //TODO: will be refactored to use hashed password
        if (username == null || password == null) {
            return false;
        }
        return userDAO.findByUsername(username)
                .map(user -> {
                    boolean ok = password.equals(user.getPassword());
                    if (!ok) {
                        logger.warn("Authentication failed for username: {}", username);
                    }
                    return ok;
                })
                .orElseGet(() -> {
                    logger.warn("Authentication failed: user not found for username: {}", username);
                    return false;
                });
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
        if (!oldPassword.equals(user.getPassword())) {
            throw new AuthenticationException("Old password is incorrect");
        }
        user.setPassword(newPassword);
        logger.info("Password changed for username: {}", username);
    }
}
