package com.example.cadastrarentrarfirebase;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TicketCafeViewModel extends ViewModel {
    private final MutableLiveData<Integer> ticketsDisponiveis = new MutableLiveData<>(0);
    private SharedPreferences preferences;

    public void init(Context context) {
        preferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        // carregar a quantidade de tikets armazenada ao iniciar o ViewModel
        int quantidade = preferences.getInt("ticketsDisponiveis", 0);
        ticketsDisponiveis.setValue(quantidade);
    }

    // para observar a quantidade de tickets
    public LiveData<Integer> getTicketsDisponiveis() {
        return ticketsDisponiveis;
    }

    public void adicionarTickets(int quantidade) {
        int ticketsAtuais = ticketsDisponiveis.getValue() == null ? 0 : ticketsDisponiveis.getValue();
        ticketsDisponiveis.setValue(ticketsAtuais + quantidade);
        // salvar a nova quantidade de tickets no sharedPreferences
        preferences.edit().putInt("ticketsDisponiveis", ticketsDisponiveis.getValue()).apply();
    }

    public void decrementarTicket() {
        Integer ticketsAtuais = ticketsDisponiveis.getValue();
        if (ticketsAtuais != null && ticketsAtuais > 0) {
            ticketsDisponiveis.setValue(ticketsAtuais - 1);
            // salvar a nova quantidade de tickets no SharedPreferences
            preferences.edit().putInt("ticketsDisponiveis", ticketsDisponiveis.getValue()).apply();
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
