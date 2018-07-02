// Trae Wright
// JAV2 - C201803
// GetManifestsFromAPI.java
package com.wright.android.t_minus.network_connection;

import android.os.AsyncTask;

import com.wright.android.t_minus.objects.ManifestDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GetManifestsDetailsFromAPI extends AsyncTask<String, Void, ManifestDetails> {
    final private OnFinished mFinishedInterface;


    public interface OnFinished {
        void onFinished(ManifestDetails details);
    }

    public GetManifestsDetailsFromAPI(OnFinished _finished) {
        mFinishedInterface = _finished;
    }

    @Override
    protected ManifestDetails doInBackground(String... _params) {
        return parseJSON(NetworkUtils.getNetworkData("https://launchlibrary.net/1.4/launch/"+_params[0]));
    }

    private ManifestDetails parseJSON(String api){
        try {
            JSONObject response = new JSONObject(api);
            JSONArray hitsJson = response.getJSONArray("launches");
            JSONObject obj = hitsJson.getJSONObject(0);
            String probability = obj.getString("probability");
            String windowStart = obj.getString("windowstart");
            String windowEnd = obj.getString("windowend");
            int statusId = obj.getInt("status");
            String status = parseStatus(statusId);
            String missionProvider = obj.getJSONObject("lsp").getString("name");

            JSONArray urlArray = obj.getJSONArray("vidURLs");
            String url = null;
            if(urlArray.length() != 0){
                url = urlArray.getString(0);
            }

            JSONObject missionsJSON = obj.getJSONArray("missions").getJSONObject(0);
            String type = missionsJSON.getString("typeName");
            String desc = missionsJSON.getString("description");

            return new ManifestDetails(status,probability,windowStart,windowEnd,missionProvider,url, type, desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String parseStatus(int id){
        switch (id){
            case 1:
                return "Launch is GO";
            case 2:
                return "TBD";
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
    protected void onPostExecute(ManifestDetails _result) {
        super.onPostExecute(_result);
        // Update the UI
        if (_result != null) {
            mFinishedInterface.onFinished(_result);
        }
    }
}