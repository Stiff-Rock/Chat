package com.stiffrock.chat.utils;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * Módulo de configuración para Glide.
 * <p>
 * Permite la generación de la API de Glide y personalización futura si es necesario.
 * Glide automáticamente detectará esta clase debido a la anotación @GlideModule.
 */
@GlideModule
public final class MyAppGlideModule extends AppGlideModule {
}