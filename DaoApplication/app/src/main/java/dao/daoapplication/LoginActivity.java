package dao.daoapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView txtEmail;
    private TextView txtPassword;
    private Button btnLogin;
    private CheckBox chkboxRememberMe;

    private SharedPreferences loginPref;

    private String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtEmail = findViewById(R.id.txt_loginEmail);
        txtPassword = findViewById(R.id.txt_passwordLogin);
        btnLogin = findViewById(R.id.btn_Login);
        chkboxRememberMe = findViewById(R.id.chkbox_rememberMe);
        mAuth = FirebaseAuth.getInstance();
        loginPref = getSharedPreferences("Carrier",MODE_PRIVATE);
        final boolean rememberMe = loginPref.getBoolean("RememberMe",false);
        final SharedPreferences.Editor editor = loginPref.edit();

        if(rememberMe){
            chkboxRememberMe.setChecked(true);
            Email = loginPref.getString("Email","");
            txtEmail.setText(Email);
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                String pw = txtPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email,pw).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if(chkboxRememberMe.isChecked()) {
                            editor.putBoolean("RememberMe", true);
                            editor.putString("Email",txtEmail.getText().toString());
                        }else if (!chkboxRememberMe.isChecked()){
                            editor.putBoolean("RememberMe", false);
                        }
                        editor.apply();
                        Intent intent = new Intent(LoginActivity.this,MenuActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastMassage("Something is wrong check your account details");
                    }
                });
            }
        });
    }
    private void toastMassage(String massage){
        Toast.makeText(this,massage,Toast.LENGTH_SHORT).show();
    }
}
