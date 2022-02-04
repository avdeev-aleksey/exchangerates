package com.avdprog.exchangerates;

import com.google.gson.*;
import com.google.gson.internal.*;

import java.util.*;

public class WorkJson {

    public static List<ValuteData> parse(String inputString) {


        JsonObject jsonObject = new JsonParser().parse(inputString).getAsJsonObject();
        JsonObject valuteObject = jsonObject.getAsJsonObject("Valute");

        GsonBuilder builder = new GsonBuilder();

        HashMap map = builder.create().fromJson(valuteObject.toString(), HashMap.class);

        List<ValuteData> valuteList = new ArrayList<>();

        for (Object element : map.values()) {
            LinkedTreeMap value = (LinkedTreeMap) element;

            valuteList.add(new ValuteData(
                    value.get("ID").toString(),
                    value.get("NumCode").toString(),
                    value.get("CharCode").toString(),
                    value.get("Nominal").toString(),
                    value.get("Name").toString(),
                    value.get("Value").toString(),
                    value.get("Previous").toString()
            ));
        }

        return valuteList;

    }

}
