
import com.google.gson.*;

class XYZException extends Exception {
    XYZException(String message){
        super(message);
    }
}

class Response {
    public Object exception;
}

class eg2 {
    public static void main(String g[]){
        try{
            XYZException x = new XYZException("some problem");
            Response r = new Response();
            r.exception = x;
            Gson gson = new Gson();
            String  jsonString = gson.toJson(r);
            System.out.println(jsonString);

            // Response r2 = gson.fromJson(jsonString,Response.class);
            // Class c = r2.exception.getClass();
            // System.out.println(c.getName());
        }catch(Exception e){
            System.out.println(e);
        }
    }
}
