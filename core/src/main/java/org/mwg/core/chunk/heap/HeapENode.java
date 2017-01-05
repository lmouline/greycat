package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.base.BaseExternalAttribute;
import org.mwg.base.BaseNode;
import org.mwg.core.CoreConstants;
import org.mwg.plugin.Resolver;
import org.mwg.struct.*;
import org.mwg.utility.Base64;
import org.mwg.utility.HashHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class HeapENode implements ENode {

    private final HeapEGraph egraph;
    private final HeapStateChunk chunk;
    private final Graph _graph;
    int _id;

    HeapENode(final HeapStateChunk p_chunk, final HeapEGraph p_egraph, final Graph p_graph, final int p_id, final HeapENode origin) {
        chunk = p_chunk;
        egraph = p_egraph;
        _graph = p_graph;
        _id = p_id;
        if (origin != null) {
            _capacity = origin._capacity;
            _size = origin._size;
            //copy keys
            if (origin._k != null) {
                long[] cloned_k = new long[_capacity];
                System.arraycopy(origin._k, 0, cloned_k, 0, _capacity);
                _k = cloned_k;
            }
            //copy types
            if (origin._type != null) {
                byte[] cloned_type = new byte[_capacity];
                System.arraycopy(origin._type, 0, cloned_type, 0, _capacity);
                _type = cloned_type;
            }
            //copy next if not empty
            if (origin._next != null) {
                int[] cloned_next = new int[_capacity];
                System.arraycopy(origin._next, 0, cloned_next, 0, _capacity);
                _next = cloned_next;
            }
            if (origin._hash != null) {
                int[] cloned_hash = new int[_capacity * 2];
                System.arraycopy(origin._hash, 0, cloned_hash, 0, _capacity * 2);
                _hash = cloned_hash;
            }
            if (origin._v != null) {
                _v = new Object[_capacity];
                for (int i = 0; i < _size; i++) {
                    switch (origin._type[i]) {
                        case Type.LONG_TO_LONG_MAP:
                            if (origin._v[i] != null) {
                                _v[i] = ((HeapLongLongMap) origin._v[i]).cloneFor(chunk);
                            }
                            break;
                        case Type.RELATION_INDEXED:
                            if (origin._v[i] != null) {
                                _v[i] = ((HeapRelationIndexed) origin._v[i]).cloneIRelFor(chunk);
                            }
                            break;
                        case Type.LONG_TO_LONG_ARRAY_MAP:
                            if (origin._v[i] != null) {
                                _v[i] = ((HeapLongLongArrayMap) origin._v[i]).cloneFor(chunk);
                            }
                            break;
                        case Type.STRING_TO_LONG_MAP:
                            if (origin._v[i] != null) {
                                _v[i] = ((HeapStringLongMap) origin._v[i]).cloneFor(chunk);
                            }
                            break;
                        case Type.RELATION:
                            if (origin._v[i] != null) {
                                _v[i] = new HeapRelation(chunk, (HeapRelation) origin._v[i]);
                            }
                            break;
                        case Type.DMATRIX:
                            if (origin._v[i] != null) {
                                _v[i] = new HeapDMatrix(chunk, (HeapDMatrix) origin._v[i]);
                            }
                            break;
                        case Type.LMATRIX:
                            if (origin._v[i] != null) {
                                _v[i] = new HeapLMatrix(chunk, (HeapLMatrix) origin._v[i]);
                            }
                            break;
                        case Type.EXTERNAL:
                            if (origin._v[i] != null) {
                                _v[i] = ((BaseExternalAttribute) origin._v[i]).copy();
                            }
                            break;
                        default:
                            _v[i] = origin._v[i];
                            break;
                    }
                }
            }
        } else {
            _capacity = 0;
            _size = 0;
        }
    }

    private int _capacity;
    private volatile int _size;
    private long[] _k;
    private Object[] _v;
    private int[] _next;
    private int[] _hash;
    private byte[] _type;
    private boolean _dirty;

    private void declareDirty() {
        if (!_dirty) {
            _dirty = true;
            egraph.declareDirty();
        }
    }

    void rebase() {
        for (int i = 0; i < _size; i++) {
            switch (_type[i]) {
                case Type.ERELATION:
                    final HeapERelation previousERel = (HeapERelation) _v[i];
                    previousERel.rebase(egraph);
                    break;
                case Type.ENODE:
                    final HeapENode previous = (HeapENode) _v[i];
                    _v[i] = egraph._nodes[previous._id];
                    break;
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private void allocate(int newCapacity) {
        if (newCapacity <= _capacity) {
            return;
        }
        long[] ex_k = new long[newCapacity];
        if (_k != null) {
            System.arraycopy(_k, 0, ex_k, 0, _capacity);
        }
        _k = ex_k;
        Object[] ex_v = new Object[newCapacity];
        if (_v != null) {
            System.arraycopy(_v, 0, ex_v, 0, _capacity);
        }
        _v = ex_v;
        byte[] ex_type = new byte[newCapacity];
        if (_type != null) {
            System.arraycopy(_type, 0, ex_type, 0, _capacity);
        }
        _type = ex_type;
        _capacity = newCapacity;
        _hash = new int[_capacity * 2];
        Arrays.fill(_hash, 0, _capacity * 2, -1);
        _next = new int[_capacity];
        Arrays.fill(_next, 0, _capacity, -1);
        for (int i = 0; i < _size; i++) {
            int keyHash = (int) HashHelper.longHash(_k[i], _capacity * 2);
            _next[i] = _hash[keyHash];
            _hash[keyHash] = i;
        }
    }

    private int internal_find(final long p_key) {
        if (_size == 0) {
            return -1;
        }
        final int hashIndex = (int) HashHelper.longHash(p_key, _capacity * 2);
        int m = _hash[hashIndex];
        while (m >= 0) {
            if (p_key == _k[m]) {
                return m;
            } else {
                m = _next[m];
            }
        }
        return -1;
    }

    private Object internal_get(final long p_key) {
        //empty chunk, we return immediately
        if (_size == 0) {
            return null;
        }
        int found = internal_find(p_key);
        if (found != -1) {
            return _v[found];
        }
        return null;
    }

    private void internal_set(final long p_key, final byte p_type, final Object p_unsafe_elem, boolean replaceIfPresent, boolean initial) {
        Object param_elem = null;
        //check the param type
        if (p_unsafe_elem != null) {
            try {
                switch (p_type) {
                    case Type.BOOL:
                        param_elem = (boolean) p_unsafe_elem;
                        break;
                    case Type.DOUBLE:
                        param_elem = (double) p_unsafe_elem;
                        break;
                    case Type.LONG:
                        if (p_unsafe_elem instanceof Integer) {
                            int preCasting = (Integer) p_unsafe_elem;
                            param_elem = (long) preCasting;
                        } else {
                            param_elem = (long) p_unsafe_elem;
                        }
                        break;
                    case Type.INT:
                        param_elem = (int) p_unsafe_elem;
                        break;
                    case Type.STRING:
                        param_elem = (String) p_unsafe_elem;
                        break;
                    case Type.DMATRIX:
                        param_elem = (DMatrix) p_unsafe_elem;
                        break;
                    case Type.LMATRIX:
                        param_elem = (LMatrix) p_unsafe_elem;
                        break;
                    case Type.RELATION:
                        param_elem = (Relation) p_unsafe_elem;
                        break;
                    case Type.ERELATION:
                        param_elem = (ERelation) p_unsafe_elem;
                        break;
                    case Type.ENODE:
                        param_elem = (ENode) p_unsafe_elem;
                        break;
                    case Type.EXTERNAL:
                        param_elem = p_unsafe_elem;
                        break;
                    case Type.DOUBLE_ARRAY:
                        double[] castedParamDouble = (double[]) p_unsafe_elem;
                        double[] clonedDoubleArray = new double[castedParamDouble.length];
                        System.arraycopy(castedParamDouble, 0, clonedDoubleArray, 0, castedParamDouble.length);
                        param_elem = clonedDoubleArray;
                        break;
                    case Type.LONG_ARRAY:
                        long[] castedParamLong = (long[]) p_unsafe_elem;
                        long[] clonedLongArray = new long[castedParamLong.length];
                        System.arraycopy(castedParamLong, 0, clonedLongArray, 0, castedParamLong.length);
                        param_elem = clonedLongArray;
                        break;
                    case Type.INT_ARRAY:
                        int[] castedParamInt = (int[]) p_unsafe_elem;
                        int[] clonedIntArray = new int[castedParamInt.length];
                        System.arraycopy(castedParamInt, 0, clonedIntArray, 0, castedParamInt.length);
                        param_elem = clonedIntArray;
                        break;
                    case Type.STRING_TO_LONG_MAP:
                        param_elem = (StringLongMap) p_unsafe_elem;
                        break;
                    case Type.LONG_TO_LONG_MAP:
                        param_elem = (LongLongMap) p_unsafe_elem;
                        break;
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        param_elem = (LongLongArrayMap) p_unsafe_elem;
                        break;
                    case Type.RELATION_INDEXED:
                        param_elem = (RelationIndexed) p_unsafe_elem;
                        break;
                    default:
                        throw new RuntimeException("Internal Exception, unknown type");
                }
            } catch (Exception e) {
                throw new RuntimeException("mwDB usage error, set method called with type " + Type.typeName(p_type) + " while param object is " + p_unsafe_elem);
            }
        }
        //first value
        if (_k == null) {
            //we do not allocate for empty element
            if (param_elem == null) {
                return;
            }
            _capacity = Constants.MAP_INITIAL_CAPACITY;
            _k = new long[_capacity];
            _v = new Object[_capacity];
            _type = new byte[_capacity];
            _next = new int[_capacity];
            _hash = new int[_capacity * 2];
            Arrays.fill(_hash, 0, _capacity * 2, -1);
            Arrays.fill(_next, 0, _capacity, -1);
            _k[0] = p_key;
            _v[0] = param_elem;
            _type[0] = p_type;
            _size = 1;
            int hashIndex = (int) HashHelper.longHash(p_key, _capacity * 2);
            _hash[hashIndex] = 0;
            if (!initial) {
                declareDirty();
            }
            return;
        }
        int entry = -1;
        int p_entry = -1;
        final int hashIndex = (int) HashHelper.longHash(p_key, _capacity * 2);
        int m = _hash[hashIndex];
        while (m != -1) {
            if (_k[m] == p_key) {
                entry = m;
                break;
            }
            p_entry = m;
            m = _next[m];
        }
        //case already present
        if (entry != -1) {
            if (replaceIfPresent || (p_type != _type[entry])) {
                if (param_elem == null) {
                    //unHash previous
                    if (p_entry != -1) {
                        _next[p_entry] = _next[entry];
                    } else {
                        _hash[hashIndex] = -1;
                    }
                    int indexVictim = _size - 1;
                    //just pop the last value
                    if (entry == indexVictim) {
                        _k[entry] = -1;
                        _v[entry] = null;
                        _type[entry] = -1;
                    } else {
                        //we need to reHash the new last element at our place
                        _k[entry] = _k[indexVictim];
                        _v[entry] = _v[indexVictim];
                        _type[entry] = _type[indexVictim];
                        _next[entry] = _next[indexVictim];
                        int victimHash = (int) HashHelper.longHash(_k[entry], _capacity * 2);
                        m = _hash[victimHash];
                        if (m == indexVictim) {
                            //the victim was the head of hashing list
                            _hash[victimHash] = entry;
                        } else {
                            //the victim is in the next, reChain it
                            while (m != -1) {
                                if (_next[m] == indexVictim) {
                                    _next[m] = entry;
                                    break;
                                }
                                m = _next[m];
                            }
                        }
                    }
                    _size--;
                } else {
                    _v[entry] = param_elem;
                    if (_type[entry] != p_type) {
                        _type[entry] = p_type;
                    }
                }
            }
            if (!initial) {
                declareDirty();
            }
            return;
        }
        if (_size < _capacity) {
            _k[_size] = p_key;
            _v[_size] = param_elem;
            _type[_size] = p_type;
            _next[_size] = _hash[hashIndex];
            _hash[hashIndex] = _size;
            _size++;
            if (!initial) {
                declareDirty();
            }
            return;
        }
        //extend capacity
        int newCapacity = _capacity * 2;
        long[] ex_k = new long[newCapacity];
        System.arraycopy(_k, 0, ex_k, 0, _capacity);
        _k = ex_k;
        Object[] ex_v = new Object[newCapacity];
        System.arraycopy(_v, 0, ex_v, 0, _capacity);
        _v = ex_v;
        byte[] ex_type = new byte[newCapacity];
        System.arraycopy(_type, 0, ex_type, 0, _capacity);
        _type = ex_type;
        _capacity = newCapacity;
        //insert the next
        _k[_size] = p_key;
        _v[_size] = param_elem;
        _type[_size] = p_type;
        _size++;
        //reHash
        _hash = new int[_capacity * 2];
        Arrays.fill(_hash, 0, _capacity * 2, -1);
        _next = new int[_capacity];
        Arrays.fill(_next, 0, _capacity, -1);
        final long hashCapacity = _capacity * 2;
        for (int i = 0; i < _size; i++) {
            int keyHash = (int) HashHelper.longHash(_k[i], hashCapacity);
            _next[i] = _hash[keyHash];
            _hash[keyHash] = i;
        }
        if (!initial) {
            declareDirty();
        }
    }

    @Override
    public ENode set(String name, byte type, Object value) {
        internal_set(_graph.resolver().stringToHash(name, true), type, value, true, false);
        return this;
    }

    @Override
    public ENode setAt(long key, byte type, Object value) {
        internal_set(key, type, value, true, false);
        return this;
    }

    @Override
    public Object get(String name) {
        return internal_get(_graph.resolver().stringToHash(name, false));
    }

    @Override
    public Object getAt(long key) {
        return internal_get(key);
    }

    @Override
    public void drop() {
        egraph.drop(this);
    }

    @Override
    public EGraph graph() {
        return egraph;
    }

    @Override
    public Object getOrCreate(String key, byte type) {
        Object previous = get(key);
        if (previous != null) {
            return previous;
        } else {
            return getOrCreateAt(_graph.resolver().stringToHash(key, true), type);
        }
    }

    @Override
    public final Object getOrCreateAt(final long key, final byte type) {
        final int found = internal_find(key);
        if (found != -1) {
            if (_type[found] == type) {
                return _v[found];
            }
        }
        Object toSet = null;
        switch (type) {
            case Type.ERELATION:
                toSet = new HeapERelation(chunk, null);
                break;
            case Type.RELATION:
                toSet = new HeapRelation(chunk, null);
                break;
            case Type.RELATION_INDEXED:
                toSet = new HeapRelationIndexed(chunk);
                break;
            case Type.DMATRIX:
                toSet = new HeapDMatrix(chunk, null);
                break;
            case Type.LMATRIX:
                toSet = new HeapLMatrix(chunk, null);
                break;
            case Type.STRING_TO_LONG_MAP:
                toSet = new HeapStringLongMap(chunk);
                break;
            case Type.LONG_TO_LONG_MAP:
                toSet = new HeapLongLongMap(chunk);
                break;
            case Type.LONG_TO_LONG_ARRAY_MAP:
                toSet = new HeapLongLongArrayMap(chunk);
                break;
        }
        internal_set(key, type, toSet, true, false);
        return toSet;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        final boolean[] isFirst = {true};
        boolean isFirstField = true;
        builder.append("{");
        for (int i = 0; i < _size; i++) {
            final Object elem = _v[i];
            final Resolver resolver = _graph.resolver();
            final long attributeKey = _k[i];
            final byte elemType = _type[i];
            if (elem != null) {
                if (isFirstField) {
                    isFirstField = false;
                } else {
                    builder.append(",");
                }
                String resolveName = resolver.hashToString(attributeKey);
                if (resolveName == null) {
                    resolveName = attributeKey + "";
                }
                switch (elemType) {
                    case Type.BOOL: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        if ((Boolean) elem) {
                            builder.append("1");
                        } else {
                            builder.append("0");
                        }
                        break;
                    }
                    case Type.STRING: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("\"");
                        builder.append(elem);
                        builder.append("\"");
                        break;
                    }
                    case Type.LONG: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append(elem);
                        break;
                    }
                    case Type.INT: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append(elem);
                        break;
                    }
                    case Type.DOUBLE: {
                        if (!BaseNode.isNaN((double) elem)) {
                            builder.append("\"");
                            builder.append(resolveName);
                            builder.append("\":");
                            builder.append(elem);
                        }
                        break;
                    }
                    case Type.DOUBLE_ARRAY: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("[");
                        double[] castedArr = (double[]) elem;
                        for (int j = 0; j < castedArr.length; j++) {
                            if (j != 0) {
                                builder.append(",");
                            }
                            builder.append(castedArr[j]);
                        }
                        builder.append("]");
                        break;
                    }
                    case Type.RELATION:
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("[");
                        Relation castedRelArr = (Relation) elem;
                        for (int j = 0; j < castedRelArr.size(); j++) {
                            if (j != 0) {
                                builder.append(",");
                            }
                            builder.append(castedRelArr.get(j));
                        }
                        builder.append("]");
                        break;
                    case Type.ERELATION:
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("[");
                        ERelation castedERelArr = (ERelation) elem;
                        for (int j = 0; j < castedERelArr.size(); j++) {
                            if (j != 0) {
                                builder.append(",");
                            }
                            builder.append(((HeapENode) castedERelArr.node(j))._id);
                        }
                        builder.append("]");
                        break;
                    case Type.LONG_ARRAY: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("[");
                        long[] castedArr2 = (long[]) elem;
                        for (int j = 0; j < castedArr2.length; j++) {
                            if (j != 0) {
                                builder.append(",");
                            }
                            builder.append(castedArr2[j]);
                        }
                        builder.append("]");
                        break;
                    }
                    case Type.INT_ARRAY: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("[");
                        int[] castedArr3 = (int[]) elem;
                        for (int j = 0; j < castedArr3.length; j++) {
                            if (j != 0) {
                                builder.append(",");
                            }
                            builder.append(castedArr3[j]);
                        }
                        builder.append("]");
                        break;
                    }
                    case Type.LONG_TO_LONG_MAP: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("{");
                        LongLongMap castedMapL2L = (LongLongMap) elem;
                        isFirst[0] = true;
                        castedMapL2L.each(new LongLongMapCallBack() {
                            @Override
                            public void on(long key, long value) {
                                if (!isFirst[0]) {
                                    builder.append(",");
                                } else {
                                    isFirst[0] = false;
                                }
                                builder.append("\"");
                                builder.append(key);
                                builder.append("\":");
                                builder.append(value);
                            }
                        });
                        builder.append("}");
                        break;
                    }
                    case Type.RELATION_INDEXED:
                    case Type.LONG_TO_LONG_ARRAY_MAP: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("{");
                        LongLongArrayMap castedMapL2LA = (LongLongArrayMap) elem;
                        isFirst[0] = true;
                        Set<Long> keys = new HashSet<Long>();
                        castedMapL2LA.each(new LongLongArrayMapCallBack() {
                            @Override
                            public void on(long key, long value) {
                                keys.add(key);
                            }
                        });
                        final Long[] flatKeys = keys.toArray(new Long[keys.size()]);
                        for (int k = 0; k < flatKeys.length; k++) {
                            long[] values = castedMapL2LA.get(flatKeys[k]);
                            if (!isFirst[0]) {
                                builder.append(",");
                            } else {
                                isFirst[0] = false;
                            }
                            builder.append("\"");
                            builder.append(flatKeys[k]);
                            builder.append("\":[");
                            for (int j = 0; j < values.length; j++) {
                                if (j != 0) {
                                    builder.append(",");
                                }
                                builder.append(values[j]);
                            }
                            builder.append("]");
                        }
                        builder.append("}");
                        break;
                    }
                    case Type.STRING_TO_LONG_MAP: {
                        builder.append("\"");
                        builder.append(resolveName);
                        builder.append("\":");
                        builder.append("{");
                        StringLongMap castedMapS2L = (StringLongMap) elem;
                        isFirst[0] = true;
                        castedMapS2L.each(new StringLongMapCallBack() {
                            @Override
                            public void on(String key, long value) {
                                if (!isFirst[0]) {
                                    builder.append(",");
                                } else {
                                    isFirst[0] = false;
                                }
                                builder.append("\"");
                                builder.append(key);
                                builder.append("\":");
                                builder.append(value);
                            }
                        });
                        builder.append("}");
                        break;
                    }

                }
            }
        }
        builder.append("}");
        return builder.toString();
    }

    @SuppressWarnings("Duplicates")
    final void save(final Buffer buffer) {
        Base64.encodeIntToBuffer(_size, buffer);
        for (int i = 0; i < _size; i++) {
            if (_v[i] != null) { //there is a real value
                final Object loopValue = _v[i];
                if (loopValue != null) {
                    buffer.write(CoreConstants.CHUNK_SEP);
                    Base64.encodeIntToBuffer(_type[i], buffer);
                    buffer.write(CoreConstants.CHUNK_SEP);
                    Base64.encodeLongToBuffer(_k[i], buffer);
                    buffer.write(CoreConstants.CHUNK_SEP);
                    switch (_type[i]) {
                        //additional types for embedded
                        case Type.ENODE:
                            Base64.encodeIntToBuffer(((HeapENode) loopValue)._id, buffer);
                            break;
                        case Type.ERELATION:
                            HeapERelation castedLongArrERel = (HeapERelation) loopValue;
                            Base64.encodeIntToBuffer(castedLongArrERel.size(), buffer);
                            for (int j = 0; j < castedLongArrERel.size(); j++) {
                                buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                Base64.encodeIntToBuffer(((HeapENode) castedLongArrERel.node(j))._id, buffer);
                            }
                            break;
                        //common types
                        case Type.STRING:
                            Base64.encodeStringToBuffer((String) loopValue, buffer);
                            break;
                        case Type.BOOL:
                            if ((Boolean) _v[i]) {
                                Base64.encodeIntToBuffer(CoreConstants.BOOL_TRUE, buffer);
                            } else {
                                Base64.encodeIntToBuffer(CoreConstants.BOOL_FALSE, buffer);
                            }
                            break;
                        case Type.LONG:
                            Base64.encodeLongToBuffer((Long) loopValue, buffer);
                            break;
                        case Type.DOUBLE:
                            Base64.encodeDoubleToBuffer((Double) loopValue, buffer);
                            break;
                        case Type.INT:
                            Base64.encodeIntToBuffer((Integer) loopValue, buffer);
                            break;
                        case Type.DOUBLE_ARRAY:
                            double[] castedDoubleArr = (double[]) loopValue;
                            Base64.encodeIntToBuffer(castedDoubleArr.length, buffer);
                            for (int j = 0; j < castedDoubleArr.length; j++) {
                                buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                Base64.encodeDoubleToBuffer(castedDoubleArr[j], buffer);
                            }
                            break;
                        case Type.EXTERNAL:
                            BaseExternalAttribute externalAttribute = (BaseExternalAttribute) loopValue;
                            final long encodedName = _graph.resolver().stringToHash(externalAttribute.name(), false);
                            Base64.encodeLongToBuffer(encodedName, buffer);
                            buffer.write(CoreConstants.CHUNK_VAL_SEP);
                            String saved = externalAttribute.save();
                            if (saved != null) {
                                Base64.encodeStringToBuffer(saved, buffer);
                            }
                            break;
                        case Type.RELATION:
                            HeapRelation castedLongArrRel = (HeapRelation) loopValue;
                            Base64.encodeIntToBuffer(castedLongArrRel.size(), buffer);
                            for (int j = 0; j < castedLongArrRel.size(); j++) {
                                buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                Base64.encodeLongToBuffer(castedLongArrRel.unsafe_get(j), buffer);
                            }
                            break;
                        case Type.LONG_ARRAY:
                            long[] castedLongArr = (long[]) loopValue;
                            Base64.encodeIntToBuffer(castedLongArr.length, buffer);
                            for (int j = 0; j < castedLongArr.length; j++) {
                                buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                Base64.encodeLongToBuffer(castedLongArr[j], buffer);
                            }
                            break;
                        case Type.INT_ARRAY:
                            int[] castedIntArr = (int[]) loopValue;
                            Base64.encodeIntToBuffer(castedIntArr.length, buffer);
                            for (int j = 0; j < castedIntArr.length; j++) {
                                buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                Base64.encodeIntToBuffer(castedIntArr[j], buffer);
                            }
                            break;
                        case Type.DMATRIX:
                            HeapDMatrix castedMatrix = (HeapDMatrix) loopValue;
                            final double[] unsafeContent = castedMatrix.unsafe_data();
                            if (unsafeContent != null) {
                                Base64.encodeIntToBuffer(unsafeContent.length, buffer);
                                for (int j = 0; j < unsafeContent.length; j++) {
                                    buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                    Base64.encodeDoubleToBuffer(unsafeContent[j], buffer);
                                }
                            }
                            break;
                        case Type.LMATRIX:
                            HeapLMatrix castedLMatrix = (HeapLMatrix) loopValue;
                            final long[] unsafeLContent = castedLMatrix.unsafe_data();
                            if (unsafeLContent != null) {
                                Base64.encodeIntToBuffer(unsafeLContent.length, buffer);
                                for (int j = 0; j < unsafeLContent.length; j++) {
                                    buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                    Base64.encodeLongToBuffer(unsafeLContent[j], buffer);
                                }
                            }
                            break;
                        case Type.STRING_TO_LONG_MAP:
                            HeapStringLongMap castedStringLongMap = (HeapStringLongMap) loopValue;
                            Base64.encodeLongToBuffer(castedStringLongMap.size(), buffer);
                            castedStringLongMap.unsafe_each(new StringLongMapCallBack() {
                                @Override
                                public void on(final String key, final long value) {
                                    buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                    Base64.encodeStringToBuffer(key, buffer);
                                    buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                    Base64.encodeLongToBuffer(value, buffer);
                                }
                            });
                            break;
                        case Type.LONG_TO_LONG_MAP:
                            HeapLongLongMap castedLongLongMap = (HeapLongLongMap) loopValue;
                            Base64.encodeLongToBuffer(castedLongLongMap.size(), buffer);
                            castedLongLongMap.unsafe_each(new LongLongMapCallBack() {
                                @Override
                                public void on(final long key, final long value) {
                                    buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                    Base64.encodeLongToBuffer(key, buffer);
                                    buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                    Base64.encodeLongToBuffer(value, buffer);
                                }
                            });
                            break;
                        case Type.RELATION_INDEXED:
                        case Type.LONG_TO_LONG_ARRAY_MAP:
                            HeapLongLongArrayMap castedLongLongArrayMap = (HeapLongLongArrayMap) loopValue;
                            Base64.encodeLongToBuffer(castedLongLongArrayMap.size(), buffer);
                            castedLongLongArrayMap.unsafe_each(new LongLongArrayMapCallBack() {
                                @Override
                                public void on(final long key, final long value) {
                                    buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                    Base64.encodeLongToBuffer(key, buffer);
                                    buffer.write(CoreConstants.CHUNK_VAL_SEP);
                                    Base64.encodeLongToBuffer(value, buffer);
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        _dirty = false;
    }

    private static final byte LOAD_WAITING_ALLOC = 0;
    private static final byte LOAD_WAITING_TYPE = 1;
    private static final byte LOAD_WAITING_KEY = 2;
    private static final byte LOAD_WAITING_VALUE = 3;

    @SuppressWarnings("Duplicates")
    public final long load(final Buffer buffer, final long currentCursor, final HeapStateChunk p_parent) {
        final boolean initial = _k == null;
        final long payloadSize = buffer.length();
        long cursor = currentCursor;
        long previous = cursor;
        byte state = LOAD_WAITING_ALLOC;
        byte read_type = -1;
        long read_key = -1;
        while (cursor < payloadSize) {
            byte current = buffer.read(cursor);
            if (current == Constants.CHUNK_ENODE_SEP) {
                break;
            } else if (current == Constants.CHUNK_SEP) {
                switch (state) {
                    case LOAD_WAITING_ALLOC:
                        allocate((int) Base64.decodeToLongWithBounds(buffer, previous, cursor));
                        state = LOAD_WAITING_TYPE;
                        cursor++;
                        previous = cursor;
                        break;
                    case LOAD_WAITING_TYPE:
                        read_type = (byte) Base64.decodeToIntWithBounds(buffer, previous, cursor);
                        state = LOAD_WAITING_KEY;
                        cursor++;
                        previous = cursor;
                        break;
                    case LOAD_WAITING_KEY:
                        read_key = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                        //primitive default loader
                        switch (read_type) {
                            //primitive types
                            case Type.BOOL:
                            case Type.INT:
                            case Type.DOUBLE:
                            case Type.LONG:
                            case Type.STRING:
                            case Type.ENODE:
                                state = LOAD_WAITING_VALUE;
                                cursor++;
                                previous = cursor;
                                break;
                            //arrays
                            case Type.DOUBLE_ARRAY:
                                double[] doubleArrayLoaded = null;
                                int doubleArrayIndex = 0;
                                cursor++;
                                previous = cursor;
                                current = buffer.read(cursor);
                                while (cursor < payloadSize && current != Constants.CHUNK_SEP && current != Constants.CHUNK_ENODE_SEP) {
                                    if (current == Constants.CHUNK_VAL_SEP) {
                                        if (doubleArrayLoaded == null) {
                                            doubleArrayLoaded = new double[(int) Base64.decodeToLongWithBounds(buffer, previous, cursor)];
                                        } else {
                                            doubleArrayLoaded[doubleArrayIndex] = Base64.decodeToDoubleWithBounds(buffer, previous, cursor);
                                            doubleArrayIndex++;
                                        }
                                        previous = cursor + 1;
                                    }
                                    cursor++;
                                    current = buffer.read(cursor);
                                }
                                if (doubleArrayLoaded == null) {
                                    doubleArrayLoaded = new double[(int) Base64.decodeToLongWithBounds(buffer, previous, cursor)];
                                } else {
                                    doubleArrayLoaded[doubleArrayIndex] = Base64.decodeToDoubleWithBounds(buffer, previous, cursor);
                                }
                                internal_set(read_key, read_type, doubleArrayLoaded, true, initial);
                                if (current != Constants.CHUNK_ENODE_SEP) {
                                    state = LOAD_WAITING_TYPE;
                                    cursor++;
                                    previous = cursor;
                                }
                                break;
                            case Type.LONG_ARRAY:
                                long[] longArrayLoaded = null;
                                int longArrayIndex = 0;
                                cursor++;
                                previous = cursor;
                                current = buffer.read(cursor);
                                while (cursor < payloadSize && current != Constants.CHUNK_SEP && current != Constants.CHUNK_ENODE_SEP) {
                                    if (current == Constants.CHUNK_VAL_SEP) {
                                        if (longArrayLoaded == null) {
                                            longArrayLoaded = new long[(int) Base64.decodeToLongWithBounds(buffer, previous, cursor)];
                                        } else {
                                            longArrayLoaded[longArrayIndex] = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                                            longArrayIndex++;
                                        }
                                        previous = cursor + 1;
                                    }
                                    cursor++;
                                    current = buffer.read(cursor);
                                }
                                if (longArrayLoaded == null) {
                                    longArrayLoaded = new long[(int) Base64.decodeToLongWithBounds(buffer, previous, cursor)];
                                } else {
                                    longArrayLoaded[longArrayIndex] = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                                }
                                internal_set(read_key, read_type, longArrayLoaded, true, initial);
                                if (current != Constants.CHUNK_ENODE_SEP) {
                                    state = LOAD_WAITING_TYPE;
                                    cursor++;
                                    previous = cursor;
                                }
                                break;
                            case Type.INT_ARRAY:
                                int[] intArrayLoaded = null;
                                int intArrayIndex = 0;
                                cursor++;
                                previous = cursor;
                                current = buffer.read(cursor);
                                while (cursor < payloadSize && current != Constants.CHUNK_SEP && current != Constants.CHUNK_ENODE_SEP) {
                                    if (current == Constants.CHUNK_VAL_SEP) {
                                        if (intArrayLoaded == null) {
                                            intArrayLoaded = new int[(int) Base64.decodeToLongWithBounds(buffer, previous, cursor)];
                                        } else {
                                            intArrayLoaded[intArrayIndex] = Base64.decodeToIntWithBounds(buffer, previous, cursor);
                                            intArrayIndex++;
                                        }
                                        previous = cursor + 1;
                                    }
                                    cursor++;
                                    current = buffer.read(cursor);
                                }
                                if (intArrayLoaded == null) {
                                    intArrayLoaded = new int[(int) Base64.decodeToLongWithBounds(buffer, previous, cursor)];
                                } else {
                                    intArrayLoaded[intArrayIndex] = Base64.decodeToIntWithBounds(buffer, previous, cursor);
                                }
                                internal_set(read_key, read_type, intArrayLoaded, true, initial);
                                if (current != Constants.CHUNK_ENODE_SEP) {
                                    state = LOAD_WAITING_TYPE;
                                    cursor++;
                                    previous = cursor;
                                }
                                break;
                            case Type.RELATION:
                                HeapRelation relation = new HeapRelation(p_parent, null);
                                cursor++;
                                cursor = relation.load(buffer, cursor, payloadSize);
                                cursor++;
                                internal_set(read_key, read_type, relation, true, initial);
                                previous = cursor;
                                state = LOAD_WAITING_TYPE;
                                break;
                            case Type.DMATRIX:
                                HeapDMatrix matrix = new HeapDMatrix(p_parent, null);
                                cursor++;
                                cursor = matrix.load(buffer, cursor, payloadSize);
                                cursor++;
                                internal_set(read_key, read_type, matrix, true, initial);
                                previous = cursor;
                                state = LOAD_WAITING_TYPE;
                                break;
                            case Type.LMATRIX:
                                HeapLMatrix lmatrix = new HeapLMatrix(p_parent, null);
                                cursor++;
                                cursor = lmatrix.load(buffer, cursor, payloadSize);
                                cursor++;
                                internal_set(read_key, read_type, lmatrix, true, initial);
                                previous = cursor;
                                state = LOAD_WAITING_TYPE;
                                break;
                            case Type.LONG_TO_LONG_MAP:
                                HeapLongLongMap l2lmap = new HeapLongLongMap(p_parent);
                                cursor++;
                                cursor = l2lmap.load(buffer, cursor, payloadSize);
                                cursor++;
                                internal_set(read_key, read_type, l2lmap, true, initial);
                                previous = cursor;
                                state = LOAD_WAITING_TYPE;
                                break;
                            case Type.LONG_TO_LONG_ARRAY_MAP:
                                HeapLongLongArrayMap l2lrmap = new HeapLongLongArrayMap(p_parent);
                                cursor++;
                                cursor = l2lrmap.load(buffer, cursor, payloadSize);
                                cursor++;
                                internal_set(read_key, read_type, l2lrmap, true, initial);
                                previous = cursor;
                                state = LOAD_WAITING_TYPE;
                                break;
                            case Type.RELATION_INDEXED:
                                HeapRelationIndexed relationIndexed = new HeapRelationIndexed(p_parent);
                                cursor++;
                                cursor = relationIndexed.load(buffer, cursor, payloadSize);
                                cursor++;
                                internal_set(read_key, read_type, relationIndexed, true, initial);
                                previous = cursor;
                                state = LOAD_WAITING_TYPE;
                                break;
                            case Type.STRING_TO_LONG_MAP:
                                HeapStringLongMap s2lmap = new HeapStringLongMap(p_parent);
                                cursor++;
                                cursor = s2lmap.load(buffer, cursor, payloadSize);
                                cursor++;
                                internal_set(read_key, read_type, s2lmap, true, initial);
                                previous = cursor;
                                state = LOAD_WAITING_TYPE;
                                break;
                            case Type.ERELATION:
                                HeapERelation eRelation = null;
                                cursor++;
                                previous = cursor;
                                current = buffer.read(cursor);
                                while (cursor < payloadSize && current != Constants.CHUNK_SEP && current != Constants.CHUNK_ENODE_SEP) {
                                    if (current == Constants.CHUNK_VAL_SEP) {
                                        if (eRelation == null) {
                                            eRelation = new HeapERelation(p_parent, null);
                                            eRelation.allocate(Base64.decodeToIntWithBounds(buffer, previous, cursor));
                                        } else {
                                            eRelation.add(egraph.nodeByIndex((int) Base64.decodeToLongWithBounds(buffer, previous, cursor), true));
                                        }
                                        previous = cursor + 1;
                                    }
                                    cursor++;
                                    current = buffer.read(cursor);
                                }
                                if (eRelation == null) {
                                    eRelation = new HeapERelation(p_parent, null);
                                    eRelation.allocate((int) Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                } else {
                                    eRelation.add(egraph.nodeByIndex(Base64.decodeToIntWithBounds(buffer, previous, cursor), true));
                                }
                                internal_set(read_key, read_type, eRelation, true, initial);
                                if (current != Constants.CHUNK_ENODE_SEP) {
                                    state = LOAD_WAITING_TYPE;
                                    cursor++;
                                    previous = cursor;
                                }
                                break;
                            default:
                                throw new RuntimeException("Not implemented yet!!!");
                        }
                        break;
                    case LOAD_WAITING_VALUE:
                        load_primitive(read_key, read_type, buffer, previous, cursor, initial);
                        state = LOAD_WAITING_TYPE;
                        cursor++;
                        previous = cursor;
                        break;
                }
            } else {
                cursor++;
            }
        }
        if (state == LOAD_WAITING_VALUE) {
            load_primitive(read_key, read_type, buffer, previous, cursor, initial);
        }
        return cursor;
    }

    private void load_primitive(final long read_key, final byte read_type, final Buffer buffer, final long previous, final long cursor, final boolean initial) {
        switch (read_type) {
            case Type.BOOL:
                internal_set(read_key, read_type, (((byte) Base64.decodeToIntWithBounds(buffer, previous, cursor)) == CoreConstants.BOOL_TRUE), true, initial);
                break;
            case Type.INT:
                internal_set(read_key, read_type, Base64.decodeToIntWithBounds(buffer, previous, cursor), true, initial);
                break;
            case Type.DOUBLE:
                internal_set(read_key, read_type, Base64.decodeToDoubleWithBounds(buffer, previous, cursor), true, initial);
                break;
            case Type.LONG:
                internal_set(read_key, read_type, Base64.decodeToLongWithBounds(buffer, previous, cursor), true, initial);
                break;
            case Type.STRING:
                internal_set(read_key, read_type, Base64.decodeToStringWithBounds(buffer, previous, cursor), true, initial);
                break;
            case Type.ENODE:
                internal_set(read_key, read_type, egraph.nodeByIndex(Base64.decodeToIntWithBounds(buffer, previous, cursor), true), true, initial);
                break;
        }
    }

}
