package ru.itb.web.config.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.context.support.WebApplicationContextUtils;
import ru.itb.web.config.UserSessionManager;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import java.util.Date;

public class TAPSessionEventListener extends HttpSessionEventPublisher {
    private static final Logger LOGGER = LogManager.getLogger(TAPSessionEventListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        super.sessionCreated(event);
        event.getSession().setMaxInactiveInterval(3600*2);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        try {
            SessionRegistry sessionRegistry = getSessionBean(event, "sessionRegistry");
            SessionInformation sessionInformation = sessionRegistry != null ? sessionRegistry.getSessionInformation(event.getSession().getId()) : null;
            UserDetails userDetails;
            if (sessionInformation!= null) {
                userDetails = (UserDetails)sessionInformation.getPrincipal();
                if (userDetails!= null) {
                    UserSessionManager.getInstance().removeUser(userDetails.getUsername());
                    LOGGER.info(String.format("User [%s] logged out at [%s]", userDetails.getUsername(), new Date().toString()));
                }
            }
            super.sessionDestroyed(event);
        } catch (Exception exc) {
            LOGGER.warn("Session expired");
        }

    }

    private SessionRegistry getSessionBean(HttpSessionEvent event, String name) {
        HttpSession httpSession = event.getSession();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(httpSession.getServletContext());
        return (SessionRegistry)ctx.getBean(name);
    }


}
