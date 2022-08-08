package com.poliana.alura.challenges.budgetplannerapi.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter @Setter @NoArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private ExpenseCategory category = ExpenseCategory.MISCELLANEOUS;

    public Expense(String description, BigDecimal amount, LocalDate date) {
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public Expense(String description, BigDecimal amount, LocalDate date, ExpenseCategory category) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }
}
