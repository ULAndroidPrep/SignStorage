package edu.rosehulman.photobucket;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.rosehulman.photobucket.fragments.PicDetailFragment;
import edu.rosehulman.photobucket.fragments.PicListFragment;

public class MainActivity extends AppCompatActivity implements PicListFragment.OnPicSelectedListener {
  private DatabaseReference mTitleRef;
  private ValueEventListener mTitleValueEventListener;

  public static final int RC_WRITE_EXTERNAL_STORAGE_PERMISSION = 42;

  private FloatingActionButton mFab;

  public FloatingActionButton getFab() {
    return mFab;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mFab = (FloatingActionButton) findViewById(R.id.fab);

    mTitleRef = FirebaseDatabase.getInstance().getReference().child("title");
    mTitleValueEventListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        MainActivity.this.setTitle(dataSnapshot.getValue(String.class));
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e(Constants.TAG, "Database error: " + databaseError.getMessage());
      }
    };

    // What would mCurrentFragment be if got here and wasn't null?
    if (savedInstanceState == null) {
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.add(R.id.container, new PicListFragment());
      ft.commit();
    }

    checkPermission();
  }

  private void checkPermission() {
    if (ContextCompat.checkSelfPermission(this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
          new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
          RC_WRITE_EXTERNAL_STORAGE_PERMISSION);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    mTitleRef.addValueEventListener(mTitleValueEventListener);
  }

  @Override
  protected void onStop() {
    super.onStop();
    mTitleRef.removeEventListener(mTitleValueEventListener);
  }

  @Override
  public void onPicSelected(Pic pic) {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.addToBackStack("pic");
    ft.replace(R.id.container, PicDetailFragment.newInstance(pic));
    ft.commit();
  }
}
