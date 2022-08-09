package com.poliana.alura.challenges.budgetplannerapi.models;

import com.poliana.alura.challenges.budgetplannerapi.repository.MonthlySummaryRepository;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

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

    public void addSummary(MonthlySummaryRepository monthlySummaryRepository) {
        Optional<MonthlySummary> summary = monthlySummaryRepository.findByYearAndMonth(date.getYear(), date.getMonthValue());
        if(summary.isPresent()){
            updateAndSaveSummary(monthlySummaryRepository, summary.get());
        } else {
            MonthlySummary monthlySummary = new MonthlySummary(date.getYear(), date.getMonthValue());
            updateAndSaveSummary(monthlySummaryRepository, monthlySummary);
        }
    }

    private void updateAndSaveSummary(MonthlySummaryRepository repository, MonthlySummary summary) {
        summary.getIncomes().add(this);
        summary.updateCashBalance();
        repository.save(summary);
        setMonthlySummary(summary);
    }

    public void updateSummaryOnDelete(MonthlySummaryRepository repository) {
        monthlySummary.setCashBalance(monthlySummary.getCashBalance().subtract(amount));
        repository.save(monthlySummary);
    }
}
