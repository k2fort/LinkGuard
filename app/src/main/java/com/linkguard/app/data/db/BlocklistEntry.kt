package com.linkguard.app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "blocklist",
    indices = [Index(value = ["domain"], unique = true)]
)
data class BlocklistEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "domain") val domain: String,
    // "seed" = bundled at install, "phishtank" = downloaded feed, "user" = user-reported
    @ColumnInfo(name = "source") val source: String = "seed"
)
