package apk.com.example.sourav.sunshine;

import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingActivityFragment extends Fragment {
//public class SettingActivityFragment extends PreferenceFragment {

    public SettingActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        //addPreferencesFromResource(R.xml.pref_general);
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }


}
