package com.pxene.odin.cloud.domain.model;

import java.util.Date;

public class UserModel {
    private String id;

    private String username;

    private String password;

    private Boolean status;

    private Date passwordLastUpdatetime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getPasswordLastUpdatetime() {
        return passwordLastUpdatetime;
    }

    public void setPasswordLastUpdatetime(Date passwordLastUpdatetime) {
        this.passwordLastUpdatetime = passwordLastUpdatetime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", username=").append(username);
        sb.append(", password=").append(password);
        sb.append(", status=").append(status);
        sb.append(", passwordLastUpdatetime=").append(passwordLastUpdatetime);
        sb.append("]");
        return sb.toString();
    }
}