package com.example.mefitness.viewmodel;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mefitness.view.FragmentTreino;
import com.example.mefitness.view.FragmentTools;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new FragmentTools();
        }
        return new FragmentTreino();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
