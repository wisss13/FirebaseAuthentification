package tn.com.wihraiech.firebaseauthentification;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button bt_Login;
    EditText et_email, et_password;
    TextView tv_Signup, tv_ForgotPass;

    RelativeLayout activity_main;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //View
        bt_Login = (Button)findViewById(R.id.login_btn_login);
        et_email = (EditText)findViewById(R.id.login_email);
        et_password = (EditText)findViewById(R.id.login_password);
        tv_Signup = (TextView)findViewById(R.id.login_btn_signup);
        tv_ForgotPass = (TextView)findViewById(R.id.login_btn_forgot_password);
        activity_main = (RelativeLayout)findViewById(R.id.intent_activity);

        tv_Signup.setOnClickListener(this);
        tv_ForgotPass.setOnClickListener(this);
        bt_Login.setOnClickListener(this);

        //Init Firebase Auth
        auth = FirebaseAuth.getInstance();

        //Check already session , if ok-> DashBoard
        if(auth.getCurrentUser() != null)
            startActivity(new Intent(LoginActivity.this,Dash_Board.class));

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.login_btn_login:
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                if(!email.isEmpty() && !password.isEmpty()) {
                    if(email.contains("@")){
                        loginUser(email, password);
                    } else {
                        Snackbar.make(activity_main, "Adresse e-mail incorrecte !", Snackbar.LENGTH_LONG).show();
                    }

                } else {

                    Snackbar.make(activity_main, "Les champs sont vides !", Snackbar.LENGTH_LONG).show();
                }
                break;

            case R.id.login_btn_forgot_password:
                startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
                finish();
                break;

            case R.id.login_btn_signup:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
                break;

        }

    }

    private void loginUser(String email, final String password) {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Log.i("Logy", task.getException().getMessage());
                            if(task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted."))
                            {
                                Snackbar.make(activity_main, "Aucun compte ne correspond à cette adresse e-mail ! Veuillez la vérifier et réessayer.", Snackbar.LENGTH_LONG).show();
                            }
                            else if(task.getException().getMessage().equals("The email address is badly formatted."))
                            {
                                Snackbar.make(activity_main, "Il semble que vous avez saisie une adresse e-mail mal orthographiée.", Snackbar.LENGTH_LONG).show();
                            }
                            else if(task.getException().getMessage().equals("Error description received from server: INVALID_PASSWORD"))
                            {
                                Snackbar.make(activity_main, "Mot de passe invalide", Snackbar.LENGTH_LONG).show();
                            }
                            else if(task.getException().getMessage().equals("The password is invalid or the user does not have a password."))
                            {
                                Snackbar.make(activity_main, "Le mot de passe est invalide ou l'utilisateur n'a pas de mot de passe. !", Snackbar.LENGTH_LONG).show();
                            }
                        }
                        else{
                            startActivity(new Intent(LoginActivity.this,Dash_Board.class));
                        }
                    }
                });
    }
}
