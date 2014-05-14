package jianshu.io.app.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jianshu.io.app.R;
import jianshu.io.app.model.datapool.HotPageDataPool;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HotPagerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HotPagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotPagerFragment extends Fragment {

  private static final String WEEKLY_HOT_URL = "http://jianshu.io/top/weekly.html";
  private static final String MONTHLY_HOT_URL = "http://jianshu.io/top/monthly.html";

  private HotPageDataPool mWeeklyPool = new HotPageDataPool(WEEKLY_HOT_URL);
  private HotPageDataPool mMonthlyPool = new HotPageDataPool(MONTHLY_HOT_URL);
  private OnFragmentInteractionListener mListener;
  private ViewPager mPager;
  private PagerTabStrip mPagerTabStrip;
  private PagerAdapter mPagerAdapter;


  public static HotPagerFragment newInstance() {
    HotPagerFragment fragment = new HotPagerFragment();
    return fragment;
  }

  public HotPagerFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.hot_viewpager, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mPager = (ViewPager) getActivity().findViewById(R.id.pager);
    mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
    mPager.setAdapter(mPagerAdapter);
    mPagerTabStrip = (PagerTabStrip)getActivity().findViewById(R.id.pager_title_strip);
    mPagerTabStrip.setTabIndicatorColorResource(R.color.card_list_gray);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
//    try {
//      mListener = (OnFragmentInteractionListener) activity;
//    } catch (ClassCastException e) {
//      throw new ClassCastException(activity.toString()
//          + " must implement OnFragmentInteractionListener");
//    }
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

  private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter implements HotFragment.HotFragmentListner {
    private static final int NUM_PAGES = 2;

    public ScreenSlidePagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      if(position == 0) {
        return HotFragment.newInstance(this, mWeeklyPool);
      } else {
        return HotFragment.newInstance(this, mMonthlyPool);
      }
    }

    @Override
    public CharSequence getPageTitle(int position) {
      if(position == 0) {
        return "七日热门文章";
      } else {
        return "三十日热门文章";
      }
    }

    @Override
    public int getCount() {
      return NUM_PAGES;
    }

    @Override
    public void onRefreshStart() {
      //mPagerTabStrip.setTabIndicatorColorResource(R.color.card_list_gray);
    }

    @Override
    public void onRefreshEnd() {
      //mPagerTabStrip.setTabIndicatorColorResource(R.color.jianshu);
    }
  }

}
