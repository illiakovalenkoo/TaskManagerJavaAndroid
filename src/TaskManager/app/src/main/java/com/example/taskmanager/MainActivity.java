package com.example.taskmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DataBase database;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private EditText list_name_field;
    private SharedPreferences sharedPreferences;
    private TextView info_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        database = new DataBase(this);
        listView = findViewById(R.id.task_list);
        list_name_field = findViewById(R.id.list_name_field);
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String list_name = sharedPreferences.getString("list_name", "");
        list_name_field.setText(list_name);
        info_app = findViewById(R.id.info_app);
        info_app.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_info_text));
        loadAllTask();
        changeTextAction();
    }

    private void changeTextAction() {
        list_name_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("list_name", String.valueOf(list_name_field.getText()));
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadAllTask() {
        ArrayList<String> allTask = database.getAllTask();
        if(arrayAdapter == null) {
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.task_list_row, R.id.text_label_row, allTask);
            listView.setAdapter(arrayAdapter);
        } else {
            arrayAdapter.clear();
            arrayAdapter.addAll(allTask);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_new_task) {
            final EditText userTaskField = new EditText(this);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Adding new task")
                    .setMessage("What do you want to add?")
                    .setView(userTaskField)
                    .setPositiveButton("Add new", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String task = String.valueOf(userTaskField.getText());
                            database.insertData(task);
                            loadAllTask();
                        }
                    })
                    .setNegativeButton("Nothing", null)
                    .create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteTask(View button) {
        final View parent = (View) button.getParent();
        TextView textView = parent.findViewById(R.id.text_label_row);
        final String task = String.valueOf(textView.getText());


        parent.animate().alpha(0).setDuration(1500).withEndAction(new Runnable() {
            @Override
            public void run() {
                database.deleteData(task);
                loadAllTask();
                parent.animate().alpha(1).setDuration(0);
            }
        });
    }
}