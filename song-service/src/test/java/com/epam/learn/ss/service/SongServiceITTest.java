package com.epam.learn.ss.service;

import com.epam.learn.ss.dto.DeleteSongRequestDto;
import com.epam.learn.ss.dto.SongDto;
import com.epam.learn.ss.entity.Song;
import com.epam.learn.ss.exception.DeleteInvalidCsvException;
import com.epam.learn.ss.exception.SongAlreadyExistsException;
import com.epam.learn.ss.exception.SongNotFoundException;
import com.epam.learn.ss.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
    properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
    },
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@Testcontainers
class SongServiceITTest {

    private static final String POSTGRES_IMAGE = "postgres:17-alpine";

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_IMAGE)
        .withDatabaseName("song_db")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private SongService songService;

    @Autowired
    private SongRepository songRepository;

    @BeforeEach
    void cleanDb() {
        songRepository.deleteAll();
    }

    @Test
    void shouldSaveSongWhenSongDoesNotExist() {
        final SongDto songDto = SongDto.builder()
            .id(1)
            .name("Song")
            .artist("Artist")
            .album("Album")
            .duration("01:15")
            .year("2026")
            .build();

        SongDto saved = songService.save(songDto);
        Optional<Song> found = songRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1);
        assertThat(found.get().getName()).isEqualTo("Song");
        assertThat(found.get().getArtist()).isEqualTo("Artist");
        assertThat(found.get().getAlbum()).isEqualTo("Album");
        assertThat(found.get().getDuration()).isEqualTo("01:15");
        assertThat(found.get().getYear()).isEqualTo("2026");
    }

    @Test
    void shouldThrowSongAlreadyExistsExceptionWhenSongExists() {
        final SongDto songDto = SongDto.builder()
            .id(1)
            .name("Song")
            .artist("Artist")
            .album("Album")
            .duration("01:15")
            .year("2026")
            .build();

        songService.save(songDto);

        assertThatThrownBy(() -> songService.save(songDto))
            .isExactlyInstanceOf(SongAlreadyExistsException.class);
    }

    @Test
    void shouldThrowSongNotFoundExceptionWhenSongDoesNotExist() {
        assertThatThrownBy(() -> songService.findById(1))
            .isExactlyInstanceOf(SongNotFoundException.class);
    }

    @Test
    void shouldDeleteSongsWhenSongsExist() {
        final List<Song> songs = Stream.iterate(1, i -> i + 1)
            .map(id ->
                Song.builder()
                    .id(id)
                    .name("Song")
                    .artist("Artist")
                    .album("Album")
                    .duration("01:15")
                    .year("2026")
                    .build()
            )
            .limit(3)
            .toList();
        final DeleteSongRequestDto deleteSongRequestDto = new DeleteSongRequestDto("1,2");

        songRepository.saveAll(songs);
        List<Integer> deleted = songService.deleteAllByIds(deleteSongRequestDto);

        assertThat(deleted).isEqualTo(List.of(1, 2));
        assertThat(songRepository.existsById(1)).isFalse();
        assertThat(songRepository.existsById(2)).isFalse();
        assertThat(songRepository.existsById(3)).isTrue();
    }

    @Test
    void shouldThrowDeleteInvalidCsvExceptionWhenDeleteSongRequestDtoIsInvalid() {
        final DeleteSongRequestDto deleteSongRequestDto = new DeleteSongRequestDto("1,2,invalid");

        assertThatThrownBy(() -> songService.deleteAllByIds(deleteSongRequestDto))
            .isExactlyInstanceOf(DeleteInvalidCsvException.class);
    }

}
