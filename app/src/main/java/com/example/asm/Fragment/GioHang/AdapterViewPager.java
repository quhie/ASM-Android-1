package com.example.asm.Fragment.GioHang;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class AdapterViewPager extends FragmentStateAdapter {

    private final ArrayList<Fragment> fragmentsList = new ArrayList<>();

    public AdapterViewPager(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public void addFragment(Fragment fragment) {
        fragmentsList.add(fragment);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {     // Khởi tạo Fragment tại vị trí
        return fragmentsList.get(position);
    }

    @Override
    public int getItemCount() {                    // Số lượng Fragment
        return fragmentsList.size();
    }

}
