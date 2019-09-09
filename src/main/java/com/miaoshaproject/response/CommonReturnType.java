package com.miaoshaproject.response;

public class CommonReturnType {
    // Illustrate process result to the response: "success" or "fail"
    private String status;

    // status == success, return data as json
    // status == fail, data will be general error code
    private Object data;

    // define a general create() method
    public static CommonReturnType create(Object result) {
        return CommonReturnType.create(result, "success");
    }

    public static CommonReturnType create(Object result, String status) {
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
