package com.example.muhammad.yarab;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by muhammad on 16/07/18.
 */

public class TaskAdapter extends ArrayAdapter <Task>{

    ArrayList<Task> tasks;

    Task task_done;

    ArrayList <Task> tasks2;
    DoneAdapter arrayadapter2;
    ListView list_view2;




    public TaskAdapter(Activity context, ArrayList<Task> tasks,ArrayList <Task> tasks2,DoneAdapter arrayadapter2,ListView list_view2) {
        super(context,0, tasks);
        this.tasks=tasks;
        this.tasks2=tasks2;
        this.arrayadapter2=arrayadapter2;
        this.list_view2=list_view2;
    }



    @Override
    public View getView(int position,  View convertView,ViewGroup parent) {

        View listItemView = convertView;
        //turning the xml layout into java object
        if(listItemView == null) {



                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item, parent, false);

        }


        // Get the {@link task} object located at this position in the list
        Task task = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView task_details =  listItemView.findViewById(R.id.task_details);
        // Get the task details from the current  object and
        // Get the task details from the current  object and
        // set this text on the name TextView
        task_details.setText(task.task_details);


        Button done = listItemView.findViewById(R.id.done);
        done.setTag(position);
        done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int pos = (int) view.getTag();
                task_done= tasks.get(pos);
                //delete task from tasks not done
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                Query DoneQuery = ref.child(mAuth.getCurrentUser().getPhoneNumber()).child(MainActivity.date).child("Tasks not done").orderByChild("task_details").equalTo(task_done.task_details);

                DoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot doneSnapshot: dataSnapshot.getChildren()) {
                            doneSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                    tasks.remove(pos);
                    notifyDataSetChanged();
                    tasks2.add(task_done);
                    arrayadapter2.notifyDataSetChanged();

                //add task to tasks done in database

                ref.child(mAuth.getCurrentUser().getPhoneNumber()).child(MainActivity.date)
                        .child("Tasks done").push().setValue(task_done);



                }
        });




        return listItemView;

    }
    public Task get_task_done (){
        return task_done;
    }

}
