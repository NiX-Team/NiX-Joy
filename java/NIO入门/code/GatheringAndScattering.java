package wiki;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

public class GatheringAndScattering {

    private static String FILENAME = "config/sample.txt";
    private static Charset charset = Charset.forName("UTF-8");

    public static void main(String[] args) {

        String data1 = "1A channel that can write bytes from a sequence of buffers.";
        String data2 = "23A channel that can read bytes into a sequence of buffers.";

        // We are going to store 2 data's to the file using GatheringByteChannel
//        gathering(data1, data2);

        scattering();
    }

    private static void scattering() {
        ByteBuffer bblen1 = ByteBuffer.allocate(1024);
        ByteBuffer bblen2 = ByteBuffer.allocate(1024);

        ByteBuffer bbdata1 = null;
        ByteBuffer bbdata2 = null;

        FileInputStream in;
        try {
            in = new FileInputStream(FILENAME);
            ScatteringByteChannel scatter = in.getChannel();

            // Read 2 length first to get the length of 2 data
            scatter.read(new ByteBuffer[] {bblen1, bblen2});

            // We have to call rewind if want to read buffer again. It is same as bblen1.position(0).
            // bblen1.rewind();
            // bblen2.rewind();

            // Seek position to 0 so that we can read the data again.
            bblen1.position(0);
            bblen2.position(0);

            int len1 = bblen1.asIntBuffer().get();
            int len2 = bblen2.asIntBuffer().get();

            // Try to test lengths are correct or not.
            System.out.println("Scattering : Len1= " + len1);
            System.out.println("Scattering : Len2= " + len2);

            bbdata1 = ByteBuffer.allocate(len1);
            bbdata2 = ByteBuffer.allocate(len2);

            // Read data from the channel
            scatter.read(new ByteBuffer[] {bbdata1, bbdata2});

        } catch (FileNotFoundException exObj) {
            exObj.printStackTrace();
        } catch (IOException ioObj) {
            ioObj.printStackTrace();
        }

        // Testing the data is correct or not.
        String data1 = new String(bbdata1.array(), charset);
        String data2 = new String(bbdata2.array(), charset);

        System.out.println(data1);
        System.out.println(data2);
    }

    private static void gathering(String data1, String data2) {
        // Store the length of 2 data using GatheringByteChannel
        ByteBuffer bblen1 = ByteBuffer.allocate(1024);
        ByteBuffer bblen2 = ByteBuffer.allocate(1024);

        // Next two buffer hold the data we want to write
        ByteBuffer bbdata1 = ByteBuffer.wrap(data1.getBytes());
        ByteBuffer bbdata2 = ByteBuffer.wrap(data2.getBytes());

        int len1 = data1.length();
        int len2 = data2.length();

        // Writing length(data) to the Buffer
        bblen1.asIntBuffer().put(len1);
        bblen2.asIntBuffer().put(len2);

        System.out.println("Gathering : Len1= " + len1);
        System.out.println("Gathering : Len2= " + len2);

        // Write data to the file
        try {
            FileOutputStream out = new FileOutputStream(FILENAME);
            GatheringByteChannel gather = out.getChannel();
            gather.write(new ByteBuffer[] {bblen1, bblen2, bbdata1, bbdata2});
            out.close();
            gather.close();
        } catch (FileNotFoundException exObj) {
            exObj.printStackTrace();
        } catch(IOException ioObj) {
            ioObj.printStackTrace();
        }
    }
}