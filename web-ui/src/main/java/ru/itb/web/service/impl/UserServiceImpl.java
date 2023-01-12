package ru.itb.web.service.impl;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itb.web.dao.impl.UserDaoImpl;
import ru.itb.web.dao.intf.UserDao;
import ru.itb.web.model.security.User;
import ru.itb.web.service.intf.UserService;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    private UserDao dao = new UserDaoImpl();

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User getById(int id) {
        return dao.getById(id);
    }

    public User getByLogin(String login) {
        return dao.getByLogin(login);
    }

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        dao.save(user);
    }

    public void updateUser(User user, Boolean updatePassword) {
        User entity = dao.getById(user.getId());
        if(entity!=null){
            if(updatePassword){
                entity.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            entity.setFirstName(user.getFirstName());
            entity.setLastName(user.getLastName());
            entity.setEmail(user.getEmail());
            entity.setUserProfiles(user.getUserProfiles());
            dao.update(entity);
        }
    }


    public void deleteUserByLogin(String login) {
        dao.deleteByLogin(login);
    }

    public List<User> getAllUsers() {
        return dao.getAllUsers();
    }

    public boolean isUserUnique(Integer id, String login) {
        User user = getByLogin(login);
        return ( user == null || ((id != null) && (user.getId().equals(id))));
    }

}
