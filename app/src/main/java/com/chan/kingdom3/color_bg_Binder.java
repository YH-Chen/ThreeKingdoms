package com.chan.kingdom3;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

/**
 * Created by Shaw on 2017/11/19.
 */

public class color_bg_Binder  implements SimpleAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Object data, String textRepresentation){
        if((view instanceof RelativeLayout) && (data instanceof Integer)){
            RelativeLayout ReView = (RelativeLayout) view;
            int color = (int) data;
            ReView.setBackgroundColor(color);
            return  true;
        }
        return false;
    }
}
