package ru.itb.configuration.util;

import ru.itb.configuration.DAO.action.ActionDAO;
import ru.itb.configuration.DAO.dataset.DataSetDAO;
import ru.itb.configuration.DAO.document.DocumentDAO;
import ru.itb.configuration.DAO.jira.JiraProfileDAO;
import ru.itb.configuration.DAO.mail.MailProfileDAO;
import ru.itb.configuration.DAO.project.ProjectDAO;
import ru.itb.configuration.DAO.step.UIStepDAO;
import ru.itb.configuration.DAO.testcase.TestCaseDAO;
import ru.itb.configuration.DAO.testcase.TestCaseGroupDAO;
import ru.itb.testautomation.core.bobject.BusinessObject;
import ru.itb.testautomation.core.dataset.impl.DataSetInfo;
import ru.itb.testautomation.core.document.Document;
import ru.itb.testautomation.core.instance.action.ActionParam;
import ru.itb.testautomation.core.instance.action.FieldType;
import ru.itb.testautomation.core.instance.action.impl.ActionImpl;
import ru.itb.testautomation.core.instance.action.intf.Action;
import ru.itb.testautomation.core.instance.jira.impl.JiraProfileImpl;
import ru.itb.testautomation.core.instance.jira.intf.JiraProfile;
import ru.itb.testautomation.core.instance.mail.MailProfile;
import ru.itb.testautomation.core.instance.mail.impl.MailProfileImpl;
import ru.itb.testautomation.core.instance.project.impl.ProjectImpl;
import ru.itb.testautomation.core.instance.project.intf.Project;
import ru.itb.testautomation.core.instance.step.impl.UIStepImpl;
import ru.itb.testautomation.core.instance.step.intf.Step;
import ru.itb.testautomation.core.instance.testcase.TestCaseStep;
import ru.itb.testautomation.core.instance.testcase.impl.TestCaseGroupImpl;
import ru.itb.testautomation.core.instance.testcase.impl.TestCaseImpl;
import ru.itb.testautomation.core.instance.testcase.intf.TestCase;
import ru.itb.testautomation.core.instance.testcase.intf.TestCaseGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ObjectConverter {
    private static volatile ObjectConverter instance;

    private ObjectConverter() {
    }

    public static ObjectConverter getInstance() {
        ObjectConverter localInstance = instance;
        if (localInstance == null) {
            synchronized (ObjectConverter.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ObjectConverter();
                }
            }
        }
        return localInstance;
    }

    public List<BusinessObject> convertTestCases(List<TestCaseDAO> testCases, boolean lazy) {
        return testCases.stream().map(entity -> convertTestCaseDAOToCore(entity, lazy)).collect(Collectors.toCollection(ArrayList::new));
    }

    public DataSetDAO convertDatasetToDAO(DataSetInfo dataSet) {
        DataSetDAO dataSetDAO = new DataSetDAO();
        dataSetDAO.setId((Integer) dataSet.getId());
        dataSetDAO.setFileName(dataSet.getName());
        dataSetDAO.setFileSize(dataSet.getFileSize());
        dataSetDAO.setDescription(dataSet.getDescription());
        dataSetDAO.setDateCreate(dataSet.getDateCreate());
        dataSetDAO.setDateUpdate(dataSet.getDateUpdate());
        dataSetDAO.setUserCreate(dataSet.getUserCreate());
        dataSetDAO.setUserUpdate(dataSet.getUserUpdate());
        return dataSetDAO;
    }

    public ProjectDAO convertProjectToDAO(Project project) {
        ProjectDAO projectDAO = new ProjectDAO();
        projectDAO.setId((Integer) project.getId());
        projectDAO.setName(project.getName());
        projectDAO.setDescription(project.getDescription());
        JiraProfileDAO jiraProfileDAO = convertJiraProfileToDAO(project.getJiraProfile());
        MailProfileDAO mailProfileDAO = convertMailProfileToDAO(project.getMailProfile());
        projectDAO.setJiraProfile(jiraProfileDAO);
        projectDAO.setMailProfile(mailProfileDAO);
        projectDAO.setDateCreate(project.getDateCreate());
        projectDAO.setDateUpdate(project.getDateUpdate());
        projectDAO.setUserCreate(project.getUserCreate());
        projectDAO.setUserUpdate(project.getUserUpdate());
        Set<TestCaseGroupDAO> testCaseGroupDAOList = project.getGroups().stream().map(this::convertTestCaseGroupToDAO).collect(Collectors.toSet());
        projectDAO.setTestcaseGroup(testCaseGroupDAOList);
        return projectDAO;
    }

    public TestCaseGroupDAO convertTestCaseGroupToDAO(TestCaseGroup testCaseGroup) {
        TestCaseGroupDAO groupDAO = new TestCaseGroupDAO();
        if (testCaseGroup instanceof TestCaseGroupImpl) {
            groupDAO.setId((Integer) testCaseGroup.getId());
            groupDAO.setSortOrder(testCaseGroup.getSortOrder());
            groupDAO.setName(testCaseGroup.getName());
            groupDAO.setDescription(testCaseGroup.getDescription());
            groupDAO.setDateCreate(testCaseGroup.getDateCreate());
            groupDAO.setDateUpdate(testCaseGroup.getDateUpdate());
            groupDAO.setUserCreate(testCaseGroup.getUserCreate());
            groupDAO.setUserUpdate(testCaseGroup.getUserUpdate());
            Set<TestCaseDAO> testCaseSet = testCaseGroup.getSteps().stream().filter(entry -> entry instanceof TestCaseStep).map(entry -> convertTestCaseToDAO(((TestCaseStep) entry))).collect(Collectors.toSet());
            groupDAO.setTestCases(testCaseSet);
        }
        return groupDAO;
    }

    public JiraProfileDAO convertJiraProfileToDAO(JiraProfile jiraProfile) {
        if (jiraProfile != null) {
            JiraProfileDAO jiraProfileDAO = new JiraProfileDAO();
            jiraProfileDAO.setId((Integer) jiraProfile.getId());
            jiraProfileDAO.setName(jiraProfile.getName());
            jiraProfileDAO.setDescription(jiraProfile.getDescription());
            jiraProfileDAO.setUrl(jiraProfile.getUrl());
            jiraProfileDAO.setProjectCode(jiraProfile.getProjectCode());
            jiraProfileDAO.setWriteError(jiraProfile.getWriteError());
            jiraProfileDAO.setUserResponsible(jiraProfile.getResponsibleUser());
            jiraProfileDAO.setUserName(jiraProfile.getUserName());
            jiraProfileDAO.setUserPassword(jiraProfile.getUserPassword());
            jiraProfileDAO.setDateCreate(jiraProfile.getDateCreate());
            jiraProfileDAO.setDateUpdate(jiraProfile.getDateUpdate());
            jiraProfileDAO.setUserCreate(jiraProfile.getUserCreate());
            jiraProfileDAO.setUserUpdate(jiraProfile.getUserUpdate());
            jiraProfileDAO.setUserWatcher(jiraProfile.getWatcherUser().toLowerCase());
            return jiraProfileDAO;
        }
        return null;
    }

    public MailProfileDAO convertMailProfileToDAO(MailProfile mailProfile) {
        if (mailProfile != null) {
            MailProfileDAO mailProfileDAO = new MailProfileDAO();
            mailProfileDAO.setId((Integer) mailProfile.getId());
            mailProfileDAO.setName(mailProfile.getName());
            mailProfileDAO.setDescription(mailProfile.getDescription());
            mailProfileDAO.setServer(mailProfile.getServer());
            mailProfileDAO.setUser(mailProfile.getUser());
            mailProfileDAO.setPassword(mailProfile.getPassword());
            mailProfileDAO.setDateCreate(mailProfile.getDateCreate());
            mailProfileDAO.setDateUpdate(mailProfile.getDateUpdate());
            mailProfileDAO.setUserCreate(mailProfile.getUserCreate());
            mailProfileDAO.setUserUpdate(mailProfile.getUserUpdate());
            return mailProfileDAO;
        }
        return null;
    }

    public DataSetInfo convertDataSetDAOToCore(DataSetDAO dataSetDAO) {
        if (dataSetDAO != null) {
            DataSetInfo dataSet = new DataSetInfo();
            dataSet.setId(dataSetDAO.getId());
            dataSet.setName(dataSetDAO.getFileName());
            dataSet.setFileSize(dataSetDAO.getFileSize());
            dataSet.setDescription(dataSetDAO.getDescription());
            dataSet.setDateCreate(dataSetDAO.getDateCreate());
            dataSet.setDateUpdate(dataSetDAO.getDateUpdate());
            dataSet.setUserCreate(dataSetDAO.getUserCreate());
            dataSet.setUserUpdate(dataSetDAO.getUserUpdate());
            return dataSet;
        }
        return null;
    }

    public Project convertProjectDAOToCore(ProjectDAO projectDAO, boolean lazy) {
        Project project = new ProjectImpl();
        project.setName(projectDAO.getName());
        project.setId(projectDAO.getId());
        project.setDescription(projectDAO.getDescription());
        project.setJiraProfile(convertJiraProfile(projectDAO.getJiraProfile()));
        project.setMailProfile(convertMailProfile(projectDAO.getMailProfile()));
        List<TestCaseGroup> testCaseGroupList = projectDAO.getTestcaseGroup().stream().map(entry -> convertTestCaseGroup(entry, lazy)).collect(Collectors.toList());
        project.setGroups(testCaseGroupList);
        project.setDateCreate(projectDAO.getDateCreate());
        project.setDateUpdate(projectDAO.getDateUpdate());
        project.setUserCreate(projectDAO.getUserCreate());
        project.setUserUpdate(projectDAO.getUserUpdate());
        return project;
    }

    public JiraProfile convertJiraProfileDAOToCore(JiraProfileDAO jiraProfileDAO) {
        JiraProfile profile = new JiraProfileImpl();
        profile.setName(jiraProfileDAO.getName());
        profile.setId(jiraProfileDAO.getId());
        profile.setDescription(jiraProfileDAO.getDescription());
        profile.setProjectCode(jiraProfileDAO.getProjectCode());
        profile.setUrl(jiraProfileDAO.getUrl());
        profile.setWriteError(jiraProfileDAO.getWriteError());
        profile.setResponsibleUser(jiraProfileDAO.getUserResponsible());
        profile.setUserName(jiraProfileDAO.getUserName());
        profile.setUserPassword(jiraProfileDAO.getUserPassword());
        profile.setDateCreate(jiraProfileDAO.getDateCreate());
        profile.setDateUpdate(jiraProfileDAO.getDateUpdate());
        profile.setUserCreate(jiraProfileDAO.getUserCreate());
        profile.setUserUpdate(jiraProfileDAO.getUserUpdate());
        profile.setWatcherUser(jiraProfileDAO.getUserWatcher().toLowerCase());
        return profile;
    }

    public TestCaseDAO convertTestCaseToDAO(TestCaseStep testCaseStep) {
        TestCaseDAO testCaseDAO = new TestCaseDAO();
        testCaseDAO.setId((Integer) testCaseStep.getTestCase().getId());
        testCaseDAO.setSortOrder(testCaseStep.getTestCase().getSortOrder());
        testCaseDAO.setName(testCaseStep.getTestCase().getName());
        testCaseDAO.setDescription(testCaseStep.getTestCase().getDescription());
        testCaseDAO.setDatasetName(testCaseStep.getTestCase().getDatasetName());
        testCaseDAO.setDateCreate(testCaseStep.getTestCase().getDateCreate());
        testCaseDAO.setDateUpdate(testCaseStep.getTestCase().getDateUpdate());
        testCaseDAO.setUserCreate(testCaseStep.getTestCase().getUserCreate());
        testCaseDAO.setUserUpdate(testCaseStep.getTestCase().getUserUpdate());
        testCaseDAO.setSteps(convertStepsToDAO(testCaseStep.getTestCase().getSteps()));
        return testCaseDAO;
    }

    private Project convertProject(ProjectDAO projectDAO, boolean lazy) {
        if (projectDAO != null) {
            ProjectImpl project = new ProjectImpl();
            project.setId(projectDAO.getId());
            project.setName(projectDAO.getName());
            project.setDescription(projectDAO.getDescription());
            if (!lazy && projectDAO.getJiraProfile() != null) {
                JiraProfile jiraProfile = convertJiraProfile(projectDAO.getJiraProfile());
                project.setJiraProfile(jiraProfile);
            }
            if (!lazy && projectDAO.getMailProfile() != null) {
                MailProfile mailProfile = convertMailProfile(projectDAO.getMailProfile());
                project.setMailProfile(mailProfile);
            }
            project.setDateCreate(projectDAO.getDateCreate());
            project.setDateUpdate(projectDAO.getDateUpdate());
            project.setUserCreate(projectDAO.getUserCreate());
            project.setUserUpdate(projectDAO.getUserUpdate());
            List<TestCaseGroup> testCaseGroups = projectDAO.getTestcaseGroup().stream().map(stepDao -> convertTestCaseGroup(stepDao, lazy)).collect(Collectors.toList());
            project.setGroups(testCaseGroups);
            return project;
        } else return null;
    }

    private Project convertProjectLazy(ProjectDAO projectDAO) {
        if (projectDAO != null) {
            ProjectImpl project = new ProjectImpl();
            project.setId(projectDAO.getId());
            project.setName(projectDAO.getName());
            project.setDescription(projectDAO.getDescription());
//            if (projectDAO.getJiraProfile() != null) {
//                JiraProfile jiraProfile = convertJiraProfile(projectDAO.getJiraProfile());
//                project.setJiraProfile(jiraProfile);
//            }
            project.setDateCreate(projectDAO.getDateCreate());
            project.setDateUpdate(projectDAO.getDateUpdate());
            project.setUserCreate(projectDAO.getUserCreate());
            project.setUserUpdate(projectDAO.getUserUpdate());
            List<TestCaseGroup> testCaseGroups = projectDAO.getTestcaseGroup().stream().map(this::convertTestCaseGroupLazy).collect(Collectors.toList());
            project.setGroups(testCaseGroups);
            return project;
        } else return null;
    }

    public TestCaseGroupImpl convertTestCaseGroupLazy(TestCaseGroupDAO testCaseGroupDAO) {
        TestCaseGroupImpl testCaseGroup = new TestCaseGroupImpl();
        testCaseGroup.setId(testCaseGroupDAO.getId());
        testCaseGroup.setSortOrder(testCaseGroupDAO.getSortOrder());
        testCaseGroup.setName(testCaseGroupDAO.getName());
        testCaseGroup.setDescription(testCaseGroupDAO.getDescription());
        testCaseGroup.setDateCreate(testCaseGroupDAO.getDateCreate());
        testCaseGroup.setDateUpdate(testCaseGroupDAO.getDateUpdate());
        testCaseGroup.setUserCreate(testCaseGroupDAO.getUserCreate());
        testCaseGroup.setUserUpdate(testCaseGroupDAO.getUserUpdate());
//        List<Step> stepList = new ArrayList<>();
//        for (TestCaseDAO entry : testCaseGroupDAO.getTestCases()) {
//            Step testCaseStep = convertTestCaseDAOToCore(entry);
//            stepList.add(testCaseStep);
//        }
//        testCaseGroup.setSteps(stepList);
        return testCaseGroup;
    }

    public TestCaseGroupImpl convertTestCaseGroup(TestCaseGroupDAO testCaseGroupDAO, boolean lazy) {
        TestCaseGroupImpl testCaseGroup = new TestCaseGroupImpl();
        testCaseGroup.setId(testCaseGroupDAO.getId());
        testCaseGroup.setSortOrder(testCaseGroupDAO.getSortOrder());
        testCaseGroup.setName(testCaseGroupDAO.getName());
        testCaseGroup.setDescription(testCaseGroupDAO.getDescription());
        testCaseGroup.setDateCreate(testCaseGroupDAO.getDateCreate());
        testCaseGroup.setDateUpdate(testCaseGroupDAO.getDateUpdate());
        testCaseGroup.setUserCreate(testCaseGroupDAO.getUserCreate());
        testCaseGroup.setUserUpdate(testCaseGroupDAO.getUserUpdate());
        List<Step> stepList = new ArrayList<>();
        if (!lazy) {
            stepList = testCaseGroupDAO.getTestCases().stream().map(entry -> convertTestCaseDAOToCore(entry, lazy)).collect(Collectors.toList());
        }
        testCaseGroup.setSteps(stepList);
        return testCaseGroup;
    }

    public List<Project> convertProjects(List<ProjectDAO> projectDAO, boolean lazy) {
        return projectDAO.stream().map(entity -> convertProject(entity, lazy)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Project> convertProjectsLazy(List<ProjectDAO> projectDAO) {
        return projectDAO.stream().map(this::convertProjectLazy).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<JiraProfile> convertJiraProfiles(List<JiraProfileDAO> jiraProfileDAOList) {
        return jiraProfileDAOList.stream().map(this::convertJiraProfile).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<DataSetInfo> convertDataSets(List<DataSetDAO> dataSetDAOList) {
        return dataSetDAOList.stream().map(this::convertDataSetDAOToCore).collect(Collectors.toCollection(ArrayList::new));
    }

    private JiraProfile convertJiraProfile(JiraProfileDAO jiraProfileDAO) {
        if (jiraProfileDAO != null) {
            JiraProfile profile = new JiraProfileImpl();
            profile.setId(jiraProfileDAO.getId());
            profile.setName(jiraProfileDAO.getName());
            profile.setDescription(jiraProfileDAO.getDescription());
            profile.setUrl(jiraProfileDAO.getUrl());
            profile.setUserName(jiraProfileDAO.getUserName());
            profile.setUserPassword(jiraProfileDAO.getUserPassword());
            profile.setProjectCode(jiraProfileDAO.getProjectCode());
            profile.setResponsibleUser(jiraProfileDAO.getUserResponsible());
            profile.setWriteError(jiraProfileDAO.getWriteError());
            profile.setDateCreate(jiraProfileDAO.getDateCreate());
            profile.setDateUpdate(jiraProfileDAO.getDateUpdate());
            profile.setUserCreate(jiraProfileDAO.getUserCreate());
            profile.setUserUpdate(jiraProfileDAO.getUserUpdate());
            profile.setWatcherUser(jiraProfileDAO.getUserWatcher().toLowerCase());
            return profile;
        } else return null;
    }

    public List<MailProfile> convertMailProfiles(List<MailProfileDAO> mailProfileDAOList) {
        return mailProfileDAOList.stream().map(this::convertMailProfile).collect(Collectors.toCollection(ArrayList::new));
    }

    public MailProfile convertMailProfile(MailProfileDAO mailProfileDAO) {
        if (mailProfileDAO != null) {
            MailProfile profile = new MailProfileImpl();
            profile.setId(mailProfileDAO.getId());
            profile.setName(mailProfileDAO.getName());
            profile.setDescription(mailProfileDAO.getDescription());
            profile.setServer(mailProfileDAO.getServer());
            profile.setUser(mailProfileDAO.getUser());
            profile.setPassword(mailProfileDAO.getPassword());

            profile.setDateCreate(mailProfileDAO.getDateCreate());
            profile.setDateUpdate(mailProfileDAO.getDateUpdate());
            profile.setUserCreate(mailProfileDAO.getUserCreate());
            profile.setUserUpdate(mailProfileDAO.getUserUpdate());
            return profile;
        } else return null;
    }

    public TestCaseStep convertTestCaseDAOToCore(TestCaseDAO testCaseDAO, boolean lazy) {
        TestCase testCase = new TestCaseImpl();
        testCase.setName(testCaseDAO.getName());
        testCase.setId(testCaseDAO.getId());
        testCase.setSortOrder(testCaseDAO.getSortOrder());
        testCase.setDescription(testCaseDAO.getDescription());
        testCase.setDatasetName(testCaseDAO.getDatasetName());
        testCase.setDateCreate(testCaseDAO.getDateCreate());
        testCase.setDateUpdate(testCaseDAO.getDateUpdate());
        testCase.setUserCreate(testCaseDAO.getUserCreate());
        testCase.setUserUpdate(testCaseDAO.getUserUpdate());
        testCase.setSteps(convertSteps(testCaseDAO.getSteps(), lazy));
        return new TestCaseStep(testCase);
    }

    private Set<UIStepDAO> convertStepsToDAO(List<Step> steps) {
        return steps.stream().map(this::convertStepToDAO).collect(Collectors.toSet());
    }

    public UIStepDAO convertStepToDAO(Step step) {
        UIStepDAO uiStepDAO = new UIStepDAO();
        uiStepDAO.setId(step.getId() == null ? null : new Integer(step.getId().toString()));
        uiStepDAO.setSortOrder(step.getSortOrder());
        uiStepDAO.setName(step.getName());
        uiStepDAO.setDelay(((UIStepImpl) step).getDelay());
        uiStepDAO.setDescription(step.getDescription());
        uiStepDAO.setUnit(((UIStepImpl) step).getUnit().toString());
        uiStepDAO.setStepType(((UIStepImpl) step).getStepType());
        uiStepDAO.setAction(convertActionsToDAO(((UIStepImpl) step).getAction()));
        uiStepDAO.setDateCreate(step.getDateCreate());
        uiStepDAO.setDateUpdate(step.getDateUpdate());
        uiStepDAO.setUserCreate(step.getUserCreate());
        uiStepDAO.setUserUpdate(step.getUserUpdate());
        return uiStepDAO;
    }

    private List<Step> convertSteps(Set<UIStepDAO> steps, boolean lazy) {
        return steps.stream().map(stepItem -> convertStep(stepItem, lazy)).collect(Collectors.toCollection(ArrayList::new));
    }

    public Step convertStep(UIStepDAO step, boolean lazy) {
        Step uiStepCore = new UIStepImpl();
        uiStepCore.setId(step.getId());
        uiStepCore.setSortOrder(step.getSortOrder());
        ((UIStepImpl) uiStepCore).setDelay(step.getDelay());
        ((UIStepImpl) uiStepCore).setUnit(TimeUnit.valueOf(step.getUnit()));
        ((UIStepImpl) uiStepCore).setStepType(step.getStepType());
        if (!lazy) {
            ((UIStepImpl) uiStepCore).setAction(convertActions(step.getAction()));
        }
        uiStepCore.setName(step.getName());
        uiStepCore.setDescription(step.getDescription());
        uiStepCore.setDateCreate(step.getDateCreate());
        uiStepCore.setDateUpdate(step.getDateUpdate());
        uiStepCore.setUserCreate(step.getUserCreate());
        uiStepCore.setUserUpdate(step.getUserUpdate());
        return uiStepCore;
    }


    public Set<ActionDAO> convertActionsToDAO(List<Action> actions) {
        return actions.stream().map(this::convertActionToDAO).collect(Collectors.toSet());
    }

    public ActionDAO convertActionToDAO(Action action) {
        ActionDAO actionDAO = new ActionDAO();
        ActionParam actionParam = action.getActionParam();
        actionDAO.setName(actionParam.getName());
        actionDAO.setId((Integer) action.getId());
        actionDAO.setField(actionParam.getField());
        actionDAO.setVariable(actionParam.getValue());
        actionDAO.setFieldType(actionParam.getFieldType().toString());
        actionDAO.setData(actionParam.getData());
        actionDAO.setDateCreate(action.getDateCreate());
        actionDAO.setDateUpdate(action.getDateUpdate());
        actionDAO.setUserCreate(action.getUserCreate());
        actionDAO.setUserUpdate(action.getUserUpdate());
        actionDAO.setSortOrder(action.getSortOrder());
        actionDAO.setDelay(action.getDelay());
        actionDAO.setUnit(action.getUnit().toString());
        action.setActionParam(actionParam);
        return actionDAO;
    }

    public List<Action> convertActions(Set<ActionDAO> actionsDAO) {
        return actionsDAO.stream().map(this::convertAction).collect(Collectors.toList());
    }

    public List<Action> convertActions(List<ActionDAO> actionsDAO) {
        return actionsDAO.stream().map(this::convertAction).collect(Collectors.toList());
    }

    public Action convertAction(ActionDAO actionDAO) {
        Action action = new ActionImpl();
        ActionParam actionParam = new ActionParam();
        actionParam.setField(actionDAO.getField());
        actionParam.setName(actionDAO.getName());
        actionParam.setValue(actionDAO.getVariable());
        actionParam.setFieldType(FieldType.valueOf(actionDAO.getFieldType()));
        actionParam.setData(actionDAO.getData());
        action.setActionParam(actionParam);
        action.setId(actionDAO.getId());
        action.setDelay(actionDAO.getDelay());
        action.setUnit(TimeUnit.valueOf(actionDAO.getUnit()));
        action.setSortOrder(actionDAO.getSortOrder());
        action.setDateCreate(actionDAO.getDateCreate());
        action.setDateUpdate(actionDAO.getDateUpdate());
        action.setUserCreate(actionDAO.getUserCreate());
        action.setUserUpdate(actionDAO.getUserUpdate());
        return action;
    }

    public Document convertDocumentDAOToCore(DocumentDAO documentDAO) {
        Document document = new Document();
        document.setId(documentDAO.getId());
        document.setName(documentDAO.getName());
        document.setDescription(documentDAO.getDescription());
        document.setPath(documentDAO.getPath());
        return document;
    }
}
