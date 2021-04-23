package com.example.todomvvm;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {

    private String title;

    private String description;

    private boolean finished;

    @PrimaryKey(autoGenerate = true)
    private int id;

    public Note(String title, String description, boolean finished) {
        this.title = title;
        this.description = description;
        this.finished = finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getId() {
        return id;
    }
}
