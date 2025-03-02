package com.stiffrock.chat.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.stiffrock.chat.R;

/**
 * Clase abstracta de utilidad que centraliza la gestion de fragments
 */
public class FragmentContainerActivity extends AppCompatActivity {
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Animaciones de entrada y salida entre fragments
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        transaction.replace(R.id.fcv, fragment);

        Fragment loadedFragment = fragmentManager.findFragmentById(R.id.fcv);
        if (loadedFragment != null) transaction.addToBackStack(loadedFragment.getClass().getName());

        transaction.commit();
    }
}
