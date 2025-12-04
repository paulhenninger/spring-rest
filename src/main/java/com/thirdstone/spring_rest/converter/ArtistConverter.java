package com.thirdstone.spring_rest.converter;

import com.thirdstone.spring_rest.dto.ArtistDto;
import com.thirdstone.spring_rest.entity.Artist;
import org.springframework.stereotype.Service;

@Service
public class ArtistConverter {

    public static ArtistDto convertToDto(Artist artist) {
        return artist != null ? new ArtistDto(artist.getId(), artist.getName()) : null;
    }

    public static Artist convertFromDto(ArtistDto artistDto) {
        return artistDto != null ? new Artist(artistDto.getId(), artistDto.getName()) : null;
    }
}
