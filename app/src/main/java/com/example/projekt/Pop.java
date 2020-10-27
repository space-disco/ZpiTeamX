package com.example.projekt;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Pop extends Activity {


    private String selectedImagePath;
    static String imagePath = null;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    static boolean czyUploadowac=false;
     static  String obrazekURL=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.popwindows);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height * 0.8));


/*

        buttonDodawanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Query Referencja = FirebaseDatabase.getInstance().getReference().child("Przepisy");
               // DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Przepisy");
              //  usersRef.child("Groch z kapusta").setValue(new Przepis(obrazek.getText().toString(), autor.getText().toString(),ocena.getText().toString(),dataDodania.getText().toString()));
             //   usersRef.child("Groch z kapusta").setValueAsync(new Przepis(obrazek.getText().toString(), autor.getText().toString(),ocena.getText().toString(),dataDodania.getText().toString()));

            }
        });
     */




    }


    public void DodaniePrzepisu(View view) {

        final EditText nazwa,skladniki,sposobPrzygotowania;
        nazwa=findViewById(R.id.nazwa);
        skladniki=findViewById(R.id.skladniki);
        sposobPrzygotowania=findViewById(R.id.sposobPrzygotowania);
       // Date dzisiejszaData = Calendar.getInstance().getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dzisiejszaData = dateFormat.format(Calendar.getInstance().getTime());
        //dateTimeDisplay.setText(date);

        String obrazek = "https://firebasestorage.googleapis.com/v0/b/projekt-zpi-ad1f3.appspot.com/o/images%2F"+nazwa.getText().toString()+"?alt=media";

      //  Log.d("obrazekURL",obrazekURL);



        if(czyUploadowac==true)
            uploadPicture(nazwa.getText().toString());

        if(obrazekURL!=null) {
            obrazek = obrazekURL;
            Log.d("obrazek",obrazek);
        } else {Log.d("obrazekURL","JESTEM NULL");}

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String nickname =  user.getEmail();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Przepisy");
        usersRef.child(nazwa.getText().toString()).setValue(new Przepis(obrazek, nickname,"5",dzisiejszaData.toString(),skladniki.getText().toString(),sposobPrzygotowania.getText().toString(),nazwa.getText().toString()));
    }


    public void DodanieZdjecia(View view) {

        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        final int ACTIVITY_SELECT_IMAGE = 1234;
        startActivityForResult(i, ACTIVITY_SELECT_IMAGE);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1234) {
                Uri selectedImageUri = data.getData();

                selectedImagePath = getPath(selectedImageUri);
                imagePath=selectedImagePath;
                Log.d("sciezka", selectedImagePath);
                Log.d("sciezka", "dsasdasadsad");
                System.out.println("Image Path : " + selectedImagePath);
                imageUri = data.getData();

                storage= FirebaseStorage.getInstance();
                storageReference= storage.getReference();
                czyUploadowac=true;
                //uploadPicture();

            }
        }
    }

    private void uploadPicture(String nazwaPliku) {

        //final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/" + nazwaPliku);

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                     //   obrazekURL=taskSnapshot.getUploadSessionUri().toString();

                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                obrazekURL = uri.toString();

                                Log.d("obrazekURL",obrazekURL);
                            }
                        });

                       // Log.d("taskSnapshotURI",taskSnapshot.getStorage().getDownloadUrl().toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "nie udalo się zuploadować pliku", Toast.LENGTH_SHORT).show();
                    }
                });


    }





    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }





}


