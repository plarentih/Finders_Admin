package com.admin.finders.app.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import com.admin.finders.R;
import com.admin.finders.app.app.adapter.ReportAdapter;
import com.admin.finders.app.app.model.Report;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReportListActivity extends AppCompatActivity {

    private ListView reportListView;
    private ReportAdapter reportAdapter;
    private TextView sortTextView;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private List<Report> reportList;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        reportListView = findViewById(R.id.reportListView);
        sortTextView = findViewById(R.id.textViewListTitle);

        preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = preferences.edit();

        centerTitle();
        String name = preferences.getString("NAME", "Name couldn't be retrieved");
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();


        reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Report report = reportList.get(i);
                Intent intent = new Intent(ReportListActivity.this, ReportDetailActivity.class);
                intent.putExtra("REPORT", report);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reportList = new ArrayList<>();
        getDataFromServer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.open, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            editor.putString("NAME", "Empty");
            editor.apply();
            Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_zip){
            Collections.sort(reportList, new Comparator<Report>() {
                @Override
                public int compare(Report o1, Report o2) {
                    if(o1.getZipCode() < o2.getZipCode()){
                        return -1;
                    } else if (o2.getZipCode() < o1.getZipCode()){
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            sortTextView.setText("Sorting by ZIP code");
            reportAdapter = new ReportAdapter(getBaseContext(), reportList);
            reportListView.setAdapter(reportAdapter);
        }else if(id == R.id.action_locality){
            if(reportList.size() > 0){
                Collections.sort(reportList, new Comparator<Report>() {
                    @Override
                    public int compare(Report o1, Report o2) {
                        return o1.getLocality().compareTo(o2.getLocality());
                    }
                });
                sortTextView.setText("Sorting by locality");
                reportAdapter = new ReportAdapter(getBaseContext(), reportList);
                reportListView.setAdapter(reportAdapter);
            }
        }else if(id == R.id.action_priority){
            Collections.sort(reportList, new Comparator<Report>() {
                @Override
                public int compare(Report o1, Report o2) {
                    if(o1.getPriority() > o2.getPriority()){
                        return -1;
                    } else if (o2.getPriority() > o1.getPriority()){
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            sortTextView.setText("Sorting by priority (High to Low)");
            reportAdapter = new ReportAdapter(getBaseContext(), reportList);
            reportListView.setAdapter(reportAdapter);
        }else if(id == R.id.action_type){
            if(reportList.size() > 0){
                Collections.sort(reportList, new Comparator<Report>() {
                    @Override
                    public int compare(Report o1, Report o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                });
                sortTextView.setText("Sorting by type of damage");
                reportAdapter = new ReportAdapter(getBaseContext(), reportList);
                reportListView.setAdapter(reportAdapter);
            }
        } else if(id == R.id.action_status){
            if(reportList.size() > 0){
                Collections.sort(reportList, new Comparator<Report>() {
                    @Override
                    public int compare(Report o1, Report o2) {
                        return o1.getStatus().compareTo(o2.getStatus());
                    }
                });
                sortTextView.setText("Sorting by status (Fixed to Not fixed)");
                reportAdapter = new ReportAdapter(getBaseContext(), reportList);
                reportListView.setAdapter(reportAdapter);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //What a nice name
    private void getDataFromServer(){
        databaseReference.child("reports").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child : children){
                    Report event = child.getValue(Report.class);
                    reportList.add(event);
                }
                reportAdapter = new ReportAdapter(getBaseContext(), reportList);
                reportListView.setAdapter(reportAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
