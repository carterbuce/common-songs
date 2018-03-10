package com.github.cmb9400.commonsongs.domain;

import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MockRepository {

    private Map<String, User> userMap = new HashMap<>();
    private Map<String, Group> groupMap = new HashMap<>();


    public User getUser(String userId) {
        return userMap.get(userId);
    }

    public Group getGroup(String groupId) {
        return groupMap.get(groupId);
    }

    public Set<Group> getGroups(String userId) {
        return userMap.get(userId).groups;
    }

    public Set<User> getUsers(String groupId) {
        return groupMap.get(groupId).users;
    }

    public Set<String> getTracksForUserId(String userId) {
        return userMap.get(userId).savedSongs;
    }

    public void createUser(User user) {
        userMap.put(user.userId, user);
    }

    public void createGroup(Group group) {
        groupMap.put(group.groupId, group);
    }

    @Transactional
    public void setUserSavedTracks(String userId, Set<String> tracks) {
        userMap.get(userId).setSavedSongs(tracks);
    }

    @Transactional
    public void registerUserWithGroup(String userId, String groupId) {
        if(!isUserRegisteredWithGroup(userId, groupId)) {
            Group group = getGroup(groupId);
            User user = getUser(userId);

            group.users.add(user);
            user.groups.add(group);
        }
    }

    public boolean isUserRegisteredWithGroup(String userId, String groupId) {
        boolean groupHasUser = (getUsers(groupId) != null && getUsers(groupId).stream().map(User::getUserId).collect(Collectors.toSet()).contains(userId));
        boolean userHasGroup = (getGroups(userId) != null && getGroups(userId).stream().map(Group::getGroupId).collect(Collectors.toSet()).contains(groupId));

        return groupHasUser && userHasGroup;
    }

}
