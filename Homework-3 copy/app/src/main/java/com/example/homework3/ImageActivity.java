package com.example.homework3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    // A reference for real-time database
    private DatabaseReference mDatabaseReference;
    // A list of upload item
    private List<Upload> mUploads;

    StorageReference mImageRef;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_image);
         setContentView(R.layout.image_retrieve);
        // The image view to
        imageView = findViewById(R.id.get_image);

        mImageRef = FirebaseStorage.getInstance().getReference("images/screenshot.jpg");;

        Picasso.get()
                .load("https://firebasestorage.googleapis.com/v0/b/homework3-5e976" +
                        ".appspot.com/o/images%2F1589437260018.jpg?alt=media&token=673c4920-471c-4923-bc0e-3fa96ba719bb")
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(imageView);


        //RetrieveImage();

        /*

        //TRYING TO GET RECYCLER VIEW TO WORK
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();

        // The images are stored under uploads. Maybe use the userID for this??
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        // Retrieving the data out of the uploads node
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            // This will be called when the images are added as well
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Loop through dataSnapshot to get our upload in the "uploads"
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Upload upload = postSnapshot.getValue(Upload.class);
                    // Add it to the list
                    mUploads.add(upload);
                }

                // Pass Context and List of uploads to the adapter
                mAdapter = new ImageAdapter(ImageActivity.this, mUploads);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImageActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

         */

    }

    /*********
     *
     * NOT NEEDED ANYMORE. USE PICASSO
     *
     */
    public void RetrieveImage() {
        // Testting on a sample image, that is in Storage.
        mImageRef = FirebaseStorage.getInstance().getReference("images/screenshot.jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        mImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);

                imageView.setMinimumHeight(dm.heightPixels);
                imageView.setMinimumWidth(dm.widthPixels);
                imageView.setImageBitmap(bm);

                Toast.makeText(getApplicationContext(),"Firebase Retrieved Success",Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
}
