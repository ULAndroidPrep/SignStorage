package edu.rosehulman.photobucket.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import edu.rosehulman.photobucket.MainActivity;
import edu.rosehulman.photobucket.R;
import edu.rosehulman.photobucket.Util;
import edu.rosehulman.photobucket.Pic;
import edu.rosehulman.photobucket.PicAdapter;


public class PicListFragment extends Fragment {

    private OnPicSelectedListener mOnPicSelectedListener;
    private PicAdapter mAdapter;

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
                showAddEditDialog(null);
            }
        });
        return rootView;
    }

    public void showAddEditDialog(final Pic pic) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_pic_add_edit, null, false);
        builder.setView(view);

        builder.setTitle(getContext().getString(pic == null ? R.string.dialog_add_pic_title : R.string.dialog_edit_pic_title));
        final EditText captionEditText = (EditText) view.findViewById(R.id.dialog_add_caption);
        final EditText imageUrlEditText = (EditText) view.findViewById(R.id.dialog_add_url);

        if (pic != null) {
            captionEditText.setText(pic.getCaption());
            imageUrlEditText.setText(pic.getImageUrl());
        }

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String caption = captionEditText.getText().toString();
                String imageUrl = imageUrlEditText.getText().toString();
                if (imageUrl.isEmpty()) {
                    imageUrl = Util.randomImageUrl();
                }
                if (pic == null) {
                    mAdapter.firebasePush(caption, imageUrl);
                } else {
                    mAdapter.firebaseEdit(pic, caption, imageUrl);
                }
            }
        });

        if (pic != null) {
            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAdapter.firebaseRemove(pic);
                }
            });
        }
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
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
