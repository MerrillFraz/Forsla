package org.thebentfork.forsla.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.thebentfork.forsla.R;
import org.thebentfork.forsla.background.ItemClickSupport;
import org.thebentfork.forsla.background.SQLiteHandler;
import org.thebentfork.forsla.background.SessionManager;
import org.thebentfork.forsla.background.SimpleCursorRecyclerAdapter;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // This setting improves performance if changes in content do not
        // change the size of the layout.
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Create the list!
        final Cursor cursor = db.GetRouteData();
        String from [] = new String[]{db.ROUTES_COLUMN_ID, db.ROUTES_COLUMN_CLIENT, db.ROUTES_COLUMN_DATETIME};
        int to [] = new int[]{R.id.routeView1};
        int layout = R.id.my_recycler_view;
        RecyclerView.Adapter mAdapter = new SimpleCursorRecyclerAdapter(layout, cursor, from, to);
        mRecyclerView.setAdapter(mAdapter);

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter odometer miles");

                // Set up the input
                final EditText input = new EditText(MainActivity.this);

                // Specify the type of input expected
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        int odoValue = Integer.parseInt(m_Text);
                        int route_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(db.ROUTES_COLUMN_ID)));
                        db.OdometerStart(route_id, odoValue);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        //TODO: Am I going to use this FAB?
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void logoutUser(MenuItem item) {
        SessionManager session = new SessionManager(getApplicationContext());
        session.logoutUser();
    }
}
