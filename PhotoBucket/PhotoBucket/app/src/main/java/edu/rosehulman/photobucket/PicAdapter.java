package edu.rosehulman.photobucket;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public PicAdapter(PicListFragment.OnPicSelectedListener onPicSelectedListener, PicListFragment picListFragment) {
        mOnPicSelectedListener = onPicSelectedListener;
        mPicListFragment = picListFragment;
        mPics = new ArrayList<>();
        mPicsRef = FirebaseDatabase.getInstance().getReference().child("pics");
        mPicsRef.addChildEventListener(new PicChildEventListener());

    }

    public void firebasePush(String caption, String url) {
        Pic pic = new Pic(caption, url);
        mPicsRef.push().setValue(pic);
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
                    notifyItemChanged(mPics.indexOf(pic));
                    //notifyDataSetChanged();
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
                    notifyItemRemoved(mPics.indexOf(pic));
                    //notifyDataSetChanged();
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
                    mPicListFragment.showAddEditDialog(pic);
                    return true;
                }
            });

        }
    }
}
