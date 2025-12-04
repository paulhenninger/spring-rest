package com.thirdstone.spring_rest.service;

import com.thirdstone.spring_rest.converter.AlbumConverter;
import com.thirdstone.spring_rest.converter.ArtistConverter;
import com.thirdstone.spring_rest.dto.AlbumDto;
import com.thirdstone.spring_rest.dto.ArtistDto;
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

    public List<AlbumDto> getAlbums() {
        List<Album> albums = albumRepository.findAll();
        return albums.stream().map(AlbumConverter::convertToDto).toList();
    }

    public Optional<AlbumDto> getAlbumById(long id) {
        return albumRepository.findById(id).map(AlbumConverter::convertToDto);
    }

    public Optional<List<AlbumDto>> getAlbumsByArtist(ArtistDto artist) {
        Optional<List<Album>> albums = albumRepository.findByArtist(ArtistConverter.convertFromDto(artist));
        return albums.map(albumList -> albumList.stream().map(AlbumConverter::convertToDto).toList());
    }

    public Optional<List<AlbumDto>> getAlbumsByName(String name) {
        Optional<List<Album>> albums = albumRepository.findByName(name);
        return albums.map(albumList -> albumList.stream().map(AlbumConverter::convertToDto).toList());
    }

    public Optional<List<AlbumDto>> getAlbumsByYearReleased(Integer yearReleased) {
        Optional<List<Album>> albums = albumRepository.findByYearReleased(yearReleased);
        return albums.map(albumList -> albumList.stream().map(AlbumConverter::convertToDto).toList());
    }

    public AlbumDto createOrUpdateAlbum(AlbumDto albumDto) {
        Album album =  albumRepository.saveAndFlush(AlbumConverter.convertFromDto(albumDto));
        return AlbumConverter.convertToDto(album);
    }

    public void deleteAlbum(Long id) {
        albumRepository.deleteById(id);
    }
}
