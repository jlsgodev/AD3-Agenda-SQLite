package com.example.agenda.controller;

import android.content.Context;

import com.example.agenda.model.Contato;
import com.example.agenda.model.ContatoDAO;

import java.util.List;

// Controller: intermedia View (Activities) e Model (DAO). O SQL fica só no Model.
public class ContatoController {
    private final ContatoDAO contatoDAO;

    public ContatoController(Context context) {
        contatoDAO = new ContatoDAO(context);
        contatoDAO.open();
    }

    // regra de negócio: nome e telefone são obrigatórios
    public static boolean dadosValidos(String nome, String telefone) {
        return nome != null && !nome.trim().isEmpty()
                && telefone != null && !telefone.trim().isEmpty();
    }

    public long adicionarContato(String nome, String email, String telefone, String foto) {
        return contatoDAO.adicionarContato(new Contato(nome, email, telefone, foto));
    }

    public List<Contato> listarContatos() {
        return contatoDAO.listarContatos();
    }

    public Contato buscarContato(int id) {
        return contatoDAO.buscarContatoPorId(id);
    }

    public int atualizarContato(int id, String nome, String email, String telefone, String foto) {
        return contatoDAO.atualizarContato(new Contato(id, nome, email, telefone, foto));
    }

    public int apagarContato(int id) {
        return contatoDAO.apagarContato(id);
    }

    public void close() {
        contatoDAO.close();
    }
}
