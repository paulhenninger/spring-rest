package com.thirdstone.spring_rest.converter;

import com.thirdstone.spring_rest.dto.AlbumDto;
import com.thirdstone.spring_rest.entity.Album;
import org.springframework.stereotype.Service;

@Service
public class AlbumConverter {

    public static AlbumDto convertToDto(Album album) {
        return album != null ? new AlbumDto(album.getId(), album.getName(), album.getSongCount(),
                album.getYearReleased(), ArtistConverter.convertToDto(album.getArtist())) : null;
    }

    public static Album convertFromDto(AlbumDto albumDto) {
        return albumDto != null ? new Album(albumDto.getId(), albumDto.getName(), albumDto.getSongCount(),
                albumDto.getYearReleased(), ArtistConverter.convertFromDto(albumDto.getArtist())) : null;
    }
}
