package com.epam.learn.ss.controller;

import com.epam.learn.ss.dto.CreateStorageRequestDto;
import com.epam.learn.ss.dto.CreateStorageResponseDto;
import com.epam.learn.ss.dto.StorageResponseDto;
import com.epam.learn.ss.service.StorageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/storages")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping
    public ResponseEntity<CreateStorageResponseDto> createStorage(@Valid @RequestBody CreateStorageRequestDto request) {
        return ResponseEntity.ok(storageService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<StorageResponseDto>> getStorages() {
        return ResponseEntity.ok(storageService.findAll());
    }

    @DeleteMapping
    public ResponseEntity<List<Integer>> deleteStorages(@RequestParam("id")
                                                        @Pattern(regexp = "\\d+(,\\d+)*")
                                                        @Size(max = 200)
                                                        String ids) {
        return ResponseEntity.ok(storageService.deleteAllByIds(ids));
    }

}
