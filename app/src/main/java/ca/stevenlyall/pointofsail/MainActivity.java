package ca.stevenlyall.pointofsail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    Button startButton, quitButton;
    TextView titleTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        Typeface pirate = Typeface.createFromAsset(getAssets(),"fonts/Treamd.ttf");
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        titleTextView.setTypeface(pirate);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setTypeface(pirate);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startSale = new Intent(getBaseContext(), SaleActivity.class);
                startActivity(startSale);
            }
        });
        quitButton = (Button) findViewById(R.id.quitButton);
        quitButton.setTypeface(pirate);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
