package com.thirdstone.spring_rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ArtistDto {

    private long id;

    @NotNull(message = "Name cannot be null")
    private String name;

    public ArtistDto(String name) {
        this.name = name;
    }
}