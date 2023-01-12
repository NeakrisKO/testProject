package ru.itb.web.model.ui;

public class UIErrorObject {
    private String message;
    private String imgPath;

    public UIErrorObject() {
    }

    public UIErrorObject(String message, String imgPath) {
        this.message = message;
        this.imgPath = imgPath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
