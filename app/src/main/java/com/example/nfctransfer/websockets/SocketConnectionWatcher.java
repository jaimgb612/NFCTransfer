package com.example.nfctransfer.websockets;


public interface SocketConnectionWatcher {
    void onSocketConnected();
    void onSocketDisconnected();
}
