package com.jbapplab.tasks.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.jbapplab.tasks.interfaces.OnEditTask;

public class TaskListActivity extends AppCompatActivity implements OnEditTask {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar)); //this is where I made a change and added support for v7
    }

    //called when the user asks to edit or insert a task
    @Override
    public void editTask(long id){
        //when is asked to edit and activity start the TaskEditActivity with the id of the task to edit
        startActivity(new Intent(this, TaskEditActivity.class).putExtra(TaskEditActivity.EXTRA_TASKID, id));
    }
}
