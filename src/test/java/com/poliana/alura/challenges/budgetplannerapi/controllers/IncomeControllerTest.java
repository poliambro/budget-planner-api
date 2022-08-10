package com.poliana.alura.challenges.budgetplannerapi.controllers;

import com.poliana.alura.challenges.budgetplannerapi.models.Income;
import com.poliana.alura.challenges.budgetplannerapi.repository.IncomeRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = IncomeController.class)
@AutoConfigureMockMvc
class IncomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    IncomeRepository incomeRepository;

    @MockBean
    MonthlySummaryRepository summaryRepository;

    @Test
    void shouldAddNewIncome() throws Exception {
        String json = "{\"description\":\"salary\",\"amount\":5000,\"date\":\"2022-08-05\"}";
        mockMvc.perform(MockMvcRequestBuilders
                    .post("/incomes")
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201));
    }

    @Test
    void shouldReturnBadRequestForDuplicatedIncome() throws Exception {
        String json = "{\"description\":\"salary\",\"amount\":5000,\"date\":\"2022-08-10\"}";
        Income income = new Income("salary", new BigDecimal("5000"), LocalDate.of(2022, 8, 10));
        when(incomeRepository.findByDescriptionAndDate(Mockito.any(), Mockito.any())).thenReturn(Optional.of(income));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/incomes")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }
}