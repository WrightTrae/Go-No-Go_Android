package com.wright.android.t_minus.main_tabs.photos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.wright.android.t_minus.R;
import com.wright.android.t_minus.objects.ImageObj;
import com.wright.android.t_minus.objects.PadLocation;
import com.wright.android.t_minus.settings.PreferencesActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class PhotosFragment extends Fragment {

    public static final int MY_CAMERA_PERMISSION_CODE = 1231;
    public static final int CAMERA_REQUEST = 0x1010;
    private static final String STRING_AUTHORITY = "com.wright.android.t_minus.ACCESS_DATA";
    private static final String IMAGE_FOLDER = "images/";
    FirebaseStorage storage;
    StorageReference storageReference;
    private PhotoAdapter photoAdapter;
    private String image_path;
    private DatabaseReference activeImageDatabaseReference;

    public PhotosFragment() {
        // Required empty public constructor
    }

    public static PhotosFragment newInstance() {
        return new PhotosFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForSignIn(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        return inflater.inflate(R.layout.fragment_photos, container, false);
    }

    private boolean checkForSignIn(Boolean showToast){
        if(getView() == null){
            return false;
        }
        View signInLayout = getView().findViewById(R.id.photos_sign_in_layout);
        View fab = getView().findViewById(R.id.photo_capture_fab);
        Boolean userSigned = FirebaseAuth.getInstance().getCurrentUser() != null;
        if(!userSigned){
            if(showToast){
                Toast.makeText(getContext(), "Sign In Required",Toast.LENGTH_SHORT).show();
            }
            signInLayout.setVisibility( View.VISIBLE);
            fab.setVisibility(View.GONE);
            getView().findViewById(R.id.photo_sign_in_button).setOnClickListener((View v)->{
                Intent intent = new Intent(getContext(), PreferencesActivity.class);
                startActivity(intent);
            });
        }else{
            signInLayout.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        }
        return userSigned;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(checkForSignIn(false)){
            resetPushPath();
            getLikedImages();
            FloatingActionButton fab = view.findViewById(R.id.photo_capture_fab);
            fab.setOnClickListener((View v) -> showDataFromFAB());

            GridView gridView = view.findViewById(R.id.photoGrid);
            photoAdapter = new PhotoAdapter(getContext());
            gridView.setAdapter(photoAdapter);
        }
    }

    private void resetPushPath(){
        if(getActivity()==null){
            return;
        }
        activeImageDatabaseReference = FirebaseDatabase.getInstance().getReference().child("images").push();
        image_path = getActivity().getExternalFilesDir(IMAGE_FOLDER)+"/"+activeImageDatabaseReference.getKey()+".jpg";
    }

    private void showDataFromFAB(){
        if(checkForSignIn(true) && getActivity() != null) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_CAMERA_PERMISSION_CODE);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputUri());
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
                showDataFromFAB();
            } else {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    public void onActivityResult ( int requestCode, int resultCode, Intent data){
        if(getContext()==null||getImageFile()==null){
            return;
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap;
            File imageFile = getImageFile();
            ExifInterface exif;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), getOutputUri());
                exif = new ExifInterface(
                        imageFile.getAbsolutePath());
            } catch (Exception e) {
                Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            int rotate = 0;
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            bitmap.recycle();
            uploadImage(byteArray);
        }

    }

    private Uri getOutputUri() {
        if(getContext() == null){
            return null;
        }
        File f = getImageFile();
        return (f==null?null: FileProvider.getUriForFile(getContext(),STRING_AUTHORITY,f));
    }

    private File getImageFile(){
        File imageFile = new File(image_path);
        boolean created = false;
        try {
            created = imageFile.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return imageFile;
    }

    private void downloadImages(ArrayList<String> likedImagesIds){
        if(checkForSignIn(true)) {
            DatabaseReference imageRef = FirebaseDatabase.getInstance().getReference().child("images");
            imageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String id = snapshot.getKey();
                        String path = (String) snapshot.child("path").getValue();
                        long likes = (long)snapshot.child("likes").getValue();
                        boolean reported = (boolean)snapshot.child("reported").getValue();
                        String time = (String)snapshot.child("time_stamp").getValue();
                        String userId = (String)snapshot.child("user_id").getValue();
                        ImageObj imageObj = new ImageObj(
                                id,
                                path == null ? "" : path,
                                likes,
                                reported,
                                time == null ? "" : time,
                                userId == null ? "" : userId);
                        if (imageObj.getPath().equals("")||imageObj.isReported()) {
                            continue;
                        }
                        StorageReference storageRef = storageReference.child(imageObj.getPath());
                        storageRef.getDownloadUrl().addOnCompleteListener((@NonNull Task<Uri> task) -> {
                            if (task.getException() == null) {
                                imageObj.setDownloadUrl(task.getResult().toString());
                                downloadFinished(likedImagesIds, imageObj);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void downloadFinished(ArrayList<String> list, ImageObj imageObj){
        boolean isLiked = list.stream().anyMatch((listId -> listId.equals(imageObj.getId())));
        imageObj.setLiked(isLiked);
        photoAdapter.addData(imageObj);
        photoAdapter.notifyDataSetChanged();
    }

    private void getLikedImages(){
        if(checkForSignIn(false)) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(FirebaseAuth.getInstance().getUid());
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> likedImagesId = new ArrayList<>();
                    for(DataSnapshot snap: dataSnapshot.child("likes").getChildren()){
                        likedImagesId.add(snap.getKey());
                    }
                    downloadImages(likedImagesId);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    downloadImages(new ArrayList<>());
                }
            });
        }
    }

    private void uploadImage(byte[] byteArray) {
        if(byteArray != null&&checkForSignIn(true))
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String uuid = activeImageDatabaseReference.getKey();
            String stringUri = "images/"+ uuid;
            StorageReference ref = storageReference.child(stringUri);
            ref.putBytes(byteArray)
                    .addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot)-> {
                        progressDialog.dismiss();
                        addImageUrlToDatabase(stringUri);
                        photoAdapter.resetData();
                        getLikedImages();
                        Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener((@NonNull Exception e)-> {
                        progressDialog.dismiss();
                        resetPushPath();
                        Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener((UploadTask.TaskSnapshot taskSnapshot)-> {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    });
        }
    }

    private void addImageUrlToDatabase(String stringUri){
        if(checkForSignIn(true)) {
            HashMap<String, Object> imageMap = new HashMap<>();
            imageMap.put("path", stringUri);
            imageMap.put("likes", 0);
            imageMap.put("reported", false);
            imageMap.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            Calendar calendar = Calendar.getInstance();
            imageMap.put("time_stamp", calendar.getTime().toString());
            activeImageDatabaseReference.setValue(imageMap);

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(FirebaseAuth.getInstance().getUid()).child("images");
            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put(activeImageDatabaseReference.getKey(), stringUri);
            userRef.updateChildren(userMap);
            resetPushPath();
        }
    }
}
