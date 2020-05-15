/*****
 * This class handles uploading of the running stats of the user to the Firebase. All the
 * stats will be stored in Realtime Database and a screenshot of a route image will
 * be stored in Storage.
 ******/

package com.example.runningmate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class FirebaseHandler {

    // References
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private FirebaseUser currentUser;


    public FirebaseHandler() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users Activity");
        mStorageReference = FirebaseStorage.getInstance().getReference("Routes");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    // Push the run data to the database
    public void pushRunData(RunData runData, String uid) {
        mDatabaseReference.child(uid).push().setValue(runData);
    }

    // Push the route image of the run to the storage
    public UploadTask pushRouteImage(byte[] data, String uid) {
        // Gives a unique name to each image
        UUID fileName = UUID.randomUUID();
        return mStorageReference.child(uid).child(fileName+".jpeg").putBytes(data);
    }


    public FirebaseUser getCurrentUser() {
        return currentUser;
    }
}
