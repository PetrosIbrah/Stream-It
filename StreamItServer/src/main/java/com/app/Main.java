package com.app;

import com.app.InitiatingClasses.LoadBalancer;
import com.app.InitiatingClasses.StreamServer;

public class Main {
    public static void main(String[] args) {
        LoadBalancer.BalanceLoad(8000);
        // StreamServer.StartSever(8000);
    }
}