package com.example.asm.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.asm.Fragment.GioHang.AdapterViewPager;
import com.example.asm.Fragment.GioHang.Quan_ly_ban_hang;
import com.example.asm.Fragment.GioHang.cart_thanh_toan;
import com.example.asm.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Cart extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    AdapterViewPager adapter;

    public Cart() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Cart");


        tabLayout = view.findViewById(R.id.tablayout);
        viewPager2 = view.findViewById(R.id.viewpager);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //khởi tạo adapter và thêm fragment vào adapter
        adapter = new AdapterViewPager(getChildFragmentManager(), getLifecycle());
        adapter.addFragment(new cart_thanh_toan());
        adapter.addFragment(new Quan_ly_ban_hang());
        //set adapter cho viewpager2
        viewPager2.setAdapter(adapter);
        //khởi tạo tablayout
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Khoản thanh toán");
                        break;
                    case 1:
                        tab.setText("Quan lý bán hàng");
                        break;
                    default:
                        break;
                }
            }
        }).attach();
    }
}
