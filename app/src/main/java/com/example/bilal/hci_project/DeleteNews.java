package com.example.bilal.hci_project;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeleteNews extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference ReferenceToUserInterest;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private ArrayList<String> interestArray;
    private String[] queriedLocation;
    private Context mContext;
    private ArrayList<Message> messageArrayList = new ArrayList<Message>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_news);
        mContext=this;

        mAuth = FirebaseAuth.getInstance();
        ReferenceToUserInterest = ref.child("Messages");

        FetchDataAysncTask1 fetchDataAysncTask = new FetchDataAysncTask1();
        fetchDataAysncTask.doInBackground(null);
    }

    private void NextTast()
    {
        ListAdapter1 listAdapter = new ListAdapter1(this,messageArrayList);
        ListView listView = (ListView) findViewById(R.id.DeleteNewsListView);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new DataClickListener(messageArrayList));
    }
    private class DataClickListener implements ListView.OnItemClickListener {
        ArrayList<Message> dataList;

        DataClickListener(ArrayList<Message> mDoc) {
            dataList = mDoc;
        }
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d("billal -> ",dataList.get(i).getMessageText());
//            String interest = interestArray.get(i);
//            DatabaseReference demo =  ReferenceToUserInterest.orderByChild("Interests").equalTo(interest).getRef();
//            demo.setValue(null);//removeValue();
            startActivity(new Intent(DeleteNews.this,DeleteNews.class));
            finish();
        }
    }
    private class ListAdapter1 extends ArrayAdapter<Message> {

        ListAdapter1(Context context, ArrayList<Message> values) {
            super(context, 0, values);
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;

            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.delete_news_listview_content, parent, false);
            }
            Message message = getItem(position);

            TextView textViewTitle = (TextView) listItemView.findViewById(R.id.txtdeletenews);
            textViewTitle.setText((CharSequence) message.getMessageText());
            return listItemView;
        }
    }
    class FetchDataAysncTask1 extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            ReferenceToUserInterest.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot data: dataSnapshot.getChildren())
                    {
                        for(DataSnapshot data1: data.getChildren())
                        {
                            DataSnapshot d=data1;
                            String from = (String) d.child("from").getValue();
                            Log.d("fromsdf",from);
                            String user = mAuth.getUid();
                            if(from.equals(user))
                            {
                                 messageArrayList.add(new Message(d.child("category").getValue().toString(),d.child("expiry date").getValue().toString(),d.child("message").getValue().toString(),d.child("from").getValue().toString()));
                            }
                        }
                    }
                    if(messageArrayList.size()!=0)
                    {
                        NextTast();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return interestArray;
        }

        public FetchDataAysncTask1() {
            super();
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(mContext,"Interest Is Retreiving...",Toast.LENGTH_LONG).show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }
}
