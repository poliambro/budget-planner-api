package com.poliana.alura.challenges.budgetplannerapi.controllers.form;

import com.poliana.alura.challenges.budgetplannerapi.models.Expense;
import com.poliana.alura.challenges.budgetplannerapi.models.ExpenseCategory;
import com.poliana.alura.challenges.budgetplannerapi.repository.ExpenseRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FormExpense {

    @NotNull @NotEmpty
    private String description;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private LocalDate date;
    private ExpenseCategory category;

    public Expense convert(ExpenseRepository repository){
        Optional<Expense> byDescriptionAndDate = repository.findByDescriptionAndDate(description, date);
        if(byDescriptionAndDate.isEmpty()){
            if(category == null)
                return new Expense(description, amount, date);
            return new Expense(description, amount, date, category);
        }
        return null;
    }

    public Expense update(Long id, ExpenseRepository repository) {
        Expense expense = repository.getReferenceById(id);
        expense.setDescription(this.description);
        expense.setAmount(this.amount);
        expense.setDate(this.date);
        if(category == null)
            expense.setCategory(ExpenseCategory.MISCELLANEOUS);
        else
            expense.setCategory(this.category);
        return expense;
    }
}
