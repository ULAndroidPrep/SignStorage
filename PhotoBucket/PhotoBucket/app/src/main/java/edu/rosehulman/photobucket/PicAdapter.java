package edu.rosehulman.photobucket;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.photobucket.fragments.PicListFragment;

/**
 * Created by Matt Boutell on 12/18/2015. DONE
 */
public class PicAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {
  private final PicListFragment mPicListFragment;
  private PicListFragment.OnPicSelectedListener mOnPicSelectedListener;
  private List<Pic> mPics;
  private DatabaseReference mPicsRef;
  private StorageReference mPicsStorageRef;

  public PicAdapter(PicListFragment.OnPicSelectedListener onPicSelectedListener, PicListFragment picListFragment) {
    mOnPicSelectedListener = onPicSelectedListener;
    mPicListFragment = picListFragment;
    mPics = new ArrayList<>();
    mPicsRef = FirebaseDatabase.getInstance().getReference().child("pics");
    mPicsRef.addChildEventListener(new PicChildEventListener());
    mPicsStorageRef = FirebaseStorage.getInstance().getReference().child("pics");
  }

  public void firebaseRemove(Pic pic) {
    mPicsRef.child(pic.getKey()).removeValue();
  }

  public void firebaseEdit(Pic pic, String newCaption, String newImageUrl) {
    pic.setCaption(newCaption);
    pic.setImageUrl(newImageUrl);
    mPicsRef.child(pic.getKey()).setValue(pic);
  }

  @Override
  public PicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pic, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(PicAdapter.ViewHolder holder, int position) {
    Pic pic = mPics.get(position);
    holder.mCaptionTextView.setText(pic.getCaption());
    holder.mImageUrlTextView.setText(pic.getImageUrl());
  }

  @Override
  public int getItemCount() {
    return mPics.size();
  }

  public void addPhoto(final String name, String location, Bitmap bitmap) {
    // Need bitmap to send it to storage
    //Pic photo = new Pic(name, location);  // old

    final String documentKey = mPicsRef.push().getKey();

    // Push the bitmap to Firebase Storage.
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    if (bitmap == null) {
      Log.e(Constants.TAG, "bitmap is null!!!!!!!!!!!!!");
      return; // just to avoid the crash
    }
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    byte[] data = baos.toByteArray();

    UploadTask uploadTask = mPicsStorageRef.child(documentKey).putBytes(data);
    uploadTask.addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception exception) {
        // Handle unsuccessful uploads
      }
    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
        // ...

        Pic photo = new Pic(name, taskSnapshot.getDownloadUrl().toString());
        mPicsRef.child(documentKey).setValue(photo);

      }
    });



  }

  private class PicChildEventListener implements ChildEventListener {

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
      Pic pic = dataSnapshot.getValue(Pic.class);
      pic.setKey(dataSnapshot.getKey());
      mPics.add(0, pic);
      notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
      String key = dataSnapshot.getKey();
      for (Pic pic : mPics) {
        if (key.equals(pic.getKey())) {
          pic.setValues(dataSnapshot.getValue(Pic.class));
          //notifyItemChanged(mPics.indexOf(pic));
          notifyDataSetChanged();
          return;
        }
      }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
      String key = dataSnapshot.getKey();
      for (Pic pic : mPics) {
        if (key.equals(pic.getKey())) {
          mPics.remove(pic);
          //notifyItemRemoved(mPics.indexOf(pic));
          notifyDataSetChanged();
          return;
        }
      }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
      // empty
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
      Log.e(Constants.TAG, "Cancelled: " + databaseError.getMessage());
    }
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView mCaptionTextView;
    private final TextView mImageUrlTextView;

    public ViewHolder(View itemView) {
      super(itemView);
      mCaptionTextView = (TextView) itemView.findViewById(R.id.row_pic_caption);
      mImageUrlTextView = (TextView) itemView.findViewById(R.id.row_pic_image_url);

      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Pic pic = mPics.get(getAdapterPosition());
          mOnPicSelectedListener.onPicSelected(pic);
        }
      });

      itemView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          Pic pic = mPics.get(getAdapterPosition());
          mPicListFragment.showPhotoDialog(pic);
          return true;
        }
      });

    }
  }
}
