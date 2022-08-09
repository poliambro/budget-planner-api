package com.poliana.alura.challenges.budgetplannerapi.models;

import com.poliana.alura.challenges.budgetplannerapi.repository.MonthlySummaryRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "monthly_summary")
@Getter @Setter @NoArgsConstructor
public class MonthlySummary {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int month;
    @OneToMany(mappedBy = "monthlySummary")
    private List<Income> incomes = new ArrayList<>();
    @OneToMany(mappedBy = "monthlySummary")
    private List<Expense> expenses = new ArrayList<>();
    private BigDecimal cashBalance = BigDecimal.ZERO;
    @MapKeyEnumerated(EnumType.STRING)
    private HashMap<ExpenseCategory, BigDecimal> categoryExpenses = new HashMap<>();

    public MonthlySummary(int month) {
        this.month = month;
    }

    public BigDecimal updateCashBalance() {
        BigDecimal totalIncomes = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        for(Income income: incomes){
            totalIncomes = totalIncomes.add(income.getAmount());
        }
        for(Expense expense: expenses){
            totalExpenses = totalExpenses.add(expense.getAmount());
        }
        setCashBalance(totalIncomes.subtract(totalExpenses));
        return getCashBalance();
    }
}
