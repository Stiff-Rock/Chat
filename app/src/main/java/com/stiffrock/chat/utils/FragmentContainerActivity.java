package com.stiffrock.chat.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.stiffrock.chat.R;

//TODO: HACER QUE SI SE HA HECHOAUTO LOGIN NO PUEDA VOVLER A CON LA FLECHA DE HACIA ATRAS AL LOGIN
public class FragmentContainerActivity extends AppCompatActivity {
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Animaciones de entrada y salida entre fragments
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        transaction.replace(R.id.fcv, fragment);

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fcv);
        if (currentFragment != null)
            transaction.addToBackStack(currentFragment.getClass().getName());

        transaction.commit();
    }
}
