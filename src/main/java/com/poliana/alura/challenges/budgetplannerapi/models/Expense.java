package com.poliana.alura.challenges.budgetplannerapi.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter @Setter @NoArgsConstructor
public class Expense {

    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
}
