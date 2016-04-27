package paquete.push_notifications;

/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import paquete.Droidlogin.library.Httppostaux;
import paquete.tufanoapp.R;

public class RegistrationIntentService extends IntentService
{
    private static final String TAG = "RegIntentService";
    //private static final String[] TOPICS = {"global"};

    public RegistrationIntentService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try
        {
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.

            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "GCM Registration Token: " + token);

            boolean correcto = sendRegistrationToServer(token);

            if(correcto)
            {
                // Subscribe to topic channels
                //subscribeTopics(token);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * <p>
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The token to be sent to the server.
     */
    private boolean sendRegistrationToServer(String token)
    {
        boolean exito = false;
        Httppostaux post= new Httppostaux();

        ArrayList<NameValuePair> listaParametros= new ArrayList<>();
        listaParametros.add(new BasicNameValuePair("token",token));

        String URL_connect = "http://tabbuche.net/tufano/android/add_token.php";
        JSONArray jdata = post.getserverdata(listaParametros, URL_connect);

        if (jdata!=null && jdata.length() > 0)
        {
            JSONObject json_data;
            try
            {
                json_data = jdata.getJSONObject(0);
                int logstatus=json_data.getInt("salida");

                if(logstatus==1)
                {
                    // Accion exitosa..
                    Log.i(TAG, "Token agregado exitosamente..");
                    exito=true;
                }
                else if(logstatus==2)
                {
                    // Accion exitosa, pero el token ya habia sido agregado en BD..
                    Log.i(TAG, "Token ya existente..");
                    exito=true;
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Log.e("JSON  ", "ERROR: NO DATA");
            return false;
        }

        return exito;
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    /*private void subscribeTopics(String token) throws IOException
    {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS)
        {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }*/

}
