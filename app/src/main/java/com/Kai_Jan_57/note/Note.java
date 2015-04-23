package com.Kai_Jan_57.note;

import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Note {
    public Note(MainActivity mainActivity, String Note, int color, float posx, float posy) {
        Button neueNotitz = new Button(mainActivity);
        //neueNotitz.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        neueNotitz.setBackgroundColor(color);
        neueNotitz.invalidate();
        neueNotitz.setText(Note);
        neueNotitz.setTextColor(getInverseColor(color));
        neueNotitz.setX(posx);
        neueNotitz.setY(posy);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 50);
        layoutParams.width = LayoutParams.WRAP_CONTENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        neueNotitz.setLayoutParams(layoutParams);
        RelativeLayout rl = (RelativeLayout) mainActivity.findViewById(R.id.container);
        rl.addView(neueNotitz);
        MultiTouchListener mlt = new MultiTouchListener(mainActivity, neueNotitz);
        neueNotitz.setOnTouchListener(mlt);
    }

    private int getInverseColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = Color.alpha(color);
        return Color.argb(alpha, 255 - red, 255 - green, 255 - blue);
    }
}
