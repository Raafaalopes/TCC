package com.example.cadastrarentrarfirebase;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ComprarAlmocoFragment extends Fragment {

    private EditText quantidadeEditText;
    private TextView precoTotalTextView;
    private int precoTicket = 5; //preço unitário do ticket
    private TicketViewModel ticketViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comprar_almoco, container, false);

        quantidadeEditText = view.findViewById(R.id.quantidade);
        precoTotalTextView = view.findViewById(R.id.precoTotal);
        ImageButton btAumentar = view.findViewById(R.id.btAumentar);
        ImageButton btDiminuir = view.findViewById(R.id.btDiminuir);
        Button botao5Tickets = view.findViewById(R.id.botao5Tickets);
        Button botao20Tickets = view.findViewById(R.id.botao20Tickets);
        Button botaoComprar = view.findViewById(R.id.botaoComprar);

        // inicializando o ViewModel
        ticketViewModel = new ViewModelProvider(requireActivity()).get(TicketViewModel.class);
        ticketViewModel.init(requireContext());

        botaoComprar.setOnClickListener(v-> mostrarResumoCompra());

        quantidadeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int quantidadeAtual = getQuantity();
                atualizarPrecoTotal(quantidadeAtual);

            }
        });


        // ação do botao aumentar
        btAumentar.setOnClickListener(v -> {
            int quantidadeAtual = getQuantity();
            setQuantity(quantidadeAtual + 1);
        });


        // ação botao diminuir
        btDiminuir.setOnClickListener(v -> {
            int quantidadeAtual = getQuantity();
            if (quantidadeAtual > 1) {
                setQuantity(quantidadeAtual - 1);
            }
        });

        botao5Tickets.setOnClickListener(v -> setQuantity(5));

        botao20Tickets.setOnClickListener(v -> setQuantity(20));

        return view;
    }



    // funcao para obter a quantidade atual
    private int getQuantity(){
        String textoQuantidade = quantidadeEditText.getText().toString();
        return textoQuantidade.isEmpty() ? 1: Integer.parseInt(textoQuantidade);
    }

    //funcao para definir a quantidade e atualizar o total
    private void setQuantity(int quantidade) {
        quantidadeEditText.setText(String.valueOf(quantidade));
        atualizarPrecoTotal(quantidade);
    }

    // funcao que atualiza o preco total baseado na quantidade
    private void atualizarPrecoTotal(int quantidade) {
        int precoTotal = quantidade * precoTicket;
        precoTotalTextView.setText("Total: R$ " + precoTotal + ",00");
    }

    private void mostrarResumoCompra(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_resumo_compra, null);

        //elementos do popup
        TextView quantidadeResumo = dialogView.findViewById(R.id.quantidadeResumo);
        TextView valorTotalResumo = dialogView.findViewById(R.id.valorTotalResumo);
        Button botaoConfirmar = dialogView.findViewById(R.id.botaoConfirmar);
        Button botaoCancelar = dialogView.findViewById(R.id.botaoCancelar);

        //obtem os valores atuais de quantidade e total
        int quantidade = getQuantity();
        int precoTotal = quantidade * precoTicket;

        // atualizando os textos do resumo
        quantidadeResumo.setText("Quantidade de tickets: " + quantidade);
        valorTotalResumo.setText("Valor total: R$" + precoTotal + ",00");

        AlertDialog dialog = builder.setView(dialogView).create();


        // atualiza a quantidade de tickets na aba de meus tickets e
        // mostra o resumo da compra
        botaoConfirmar.setOnClickListener(v -> {

            //atualiza a quantidade de tickets no ViewModel
            ticketViewModel.adicionarTickets(quantidade);


            //mostra o recibo
            mostrarRecibo(quantidade, precoTotal);

            //fecha o pop-up de resumo
            dialog.dismiss();
        });

        botaoCancelar.setOnClickListener(v -> dialog.dismiss());


        dialog.show();
    }

    // funcao para mostrar o recibo como pop-up
    private void mostrarRecibo(int quantidade, int precoTotal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_recibo, null);

        // elementos do pop-up de recibo
        TextView emailRecibo = dialogView.findViewById(R.id.emailRecibo);
        TextView dataRecibo = dialogView.findViewById(R.id.dataRecibo);
        TextView quantidadeRecibo = dialogView.findViewById(R.id.quantidadeRecibo);
        TextView valorTotalRecibo = dialogView.findViewById(R.id.valorTotalRecibo);
        Button botaoCompartilhar = dialogView.findViewById(R.id.botaoCompartilhar);
        ImageButton botaoFechar = dialogView.findViewById(R.id.botaoFechar);


        // preenchendo os campos do recibo
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        emailRecibo.setText("Usuário: " + user.getEmail());
        dataRecibo.setText("Data: " + getCurrentDate());
        quantidadeRecibo.setText("Quantidade de tickets: " + quantidade);
        valorTotalRecibo.setText("Valor Total: R$ " + precoTotal + ",00");

        AlertDialog dialog = builder.setView(dialogView).create();
        dialog.show();

        botaoFechar.setOnClickListener(v -> dialog.dismiss());
    }

    // funcao para ter a data atual
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
}