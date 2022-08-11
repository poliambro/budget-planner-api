package com.poliana.alura.challenges.budgetplannerapi.services;

import com.poliana.alura.challenges.budgetplannerapi.models.Expense;
import com.poliana.alura.challenges.budgetplannerapi.models.ExpenseCategory;
import com.poliana.alura.challenges.budgetplannerapi.models.Income;
import com.poliana.alura.challenges.budgetplannerapi.models.MonthlySummary;
import com.poliana.alura.challenges.budgetplannerapi.repository.MonthlySummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class SummaryService {

    private final MonthlySummaryRepository monthlySummaryRepository;

    @Autowired
    public SummaryService(MonthlySummaryRepository monthlySummaryRepository) {
        this.monthlySummaryRepository = monthlySummaryRepository;
    }

    public void addIncomeToSummary(Income income) {
        Optional<MonthlySummary> summary = monthlySummaryRepository.findByYearAndMonth(income.getDate().getYear(), income.getDate().getMonthValue());
        if(summary.isPresent()){
            updateAndSaveSummaryWithIncome(income, summary.get());
        } else {
            MonthlySummary monthlySummary = new MonthlySummary(income.getDate().getYear(), income.getDate().getMonthValue());
            updateAndSaveSummaryWithIncome(income, monthlySummary);
        }
    }

    private void updateAndSaveSummaryWithIncome(Income income, MonthlySummary summary) {
        summary.getIncomes().add(income);
        updateCashBalance(summary);
        monthlySummaryRepository.save(summary);
        income.setMonthlySummary(summary);
    }

    public void addExpenseToSummary(Expense expense) {
        Optional<MonthlySummary> summary = monthlySummaryRepository.findByYearAndMonth(expense.getDate().getYear(), expense.getDate().getMonthValue());
        if(summary.isPresent()){
            updateAndSaveSummaryWithExpense(expense, summary.get());
        } else {
            MonthlySummary monthlySummary = new MonthlySummary(expense.getDate().getYear(), expense.getDate().getMonthValue());
            updateAndSaveSummaryWithExpense(expense, monthlySummary);
        }
    }

    private void updateAndSaveSummaryWithExpense(Expense expense, MonthlySummary summary) {
        summary.getExpenses().add(expense);
        updateCashBalance(summary);
        updateCategoryExpenses(summary);
        monthlySummaryRepository.save(summary);
        expense.setMonthlySummary(summary);
    }

    public void updateSummaryUponIncomeDeletion(Income income) {
        income.getMonthlySummary().setCashBalance(income.getMonthlySummary().getCashBalance().subtract(income.getAmount()));
        monthlySummaryRepository.save(income.getMonthlySummary());
    }

    public void updateSummaryUponExpenseDeletion(Expense expense) {
        expense.getMonthlySummary().setCashBalance(expense.getMonthlySummary().getCashBalance().add(expense.getAmount()));
        BigDecimal currentTotalExpense = expense.getMonthlySummary().getCategoryExpenses().get(expense.getCategory());
        expense.getMonthlySummary().getCategoryExpenses().put(expense.getCategory(), currentTotalExpense.subtract(expense.getAmount()));
        monthlySummaryRepository.save(expense.getMonthlySummary());
    }

    public BigDecimal updateCashBalance(MonthlySummary summary) {
        BigDecimal totalIncomes = summary.getIncomes().stream().map(Income::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpenses = summary.getExpenses().stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setCashBalance(totalIncomes.subtract(totalExpenses));
        return summary.getCashBalance();
    }

    public void updateCategoryExpenses(MonthlySummary summary) {
        for(ExpenseCategory category : ExpenseCategory.values()) {
            List<Expense> filteredExpenses = summary.getExpenses().stream().filter(e -> e.getCategory().equals(category)).toList();
            BigDecimal totalExpense = filteredExpenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            summary.getCategoryExpenses().put(category, totalExpense);
        }
    }
}
