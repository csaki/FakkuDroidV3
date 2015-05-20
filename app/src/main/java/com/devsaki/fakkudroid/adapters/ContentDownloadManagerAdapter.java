package com.devsaki.fakkudroid.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devsaki.fakkudroid.DownloadManagerActivity;
import com.devsaki.fakkudroid.R;
import com.devsaki.fakkudroid.database.domains.Attribute;
import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.database.enums.Status;
import com.devsaki.fakkudroid.util.ConstantsPreferences;
import com.devsaki.fakkudroid.util.Helper;
import com.devsaki.fakkudroid.util.ImageQuality;

import java.io.File;
import java.util.List;

/**
 * Created by neko on 11/05/2015.
 */
public class ContentDownloadManagerAdapter extends ArrayAdapter<Content> {

    private final static String TAG = ContentDownloadManagerAdapter.class.getName();
    private final Context context;
    private final List<Content> contents;
    private LruCache<String, Bitmap> mMemoryCache;
    private SharedPreferences sharedPreferences;

    public ContentDownloadManagerAdapter(Context context, List<Content> contents) {
        super(context, R.layout.row_download, contents);
        this.context = context;
        this.contents = contents;
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void loadBitmap(File file, ImageView mImageView) {
        final String imageKey = file.getAbsolutePath();

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        } else {
            mImageView.setImageResource(R.drawable.ic_fakkudroid_launcher);
            BitmapWorkerTask task = new BitmapWorkerTask(mImageView);
            task.execute(file);
        }
    }

    class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {

        private ImageView imageView;

        public BitmapWorkerTask(ImageView imageView) {
            this.imageView = imageView;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(File... params) {
            if(params[0].exists()&&params[0].getAbsolutePath()!=null){
                String imageQualityPref = sharedPreferences.getString(ConstantsPreferences.PREF_QUALITY_IMAGE_LISTS, ConstantsPreferences.PREF_QUALITY_IMAGE_DEFAULT);
                ImageQuality imageQuality = ImageQuality.LOW;
                switch (imageQualityPref){
                    case "Medium":
                        imageQuality = ImageQuality.MEDIUM;
                        break;
                    case "High":
                        imageQuality = ImageQuality.HIGH;
                        break;
                    case "Low":
                        imageQuality = ImageQuality.LOW;
                        break;
                }

                Bitmap thumbBitmap = Helper.decodeSampledBitmapFromFile(
                        params[0].getAbsolutePath(), imageQuality.getWidth(),
                        imageQuality.getHeight());
                addBitmapToMemoryCache(params[0].getAbsolutePath(), thumbBitmap);
                return thumbBitmap;
            }else
                return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null){
                imageView.setImageBitmap(bitmap);
            }else{
                imageView.setImageResource(R.drawable.ic_fakkudroid_launcher);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_download_manager, parent, false);

        final Content content = contents.get(position);

        String templateTvSerie = context.getResources().getString(R.string.tvSeries);
        String templateTvArtist = context.getResources().getString(R.string.tvArtists);
        String templateTvTags = context.getResources().getString(R.string.tvTags);

        TextView tvTitle = (TextView) rowView.findViewById(R.id.tvTitle);
        ImageView ivCover = (ImageView) rowView.findViewById(R.id.ivCover);
        TextView tvSerie = (TextView) rowView.findViewById(R.id.tvSerie);
        TextView tvArtist = (TextView) rowView.findViewById(R.id.tvArtist);
        TextView tvTags = (TextView) rowView.findViewById(R.id.tvTags);

        tvTitle.setText(content.getTitle());
        if (content.getSerie() != null)
            tvSerie.setText(Html.fromHtml(templateTvSerie.replace("@serie@", content.getSerie().getName())));
        else
            tvSerie.setText(Html.fromHtml(templateTvSerie.replace("@serie@", "")));

        String artists = "";
        if (content.getArtists() != null)
            for (int i = 0; i < content.getArtists().size(); i++) {
                Attribute attribute = content.getArtists().get(i);
                artists += attribute.getName();
                if (i != content.getArtists().size() - 1) {
                    artists += ", ";
                }
            }
        tvArtist.setText(Html.fromHtml(templateTvArtist.replace("@artist@", artists)));

        String tags = "";
        if (content.getTags() != null)
            for (int i = 0; i < content.getTags().size(); i++) {
                Attribute attribute = content.getTags().get(i);
                tags += templateTvTags.replace("@tag@", attribute.getName());
                if (i != content.getTags().size() - 1) {
                    tags += ", ";
                }
            }
        tvTags.setText(Html.fromHtml(tags));

        final File dir = Helper.getDownloadDir(content.getFakkuId(), getContext());
        File coverFile = new File(dir, "thumb.jpg");

        loadBitmap(coverFile, ivCover);

        Button btnCancel = (Button) rowView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DownloadManagerActivity) getContext()).cancel(content);
                notifyDataSetChanged();
            }
        });
        Button btnPause = (Button) rowView.findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(content.getStatus()!=Status.DOWNLOADING){
                    ((DownloadManagerActivity)getContext()).resume(content);
                }else {
                    ((DownloadManagerActivity)getContext()).pause(content);
                    notifyDataSetChanged();
                }
            }
        });
        if(content.getStatus()!=Status.DOWNLOADING){
            btnPause.setText(R.string.resume);
        }

        ProgressBar pb = (ProgressBar) rowView.findViewById(R.id.pbDownload);
        if(content.getStatus() == Status.PAUSED){
            pb.setVisibility(View.INVISIBLE);
        }else if(content.getPercent()>0){
            pb.setVisibility(View.VISIBLE);
            pb.setIndeterminate(false);
            pb.setProgress((int)content.getPercent());
        }else{
            pb.setVisibility(View.VISIBLE);
            pb.setIndeterminate(true);
        }

        return rowView;
    }
}
