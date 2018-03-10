package com.github.cmb9400.commonsongs.domain;

import java.io.Serializable;
import java.util.Set;

public class Group implements Serializable{
    protected String groupId;
    protected String name;
    protected Set<User> users;

    public Group(){}

    public Group(String groupId, String name, Set<User> users) {
        this.groupId = groupId;
        this.name = name;
        this.users = users;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((groupId == null) ? 0 : groupId.hashCode());
        result = prime * result
                + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null) return false;

        if (obj instanceof Group) {
            Group other = (Group) obj;

            return other.groupId.equals(this.groupId) &&
                    other.name.equals(this.name);
        } else {
            return false;
        }
    }

}
