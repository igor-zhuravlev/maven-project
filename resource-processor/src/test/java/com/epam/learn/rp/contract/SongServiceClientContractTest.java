package com.epam.learn.rp.contract;

import com.epam.learn.rp.client.SongServiceClient;
import com.epam.learn.rp.config.GatewayClientConfig;
import com.epam.learn.rp.dto.MetadataDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(
    classes = {
        GatewayClientConfig.class,
        SongServiceClient.class
    },
    properties = {
        "GATEWAY_SERVER_URL=http://localhost:${stubrunner.runningstubs.song-service.port}",
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
