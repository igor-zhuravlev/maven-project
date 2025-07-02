package com.epam.learn.ss.repository;

import com.epam.learn.ss.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Integer> { }
