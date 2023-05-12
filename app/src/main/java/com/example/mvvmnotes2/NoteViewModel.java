package com.example.mvvmnotes2;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;
    private List<Note> allNotesDeleted;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
//        allNotesDeleted = repository.getAllNotes();
    }

    public void insert(Note note){
        repository.insertNoteAsync(note);
    }

    public void update(Note note){
        repository.updateNoteAsync(note);
    }

    public void delete(Note note){
        repository.deleteNoteAsync(note);
    }
    public void deleteAll(){
        repository.deleteAllNoteAsync();
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

//    public List<Note> getAllNotesDeleted() {
//        return allNotesDeleted;
//    }

}
