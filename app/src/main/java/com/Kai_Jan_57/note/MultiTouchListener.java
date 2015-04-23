package com.Kai_Jan_57.note;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class MultiTouchListener implements OnTouchListener, ColorPickerView.OnColorChangedListener {

    ColorPickerView ColorPickerView1;
    ColorPanelView ColorPanelView1;
    private View bu;
    int mx;
    int my;
    int _xDelta, _yDelta;
    float bxdelta, bydelta;
    boolean ads = false;

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    ViewGroup vg = (ViewGroup) bu.getParent();
                    vg.removeView(bu);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
            ads = false;
        }
    };

    public MainActivity mainActivity;

    public MultiTouchListener(MainActivity mainActivity1, Button bus) {
        mainActivity = mainActivity1;
        bu = bus;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) bu.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                bxdelta = bu.getX();
                bydelta = bu.getY();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (!ads) {

                    mx = (int) ((X - _xDelta) + bxdelta);
                    my = (int) ((Y - _yDelta) + bydelta);

                    bu.setX(mx);
                    bu.setY(my);
                    if (my < 1) {
                        bu.setY(1);
                    }
                }
                break;
            }


            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_UP: {
                if (mx < 0 && !ads) {
                    ads = true;
                    AlertDialog.Builder ab = new AlertDialog.Builder(mainActivity);
                    ab.setMessage(mainActivity.getString(R.string.question_delete))
                            .setCancelable(false)
                            .setTitle(mainActivity.getString(R.string.delete))
                            .setPositiveButton(mainActivity.getString(R.string.ok), dialogClickListener)
                            .setNegativeButton(mainActivity.getString(R.string.no), dialogClickListener);
                    AlertDialog ad = ab.create();
                    ad.show();
                }

                if (bxdelta - ((int) ((X - _xDelta) + bxdelta)) < 1 && bxdelta - ((int) ((X - _xDelta) + bxdelta)) > -1 && bydelta - ((int) ((Y - _yDelta) + bydelta)) < 1 && bydelta - ((int) ((Y - _yDelta) + bydelta)) > -1 && !ads) {

                    ads = true;
                    Button b = (Button) bu;
                    LayoutInflater li = LayoutInflater.from(mainActivity);
                    View pV = li.inflate(R.layout.input_dialog, null);
                    AlertDialog.Builder ab = new AlertDialog.Builder(mainActivity);
                    ab.setView(pV);
                    final EditText i = (EditText) pV.findViewById(R.id.editText1);
                    ColorPanelView1 = (ColorPanelView) pV.findViewById(R.id.ColorPanelView1);
                    ColorPickerView1 = (ColorPickerView) pV.findViewById(R.id.ColorPicker1);
                    ColorPickerView1.setOnColorChangedListener(this);
                    ColorPickerView1.setAlphaSliderVisible(true);
                    ColorPickerView1.setColor(getInverseColor(b.getCurrentTextColor()));
                    ColorPanelView1.setColor(ColorPickerView1.getColor());
                    i.setText(b.getText());
                    ab.setCancelable(false)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Button b = (Button) bu;
                                    b.setText(i.getText());
                                    b.setTextColor(getInverseColor(ColorPickerView1.getColor()));
                                    b.setBackgroundColor(ColorPickerView1.getColor());
                                    ads = false;
                                }
                            })
                            .setNegativeButton(R.string.abort, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ads = false;
                                    dialog.cancel();
                                }
                            })
                            .setTitle(R.string.edit_note);

                    AlertDialog aD = ab.create();
                    aD.show();
                }
            }

            break;
        }
        view.performClick();
        return true;
    }

    @Override
    public void onColorChanged(int newColor) {
        ColorPanelView1.setColor(ColorPickerView1.getColor());
    }

    private int getInverseColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = Color.alpha(color);
        return Color.argb(alpha, 255 - red, 255 - green, 255 - blue);
    }
}
