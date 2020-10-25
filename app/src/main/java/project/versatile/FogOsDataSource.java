package project.versatile;

import android.net.Uri;
import android.util.Log;

import FogOSSecurity.SecureFlexIDSession;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.BaseDataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FogOSSocket.FlexIDSession;

public class FogOsDataSource extends BaseDataSource {

    private SecureFlexIDSession session = null;
    private int limit = 0;
    private int count = 2048;
    private int total = 0;

    public static final class FogOsDataSourceException extends IOException {

        public FogOsDataSourceException(IOException cause) {
            super(cause);
        }
    }

    /**
     * The default maximum datagram packet size, in bytes.
     */
    public static final int DEFAULT_MAX_PACKET_SIZE = 20000;

    /** The default socket timeout, in milliseconds. */
    public static final int DEFAULT_SOCKET_TIMEOUT_MILLIS = 20 * 1000;

    private final int socketTimeoutMillis;
    private final byte[] packetBuffer;
    private final DatagramPacket packet;

    private @Nullable Uri uri;
    private @Nullable
    DatagramSocket socket;
    private @Nullable
    MulticastSocket multicastSocket;
    private @Nullable
    InetAddress address;
    private @Nullable
    InetSocketAddress socketAddress;
    private boolean opened;

    private int packetRemaining;
    private int headerLength = 0;
    private boolean headerRemoved = false;

    public FogOsDataSource() {
        this(DEFAULT_MAX_PACKET_SIZE);
    }
    
    /**
     * Constructs a new instance.
     *
     * @param maxPacketSize The maximum datagram packet size, in bytes.
     */
    public FogOsDataSource(int maxPacketSize) {
        this(maxPacketSize, DEFAULT_SOCKET_TIMEOUT_MILLIS);
    }
    
    public FogOsDataSource(SecureFlexIDSession session, int limit) {
        this(DEFAULT_MAX_PACKET_SIZE);
        this.session = session;
        this.limit = limit;
        this.packetRemaining = 0;
    }

    public FogOsDataSource(SecureFlexIDSession session, int limit, int maxPacketSize) {
        this(maxPacketSize);
        this.session = session;
        this.limit = limit;
        this.packetRemaining = 0;
    }

    /**
     * Constructs a new instance.
     *
     * @param maxPacketSize The maximum datagram packet size, in bytes.
     * @param socketTimeoutMillis The socket timeout in milliseconds. A timeout of zero is interpreted
     *     as an infinite timeout.
     */
    public FogOsDataSource(int maxPacketSize, int socketTimeoutMillis) {
        super(/* isNetwork= */ true);
        this.socketTimeoutMillis = socketTimeoutMillis;
        packetBuffer = new byte[maxPacketSize];
        packet = new DatagramPacket(packetBuffer, 0, maxPacketSize);
    }

    @Override
    public long open(DataSpec dataSpec)  {
        uri = dataSpec.uri;
        /*
        try {
            address = InetAddress.getByName(host);
            socketAddress = new InetSocketAddress(address, port);
            if (address.isMulticastAddress()) {
                multicastSocket = new MulticastSocket(socketAddress);
                multicastSocket.joinGroup(address);
                socket = multicastSocket;
            } else {
                socket = new DatagramSocket(socketAddress);
            }
        } catch (IOException e) {
            throw new FogOsDataSourceException(e);
        }
        // Log.e("mckwak", "opened");
        */

        if (!opened) {
            String a = "GET /dash/test_input.mp4 HTTP/1.1\r\nConnection: keep-alive\r\nHost: 52.78.23.173\r\n\r\n";
            System.out.println(a.length());
            try {
                session.send(a);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        opened = true;
        return C.LENGTH_UNSET;
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        Log.e("mckwak","offset: " + offset + " readLength: " + readLength + " packetRemaining: " + packetRemaining);

        if (readLength == 0) {
            return 0;
        }

        if (packetRemaining == 0) {
            // We've read all of the data from the current packet. Get another.
            do {
                count = session.recv(packetBuffer, packetBuffer.length);
            } while (count <= 0);

            packetRemaining = count;

            if (headerRemoved == false) {
                String tmp = new String(packetBuffer);
                String avp = null;
                String key = null;
                String value = null;
                int idx = 0, colon = 0;
                Log.e("mckwak", "Header: " + tmp);
                do {
                    //Log.e("mckwak", "Header: " + tmp);
                    idx = tmp.indexOf("\r\n");
                    this.headerLength += idx + 2;
                    if (idx == 0) {
                        headerRemoved = true;
                        break;
                    }
                    avp = tmp.substring(0, idx);
                    colon = avp.indexOf(":");
                    if (colon > 0) {
                        key = avp.substring(0, colon).trim();
                        value = avp.substring(colon + 1, idx).trim();
                        Log.e("mckwak", "key: "+ key);
                        Log.e("mckwak", "value: " + value);
                        if (key.equals("Content-Length")) {
                            total = Integer.parseInt(value);
                            Log.e("mckwak", "Total Content Length: " + total);
                        }
                    }

                    tmp = tmp.substring(idx + 2);
                } while (true);
                packetRemaining -= this.headerLength;
                Log.e("mckwak", "Header Length: " + this.headerLength);
            }

            Log.e("mckwak", "count: " + count + " packetBuffer: " + MobilityActivity.byteArrayToHex(packetBuffer, 16));
            Log.e("mckwak", "First 5 bytes: " + packetBuffer[0] + " " + packetBuffer[1] + " " + packetBuffer[2] + " " + packetBuffer[3] + " " + packetBuffer[4]);
            Log.e("mckwak", "Last 5 bytes: " + packetBuffer[count-5] + " " + packetBuffer[count-4] + " " + packetBuffer[count-3] + " " + packetBuffer[count-2] + " " + packetBuffer[count-1]);
            Log.e("FogOSDataSource", "downloaded bytes: " + total);
        }
        int packetOffset = count - packetRemaining;
        int bytesToRead = Math.min(packetRemaining, readLength);
        System.arraycopy(packetBuffer, packetOffset, buffer, offset, bytesToRead);
        packetRemaining -= bytesToRead;
        // Log.e("mckwak", "packetOffset: " + packetOffset + " bytesToRead: " + bytesToRead + " packetRemaining: " + packetRemaining);

        return bytesToRead;

    }

    @Nullable
    @Override
    public Uri getUri() {
        return uri;
    }

    @Override
    public void close() {
        uri = null;
        if (multicastSocket != null) {
            try {
                multicastSocket.leaveGroup(address);
            } catch (IOException e) {
                // Do nothing.
            }
            multicastSocket = null;
        }
        if (socket != null) {
            socket.close();
            socket = null;
        }
        address = null;
        socketAddress = null;
        // packetRemaining = 0;
        // Log.e("mckwak", "closed");
        if (opened) {
            opened = false;
        }
    }
}
