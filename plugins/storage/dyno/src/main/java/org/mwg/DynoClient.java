package org.mwg;

import com.netflix.dyno.jedis.DynoJedisClient;
import org.mwg.plugin.Storage;
import org.mwg.struct.Buffer;

public class DynoClient implements Storage {

    private DynoJedisClient jClient;
    private final String name;
    private final String clusterName;

    public DynoClient(String name, String clusterName) {
        this.name = name;
        this.clusterName = clusterName;
    }

    @Override
    public void connect(Graph graph, Callback<Boolean> callback) {
        /*
        DynoJedisClient jClient = new DynoJedisClient.Builder()
                .withApplicationName(name)
                .withDynomiteClusterName(clusterName)
                .withDiscoveryClient(discoveryClient)
                .build();
                */
    }

    @Override
    public void get(Buffer keys, Callback<Buffer> callback) {

    }

    @Override
    public void put(Buffer stream, Callback<Boolean> callback) {
        //jClient.set();
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
