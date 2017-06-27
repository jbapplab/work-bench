package com.jbapplab.tasks.activity;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.jbapplab.tasks.fragment.TaskEditFragment;
import com.jbapplab.tasks.interfaces.OnEditFinished;

public class TaskEditActivity extends AppCompatActivity implements OnEditFinished {
    //called when user finishes editing a task
    @Override
    public void finishEditingTask(){
        //when user diminishes the editor, call finish to destroy this activity
        finish();
    }

    public static final String EXTRA_TASKID = "taskId";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar)); //again the support here for compatibility

        long id = getIntent().getLongExtra(TaskEditActivity.EXTRA_TASKID,0L);

        Fragment fragment = TaskEditFragment.newInstance(id);

        String fragmentTag = TaskEditFragment.DEFAULT_FRAGMENT_TAG;

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add( //added the Support at the fragment manager for compatibility
                    R.id.container,
                    fragment,
                    fragmentTag).commit();
    }
}
