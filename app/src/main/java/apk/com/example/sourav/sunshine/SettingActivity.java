package apk.com.example.sourav.sunshine;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.content.SharedPreferences;




//public class SettingActivity extends AppCompatActivity {
public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener,SharedPreferences.OnSharedPreferenceChangeListener{
//public class SettingActivity extends PreferenceFragment implements Preference.OnPreferenceChangeListener{
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        String stringValue= newValue.toString();

        if(preference instanceof ListPreference){
            ListPreference listPreference=(ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if(prefIndex>=0){
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }else {
                preference.setSummary(stringValue);
            }
        }


        //setPreferenceSummary(preference, value);
        return true;
        // return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            String s= getString(R.string.pref_general_key);
            String temp= getString(R.string.Pref_temp_unit_key);


            Log.e("Setting String:", " Strings: " + s);
            Log.e("Setting PreferenceFnd:", " ResponseFindOncreate  : " + findPreference(s));

            Log.e("Setting String:", " String: " + temp);
            Log.e("Setting tem Pref:", " Respons  : " + findPreference(temp));


            SettingActivity ac= new SettingActivity();
            ac.bindPreferenceSummaryToValue(findPreference(s));
            ac.bindPreferenceSummaryToValue(findPreference(temp));

        }

    }



    @Override
    //protected void onCreate(Bundle savedInstanceState) {
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

      //  Log.e("Setting Preference:", " Response  : " + getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragmentFindPreference()).commit());
       // Log.e("Setting PreferenceFnd:", " ResponseFindOncreate  : " + findPreference(getString(R.string.pref_general_key)));
       // bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_general_key)));

    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
         Log.e("From Preference() :", " preference: " + preference);
        preference.setOnPreferenceChangeListener(this);

        // Set the preference summaries
        onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }



    // This gets called after th
}
