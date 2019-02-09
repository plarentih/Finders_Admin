package com.admin.finders.app.app.activities;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.admin.finders.R;
import com.admin.finders.app.app.model.Report;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportDetailActivity extends AppCompatActivity {

    private TextView textViewTitle, textViewDescription, textViewAddress;
    private ImageView imgViewEvent;
    private Report report;
    private Switch aSwitch;
    private RatingBar ratingBar;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private String name, address;
    private Geocoder geocoder;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);
        initializeWidgets();
        geocoder = new Geocoder(this, Locale.getDefault());
        preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = preferences.edit();

        setInformation();
        toggleSwitch();
        name = preferences.getString("NAME", "Name couldn't be retrieved");

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("reports/");
        centerTitle();

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mFirebaseDatabase.child(report.getKey()).child("status").setValue("FIXED");
                    mFirebaseDatabase.child(report.getKey()).child("person_reponsible").setValue(name);
                    report.setStatus("FIXED");
                    aSwitch.setText("Fixed");
                }else {
                    mFirebaseDatabase.child(report.getKey()).child("status").setValue("NOT_FIXED");
                    mFirebaseDatabase.child(report.getKey()).child("person_reponsible").setValue(name);
                    report.setStatus("NOT_FIXED");
                    aSwitch.setText("Not Fixed");
                }
            }
        });
    }

    private void setInformation(){
        report = (Report) getIntent().getSerializableExtra("REPORT");

        try {
            addresses = geocoder.getFromLocation(report.getLatitude(), report.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        textViewTitle.setText(report.getTitle());
        textViewDescription.setText(report.getDescription());
        textViewAddress.setText(address);
        Glide.with(getApplicationContext()).load(report.getUrlPhoto()).into(imgViewEvent);
        ratingBar.setRating((int)report.getPriority());
    }

    private void toggleSwitch(){
        report = (Report) getIntent().getSerializableExtra("REPORT");
        if(report.getStatus().equalsIgnoreCase("NOT_FIXED")){
            aSwitch.setChecked(false);
            aSwitch.setText("Not Fixed");
        }else {
            aSwitch.setChecked(true);
            aSwitch.setText("Fixed");
        }
    }

    private void initializeWidgets(){
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDescription = findViewById(R.id.textViewDesc);
        imgViewEvent = findViewById(R.id.imageView);
        aSwitch = findViewById(R.id.switch1);
        textViewAddress = findViewById(R.id.address_report);
        ratingBar = findViewById(R.id.ratingBar);
    }

    private void centerTitle() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }
}
