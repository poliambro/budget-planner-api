package com.poliana.alura.challenges.budgetplannerapi.controllers;

import com.poliana.alura.challenges.budgetplannerapi.models.Expense;
import com.poliana.alura.challenges.budgetplannerapi.models.ExpenseCategory;
import com.poliana.alura.challenges.budgetplannerapi.models.MonthlySummary;
import com.poliana.alura.challenges.budgetplannerapi.repository.ExpenseRepository;
import com.poliana.alura.challenges.budgetplannerapi.repository.MonthlySummaryRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ExpenseController.class)
@AutoConfigureMockMvc
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ExpenseRepository expenseRepository;

    @MockBean
    MonthlySummaryRepository summaryRepository;

    @Test
    void shouldAddNewExpense() throws Exception {
        String json = "{\"description\":\"emergency\",\"amount\":5000,\"date\":\"2022-08-05\"}";
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/expenses")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequestForDuplicatedExpense() throws Exception {
        String json = "{\"description\":\"emergency\",\"amount\":5000,\"date\":\"2022-08-10\"}";
        Expense expense = new Expense("emergency", new BigDecimal("5000"), LocalDate.of(2022, 8, 10));
        when(expenseRepository.findByDescriptionAndDate(Mockito.any(), Mockito.any())).thenReturn(Optional.of(expense));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/expenses")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnOkForDetailingExistingExpense() throws Exception {
        Expense expense = new Expense("emergency", new BigDecimal("5000"), LocalDate.of(2022, 8, 10));
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/expenses/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(response);
        assertEquals(expense.getDescription(), jsonObject.get("description"));
        assertEquals(expense.getAmount().toString(), jsonObject.getString("amount"));
        assertEquals(expense.getDate().toString(), jsonObject.getString("date"));
    }

    @Test
    void shouldReturnNotFoundWhenTheExpenseDoesNotExist() throws Exception{
        when(expenseRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/expenses/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOkWhenUpdatingExistingExpense() throws Exception {
        Expense expense = new Expense("emergency", new BigDecimal("5000"), LocalDate.of(2022, 8, 10));
        expense.setMonthlySummary(new MonthlySummary(2022, 8));
        String json = "{\"description\":\"New job\",\"amount\":2000,\"date\":\"2022-08-15\"}";
        when(expenseRepository.findById(Mockito.any())).thenReturn(Optional.of(expense));
        when(expenseRepository.getReferenceById(Mockito.any())).thenReturn(expense);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .put("/expenses/{id}", 1L)
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
    void shouldReturnNotFoundWhenTheExpenseDoesNotExistUponUpdate() throws Exception {
        String json = "{\"description\":\"New job\",\"amount\":2000,\"date\":\"2022-08-15\"}";
        when(expenseRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.put("/expenses/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOkWhenDeletingAnExistingExpense() throws Exception {
        Expense expense = new Expense("emergency", new BigDecimal("5000"), LocalDate.of(2022, 8, 10), ExpenseCategory.MISCELLANEOUS);
        MonthlySummary summary = new MonthlySummary(2022, 8);
        summary.getCategoryExpenses().put(ExpenseCategory.MISCELLANEOUS, BigDecimal.ZERO);
        expense.setMonthlySummary(summary);
        when(expenseRepository.findById(Mockito.any())).thenReturn(Optional.of(expense));
        mockMvc.perform(MockMvcRequestBuilders.delete("/expenses/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(expenseRepository, times(1)).deleteById(Mockito.any());
        verify(summaryRepository, times(1)).save(Mockito.any());
    }

    @Test
    void shouldReturnNotFoundWhenTheExpenseDoesNotExistUponDeletion() throws Exception {
        when(expenseRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.delete("/expenses/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}