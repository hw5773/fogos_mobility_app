package project.versatile;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.BaseDataSource;
import com.google.android.exoplayer2.upstream.DataSpec;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

import FogOSSocket.FlexIDSession;

public class FogOsDataSource extends BaseDataSource {

    private FlexIDSession session = null;
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
    public static final int DEFAULT_MAX_PACKET_SIZE = 2048;

    /** The default socket timeout, in milliseconds. */
    public static final int DEFAULT_SOCKET_TIMEOUT_MILLIS = 8 * 1000;

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
    
    public FogOsDataSource(FlexIDSession session, int limit) {
        this(DEFAULT_MAX_PACKET_SIZE);
        this.session = session;
        this.limit = limit;
    }

    public FogOsDataSource(FlexIDSession session, int limit, int maxPacketSize) {
        this(maxPacketSize);
        this.session = session;
        this.limit = limit;
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
    public long open(DataSpec dataSpec) throws FogOsDataSourceException {
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
        }*/
        // Log.e("mckwak", "opened");

        opened = true;
        return C.LENGTH_UNSET;
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws FogOsDataSourceException {
        // Log.e("mckwak","offset: " + offset + " readLength: " + readLength + " packetRemaining: " + packetRemaining);
        if (readLength == 0) {
            return 0;
        }
        if (packetRemaining == 0) {
            // We've read all of the data from the current packet. Get another.
            count = session.receive(packetBuffer);
            while (count <= 0)
                count = session.receive(packetBuffer);
            total += count;
            packetRemaining = count;
            // Log.e("mckwak", "count: " + count + " packetBuffer: " + MobilityActivity.byteArrayToHex(packetBuffer, 16));
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
