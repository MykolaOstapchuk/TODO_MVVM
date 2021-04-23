package com.example.todomvvm;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.todomvvm.Fragments.AddNoteFragment;
import com.example.todomvvm.Fragments.MainActivityFragment;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.openAddNoteFragment, AddNoteFragment.noteFragment {

    private Fragment cuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cuFragment = new MainActivityFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_page, cuFragment)
                .addToBackStack("MainPageFragment")
                .commit();
    }

    @Override
    public void openNoteFragment(boolean check, String title, String description, int id, boolean checkbox_bool) {
        getSupportFragmentManager().popBackStack();

        if (!check) {
            cuFragment = new AddNoteFragment();
        } else {
            cuFragment = new AddNoteFragment(title, description, id, checkbox_bool);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_page, cuFragment)
                .addToBackStack("AddNoteFragment")
                .commit();
    }

    @Override
    public void addNote(boolean check, String title, String description) {
        getSupportFragmentManager().popBackStack();

        if (!check) {
            cuFragment = new MainActivityFragment();
        } else {
            cuFragment = new MainActivityFragment(title, description);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_page, cuFragment)
                .addToBackStack("MainActivityFragment")
                .commit();
    }

    @Override
    public void editNote(String title, String description, int id, boolean checkbox_bool) {
        getSupportFragmentManager().popBackStack();

        cuFragment = new MainActivityFragment(title, description, id, checkbox_bool);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_page, cuFragment)
                .addToBackStack("MainActivityFragment")
                .commit();
    }
}