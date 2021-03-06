package com.online.ojek.ojekonline.Rider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.online.ojek.ojekonline.Common.Common;
import com.online.ojek.ojekonline.Driver.DriverMainActivity;
import com.online.ojek.ojekonline.Model.Rider;
import com.online.ojek.ojekonline.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RiderMainActivity extends AppCompatActivity {

    Button btnSignIn, btnRegister;
    RelativeLayout rootRiderLayout;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    TextView txt_forgot_password;

    private final static int PERMISSION = 1000;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_rider_main);

        Paper.init(this);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference(Common.user_rider_tbl);


        btnSignIn = (Button)findViewById(R.id.btnSignInRider);
        btnRegister = (Button)findViewById(R.id.btnRegisterRider);
        rootRiderLayout = (RelativeLayout)findViewById(R.id.rootRiderLayout);

        txt_forgot_password = (TextView) findViewById(R.id.txt_forgot_password);
        txt_forgot_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showDialogForgotPwd();
                return false;
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });

        String user = Paper.book().read(Common.rider_user_field);
        String password = Paper.book().read(Common.rider_password_field);
        if(user!= null && password!= null){
            if(!TextUtils.isEmpty(user) && !TextUtils.isEmpty(password)){
                autoLogin(user,password);
            }
        }
    }

    private void autoLogin(String user, String password) {
        final SpotsDialog waitingDialog = new SpotsDialog(RiderMainActivity.this);
        waitingDialog.show();

        auth.signInWithEmailAndPassword(user,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        waitingDialog.dismiss();
                        startActivity(new Intent(RiderMainActivity.this, RiderWelcomeActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                waitingDialog.dismiss();
                Snackbar.make(rootRiderLayout, "Failed "+e.getMessage(), Snackbar.LENGTH_SHORT)
                        .show();
                btnSignIn.setEnabled(true);
            }
        });
    }

    private void showDialogForgotPwd() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RiderMainActivity.this);
        alertDialog.setTitle("FORGOT PASSWORD");
        alertDialog.setMessage("Please enter your email");

        LayoutInflater inflater = LayoutInflater.from(RiderMainActivity.this);
        View forgot_your_password = inflater.inflate(R.layout.layout_forgot_password,null);

        final MaterialEditText editEmail = (MaterialEditText) forgot_your_password.findViewById(R.id.editEmail);
        alertDialog.setView(forgot_your_password);

        alertDialog.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                final SpotsDialog waitingDialog = new SpotsDialog(RiderMainActivity.this);
                waitingDialog.show();

                auth.sendPasswordResetEmail(editEmail.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialogInterface.dismiss();
                                waitingDialog.dismiss();

                                Snackbar.make(rootRiderLayout, "Reset password link has been sent", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogInterface.dismiss();
                        waitingDialog.dismiss();

                        Snackbar.make(rootRiderLayout, ""+e.getMessage(), Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }


    private void showLoginDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("MASUK");
        dialog.setMessage("Please use email to login");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_login = inflater.inflate(R.layout.layout_login,null);

        final MaterialEditText editEmail = layout_login.findViewById(R.id.editEmail);
        final MaterialEditText editPassword = layout_login.findViewById(R.id.editPassword);

        dialog.setView(layout_login);

        dialog.setPositiveButton("MASUK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                btnSignIn.setEnabled(false);


                if (TextUtils.isEmpty(editEmail.getText().toString())) {
                    Snackbar.make(rootRiderLayout, "Please enter email address", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (TextUtils.isEmpty(editPassword.getText().toString())) {
                    Snackbar.make(rootRiderLayout, "Please enter password", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (editPassword.getText().length() < 6) {
                    Snackbar.make(rootRiderLayout, "Password too short !!!", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                final SpotsDialog waitingDialog = new SpotsDialog(RiderMainActivity.this);
                waitingDialog.show();

                auth.signInWithEmailAndPassword(editEmail.getText().toString(),editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waitingDialog.dismiss();
                                Paper.book().write(Common.rider_user_field,editEmail.getText().toString());
                                Paper.book().write(Common.rider_password_field,editPassword.getText().toString());
                                startActivity(new Intent(RiderMainActivity.this, RiderWelcomeActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        Snackbar.make(rootRiderLayout, "Failed "+e.getMessage(), Snackbar.LENGTH_SHORT)
                                .show();
                        btnSignIn.setEnabled(true);
                    }
                });

            }
        });
        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });



        dialog.show();
    }

    private void showRegisterDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("DAFTAR");
        dialog.setMessage("Please use email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_register = inflater.inflate(R.layout.layout_register,null);

        final MaterialEditText editEmail = layout_register.findViewById(R.id.editEmail);
        final MaterialEditText editPassword = layout_register.findViewById(R.id.editPassword);
        final MaterialEditText editName = layout_register.findViewById(R.id.editName);
        final MaterialEditText editPhone = layout_register.findViewById(R.id.editPhone);

        dialog.setView(layout_register);

        dialog.setPositiveButton("DAFTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if(TextUtils.isEmpty(editEmail.getText().toString())){
                    Snackbar.make(rootRiderLayout, "Please enter email address", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(TextUtils.isEmpty(editPhone.getText().toString())){
                    Snackbar.make(rootRiderLayout, "Please enter phone number", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(TextUtils.isEmpty(editPassword.getText().toString())){
                    Snackbar.make(rootRiderLayout, "Please enter password", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(editPassword.getText().length() < 6){
                    Snackbar.make(rootRiderLayout, "Password too short !!!", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                auth.createUserWithEmailAndPassword(editEmail.getText().toString(),editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Rider user = new Rider();
                                user.setEmail(editEmail.getText().toString());
                                user.setPassword(editPassword.getText().toString());
                                user.setName(editName.getText().toString());
                                user.setPhone(editPhone.getText().toString());

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootRiderLayout, "Register successfully", Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootRiderLayout, "Failed "+e.getMessage(), Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootRiderLayout, "Failed "+e.getMessage(), Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        });
        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();

    }

    public void onDriverClick(View v){
        startActivity(new Intent(RiderMainActivity.this, DriverMainActivity.class));
        finish();
    }



}
