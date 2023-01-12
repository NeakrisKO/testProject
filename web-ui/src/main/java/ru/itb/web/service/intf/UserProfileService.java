package ru.itb.web.service.intf;

import ru.itb.web.model.security.UserProfile;

import java.util.List;

public interface UserProfileService {
    UserProfile getById(int id);

    UserProfile getByType(String type);

    List<UserProfile> getAll();
}
