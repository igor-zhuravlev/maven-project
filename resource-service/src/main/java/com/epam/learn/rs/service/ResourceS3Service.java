package com.epam.learn.rs.service;

import com.epam.learn.rs.entity.Resource;

import java.util.List;

public interface ResourceS3Service {
    String upload(byte[] data, String bucket, String path);
    byte[] download(String bucket, String key);
    void delete(List<Resource> resources);
    String move(Resource resource, String bucket, String path);
}
