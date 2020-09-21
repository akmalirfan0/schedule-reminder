package com.isaiahvonrundstedt.fokus.features.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.forEach
import androidx.fragment.app.FragmentManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.timePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.chip.Chip
import com.isaiahvonrundstedt.fokus.R
import com.isaiahvonrundstedt.fokus.components.extensions.android.createToast
import com.isaiahvonrundstedt.fokus.components.extensions.android.setTextColorFromResource
import com.isaiahvonrundstedt.fokus.components.extensions.jdk.toCalendar
import com.isaiahvonrundstedt.fokus.components.extensions.jdk.toLocalTime
import com.isaiahvonrundstedt.fokus.components.extensions.jdk.toZonedDateTimeToday
import com.isaiahvonrundstedt.fokus.components.extensions.jdk.withCalendarFields
import com.isaiahvonrundstedt.fokus.features.shared.abstracts.BaseBottomSheet
import kotlinx.android.synthetic.main.layout_sheet_schedule_editor.*
import java.time.DayOfWeek
import java.time.LocalTime

class ScheduleEditor(manager: FragmentManager) : BaseBottomSheet<Schedule>(manager) {

    private var schedule: Schedule = Schedule()
    private var requestCode: Int = REQUEST_CODE_INSERT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_sheet_schedule_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.also {
            schedule.subject = it.getString(EXTRA_SUBJECT_ID)

            it.getParcelable<Schedule>(EXTRA_SCHEDULE)?.also { schedule ->
                this.schedule = schedule
                requestCode = REQUEST_CODE_UPDATE

                startTimeTextView.text = schedule.formatStartTime()
                endTimeTextView.text = schedule.formatEndTime()

                startTimeTextView.setTextColorFromResource(R.color.color_primary_text)
                endTimeTextView.setTextColorFromResource(R.color.color_primary_text)

                schedule.getDaysAsList().forEach { day ->
                    when (day) {
                        DayOfWeek.SUNDAY.value -> sundayChip.isChecked = true
                        DayOfWeek.MONDAY.value -> mondayChip.isChecked = true
                        DayOfWeek.TUESDAY.value -> tuesdayChip.isChecked = true
                        DayOfWeek.WEDNESDAY.value -> wednesdayChip.isChecked = true
                        DayOfWeek.THURSDAY.value -> thursdayChip.isChecked = true
                        DayOfWeek.FRIDAY.value -> fridayChip.isChecked = true
                        DayOfWeek.SATURDAY.value -> saturdayChip.isChecked = true
                    }
                }
            }
        }

        startTimeTextView.setOnClickListener {
            MaterialDialog(it.context).show {
                lifecycleOwner(this@ScheduleEditor)
                title(R.string.dialog_pick_start_time)
                timePicker(show24HoursView = false,
                    currentTime = schedule.startTime?.toZonedDateTimeToday()?.toCalendar()) { _, time ->
                    val startTime = time.toLocalTime()

                    schedule.startTime = startTime
                    if (schedule.endTime == null) schedule.endTime = startTime
                    if (startTime.isAfter(schedule.endTime) || startTime.compareTo(schedule.endTime) == 0) {
                        schedule.endTime = schedule.startTime?.plusHours(1)?.plusMinutes(30)
                        this@ScheduleEditor.endTimeTextView.text = schedule.formatEndTime()
                    }
                }
                positiveButton(R.string.button_done) { _ ->
                    if (it is AppCompatTextView) {
                        it.text = schedule.formatStartTime()
                        it.setTextColorFromResource(R.color.color_primary_text)
                        this@ScheduleEditor.endTimeTextView.setTextColorFromResource(R.color.color_primary_text)
                    }
                }
            }
        }


        endTimeTextView.setOnClickListener {
            MaterialDialog(it.context).show {
                lifecycleOwner(this@ScheduleEditor)
                title(R.string.dialog_pick_end_time)
                timePicker(show24HoursView = false,
                    currentTime = schedule.endTime?.toZonedDateTimeToday()?.toCalendar()) { _, time ->
                    val endTime = time.toLocalTime()

                    schedule.endTime = endTime
                    if (schedule.startTime == null) schedule.startTime = endTime
                    if (endTime.isBefore(schedule.startTime) || endTime.compareTo(schedule.startTime) == 0) {
                        schedule.startTime = schedule.endTime?.minusHours(1)?.minusMinutes(30)
                        this@ScheduleEditor.startTimeTextView.text = schedule.formatStartTime()
                    }
                }
                positiveButton(R.string.button_done) { _ ->
                    if (it is AppCompatTextView) {
                        it.text = schedule.formatEndTime()
                        it.setTextColorFromResource(R.color.color_primary_text)
                        this@ScheduleEditor.startTimeTextView.setTextColorFromResource(R.color.color_primary_text)
                    }
                }
            }
        }

        actionButton.setOnClickListener {
            schedule.daysOfWeek = 0
            daysOfWeekGroup.forEach {
                if ((it as? Chip)?.isChecked == true) {
                    schedule.daysOfWeek += when (it.id) {
                        R.id.sundayChip -> Schedule.BIT_VALUE_SUNDAY
                        R.id.mondayChip -> Schedule.BIT_VALUE_MONDAY
                        R.id.tuesdayChip -> Schedule.BIT_VALUE_TUESDAY
                        R.id.wednesdayChip -> Schedule.BIT_VALUE_WEDNESDAY
                        R.id.thursdayChip -> Schedule.BIT_VALUE_THURSDAY
                        R.id.fridayChip -> Schedule.BIT_VALUE_FRIDAY
                        R.id.saturdayChip -> Schedule.BIT_VALUE_SATURDAY
                        else -> 0
                    }
                }
            }

            // This ifs is used to check if some fields are
            // blank or null, if these returned true,
            // we'll show a Snackbar then direct the focus to
            // the corresponding field then return to stop
            // the execution of the code
            if (schedule.daysOfWeek == 0) {
                createToast(R.string.feedback_schedule_empty_days)
                return@setOnClickListener
            }

            if (schedule.startTime == null) {
                createToast(R.string.feedback_schedule_empty_start_time)
                startTimeTextView.performClick()
                return@setOnClickListener
            }

            if (schedule.endTime == null) {
                createToast(R.string.feedback_schedule_empty_end_time)
                endTimeTextView.performClick()
                return@setOnClickListener
            }

            receiver?.onReceive(schedule)
            this.dismiss()
        }
    }

    companion object {
        const val REQUEST_CODE_INSERT = 43
        const val REQUEST_CODE_UPDATE = 89
        const val EXTRA_SCHEDULE = "extra:schedule"
        const val EXTRA_SUBJECT_ID = "extra:subject:id"
    }
}