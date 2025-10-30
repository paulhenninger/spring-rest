package com.thirdstone.spring_rest.controller;

import com.thirdstone.spring_rest.entity.Album;
import com.thirdstone.spring_rest.service.AlbumService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<List<Album>> getAlbums() {
        List<Album> albums = albumService.getAlbums();
        return new ResponseEntity<>(albums, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbumsById(@PathVariable Long id) {
        Optional<Album> album = albumService.getAlbumById(id);
        return album.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/artist/{artist}")
    public ResponseEntity<List<Album>> getAlbumsByArtist(@PathVariable String artist) {
        Optional<List<Album>> albums = albumService.getAlbumsByArtist(artist);
        return albums.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<Album>> getAlbumsByTitle(@PathVariable String title) {
        Optional<List<Album>> albums = albumService.getAlbumsByTitle(title);
        return albums.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/yearReleased/{yearReleased}")
    public ResponseEntity<List<Album>> getAlbumsByYearReleased(@PathVariable Integer yearReleased) {
        Optional<List<Album>> albums = albumService.getAlbumsByYearReleased(yearReleased);
        return albums.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Album> createAlbum(@RequestBody Album album) {
        // Since we are creating, and the id is auto-generated, ignore any id passed in the request body.
        Album newAlbum = new Album(album.getArtist(), album.getTitle(), album.getSongCount(), album.getYearReleased());
        newAlbum = albumService.createOrUpdateAlbum(newAlbum);
        return new ResponseEntity<>(newAlbum, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Album> updateAlbumById(@PathVariable Long id, @RequestBody Album album) {
        return albumService.getAlbumById(id)
                .map(existingAlbum -> {
                    album.setId(id);
                    albumService.createOrUpdateAlbum(album);
                    return new ResponseEntity<>(album, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Album> deleteAlbumById(@PathVariable Long id) {
        return albumService.getAlbumById(id)
                .map(album -> {
                    albumService.deleteAlbum(id);
                    return new ResponseEntity<Album>(HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
