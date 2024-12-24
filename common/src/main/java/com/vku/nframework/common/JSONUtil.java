package com.vku.nframework.common;

import com.google.gson.*;
import java.lang.reflect.*;

class ThrowableTypeAdapter implements JsonSerializer<Throwable> {
    @Override
    public JsonElement serialize(Throwable src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return context.serialize(null);
        }
        return context.serialize(src.toString());
    }
}

public class JSONUtil{

private JSONUtil(){}

public static String toJSON(java.io.Serializable serializable){
try{
    Gson gson = new GsonBuilder().registerTypeAdapter(Throwable.class, new ThrowableTypeAdapter()).create();
    return gson.toJson(serializable);
}catch(Exception e){
System.out.println(e);
e.getStackTrace();
System.out.println("Exception in toJSON");
return "{}";
}
}

// now making the method generic
// can be used as 
// Bulb b = JSONUtil.fromJSON(someString,Bulb.class);

public static <T> T fromJSON(String jsonString, Class<T> c){
try{
 Gson gson = new GsonBuilder().registerTypeAdapter(Throwable.class, new ThrowableTypeAdapter()).create();
T t ;
t = gson.fromJson(jsonString,c);
return t;
}catch(Exception e){
System.out.println("Exception in fromJSON");
return null;
}
}

}