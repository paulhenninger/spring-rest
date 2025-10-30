package com.thirdstone.spring_rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdstone.spring_rest.entity.Album;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AlbumControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String url;

    @PostConstruct
    public void setup() {
        url = "http://localhost:" + port + "/api/v1/albums";
    }

    @Test
    void whenGetAllAlbums_thenAssertKnownData() throws Exception {
        ResponseEntity<List<Album>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Album> albums = responseEntity.getBody();
        assertThat(albums).isNotEmpty();
    }

    @Test
    void whenGetExistingAlbum_thenAssertKnownData() throws Exception {
        ResponseEntity<Album> responseEntity = this.restTemplate.getForEntity(url + "/1", Album.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Album album = responseEntity.getBody();
        assertThat(album).isNotNull();
        assertEquals(1, album.getId());
        assertEquals("Marvin Gaye", album.getArtist());
        assertEquals("What`s Going On", album.getTitle());
        assertEquals(9, album.getSongCount());
        assertEquals(1971, album.getYearReleased());
    }

    @Test
    void whenGetNonexistentId_thenAssertNotFound() throws Exception {
        ResponseEntity<Album> responseEntity = this.restTemplate.getForEntity(url + "/100", Album.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void whenCreateAlbum_thenAssertCreateResponseAndRead() throws Exception {
        Album album = new Album("The Beatles", "Revolver", 1, 1966);

        ResponseEntity<Album> responseEntity = this.restTemplate.postForEntity(url, album, Album.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Album createdAlbum = responseEntity.getBody();
        assertEquals(11, createdAlbum.getId());
        assertEquals(album.getArtist(), createdAlbum.getArtist());
        assertEquals(album.getTitle(), createdAlbum.getTitle());
        assertEquals(album.getSongCount(), createdAlbum.getSongCount());
        assertEquals(album.getYearReleased(), createdAlbum.getYearReleased());

        responseEntity = this.restTemplate.getForEntity(url + "/11", Album.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Album readAlbum = responseEntity.getBody();
        assertEquals(createdAlbum, readAlbum);
    }

    @Test
    void whenCreateAlbumWithMissingAlbum_thenAssertBadRequest() throws Exception {
        Album album = new Album("The Clash", null, 16, 1979);

        ResponseEntity<Album> responseEntity = this.restTemplate.postForEntity(url, album, Album.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    void whenCreateAlbumWithMissingArtist_thenAssertBadRequest() throws Exception {
        Album album = new Album(null, "London Calling", 16, 1979);

        ResponseEntity<Album> responseEntity = this.restTemplate.postForEntity(url, album, Album.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    void whenCreateAlbumWithMissingYearReleased_thenAssertBadRequest() throws Exception {
        Album album = new Album("The Clash", "London Calling", 16, null);

        ResponseEntity<Album> responseEntity = this.restTemplate.postForEntity(url, album, Album.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    void whenUpdateExistingAlbum_thenAssertUpdateResponseAndRead() throws Exception {
        ResponseEntity<Album> responseEntity = this.restTemplate.getForEntity(url + "/9", Album.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        Album album = responseEntity.getBody();
        assertEquals(9, album.getId());
        assertEquals("Bob Dylan", album.getArtist());
        assertEquals("Blood on the Tracks", album.getTitle());
        assertEquals(10, album.getSongCount());
        assertEquals(1975, album.getYearReleased());

        album.setArtist("Robert Zimmerman");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Album> requestEntity = new HttpEntity<>(album, headers);
        ResponseEntity<Album> responseEntityAlbum = restTemplate.exchange(
                url + "/9",
                HttpMethod.PUT,
                requestEntity,
                Album.class);

        assertThat(responseEntityAlbum.getStatusCode()).isEqualTo(HttpStatus.OK);
        Album updatedAlbum = responseEntityAlbum.getBody();
        assertEquals(album, updatedAlbum);

        responseEntity = this.restTemplate.getForEntity(url + "/9", Album.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        Album readAlbum = responseEntity.getBody();
        assertEquals(album, readAlbum);
    }

    @Test
    void whenUpdateNonExistingAlbum_thenAssertNotFound() throws Exception {
        Album album = new Album("The Beatles", "Revolver", 1, 1966);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Album> requestEntity = new HttpEntity<>(album, headers);
        ResponseEntity<Album> responseEntity = restTemplate.exchange(
                url + "/100",
                HttpMethod.PUT,
                requestEntity,
                Album.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void whenDeleteExistingAlbum_thenAssertResponseAndRead() throws Exception {
        ResponseEntity<Album> responseEntity = this.restTemplate.getForEntity(url + "/3", Album.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        Album album = responseEntity.getBody();
        assertEquals(3, album.getId());
        assertEquals("Joni Mitchell", album.getArtist());
        assertEquals("Blue", album.getTitle());
        assertEquals(10, album.getSongCount());
        assertEquals(1971, album.getYearReleased());

        ResponseEntity<Void> responseEntityDelete = restTemplate.exchange(
                url + "/3",
                HttpMethod.DELETE,
                null,
                Void.class);
        assertThat(responseEntityDelete.getStatusCode()).isEqualTo(HttpStatus.OK);

        responseEntity = this.restTemplate.getForEntity(url + "/3", Album.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void whenDeleteNonexistingAlbum_thenAssertNotFound() throws Exception {
        ResponseEntity<Void> responseEntity = restTemplate.exchange(
                url + "/100",
                HttpMethod.DELETE,
                null,
                Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }
}
