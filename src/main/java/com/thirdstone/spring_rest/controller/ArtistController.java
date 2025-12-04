package com.thirdstone.spring_rest.controller;

import com.thirdstone.spring_rest.dto.ArtistDto;
import com.thirdstone.spring_rest.service.ArtistService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public ResponseEntity<List<ArtistDto>> getArtists() {
        List<ArtistDto> artists = artistService.getArtists();
        return new ResponseEntity<>(artists, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDto> getArtistById(@PathVariable @NotNull(message = "Artist ID cannot be null") Long id) {
        Optional<ArtistDto> artist = artistService.getArtistById(id);
        return artist.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ArtistDto> createArtist(@RequestBody @Valid ArtistDto artist) {
        // Since we are creating, and the id is auto-generated, ignore any id passed in the request body.
        ArtistDto newArtist = new ArtistDto(artist.getName());
        newArtist = artistService.createOrUpdateArtist(newArtist);
        return new ResponseEntity<>(newArtist, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistDto> updateArtistById(@PathVariable @NotNull(message = "Artist ID cannot be null") Long id, @RequestBody @Valid ArtistDto artist) {
        return artistService.getArtistById(id)
                .map(existingArtist -> {
                    artist.setId(id);
                    artistService.createOrUpdateArtist(artist);
                    return new ResponseEntity<>(artist, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ArtistDto> deleteArtistById(@PathVariable @NotNull(message = "Artist ID cannot be null") Long id) {
        return artistService.getArtistById(id)
                .map(artist -> {
                    artistService.deleteArtist(id);
                    return new ResponseEntity<ArtistDto>(HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
