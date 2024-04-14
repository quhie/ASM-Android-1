package com.example.asm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.asm.Fragment.Cart;
import com.example.asm.Fragment.Settings;
import com.example.asm.Fragment.fragment_user;
import com.example.asm.Fragment.ProductManagement;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SearchListener {
    private static final int FRAGMENT_PRODUCT = 0;
    private static final int FRAGMENT_CART = 1;
    private static final int FRAGMENT_USER = 2;

    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    private ShapeableImageView imgAvatar;
    private TextView nameUser;

    private SearchView searchView;
    private ProductManagement productManagementFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        setupViews();
        setupNavigationView();
        setupBottomNavigationView();
        displayFragment(FRAGMENT_PRODUCT);
    }

    private void setupViews() {
        searchView = findViewById(R.id.search);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        imgAvatar = findViewById(R.id.avtUser);
        nameUser = findViewById(R.id.nameUser);

        String displayName = mAuth.getCurrentUser().getDisplayName();
        String avatar = mAuth.getCurrentUser().getPhotoUrl().toString();
        Log.d("TAG", "onCreate: " + avatar);
        if (displayName == null) {
            displayName = "User";
        }
        if (avatar != null) {
            Picasso.get().load(avatar).into(imgAvatar);}

        nameUser.setText(displayName);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerToggle();
        setupSearchView();
    }

    private void setupDrawerToggle() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                displayFragment(FRAGMENT_PRODUCT);
                getWindow().setNavigationBarColor(Color.parseColor("#ffffff"));
            } else if (itemId == R.id.menu_cart) {
                displayFragment(FRAGMENT_CART);
                getWindow().setNavigationBarColor(Color.parseColor("#ffffff"));
            } else if (itemId == R.id.menu_user) {
                displayFragment(FRAGMENT_USER);
                getWindow().setNavigationBarColor(Color.parseColor("#ffffff"));
            }
            return true;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, firthActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayFragment(int fragmentId) {
        Fragment fragment = null;

        switch (fragmentId) {
            case FRAGMENT_PRODUCT:
                fragment = new ProductManagement();
                searchView.setVisibility(SearchView.VISIBLE);
                productManagementFragment = (ProductManagement) fragment;
                break;

            case FRAGMENT_CART:
                fragment = new Cart();
                searchView.setVisibility(SearchView.GONE);
                break;

            case FRAGMENT_USER:
                fragment = new fragment_user();
                searchView.setVisibility(SearchView.GONE);
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });
    }

    @Override
    public void performSearch(String query) {
        if (productManagementFragment != null) {
            productManagementFragment.searchList(query);
        }
    }
}
