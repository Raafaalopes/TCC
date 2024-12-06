package com.example.cadastrarentrarfirebase;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

public class PagamentoFragment extends Fragment {

    private int almocoTickets;
    private int cafeTickets;
    private int valorTotal;

    // Construtor do Fragment com os valores de tickets e total
    public PagamentoFragment(int almocoTickets, int cafeTickets, int valorTotal) {
        this.almocoTickets = almocoTickets;
        this.cafeTickets = cafeTickets;
        this.valorTotal = valorTotal;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pagamento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referências aos componentes da tela
        TextView resumoTickets = view.findViewById(R.id.resumoTickets);
        TextView totalPagamento = view.findViewById(R.id.totalPagamento);
        RadioGroup formasPagamento = view.findViewById(R.id.formasPagamento);
        Button finalizarPagamento = view.findViewById(R.id.finalizarPagamento);

        // Configurando os textos
        resumoTickets.setText("Almoço/Jantar: " + almocoTickets + "\nCafé da manhã: " + cafeTickets);
        totalPagamento.setText("Total: R$" + valorTotal + ",00");

        finalizarPagamento.setOnClickListener(v -> {
            // Verificar se alguma forma de pagamento foi selecionada
            int formaPagamentoSelecionadaId = formasPagamento.getCheckedRadioButtonId();
            if (formaPagamentoSelecionadaId == -1) {
                // Nenhuma opção foi selecionada
                Toast.makeText(requireContext(), "Por favor, selecione uma forma de pagamento.", Toast.LENGTH_SHORT).show();
            } else {
                // Atualizar os tickets diretamente nos ViewModels
                TicketViewModel ticketViewModel = new TicketViewModel();
                ticketViewModel.init(requireContext()); // Inicializar com contexto
                ticketViewModel.adicionarTickets(almocoTickets); // Adiciona os tickets de almoço/janta

                TicketCafeViewModel ticketCafeViewModel = new TicketCafeViewModel();
                ticketCafeViewModel.init(requireContext()); // Inicializar com contexto
                ticketCafeViewModel.adicionarTickets(cafeTickets); // Adiciona os tickets de café da manhã

                // identificar o metodo de pagamento
                String metodoPagamento = "";
                if (formaPagamentoSelecionadaId == R.id.cartaoCredito) {
                    metodoPagamento = "Cartão de Crédito";
                } else if (formaPagamentoSelecionadaId == R.id.cartaoDebito) {
                    metodoPagamento = "Cartão de Débito";
                } else if (formaPagamentoSelecionadaId == R.id.pix) {
                    metodoPagamento = "PIX";
                }


                // Redirecionar para a tela de confirmação com os dados necessarios
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_conteiner, new ConfirmacaoPagamentoFragment(almocoTickets, cafeTickets, valorTotal, metodoPagamento))
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
