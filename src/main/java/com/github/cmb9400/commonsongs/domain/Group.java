package com.github.cmb9400.commonsongs.domain;

import java.io.Serializable;

public class Group implements Serializable{
    protected String groupId;
    protected String name;

    public Group(){}

    public Group(String groupId, String name) {
        this.groupId = groupId;
        this.name = name;
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
