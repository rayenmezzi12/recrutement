package com.recrutement.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(0)
@RequiredArgsConstructor
@Slf4j
public class DatabaseMigrationRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        migrateApplicationSteps();
        migrateReadColumns("messages");
        migrateReadColumns("app_notifications");
        ensureCandidateColumns();
    }

    private void migrateApplicationSteps() {
        if (!tableExists("applications")) {
            return;
        }
        runQuietly("UPDATE applications SET current_step = 'ENTRETIEN_RH' WHERE current_step = 'ENTRETIEN'");
        runQuietly("UPDATE applications SET current_step = 'TEST_TECHNIQUE' WHERE current_step = 'TEST'");
        runQuietly("UPDATE applications SET current_step = 'OFFRE_EMBAUCHE' WHERE current_step = 'OFFRE'");
        runQuietly("UPDATE applications SET current_step = 'PRE_SELECTION' WHERE current_step IS NULL");
        runQuietly("ALTER TABLE applications ADD COLUMN IF NOT EXISTS global_score DOUBLE PRECISION");
        runQuietly("UPDATE applications SET status = 'EN_ATTENTE' WHERE status = 'PENDING'");
        runQuietly("UPDATE applications SET status = 'SELECTIONNE' WHERE status IN ('SELECTED', 'APPROVED')");
        runQuietly("UPDATE applications SET status = 'REJETE' WHERE status IN ('REJECTED', 'REFUSED')");
        runQuietly("UPDATE applications SET archived = false WHERE archived IS NULL");
    }

    private void migrateReadColumns(String table) {
        if (!tableExists(table)) {
            return;
        }
        if (columnExists(table, "read") && !columnExists(table, "is_read")) {
            runQuietly("ALTER TABLE " + table + " RENAME COLUMN read TO is_read");
            log.info("Renamed {}.read -> is_read", table);
        }
        if (!columnExists(table, "is_read")) {
            runQuietly("ALTER TABLE " + table + " ADD COLUMN is_read BOOLEAN DEFAULT false");
        }
    }

    private void ensureCandidateColumns() {
        if (!tableExists("candidates")) {
            return;
        }
        renameColumnIfExists("candidates", "firstname", "first_name");
        renameColumnIfExists("candidates", "lastname", "last_name");
        renameColumnIfExists("candidates", "firstName", "first_name");
        renameColumnIfExists("candidates", "lastName", "last_name");
        renameColumnIfExists("candidates", "resumeurl", "resume_url");
        renameColumnIfExists("candidates", "coverletter", "cover_letter");
        if (columnExists("candidates", "experience") && !columnExists("candidates", "experience_years")) {
            runQuietly("ALTER TABLE candidates RENAME COLUMN experience TO experience_years");
        }
        runQuietly("ALTER TABLE candidates ADD COLUMN IF NOT EXISTS username VARCHAR(255)");
        runQuietly("ALTER TABLE candidates ADD COLUMN IF NOT EXISTS title VARCHAR(255)");
        runQuietly("ALTER TABLE candidates ADD COLUMN IF NOT EXISTS skills VARCHAR(1000)");
        runQuietly("ALTER TABLE candidates ADD COLUMN IF NOT EXISTS experience_years INTEGER");
        runQuietly("ALTER TABLE candidates ADD COLUMN IF NOT EXISTS resume_url VARCHAR(500)");
        runQuietly("ALTER TABLE candidates ADD COLUMN IF NOT EXISTS cover_letter TEXT");
        runQuietly("ALTER TABLE candidates ADD COLUMN IF NOT EXISTS phone VARCHAR(50)");
        runQuietly("ALTER TABLE candidates ADD COLUMN IF NOT EXISTS address VARCHAR(500)");
        runQuietly("ALTER TABLE candidates ADD COLUMN IF NOT EXISTS date_of_birth DATE");
        runQuietly("ALTER TABLE candidates ADD COLUMN IF NOT EXISTS linkedin VARCHAR(255)");
        migrateOfferStatuses();
    }

    private void migrateOfferStatuses() {
        if (!tableExists("offers")) {
            return;
        }
        runQuietly("UPDATE offers SET status = 'EN_ATTENTE' WHERE status = 'GENERATED'");
        runQuietly("UPDATE offers SET status = 'ENVOYEE' WHERE status = 'SENT'");
        runQuietly("UPDATE offers SET status = 'ACCEPTEE' WHERE status = 'ACCEPTED'");
        runQuietly("UPDATE offers SET status = 'REFUSEE' WHERE status = 'REJECTED'");
    }

    private boolean tableExists(String table) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = ?",
                Integer.class,
                table);
        return count != null && count > 0;
    }

    private boolean columnExists(String table, String column) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = 'public' AND table_name = ? AND column_name = ?",
                Integer.class,
                table,
                column);
        return count != null && count > 0;
    }

    private void renameColumnIfExists(String table, String from, String to) {
        if (columnExists(table, from) && !columnExists(table, to)) {
            runQuietly("ALTER TABLE " + table + " RENAME COLUMN " + from + " TO " + to);
            log.info("Renamed {}.{} -> {}", table, from, to);
        }
    }

    private void runQuietly(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            log.debug("Migration skipped ({}): {}", sql, e.getMessage());
        }
    }
}
