package com.example.mvvmnotes2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private NoteViewModel noteViewModel;
    private List<Note> deletedNotes;
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();
                        
                        if (resultCode == Activity.RESULT_OK) {
                            int requestCode = data.getIntExtra(AddEditNoteActivity.REQUEST_CODE, 1);

                            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
                            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
                            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);
                            Note note = new Note(title, description, priority);

                            if (requestCode == ADD_NOTE_REQUEST) {
                                noteViewModel.insert(note);
                                Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                            } else if (requestCode == EDIT_NOTE_REQUEST) {
                                int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
                                if (id == -1) {
                                    Toast.makeText(MainActivity.this, "Note cant be updated!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                note.setId(id);
                                noteViewModel.update(note);
                                Toast.makeText(MainActivity.this, "Note updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Note NOT saved", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                });

        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.REQUEST_CODE, ADD_NOTE_REQUEST);
                activityResultLauncher.launch(intent);

            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);


        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        noteViewModel = viewModelProvider.get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.submitList(notes);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Note deletedNote = adapter.getNoteAt(viewHolder.getAdapterPosition());
                deletedNotes = new ArrayList<>();
                deletedNotes.add(deletedNote);
                showUndoSnackbar();
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.getPriority());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddEditNoteActivity.REQUEST_CODE, EDIT_NOTE_REQUEST);
                activityResultLauncher.launch(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delete_all_notes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                deletedNotes = new ArrayList<>(Objects.requireNonNull(noteViewModel.getAllNotes().getValue()));
                showUndoSnackbar();
                noteViewModel.deleteAll();
                Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    private void showUndoSnackbar() {
        View rootView = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, "Item deleted", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deletedNotes !=null) {
                    for (Note note: deletedNotes) {
                        noteViewModel.insert(note);
                    }
                }
                deletedNotes = null;

            }
        });


        snackbar.show();
    }



}
