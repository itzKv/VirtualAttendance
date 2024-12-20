package com.dnk.virtualattendance.ui.attendancesummary;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dnk.virtualattendance.HomeActivity;
import com.dnk.virtualattendance.model.RoleModel;
import com.dnk.virtualattendance.R;
import com.dnk.virtualattendance.database.DBHelper;
import com.dnk.virtualattendance.database.DBManager;
import com.dnk.virtualattendance.databinding.FragmentAttendanceSummaryBinding;
import com.dnk.virtualattendance.model.AttendanceModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.SimpleDateFormat;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class AttendanceSummaryFragment extends Fragment {
    private FragmentAttendanceSummaryBinding binding;
    private RecyclerView recyclerView;;
    private AttendanceAdapter attendanceAdapter;
    private List<AttendanceModel> attendanceList = new ArrayList<>();

    private DBManager dbManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AttendanceSummaryViewModel attendanceSummaryViewModel =
                new ViewModelProvider(this).get(AttendanceSummaryViewModel.class);

        binding = FragmentAttendanceSummaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        HomeActivity activity = (HomeActivity) getActivity();
        if (activity != null) {
            RoleModel userRole = activity.getUserRole();
            Log.d("UserRoleFragment", "User Role: " + userRole.getRoleName());
        }

        dbManager = new DBManager(getContext());
        dbManager.open();
        dbManager.insertDummyData();
        attendanceList = dbManager.getAttendanceListForUser("3");
        Log.d("AttendanceListSize", Integer.toString(attendanceList.size()));
        setupRecyclerView();
        MaterialCalendarView materialCalendarView = binding.calendarView;

        // SimpleDateFormat to parse the date from string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Change format if needed

        // Process the attendance data
        for (AttendanceModel attendance : attendanceList) {
            Log.d("AttendanceData", "Date: " + attendance.getDate() + " Status: " + attendance.getIsAttended());
            try {
                Date date = dateFormat.parse(attendance.getDate());
                if (date != null) {
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    calendar.setTime(date);

                    int year = calendar.get(java.util.Calendar.YEAR);
                    int month = calendar.get(java.util.Calendar.MONTH) + 1; // Months are 0-based
                    int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

                    org.threeten.bp.LocalDate localDate = org.threeten.bp.LocalDate.of(year, month, day);
                    CalendarDay calendarDay = CalendarDay.from(localDate);

                    if (attendance.getIsAttended() == 1) {
                        addDotToCalendar(materialCalendarView, calendarDay, Color.GREEN);
                    } else if (attendance.getIsAttended() == 0){
                        addDotToCalendar(materialCalendarView, calendarDay, Color.RED);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String yearMonth = date.getYear() + "-" + date.getMonth();
                Log.d("MonthChanged", yearMonth);
                filterAttendanceByMonthAndYear(date.getMonth(), date.getYear());

            }
        });
        // Set a listener for date selection
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // Handle date selection
                // Get the selected date as a string or any format you want
                String selectedDate = String.format("%d-%02d-%02d",
                        date.getYear(),
                        date.getMonth(),
                        date.getDay());
                Log.d("SelectedDate", "Date selected: " + selectedDate);

                filterAttendanceByDate(selectedDate);
            }
        });

        return root;
    }

    private void filterAttendanceByMonthAndYear(int selectedMonth, int selectedYear) {
        List<AttendanceModel> filteredList = new ArrayList<>();

        // SimpleDateFormat to parse the date from string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (AttendanceModel attendance : attendanceList) {
            Log.d("AttendanceData", attendance.getDate());
            try {
                Date date = dateFormat.parse(attendance.getDate());
                if (date != null) {
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    calendar.setTime(date);

                    int year = calendar.get(java.util.Calendar.YEAR);
                    int month = calendar.get(java.util.Calendar.MONTH) + 1; // Months are 0-based
                    Log.d("YearInMonthChanged", Integer.toString(year));
                    Log.d("MonthInMonthChanged", Integer.toString(month));
                    Log.d("SelectedYear", Integer.toString(selectedYear));
                    Log.d("SelectedMonth", Integer.toString(selectedMonth));
                    // Check if the year and month match the current ones
                    if (year == selectedYear && month == selectedMonth) {
                        Log.d("FilteredListMonth", "Adding: " + attendance.getDate());
                        filteredList.add(attendance);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Update the RecyclerView with the filtered list
        updateRecyclerView(filteredList);
    }

    private void filterAttendanceByDate(String selectedDate) {
        List<AttendanceModel> filteredList = new ArrayList<>();

        for (AttendanceModel attendance : attendanceList) {
            Log.d("SelectedDate", "Selected Date: " + selectedDate);
            if (attendance.getDate().equals(selectedDate)) {
                Log.d("FilteredList", selectedDate);
                filteredList.add(attendance);
            }
        }

        Log.d("FilteredList", filteredList.toString());

        // Update the RecyclerView with the filtered list
        updateRecyclerView(filteredList);
    }

    private void updateRecyclerView(List<AttendanceModel> filteredList) {
        Log.d("Updated Filtered List", Integer.toString(filteredList.size()));
        attendanceAdapter.updateData(filteredList);
    }

    private void setupRecyclerView() {
        recyclerView = binding.attendanceRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Assuming you have fetched the attendance data in attendanceList
        attendanceAdapter = new AttendanceAdapter(getContext(), attendanceList);
        recyclerView.setAdapter(attendanceAdapter);
    }

//    private void fetchAttendanceData() {
//
//        // Notify the adapter to update the RecyclerView
//        attendanceAdapter.notifyDataSetChanged();
//    }


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
            // Add a dot below the date (set the size and color)
            DotSpan dotSpan = new DotSpan(10, color);
            view.addSpan(dotSpan);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dbManager.close();
        binding = null;
    }

    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.jakewharton.threetenabp.AndroidThreeTen.init(getContext());
        Log.d("FragmentChange", "Attendance Summary onCreate() called");
    }

}