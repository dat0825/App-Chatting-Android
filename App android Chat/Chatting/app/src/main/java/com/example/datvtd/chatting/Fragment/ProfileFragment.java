package com.example.datvtd.chatting.Fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.datvtd.chatting.LoginActivity;
import com.example.datvtd.chatting.MainActivity;
import com.example.datvtd.chatting.Model.User;
import com.example.datvtd.chatting.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.usernameTextView = view.findViewById(R.id.text_username);
        this.profileImage = view.findViewById(R.id.profile_image);
        this.logOutButton = view.findViewById(R.id.b_logout);
        this.storageReference = FirebaseStorage.getInstance().getReference("profileImage");
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.progressDialog = new ProgressDialog(getContext());
        this.reference = FirebaseDatabase.getInstance().
                getReference("Users").child(firebaseUser.getUid());

        this.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                usernameTextView.setText(user.getUser());

                if (isAdded()) {
//                add ảnh vào profileImage
                    if (user.getImageURL() != null
                    &&user.getImageURL().equals("default")) {
                        profileImage.setImageResource(R.mipmap.ic_launcher_round);
                    } else {
                        Glide.with(ProfileFragment.this).load(user.getImageURL()).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        changeProfileImage();

        this.logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set status offline
                status("offline");

                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        return view;
    }

    public void changeProfileImage() {
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
    }

    public void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    public String getFileExtenstion(Uri uri) {

        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void upload_image() {
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        progressDialog.setCancelable(false);

        if (imageUri != null) {
            final StorageReference fileReference = storageReference
                    .child(System.currentTimeMillis() + "." + getFileExtenstion(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri dowloadUri = task.getResult();
                        String mUri = dowloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageURL", mUri);
                        reference.updateChildren(hashMap);

                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Fail load image", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_LONG).show();
            } else {
                upload_image();
            }
        }
    }

    //set trang thai online hay offline
    public void status(String status) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            reference.updateChildren(hashMap);
        }
    }

    private Button logOutButton;
    private CircleImageView profileImage;
    private TextView usernameTextView;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private Uri imageUri;
    private StorageTask uploadTask;
    private ProgressDialog progressDialog;
    static final int IMAGE_REQUEST = 1;
    private static final int RESULT_OK = -1;
}
