package ru.itb.web.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itb.web.dao.impl.UserProfileDaoImpl;
import ru.itb.web.dao.intf.UserProfileDao;
import ru.itb.web.model.security.UserProfile;
import ru.itb.web.service.intf.UserProfileService;

import java.util.List;

@Service("userProfileService")
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private UserProfileDao dao = new UserProfileDaoImpl();

    public UserProfile getById(int id) {
        return dao.getById(id);
    }

    public UserProfile getByType(String type){
        return dao.getByType(type);
    }

    public List<UserProfile> getAll() {
        return dao.getAll();
    }
}
