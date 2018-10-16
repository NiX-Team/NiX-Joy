package wiki;

import org.junit.Test;

import java.awt.desktop.SystemSleepEvent;
import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class channel {

    @Test
    public void test0() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("abc.txt", "rw"); // throws FileNotFoundException
        FileChannel inChannel = randomAccessFile.getChannel();
        ByteBuffer buf = ByteBuffer.allocate(48);
        int byteRead = inChannel.read(buf);// throws IOException
        while (byteRead != -1){
            System.out.println(byteRead);
            buf.flip();
            while (buf.hasRemaining()){
                System.out.println((char)buf.get());   // 针对 ASCII 字符， 中文用 CharBuffer
            }
            buf.clear();
            byteRead = inChannel.read(buf);
        }
    }

    @Test
    public void test1(){
        PrintStream ps = null;
        try {
            ps = new PrintStream("斗破苍穹_copy.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(ps);

        Path path = Paths.get("./斗破苍穹.txt");
        try {
            SeekableByteChannel channel = Files.newByteChannel(path);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int read = 0;
            while ( (read=channel.read(buffer)) != -1 || read!=0){
                buffer.flip();
                CharBuffer gbk = Charset.forName("GBK").decode(buffer);
//                System.out.println("10241024102410241024102410241024102410241024102410241024102410241024102410241024");
                System.out.println(gbk);
//                System.out.println("10241024102410241024102410241024102410241024102410241024102410241024102410241024");
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2(){
        FileChannel fileChannel = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("斗破苍穹_copy.txt");
            fileChannel = fos.getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Path path = Paths.get("./斗破苍穹.txt");
        ByteBuffer buffer = null;
        try {
            SeekableByteChannel channel = Files.newByteChannel(path);
            buffer = ByteBuffer.allocate(1024);
            int read = 0;
            while ( (read=channel.read(buffer)) != -1 || read!=0){
                buffer.flip();
//                CharBuffer gbk = Charset.forName("GBK").decode(buffer);
                fileChannel.write(buffer);
                buffer.clear();
            }
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fileChannel.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
