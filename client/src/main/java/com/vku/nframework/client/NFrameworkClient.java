package com.vku.nframework.client;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import com.vku.nframework.common.*;

public class NFrameworkClient {
        public Object execute(String servicePath, Object  ...arguments) throws Throwable {
        try {
            Request request = new Request();
            request.setServicePath(servicePath);
            request.setArguments(arguments);
            String requestJSONString = JSONUtil.toJSON(request);
            // serializability of Student
            
            byte objectBytes[];
            objectBytes = requestJSONString.getBytes(StandardCharsets.UTF_8);

            // Creating header , contains size/length of byteArray;
            int requestLength = objectBytes.length;
            byte header[] = new byte[1024];
            // logic to put length into header
            int x;
            int i;
            i = 1023;
            x = requestLength;
            while (x > 0) {
                header[i] = (byte) (x % 10);
                x = x / 10;
                i--;
            } // header is created and initialized here

            // Creating connection to server
            Socket socket = new Socket("localhost", 5500);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();

            // sending the request header to the server
            os.write(header, 0, 1024);
            os.flush();

            // getting acknowledgement from server
            byte ack[] = new byte[1];
            int bytesReadCount;
            while (true) {
                bytesReadCount = is.read(ack);
                if (bytesReadCount == -1)
                    continue;
                break;
            } // acknowledgement recieved

            // sending request data to server
            int bytesToSend = requestLength;
            int chunkSize = 1024;
            int j = 0;
            while (j < bytesToSend) {
                if ((bytesToSend - j) < chunkSize)
                    chunkSize = bytesToSend - j;
                os.write(objectBytes, j, chunkSize);
                os.flush();
                j = j + chunkSize;
            }

            // recieving response header from server
            int bytesToRecieve = 1024;
            byte[] tmp = new byte[1024];
            int k;
            i = 0;
            j = 0;
            header = new byte[1024];
            while (j < bytesToRecieve) {
                bytesReadCount = is.read(tmp);
                if (bytesReadCount == -1)
                    continue;
                for (k = 0; k < bytesReadCount; k++) {
                    header[i] = tmp[k];
                    i++;
                }
                j = j + bytesReadCount;
            } // response header recieved from server

            // getting the length of the response data from response header
            int responseLength = 0;
            i = 1;
            j = 1023;
            while (j >= 0) {
                responseLength = responseLength + (header[j] * i);
                i = i * 10;
                j--;
            }

            // Acknowledging the response Header
            ack[0] = 1;
            os.write(ack);
            os.flush();

            // Recieving response data from server
            byte response[] = new byte[responseLength];
            bytesToRecieve = responseLength;
            i = 0;
            j = 0;
            while (j < bytesToRecieve) {
                bytesReadCount = is.read(tmp);
                if (bytesReadCount == -1)
                    continue;
                for (k = 0; k < bytesReadCount; k++) {
                    response[i] = tmp[k];
                    i++;
                }
                j += bytesReadCount;
            } // response data received
            System.out.println("Response recieved");

            // acknowledging response data
            ack[0] = 1;
            os.write(ack);
            os.flush(); // acknowledgement sent to server

            // closing the connection
            socket.close();

            // converting response data (byte array) into JSON then into Response object
            String  responseJSONString = new String(response,StandardCharsets.UTF_8);
            Response responseObject = JSONUtil.fromJSON(responseJSONString,Response.class);
            if(responseObject.getSuccess()) {
                return responseObject.getResult();
            }
            else{
                throw responseObject.getException();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;  
    }
}