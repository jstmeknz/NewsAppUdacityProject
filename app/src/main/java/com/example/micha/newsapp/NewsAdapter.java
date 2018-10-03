package com.example.micha.newsapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter {

    public NewsAdapter(Activity context, ArrayList<News> newsList) {
        super(context, 0, newsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listViewItem = convertView;
        if (listViewItem == null) {
            listViewItem = LayoutInflater.from(getContext()).inflate(R.layout.list_layout, parent, false);
        }
        News news = (News) getItem(position);


        //set headline
        TextView titleText = (TextView) listViewItem.findViewById(R.id.title);
        titleText.setText(news.getTitle());

        //set section (world etc)
        TextView sectionText = (TextView) listViewItem.findViewById(R.id.section);
        sectionText.setText(news.getSection());

        //set date
        TextView pubDate = (TextView) listViewItem.findViewById(R.id.pub_date);
        pubDate.setText(news.getDate());

        //set author
        TextView authorName = (TextView) listViewItem.findViewById(R.id.author);
        authorName.setText(news.getAuthorName());

        ImageView imageImage = (ImageView) listViewItem.findViewById(R.id.thumbnail);
        imageImage.setImageBitmap(news.getThumbnail());




        return listViewItem;
    }
}
