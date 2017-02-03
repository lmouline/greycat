package org.mwg;

import io.nats.client.Connection;
import io.nats.client.ConnectionFactory;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import org.mwg.plugin.Storage;
import org.mwg.struct.Buffer;

import java.util.concurrent.TimeUnit;

public class NatsClient implements Storage {

    private final ConnectionFactory cf;
    private Connection nc;

    public NatsClient(String... servers) {
        cf = new ConnectionFactory();
        cf.setServers(servers);
    }

    @Override
    public void connect(Graph graph, Callback<Boolean> callback) {
        try {
            nc = cf.createConnection();
            callback.on(true);
        } catch (Exception e) {
            e.printStackTrace();
            callback.on(false);
        }
    }

    @Override
    public void get(Buffer keys, Callback<Buffer> callback) {
        /*
        nc.request("","", 4, TimeUnit.DAYS, new MessageHandler(){

            @Override
            public void onMessage(Message message) {

            }
        });*/
    }

    @Override
    public void put(Buffer stream, Callback<Boolean> callback) {

    }

    @Override
    public void remove(Buffer keys, Callback<Boolean> callback) {

    }


    @Override
    public void lock(Callback<Buffer> callback) {

    }

    @Override
    public void unlock(Buffer previousLock, Callback<Boolean> callback) {

    }

    @Override
    public void disconnect(Callback<Boolean> callback) {

    }
}
