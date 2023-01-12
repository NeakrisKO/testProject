package ru.itb.configuration.intercepter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.itb.configuration.DAO.action.ActionDAO;
import ru.itb.configuration.DAO.dataset.DataSetDAO;
import ru.itb.configuration.DAO.jira.JiraProfileDAO;
import ru.itb.configuration.DAO.label.LabelDAO;
import ru.itb.configuration.DAO.project.ProjectDAO;
import ru.itb.configuration.DAO.step.UIStepDAO;
import ru.itb.configuration.DAO.testcase.TestCaseDAO;
import ru.itb.configuration.DAO.testcase.TestCaseGroupDAO;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AuditInterceptor extends EmptyInterceptor {
    private static final Logger LOGGER = LogManager.getLogger(AuditInterceptor.class);
    private int updates;
    private int creates;
    private int loads;
    private int deletes;

    @Override
    public void onDelete(Object entity,
                         Serializable id,
                         Object[] state,
                         String[] propertyNames,
                         Type[] types) {
        deletes++;

        if (entity instanceof ActionDAO) {
            addDeleteOperationToLog("Action", ((ActionDAO) entity).getName(), ((ActionDAO) entity).getId());
        } else if ( entity instanceof JiraProfileDAO) {
            addDeleteOperationToLog("JIRA Profile", ((JiraProfileDAO) entity).getName(), ((JiraProfileDAO) entity).getId());
        } else if ( entity instanceof LabelDAO) {
            addDeleteOperationToLog("Label", ((LabelDAO) entity).getName(), ((LabelDAO) entity).getId());
        } else if (entity instanceof ProjectDAO) {
            addDeleteOperationToLog("Project", ((ProjectDAO) entity).getName(), ((ProjectDAO) entity).getId());
        } else if (entity instanceof UIStepDAO) {
            addDeleteOperationToLog("UI Step", ((UIStepDAO) entity).getName(), ((UIStepDAO) entity).getId());
        } else if (entity instanceof TestCaseDAO) {
            addDeleteOperationToLog("TestCase", ((TestCaseDAO) entity).getName(), ((TestCaseDAO) entity).getId());
        } else if (entity instanceof TestCaseGroupDAO) {
            addDeleteOperationToLog("Group of TestCase", ((TestCaseGroupDAO) entity).getName(), ((TestCaseGroupDAO) entity).getId());
        }
    }

    private void addDeleteOperationToLog(String objectType, String name, Integer id) {
        LOGGER.warn(String.format("%s [%s] with id [%s] was deleted by user [%s]",objectType, name, id, SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    @Override
    public boolean onFlushDirty(Object entity,
                                Serializable id,
                                Object[] currentState,
                                Object[] previousState,
                                String[] propertyNames,
                                Type[] types) {
        if (entity instanceof ActionDAO ||
                entity instanceof JiraProfileDAO ||
                entity instanceof LabelDAO ||
                entity instanceof ProjectDAO ||
                entity instanceof UIStepDAO ||
                entity instanceof TestCaseDAO ||
                entity instanceof TestCaseGroupDAO ||
                entity instanceof DataSetDAO) {
                updates++;
                int dateUpdateIndex = ArrayUtils.indexOf(propertyNames, "dateUpdate");
                currentState[dateUpdateIndex] = LocalDateTime.now();
                int userUpdateIndex = ArrayUtils.indexOf(propertyNames, "userUpdate");
                currentState[userUpdateIndex] = SecurityContextHolder.getContext().getAuthentication().getName();
                return true;
        }
        return false;
    }
    @Override
    public boolean onLoad(Object entity,
                          Serializable id,
                          Object[] state,
                          String[] propertyNames,
                          Type[] types) {
        if (entity instanceof ActionDAO ||
                entity instanceof JiraProfileDAO ||
                entity instanceof LabelDAO ||
                entity instanceof ProjectDAO ||
                entity instanceof UIStepDAO ||
                entity instanceof TestCaseDAO ||
                entity instanceof TestCaseGroupDAO ||
                entity instanceof DataSetDAO) {
            loads++;
        }
        return false;
    }
    @Override
    public boolean onSave(Object entity,
                          Serializable id,
                          Object[] state,
                          String[] propertyNames,
                          Type[] types) {
        if (entity instanceof ActionDAO ||
                entity instanceof JiraProfileDAO ||
                entity instanceof LabelDAO ||
                entity instanceof ProjectDAO ||
                entity instanceof UIStepDAO ||
                entity instanceof TestCaseDAO ||
                entity instanceof TestCaseGroupDAO ||
                entity instanceof DataSetDAO) {
            creates++;
            int dateCreateIndex = ArrayUtils.indexOf(propertyNames, "dateCreate");
            state[dateCreateIndex] = LocalDateTime.now();
            int userCreateIndex = ArrayUtils.indexOf(propertyNames, "userCreate");
            state[userCreateIndex] = SecurityContextHolder.getContext().getAuthentication().getName();
            return true;
        }
        return false;
    }
    @Override
    public void afterTransactionCompletion(Transaction tx) {
        LOGGER.info(String.format("Selects: %d, Updates: %d, Insert: %d, Deletes: %d",loads, updates,creates, deletes));
        updates = 0;
        creates = 0;
        loads = 0;
        deletes = 0;
    }

}