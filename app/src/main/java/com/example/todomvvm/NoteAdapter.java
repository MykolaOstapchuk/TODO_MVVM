package com.example.todomvvm;


import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;
import java.util.List;


public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder> implements Filterable {

    private OnNoteListener monNoteListener;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    Context context;
    private List<Note> helpedCurrentList = new ArrayList<>();

    public NoteAdapter(OnNoteListener monNoteListener, Context context) {
        super(DIFF_CALLBACK);
        this.monNoteListener = monNoteListener;
        this.context = context;
    }

    public void modifyList(List<Note> list) {
        helpedCurrentList.clear();
        helpedCurrentList.addAll(list);
        submitList(list);
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.isFinished() == newItem.isFinished();
        }
    };

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView, monNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = getItem(position);

        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(currentNote.getTitle()));
        viewBinderHelper.closeLayout(String.valueOf(currentNote.getTitle()));

        holder.setData(getItem(position));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int pos = holder.getAdapterPosition();
                if (b) {
                    getCurrentList().get(pos).setFinished(true);
                    holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    getCurrentList().get(pos).setFinished(false);
                    holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                monNoteListener.onUpdateNoteCheckbox(getItem(pos));
            }
        });
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Note> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(helpedCurrentList);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Note note : helpedCurrentList) {
                    if (note.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(note);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            submitList((List<Note>) filterResults.values);
        }
    };

    class NoteHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView textViewTitle;
        private TextView textViewDescription;
        private SwipeRevealLayout swipeRevealLayout;

        public NoteHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            textViewTitle = itemView.findViewById(R.id.noteTitle);
            textViewDescription = itemView.findViewById(R.id.noteDescription);

            TextView edit = itemView.findViewById(R.id.editTextBtn);
            TextView delete = itemView.findViewById(R.id.deleteTextBtn);
            swipeRevealLayout = itemView.findViewById(R.id.swipeLayout);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    monNoteListener.onEditDeleteClick(getAdapterPosition(), true, getItem(getAdapterPosition()));
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    monNoteListener.onEditDeleteClick(getAdapterPosition(), false, getItem(getAdapterPosition()));
                }
            });

        }

        public void setData(Note note) {
            boolean help = note.isFinished();

            textViewTitle.setText(note.getTitle());
            if (help) {
                textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            String desc = note.getDescription();
            if (desc.length() >= 30) {
                String upToNCharacters = desc.substring(0, 30);
                textViewDescription.setText(upToNCharacters + " ...");
            } else
                textViewDescription.setText(note.getDescription());

            checkBox.setChecked(note.isFinished());
        }
    }


    public interface OnNoteListener {
        void onEditDeleteClick(int position, boolean choose, Note note);

        void onUpdateNoteCheckbox(Note note);
    }
}
