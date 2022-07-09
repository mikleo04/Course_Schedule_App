package com.dicoding.courseschedule.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dicoding.courseschedule.util.QueryType
import com.dicoding.courseschedule.util.QueryUtil
import com.dicoding.courseschedule.util.QueryUtil.nearestQuery
import com.dicoding.courseschedule.util.SortType
import com.dicoding.courseschedule.util.executeThread
import java.util.*

//TODO 4 : Implement repository with appropriate dao
class DataRepository(private val dao: CourseDao) {

    fun getNearestSchedule(queryType: QueryType) : LiveData<Course?> {
        return dao.getNearestSchedule(nearestQuery(queryType))
    }

    fun getAllCourse(sortType: SortType): LiveData<PagedList<Course>> {
        return LivePagedListBuilder(
            dao.getAll(
                QueryUtil.sortedQuery(sortType)
            ),
            PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(30)
                .setPageSize(PAGE_SIZE)
                .build()
        ).build()
    }

    fun getCourse(id: Int) : LiveData<Course> {
        return dao.getCourse(id)
    }

    fun getTodaySchedule() : List<Course> {
        return dao.getTodaySchedule(
            Calendar.getInstance()
                .get(Calendar.DAY_OF_WEEK)
        )
    }

    fun insert(course: Course) = executeThread {
        dao.insert(course)
    }

    fun delete(course: Course) = executeThread {
        dao.delete(course)
    }

    companion object {
        @Volatile
        private var instance: DataRepository? = null
        private const val PAGE_SIZE = 10

        fun getInstance(context: Context): DataRepository? {
            return instance ?: synchronized(DataRepository::class.java) {
                if (instance == null) {
                    instance = DataRepository(CourseDatabase.getInstance(context).courseDao())
                }
                return instance
            }
        }
    }
}