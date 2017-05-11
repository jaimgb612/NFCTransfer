package com.example.nfctransfer.websockets;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

public class SocketConnectionManager {

    private boolean _isConnected = false;
    private List<SocketConnectionWatcher> _watchers;
    private Socket _socket;


    private SocketConnectionManager() {
        _watchers = new ArrayList<>();
    }
    private static SocketConnectionManager INSTANCE = new SocketConnectionManager();

    public static SocketConnectionManager getInstance()
    {	return INSTANCE;
    }

    public void registerWatcher(SocketConnectionWatcher watcher) {
        _watchers.add(watcher);
        if (_isConnected) {
            watcher.onSocketConnected();
        }
    }

    public void notifySocketConnected() {
        _isConnected = true;
        for (SocketConnectionWatcher w : _watchers) {
            w.onSocketConnected();
        }
    }

    public void notifySocketDisonnected() {
        _isConnected = false;
        for (SocketConnectionWatcher w : _watchers) {
            w.onSocketDisconnected();
        }
    }

    public Socket getSocket() {
        return _socket;
    }

    public void setSocket(Socket socket) {
        this._socket = socket;
    }
}
