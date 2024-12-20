package com.dnk.virtualattendance.ui.attendancemachine;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dnk.virtualattendance.BuildConfig;
import com.dnk.virtualattendance.HomeActivity;
import com.dnk.virtualattendance.database.DBManager;
import com.dnk.virtualattendance.databinding.FragmentAttendanceMachineBinding;
import com.dnk.virtualattendance.model.AttendanceModel;
import com.dnk.virtualattendance.model.RoleModel;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AttendanceMachineFragment extends Fragment {
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private FragmentAttendanceMachineBinding binding;
    private TextView timeCounterTextView;
    private Handler handler = new Handler();
    private Runnable timeUpdater;
    private RoleModel userRole;
    private ExecutorService executorService;
    private TextView atmachineStatusTV;
    private DBManager dbManager;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission granted, retry accessing location
                checkCurrentLocation(new LocationCallback() {
                    @Override
                    public void onLocationChecked(boolean isLocationValid) {
                        if (isLocationValid) {
                            Log.d("AttendanceMachineFragment", "Location is valid");
                        } else {
                            Log.d("AttendanceMachineFragment", "Location is not valid");
                        }
                    }
                });
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(getActivity(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        });


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AttendanceMachineViewModel attendanceMachineViewModel =
                new ViewModelProvider(this).get(AttendanceMachineViewModel.class);

        binding = FragmentAttendanceMachineBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        HomeActivity activity = (HomeActivity) getActivity();
        if (activity != null) {
            userRole = activity.getUserRole();
            Log.d("UserRoleFragment", "User Role: " + userRole.getRoleName());
        }

        timeCounterTextView = binding.atmachineTimeCounterTV; // Link to your TextView in XML
        startRealTimeCounter();

        TextView atmachineWorkingLocationTV = binding.atmachineWorkingLocationTV;
        atmachineWorkingLocationTV.setText(userRole.getWorkingLocation());

        TextView atmachineWorkingTimeTV = binding.atmachineWorkingTimeTV;
        String workingTime = userRole.getWorkingStartTime() + " - " + userRole.getWorkingEndTime();
        atmachineWorkingTimeTV.setText(workingTime);

        atmachineStatusTV = binding.atmachineStatusTV;
        ImageButton attendanceBtn = binding.atmachineAttendanceBtn;
        attendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AttendanceMachineFragment", "Attendance button clicked");
                atmachineStatusTV.setText("Status: Verifying Fingerprint...");
                checkFingerprint(new FingerprintCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("AttendanceMachineFragment", "Fingerprint is valid");
                        Toast.makeText(getContext(), "Fingerprint is valid", Toast.LENGTH_SHORT).show();
                        atmachineStatusTV.setText("Status: Verifying Location...");
                        checkCurrentLocation(new LocationCallback() {
                            @Override
                            public void onLocationChecked(boolean isLocationValid) {
                                if (isLocationValid) {
                                    Log.d("AttendanceMachineFragment", "Location is valid");
                                    Toast.makeText(getContext(), "Location is valid", Toast.LENGTH_SHORT).show();
                                    performAttendance();
                                } else {
                                    Log.d("AttendanceMachineFragment", "Location is not valid");
                                    Toast.makeText(getContext(), "Location is not valid", Toast.LENGTH_SHORT).show();
                                    atmachineStatusTV.setText("Status: Location Verification failed...");
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        Log.d("AttendanceMachineFragment", "Fingerprint Authentication failed");
                        Toast.makeText(getContext(), "Fingerprint Authentication failed", Toast.LENGTH_SHORT).show();
                        atmachineStatusTV.setText("Status: Fingerprint Authentication failed...");
                    }

                    @Override
                    public void onError(CharSequence error) {
                        Log.d("AttendanceMachineFragment", "Fingerprint Authentication error: " + error);
                        Toast.makeText(getContext(), "Fingerprint Authentication error: " + error, Toast.LENGTH_SHORT).show();
                        atmachineStatusTV.setText("Status: Fingerprint Authentication Error...");
                    }
                });
            }
        });

        return root;
    }

    // Overrides Method
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopRealTimeCounter();
        binding = null;
    }
    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        executorService = Executors.newSingleThreadExecutor();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (executorService != null) {
            executorService.shutdown();
        }
    }

    // Primary Function
    private void startRealTimeCounter() {
        timeUpdater = new Runnable() {
            @Override
            public void run() {
                // Get the current time
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis());
                timeCounterTextView.setText(currentTime); // Display the time in the TextView

                // Schedule the next update in 1 second
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(timeUpdater); // Start the Runnable
    }
    private void stopRealTimeCounter() {
        handler.removeCallbacks(timeUpdater); // Stop updates
    }
    public void checkFingerprint(final FingerprintCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    BiometricPrompt biometricPrompt = new BiometricPrompt(getActivity(),
                            ContextCompat.getMainExecutor(getContext()), new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            getActivity().runOnUiThread(() -> callback.onSuccess());
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            getActivity().runOnUiThread(() -> callback.onFailure());
                        }

                        @Override
                        public void onAuthenticationError(int errorCode, CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            getActivity().runOnUiThread(() -> callback.onError(errString));
                        }
                    });

                    BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Fingerprint Authentication")
                            .setSubtitle("Authenticate using your fingerprint")
                            .setNegativeButtonText("Cancel")
                            .build();

                    biometricPrompt.authenticate(promptInfo);
                }
            });
        } else {
            Toast.makeText(getContext(), "Biometric authentication is not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }
    private void checkCurrentLocation(final LocationCallback callback) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                try {
                    // Meminta lokasi hanya sekali
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            // Mengecek lokasi apakah menggunakan mock location
                            if (isMockLocation(location)) {
                                // Jika lokasi palsu
                                Toast.makeText(getActivity(), "Fake GPS detected!", Toast.LENGTH_SHORT).show();
                                callback.onLocationChecked(false);  // Mengirim hasil false
                            } else {
                                // Lokasi valid
                                Toast.makeText(getActivity(), "Current Location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                                // Mengecek apakah lokasi dalam range yang diinginkan
                                boolean result = checkLocationRange(location, userRole.getWorkingLocation(), 1000);  // Cek range 1000 meter
                                Log.d("LocationUtils", "Location check result: " + result);

                                callback.onLocationChecked(result);  // Mengirim hasil validasi
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {}

                        @Override
                        public void onProviderEnabled(@NonNull String provider) {}

                        @Override
                        public void onProviderDisabled(@NonNull String provider) {}
                    }, null);

                } catch (SecurityException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Location permission is required", Toast.LENGTH_SHORT).show();
                    callback.onLocationChecked(false);  // Jika ada masalah dengan permission
                }
            }
        } else {
            // Jika permission belum diberikan, meminta permission terlebih dahulu
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            callback.onLocationChecked(false);  // Hasil false sampai permission diberikan
        }
    }
    private boolean isMockLocation(Location location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return location.isMock();
        }
        return false;
    }
    private boolean checkLocationRange(Location userLocation, String targetAddress, double rangeInMeters){
        try {
            LatLng targetCoordinates = getCoordinatesFromAddress(targetAddress);
            if (targetCoordinates != null) {
                float[] results = new float[1];
                Location.distanceBetween(
                        userLocation.getLatitude(),
                        userLocation.getLongitude(),
                        targetCoordinates.latitude,
                        targetCoordinates.longitude,
                        results
                );
                Log.d("LocationUtils", "Distance between locations: " + results[0] + " meters");
                return results[0] <= rangeInMeters;
            }
        } catch (Exception e) {
            Log.e("LocationUtils", "Error checking location range", e);
        }
        return false;
    }
    private LatLng getCoordinatesFromAddress(String address) {
        final LatLng[] result = {null};
        new Thread(() -> {
            try {
                // Construct the Geocoding API URL
                String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                        address.replace(" ", "%20") + "&key=" + BuildConfig.MAPS_API_KEY;
                URL url = new URL(urlString);

                // Open connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Read response
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject location = jsonResponse.getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location");

                double latitude = location.getDouble("lat");
                double longitude = location.getDouble("lng");

                result[0] = new LatLng(latitude, longitude);
            } catch (Exception e) {
                Log.e("LocationUtils", "Error getting coordinates", e);
            }
        }).start();

        // Wait for background thread to finish and return result
        while (result[0] == null) {
            Log.d("LocationUtils", "Waiting for coordinates...");
        }
        Log.d("LocationUtils", "Coordinates received: " + result[0]);
        return result[0];
    }
    public void performAttendance() {
        if (isWeekdayOrSaturday()) {
            checkAttendanceRecord(new AttendanceCheckCallback() {
                @Override
                public void onAttendanceChecked(boolean isAttendanceRecordExists, AttendanceModel record) {
                    if (!isAttendanceRecordExists) {
                        insertAttendStartTime();
                    } else {
                        if (record.getAttendStartTime() == null) {
                            insertAttendStartTime();
                        } else if (record.getAttendCloseTime() == null) {
                            insertAttendCloseTime(record);
                        } else {
                            Log.d("AttendanceMachineFragment", "Today has already attended");
                            Toast.makeText(getContext(), "Today has already attended", Toast.LENGTH_SHORT).show();
                            atmachineStatusTV.setText("Status: Today has already attended");
                        }
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Can't attend in weekend", Toast.LENGTH_SHORT).show();
            atmachineStatusTV.setText("Can't attend in weekend");
        }
    }
    private void checkAttendanceRecord(AttendanceCheckCallback callback) {
        String todayDate = getCurrentDate();
        int userId = userRole.getId();

        dbManager = new DBManager(getContext());
        dbManager.open();
        AttendanceModel record = dbManager.getAttendanceRecord(userId, todayDate);
        dbManager.close();

        if (record != null) {
            callback.onAttendanceChecked(true, record);
        } else {
            callback.onAttendanceChecked(false, null);
        }
    }
    private void insertAttendStartTime() {
        String currentTime = getCurrentTime();
        int userId = userRole.getId();
        String todayDate = getCurrentDate();

        if (isWithinSpareTime(currentTime, userRole.getWorkingStartTime(), userRole.getWorkingSpareTime())) {

            dbManager = new DBManager(getContext());
            dbManager.open();
            dbManager.startAttendanceRecord(new AttendanceModel(userId, todayDate, currentTime, null));
            dbManager.close();
            Log.d("AttendanceMachineFragment", "Attendance Started");
            Toast.makeText(getContext(), "Attendance Started", Toast.LENGTH_SHORT).show();
            atmachineStatusTV.setText("Status: Attendance Started");
        } else {
            Toast.makeText(getContext(), "Can't start attend in this time", Toast.LENGTH_SHORT).show();
            atmachineStatusTV.setText("Status: Can't start attend in this time");
        }
    }
    private void insertAttendCloseTime(AttendanceModel record) {
        String currentTime = getCurrentTime();
        int userId = userRole.getId();
        String todayDate = getCurrentDate();

        if (isWithinSpareTime(currentTime, userRole.getWorkingEndTime(), userRole.getWorkingSpareTime())) {

            dbManager = new DBManager(getContext());
            dbManager.open();
            dbManager.closeAttendanceRecord(new AttendanceModel(userId, todayDate, record.getAttendStartTime(), currentTime));
            dbManager.close();
            Log.d("AttendanceMachineFragment", "Attendance Closed");
            Toast.makeText(getContext(), "Attendance Closed", Toast.LENGTH_SHORT).show();
            atmachineStatusTV.setText("Status: Attendance Closed");
        } else {
            Toast.makeText(getContext(), "Can't close attend in this time", Toast.LENGTH_SHORT).show();
            atmachineStatusTV.setText("Status: Can't close attend in this time");
        }
    }
    private boolean isWeekdayOrSaturday() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek != Calendar.SUNDAY;
    }
    private boolean isWithinSpareTime(String currentTime, String attendTime, String spareTime) {
        int currentMinute = convertToMinutes(currentTime);
        int attendMinute = convertToMinutes(attendTime);
        int spareMinute = convertToMinutes(spareTime);
        return Math.abs(currentMinute - attendMinute) <= spareMinute;
    }
    public int convertToMinutes(String time) {
        // Parse the time string into LocalTime
        LocalTime parsedTime = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            parsedTime = LocalTime.parse(time);
            return parsedTime.getHour() * 60 + parsedTime.getMinute();
        }
        return 0;
    }
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
    private String getCurrentDate() {
        // Mendapatkan tanggal hari ini dalam format yyyy-MM-dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Interfaces
    public interface FingerprintCallback {
        void onSuccess();
        void onFailure();
        void onError(CharSequence error);
    }
    public interface LocationCallback {
        void onLocationChecked(boolean isLocationValid);
    }
    public interface AttendanceCheckCallback {
        void onAttendanceChecked(boolean isAttendanceRecordExists, AttendanceModel record);
    }
}