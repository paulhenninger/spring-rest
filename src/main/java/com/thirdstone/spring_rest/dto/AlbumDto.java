package com.thirdstone.spring_rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AlbumDto {

    private long id;

    @NotNull(message = "Name cannot be null")
    private String name;

    private Integer songCount;

    @NotNull(message = "Year released cannot be null")
    private Integer yearReleased;

    private ArtistDto artist;

    public AlbumDto(String name, Integer songCount, Integer yearReleased, ArtistDto artist) {
        this.name = name;
        this.songCount = songCount;
        this.yearReleased = yearReleased;
        this.artist = artist;
    }
}