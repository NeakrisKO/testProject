package ru.itb.configuration.Manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.hibernate.type.IntegerType;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.itb.configuration.DAO.action.ActionDAO;
import ru.itb.configuration.DAO.dataset.DataSetDAO;
import ru.itb.configuration.DAO.document.DocumentDAO;
import ru.itb.configuration.DAO.jira.JiraProfileDAO;
import ru.itb.configuration.DAO.mail.MailProfileDAO;
import ru.itb.configuration.DAO.project.ProjectDAO;
import ru.itb.configuration.DAO.step.UIStepDAO;
import ru.itb.configuration.DAO.testcase.TestCaseDAO;
import ru.itb.configuration.DAO.testcase.TestCaseGroupDAO;
import ru.itb.configuration.factory.HibSessionFactory;
import ru.itb.configuration.util.ObjectConverter;
import ru.itb.testautomation.core.bobject.BusinessObject;
import ru.itb.testautomation.core.dataset.impl.DataSetInfo;
import ru.itb.testautomation.core.document.Document;
import ru.itb.testautomation.core.instance.action.intf.Action;
import ru.itb.testautomation.core.instance.jira.impl.JiraProfileImpl;
import ru.itb.testautomation.core.instance.jira.intf.JiraProfile;
import ru.itb.testautomation.core.instance.mail.MailProfile;
import ru.itb.testautomation.core.instance.mail.impl.MailProfileImpl;
import ru.itb.testautomation.core.instance.project.intf.Project;
import ru.itb.testautomation.core.instance.step.impl.UIStepImpl;
import ru.itb.testautomation.core.instance.step.intf.Step;
import ru.itb.testautomation.core.instance.testcase.TestCaseStep;
import ru.itb.testautomation.core.instance.testcase.impl.TestCaseGroupImpl;
import ru.itb.testautomation.core.instance.testcase.impl.TestCaseImpl;
import ru.itb.testautomation.core.instance.testcase.intf.TestCase;
import ru.itb.testautomation.core.instance.testcase.intf.TestCaseGroup;
import ru.itb.testautomation.core.manager.SystemPreferenceManager;
import ru.itb.testautomation.core.manager.intf.EntityObjectManagerExt;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EntityManager implements EntityObjectManagerExt {
    private static final Logger LOGGER = LogManager.getLogger(EntityManager.class);

    @Override
    public Integer addProject(Project entity) {
        Transaction tx = null;
        Integer id = null;
        ProjectDAO projectDAO;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            projectDAO = ObjectConverter.getInstance().convertProjectToDAO(entity);
            id = (Integer) session.save(projectDAO);
            tx.commit();
        } catch (HibernateException exc) {
            if (tx != null) tx.rollback();
            LOGGER.error("Transaction rollback", exc);
        }
        return id;
    }

    @Override
    public Project getProject(Integer id, boolean lazy) {
        Transaction tx = null;
        Project project = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            project = ObjectConverter.getInstance().convertProjectDAOToCore(session.get(ProjectDAO.class, id), lazy);
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't get Project by id: %d", id), exc);
        }
        return project;
    }

    @Override
    public List<Project> getProjectList(boolean lazy) {
        Transaction tx;
        List<ProjectDAO> projectDAOList = new ArrayList<>();
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            projectDAOList = session.createCriteria(ProjectDAO.class)
                    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
            tx.commit();
            return ObjectConverter.getInstance().convertProjects(projectDAOList, lazy);
        } catch (Exception exc) {
            LOGGER.error("Can't get list of Projects", exc);
        }
        return ObjectConverter.getInstance().convertProjects(projectDAOList, lazy);
    }

    @Override
    public void removeProject(Integer id) {
        Transaction tx;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            ProjectDAO projectDAO = session.get(ProjectDAO.class, id);
            projectDAO.setJiraProfile(null);
            projectDAO.setMailProfile(null);
            session.saveOrUpdate(projectDAO);
            session.delete(projectDAO);
            tx.commit();
        } catch (Exception exc) {
            LOGGER.error("Can't remove Project with id: " + id, exc);
        }
    }

    @Override
    public Integer updateProject(BusinessObject entity, Integer parentId) {
        Transaction tx = null;
        Integer id = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            ProjectDAO projectDAO = ObjectConverter.getInstance().convertProjectToDAO(((Project) entity));
            session.saveOrUpdate(projectDAO);
            id = projectDAO.getId();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't update Project with id: %s, name: %s", ((Project) entity).getId(), ((Project) entity).getName()), exc);
        }
        return id;
    }

    @Override
    public void removeAllProject() {
    }

    @Override
    public TestCaseGroup getTCGroup(Integer id, boolean lazy) {
        Transaction tx = null;
        TestCaseGroupImpl testCaseGroup = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            testCaseGroup = ObjectConverter.getInstance().convertTestCaseGroup(session.get(TestCaseGroupDAO.class, id), lazy);
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't get TestCase Group by id: %d", id), exc);
        }
        return testCaseGroup;
    }

    @Override
    public List getTCGroupList(boolean lazy) {
        Transaction tx;
        List tcGroupList;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            tcGroupList = session.createCriteria(TestCaseGroupDAO.class)
                    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
            tx.commit();
            return ObjectConverter.getInstance().convertProjectsLazy(tcGroupList);
        } catch (Exception exc) {
            LOGGER.error("Can't get list of Test Case group for Project: []", exc);
        }
        return null;
    }

    @Override
    public void removeTCGroup(int id) {
        Transaction tx;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            TestCaseGroupDAO testCaseGroupDAO = session.get(TestCaseGroupDAO.class, id);
            session.delete(testCaseGroupDAO);
            tx.commit();
        } catch (Exception exc) {
            LOGGER.error("Can't remove Group with id: " + id, exc);
        }
    }

    @Override
    public Integer updateTCGroup(TestCaseGroup entity, Integer parentId) {
        Transaction tx = null;
        Integer id = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            TestCaseGroupDAO testCaseGroupDAO = ObjectConverter.getInstance().convertTestCaseGroupToDAO(entity);
            session.saveOrUpdate(testCaseGroupDAO);
            id = testCaseGroupDAO.getId();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't update Group with id: %s, name: %s", entity.getId(), entity.getName()), exc);
        }
        return id;
    }

    @Override
    public int updateTCGroupSimple(TestCaseGroup entity) {
        Transaction tx = null;
        int result = 0;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            Query updateQuery = session.createQuery("update TestCaseGroupDAO set name = :name, description = :description, dateUpdate = :dateUpdate, userUpdate = :userUpdate where id = :id");
            updateQuery.setParameter("name", entity.getName());
            updateQuery.setParameter("description", entity.getDescription());
            updateQuery.setParameter("dateUpdate", LocalDateTime.now());
            updateQuery.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            updateQuery.setParameter("id", entity.getId());
            result = updateQuery.executeUpdate();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't update Group with id: %s, name: %s", entity.getId(), entity.getName()), exc);
        }
        return result;
    }

    @Override
    public void removeAllTCGroup(Integer parentId) {
    }

    @Override
    public int changeTCGroupPosition(int firstId, int firstSortOrder, int secondId, int secondSortOrder) {
        int result = 0;
        Transaction tx = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            Query updateFirstQuery = session.createQuery("update TestCaseGroupDAO set sortOrder = :sortOrder, dateUpdate = :dateUpdate, userUpdate = :userUpdate where id = :id");
            updateFirstQuery.setParameter("sortOrder", secondSortOrder);
            updateFirstQuery.setParameter("dateUpdate", LocalDateTime.now());
            updateFirstQuery.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            updateFirstQuery.setParameter("id", firstId);

            Query updateSecondQuery = session.createQuery("update TestCaseGroupDAO set sortOrder = :sortOrder, dateUpdate = :dateUpdate, userUpdate = :userUpdate where id = :id");
            updateSecondQuery.setParameter("sortOrder", firstSortOrder);
            updateSecondQuery.setParameter("dateUpdate", LocalDateTime.now());
            updateSecondQuery.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            updateSecondQuery.setParameter("id", secondId);

            result = updateFirstQuery.executeUpdate();
            result = result + updateSecondQuery.executeUpdate();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't change position of Groups with id: %d, id: %d", firstId, secondId), exc);
        }
        return result;
    }

    @Override
    public int moveTCGroup(int projectId, int groupId, int sortOrder) {
        int result = 0;
        Transaction tx = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            Query query = session.createNativeQuery("UPDATE testcase_group SET project_id = :projectId, sort_order = :sortOrder, user_update= :userUpdate, date_update = :dateUpdate WHERE id = :id");
            query.setParameter("projectId", projectId);
            query.setParameter("sortOrder", sortOrder);
            query.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            query.setParameter("dateUpdate", LocalDateTime.now());
            query.setParameter("id", groupId);
            result = query.executeUpdate();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't move Group with id: %d to Project with id: %d", groupId, projectId), exc);
        }
        return result;
    }

    @Override
    public int moveTC(int groupId, int testCaseId, int sortOrder) {
        int result = 0;
        Transaction tx = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            Query query = session.createNativeQuery("UPDATE testcase SET testcase_group_id = :groupId, sort_order = :sortOrder, user_update= :userUpdate, date_update = :dateUpdate WHERE id = :id");
            query.setParameter("groupId", groupId);
            query.setParameter("sortOrder", sortOrder);
            query.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            query.setParameter("dateUpdate", LocalDateTime.now());
            query.setParameter("id", testCaseId);
            result = query.executeUpdate();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't move TestCase with id: %d to Group with id: %d", testCaseId, groupId), exc);
        }
        return result;
    }

    @Override
    public int moveUIStep(int testCaseId, int uiStepId, int sortOrder) {
        int result = 0;
        Transaction tx = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            Query query = session.createNativeQuery("UPDATE step SET testcase_id = :testCaseId, sort_order = :sortOrder, user_update= :userUpdate, date_update = :dateUpdate WHERE id = :id");
            query.setParameter("testCaseId", testCaseId);
            query.setParameter("sortOrder", sortOrder);
            query.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            query.setParameter("dateUpdate", LocalDateTime.now());
            query.setParameter("id", uiStepId);
            result = query.executeUpdate();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't move Step with id: %d to TestCase with id: %d", uiStepId, testCaseId), exc);
        }
        return result;
    }

    @Override
    public int moveAction(int uiStepId, int actionId, int sortOrder) {
        int result = 0;
        Transaction tx = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            Query query = session.createNativeQuery("UPDATE action SET step_id = :uiStepId, sort_order = :sortOrder, user_update= :userUpdate, date_update = :dateUpdate WHERE id = :id");
            query.setParameter("uiStepId", uiStepId);
            query.setParameter("sortOrder", sortOrder);
            query.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            query.setParameter("dateUpdate", LocalDateTime.now());
            query.setParameter("id", actionId);
            result = query.executeUpdate();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't move Action with id: %d to Step with id: %d", actionId, uiStepId), exc);
        }
        return result;
    }

    @Override
    public Integer addTC(BusinessObject entity) {
        Transaction tx = null;
        Integer id = null;
        TestCaseDAO testCaseDAO = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            if (entity instanceof TestCaseImpl) {
                testCaseDAO = ObjectConverter.getInstance().convertTestCaseToDAO((TestCaseStep) entity);
            }
            id = (Integer) session.save(testCaseDAO);
            tx.commit();
        } catch (HibernateException exc) {
            if (tx != null) tx.rollback();
            LOGGER.error("Transaction rollback", exc);
        }
        return id;
    }

    @Override
    public TestCaseStep getTC(Integer id, boolean lazy) {
        Transaction tx = null;
        TestCaseStep testCaseStep = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            testCaseStep = ObjectConverter.getInstance().convertTestCaseDAOToCore(session.get(TestCaseDAO.class, id), lazy);
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't get TestCase by id: %d", id), exc);
        }
        return testCaseStep;
    }

    @Override
    public List<BusinessObject> getTCList(boolean lazy) {
        Transaction tx;
        List<TestCaseDAO> testCaseDAOList = new ArrayList<>();
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            testCaseDAOList = session.createCriteria(TestCaseDAO.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
            tx.commit();
        } catch (Exception exc) {
            LOGGER.error("Can't get list of Test cases", exc);
        }
        return ObjectConverter.getInstance().convertTestCases(testCaseDAOList, lazy);
    }

    @Override
    public void removeTC(Integer id) {
        Transaction tx;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            TestCaseDAO testCaseDAO = session.get(TestCaseDAO.class, id);
            session.delete(testCaseDAO);
            tx.commit();
        } catch (Exception exc) {
            LOGGER.error("Can't remove TestCase with id: " + id, exc);
        }
    }

    @Override
    public Integer updateTC(BusinessObject testCase, Integer parentId) {
        Transaction tx = null;
        Integer id = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            TestCaseDAO testCaseDAO = ObjectConverter.getInstance().convertTestCaseToDAO((TestCaseStep) testCase);
            session.saveOrUpdate(testCaseDAO);
            id = testCaseDAO.getId();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't update TestCase with id: %s, name: %s", ((TestCase) testCase).getId(), ((TestCase) testCase).getName()), exc);
        }
        return id;
    }

    @Override
    public Integer updateTCSimple(TestCaseStep testCase) {
        Transaction tx = null;
        int result = 0;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            Query updateQuery = session.createQuery("update TestCaseDAO set name = :name, description = :description, datasetName = :datasetName, dateUpdate = :dateUpdate, userUpdate = :userUpdate  where id = :id");
            updateQuery.setParameter("name", testCase.getTestCase().getName());
            updateQuery.setParameter("description", testCase.getTestCase().getDescription());
            updateQuery.setParameter("datasetName", testCase.getTestCase().getDatasetName());
            updateQuery.setParameter("dateUpdate", LocalDateTime.now());
            updateQuery.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            updateQuery.setParameter("id", testCase.getTestCase().getId());
            result = updateQuery.executeUpdate();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't update TestCase with id: %s, name: %s", testCase.getId(), testCase.getName()), exc);
        }
        return result;
    }

    @Override
    public void removeAllTC(Integer parentId) {
    }

    @Override
    public int changeTCPosition(int firstId, int firstSortOrder, int secondId, int secondSortOrder) {
        int result = 0;
        Transaction tx = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            Query updateFirstQuery = session.createQuery("update TestCaseDAO set sortOrder = :sortOrder, dateUpdate = :dateUpdate, userUpdate = :userUpdate where id = :id");
            updateFirstQuery.setParameter("sortOrder", secondSortOrder);
            updateFirstQuery.setParameter("dateUpdate", LocalDateTime.now());
            updateFirstQuery.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            updateFirstQuery.setParameter("id", firstId);

            Query updateSecondQuery = session.createQuery("update TestCaseDAO set sortOrder = :sortOrder, dateUpdate = :dateUpdate, userUpdate = :userUpdate where id = :id");
            updateSecondQuery.setParameter("sortOrder", firstSortOrder);
            updateSecondQuery.setParameter("dateUpdate", LocalDateTime.now());
            updateSecondQuery.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            updateSecondQuery.setParameter("id", secondId);

            result = updateFirstQuery.executeUpdate();
            result = result + updateSecondQuery.executeUpdate();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't change position of TestCases with id: %d, id: %d", firstId, secondId), exc);
        }

        return result;
    }

    @Override
    public Step getStepUI(Integer id, boolean lazy) {
        Transaction tx = null;
        Step step = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            step = ObjectConverter.getInstance().convertStep(session.get(UIStepDAO.class, id), lazy);
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't get UI Step by id: %d", id), exc);
        }
        return step;
    }

    @Override
    public List getStepUIList(boolean lazy) {
        return null;
    }

    @Override
    public void removeStepUI(Integer id) {
        Transaction tx;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            UIStepDAO uiStepDAO = session.get(UIStepDAO.class, id);
            session.delete(uiStepDAO);
            tx.commit();
        } catch (Exception exc) {
            LOGGER.error("Can't remove UI Step with id: " + id, exc);
        }
    }

    @Override
    public Integer updateStepUI(BusinessObject entity, Integer parentId) {
        Transaction tx = null;
        Integer id = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            UIStepDAO uiStepDAO = ObjectConverter.getInstance().convertStepToDAO((UIStepImpl) entity);
            session.saveOrUpdate(uiStepDAO);
            id = uiStepDAO.getId();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error("Can't update Step with id: %d" + ((UIStepImpl) entity).getId(), exc);
        }
        return id;
    }

    @Override
    public void removeAllStepUI(Integer parentId) {
    }

    @Override
    public int changeStepUIPosition(int firstId, int firstSortOrder, int secondId, int secondSortOrder) {
        int result = 0;
        Transaction tx = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            Query updateFirstQuery = session.createQuery("update UIStepDAO set sortOrder = :sortOrder, dateUpdate = :dateUpdate, userUpdate = :userUpdate where id = :id");
            updateFirstQuery.setParameter("sortOrder", secondSortOrder);
            updateFirstQuery.setParameter("dateUpdate", LocalDateTime.now());
            updateFirstQuery.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            updateFirstQuery.setParameter("id", firstId);

            Query updateSecondQuery = session.createQuery("update UIStepDAO set sortOrder = :sortOrder, dateUpdate = :dateUpdate, userUpdate = :userUpdate where id = :id");
            updateSecondQuery.setParameter("sortOrder", firstSortOrder);
            updateSecondQuery.setParameter("dateUpdate", LocalDateTime.now());
            updateSecondQuery.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            updateSecondQuery.setParameter("id", secondId);

            result = updateFirstQuery.executeUpdate();
            result = result + updateSecondQuery.executeUpdate();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't change position of UISteps with id: %d, id: %d", firstId, secondId), exc);
        }

        return result;
    }

    @Override
    public Integer addJiraProfile(BusinessObject entity) {
        Transaction tx = null;
        Integer id = null;
        JiraProfileDAO jiraProfileDAO;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            jiraProfileDAO = ObjectConverter.getInstance().convertJiraProfileToDAO((JiraProfileImpl) entity);
            id = (Integer) session.save(jiraProfileDAO);
            tx.commit();
        } catch (HibernateException exc) {
            if (tx != null) tx.rollback();
            LOGGER.error("Transaction rollback", exc);
        }
        return id;
    }

    @Override
    public JiraProfile getJiraProfile(Integer id, boolean lazy) {
        Transaction tx = null;
        JiraProfile jiraProfile = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            jiraProfile = ObjectConverter.getInstance().convertJiraProfileDAOToCore(session.get(JiraProfileDAO.class, id));
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't get JIRA Profile by id: %d", id), exc);
        }
        return jiraProfile;
    }

    @Override
    public List<JiraProfile> getJiraProfileList(boolean lazy) {
        Transaction tx;
        List<JiraProfileDAO> jiraProfileDAOList = new ArrayList<>();
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            jiraProfileDAOList = session.createCriteria(JiraProfileDAO.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
            tx.commit();
            return ObjectConverter.getInstance().convertJiraProfiles(jiraProfileDAOList);
        } catch (Exception exc) {
            LOGGER.error("Can't get list of JIRA Profiles", exc);
        }
        return ObjectConverter.getInstance().convertJiraProfiles(jiraProfileDAOList);
    }

    @Override
    public void removeJiraProfile(Integer id) {
        Transaction tx;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            JiraProfileDAO jiraProfileDAO = session.get(JiraProfileDAO.class, id);
            session.delete(jiraProfileDAO);
            tx.commit();
        } catch (Exception exc) {
            LOGGER.error("Can't remove JIRA Profile with id: " + id, exc);
        }
    }

    @Override
    public Integer updateJiraProfile(BusinessObject entity) {
        Transaction tx = null;
        Integer id = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            JiraProfileDAO jiraProfileDAO = ObjectConverter.getInstance().convertJiraProfileToDAO((JiraProfile) entity);
            session.update(jiraProfileDAO);
            id = jiraProfileDAO.getId();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't update JIRA profile with id: %s, name: %s", ((JiraProfile) entity).getId(), ((JiraProfile) entity).getName()), exc);
        }
        return id;
    }

    @Override
    public void removeAllJiraProfile() {
    }

    @Override
    public void addDataSet(BusinessObject entity) {
        Transaction tx = null;
        DataSetDAO dataSetDAO;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            dataSetDAO = ObjectConverter.getInstance().convertDatasetToDAO((DataSetInfo) entity);
            session.saveOrUpdate(dataSetDAO);
            tx.commit();
        } catch (HibernateException exc) {
            if (tx != null) tx.rollback();
            LOGGER.error("Transaction rollback", exc);
        }
    }

    @Override
    public BusinessObject getDataSet(Integer id) {
        Transaction tx = null;
        DataSetInfo dataSet = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            dataSet = ObjectConverter.getInstance().convertDataSetDAOToCore(session.get(DataSetDAO.class, id));
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't get DataSet by id: %d", id), exc);
        }
        return dataSet;
    }

    @Override
    public DataSetInfo getDataSetByName(String name) {
//        Transaction tx = null;
//        DataSetInfo dataSet = null;
//        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
//            tx = session.beginTransaction();
//            dataSet = ObjectConverter.getInstance().convertDataSetDAOToCore((DataSetDAO) session.createCriteria(DataSetDAO.class).add(Restrictions.eq("fileName", name)).uniqueResult());
//            tx.commit();
//        } catch (Exception exc) {
//            if (tx != null) tx.rollback();
//            LOGGER.error(String.format("Can't get DataSet by name: %s", name), exc);
//        }
//        return dataSet;

        Transaction tx;
        DataSetInfo dataSet;
        Session session = HibSessionFactory.getInstance().getFactory().openSession();
        tx = session.beginTransaction();
        dataSet = ObjectConverter.getInstance().convertDataSetDAOToCore((DataSetDAO) session.createCriteria(DataSetDAO.class).add(Restrictions.eq("fileName", name)).uniqueResult());
        tx.commit();

        return dataSet;
    }

    @Override
    public List<DataSetInfo> getDataSetList(boolean lazy) {
        Transaction tx;
        List<DataSetDAO> dataSetDAOList = new ArrayList<>();
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            dataSetDAOList = session.createCriteria(DataSetDAO.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
            tx.commit();
            return ObjectConverter.getInstance().convertDataSets(dataSetDAOList);
        } catch (Exception exc) {
            LOGGER.error("Can't get list of DataSet", exc);
        }
        return ObjectConverter.getInstance().convertDataSets(dataSetDAOList);
    }

    @Override
    public void removeDataSet(Integer id) {
        Transaction tx;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            DataSetDAO dataSetDAO = session.get(DataSetDAO.class, id);
            String fullPath = String.format("%s/%s", SystemPreferenceManager.getInstance().getPreference("dataset_path"), dataSetDAO.getFileName());
            session.delete(dataSetDAO);
            tx.commit();
            File file = new File(fullPath);
            if (file.exists()) {
                if (file.delete()) {
                    LOGGER.warn("DataSet file deleted: " + fullPath);
                } else {
                    LOGGER.warn("Can't delete DataSet file: " + fullPath);
                }
            } else {
                LOGGER.warn("File not exist: " + fullPath);
            }
        } catch (Exception exc) {
            LOGGER.error("Can't remove DataSet with id: " + id, exc);
        }
    }

    @Override
    public Integer updateDataSet(DataSetInfo entity) {
        Transaction tx = null;
        int result = 0;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            Query updateQuery = session.createQuery("update DataSetDAO set description = :description, dateUpdate = :dateUpdate, userUpdate = :userUpdate where id = :id");
            updateQuery.setParameter("description", entity.getDescription());
            updateQuery.setParameter("dateUpdate", LocalDateTime.now());
            updateQuery.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            updateQuery.setParameter("id", entity.getId());
            result = updateQuery.executeUpdate();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error("Can't update DataSet record with id: " + entity.getId(), exc);
        }
        return result;
    }

    @Override
    public Action getAction(Integer id, boolean lazy) {
        Transaction tx = null;
        Action action = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            action = ObjectConverter.getInstance().convertAction(session.get(ActionDAO.class, id));
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't get Action by id: %d", id), exc);
        }
        return action;
    }

    @Override
    public List<Action> getActionList(Integer stepId) {
        Transaction tx;
        List<ActionDAO> actionDAOList = new ArrayList<>();
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            actionDAOList = session.createCriteria(ActionDAO.class)
                    .add(Restrictions.sqlRestriction("step_id = ?", stepId, IntegerType.INSTANCE))
                    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.asc("sortOrder")).list();
            tx.commit();
            return ObjectConverter.getInstance().convertActions(actionDAOList);
        } catch (Exception exc) {
            LOGGER.error("Can't get Action list for Step with id: " + stepId, exc);
        }
        return ObjectConverter.getInstance().convertActions(actionDAOList);
    }

    @Override
    public void removeAction(Integer id) {
        Transaction tx;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            ActionDAO actionDAO = session.get(ActionDAO.class, id);
            session.delete(actionDAO);
            tx.commit();
        } catch (Exception exc) {
            LOGGER.error("Can't remove Action with id: " + id, exc);
        }
    }

    @Override
    public int updateAction(Action entity) {
        Transaction tx = null;
        int result = 0;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            Query updateQuery = session.createQuery("update ActionDAO set name = :name, field = :field, variable = :variable, fieldType = :fieldType, delay = :delay, unit = :unit, data = :data, dateUpdate = :dateUpdate, userUpdate = :userUpdate where id = :id");
            updateQuery.setParameter("name", entity.getActionParam().getName());
            updateQuery.setParameter("field", entity.getActionParam().getField());
            updateQuery.setParameter("variable", entity.getActionParam().getValue());
            updateQuery.setParameter("fieldType", entity.getActionParam().getFieldType().toString());
            updateQuery.setParameter("delay", entity.getDelay());
            updateQuery.setParameter("unit", entity.getUnit().toString());
            updateQuery.setParameter("data", entity.getActionParam().getData());

            updateQuery.setParameter("dateUpdate", LocalDateTime.now());
            updateQuery.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            updateQuery.setParameter("id", entity.getId());
            result = updateQuery.executeUpdate();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error("Can't update Action with id: " + entity.getId(), exc);
        }
        return result;
    }

    @Override
    public int changeActionPosition(int firstId, int firstSortOrder, int secondId, int secondSortOrder) {
        int result = 0;
        Transaction tx = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            Query updateFirstQuery = session.createQuery("update ActionDAO set sortOrder = :sortOrder, dateUpdate = :dateUpdate, userUpdate = :userUpdate where id = :id");
            updateFirstQuery.setParameter("sortOrder", secondSortOrder);
            updateFirstQuery.setParameter("dateUpdate", LocalDateTime.now());
            updateFirstQuery.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            updateFirstQuery.setParameter("id", firstId);

            Query updateSecondQuery = session.createQuery("update ActionDAO set sortOrder = :sortOrder, dateUpdate = :dateUpdate, userUpdate = :userUpdate where id = :id");
            updateSecondQuery.setParameter("sortOrder", firstSortOrder);
            updateSecondQuery.setParameter("dateUpdate", LocalDateTime.now());
            updateSecondQuery.setParameter("userUpdate", SecurityContextHolder.getContext().getAuthentication().getName());
            updateSecondQuery.setParameter("id", secondId);

            result = updateFirstQuery.executeUpdate();
            result = result + updateSecondQuery.executeUpdate();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't change position of Actions with id: %d, id: %d", firstId, secondId), exc);
        }

        return result;
    }

    @Override
    public void removeMailProfile(Integer id) {
        Transaction tx;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            MailProfileDAO mailProfileDAO = session.get(MailProfileDAO.class, id);
            session.delete(mailProfileDAO);
            tx.commit();
        } catch (Exception exc) {
            LOGGER.error("Can't remove Mail Profile with id: " + id, exc);
        }
    }

    @Override
    public Integer addMailProfile(BusinessObject entity) {
        Transaction tx = null;
        Integer id = null;
        MailProfileDAO mailProfileDAO;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            mailProfileDAO = ObjectConverter.getInstance().convertMailProfileToDAO((MailProfileImpl) entity);
            id = (Integer) session.save(mailProfileDAO);
            tx.commit();
        } catch (HibernateException exc) {
            if (tx != null) tx.rollback();
            LOGGER.error("Transaction rollback", exc);
        }
        return id;
    }

    @Override
    public MailProfile getMailProfile(Integer id, boolean lazy) {
        Transaction tx = null;
        MailProfile mailProfile = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            mailProfile = ObjectConverter.getInstance().convertMailProfile(session.get(MailProfileDAO.class, id));
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't get JIRA Profile by id: %d", id), exc);
        }
        return mailProfile;
    }

    @Override
    public List<MailProfile> getMailProfileList(boolean lazy) {
        Transaction tx;
        List<MailProfileDAO> mailProfileDAOList = new ArrayList<>();
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            mailProfileDAOList = session.createCriteria(MailProfileDAO.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
            tx.commit();
            return ObjectConverter.getInstance().convertMailProfiles(mailProfileDAOList);
        } catch (Exception exc) {
            LOGGER.error("Can't get list of Mail Profiles", exc);
        }
        return ObjectConverter.getInstance().convertMailProfiles(mailProfileDAOList);
    }

    @Override
    public Integer updateMailProfile(BusinessObject entity) {
        Transaction tx = null;
        Integer id = null;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            MailProfileDAO mailProfileDAO = ObjectConverter.getInstance().convertMailProfileToDAO((MailProfile) entity);
            session.update(mailProfileDAO);
            id = mailProfileDAO.getId();
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't update Mail profile with id: %s, name: %s", ((MailProfile) entity).getId(), ((MailProfile) entity).getName()), exc);
        }
        return id;
    }

    @Override
    public Document getDocument(Integer id) {
        Transaction tx = null;
        Document document = new Document();

        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            document = ObjectConverter.getInstance().convertDocumentDAOToCore(session.get(DocumentDAO.class, id));
            tx.commit();
        } catch (Exception exc) {
            if (tx != null) tx.rollback();
            LOGGER.error(String.format("Can't get Project by id: %d", id), exc);
        }
        return document;
    }
}
