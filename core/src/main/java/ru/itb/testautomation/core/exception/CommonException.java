package ru.itb.testautomation.core.exception;

public class CommonException extends RuntimeException {
    private String data;

    public CommonException(String message, String data) {
        super(message);
        this.data = data;
    }

    public CommonException(String message) {
        super(message);
    }

    public String getData() {
        return data;
    }
}
