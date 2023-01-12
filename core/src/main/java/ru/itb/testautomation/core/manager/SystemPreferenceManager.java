package ru.itb.testautomation.core.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class SystemPreferenceManager {
    private static final Logger LOGGER = LogManager.getLogger(SystemPreferenceManager.class);
    private static volatile SystemPreferenceManager instance;
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost/testautomation";
    private Connection connection = null;
    private Statement statement = null;

    //  Database credentials
    private static final String USER = "postgres";
    private static final String PASS = "postgres";
    private Map<String, String> preference = new LinkedHashMap<>();
    private SystemPreferenceManager() {
        init();
    }

    public static SystemPreferenceManager getInstance() {
        SystemPreferenceManager localInstance = instance;
        if (localInstance == null) {
            synchronized (SystemPreferenceManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SystemPreferenceManager();
                }
            }
        }
        return localInstance;
    }

    public Map<String, String> getPreference() {

        return preference;
    }

    public String getPreference(String name) {
        return preference.get(name);
    }

    private void init() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
            String sql;
            sql = "SELECT name, value FROM tap_preference";
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                String name = rs.getString("name");
                String value = rs.getString("value");
                preference.put(name, value);
            }
        } catch (SQLException exc) {
            LOGGER.error("SQL exception", exc);
        } catch (Exception exc) {
            LOGGER.error(exc);
        } finally {
            try {
                if (statement != null)
                    connection.close();
            } catch (SQLException exc) {
                LOGGER.error("SQL exception", exc);
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException exc) {
                LOGGER.error("SQL exception", exc);
            }
        }
    }
}
