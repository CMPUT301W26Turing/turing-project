package com.example.turing_eventlottery;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class CreateEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_event);

        ImageView closeButton = findViewById(R.id.closeButton);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> finish());
        }
    }
}
