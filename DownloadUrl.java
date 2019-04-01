package com.example.iqmma.whathappensinthecity;

import android.provider.ContactsContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//This class it will retrieve place data from
//A url through an http connection
public class DownloadUrl {

    public String ReadTheUrl(String placeURL) throws IOException{
        String data = "" ;
        InputStream inputStream = null;

        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(placeURL);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //read the data from url
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line = "";
            while ((line = bufferedReader.readLine() ) !=  null){
                stringBuffer.append(line);
            }

            // Convert String buffer to String
            data = stringBuffer.toString();
            bufferedReader.close();



        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }



        return  data;
    }

}
