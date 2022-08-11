package com.poliana.alura.challenges.budgetplannerapi.controllers;

import com.poliana.alura.challenges.budgetplannerapi.models.Income;
import com.poliana.alura.challenges.budgetplannerapi.models.MonthlySummary;
import com.poliana.alura.challenges.budgetplannerapi.repository.IncomeRepository;
import com.poliana.alura.challenges.budgetplannerapi.services.SummaryService;
import org.json.JSONObject;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
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
    SummaryService summaryService;

    @Test
    void shouldAddNewIncome() throws Exception {
        String json = "{\"description\":\"salary\",\"amount\":5000,\"date\":\"2022-08-05\"}";
        mockMvc.perform(MockMvcRequestBuilders
                    .post("/incomes")
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
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
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnOkForDetailingExistingIncome() throws Exception {
        Income income = new Income("salary", new BigDecimal("5000"), LocalDate.of(2022, 8, 10));
        when(incomeRepository.findById(1L)).thenReturn(Optional.of(income));
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/incomes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(response);
        assertEquals(income.getDescription(), jsonObject.get("description"));
        assertEquals(income.getAmount().toString(), jsonObject.getString("amount"));
        assertEquals(income.getDate().toString(), jsonObject.getString("date"));
    }

    @Test
    void shouldReturnNotFoundWhenTheIncomeDoesNotExist() throws Exception{
        when(incomeRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/incomes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOkWhenUpdatingExistingIncome() throws Exception {
        Income income = new Income("salary", new BigDecimal("5000"), LocalDate.of(2022, 8, 10));
        income.setMonthlySummary(new MonthlySummary(2022, 8));
        String json = "{\"description\":\"New job\",\"amount\":2000,\"date\":\"2022-08-15\"}";
        when(incomeRepository.findById(Mockito.any())).thenReturn(Optional.of(income));
        when(incomeRepository.getReferenceById(Mockito.any())).thenReturn(income);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .put("/incomes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(response);
        assertEquals("New job", jsonObject.get("description"));
        assertEquals("2000", jsonObject.getString("amount"));
        assertEquals("2022-08-15", jsonObject.getString("date"));
    }

    @Test
    void shouldReturnNotFoundWhenTheIncomeDoesNotExistUponUpdate() throws Exception {
        String json = "{\"description\":\"New job\",\"amount\":2000,\"date\":\"2022-08-15\"}";
        when(incomeRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.put("/incomes/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOkWhenDeletingAnExistingIncome() throws Exception {
        Income income = new Income("salary", new BigDecimal("5000"), LocalDate.of(2022, 8, 10));
        income.setMonthlySummary(new MonthlySummary(2022, 8));
        when(incomeRepository.findById(Mockito.any())).thenReturn(Optional.of(income));
        mockMvc.perform(MockMvcRequestBuilders.delete("/incomes/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(incomeRepository, times(1)).deleteById(Mockito.any());
    }

    @Test
    void shouldReturnNotFoundWhenTheIncomeDoesNotExistUponDeletion() throws Exception {
        when(incomeRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.delete("/incomes/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}