package com.example.cadastrarentrarfirebase;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TicketViewModel extends ViewModel {
    private final MutableLiveData<Integer> quantidadeTickets = new MutableLiveData<>(0);
    private SharedPreferences preferences;

    // metodo para inicializar o ViewModel com contexto
    public void init(Context context) {
        preferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        // carregar a quantidade de tickets armazenada ao iniciar o ViewModel
        int quantidade = preferences.getInt("quantidadeTickets", 0);
        quantidadeTickets.setValue(quantidade);
    }

    // Getter para observar a quantidade de tickets
    public LiveData<Integer> getQuantidadeTickets() {
        return quantidadeTickets;
    }

    // Função para adicionar tickets
    public void adicionarTickets(int quantidade) {
        int quantidadeAtual = quantidadeTickets.getValue() == null ? 0 : quantidadeTickets.getValue();
        quantidadeTickets.setValue(quantidadeAtual + quantidade);
        // salvar a nova quantidade de tickets no sharedpreferences
        preferences.edit().putInt("quantidadeTickets", quantidadeTickets.getValue()).apply();
    }

    public void decrementarTicket(){
        Integer ticketsAtuais = quantidadeTickets.getValue();
        if (ticketsAtuais != null && ticketsAtuais > 0) {
            quantidadeTickets.setValue(ticketsAtuais - 1);
            // salvar a nova quantidade de tickets no SharedPreferences
            preferences.edit().putInt("quantidadeTickets", quantidadeTickets.getValue()).apply();
        }
    }

    public String getCodigoUnico(){
        return preferences.getString("codigoUnico", null);
    }

    public void salvarCodigoUnico(String codigoUnico) {
        preferences.edit().putString("codigoUnico", codigoUnico).apply();
    }


    public void limparCodigoUnico() {
        preferences.edit().remove("codigoUnico").apply();
    }
}
