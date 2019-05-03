package ru.merkulyevsasha.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import ru.merkulyevsasha.database.entities.RssSourceEntity

@Dao
interface SetupDao {
    @Insert
    fun saveRssSources(items: List<RssSourceEntity>)

    @Query("delete from sources")
    fun deleteRssSources()

    @Query("select id, name, title from sources")
    fun getRssSources(): List<RssSourceEntity>
}
