package com.gmail.iikaliada.model;

public class User {
    private int id;
    private String name;
    private String lastname;
    private String username;
    private String userId;
    private int roleId;
    private int kicked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getKicked() {
        return kicked;
    }

    public void setKicked(int kicked) {
        this.kicked = kicked;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", username='" + username + '\'' +
                ", userId='" + userId + '\'' +
                ", roleId=" + roleId + '\'' +
                ", kicked=" + kicked +
                '}';
    }
}
