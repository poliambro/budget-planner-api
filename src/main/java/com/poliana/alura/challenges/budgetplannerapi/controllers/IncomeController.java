package com.poliana.alura.challenges.budgetplannerapi.controllers;

import com.poliana.alura.challenges.budgetplannerapi.controllers.dto.IncomeDto;
import com.poliana.alura.challenges.budgetplannerapi.controllers.form.FormIncome;
import com.poliana.alura.challenges.budgetplannerapi.models.Income;
import com.poliana.alura.challenges.budgetplannerapi.repository.IncomeRepository;
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
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/incomes")
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> newIncome(@RequestBody @Valid FormIncome formIncome) {
        Income income = formIncome.convert(incomeRepository);
        if (income != null) {
            incomeRepository.save(income);
            return new ResponseEntity<>(new IncomeDto(income), HttpStatus.CREATED);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The income already exists!");
    }

    @GetMapping
    public Page<IncomeDto> listIncomes(@PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable){
        Page<Income> incomePage = incomeRepository.findAll(pageable);
        return IncomeDto.convert(incomePage);
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
            return ResponseEntity.ok("The income was successfully removed!");
        }
        return ResponseEntity.notFound().build();
    }
}
