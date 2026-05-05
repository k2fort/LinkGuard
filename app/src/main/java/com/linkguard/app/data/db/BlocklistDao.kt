package com.linkguard.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BlocklistDao {

    /** Returns 1 if the exact domain (or any parent domain) is blocked, 0 otherwise. */
    @Query("SELECT COUNT(*) FROM blocklist WHERE domain = :domain LIMIT 1")
    suspend fun isDomainBlocked(domain: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entries: List<BlocklistEntry>)

    @Query("SELECT COUNT(*) FROM blocklist")
    suspend fun count(): Int

    @Query("DELETE FROM blocklist WHERE source = :source")
    suspend fun deleteBySource(source: String)
}
