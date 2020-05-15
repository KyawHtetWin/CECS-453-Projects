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


    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;

    private FirebaseUser currentUser;

    public FirebaseHandler(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users Activity");
        mStorageReference = FirebaseStorage.getInstance().getReference("Routes");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void pushRunData(RunData runData, String uid) {
        mDatabaseReference.child(uid).push().setValue(runData);
    }

    public UploadTask pushRouteImage(byte[] data, String uid) {
        UUID fileName = UUID.randomUUID();
        return mStorageReference.child(uid).child(fileName+".jpeg").putBytes(data);
    }



    public FirebaseUser getCurrentUser() {
        return currentUser;
    }
}
