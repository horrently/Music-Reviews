package com.example.musicreviews.controller;

import com.example.musicreviews.model.Album;
import com.example.musicreviews.model.AlbumProposal;
import com.example.musicreviews.model.AlbumReview;
import com.example.musicreviews.security.UserDetailsImpl;
import com.example.musicreviews.service.AlbumReviewService;
import com.example.musicreviews.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private AlbumService albumService;

    @Autowired
    private AlbumReviewService albumReviewService;

    @GetMapping("/")
    public String home(Model model) {
        List<Album> albums = albumService.getAllAlbums();
        if (!albums.isEmpty()) {
            Album latestAlbum = albums.get(albums.size() - 1);  // получение последнего альбома
            model.addAttribute("latestAlbum", latestAlbum);

            List<AlbumReview> reviews = albumReviewService.getAllReviews();
            if (!reviews.isEmpty()) {
                AlbumReview latestReview = reviews.get(reviews.size() - 1);  // получение последней рецензии
                model.addAttribute("latestReview", latestReview);
            }
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            model.addAttribute("user", userDetails);
        }
        return "home";
    }

    @GetMapping ("/profile")
    public String userProfile (Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            model.addAttribute("user", userDetails);

            List<AlbumReview> reviews = albumReviewService.getReviewsByUserId(userDetails.getId());
            model.addAttribute("reviews", reviews);

            // счетчик рецензий
            int reviewCount = reviews.size();
            model.addAttribute("reviewCount", reviewCount);
        }
        return "profile";
    }

    @GetMapping("/album_form")
    public String showProposalForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            model.addAttribute("user", userDetails);
            model.addAttribute("proposal", new AlbumProposal());
        }
        return "album_form";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
