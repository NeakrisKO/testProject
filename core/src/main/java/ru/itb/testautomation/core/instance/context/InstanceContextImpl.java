package ru.itb.testautomation.core.instance.context;

import ru.itb.testautomation.core.context.TCContext;
import ru.itb.testautomation.core.context.impl.JSONContextImpl;

public class InstanceContextImpl extends JSONContextImpl {

    private static final String SESSION_ID = "sessionId";

    public static InstanceContextImpl from(TCContext tc) {
        InstanceContextImpl context = new InstanceContextImpl();
        context.put(TCContext.TC, tc);
        return context;
    }

    public TCContext tc() {
        return get(TCContext.TC, TCContext.class);
    }


    public void setTC(TCContext tc) {
        put(TCContext.TC, tc);
    }

    public void setSessionId(Object sessionId) {
        put(SESSION_ID, sessionId);
    }

    public Object getSessionId() {
        return get(SESSION_ID);
    }
}
