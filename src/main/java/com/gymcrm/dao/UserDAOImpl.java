package com.gymcrm.dao;

import com.gymcrm.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class UserDAOImpl implements UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return entityManager
                .createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }
}
