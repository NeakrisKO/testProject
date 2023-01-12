package ru.itb.web.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.itb.testautomation.core.utils.ObjectUpdater;
import ru.itb.web.config.UserSessionManager;
import ru.itb.web.model.security.User;
import ru.itb.web.model.ui.UILoggedUserInfo;
import ru.itb.web.model.ui.UIUserInfo;
import ru.itb.web.service.intf.UserService;
import ru.itb.web.util.UIObjectConverter;
import ru.itb.web.util.UserSessionInfo;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {
    private static final Logger LOGGER = LogManager.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "users", method = RequestMethod.GET)
    @ResponseBody
    public List<UIUserInfo> getUserList() {
        return UIObjectConverter.getInstance().convertUsers(userService.getAllUsers());
    }


    @RequestMapping(value = "logged_users", method = RequestMethod.GET)
    @ResponseBody
    public List<UILoggedUserInfo> getLoggedUserList() {
        ArrayList<UILoggedUserInfo> loggedUsers = new ArrayList<>();

        for(Map.Entry<String, UserSessionInfo> entry : UserSessionManager.getInstance().getInfo().entrySet()) {
            String computerName = "";
            try {
                InetAddress address;
                address = InetAddress.getByName(entry.getValue().getRemoteAddr());
                computerName = address.getHostName();
            } catch (Exception exc) {
                LOGGER.error("Cant get computer name",exc);
            }
            User user = userService.getByLogin(entry.getKey());
            UILoggedUserInfo userInfo = new UILoggedUserInfo(entry.getKey(), user.getLastName()+" " + user.getFirstName(), entry.getValue().getRemoteAddr(), computerName);
            loggedUsers.add(userInfo);
        }
        return loggedUsers;
    }

    @RequestMapping(value = "kick/{user}", method = RequestMethod.GET)
    @ResponseBody
    public void kick(@PathVariable("user") String user) {
        UserSessionManager.getInstance().kickUser(user);
    }

    @RequestMapping(value = "is_admin", method = RequestMethod.GET)
    @ResponseBody
    public boolean isAdmin() {

        boolean isAdmin = false;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            for (GrantedAuthority auth : ((UserDetails) principal).getAuthorities()) {
                if ("ROLE_ADMIN".equals(auth.getAuthority())) {
                    isAdmin = true;
                }
            }
        }
        return isAdmin;
    }

    @RequestMapping(value = "add_user", method = RequestMethod.POST)
    @ResponseBody
    public void createUser(@RequestBody User user) {
        if (!userService.isUserUnique(user.getId(), user.getLogin())) {
            LOGGER.error("User not Unique");
        } else {
            userService.saveUser(user);
        }
    }

    @RequestMapping(value = "update_user/{updatePassword}", method = RequestMethod.POST)
    @ResponseBody
    public void updateUser(@PathVariable("updatePassword") Boolean updatePassword, @RequestBody UIUserInfo user) {
        if (!userService.isUserUnique(user.getId(), user.getLogin())) {
            LOGGER.error("User not Unique");
        } else {
            User updatedUser = updateUser(user, updatePassword);
            userService.updateUser(updatedUser, updatePassword);
        }
    }

    private User updateUser(UIUserInfo uiUserInfo, Boolean updatePassword) {
        User user = userService.getById(uiUserInfo.getId());
        if (updatePassword) {
            user.setPassword(user.getPassword());
        }
        ObjectUpdater updater = new ObjectUpdater();
        updater.update(user, uiUserInfo);
        return user;
    }
}
