package com.epam.learn.rp.contract;

import com.epam.learn.rp.client.SongServiceClient;
import com.epam.learn.rp.config.ClientConfig;
import com.epam.learn.rp.dto.MetadataDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(
    classes = {
        ClientConfig.class,
        SongServiceClient.class
    },
    properties = {
        "app.services.song-service-url=http://localhost:${stubrunner.runningstubs.song-service.port}",
        "app.retry.max-attempts=5",
        "app.retry.initial-interval=1000",
        "app.retry.multiplier=2.0",
        "app.retry.max-interval=10000"
    },
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@AutoConfigureStubRunner(
    ids = "com.epam.learn:song-service:+:stubs",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
class SongServiceClientContractTest {

    @Autowired
    private SongServiceClient songServiceClient;

    @Test
    void shouldCreateSongMetadata() {
        final MetadataDto metadataDto = new MetadataDto(
            1, "Song", "Artist", "Album", "01:15", "2026");

        assertDoesNotThrow(() -> songServiceClient.createSong(metadataDto));
    }

}
