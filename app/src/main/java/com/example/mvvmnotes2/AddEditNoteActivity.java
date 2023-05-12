package com.example.mvvmnotes2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class AddEditNoteActivity extends AppCompatActivity {
    public static final String EXTRA_TITLE =
            "com.example.mvvmnotes2.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION =
            "com.example.mvvmnotes2.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY =
            "com.example.mvvmnotes2.EXTRA_PRIORITY";
    public static final String EXTRA_ID =
            "com.example.mvvmnotes2.EXTRA_ID";
    public static final String REQUEST_CODE =
            "com.example.mvvmnotes2.REQUEST_CODE";
    private EditText editTextTitle, editTextDescription;
    private NumberPicker numberPickerPriority;
    private int requestCode;
    private String initialTitle, initialDescription;
    private int initialPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);

        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);


        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            actionBar.setTitle("Edit Note");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY, 1));
            requestCode = intent.getIntExtra(REQUEST_CODE, 1);

            initialTitle = intent.getStringExtra(EXTRA_TITLE);
            initialDescription = intent.getStringExtra(EXTRA_DESCRIPTION);
            initialPriority = numberPickerPriority.getValue();


        } else {
            actionBar.setTitle("Add Note");
            requestCode = intent.getIntExtra(REQUEST_CODE, 1);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        int priority = numberPickerPriority.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert the title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);
        data.putExtra(REQUEST_CODE, requestCode);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        if (requestCode == MainActivity.EDIT_NOTE_REQUEST) {
            if (initialTitle != null && initialDescription != null && initialTitle.equals(title) &&
                    initialDescription.equals(description) && initialPriority == priority) {
                Toast.makeText(this, "No changes have been made", Toast.LENGTH_SHORT).show();
                return;
            }

        }

        setResult(RESULT_OK, data);
        finish();


    }
}