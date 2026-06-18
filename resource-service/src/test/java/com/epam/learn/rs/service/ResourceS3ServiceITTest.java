package com.epam.learn.rs.service;

import com.epam.learn.rs.config.S3ClientConfig;
import com.epam.learn.rs.entity.Resource;
import com.epam.learn.rs.entity.StorageType;
import com.epam.learn.rs.service.impl.ResourceS3ServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
    classes = {
        S3ClientConfig.class,
        ResourceS3ServiceImpl.class
    },
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@Testcontainers
class ResourceS3ServiceITTest {

    private static final String LOCALSTACK_IMAGE = "localstack/localstack:4";

    private static final String STAGING_BUCKET = "resources-staging";
    private static final String PERMANENT_BUCKET = "resources-permanent";
    private static final String PATH = "/files";

    @Container
    private static final LocalStackContainer LOCALSTACK = new LocalStackContainer(DockerImageName.parse(LOCALSTACK_IMAGE))
        .withCopyFileToContainer(
            MountableFile.forClasspathResource("localstack.sh"),
            "/etc/localstack/init/ready.d/localstack.sh"
        )
        .withServices(LocalStackContainer.Service.S3);

    @Autowired
    private ResourceS3Service resourceS3Service;

    @Autowired
    private S3Client s3Client;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.s3.endpoint", () ->
            LOCALSTACK.getEndpointOverride(LocalStackContainer.Service.S3).toString()
        );
        registry.add("app.s3.region", LOCALSTACK::getRegion);
        registry.add("app.s3.access-key", LOCALSTACK::getAccessKey);
        registry.add("app.s3.secret-key", LOCALSTACK::getSecretKey);
    }

    @Test
    void shouldUploadResourceToS3() {
        final byte[] data = "data".getBytes(StandardCharsets.UTF_8);

        String key = resourceS3Service.upload(data, STAGING_BUCKET, PATH);

        assertNotNull(key);
        assertTrue(key.startsWith(PATH + "/"));
        assertTrue(key.endsWith(".mp3"));

        byte[] actual = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(STAGING_BUCKET)
                .key(key)
                .build())
            .asByteArray();

        assertArrayEquals(data, actual);
    }

    @Test
    void shouldReturnResourceFromS3() {
        final String key = PATH + "/test.mp3";
        final byte[] data = "data".getBytes(StandardCharsets.UTF_8);

        putResource(STAGING_BUCKET, key, data);

        byte[] actual = resourceS3Service.download(STAGING_BUCKET, key);

        assertArrayEquals(data, actual);
    }

    @Test
    void shouldDeleteResourcesFromS3() {
        final String key1 = PATH + "/1.mp3";
        final String key2 = PATH + "/2.mp3";
        final byte[] data1 = "data".getBytes(StandardCharsets.UTF_8);
        final byte[] data2 = "data".getBytes(StandardCharsets.UTF_8);
        final List<Resource> resources = List.of(
            new Resource(1, StorageType.STAGING, STAGING_BUCKET, key1),
            new Resource(2, StorageType.PERMANENT, PERMANENT_BUCKET, key2)
        );

        putResource(STAGING_BUCKET, key1, data1);
        putResource(PERMANENT_BUCKET, key2, data2);

        resourceS3Service.delete(resources);

        assertThrows(NoSuchKeyException.class, () ->
            s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                    .bucket(STAGING_BUCKET)
                    .key(key1)
                    .build()
            )
        );

        assertThrows(NoSuchKeyException.class, () ->
            s3Client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(PERMANENT_BUCKET)
                .key(key2)
                .build())
        );
    }

    private void putResource(String bucket, String key, byte[] data) {
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("audio/mpeg")
                .build(),
            RequestBody.fromBytes(data)
        );
    }

}