package ru.itb.web.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.itb.testautomation.core.context.TCContext;
import ru.itb.testautomation.core.context.manager.ContextManager;
import ru.itb.testautomation.core.instance.project.intf.Project;
import ru.itb.testautomation.core.instance.testcase.executor.impl.TestCaseGroupExecutor;
import ru.itb.testautomation.core.instance.testcase.TestCaseInstance;
import ru.itb.testautomation.core.instance.testcase.TestCaseStep;
import ru.itb.testautomation.core.instance.testcase.executor.impl.TestCaseStepExecutor;
import ru.itb.testautomation.core.instance.testcase.impl.TestCaseGroupImpl;
import ru.itb.testautomation.core.instance.testcase.intf.TestCaseGroup;
import ru.itb.testautomation.core.manager.CoreObjectManager;
import ru.itb.testautomation.core.user.UserInfo;
import ru.itb.web.model.security.User;
import ru.itb.web.service.intf.UserService;


@Controller
@RequestMapping("/execution")
public class ExecutionController {
    private static final Logger LOGGER = LogManager.getLogger(ExecutionController.class);
    private Project targetProject;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "run/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void run(@PathVariable("id") Integer id) {
        LOGGER.info("Execute TestCase - UI");
        if (id != null) {
            try {
                TestCaseStep testCase = CoreObjectManager.getInstance().getManager().getTC(id, false);
                TestCaseInstance instance = TestCaseStepExecutor.getInstance().execute(testCase.getTestCase(), null, null, null, getCurrentUserInfo(), null);
            } catch (Exception exc) {
                LOGGER.error(String.format("Can not execute TestCase with id: [%s]", id), exc);
            }
        } else LOGGER.error("TestCase id can not be null or empty");
    }

    @RequestMapping(value = "run_group/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void runGroup(@PathVariable("id") Integer id) {
        LOGGER.info("Execute Test case Group - UI");

        if (id != null) {
            try {
                TestCaseGroup testCaseGroup = CoreObjectManager.getInstance().getManager().getTCGroup(id, false);
                if (testCaseGroup != null) {
                    for(Project project : CoreObjectManager.getInstance().getManager().getProjectList(false)) {
                        if ( project.getGroups().stream().filter(entry -> id.equals(Integer.parseInt(entry.getId().toString()))).distinct().count() > 0 ) {
                            targetProject = project;
                        }
                    }
                    TestCaseInstance instance = TestCaseGroupExecutor.getInstance().execute((TestCaseGroupImpl) testCaseGroup, null, null, null, getCurrentUserInfo(), targetProject.getJiraProfile());
                } else LOGGER.warn("[NOT FOUND] TestCase Group with id:" + id);
            } catch (Exception exc) {
                LOGGER.error("Can't execute TestCase Group with id:" + id, exc);
            }
        } else LOGGER.error("TestCase Group can't be null or empty");
    }

//    private void parallelExecute(Integer id) {
//        LOGGER.info("Prepare running");
//        ArrayList<TestCaseGroup> datas = new ArrayList<>();
//        for (int i = 1; i < 9; i++) {
//            TestCaseGroup testCaseGroup = CoreObjectManager.getInstance().getManager().getTCGroup(id, false);
//            datas.add(testCaseGroup);
//        }
//        final int[] counter = {1};
//        LOGGER.info("Execute running");
//        datas.parallelStream().forEach((data) -> {
//            try {
//                TestCaseInstance instance = TestCaseGroupExecutor.getInstance().execute((TestCaseGroupImpl) data, null, null, null);
//                counter[0] = counter[0] + 1;
//                LOGGER.info("Run:" + counter[0]);
//            } catch (Exception exc) {
//                LOGGER.error("Error - parallelExecute", exc);
//            }
//        });
//    }

    @RequestMapping(value = "stop/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void stop(@PathVariable("id") String id) {
        if (id != null && !id.isEmpty()) {
            TCContext context = ContextManager.getInstance().findById(id);
            if (context != null) {
                context.stop();
            } else LOGGER.warn("Can't find context with id:" + id);
        } else LOGGER.warn("ContextID can't be null or empty");
    }


    private UserInfo getCurrentUserInfo() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserInfo userInfo = null;
        if (principal instanceof UserDetails) {
            User user = userService.getByLogin(((UserDetails) principal).getUsername());
            userInfo = new UserInfo(user.getFirstName(), user.getLastName(), user.getEmail(), user.getLogin());
        }
        return userInfo;
    }

}
