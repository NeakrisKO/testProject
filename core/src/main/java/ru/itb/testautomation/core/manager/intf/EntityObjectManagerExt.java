package ru.itb.testautomation.core.manager.intf;

import ru.itb.testautomation.core.bobject.BusinessObject;
import ru.itb.testautomation.core.dataset.impl.DataSetInfo;
import ru.itb.testautomation.core.document.Document;
import ru.itb.testautomation.core.instance.action.intf.Action;
import ru.itb.testautomation.core.instance.jira.intf.JiraProfile;
import ru.itb.testautomation.core.instance.mail.MailProfile;
import ru.itb.testautomation.core.instance.project.intf.Project;
import ru.itb.testautomation.core.instance.step.intf.Step;
import ru.itb.testautomation.core.instance.testcase.TestCaseStep;
import ru.itb.testautomation.core.instance.testcase.intf.TestCaseGroup;

import java.util.List;

public interface EntityObjectManagerExt {
    Integer addProject(Project entity);

    Project getProject(Integer id, boolean lazy);

    List<Project> getProjectList(boolean lazy);

    void removeProject(Integer id);

    Integer updateProject(BusinessObject entity, Integer parentId);

    void removeAllProject();

    TestCaseGroup getTCGroup(Integer id, boolean lazy);

    List getTCGroupList(boolean lazy);

    void removeTCGroup(int id);

    Integer updateTCGroup(TestCaseGroup entity, Integer parentId);

    void removeAllTCGroup(Integer parentId);

    int changeTCGroupPosition(int firstId, int firstSortOrder, int secondId, int secondSortOrder);

    Integer addTC(BusinessObject entity);

    TestCaseStep getTC(Integer id, boolean lazy);

    List<BusinessObject> getTCList(boolean lazy);

    void removeTC(Integer id);

    Integer updateTC(BusinessObject testCase, Integer parentId);

    Integer updateTCSimple(TestCaseStep testCase);

    int updateTCGroupSimple(TestCaseGroup entity);

    int moveTCGroup(int projectId, int groupId, int sortOrder);

    int moveTC(int groupId, int testCaseId, int sortOrder);

    int moveUIStep(int testCaseId, int uiStepId, int sortOrder);

    int moveAction(int uiStepId, int actionId, int sortOrder);

    void removeAllTC(Integer parentId);

    int changeTCPosition(int firstId, int firstSortOrder, int secondId, int secondSortOrder);

    Step getStepUI(Integer id, boolean lazy);

    List getStepUIList(boolean lazy);

    void removeStepUI(Integer id);

    Integer updateStepUI(BusinessObject entity, Integer parentId);

    void removeAllStepUI(Integer parentId);

    int changeStepUIPosition(int firstId, int firstSortOrder, int secondId, int secondSortOrder);

    Integer addJiraProfile(BusinessObject entity);

    JiraProfile getJiraProfile(Integer id, boolean lazy);

    List<JiraProfile> getJiraProfileList(boolean lazy);

    void removeJiraProfile(Integer id);

    Integer updateJiraProfile(BusinessObject entity);

    void removeAllJiraProfile();

    void addDataSet(BusinessObject entity);

    BusinessObject getDataSet(Integer id);

    DataSetInfo getDataSetByName(String name);

    List<DataSetInfo> getDataSetList(boolean lazy);

    void removeDataSet(Integer id);

    Integer updateDataSet(DataSetInfo entity);

    Action getAction(Integer id, boolean lazy);

    List<Action> getActionList(Integer stepId);

    void removeAction(Integer id);

    int updateAction(Action entity);

    int changeActionPosition(int firstId, int firstSortOrder, int secondId, int secondSortOrder);

    void removeMailProfile(Integer id);

    Integer addMailProfile(BusinessObject entity);

    MailProfile getMailProfile(Integer id, boolean lazy);

    List<MailProfile> getMailProfileList(boolean lazy);

    Integer updateMailProfile(BusinessObject entity);

    Document getDocument(Integer id);
}
