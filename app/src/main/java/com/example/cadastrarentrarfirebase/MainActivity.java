package com.example.cadastrarentrarfirebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth auth;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbarSetup();
        preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();

        // verificar se ta logado ou nao
        if (auth.getCurrentUser() == null) {
            // se o usuario nao estiver logado, redireciona para a tela de login
            startActivity(new Intent(MainActivity.this, EntrarActivity.class));
            finish();
        }
        //se o usuario estiver logado continuar aqui
        if (preferences.getBoolean("manterConectado", false)) {
            Toast.makeText(this, "Bem vindo de volta!", Toast.LENGTH_SHORT).show();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_conteiner, new HomeFragment()).commit();
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_home);

            // daqui pra baixo é para colocar o email da pessoa logada
            // Localiza a View do cabeçalho no NavigationView
            View headerView = navigationView.getHeaderView(0);

            // Localiza o TextView do e-mail no cabeçalho
            TextView emailTextView = headerView.findViewById(R.id.nav_header_email);

            // Define o e-mail do usuário logado no TextView
            if (auth.getCurrentUser() != null) {
                String userEmail = auth.getCurrentUser().getEmail();
                emailTextView.setText(userEmail);
            }
        }

    }

    private void toolbarSetup(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_conteiner, new HomeFragment()).commit();
            }
            if (item.getItemId() == R.id.nav_tickets) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_conteiner, new TicketsFragment()).commit();
            }

            if (item.getItemId() == R.id.nav_cardapio) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_conteiner, new CardapioFragment()).commit();
            }

            if (item.getItemId() == R.id.nav_comprar) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_conteiner, new ComprarFragment()).commit();
            }

            if (item.getItemId() == R.id.nav_logout) {
                // limpar a preferencia de manter conectado
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("manterConectado", false);
                editor.apply();

                auth.signOut();

                startActivity(new Intent(MainActivity.this, EntrarActivity.class));
                finish();
            }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}