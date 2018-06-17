package com.wright.android.t_minus.networkConnection;

import android.os.AsyncTask;

import com.wright.android.t_minus.Objects.LaunchPad;
import com.wright.android.t_minus.Objects.Manifest;
import com.wright.android.t_minus.Objects.PadLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;


public class GetPadsFromAPI extends AsyncTask<String, Void, ArrayList<LaunchPad>> {
    final private OnFinished mFinishedInterface;


    public interface OnFinished {
        void onFinished(ArrayList<LaunchPad> _padList);
    }

    public GetPadsFromAPI(OnFinished _finished) {
        mFinishedInterface = _finished;
    }

    @Override
    protected ArrayList<LaunchPad> doInBackground(String... _params) {
        return parseJSON(NetworkUtils.getNetworkData("https://launchlibrary.net/1.3/pad?locationid="+_params[0]));
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
                int locationId = obj.getInt("locationid");
//                android.location.Location location = new android.location.Location("");
//                location.setLatitude(latitude);
//                location.setLongitude(longitude);
                launchPadArrayList.add(new LaunchPad(name, latitude, longitude, locationId));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        launchPadArrayList.sort(Comparator.comparing(LaunchPad::getLocationId));
        return launchPadArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<LaunchPad> _result) {
        super.onPostExecute(_result);
        // Update the UI
        if (_result != null) {
            mFinishedInterface.onFinished(_result);
        }
    }
}
