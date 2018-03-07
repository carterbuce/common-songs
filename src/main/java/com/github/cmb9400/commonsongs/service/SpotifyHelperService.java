package com.github.cmb9400.commonsongs.service;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import com.wrapper.spotify.model_objects.specification.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Service
public class SpotifyHelperService {

    @Resource
    public Environment env;

    @Autowired
    ApplicationContext applicationContext;

    public Map<String, SpotifyApi> runningUsers = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyHelperService.class);


    /**
     * Get an API builder
     * @return a new API builder with client settings
     */
    public SpotifyApi.Builder getApiBuilder() {
        SpotifyApi.Builder builder = null;


        URI redirectUri = SpotifyHttpManager.makeUri(env.getProperty("spotify.redirect.uri"));
        builder = SpotifyApi.builder()
                .setClientId(env.getProperty("spotify.client.id"))
                .setClientSecret(env.getProperty("spotify.client.secret"))
                .setRedirectUri(redirectUri);

        return builder;
    }


    /**
     * Get a generated URL to authorize this app with spotify and log in
     * @return a URL string
     */
    public String getAuthorizationURL() {
        LOGGER.info("Getting Authorize URL");
        SpotifyApi api = getApiBuilder().build();

        // Set the necessary scopes that the application will need from the user
        String scopes = env.getProperty("spotify.oauth.scope");

        return api.authorizationCodeUri().scope(scopes).build().execute().toString();
    }


    /**
     * Log in to the account using the authorization code and get the access token
     */
    public String login(String code) throws IOException, SpotifyWebApiException, RuntimeException {
        try {
            SpotifyApi api = getApiBuilder().build();
            User user;

            LOGGER.info("Getting Tokens from Authorization Code...");
            AuthorizationCodeCredentials authorizationCodeCredentials = api.authorizationCode(code).build().execute();
            api.setAccessToken(authorizationCodeCredentials.getAccessToken());
            api.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            user = api.getCurrentUsersProfile().build().execute();
            String userId = user.getId();

            runningUsers.put(userId, api);
            return userId;
        }
        catch (SpotifyWebApiException | IOException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }



    public void collectTracks(String userId, SpotifyApi api) throws SpotifyWebApiException, IOException {
        // Collect all of a user's saved tracks
        Set<SavedTrack> savedTracks = new HashSet<>();
        Paging<SavedTrack> savedTrackPage = api.getUsersSavedTracks().build().execute();
        for(int i = 0; i <= savedTrackPage.getTotal(); i += 50) {
            savedTrackPage = api.getUsersSavedTracks().limit(50).offset(i).build().execute(); // 50 is spotify's max
            savedTracks.addAll(Arrays.asList(savedTrackPage.getItems())); // arrays.asList() is O(1)

        }

    }

}
