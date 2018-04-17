package com.example.bilal.hci_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dr Ishfaque on 11-Dec-17.
 */

public class MessagesAdapter extends ArrayAdapter<Message> {

    public MessagesAdapter(@NonNull Context context,ArrayList<Message> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);}

        final Message message = getItem(position);

        TextView textView = listItemView.findViewById(R.id.message_textview);
        textView.setText("Message: "+message.getMessageText());

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Message");
                alert.setMessage(message.getMessageText());
                alert.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        dialogInterface.dismiss();
                        return false;
                    }
                });
                alert.show();
            }
        });

        TextView textView1 = listItemView.findViewById(R.id.category_textview);
        if(message.getCategory().length()!=0)
        {
            textView1.setText("Category: "+message.getCategory());
            TextView textView2 = listItemView.findViewById(R.id.expirydate_textview);
            textView2.setText("Expiry Date: "+message.getExpiryDate());

        }



        return listItemView;
}}
