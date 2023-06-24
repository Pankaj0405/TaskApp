package ucs.tech.taskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private TextView signup;
    private EditText email;
    private EditText pass;
    private Button btnLogin;

    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
        }

        signup=findViewById(R.id.signup_txt);
        email=findViewById(R.id.email_login);
        pass=findViewById(R.id.password_login);
        btnLogin=findViewById(R.id.login_btn);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mEmail=email.getText().toString().trim();
                String mPass=pass.getText().toString().trim();

                if(TextUtils.isEmpty(mEmail))
                {
                    email.setError("Required Field....");
                    return;
                }

                if(TextUtils.isEmpty(mPass))
                {
                    pass.setError("Required Field....");
                    return;
                }

                mDialog.setMessage("Processing.....");
                mDialog.show();


                mAuth.signInWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this,"Login Successfull",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MainActivity.this,HomeActivity.class));
                            finish();
                            mDialog.dismiss();
                        }
                        else {
                            Toast.makeText(MainActivity.this,"Invalid User name or Password",Toast.LENGTH_LONG).show();
                            mDialog.dismiss();
                        }
                    }
                });
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegistrationActivity.class));
            }
        });
    }
}
