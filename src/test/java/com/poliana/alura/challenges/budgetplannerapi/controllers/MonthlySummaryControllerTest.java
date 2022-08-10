package com.poliana.alura.challenges.budgetplannerapi.controllers;

import com.poliana.alura.challenges.budgetplannerapi.models.MonthlySummary;
import com.poliana.alura.challenges.budgetplannerapi.repository.MonthlySummaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = MonthlySummaryController.class)
@AutoConfigureMockMvc
class MonthlySummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MonthlySummaryRepository repository;

    @Test
    void shouldReturnOkForExistingSummary() throws Exception {
        MonthlySummary summary = new MonthlySummary(2022, 8);
        when(repository.findByYearAndMonth(Mockito.anyInt(), Mockito.anyInt())).thenReturn(Optional.of(summary));
        mockMvc.perform(MockMvcRequestBuilders.get("/summary/{year}/{month}", 2022, 8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenTheSummaryDoesNotExist() throws Exception {
        when(repository.findByYearAndMonth(Mockito.anyInt(), Mockito.anyInt())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/summary/{year}/{month}", 2022, 8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}