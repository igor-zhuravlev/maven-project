package com.epam.learn.rp.service;

import com.epam.learn.rp.event.ResourceUploadedEvent;

public interface ResourceMetadataService {
    void handle(ResourceUploadedEvent event);
}
