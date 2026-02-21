package com.gymcrm.service;

public interface UserService {

    boolean authenticate(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);
}
