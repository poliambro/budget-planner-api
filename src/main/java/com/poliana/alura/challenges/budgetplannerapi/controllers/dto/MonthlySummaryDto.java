package com.poliana.alura.challenges.budgetplannerapi.controllers.dto;

import com.poliana.alura.challenges.budgetplannerapi.models.ExpenseCategory;
import com.poliana.alura.challenges.budgetplannerapi.models.MonthlySummary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummaryDto {

    private int month;
    private int year;
    private List<IncomeDto> incomes;
    private List<ExpenseDto> expenses;
    private BigDecimal cashBalance;
    private HashMap<ExpenseCategory, BigDecimal> categoryExpenses;

    public MonthlySummaryDto(MonthlySummary monthlySummary) {
        month = monthlySummary.getMonth();
        year = monthlySummary.getYear();
        incomes = IncomeDto.convert(monthlySummary.getIncomes());
        expenses = ExpenseDto.convert(monthlySummary.getExpenses());
        cashBalance = monthlySummary.getCashBalance();
        categoryExpenses = monthlySummary.getCategoryExpenses();
    }

    public static Page<MonthlySummaryDto> convert(Page<MonthlySummary> summaryPage) {
        return summaryPage.map(MonthlySummaryDto::new);
    }
}
