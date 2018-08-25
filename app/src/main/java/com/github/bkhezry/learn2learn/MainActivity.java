package com.github.bkhezry.learn2learn;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class MainActivity extends AppCompatActivity {

  protected BottomAppBar bar;
  private CoordinatorLayout coordinatorLayout;
  private BottomSheetBehavior<View> bottomDrawerBehavior;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("Title");
    toolbar.setNavigationOnClickListener(
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          MainActivity.this.onBackPressed();
        }
      });

    coordinatorLayout = findViewById(R.id.coordinator_layout);
    bar = findViewById(R.id.bar);
    setSupportActionBar(bar);
    setUpBottomDrawer();

    final FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Toast.makeText(MainActivity.this, fab.getContentDescription(), Toast.LENGTH_SHORT).show();
        }
      });
    NavigationView navigationView = findViewById(R.id.navigation_view);
    navigationView.setNavigationItemSelectedListener(
      new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
          return false;
        }
      });
  }

//  @Override
//  public boolean onCreateOptionsMenu(Menu menu) {
//    MenuInflater inflater = getMenuInflater();
//    inflater.inflate(R.menu.demo_primary, menu);
//    return super.onCreateOptionsMenu(menu);
//  }
//
//  @Override
//  public boolean onOptionsItemSelected(MenuItem menuItem) {
//    Toast.makeText(this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
//    return true;
//  }

  protected void setUpBottomDrawer() {
    View bottomDrawer = coordinatorLayout.findViewById(R.id.bottom_drawer);
    bottomDrawerBehavior = BottomSheetBehavior.from(bottomDrawer);
    bottomDrawerBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    bar.setNavigationOnClickListener(
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          bottomDrawerBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        }
      });
    bar.setNavigationIcon(R.drawable.ic_drawer_menu_24px);
    bar.replaceMenu(R.menu.demo_primary);
  }
}

