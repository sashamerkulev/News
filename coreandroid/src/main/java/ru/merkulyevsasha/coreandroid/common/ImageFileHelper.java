package ru.merkulyevsasha.coreandroid.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ImageFileHelper {

    private final File imageFile;

    public ImageFileHelper(Context context, String fileName) {
        String imageFilename = fileName.contains(".png") ? "" : ".png";
        imageFile = new File(context.getFilesDir(), imageFilename);
    }

    public boolean exists() {
        return imageFile.exists();
    }

    public File file() {
        return imageFile;
    }

    public void compress() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        if (myBitmap == null) return;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);

            bos.flush();
            bos.close();
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createImageFile(InputStream in) throws IOException {
        FileOutputStream fos = new FileOutputStream(imageFile);

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;

        while ((len = in.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }

        in.close();
        fos.close();
    }

    public static String getTempFileName() {
        DateFormat format = new SimpleDateFormat("yyyyMMdd-hhmmss", Locale.getDefault());
        return "File" + format.format(Calendar.getInstance().getTime());
    }

    void createFile(Bitmap bitmap) throws IOException {
        //noinspection ResultOfMethodCallIgnored
        imageFile.createNewFile();

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(imageFile);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
    }
}
