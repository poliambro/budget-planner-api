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
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ExpenseTest {


    @Mock
    MonthlySummaryRepository monthlySummaryRepository;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddTheSummaryToExpense() {
        Expense expense = new Expense("Emergency", new BigDecimal("100"), LocalDate.of(2022, 1, 10), ExpenseCategory.MEDICAL_CARE);
        when(monthlySummaryRepository.findByYearAndMonth(Mockito.anyInt(), Mockito.anyInt())).thenReturn(Optional.empty());
        assertNull(expense.getMonthlySummary());
        expense.addExpense(monthlySummaryRepository);
        assertNotNull(expense.getMonthlySummary());
    }

    @Test
    void shouldUpdateTheSummaryUponTheDeletionOfTheExpense() {
        Expense expense = new Expense("Home school teacher", new BigDecimal("1000"), LocalDate.of(2022, 1, 10), ExpenseCategory.EDUCATION);
        MonthlySummary summary = initSummary();
        expense.setMonthlySummary(summary);
        summary.getExpenses().add(expense);
        summary.updateCashBalance();
        summary.updateCategoryExpenses();

        assertEquals(new BigDecimal("1000"), summary.getCashBalance());
        assertEquals(new BigDecimal("2000"), summary.getCategoryExpenses().get(ExpenseCategory.EDUCATION));

        expense.updateSummaryOnDelete(monthlySummaryRepository);

        assertEquals(new BigDecimal("2000"), summary.getCashBalance());
        assertEquals(new BigDecimal("1000"), summary.getCategoryExpenses().get(ExpenseCategory.EDUCATION));
    }

    private MonthlySummary initSummary() {
        MonthlySummary summary = new MonthlySummary(2022, 1);

        Income income1 = new Income("Salary", new BigDecimal("5000"), LocalDate.of(2022, 1, 5));
        income1.setMonthlySummary(summary);
        summary.getIncomes().add(income1);

        Expense expense1 = new Expense("Rent", new BigDecimal("1500"), LocalDate.of(2022, 1, 10), ExpenseCategory.HOUSING);
        expense1.setMonthlySummary(summary);
        Expense expense2 = new Expense("Groceries", new BigDecimal("500"), LocalDate.of(2022, 1, 5), ExpenseCategory.FOOD);
        expense2.setMonthlySummary(summary);
        Expense expense3 = new Expense("Kid's school", new BigDecimal("1000"), LocalDate.of(2022, 1, 10), ExpenseCategory.EDUCATION);
        expense3.setMonthlySummary(summary);
        summary.getExpenses().addAll(Arrays.asList(expense1, expense2, expense3));

        return summary;
    }
}