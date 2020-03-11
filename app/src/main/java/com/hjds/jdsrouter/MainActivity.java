package com.hjds.jdsrouter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
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
                Object fragment=  JdsRouter.build("TestPresent").navigation();
                Log.e("fragmentfragment"," 11111111= "+fragment.getClass().getName());
                textView.setText(fragment.getClass().getName());
//                JdsRouter.build("SecondActivity").navigation();
            }
        });
    }
}
