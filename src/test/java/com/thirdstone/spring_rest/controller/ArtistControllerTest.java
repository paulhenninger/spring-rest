package com.thirdstone.spring_rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class ArtistControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String url;

    @PostConstruct
    public void setup() {
        url = "http://localhost:" + port + "/api/v1/artists";
    }

    @Test
    void whenGetAllArtists_thenAssertKnownData() throws Exception {
        ResponseEntity<List<ArtistDto>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ArtistDto> artists = responseEntity.getBody();
        assertThat(artists).isNotEmpty();
    }

    @Test
    void whenGetExistingArtist_thenAssertKnownData() throws Exception {
        ResponseEntity<ArtistDto> responseEntity = this.restTemplate.getForEntity(url + "/1", ArtistDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        ArtistDto artist = responseEntity.getBody();
        assertThat(artist).isNotNull();
        assertEquals(1, artist.getId());
        assertEquals("Marvin Gaye", artist.getName());
    }

    @Test
    void whenGetNonexistentId_thenAssertNotFound() throws Exception {
        ResponseEntity<ArtistDto> responseEntity = this.restTemplate.getForEntity(url + "/100", ArtistDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void whenCreateArtist_thenAssertCreateResponseAndRead() throws Exception {
        ArtistDto artist = new ArtistDto("The Beatles");

        ResponseEntity<ArtistDto> responseEntity = this.restTemplate.postForEntity(url, artist, ArtistDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ArtistDto createdArtist = responseEntity.getBody();
        assertEquals(11, createdArtist.getId());
        assertEquals(artist.getName(), createdArtist.getName());

        responseEntity = this.restTemplate.getForEntity(url + "/11", ArtistDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        ArtistDto readArtist = responseEntity.getBody();
        assertEquals(createdArtist, readArtist);
    }

    @Test
    void whenCreateArtistWithMissingName_thenAssertBadRequest() throws Exception {
        ArtistDto artist = new ArtistDto();

        ResponseEntity<ArtistDto> responseEntity = this.restTemplate.postForEntity(url, artist, ArtistDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    void whenUpdateExistingArtist_thenAssertUpdateResponseAndRead() throws Exception {
        ResponseEntity<ArtistDto> responseEntity = this.restTemplate.getForEntity(url + "/9", ArtistDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        ArtistDto artist = responseEntity.getBody();
        assertEquals(9, artist.getId());
        assertEquals("Bob Dylan", artist.getName());

        artist.setName("Robert Zimmerman");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<ArtistDto> requestEntity = new HttpEntity<>(artist, headers);
        ResponseEntity<ArtistDto> responseEntityArtist = restTemplate.exchange(
                url + "/9",
                HttpMethod.PUT,
                requestEntity,
                ArtistDto.class);

        assertThat(responseEntityArtist.getStatusCode()).isEqualTo(HttpStatus.OK);
        ArtistDto updatedArtist = responseEntityArtist.getBody();
        assertEquals(artist, updatedArtist);

        responseEntity = this.restTemplate.getForEntity(url + "/9", ArtistDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        ArtistDto readArtist = responseEntity.getBody();
        assertEquals(artist, readArtist);
    }

    @Test
    void whenUpdateNonExistingArtist_thenAssertNotFound() throws Exception {
        ArtistDto artist = new ArtistDto("The Rutles");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<ArtistDto> requestEntity = new HttpEntity<>(artist, headers);
        ResponseEntity<ArtistDto> responseEntity = restTemplate.exchange(
                url + "/100",
                HttpMethod.PUT,
                requestEntity,
                ArtistDto.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void whenDeleteExistingArtist_thenAssertResponseAndRead() throws Exception {
        ResponseEntity<ArtistDto> responseEntity = this.restTemplate.getForEntity(url + "/3", ArtistDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        ArtistDto artist = responseEntity.getBody();
        assertEquals(3, artist.getId());
        assertEquals("Joni Mitchell", artist.getName());

        ResponseEntity<Void> responseEntityDelete = restTemplate.exchange(
                url + "/3",
                HttpMethod.DELETE,
                null,
                Void.class);
        assertThat(responseEntityDelete.getStatusCode()).isEqualTo(HttpStatus.OK);

        responseEntity = this.restTemplate.getForEntity(url + "/3", ArtistDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }

    @Test
    void whenDeleteNonexistingArtist_thenAssertNotFound() throws Exception {
        ResponseEntity<Void> responseEntity = restTemplate.exchange(
                url + "/100",
                HttpMethod.DELETE,
                null,
                Void.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }
}
