package com.epam.learn.ss.controller;

import com.epam.learn.ss.dto.DeleteSongRequestDto;
import com.epam.learn.ss.dto.SongDto;
import com.epam.learn.ss.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PostMapping
    public ResponseEntity<SongDto> createSongMetadata(@RequestBody @Valid SongDto dto) {
        return ResponseEntity.ok(songService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDto> findSongMetadata(@PathVariable Integer id) {
        return ResponseEntity.ok(songService.findById(id));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, List<Integer>>> deleteSongsMetadata(@ModelAttribute DeleteSongRequestDto dto) {
        List<Integer> deletedIds = songService.deleteAllByIds(dto);
        return ResponseEntity.ok(Map.of("ids", deletedIds));
    }

}
