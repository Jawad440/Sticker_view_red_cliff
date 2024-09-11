package com.example.sticker_view1;

import androidx.core.content.ContextCompat;

import com.example.stickerview.BitmapStickerIcon;
import com.example.stickerview.DeleteIconEvent;
import com.example.stickerview.FlipHorizontallyEvent;
import com.example.stickerview.StickerView;
import com.example.stickerview.ZoomIconEvent;

import java.util.Arrays;

public class StickerManager {
    private StickerView stickerView;
    public StickerManager(StickerView stickerView) {
        this.stickerView = stickerView;
        configureStickerView();
    }
private void configureStickerView(){
    BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(stickerView.getContext(),
            com.example.stickerview.R.drawable.sticker_ic_close_white_18dp),
            BitmapStickerIcon.LEFT_TOP);
    deleteIcon.setIconEvent(new DeleteIconEvent());

    BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(stickerView.getContext(),
            com.example.stickerview.R.drawable.sticker_ic_scale_white_18dp),
            BitmapStickerIcon.RIGHT_BOTOM);
    zoomIcon.setIconEvent(new ZoomIconEvent());

    BitmapStickerIcon flipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(stickerView.getContext(),
            com.example.stickerview.R.drawable.sticker_ic_flip_white_18dp),
            BitmapStickerIcon.RIGHT_TOP);
    flipIcon.setIconEvent(new FlipHorizontallyEvent());

    BitmapStickerIcon heartIcon =
            new BitmapStickerIcon(ContextCompat.getDrawable(stickerView.getContext(), R.drawable.ic_favorite_white_24dp),
                    BitmapStickerIcon.LEFT_BOTTOM);
    heartIcon.setIconEvent(new com.example.sticker_view1.HelloIconEvent());

    stickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon, heartIcon));}
}
