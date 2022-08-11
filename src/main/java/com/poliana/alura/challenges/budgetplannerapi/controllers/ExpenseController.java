package com.poliana.alura.challenges.budgetplannerapi.controllers;

import com.poliana.alura.challenges.budgetplannerapi.controllers.dto.ExpenseDto;
import com.poliana.alura.challenges.budgetplannerapi.controllers.form.FormExpense;
import com.poliana.alura.challenges.budgetplannerapi.models.Expense;
import com.poliana.alura.challenges.budgetplannerapi.repository.ExpenseRepository;
import com.poliana.alura.challenges.budgetplannerapi.services.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private SummaryService summaryService;

    @PostMapping
    @Transactional
    public ResponseEntity<?> newExpense(@RequestBody @Valid FormExpense formExpense) {
        Expense expense = formExpense.convert(expenseRepository);
        if (expense != null) {
            summaryService.addExpenseToSummary(expense);
            expenseRepository.save(expense);
            return new ResponseEntity<>(new ExpenseDto(expense), HttpStatus.CREATED);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The expense already exists!");
    }

    @GetMapping
    public Page<ExpenseDto> listExpenses(@PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
                                         @RequestParam(required = false) String description){
        if(description == null || description.trim().isEmpty()){
            Page<Expense> expensePage = expenseRepository.findAll(pageable);
            return ExpenseDto.convert(expensePage);
        }
        Page<Expense> expenses = expenseRepository.findByDescription(pageable, description);
        return ExpenseDto.convert(expenses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> expenseDetails(@PathVariable Long id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        if(expense.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ExpenseDto(expense.get()));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @RequestBody @Valid FormExpense formExpense) {
        Optional<Expense> optional = expenseRepository.findById(id);
        if(optional.isPresent()) {
            Expense expense = formExpense.update(id, expenseRepository, summaryService);
            return ResponseEntity.ok(new ExpenseDto(expense));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        Optional<Expense> optional = expenseRepository.findById(id);
        if(optional.isPresent()) {
            expenseRepository.deleteById(id);
            summaryService.updateSummaryUponExpenseDeletion(optional.get());
            return ResponseEntity.ok("The expense was successfully removed!");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{year}/{month}")
    public Page<ExpenseDto> listExpensesByYearAndMonth(@PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
                                                     @PathVariable int year, @PathVariable int month){
        LocalDate minDate = LocalDate.of(year, month, 1);
        LocalDate maxDate = LocalDate.of(year, month, minDate.lengthOfMonth());
        Page<Expense> expenses = expenseRepository.findByYearAndMonth(pageable, minDate, maxDate);
        return ExpenseDto.convert(expenses);
    }
}
