package com.example.jekaterinaleitarte2uzd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ManaVirsma virsma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        virsma = findViewById(R.id.manaVirsma);
        SeekBar seek = findViewById(R.id.seekBar);

        TextView lbl = findViewById(R.id.seekText);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                lbl.setText(String.valueOf(progress));
                virsma.ChangeBrush(progress);
            }
        });
    }

    public void ClearVirsma(View aView){
        virsma.clearScreen();
    }

    @Override
    protected void onResume(){
        super.onResume();
        virsma.startDrawThread();
    }

    @Override
    protected void onPause(){
        virsma.startDrawThread();
        super.onPause();
    }

}