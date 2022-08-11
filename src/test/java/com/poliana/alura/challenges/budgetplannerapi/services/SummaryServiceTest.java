package com.poliana.alura.challenges.budgetplannerapi.services;

import com.poliana.alura.challenges.budgetplannerapi.models.Expense;
import com.poliana.alura.challenges.budgetplannerapi.models.ExpenseCategory;
import com.poliana.alura.challenges.budgetplannerapi.models.Income;
import com.poliana.alura.challenges.budgetplannerapi.models.MonthlySummary;
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
class SummaryServiceTest {

    @Mock
    MonthlySummaryRepository repository;

    SummaryService service;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
        service = new SummaryService(repository);
    }

    @Test
    void shouldAddIncomeToSummary() {
        Income income = new Income("Salary", new BigDecimal("1200"), LocalDate.of(2022, 1, 5));
        when(repository.findByYearAndMonth(Mockito.anyInt(), Mockito.anyInt())).thenReturn(Optional.empty());
        service.addIncomeToSummary(income);
        assertNotNull(income.getMonthlySummary());
        assertEquals(1, income.getMonthlySummary().getIncomes().size());
    }

    @Test
    void shouldAddExpenseToSummary() {
        Expense expense = new Expense("Rent", new BigDecimal("1200"), LocalDate.of(2022, 1, 5));
        when(repository.findByYearAndMonth(Mockito.anyInt(), Mockito.anyInt())).thenReturn(Optional.empty());
        service.addExpenseToSummary(expense);
        assertNotNull(expense.getMonthlySummary());
        assertEquals(1, expense.getMonthlySummary().getExpenses().size());
    }

    @Test
    void shouldUpdateSummaryUponIncomeDeletion() {
        MonthlySummary summary = initMonthlySummary();
        Income income = new Income("Salary", new BigDecimal("1200"), LocalDate.of(2022, 1, 5));
        income.setMonthlySummary(summary);
        summary.getIncomes().add(income);
        service.updateCashBalance(summary);
        service.updateSummaryUponIncomeDeletion(income);
        assertEquals(new BigDecimal("2000"), summary.getCashBalance());
    }

    @Test
    void shouldUpdateSummaryUponExpenseDeletion() {
        MonthlySummary summary = initMonthlySummary();
        Expense expense = new Expense("Piano lessons", new BigDecimal("500"), LocalDate.of(2022, 1, 5), ExpenseCategory.EDUCATION);
        expense.setMonthlySummary(summary);
        summary.getExpenses().add(expense);
        service.updateCashBalance(summary);
        service.updateCategoryExpenses(summary);

        service.updateSummaryUponExpenseDeletion(expense);

        assertEquals(new BigDecimal("2000"), summary.getCashBalance());
        assertEquals(new BigDecimal("1000"), summary.getCategoryExpenses().get(ExpenseCategory.EDUCATION));
    }

    @Test
    void shouldUpdateSummaryCashBalance() {
        MonthlySummary summary = initMonthlySummary();
        BigDecimal result = service.updateCashBalance(summary);
        assertEquals(new BigDecimal("2000"), result);
    }

    @Test
    void shouldUpdateSummaryCategoryExpenses() {
        MonthlySummary summary = initMonthlySummary();
        Expense expense = new Expense("Piano lessons", new BigDecimal("500"), LocalDate.of(2022, 1, 5), ExpenseCategory.EDUCATION);
        expense.setMonthlySummary(summary);
        summary.getExpenses().add(expense);

        service.updateCategoryExpenses(summary);

        assertEquals(new BigDecimal("1500"), summary.getCategoryExpenses().get(ExpenseCategory.EDUCATION));
    }

    private MonthlySummary initMonthlySummary() {
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