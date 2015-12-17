package com.handybook.handybook.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.net.URL;

//TODO: definitely rename this
public class UrlImageView extends ImageView
{
    public UrlImageView(final Context context)
    {
        super(context);
    }

    public UrlImageView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public UrlImageView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public UrlImageView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void loadFromUrl(URL url)
    {

    }
//TODO: set a timeout and make sure the task is cancelled when image destroyed
//    public void loadFromUrl(URL url)
//    {
//        LoadImageFromUrlTask loadImageFromUrlTask = new LoadImageFromUrlTask(){
//            @Override
//            protected void onPostExecute(final Bitmap bitmap)
//            {
//                super.onPostExecute(bitmap);
//                UrlImageView.this.setImageBitmap(bitmap);
//            }
//        };
//        loadImageFromUrlTask.execute(url);
//        loadImageFromUrlTask.get(1, TimeUnit.MINUTES);
//    }
//
//    private class LoadImageFromUrlTask extends AsyncTask<URL, Integer, Bitmap>
//    {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//        protected Bitmap doInBackground(URL ... url) {
//            Bitmap bitmap = null;
//            try {
//                bitmap = BitmapFactory.decodeStream((InputStream) url[0].getContent());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return bitmap;
//        }
//
//        @Override
//        protected void onProgressUpdate(final Integer... values)
//        {
//            super.onProgressUpdate(values);
//        }
//    }
}
