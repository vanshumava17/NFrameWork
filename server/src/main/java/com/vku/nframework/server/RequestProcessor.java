package com.vku.nframework.server;
import com.vku.nframework.common.*;
import java.net.*;
import java.io.*;
import java.nio.charset.*;
import java.lang.reflect.*;

class RequestProcessor extends Thread
{
private NFrameworkServer server;
private Socket socket;

RequestProcessor(NFrameworkServer server, Socket socket)
{
this.server = server;
this.socket = socket;
start();
}

public void run()
{
        try {
            // Initializing o/p and i/p streams
            InputStream is = this.socket.getInputStream();
            OutputStream os = this.socket.getOutputStream();

            // recieving request header from client
            int bytesToRecieve = 1024;
            int bytesReadCount;
            byte[] tmp = new byte[1024];
            byte[] header = new byte[1024];
            int k;
            int i = 0;
            int j = 0;
            while (j < bytesToRecieve) {
                bytesReadCount = is.read(tmp);
                if (bytesReadCount == -1)
                    continue;
                for (k = 0; k < bytesReadCount; k++) {
                    header[i] = tmp[k];
                    i++;
                }
                j = j + bytesReadCount;
            } // Request header recieved from client

            // getting the length of the request data from request header
            int requestLength = 0;
            i = 1;
            j = 1023;
            while (j >= 0) {
                requestLength = requestLength + (header[j] * i);
                i = i * 10;
                j--;
            }

            // Acknowledging the request Header
            byte ack[] = new byte[1];
            ack[0] = 1;
            os.write(ack);
            os.flush();

            // Recieving request data from client
            byte request[] = new byte[requestLength];
            bytesToRecieve = requestLength;
            i = 0;
            j = 0;
            while (j < bytesToRecieve) {
                bytesReadCount = is.read(tmp);
                if (bytesReadCount == -1)
                    continue;
                for (k = 0; k < bytesReadCount; k++) {
                    request[i] = tmp[k];
                    i++;
                }
                j += bytesReadCount;
            } // request data received
            System.out.println("Request recieved");// remove after testing

            // Deserializing byte array into json 
            String jsonString = new String(request,StandardCharsets.UTF_8);
            // deserialized into jsonString from 

            // converting jsonString to Request Object
            Request requestObject = JSONUtil.fromJSON(jsonString, Request.class);
                // requestObjectt contains servicePath and arguments
                // we want the Reference of that TCPServicd whict contains
                // Class and Method refrences;
            String servicePath = requestObject.getServicePath();
            TCPService tcpService = this.server.getTCPService(servicePath);

            // processing request and getting response
            Response responseObject = new Response();
            // System.out.println(responseObject + " created");
            if(tcpService == null){
                responseObject.setSuccess(false);
                responseObject.setResult(null);
                responseObject.setException(new Exception("Invalid Path : " + servicePath));
            } else {
                Class c = tcpService.c;
                Method method = tcpService.method;
                try{
                    Object serviceObject = c.newInstance();
                    // above method is used by sir which is depricated now so we can use below method
                    // Object serviceObject = c.getDeclaredConstructor().newInstance();
                    Object result = method.invoke(serviceObject, requestObject.getArguments());
                    responseObject.setSuccess(true);
                    responseObject.setResult(result);
                    responseObject.setException(null);
                }
                catch(InstantiationException instantiationException){
                    responseObject.setSuccess(false);
                    responseObject.setResult(null);
                    responseObject.setException(new RuntimeException("Unable to create Object to service class associated with the path : "+servicePath));
                }
                catch(IllegalAccessException illegalAccessException) {
                    responseObject.setSuccess(false);
                    responseObject.setResult(null);
                    responseObject.setException(new RuntimeException("Unable to create Object to service class associated with the path : "+servicePath));
                }
                catch(InvocationTargetException invocationTargetException) {
                    responseObject.setSuccess(false);
                    responseObject.setResult(null);
                    Throwable t = invocationTargetException.getCause();
                    responseObject.setException(t);
                }catch(Exception e) {
                    responseObject.setSuccess(false);
                    responseObject.setResult(null);
                    responseObject.setException(new RuntimeException("Unable to create Object to service class associated with the path : "+servicePath));
                }
            }

            // converting response into json
            String responseJSONString = JSONUtil.toJSON(responseObject);
            // System.out.println(responseJSONString);
            // serializing response json string into byte array
            byte objectBytes[] = responseJSONString.getBytes(StandardCharsets.UTF_8);

            // sending response header to client
            int responseLength = objectBytes.length;
            int x;
            i = 1023;
            x = responseLength;
            header = new byte[1024];
            while (x > 0) {
                header[i] = (byte) (x % 10);
                x = x / 10;
                i--;
            }
            os.write(header, 0, 1024);
            os.flush();

            // Recieving acknowledgement of response header from client
            while (true) {
                bytesReadCount = is.read(ack);
                if (bytesReadCount == -1)
                    continue;
                break;
            }

            // sending response to client
            int bytesToSend = responseLength;
            int chunkSize = 1024;
            j = 0;
            while (j < bytesToSend) {
                if (bytesToSend - j < chunkSize)
                    chunkSize = bytesToSend - j;
                os.write(objectBytes, j, chunkSize);
                os.flush();
                j = j + chunkSize;
            } // response sent

            // Recieving acknowledgement of response from client
            while (true) {
                bytesReadCount = is.read(ack);
                if (bytesReadCount == -1)
                    continue;
                break;
            }
            socket.close();
        } catch (Exception e) {
            e.getStackTrace();
            System.out.println(e);
        }


}
}