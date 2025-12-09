package com.thirdstone.spring_rest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.NonNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude="artist")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    private Integer songCount;

    @NonNull
    @Column(nullable = false)
    private Integer yearReleased;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Artist artist;

    public Album(String name, Integer songCount, Integer yearReleased, Artist artist) {
        this.name = name;
        this.songCount = songCount;
        this.yearReleased = yearReleased;
        this.artist = artist;
    }
}
