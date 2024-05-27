package com.example.musicreviews.controller;

import com.example.musicreviews.model.Album;
import com.example.musicreviews.model.AlbumReview;
import com.example.musicreviews.security.UserDetailsImpl;
import com.example.musicreviews.service.AlbumReviewService;
import com.example.musicreviews.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AlbumController {
    @Autowired
    private AlbumService albumService;
    @Autowired
    private AlbumReviewService albumReviewService;

    @GetMapping("/albums")
    public String getAllAlbums(Model model, @RequestParam(required = false) String sort,
                               @RequestParam(required = false) String query) {
        List<Album> albums;
        if ("year".equals(sort)) {
            albums = albumService.getAllAlbumsSortedByYear();
        }
        else {
            albums = albumService.getAllAlbums();
        }

        if (query != null && !query.isEmpty()) {
            albums = albums.stream()
                    .filter(album -> album.getTitle().toLowerCase().startsWith(query.toLowerCase()))
                    .collect(Collectors.toList());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            model.addAttribute("user", userDetails);
        }

        model.addAttribute("albums", albums);
        return "albums";
    }

    @GetMapping("/albums/{id}")
    public String getAlbumById(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Optional<Album> albumOptional = albumService.getAlbumById(id);
            if (albumOptional.isPresent()) {
                Album album = albumOptional.get();
                model.addAttribute("album", album);

                List<AlbumReview> reviews = albumReviewService.getReviewsByAlbumId(id);

                model.addAttribute("user", userDetails);
                model.addAttribute("reviews", reviews != null ? reviews : new ArrayList<>());
            }
            return "album";
        } else {
            return "albumNotFound";
        }
    }

    @PostMapping("/albums/{albumId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long albumId, @PathVariable Long reviewId) {
        // удаление рецензии
        albumReviewService.deleteReview(reviewId);
        return "redirect:/albums/{albumId}"; // перенаправление на страницу альбома после удаления
    }


    // удаление альбома
    @PostMapping("/albums/{id}/delete")
    public String deleteAlbum(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Album> albumOptional = albumService.getAlbumById(id);
        if (albumOptional.isPresent()) {
            Album album = albumOptional.get();
            List<AlbumReview> reviews = albumReviewService.getReviewsByAlbumId(album.getId());
            if (!reviews.isEmpty()) {
                // есть рецензии, добавляем предупреждение и перенаправление на альбом
                redirectAttributes.addFlashAttribute("deleteError",
                        "Альбом содержит рецензии и не может быть удален.");
                return "redirect:/albums/" + id;
            } else {
                // рецензий нет, удалить альбом
                albumService.deleteAlbum(id);
                return "redirect:/albums";
            }
        } else {
            return "redirect:/error";
        }
    }

    // отображение формы редактирования альбома
    @GetMapping("/albums/{id}/edit")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN_ROLE"))) {
                return "redirect:/";
            }
            Album album = albumService.getAlbumById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid album Id:" + id));

            model.addAttribute("album", album);
            model.addAttribute("user", userDetails);
            return "edit_album";
        }
        return "error";
    }

    // сохранение обновленного альбома
    @PostMapping("/albums/{id}/update")
    public String updateAlbum(@PathVariable Long id, @ModelAttribute("album") Album albumDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN_ROLE"))) {
            return "redirect:/";
        }

        Album album = albumService.getAlbumById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid album Id:" + id));

        album.setTitle(albumDetails.getTitle());
        album.setArtist(albumDetails.getArtist());
        album.setGenre(albumDetails.getGenre());
        album.setReleaseYear(albumDetails.getReleaseYear());
        albumService.saveAlbum(album);

        return "redirect:/albums/" + id;
    }


}
