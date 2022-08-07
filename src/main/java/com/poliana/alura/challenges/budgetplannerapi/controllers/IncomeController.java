package com.poliana.alura.challenges.budgetplannerapi.controllers;

import com.poliana.alura.challenges.budgetplannerapi.controllers.dto.IncomeDto;
import com.poliana.alura.challenges.budgetplannerapi.controllers.form.FormIncome;
import com.poliana.alura.challenges.budgetplannerapi.models.Income;
import com.poliana.alura.challenges.budgetplannerapi.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;

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
}
