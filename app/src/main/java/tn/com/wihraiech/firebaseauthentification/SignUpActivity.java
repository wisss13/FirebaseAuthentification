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
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSignup, bt_facebook, bt_google, bt_email;
    private TextView tv_Login, tv_ForgotPass;
    private EditText et_email, et_password;
    private RelativeLayout activity_sign_up;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //View

        btnSignup = (Button) findViewById(R.id.signup_btn_register);
        bt_email = (Button) findViewById(R.id.bt_email);
        bt_facebook = (Button) findViewById(R.id.bt_facebook);
        bt_google = (Button) findViewById(R.id.bt_google);

        tv_Login = (TextView) findViewById(R.id.signup_btn_login);
        tv_ForgotPass = (TextView) findViewById(R.id.signup_btn_forgot_password);

        et_email = (EditText) findViewById(R.id.signup_email);
        et_password = (EditText) findViewById(R.id.signup_password);

        activity_sign_up = (RelativeLayout) findViewById(R.id.activity_sign_up);


        //TODO: Init Firebase Auth
        auth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    startActivity(new Intent(SignUpActivity.this, Dash_Board.class));
                }
            }
        };

        tv_Login.setOnClickListener(this);
        tv_ForgotPass.setOnClickListener(this);
        bt_email.setOnClickListener(this);
        bt_facebook.setOnClickListener(this);
        bt_google.setOnClickListener(this);

        btnSignup.setOnClickListener(this);

        // TODO: [ Configure Google Sign In ]
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(SignUpActivity.this, "Erreur d'authentification !", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    public void onClick(View view)
    {

        switch (view.getId()) {

            //case R.id.signup_btn_register:
            case R.id.bt_email:
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    if (email.contains("@")) {
                        SignUpUser(email, password);
                    } else {
                        Snackbar.make(activity_sign_up, "Adresse e-mail incorrecte !", Snackbar.LENGTH_LONG).show();
                    }

                } else {
                    Snackbar.make(activity_sign_up, "Les champs sont vides !", Snackbar.LENGTH_LONG).show();
                }
                break;

            case R.id.bt_facebook:
                startActivity(new Intent(this, FacebookLoginActivity.class));
                finish();
                break;

            case R.id.bt_google:
                googleSignIn();
                break;

            case R.id.signup_btn_login:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;

            /*case R.id.signup_btn_forgot_password:
                startActivity(new Intent(this, ForgotPassword.class));
                finish();
                break;*/
        }

    }


    private void SignUpUser(String email, String password)

    {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.i("Logy", task.getException().getMessage());
                            if(task.getException().getMessage().equals("The email address is badly formatted."))
                            {
                                Snackbar.make(activity_sign_up, "Il semble que vous avez saisie une adresse e-mail mal orthographiée !", Snackbar.LENGTH_LONG).show();
                            }
                            else if(task.getException().getMessage().equals("The email address is already in use by another account."))
                            {
                                Snackbar.make(activity_sign_up, "L'adresse email est déjà utilisée par un autre compte !", Snackbar.LENGTH_LONG).show();
                            }
                            else if(task.getException().getMessage().equals("The given password is invalid. [ Password should be at least 6 characters ]"))
                            {
                                Snackbar.make(activity_sign_up, "Le mot de passe doit comporter au moins 6 caractères !", Snackbar.LENGTH_LONG).show();
                            }

                        } else {
                            Snackbar.make(activity_sign_up, "Register success", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //TODO [Start: Google Sign in]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = auth.getCurrentUser();
        //updateUI(currentUser);

        auth.addAuthStateListener(mAuthListener);
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Log", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Log", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Log", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


}



