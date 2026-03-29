package com.gymcrm.workload.service;

import com.gymcrm.workload.dto.WorkloadRequest;
import com.gymcrm.workload.dto.WorkloadSummaryResponse;
import com.gymcrm.workload.entity.TrainerWorkloadEntry;
import com.gymcrm.workload.repository.TrainerWorkloadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkloadService {

    private static final Logger log = LoggerFactory.getLogger(WorkloadService.class);

    private final TrainerWorkloadRepository repository;

    public WorkloadService(TrainerWorkloadRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void processWorkload(WorkloadRequest req) {
        int year  = req.getTrainingDate().getYear();
        int month = req.getTrainingDate().getMonthValue();

        TrainerWorkloadEntry entry = findOrCreateEntry(req.getTrainerUsername(), year, month);

        entry.setFirstName(req.getFirstName());
        entry.setLastName(req.getLastName());
        entry.setIsActive(req.getIsActive());

        applyDelta(entry, req, year, month);

        repository.save(entry);
    }

    /**
     * Returns the nested year → month summary for a trainer.
     * Returns empty years list if trainer has no recorded trainings.
     */
    @Transactional(readOnly = true)
    public WorkloadSummaryResponse getSummary(String trainerUsername) {
        List<TrainerWorkloadEntry> entries = repository.findByTrainerUsername(trainerUsername);

        WorkloadSummaryResponse response = new WorkloadSummaryResponse();
        response.setTrainerUsername(trainerUsername);

        if (entries.isEmpty()) {
            log.warn("No workload entries found for trainer: {}", trainerUsername);
            response.setYears(Collections.emptyList());
            return response;
        }

        populateProfile(response, entries.get(0));

        List<WorkloadSummaryResponse.YearSummary> yearSummaries = entries.stream()
                .collect(Collectors.groupingBy(TrainerWorkloadEntry::getYear))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(this::toYearSummary)
                .collect(Collectors.toList());

        response.setYears(yearSummaries);
        return response;
    }

    //helpers

    private TrainerWorkloadEntry findOrCreateEntry(String username, int year, int month) {
        return repository
                .findByTrainerUsernameAndYearAndMonth(username, year, month)
                .orElseGet(() -> {
                    TrainerWorkloadEntry e = new TrainerWorkloadEntry();
                    e.setTrainerUsername(username);
                    e.setYear(year);
                    e.setMonth(month);
                    e.setTotalMinutes(0);
                    return e;
                });
    }

    /**
     * Adds or subtracts the training duration from the entry's total minutes.
     * DELETE is floored at 0 — minutes can never go negative.
     */
    private void applyDelta(TrainerWorkloadEntry entry, WorkloadRequest req, int year, int month) {
        int delta = req.getTrainingDuration();
        if (req.getActionType() == WorkloadRequest.ActionType.ADD) {
            entry.setTotalMinutes(entry.getTotalMinutes() + delta);
            log.info("ADD workload: trainer={} year={} month={} +{}min -> {}min total",
                     req.getTrainerUsername(), year, month, delta, entry.getTotalMinutes());
        } else {
            int updated = Math.max(0, entry.getTotalMinutes() - delta);
            entry.setTotalMinutes(updated);
            log.info("DELETE workload: trainer={} year={} month={} -{}min -> {}min total",
                     req.getTrainerUsername(), year, month, delta, updated);
        }
    }

    private void populateProfile(WorkloadSummaryResponse response, TrainerWorkloadEntry sample) {
        response.setFirstName(sample.getFirstName());
        response.setLastName(sample.getLastName());
        response.setTrainerStatus(sample.getIsActive());
    }

    /**
     * Converts a (year → entries) map entry into a YearSummary,
     * with months sorted ascending.
     */
    private WorkloadSummaryResponse.YearSummary toYearSummary(
            Map.Entry<Integer, List<TrainerWorkloadEntry>> yearEntry) {
        List<WorkloadSummaryResponse.MonthSummary> months = yearEntry.getValue().stream()
                .sorted(Comparator.comparingInt(TrainerWorkloadEntry::getMonth))
                .map(this::toMonthSummary)
                .collect(Collectors.toList());

        return new WorkloadSummaryResponse.YearSummary(yearEntry.getKey(), months);
    }

    /**
     * Maps a single DB row to a MonthSummary (month number + total minutes).
     */
    private WorkloadSummaryResponse.MonthSummary toMonthSummary(TrainerWorkloadEntry e) {
        return new WorkloadSummaryResponse.MonthSummary(e.getMonth(), e.getTotalMinutes());
    }
}
