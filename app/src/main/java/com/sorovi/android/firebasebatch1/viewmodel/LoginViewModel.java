package com.sorovi.android.firebasebatch1.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {
    public enum AuthSate {
        AUTHENTICATED, UNAUTHENTICATED
    }

    public MutableLiveData<AuthSate> authLiveData;
    private MutableLiveData<String> errMsgLiveData;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public LoginViewModel(){
        authLiveData = new MutableLiveData<>();
        errMsgLiveData = new MutableLiveData<>();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user==null){
            authLiveData.postValue(AuthSate.UNAUTHENTICATED);
        }else {
            authLiveData.postValue(AuthSate.AUTHENTICATED);
        }
    }

    public FirebaseUser getUser() {
        return user;
    }

    public LiveData<String> getErrMsgLiveData() {
        return errMsgLiveData;
    }

    public void login(String email, String password){
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            user = authResult.getUser();
            authLiveData.postValue(AuthSate.AUTHENTICATED);
        }).addOnFailureListener(e -> {
          errMsgLiveData.postValue(e.getLocalizedMessage());
        });
    }
    public void register(String email, String password){
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            user = authResult.getUser();
            authLiveData.postValue(AuthSate.AUTHENTICATED);
        }).addOnFailureListener(e -> {
            errMsgLiveData.postValue(e.getLocalizedMessage());
        });
    }
    public void logout(){

    }
}
