package com.online.ojek.ojekonline.Rider;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.online.ojek.ojekonline.Common.Common;
import com.online.ojek.ojekonline.Model.Rating;
import com.online.ojek.ojekonline.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RatingActivity extends AppCompatActivity {

    Button btnSubmit;
    MaterialRatingBar ratingBar;
    MaterialEditText txtComments;

    FirebaseDatabase database;
    DatabaseReference ratingDetailRef;
    DatabaseReference driverInformationRef;

    double ratingStars = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        database = FirebaseDatabase.getInstance();
        ratingDetailRef = database.getReference(Common.rating_tbl);
        driverInformationRef = database.getReference(Common.user_driver_tbl);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        ratingBar = (MaterialRatingBar) findViewById(R.id.ratingBar);
        txtComments = (MaterialEditText) findViewById(R.id.txtComment);

        ratingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                ratingStars = rating;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRatingDetails(Common.driverId);
            }
        });
    }

    private void submitRatingDetails(final String driverId) {
        final android.app.AlertDialog alertDialog = new SpotsDialog(this);
        alertDialog.show();

        Rating rating = new Rating();
        rating.setRatings(String.valueOf(ratingStars));
        rating.setComments(txtComments.getText().toString());

        ratingDetailRef.child(driverId)
                .push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ratingDetailRef.child(driverId)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        double averageStars = 0.0;
                                        int count = 0;
                                        for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                                            Rating rating = postSnapShot.getValue(Rating.class);
                                            averageStars+=Double.parseDouble(rating.getRatings());
                                            count++;
                                        }
                                        double finalAverage = averageStars/count;
                                        DecimalFormat df = new DecimalFormat("#.#");
                                        String valueUpdate = df.format(finalAverage);

                                        Map<String,Object> driverUpdateRating = new HashMap<>();
                                        driverUpdateRating.put("ratings", valueUpdate);

                                        driverInformationRef.child(Common.driverId)
                                                .updateChildren(driverUpdateRating)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        alertDialog.dismiss();
                                                        Toast.makeText(RatingActivity.this, "Thankyou for submit ;)", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        alertDialog.dismiss();
                                                        Toast.makeText(RatingActivity.this, "Rating update but cannot write to database", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        alertDialog.dismiss();
                        Toast.makeText(RatingActivity.this, "Rating failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
