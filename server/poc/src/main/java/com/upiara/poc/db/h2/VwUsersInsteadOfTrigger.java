package com.upiara.poc.db.h2;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.h2.api.Trigger;

/**
 * H2 trigger used ONLY in tests to emulate Oracle's INSTEAD OF trigger on the VW_USERS view.
 *
 * <p>
 * Oracle behavior in db/database.sql:
 * - On INSERT/UPDATE, resolve LANGUAGE_ID from LANGUAGES.CODE (or NULL if not found)
 * - Set BIRTHDAY to SYSDATE (we use CURRENT_DATE for deterministic date semantics in H2)
 * - Perform DML against USERS table
 * </p>
 *
 * <p>
 * This keeps production mappings untouched (entity remains mapped to VW_USERS), but makes
 * CRUD work in H2 tests even though VW_USERS is a view.
 * </p>
 */
public class VwUsersInsteadOfTrigger implements Trigger {

    @Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type)
            throws SQLException {
        // No-op
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        // View columns order from test-schema.sql:
        // 0 ID, 1 NAME, 2 BIRTHDAY, 3 LANGUAGE_CODE, 4 LANGUAGE_DESCRIPTION
        final boolean isInsert = oldRow == null && newRow != null;
        final boolean isDelete = oldRow != null && newRow == null;
        final boolean isUpdate = oldRow != null && newRow != null;

        if (isInsert) {
            handleInsert(conn, newRow);
        } else if (isUpdate) {
            handleUpdate(conn, oldRow, newRow);
        } else if (isDelete) {
            handleDelete(conn, oldRow);
        }
    }

    private void handleInsert(Connection conn, Object[] newRow) throws SQLException {
        final String name = (String) newRow[1];
        final String languageCode = (String) newRow[3];

        final Long languageId = resolveLanguageId(conn, languageCode);

        // Mirror Oracle trigger behavior: set birthday to "now" at DB-level.
        final Date birthday = currentDate(conn);

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO USERS (NAME, BIRTHDAY, LANGUAGE_ID) VALUES (?, ?, ?)")) {
            ps.setString(1, name);
            ps.setDate(2, birthday);
            if (languageId == null) {
                ps.setNull(3, java.sql.Types.BIGINT);
            } else {
                ps.setLong(3, languageId);
            }
            ps.executeUpdate();
        }
    }

    private void handleUpdate(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        final Long id = toLong(newRow[0] != null ? newRow[0] : oldRow[0]);
        final String name = (String) newRow[1];
        final String languageCode = (String) newRow[3];

        final Long languageId = resolveLanguageId(conn, languageCode);
        final Date birthday = currentDate(conn);

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE USERS SET NAME = ?, BIRTHDAY = ?, LANGUAGE_ID = ? WHERE ID = ?")) {
            ps.setString(1, name);
            ps.setDate(2, birthday);
            if (languageId == null) {
                ps.setNull(3, java.sql.Types.BIGINT);
            } else {
                ps.setLong(3, languageId);
            }
            ps.setLong(4, id);
            ps.executeUpdate();
        }
    }

    private void handleDelete(Connection conn, Object[] oldRow) throws SQLException {
        final Long id = toLong(oldRow[0]);

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM USERS WHERE ID = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Long resolveLanguageId(Connection conn, String languageCode) throws SQLException {
        if (languageCode == null || languageCode.trim().isEmpty()) {
            return null;
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT ID FROM LANGUAGES WHERE CODE = ?")) {
            ps.setString(1, languageCode.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return null;
    }

    private Date currentDate(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT CURRENT_DATE");
                ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getDate(1);
        }
    }

    private Long toLong(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof Long) {
            return (Long) v;
        }
        if (v instanceof Integer) {
            return ((Integer) v).longValue();
        }
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        return Long.valueOf(v.toString());
    }

    @Override
    public void close() throws SQLException {
        // No-op
    }

    @Override
    public void remove() throws SQLException {
        // No-op
    }
}
