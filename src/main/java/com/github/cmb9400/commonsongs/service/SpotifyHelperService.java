package com.github.cmb9400.commonsongs.service;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import com.wrapper.spotify.model_objects.specification.Track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;


@Service
public class SpotifyHelperService {

    @Resource
    private Environment env;

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyHelperService.class);

    @Autowired
    private MockDatabase database;

    /**
     * Get an API builder
     * @return a new API builder with client settings
     */
    private SpotifyApi.Builder getApiBuilder() {
        SpotifyApi.Builder builder;

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
    public SpotifyApi login(String code) throws IOException, SpotifyWebApiException, RuntimeException {
        try {
            SpotifyApi api = getApiBuilder().build();

            LOGGER.info("Getting Tokens from Authorization Code...");
            AuthorizationCodeCredentials authorizationCodeCredentials = api.authorizationCode(code).build().execute();
            api.setAccessToken(authorizationCodeCredentials.getAccessToken());
            api.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            return api;
        }
        catch (SpotifyWebApiException | IOException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }



    public boolean collectTracks(SpotifyApi api) {
        try {
            // Collect all of a user's saved tracks using spotify's paginated request format
            // TODO maybe keep the track item itself instead of just URI? check their .equals() method
            // TODO handle the same track with different spotify URIs, ex 3xZ4wgiv2fIiIWrKYPLlng and 0OgGn1ofaj55l2PcihQQGV
            Set<String> savedTracks = new HashSet<>();
            Paging<SavedTrack> savedTrackPage = api.getUsersSavedTracks().build().execute();
            for (int i = 0; i <= savedTrackPage.getTotal(); i += 50) {
                savedTrackPage = api.getUsersSavedTracks().limit(50).offset(i).build().execute(); // 50 is spotify's max

                savedTracks.addAll(
                        Arrays.stream(savedTrackPage.getItems())
                                .map(SavedTrack::getTrack)
                                .map(Track::getUri)
                                .collect(Collectors.toList())
                );
            }

            // save the set of saved tracks
            String userId = api.getCurrentUsersProfile().build().execute().getId();
            database.setUserSavedTracks(userId, savedTracks);

            return true;
        }
        catch (SpotifyWebApiException | IOException e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

}
