package com.eatcarefully.backend.service;

import org.json.JSONObject;

import java.util.List;

public class ProductJSONUtils {



    //remove 'en:' and replace '-' with ' '
    public static String formatApiString(String string){

        if(string == null || string.isEmpty())
            return null;

        if(string.contains(":")){
            String[] temp = string.split(":");
            if(temp.length > 1)
                string = temp[1];
        }

        if(string.contains("-")){
            string = string.replace("-", " ");
        }

        string = string.substring(0,1).toUpperCase() + string.substring(1);

        return string;

    }

    public static JSONObject getNestedObject(JSONObject object, List<String> keys){

        if(object == null || keys.isEmpty())
            return null;

        // get first
        JSONObject temp = object.optJSONObject(keys.get(0));

        for(int i = 1; i <= keys.size(); i++){

            if(temp == null)
                break;

            if(i == keys.size()){
                return temp;
            }

            temp = temp.optJSONObject(keys.get(i));

        }

        return null;
    }




}
