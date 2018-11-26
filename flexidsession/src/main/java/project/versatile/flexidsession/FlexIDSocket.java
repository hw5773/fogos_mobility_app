package project.versatile.flexidsession;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SdpObserver;

import java.io.*;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

import project.versatile.flexid.FlexID;

import static io.socket.client.Socket.EVENT_CONNECT;
import static io.socket.client.Socket.EVENT_DISCONNECT;

public class FlexIDSocket {
    private final String TAG = "FogOSSocket";
    private Socket socket;
    private boolean isInitiator = false;
    private boolean isChannelReady = false;
    private boolean isStarted = false;

    PeerConnectionFactory peerConnectionFactory;
    PeerConnection peerConnection;

    private DataInputStream dIn;
    private DataOutputStream dOut;

    public FlexIDSocket(FlexID flexid, byte[] connid) {
        try {
            // convert FlexID to IP address.
            String IP_addr = flexid.getLocator().getAddr();

            socket = IO.socket("http://13.125.93.129:3000");
            socket.connect();
            Log.d(TAG, "connect to signalling server: connect");

            socket.on(EVENT_CONNECT, args -> {
                Log.d(TAG, "connect to signalling server: connect");
                socket.emit("create or join", new String(connid));
            }).on("ipaddr", arg -> {
                Log.d(TAG, "connect to signalling server: ipaddr");
            }).on("created", args -> {
                Log.d(TAG, "connect to signalling server: created");
                isInitiator = true;
            }).on("full", args -> {
                Log.d(TAG, "connect to signalling server: full");
            }).on("join", args -> {
                Log.d(TAG, "connect to signalling server: join");
                Log.d(TAG, "connect to signalling server: another peer made a request to join room");
                Log.d(TAG, "connect to signalling server: this peer is the initiator of a room");
                isChannelReady = true;
            }).on("joined", args -> {
                Log.d(TAG, "connect to signalling server: joined");
                isChannelReady = true;
            }).on("log", args -> {
                for (Object arg : args) {
                    Log.d(TAG, "connect to signalling server: " + String.valueOf(arg));
                }
            }).on("message", args -> {
                Log.d(TAG, "connect to signalling server: got a message");
                if (args[0] instanceof String) {
                    String message = (String) args[0];

                    if (message.equals("got user media"))
                        maybeStart();
                } else {
                    JSONObject message = (JSONObject) args[0];

                    try {
                        if (message.getString("type").equals("offer")) {
                            if (!isInitiator && !isStarted)
                                maybeStart();

                            peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(SessionDescription.Type.OFFER, message.getString("sdp")));
                            doAnswer();
                        } else if (message.getString("type").equals("answer") && isStarted) {
                            peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(SessionDescription.Type.ANSWER, message.getString("sdp")));
                        } else if (message.getString("type").equals("candidate") && isStarted) {
                            IceCandidate candidate = new IceCandidate(message.getString("id"), message.getInt("label"), message.getString("candidate"));
                            peerConnection.addIceCandidate(candidate);
                        }
                    } catch (JSONException e) {
                        Log.getStackTraceString(e);
                    }
                }
            }).on(EVENT_DISCONNECT, args -> {
                Log.d(TAG, "connect to signalling server: disconnect");
            });
        } catch (URISyntaxException e) {
            Log.getStackTraceString(e);
        }
/*
            if((dIn = new DataInputStream(socket.getInputStream())) == null)
                System.exit(0);
            if((dOut = new DataOutputStream(socket.getOutputStream())) == null)
                System.exit(0);
        } catch(Exception e) {
            Log.getStackTraceString(e);
        }
*/
    }

    public FlexIDSocket(Socket sock) {
        try {
            socket = sock;
            dIn = new DataInputStream(socket.getInputStream());
            dOut = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public byte[] read() {
        try {
            int length = dIn.readInt();
//			System.out.println("received message lengeth: " + length);
            if(length > 0) {
                byte[] msg = new byte[length];
                dIn.readFully(msg, 0, msg.length);
                return msg;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
    public void write(byte[] msg) {
        try {
            dOut.writeInt(msg.length);
            dOut.write(msg);
            dOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public InputStream getInputStream() {
        try {
            return socket.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }
    public OutputStream getOutputStream() {
        try {
            return socket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }
    public void bind(SocketAddress bindpoint) {
        try {
            socket.bind(bindpoint);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void close() {
        try {
            socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void connect(SocketAddress endpoint) {
        try {
            socket.connect(endpoint);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    class SimpleSdpObserver implements SdpObserver {
        @Override
        public void onCreateSuccess(final SessionDescription sessionDescription) {

        }

        @Override
        public void onSetSuccess() {

        }

        @Override
        public void onCreateFailure(String s) {
            Log.d(TAG, "create fail: " + s);
        }

        @Override
        public void onSetFailure(String s) {
            Log.d(TAG, "set fail: " + s);
        }
    }
}