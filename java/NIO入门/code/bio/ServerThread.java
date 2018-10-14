package cn.gl.bio.demo2;

import java.io.*;
import java.net.Socket;

public class ServerThread implements Runnable {
    private Socket client = null;

    public ServerThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            PrintStream out = new PrintStream(client.getOutputStream());
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
            boolean flag = true;
            while (flag){

                String s = buf.readLine();
                if (s==null || "".equals(s)){
                    flag = false;
                }else {
                    if ("bye".equals(s)){
                        flag = false;
                    }else {
                        out.println("echo: "+s);
                    }
                }
            }
            out.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
