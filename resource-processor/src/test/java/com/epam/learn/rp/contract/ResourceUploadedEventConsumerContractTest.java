package com.epam.learn.rp.contract;

import com.epam.learn.rp.consumer.ResourceUploadedEventConsumer;
import com.epam.learn.rp.event.ResourceUploadedEvent;
import com.epam.learn.rp.service.ResourceMetadataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(
    classes = {
        ResourceUploadedEventConsumerContractTest.Config.class,
        ResourceUploadedEventConsumer.class
    },
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@AutoConfigureStubRunner(
    ids = "com.epam.learn:resource-service:+:stubs",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
class ResourceUploadedEventConsumerContractTest {

    @Autowired
    private StubTrigger stubTrigger;

    @MockitoBean
    private ResourceMetadataService resourceMetadataService;

    @Test
    void shouldConsumeResourceUploadedEvent() {
        stubTrigger.trigger("resource_uploaded_event");

        verify(resourceMetadataService, timeout(2000))
            .handle(new ResourceUploadedEvent(1));
    }

    @TestConfiguration
    static class Config {

        @Bean(name = "resources.exchange")
        MessageChannel resourcesExchange(ResourceUploadedEventConsumer consumer) {
            DirectChannel channel = new DirectChannel();
            ObjectMapper objectMapper = new ObjectMapper();

            channel.subscribe(message -> {
                try {
                    ResourceUploadedEvent event = objectMapper.readValue(
                        message.getPayload().toString(),
                        ResourceUploadedEvent.class
                    );
                    consumer.accept(event);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            return channel;
        }

    }

}
