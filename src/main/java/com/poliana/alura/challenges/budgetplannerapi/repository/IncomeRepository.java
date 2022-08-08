package com.poliana.alura.challenges.budgetplannerapi.repository;

import com.poliana.alura.challenges.budgetplannerapi.models.Income;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface IncomeRepository extends PagingAndSortingRepository<Income, Long>, JpaRepository<Income, Long> {

    Optional<Income> findByDescriptionAndDate(String description, LocalDate date);

    Page<Income> findByDescription(Pageable pageable, String description);
}
