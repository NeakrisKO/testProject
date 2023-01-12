package ru.itb.web.dao.intf;

import ru.itb.web.model.security.UserProfile;

import java.util.List;

public interface UserProfileDao {
    List<UserProfile> getAll();

    UserProfile getByType(String type);

    UserProfile getById(Integer id);
}
