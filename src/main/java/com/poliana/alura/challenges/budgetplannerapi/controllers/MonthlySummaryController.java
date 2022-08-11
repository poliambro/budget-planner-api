package com.poliana.alura.challenges.budgetplannerapi.controllers;

import com.poliana.alura.challenges.budgetplannerapi.controllers.dto.MonthlySummaryDto;
import com.poliana.alura.challenges.budgetplannerapi.models.MonthlySummary;
import com.poliana.alura.challenges.budgetplannerapi.repository.MonthlySummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("summary")
public class MonthlySummaryController {

    @Autowired
    MonthlySummaryRepository repository;

    @GetMapping
    public Page<MonthlySummaryDto> listSummaries(@PageableDefault(sort = "month", direction = Sort.Direction.ASC) Pageable pageable){
        Page<MonthlySummary> summaryPage = repository.findAll(pageable);
        return MonthlySummaryDto.convert(summaryPage);
    }

    @GetMapping("/{year}/{month}")
    public ResponseEntity<?> summaryDetailsByMonth(@PathVariable int year, @PathVariable int month) {
        Optional<MonthlySummary> summary = repository.findByYearAndMonth(year, month);
        if (summary.isPresent()) {
            return ResponseEntity.ok(new MonthlySummaryDto(summary.get()));
        }
        return ResponseEntity.notFound().build();
    }
}
