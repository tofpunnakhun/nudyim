package com.ayp.nudyim.photo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ayp.nudyim.R;
import com.ayp.nudyim.SignInActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Punnakhun on 10/18/2016.
 */

public class PhotoGallery extends Fragment {

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imageProfileUploader;
        public TextView nameUploader;
        public ImageView imageShow;

        public PhotoViewHolder(View v) {
            super(v);
            nameUploader = (TextView) itemView.findViewById(R.id.nameUploader);
            imageProfileUploader = (CircleImageView) itemView.findViewById(R.id.profileImageView);
            imageShow = (ImageView) itemView.findViewById(R.id.imageUpload);
        }
    }

    public static final String TRIP_CHILD = "trip";
    public static final String PHOTO_CHILD = "photo";

    private static final int SELECT_FILE1 = 555;
    private FloatingActionButton mFloatingActionButton;
    private FirebaseStorage mStorage;

    private String mUsername;
    private String mPhotoUrl;

    private String KEY_CHILD;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Photo, PhotoViewHolder>
            mFirebaseAdapter;

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_gallery, container, false);
        mStorage = FirebaseStorage.getInstance();
        KEY_CHILD = getArguments().getString("KEY_CHILD");

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // components
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(SELECT_FILE1);
            }
        });

        mUsername = "anonymous";
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity

            startActivity(new Intent(getActivity(), SignInActivity.class));
            getActivity().finish();
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Photo, PhotoViewHolder>(Photo.class,
                R.layout.item_photo,
                PhotoViewHolder.class,
                mFirebaseDatabaseReference.child(TRIP_CHILD).child(KEY_CHILD).child(PHOTO_CHILD)) {

            @Override
            protected void populateViewHolder(PhotoViewHolder viewHolder, Photo model, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.nameUploader.setText(model.getName());
                Glide.with(getActivity())
                        .load(model.getImageProfile())
                        .into(viewHolder.imageShow);

                if (model.getPhotoURL() == null) {
                    viewHolder.imageProfileUploader
                            .setImageDrawable(ContextCompat
                                    .getDrawable(getActivity(),
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(getActivity())
                            .load(model.getPhotoURL())
                            .into(viewHolder.imageProfileUploader);
                }
            }
        };

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);


        return view;
    }

    public void openGallery(int req_code) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select file to upload "), req_code);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Log.d("PhotoGallery", "Result OK");
            Uri selectedImageUri = data.getData();
            String selectedPath1 = selectedImageUri.toString();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                encodeBitmapAndSaveToFirebase(bitmap, selectedImageUri);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap, Uri uri) {
        UUID uuid = UUID.randomUUID();
        String namePath  = uuid.toString() + ".jpg";
        // Create a storage reference from our app
        StorageReference storageRef = mStorage.getReferenceFromUrl("gs://nudyim-9227e.appspot.com/");
        StorageReference riversRef = storageRef.child("images/" + namePath);

        UploadTask uploadTask = riversRef.putFile(uri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("PhotoGallery", String.valueOf(exception));
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("PhotoGallery", "Upload Success URI = " + downloadUrl.toString());
                Photo photo = new Photo(mUsername, mPhotoUrl,downloadUrl.toString());
                mFirebaseDatabaseReference.child(TRIP_CHILD).child(KEY_CHILD).child(PHOTO_CHILD).push().setValue(photo);
            }
        });
    }

}
