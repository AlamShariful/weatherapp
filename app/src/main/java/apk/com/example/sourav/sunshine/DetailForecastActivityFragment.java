package apk.com.example.sourav.sunshine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailForecastActivityFragment extends Fragment {

    private String mforCcast;
    private static final String hashTagValue="#Sunshine";
   // boolean mDrawTabWhileInitializing = false;



    public DetailForecastActivityFragment() {

        Log.e("FromDetailFor","FromForeCastInside:"+3);
        //createShareForecastIntent();
        setHasOptionsMenu(true);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Intent intent =getActivity().getIntent();

        View rootView = inflater.inflate(R.layout.fragment_detail_forecast, container, false);
        if (intent!=null&& intent.hasExtra(intent.EXTRA_TEXT)){
            mforCcast = intent.getStringExtra(intent.EXTRA_TEXT);
            ((TextView) rootView.findViewById(R.id.detail_forecast_text)).setText(mforCcast);

        }

        return rootView;
    }

    public Intent createShareForecastIntent(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mforCcast + hashTagValue);
        shareIntent.setType("text/plain");
        //startActivity(shareIntent);

        return shareIntent;
    }




}
