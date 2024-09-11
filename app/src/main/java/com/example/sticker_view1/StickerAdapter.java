package com.example.sticker_view1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class StickerAdapter extends BaseAdapter {
    private Context context;
    private Integer[] stickerImages;

    public StickerAdapter(Context context, Integer[] stickerImages) {
        this.context = context;
        this.stickerImages = stickerImages;
    }

    @Override
    public int getCount() {
        return stickerImages.length;
    }

    @Override
    public Object getItem(int position) {
        return stickerImages[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(stickerImages[position]);
        return imageView;
    }
}
