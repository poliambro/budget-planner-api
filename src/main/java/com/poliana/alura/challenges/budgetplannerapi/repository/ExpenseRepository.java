package com.poliana.alura.challenges.budgetplannerapi.repository;

import com.poliana.alura.challenges.budgetplannerapi.models.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends PagingAndSortingRepository<Expense, Long>, JpaRepository<Expense, Long> {

    String FIND_BY_YEAR_AND_MONTH_QUERY = "select * from expenses e " +
            "where e.date between :minDate and :maxDate";

    Optional<Expense> findByDescriptionAndDate(String description, LocalDate date);

    Page<Expense> findByDescription(Pageable pageable, String description);

    @Query(value = FIND_BY_YEAR_AND_MONTH_QUERY, nativeQuery = true)
    Page<Expense> findByYearAndMonth(Pageable pageable, LocalDate minDate, LocalDate maxDate);
}
