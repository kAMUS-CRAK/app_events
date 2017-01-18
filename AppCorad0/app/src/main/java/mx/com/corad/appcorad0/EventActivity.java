package mx.com.corad.appcorad0;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.security.SecureRandom;

public class EventActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tv_Nombre;
    TextView tv_Email;
    ImageView imgV_user;
    FirebaseAuth fAuth;
    private static int RESULT_LOAD_IMAGE = 0;
    private ProgressDialog mProgress;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        tv_Nombre = (TextView) headerView.findViewById(R.id.name_profile);
        tv_Email = (TextView) headerView.findViewById(R.id.email_profile);
        imgV_user = (ImageView) headerView.findViewById(R.id.img_profile);
        fAuth = FirebaseAuth.getInstance();

        imgV_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                if (intent.resolveActivity(getPackageManager()) != null) {

                    startActivityForResult(intent.createChooser(intent, "Select a picture for your profile"), RESULT_LOAD_IMAGE);
                }
            }
        });
        mProgress = new ProgressDialog(this);
        fAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    mStorage = FirebaseStorage.getInstance().getReference();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                    mDatabase.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            tv_Nombre.setText(snapshot.child("name").getValue().toString());
                            tv_Email.setText(snapshot.child("email").getValue().toString());
                            String imgUrl = snapshot.child("image").getValue().toString();
                            if (!imgUrl.equals("default") || TextUtils.isEmpty((imgUrl))) {
                                //Para la imagen redondeada se creo la clase CircleTrasform  CircleTransform()
                                Picasso.with(EventActivity.this).load(Uri.parse(snapshot.child("image").getValue().toString())).resize(110, 110).transform(new CircleTransform()).into(imgV_user);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    startActivity(new Intent(EventActivity.this,MainActivity.class));
                    finish();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        fAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sede) {
            // Handle the camera action

        } else if (id == R.id.nav_information) {

        } else if (id == R.id.nav_agenda) {

        } else if (id == R.id.nav_ponentes) {

        } else if (id == R.id.nav_chat) {
                Intent intent = new Intent(EventActivity.this,Chat.class);
                startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_exit) {
            if(fAuth.getCurrentUser() != null)
                fAuth.signOut();
                //finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK)
        {
            if(fAuth.getCurrentUser() == null)
                return;
            mProgress.setMessage("Uploading image...");
            mProgress.show();
            final Uri uri = data.getData();
            if(uri == null)
            {
                mProgress.show();
                return;
            }
            if(fAuth.getCurrentUser() == null)
                return;
            if(mStorage == null)
                mStorage =  FirebaseStorage.getInstance().getReference();
            if(mDatabase == null)
                mDatabase =  FirebaseDatabase.getInstance().getReference().child("users");

            final StorageReference filepath = mStorage.child("Photos").child(getRandomString());
            final DatabaseReference currentUserDB = mDatabase.child(fAuth.getCurrentUser().getUid());
            currentUserDB.child("image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String image = snapshot.getValue().toString();
                    if(!image.equals("default") && !image.isEmpty())
                    {
                        Task<Void> task = FirebaseStorage.getInstance().getReferenceFromUrl(image).delete();
                        task.addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Toast.makeText(EventActivity.this, "Delete image succesfullly",Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(EventActivity.this, "Delete image failed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    currentUserDB.child("image").removeEventListener(this);

                    filepath.putFile(uri).addOnSuccessListener(EventActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>(){

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgress.dismiss();
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            Toast.makeText(EventActivity.this,"Finished", Toast.LENGTH_SHORT).show();
                            Picasso.with(EventActivity.this).load(uri).fit().centerCrop().into(imgV_user);
                            DatabaseReference currentUserDB = mDatabase.child(fAuth.getCurrentUser().getUid());
                            currentUserDB.child("image").setValue(downloadUri.toString());
                        }
                    }).addOnFailureListener(EventActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgress.dismiss();
                            Toast.makeText(EventActivity.this,  e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public String getRandomString()
    {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
