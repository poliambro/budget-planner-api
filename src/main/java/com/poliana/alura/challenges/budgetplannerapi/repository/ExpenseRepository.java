package com.poliana.alura.challenges.budgetplannerapi.repository;

import com.poliana.alura.challenges.budgetplannerapi.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends PagingAndSortingRepository<Expense, Long>, JpaRepository<Expense, Long> {

    Optional<Expense> findByDescriptionAndDate(String description, LocalDate date);
}
