package com.gymcrm.workload.service;

import com.gymcrm.workload.document.TrainerWorkloadDocument;
import com.gymcrm.workload.document.TrainerWorkloadDocument.MonthEntry;
import com.gymcrm.workload.document.TrainerWorkloadDocument.YearEntry;
import com.gymcrm.workload.dto.WorkloadSummaryResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *mapper between {@link TrainerWorkloadDocument} and {@link WorkloadSummaryResponse}.
 */
class WorkloadMapper {

    private WorkloadMapper() {}

    static WorkloadSummaryResponse toSummaryResponse(TrainerWorkloadDocument doc) {
        List<WorkloadSummaryResponse.YearSummary> yearSummaries = doc.getYears().stream()
                .sorted(Comparator.comparingInt(YearEntry::getYear))
                .map(WorkloadMapper::toYearSummary)
                .toList();

        WorkloadSummaryResponse response = new WorkloadSummaryResponse();
        response.setTrainerUsername(doc.getTrainerUsername());
        response.setFirstName(doc.getFirstName());
        response.setLastName(doc.getLastName());
        response.setTrainerStatus(doc.getActive());
        response.setYears(yearSummaries);
        return response;
    }

    static WorkloadSummaryResponse emptyResponse(String trainerUsername) {
        WorkloadSummaryResponse response = new WorkloadSummaryResponse();
        response.setTrainerUsername(trainerUsername);
        response.setYears(Collections.emptyList());
        return response;
    }

    private static WorkloadSummaryResponse.YearSummary toYearSummary(YearEntry yearEntry) {
        List<WorkloadSummaryResponse.MonthSummary> months = yearEntry.getMonths().stream()
                .sorted(Comparator.comparingInt(MonthEntry::getMonth))
                .map(m -> new WorkloadSummaryResponse.MonthSummary(m.getMonth(), m.getTrainingSummaryDuration()))
                .toList();

        return new WorkloadSummaryResponse.YearSummary(yearEntry.getYear(), months);
    }
}
