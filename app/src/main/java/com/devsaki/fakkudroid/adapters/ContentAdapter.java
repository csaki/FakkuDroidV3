package com.devsaki.fakkudroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devsaki.fakkudroid.R;
import com.devsaki.fakkudroid.database.domains.Attribute;
import com.devsaki.fakkudroid.database.domains.Content;

import java.util.List;

/**
 * Created by neko on 11/05/2015.
 */
public class ContentAdapter extends ArrayAdapter<Content> {

    private final Context context;
    private final List<Content> contents;

    public ContentAdapter(Context context, List<Content> contents) {
        super(context, R.layout.row_download, contents);
        this.context = context;
        this.contents = contents;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_download, parent, false);

        Content content = contents.get(position);

        String templateTvSerie = context.getResources().getString(R.string.tvSeries);
        String templateTvArtist = context.getResources().getString(R.string.tvArtists);
        String templateTvTags = context.getResources().getString(R.string.tvTags);

        TextView tvTitle = (TextView) rowView.findViewById(R.id.tvTitle);
        ImageView ivCover = (ImageView) rowView.findViewById(R.id.ivCover);
        TextView tvSerie = (TextView) rowView.findViewById(R.id.tvSerie);
        TextView tvArtist = (TextView) rowView.findViewById(R.id.tvArtist);
        TextView tvTags = (TextView) rowView.findViewById(R.id.tvTags);

        tvTitle.setText(content.getTitle());
        tvSerie.setText(templateTvSerie.replace("@serie@", content.getSerie().getName()));

        String artists = "";
        for(int i = 0; i < content.getArtists().size(); i++){
            Attribute attribute = content.getArtists().get(i);
            artists += attribute.getName();
            if(i!=content.getArtists().size()-1){
                artists += ", ";
            }
        }
        tvArtist.setText(templateTvArtist.replace("@artist@", artists));

        String tags = "";
        for(int i = 0; i < content.getTags().size(); i++){
            Attribute attribute = content.getTags().get(i);
            tags += templateTvTags.replace("@tag@", attribute.getName());
            if(i!=content.getArtists().size()-1){
                tags += ", ";
            }
        }
        tvArtist.setText(tags);

        return rowView;
    }
}
