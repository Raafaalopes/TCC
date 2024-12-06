package com.example.cadastrarentrarfirebase;


import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class CafeManhaFragment extends Fragment {

    private TextView ticketsDisponiveisCafeManha;
    private Button btnGerarCodigoCafeManha;
    private TicketCafeViewModel ticketCafeViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cafe_manha, container, false);

        ticketsDisponiveisCafeManha = view.findViewById(R.id.ticketsDisponiveisCafeManha);
        btnGerarCodigoCafeManha = view.findViewById(R.id.btnGerarCodigoCafeManha);

        ticketCafeViewModel = new ViewModelProvider(requireActivity()).get(TicketCafeViewModel.class);
        ticketCafeViewModel.init(requireContext());
        ticketCafeViewModel.getTicketsDisponiveis().observe(getViewLifecycleOwner(), tickets -> {
            ticketsDisponiveisCafeManha.setText("Você tem: " + tickets + " tickets de café da manhã diponíveis");
        });

        // configurações do botao para gerar o qr code
        btnGerarCodigoCafeManha.setOnClickListener(v -> gerarCodigoETicket());

        return view;
    }

    private void gerarCodigoETicket() {
        // verifica a quantidade de tickets antes de gerar o código
        int ticketsAtuais = ticketCafeViewModel.getTicketsDisponiveis().getValue() != null ? ticketCafeViewModel.getTicketsDisponiveis().getValue() : 0;
        if (ticketsAtuais > 0) {
            // exibe o qr code
            mostrarCodigoTicket();
        } else {
            // exibe a mensagem pra dizer que a pessoa nao tem ticket
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Você não possui tickets de café da manhã disponíveis.")
                    .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void mostrarCodigoTicket(){
        // Pegando o email do usuário para colocar no ticket
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        try {
            // Gera um código unico para o ticket usando UUID
            String codigoUnico = ticketCafeViewModel.getCodigoUnico();

            if (codigoUnico == null) {
                codigoUnico = UUID.randomUUID().toString();
                ticketCafeViewModel.salvarCodigoUnico(codigoUnico);
            }

            // combina o código unico com a data de geração e o usuário para o QR code
            String conteudoQRCode = "\nUsuario: " + user.getEmail() +
                                    "\nData: " + getCurrentDate() +
                                    "\nTicket ID: \n" + codigoUnico;

            // geração do Qr code com o conteudo desejado
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(conteudoQRCode, BarcodeFormat.QR_CODE, 400, 400);

            // configuração e exibição do pop-up com o Qr code e informações
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_qr_ticket, null);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            //TextView emailQRCode = dialogView.findViewById(R.id.emailQRCode);
            //TextView dataGeracao = dialogView.findViewById(R.id.dataGeracao);
            //TextView codigoTicket = dialogView.findViewById(R.id.codigoTicket);
            ImageView qrCodeImage = dialogView.findViewById(R.id.qrCodeImage);
            Button btnValidar = dialogView.findViewById(R.id.btnValidar);
            ImageView btnFechar = dialogView.findViewById(R.id.btnFecharPopUp);


            //emailQRCode.setText("Usuário: " + user.getEmail());
            //dataGeracao.setText("Data: " + getCurrentDate());
            //codigoTicket.setText("Código do Ticket: \n" + codigoUnico);
            qrCodeImage.setImageBitmap(bitmap);

            btnValidar.setOnClickListener(v -> {
                ticketCafeViewModel.decrementarTicket();
                ticketCafeViewModel.limparCodigoUnico();
                dialog.dismiss();
                mostrarMensagemSucesso();
            });

            btnFechar.setOnClickListener(v -> dialog.dismiss());

            dialog.setCanceledOnTouchOutside(true);

            //AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarMensagemSucesso(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Seu ticket foi validado com sucesso!")
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
}