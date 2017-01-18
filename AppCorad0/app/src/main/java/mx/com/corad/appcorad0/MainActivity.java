package mx.com.corad.appcorad0;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button llBtn_Login;
    Button llBtn_Register;
    Button btn_Acction;
    EditText et_Name;
    LinearLayout linearLayout;
    EditText et_Email;
    EditText et_Password;
    //Esta variable determina que pantalal tiene de frenete el usuario Login (0) / Register (1)
    public Integer UId = 1;
    private FirebaseAuth fAuth;
    private ProgressDialog mProgress;
    //Varibale para ecuchar el cambio de estado en la autentcicacoin de Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        llBtn_Login = (Button)findViewById(R.id.btn_login);
        llBtn_Register = (Button)findViewById(R.id.btn_register);
        btn_Acction = (Button)findViewById(R.id.btn_action);
        et_Name = (EditText)findViewById(R.id.et_Name);
        et_Email = (EditText)findViewById(R.id.et_Email);
        et_Password = (EditText)findViewById(R.id.et_Password);
        //Firebase Authentication
        fAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        llBtn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBtn_Login.setBackgroundColor(Color.WHITE);
                llBtn_Login.setTextColor(Color.BLUE);
                llBtn_Register.setBackgroundColor(Color.TRANSPARENT);
                llBtn_Register.setTextColor(Color.WHITE);
                btn_Acction.setText("Login");
                et_Name.setVisibility(View.GONE);
                UId = 0;
                //Intent btn_registrar = new Intent(login.this, MainActivity.class);
                //startActivity(btn_registrar);
            }
        });

        llBtn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBtn_Register.setBackgroundColor(Color.WHITE);
                llBtn_Register.setTextColor(Color.BLUE);
                llBtn_Login.setBackgroundColor(Color.TRANSPARENT);
                llBtn_Login.setTextColor(Color.WHITE);
                et_Name.setVisibility(View.VISIBLE);
                btn_Acction.setText("Register");
                UId = 1;
            }
        });
        btn_Acction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UId == 1)
                {
                    statRegister();
                }
                else
                {
                    startLogin();
                    //Toast.makeText(MainActivity.this,"Iniciar Sesion",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null)
                {
                    Toast.makeText(MainActivity.this,"Iniciar Sesion",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, EventActivity.class);
                    startActivity(intent);
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

    public void statRegister()
    {
        final String name = et_Name.getText().toString();
        final String email = et_Email.getText().toString();
        final String password = et_Password.getText().toString();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
        {
            mProgress.setMessage("Registering, please wait...");
            mProgress.show();
            fAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgress.dismiss();
                            if(task.isSuccessful())
                            {
                                //String user_id = fAuth.getCurrentUser().getUid();
                                //Toast.makeText(MainActivity.this,user_id,Toast.LENGTH_SHORT).show();
                                fAuth.signInWithEmailAndPassword(email,password);
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                                DatabaseReference currentUserDB = mDatabase.child(fAuth.getCurrentUser().getUid());
                                currentUserDB.child("name").setValue(name);
                                currentUserDB.child("email").setValue(email);
                                currentUserDB.child("image").setValue("default");
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this,"Error registering user",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(MainActivity.this,"Algunos campos vacios",Toast.LENGTH_SHORT).show();
        }
    }
    public void startLogin()
    {
        String email = et_Email.getText().toString();
        String password = et_Password.getText().toString();
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgress.setMessage("Registering, please wait...");
            mProgress.show();
            fAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                          mProgress.dismiss();
                            if(task.isSuccessful())
                            {

                                Toast.makeText(MainActivity.this,"Login Succesful",Toast.LENGTH_SHORT).show();

                                //Intent btn_registrar = new Intent(login.this, MainActivity.class);
                                //startActivity(btn_registrar);
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this,"Error on Login",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(MainActivity.this,"Algunos campos vacios",Toast.LENGTH_SHORT).show();
        }

    }

}
