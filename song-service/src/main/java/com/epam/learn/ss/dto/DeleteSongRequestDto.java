package com.epam.learn.ss.dto;

import com.epam.learn.ss.validation.DeleteResource;

public record DeleteSongRequestDto(@DeleteResource String id) {
}
