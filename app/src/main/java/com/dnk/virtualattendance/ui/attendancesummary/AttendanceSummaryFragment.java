package com.dnk.virtualattendance.ui.attendancesummary;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dnk.virtualattendance.database.DBManager;
import com.dnk.virtualattendance.databinding.FragmentAttendanceSummaryBinding;
import com.dnk.virtualattendance.model.AttendanceModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AttendanceSummaryFragment extends Fragment {
    private FragmentAttendanceSummaryBinding binding;

    private Set<Long> attendedDates = new HashSet<>();
    private Set<Long> absentDates = new HashSet<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AttendanceSummaryViewModel attendanceSummaryViewModel =
                new ViewModelProvider(this).get(AttendanceSummaryViewModel.class);

        binding = FragmentAttendanceSummaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        DBManager dbManager = new DBManager(getContext());
        dbManager.open();
        dbManager.insertDummyData();

        MaterialCalendarView materialCalendarView = binding.calendarView;

        // Get the attendance list for a specific user (pass the user ID)
        List<AttendanceModel> attendanceList = dbManager.getAttendanceListForUser("1");

        // SimpleDateFormat to parse the date from string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Change format if needed

        // Process the attendance data
        for (AttendanceModel attendance : attendanceList) {
            Log.d("Attendance", "Date: " + attendance.getDate() + ", Status: " + attendance.getStatus());

            try {
                // Parse the date string into a Date object
                Date date = dateFormat.parse(attendance.getDate());

                // If the date is valid, get the time in milliseconds
                if (date != null) {
                    long dateInMillis = date.getTime(); // Get time in milliseconds
                    if ("attended".equals(attendance.getStatus())) {
                        attendedDates.add(dateInMillis);
                    } else if ("absent".equals(attendance.getStatus())) {
                        absentDates.add(dateInMillis);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // Handle date parsing exceptions
            }
        }


        // Set a listener for date selection
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // Handle date selection if needed (e.g., show attendance details for selected date)

            }
        });

        dbManager.close();

        return root;
    }



    private void addDotToCalendar(MaterialCalendarView calendarView, CalendarDay calendarDay, int color) {
        // Add a decorator (dot) for the specific date
        calendarView.addDecorator(new EventDecorator(color, calendarDay));
    }

    // Custom decorator class for event markers (dots)
    public static class EventDecorator implements DayViewDecorator {
        private final int color;
        private final CalendarDay day;

        public EventDecorator(int color, CalendarDay day) {
            this.color = color;
            this.day = day;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return this.day.equals(day); // Only decorate the specific day
        }

        @Override
        public void decorate(DayViewFacade view) {
            // Set the dot color for the date
            view.addSpan(new DotSpan(10, color)); // 10 is the size of the dot
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FragmentChange", "Attendance Summary onCreate() called");
    }
}