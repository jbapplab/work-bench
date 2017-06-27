package com.jbapplab.tasks.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import java.text.DateFormat;
import java.util.Calendar; //android.icu. changed to java. to support pre 24API

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jbapplab.tasks.activity.R;
import com.jbapplab.tasks.activity.TaskEditActivity;
import com.jbapplab.tasks.adapter.TaskListAdapter;
import com.jbapplab.tasks.interfaces.OnEditFinished;
import com.jbapplab.tasks.provider.TaskProvider;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class TaskEditFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    static final String TASK_ID = "taskId";
    static final String TASK_DATE_AND_TIME = "taskDateAndTime";

    public static final String DEFAULT_FRAGMENT_TAG = "taskEditFragment";

    // views
    View rootView;
    EditText titleText;
    EditText notesText;
    ImageView imageView;
    TextView dateButton;
    TextView timeButton;

    //some info about the task that we store here before the database
    long taskId;
    Calendar taskDateAndTime;

    /* Attempt to invoke virtual method 'java.io.Serializable android.os.Bundle.getSerializable(java.lang.String)' on a null object reference
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if we are restoring state from previous activity restore date as well
        Bundle arguments = getArguments();
        if (arguments != null){
            taskId = arguments.getLong(TaskEditActivity.EXTRA_TASKID, 0L);
            taskDateAndTime = (Calendar) savedInstanceState.getSerializable(TASK_DATE_AND_TIME);
        }

        //if we didn't have a previous date, use "now"
        if (taskDateAndTime == null){
            taskDateAndTime = Calendar.getInstance();
        }

        if (savedInstanceState != null){
            taskId = savedInstanceState.getLong(TASK_ID);
        }
    }
    */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if we are restoring state from previous activity restore date as well
        if (savedInstanceState != null){
            taskId = savedInstanceState.getLong(TaskEditActivity.EXTRA_TASKID, 0L);
            taskDateAndTime = (Calendar) savedInstanceState.getSerializable(TASK_DATE_AND_TIME);
        }

        //if we didn't have a previous date, use "now"
        if (taskDateAndTime == null){
            taskDateAndTime = Calendar.getInstance();
        }

        if (savedInstanceState != null){
            taskId = savedInstanceState.getLong(TASK_ID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_task_edit,container, false);
        rootView = v.getRootView();
        titleText = (EditText) v.findViewById(R.id.title);
        notesText = (EditText) v.findViewById(R.id.notes);
        imageView = (ImageView) v.findViewById(R.id.image);
        dateButton = (TextView) v.findViewById(R.id.task_date);
        timeButton = (TextView) v.findViewById(R.id.task_time);

        //set thumbnail picture
        Picasso.with(getActivity())
                .load(TaskListAdapter.getImageUrlForTask(taskId))
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Activity activity = getActivity();

                        if (activity == null)
                            return;

                        //set the colours of the activity based on the colours of the image if available.
                        Bitmap bitmap = ((BitmapDrawable) imageView
                        .getDrawable())
                                .getBitmap();

                        Palette palette = Palette.generate(bitmap, 32);
                        int bgColor = palette.getLightMutedColor(0);

                        if (bgColor != 0){
                            rootView.setBackgroundColor(bgColor);
                        }
                    }

                    @Override
                    public void onError() {
                        //do nothing use default colours!
                    }
                });

        updateDateAndTimeButtons();

        //tell the date and time buttons what to do when we click them
        dateButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        showDatePicker();
                    }
                });

        timeButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        showTimePicker();
                    }
                });

        return v;
    }
    //helper method to show our Date picker
    private void showDatePicker(){
        //create fragment transaction
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DatePickerDialogFragment newFragment = DatePickerDialogFragment.newInstance(taskDateAndTime);
        newFragment.show(ft, "datePicker");
    }

    private void showTimePicker(){
        //create fragment transaction
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        TimePickerDialogFragment fragment = TimePickerDialogFragment.newInstance(taskDateAndTime);
        fragment.show(ft, "timePicker");
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        //this may have changed while the activity is running so make sure to save it to the outState bundle so we can restore it onCreate
        outState.putLong(TASK_ID, taskId);
        //outState.putSerializable(TASK_DATE_AND_TIME, taskDateAndTime);
    }

    //call this method whenever dat/time has changed for a task and we need button update
   private void updateDateAndTimeButtons(){
        //Set the time button text
        DateFormat timeFormat =
                DateFormat.getTimeInstance(DateFormat.SHORT);
        String timeForButton = timeFormat.format(
                taskDateAndTime.getTime());
        timeButton.setText(timeForButton);

        //Set the date button text
        DateFormat dateFormat = DateFormat.getDateInstance();
        String dateForButton = dateFormat.format(
                taskDateAndTime.getTime());
        dateButton.setText(dateForButton);
    }

    public static TaskEditFragment newInstance(long id){
        TaskEditFragment fragment = new TaskEditFragment();
        Bundle args = new Bundle();
        args.putLong(TaskEditActivity.EXTRA_TASKID, id);
        fragment.setArguments(args);
        return fragment;
    }

    //Programmatic menu
    private static final int MENU_SAVE = 1; //this will represent the save menu / eah item will have a unique integer

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);

        menu.add(0, MENU_SAVE, 0, R.string.confirm).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        //creates the menu item save if you have manyis best to use static final ints to name them
    }

    //handles the menu option when is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            //the save button was pressed
            case MENU_SAVE:

                /* //JONBEN dialog with fragment transaction
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DialogFragment newFragment = new AlertDialogFragment();
                newFragment.show(ft, "alertDialog"); */

                save();
                //moved to alert dialog fragment save

                ((OnEditFinished) getActivity()).finishEditingTask();
                //moved to alert dialog fragment

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //this is the method that our date picker dialog will call when the user picks date
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
        taskDateAndTime.set(Calendar.YEAR, year);
        taskDateAndTime.set(Calendar.MONTH, monthOfYear);
        taskDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateAndTimeButtons();
    }

    //this is the method that our date picker dialog will call when the user picks time
    @Override
    public void onTimeSet(TimePicker view, int hour, int minute){
        taskDateAndTime.set(Calendar.HOUR_OF_DAY, hour);
        taskDateAndTime.set(Calendar.MINUTE, minute);
        updateDateAndTimeButtons();
    }

    //SAVE method
    public void save(){
        //put all the values the user entered into a ContentValues object
        String title = titleText.getText().toString();
        ContentValues values = new ContentValues();

        values.put(TaskProvider.COLUMN_TITLE, title);
        values.put(TaskProvider.COLUMN_NOTES, notesText.getText().toString());
        values.put(TaskProvider.COLUMN_DATE_TIME, taskDateAndTime.getTimeInMillis());

        //taskId==0 when we create a new task otherwise it is the id of the task being edited.
        if(taskId == 0){
            //create new task and set taskId to the id of the new task
            Uri itemUri = getActivity().getContentResolver().insert(TaskProvider.CONTENT_URI, values);
            taskId = ContentUris.parseId(itemUri);
        }
        else
        {
            //update the existing task
            Uri uri = ContentUris.withAppendedId(TaskProvider.CONTENT_URI, taskId);
            int count = getActivity().getContentResolver().update(uri, values, null, null);

            //if somehow we didn't edit exactly one task throw an error
            if(count != 1)
                throw new IllegalStateException("Unable to update"+taskId);
        }
        Toast.makeText(
                getActivity(),
                getString(R.string.task_saved_message),
                Toast.LENGTH_SHORT).show();
        }
}
