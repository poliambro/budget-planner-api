package com.poliana.alura.challenges.budgetplannerapi.repository;

import com.poliana.alura.challenges.budgetplannerapi.models.MonthlySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonthlySummaryRepository extends PagingAndSortingRepository<MonthlySummary, Long>, JpaRepository<MonthlySummary, Long> {

    Optional<MonthlySummary> findByYearAndMonth(int year, int month);
}
