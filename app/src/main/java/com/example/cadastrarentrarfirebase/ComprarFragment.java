package com.example.cadastrarentrarfirebase;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ComprarFragment extends Fragment {

    private EditText almocoPicker, cafePicker;
    private ImageButton btDiminuirAlmoco, btAumentarAlmoco, btDiminuirCafe, btAumentarCafe;
    private TextView precoTotalAlmoco, precoTotalCafe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comprar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurações da Toolbar
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Comprar Tickets");
        }

        // Encontrar os componentes
        almocoPicker = view.findViewById(R.id.quantidadeAlmoco);
        cafePicker = view.findViewById(R.id.quantidadeCafe);
        btDiminuirAlmoco = view.findViewById(R.id.btDiminuirAlmoco);
        btAumentarAlmoco = view.findViewById(R.id.btAumentarAlmoco);
        btDiminuirCafe = view.findViewById(R.id.btDiminuirCafe);
        btAumentarCafe = view.findViewById(R.id.btAumentarCafe);
        precoTotalAlmoco = view.findViewById(R.id.precoTotalAlmoco);
        precoTotalCafe = view.findViewById(R.id.precoTotalCafe);

        // Configuração dos botões de "mais" e "menos"
        btDiminuirAlmoco.setOnClickListener(v -> alterarQuantidade(almocoPicker, -1));
        btAumentarAlmoco.setOnClickListener(v -> alterarQuantidade(almocoPicker, 1));
        btDiminuirCafe.setOnClickListener(v -> alterarQuantidade(cafePicker, -1));
        btAumentarCafe.setOnClickListener(v -> alterarQuantidade(cafePicker, 1));

        // Listener de texto para os campos de quantidade
        almocoPicker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                atualizarPreco(); // Atualizar preços sempre que o texto mudar
            }
        });

        cafePicker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                atualizarPreco(); // Atualizar preços sempre que o texto mudar
            }
        });

        // Botão de Comprar Tickets
        Button btnComprar = view.findViewById(R.id.botaoComprarTodos);
        btnComprar.setOnClickListener(v -> {
            // le as quantidades de tickets digitadas
            int almocoTickets = obterQuantidade(almocoPicker);
            int cafeTickets = obterQuantidade(cafePicker);

            // verifica se ambos os valores são zero
            if (almocoTickets == 0 && cafeTickets == 0) {
                Toast.makeText(getContext(), "Seleione uma quantidade válida de tickets!", Toast.LENGTH_SHORT).show();
                return;
            }

            int valorTotal = calcularTotal(almocoTickets, cafeTickets);

            // Redireciona para a tela de pagamento com os dados
            PagamentoFragment pagamentoFragment = new PagamentoFragment(almocoTickets, cafeTickets, valorTotal);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_conteiner, pagamentoFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Inicializa os preços exibidos
        atualizarPreco();
    }

    private void alterarQuantidade(EditText editText, int delta) {
        int quantidade = obterQuantidade(editText);
        quantidade += delta;
        if (quantidade < 0) {
            quantidade = 0;
        }
        editText.setText(String.valueOf(quantidade));
    }

    private int obterQuantidade(EditText editText) {
        String texto = editText.getText().toString();
        if (TextUtils.isEmpty(texto)) {
            return 0;
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int calcularTotal(int almocoTickets, int cafeTickets) {
        int valorAlmoco = 5; // Valor unitário de almoço/janta
        int valorCafe = 2; // Valor unitário de café
        return (almocoTickets * valorAlmoco) + (cafeTickets * valorCafe);
    }

    private void atualizarPreco() {
        int almocoTickets = obterQuantidade(almocoPicker);
        int cafeTickets = obterQuantidade(cafePicker);

        int valorAlmoco = almocoTickets * 5; // Cálculo do preço de almoço/janta
        int valorCafe = cafeTickets * 2; // Cálculo do preço de café

        precoTotalAlmoco.setText("Total: R$ " + valorAlmoco + ",00");
        precoTotalCafe.setText("Total: R$ " + valorCafe + ",00");
    }
}
