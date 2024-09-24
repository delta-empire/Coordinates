package ru.sergeipavlov.coordinates;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvLattitude;
    private TextView tvLongitude;
    private TextView tvHeigt;
    private TextView tvAddress;

    private static final DecimalFormat REAL_FORMATTER = new DecimalFormat("0.###");

    String addressString = "No address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvLattitude = findViewById(R.id.tvLattitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvHeigt = findViewById(R.id.tvHeight);
        tvAddress = findViewById(R.id.tvAddress);

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double lattitude, longitude;
                    float height;

                    lattitude = location.getLatitude();
                    longitude = location.getLongitude();
                    height = location.getVerticalAccuracyMeters();

                    tvLattitude.setText(REAL_FORMATTER.format(lattitude));
                    tvLongitude.setText(REAL_FORMATTER.format(longitude));
                    tvHeigt.setText(REAL_FORMATTER.format(height));

                    Geocoder gc = new Geocoder(MainActivity.this, Locale.getDefault());

                    try {
                        List<Address> adresses = gc.getFromLocation(lattitude, longitude, 1);
                        StringBuilder sb = new StringBuilder();
                        if (adresses.size() > 0) {
                            Address address = adresses.get(0);
                            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                sb.append(address.getAddressLine(i)).append("\n");
                            }

                            sb.append(address.getCountryName()).append("\n");
                            sb.append(address.getPostalCode()).append("\n");
                            sb.append(address.getLocality()).append("\n");
                        }
                            addressString = sb.toString();
                            tvAddress.setText(sb.toString());

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });
    }
}