package org.example.utills;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.util.Arrays;
import java.util.List;

public class YelpAPI {
    public void getYelpApi(){
        String[] array = {"chinese", "korean", "american", "japanese", "thai"}; //limited to certain cuisine so I don't explode DB
        List<String> cuisines= Arrays.asList(array);
        DataUploader dataUploader = new DataUploader();
        for(String cuisine:cuisines){
            System.out.println(cuisine);
            OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.yelp.com/v3/businesses/search");
            request.addHeader("Authorization", someSecret);
            request.addHeader("Cookie", secretHeader);
            request.addQuerystringParameter("term", cuisine);
            request.addQuerystringParameter("location", "nyc");
            request.addQuerystringParameter("limit", "50");
            request.addQuerystringParameter("businesses", "restaurant");
            Response response = request.send();
            System.out.println("body received from yelp: " + response.getBody());
            dataUploader.writeDB(response.getBody(), cuisine);
        }
    }
}
