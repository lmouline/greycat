package org.mwg;

import org.apache.hadoop.hbase.thrift.generated.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.mwg.plugin.Storage;
import org.mwg.struct.Buffer;
import org.mwg.struct.BufferIterator;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HBaseThriftStorage implements Storage {
    private static final String _connectedError = "PLEASE CONNECT YOUR DATABASE FIRST";
    private static final byte[] prefixKey = "prefix".getBytes();

    private static final String COLUMN_FAMILY = "data";
    private static final String COLUMN_NAME = "value";

    private String tablename;

    private TTransport transport;
    private Hbase.Client client;

    private boolean isConnected;
    private Graph graph;

    public HBaseThriftStorage(String tablename) {
        this.tablename = tablename;
    }


    @Override
    public void get(Buffer keys, Callback<Buffer> callback) {

        if (!isConnected) {
            throw new RuntimeException(_connectedError);
        }
        final Buffer result = graph.newBuffer();
        final BufferIterator it = keys.iterator();

        boolean isFirst = true;
//        final List<Get> gets = new ArrayList<>();
        final List<ByteBuffer> gets = new ArrayList<>();

        while (it.hasNext()) {
            final Buffer view = it.next();
            if (!isFirst) {
                result.write(Constants.BUFFER_SEP);
            } else {
                isFirst = false;
            }
//            final Get get = new Get(view.data());
//            gets.add(get);
            gets.add(ByteBuffer.wrap(view.data()));
        }

        try {
//            Result[] res = table.get(gets);
            List<TRowResult> res = client.getRows(ByteBuffer.wrap(Bytes.toBytes(tablename)), gets, new HashMap<>());
            if (res != null && !res.isEmpty()) {
                for (int i = 0; i < res.size(); i++) {
                    final TRowResult row = res.get(i);
                    // TODO check
                    TColumn c = row.getSortedColumns().iterator().next();
                    byte[] r = new byte[c.bufferForColumnName().remaining()];
                    c.bufferForColumnName().get(r);

//                    byte[] r = res[i].getValue(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(COLUMN_NAME));
                    if (r != null) {
                        result.writeAll(r);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (callback != null) {
            callback.on(result);
        }
    }


    @Override
    public void put(Buffer stream, Callback<Boolean> callback) {
        if (!isConnected) {
            throw new RuntimeException(_connectedError);
        }
        try {
            final List<BatchMutation> batchMutations = new ArrayList<>();

            final BufferIterator it = stream.iterator();
            while (it.hasNext()) {
                final Buffer keyView = it.next();
                final Buffer valueView = it.next();
                if (valueView != null) {
//                    final Put put = new Put(keyView.data());
//                    put.addColumn(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(COLUMN_NAME), valueView.data());

                    final List<Mutation> rowMutations = new ArrayList<>();
                    rowMutations.add(new Mutation(false, ByteBuffer.wrap(Bytes.toBytes(COLUMN_NAME)), ByteBuffer.wrap(valueView.data()), true));
                    batchMutations.add(new BatchMutation(ByteBuffer.wrap(keyView.data()), rowMutations));
                }
            }
//            table.put(puts);
            client.mutateRows(ByteBuffer.wrap(Bytes.toBytes(tablename)), batchMutations, new HashMap<>());

            if (callback != null) {
                callback.on(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.on(false);
            }
        }
    }

    @Override
    public void remove(Buffer keys, Callback<Boolean> callback) {

    }

    @Override
    public void connect(Graph graph, Callback<Boolean> callback) {
        if (isConnected) {
            if (callback != null) {
                callback.on(null);
            }
            return;
        }
        this.graph = graph;

        // Create a connection to the cluster.
        try {
            transport = new TSocket("192.168.25.60", 8383);
            TProtocol protocol = new TBinaryProtocol(transport, true, true);

            client = new Hbase.Client(protocol);

            transport.open();

            final List<ColumnDescriptor> descriptors = new ArrayList<>();
            final ColumnDescriptor cd = new ColumnDescriptor();
            cd.setName(Bytes.toBytes(COLUMN_FAMILY));
            descriptors.add(cd);
            byte[] tablename = Bytes.toBytes(this.tablename);

            try {
                client.createTable(ByteBuffer.wrap(tablename), descriptors);
            } catch (AlreadyExists alreadyExists) {
                System.out.println("WARN: table already exists");
            }

            isConnected = true;
            if (callback != null) {
                callback.on(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.on(null);
            }
        }


    }

    @Override
    public void lock(Callback<Buffer> callback) {
//        if (!isConnected) {
//            throw new RuntimeException(_connectedError);
//        }
//
//        try {
//            final Result result = table.get(new Get(prefixKey));
//            byte[] current = result.getValue(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(COLUMN_NAME));
//            if (current == null) {
//                current = new String("0").getBytes();
//            }
//            final Short currentPrefix = Short.parseShort(new String(current));
//            final Put put = new Put(prefixKey);
//            put.addColumn(Bytes.toBytes(COLUMN_FAMILY), Bytes.toBytes(COLUMN_NAME), ((currentPrefix + 1) + "").getBytes());
//            table.put(put);
//
//            if (callback != null) {
//                Buffer newBuf = graph.newBuffer();
//                Base64.encodeIntToBuffer(currentPrefix, newBuf);
//                callback.on(newBuf);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void unlock(Buffer previousLock, Callback<Boolean> callback) {
//        if (!isConnected) {
//            throw new RuntimeException(_connectedError);
//        }
//        callback.on(true);
    }

    @Override
    public void disconnect(Callback<Boolean> callback) {
//        try {
//            connection.close();
//            connection = null;
//            isConnected = false;
//
//            if (callback != null) {
//                callback.on(true);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (callback != null) {
//                callback.on(false);
//            }
//        }
    }
}
