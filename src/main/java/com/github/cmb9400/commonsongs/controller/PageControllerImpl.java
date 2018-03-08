package com.github.cmb9400.commonsongs.controller;

import com.github.cmb9400.commonsongs.service.SpotifyHelperService;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

import java.io.IOException;

@Controller
public class PageControllerImpl implements PageController {


    @Autowired
    SpotifyHelperService spotifyHelperService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PageControllerImpl.class);


    @Override
    public String index(Model model, HttpSession session) {
        if (session.getAttribute("api") == null) {
            String authURL = spotifyHelperService.getAuthorizationURL();
            model.addAttribute("loginURL", authURL);

            return "login";
        }
        else {
            return "index";

            //click add user, it adds their userid to a session variable (so maintain a list of those, no need for api)
            // on the site, it'll list "[<userid> 5376 songs]" for each user, have a "compare!" button
            // "you'll have to do this on a different device or a private browsing window" for share link

            // eventually store in a non-relational database (?) to prevent memory leak and reset it every time
            // collect tracks is called anyway


            // have a list of "groups" that gets stored with each user, each group has an ID which is also the sharing link
            // click on sharing link, get added to group (and group gets added to you)
        }
    }


    @Override
    public String callback(@RequestParam(value="code", required=true) String code, Model model, HttpSession session) {
        try {
            SpotifyApi api = spotifyHelperService.login(code);
            session.setAttribute("api", api);

            return "redirect:/";
        }
        catch (SpotifyWebApiException | IOException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    @Override
    public ResponseEntity updateSavedTracks(HttpSession session) {
        boolean success = false;

        if (session.getAttribute("api") != null) {
            LOGGER.info("Getting saved tracks...");
            SpotifyApi api = (SpotifyApi) session.getAttribute("api");
            success = spotifyHelperService.collectTracks(api);
            LOGGER.info("Saved tracks collected.");
        }

        return success ? new ResponseEntity(HttpStatus.OK) : new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

}
