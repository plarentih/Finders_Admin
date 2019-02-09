package com.admin.finders.app.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.admin.finders.R;

public class AuthenticationActivity extends AppCompatActivity {

    private EditText nameEditTxt;
    private EditText codeEditTxt;
    private Button signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        initializeWidgets();

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditTxt.getText().toString();
                String code = codeEditTxt.getText().toString();
                if(code.equalsIgnoreCase("987654")){
                    SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("NAME", name);
                    editor.apply();
                    Intent intent = new Intent(AuthenticationActivity.this, ReportListActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "The code you entered is not valid!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeWidgets(){
        nameEditTxt = findViewById(R.id.field_name);
        codeEditTxt = findViewById(R.id.field_verification_code);
        signInBtn = findViewById(R.id.button_start_verification);
    }
}
