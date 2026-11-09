package com.example.agenda;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agenda.controller.ContatoController;
import com.example.agenda.model.Contato;

// View: formulário de inclusão e alteração de contato
public class AdicionarContatoActivity extends AppCompatActivity {
    public static final String EXTRA_CONTATO = "contato";

    private EditText editNome, editEmail, editTelefone;
    private ImageView imageViewFoto;
    private ContatoController contatoController;
    private Contato contatoEmEdicao; // null = inclusão
    private String fotoUri = "";

    // abre o seletor de arquivos do sistema e guarda a permissão de leitura da imagem
    private final ActivityResultLauncher<String[]> selecionarFoto =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) return;
                try {
                    getContentResolver().takePersistableUriPermission(uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException ignored) {
                    // alguns provedores não dão permissão permanente; a foto vale nesta sessão
                }
                fotoUri = uri.toString();
                FotoUtil.mostrar(imageViewFoto, fotoUri);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adicionar_contato);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        contatoController = new ContatoController(this);
        inicializarViews();
        carregarContatoRecebido();
    }

    private void inicializarViews() {
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setNavigationOnClickListener(v -> finish());
        editNome = findViewById(R.id.editTextNome);
        editEmail = findViewById(R.id.editTextEmail);
        editTelefone = findViewById(R.id.editTextTelefone);
        imageViewFoto = findViewById(R.id.imageView); // o livro usava R.id.imageView2, que é da lista
        Button btnSalvar = findViewById(R.id.btnSalvar);
        Button btnSelecionarFoto = findViewById(R.id.btnSelecionarFoto);

        btnSalvar.setOnClickListener(v -> salvarContato());
        btnSelecionarFoto.setOnClickListener(v -> selecionarFoto.launch(new String[]{"image/*"}));
        FotoUtil.mostrar(imageViewFoto, null);
    }

    // modo alteração: veio um contato pela Intent → preenche o formulário
    @SuppressWarnings("deprecation")
    private void carregarContatoRecebido() {
        Intent intent = getIntent();
        contatoEmEdicao = Build.VERSION.SDK_INT >= 33
                ? intent.getSerializableExtra(EXTRA_CONTATO, Contato.class)
                : (Contato) intent.getSerializableExtra(EXTRA_CONTATO);
        if (contatoEmEdicao == null) return;

        ((Toolbar) findViewById(R.id.toolbar2)).setTitle("Editar Contato");
        editNome.setText(contatoEmEdicao.getNome());
        editEmail.setText(contatoEmEdicao.getEmail());
        editTelefone.setText(contatoEmEdicao.getTelefone());
        fotoUri = contatoEmEdicao.getFoto() == null ? "" : contatoEmEdicao.getFoto();
        FotoUtil.mostrar(imageViewFoto, fotoUri);
    }

    private void salvarContato() {
        String nome = editNome.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();

        // o livro mostrava o aviso mas salvava mesmo assim; aqui o return impede
        if (!ContatoController.dadosValidos(nome, telefone)) {
            Toast.makeText(this, "Nome e telefone são obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contatoEmEdicao == null) {
            contatoController.adicionarContato(nome, email, telefone, fotoUri);
            Toast.makeText(this, "Contato adicionado com sucesso", Toast.LENGTH_SHORT).show();
        } else {
            contatoController.atualizarContato(contatoEmEdicao.getId(), nome, email, telefone, fotoUri);
            Toast.makeText(this, "Contato atualizado", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        contatoController.close();
    }
}
