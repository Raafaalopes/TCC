package com.example.cadastrarentrarfirebase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class EntrarActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText entrarEmail, entrarSenha;
    private TextView cadastroRedirectText;
    private Button entrarBotao;
    //private Spinner entrarSpinner;
    // manter conectado
    private CheckBox checkBoxManterConectado;
    TextView esqueciSenha;

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.entrar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        entrarEmail = findViewById(R.id.entrar_email);
        entrarSenha = findViewById(R.id.entrar_senha);
        entrarBotao = findViewById(R.id.entrar_botao);
        cadastroRedirectText = findViewById(R.id.cadastroRedirectText);
        //entrarSpinner = findViewById(R.id.entrar_spinner);
        // manter conectado
        checkBoxManterConectado = findViewById(R.id.checkbox_manter_conectado);
        esqueciSenha = findViewById(R.id.esqueci_senha);

        // manter conectado
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (auth.getCurrentUser() != null && preferences.getBoolean("manterConectado", false)) {
            // o usuário esta logado e escolheu se manter conectado
            startActivity(new Intent(EntrarActivity.this, MainActivity.class));
            finish();
        }


        /* spinner
        // Novos itens que você deseja mostrar
        List<String> novosItens = Arrays.asList("RA", "PG", "Externo");
        // Crie um ArrayAdapter com os novos itens
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, novosItens);
        // Defina o layout para o dropdown do spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Aplique o adapter ao spinner
        entrarSpinner.setAdapter(adapter);
         */

        // definir icones iniciais para o campo de senha
        final Drawable[] eyeIcon = new Drawable[1];
        eyeIcon[0] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_visibility_off);

        //inicializando o EditText da sennha
        entrarSenha.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_lock_24),
                null,
                eyeIcon[0],
                null
        );
        entrarSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Alternar visibilidade da senha ao clicar no ícone de olho
        entrarSenha.setOnTouchListener((v, event) -> {
            int drawableStartX = entrarSenha.getWidth() - entrarSenha.getPaddingRight() - eyeIcon[0].getIntrinsicWidth();

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= drawableStartX) {
                    // Tocar no ícone de olho, alterna a visibilidade da senha
                    if (entrarSenha.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        entrarSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        eyeIcon[0] = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_visibility); // ícone de "mostrar"
                    } else {
                        entrarSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        eyeIcon[0] = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_visibility_off); // ícone de "esconder"
                    }

                    // Atualiza os ícones
                    entrarSenha.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(getApplicationContext(),R.drawable.baseline_lock_24), // cadeado
                            null,
                            eyeIcon[0], // ícone de visibilidade
                            null
                    );
                    // Move o cursor para o final do texto
                    entrarSenha.setSelection(entrarSenha.getText().length());
                    return true;
                }
            }
            return false;
        });

        entrarBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = entrarEmail.getText().toString();
                String senha = entrarSenha.getText().toString();

                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!senha.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, senha)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                // salva a preferencia de manter-se conectado
                                // manter conectado
                                editor.putBoolean("manterConectado", checkBoxManterConectado.isChecked());
                                editor.apply();

                                startActivity(new Intent(EntrarActivity.this, MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EntrarActivity.this, "Algo deu errado para entrar", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        entrarSenha.setError("senha não pode ser vazia");
                    }
                } else if(email.isEmpty()) {
                    entrarEmail.setError("email nao pode ser vazio");
                } else {
                    entrarEmail.setError("Por favor entre com um email válido");
                }
            }
        });

        cadastroRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EntrarActivity.this, CadastroActivity.class));
            }
        });

        esqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EntrarActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_esqueci, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.btnRedefinir).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String usuarioEmail = emailBox.getText().toString();

                        if(TextUtils.isEmpty(usuarioEmail) && !Patterns.EMAIL_ADDRESS.matcher(usuarioEmail).matches()){
                            Toast.makeText(EntrarActivity.this, "Digite seu id de email cadastrado", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        auth.sendPasswordResetEmail(usuarioEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(EntrarActivity.this, "Verifique seu email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(EntrarActivity.this, "Não foi possivel enviar, falhou", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });
    }
}