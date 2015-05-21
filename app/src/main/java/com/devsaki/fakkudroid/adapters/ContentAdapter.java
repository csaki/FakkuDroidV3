package com.devsaki.fakkudroid.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devsaki.fakkudroid.FakkuDroidApplication;
import com.devsaki.fakkudroid.MainActivity;
import com.devsaki.fakkudroid.R;
import com.devsaki.fakkudroid.database.FakkuDroidDB;
import com.devsaki.fakkudroid.database.domains.Attribute;
import com.devsaki.fakkudroid.database.domains.Content;
import com.devsaki.fakkudroid.database.domains.ImageFile;
import com.devsaki.fakkudroid.util.Constants;
import com.devsaki.fakkudroid.util.ConstantsPreferences;
import com.devsaki.fakkudroid.util.Helper;
import com.devsaki.fakkudroid.util.ImageQuality;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by neko on 11/05/2015.
 */
public class ContentAdapter extends ArrayAdapter<Content> {

    private final static String TAG = ContentAdapter.class.getName();
    private final Context context;
    private final List<Content> contents;

    public ContentAdapter(Context context, List<Content> contents) {
        super(context, R.layout.row_download, contents);
        this.context = context;
        this.contents = contents;
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_download, parent, false);

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

        ((FakkuDroidApplication)getContext().getApplicationContext()).loadBitmap(coverFile, ivCover);

        Button btnRead = (Button) rowView.findViewById(R.id.btnRead);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readContent(content, dir);
            }
        });
        Button btnDelete = (Button) rowView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContent(content, dir);
            }
        });
        Button btnView = (Button) rowView.findViewById(R.id.btnViewBrowser);
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewContent(content);
            }
        });
        return rowView;
    }

    private void readContent(Content content, File dir) {
        if (content.getImageFiles() != null)
            for (ImageFile imageFile : content.getImageFiles()) {
                File file = new File(dir, imageFile.getName());
                if (file.exists()) {
                    Helper.openFile(file, getContext());
                    return;
                }
            }
        else
            Helper.openFile(new File(dir, "001.jpg"), getContext());
    }

    private void deleteContent(final Content content, final File dir) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMessage(R.string.ask_delete)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FakkuDroidDB db = new FakkuDroidDB(getContext());

                                try {
                                    FileUtils.deleteDirectory(dir);
                                } catch (IOException e) {
                                    Log.e(TAG, "error deleting content directory", e);
                                }

                                db.deleteContent(content);

                                Toast.makeText(getContext(),
                                        getContext().getResources().getString(R.string.deleted)
                                                .replace("@content", content.getTitle()),
                                        Toast.LENGTH_SHORT).show();
                                contents.remove(content);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton(android.R.string.no, null).create().show();
    }

    private void viewContent(Content content) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra(MainActivity.INTENT_URL, Constants.FAKKU_URL + content.getUrl());

        getContext().startActivity(intent);
    }
}
