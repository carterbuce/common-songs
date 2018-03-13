package com.github.cmb9400.commonsongs.service;

import com.github.cmb9400.commonsongs.domain.User;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;


@Service
public class SpotifyHelperService {

    @Resource
    private Environment env;

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyHelperService.class);


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

    /**
     * get a userId from a spotifyApi and handle errors to return null
     */
    public String getUserId(SpotifyApi api) {
        try {
            return api.getCurrentUsersProfile().build().execute().getId();
        }
        catch (SpotifyWebApiException | IOException e) {
            return null;
        }
    }


    /**
     * combine the common tracks of a set of users, ignoring users with zero saved songs
     */
    public Set<Track> getCommonSongs(Set<User> userSet) {
        Set<Track> tracks = new HashSet<>();

        // first add all the tracks from all the users
        for(User user : userSet) {
            tracks.addAll(user.getSavedSongs());
        }

        // next take away tracks that users don't have
        for(User user : userSet) {
            Set<Track> savedSongs = user.getSavedSongs();

            if(savedSongs.size() > 0) {
                tracks.retainAll(savedSongs); // take the intersection
            }
        }

        return tracks;
    }


}
