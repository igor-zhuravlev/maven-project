package com.epam.learn.rs.controller;

import com.epam.learn.rs.dto.DeleteResourceRequestDto;
import com.epam.learn.rs.dto.ResourceResponseDto;
import com.epam.learn.rs.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/resources")
@RequiredArgsConstructor
@Validated
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping(consumes = "audio/mpeg")
    public ResponseEntity<ResourceResponseDto> uploadResource(@RequestBody byte[] data) {
        return ResponseEntity.ok(resourceService.save(data));
    }

    @GetMapping(value = "/{id}", produces = "audio/mpeg")
    public ResponseEntity<byte[]> downloadResource(@PathVariable Integer id) {
        final byte[] data = resourceService.findById(id);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
            .contentLength(data.length)
            .body(data);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, List<Integer>>> deleteResources(@Valid @ModelAttribute DeleteResourceRequestDto dto) {
        List<Integer> ids = resourceService.deleteAllByIds(dto);
        return ResponseEntity.ok(Map.of("ids", ids));
    }

}
