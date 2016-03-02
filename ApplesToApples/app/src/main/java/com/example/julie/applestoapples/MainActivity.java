package com.example.julie.applestoapples;
import java.util.List;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Game mGame = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        final Intent main = getIntent();
        Player player = main.getExtras().getParcelable("player");

        this.mGame = new Game(player);

        final GridView grid = (GridView) findViewById(R.id.gridView);
        grid.setAdapter(new ButtonAdapter(mGame.mPlayer.mCards, this));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, position, Toast.LENGTH_SHORT).show();
                Log.i("MA", "Click "+((Card) grid.getAdapter().getItem(position)).mName);
                if(mGame.mIfJudge) {
                    boolean success = mGame.mPlayer.selectCard((Card) grid.getAdapter().getItem(position));
                    Log.i("MA", "select:" + ((Card) grid.getAdapter().getItem(position)).mName + ", ret: "+ success);
                }
                else {
                    boolean success = mGame.mPlayer.submitCard((Card)grid.getAdapter().getItem(position));
                    Log.i("MA", "submit:" + ((Card) grid.getAdapter().getItem(position)).mName + ", ret: " + success);
                }
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


