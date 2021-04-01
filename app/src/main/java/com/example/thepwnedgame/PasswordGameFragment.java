package com.example.thepwnedgame;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.thepwnedgame.viewmodel.PasswordViewModel;

public class PasswordGameFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.password_fragment_layout, container,false);
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**Verifica: mettere i testi giusti al fragment --> ViewModel*/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();

        TextView passTextView = view.findViewById(R.id.passwordTextView);
        TextView passwordStrength = view.findViewById(R.id.passwordStrengthTextView);
        PasswordViewModel passwordViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(PasswordViewModel.class);

        passwordViewModel.getPassword().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                passTextView.setText(s);
            }
        });

        if(this.getId()==R.id.firstPasswordFragment){
            passwordViewModel.getValue().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    passwordStrength.setText(Integer.toString(integer));
                }
            });
        } else {
            passwordStrength.setText("***");
        }

    }

    //TODO: settare l'onclick al fragment per inviare l'answer
    @Override
    public void onClick(View v) {

    }
}
