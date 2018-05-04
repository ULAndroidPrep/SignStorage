package edu.rosehulman.photobucket.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import edu.rosehulman.photobucket.Constants;
import edu.rosehulman.photobucket.MainActivity;
import edu.rosehulman.photobucket.Pic;
import edu.rosehulman.photobucket.PicAdapter;
import edu.rosehulman.photobucket.R;


public class PicListFragment extends Fragment {

  private OnPicSelectedListener mOnPicSelectedListener;
  private PicAdapter mAdapter;
  public static final int RC_TAKE_PICTURE = 43;

  public PicListFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAdapter = new PicAdapter(mOnPicSelectedListener, this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_pic_list, container, false);

    RecyclerView view = (RecyclerView) rootView.findViewById(R.id.recycler_view);
    view.setHasFixedSize(true);
    view.setLayoutManager(new LinearLayoutManager(getActivity()));
    view.setAdapter(mAdapter);

    ((MainActivity) getActivity()).getFab().setVisibility(View.VISIBLE);
    ((MainActivity) getActivity()).getFab().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showPhotoDialog(null);
      }
    });
    return rootView;
  }

  public void showPhotoDialog(final Pic photo) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle("Choose a photo choice");

    builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, RC_TAKE_PICTURE);
      }
    });

//    builder.setPositiveButton("Choose Photo", new DialogInterface.OnClickListener() {
//      @Override
//      public void onClick(DialogInterface dialog, int which) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(takePictureIntent, RC_TAKE_PICTURE);
//      }
//    });

    if (photo != null) {
      builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          mAdapter.firebaseRemove(photo);
        }
      });
    }
    builder.setNegativeButton(android.R.string.cancel, null);
    builder.create().show();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, final Intent data) {
    //Toast.makeText(getActivity(), "onActivityResult", Toast.LENGTH_SHORT).show();
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle("What is the caption?");
    final EditText editText = new EditText(getActivity());
    builder.setView(editText);

    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String name = editText.getText().toString();
        sendPhotoToAdapter(name, data);
      }
    });
    builder.create().show();
  }

  private void sendPhotoToAdapter(String name, Intent data) {
    Log.d(Constants.TAG, "Working so far!!!");

    Bitmap bitmap = (Bitmap)data.getExtras().get("data");
    String location = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
        bitmap, name, null);
    mAdapter.addPhoto(name, location, bitmap);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnPicSelectedListener) {
      mOnPicSelectedListener = (OnPicSelectedListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement PicListFragment.OnPicSelectedListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mOnPicSelectedListener = null;
  }

  public interface OnPicSelectedListener {
    void onPicSelected(Pic pic);
  }
}
