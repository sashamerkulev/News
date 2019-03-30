package ru.merkulyevsasha.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import ru.merkulyevsasha.database.entities.RssSourceEntity

@Dao
interface SetupDao {
    @Insert
    fun saveRssSources(items: List<RssSourceEntity>)
}
