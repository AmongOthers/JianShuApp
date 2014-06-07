package jianshu.io.app.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.extra.staggeredgrid.view.CardGridStaggeredView;
import it.gmariotti.cardslib.library.internal.Card;
import jianshu.io.app.R;
import jianshu.io.app.adapter.TimeStreamCardArrayAdapter;
import jianshu.io.app.card.CollectinUpdateCard;
import jianshu.io.app.card.CommentCard;
import jianshu.io.app.card.TimeStreamCard;
import jianshu.io.app.card.UserUpdateArticleCard;
import jianshu.io.app.card.UserUpdateFollowCard;
import jianshu.io.app.card.UserUpdateLikeCard;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimeStreamFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimeStreamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeStreamFragment extends Fragment {

  private OnFragmentInteractionListener mListener;
  private TimeStreamCardArrayAdapter mAdapter;
  private CardGridStaggeredView mGridView;

  public static TimeStreamFragment newInstance() {
    TimeStreamFragment fragment = new TimeStreamFragment();
    return fragment;
  }

  public TimeStreamFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    Activity activity = getActivity();
    View view = inflater.inflate(R.layout.fragment_timestream, container, false);
    mGridView = (CardGridStaggeredView)view.findViewById(R.id.timestream_grid);
    // Inflate the layout for this fragment
    List<Card> cards = new ArrayList<Card>();
    cards.add(new UserUpdateArticleCard(activity));
    cards.add(new CommentCard(activity));
    cards.add(new UserUpdateLikeCard(activity));
    cards.add(new UserUpdateFollowCard(activity));
    cards.add(new CollectinUpdateCard(activity));
    for(int i = 0; i < 10; i++) {
      cards.add(new TimeStreamCard(activity));
    }
    mAdapter = new TimeStreamCardArrayAdapter(activity, cards);
    mGridView.setAdapter(mAdapter);
    return view;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
    // TODO: Update argument type and name
    public void onFragmentInteraction(Uri uri);
  }

}
