package com.sorovi.android.firebasebatch1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sorovi.android.firebasebatch1.databinding.FragmentLoginBinding;
import com.sorovi.android.firebasebatch1.viewmodel.LoginViewModel;


public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private boolean isLogin = true;
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLogin = true;
                authenticate();
            }

        });
        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLogin = false;
                authenticate();
            }
        });
        loginViewModel.getErrMsgLiveData()
                .observe(getViewLifecycleOwner(), s ->
                        binding.errorMsgTV.setText(s));
        loginViewModel.authLiveData.observe(getViewLifecycleOwner(), authSate -> {
           if (authSate== LoginViewModel.AuthSate.AUTHENTICATED){
               Navigation.findNavController(container).popBackStack();
           }
        });
        return binding.getRoot();
    }

    private void authenticate() {
        final String email = binding.emailET.getText().toString();
        final String pass = binding.passwordET.getText().toString();
        // TODO: 1/23/2022 validate empty fields
        if (isLogin){
            loginViewModel.login(email,pass);
        }else {
            loginViewModel.register(email,pass);
        }
    }
}