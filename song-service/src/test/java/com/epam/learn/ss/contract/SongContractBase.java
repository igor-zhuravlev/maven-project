package com.epam.learn.ss.contract;

import com.epam.learn.ss.controller.SongController;
import com.epam.learn.ss.dto.SongDto;
import com.epam.learn.ss.service.SongService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(SongController.class)
public abstract class SongContractBase {

    @MockitoBean
    private SongService songService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        final SongDto response = SongDto.builder()
            .id(1)
            .build();

        when(songService.save(any(SongDto.class))).thenReturn(response);

        RestAssuredMockMvc.mockMvc(mockMvc);
    }

}
