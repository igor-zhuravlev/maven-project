package com.epam.learn.ss.service.impl

import com.epam.learn.ss.dto.DeleteSongRequestDto
import com.epam.learn.ss.dto.SongDto
import com.epam.learn.ss.entity.Song
import com.epam.learn.ss.exception.DeleteInvalidCsvException
import com.epam.learn.ss.exception.SongAlreadyExistsException
import com.epam.learn.ss.exception.SongNotFoundException
import com.epam.learn.ss.mapper.SongMapper
import com.epam.learn.ss.repository.SongRepository
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator
import spock.lang.Specification

class SongServiceImplSpec extends Specification {

    def songRepository = Mock(SongRepository)
    def songMapper = Mock(SongMapper)
    def validator = Mock(Validator)

    def songService = new SongServiceImpl(songRepository, songMapper, validator)

    def "should save song when song does not exist"() {
        given:
        def dto = SongDto.builder()
            .id(1)
            .name("Song")
            .artist("Artist")
            .album("Album")
            .duration("01:15")
            .year("2026")
            .build()
        def song = Song.builder()
            .id(1)
            .name("Song")
            .artist("Artist")
            .album("Album")
            .duration("01:15")
            .year("2026")
            .build()
        def savedSong = Song.builder()
            .id(1)
            .name("Song")
            .artist("Artist")
            .album("Album")
            .duration("01:15")
            .year("2026")
            .build()

        when:
        def actual = songService.save(dto)

        then:
        actual.id == 1

        1 * songRepository.existsById(1) >> false
        1 * songMapper.toEntity(dto) >> song
        1 * songRepository.save(song) >> savedSong
        0 * _
    }

    def "should throw song already exists exception when song exists"() {
        given:
        def dto = SongDto.builder()
            .id(1)
            .name("Song")
            .artist("Artist")
            .album("Album")
            .duration("01:15")
            .year("2026")
            .build()

        when:
        songService.save(dto)

        then:
        thrown(SongAlreadyExistsException)

        1 * songRepository.existsById(1) >> true
        0 * songMapper._
        0 * songRepository.save(_)
        0 * _
    }

    def "should return song when song exists"() {
        given:
        def song = Song.builder()
            .id(1)
            .name("Song")
            .artist("Artist")
            .album("Album")
            .duration("01:15")
            .year("2026")
            .build()

        def dto = SongDto.builder()
            .id(1)
            .name("Song")
            .artist("Artist")
            .album("Album")
            .duration("01:15")
            .year("2026")
            .build()

        when:
        def actual = songService.findById(1)

        then:
        actual == dto

        1 * songRepository.findById(1) >> Optional.of(song)
        1 * songMapper.toDto(song) >> dto
        0 * _
    }

    def "should throw song not found exception when song does not exist"() {
        when:
        songService.findById(1)

        then:
        thrown(SongNotFoundException)

        1 * songRepository.findById(1) >> Optional.empty()
        0 * songMapper._
        0 * _
    }

    def "should delete songs when songs exist"() {
        given:
        def dto = new DeleteSongRequestDto("1,2")
        def ids = Set.of(1, 2)

        def songs = [
            Song.builder().id(1).build(),
            Song.builder().id(2).build()
        ]

        when:
        def actual = songService.deleteAllByIds(dto)

        then:
        actual == [1, 2]

        1 * validator.validate(dto) >> Set.of()
        1 * songRepository.findAllById(ids) >> songs
        1 * songRepository.deleteAllById([1, 2])
        0 * _
    }

    def "should throw delete invalid csv exception when delete song request dto is invalid"() {
        given:
        def dto = new DeleteSongRequestDto("invalid")
        def violation = Mock(ConstraintViolation<DeleteSongRequestDto>)
        def violations = Set.of(violation)

        when:
        songService.deleteAllByIds(dto)

        then:
        thrown(DeleteInvalidCsvException)

        1 * validator.validate(dto) >> violations
        0 * songRepository._
        0 * _
    }

}