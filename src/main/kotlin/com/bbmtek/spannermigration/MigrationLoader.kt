package com.bbmtek.spannermigration

import com.bbmtek.spannermigration.model.Migrations
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.File

/**
 * Created by woi on 04/09/17.
 */
class MigrationLoader {
    fun loadMigrations(migrationDir: String, versions: List<Long>): List<Migrations> {
        val migrationFiles = File(migrationDir).listFiles().sortedBy { it.name }
        val migrations = arrayListOf<Migrations>()
        val filteredFiles = migrationFiles.filter {
            !versions.contains(it.nameWithoutExtension.split("_")[0].toLong())
        }

        val objectMapper = ObjectMapper(YAMLFactory())
        filteredFiles.mapTo(migrations) {
            objectMapper.readValue(it, Migrations::class.java)
                    .copy(version = it.nameWithoutExtension.split("_")[0].toLong())
        }
        return migrations
    }
}