package com.example.mvvmnotes2;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    public NoteRepository(Application application) {
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
    }


    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public void insertNoteAsync(Note note) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            noteDao.insert(note);
        });
        executor.shutdown();
    }

    public void updateNoteAsync(Note note) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            noteDao.update(note);
        });
        executor.shutdown();
    }

    public void deleteNoteAsync(Note note) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            noteDao.delete(note);
        });
        executor.shutdown();
    }

    public void deleteAllNoteAsync() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            noteDao.deleteAllNotes();
        });
        executor.shutdown();
    }



}
