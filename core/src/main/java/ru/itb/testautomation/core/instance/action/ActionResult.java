package ru.itb.testautomation.core.instance.action;

public class ActionResult {
    private Object resultRef;
    private String status;
    private String result;

    public ActionResult(){}

    public Object getResultRef() {
        return resultRef;
    }

    public void setResultRef(Object resultRef) {
        this.resultRef = resultRef;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
