package com.poliana.alura.challenges.budgetplannerapi.controllers;

import com.poliana.alura.challenges.budgetplannerapi.controllers.dto.IncomeDto;
import com.poliana.alura.challenges.budgetplannerapi.controllers.form.FormIncome;
import com.poliana.alura.challenges.budgetplannerapi.models.Income;
import com.poliana.alura.challenges.budgetplannerapi.models.MonthlySummary;
import com.poliana.alura.challenges.budgetplannerapi.repository.IncomeRepository;
import com.poliana.alura.challenges.budgetplannerapi.repository.MonthlySummaryRepository;
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
@RequestMapping("/incomes")
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private MonthlySummaryRepository monthlySummaryRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> newIncome(@RequestBody @Valid FormIncome formIncome) {
        Income income = formIncome.convert(incomeRepository);
        if (income != null) {
            income.addSummary(monthlySummaryRepository);
            incomeRepository.save(income);
            return new ResponseEntity<>(new IncomeDto(income), HttpStatus.CREATED);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The income already exists!");
    }

    @GetMapping
    public Page<IncomeDto> listIncomes(@PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
                                       @RequestParam(required = false) String description){
        if(description == null || description.trim().isEmpty()){
            Page<Income> incomePage = incomeRepository.findAll(pageable);
            return IncomeDto.convert(incomePage);
        }
        Page<Income> incomes = incomeRepository.findByDescription(pageable, description);
        return IncomeDto.convert(incomes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> incomeDetails(@PathVariable Long id) {
        Optional<Income> income = incomeRepository.findById(id);
        if(income.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new IncomeDto(income.get()));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateIncome(@PathVariable Long id, @RequestBody @Valid FormIncome formIncome) {
        Optional<Income> optional = incomeRepository.findById(id);
        if(optional.isPresent()) {
            Income income = formIncome.update(id, incomeRepository);
            return ResponseEntity.ok(new IncomeDto(income));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteIncome(@PathVariable Long id) {
        Optional<Income> optional = incomeRepository.findById(id);
        if(optional.isPresent()) {
            incomeRepository.deleteById(id);
            optional.get().updateSummaryOnDelete(monthlySummaryRepository);
            return ResponseEntity.ok("The income was successfully removed!");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{year}/{month}")
    public Page<IncomeDto> listIncomesByYearAndMonth(@PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
                                                     @PathVariable int year, @PathVariable int month){
        LocalDate minDate = LocalDate.of(year, month, 1);
        LocalDate maxDate = LocalDate.of(year, month, minDate.lengthOfMonth());
        Page<Income> incomes = incomeRepository.findByYearAndMonth(pageable, minDate, maxDate);
        return IncomeDto.convert(incomes);
    }
}
