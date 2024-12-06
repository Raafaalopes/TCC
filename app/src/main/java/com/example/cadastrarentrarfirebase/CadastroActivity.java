package com.example.cadastrarentrarfirebase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class CadastroActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText cadastroEmail, cadastroSenha;
    private Button cadastroBotao;
    private TextView entrarRedirectText;
    //private Spinner cadastroSpinner;
    private CheckBox cadastroCiente;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cadastro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        cadastroEmail = findViewById(R.id.cadastro_email);
        cadastroSenha = findViewById(R.id.cadastro_senha);
        cadastroBotao = findViewById(R.id.cadastro_botao);
        entrarRedirectText = findViewById(R.id.entrarRedirectText);
        //cadastroSpinner = findViewById(R.id.cadastro_spinner);
        cadastroCiente = findViewById(R.id.cadastroCiente);

        /* spinner
        // Novos itens que você deseja mostrar
        List<String> novosItens = Arrays.asList("RA", "PG", "Externo");
        // Crie um ArrayAdapter com os novos itens
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, novosItens);
        // Defina o layout para o dropdown do spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Aplique o adapter ao spinner
        cadastroSpinner.setAdapter(adapter);
        */

        // definir icones iniciais para o campo de senha
        final Drawable[] eyeIcon = new Drawable[1];
        eyeIcon[0] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_visibility_off);

        // inicializando o EditText da senha
        cadastroSenha.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_lock_24),
                null,
                eyeIcon[0],
                null
        );
        cadastroSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Alternar visibilidade da senha ao clicar no icone de olho
        cadastroSenha.setOnTouchListener((v, event) -> {
            int drawableStartX = cadastroSenha.getWidth() - cadastroSenha.getPaddingRight() - eyeIcon[0].getIntrinsicWidth();

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= drawableStartX) {
                    // Tocar no ícone de olho, alterna a visibilidade da senha
                    if (cadastroSenha.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        cadastroSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        eyeIcon[0] = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_visibility); // ícone de "mostrar"
                    } else {
                        cadastroSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        eyeIcon[0] = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_visibility_off); // ícone de "esconder"
                    }

                    // Atualiza os ícones
                    cadastroSenha.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(getApplicationContext(),R.drawable.baseline_lock_24), // cadeado
                            null,
                            eyeIcon[0], // ícone de visibilidade
                            null
                    );
                    // Move o cursor para o final do texto
                    cadastroSenha.setSelection(cadastroSenha.getText().length());
                    return true;
                }
            }

            return false;
        });


        cadastroBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuario = cadastroEmail.getText().toString().trim();
                String senha = cadastroSenha.getText().toString().trim();
                boolean ciente = cadastroCiente.isChecked();

                if(usuario.isEmpty()) {
                    cadastroEmail.setError("Email não pode ser vazio");
                }
                else if(senha.isEmpty()) {
                    cadastroSenha.setError("Senha não pode ser vazia");
                }
                else if(!ciente) {
                    cadastroCiente.setError("Voce deve concordar com os termos");
                    Toast.makeText(CadastroActivity.this, "Voce deve concordar com os termos", Toast.LENGTH_SHORT).show();
                } else {
                    auth.createUserWithEmailAndPassword(usuario, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CadastroActivity.this, EntrarActivity.class));
                            } else {
                                Toast.makeText(CadastroActivity.this, "Erro no cadastro\n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        entrarRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CadastroActivity.this, EntrarActivity.class));
            }
        });


    }
}