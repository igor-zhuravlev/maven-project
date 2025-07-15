package com.epam.learn.rs.dto;

import com.epam.learn.rs.validation.DeleteResource;

public record DeleteResourceRequestDto(@DeleteResource String id) {
}
