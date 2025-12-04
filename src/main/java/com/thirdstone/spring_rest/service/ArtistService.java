package com.thirdstone.spring_rest.service;

import com.thirdstone.spring_rest.converter.ArtistConverter;
import com.thirdstone.spring_rest.dto.ArtistDto;
import com.thirdstone.spring_rest.entity.Artist;
import com.thirdstone.spring_rest.repository.ArtistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<ArtistDto> getArtists() {
        List<Artist> artists = artistRepository.findAll();
        return artists.stream().map(ArtistConverter::convertToDto).toList();
    }

    public Optional<ArtistDto> getArtistById(long id) {
        return artistRepository.findById(id).map(ArtistConverter::convertToDto);
    }

    public ArtistDto createOrUpdateArtist(ArtistDto artistDto) {
        Artist artist =  artistRepository.saveAndFlush(ArtistConverter.convertFromDto(artistDto));
        return ArtistConverter.convertToDto(artist);
    }

    public void deleteArtist(Long id) {
        artistRepository.deleteById(id);
    }
}
