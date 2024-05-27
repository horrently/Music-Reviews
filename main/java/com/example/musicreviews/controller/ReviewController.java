package com.example.musicreviews.controller;

import com.example.musicreviews.model.Album;
import com.example.musicreviews.model.AlbumReview;
import com.example.musicreviews.model.User;
import com.example.musicreviews.security.UserDetailsImpl;
import com.example.musicreviews.service.AlbumReviewService;
import com.example.musicreviews.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/albums/{albumId}/reviews")
public class ReviewController {
    @Autowired
    private AlbumReviewService albumReviewService;
    @Autowired
    private AlbumService albumService;

    @GetMapping("/new")
    public String showReviewForm(@PathVariable Long albumId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            model.addAttribute("user", userDetails);
            Album album = albumService.getAlbumById(albumId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid album Id:" + albumId));

            // проверка, есть ли уже рецензия данного пользователя на данный альбом
            List<AlbumReview> existingReviews = albumReviewService.getReviewsByUserIdAndAlbumId(userDetails.getId(), albumId);
            boolean reviewExists = !existingReviews.isEmpty();

            // информация о наличии рецензии в модель
            model.addAttribute("reviewExists", reviewExists);
            model.addAttribute("album", album);

            // рецензия уже оставлена, не передаем review в модель
            if (!reviewExists) {
                AlbumReview review = new AlbumReview();
                review.setAlbum(album);
                model.addAttribute("review", review);
            }
        }
        return "review_form";
    }

    @PostMapping("/save")
    public String saveReview(@PathVariable Long albumId, @ModelAttribute("review") AlbumReview review) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = new User();
        user.setId(userDetails.getId());

        Album album = albumService.getAlbumById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid album Id:" + albumId));

        review.setUser(user);
        review.setAlbum(album);
        albumReviewService.saveReview(review);
        return "redirect:/albums/" + albumId;
    }


    // форма редактирования рецензии
    @GetMapping("/edit/{reviewId}")
    public String showEditReviewForm(@PathVariable Long albumId, @PathVariable Long reviewId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            AlbumReview review = albumReviewService.getReviewById(reviewId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid review Id:" + reviewId));
            if (!review.getUser().getId().equals(userDetails.getId())) {
                return "error";
            }
            model.addAttribute("review", review);
            model.addAttribute("album", review.getAlbum());
            model.addAttribute("user", userDetails);
        }
        return "edit_review_form";
    }

    // сохранение изменений рецензии
    @PostMapping("/edit/{reviewId}")
    public String updateReview(@PathVariable Long albumId, @PathVariable Long reviewId,
                               @ModelAttribute("review") AlbumReview reviewDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        AlbumReview review = albumReviewService.getReviewById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid review Id:" + reviewId));
        if (!review.getUser().getId().equals(userDetails.getId())) {
            return "error";
        }

        review.setContent(reviewDetails.getContent());
        review.setRating(reviewDetails.getRating());
        albumReviewService.saveReview(review);

        return "redirect:/profile";
    }

    // удаление рецензии
    @PostMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Long albumId, @PathVariable Long reviewId) {

        albumReviewService.deleteReview(reviewId);

        return "redirect:/profile";
    }


}
