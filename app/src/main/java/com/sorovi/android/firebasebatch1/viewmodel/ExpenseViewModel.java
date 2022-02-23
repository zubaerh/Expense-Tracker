package com.sorovi.android.firebasebatch1.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sorovi.android.firebasebatch1.models.Expense;
import com.sorovi.android.firebasebatch1.repos.ExpenseRepository;

import java.util.List;

public class ExpenseViewModel extends ViewModel {
    private  final ExpenseRepository repository;

    public ExpenseViewModel(){
        repository = new ExpenseRepository();
    }
    public void addExpense(Expense expense){
        repository.addNewExpense(expense).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    public LiveData<List<Expense>> getAllExpensesByUserId(String uid){
        return repository.getAllExpense(uid);
    }
}
