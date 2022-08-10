package com.poliana.alura.challenges.budgetplannerapi.models;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

@SpringBootTest
class MonthlySummaryTest {


    @Test
    void shouldUpdateSummaryCashBalance() {
        MonthlySummary summary = initSummary();
        BigDecimal result = summary.updateCashBalance();
        assertEquals(new BigDecimal("2000"), result);
        Expense expense = new Expense("Party", new BigDecimal("200"), LocalDate.of(2022, 1, 20), ExpenseCategory.RECREATION_AND_ENTERTAINMENT);
        expense.setMonthlySummary(summary);
        summary.getExpenses().add(expense);
        BigDecimal newBalance = summary.updateCashBalance();
        assertEquals(new BigDecimal("1800"), newBalance);
    }

    @Test
    void shouldUpdateSummaryCategoryExpenses() {
        MonthlySummary summary = initSummary();
        summary.updateCategoryExpenses();
        assertEquals(new BigDecimal("1000"), summary.getCategoryExpenses().get(ExpenseCategory.EDUCATION));
        Expense expense = new Expense("Piano lessons", new BigDecimal("200"), LocalDate.of(2022, 1, 20), ExpenseCategory.EDUCATION);
        expense.setMonthlySummary(summary);
        summary.getExpenses().add(expense);
        summary.updateCategoryExpenses();
        assertEquals(new BigDecimal("1200"), summary.getCategoryExpenses().get(ExpenseCategory.EDUCATION));
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