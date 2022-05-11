package me.home.chat.server.services;

import me.home.chat.server.models.users.User;

import java.util.List;

public interface UserService {
    boolean checkOfUsersExistence(String userId);
    User getUserById(String userId);
    void deleteUserById(String userId);
    void insertUser(User user);
    void updateUserLastVisit(User user);
    List<String> getUsersIdByChatId(long id);
}
