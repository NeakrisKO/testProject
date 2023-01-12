package ru.itb.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.itb.testautomation.core.instance.jira.intf.JiraProfile;
import ru.itb.testautomation.core.instance.mail.MailProfile;
import ru.itb.testautomation.core.instance.project.impl.ProjectImpl;
import ru.itb.testautomation.core.instance.project.intf.Project;
import ru.itb.testautomation.core.instance.testcase.impl.TestCaseGroupImpl;
import ru.itb.testautomation.core.instance.testcase.intf.TestCaseGroup;
import ru.itb.testautomation.core.manager.CoreObjectManager;
import ru.itb.web.model.ui.UIObjectNode;
import ru.itb.web.model.ui.UIProject;
import ru.itb.web.model.ui.UITestCaseGroup;
import ru.itb.web.util.UIObjectConverter;

import java.util.List;

@RestController
@RequestMapping(value = "/project")
public class ProjectController {
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public List<UIProject> getList() {
        return UIObjectConverter.getInstance().convertProjects(CoreObjectManager.getInstance().getManager().getProjectList(true));
    }

    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    @ResponseBody
    public UIProject getProject(@PathVariable Integer id) {
        return UIObjectConverter.getInstance().convertProject(CoreObjectManager.getInstance().getManager().getProject(id, false));
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void createProject(@RequestBody UIProject uiProject) {
        JiraProfile selectedProfile;
        MailProfile selectedMailProfile;
        ProjectImpl project = new ProjectImpl();
        project.setName(uiProject.getName());
        project.setDescription(uiProject.getDescription());
        Integer profileId = uiProject.getJiraProfile().getId();
        Integer mailProfileId = uiProject.getMailProfile().getId();
        if (profileId != null) {
            selectedProfile = CoreObjectManager.getInstance().getManager().getJiraProfile(profileId, false);
            if (selectedProfile != null) {
                project.setJiraProfile(selectedProfile);
            }
        }

        if (mailProfileId != null) {
            selectedMailProfile = CoreObjectManager.getInstance().getManager().getMailProfile(mailProfileId, false);
            if (selectedMailProfile != null) {
                project.setMailProfile(selectedMailProfile);
            }
        }

        CoreObjectManager.getInstance().getManager().addProject(project);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void updateProject(@RequestBody UIProject uiProject) {
        Integer jiraProfileId;
        Integer mailProfileId;
        Project oldProject = CoreObjectManager.getInstance().getManager().getProject(uiProject.getId(), false);
        oldProject.setName(uiProject.getName());
        oldProject.setDescription(uiProject.getDescription());

        //Update JIRA profile
        if (uiProject.getJiraProfile() != null) {
            Integer uiJiraProfileId = uiProject.getJiraProfile().getId();
            if (oldProject.getJiraProfile() != null) {
                jiraProfileId = (Integer) oldProject.getJiraProfile().getId();
                if (uiJiraProfileId != jiraProfileId) {
                    JiraProfile jiraProfile = CoreObjectManager.getInstance().getManager().getJiraProfile(uiJiraProfileId, false);
                    oldProject.setJiraProfile(jiraProfile);
                }
            } else {
                JiraProfile jiraProfile = CoreObjectManager.getInstance().getManager().getJiraProfile(uiJiraProfileId, false);
                oldProject.setJiraProfile(jiraProfile);
            }
        }

        //Update Mail profile
        if (uiProject.getMailProfile() != null) {
            Integer uiMailProfileId = uiProject.getMailProfile().getId();
            if (oldProject.getMailProfile() != null) {
                mailProfileId = (Integer) oldProject.getMailProfile().getId();
                if (uiMailProfileId != mailProfileId) {
                    MailProfile mailProfile = CoreObjectManager.getInstance().getManager().getMailProfile(uiMailProfileId, false);
                    oldProject.setMailProfile(mailProfile);
                }
            } else {
                MailProfile mailProfile = CoreObjectManager.getInstance().getManager().getMailProfile(uiMailProfileId, false);
                oldProject.setMailProfile(mailProfile);
            }
        }

        CoreObjectManager.getInstance().getManager().updateProject(oldProject, null);
    }

    @RequestMapping(value = "remove/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void removeProject(@PathVariable("id") Integer id) {
        CoreObjectManager.getInstance().getManager().removeProject(id);
    }

    @RequestMapping(value = "remove_group/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void removeGroup(@PathVariable("id") Integer id) {
        CoreObjectManager.getInstance().getManager().removeTCGroup(id);
    }

    @RequestMapping(value = "bindprofile/{projectId}/{profileId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void bindProfile(@PathVariable("projectId") Integer projectId, @PathVariable("profileId") Integer profileId) {
        Project project = CoreObjectManager.getInstance().getManager().getProject(projectId, false);
        JiraProfile jiraProfile = CoreObjectManager.getInstance()
                .getManager().getJiraProfile(profileId, false);
        project.setJiraProfile(jiraProfile);
        CoreObjectManager.getInstance().getManager().updateProject(project, null);
    }

    @RequestMapping(value = "add_group/{projectId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void addGroup(@RequestBody UITestCaseGroup uiTestCaseGroup, @PathVariable("projectId") Integer projectId) {
        Project project = CoreObjectManager.getInstance().getManager().getProject(projectId, false);
        TestCaseGroup group = new TestCaseGroupImpl();
        group.setName(uiTestCaseGroup.getName());
        group.setDescription(uiTestCaseGroup.getDescription());
        project.addGroup(group);
        CoreObjectManager.getInstance().getManager().updateProject(project, null);
    }

    @RequestMapping(value = "update_group", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updateGroup(@RequestBody UITestCaseGroup uiTestCaseGroup) {
        TestCaseGroup updatedGroupStep = CoreObjectManager.getInstance().getManager().getTCGroup(uiTestCaseGroup.getId(), false);
        updatedGroupStep.setName(uiTestCaseGroup.getName());
        updatedGroupStep.setDescription(uiTestCaseGroup.getDescription());
        CoreObjectManager.getInstance().getManager().updateTCGroupSimple(updatedGroupStep);
    }

    @RequestMapping(value = "project_tree", method = RequestMethod.GET)
    @ResponseBody
    public List<UIObjectNode> buildProjectTree() {
        return UIObjectConverter.getInstance().convertForGroupTree(CoreObjectManager.getInstance().getManager().getProjectList(false));
    }

    @RequestMapping(value = "copy_group/{tcgId}/{projectId}", method = RequestMethod.GET)
    @ResponseBody
    public Integer copyTCGroup(@PathVariable("tcgId") Integer tcgId,
                                   @PathVariable("projectId") Integer projectId)
    {
        TestCaseGroup testCaseGroup = CoreObjectManager.getInstance().getManager().getTCGroup(tcgId, false);
        Project project = CoreObjectManager.getInstance().getManager().getProject(projectId, false);

        UIObjectConverter.getInstance().prepareTCGroupForCopy(testCaseGroup);
        project.addGroup(testCaseGroup);

        return CoreObjectManager.getInstance().getManager().updateProject(project,null);
    }

    @RequestMapping(value = "move_group/{projectId}/{groupId}", method = RequestMethod.GET)
    @ResponseBody
    public Integer moveTCGroup(@PathVariable("projectId") Integer projectId,
                                   @PathVariable("groupId") Integer groupId)
    {
        Project project = CoreObjectManager.getInstance().getManager().getProject(projectId, false);
        int groupSize = project.getGroups() != null ? project.getGroups().size() : 0;
        int groupSortOrder = groupSize + 1;
        if (groupSize != 0) {
            groupSortOrder = project.getGroups().get(groupSize - 1).getSortOrder() + 1;
        }
        return CoreObjectManager.getInstance().getManager().moveTCGroup(projectId,groupId,groupSortOrder);
    }

    @RequestMapping(value = "copy/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Integer copyProject(@PathVariable("id") Integer id)
    {
        Project project = CoreObjectManager.getInstance().getManager().getProject(id, false);
        UIObjectConverter.getInstance().prepareProjectForCopy(project);
        return CoreObjectManager.getInstance().getManager().updateProject(project,null);
    }



}
