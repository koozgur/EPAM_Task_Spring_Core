package com.gymcrm.workload.service;

import com.gymcrm.workload.document.TrainerWorkloadDocument;
import com.gymcrm.workload.document.TrainerWorkloadDocument.MonthEntry;
import com.gymcrm.workload.document.TrainerWorkloadDocument.YearEntry;
import com.gymcrm.workload.dto.WorkloadRequest;
import com.gymcrm.workload.dto.WorkloadSummaryResponse;
import com.gymcrm.workload.repository.TrainerWorkloadDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class WorkloadService {

    private static final Logger log = LoggerFactory.getLogger(WorkloadService.class);

    private final TrainerWorkloadDocumentRepository repository;

    public WorkloadService(TrainerWorkloadDocumentRepository repository) {
        this.repository = repository;
    }

    /**
     * Processes an ADD or DELETE workload event for a trainer.
     * Finds the trainer's document (or creates one), updates the year/month entry,
     * and persists the whole document in a single atomic write.
     *
     * No @Transactional — MongoDB guarantees atomicity at the single-document level.
     */
    public void processWorkload(WorkloadRequest req) {
        String txId = MDC.get("transactionId");
        int year  = req.getTrainingDate().getYear();
        int month = req.getTrainingDate().getMonthValue();

        log.info("[WORKLOAD-START] trainer={} action={} txId={}", req.getTrainerUsername(), req.getActionType(), txId);

        log.debug("[WORKLOAD-LOOKUP] trainer={}", req.getTrainerUsername());
        TrainerWorkloadDocument doc = repository.findByTrainerUsername(req.getTrainerUsername())
                .orElseGet(TrainerWorkloadDocument::new);

        updateProfileFields(doc, req);
        applyDelta(doc, req, year, month, txId);

        log.debug("[WORKLOAD-SAVE] trainer={} txId={}", req.getTrainerUsername(), txId);
        repository.save(doc);
        log.info("[WORKLOAD-END] trainer={} txId={}", req.getTrainerUsername(), txId);
    }

    /**
     * Returns the nested year → month workload summary for a trainer.
     * Years and months are sorted ascending.
     * Returns an empty years list when no document exists yet.
     */
    public WorkloadSummaryResponse getSummary(String trainerUsername) {
        String txId = MDC.get("transactionId");
        log.info("[SUMMARY-START] trainer={} txId={}", trainerUsername, txId);

        WorkloadSummaryResponse response = repository.findByTrainerUsername(trainerUsername)
                .map(WorkloadMapper::toSummaryResponse)
                .orElseGet(() -> WorkloadMapper.emptyResponse(trainerUsername));

        log.info("[SUMMARY-END] trainer={} years={} txId={}", trainerUsername, response.getYears().size(), txId);
        return response;
    }

    /** Refreshes all trainer profile fields from the incoming request, including on a new document. */
    private void updateProfileFields(TrainerWorkloadDocument doc, WorkloadRequest req) {
        doc.setTrainerUsername(req.getTrainerUsername());
        doc.setFirstName(req.getFirstName());
        doc.setLastName(req.getLastName());
        doc.setActive(req.getIsActive());
    }

    /**
     * Finds (or creates) the year/month subdocument and applies the ADD or DELETE delta.
     * DELETE is floored at 0 — duration can never go negative.
     */
    private void applyDelta(TrainerWorkloadDocument doc, WorkloadRequest req, int year, int month, String txId) {
        YearEntry  yearEntry  = findOrCreateYear(doc, year);
        MonthEntry monthEntry = findOrCreateMonth(yearEntry, month);

        int before = monthEntry.getTrainingSummaryDuration();
        int delta  = req.getTrainingDuration();
        int after  = req.getActionType() == WorkloadRequest.ActionType.ADD
                ? before + delta
                : Math.max(0, before - delta);

        monthEntry.setTrainingSummaryDuration(after);
        log.debug("[WORKLOAD-DELTA] trainer={} year={} month={} action={} before={} after={} txId={}",
                req.getTrainerUsername(), year, month, req.getActionType(), before, after, txId);
    }

    /**
     * Returns the YearEntry for the given year, creating and adding it to the document if absent.
     */
    private YearEntry findOrCreateYear(TrainerWorkloadDocument doc, int year) {
        return doc.getYears().stream()
                .filter(y -> y.getYear().equals(year))
                .findFirst()
                .orElseGet(() -> {
                    YearEntry newYear = new YearEntry(year);
                    doc.getYears().add(newYear);
                    return newYear;
                });
    }

    /**
     * Returns the MonthEntry for the given month within a year, creating and adding it if absent.
     * New entries start at 0 so that the first ADD produces the correct total.
     */
    private MonthEntry findOrCreateMonth(YearEntry yearEntry, int month) {
        return yearEntry.getMonths().stream()
                .filter(m -> m.getMonth().equals(month))
                .findFirst()
                .orElseGet(() -> {
                    MonthEntry newMonth = new MonthEntry(month, 0);
                    yearEntry.getMonths().add(newMonth);
                    return newMonth;
                });
    }
}
