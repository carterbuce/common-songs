package com.github.cmb9400.commonsongs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

public interface PageController {

    /**
     * The index of the service.
     * Provides a login link if not logged in already,
     * otherwise shows a list of skipped songs and relevant actions
     */
    @GetMapping("/")
    public String index(Model model, HttpSession session);

    /**
     * callback endpoint for the Spotify OAuth to hit
     */
    @GetMapping("/callback")
    public String callback(String code, Model model, HttpSession session);


    /**
     * endpoint to update a user's saved tracks
     */
    @PostMapping("/update")
    public ResponseEntity updateSavedTracks(HttpSession session);

}