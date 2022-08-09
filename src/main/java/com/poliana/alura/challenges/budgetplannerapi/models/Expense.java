package com.poliana.alura.challenges.budgetplannerapi.models;

import com.poliana.alura.challenges.budgetplannerapi.repository.MonthlySummaryRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

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
    @ManyToOne
    private MonthlySummary monthlySummary;

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

    public void addExpense(MonthlySummaryRepository repository) {
        Optional<MonthlySummary> summary = repository.findByMonth(date.getMonthValue());
        if(summary.isPresent()){
            updateAndSaveSummary(repository, summary.get());
        } else {
            MonthlySummary monthlySummary = new MonthlySummary(date.getMonthValue());
            updateAndSaveSummary(repository, monthlySummary);
        }
    }
    private void updateAndSaveSummary(MonthlySummaryRepository repository, MonthlySummary summary) {
        summary.getExpenses().add(this);
        summary.updateCashBalance();
        summary.updateCategoryExpenses();
        repository.save(summary);
        setMonthlySummary(summary);
    }

    public void updateSummaryOnDelete(MonthlySummaryRepository repository) {
        monthlySummary.setCashBalance(monthlySummary.getCashBalance().add(amount));
        BigDecimal currentTotalExpense = monthlySummary.getCategoryExpenses().get(category);
        monthlySummary.getCategoryExpenses().put(category, currentTotalExpense.subtract(amount));
        repository.save(monthlySummary);
    }
}
