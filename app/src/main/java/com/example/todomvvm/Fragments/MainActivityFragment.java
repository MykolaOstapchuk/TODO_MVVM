package com.example.todomvvm.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todomvvm.Note;
import com.example.todomvvm.NoteAdapter;
import com.example.todomvvm.NoteViewModel;
import com.example.todomvvm.NoteViewModelFactory;
import com.example.todomvvm.R;
import com.example.todomvvm.RecyclerView.SimpleDividerItemDecoration;

import java.util.List;

public class MainActivityFragment extends Fragment implements NoteAdapter.OnNoteListener {

    public static final int DEFAULT_ID = -1;

    private openAddNoteFragment addNoteFragment;
    private RecyclerView recyclerView;
    private boolean isNightModeOn = false;
    private boolean addNewElement = false;
    private boolean checkbox_bool = false;
    private String title;
    private String description;
    private boolean editNote = false;
    private NoteViewModel noteViewModel;
    private Button addBtn;
    private Button darkMode;
    private Button deleteAllNotesBtn;
    private int id = DEFAULT_ID;
    private SharedPreferences.Editor editor;
    private NoteAdapter adapter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppSettingPrefs", 0);
        editor = sharedPreferences.edit();
        editor.apply();
        isNightModeOn = sharedPreferences.getBoolean("NightMode", false);
        try {
            addNoteFragment = (openAddNoteFragment) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public void onEditDeleteClick(int position, boolean choose, Note note) {
        if (choose) {
            addNoteFragment.openNoteFragment(true, note.getTitle(), note.getDescription(), note.getId(), note.isFinished());
        } else {
            noteViewModel.delete(note);
        }
    }

    @Override
    public void onUpdateNoteCheckbox(Note note) {
        noteViewModel.update(note);
    }


    public interface openAddNoteFragment {
        void openNoteFragment(boolean check, String title, String description, int id, boolean checkbox_bool);
    }

    public MainActivityFragment() {
        this.addNewElement = false;
    }

    public MainActivityFragment(String title, String description) {
        this.title = title;
        this.description = description;
        this.addNewElement = true;
    }

    public MainActivityFragment(String title, String description, int id, boolean checkbox_bool) {
        this.title = title;
        this.description = description;
        this.editNote = true;
        this.checkbox_bool = checkbox_bool;
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);

        addBtn = rootView.findViewById(R.id.add_note_button);
        darkMode = rootView.findViewById(R.id.dark_mode_button);
        deleteAllNotesBtn = rootView.findViewById(R.id.delete_notes_button);

        recyclerView = rootView.findViewById(R.id.noteRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        recyclerView.setAdapter(null);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.find_note_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.find_note);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;//or true
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        adapter = new NoteAdapter(this, getContext());
        recyclerView.setAdapter(adapter);

        deleteAllNotesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteViewModel.deleteAllNotes();
            }
        });

        darkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNightModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("NightMode", false);
                    editor.commit();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("NightMode", true);
                    editor.commit();
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNoteFragment.openNoteFragment(false, "", "", 0, checkbox_bool);
            }
        });

        noteViewModel = new ViewModelProvider(this, new NoteViewModelFactory(getActivity().getApplication())).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(getViewLifecycleOwner(), new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.modifyList(notes);
            }
        });

        if (addNewElement) {
            addNewElement = false;
            noteViewModel.insert(new Note(title, description, false));
        }
        if (editNote) {
            editNote = false;
            if (id != DEFAULT_ID) {

                Note help = new Note(title, description, checkbox_bool);
                help.setId(id);
                noteViewModel.update(help);
            }
        }

        super.onViewCreated(view, savedInstanceState);
    }
}