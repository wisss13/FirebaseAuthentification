package tn.com.wihraiech.firebaseauthentification;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dash_Board extends AppCompatActivity implements View.OnClickListener {

    private TextView txtWelcome;
    private EditText input_new_password;
    private Button btnChangePass,btnLogout;
    private RelativeLayout activity_dashboard;

    //private CallbackManager callbackManager;

    private FirebaseUser user;
    private FirebaseAuth auth;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash__board);

        //View
        txtWelcome = (TextView)findViewById(R.id.dashboard_welcome);
        input_new_password = (EditText)findViewById(R.id.dashboard_new_password);
        btnChangePass = (Button)findViewById(R.id.dashboard_btn_change_pass);
        btnLogout = (Button)findViewById(R.id.dashboard_btn_logout);
        activity_dashboard = (RelativeLayout)findViewById(R.id.activity_dash_board);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //callbackManager = CallbackManager.Factory.create();

        btnChangePass.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        //Init Firebase
        /*auth = FirebaseAuth.getInstance();

        //Session check
        if(auth.getCurrentUser() != null)
            txtWelcome.setText("Welcome , "+auth.getCurrentUser().getEmail());*/

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String uid = user.getUid();

            Log.i("logName", email);
            txtWelcome.setText("Welcome , " + email);

        } else
        {
            goLoginScreen();
        }


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.dashboard_btn_change_pass){
            String password = input_new_password.getText().toString();
            if(!password.isEmpty()) {
                changePassword(password);
            } else {
                Snackbar.make(activity_dashboard, "Saisissez votre nouveau mot de passe !", Snackbar.LENGTH_LONG).show();
            }
        }

        else if(view.getId() == R.id.dashboard_btn_logout)
            logout();
    }

    private void logoutUser() {
        auth.signOut();
        if(auth.getCurrentUser() == null)
        {
            startActivity(new Intent(Dash_Board.this,LoginActivity.class));
            finish();
        }
    }

    private void goLoginScreen() {
        //Intent intent = new Intent(this, FacebookLoginActivity.class);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    private void changePassword(String newPassword) {
        //FirebaseUser user = auth.getCurrentUser();
        user.updatePassword(newPassword).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(!task.isSuccessful()) {
                    Log.i("Logy", task.getException().getMessage());
                    if (task.getException().getMessage().equals("WEAK_PASSWORD : Password should be at least 6 characters")) {
                        Snackbar.make(activity_dashboard, "Le mot de passe doit comporter au moins 6 caractères !", Snackbar.LENGTH_LONG).show();
                    } else if (task.getException().getMessage().equals("This operation is sensitive and requires recent authentication. Log in again before retrying this request.")) {
                        Snackbar.make(activity_dashboard, "Cette opération est sensible et nécessite une authentification récente. Connectez-vous à nouveau avant de réessayer cette demande.", Snackbar.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Snackbar.make(activity_dashboard,"Mot de passe changé avec succès !",Snackbar.LENGTH_LONG).show();
                }


            }
        });
    }
}