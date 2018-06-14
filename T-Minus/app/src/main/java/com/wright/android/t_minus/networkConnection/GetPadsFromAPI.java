package com.wright.android.t_minus.networkConnection;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.wright.android.t_minus.LaunchPad;
import com.wright.android.t_minus.Manifest;
import com.wright.android.t_minus.Objects.Agency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class GetPadsFromAPI extends AsyncTask<String, Void, LaunchPad[]> {
    final private OnFinished mFinishedInterface;


    public interface OnFinished {
        void onFinished(LaunchPad[] _padList);
    }

    public GetPadsFromAPI(OnFinished _finished) {
        mFinishedInterface = _finished;
    }

    @Override
    protected LaunchPad[] doInBackground(String... _params) {
        ArrayList<LaunchPad> launchPadArrayList = parseJSON(NetworkUtils.getNetworkData("https://launchlibrary.net/1.3/pad?next=50&mode=verbose"));
        return launchPadArrayList.toArray(new LaunchPad[launchPadArrayList.size()]);
    }

    private ArrayList<LaunchPad> parseJSON(String api){
        ArrayList<LaunchPad> launchPadArrayList = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(api);
            JSONArray hitsJson = response.getJSONArray("pads");
            for (int i = 0; i < hitsJson.length(); i++) {
                JSONObject obj = hitsJson.getJSONObject(i);
                String name = obj.getString("name");
                double latitude  = obj.getDouble("latitude");
                double longitude = obj.getDouble("longitude");
                JSONArray agenciesJson = obj.getJSONArray("agencies");
                ArrayList<Agency> agencies = new ArrayList<>();
                for(int j = 0; j < agenciesJson.length(); j++){
                    JSONObject agencyObj = hitsJson.getJSONObject(i);
                    int id = agencyObj.getInt("id");
                    String agencyName = agencyObj.getString("name");
                    String countryCode = agencyObj.getString("countryCode");
                    agencies.add(new Agency(id, agencyName,countryCode));
                }
                Location location = new Location("");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                launchPadArrayList.add(new LaunchPad(name, location, agencies.toArray(new Agency[agencies.size()])));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return launchPadArrayList;
    }

    @Override
    protected void onPostExecute(LaunchPad[] _result) {
        super.onPostExecute(_result);
        // Update the UI
        if (_result != null) {
            mFinishedInterface.onFinished(_result);
        }
    }
}
