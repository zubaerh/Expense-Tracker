package com.sorovi.android.firebasebatch1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sorovi.android.firebasebatch1.adapters.ExpenseAdapter;
import com.sorovi.android.firebasebatch1.databinding.BottomSheetAddExpenseLayoutBinding;
import com.sorovi.android.firebasebatch1.databinding.FragmentExpanseListBinding;
import com.sorovi.android.firebasebatch1.models.Expense;
import com.sorovi.android.firebasebatch1.viewmodel.ExpenseViewModel;
import com.sorovi.android.firebasebatch1.viewmodel.LoginViewModel;

import java.io.ByteArrayOutputStream;


public class ExpanseListFragment extends Fragment {


    private FragmentExpanseListBinding binding;
    private BottomSheetAddExpenseLayoutBinding bottomSheetBinding;
    private LoginViewModel loginViewModel;
    private ExpenseViewModel expenseViewModel;
    private boolean isSaving = false;
    private ImageView memoImageView;
    private String imageUrl;
    private final String TAG = ExpanseListFragment.class.getSimpleName();
    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK){
                Uri photoUri = result.getData().getData();
                memoImageView.setImageURI(photoUri);
                //uploadPhoto();
            }
        }


    });
    private void uploadPhoto(String title, double amount) {
        final StorageReference photoRef = FirebaseStorage.getInstance().getReference().child("memos/"+System.currentTimeMillis());

        // Get the data from an ImageView as bytes
        memoImageView.setDrawingCacheEnabled(true);
        memoImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) memoImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = photoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Log.e(TAG,"onFailure: "+ )
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    final Expense expense = new Expense(null,title,amount,System.currentTimeMillis(),loginViewModel.getUser().getUid(), imageUrl);
                    expenseViewModel.addExpense(expense);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });

    }
    public ExpanseListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExpanseListBinding.inflate(inflater);
        bottomSheetBinding = BottomSheetAddExpenseLayoutBinding.inflate(inflater);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        expenseViewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);
        loginViewModel.authLiveData.observe(getViewLifecycleOwner(), authSate -> {
            if (authSate == LoginViewModel.AuthSate.UNAUTHENTICATED){
                Navigation.findNavController(container).navigate(R.id.login_action);
            }
        });
        final ExpenseAdapter adapter = new ExpenseAdapter();
        binding.expenseRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.expenseRV.setAdapter(adapter);
        if (loginViewModel.getUser() != null){
            expenseViewModel.getAllExpensesByUserId(loginViewModel.getUser().getUid()).observe(getViewLifecycleOwner(),expenses -> {
                adapter.submitList(expenses);
            });
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final CardView cv = view.findViewById(R.id.addExpenseLayout);
        final BottomSheetBehavior<CardView> behavior = BottomSheetBehavior.from(cv);
        final EditText titleET = cv.findViewById(R.id.titleInputET);
        final EditText amountET = cv.findViewById(R.id.amountInputET);
        final Button saveBtn = cv.findViewById(R.id.saveBtn);
        final Button galleryBtn = cv.findViewById(R.id.galleryBtn);
        memoImageView = cv.findViewById(R.id.memoImage);


        binding.addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }else {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                launcher.launch(intent);
               // startActivityForResult(intent,999);
            }
        });
       saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                final String title = titleET.getText().toString();
                final double amount = Double.parseDouble(amountET.getText().toString());
                uploadPhoto(title, amount);

                titleET.setText("");
                amountET.setText("");
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }


}