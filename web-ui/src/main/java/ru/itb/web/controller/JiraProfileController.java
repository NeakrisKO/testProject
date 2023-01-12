package ru.itb.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itb.testautomation.core.instance.jira.impl.JiraProfileImpl;
import ru.itb.testautomation.core.instance.jira.intf.JiraProfile;
import ru.itb.testautomation.core.manager.CoreObjectManager;
import ru.itb.web.model.ui.UIJiraProfile;
import ru.itb.web.util.UIObjectConverter;

import java.util.List;

@RestController
@RequestMapping(value = "/jira")
public class JiraProfileController {
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public List<UIJiraProfile> getList(){
        return UIObjectConverter.getInstance().convertProfiles(CoreObjectManager.getInstance().getManager().getJiraProfileList(false));
    }

    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    @ResponseBody
    public UIJiraProfile getProfile(@PathVariable Integer id) {
        return UIObjectConverter.getInstance().convertProfile(CoreObjectManager.getInstance().getManager().getJiraProfile(id, false));
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public void createProfile(@RequestBody UIJiraProfile uiJiraProfile) {
        JiraProfile jiraProfile = new JiraProfileImpl();
        jiraProfile.setName(uiJiraProfile.getName());
        jiraProfile.setDescription(uiJiraProfile.getDescription());
        jiraProfile.setProjectCode(uiJiraProfile.getProjectCode());
        jiraProfile.setUrl(uiJiraProfile.getUrl());
        jiraProfile.setResponsibleUser(uiJiraProfile.getUserResponsible());
        jiraProfile.setUserName(uiJiraProfile.getUserName());
        jiraProfile.setUserPassword(uiJiraProfile.getUserPassword());
        jiraProfile.setResponsibleUser(uiJiraProfile.getUserResponsible());
        jiraProfile.setWriteError(uiJiraProfile.getWriteError());
        jiraProfile.setWatcherUser(uiJiraProfile.getUserWatcher());
        CoreObjectManager.getInstance().getManager().addJiraProfile(jiraProfile);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ResponseEntity<UIJiraProfile> updateProfile(@RequestBody UIJiraProfile uiJiraProfile) {
        JiraProfile oldProfile = CoreObjectManager.getInstance()
                .getManager().getJiraProfile(uiJiraProfile.getId() ,false);
        oldProfile.setName(uiJiraProfile.getName());
        oldProfile.setDescription(uiJiraProfile.getDescription());
        oldProfile.setUrl(uiJiraProfile.getUrl());
        oldProfile.setProjectCode(uiJiraProfile.getProjectCode());
        oldProfile.setWriteError(uiJiraProfile.getWriteError());
        oldProfile.setResponsibleUser(uiJiraProfile.getUserResponsible());
        oldProfile.setUserName(uiJiraProfile.getUserName());
        oldProfile.setUserPassword(uiJiraProfile.getUserPassword());
        oldProfile.setWatcherUser(uiJiraProfile.getUserWatcher());

        Integer id = CoreObjectManager.getInstance().getManager().updateJiraProfile(oldProfile);
        UIJiraProfile response = UIObjectConverter.getInstance().convertProfile(CoreObjectManager.getInstance().getManager().getJiraProfile(id, false));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "remove/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void removeProfile(@PathVariable("id") Integer id) {
        CoreObjectManager.getInstance().getManager().removeJiraProfile(id);
    }
}
