package com.github.cmb9400.commonsongs.domain;

import com.wrapper.spotify.model_objects.specification.Track;

import java.io.Serializable;
import java.util.Set;

public class User implements Serializable{

    protected String userId;
    protected Set<Track> savedSongs;
    protected Set<Group> groups;

    public User(){}

    public User(String userId, Set<Track> savedSongs, Set<Group> groups) {
        this.userId = userId;
        this.savedSongs = savedSongs;
        this.groups = groups;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Set<Track> getSavedSongs() {
        return savedSongs;
    }

    public void setSavedSongs(Set<Track> savedSongs) {
        this.savedSongs = savedSongs;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((userId == null) ? 0 : userId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null) return false;

        if (obj instanceof User) {
            User other = (User) obj;
            return other.userId.equals(this.userId);
        } else {
            return false;
        }
    }

}
