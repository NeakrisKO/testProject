package ru.itb.web.config.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.itb.web.config.UserSessionManager;
import ru.itb.web.util.UserSessionInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@Component("tapAuthenticationSuccessHandler")
public class TAPAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger LOGGER = LogManager.getLogger(TAPAuthenticationSuccessHandler.class);
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        handle(httpServletRequest, httpServletResponse, authentication);
        final HttpSession session = httpServletRequest.getSession(false);
        if (session!= null) {
            UserSessionManager userSessionManager = UserSessionManager.getInstance();
            String user = ((UserDetails)authentication.getPrincipal()).getUsername();
            userSessionManager.addUser(user, new UserSessionInfo(session, httpServletRequest.getRemoteAddr()));
            LOGGER.info(String.format("User [%s] logged in at [%s] form [%s]", user, new Date().toString(), httpServletRequest.getRemoteAddr()));

        }
    }

    private void handle(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
        redirectStrategy.sendRedirect(request, response, "/");
    }
}
