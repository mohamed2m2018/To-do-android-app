package com.example.muhammad.yarab;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.SocketImpl;
import java.text.ParseException;
import java.util.Calendar;
import java.text.SimpleDateFormat;


import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static android.app.PendingIntent.getActivity;
import static android.app.PendingIntent.readPendingIntentOrNullFromParcel;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {

    ArrayList<Task> tasks;
    TaskAdapter arrayAdapter;
    ListView list_view;

    ArrayList<Task> tasks2;
    DoneAdapter arrayAdapter2;
    ListView list_view2;

    Calendar myCalendar;

    Calendar stored_date;

    SimpleDateFormat dateFormat;

     SimpleDateFormat sdf;

    static String date;

     String phone_number;

     FirebaseDatabase mFirebaseDatabase; //entry point for app to access database
    static DatabaseReference tasks_done; //reference to portion of the database
    static DatabaseReference tasks_not_done; //reference to portion of the database
    static DatabaseReference tasks_sent; //reference to portion of the database

    static String phone_number_copy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        date = getIntent().getStringExtra("Date");
        phone_number = getIntent().getStringExtra("phone_number");


        if(date==null){
            MyDate today =new MyDate();
            TextView date_text= findViewById(R.id.date) ;
            sdf = new SimpleDateFormat("EEE MMM dd yyyy");
            try {
                Date d = sdf.parse(today.ShowDate(0));
            } catch (ParseException e) {
                e.printStackTrace();
            }// all done
             stored_date = sdf.getCalendar();
            date=today.ShowDate(0);
            date_text.append(today.ShowDate(0)+ ":");

        }else{
            TextView date_text= findViewById(R.id.date) ;
            sdf = new SimpleDateFormat("EEE MMM dd yyyy");
            try {
                Date d = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }// all done
            stored_date = sdf.getCalendar();

            date_text.setText(date+ ":");
        }
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        if(phone_number!=null) {
            phone_number_copy=phone_number;
            tasks_done = mFirebaseDatabase.getReference().child(phone_number_copy).child(date).child("Tasks done");
            tasks_not_done = mFirebaseDatabase.getReference().child(phone_number_copy).child(date).child("Tasks not done");
            tasks_sent = mFirebaseDatabase.getReference().child(phone_number_copy).child(date).child("Tasks sent");
        }else{

            tasks_done = mFirebaseDatabase.getReference().child(phone_number_copy).child(date).child("Tasks done");
            tasks_not_done = mFirebaseDatabase.getReference().child(phone_number_copy).child(date).child("Tasks not done");
            tasks_sent = mFirebaseDatabase.getReference().child(phone_number_copy).child(date).child("Tasks sent");

        }
        pick_date();

        tasks= new ArrayList<Task>(); //to be done
        tasks2=new ArrayList<Task>(); //done

        list_view = findViewById(R.id.list_view);
        list_view2=findViewById(R.id.list_view2);

        tasks_not_done.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    tasks.add(task);
                    arrayAdapter.notifyDataSetChanged();
                    arrayAdapter2.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        tasks_done.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    tasks2.add(task);
                    arrayAdapter.notifyDataSetChanged();
                    arrayAdapter2.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        tasks_sent.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                    Task task_sent=new Task();

                   for (DataSnapshot jobSnapshot: dataSnapshot.getChildren()) {
                       task_sent=jobSnapshot.getValue(Task.class);}


                       Intent intentAction = new Intent(getBaseContext(),ActionReceiver.class);

//This is optional if you have more than one buttons and want to differentiate between two
                   intentAction.putExtra("action","yes");
                   intentAction.putExtra("task_details",task_sent.task_details);
                   intentAction.putExtra("date",task_sent.date);
                   intentAction.putExtra("done",task_sent.done);
                   intentAction.putExtra("sender_phone",task_sent.sender_phone);


                   PendingIntent pIntentlogin = PendingIntent.getBroadcast(getBaseContext(),1,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);


                   Intent intentAction2 = new Intent(getApplicationContext(),ActionReceiver.class);

//This is optional if you have more than one buttons and want to differentiate between two
                   intentAction2.putExtra("action","no");
                   intentAction2.putExtra("task_details",task_sent.task_details);
                   intentAction2.putExtra("date",task_sent.date);
                   intentAction2.putExtra("done",task_sent.done);
                   intentAction2.putExtra("sender_phone",task_sent.sender_phone);

                   PendingIntent pIntentlogin2 = PendingIntent.getBroadcast(getBaseContext(),1,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);

                   for (DataSnapshot jobSnapshot: dataSnapshot.getChildren()) {


                       NotificationCompat.Builder mBuilder =
                               new NotificationCompat.Builder(getBaseContext())
                                       .setSmallIcon(R.drawable.ic_stat_name)
                                       .setContentTitle("New task from "+task_sent.sender_phone)
                                       .setContentText(task_sent.task_details)
                                        .setAutoCancel(true);


                       mBuilder
                               .addAction(R.drawable.ic_stat_name, "Yes", pIntentlogin)
                               .addAction(R.drawable.ic_stat_name, "No", pIntentlogin2);

                       mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                       NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                       notificationManager.notify(1, mBuilder.build());


                   }



               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });




        arrayAdapter2= new DoneAdapter(this,tasks2,tasks,arrayAdapter,list_view);
        arrayAdapter= new TaskAdapter(this,tasks,tasks2,arrayAdapter2,list_view2);
        arrayAdapter2.set_task_adapter(arrayAdapter); //as the arrayadapter2 took arrayadapter while it's null

        TextView textView = new TextView(this);
        textView.setText("TO BE DONE");
        textView.setTextSize(18);

        TextView textView2 = new TextView(this);
        textView2.setText(" DONE");
        textView2.setTextSize(18);

        list_view.addHeaderView(textView);
        list_view.setAdapter(arrayAdapter);

        list_view2.addHeaderView(textView2);
        list_view2.setAdapter(arrayAdapter2);

    }



    public void pick_date(){

        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date;

        Button select_date= findViewById(R.id.select_date);
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String current_date=save_selected_date();

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("Date", current_date);
                startActivity(intent);
            }

        };

        select_date.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }




    private String save_selected_date() {
        String myFormat = "MM/dd/yy";
         dateFormat = new SimpleDateFormat("EEE MMM dd yyyy");

        TextView date_text= findViewById(R.id.date) ;

        return (dateFormat.format(myCalendar.getTime()));
        //date_text.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void add_new_task(View view) {
        EditText text_input = findViewById(R.id.text_input);
        Task task=new Task(text_input.getText().toString(), FALSE,date, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        tasks.add(task);
        tasks_not_done.push().setValue(task);
        text_input.setText("");
        list_view.setAdapter(arrayAdapter);
    }


    public void tomorrow (View view){

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        stored_date.add(Calendar.DATE, 1);
        String  current_date= sdf.format(stored_date.getTime());  // dt is now the new date
        intent.putExtra("Date", current_date);
        startActivity(intent);
    }
    public void yesterday (View view){


        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        stored_date.add(Calendar.DATE, -1);
        String  current_date= sdf.format(stored_date.getTime());  // dt is now the new date
        intent.putExtra("Date", current_date);
        startActivity(intent);
    }


    public void sign_out(View view){
        Intent intent = new Intent(getBaseContext(), sign_in.class);
        intent.putExtra("call_sign_out", "S");
        startActivity(intent);
    }

    Task task_sent;
    public void add_new_task_for_others(View view) {

        // user BoD suggests using Intent.ACTION_PICK instead of .ACTION_GET_CONTENT to avoid the chooser
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // BoD con't: CONTENT_TYPE instead of CONTENT_ITEM_TYPE
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        //to start activity and recieve the result back
        startActivityForResult(intent, 1);

        EditText text_input = findViewById(R.id.text_input);
        task_sent=new Task(text_input.getText().toString(), FALSE,date, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());


    }

    String contact_number;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = getContentResolver().query(uri, new String[]{
                                    ContactsContract.CommonDataKinds.Phone.NUMBER},
                            null, null, null);

                    if (c != null && c.moveToFirst()) {
                        contact_number = "+2" + c.getString(0).replaceAll("\\s","");;
                        send_tasks(task_sent);

                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }


    public  void send_tasks(Task task){
        DatabaseReference tasks_sent = mFirebaseDatabase.getReference().child(contact_number).child(date).child("Tasks sent");
        tasks_sent.push().setValue(task);
        Toast.makeText(getBaseContext(), "your message has been sent :D",
                Toast.LENGTH_LONG).show();

    }

}
