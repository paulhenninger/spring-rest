package com.thirdstone.spring_rest.controller;

import com.thirdstone.spring_rest.dto.AlbumDto;
import com.thirdstone.spring_rest.dto.ArtistDto;
import com.thirdstone.spring_rest.entity.Artist;
import com.thirdstone.spring_rest.service.AlbumService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    public ResponseEntity<List<AlbumDto>> getAlbums() {
        List<AlbumDto> albums = albumService.getAlbums();
        return new ResponseEntity<>(albums, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumDto> getAlbumsById(@PathVariable @NotNull(message = "Album ID cannot be null") Long id) {
        Optional<AlbumDto> album = albumService.getAlbumById(id);
        return album.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/artist")
    public ResponseEntity<List<AlbumDto>> getAlbumsByArtist(@RequestBody @Valid ArtistDto artist) {
        Optional<List<AlbumDto>> albums = albumService.getAlbumsByArtist(artist);
        return albums.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<AlbumDto>> getAlbumsByName(@PathVariable @NotNull(message = "Album name cannot be null") String name) {
        Optional<List<AlbumDto>> albums = albumService.getAlbumsByName(name);
        return albums.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/yearReleased/{yearReleased}")
    public ResponseEntity<List<AlbumDto>> getAlbumsByYearReleased(@PathVariable @NotNull(message = "Year released cannot be null")Integer yearReleased) {
        Optional<List<AlbumDto>> albums = albumService.getAlbumsByYearReleased(yearReleased);
        return albums.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(@RequestBody @Valid AlbumDto album) {
        // Require that the artist be provided.
        if (album.getArtist() == null) {
            return new ResponseEntity<>(album, HttpStatus.BAD_REQUEST);
        }
        // Since we are creating, and the id is auto-generated, ignore any id passed in the request body.
        AlbumDto newAlbum = new AlbumDto(album.getName(), album.getSongCount(), album.getYearReleased(), album.getArtist());
        newAlbum = albumService.createOrUpdateAlbum(newAlbum);
        return new ResponseEntity<>(newAlbum, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumDto> updateAlbumById(@PathVariable @NotNull(message = "Album ID cannot be null") Long id, @RequestBody @Valid AlbumDto album) {
        return albumService.getAlbumById(id)
                .map(existingAlbum -> {
                    album.setId(id);
                    albumService.createOrUpdateAlbum(album);
                    return new ResponseEntity<>(album, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AlbumDto> deleteAlbumById(@PathVariable @NotNull(message = "Album ID cannot be null") Long id) {
        return albumService.getAlbumById(id)
                .map(album -> {
                    albumService.deleteAlbum(id);
                    return new ResponseEntity<AlbumDto>(HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
