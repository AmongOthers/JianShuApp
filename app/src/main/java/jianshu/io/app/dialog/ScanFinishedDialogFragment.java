package jianshu.io.app.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import jianshu.io.app.R;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScanFinishedDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScanFinishedDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFinishedDialogFragment extends DialogFragment {

  private OnFragmentInteractionListener mListener;
  private TextView scanText;
  private Button scanButton;

  public static ScanFinishedDialogFragment newInstance() {
    ScanFinishedDialogFragment fragment = new ScanFinishedDialogFragment();
    return fragment;
  }

  public ScanFinishedDialogFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_scan_finished_dialog, container, false);
    this.scanText = (TextView) view.findViewById(R.id.scan_text);
    this.scanButton = (Button) view.findViewById(R.id.scan_button);
    this.scanButton.setOnClickListener(new Button.OnClickListener() {

      @Override
      public void onClick(View v) {
        onViewButtonPressed();
      }
    });
    return view;
  }

  public void onViewButtonPressed() {
    if (mListener != null) {
      mListener.onViewButtonPressed();
    }
  }

  public void onScanFinished() {
    this.scanText.setText("扫描完成");
    this.scanButton.setVisibility(View.VISIBLE);
  }

  public void onScanError(String err) {
    this.scanText.setText(err);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (OnFragmentInteractionListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   * <p/>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface OnFragmentInteractionListener {
    public void onViewButtonPressed();
  }

}
