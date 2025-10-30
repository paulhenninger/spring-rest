package com.thirdstone.spring_rest;

import com.thirdstone.spring_rest.controller.AlbumController;
import com.thirdstone.spring_rest.repository.AlbumRepository;
import com.thirdstone.spring_rest.service.AlbumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class SpringRestApplicationTests {

    @Autowired
    private AlbumController albumController;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private AlbumRepository albumRepository;

    @Test
    void contextLoads() {
        assertThat(albumController).isNotNull();
        assertThat(albumService).isNotNull();
        assertThat(albumRepository).isNotNull();
    }
}
