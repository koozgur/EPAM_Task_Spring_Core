package com.gymcrm.workload.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB document representing the aggregated training workload of a single trainer.
 * The data is stored using an embedded structure (Trainer → Years → Months → trainingSummaryDuration),
 * allowing all workload information to be retrieved and updated within a single document.
 * A unique index on {@code trainerUsername} ensures one document per trainer and enables fast primary lookups,
 * and a compound index on {@code (firstName, lastName)} supports search operations.
 *
 * <p>MongoDB guarantees atomic writes at the document level, and optimistic locking via {@code @Version}
 * prevents lost updates in concurrent scenarios. Each document aggregates training durations
 * per (year, month),
 */

@Document(collection = "trainer_workload")
@CompoundIndex(name = "idx_first_last_name", def = "{'firstName': 1, 'lastName': 1}")
public class TrainerWorkloadDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("trainerUsername")
    private String trainerUsername;

    @Field("firstName")
    private String firstName;

    @Field("lastName")
    private String lastName;

    @Field("isActive")
    private Boolean active;

    @Field("years")
    private List<YearEntry> years = new ArrayList<>();

    /**
     * Incremented by Spring Data MongoDB on every save.
     * Prevents lost-update anomalies when two threads process
     * messages for the same trainer concurrently.
     */
    @Version
    private Long version;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public List<YearEntry> getYears() { return years; }
    public void setYears(List<YearEntry> years) { this.years = years; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    // ── Nested: one subdocument per calendar year ────────────────────────────

    public static class YearEntry {

        @Field("year")
        private Integer year;

        @Field("months")
        private List<MonthEntry> months = new ArrayList<>();

        /** Required by MongoDB driver for deserialization. */
        public YearEntry() {}

        public YearEntry(Integer year) {
            this.year = year;
        }

        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }

        public List<MonthEntry> getMonths() { return months; }
        public void setMonths(List<MonthEntry> months) { this.months = months; }
    }

    // ── Nested: one subdocument per calendar month ───────────────────────────

    public static class MonthEntry {

        @Field("month")
        private Integer month;

        /**
         * Total training minutes for this month.
         * Named trainingSummaryDuration to match the task schema
         * and WorkloadSummaryResponse.MonthSummary.
         */
        @Field("trainingSummaryDuration")
        private Integer trainingSummaryDuration;

        /** Required by MongoDB driver for deserialization. */
        public MonthEntry() {}

        public MonthEntry(Integer month, Integer trainingSummaryDuration) {
            this.month = month;
            this.trainingSummaryDuration = trainingSummaryDuration;
        }

        public Integer getMonth() { return month; }
        public void setMonth(Integer month) { this.month = month; }

        public Integer getTrainingSummaryDuration() { return trainingSummaryDuration; }
        public void setTrainingSummaryDuration(Integer trainingSummaryDuration) {
            this.trainingSummaryDuration = trainingSummaryDuration;
        }
    }
}
