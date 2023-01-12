package ru.itb.web.util;

import javax.servlet.http.HttpSession;

public class UserSessionInfo {
    private HttpSession currentSession;
    private String remoteAddr;

    public UserSessionInfo(HttpSession session, String remoteAddr){
        currentSession = session;
        this.remoteAddr = remoteAddr;
    }

    public HttpSession getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(HttpSession currentSession) {
        this.currentSession = currentSession;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }
}
