package com.stiffrock.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.stiffrock.chat.utils.FragmentContainerActivity;


/**
 * Clase de la activity que contiene los fragments relacionados con el registro e inicio de sesión
 * del usuario.
 * <p>
 * Hereda de {@link FragmentContainerActivity}, clase que contiene comportamientos comunes entre
 * activities que contienen fragments.
 */
public class AuthActivity extends FragmentContainerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    /**
     * Inicia HomeActivity
     */
    public void navigateToHomeActivity() {
        Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}