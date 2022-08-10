package com.poliana.alura.challenges.budgetplannerapi.models;

import com.poliana.alura.challenges.budgetplannerapi.repository.MonthlySummaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class IncomeTest {

    @Mock
    MonthlySummaryRepository monthlySummaryRepository;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddSummaryToIncome() {
        Income income = new Income("Salary", new BigDecimal("1000"), LocalDate.of(2022, 5, 10));
        when(monthlySummaryRepository.findByYearAndMonth(Mockito.anyInt(), Mockito.anyInt())).thenReturn(Optional.empty());
        income.addSummary(monthlySummaryRepository);
        verify(monthlySummaryRepository, times(1)).save(Mockito.any());
        assertEquals(1, income.getMonthlySummary().getIncomes().size());
        assertEquals(income.getAmount(), income.getMonthlySummary().getCashBalance());
    }

    @Test
    void shouldSubtractIncomeAmountFromSummaryOnDelete() {
        MonthlySummary summary = new MonthlySummary(2022, 5);
        Income income1 = new Income("Salary", new BigDecimal("1000"), LocalDate.of(2022, 5, 10));
        Income income2 = new Income("Garage Sell", new BigDecimal("2000"), LocalDate.of(2022, 5, 15));
        summary.getIncomes().add(income1);
        summary.getIncomes().add(income2);
        summary.setCashBalance(income1.getAmount().add(income2.getAmount()));
        income1.setMonthlySummary(summary);
        income2.setMonthlySummary(summary);

        income1.updateSummaryOnDelete(monthlySummaryRepository);

        verify(monthlySummaryRepository, times(1)).save(Mockito.any());
        assertEquals(new BigDecimal("2000"), summary.getCashBalance());
    }
}