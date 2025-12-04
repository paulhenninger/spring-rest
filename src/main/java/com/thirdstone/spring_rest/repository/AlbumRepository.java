package com.thirdstone.spring_rest.repository;

import com.thirdstone.spring_rest.entity.Album;
import com.thirdstone.spring_rest.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<List<Album>> findByArtist(Artist artist);

    Optional<List<Album>> findByName(String name);

    Optional<List<Album>> findByYearReleased(Integer yearReleased);
}
