package com.poliana.alura.challenges.budgetplannerapi.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name = "monthly_summary")
@Getter @Setter @NoArgsConstructor
public class MonthlySummary {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int month;
    private int year;
    @OneToMany(mappedBy = "monthlySummary")
    private List<Income> incomes = new ArrayList<>();
    @OneToMany(mappedBy = "monthlySummary")
    private List<Expense> expenses = new ArrayList<>();
    private BigDecimal cashBalance = BigDecimal.ZERO;
    @MapKeyEnumerated(EnumType.STRING)
    private HashMap<ExpenseCategory, BigDecimal> categoryExpenses = new HashMap<>();

    public MonthlySummary(int year, int month) {
        this.year = year;
        this.month = month;
    }
}
