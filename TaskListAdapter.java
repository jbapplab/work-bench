package com.jbapplab.tasks.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jbapplab.tasks.activity.R;
import com.jbapplab.tasks.interfaces.OnEditTask;
import com.squareup.picasso.Picasso;

/**
 * Created by JohnB on 20/06/2017.
 */

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder>
{
    static String[] fakeData = new String[]{
        "One",
        "Two",
        "Three",
        "Four",
        "Five",
        "Ah... ah... ah!"
        };

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i){
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_task, parent, false);

            return new ViewHolder(v);
        }
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i){ //final ViewHolder because is being accessed from within an inner class
        final Context context = viewHolder.titleView.getContext(); //context is the current activity
        viewHolder.titleView.setText(fakeData[i]);

        Picasso.with(context) //with instructs which context to use to download the image
                .load(getImageUrlForTask(i))
                .into(viewHolder.imageView);

        //set the click action
        viewHolder.cardView.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        ((OnEditTask) context).editTask(i);
                    }
                });

        //set the long press action for the Alert Dialogue Delete mode
        viewHolder.cardView.setOnLongClickListener(
                new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View view){
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.delete_q)
                                .setMessage(viewHolder.titleView.getText())
                                .setCancelable(true) //set to false to disable the back button in dialogs
                                .setNegativeButton(android.R.string.cancel, null)
                                .setPositiveButton(
                                        R.string.delete, new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialogInterface, int i)
                                            {
                                                deleteTask(context, i);
                                            }
                                         }
                                ).show(); //call show to display the dialog
                        return true;
                    }
                }
        );
    }

    @Override
    public int getItemCount(){
        return fakeData.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleView;
        ImageView imageView;
        public ViewHolder(CardView card) {
            super(card);
            cardView = card;
            titleView = (TextView)card.findViewById(R.id.text1);
            imageView = (ImageView)card.findViewById(R.id.image);
        }
    }

    public static String getImageUrlForTask(long taskID){
        //return "http://lorempixel.com/600/400/cats/?fakeID="+taskID;
        return "http://xiostorage.com/wp-content/uploads/2015/10/test.png";
    }

    //For now this is not supposed to do anything else
    void deleteTask(Context context, long id){
        Log.d("TaskListAdapter", "Called deleteTask");
    }
}
