package com.epam.learn.rs.service.impl;

import com.epam.learn.rs.entity.Resource;
import com.epam.learn.rs.exception.ResourceS3ServiceException;
import com.epam.learn.rs.service.ResourceS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceS3ServiceImpl implements ResourceS3Service {

    private static final String S3_KEY_TEMPLATE = "%s/%s.mp3";
    private static final String TARGET_S3_KEY_TEMPLATE = "%s/%s";

    private final S3Client s3Client;

    @Override
    public String upload(byte[] data, String bucket, String path) {
        String key = S3_KEY_TEMPLATE.formatted(path, UUID.randomUUID());

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
    public byte[] download(String bucket, String key) {
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
    public void delete(final List<Resource> resources) {
        resources.stream()
            .collect(Collectors.groupingBy(Resource::getBucket))
            .forEach((bucket, bucketResources) -> {
                List<ObjectIdentifier> objects = bucketResources.stream()
                    .map(resource -> ObjectIdentifier.builder()
                        .key(resource.getS3Key())
                        .build())
                    .toList();

                DeleteObjectsResponse response = s3Client.deleteObjects(builder -> builder
                    .bucket(bucket)
                    .delete(delete -> delete.objects(objects))
                );

                if (response.hasErrors()) {
                    throw new ResourceS3ServiceException("Failed to delete S3 objects: " + response.errors());
                }
            });
    }

    @Override
    public String move(final Resource resource, final String targetBucket, final String targetPath) {
        final String sourceBucket = resource.getBucket();
        final String sourceKey = resource.getS3Key();

        final String fileName = sourceKey.substring(sourceKey.lastIndexOf('/') + 1);
        final String targetKey = TARGET_S3_KEY_TEMPLATE.formatted(targetPath, fileName);

        s3Client.copyObject(builder -> builder
            .sourceBucket(sourceBucket)
            .sourceKey(sourceKey)
            .destinationBucket(targetBucket)
            .destinationKey(targetKey)
        );

        s3Client.deleteObject(builder -> builder
            .bucket(sourceBucket)
            .key(sourceKey)
        );

        log.info("Moved resource in S3 :: source: {}/{}, target: {}/{}",
            sourceBucket, sourceKey,
            targetBucket, targetKey
        );

        return targetKey;
    }

}
