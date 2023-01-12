package ru.itb.web.config.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import ru.itb.web.config.UserSessionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component("tapLogoutSuccessHandler")
public class TAPLogoutSuccessHandler implements LogoutSuccessHandler {
    private static final Logger LOGGER = LogManager.getLogger(TAPLogoutSuccessHandler.class);

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails)authentication.getPrincipal();
        LOGGER.info(String.format("User [%s] logged out at [%s]", userDetails.getUsername(), new Date().toString()));
        httpServletResponse.sendRedirect("/TestAutomation");
        UserSessionManager.getInstance().kickUser(userDetails.getUsername());
    }
}
