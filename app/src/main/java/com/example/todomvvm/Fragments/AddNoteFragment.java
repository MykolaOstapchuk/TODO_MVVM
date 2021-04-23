package com.example.todomvvm.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.todomvvm.R;


public class AddNoteFragment extends Fragment {

    public static final String TITLE_ERROR = "The item title cannot be empty";
    public static final String DESCRIPTION_ERROR = "The item description cannot be empty";
    public static final String LONG_TITLE_ERROR = "The title name should have less than 20 characters";
    public static final String NOTE_SAVE_ERROR = "Note can not be saved!";
    public static final int DEFAULT_ID = -1;

    private noteFragment noteFragment;
    private EditText title, description;
    boolean checkEdit = false;
    boolean checkbox_bool = false;
    private String a, b;
    int id = DEFAULT_ID;

    public interface noteFragment {
        void addNote(boolean check, String title, String description);

        void editNote(String title, String description, int id, boolean checkbox_bool);
    }

    public AddNoteFragment() {
    }

    public AddNoteFragment(String a, String b, int id, boolean checkbox_bool) {
        this.a = a;
        this.b = b;
        this.id = id;
        this.checkEdit = true;
        this.checkbox_bool = checkbox_bool;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            noteFragment = (noteFragment) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        title = view.findViewById(R.id.titleTxt);
        description = view.findViewById(R.id.descriptionTxt);
        Button confirm = view.findViewById(R.id.confirmBtn);
        Button cancel = view.findViewById(R.id.cancelBtn);

        if (checkEdit) {
            title.setText(a);
            description.setText(b);
        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tit = title.getText().toString();
                String des = description.getText().toString();

                tit = tit.replace("\n", "").replace("\r", "");
                des = des.replace("\n", "").replace("\r", "");

                if (TextUtils.isEmpty(tit)) {
                    title.setError(TITLE_ERROR);
                } else if (TextUtils.isEmpty(des)) {
                    description.setError(DESCRIPTION_ERROR);
                } else {
                    if (tit.length() >= 20) {
                        title.setError(LONG_TITLE_ERROR);
                    } else {
                        if (!checkEdit) {
                            noteFragment.addNote(true, tit, des);
                        } else {
                            if (id == DEFAULT_ID) {
                                Toast.makeText(getContext(), NOTE_SAVE_ERROR, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            noteFragment.editNote(tit, des, id, checkbox_bool);
                        }
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteFragment.addNote(false, "", "");
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}