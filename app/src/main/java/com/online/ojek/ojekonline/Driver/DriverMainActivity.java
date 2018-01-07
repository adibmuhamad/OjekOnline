package com.online.ojek.ojekonline.Driver;

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
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.online.ojek.ojekonline.Common.Common;
import com.online.ojek.ojekonline.Model.Driver;
import com.online.ojek.ojekonline.R;
import com.online.ojek.ojekonline.Rider.RiderMainActivity;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DriverMainActivity extends AppCompatActivity {

    Button btnSignIn, btnRegister;
    RelativeLayout rootLayout;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

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
        setContentView(R.layout.activity_driver_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference(Common.user_driver_tbl);


        btnSignIn = (Button)findViewById(R.id.btnSignInDriver);
        btnRegister = (Button)findViewById(R.id.btnRegisterDriver);
        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

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
                            Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT)
                                    .show();
                            return;
                        }

                        if (TextUtils.isEmpty(editPassword.getText().toString())) {
                            Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT)
                                    .show();
                            return;
                        }

                        if (editPassword.getText().length() < 6) {
                            Snackbar.make(rootLayout, "Password too short !!!", Snackbar.LENGTH_SHORT)
                                    .show();
                            return;
                        }

                        final SpotsDialog waitingDialog = new SpotsDialog(DriverMainActivity.this);
                        waitingDialog.show();

                        auth.signInWithEmailAndPassword(editEmail.getText().toString(),editPassword.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        waitingDialog.dismiss();
                                        startActivity(new Intent(DriverMainActivity.this, DriverWelcomeActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waitingDialog.dismiss();
                                Snackbar.make(rootLayout, "Failed "+e.getMessage(), Snackbar.LENGTH_SHORT)
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
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(TextUtils.isEmpty(editPhone.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter phone number", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(TextUtils.isEmpty(editPassword.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(editPassword.getText().length() < 6){
                    Snackbar.make(rootLayout, "Password too short !!!", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                auth.createUserWithEmailAndPassword(editEmail.getText().toString(),editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Driver user =new Driver();
                                user.setEmail(editEmail.getText().toString());
                                user.setPassword(editPassword.getText().toString());
                                user.setName(editName.getText().toString());
                                user.setPhone(editPhone.getText().toString());

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout, "Register successfully", Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout, "Failed "+e.getMessage(), Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootLayout, "Failed "+e.getMessage(), Snackbar.LENGTH_SHORT)
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

    public void onRiderClick(View v){
        startActivity(new Intent(DriverMainActivity.this, RiderMainActivity.class));
        finish();
    }
}
