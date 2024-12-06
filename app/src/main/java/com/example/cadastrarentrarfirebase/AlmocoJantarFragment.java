package com.example.cadastrarentrarfirebase;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AlmocoJantarFragment extends Fragment {

    private TextView ticketsDisponiveisTextView;
    private Button btnGerarCodigoAlmoco;
    private TicketViewModel ticketViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_almoco_jantar, container, false);
        ticketsDisponiveisTextView = view.findViewById(R.id.ticketsDisponiveisAlmocoJantar);

        btnGerarCodigoAlmoco = view.findViewById(R.id.btnGerarCodigoAlmocoJantar);

        // inicializa o ViewModel e observa as mudanças na quantidade de tickets
        ticketViewModel = new ViewModelProvider(requireActivity()).get(TicketViewModel.class);
        ticketViewModel.init(requireContext()); // inicializando o ViewModel com o contexto
        ticketViewModel.getQuantidadeTickets().observe(getViewLifecycleOwner(), quantidade -> {
            ticketsDisponiveisTextView.setText("Voce tem: " + quantidade + " tickets de almoço/jantar disponiveis");
        });

        // configurando botao para gerar o qr code
        btnGerarCodigoAlmoco.setOnClickListener(v -> gerarCodigoETicket());

        return view;
    }

    private void gerarCodigoETicket(){
        // verifica a quantidade de tickets antes de gerar o código
        int ticketsAtuais = ticketViewModel.getQuantidadeTickets().getValue() != null ? ticketViewModel.getQuantidadeTickets().getValue() : 0;
        if (ticketsAtuais > 0) {

            mostrarCodigoTicket();
        } else {
            // mensagem para dizer que o usuario nao tem tickets
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Você não possui tickets de almoço/jantar disponíveis.")
                    .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void mostrarCodigoTicket() {
        // pegando o email do usuario
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        try {
            String codigoUnico = ticketViewModel.getCodigoUnico();
            //String codigoUnico = UUID.randomUUID().toString();

            if (codigoUnico == null) {
                codigoUnico = UUID.randomUUID().toString();
                ticketViewModel.salvarCodigoUnico(codigoUnico);
            }

            // informações para ter no ticket lido
            String conteudoQRCode = "\nUsuario: " + user.getEmail() +
                                    "\nData: " + getCurrentDate() +
                                    "\nTicket ID: \n" + codigoUnico;

            // geracao do QR code com o conteudo desejado
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(conteudoQRCode, BarcodeFormat.QR_CODE, 400, 400);

            // configuração e exibicao do pop-up com o qr code e informacoes
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
                ticketViewModel.decrementarTicket();
                ticketViewModel.limparCodigoUnico();
                dialog.dismiss();
                mostrarMensagemSucesso();
            });

            btnFechar.setOnClickListener(v -> dialog.dismiss());

            dialog.setCanceledOnTouchOutside(true); // para fechar o pop up só clicando fora dele

            //AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarMensagemSucesso() {
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