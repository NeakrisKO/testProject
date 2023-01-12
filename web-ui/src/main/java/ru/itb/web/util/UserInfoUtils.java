package ru.itb.web.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.*;
import org.hibernate.query.Query;
import ru.itb.web.factory.HibSessionFactory;
import ru.itb.web.model.ui.UISimpleObject;
import ru.itb.web.model.ui.UIUserTestCases;
import ru.itb.web.model.ui.graph.GraphDataSet;
import ru.itb.web.model.ui.graph.TestCaseTotalGraph;

import java.math.BigInteger;

public class UserInfoUtils {
    private static final Logger LOGGER = LogManager.getLogger(UserInfoUtils.class);
    private static volatile UserInfoUtils instance;

    private UserInfoUtils() {
    }

    public static UserInfoUtils getInstance() {
        UserInfoUtils localInstance = instance;
        if (localInstance == null) {
            synchronized (UserInfoUtils.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new UserInfoUtils();
                }
            }
        }
        return localInstance;
    }

    public TestCaseTotalGraph getTestCaseTotalGraph(String user) {
        return getDataForUser(user);
    }

    public UIUserTestCases getUserTestCaseList(String user) {
        return getTestCaseDataListForUser(user);
    }

    private UIUserTestCases getTestCaseDataListForUser(String user) {
        UIUserTestCases uiUserTestCases = new UIUserTestCases();
        Transaction tx;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            Query selectQuery = session.createNativeQuery("SELECT id, name\n" +
                    "  FROM public.testcase where user_create = :userName order by id;");
            selectQuery.setParameter("userName",user);

            ScrollableResults results = selectQuery.scroll(ScrollMode.FORWARD_ONLY);

            while (results.next()) {
                UISimpleObject uiSimpleObject = new UISimpleObject();
                Integer id = (Integer) results.get()[0];
                String name = results.get()[1].toString();
                uiSimpleObject.setId(id);
                uiSimpleObject.setName(name);
                uiUserTestCases.add(uiSimpleObject);
            }
            results.close();

            tx.commit();
        } catch (Exception exc) {
            LOGGER.error("Can't get TestCase list for user: ",exc);
        }
        return uiUserTestCases;
    }

    private TestCaseTotalGraph getDataForUser(String user) {
        TestCaseTotalGraph testCaseTotalGraph = new TestCaseTotalGraph();
        Transaction tx;
        try (Session session = HibSessionFactory.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            Query selectQuery = session.createNativeQuery("SELECT count(*) as \"MY\",to_char(date_create,'YYYY') as \"YEAR\", to_char(date_create,'MM') as \"MONTH\", string_agg(name,',') as \"NAMES\"\n" +
                    "  FROM public.testcase\n" +
                    "  WHERE user_create = :userName group by to_char(date_create,'YYYY'), to_char(date_create,'MM');\n");
            selectQuery.setParameter("userName",user);

            Query selectQueryTotal = session.createNativeQuery("SELECT count(*) as \"MY\",to_char(date_create,'YYYY') as \"YEAR\", to_char(date_create,'MM') as \"MONTH\", string_agg(name,',') as \"NAMES\"\n" +
                    "  FROM public.testcase\n" +
                    "  group by to_char(date_create,'YYYY'), to_char(date_create,'MM');\n");

            ScrollableResults resultsTotal = selectQueryTotal.scroll(ScrollMode.FORWARD_ONLY);
            GraphDataSet datasetTotal = new GraphDataSet();
            while (resultsTotal.next()) {
                Integer count =((BigInteger) resultsTotal.get()[0]).intValue();
                Integer year = Integer.parseInt(resultsTotal.get()[1].toString());
                Integer month = Integer.parseInt(resultsTotal.get()[2].toString());
                String names = resultsTotal.get()[3].toString();
                datasetTotal.addData(count);
                testCaseTotalGraph.addLabel(year.toString() + "-" + month.toString());
            }
            datasetTotal.setLabel("Total TestCases");
            datasetTotal.setBackgroundColor("#9CCC65");
            datasetTotal.setBorderColor("#7CB342");
            testCaseTotalGraph.addDatasets(datasetTotal);

            ScrollableResults results = selectQuery.scroll(ScrollMode.FORWARD_ONLY);
            GraphDataSet dataset = new GraphDataSet();
            while (results.next()) {
                Integer count =((BigInteger) results.get()[0]).intValue();
                Integer year = Integer.parseInt(results.get()[1].toString());
                Integer month = Integer.parseInt(results.get()[2].toString());
                String names = results.get()[3].toString();
                dataset.addData(count);
//                testCaseTotalGraph.addLabel(year.toString() + "-" + month.toString());
            }
            dataset.setLabel("My TestCases");
            dataset.setBackgroundColor("#42A5F5");
            dataset.setBorderColor("#1E88E5");
            testCaseTotalGraph.addDatasets(dataset);

            results.close();
            tx.commit();
        } catch (Exception exc) {
            LOGGER.error("Can't get TestCase's data for user: " + user,exc);
        }
        return testCaseTotalGraph;
    }
}
