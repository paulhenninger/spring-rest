package com.thirdstone.spring_rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdstone.spring_rest.dto.AlbumDto;
import com.thirdstone.spring_rest.dto.ArtistDto;
import jakarta.annotation.PostConstruct;
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
        ResponseEntity<List<AlbumDto>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<AlbumDto> albums = responseEntity.getBody();
        assertThat(albums).isNotEmpty();
    }

    @Test
    void whenGetExistingAlbum_thenAssertKnownData() throws Exception {
        ResponseEntity<AlbumDto> responseEntity = this.restTemplate.getForEntity(url + "/1", AlbumDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        AlbumDto album = responseEntity.getBody();
        assertThat(album).isNotNull();
        assertEquals(1, album.getId());
        assertEquals("Marvin Gaye", album.getArtist().getName());
        assertEquals("What`s Going On", album.getName());
        assertEquals(9, album.getSongCount());
        assertEquals(1971, album.getYearReleased());
    }

    @Test
    void whenGetExistingAlbumsByArtist_thenAssertKnownData() throws Exception {
        ArtistDto artist = new ArtistDto(1, "Marvin Gaye");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ArtistDto> requestEntity = new HttpEntity<>(artist, headers);


        ResponseEntity<List<AlbumDto>> responseEntity = restTemplate.exchange(
                url + "/artist",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<AlbumDto> albums = responseEntity.getBody();
        assertThat(albums).isNotNull();
        assertEquals(1, albums.size());
        AlbumDto album = albums.getFirst();
        assertEquals(1, album.getId());
        assertEquals("Marvin Gaye", album.getArtist().getName());
        assertEquals("What`s Going On", album.getName());
        assertEquals(9, album.getSongCount());
        assertEquals(1971, album.getYearReleased());
    }

    @Test
    void whenGetNonexistentId_thenAssertNotFound() throws Exception {
        ResponseEntity<AlbumDto> responseEntity = this.restTemplate.getForEntity(url + "/100", AlbumDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void whenCreateAlbum_thenAssertCreateResponseAndRead() throws Exception {
        ArtistDto artist = new ArtistDto(5, "The Beatles");
        AlbumDto album = new AlbumDto("Revolver", 1, 1966, artist);

        ResponseEntity<AlbumDto> albumResponseEntity = this.restTemplate.postForEntity(url, album, AlbumDto.class);
        assertThat(albumResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        AlbumDto createdAlbum = albumResponseEntity.getBody();
        assertEquals(11, createdAlbum.getId());
        assertEquals(album.getArtist().getName(), createdAlbum.getArtist().getName());
        assertEquals(album.getName(), createdAlbum.getName());
        assertEquals(album.getSongCount(), createdAlbum.getSongCount());
        assertEquals(album.getYearReleased(), createdAlbum.getYearReleased());

        albumResponseEntity = this.restTemplate.getForEntity(url + "/11", AlbumDto.class);
        assertThat(albumResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        AlbumDto readAlbum = albumResponseEntity.getBody();
        assertEquals(createdAlbum, readAlbum);
    }

    @Test
    void whenCreateAlbumWithMissingName_thenAssertBadRequest() throws Exception {
        ArtistDto artist = new ArtistDto(5, "The Beatles");
        AlbumDto album = new AlbumDto(null, 16, 1979, artist);

        ResponseEntity<AlbumDto> responseEntity = this.restTemplate.postForEntity(url, album, AlbumDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    void whenCreateAlbumWithMissingArtist_thenAssertBadRequest() throws Exception {
        AlbumDto album = new AlbumDto("London Calling", 16, 1979, null);

        ResponseEntity<AlbumDto> responseEntity = this.restTemplate.postForEntity(url, album, AlbumDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    void whenCreateAlbumWithMissingYearReleased_thenAssertBadRequest() throws Exception {
        ArtistDto artist = new ArtistDto(11, "The Clash");
        AlbumDto album = new AlbumDto("London Calling", 16, null, artist);

        ResponseEntity<AlbumDto> responseEntity = this.restTemplate.postForEntity(url, album, AlbumDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    void whenUpdateExistingAlbum_thenAssertUpdateResponseAndRead() throws Exception {
        ResponseEntity<AlbumDto> responseEntity = this.restTemplate.getForEntity(url + "/9", AlbumDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        AlbumDto album = responseEntity.getBody();
        assertEquals(9, album.getId());
        assertEquals("Bob Dylan", album.getArtist().getName());
        assertEquals("Blood on the Tracks", album.getName());
        assertEquals(10, album.getSongCount());
        assertEquals(1975, album.getYearReleased());

        album.setName("Blonde on Blonde");
        album.setSongCount(14);
        album.setYearReleased(1966);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<AlbumDto> requestEntity = new HttpEntity<>(album, headers);
        ResponseEntity<AlbumDto> responseEntityAlbum = restTemplate.exchange(
                url + "/9",
                HttpMethod.PUT,
                requestEntity,
                AlbumDto.class);

        assertThat(responseEntityAlbum.getStatusCode()).isEqualTo(HttpStatus.OK);
        AlbumDto updatedAlbum = responseEntityAlbum.getBody();
        assertEquals(album, updatedAlbum);

        responseEntity = this.restTemplate.getForEntity(url + "/9", AlbumDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        AlbumDto readAlbum = responseEntity.getBody();
        assertEquals(updatedAlbum, readAlbum);
    }

    @Test
    void whenUpdateNonExistingAlbum_thenAssertNotFound() throws Exception {
        ArtistDto artist = new ArtistDto(5, "The Beatles");
        AlbumDto album = new AlbumDto("Revolver", 1, 1966, artist);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<AlbumDto> requestEntity = new HttpEntity<>(album, headers);
        ResponseEntity<AlbumDto> responseEntity = restTemplate.exchange(
                url + "/100",
                HttpMethod.PUT,
                requestEntity,
                AlbumDto.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void whenDeleteExistingAlbum_thenAssertResponseAndRead() throws Exception {
        ResponseEntity<AlbumDto> responseEntity = this.restTemplate.getForEntity(url + "/3", AlbumDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        AlbumDto album = responseEntity.getBody();
        assertEquals(3, album.getId());
        assertEquals("Joni Mitchell", album.getArtist().getName());
        assertEquals("Blue", album.getName());
        assertEquals(10, album.getSongCount());
        assertEquals(1971, album.getYearReleased());

        ResponseEntity<Void> responseEntityDelete = restTemplate.exchange(
                url + "/3",
                HttpMethod.DELETE,
                null,
                Void.class);
        assertThat(responseEntityDelete.getStatusCode()).isEqualTo(HttpStatus.OK);

        responseEntity = this.restTemplate.getForEntity(url + "/3", AlbumDto.class);
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
