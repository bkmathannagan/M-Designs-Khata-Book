package com.mdesigns.khata.data.dao

import androidx.room.*
import com.mdesigns.khata.data.entity.LedgerPersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerPersonDao {
    @Query("SELECT * FROM ledger_people WHERE profileId = :profileId AND isDeleted = 0 ORDER BY name ASC")
    fun getActivePeople(profileId: Long): Flow<List<LedgerPersonEntity>>

    @Query("SELECT * FROM ledger_people WHERE profileId = :profileId")
    suspend fun getAllPeopleList(profileId: Long): List<LedgerPersonEntity>

    @Query("SELECT * FROM ledger_people WHERE profileId = :profileId AND isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedPeople(profileId: Long): Flow<List<LedgerPersonEntity>>

    @Query("SELECT * FROM ledger_people WHERE id = :id LIMIT 1")
    suspend fun getPersonById(id: Long): LedgerPersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: LedgerPersonEntity): Long

    @Update
    suspend fun updatePerson(person: LedgerPersonEntity)

    @Query("UPDATE ledger_people SET isDeleted = 1, deletedAt = :timestamp WHERE id = :id")
    suspend fun softDeletePerson(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE ledger_people SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restorePerson(id: Long)

    @Query("DELETE FROM ledger_people WHERE id = :id")
    suspend fun permanentlyDeletePerson(id: Long)

    @Query("UPDATE ledger_people SET isSettled = :isSettled WHERE id = :id")
    suspend fun updateSettledStatus(id: Long, isSettled: Boolean)
}
