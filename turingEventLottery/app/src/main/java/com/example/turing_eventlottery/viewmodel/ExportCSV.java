package com.example.turing_eventlottery.viewmodel;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Export the list of enrolled entrants to CSV.
 */
public final class ExportCSV {

    private static final String DEFAULT_FILE_PREFIX = "event";

    private ExportCSV() {
    }

    public static String buildFinalEnrolledEntrantsCsv(List<Map<String, Object>> entrants) {
        StringBuilder csvBuilder = new StringBuilder();
        appendRow(csvBuilder, "Entrant Name", "Enrollment Status", "Date/Time of Enrollment");

        if (entrants == null) {
            return csvBuilder.toString();
        }

        for (Map<String, Object> entrant : entrants) {
            if (entrant == null) {
                appendRow(csvBuilder, "Unknown User", "Enrolled", "");
                continue;
            }

            String username = safeString(entrant.get("username"), "Unknown User");
            String status = safeString(entrant.get("status"), "Enrolled");
            String enrolledAt = formatEnrollmentTimestamp(entrant.get("timestamp"));

            appendRow(csvBuilder, username, status, enrolledAt);
        }

        return csvBuilder.toString();
    }

    public static String createFinalEnrolledFileName(String eventName, Date exportDate) {
        String safeEventName = sanitizeFileNameSegment(eventName);
        Date timestamp = exportDate != null ? exportDate : new Date();
        String dateSuffix = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(timestamp);
        return safeEventName + "_final_enrolled_" + dateSuffix + ".csv";
    }

    private static void appendRow(StringBuilder csvBuilder, String... values) {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                csvBuilder.append(',');
            }
            csvBuilder.append(escapeCsv(values[i]));
        }
        csvBuilder.append("\r\n");
    }

    private static String escapeCsv(String value) {
        String safeValue = value == null ? "" : value;
        boolean requiresQuotes = safeValue.contains(",")
                || safeValue.contains("\"")
                || safeValue.contains("\n")
                || safeValue.contains("\r");

        if (!requiresQuotes) {
            return safeValue;
        }

        return "\"" + safeValue.replace("\"", "\"\"") + "\"";
    }

    private static String formatEnrollmentTimestamp(Object timestampValue) {
        if (timestampValue instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) timestampValue;
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(timestamp.toDate());
        }

        if (timestampValue instanceof Date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format((Date) timestampValue);
        }

        return "";
    }

    private static String safeString(Object value, String fallback) {
        if (!(value instanceof String)) {
            return fallback;
        }

        String text = ((String) value).trim();
        return text.isEmpty() ? fallback : text;
    }

    private static String sanitizeFileNameSegment(String eventName) {
        String trimmedName = eventName == null ? "" : eventName.trim();
        String normalizedName = trimmedName.replaceAll("[^a-zA-Z0-9._-]+", "_");
        normalizedName = normalizedName.replaceAll("^_+|_+$", "");
        return normalizedName.isEmpty() ? DEFAULT_FILE_PREFIX : normalizedName;
    }
}
