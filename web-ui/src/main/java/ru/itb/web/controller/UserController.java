package ru.itb.web.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itb.web.model.security.User;
import ru.itb.web.model.ui.UIUserInfo;
import ru.itb.web.model.ui.UIUserTestCases;
import ru.itb.web.model.ui.graph.TestCaseTotalGraph;
import ru.itb.web.service.intf.UserService;
import ru.itb.web.util.UserInfoUtils;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    private static final Logger LOGGER = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "get_info", method = RequestMethod.GET)
    @ResponseBody
    public UIUserInfo getUserInfo() {

        UIUserInfo uiUserInfo = new UIUserInfo();
        UserInfoUtils.getInstance().getTestCaseTotalGraph("admin");

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            User user = userService.getByLogin(((UserDetails) principal).getUsername());
            uiUserInfo.setFirstName(user.getFirstName());
            uiUserInfo.setLastName(user.getLastName());
            uiUserInfo.setEmail(user.getEmail());
            uiUserInfo.setLogin(user.getLogin());
        }
        return uiUserInfo;
    }

    @RequestMapping(value = "get_total_tc_info", method = RequestMethod.GET)
    @ResponseBody
    public TestCaseTotalGraph getTotalTestCaseInfo() {

        TestCaseTotalGraph testCaseTotalGraph = new TestCaseTotalGraph();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            testCaseTotalGraph = UserInfoUtils.getInstance().getTestCaseTotalGraph(((UserDetails) principal).getUsername());
        }

        return testCaseTotalGraph;
    }

    @RequestMapping(value = "get_user_tc", method = RequestMethod.GET)
    @ResponseBody
    public UIUserTestCases getUserTestCases() {

        UIUserTestCases uiUserTestCases = new UIUserTestCases();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            uiUserTestCases = UserInfoUtils.getInstance().getUserTestCaseList(((UserDetails) principal).getUsername());
        }

        return uiUserTestCases;
    }
}
