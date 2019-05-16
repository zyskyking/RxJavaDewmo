package com.example.rxjavadewmo.bean;

public class BaseHttpBean {
    private String status;
    private String info;
    private String server_no;
    private String ip;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getServer_no() {
        return server_no;
    }

    public void setServer_no(String server_no) {
        this.server_no = server_no;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "BaseHttpBean{" +
                "status='" + status + '\'' +
                ", info='" + info + '\'' +
                ", server_no='" + server_no + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
