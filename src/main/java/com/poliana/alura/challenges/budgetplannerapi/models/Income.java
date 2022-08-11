package com.poliana.alura.challenges.budgetplannerapi.models;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "incomes")
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode
public class Income {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    @ManyToOne
    private MonthlySummary monthlySummary;

    public Income(String description, BigDecimal amount, LocalDate date) {
        this.description = description;
        this.amount = amount;
        this.date = date;
    }
}
