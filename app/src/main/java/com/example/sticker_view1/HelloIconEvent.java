package com.example.sticker_view1;

import android.view.MotionEvent;
import android.widget.Toast;
import com.example.stickerview.StickerIconEvent;
import com.example.stickerview.StickerView;

/**
 * @author wupanjie
 * @see StickerIconEvent
 */

public class HelloIconEvent implements StickerIconEvent{
  @Override public void onActionDown(StickerView stickerView, MotionEvent event) {

  }

  @Override public void onActionMove(StickerView stickerView, MotionEvent event) {

  }

  @Override public void onActionUp(StickerView stickerView, MotionEvent event) {
    Toast.makeText(stickerView.getContext(), "Hello World!", Toast.LENGTH_SHORT).show();
  }
}
