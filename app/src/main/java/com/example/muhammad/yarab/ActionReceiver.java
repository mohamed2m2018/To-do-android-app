package com.example.muhammad.yarab;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static java.lang.Boolean.FALSE;

/**
 * Created by muhammad on 21/07/18.
 */

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        String action = intent.getStringExtra("action");
        String task_details = intent.getStringExtra("task_details");
        String done = intent.getStringExtra("done");
        String sender_phone = intent.getStringExtra("sender_phone");
        String date = intent.getStringExtra("date");

        if (action.equals("yes")) {

            Task task = new Task(task_details, FALSE, date, sender_phone);
            String user_phone=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            FirebaseDatabase mFirebaseDatabase=FirebaseDatabase.getInstance();
            DatabaseReference tasks_not_done = mFirebaseDatabase.getReference().child(user_phone).child(date).child("Tasks not done");
            tasks_not_done.push().setValue(task);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            Query Delete = ref.child(mAuth.getCurrentUser().getPhoneNumber()).child(MainActivity.date).child("Tasks sent").orderByChild("task_details").equalTo(task_details);

            Delete.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot deleteSnapshot: dataSnapshot.getChildren()) {
                        deleteSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent2.putExtra("Date", date);
            intent2.putExtra("phone_number",mAuth.getCurrentUser().getPhoneNumber());
            context.startActivity(intent2);
            //This is used to close the notification tray
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);

        } else if (action.equals("no")) {


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            Query Delete = ref.child(mAuth.getCurrentUser().getPhoneNumber()).child(MainActivity.date).child("Tasks sent").orderByChild("task_details").equalTo(task_details);

            Delete.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot deleteSnapshot: dataSnapshot.getChildren()) {
                        deleteSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //This is used to close the notification tray
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }


    }
}
