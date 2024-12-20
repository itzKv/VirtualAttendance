package com.dnk.virtualattendance.ui.attendancesummary;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dnk.virtualattendance.R;
import com.dnk.virtualattendance.model.AttendanceModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_NO_DATA = 0;
    private static final int TYPE_ATTENDANCE = 1;

    private List<AttendanceModel> attendanceList;
    private String selectedDate;
    private Context context;

    public AttendanceAdapter(Context context, List<AttendanceModel> attendanceList) {
        this.context = context;
        this.attendanceList = attendanceList;
    }

    @Override
    public int getItemViewType(int position) {
        return (attendanceList == null || attendanceList.size() == 0) ? TYPE_NO_DATA : TYPE_ATTENDANCE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("RecyclerViewType", Integer.toString(viewType));
        if (viewType == TYPE_NO_DATA) {
            View view = LayoutInflater.from(context).inflate(R.layout.no_attendance_item, parent, false);
            return new NoDataViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.attendance_item, parent, false);
            return new AttendanceViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NO_DATA) {
            NoDataViewHolder noDataHolder = (NoDataViewHolder) holder;
            // Bind the no data layout
            noDataHolder.selectedDateTV.setText("");
        } else {
            AttendanceViewHolder attendanceHolder = (AttendanceViewHolder) holder;
            AttendanceModel attendance = attendanceList.get(position);

            // Bind the data to the views
            String dateString = attendance.getDate();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault()); // e.g., "Dec"
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

            try {
                Date date = inputFormat.parse(dateString);
                // Extract day, month, and year
                String day = dayFormat.format(date);
                String month = monthFormat.format(date);
                String year = yearFormat.format(date);

                // Bind the parsed data to the views
                attendanceHolder.dateTV.setText(day);
                attendanceHolder.monthTV.setText(month);
                attendanceHolder.yearTV.setText(year);
            } catch (ParseException e) {
                e.printStackTrace();
                // Handle parsing error, optionally set default values
                attendanceHolder.dateTV.setText("N/A");
                attendanceHolder.monthTV.setText("N/A");
                attendanceHolder.yearTV.setText("N/A");
            }

            attendanceHolder.checkinTV.setText("Check-in: " + attendance.getCheckin());
            attendanceHolder.checkoutTV.setText("Check-out: " + attendance.getCheckout());
            attendanceHolder.statusTV.setText(attendance.getIsAttended() == 1 ? "Attended" : "Absent");
            attendanceHolder.statusTV.setTextColor(holder.itemView.getContext().getColor(
                    attendance.getIsAttended() == 1 ?
                            R.color.status_attended :
                            R.color.status_absent
            ));
        }
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    // ViewHolder class to hold the views for each item
    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView dateTV, monthTV, yearTV, checkinTV, checkoutTV, statusTV, salaryTV;

        public AttendanceViewHolder(View itemView) {
            super(itemView);
            // Find the views in the item layout
            dateTV = itemView.findViewById(R.id.dateTV);
            monthTV = itemView.findViewById(R.id.monthTV);
            yearTV = itemView.findViewById(R.id.yearTV);
            checkinTV = itemView.findViewById(R.id.checkinTV);
            checkoutTV = itemView.findViewById(R.id.checkoutTV);
            statusTV = itemView.findViewById(R.id.statusTV);
        }
    }

    // Add a new ViewHolder for the no-data state
    public static class NoDataViewHolder extends RecyclerView.ViewHolder {
        TextView selectedDateTV;

        public NoDataViewHolder(View itemView) {
            super(itemView);
            selectedDateTV = itemView.findViewById(R.id.selectedDateTV);
        }
    }

    public void updateData(List<AttendanceModel> newAttendanceList) {
        Log.d("UpdateData", "Updated Here");
        // If no data, pass an empty list to show no data state
        this.attendanceList = newAttendanceList;
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }
}