package com.example.agenda.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

// DAO: todas as operações SQL da entidade Contato (CRUD)
public class ContatoDAO {
    private SQLiteDatabase database;
    private final DatabaseHelper databaseHelper;

    public ContatoDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();
    }

    private ContentValues valores(Contato contato) {
        ContentValues values = new ContentValues();
        values.put("nome", contato.getNome());
        values.put("email", contato.getEmail());
        values.put("telefone", contato.getTelefone());
        values.put("foto", contato.getFoto());
        return values;
    }

    private Contato lerLinha(Cursor cursor) {
        Contato contato = new Contato();
        contato.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        contato.setNome(cursor.getString(cursor.getColumnIndexOrThrow("nome")));
        contato.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        contato.setTelefone(cursor.getString(cursor.getColumnIndexOrThrow("telefone")));
        contato.setFoto(cursor.getString(cursor.getColumnIndexOrThrow("foto")));
        return contato;
    }

    // Create
    public long adicionarContato(Contato contato) {
        return database.insert("contato", null, valores(contato));
    }

    // Read (todos)
    public List<Contato> listarContatos() {
        List<Contato> contatos = new ArrayList<>();
        try (Cursor cursor = database.query("contato", null, null, null, null, null,
                "nome COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                contatos.add(lerLinha(cursor));
            }
        }
        return contatos;
    }

    // Read (um) — consulta parametrizada evita SQL injection
    public Contato buscarContatoPorId(int id) {
        try (Cursor cursor = database.query("contato", null, "id=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return cursor.moveToFirst() ? lerLinha(cursor) : null;
        }
    }

    // Update
    public int atualizarContato(Contato contato) {
        return database.update("contato", valores(contato), "id=?",
                new String[]{String.valueOf(contato.getId())});
    }

    // Delete
    public int apagarContato(int id) {
        return database.delete("contato", "id=?", new String[]{String.valueOf(id)});
    }
}
