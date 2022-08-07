package com.poliana.alura.challenges.budgetplannerapi.controllers.dto;

import com.poliana.alura.challenges.budgetplannerapi.models.Income;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class IncomeDto {

    private String description;
    private BigDecimal amount;
    private LocalDate date;

    public IncomeDto(Income income) {
        this.description = income.getDescription();
        this.amount = income.getAmount();
        this.date = income.getDate();
    }
}
