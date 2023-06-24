package ucs.tech.taskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import ucs.tech.taskapp.Model.Data;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton fabBtn;
    private Toolbar toolbar;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    private RecyclerView recyclerView;

    private EditText titleUp,noteUp;
    private Button btnDeleteUp,btnUpdateUp;

    private String title,note,post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar=findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Task App");

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();
        final String uId=mUser.getUid();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("TaskNote").child(uId);

        mDatabase.keepSynced(true);

        //Recycler View

        recyclerView=findViewById(R.id.recycler);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        fabBtn=findViewById(R.id.fab_btn);

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder myDialog=new AlertDialog.Builder(HomeActivity.this);

                LayoutInflater inflater=LayoutInflater.from(HomeActivity.this);

                View myview=inflater.inflate(R.layout.custominputfield,null);
                myDialog.setView(myview);
                final AlertDialog dialog=myDialog.create();

                final EditText title=myview.findViewById(R.id.edt_title);
                final EditText note=myview.findViewById(R.id.edt_note);
                TimePicker alarmTimePicker = myview.findViewById(R.id.timePicker);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();

                // calendar is called to get current time in hour and minute

                Button btnsave=myview.findViewById(R.id.btn_save);

                btnsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mTitle=title.getText().toString().trim();
                        String mNote=note.getText().toString().trim();

                        if(TextUtils.isEmpty(mTitle))
                        {
                            title.setError("Required Field....");
                            return;
                        }

                        if(TextUtils.isEmpty(mNote))
                        {
                            note.setError("Required Field....");
                            return;
                        }

                        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                        calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
                        // using intent i have class AlarmReceiver class which inherits
                        // BroadcastReceiver
                        Intent intent = new Intent(HomeActivity.this, AlarmReceiver.class);

                        // we call broadcast using pendingIntent
//                        PendingIntent pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 0, intent, 0);
                        PendingIntent pendingIntent;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            pendingIntent = PendingIntent.getBroadcast(HomeActivity.this,
                                    0, intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                        }else {
                            pendingIntent = PendingIntent.getBroadcast(HomeActivity.this,
                                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        }
                        long time;
                        time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
                        if (System.currentTimeMillis() > time) {
                            // setting time as AM and PM
                            if (calendar.AM_PM == 0)
                                time = time + (1000 * 60 * 60 * 12);
                            else
                                time = time + (1000 * 60 * 60 * 24);
                        }
                        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (time), pendingIntent);
                        String id=mDatabase.push().getKey();
                        String datee= DateFormat.getDateInstance().format(new Date());
                        Data data=new Data(mTitle,mNote,datee,id);
                        mDatabase.child(id).setValue(data);
                        Toast.makeText(getApplicationContext(),"Data Inserted",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        https://firebaseui.com/docs/android/com/firebase/ui/FirebaseRecyclerViewAdapter.html
        // FirebaseRecyclerAdapter is a class provided by. FirebaseUI. it provides functions to bind, adapt and show database contents in a Recycler View.
        FirebaseRecyclerAdapter<Data,MyViewHolder>adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.item_data,
                        MyViewHolder.class,
                        mDatabase
                ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());

                viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        post_key=getRef(position).getKey();
                        title=model.getTitle();
                        note=model.getNote();

                        updateData();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View myview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview=itemView;
        }

        public void setTitle(String title){
            TextView mTitle=myview.findViewById(R.id.title);
            mTitle.setText(title);
        }

        public void setNote(String note){
            TextView mNote=myview.findViewById(R.id.note);
            mNote.setText(note);
        }

        public void setDate(String date){
            TextView mDate=myview.findViewById(R.id.date);
            mDate.setText(date);
        }

    }

    public void updateData()
    {
        AlertDialog.Builder mydialog=new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater=LayoutInflater.from(HomeActivity.this);
        View myview=inflater.inflate(R.layout.updateinputfield,null);
        mydialog.setView(myview);
        final AlertDialog dialog=mydialog.create();

        titleUp=myview.findViewById(R.id.edt_title_upd);
        noteUp=myview.findViewById(R.id.edt_note_upd);

        titleUp.setText(title);
        titleUp.setSelection(title.length());

        noteUp.setText(note);
        noteUp.setSelection(note.length());

        btnDeleteUp=myview.findViewById(R.id.btn_delete_upd);
        btnUpdateUp=myview.findViewById(R.id.btn_update_upd);

        btnUpdateUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title=titleUp.getText().toString().trim();
                note=noteUp.getText().toString().trim();

                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(title,note,mDate,post_key);

                mDatabase.child(post_key).setValue(data);
                dialog.dismiss();
            }
        });

        btnDeleteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}