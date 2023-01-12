package ru.itb.web.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;
import ru.itb.web.util.UserSessionInfo;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserSessionManager {
    private static final Logger LOGGER = LogManager.getLogger(UserSessionManager.class);
    private Map<String, UserSessionInfo> dataMap;

    private static volatile UserSessionManager instance;

    private UserSessionManager() {
        dataMap = new LinkedHashMap<>();
    }

    public static UserSessionManager getInstance() {
        UserSessionManager localInstance = instance;
        if (localInstance == null) {
            synchronized (UserSessionManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new UserSessionManager();
                }
            }
        }
        return localInstance;
    }

    public Map<String, UserSessionInfo> getInfo() {
        return dataMap;
    }

    public void addUser(String user, UserSessionInfo userSessionInfo) {
        dataMap.put(user, userSessionInfo);
    }

    public void removeUser(String user) {
        if (dataMap.containsKey(user)) {
            dataMap.remove(user);
        }
    }

    public void kickUser(String user) {
        if (dataMap.containsKey(user)) {

            ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(dataMap.get(user).getCurrentSession().getServletContext());
            SessionRegistry registry = (SessionRegistry)ctx.getBean("sessionRegistry");
            for (Object principal : registry.getAllPrincipals()) {
                if (principal instanceof User) {
                    UserDetails userDetails = (UserDetails)principal;
                    if (userDetails.getUsername().equals(user)) {
                        for(SessionInformation information : registry.getAllSessions(userDetails,true)) {
                            information.expireNow();
                            killExpiredSession(information.getSessionId());
                        }
                    }
                }
            }
            dataMap.remove(user);
        }
    }

    private void killExpiredSession(String id) {
        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("Cookie","JSESSIONID="+id);
            HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.exchange("http://localhost:8080/TestAutomation", HttpMethod.GET, requestEntity, String.class);
        } catch (Exception exc) {
            LOGGER.warn(String.format("Can't kill session with id: [%s]",id));
        }
    }

}
