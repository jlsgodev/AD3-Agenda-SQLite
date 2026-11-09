package com.example.agenda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.agenda.controller.ContatoController;
import com.example.agenda.model.Contato;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

// View: lista de contatos
public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private TextView textVazio;
    private FloatingActionButton fabAdicionar;
    private final List<Contato> contatos = new ArrayList<>();
    private ArrayAdapter<Contato> adapter;
    private ContatoController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // um controller só, criado uma vez (o livro criava um novo a cada recarga)
        controller = new ContatoController(this);
        inicializarViews();
        configurarAdapter();
        configurarListeners();
    }

    private void inicializarViews() {
        listView = findViewById(R.id.listView);
        textVazio = findViewById(R.id.textVazio);
        fabAdicionar = findViewById(R.id.fabAdicionar);
        listView.setEmptyView(textVazio);
    }

    private void configurarAdapter() {
        adapter = new ArrayAdapter<>(this, R.layout.list_item, contatos) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                }
                Contato contato = getItem(position);
                TextView textNome = convertView.findViewById(R.id.textNomeLista);
                TextView textTelefone = convertView.findViewById(R.id.textTelLista);
                ImageView imageFoto = convertView.findViewById(R.id.imageFotoLista);
                textNome.setText(contato.getNome());
                textTelefone.setText(contato.getTelefone());
                FotoUtil.mostrar(imageFoto, contato.getFoto());
                return convertView;
            }
        };
        listView.setAdapter(adapter);
    }

    private void carregarContatos() {
        contatos.clear();
        contatos.addAll(controller.listarContatos());
        adapter.notifyDataSetChanged();
    }

    private void configurarListeners() {
        // botão flutuante: novo contato (formulário vazio)
        fabAdicionar.setOnClickListener(v ->
                startActivity(new Intent(this, AdicionarContatoActivity.class)));

        // clique curto: alterar (formulário preenchido)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, AdicionarContatoActivity.class);
            intent.putExtra(AdicionarContatoActivity.EXTRA_CONTATO, contatos.get(position));
            startActivity(intent);
        });

        // clique longo: excluir, com confirmação
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Contato contato = contatos.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Confirmação de Exclusão")
                    .setMessage("Deseja realmente excluir o contato " + contato.getNome() + "?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        controller.apagarContato(contato.getId());
                        carregarContatos();
                        Toast.makeText(this, "Contato excluído", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Não", null)
                    .show();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarContatos(); // ao voltar do formulário, recarrega a lista
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.close();
    }
}
