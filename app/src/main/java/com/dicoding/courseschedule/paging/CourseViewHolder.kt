package com.dicoding.courseschedule.paging

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.util.DayName.Companion.getByNumber

class CourseViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private lateinit var course: Course
    private val timeString = itemView.context.resources.getString(R.string.time_format)

    //TODO 7 : Complete ViewHolder to show item
    fun bind(course: Course, clickListener: (Course) -> Unit) {
        this.course = course

        course.apply {
            val day = getByNumber(day)
            val timeFormat = String.format(timeString, day, startTime, endTime)
            
            (itemView.findViewById(R.id.tv_time) as TextView).text = timeFormat
            (itemView.findViewById(R.id.tv_course) as TextView).text = courseName
            (itemView.findViewById(R.id.tv_lecturer) as TextView).text = lecturer
        }

        itemView.setOnClickListener {
            clickListener(course)
        }
    }

    fun getCourse(): Course = course
}