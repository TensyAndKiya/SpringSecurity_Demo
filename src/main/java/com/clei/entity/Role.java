package com.clei.entity;

/**
 * 用户角色
 *
 * @author KIyA
 * @date 2020-04-17
 */
public class Role {
    private String id;
    private String name;
    private boolean deleted;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
