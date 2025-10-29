package com.thirdstone.spring_rest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @NonNull
    @Column(nullable = false)
    private String artist;

    @NonNull
    @Column(nullable = false)
    private String title;

    private Integer songCount;

    @NonNull
    @Column(nullable = false)
    private Integer yearReleased;

    public Album(String artist, String title, Integer songCount, Integer yearReleased) {
        this.artist = artist;
        this.title = title;
        this.songCount = songCount;
        this.yearReleased = yearReleased;
    }
}
