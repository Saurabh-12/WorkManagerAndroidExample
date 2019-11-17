package com.example.myapplication;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<String, Void, String> {

    String dstAddress;
    int dstPort;
    String response = "";
    TextView textResponse;

    Client(String addr, int port, TextView textResponse) {
        dstAddress = addr;
        dstPort = port;
        this.textResponse = textResponse;
    }

    @Override
    protected String doInBackground(String... voids) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[]buffer = new byte[1024];

            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

// Send first message
            dOut.writeByte(1);
            dOut.writeUTF("This is the first type of message.");
            dOut.flush(); // Send off the data

// Send the second message
            dOut.writeByte(2);
            dOut.writeUTF("This is the second type of message.");
            dOut.flush(); // Send off the data

// Send the third message
            dOut.writeByte(3);
            dOut.writeUTF("This is the third type of message (Part 1).");
            dOut.writeUTF("This is the third type of message (Part 2).");
            dOut.flush(); // Send off the data

// Send the exit message
            dOut.writeByte(-1);
            dOut.flush();

            dOut.close();

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

            /*
             * notice: inputStream.read() will block if no data return
             */
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
                Log.d("Saurabh", response);
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        textResponse.setText(response);
        super.onPostExecute(s);
    }
}
