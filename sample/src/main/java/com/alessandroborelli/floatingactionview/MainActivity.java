package com.alessandroborelli.floatingactionview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.alessandroborelli.design.FloatingActionView;

public class MainActivity extends AppCompatActivity implements FloatingActionView.OnFloatingActionViewListener {

    private FloatingActionView mFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFav = (FloatingActionView) findViewById(R.id.fav);
        mFav.setOnFloatingActionViewListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFav.open();
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

    @Override
    public void onBackPressed() {
        // Close the Fav when you press back
        if (mFav.isOpened()) {
            mFav.close();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * OnFloatingActionViewListener
     */

    @Override
    public void onOpen() {
        //Toast.makeText(this, "open", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClose() {
        //Toast.makeText(this, "close", Toast.LENGTH_SHORT).show();
    }
}
