package com.thirdstone.spring_rest.service;

import com.thirdstone.spring_rest.entity.Album;
import com.thirdstone.spring_rest.repository.AlbumRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public List<Album> getAlbums() {
        return albumRepository.findAll();
    }

    public Optional<Album> getAlbumById(long id) {
        return albumRepository.findById(id);
    }

    public Optional<List<Album>> getAlbumsByArtist(String artist) {
        return albumRepository.findByArtist(artist);
    }

    public Optional<List<Album>> getAlbumsByTitle(String title) {
        return albumRepository.findByTitle(title);
    }

    public Optional<List<Album>> getAlbumsByYearReleased(Integer yearReleased) {
        return albumRepository.findByYearReleased(yearReleased);
    }

    public Album createOrUpdateAlbum(Album album) {
        return albumRepository.saveAndFlush(album);
    }

    public void deleteAlbum(Long id) {
        albumRepository.deleteById(id);
    }
}
