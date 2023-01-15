package com.example.babyneeds;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babyneeds.data.DatabaseHandler;
import com.example.babyneeds.model.Item;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHoleder> {
    private Context context;
    private List<Item> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;


    public RecyclerViewAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;


    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHoleder onCreateViewHolder(@NonNull ViewGroup viewGroup,
                                                              int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row, viewGroup, false);


        return new ViewHoleder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHoleder viewHoleder, int position) {

        Item item = itemList.get(position);
        viewHoleder.itemName.setText(String.format("Name: %s", item.getItemName()));
        viewHoleder.itemColor.setText(String.format(" Color: %s", item.getItemColor()));
        viewHoleder.itemQuantity.setText(String.format("Quantity: %d ", item.getItemQuantity()));
        viewHoleder.itemSize.setText("Size: " + item.getItemSize() + " ");
        viewHoleder.dateAdded.setText(String.format("Date: %s", item.getDateItemAdded()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHoleder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView itemName;
        public TextView itemColor;
        public TextView itemQuantity;
        public TextView itemSize;
        public TextView dateAdded;
        public int id;

        public Button editButton;
        public Button deleteButton;

        public ViewHoleder(@Nullable View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            itemName = itemView.findViewById(R.id.itemName);
            itemColor = itemView.findViewById(R.id.itemColor);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemSize = itemView.findViewById(R.id.itemSize);
            dateAdded = itemView.findViewById(R.id.itemDate);

            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int position;
            switch (v.getId()) {
                case R.id.editButton:
                    position = getAdapterPosition();
                    Item item = itemList.get(position);
                    editeItem(item);
                    //edite item
                    break;
                case R.id.deleteButton:
                    //delete item
                    position = getAdapterPosition();
                    deleteItem(position);
                    break;

            }


        }

        private void deleteItem(final int position) {

            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_pop, null);

            Button noButton = view.findViewById(R.id.cofir_no_btn);
            Button yesButton = view.findViewById(R.id.cofir_yes_btn);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();


            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteItem(itemList.get(position).getId());
                    itemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyDataSetChanged();
                    dialog.dismiss();


                }
            });

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }


        private void editeItem(final Item newItem) {


            Item item = itemList.get(getAdapterPosition());
            Button saveButton;
            final EditText babyItem;
            final EditText itemQuantity;
            final EditText itemColor;
            final EditText itemSize;
            TextView title;

            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.popup, null);



            babyItem = view.findViewById(R.id.babyItem);
            itemQuantity = view.findViewById(R.id.item_qty);
            itemColor = view.findViewById(R.id.item_color);
            itemSize = view.findViewById(R.id.item_size);
            saveButton = view.findViewById(R.id.saveButton);
            title = view.findViewById(R.id.title);


            title.setText(R.string.title_item);
            babyItem.setText(newItem.getItemName());
            itemQuantity.setText(String.valueOf(newItem.getItemQuantity()));
            itemColor.setText(newItem.getItemColor());
            itemSize.setText(String.valueOf(newItem.getItemSize()));
            saveButton.setText(R.string.update_edit);





            builder.setView(view);
            dialog=builder.create();
            dialog.show();
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseHandler databaseHandler = new DatabaseHandler(context);

                    newItem.setItemName(babyItem.getText().toString());
                    newItem.setItemColor(itemColor.getText().toString());
                    newItem.setItemQuantity(Integer.parseInt(itemQuantity.getText().toString()));
                    newItem.setItemSize(Integer.parseInt(itemSize.getText().toString()));

                    if (!babyItem.getText().toString().isEmpty()
                            && !itemColor.getText().toString().isEmpty()
                            && !itemSize.getText().toString().isEmpty()
                            && !itemQuantity.getText().toString().isEmpty()
                    ){
                        databaseHandler.updateItem(newItem);
                        notifyItemChanged(getAdapterPosition(),newItem); //imoartant
                        dialog.dismiss();
                    }else {

                        Snackbar.make(view, "fileds empty",Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
