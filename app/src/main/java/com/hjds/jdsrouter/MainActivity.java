package com.hjds.jdsrouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hjds.hjdsrouterlib.util.JdsRouter;
import com.hjds.jrouterannotation.JRouter;

@JRouter(path = "MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.tttttt);
        textView.setText(this.getClass().getName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JdsRouter.build("SecondActivity").navigation();
            }
        });
    }
}
