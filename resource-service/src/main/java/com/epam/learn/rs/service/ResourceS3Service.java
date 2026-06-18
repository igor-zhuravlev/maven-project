package com.epam.learn.rs.service;

import com.epam.learn.rs.entity.Resource;

import java.util.List;

public interface ResourceS3Service {
    String upload(byte[] data);
    byte[] download(String key);
    void delete(List<Resource> resources);
}
