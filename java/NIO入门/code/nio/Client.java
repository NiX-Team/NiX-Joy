package cn.gl.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 7878));
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        boolean flag = true;
        while (flag){
            System.out.print("请输入：");
            String s = input.readLine();
            if ("bye".equals(s)){
                flag = false;
            }else {
                s = s + "\n";
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                byteBuffer.clear();
                byteBuffer.put(s.getBytes());
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
                byteBuffer.clear();
                socketChannel.read(byteBuffer);
                byteBuffer.flip();
                byte[] message = new byte[byteBuffer.remaining()];
                while (byteBuffer.hasRemaining()){
                    byteBuffer.get(message);
                }
                byteBuffer.clear();
                String messageString = new String(message);
                System.out.print(messageString);
            }
        }
        socketChannel.close();
    }

}
