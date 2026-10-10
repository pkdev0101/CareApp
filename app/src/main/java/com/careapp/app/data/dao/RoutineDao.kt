package com.careapp.app.data.dao

import androidx.room.*
import com.careapp.app.data.RoutineTask
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineTask(task: RoutineTask)

    @Query("SELECT * FROM routine_tasks WHERE category = :category ORDER BY suggestedTime ASC")
    fun getTasksByCategory(category: String): Flow<List<RoutineTask>>

    @Query("SELECT * FROM routine_tasks ORDER BY category ASC, suggestedTime ASC")
    suspend fun getAllRoutineTasks(): List<RoutineTask>

    @Update
    suspend fun updateRoutineTask(task: RoutineTask)

    @Delete
    suspend fun deleteRoutineTask(task: RoutineTask)

}
