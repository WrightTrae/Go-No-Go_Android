package com.wright.android.t_minus.network_connection;

import android.os.AsyncTask;

import com.wright.android.t_minus.objects.Manifest;

import org.json.JSONArray;
import org.json.JSONException;

public class GetAgencyUrlAPI extends AsyncTask<String, Void, Manifest[]> {
    final private OnFinished mFinishedInterface;
    private final Manifest[] manifestArrayList;


    public interface OnFinished {
        void onFinished(Manifest[] _url);
    }

    public GetAgencyUrlAPI(OnFinished _finished, Manifest[] manifests) {
        mFinishedInterface = _finished;
        manifestArrayList = manifests;
    }

    @Override
    protected Manifest[] doInBackground(String... _params) {
        for (Manifest manifest:manifestArrayList) {
            if(manifest.getImageUrl() == null) {
                manifest.setAgencyURL(parseJSON(NetworkUtils.getNetworkData("https://autocomplete.clearbit.com/v1/companies/suggest?query="
                        + manifest.getAgencyName())));
            }
        }
        return manifestArrayList;
    }

    private String parseJSON(String api){
        try {
            JSONArray response = new JSONArray(api);
            if (response.length()>0){
                return response.getJSONObject(0).getString("logo");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
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
