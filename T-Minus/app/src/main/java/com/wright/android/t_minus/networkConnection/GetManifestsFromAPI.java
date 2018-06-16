// Trae Wright
// JAV2 - C201803
// GetManifestsFromAPI.java
package com.wright.android.t_minus.networkConnection;

import android.os.AsyncTask;

import com.wright.android.t_minus.Objects.Manifest;
import com.wright.android.t_minus.Objects.PadLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class GetManifestsFromAPI extends AsyncTask<String, Void, Manifest[]> {
    final private OnFinished mFinishedInterface;


    public interface OnFinished {
        void onFinished(Manifest[] _redditList);
    }

    public GetManifestsFromAPI(OnFinished _finished) {
        mFinishedInterface = _finished;
    }

    @Override
    protected Manifest[] doInBackground(String... _params) {
        ArrayList<Manifest> ManifestArrayList = parseJSON(NetworkUtils.getNetworkData("https://launchlibrary.net/1.3/launch?next=50&mode=verbose"));
        return ManifestArrayList.toArray(new Manifest[ManifestArrayList.size()]);
    }

    private ArrayList<Manifest> parseJSON(String api){
        ArrayList<Manifest> ManifestArrayList = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(api);
            JSONArray hitsJson = response.getJSONArray("launches");
            for (int i = 0; i < hitsJson.length(); i++) {
                JSONObject obj = hitsJson.getJSONObject(i);
                String title = obj.getString("name");
                String time = obj.getString("net");
                String probability = obj.getString("probability");
                String windowStart = obj.getString("windowstart");
                String windowEnd = obj.getString("windowend");
                int statusId = obj.getInt("status");
                String status = parseStatus(statusId);
                String missionProvider = obj.getJSONObject("lsp").getString("name");
                JSONObject locationObj = obj.getJSONObject("location");
                String location  = locationObj.getString("name");
                int locationId  = locationObj.getInt("id");
                String countryCode  = locationObj.getString("countryCode");
                String imageURL = obj.getJSONObject("rocket").getString("imageURL");
                JSONArray urlArray = obj.getJSONArray("vidURLs");
                String url = null;
                if(urlArray.length() != 0){
                    url = urlArray.getString(0);
                }
                ManifestArrayList.add(new Manifest(title,time,imageURL, new PadLocation(locationId,location,countryCode),
                        status, probability, windowStart, windowEnd, missionProvider, url));
            }

            return ManifestArrayList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ManifestArrayList;
    }

    private String parseStatus(int id){
        switch (id){
            case 1:
                return "Launch is GO";
            case 2:
                return "Launch is NO-GO";
            case 3:
                return "Launch was a success";
            case 4:
                return "Launch failed";
            case 5:
                return "Unplanned hold";
            case 6:
                return "Vehicle is in flight";
            case 7:
                return "There was a partial failure during launch";
            default:
                return "Unknown";
        }
    }

    @Override
    protected void onPostExecute(Manifest[] _result) {
        super.onPostExecute(_result);
        // Update the UI
        if (_result != null) {
            mFinishedInterface.onFinished(_result);
        }
    }
}