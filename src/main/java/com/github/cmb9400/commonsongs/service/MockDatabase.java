package com.github.cmb9400.commonsongs.service;

import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


// TODO replace this data store with an actual database
@Component
public class MockDatabase {

    // map of spotify userId to a set of track URIs that they have saved
    private Map<String, Set<String>> userSavedTracks = new HashMap<>();

    // map of share code to a set of userId
    private Map<String, Set<String>> groupToUsers = new HashMap<>();

    // map of userId to set of share codes
    private Map<String, Set<String>> userToGroup = new HashMap<>();


    public Set<String> getGroupsForUserId(String userId) {
        return userToGroup.get(userId);
    }

    public Set<String> getUserIdsForGroup(String groupId) {
        return groupToUsers.get(groupId);
    }

    public Set<String> getTracksForUserId(String userId) {
        return userSavedTracks.get(userId);
    }

    @Transactional
    public void setUserSavedTracks(String userId, Set<String> tracks) {
        userSavedTracks.put(userId, tracks);
    }

    @Transactional
    public void registerUserWithGroup(String userId, String groupId) {
        if(!isUserRegisteredWithGroup(userId, groupId)) {
            groupToUsers.computeIfAbsent(groupId, k -> new HashSet<>()).add(userId);
            userToGroup.computeIfAbsent(userId, k -> new HashSet<>()).add(groupId);
        }
    }

    public boolean isUserRegisteredWithGroup(String userId, String groupId) {
        boolean groupHasUser = (getUserIdsForGroup(groupId) != null && getUserIdsForGroup(groupId).contains(userId));
        boolean userHasGroup = (getGroupsForUserId(userId) != null && getGroupsForUserId(userId).contains(groupId));

        return groupHasUser && userHasGroup;
    }


}
