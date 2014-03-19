package jianshu.io.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Administrator on 14-3-8.
 */
public class NotReadyFragment extends DialogFragment {

  public static NotReadyFragment newInstance() {
    return new NotReadyFragment();
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity()).setTitle("界面维护中")
        .setPositiveButton("OK",
            new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
              }
            })
        .setNegativeButton("Cancel",
            new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialogInterface, int i) {

              }
            })
        .create();
  }
}
