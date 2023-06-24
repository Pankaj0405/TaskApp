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

public class RegistrationActivity extends AppCompatActivity {

    private EditText email,pass;
    private Button btnReg;
    private TextView login_txt;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();

        email=(EditText) findViewById(R.id.email_reg);
        pass=(EditText) findViewById(R.id.password_reg);

        btnReg=(Button) findViewById(R.id.reg_btn);
        login_txt=(TextView) findViewById(R.id.login_txt);

        login_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i=new Intent(getApplicationContext(),MainActivity.class);
//                startActivity(i);

                startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
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

                mAuth.createUserWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            Toast.makeText(RegistrationActivity.this,"Successfull",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                            finish();
                            mDialog.dismiss();
                        }
                        else {
                            Toast.makeText(RegistrationActivity.this,"Problem",Toast.LENGTH_LONG).show();
                            mDialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}
