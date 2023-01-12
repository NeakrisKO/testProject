package ru.itb.web.service.intf;

import ru.itb.web.model.security.User;

import java.util.List;

public interface UserService {

    User getById(int id);

    User getByLogin(String login);

    void saveUser(User user);

    void updateUser(User user, Boolean updatePassword);

    void deleteUserByLogin(String login);

    List<User> getAllUsers();

    boolean isUserUnique(Integer id, String login);
}
