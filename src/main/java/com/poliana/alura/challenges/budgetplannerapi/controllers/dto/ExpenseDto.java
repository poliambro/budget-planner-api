package com.poliana.alura.challenges.budgetplannerapi.controllers.dto;

import com.poliana.alura.challenges.budgetplannerapi.models.Expense;
import com.poliana.alura.challenges.budgetplannerapi.models.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ExpenseDto {

    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private ExpenseCategory category;

    public ExpenseDto(Expense expense) {
        this.description = expense.getDescription();
        this.amount = expense.getAmount();
        this.date = expense.getDate();
        this.category = expense.getCategory();
    }

    public static Page<ExpenseDto> convert(Page<Expense> expensePage) {
        return expensePage.map(ExpenseDto::new);
    }
}
