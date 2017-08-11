package com.bbmtek.spannermigration.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * Created by woi on 11/08/17.
 */
data class Migrations(val up: List<MigrationUp> = listOf(), val version: Long = 0)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(*arrayOf(
        JsonSubTypes.Type(value = MigrationUp.CreateTable::class, name = "CreateTable")
))
sealed class MigrationUp {
    abstract var name: kotlin.String
    abstract var columns: List<ColumnDefinition>
    abstract var primaryKeys: List<PrimaryKeyDefinition>

    data class CreateTable(
            override var name: kotlin.String = "",
            override var columns: List<ColumnDefinition> = listOf(),
            override var primaryKeys: List<PrimaryKeyDefinition> = listOf()
    ) : MigrationUp() {
        fun columnsToSqlString(): kotlin.String {
            return this.columns
                    .map {
                        when(it) {
                            is ColumnDefinition.String -> {
                                "${it.name} ${it.dataType()}(${it.maxLengthString()}) ${it.requiredString()}"
                            }
                            else -> {
                                "${it.name} ${it.dataType()} ${it.requiredString()}"
                            }
                        }
                    }
                    .joinToString(", ")
        }

        fun primaryKeysToSqlString(): kotlin.String {
            return this.primaryKeys
                    .map { "${it.name} ${it.order}" }
                    .joinToString(", ")
        }
    }

}

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes(*arrayOf(
        JsonSubTypes.Type(value = ColumnDefinition.Int64::class, name = "Int64"),
        JsonSubTypes.Type(value = ColumnDefinition.Timestamp::class, name = "Timestamp"),
        JsonSubTypes.Type(value = ColumnDefinition.String::class, name = "String")
))
sealed class ColumnDefinition {
    abstract var name: kotlin.String
    abstract var required: Boolean

    data class Int64(
            override var name: kotlin.String = "",
            override var required: Boolean = false
    ) : ColumnDefinition()

    data class String(
            override var name: kotlin.String = "",
            override var required: Boolean = false,
            var maxLength: Int? = null
    ) : ColumnDefinition() {
        fun maxLengthString(): kotlin.String {
            return if(maxLength != null) {
                maxLength.toString()
            } else {
                "MAX"
            }
        }
    }

    data class Timestamp(
            override var name: kotlin.String = "",
            override var required: Boolean = false
    ) : ColumnDefinition()

    fun dataType(): kotlin.String {
        return this::class.java.simpleName
    }

    fun requiredString(): kotlin.String {
        return if(required) {
            "NOT NULL"
        } else {
            ""
        }
    }
}

data class PrimaryKeyDefinition(
        var name: kotlin.String = "",
        var order: kotlin.String = ""
)