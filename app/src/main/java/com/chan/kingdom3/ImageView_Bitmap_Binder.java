package com.chan.kingdom3;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

/**
 * Created by 61915 on 17/11/12.
 */

public class ImageView_Bitmap_Binder implements SimpleAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Object data, String textRepresentation){
        if((view instanceof ImageView) && (data instanceof Bitmap)){
            ImageView imageView = (ImageView) view;
            Bitmap bm = (Bitmap) data;
            imageView.setImageBitmap(bm);
            return  true;
        }
        return false;
    }
}
