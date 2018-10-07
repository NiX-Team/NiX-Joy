package cn.gl.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class Server {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocketChannel.configureBlocking(false);
        InetSocketAddress address = new InetSocketAddress(7878);
        serverSocket.bind(address);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true){
            int num = selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = channel.accept();
                    System.out.println("收到客户端的连接："+socketChannel);
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                if (selectionKey.isReadable()){
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    byteBuffer.clear();
                    int readBytes = channel.read(byteBuffer);
                    if (readBytes>0){
                        byteBuffer.flip();
                        byte[] bytes = new byte[byteBuffer.remaining()];
                        byteBuffer.get(bytes);
                        byteBuffer.clear();
                        String message = new String(bytes);
                        System.out.print("收到消息："+message);
                        String response = "echo: "+message;
                        byte[] responseBytes = response.getBytes();
                        byteBuffer.put(responseBytes);
                        byteBuffer.flip();
                        while (byteBuffer.hasRemaining()){
                            channel.write(byteBuffer);
                        }
                        byteBuffer.clear();
                    }
                }
                iterator.remove();
            }
        }

    }
}
