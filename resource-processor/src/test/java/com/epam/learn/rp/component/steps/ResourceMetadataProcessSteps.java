package com.epam.learn.rp.component.steps;

import com.epam.learn.rp.client.ResourceServiceClient;
import com.epam.learn.rp.client.SongServiceClient;
import com.epam.learn.rp.dto.MetadataDto;
import com.epam.learn.rp.event.ResourceUploadedEvent;
import com.epam.learn.rp.mapper.ResourceMetadataMapper;
import com.epam.learn.rp.service.ResourceMetadataService;
import com.epam.learn.rp.service.impl.ResourceMetadataServiceImpl;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@CucumberContextConfiguration
@SpringBootTest(classes = {
    ResourceMetadataServiceImpl.class,
    ResourceMetadataMapper.class
})
public class ResourceMetadataProcessSteps {

    @Autowired
    private ResourceMetadataService resourceMetadataService;

    @MockitoBean
    private ResourceServiceClient resourceServiceClient;

    @MockitoBean
    private SongServiceClient songServiceClient;

    private byte[] mp3File;

    @Given("resource service contains uploaded mp3 resource with id {int}")
    public void resourceServiceContainsUploadedMp3ResourceWithId(Integer id) throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("audio/sample.mp3")) {
            assertNotNull(inputStream);
            mp3File = inputStream.readAllBytes();
        }

        when(resourceServiceClient.downloadResource(id)).thenReturn(mp3File);
    }

    @When("resource uploaded event with id {int} is received")
    public void resourceUploadedEventWithIdIsReceived(Integer id) {
        resourceMetadataService.handle(new ResourceUploadedEvent(id));
    }

    @Then("resource processor should request resource with id {int} from resource service")
    public void resourceProcessorShouldRequestResourceWithIdFromResourceService(Integer id) {
        verify(resourceServiceClient).downloadResource(id);
    }

    @Then("song metadata should be created in song service")
    public void songMetadataShouldBeCreatedInSongService() {
        ArgumentCaptor<MetadataDto> captor = ArgumentCaptor.forClass(MetadataDto.class);

        verify(songServiceClient).createSong(captor.capture());

        MetadataDto metadataDto = captor.getValue();

        assertNotNull(metadataDto);
        assertNotNull(metadataDto.name());
        assertNotNull(metadataDto.artist());
        assertNotNull(metadataDto.album());
        assertNotNull(metadataDto.duration());
        assertNotNull(metadataDto.year());
    }

}
