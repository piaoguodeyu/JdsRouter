package com.hjds.jdsrouter;

import android.os.Bundle;
import android.widget.TextView;

import com.hjds.jrouterannotation.Router;

import androidx.appcompat.app.AppCompatActivity;
@Router(path = "ThirdActivity")
public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.tttttt);
        textView.setText(this.getClass().getName());
    }
}
