package com.example.agenda;

import android.net.Uri;
import android.widget.ImageView;

// Mostra a foto do contato; se não houver (ou a imagem sumiu), mostra o avatar padrão
final class FotoUtil {
    private FotoUtil() {
    }

    static void mostrar(ImageView imageView, String foto) {
        if (foto != null && !foto.isEmpty()) {
            try {
                imageView.setImageURI(Uri.parse(foto));
                if (imageView.getDrawable() != null) return;
            } catch (SecurityException | IllegalArgumentException ignored) {
                // sem permissão ou arquivo apagado: cai no avatar padrão
            }
        }
        imageView.setImageResource(R.drawable.ic_contato);
    }
}
