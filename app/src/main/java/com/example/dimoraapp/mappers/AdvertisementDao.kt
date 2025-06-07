package com.example.dimoraapp.mappers

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dimoraapp.entities.AdvertisementEntity

@Dao
interface AdvertisementDao {
    @Query("SELECT * FROM advertisements")
    suspend fun getAllAds(): List<AdvertisementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAds(ads: List<AdvertisementEntity>)

    @Query("DELETE FROM advertisements")
    suspend fun clearAds()
}