package mx.com.corad.appcorad0;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Chat extends AppCompatActivity {

    private FloatingActionButton fab_sendMesasage;
    private EditText mensaje;

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_users = new ArrayList<>();

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private String name;
    //private DatabaseReference root = FirebaseDatabase.getInstance().getReference("users").getRoot();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("users").getRoot();
    FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Intancio el boton y el EditTex del mesnaje
        fab_sendMesasage = (FloatingActionButton) findViewById(R.id.fab_send_message);
        mensaje = (EditText)findViewById(R.id.tv_messageWrited);
        //instancio el ListView de los contactos en para el chat
        listView = (ListView) findViewById(R.id.listView_charRoom);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_of_users);
        listView.setAdapter(arrayAdapter);

        //fAuth = FirebaseAuth.getInstance();

        fab_sendMesasage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map = new HashMap<String, Object>();
                map.put(mensaje.getText().toString(),"");
                root.updateChildren(map);
            }
        });
        //request_user_name();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    mStorage = FirebaseStorage.getInstance().getReference();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                    mDatabase.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            name = snapshot.child("name").getValue().toString();
                            //tv_Nombre.setText(snapshot.child("name").getValue().toString());
                            //tv_Email.setText(snapshot.child("email").getValue().toString());
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    startActivity(new Intent(Chat.this, MainActivity.class));
                    finish();
                }
            }
        };

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                list_of_users.clear();
                list_of_users.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                /*
                Intent intent = new Intent(getApplicationContext(),Chat_Room.class);
                intent.putExtra("room_name",((TextView)view).getText().toString() );
                intent.putExtra("user_name",name);
                startActivity(intent);*/
            }
        });

    }

/*
    private void request_user_name() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name:");

        final EditText input_field = new EditText(this);

        builder.setView(input_field);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                name = input_field.getText().toString();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                request_user_name();
            }
        });

        builder.show();
    }*/
}
