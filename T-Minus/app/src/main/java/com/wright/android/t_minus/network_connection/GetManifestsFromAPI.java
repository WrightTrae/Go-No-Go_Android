package com.wright.android.t_minus.network_connection;

import android.os.AsyncTask;

import com.wright.android.t_minus.main_tabs.manifest.ManifestDetailsActivity;
import com.wright.android.t_minus.objects.LaunchPad;
import com.wright.android.t_minus.objects.Manifest;
import com.wright.android.t_minus.objects.PadLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


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


        //TODO: Test Manifest
        SimpleDateFormat testdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a",Locale.getDefault());
        testdf.setTimeZone(TimeZone.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 60);
        String testFormattedDate = testdf.format(calendar.getTime());
        ManifestArrayList.add(new Manifest(ManifestDetailsActivity.testLaunchID, "Go/No-Go Test Launch", testFormattedDate,
                "https://s3.amazonaws.com/launchlibrary/RocketImages/placeholder_1920.png", null,null, null));


        try {
            JSONObject response = new JSONObject(api);
            JSONArray hitsJson = response.getJSONArray("launches");
            for (int i = 0; i < hitsJson.length(); i++) {
                JSONObject obj = hitsJson.getJSONObject(i);
                int id = obj.getInt("id");
                String title = obj.getString("name");
                String time = obj.getString("net");

                time = time.replace("UTC","");
                SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = df.parse(time);
                df = new SimpleDateFormat("MMM dd, yyyy hh:mm a",Locale.getDefault());
                df.setTimeZone(TimeZone.getDefault());
                String formattedDate = df.format(date);

                JSONObject locationObj = obj.getJSONObject("location");
                String location  = locationObj.getString("name");
                int locationId  = locationObj.getInt("id");
                JSONObject rocketObj = obj.getJSONObject("rocket");
                String imageURL = rocketObj.getString("imageURL");
                if(imageURL.equals("https://s3.amazonaws.com/launchlibrary/RocketImages/placeholder_1920.png")){
                    imageURL = null;
                }
                JSONArray agencyArray = rocketObj.getJSONArray("agencies");
                String agencyURL = null;
                String agencyName = null;
                if(agencyArray.length() > 0){
                    agencyURL = agencyArray.getJSONObject(0).getString("infoURL");
                    agencyName = agencyArray.getJSONObject(0).getString("name");
                }

                JSONArray padsArrayJSON = locationObj.getJSONArray("pads");
                ArrayList<LaunchPad> launchPads = null;
                for(int j = 0; j < padsArrayJSON.length(); j++){
                    if(launchPads == null){
                       launchPads = new ArrayList<>();
                    }
                    JSONObject padObj = padsArrayJSON.getJSONObject(j);
                    int padId = padObj.getInt("id");
                    String padName = padObj.getString("name");
                    Double padLat = padObj.getDouble("latitude");
                    Double padLong = padObj.getDouble("longitude");
                    launchPads.add(new LaunchPad(padId, padName,padLat,padLong,locationId));
                }
                ManifestArrayList.add(new Manifest(id,title,formattedDate,imageURL, agencyName, agencyURL,
                        new PadLocation(locationId,location,launchPads)));
            }
            return ManifestArrayList;
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        return ManifestArrayList;
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