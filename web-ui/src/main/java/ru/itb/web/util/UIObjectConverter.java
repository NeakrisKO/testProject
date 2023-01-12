package ru.itb.web.util;

import ru.itb.testautomation.core.dataset.impl.DataSetInfo;
import ru.itb.testautomation.core.document.Document;
import ru.itb.testautomation.core.instance.action.ActionParam;
import ru.itb.testautomation.core.instance.action.FieldType;
import ru.itb.testautomation.core.instance.action.impl.ActionImpl;
import ru.itb.testautomation.core.instance.action.intf.Action;
import ru.itb.testautomation.core.instance.jira.intf.JiraProfile;
import ru.itb.testautomation.core.instance.mail.MailProfile;
import ru.itb.testautomation.core.instance.project.intf.Project;
import ru.itb.testautomation.core.instance.step.impl.UIStepImpl;
import ru.itb.testautomation.core.instance.step.intf.Step;
import ru.itb.testautomation.core.instance.testcase.TestCaseStep;
import ru.itb.testautomation.core.instance.testcase.impl.TestCaseGroupImpl;
import ru.itb.testautomation.core.instance.testcase.impl.TestCaseImpl;
import ru.itb.testautomation.core.instance.testcase.intf.TestCaseGroup;
import ru.itb.web.model.security.User;
import ru.itb.web.model.security.UserProfile;
import ru.itb.web.model.ui.*;
import ru.itb.web.model.ui.user.UIUserProfile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UIObjectConverter {
    private static volatile UIObjectConverter instance;
    private DateTimeFormatter dateFormat;

    private UIObjectConverter() {
        dateFormat  = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss");
    }

    public static UIObjectConverter getInstance() {
        UIObjectConverter localInstance = instance;
        if (localInstance == null) {
            synchronized (UIObjectConverter.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new UIObjectConverter();
                }
            }
        }
        return localInstance;
    }

    public List<UITestCase> convertTestCases(List<TestCaseStep> testCases) {
        return testCases.stream().map(this::convertTestCase).collect(Collectors.toCollection(ArrayList::new));
    }

    public UITestCase convertTestCase(TestCaseStep testCaseStep) {
        UITestCase uiTestCase = new UITestCase();
        uiTestCase.setName(testCaseStep.getTestCase().getName());
        uiTestCase.setId((Integer) testCaseStep.getTestCase().getId());
        uiTestCase.setSortOrder(testCaseStep.getTestCase().getSortOrder());
        uiTestCase.setDescription(testCaseStep.getTestCase().getDescription());
        uiTestCase.setDataset(testCaseStep.getTestCase().getDatasetName());
        if (testCaseStep.getTestCase().getSteps() != null) {
            uiTestCase.setSteps(convertSteps(testCaseStep.getTestCase().getSteps(), uiTestCase.getId()));
        }
        return uiTestCase;
    }

    public TestCaseStep convertUITestCase(UITestCase uiTestCase) {
        TestCaseImpl testCase = new TestCaseImpl();
        testCase.setId(uiTestCase.getId());
        testCase.setSortOrder(uiTestCase.getSortOrder() == null ? -1 : uiTestCase.getSortOrder());
        testCase.setName(uiTestCase.getName());
        testCase.setDescription(uiTestCase.getDescription());
        testCase.setDatasetName(uiTestCase.getDataset());
        List<Step> stepList = uiTestCase.getSteps().stream().map(this::convertUIStep).collect(Collectors.toList());
        testCase.setSteps(stepList);
        return new TestCaseStep(testCase);
    }

    public List<UIStep> convertSteps(List<Step> steps, Integer parentId) {
        ArrayList<UIStep> dest = new ArrayList<>();
        steps.stream().map(this::convertStep).forEachOrdered(uiStep -> {
            uiStep.setTestCaseId(parentId);
            dest.add(uiStep);
        });
        return dest;
    }

    public UIStep convertStep(Step step) {
        UIStep uiStep = new UIStep();
        uiStep.setName(step.getName());
        uiStep.setId((Integer) step.getId());
        uiStep.setSortOrder(step.getSortOrder());
        uiStep.setDescription(step.getDescription());
        if (step instanceof UIStepImpl) {
            uiStep.setDelay(((UIStepImpl) step).getDelay());
            uiStep.setUnit(((UIStepImpl) step).getUnit() == null ? null : ((UIStepImpl) step).getUnit().toString());
            uiStep.setStepType(((UIStepImpl) step).getStepType());
            uiStep.setActions(convertActions(((UIStepImpl) step).getAction()));
        }
        return uiStep;
    }

    public List<UIAction> convertActions(List<Action> actions) {
        List<UIAction> uiActions = actions.stream().map(this::convertAction).collect(Collectors.toList());
        return uiActions;
    }

    public UIAction convertAction(Action action) {
        UIAction uiAction = new UIAction();
        ActionParam actionParam = action.getActionParam();
        uiAction.setId((Integer) action.getId());
        uiAction.setSortOrder(action.getSortOrder());
        uiAction.setDelay(action.getDelay());
        uiAction.setUnit(action.getUnit().toString());
        uiAction.setName(actionParam.getName());
        uiAction.setField(actionParam.getField());
        uiAction.setFieldType(actionParam.getFieldType().toString());
        uiAction.setVariable(actionParam.getValue());
        uiAction.setData(actionParam.getData());
        return uiAction;
    }

    public Step convertUIStep(UIStep uiStep) {
        UIStepImpl step = new UIStepImpl();
        step.setId(uiStep.getId());
        step.setSortOrder(uiStep.getSortOrder());
        step.setName(uiStep.getName());
        step.setStepType(uiStep.getStepType());
        step.setDescription(uiStep.getDescription());
        step.setDelay(uiStep.getDelay());
        step.setUnit(uiStep.getUnit() == null ? null : TimeUnit.valueOf(uiStep.getUnit()));
        step.setAction(convertUIActions(uiStep.getActions()));
        return step;
    }

    public List<Step> convertUISteps(List<UIStep> uiSteps) {
        ArrayList<Step> dest = uiSteps.stream().map(this::convertUIStep).collect(Collectors.toCollection(ArrayList::new));
        return dest;
    }

    private List<Action> convertUIActions(List<UIAction> uiActions) {
        List<Action> actions = uiActions.stream().map(this::convertUIAction).collect(Collectors.toList());
        return actions;
    }

    public Action convertUIAction(UIAction uiAction) {
        Action action = new ActionImpl();
        ActionParam actionParam = new ActionParam();
        action.setId(uiAction.getId());
        action.setSortOrder(uiAction.getSortOrder() == null ? -1 : uiAction.getSortOrder());
        action.setUnit(TimeUnit.valueOf(uiAction.getUnit()));
        action.setDelay(uiAction.getDelay());
        actionParam.setName(uiAction.getName());
        actionParam.setField(uiAction.getField());
        actionParam.setValue(uiAction.getVariable());
        actionParam.setData(uiAction.getData());
        actionParam.setFieldType(FieldType.valueOf(uiAction.getFieldType()));
        action.setActionParam(actionParam);
        return action;
    }


    public UIProject convertProject(Project project) {
        UIProject uiProject = new UIProject();
        uiProject.setId((Integer) project.getId());
        uiProject.setName(project.getName());
        uiProject.setDescription(project.getDescription());
        if (project.getJiraProfile() != null) {
            uiProject.setJiraProfile(convertProfile(project.getJiraProfile()));
        }

        if (project.getMailProfile() != null) {
            uiProject.setMailProfile(convertMailProfile(project.getMailProfile()));
        }

        List<UITestCaseGroup> uiTestCaseGroups = new ArrayList<>();
        if (project.getGroups() != null) {
            uiTestCaseGroups = project.getGroups().stream().map(this::convertTestCaseToUI).collect(Collectors.toList());
        }
        uiProject.setGroups(uiTestCaseGroups);
        return uiProject;
    }

    public UIJiraProfile convertProfile(JiraProfile jiraProfile) {
        UIJiraProfile uiJiraProfile = new UIJiraProfile();
        uiJiraProfile.setId((Integer) jiraProfile.getId());
        uiJiraProfile.setName(jiraProfile.getName());
        uiJiraProfile.setDescription(jiraProfile.getDescription());
        uiJiraProfile.setUrl(jiraProfile.getUrl());
        uiJiraProfile.setUserName(jiraProfile.getUserName());
        uiJiraProfile.setUserPassword(jiraProfile.getUserPassword());
        uiJiraProfile.setUserResponsible(jiraProfile.getResponsibleUser());
        uiJiraProfile.setWriteError(jiraProfile.getWriteError());
        uiJiraProfile.setProjectCode(jiraProfile.getProjectCode());
        uiJiraProfile.setUserWatcher(jiraProfile.getWatcherUser());
        return uiJiraProfile;
    }

    public List<UIJiraProfile> convertProfiles(List<JiraProfile> jiraProfiles) {
        return jiraProfiles.stream().map(this::convertProfile).collect(Collectors.toList());
    }

    public UIMailProfile convertMailProfile(MailProfile mailProfile) {
        UIMailProfile uiMailProfile = new UIMailProfile();
        uiMailProfile.setId((Integer)mailProfile.getId());
        uiMailProfile.setName(mailProfile.getName());
        uiMailProfile.setDescription(mailProfile.getDescription());
        uiMailProfile.setServer(mailProfile.getServer());
        uiMailProfile.setUser(mailProfile.getUser());
        uiMailProfile.setPassword(mailProfile.getPassword());
        return uiMailProfile;
    }

    public List<UIMailProfile> convertMailProfiles(List<MailProfile> mailProfiles) {
        return mailProfiles.stream().map(this::convertMailProfile).collect(Collectors.toList());
    }

    public UITestCaseGroup convertTestCaseToUI(TestCaseGroup testCaseGroup) {
        UITestCaseGroup uiTestCaseGroup = new UITestCaseGroup();
        if (testCaseGroup instanceof TestCaseGroupImpl) {
            uiTestCaseGroup.setId((Integer) testCaseGroup.getId());
            uiTestCaseGroup.setSortOrder(testCaseGroup.getSortOrder());
            uiTestCaseGroup.setName(testCaseGroup.getName());
            uiTestCaseGroup.setDescription(testCaseGroup.getDescription());
            List<UITestCase> testCaseList = testCaseGroup.getSteps().stream().filter(entry -> entry instanceof TestCaseStep).map(entry -> convertTestCase(((TestCaseStep) entry))).collect(Collectors.toList());
            uiTestCaseGroup.setTestCases(testCaseList);
        }
        return uiTestCaseGroup;
    }

    public List<UIProject> convertProjects(List<Project> projects) {
        List<UIProject> uiProjects = projects.stream().map(this::convertProject).collect(Collectors.toList());
        return uiProjects;
    }


    public Step updateStep(Step step, UIStep uiStep) {
        Step newStep = new UIStepImpl();
        newStep.setId(uiStep.getId());
        newStep.setSortOrder(uiStep.getSortOrder());
        newStep.setName(uiStep.getName());
        newStep.setDescription(uiStep.getDescription());
        if (newStep instanceof UIStepImpl) {
            ((UIStepImpl) newStep).setDelay(uiStep.getDelay());
            ((UIStepImpl) newStep).setUnit(TimeUnit.valueOf(uiStep.getUnit()));
            ((UIStepImpl) newStep).setStepType(uiStep.getStepType());
            ((UIStepImpl) newStep).setAction(((UIStepImpl) step).getAction());
        }
        newStep.setDateCreate(step.getDateCreate());
        newStep.setDateUpdate(step.getDateUpdate());
        newStep.setUserCreate(step.getUserCreate());
        newStep.setUserUpdate(step.getUserUpdate());

        return newStep;
    }

    public List<UIObjectNode> convertForStepTree(List<Project> projects, boolean needLastChild) {
        return projects.stream().map(entry -> convertProjectNode(entry, needLastChild)).collect(Collectors.toList());
    }

    public List<UIObjectNode> convertForGroupTree(List<Project> projects) {
        List<UIObjectNode> tree = new ArrayList<>();
        for(Project projectEntry : projects) {
            UIObjectNode projectNode = new UIObjectNode();
            projectNode.setName(projectEntry.getName());
            projectNode.setId(projectEntry.getId().toString());
            projectNode.setDescription(projectEntry.getDescription());
            projectNode.setType("project");
            if (projectEntry.getGroups() != null) {
                List<UIObjectNode> projectChild = new ArrayList<>();
                projectEntry.getGroups().forEach(groupEntry -> {
                    UIObjectNode groupNode = new UIObjectNode();
                    groupNode.setName(groupEntry.getName());
                    groupNode.setId(groupEntry.getId().toString());
                    groupNode.setDescription(groupEntry.getDescription());
                    groupNode.setType("group");
                    projectChild.add(groupNode);
                });
                projectNode.setChild(projectChild);
                projectNode.setChildCount(projectChild.size());
            }
            tree.add(projectNode);
        }
        return tree;
    }


    private UIObjectNode convertProjectNode(Project project, boolean needLastChild) {
        UIObjectNode projectNode = new UIObjectNode();
        projectNode.setName(project.getName());
        projectNode.setId(project.getId().toString());
        projectNode.setDescription(project.getDescription());
        projectNode.setType("project");
        if (project.getGroups() != null) {
            List<UIObjectNode> child = project.getGroups().stream().map(entry -> convertGroupNode(entry, needLastChild)).collect(Collectors.toList());
            projectNode.setChild(child);
            projectNode.setChildCount(child.size());
        }
        return projectNode;
    }

    private UIObjectNode convertGroupNode(TestCaseGroup group, boolean needLastChild) {
        UIObjectNode groupNode = new UIObjectNode();
        groupNode.setName(group.getName());
        groupNode.setId(group.getId().toString());
        groupNode.setDescription(group.getDescription());
        groupNode.setType("group");
        if (group.getSteps() != null) {
            List<UIObjectNode> child = group.getSteps().stream().map(entry -> convertTestCaseNode(entry, needLastChild)).collect(Collectors.toList());
            groupNode.setChild(child);
            groupNode.setChildCount(child.size());
        }

        return groupNode;
    }

    private UIObjectNode convertTestCaseNode(Step step, boolean needLastChild) {
        UIObjectNode testCaseNode = new UIObjectNode();
        if (step instanceof TestCaseStep) {
            testCaseNode.setName(((TestCaseStep) step).getTestCase().getName());
            testCaseNode.setId(((TestCaseStep) step).getTestCase().getId().toString());
            testCaseNode.setDescription(((TestCaseStep) step).getTestCase().getDescription());
            testCaseNode.setType("testcase");
            if (needLastChild && ((TestCaseStep) step).getTestCase().getSteps() != null) {
                List<UIObjectNode> child = ((TestCaseStep) step).getTestCase().getSteps().stream().map(this::convertStepNode).collect(Collectors.toList());
                testCaseNode.setChild(child);
                testCaseNode.setChildCount(child.size());
            }
        }
        return testCaseNode;
    }

    private UIObjectNode convertStepNode(Step step) {
        UIObjectNode stepNode = new UIObjectNode();
        stepNode.setName(step.getName());
        stepNode.setId(step.getId().toString());
        stepNode.setDescription(step.getDescription());
        stepNode.setType("step");
        if (step instanceof UIStepImpl) {
            stepNode.setChildCount(((UIStepImpl) step).getAction()!= null ? ((UIStepImpl) step).getAction().size() : 0);
        }
        return stepNode;
    }

    public void prepareProjectForCopy(Project project) {
        project.setId(null);
        project.setName(project.getName() + " copy");
        project.getGroups().forEach(this::prepareTCGroupForCopy);
    }

    public void prepareTCGroupForCopy(TestCaseGroup testCaseGroup) {
        testCaseGroup.setId(null);
        testCaseGroup.getSteps().stream().filter(entry -> entry instanceof TestCaseStep).map(entry -> (TestCaseStep) entry).forEachOrdered(this::prepareTCForCopy);
    }

    public void prepareStepForCopy(Step step) {
        step.setId(null);
        if (step instanceof UIStepImpl) {
            ((UIStepImpl) step).getAction().forEach(action -> action.setId(null));
        }
    }

    public void prepareTCForCopy(TestCaseStep testCase) {
        testCase.getTestCase().setId(null);
        testCase.getTestCase().getSteps().forEach(this::prepareStepForCopy);
    }


    public List<UIDataSet> convertDataSetInfoList(List<DataSetInfo> dataSetList) {
        return dataSetList.stream().map(this::convertDataSetInfo).collect(Collectors.toList());
    }

    public DataSetInfo convertDataSetInfo(UIDataSet uiDataSet) {
        DataSetInfo dataSetInfo = new DataSetInfo();
        dataSetInfo.setId(uiDataSet.getId());
        dataSetInfo.setName(uiDataSet.getFileName());
        dataSetInfo.setDescription(uiDataSet.getDescription());
        dataSetInfo.setFileSize(uiDataSet.getFileSize());
        dataSetInfo.setDateCreate(LocalDateTime.parse(uiDataSet.getDateCreate(),DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss")));
        if (uiDataSet.getDateUpdate() != null && !uiDataSet.getDateUpdate().isEmpty()) {
            dataSetInfo.setDateUpdate(LocalDateTime.parse(uiDataSet.getDateUpdate(),DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss")));
        }
        dataSetInfo.setUserCreate(uiDataSet.getUserCreate());
        dataSetInfo.setUserUpdate(uiDataSet.getUserUpdate());
        return dataSetInfo;
    }

    private UIDataSet convertDataSetInfo(DataSetInfo dataSetInfo) {
        UIDataSet uiDataSet = new UIDataSet();
        uiDataSet.setId(Integer.parseInt(dataSetInfo.getId().toString()));
        uiDataSet.setFileName(dataSetInfo.getName());
        uiDataSet.setDescription(dataSetInfo.getDescription());
        uiDataSet.setFileSize(dataSetInfo.getFileSize());
        uiDataSet.setDateCreate(dataSetInfo.getDateCreate().format(dateFormat));
        uiDataSet.setDateUpdate(dataSetInfo.getDateUpdate() == null ? "" : dataSetInfo.getDateUpdate().format(dateFormat));
        uiDataSet.setUserCreate(dataSetInfo.getUserCreate());
        uiDataSet.setUserUpdate(dataSetInfo.getUserUpdate());
        return uiDataSet;
    }

    public List<UIUserInfo> convertUsers(List<User> users) {
        return users.stream().map(this::convertUser).collect(Collectors.toList());

    }

    public UIUserInfo convertUser(User user) {
        UIUserInfo uiUserInfo = new UIUserInfo();
        uiUserInfo.setId(user.getId());
        uiUserInfo.setEmail(user.getEmail());
        uiUserInfo.setFirstName(user.getFirstName());
        uiUserInfo.setLastName(user.getLastName());
        uiUserInfo.setLogin(user.getLogin());
        uiUserInfo.setUserProfiles(convertUserProfiles(user.getUserProfiles()));
        return uiUserInfo;
    }

    private List<UIUserProfile> convertUserProfiles(Set<UserProfile> userProfiles) {
        List<UIUserProfile> uiUserProfilesList = new ArrayList<>();
        for(UserProfile userProfile :userProfiles) {
            UIUserProfile uiUserProfile = convertUserProfile(userProfile);
            uiUserProfilesList.add(uiUserProfile);
        }
        return uiUserProfilesList;
    }

    private UIUserProfile convertUserProfile(UserProfile userProfile) {
        UIUserProfile uiUserProfile = new UIUserProfile();
        uiUserProfile.setId(userProfile.getId());
        uiUserProfile.setRole(userProfile.getType());
        return uiUserProfile;
    }

    public UIDocument convertDocument(Document document) {
        UIDocument uiDocument = new UIDocument();
        uiDocument.setId(document.getId());
        uiDocument.setName(document.getName());
        uiDocument.setDescription(document.getDescription());
        uiDocument.setPath(document.getPath());
        return uiDocument;
    }

//    public Set<UserProfile> convertUserProfiles(List<UIUserProfile> uiUserProfiles) {
//        Set<UserProfile> userProfiles = new HashSet<>();
//        for(UIUserProfile uiUserProfile :uiUserProfiles) {
//            UserProfile userProfile = convertUserProfile(uiUserProfile);
//            userProfiles.add(userProfile);
//        }
//        return userProfiles;
//    }
//
//    public UserProfile convertUserProfile(UIUserProfile uiUserProfile) {
//        UserProfile userProfile = new UserProfile();
//        userProfile.setId(uiUserProfile.getId());
//        userProfile.setType(uiUserProfile.getRole());
//        return userProfile;
//    }
}

