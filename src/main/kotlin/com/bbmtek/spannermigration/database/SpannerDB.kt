package com.bbmtek.spannermigration.database

import com.bbmtek.spannermigration.model.Migrations

/**
 * Created by woi on 14/08/17.
 */
interface SpannerDB {
    fun createSchemaMigrationsTable(): Unit
    fun isTableExists(tableName: String): Boolean
    fun migrate(migrations: List<Migrations>)
    fun getMigratedVersions(): List<Long>
}