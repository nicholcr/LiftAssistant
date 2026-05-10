package com.example.liftassistant.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migrations for LiftAssistant.
 *
 * When making schema changes:
 * 1. Increment the version number in LiftAssistantDatabase.kt
 * 2. Add a new Migration object here following the naming convention MIGRATION_X_Y
 * 3. Register the migration in LiftAssistantDatabase.kt via .addMigrations()
 *
 * Example — adding a notes column to workouts (version 1 → 2):
 *
 * val MIGRATION_1_2 = object : Migration(1, 2) {
 *     override fun migrate(db: SupportSQLiteDatabase) {
 *         db.execSQL("ALTER TABLE workouts ADD COLUMN notes TEXT")
 *     }
 * }
 *
 * SQLite ALTER TABLE limitations:
 * - Can ADD columns but cannot DROP or RENAME them directly
 * - To rename/remove a column, create a new table, copy data, drop old, rename new
 * - New columns added via ALTER TABLE must be nullable or have a default value
 *
 * Common migration patterns:
 *
 * Add a nullable column:
 *   db.execSQL("ALTER TABLE table_name ADD COLUMN column_name TEXT")
 *
 * Add a non-null column with default:
 *   db.execSQL("ALTER TABLE table_name ADD COLUMN column_name INTEGER NOT NULL DEFAULT 0")
 *
 * Add a new table:
 *   db.execSQL("""
 *       CREATE TABLE IF NOT EXISTS `table_name` (
 *           `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
 *           `name` TEXT NOT NULL
 *       )
 *   """)
 *
 * Rename a column (full table recreation required):
 *   db.execSQL("""
 *       CREATE TABLE `table_name_new` (
 *           `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
 *           `new_column_name` TEXT NOT NULL
 *       )
 *   """)
 *   db.execSQL("INSERT INTO `table_name_new` SELECT `id`, `old_column_name` FROM `table_name`")
 *   db.execSQL("DROP TABLE `table_name`")
 *   db.execSQL("ALTER TABLE `table_name_new` RENAME TO `table_name`")
 */

// No migrations yet — currently at version 1
// Add future migrations here as the schema evolves