package me.home.chat.server.services.impls;

import me.home.chat.server.models.users.User;
import me.home.chat.server.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public boolean checkOfUsersExistence(String userId) {
        return false;
    }

    @Override
    public User getUserById(String userId) {
        return null;
    }

    @Override
    public void deleteUserById(String userId) {

    }

    @Override
    public void insertUser(User user) {

    }

    @Override
    public void updateUserLastVisit(User user) {

    }

    @Override
    public List<String> getUsersIdByChatId(long id) {
        return null;
    }
}
