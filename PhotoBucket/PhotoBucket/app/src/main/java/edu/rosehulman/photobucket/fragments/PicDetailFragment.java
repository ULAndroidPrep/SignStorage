package edu.rosehulman.photobucket.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import edu.rosehulman.photobucket.Constants;
import edu.rosehulman.photobucket.MainActivity;
import edu.rosehulman.photobucket.R;
import edu.rosehulman.photobucket.Pic;

public class PicDetailFragment extends Fragment {
    private static final String ARG_PIC = "pic";

    private Pic mPic;
    private ImageView mPicView;

    public PicDetailFragment() {
        // Required empty public constructor
    }

    public static PicDetailFragment newInstance(Pic pic) {
        PicDetailFragment fragment = new PicDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PIC, pic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPic = getArguments().getParcelable(ARG_PIC);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pic_detail, container, false);


        ((MainActivity)getActivity()).getFab().setVisibility(View.GONE);

        mPicView = (ImageView) view.findViewById(R.id.fragment_pic_detail_image);
        TextView captionView = (TextView) view.findViewById(R.id.fragment_pic_detail_caption);

        captionView.setText(mPic.getCaption());



        new DownloadImageTask().execute(mPic.getImageUrl());
        return view;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            try {
                Log.d(Constants.TAG, "Trying to download " + urls[0]);
                InputStream in = new java.net.URL(urls[0]).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            Log.d(Constants.TAG, "Downloaded image");
            mPicView.setImageBitmap(result);
            Log.d(Constants.TAG, "Set bitmap");
        }
    }
}
