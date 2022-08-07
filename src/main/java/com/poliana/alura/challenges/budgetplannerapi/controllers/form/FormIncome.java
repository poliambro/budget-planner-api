package com.poliana.alura.challenges.budgetplannerapi.controllers.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.poliana.alura.challenges.budgetplannerapi.models.Income;
import com.poliana.alura.challenges.budgetplannerapi.repository.IncomeRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FormIncome {

    @NotNull @NotEmpty
    private String description;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private LocalDate date;

    public Income convert(IncomeRepository repository){
        Optional<Income> byDescriptionAndDate = repository.findByDescriptionAndDate(description, date);
        if(byDescriptionAndDate.isEmpty())
            return new Income(description, amount, date);
        return null;
    }

    public Income update(Long id, IncomeRepository repository) {
        Income income = repository.getReferenceById(id);
        income.setDescription(this.description);
        income.setAmount(this.amount);
        income.setDate(this.date);
        return income;
    }
}
