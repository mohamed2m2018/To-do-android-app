package com.example.muhammad.yarab;

/**
 * Created by muhammad on 16/07/18.
 */

public class Task {

   public String task_details;
   public boolean done ;
   public String date;
   public String sender_phone;

    public Task(String task_details,boolean done,String date,String sender_phone){

        this.task_details=task_details;
        this.done=done;
        this.date=date;
        this.sender_phone=sender_phone;

    }

    public Task(){

        this.done=false;
        this.task_details="";
    }

}
