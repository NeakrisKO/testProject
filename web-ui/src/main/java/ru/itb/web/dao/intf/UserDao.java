package ru.itb.web.dao.intf;

import ru.itb.web.model.security.User;

import java.util.List;

public interface UserDao {
    User getById(Integer id);

    User getByLogin(String sso);

    void save(User user);

    void update(User user);

    void deleteByLogin(String sso);

    List<User> getAllUsers();
}
