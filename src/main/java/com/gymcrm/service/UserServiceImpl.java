package com.gymcrm.service;

import com.gymcrm.dao.UserDAO;
import com.gymcrm.exception.AuthenticationException;
import com.gymcrm.exception.NotFoundException;
import com.gymcrm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return userDAO.findByUsername(username)
                .map(user -> {
                    boolean ok = passwordEncoder.matches(password, user.getPassword());
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
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AuthenticationException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        logger.info("Password changed for username: {}", username);
    }
}
