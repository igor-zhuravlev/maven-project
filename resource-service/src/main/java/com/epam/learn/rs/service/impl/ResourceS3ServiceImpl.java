package com.epam.learn.rs.service.impl;

import com.epam.learn.rs.entity.Resource;
import com.epam.learn.rs.exception.ResourceS3ServiceException;
import com.epam.learn.rs.service.ResourceS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceS3ServiceImpl implements ResourceS3Service {

    private static final String S3_KEY_TEMPLATE = "resources/%s.mp3";

    private final S3Client s3Client;

    @Value("${app.s3.bucket}")
    private String bucket;

    @Override
    public String upload(byte[] data) {
        String key = S3_KEY_TEMPLATE.formatted(UUID.randomUUID());

        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType("audio/mpeg")
            .build();
        PutObjectResponse response = s3Client.putObject(request, RequestBody.fromBytes(data));

        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new ResourceS3ServiceException(
                "Failed to upload resource to S3. Status code: " + response.sdkHttpResponse().statusCode());
        }

        log.info("Uploaded resource to S3 :: bucket: {}, key: {}", bucket, key);

        return key;
    }

    @Override
    public byte[] download(String key) {
        byte[] data = s3Client.getObjectAsBytes(
            GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()
        ).asByteArray();

        log.info("Downloaded resource from S3 :: bucket: {}, key: {}", bucket, key);

        return data;
    }

    @Override
    public void delete(List<Resource> resources) {
        List<ObjectIdentifier> s3Keys = resources.stream()
            .map(resource -> ObjectIdentifier.builder()
                .key(resource.getS3Key())
                .build())
            .toList();

        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
            .bucket(bucket)
            .delete(Delete.builder().objects(s3Keys).build())
            .build();
        DeleteObjectsResponse response = s3Client.deleteObjects(request);

        if (response.hasErrors()) {
            log.error("Failed to delete some S3 objects :: bucket: {}, errors: {}", bucket, response.errors());
            throw new ResourceS3ServiceException("Failed to delete S3 objects: " + response.errors());
        }

        List<String> deletedKeys = s3Keys.stream()
            .map(ObjectIdentifier::key)
            .toList();

        log.info("Deleted resources from S3 :: bucket: {}, keys: {}", bucket, deletedKeys);
    }

}
