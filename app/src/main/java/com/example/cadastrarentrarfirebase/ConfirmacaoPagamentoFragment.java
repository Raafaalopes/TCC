package com.example.cadastrarentrarfirebase;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConfirmacaoPagamentoFragment extends Fragment {

    private int almocoTickets;
    private int cafeTickets;
    private int valorTotal;
    private String metodoPagamento;

    public ConfirmacaoPagamentoFragment(int almocoTickets, int cafeTickets, int valorTotal, String metodoPagamento) {
        this.almocoTickets = almocoTickets;
        this.cafeTickets = cafeTickets;
        this.valorTotal = valorTotal;
        this.metodoPagamento = metodoPagamento;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirmacao_pagamento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView mensagem = view.findViewById(R.id.mensagemConfirmacao);
        // se nao der certo verificar se nao é o pop up de recibo dando problema
        TextView emailRecibo = view.findViewById(R.id.emailRecibo);
        TextView dataRecibo = view.findViewById(R.id.dataRecibo);
        TextView quantidadeRecibo = view.findViewById(R.id.quantidadeRecibo);
        TextView valorTotalRecibo = view.findViewById(R.id.valorTotalRecibo);
        TextView metodoPagamentoRecibo = view.findViewById(R.id.metodoPagamentoRecibo);

        Button concluir = view.findViewById(R.id.concluirPagamento);


        mensagem.setText("Pagamento realizado com sucesso!");

        // Obter email do Firebase
        String emailUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        emailRecibo.setText("Email: " + emailUsuario);

        // Obter data atual
        String dataAtual = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        dataRecibo.setText("Data: " + dataAtual);

        // Exibir quantidade de tickets
        quantidadeRecibo.setText("Tickets - Almoço/Jantar: " + almocoTickets + "\nTickets - Café da Manhã: " + cafeTickets);

        // Exibir valor total
        valorTotalRecibo.setText("Valor total: R$ " + valorTotal + ",00");

        // Exibir método de pagamento
        metodoPagamentoRecibo.setText("Método de Pagamento: " + metodoPagamento);

        concluir.setOnClickListener(v -> {
//            TicketViewModel viewModel = new ViewModelProvider(requireActivity()).get(TicketViewModel.class);
//            TicketCafeViewModel cafeViewModel = new ViewModelProvider(requireActivity()).get(TicketCafeViewModel.class);
//
//            viewModel.adicionarTickets(almocoTickets);
//            cafeViewModel.adicionarTickets(cafeTickets);

            requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });
    }
}
