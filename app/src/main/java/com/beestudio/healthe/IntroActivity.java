package com.beestudio.healthe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.PagerAdapter;

import com.beestudio.healthe.utils.IntroUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class IntroActivity extends AppCompatActivity {

    ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnToLogin, btnToRegister;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        mAuth = FirebaseAuth.getInstance();

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layout_dots);
        btnToLogin = findViewById(R.id.btn_to_login);
        btnToRegister = findViewById(R.id.btn_to_register);

        layouts = new int[] {
                R.layout.intro_one,
                R.layout.intro_two,
                R.layout.intro_three,
        };

        addBottomDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int colorsActive = getResources().getColor(R.color.colorAccent);
        int colorsInactive = getResources().getColor(R.color.colorAccentBg);

        dotsLayout.removeAllViews();
        for(int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive);
            dotsLayout.addView(dots[i]);
        }

        if(dots.length > 0) {
            dots[currentPage].setTextColor(colorsActive);
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter{
        private LayoutInflater layoutInflater;


        public MyViewPagerAdapter() {}

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

    }
}
