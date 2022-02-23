package com.sorovi.android.firebasebatch1.repos;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sorovi.android.firebasebatch1.models.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository {
    private final String COLLECTION_EXPENSE = "Expenses";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<Void> addNewExpense(Expense expense){
        final DocumentReference docRef = db.collection(COLLECTION_EXPENSE).document();
        expense.setId(docRef.getId());
       return docRef.set(expense);
    }
    public LiveData<List<Expense>> getAllExpense(String uid){
        final MutableLiveData<List<Expense>> expenseListLiveData = new MutableLiveData<>();
        db.collection(COLLECTION_EXPENSE).whereEqualTo("uid",uid)
                //.orderBy("amount")
                //.whereGreaterThan("amount",100)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
            if (error != null) return;
            final List<Expense> temList = new ArrayList<>();
            for (DocumentSnapshot doc : value.getDocuments()){
                temList.add(doc.toObject(Expense.class));
            }
            expenseListLiveData.postValue(temList);
        });
        return expenseListLiveData;

    }
}
