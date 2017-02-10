package ru.merkulyevsasha.apprate;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class DialogHelper {

    public static Dialog getAppRateDialog(final Activity context, final DialogClickListener listener){

        final String[] sortItems = {
                context.getString(R.string.no_thanks_message),
                context.getString(R.string.remind_later_message),
                context.getString(R.string.rate_now_message)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.rate_title);
        builder.setMessage(R.string.rate_message);
        builder.setCancelable(false);

//        builder.setSingleChoiceItems(sortItems, 2,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int item) {
//                        dialog.dismiss();
//
//                        listener.onClick(item);
//                    }
//                });

        return builder.create();

    }

    public interface DialogClickListener{
        void onClick(int selectItemsIndex);
    }

}
