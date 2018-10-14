package cn.gl.bio.demo2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8989);
        while (true){
            Socket client = server.accept();
            System.out.println("与客户端连接成功");
            new Thread(new ServerThread(client)).start();
        }

    }
}
