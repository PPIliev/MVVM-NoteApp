package com.example.mvvmnotes2;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase instance;


    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            populateDbAsync(instance);
        }


    };

    public static void populateDbAsync(NoteDatabase db) {
        NoteDao noteDao = db.noteDao();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            noteDao.insert(new Note("Ttitle 1", "Description 1", 1));
            noteDao.insert(new Note("Ttitle 2", "Description 2", 2));
            noteDao.insert(new Note("Ttitle 3", "Description 3", 3));
        });
        executor.shutdown();
    }




}
