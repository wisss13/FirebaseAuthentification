package tn.com.wihraiech.firebaseauthentification;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private EditText input_email;
    private Button btnResetPass;
    private TextView btnBack;
    private RelativeLayout activity_forgot;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //View
        input_email = (EditText)findViewById(R.id.forgot_email);
        btnResetPass = (Button)findViewById(R.id.forgot_btn_reset);
        btnBack = (TextView)findViewById(R.id.forgot_btn_back);
        activity_forgot = (RelativeLayout)findViewById(R.id.activity_forgot_password);

        btnResetPass.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.forgot_btn_back)
        {
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        else  if(view.getId() == R.id.forgot_btn_reset)
        {
            String email = input_email.getText().toString();
            if(!email.isEmpty()) {
                if(email.contains("@")){
                    resetPassword(input_email.getText().toString());
                }else {
                    Snackbar.make(activity_forgot, "Adresse e-mail incorrecte !", Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(activity_forgot, "Saisissez une adresse e-mail !", Snackbar.LENGTH_LONG).show();
            }

        }


    }

    private void resetPassword(final String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Snackbar snackBar = Snackbar.make(activity_forgot,"Lien de réinitialisation envoyé à : " + email, Snackbar.LENGTH_LONG);
                            snackBar.show();
                        }
                        else if(task.getException().getMessage().equals("An internal error has occurred. [ <<Network Error>> ]")){
                            Snackbar snackBar = Snackbar.make(activity_forgot,"Pas de connexion internet.",Snackbar.LENGTH_LONG);
                            snackBar.show();
                        }else
                        {
                            Snackbar snackBar = Snackbar.make(activity_forgot,"Aucun compte ne correspond à cette adresse e-mail ! Veuillez la vérifier et réessayer.",Snackbar.LENGTH_LONG);
                            snackBar.show();
                        }
                    }
                });
    }
}
