package com.dicoding.courseschedule.ui.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.ui.home.HomeViewModelFactory
import com.dicoding.courseschedule.util.TimePickerFragment
import java.util.*

class AddCourseActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {

    private lateinit var et_name: EditText
    private lateinit var et_lecturer: EditText
    private lateinit var et_note: EditText
    private lateinit var spn_day: Spinner
    private var end_time = DEFAULT_TIME_VALUE
    private var start_time = DEFAULT_TIME_VALUE

    private lateinit var viewModel: AddCourseViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        et_name = findViewById<EditText>(R.id.et_course_name)
        et_note = findViewById<EditText>(R.id.et_note)
        et_lecturer = findViewById<EditText>(R.id.et_lecturer)
        spn_day = findViewById<Spinner>(R.id.spinner_day)

        viewModel = ViewModelProvider(this, HomeViewModelFactory.createTheFactory(this)).get(AddCourseViewModel::class.java)
        viewModel.saved.observe(this){
            if (it.getContentIfNotHandled() == true){
                Toast.makeText(this, "Course Added", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this, "Course Not Added", Toast.LENGTH_SHORT).show()
            }
        }

        val calendar = Calendar.getInstance()
        val day: Int = calendar.get(Calendar.DAY_OF_WEEK)
        val spinnerDay = findViewById<Spinner>(R.id.spinner_day)
        spinnerDay.setSelection(day-1)

    }

    fun timePicker(view: View){
        when(view.id){
            R.id.iv_start_time -> {
                val dialogFragment = TimePickerFragment()
                dialogFragment.show(supportFragmentManager, "timePickerStart")
            }
            R.id.iv_end_time -> {
                val dialogFragment = TimePickerFragment()
                dialogFragment.show(supportFragmentManager, "timePickerEnd")
            }
        }

    }
    private fun timeLeadByZero(timeNum: Int): String = if (timeNum < 10) "0$timeNum" else timeNum.toString()

    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        val hourStr = timeLeadByZero(hour)
        val minuteStr = timeLeadByZero(minute)

        when(tag){
            "timePickerStart" -> {
                start_time = "$hourStr:$minuteStr"
                Log.d("TAG", "onDialogTimeSet: $tag, $hourStr, $minuteStr")
                findViewById<TextView>(R.id.tv_start_time).text = start_time
            }
            "timePickerEnd" -> {
                end_time = "$hourStr:$minuteStr"
                Log.d("TAG", "onDialogTimeSet: $tag, $hourStr, $minuteStr")
                findViewById<TextView>(R.id.tv_end_time).text = end_time
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_insert -> {
                performAddToDatabase()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }


    private fun performAddToDatabase(){
        val name = et_name.text.toString()
        val lecturer = et_lecturer.text.toString()
        val dayName = spn_day.selectedItem.toString()
        val note = et_note.text.toString()
        val dayNum = when(dayName){
            getString(R.string.sunday) -> { 0 }
            getString(R.string.monday) -> { 1 }
            getString(R.string.tuesday) -> { 2 }
            getString(R.string.wednesday) -> { 3 }
            getString(R.string.thursday) -> { 4 }
            getString(R.string.friday) -> { 5 }
            else -> { 6 }
        }
        viewModel.insertCourse(name, dayNum, start_time, end_time, lecturer, note)
    }

    companion object{
        const val DEFAULT_TIME_VALUE = "08:00"
    }
}