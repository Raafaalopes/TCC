package com.example.cadastrarentrarfirebase;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ComprarCafeFragment extends Fragment {

    private EditText quantidadeEditTextCafe;
    private TextView precoTotalTextViewCafe;
    private int precoTicketCafe = 2;
    private TicketCafeViewModel ticketCafeViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comprar_cafe, container, false);

        quantidadeEditTextCafe = view.findViewById(R.id.quantidadeCafe);
        precoTotalTextViewCafe = view.findViewById(R.id.precoTotalCafe);
        ImageButton btAumentarCafe = view.findViewById(R.id.btAumentarCafe);
        ImageButton btDiminuirCafe = view.findViewById(R.id.btDiminuirCafe);
        Button botao5TicketsCafe = view.findViewById(R.id.botao5TicketsCafe);
        Button botao20TicketsCafe = view.findViewById(R.id.botao20TicketsCafe);
        Button botaoComprarCafe = view.findViewById(R.id.botaoComprarCafe);

        ticketCafeViewModel = new ViewModelProvider(requireActivity()).get(TicketCafeViewModel.class);
        ticketCafeViewModel.init(requireContext());

        botaoComprarCafe.setOnClickListener(v -> mostrarResumoCompra());

        quantidadeEditTextCafe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int quantidadeAtualCafe = getQuantity();
                atualizarPrecoTotalCafe(quantidadeAtualCafe);
            }
        });

        //acao do botao aumentar
        btAumentarCafe.setOnClickListener(v -> {
            int quantidadeAtualCafe = getQuantity();
            setQuantity(quantidadeAtualCafe + 1);
        });

        btDiminuirCafe.setOnClickListener(v -> {
            int quantidadeAtualCafe = getQuantity();
            if (quantidadeAtualCafe > 1) { // para impedir valores negativos
                setQuantity(quantidadeAtualCafe - 1);
            }
        });

        botao5TicketsCafe.setOnClickListener(v -> setQuantity(5));

        botao20TicketsCafe.setOnClickListener(v -> setQuantity(20));

        return view;
    }

    private int getQuantity(){
        String textoQuantidadeCafe = quantidadeEditTextCafe.getText().toString();
        return textoQuantidadeCafe.isEmpty() ? 1: Integer.parseInt(textoQuantidadeCafe);
    }

    private void setQuantity(int quantidadeCafe) {
        quantidadeEditTextCafe.setText(String.valueOf(quantidadeCafe));
        atualizarPrecoTotalCafe(quantidadeCafe);
    }

    private void atualizarPrecoTotalCafe(int quantidadeCafe) {
        int precoTotalCafe = quantidadeCafe * precoTicketCafe;
        precoTotalTextViewCafe.setText("Total: R$ " + precoTotalCafe + ",00");
    }

    private void mostrarResumoCompra() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_resumo_compra, null);

        TextView quantidadeResumo = dialogView.findViewById(R.id.quantidadeResumo);
        TextView valorTotalResumo = dialogView.findViewById(R.id.valorTotalResumo);
        Button botaoConfirmar = dialogView.findViewById(R.id.botaoConfirmar);
        Button botaoCancelar = dialogView.findViewById(R.id.botaoCancelar);

        //obtendo os valores atuais de quantidade e total
        int quantidade = getQuantity();
        int precoTotal = quantidade * precoTicketCafe;

        // atualizando os textos do resumo
        quantidadeResumo.setText("Quantidade de tickets: " + quantidade);
        valorTotalResumo.setText("Valor total: R$" + precoTotal + ",00");

        AlertDialog dialog = builder.setView(dialogView).create();

        // atualiza a quantidade de tickets disponiveis e mostra o resumo da compra
        botaoConfirmar.setOnClickListener(v -> {

            ticketCafeViewModel.adicionarTickets(quantidade);

            //mostra o recibo
            mostrarRecibo(quantidade, precoTotal);

            //fecha o pop-up de resumo
            dialog.dismiss();
        });

        botaoCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // funcao para mostrar o recibo
    private void mostrarRecibo(int quantidade, int precoTotal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_recibo, null);

        // elementos do recibo
        TextView emailRecibo = dialogView.findViewById(R.id.emailRecibo);
        TextView dataRecibo = dialogView.findViewById(R.id.dataRecibo);
        TextView quantidadeRecibo = dialogView.findViewById(R.id.quantidadeRecibo);
        TextView valorTotalRecibo = dialogView.findViewById(R.id.valorTotalRecibo);
        Button botaoCompartilhar = dialogView.findViewById(R.id.botaoCompartilhar);
        ImageButton botaoFechar = dialogView.findViewById(R.id.botaoFechar);

        // preenchendo os campos do recibo

        // pegando o email do usuário para colocar como informação do ticket
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        emailRecibo.setText("Email: " + user.getEmail());
        dataRecibo.setText("Data: " + getCurrentDate());
        quantidadeRecibo.setText("Quantidade de tikets: " + quantidade);
        valorTotalRecibo.setText("Valor total: R$ " + precoTotal + ",00");

        AlertDialog dialog = builder.setView(dialogView).create();
        dialog.show();

        botaoFechar.setOnClickListener(v -> dialog.dismiss());
    }

    //funcao para ter a data atual no recibo
    private String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
}