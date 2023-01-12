package ru.itb.testautomation.core.instance.project.intf;

import ru.itb.testautomation.core.bobject.BusinessObject;
import ru.itb.testautomation.core.common.AuditAble;
import ru.itb.testautomation.core.common.DescriptionAble;
import ru.itb.testautomation.core.common.IdAble;
import ru.itb.testautomation.core.common.Named;
import ru.itb.testautomation.core.instance.jira.intf.JiraProfile;
import ru.itb.testautomation.core.instance.mail.MailProfile;
import ru.itb.testautomation.core.instance.testcase.intf.TestCaseGroup;

import java.util.List;

public interface Project extends BusinessObject, IdAble, Named, DescriptionAble, AuditAble {
    List<TestCaseGroup> getGroups();
    void setGroups(List<TestCaseGroup> groups);
    JiraProfile getJiraProfile();
    void setJiraProfile(JiraProfile jiraProfile);
    MailProfile getMailProfile();
    void setMailProfile(MailProfile mailProfile);
    void addGroup(TestCaseGroup group);
}
