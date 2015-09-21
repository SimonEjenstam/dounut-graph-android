package com.simonevertsson.donutgraph;

import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.simonevertsson.donutgraph.view.DonutGraph;

public class MainActivity extends AppCompatActivity {

    private DonutGraph donutGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        donutGraph = (DonutGraph) findViewById(R.id.donut);
        donutGraph.setMaxValue(500);
        donutGraph.addColoredValue(100, ContextCompat.getColor(this, R.color.donut_orange_color));
        donutGraph.addColoredValue(100, ContextCompat.getColor(this, R.color.donut_blue_color));
        donutGraph.addColoredValue(50, ContextCompat.getColor(this, R.color.donut_green_color));
        donutGraph.addColoredValue(50, ContextCompat.getColor(this, R.color.donut_turquoise_color));
        donutGraph.setAnimationDuration(1000);
        donutGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donutGraph.startAnimation();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
