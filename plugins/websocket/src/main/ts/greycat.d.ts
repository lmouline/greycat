declare module java {
    module lang {
        class System {
            static gc(): void;
            static arraycopy(src: any[] | Float64Array | Int32Array, srcPos: number, dest: any[] | Float64Array | Int32Array, destPos: number, numElements: number): void;
        }
        class StringBuilder {
            private _buffer;
            length: number;
            append(val: any): StringBuilder;
            insert(position: number, val: any): StringBuilder;
            toString(): string;
        }
        class String {
            static valueOf(data: any, offset?: number, count?: number): string;
            static hashCode(str: string): number;
            static isEmpty(str: string): boolean;
            static join(delimiter: string, elements: string[]): string;
        }
        class Thread {
            static sleep(time: number): void;
        }
        class Double {
            static MAX_VALUE: number;
            static POSITIVE_INFINITY: number;
            static NEGATIVE_INFINITY: number;
            static NaN: number;
        }
        class Long {
            static parseLong(d: any): number;
        }
        class Integer {
            static parseInt(d: any): number;
        }
    }
    namespace util {
        namespace concurrent {
            namespace atomic {
                class AtomicIntegerArray {
                    _internal: Int32Array;
                    constructor(initialCapacity: number);
                    set(index: number, newVal: number): void;
                    get(index: number): number;
                    getAndSet(index: number, newVal: number): number;
                    compareAndSet(index: number, expect: number, update: number): boolean;
                }
                class AtomicLongArray {
                    _internal: Float64Array;
                    constructor(initialCapacity: number);
                    set(index: number, newVal: number): void;
                    get(index: number): number;
                    getAndSet(index: number, newVal: number): number;
                    compareAndSet(index: number, expect: number, update: number): boolean;
                    length(): number;
                }
                class AtomicReferenceArray<A> {
                    _internal: Array<A>;
                    constructor(initialCapacity: number);
                    set(index: number, newVal: A): void;
                    get(index: number): A;
                    getAndSet(index: number, newVal: A): A;
                    compareAndSet(index: number, expect: A, update: A): boolean;
                    length(): number;
                }
                class AtomicReference<A> {
                    _internal: A;
                    compareAndSet(expect: A, update: A): boolean;
                    get(): A;
                    set(newRef: A): void;
                    getAndSet(newVal: A): A;
                }
                class AtomicLong {
                    _internal: number;
                    constructor(init: number);
                    compareAndSet(expect: number, update: number): boolean;
                    get(): number;
                    incrementAndGet(): number;
                    decrementAndGet(): number;
                }
                class AtomicBoolean {
                    _internal: boolean;
                    constructor(init: boolean);
                    compareAndSet(expect: boolean, update: boolean): boolean;
                    get(): boolean;
                    set(newVal: boolean): void;
                }
                class AtomicInteger {
                    _internal: number;
                    constructor(init: number);
                    compareAndSet(expect: number, update: number): boolean;
                    get(): number;
                    set(newVal: number): void;
                    getAndSet(newVal: number): number;
                    incrementAndGet(): number;
                    decrementAndGet(): number;
                    getAndIncrement(): number;
                    getAndDecrement(): number;
                }
            }
            namespace locks {
                class ReentrantLock {
                    lock(): void;
                    unlock(): void;
                }
            }
        }
        class Random {
            private seed;
            nextInt(max?: number): number;
            nextDouble(): number;
            nextBoolean(): boolean;
            setSeed(seed: number): void;
            private nextSeeded(min?, max?);
            private haveNextNextGaussian;
            private nextNextGaussian;
            nextGaussian(): number;
        }
        interface Iterator<E> {
            hasNext(): boolean;
            next(): E;
        }
        class Arrays {
            static fill(data: any, begin: number, nbElem: number, param: number): void;
            static copyOf<T>(original: any[], newLength: number, ignore?: any): T[];
        }
        class Collections {
            static swap(list: List<any>, i: number, j: number): void;
        }
        interface Collection<E> {
            add(val: E): void;
            addAll(vals: Collection<E>): void;
            get(index: number): E;
            remove(o: any): any;
            clear(): void;
            isEmpty(): boolean;
            size(): number;
            contains(o: E): boolean;
            toArray<E>(a: Array<E>): E[];
            iterator(): Iterator<E>;
            containsAll(c: Collection<any>): boolean;
            addAll(c: Collection<any>): boolean;
            removeAll(c: Collection<any>): boolean;
        }
        interface List<E> extends Collection<E> {
            add(elem: E): void;
            add(index: number, elem: E): void;
            poll(): E;
            addAll(c: Collection<E>): boolean;
            addAll(index: number, c: Collection<E>): boolean;
            get(index: number): E;
            set(index: number, element: E): E;
            indexOf(o: E): number;
            lastIndexOf(o: E): number;
            remove(index: number): E;
        }
        interface Set<E> extends Collection<E> {
            forEach(f: (e: any) => void): void;
        }
        class Itr<E> implements Iterator<E> {
            cursor: number;
            lastRet: number;
            protected list: Collection<E>;
            constructor(list: Collection<E>);
            hasNext(): boolean;
            next(): E;
        }
        class HashSet<E> implements Set<E> {
            private content;
            add(val: E): void;
            clear(): void;
            contains(val: E): boolean;
            containsAll(elems: Collection<E>): boolean;
            addAll(vals: Collection<E>): boolean;
            remove(val: E): boolean;
            removeAll(): boolean;
            size(): number;
            isEmpty(): boolean;
            toArray<E>(a: Array<E>): E[];
            iterator(): Iterator<E>;
            forEach(f: (e: any) => void): void;
            get(index: number): E;
        }
        class AbstractList<E> implements List<E> {
            private content;
            addAll(index: any, vals?: any): boolean;
            clear(): void;
            poll(): E;
            remove(indexOrElem: any): any;
            removeAll(): boolean;
            toArray(a: Array<E>): E[];
            size(): number;
            add(index: any, elem?: E): void;
            get(index: number): E;
            contains(val: E): boolean;
            containsAll(elems: Collection<E>): boolean;
            isEmpty(): boolean;
            set(index: number, element: E): E;
            indexOf(element: E): number;
            lastIndexOf(element: E): number;
            iterator(): Iterator<E>;
        }
        class LinkedList<E> extends AbstractList<E> {
        }
        class ArrayList<E> extends AbstractList<E> {
        }
        class Stack<E> {
            content: any[];
            pop(): E;
            push(t: E): void;
            isEmpty(): boolean;
            peek(): E;
        }
        interface Map<K, V> {
            get(key: K): V;
            put(key: K, value: V): V;
            containsKey(key: K): boolean;
            remove(key: K): V;
            keySet(): Set<K>;
            isEmpty(): boolean;
            values(): Set<V>;
            clear(): void;
            size(): number;
        }
        class HashMap<K, V> implements Map<K, V> {
            private content;
            get(key: K): V;
            put(key: K, value: V): V;
            containsKey(key: K): boolean;
            remove(key: K): V;
            keySet(): Set<K>;
            isEmpty(): boolean;
            values(): Set<V>;
            clear(): void;
            size(): number;
        }
        class ConcurrentHashMap<K, V> extends HashMap<K, V> {
        }
    }
}
declare function arrayInstanceOf(arr: any, arg: Function): boolean;
declare class Long {
    private high;
    private low;
    private unsigned;
    private static INT_CACHE;
    private static UINT_CACHE;
    private static pow_dbl;
    private static TWO_PWR_16_DBL;
    private static TWO_PWR_24_DBL;
    private static TWO_PWR_32_DBL;
    private static TWO_PWR_64_DBL;
    private static TWO_PWR_63_DBL;
    private static TWO_PWR_24;
    static ZERO: Long;
    static UZERO: Long;
    static ONE: Long;
    static UONE: Long;
    static NEG_ONE: Long;
    static MAX_VALUE: Long;
    static MAX_UNSIGNED_VALUE: Long;
    static MIN_VALUE: Long;
    constructor(low?: number, high?: number, unsigned?: boolean);
    static isLong(obj: any): boolean;
    static fromInt(value: number, unsigned?: boolean): Long;
    static fromNumber(value: number, unsigned?: boolean): Long;
    static fromBits(lowBits?: number, highBits?: number, unsigned?: boolean): Long;
    static fromString(str: string, radix?: number, unsigned?: boolean): Long;
    static fromValue(val: any): Long;
    toInt(): number;
    toNumber(): number;
    toString(radix: number): string;
    getHighBits(): number;
    getHighBitsUnsigned(): number;
    getLowBits(): number;
    getLowBitsUnsigned(): number;
    getNumBitsAbs(): number;
    isZero(): boolean;
    isNegative(): boolean;
    isPositive(): boolean;
    isOdd(): boolean;
    isEven(): boolean;
    equals(other: any): boolean;
    eq: (other: any) => boolean;
    notEquals(other: any): boolean;
    neq: (other: any) => boolean;
    lessThan(other: any): boolean;
    lt: (other: any) => boolean;
    lessThanOrEqual(other: any): boolean;
    lte: (other: any) => boolean;
    greaterThan(other: any): boolean;
    gt: (other: any) => boolean;
    greaterThanOrEqual(other: any): boolean;
    gte: (other: any) => boolean;
    compare(other: any): number;
    comp: (other: any) => number;
    negate(): Long;
    neg: () => Long;
    add(addend: any): Long;
    subtract(subtrahend: any): Long;
    sub: (subtrahend: any) => Long;
    multiply(multiplier: any): Long;
    mul: (multiplier: any) => Long;
    divide(divisor: any): Long;
    div: (divisor: any) => Long;
    modulo(divisor: any): Long;
    mod: (divisor: any) => Long;
    not(): Long;
    and(other: any): Long;
    or(other: any): Long;
    xor(other: any): Long;
    shiftLeft(numBits: any): Long;
    shl: (numBits: any) => Long;
    shiftRight(numBits: any): Long;
    shr: (numBits: any) => Long;
    shiftRightUnsigned(numBits: any): Long;
    shru: (numBits: any) => Long;
    toSigned(): Long;
    toUnsigned(): Long;
}
declare module greycat {
    interface Callback<A> {
        (result: A): void;
    }
    class Constants {
        static KEY_SIZE: number;
        static LONG_SIZE: number;
        static PREFIX_SIZE: number;
        static BEGINNING_OF_TIME: number;
        static END_OF_TIME: number;
        static NULL_LONG: number;
        static KEY_PREFIX_MASK: number;
        static CACHE_MISS_ERROR: string;
        static TASK_PARAM_SEP: string;
        static TASK_SEP: string;
        static TASK_PARAM_OPEN: string;
        static TASK_PARAM_CLOSE: string;
        static SUB_TASK_OPEN: string;
        static SUB_TASK_CLOSE: string;
        static SUB_TASK_DECLR: string;
        static CHUNK_SEP: number;
        static CHUNK_ENODE_SEP: number;
        static CHUNK_ESEP: number;
        static CHUNK_VAL_SEP: number;
        static BUFFER_SEP: number;
        static KEY_SEP: number;
        static MAP_INITIAL_CAPACITY: number;
        static BOOL_TRUE: number;
        static BOOL_FALSE: number;
        static DEEP_WORLD: boolean;
        static WIDE_WORLD: boolean;
        static isDefined(param: any): boolean;
        static equals(src: string, other: string): boolean;
        static longArrayEquals(src: Float64Array, other: Float64Array): boolean;
    }
    interface DeferCounter {
        count(): void;
        getCount(): number;
        then(job: greycat.plugin.Job): void;
        wrap(): greycat.Callback<any>;
    }
    interface DeferCounterSync extends greycat.DeferCounter {
        waitResult(): any;
    }
    interface Graph {
        newNode(world: number, time: number): greycat.Node;
        newTypedNode(world: number, time: number, nodeType: string): greycat.Node;
        cloneNode(origin: greycat.Node): greycat.Node;
        lookup<A extends greycat.Node>(world: number, time: number, id: number, callback: greycat.Callback<A>): void;
        lookupBatch(worlds: Float64Array, times: Float64Array, ids: Float64Array, callback: greycat.Callback<greycat.Node[]>): void;
        lookupAll(world: number, time: number, ids: Float64Array, callback: greycat.Callback<greycat.Node[]>): void;
        lookupTimes(world: number, from: number, to: number, id: number, callback: greycat.Callback<greycat.Node[]>): void;
        lookupAllTimes(world: number, from: number, to: number, ids: Float64Array, callback: greycat.Callback<greycat.Node[]>): void;
        fork(world: number): number;
        save(callback: greycat.Callback<boolean>): void;
        connect(callback: greycat.Callback<boolean>): void;
        disconnect(callback: greycat.Callback<boolean>): void;
        index(world: number, time: number, name: string, callback: greycat.Callback<greycat.NodeIndex>): void;
        indexIfExists(world: number, time: number, name: string, callback: greycat.Callback<greycat.NodeIndex>): void;
        indexNames(world: number, time: number, callback: greycat.Callback<string[]>): void;
        newCounter(expectedEventsCount: number): greycat.DeferCounter;
        newSyncCounter(expectedEventsCount: number): greycat.DeferCounterSync;
        resolver(): greycat.plugin.Resolver;
        scheduler(): greycat.plugin.Scheduler;
        space(): greycat.chunk.ChunkSpace;
        storage(): greycat.plugin.Storage;
        newBuffer(): greycat.struct.Buffer;
        newQuery(): greycat.Query;
        freeNodes(nodes: greycat.Node[]): void;
        taskHooks(): greycat.task.TaskHook[];
        actionRegistry(): greycat.plugin.ActionRegistry;
        nodeRegistry(): greycat.plugin.NodeRegistry;
        setMemoryFactory(factory: greycat.plugin.MemoryFactory): greycat.Graph;
        addGlobalTaskHook(taskHook: greycat.task.TaskHook): greycat.Graph;
    }
    class GraphBuilder {
        private _storage;
        private _scheduler;
        private _plugins;
        private _memorySize;
        private _readOnly;
        private _deepPriority;
        static newBuilder(): greycat.GraphBuilder;
        withStorage(storage: greycat.plugin.Storage): greycat.GraphBuilder;
        withReadOnlyStorage(storage: greycat.plugin.Storage): greycat.GraphBuilder;
        withMemorySize(numberOfElements: number): greycat.GraphBuilder;
        withScheduler(scheduler: greycat.plugin.Scheduler): greycat.GraphBuilder;
        withPlugin(plugin: greycat.plugin.Plugin): greycat.GraphBuilder;
        withDeepWorld(): greycat.GraphBuilder;
        withWideWorld(): greycat.GraphBuilder;
        build(): greycat.Graph;
    }
    interface Node {
        world(): number;
        time(): number;
        id(): number;
        get(name: string): any;
        getAt(index: number): any;
        type(name: string): number;
        typeAt(index: number): number;
        nodeTypeName(): string;
        set(name: string, type: number, value: any): greycat.Node;
        setAt(index: number, type: number, value: any): greycat.Node;
        forceSet(name: string, type: number, value: any): greycat.Node;
        forceSetAt(index: number, type: number, value: any): greycat.Node;
        remove(name: string): greycat.Node;
        removeAt(index: number): greycat.Node;
        getOrCreate(name: string, type: number): any;
        getOrCreateAt(index: number, type: number): any;
        relation(relationName: string, callback: greycat.Callback<greycat.Node[]>): void;
        relationAt(relationIndex: number, callback: greycat.Callback<greycat.Node[]>): void;
        addToRelation(relationName: string, relatedNode: greycat.Node, ...indexedAttributes: string[]): greycat.Node;
        addToRelationAt(relationIndex: number, relatedNode: greycat.Node, ...indexedAttributes: string[]): greycat.Node;
        removeFromRelation(relationName: string, relatedNode: greycat.Node, ...indexedAttributes: string[]): greycat.Node;
        removeFromRelationAt(relationIndex: number, relatedNode: greycat.Node, ...indexedAttributes: string[]): greycat.Node;
        timeDephasing(): number;
        lastModification(): number;
        rephase(): greycat.Node;
        timepoints(beginningOfSearch: number, endOfSearch: number, callback: greycat.Callback<Float64Array>): void;
        free(): void;
        graph(): greycat.Graph;
        travelInTime<A extends greycat.Node>(targetTime: number, callback: greycat.Callback<A>): void;
        setTimeSensitivity(deltaTime: number, offset: number): greycat.Node;
        timeSensitivity(): Float64Array;
    }
    interface NodeIndex extends greycat.Node {
        size(): number;
        all(): Float64Array;
        addToIndex(node: greycat.Node, ...attributeNames: string[]): greycat.NodeIndex;
        removeFromIndex(node: greycat.Node, ...attributeNames: string[]): greycat.NodeIndex;
        clear(): greycat.NodeIndex;
        find(callback: greycat.Callback<greycat.Node[]>, ...params: string[]): void;
        findByQuery(query: greycat.Query, callback: greycat.Callback<greycat.Node[]>): void;
    }
    interface Query {
        world(): number;
        setWorld(world: number): greycat.Query;
        time(): number;
        setTime(time: number): greycat.Query;
        add(attributeName: string, value: string): greycat.Query;
        hash(): number;
        attributes(): Int32Array;
        values(): any[];
    }
    class Type {
        static BOOL: number;
        static STRING: number;
        static LONG: number;
        static INT: number;
        static DOUBLE: number;
        static DOUBLE_ARRAY: number;
        static LONG_ARRAY: number;
        static INT_ARRAY: number;
        static STRING_ARRAY: number;
        static LONG_TO_LONG_MAP: number;
        static LONG_TO_LONG_ARRAY_MAP: number;
        static STRING_TO_INT_MAP: number;
        static RELATION: number;
        static RELATION_INDEXED: number;
        static DMATRIX: number;
        static LMATRIX: number;
        static EGRAPH: number;
        static ENODE: number;
        static ERELATION: number;
        static TASK: number;
        static TASK_ARRAY: number;
        static typeName(p_type: number): string;
        static typeFromName(name: string): number;
    }
    module base {
        class BaseHook implements greycat.task.TaskHook {
            start(initialContext: greycat.task.TaskContext): void;
            beforeAction(action: greycat.task.Action, context: greycat.task.TaskContext): void;
            afterAction(action: greycat.task.Action, context: greycat.task.TaskContext): void;
            beforeTask(parentContext: greycat.task.TaskContext, context: greycat.task.TaskContext): void;
            afterTask(context: greycat.task.TaskContext): void;
            end(finalContext: greycat.task.TaskContext): void;
        }
        class BaseNode implements greycat.Node {
            private _world;
            private _time;
            private _id;
            private _graph;
            _resolver: greycat.plugin.Resolver;
            _index_worldOrder: number;
            _index_superTimeTree: number;
            _index_timeTree: number;
            _index_stateChunk: number;
            _world_magic: number;
            _super_time_magic: number;
            _time_magic: number;
            _dead: boolean;
            private _lock;
            constructor(p_world: number, p_time: number, p_id: number, p_graph: greycat.Graph);
            cacheLock(): void;
            cacheUnlock(): void;
            init(): void;
            nodeTypeName(): string;
            unphasedState(): greycat.plugin.NodeState;
            phasedState(): greycat.plugin.NodeState;
            newState(time: number): greycat.plugin.NodeState;
            graph(): greycat.Graph;
            world(): number;
            time(): number;
            id(): number;
            get(name: string): any;
            getAt(propIndex: number): any;
            forceSet(name: string, type: number, value: any): greycat.Node;
            forceSetAt(index: number, type: number, value: any): greycat.Node;
            setAt(index: number, type: number, value: any): greycat.Node;
            set(name: string, type: number, value: any): greycat.Node;
            private isEquals(obj1, obj2, type);
            getOrCreate(name: string, type: number): any;
            getOrCreateAt(index: number, type: number): any;
            type(name: string): number;
            typeAt(index: number): number;
            remove(name: string): greycat.Node;
            removeAt(index: number): greycat.Node;
            relation(relationName: string, callback: greycat.Callback<greycat.Node[]>): void;
            relationAt(relationIndex: number, callback: greycat.Callback<greycat.Node[]>): void;
            addToRelation(relationName: string, relatedNode: greycat.Node, ...attributes: string[]): greycat.Node;
            addToRelationAt(relationIndex: number, relatedNode: greycat.Node, ...attributes: string[]): greycat.Node;
            removeFromRelation(relationName: string, relatedNode: greycat.Node, ...attributes: string[]): greycat.Node;
            removeFromRelationAt(relationIndex: number, relatedNode: greycat.Node, ...attributes: string[]): greycat.Node;
            free(): void;
            timeDephasing(): number;
            lastModification(): number;
            rephase(): greycat.Node;
            timepoints(beginningOfSearch: number, endOfSearch: number, callback: greycat.Callback<Float64Array>): void;
            travelInTime<A extends greycat.Node>(targetTime: number, callback: greycat.Callback<A>): void;
            setTimeSensitivity(deltaTime: number, offset: number): greycat.Node;
            timeSensitivity(): Float64Array;
            static isNaN(toTest: number): boolean;
            toString(): string;
        }
        class BaseTaskResult<A> implements greycat.task.TaskResult<A> {
            private _backend;
            private _capacity;
            private _size;
            _exception: Error;
            _output: string;
            asArray(): any[];
            exception(): Error;
            output(): string;
            setException(e: Error): greycat.task.TaskResult<A>;
            setOutput(output: string): greycat.task.TaskResult<A>;
            fillWith(source: greycat.task.TaskResult<A>): greycat.task.TaskResult<A>;
            constructor(toWrap: any, protect: boolean);
            iterator(): greycat.task.TaskResultIterator<any>;
            get(index: number): A;
            set(index: number, input: A): greycat.task.TaskResult<A>;
            allocate(index: number): greycat.task.TaskResult<A>;
            add(input: A): greycat.task.TaskResult<A>;
            clear(): greycat.task.TaskResult<A>;
            clone(): greycat.task.TaskResult<A>;
            free(): void;
            size(): number;
            private extendTil(index);
            toString(): string;
            private toJson(withContent);
        }
        class BaseTaskResultIterator<A> implements greycat.task.TaskResultIterator<A> {
            private _backend;
            private _size;
            private _current;
            constructor(p_backend: any[]);
            next(): A;
            nextWithIndex(): greycat.utility.Tuple<number, A>;
        }
    }
    module chunk {
        interface Chunk {
            world(): number;
            time(): number;
            id(): number;
            chunkType(): number;
            index(): number;
            save(buffer: greycat.struct.Buffer): void;
            saveDiff(buffer: greycat.struct.Buffer): void;
            load(buffer: greycat.struct.Buffer): void;
            loadDiff(buffer: greycat.struct.Buffer): void;
        }
        interface ChunkSpace {
            createAndMark(type: number, world: number, time: number, id: number): greycat.chunk.Chunk;
            getAndMark(type: number, world: number, time: number, id: number): greycat.chunk.Chunk;
            getOrLoadAndMark(type: number, world: number, time: number, id: number, callback: greycat.Callback<greycat.chunk.Chunk>): void;
            getOrLoadAndMarkAll(keys: Float64Array, callback: greycat.Callback<greycat.chunk.Chunk[]>): void;
            get(index: number): greycat.chunk.Chunk;
            unmark(index: number): void;
            mark(index: number): number;
            free(chunk: greycat.chunk.Chunk): void;
            notifyUpdate(index: number): void;
            graph(): greycat.Graph;
            save(callback: greycat.Callback<boolean>): void;
            clear(): void;
            freeAll(): void;
            available(): number;
            newVolatileGraph(): greycat.struct.EGraph;
        }
        class ChunkType {
            static STATE_CHUNK: number;
            static TIME_TREE_CHUNK: number;
            static WORLD_ORDER_CHUNK: number;
            static GEN_CHUNK: number;
        }
        interface GenChunk extends greycat.chunk.Chunk {
            newKey(): number;
        }
        interface Stack {
            enqueue(index: number): boolean;
            dequeueTail(): number;
            dequeue(index: number): boolean;
            free(): void;
            size(): number;
        }
        interface StateChunk extends greycat.chunk.Chunk, greycat.plugin.NodeState {
            loadFrom(origin: greycat.chunk.StateChunk): void;
        }
        interface TimeTreeChunk extends greycat.chunk.Chunk {
            insert(key: number): void;
            unsafe_insert(key: number): void;
            previousOrEqual(key: number): number;
            clearAt(max: number): void;
            range(startKey: number, endKey: number, maxElements: number, walker: greycat.chunk.TreeWalker): void;
            magic(): number;
            previous(key: number): number;
            next(key: number): number;
            size(): number;
            extra(): number;
            setExtra(extraValue: number): void;
            extra2(): number;
            setExtra2(extraValue: number): void;
        }
        interface TreeWalker {
            (t: number): void;
        }
        interface WorldOrderChunk extends greycat.chunk.Chunk, greycat.struct.LongLongMap {
            magic(): number;
            lock(): void;
            unlock(): void;
            externalLock(): void;
            externalUnlock(): void;
            extra(): number;
            setExtra(extraValue: number): void;
        }
    }
    module internal {
        class BlackHoleStorage implements greycat.plugin.Storage {
            private _graph;
            private prefix;
            get(keys: greycat.struct.Buffer, callback: greycat.Callback<greycat.struct.Buffer>): void;
            put(stream: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void;
            remove(keys: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void;
            connect(graph: greycat.Graph, callback: greycat.Callback<boolean>): void;
            lock(callback: greycat.Callback<greycat.struct.Buffer>): void;
            unlock(previousLock: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void;
            disconnect(callback: greycat.Callback<boolean>): void;
        }
        class CoreConstants extends greycat.Constants {
            static PREFIX_TO_SAVE_SIZE: number;
            static NULL_KEY: Float64Array;
            static GLOBAL_UNIVERSE_KEY: Float64Array;
            static GLOBAL_DICTIONARY_KEY: Float64Array;
            static GLOBAL_INDEX_KEY: Float64Array;
            static INDEX_ATTRIBUTE: string;
            static DISCONNECTED_ERROR: string;
            static SCALE_1: number;
            static SCALE_2: number;
            static SCALE_3: number;
            static SCALE_4: number;
            static DEAD_NODE_ERROR: string;
            static fillBooleanArray(target: boolean[], elem: boolean): void;
        }
        class CoreGraph implements greycat.Graph {
            private _storage;
            private _space;
            private _scheduler;
            private _resolver;
            private _isConnected;
            private _lock;
            private _plugins;
            private _prefix;
            private _nodeKeyCalculator;
            private _worldKeyCalculator;
            private _actionRegistry;
            private _nodeRegistry;
            private _memoryFactory;
            private _taskHooks;
            constructor(p_storage: greycat.plugin.Storage, memorySize: number, p_scheduler: greycat.plugin.Scheduler, p_plugins: greycat.plugin.Plugin[], deepPriority: boolean);
            fork(world: number): number;
            newNode(world: number, time: number): greycat.Node;
            newTypedNode(world: number, time: number, nodeType: string): greycat.Node;
            cloneNode(origin: greycat.Node): greycat.Node;
            factoryByCode(code: number): greycat.plugin.NodeFactory;
            taskHooks(): greycat.task.TaskHook[];
            actionRegistry(): greycat.plugin.ActionRegistry;
            nodeRegistry(): greycat.plugin.NodeRegistry;
            setMemoryFactory(factory: greycat.plugin.MemoryFactory): greycat.Graph;
            addGlobalTaskHook(newTaskHook: greycat.task.TaskHook): greycat.Graph;
            lookup<A extends greycat.Node>(world: number, time: number, id: number, callback: greycat.Callback<A>): void;
            lookupBatch(worlds: Float64Array, times: Float64Array, ids: Float64Array, callback: greycat.Callback<greycat.Node[]>): void;
            lookupAll(world: number, time: number, ids: Float64Array, callback: greycat.Callback<greycat.Node[]>): void;
            lookupTimes(world: number, from: number, to: number, id: number, callback: greycat.Callback<greycat.Node[]>): void;
            lookupAllTimes(world: number, from: number, to: number, ids: Float64Array, callback: greycat.Callback<greycat.Node[]>): void;
            save(callback: greycat.Callback<boolean>): void;
            connect(callback: greycat.Callback<boolean>): void;
            disconnect(callback: greycat.Callback<any>): void;
            newBuffer(): greycat.struct.Buffer;
            newQuery(): greycat.Query;
            index(world: number, time: number, name: string, callback: greycat.Callback<greycat.NodeIndex>): void;
            indexIfExists(world: number, time: number, name: string, callback: greycat.Callback<greycat.NodeIndex>): void;
            private internal_index(world, time, name, ifExists, callback);
            indexNames(world: number, time: number, callback: greycat.Callback<string[]>): void;
            newCounter(expectedCountCalls: number): greycat.DeferCounter;
            newSyncCounter(expectedCountCalls: number): greycat.DeferCounterSync;
            resolver(): greycat.plugin.Resolver;
            scheduler(): greycat.plugin.Scheduler;
            space(): greycat.chunk.ChunkSpace;
            storage(): greycat.plugin.Storage;
            freeNodes(nodes: greycat.Node[]): void;
        }
        class CoreNodeDeclaration implements greycat.plugin.NodeDeclaration {
            private _name;
            private _factory;
            constructor(name: string);
            name(): string;
            factory(): greycat.plugin.NodeFactory;
            setFactory(newFactory: greycat.plugin.NodeFactory): void;
        }
        class CoreNodeIndex extends greycat.base.BaseNode implements greycat.NodeIndex {
            static NAME: string;
            constructor(p_world: number, p_time: number, p_id: number, p_graph: greycat.Graph);
            init(): void;
            size(): number;
            all(): Float64Array;
            addToIndex(node: greycat.Node, ...attributeNames: string[]): greycat.NodeIndex;
            removeFromIndex(node: greycat.Node, ...attributeNames: string[]): greycat.NodeIndex;
            clear(): greycat.NodeIndex;
            find(callback: greycat.Callback<greycat.Node[]>, ...query: string[]): void;
            findByQuery(query: greycat.Query, callback: greycat.Callback<greycat.Node[]>): void;
        }
        class CoreNodeRegistry implements greycat.plugin.NodeRegistry {
            private backend;
            private backend_hash;
            constructor();
            declaration(name: string): greycat.plugin.NodeDeclaration;
            declarationByHash(hash: number): greycat.plugin.NodeDeclaration;
        }
        class CoreQuery implements greycat.Query {
            private _resolver;
            private _graph;
            private capacity;
            private _attributes;
            private _values;
            private size;
            private _hash;
            private _world;
            private _time;
            constructor(graph: greycat.Graph, p_resolver: greycat.plugin.Resolver);
            world(): number;
            setWorld(p_world: number): greycat.Query;
            time(): number;
            setTime(p_time: number): greycat.Query;
            add(attributeName: string, value: string): greycat.Query;
            hash(): number;
            attributes(): Int32Array;
            values(): any[];
            private internal_add(att, val);
            private compute();
        }
        class MWGResolver implements greycat.plugin.Resolver {
            private _storage;
            private _space;
            private _graph;
            private dictionary;
            private globalWorldOrderChunk;
            private static KEY_SIZE;
            constructor(p_storage: greycat.plugin.Storage, p_space: greycat.chunk.ChunkSpace, p_graph: greycat.Graph);
            init(): void;
            typeCode(node: greycat.Node): number;
            initNode(node: greycat.Node, codeType: number): void;
            initWorld(parentWorld: number, childWorld: number): void;
            freeNode(node: greycat.Node): void;
            externalLock(node: greycat.Node): void;
            externalUnlock(node: greycat.Node): void;
            setTimeSensitivity(node: greycat.Node, deltaTime: number, offset: number): void;
            getTimeSensitivity(node: greycat.Node): Float64Array;
            lookup<A extends greycat.Node>(world: number, time: number, id: number, callback: greycat.Callback<A>): void;
            lookupBatch(worlds: Float64Array, times: Float64Array, ids: Float64Array, callback: greycat.Callback<greycat.Node[]>): void;
            lookupTimes(world: number, from: number, to: number, id: number, callback: greycat.Callback<greycat.Node[]>): void;
            private lookupAll_end(finalResult, callback, sizeIds, worldOrders, superTimes, times, chunks);
            lookupAll(world: number, time: number, ids: Float64Array, callback: greycat.Callback<greycat.Node[]>): void;
            lookupAllTimes(world: number, from: number, to: number, ids: Float64Array, callback: greycat.Callback<greycat.Node[]>): void;
            private resolve_world(globalWorldOrder, nodeWorldOrder, timeToResolve, originWorld);
            private getOrLoadAndMarkAll(types, keys, callback);
            resolveState(node: greycat.Node): greycat.plugin.NodeState;
            private internal_resolveState(node, safe);
            alignState(node: greycat.Node): greycat.plugin.NodeState;
            newState(node: greycat.Node, world: number, time: number): greycat.plugin.NodeState;
            resolveTimepoints(node: greycat.Node, beginningOfSearch: number, endOfSearch: number, callback: greycat.Callback<Float64Array>): void;
            private resolveTimepointsFromWorlds(objectWorldOrder, node, beginningOfSearch, endOfSearch, collectedWorlds, collectedWorldsSize, callback);
            private resolveTimepointsFromSuperTimes(objectWorldOrder, node, beginningOfSearch, endOfSearch, collectedWorlds, collectedSuperTimes, collectedSize, callback);
            stringToHash(name: string, insertIfNotExists: boolean): number;
            hashToString(key: number): string;
        }
        module chunk {
            module heap {
                class HeapAtomicByteArray {
                    private _back;
                    constructor(initialSize: number);
                    get(index: number): number;
                    set(index: number, value: number): void;
                }
                class HeapChunkSpace implements greycat.chunk.ChunkSpace {
                    private static HASH_LOAD_FACTOR;
                    private _maxEntries;
                    private _hashEntries;
                    private _lru;
                    private _dirtiesStack;
                    private _hashNext;
                    private _hash;
                    private _chunkWorlds;
                    private _chunkTimes;
                    private _chunkIds;
                    private _chunkTypes;
                    private _chunkValues;
                    private _chunkMarks;
                    private _graph;
                    private _deep_priority;
                    graph(): greycat.Graph;
                    worldByIndex(index: number): number;
                    timeByIndex(index: number): number;
                    idByIndex(index: number): number;
                    constructor(initialCapacity: number, p_graph: greycat.Graph, deepWorldPriority: boolean);
                    getAndMark(type: number, world: number, time: number, id: number): greycat.chunk.Chunk;
                    get(index: number): greycat.chunk.Chunk;
                    getOrLoadAndMark(type: number, world: number, time: number, id: number, callback: greycat.Callback<greycat.chunk.Chunk>): void;
                    getOrLoadAndMarkAll(keys: Float64Array, callback: greycat.Callback<greycat.chunk.Chunk[]>): void;
                    mark(index: number): number;
                    unmark(index: number): void;
                    free(chunk: greycat.chunk.Chunk): void;
                    createAndMark(type: number, world: number, time: number, id: number): greycat.chunk.Chunk;
                    notifyUpdate(index: number): void;
                    save(callback: greycat.Callback<boolean>): void;
                    clear(): void;
                    freeAll(): void;
                    available(): number;
                    newVolatileGraph(): greycat.struct.EGraph;
                    printMarked(): void;
                }
                interface HeapContainer {
                    declareDirty(): void;
                }
                class HeapDMatrix implements greycat.struct.DMatrix {
                    private static INDEX_ROWS;
                    private static INDEX_COLUMNS;
                    private static INDEX_MAX_COLUMN;
                    private static INDEX_OFFSET;
                    private parent;
                    private backend;
                    private aligned;
                    constructor(p_parent: greycat.internal.chunk.heap.HeapContainer, origin: greycat.internal.chunk.heap.HeapDMatrix);
                    init(rows: number, columns: number): greycat.struct.DMatrix;
                    private internal_init(rows, columns);
                    appendColumn(newColumn: Float64Array): greycat.struct.DMatrix;
                    private internal_appendColumn(newColumn);
                    fill(value: number): greycat.struct.DMatrix;
                    private internal_fill(value);
                    fillWith(values: Float64Array): greycat.struct.DMatrix;
                    fillWithRandom(random: java.util.Random, min: number, max: number): greycat.struct.DMatrix;
                    fillWithRandomStd(random: java.util.Random, std: number): greycat.struct.DMatrix;
                    private internal_fillWith(values);
                    rows(): number;
                    columns(): number;
                    column(index: number): Float64Array;
                    get(rowIndex: number, columnIndex: number): number;
                    set(rowIndex: number, columnIndex: number, value: number): greycat.struct.DMatrix;
                    private internal_set(rowIndex, columnIndex, value);
                    add(rowIndex: number, columnIndex: number, value: number): greycat.struct.DMatrix;
                    private internal_add(rowIndex, columnIndex, value);
                    data(): Float64Array;
                    leadingDimension(): number;
                    unsafeGet(index: number): number;
                    unsafeSet(index: number, value: number): greycat.struct.DMatrix;
                    private internal_unsafeSet(index, value);
                    unsafe_data(): Float64Array;
                    private unsafe_init(size);
                    private unsafe_set(index, value);
                    load(buffer: greycat.struct.Buffer, offset: number, max: number): number;
                }
                class HeapEGraph implements greycat.struct.EGraph {
                    private _graph;
                    private parent;
                    _dirty: boolean;
                    _nodes: greycat.internal.chunk.heap.HeapENode[];
                    private _nodes_capacity;
                    private _nodes_index;
                    constructor(p_parent: greycat.internal.chunk.heap.HeapContainer, origin: greycat.internal.chunk.heap.HeapEGraph, p_graph: greycat.Graph);
                    size(): number;
                    free(): void;
                    graph(): greycat.Graph;
                    private allocate(newCapacity);
                    nodeByIndex(index: number, createIfAbsent: boolean): greycat.internal.chunk.heap.HeapENode;
                    declareDirty(): void;
                    newNode(): greycat.struct.ENode;
                    root(): greycat.struct.ENode;
                    setRoot(eNode: greycat.struct.ENode): greycat.struct.EGraph;
                    drop(eNode: greycat.struct.ENode): greycat.struct.EGraph;
                    toString(): string;
                    load(buffer: greycat.struct.Buffer, offset: number, max: number): number;
                }
                class HeapENode implements greycat.struct.ENode, greycat.internal.chunk.heap.HeapContainer {
                    private egraph;
                    _id: number;
                    private _capacity;
                    private _size;
                    private _k;
                    private _v;
                    private _next_hash;
                    private _type;
                    private _dirty;
                    private static LOAD_WAITING_ALLOC;
                    private static LOAD_WAITING_TYPE;
                    private static LOAD_WAITING_KEY;
                    private static LOAD_WAITING_VALUE;
                    constructor(p_egraph: greycat.internal.chunk.heap.HeapEGraph, p_id: number, origin: greycat.internal.chunk.heap.HeapENode);
                    clear(): greycat.struct.ENode;
                    declareDirty(): void;
                    rebase(): void;
                    private allocate(newCapacity);
                    private internal_find(p_key);
                    private internal_get(p_key);
                    private internal_set(p_key, p_type, p_unsafe_elem, replaceIfPresent, initial);
                    set(name: string, type: number, value: any): greycat.struct.ENode;
                    setAt(key: number, type: number, value: any): greycat.struct.ENode;
                    get(name: string): any;
                    getAt(key: number): any;
                    drop(): void;
                    graph(): greycat.struct.EGraph;
                    getOrCreate(key: string, type: number): any;
                    getOrCreateAt(key: number, type: number): any;
                    toString(): string;
                    save(buffer: greycat.struct.Buffer): void;
                    load(buffer: greycat.struct.Buffer, currentCursor: number, graph: greycat.Graph): number;
                    private load_primitive(read_key, read_type, buffer, previous, cursor, initial);
                    each(callBack: greycat.plugin.NodeStateCallback): void;
                }
                class HeapERelation implements greycat.struct.ERelation {
                    private _back;
                    private _size;
                    private _capacity;
                    private parent;
                    constructor(p_parent: greycat.internal.chunk.heap.HeapContainer, origin: greycat.internal.chunk.heap.HeapERelation);
                    rebase(newGraph: greycat.internal.chunk.heap.HeapEGraph): void;
                    size(): number;
                    nodes(): greycat.struct.ENode[];
                    node(index: number): greycat.struct.ENode;
                    add(eNode: greycat.struct.ENode): greycat.struct.ERelation;
                    addAll(eNodes: greycat.struct.ENode[]): greycat.struct.ERelation;
                    clear(): greycat.struct.ERelation;
                    toString(): string;
                    allocate(newCapacity: number): void;
                }
                class HeapFixedStack implements greycat.chunk.Stack {
                    private _next;
                    private _prev;
                    private _capacity;
                    private _first;
                    private _last;
                    private _count;
                    constructor(capacity: number, fill: boolean);
                    enqueue(index: number): boolean;
                    dequeueTail(): number;
                    dequeue(index: number): boolean;
                    free(): void;
                    size(): number;
                }
                class HeapGenChunk implements greycat.chunk.GenChunk {
                    private _space;
                    private _index;
                    private _prefix;
                    private _seed;
                    private _dirty;
                    constructor(p_space: greycat.internal.chunk.heap.HeapChunkSpace, p_id: number, p_index: number);
                    save(buffer: greycat.struct.Buffer): void;
                    saveDiff(buffer: greycat.struct.Buffer): void;
                    load(buffer: greycat.struct.Buffer): void;
                    loadDiff(buffer: greycat.struct.Buffer): void;
                    private internal_load(buffer, diff);
                    newKey(): number;
                    index(): number;
                    world(): number;
                    time(): number;
                    id(): number;
                    chunkType(): number;
                }
                class HeapLMatrix implements greycat.struct.LMatrix {
                    private static INDEX_ROWS;
                    private static INDEX_COLUMNS;
                    private static INDEX_MAX_COLUMN;
                    private static INDEX_OFFSET;
                    private parent;
                    private backend;
                    private aligned;
                    constructor(p_parent: greycat.internal.chunk.heap.HeapContainer, origin: greycat.internal.chunk.heap.HeapLMatrix);
                    init(rows: number, columns: number): greycat.struct.LMatrix;
                    private internal_init(rows, columns);
                    appendColumn(newColumn: Float64Array): greycat.struct.LMatrix;
                    private internal_appendColumn(newColumn);
                    fill(value: number): greycat.struct.LMatrix;
                    private internal_fill(value);
                    fillWith(values: Float64Array): greycat.struct.LMatrix;
                    private internal_fillWith(values);
                    fillWithRandom(min: number, max: number, seed: number): greycat.struct.LMatrix;
                    private internal_fillWithRandom(min, max, seed);
                    rows(): number;
                    columns(): number;
                    column(index: number): Float64Array;
                    get(rowIndex: number, columnIndex: number): number;
                    set(rowIndex: number, columnIndex: number, value: number): greycat.struct.LMatrix;
                    private internal_set(rowIndex, columnIndex, value);
                    add(rowIndex: number, columnIndex: number, value: number): greycat.struct.LMatrix;
                    private internal_add(rowIndex, columnIndex, value);
                    data(): Float64Array;
                    leadingDimension(): number;
                    unsafeGet(index: number): number;
                    unsafeSet(index: number, value: number): greycat.struct.LMatrix;
                    private internal_unsafeSet(index, value);
                    unsafe_data(): Float64Array;
                    unsafe_init(size: number): void;
                    unsafe_set(index: number, value: number): void;
                    load(buffer: greycat.struct.Buffer, offset: number, max: number): number;
                }
                class HeapLongLongArrayMap implements greycat.struct.LongLongArrayMap {
                    parent: greycat.internal.chunk.heap.HeapContainer;
                    mapSize: number;
                    capacity: number;
                    keys: Float64Array;
                    values: Float64Array;
                    nexts: Int32Array;
                    hashs: Int32Array;
                    constructor(p_listener: greycat.internal.chunk.heap.HeapContainer);
                    private key(i);
                    private setKey(i, newValue);
                    private value(i);
                    private setValue(i, newValue);
                    private next(i);
                    private setNext(i, newValue);
                    private hash(i);
                    private setHash(i, newValue);
                    reallocate(newCapacity: number): void;
                    cloneFor(newParent: greycat.internal.chunk.heap.HeapContainer): greycat.internal.chunk.heap.HeapLongLongArrayMap;
                    get(requestKey: number): Float64Array;
                    contains(requestKey: number, requestValue: number): boolean;
                    each(callback: greycat.struct.LongLongArrayMapCallBack): void;
                    unsafe_each(callback: greycat.struct.LongLongArrayMapCallBack): void;
                    size(): number;
                    delete(requestKey: number, requestValue: number): void;
                    put(insertKey: number, insertValue: number): void;
                    load(buffer: greycat.struct.Buffer, offset: number, max: number): number;
                }
                class HeapLongLongMap implements greycat.struct.LongLongMap {
                    private parent;
                    private mapSize;
                    private capacity;
                    private keys;
                    private values;
                    private nexts;
                    private hashs;
                    constructor(p_listener: greycat.internal.chunk.heap.HeapContainer);
                    private key(i);
                    private setKey(i, newValue);
                    private value(i);
                    private setValue(i, newValue);
                    private next(i);
                    private setNext(i, newValue);
                    private hash(i);
                    private setHash(i, newValue);
                    reallocate(newCapacity: number): void;
                    cloneFor(newParent: greycat.internal.chunk.heap.HeapContainer): greycat.internal.chunk.heap.HeapLongLongMap;
                    get(requestKey: number): number;
                    each(callback: greycat.struct.LongLongMapCallBack): void;
                    unsafe_each(callback: greycat.struct.LongLongMapCallBack): void;
                    size(): number;
                    remove(requestKey: number): void;
                    put(insertKey: number, insertValue: number): void;
                    load(buffer: greycat.struct.Buffer, offset: number, max: number): number;
                }
                class HeapRelation implements greycat.struct.Relation {
                    private _back;
                    private _size;
                    private parent;
                    private aligned;
                    constructor(p_parent: greycat.internal.chunk.heap.HeapContainer, origin: greycat.internal.chunk.heap.HeapRelation);
                    allocate(_capacity: number): void;
                    all(): Float64Array;
                    size(): number;
                    get(index: number): number;
                    set(index: number, value: number): void;
                    unsafe_get(index: number): number;
                    addNode(node: greycat.Node): greycat.struct.Relation;
                    add(newValue: number): greycat.struct.Relation;
                    addAll(newValues: Float64Array): greycat.struct.Relation;
                    insert(targetIndex: number, newValue: number): greycat.struct.Relation;
                    remove(oldValue: number): greycat.struct.Relation;
                    delete(toRemoveIndex: number): greycat.struct.Relation;
                    clear(): greycat.struct.Relation;
                    toString(): string;
                    load(buffer: greycat.struct.Buffer, offset: number, max: number): number;
                }
                class HeapRelationIndexed extends greycat.internal.chunk.heap.HeapLongLongArrayMap implements greycat.struct.RelationIndexed {
                    private _graph;
                    constructor(p_listener: greycat.internal.chunk.heap.HeapContainer, graph: greycat.Graph);
                    add(node: greycat.Node, ...attributeNames: string[]): greycat.struct.RelationIndexed;
                    remove(node: greycat.Node, ...attributeNames: string[]): greycat.struct.RelationIndexed;
                    private internal_add_remove(isIndex, node, ...attributeNames);
                    clear(): greycat.struct.RelationIndexed;
                    find(callback: greycat.Callback<greycat.Node[]>, world: number, time: number, ...params: string[]): void;
                    findByQuery(query: greycat.Query, callback: greycat.Callback<greycat.Node[]>): void;
                    all(): Float64Array;
                    cloneIRelFor(newParent: greycat.internal.chunk.heap.HeapContainer, graph: greycat.Graph): greycat.internal.chunk.heap.HeapRelationIndexed;
                }
                class HeapStateChunk implements greycat.chunk.StateChunk, greycat.internal.chunk.heap.HeapContainer {
                    private _index;
                    private _space;
                    private _capacity;
                    private _size;
                    private _k;
                    private _v;
                    private _type;
                    private next_and_hash;
                    private _dirty;
                    private static LOAD_WAITING_ALLOC;
                    private static LOAD_WAITING_TYPE;
                    private static LOAD_WAITING_KEY;
                    private static LOAD_WAITING_VALUE;
                    graph(): greycat.Graph;
                    constructor(p_space: greycat.internal.chunk.heap.HeapChunkSpace, p_index: number);
                    world(): number;
                    time(): number;
                    id(): number;
                    chunkType(): number;
                    index(): number;
                    get(p_key: number): any;
                    private internal_find(p_key);
                    private internal_get(p_key);
                    set(p_elementIndex: number, p_elemType: number, p_unsafe_elem: any): void;
                    setFromKey(key: string, p_elemType: number, p_unsafe_elem: any): void;
                    getFromKey(key: string): any;
                    getFromKeyWithDefault<A>(key: string, defaultValue: A): A;
                    getWithDefault<A>(key: number, defaultValue: A): A;
                    getType(p_key: number): number;
                    getTypeFromKey(key: string): number;
                    getOrCreate(p_key: number, p_type: number): any;
                    getOrCreateFromKey(key: string, elemType: number): any;
                    declareDirty(): void;
                    save(buffer: greycat.struct.Buffer): void;
                    saveDiff(buffer: greycat.struct.Buffer): void;
                    each(callBack: greycat.plugin.NodeStateCallback): void;
                    loadFrom(origin: greycat.chunk.StateChunk): void;
                    private internal_set(p_key, p_type, p_unsafe_elem, replaceIfPresent, initial);
                    private allocate(newCapacity);
                    load(buffer: greycat.struct.Buffer): void;
                    private load_primitive(read_key, read_type, buffer, previous, cursor, initial);
                    loadDiff(buffer: greycat.struct.Buffer): void;
                }
                class HeapStringIntMap implements greycat.struct.StringIntMap {
                    private parent;
                    private mapSize;
                    private capacity;
                    private keys;
                    private keysH;
                    private values;
                    private nexts;
                    private hashs;
                    constructor(p_parent: greycat.internal.chunk.heap.HeapContainer);
                    private key(i);
                    private setKey(i, newValue);
                    private keyH(i);
                    private setKeyH(i, newValue);
                    private value(i);
                    private setValue(i, newValue);
                    private next(i);
                    private setNext(i, newValue);
                    private hash(i);
                    private setHash(i, newValue);
                    reallocate(newCapacity: number): void;
                    cloneFor(newContainer: greycat.internal.chunk.heap.HeapContainer): greycat.internal.chunk.heap.HeapStringIntMap;
                    getValue(requestString: string): number;
                    getByHash(keyHash: number): string;
                    containsHash(keyHash: number): boolean;
                    each(callback: greycat.struct.StringLongMapCallBack): void;
                    unsafe_each(callback: greycat.struct.StringLongMapCallBack): void;
                    size(): number;
                    remove(requestKey: string): void;
                    put(insertKey: string, insertValue: number): void;
                    load(buffer: greycat.struct.Buffer, offset: number, max: number): number;
                }
                class HeapTimeTreeChunk implements greycat.chunk.TimeTreeChunk {
                    private static META_SIZE;
                    private _index;
                    private _space;
                    private _root;
                    private _back_meta;
                    private _k;
                    private _colors;
                    private _diff;
                    private _magic;
                    private _size;
                    private _dirty;
                    private _extra;
                    private _extra2;
                    constructor(p_space: greycat.internal.chunk.heap.HeapChunkSpace, p_index: number);
                    extra(): number;
                    setExtra(extraValue: number): void;
                    extra2(): number;
                    setExtra2(extraValue: number): void;
                    world(): number;
                    time(): number;
                    id(): number;
                    size(): number;
                    range(startKey: number, endKey: number, maxElements: number, walker: greycat.chunk.TreeWalker): void;
                    save(buffer: greycat.struct.Buffer): void;
                    saveDiff(buffer: greycat.struct.Buffer): void;
                    load(buffer: greycat.struct.Buffer): void;
                    loadDiff(buffer: greycat.struct.Buffer): void;
                    private internal_load(buffer, initial);
                    index(): number;
                    previous(key: number): number;
                    next(key: number): number;
                    previousOrEqual(key: number): number;
                    magic(): number;
                    insert(p_key: number): void;
                    unsafe_insert(p_key: number): void;
                    chunkType(): number;
                    clearAt(max: number): void;
                    private reallocate(newCapacity);
                    private key(p_currentIndex);
                    private setKey(p_currentIndex, p_paramIndex, initial);
                    private left(p_currentIndex);
                    private setLeft(p_currentIndex, p_paramIndex);
                    private right(p_currentIndex);
                    private setRight(p_currentIndex, p_paramIndex);
                    private parent(p_currentIndex);
                    private setParent(p_currentIndex, p_paramIndex);
                    private color(p_currentIndex);
                    private setColor(p_currentIndex, p_paramIndex);
                    private grandParent(p_currentIndex);
                    private sibling(p_currentIndex);
                    private uncle(p_currentIndex);
                    private internal_previous(p_index);
                    private internal_next(p_index);
                    private internal_previousOrEqual_index(p_key);
                    private internal_previous_index(p_key);
                    private rotateLeft(n);
                    private rotateRight(n);
                    private replaceNode(oldn, newn);
                    private insertCase1(n);
                    private insertCase2(n);
                    private insertCase3(n);
                    private insertCase4(n_n);
                    private insertCase5(n);
                    private internal_insert(p_key, initial);
                    private internal_set_dirty();
                }
                class HeapWorldOrderChunk implements greycat.chunk.WorldOrderChunk {
                    private _space;
                    private _index;
                    private _lock;
                    private _externalLock;
                    private _magic;
                    private _extra;
                    private _size;
                    private _capacity;
                    private _kv;
                    private _diff;
                    private _next;
                    private _hash;
                    private _dirty;
                    constructor(p_space: greycat.internal.chunk.heap.HeapChunkSpace, p_index: number);
                    world(): number;
                    time(): number;
                    id(): number;
                    extra(): number;
                    setExtra(extraValue: number): void;
                    lock(): void;
                    unlock(): void;
                    externalLock(): void;
                    externalUnlock(): void;
                    magic(): number;
                    each(callback: greycat.struct.LongLongMapCallBack): void;
                    get(key: number): number;
                    put(key: number, value: number): void;
                    private internal_put(key, value, notifyUpdate);
                    private resize(newCapacity);
                    load(buffer: greycat.struct.Buffer): void;
                    loadDiff(buffer: greycat.struct.Buffer): void;
                    private internal_load(initial, buffer);
                    index(): number;
                    remove(key: number): void;
                    size(): number;
                    chunkType(): number;
                    save(buffer: greycat.struct.Buffer): void;
                    saveDiff(buffer: greycat.struct.Buffer): void;
                }
            }
        }
        module memory {
            class HeapBuffer implements greycat.struct.Buffer {
                private buffer;
                private writeCursor;
                slice(initPos: number, endPos: number): Int8Array;
                write(b: number): void;
                private getNewSize(old, target);
                writeAll(bytes: Int8Array): void;
                read(position: number): number;
                data(): Int8Array;
                length(): number;
                free(): void;
                iterator(): greycat.struct.BufferIterator;
                removeLast(): void;
                toString(): string;
            }
            class HeapMemoryFactory implements greycat.plugin.MemoryFactory {
                newSpace(memorySize: number, graph: greycat.Graph, deepWorld: boolean): greycat.chunk.ChunkSpace;
                newBuffer(): greycat.struct.Buffer;
            }
        }
        module scheduler {
            class JobQueue {
                private first;
                private last;
                add(item: greycat.plugin.Job): void;
                poll(): greycat.plugin.Job;
            }
            module JobQueue {
                class JobQueueElem {
                    _ptr: greycat.plugin.Job;
                    _next: greycat.internal.scheduler.JobQueue.JobQueueElem;
                    constructor(ptr: greycat.plugin.Job, next: greycat.internal.scheduler.JobQueue.JobQueueElem);
                }
            }
            class NoopScheduler implements greycat.plugin.Scheduler {
                dispatch(affinity: number, job: greycat.plugin.Job): void;
                start(): void;
                stop(): void;
                workers(): number;
            }
            class TrampolineScheduler implements greycat.plugin.Scheduler {
                private queue;
                private wip;
                dispatch(affinity: number, job: greycat.plugin.Job): void;
                start(): void;
                stop(): void;
                workers(): number;
            }
        }
        module task {
            class ActionAddRemoveToGlobalIndex implements greycat.task.Action {
                private _name;
                private _attributes;
                private _timed;
                private _remove;
                constructor(remove: boolean, timed: boolean, name: string, ...attributes: string[]);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionAddRemoveVarToRelation implements greycat.task.Action {
                private _name;
                private _varFrom;
                private _attributes;
                private _isAdd;
                constructor(isAdd: boolean, name: string, varFrom: string, ...attributes: string[]);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionAddToVar implements greycat.task.Action {
                private _name;
                constructor(p_name: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionAttributes implements greycat.task.Action {
                private _filter;
                constructor(filterType: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionClearResult implements greycat.task.Action {
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionCreateNode implements greycat.task.Action {
                private _typeNode;
                constructor(typeNode: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionDeclareVar implements greycat.task.Action {
                private _name;
                private _isGlobal;
                constructor(isGlobal: boolean, p_name: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionDefineAsVar implements greycat.task.Action {
                private _name;
                private _global;
                constructor(p_name: string, p_global: boolean);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionExecuteExpression implements greycat.task.Action {
                private _engine;
                private _expression;
                constructor(mathExpression: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionFlat implements greycat.task.Action {
                constructor();
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionFlipVar implements greycat.task.Action {
                private _name;
                constructor(name: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionGlobalIndex implements greycat.task.Action {
                private _name;
                constructor(p_indexName: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionIndexNames implements greycat.task.Action {
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionInject implements greycat.task.Action {
                private _value;
                constructor(value: any);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionLog implements greycat.task.Action {
                private _value;
                constructor(p_value: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionLookup implements greycat.task.Action {
                private _id;
                constructor(p_id: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionLookupAll implements greycat.task.Action {
                private _ids;
                constructor(p_ids: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionNamed implements greycat.task.Action {
                private _name;
                private _params;
                constructor(name: string, ...params: string[]);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionPrint implements greycat.task.Action {
                private _name;
                private _withLineBreak;
                constructor(p_name: string, withLineBreak: boolean);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionReadGlobalIndex implements greycat.task.Action {
                private _name;
                private _params;
                constructor(p_indexName: string, ...p_query: string[]);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionReadVar implements greycat.task.Action {
                private _origin;
                private _name;
                private _index;
                constructor(p_name: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionRemove implements greycat.task.Action {
                private _name;
                constructor(name: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionSave implements greycat.task.Action {
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionScript implements greycat.task.Action {
                private _script;
                private _async;
                constructor(script: string, async: boolean);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionSelect implements greycat.task.Action {
                private _script;
                private _filter;
                constructor(script: string, filter: greycat.task.TaskFunctionSelect);
                eval(ctx: greycat.task.TaskContext): void;
                private callScript(node, context);
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionSelectObject implements greycat.task.Action {
                private _filter;
                constructor(filterFunction: greycat.task.TaskFunctionSelectObject);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionSetAsVar implements greycat.task.Action {
                private _name;
                constructor(p_name: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionSetAttribute implements greycat.task.Action {
                private _name;
                private _value;
                private _propertyType;
                private _force;
                constructor(name: string, propertyType: string, value: string, force: boolean);
                eval(ctx: greycat.task.TaskContext): void;
                private loadArray(valueAfterTemplate, type);
                private parseBoolean(booleanValue);
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionTimeSensitivity implements greycat.task.Action {
                private _delta;
                private _offset;
                constructor(delta: string, offset: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionTimepoints implements greycat.task.Action {
                private _from;
                private _to;
                constructor(from: string, to: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionTravelInTime implements greycat.task.Action {
                private _time;
                constructor(time: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionTravelInWorld implements greycat.task.Action {
                private _world;
                constructor(world: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionTraverseOrAttribute implements greycat.task.Action {
                private _name;
                private _params;
                private _isAttribute;
                private _isUnknown;
                constructor(isAttribute: boolean, isUnknown: boolean, p_name: string, ...p_params: string[]);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionWith implements greycat.task.Action {
                private _patternTemplate;
                private _name;
                constructor(name: string, stringPattern: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class ActionWithout implements greycat.task.Action {
                private _patternTemplate;
                private _name;
                constructor(name: string, stringPattern: string);
                eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            abstract class CF_Action implements greycat.task.Action {
                abstract children(): greycat.task.Task[];
                abstract cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                abstract eval(ctx: greycat.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
                toString(): string;
            }
            class CF_Atomic extends greycat.internal.task.CF_Action {
                private _variables;
                private _subTask;
                constructor(p_subTask: greycat.task.Task, ...variables: string[]);
                eval(ctx: greycat.task.TaskContext): void;
                children(): greycat.task.Task[];
                cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
            }
            class CF_DoWhile extends greycat.internal.task.CF_Action {
                private _cond;
                private _then;
                private _conditionalScript;
                constructor(p_then: greycat.task.Task, p_cond: greycat.task.ConditionalFunction, conditionalScript: string);
                eval(ctx: greycat.task.TaskContext): void;
                children(): greycat.task.Task[];
                cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
            }
            class CF_ForEach extends greycat.internal.task.CF_Action {
                private _subTask;
                constructor(p_subTask: greycat.task.Task);
                eval(ctx: greycat.task.TaskContext): void;
                children(): greycat.task.Task[];
                cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
            }
            class CF_ForEachPar extends greycat.internal.task.CF_Action {
                private _subTask;
                constructor(p_subTask: greycat.task.Task);
                eval(ctx: greycat.task.TaskContext): void;
                children(): greycat.task.Task[];
                cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
            }
            class CF_IfThen extends greycat.internal.task.CF_Action {
                private _condition;
                private _action;
                private _conditionalScript;
                constructor(cond: greycat.task.ConditionalFunction, action: greycat.task.Task, conditionalScript: string);
                eval(ctx: greycat.task.TaskContext): void;
                children(): greycat.task.Task[];
                cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
            }
            class CF_IfThenElse extends greycat.internal.task.CF_Action {
                private _condition;
                private _thenSub;
                private _elseSub;
                private _conditionalScript;
                constructor(cond: greycat.task.ConditionalFunction, p_thenSub: greycat.task.Task, p_elseSub: greycat.task.Task, conditionalScript: string);
                eval(ctx: greycat.task.TaskContext): void;
                children(): greycat.task.Task[];
                cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
            }
            class CF_Loop extends greycat.internal.task.CF_Action {
                private _lower;
                private _upper;
                private _subTask;
                constructor(p_lower: string, p_upper: string, p_subTask: greycat.task.Task);
                eval(ctx: greycat.task.TaskContext): void;
                children(): greycat.task.Task[];
                cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
            }
            class CF_LoopPar extends greycat.internal.task.CF_Action {
                private _subTask;
                private _lower;
                private _upper;
                constructor(p_lower: string, p_upper: string, p_subTask: greycat.task.Task);
                eval(ctx: greycat.task.TaskContext): void;
                children(): greycat.task.Task[];
                cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
            }
            class CF_Map extends greycat.internal.task.CF_Action {
                private _subTask;
                constructor(p_subTask: greycat.task.Task);
                eval(ctx: greycat.task.TaskContext): void;
                children(): greycat.task.Task[];
                cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
            }
            class CF_MapPar extends greycat.internal.task.CF_Action {
                private _subTask;
                constructor(p_subTask: greycat.task.Task);
                eval(ctx: greycat.task.TaskContext): void;
                children(): greycat.task.Task[];
                cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
            }
            class CF_Pipe extends greycat.internal.task.CF_Action {
                private _subTasks;
                constructor(...p_subTasks: greycat.task.Task[]);
                eval(ctx: greycat.task.TaskContext): void;
                children(): greycat.task.Task[];
                cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
            }
            class CF_PipePar extends greycat.internal.task.CF_Action {
                private _subTasks;
                constructor(...p_subTasks: greycat.task.Task[]);
                eval(ctx: greycat.task.TaskContext): void;
                children(): greycat.task.Task[];
                cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
            }
            class CF_PipeTo extends greycat.internal.task.CF_Action {
                private _subTask;
                private _targets;
                constructor(p_subTask: greycat.task.Task, ...p_targets: string[]);
                eval(ctx: greycat.task.TaskContext): void;
                children(): greycat.task.Task[];
                cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
            }
            class CF_ThenDo implements greycat.task.Action {
                private _wrapped;
                constructor(p_wrapped: greycat.task.ActionFunction);
                eval(ctx: greycat.task.TaskContext): void;
                toString(): string;
                serialize(builder: java.lang.StringBuilder): void;
            }
            class CF_WhileDo extends greycat.internal.task.CF_Action {
                private _cond;
                private _then;
                private _conditionalScript;
                constructor(p_cond: greycat.task.ConditionalFunction, p_then: greycat.task.Task, conditionalScript: string);
                eval(ctx: greycat.task.TaskContext): void;
                children(): greycat.task.Task[];
                cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
            }
            class CoreActionDeclaration implements greycat.plugin.ActionDeclaration {
                private _factory;
                private _params;
                private _description;
                private _name;
                constructor(name: string);
                factory(): greycat.plugin.ActionFactory;
                setFactory(factory: greycat.plugin.ActionFactory): greycat.plugin.ActionDeclaration;
                params(): Int8Array;
                setParams(...params: number[]): greycat.plugin.ActionDeclaration;
                description(): string;
                setDescription(description: string): greycat.plugin.ActionDeclaration;
                name(): string;
            }
            class CoreActionNames {
                static ADD_VAR_TO_RELATION: string;
                static REMOVE_VAR_TO_RELATION: string;
                static ADD_TO_GLOBAL_INDEX: string;
                static ADD_TO_GLOBAL_TIMED_INDEX: string;
                static ADD_TO_VAR: string;
                static ATTRIBUTES: string;
                static ATTRIBUTES_WITH_TYPE: string;
                static CLEAR_RESULT: string;
                static CREATE_NODE: string;
                static CREATE_TYPED_NODE: string;
                static DECLARE_GLOBAL_VAR: string;
                static DECLARE_VAR: string;
                static FLIP_VAR: string;
                static DEFINE_AS_GLOBAL_VAR: string;
                static DEFINE_AS_VAR: string;
                static EXECUTE_EXPRESSION: string;
                static INDEX_NAMES: string;
                static LOOKUP: string;
                static LOOKUP_ALL: string;
                static LOG: string;
                static PRINT: string;
                static PRINTLN: string;
                static READ_GLOBAL_INDEX: string;
                static GLOBAL_INDEX: string;
                static READ_VAR: string;
                static REMOVE: string;
                static REMOVE_FROM_GLOBAL_INDEX: string;
                static SAVE: string;
                static SCRIPT: string;
                static ASYNC_SCRIPT: string;
                static SELECT: string;
                static SET_AS_VAR: string;
                static FORCE_ATTRIBUTE: string;
                static SET_ATTRIBUTE: string;
                static TIME_SENSITIVITY: string;
                static TIMEPOINTS: string;
                static TRAVEL_IN_TIME: string;
                static TRAVEL_IN_WORLD: string;
                static WITH: string;
                static WITHOUT: string;
                static TRAVERSE: string;
                static ATTRIBUTE: string;
                static LOOP: string;
                static LOOP_PAR: string;
                static FOR_EACH: string;
                static FOR_EACH_PAR: string;
                static MAP: string;
                static MAP_PAR: string;
                static PIPE: string;
                static PIPE_PAR: string;
                static PIPE_TO: string;
                static DO_WHILE: string;
                static WHILE_DO: string;
                static IF_THEN: string;
                static IF_THEN_ELSE: string;
                static ATOMIC: string;
                static FLAT: string;
            }
            class CoreActionRegistry implements greycat.plugin.ActionRegistry {
                private backend;
                constructor();
                declaration(name: string): greycat.plugin.ActionDeclaration;
                declarations(): greycat.plugin.ActionDeclaration[];
            }
            class CoreActions {
                static flat(): greycat.task.Action;
                static travelInWorld(world: string): greycat.task.Action;
                static travelInTime(time: string): greycat.task.Action;
                static inject(input: any): greycat.task.Action;
                static defineAsGlobalVar(name: string): greycat.task.Action;
                static defineAsVar(name: string): greycat.task.Action;
                static declareGlobalVar(name: string): greycat.task.Action;
                static declareVar(name: string): greycat.task.Action;
                static readVar(name: string): greycat.task.Action;
                static flipVar(name: string): greycat.task.Action;
                static setAsVar(name: string): greycat.task.Action;
                static addToVar(name: string): greycat.task.Action;
                static setAttribute(name: string, type: number, value: string): greycat.task.Action;
                static timeSensitivity(delta: string, offset: string): greycat.task.Action;
                static forceAttribute(name: string, type: number, value: string): greycat.task.Action;
                static remove(name: string): greycat.task.Action;
                static attributes(): greycat.task.Action;
                static attributesWithTypes(filterType: number): greycat.task.Action;
                static addVarToRelation(relName: string, varName: string, ...attributes: string[]): greycat.task.Action;
                static removeVarFromRelation(relName: string, varFrom: string, ...attributes: string[]): greycat.task.Action;
                static traverse(name: string, ...params: string[]): greycat.task.Action;
                static attribute(name: string, ...params: string[]): greycat.task.Action;
                static readGlobalIndex(indexName: string, ...query: string[]): greycat.task.Action;
                static globalIndex(indexName: string): greycat.task.Action;
                static addToGlobalIndex(name: string, ...attributes: string[]): greycat.task.Action;
                static addToGlobalTimedIndex(name: string, ...attributes: string[]): greycat.task.Action;
                static removeFromGlobalIndex(name: string, ...attributes: string[]): greycat.task.Action;
                static removeFromGlobalTimedIndex(name: string, ...attributes: string[]): greycat.task.Action;
                static indexNames(): greycat.task.Action;
                static selectWith(name: string, pattern: string): greycat.task.Action;
                static selectWithout(name: string, pattern: string): greycat.task.Action;
                static select(filterFunction: greycat.task.TaskFunctionSelect): greycat.task.Action;
                static selectObject(filterFunction: greycat.task.TaskFunctionSelectObject): greycat.task.Action;
                static selectScript(script: string): greycat.task.Action;
                static log(value: string): greycat.task.Action;
                static print(name: string): greycat.task.Action;
                static println(name: string): greycat.task.Action;
                static executeExpression(expression: string): greycat.task.Action;
                static action(name: string, ...params: string[]): greycat.task.Action;
                static createNode(): greycat.task.Action;
                static createTypedNode(type: string): greycat.task.Action;
                static save(): greycat.task.Action;
                static script(script: string): greycat.task.Action;
                static asyncScript(script: string): greycat.task.Action;
                static lookup(nodeId: string): greycat.task.Action;
                static lookupAll(nodeIds: string): greycat.task.Action;
                static timepoints(from: string, to: string): greycat.task.Action;
                static clearResult(): greycat.task.Action;
            }
            class CoreTask implements greycat.task.Task {
                private insertCapacity;
                actions: greycat.task.Action[];
                insertCursor: number;
                _hooks: greycat.task.TaskHook[];
                addHook(p_hook: greycat.task.TaskHook): greycat.task.Task;
                then(nextAction: greycat.task.Action): greycat.task.Task;
                thenDo(nextActionFunction: greycat.task.ActionFunction): greycat.task.Task;
                doWhile(task: greycat.task.Task, cond: greycat.task.ConditionalFunction): greycat.task.Task;
                doWhileScript(task: greycat.task.Task, condScript: string): greycat.task.Task;
                loop(from: string, to: string, subTask: greycat.task.Task): greycat.task.Task;
                loopPar(from: string, to: string, subTask: greycat.task.Task): greycat.task.Task;
                forEach(subTask: greycat.task.Task): greycat.task.Task;
                forEachPar(subTask: greycat.task.Task): greycat.task.Task;
                map(subTask: greycat.task.Task): greycat.task.Task;
                mapPar(subTask: greycat.task.Task): greycat.task.Task;
                ifThen(cond: greycat.task.ConditionalFunction, then: greycat.task.Task): greycat.task.Task;
                ifThenScript(condScript: string, then: greycat.task.Task): greycat.task.Task;
                ifThenElse(cond: greycat.task.ConditionalFunction, thenSub: greycat.task.Task, elseSub: greycat.task.Task): greycat.task.Task;
                ifThenElseScript(condScript: string, thenSub: greycat.task.Task, elseSub: greycat.task.Task): greycat.task.Task;
                whileDo(cond: greycat.task.ConditionalFunction, task: greycat.task.Task): greycat.task.Task;
                whileDoScript(condScript: string, task: greycat.task.Task): greycat.task.Task;
                pipe(...subTasks: greycat.task.Task[]): greycat.task.Task;
                pipePar(...subTasks: greycat.task.Task[]): greycat.task.Task;
                pipeTo(subTask: greycat.task.Task, ...vars: string[]): greycat.task.Task;
                atomic(protectedTask: greycat.task.Task, ...variablesToLock: string[]): greycat.task.Task;
                execute(graph: greycat.Graph, callback: greycat.Callback<greycat.task.TaskResult<any>>): void;
                executeSync(graph: greycat.Graph): greycat.task.TaskResult<any>;
                executeWith(graph: greycat.Graph, initial: any, callback: greycat.Callback<greycat.task.TaskResult<any>>): void;
                prepare(graph: greycat.Graph, initial: any, callback: greycat.Callback<greycat.task.TaskResult<any>>): greycat.task.TaskContext;
                executeUsing(preparedContext: greycat.task.TaskContext): void;
                executeFrom(parentContext: greycat.task.TaskContext, initial: greycat.task.TaskResult<any>, affinity: number, callback: greycat.Callback<greycat.task.TaskResult<any>>): void;
                executeFromUsing(parentContext: greycat.task.TaskContext, initial: greycat.task.TaskResult<any>, affinity: number, contextInitializer: greycat.Callback<greycat.task.TaskContext>, callback: greycat.Callback<greycat.task.TaskResult<any>>): void;
                loadFromBuffer(buffer: greycat.struct.Buffer, graph: greycat.Graph): greycat.task.Task;
                saveToBuffer(buffer: greycat.struct.Buffer): greycat.task.Task;
                parse(flat: string, graph: greycat.Graph): greycat.task.Task;
                private sub_parse(reader, graph, contextTasks);
                static loadAction(registry: greycat.plugin.ActionRegistry, actionName: string, params: string[], contextTasks: java.util.Map<number, greycat.task.Task>): greycat.task.Action;
                private static condFromScript(script);
                private static executeScript(script, context);
                static fillDefault(registry: greycat.plugin.ActionRegistry): void;
                private static getOrCreate(contextTasks, param);
                hashCode(): number;
                toString(): string;
                serialize(builder: java.lang.StringBuilder, dagCounters: java.util.Map<number, number>): void;
                private static deep_analyze(t, counters, dagCollector);
                travelInWorld(world: string): greycat.task.Task;
                travelInTime(time: string): greycat.task.Task;
                inject(input: any): greycat.task.Task;
                defineAsGlobalVar(name: string): greycat.task.Task;
                defineAsVar(name: string): greycat.task.Task;
                declareGlobalVar(name: string): greycat.task.Task;
                declareVar(name: string): greycat.task.Task;
                readVar(name: string): greycat.task.Task;
                setAsVar(name: string): greycat.task.Task;
                addToVar(name: string): greycat.task.Task;
                setAttribute(name: string, type: number, value: string): greycat.task.Task;
                timeSensitivity(delta: string, offset: string): greycat.task.Task;
                forceAttribute(name: string, type: number, value: string): greycat.task.Task;
                remove(name: string): greycat.task.Task;
                attributes(): greycat.task.Task;
                timepoints(from: string, to: string): greycat.task.Task;
                attributesWithType(filterType: number): greycat.task.Task;
                addVarToRelation(relName: string, varName: string, ...attributes: string[]): greycat.task.Task;
                removeVarFromRelation(relName: string, varFrom: string, ...attributes: string[]): greycat.task.Task;
                traverse(name: string, ...params: string[]): greycat.task.Task;
                attribute(name: string, ...params: string[]): greycat.task.Task;
                readGlobalIndex(name: string, ...query: string[]): greycat.task.Task;
                globalIndex(indexName: string): greycat.task.Task;
                addToGlobalIndex(name: string, ...attributes: string[]): greycat.task.Task;
                addToGlobalTimedIndex(name: string, ...attributes: string[]): greycat.task.Task;
                removeFromGlobalIndex(name: string, ...attributes: string[]): greycat.task.Task;
                removeFromGlobalTimedIndex(name: string, ...attributes: string[]): greycat.task.Task;
                indexNames(): greycat.task.Task;
                selectWith(name: string, pattern: string): greycat.task.Task;
                selectWithout(name: string, pattern: string): greycat.task.Task;
                select(filterFunction: greycat.task.TaskFunctionSelect): greycat.task.Task;
                selectObject(filterFunction: greycat.task.TaskFunctionSelectObject): greycat.task.Task;
                log(name: string): greycat.task.Task;
                selectScript(script: string): greycat.task.Task;
                print(name: string): greycat.task.Task;
                println(name: string): greycat.task.Task;
                executeExpression(expression: string): greycat.task.Task;
                createNode(): greycat.task.Task;
                createTypedNode(type: string): greycat.task.Task;
                save(): greycat.task.Task;
                script(script: string): greycat.task.Task;
                asyncScript(ascript: string): greycat.task.Task;
                lookup(nodeId: string): greycat.task.Task;
                lookupAll(nodeIds: string): greycat.task.Task;
                clearResult(): greycat.task.Task;
                action(name: string, ...params: string[]): greycat.task.Task;
                flipVar(name: string): greycat.task.Task;
                flat(): greycat.task.Task;
            }
            class CoreTaskContext implements greycat.task.TaskContext {
                private _globalVariables;
                private _parent;
                private _graph;
                _callback: greycat.Callback<greycat.task.TaskResult<any>>;
                private _localVariables;
                private _nextVariables;
                _result: greycat.task.TaskResult<any>;
                private _world;
                private _time;
                private _origin;
                private cursor;
                _hooks: greycat.task.TaskHook[];
                private _output;
                constructor(origin: greycat.internal.task.CoreTask, p_hooks: greycat.task.TaskHook[], parentContext: greycat.task.TaskContext, initial: greycat.task.TaskResult<any>, p_graph: greycat.Graph, p_callback: greycat.Callback<greycat.task.TaskResult<any>>);
                graph(): greycat.Graph;
                world(): number;
                setWorld(p_world: number): greycat.task.TaskContext;
                time(): number;
                setTime(p_time: number): greycat.task.TaskContext;
                variables(): greycat.utility.Tuple<string, greycat.task.TaskResult<any>>[];
                private recursive_collect(ctx, collector);
                variable(name: string): greycat.task.TaskResult<any>;
                isGlobal(name: string): boolean;
                private internal_deep_resolve(name);
                wrap(input: any): greycat.task.TaskResult<any>;
                wrapClone(input: any): greycat.task.TaskResult<any>;
                newResult(): greycat.task.TaskResult<any>;
                declareVariable(name: string): greycat.task.TaskContext;
                private lazyWrap(input);
                defineVariable(name: string, initialResult: any): greycat.task.TaskContext;
                defineVariableForSubTask(name: string, initialResult: any): greycat.task.TaskContext;
                setGlobalVariable(name: string, value: any): greycat.task.TaskContext;
                setVariable(name: string, value: any): greycat.task.TaskContext;
                private internal_deep_resolve_map(name);
                addToGlobalVariable(name: string, value: any): greycat.task.TaskContext;
                addToVariable(name: string, value: any): greycat.task.TaskContext;
                globalVariables(): java.util.Map<string, greycat.task.TaskResult<any>>;
                localVariables(): java.util.Map<string, greycat.task.TaskResult<any>>;
                result(): greycat.task.TaskResult<any>;
                resultAsNodes(): greycat.task.TaskResult<greycat.Node>;
                resultAsStrings(): greycat.task.TaskResult<string>;
                continueWith(nextResult: greycat.task.TaskResult<any>): void;
                continueTask(): void;
                endTask(preFinalResult: greycat.task.TaskResult<any>, e: Error): void;
                execute(): void;
                template(input: string): string;
                templates(inputs: string[]): string[];
                append(additionalOutput: string): void;
                toString(): string;
            }
            class CoreTaskReader {
                private flat;
                private offset;
                private _end;
                constructor(p_flat: string, p_offset: number);
                available(): number;
                charAt(cursor: number): string;
                extract(begin: number, end: number): string;
                markend(p_end: number): void;
                end(): number;
                slice(cursor: number): greycat.internal.task.CoreTaskReader;
            }
            class CoreTaskResultIterator<A> implements greycat.task.TaskResultIterator<A> {
                private _backend;
                private _size;
                private _current;
                constructor(p_backend: any[]);
                next(): A;
                nextWithIndex(): greycat.utility.Tuple<number, A>;
            }
            class TaskHelper {
                static flatNodes(toFLat: any, strict: boolean): greycat.Node[];
                static parseInt(s: string): number;
                static serializeString(param: string, builder: java.lang.StringBuilder, singleQuote: boolean): void;
                static serializeType(type: number, builder: java.lang.StringBuilder): void;
                static serializeStringParams(params: string[], builder: java.lang.StringBuilder): void;
                static serializeNameAndStringParams(name: string, params: string[], builder: java.lang.StringBuilder): void;
            }
            module math {
                class CoreMathExpressionEngine implements greycat.internal.task.math.MathExpressionEngine {
                    static decimalSeparator: string;
                    static minusSign: string;
                    private _cacheAST;
                    constructor(expression: string);
                    static parse(p_expression: string): greycat.internal.task.math.MathExpressionEngine;
                    static isNumber(st: string): boolean;
                    static isDigit(c: string): boolean;
                    static isLetter(c: string): boolean;
                    static isWhitespace(c: string): boolean;
                    private shuntingYard(expression);
                    eval(context: greycat.Node, taskContext: greycat.task.TaskContext, variables: java.util.Map<string, number>): number;
                    private buildAST(rpn);
                    private parseDouble(val);
                    private parseInt(val);
                }
                class MathConditional {
                    private _engine;
                    private _expression;
                    constructor(mathExpression: string);
                    conditional(): greycat.task.ConditionalFunction;
                    toString(): string;
                }
                class MathDoubleToken implements greycat.internal.task.math.MathToken {
                    private _content;
                    constructor(_content: number);
                    type(): number;
                    content(): number;
                }
                class MathEntities {
                    private static INSTANCE;
                    operators: java.util.HashMap<string, greycat.internal.task.math.MathOperation>;
                    functions: java.util.HashMap<string, greycat.internal.task.math.MathFunction>;
                    static getINSTANCE(): greycat.internal.task.math.MathEntities;
                    constructor();
                }
                interface MathExpressionEngine {
                    eval(context: greycat.Node, taskContext: greycat.task.TaskContext, variables: java.util.Map<string, number>): number;
                }
                class MathExpressionTokenizer {
                    private pos;
                    private input;
                    private previousToken;
                    constructor(input: string);
                    hasNext(): boolean;
                    private peekNextChar();
                    next(): string;
                    getPos(): number;
                }
                class MathFreeToken implements greycat.internal.task.math.MathToken {
                    private _content;
                    constructor(content: string);
                    content(): string;
                    type(): number;
                }
                class MathFunction implements greycat.internal.task.math.MathToken {
                    private name;
                    private numParams;
                    constructor(name: string, numParams: number);
                    getName(): string;
                    getNumParams(): number;
                    eval(p: Float64Array): number;
                    private date_to_seconds(value);
                    private date_to_minutes(value);
                    private date_to_hours(value);
                    private date_to_days(value);
                    private date_to_months(value);
                    private date_to_year(value);
                    private date_to_dayofweek(value);
                    type(): number;
                }
                class MathOperation implements greycat.internal.task.math.MathToken {
                    private oper;
                    private precedence;
                    private leftAssoc;
                    constructor(oper: string, precedence: number, leftAssoc: boolean);
                    getOper(): string;
                    getPrecedence(): number;
                    isLeftAssoc(): boolean;
                    eval(v1: number, v2: number): number;
                    type(): number;
                }
                interface MathToken {
                    type(): number;
                }
            }
        }
        module utility {
            class CoreDeferCounter implements greycat.DeferCounter {
                private _nb_down;
                private _counter;
                private _end;
                constructor(nb: number);
                count(): void;
                getCount(): number;
                then(p_callback: greycat.plugin.Job): void;
                wrap(): greycat.Callback<any>;
            }
            class CoreDeferCounterSync implements greycat.DeferCounterSync {
                private _nb_down;
                private _counter;
                private _end;
                private _result;
                constructor(nb: number);
                count(): void;
                getCount(): number;
                then(p_callback: greycat.plugin.Job): void;
                wrap(): greycat.Callback<any>;
                waitResult(): any;
            }
            class ReadOnlyStorage implements greycat.plugin.Storage {
                private wrapped;
                constructor(toWrap: greycat.plugin.Storage);
                get(keys: greycat.struct.Buffer, callback: greycat.Callback<greycat.struct.Buffer>): void;
                put(stream: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void;
                remove(keys: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void;
                connect(graph: greycat.Graph, callback: greycat.Callback<boolean>): void;
                disconnect(callback: greycat.Callback<boolean>): void;
                lock(callback: greycat.Callback<greycat.struct.Buffer>): void;
                unlock(previousLock: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void;
            }
        }
    }
    module plugin {
        interface ActionDeclaration {
            factory(): greycat.plugin.ActionFactory;
            setFactory(factory: greycat.plugin.ActionFactory): greycat.plugin.ActionDeclaration;
            params(): Int8Array;
            setParams(...params: number[]): greycat.plugin.ActionDeclaration;
            description(): string;
            setDescription(description: string): greycat.plugin.ActionDeclaration;
            name(): string;
        }
        interface ActionFactory {
            (params: any[]): greycat.task.Action;
        }
        interface ActionRegistry {
            declaration(name: string): greycat.plugin.ActionDeclaration;
            declarations(): greycat.plugin.ActionDeclaration[];
        }
        interface Job {
            (): void;
        }
        interface MemoryFactory {
            newSpace(memorySize: number, graph: greycat.Graph, deepPriority: boolean): greycat.chunk.ChunkSpace;
            newBuffer(): greycat.struct.Buffer;
        }
        interface NodeDeclaration {
            name(): string;
            factory(): greycat.plugin.NodeFactory;
            setFactory(newFactory: greycat.plugin.NodeFactory): void;
        }
        interface NodeFactory {
            (world: number, time: number, id: number, graph: greycat.Graph): greycat.Node;
        }
        interface NodeRegistry {
            declaration(name: string): greycat.plugin.NodeDeclaration;
            declarationByHash(hash: number): greycat.plugin.NodeDeclaration;
        }
        interface NodeState {
            world(): number;
            time(): number;
            set(index: number, elemType: number, elem: any): void;
            setFromKey(key: string, elemType: number, elem: any): void;
            get(index: number): any;
            getFromKey(key: string): any;
            getFromKeyWithDefault<A>(key: string, defaultValue: A): A;
            getWithDefault<A>(key: number, defaultValue: A): A;
            getOrCreate(index: number, elemType: number): any;
            getOrCreateFromKey(key: string, elemType: number): any;
            getType(index: number): number;
            getTypeFromKey(key: string): number;
            each(callBack: greycat.plugin.NodeStateCallback): void;
        }
        interface NodeStateCallback {
            (attributeKey: number, elemType: number, elem: any): void;
        }
        interface Plugin {
            start(graph: greycat.Graph): void;
            stop(): void;
        }
        interface Resolver {
            init(): void;
            initNode(node: greycat.Node, typeCode: number): void;
            initWorld(parentWorld: number, childWorld: number): void;
            freeNode(node: greycat.Node): void;
            typeCode(node: greycat.Node): number;
            lookup<A extends greycat.Node>(world: number, time: number, id: number, callback: greycat.Callback<A>): void;
            lookupBatch(worlds: Float64Array, times: Float64Array, ids: Float64Array, callback: greycat.Callback<greycat.Node[]>): void;
            lookupTimes(world: number, from: number, to: number, id: number, callback: greycat.Callback<greycat.Node[]>): void;
            lookupAll(world: number, time: number, ids: Float64Array, callback: greycat.Callback<greycat.Node[]>): void;
            lookupAllTimes(world: number, from: number, to: number, ids: Float64Array, callback: greycat.Callback<greycat.Node[]>): void;
            resolveState(node: greycat.Node): greycat.plugin.NodeState;
            alignState(node: greycat.Node): greycat.plugin.NodeState;
            newState(node: greycat.Node, world: number, time: number): greycat.plugin.NodeState;
            resolveTimepoints(node: greycat.Node, beginningOfSearch: number, endOfSearch: number, callback: greycat.Callback<Float64Array>): void;
            stringToHash(name: string, insertIfNotExists: boolean): number;
            hashToString(key: number): string;
            externalLock(node: greycat.Node): void;
            externalUnlock(node: greycat.Node): void;
            setTimeSensitivity(node: greycat.Node, deltaTime: number, delta: number): void;
            getTimeSensitivity(node: greycat.Node): Float64Array;
        }
        interface ResolverFactory {
            newResolver(storage: greycat.plugin.Storage, space: greycat.chunk.ChunkSpace): greycat.plugin.Resolver;
        }
        interface Scheduler {
            dispatch(affinity: number, job: greycat.plugin.Job): void;
            start(): void;
            stop(): void;
            workers(): number;
        }
        class SchedulerAffinity {
            static SAME_THREAD: number;
            static ANY_LOCAL_THREAD: number;
            static OTHER_LOCAL_THREAD: number;
            static ANY_REMOTE_THREAD: number;
        }
        interface Storage {
            get(keys: greycat.struct.Buffer, callback: greycat.Callback<greycat.struct.Buffer>): void;
            put(stream: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void;
            remove(keys: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void;
            connect(graph: greycat.Graph, callback: greycat.Callback<boolean>): void;
            lock(callback: greycat.Callback<greycat.struct.Buffer>): void;
            unlock(previousLock: greycat.struct.Buffer, callback: greycat.Callback<boolean>): void;
            disconnect(callback: greycat.Callback<boolean>): void;
        }
        interface TaskExecutor {
            executeTasks(callback: greycat.Callback<string[]>, ...tasks: greycat.task.Task[]): void;
        }
    }
    module struct {
        interface Buffer {
            write(b: number): void;
            writeAll(bytes: Int8Array): void;
            read(position: number): number;
            data(): Int8Array;
            length(): number;
            free(): void;
            iterator(): greycat.struct.BufferIterator;
            removeLast(): void;
            slice(initPos: number, endPos: number): Int8Array;
        }
        interface BufferIterator {
            hasNext(): boolean;
            next(): greycat.struct.Buffer;
        }
        interface DMatrix {
            init(rows: number, columns: number): greycat.struct.DMatrix;
            fill(value: number): greycat.struct.DMatrix;
            fillWith(values: Float64Array): greycat.struct.DMatrix;
            fillWithRandom(random: java.util.Random, min: number, max: number): greycat.struct.DMatrix;
            fillWithRandomStd(random: java.util.Random, std: number): greycat.struct.DMatrix;
            rows(): number;
            columns(): number;
            column(i: number): Float64Array;
            get(rowIndex: number, columnIndex: number): number;
            set(rowIndex: number, columnIndex: number, value: number): greycat.struct.DMatrix;
            add(rowIndex: number, columnIndex: number, value: number): greycat.struct.DMatrix;
            appendColumn(newColumn: Float64Array): greycat.struct.DMatrix;
            data(): Float64Array;
            leadingDimension(): number;
            unsafeGet(index: number): number;
            unsafeSet(index: number, value: number): greycat.struct.DMatrix;
        }
        interface EGraph {
            root(): greycat.struct.ENode;
            newNode(): greycat.struct.ENode;
            setRoot(eNode: greycat.struct.ENode): greycat.struct.EGraph;
            drop(eNode: greycat.struct.ENode): greycat.struct.EGraph;
            size(): number;
            free(): void;
            graph(): greycat.Graph;
        }
        interface ENode {
            set(name: string, type: number, value: any): greycat.struct.ENode;
            setAt(key: number, type: number, value: any): greycat.struct.ENode;
            get(name: string): any;
            getAt(key: number): any;
            getOrCreate(key: string, type: number): any;
            getOrCreateAt(key: number, type: number): any;
            drop(): void;
            graph(): greycat.struct.EGraph;
            each(callBack: greycat.plugin.NodeStateCallback): void;
            clear(): greycat.struct.ENode;
        }
        interface ERelation {
            nodes(): greycat.struct.ENode[];
            node(index: number): greycat.struct.ENode;
            size(): number;
            add(eNode: greycat.struct.ENode): greycat.struct.ERelation;
            addAll(eNodes: greycat.struct.ENode[]): greycat.struct.ERelation;
            clear(): greycat.struct.ERelation;
        }
        interface LMatrix {
            init(rows: number, columns: number): greycat.struct.LMatrix;
            fill(value: number): greycat.struct.LMatrix;
            fillWith(values: Float64Array): greycat.struct.LMatrix;
            fillWithRandom(min: number, max: number, seed: number): greycat.struct.LMatrix;
            rows(): number;
            columns(): number;
            column(i: number): Float64Array;
            get(rowIndex: number, columnIndex: number): number;
            set(rowIndex: number, columnIndex: number, value: number): greycat.struct.LMatrix;
            add(rowIndex: number, columnIndex: number, value: number): greycat.struct.LMatrix;
            appendColumn(newColumn: Float64Array): greycat.struct.LMatrix;
            data(): Float64Array;
            leadingDimension(): number;
            unsafeGet(index: number): number;
            unsafeSet(index: number, value: number): greycat.struct.LMatrix;
        }
        interface LongLongArrayMap extends greycat.struct.Map {
            get(key: number): Float64Array;
            put(key: number, value: number): void;
            delete(key: number, value: number): void;
            each(callback: greycat.struct.LongLongArrayMapCallBack): void;
            contains(key: number, value: number): boolean;
        }
        interface LongLongArrayMapCallBack {
            (key: number, value: number): void;
        }
        interface LongLongMap extends greycat.struct.Map {
            get(key: number): number;
            put(key: number, value: number): void;
            remove(key: number): void;
            each(callback: greycat.struct.LongLongMapCallBack): void;
        }
        interface LongLongMapCallBack {
            (key: number, value: number): void;
        }
        interface Map {
            size(): number;
        }
        interface Relation {
            all(): Float64Array;
            size(): number;
            get(index: number): number;
            set(index: number, value: number): void;
            add(newValue: number): greycat.struct.Relation;
            addAll(newValues: Float64Array): greycat.struct.Relation;
            addNode(node: greycat.Node): greycat.struct.Relation;
            insert(index: number, newValue: number): greycat.struct.Relation;
            remove(oldValue: number): greycat.struct.Relation;
            delete(oldValue: number): greycat.struct.Relation;
            clear(): greycat.struct.Relation;
        }
        interface RelationIndexed {
            size(): number;
            all(): Float64Array;
            add(node: greycat.Node, ...attributeNames: string[]): greycat.struct.RelationIndexed;
            remove(node: greycat.Node, ...attributeNames: string[]): greycat.struct.RelationIndexed;
            clear(): greycat.struct.RelationIndexed;
            find(callback: greycat.Callback<greycat.Node[]>, world: number, time: number, ...params: string[]): void;
            findByQuery(query: greycat.Query, callback: greycat.Callback<greycat.Node[]>): void;
        }
        interface StringIntMap extends greycat.struct.Map {
            getValue(key: string): number;
            getByHash(index: number): string;
            containsHash(index: number): boolean;
            put(key: string, value: number): void;
            remove(key: string): void;
            each(callback: greycat.struct.StringLongMapCallBack): void;
        }
        interface StringLongMapCallBack {
            (key: string, value: number): void;
        }
    }
    module task {
        interface Action {
            eval(ctx: greycat.task.TaskContext): void;
            serialize(builder: java.lang.StringBuilder): void;
        }
        interface ActionFunction {
            (ctx: greycat.task.TaskContext): void;
        }
        interface ConditionalFunction {
            (ctx: greycat.task.TaskContext): boolean;
        }
        interface Task {
            then(nextAction: greycat.task.Action): greycat.task.Task;
            thenDo(nextActionFunction: greycat.task.ActionFunction): greycat.task.Task;
            doWhile(task: greycat.task.Task, cond: greycat.task.ConditionalFunction): greycat.task.Task;
            doWhileScript(task: greycat.task.Task, condScript: string): greycat.task.Task;
            loop(from: string, to: string, subTask: greycat.task.Task): greycat.task.Task;
            loopPar(from: string, to: string, subTask: greycat.task.Task): greycat.task.Task;
            forEach(subTask: greycat.task.Task): greycat.task.Task;
            forEachPar(subTask: greycat.task.Task): greycat.task.Task;
            flat(): greycat.task.Task;
            map(subTask: greycat.task.Task): greycat.task.Task;
            mapPar(subTask: greycat.task.Task): greycat.task.Task;
            ifThen(cond: greycat.task.ConditionalFunction, then: greycat.task.Task): greycat.task.Task;
            ifThenScript(condScript: string, then: greycat.task.Task): greycat.task.Task;
            ifThenElse(cond: greycat.task.ConditionalFunction, thenSub: greycat.task.Task, elseSub: greycat.task.Task): greycat.task.Task;
            ifThenElseScript(condScript: string, thenSub: greycat.task.Task, elseSub: greycat.task.Task): greycat.task.Task;
            whileDo(cond: greycat.task.ConditionalFunction, task: greycat.task.Task): greycat.task.Task;
            whileDoScript(condScript: string, task: greycat.task.Task): greycat.task.Task;
            pipe(...subTasks: greycat.task.Task[]): greycat.task.Task;
            pipePar(...subTasks: greycat.task.Task[]): greycat.task.Task;
            pipeTo(subTask: greycat.task.Task, ...vars: string[]): greycat.task.Task;
            parse(input: string, graph: greycat.Graph): greycat.task.Task;
            loadFromBuffer(buffer: greycat.struct.Buffer, graph: greycat.Graph): greycat.task.Task;
            saveToBuffer(buffer: greycat.struct.Buffer): greycat.task.Task;
            addHook(hook: greycat.task.TaskHook): greycat.task.Task;
            execute(graph: greycat.Graph, callback: greycat.Callback<greycat.task.TaskResult<any>>): void;
            executeSync(graph: greycat.Graph): greycat.task.TaskResult<any>;
            executeWith(graph: greycat.Graph, initial: any, callback: greycat.Callback<greycat.task.TaskResult<any>>): void;
            prepare(graph: greycat.Graph, initial: any, callback: greycat.Callback<greycat.task.TaskResult<any>>): greycat.task.TaskContext;
            executeUsing(preparedContext: greycat.task.TaskContext): void;
            executeFrom(parentContext: greycat.task.TaskContext, initial: greycat.task.TaskResult<any>, affinity: number, callback: greycat.Callback<greycat.task.TaskResult<any>>): void;
            executeFromUsing(parentContext: greycat.task.TaskContext, initial: greycat.task.TaskResult<any>, affinity: number, contextInitializer: greycat.Callback<greycat.task.TaskContext>, callback: greycat.Callback<greycat.task.TaskResult<any>>): void;
            travelInWorld(world: string): greycat.task.Task;
            travelInTime(time: string): greycat.task.Task;
            inject(input: any): greycat.task.Task;
            defineAsGlobalVar(name: string): greycat.task.Task;
            defineAsVar(name: string): greycat.task.Task;
            declareGlobalVar(name: string): greycat.task.Task;
            declareVar(name: string): greycat.task.Task;
            readVar(name: string): greycat.task.Task;
            setAsVar(name: string): greycat.task.Task;
            addToVar(name: string): greycat.task.Task;
            setAttribute(name: string, type: number, value: string): greycat.task.Task;
            timeSensitivity(delta: string, offset: string): greycat.task.Task;
            forceAttribute(name: string, type: number, value: string): greycat.task.Task;
            remove(name: string): greycat.task.Task;
            attributes(): greycat.task.Task;
            timepoints(from: string, to: string): greycat.task.Task;
            attributesWithType(filterType: number): greycat.task.Task;
            addVarToRelation(relName: string, varName: string, ...attributes: string[]): greycat.task.Task;
            removeVarFromRelation(relName: string, varFrom: string, ...attributes: string[]): greycat.task.Task;
            traverse(name: string, ...params: string[]): greycat.task.Task;
            attribute(name: string, ...params: string[]): greycat.task.Task;
            readGlobalIndex(indexName: string, ...query: string[]): greycat.task.Task;
            globalIndex(indexName: string): greycat.task.Task;
            addToGlobalIndex(name: string, ...attributes: string[]): greycat.task.Task;
            addToGlobalTimedIndex(name: string, ...attributes: string[]): greycat.task.Task;
            removeFromGlobalIndex(name: string, ...attributes: string[]): greycat.task.Task;
            removeFromGlobalTimedIndex(name: string, ...attributes: string[]): greycat.task.Task;
            indexNames(): greycat.task.Task;
            selectWith(name: string, pattern: string): greycat.task.Task;
            selectWithout(name: string, pattern: string): greycat.task.Task;
            select(filterFunction: greycat.task.TaskFunctionSelect): greycat.task.Task;
            selectScript(script: string): greycat.task.Task;
            selectObject(filterFunction: greycat.task.TaskFunctionSelectObject): greycat.task.Task;
            log(name: string): greycat.task.Task;
            print(name: string): greycat.task.Task;
            println(name: string): greycat.task.Task;
            executeExpression(expression: string): greycat.task.Task;
            createNode(): greycat.task.Task;
            createTypedNode(type: string): greycat.task.Task;
            save(): greycat.task.Task;
            script(script: string): greycat.task.Task;
            asyncScript(ascript: string): greycat.task.Task;
            lookup(nodeId: string): greycat.task.Task;
            lookupAll(nodeIds: string): greycat.task.Task;
            clearResult(): greycat.task.Task;
            action(name: string, ...params: string[]): greycat.task.Task;
            flipVar(name: string): greycat.task.Task;
            atomic(protectedTask: greycat.task.Task, ...variablesToLock: string[]): greycat.task.Task;
        }
        interface TaskActionFactory {
            (params: string[], contextTasks: java.util.Map<number, greycat.task.Task>): greycat.task.Action;
        }
        interface TaskContext {
            graph(): greycat.Graph;
            world(): number;
            setWorld(world: number): greycat.task.TaskContext;
            time(): number;
            setTime(time: number): greycat.task.TaskContext;
            variables(): greycat.utility.Tuple<string, greycat.task.TaskResult<any>>[];
            variable(name: string): greycat.task.TaskResult<any>;
            isGlobal(name: string): boolean;
            wrap(input: any): greycat.task.TaskResult<any>;
            wrapClone(input: any): greycat.task.TaskResult<any>;
            newResult(): greycat.task.TaskResult<any>;
            declareVariable(name: string): greycat.task.TaskContext;
            defineVariable(name: string, initialResult: any): greycat.task.TaskContext;
            defineVariableForSubTask(name: string, initialResult: any): greycat.task.TaskContext;
            setGlobalVariable(name: string, value: any): greycat.task.TaskContext;
            setVariable(name: string, value: any): greycat.task.TaskContext;
            addToGlobalVariable(name: string, value: any): greycat.task.TaskContext;
            addToVariable(name: string, value: any): greycat.task.TaskContext;
            result(): greycat.task.TaskResult<any>;
            resultAsNodes(): greycat.task.TaskResult<greycat.Node>;
            resultAsStrings(): greycat.task.TaskResult<string>;
            continueTask(): void;
            continueWith(nextResult: greycat.task.TaskResult<any>): void;
            endTask(nextResult: greycat.task.TaskResult<any>, e: Error): void;
            template(input: string): string;
            templates(inputs: string[]): string[];
            append(additionalOutput: string): void;
        }
        interface TaskFunctionSelect {
            (node: greycat.Node, context: greycat.task.TaskContext): boolean;
        }
        interface TaskFunctionSelectObject {
            (object: any, context: greycat.task.TaskContext): boolean;
        }
        interface TaskHook {
            start(initialContext: greycat.task.TaskContext): void;
            beforeAction(action: greycat.task.Action, context: greycat.task.TaskContext): void;
            afterAction(action: greycat.task.Action, context: greycat.task.TaskContext): void;
            beforeTask(parentContext: greycat.task.TaskContext, context: greycat.task.TaskContext): void;
            afterTask(context: greycat.task.TaskContext): void;
            end(finalContext: greycat.task.TaskContext): void;
        }
        interface TaskResult<A> {
            iterator(): greycat.task.TaskResultIterator<any>;
            get(index: number): A;
            set(index: number, input: A): greycat.task.TaskResult<A>;
            allocate(index: number): greycat.task.TaskResult<A>;
            add(input: A): greycat.task.TaskResult<A>;
            clear(): greycat.task.TaskResult<A>;
            clone(): greycat.task.TaskResult<A>;
            free(): void;
            size(): number;
            asArray(): any[];
            exception(): Error;
            output(): string;
            setException(e: Error): greycat.task.TaskResult<A>;
            setOutput(output: string): greycat.task.TaskResult<A>;
            fillWith(source: greycat.task.TaskResult<A>): greycat.task.TaskResult<A>;
        }
        interface TaskResultIterator<A> {
            next(): A;
            nextWithIndex(): greycat.utility.Tuple<number, A>;
        }
        class Tasks {
            static cond(mathExpression: string): greycat.task.ConditionalFunction;
            static newTask(): greycat.task.Task;
            static emptyResult(): greycat.task.TaskResult<any>;
            static then(action: greycat.task.Action): greycat.task.Task;
            static thenDo(actionFunction: greycat.task.ActionFunction): greycat.task.Task;
            static loop(from: string, to: string, subTask: greycat.task.Task): greycat.task.Task;
            static loopPar(from: string, to: string, subTask: greycat.task.Task): greycat.task.Task;
            static forEach(subTask: greycat.task.Task): greycat.task.Task;
            static forEachPar(subTask: greycat.task.Task): greycat.task.Task;
            static map(subTask: greycat.task.Task): greycat.task.Task;
            static mapPar(subTask: greycat.task.Task): greycat.task.Task;
            static ifThen(cond: greycat.task.ConditionalFunction, then: greycat.task.Task): greycat.task.Task;
            static ifThenScript(condScript: string, then: greycat.task.Task): greycat.task.Task;
            static ifThenElse(cond: greycat.task.ConditionalFunction, thenSub: greycat.task.Task, elseSub: greycat.task.Task): greycat.task.Task;
            static ifThenElseScript(condScript: string, thenSub: greycat.task.Task, elseSub: greycat.task.Task): greycat.task.Task;
            static doWhile(task: greycat.task.Task, cond: greycat.task.ConditionalFunction): greycat.task.Task;
            static doWhileScript(task: greycat.task.Task, condScript: string): greycat.task.Task;
            static whileDo(cond: greycat.task.ConditionalFunction, task: greycat.task.Task): greycat.task.Task;
            static whileDoScript(condScript: string, task: greycat.task.Task): greycat.task.Task;
            static pipe(...subTasks: greycat.task.Task[]): greycat.task.Task;
            static pipePar(...subTasks: greycat.task.Task[]): greycat.task.Task;
            static pipeTo(subTask: greycat.task.Task, ...vars: string[]): greycat.task.Task;
            static atomic(protectedTask: greycat.task.Task, ...variablesToLock: string[]): greycat.task.Task;
            static parse(flat: string, graph: greycat.Graph): greycat.task.Task;
        }
    }
    module utility {
        class Base64 {
            private static dictionary;
            private static powTwo;
            private static longIndexes;
            static encodeLongToBuffer(l: number, buffer: greycat.struct.Buffer): void;
            static encodeIntToBuffer(l: number, buffer: greycat.struct.Buffer): void;
            static decodeToLong(s: greycat.struct.Buffer): number;
            static decodeToLongWithBounds(s: greycat.struct.Buffer, offsetBegin: number, offsetEnd: number): number;
            static decodeToInt(s: greycat.struct.Buffer): number;
            static decodeToIntWithBounds(s: greycat.struct.Buffer, offsetBegin: number, offsetEnd: number): number;
            static encodeDoubleToBuffer(d: number, buffer: greycat.struct.Buffer): void;
            static decodeToDouble(s: greycat.struct.Buffer): number;
            static decodeToDoubleWithBounds(s: greycat.struct.Buffer, offsetBegin: number, offsetEnd: number): number;
            static encodeBoolArrayToBuffer(boolArr: Array<boolean>, buffer: greycat.struct.Buffer): void;
            static decodeBoolArray(s: greycat.struct.Buffer, arraySize: number): any[];
            static decodeToBoolArrayWithBounds(s: greycat.struct.Buffer, offsetBegin: number, offsetEnd: number, arraySize: number): any[];
            static encodeStringToBuffer(s: string, buffer: greycat.struct.Buffer): void;
            static decodeString(s: greycat.struct.Buffer): string;
            static decodeToStringWithBounds(s: greycat.struct.Buffer, offsetBegin: number, offsetEnd: number): string;
        }
        class BufferView implements greycat.struct.Buffer {
            private _origin;
            private _initPos;
            private _endPos;
            constructor(p_origin: greycat.struct.Buffer, p_initPos: number, p_endPos: number);
            write(b: number): void;
            writeAll(bytes: Int8Array): void;
            read(position: number): number;
            data(): Int8Array;
            length(): number;
            free(): void;
            iterator(): greycat.struct.BufferIterator;
            removeLast(): void;
            slice(initPos: number, endPos: number): Int8Array;
        }
        class DefaultBufferIterator implements greycat.struct.BufferIterator {
            private _origin;
            private _originSize;
            private _cursor;
            constructor(p_origin: greycat.struct.Buffer);
            hasNext(): boolean;
            next(): greycat.struct.Buffer;
        }
        class Enforcer {
            private checkers;
            asBool(propertyName: string): greycat.utility.Enforcer;
            asString(propertyName: string): greycat.utility.Enforcer;
            asLong(propertyName: string): greycat.utility.Enforcer;
            asLongWithin(propertyName: string, min: number, max: number): greycat.utility.Enforcer;
            asDouble(propertyName: string): greycat.utility.Enforcer;
            asDoubleWithin(propertyName: string, min: number, max: number): greycat.utility.Enforcer;
            asInt(propertyName: string): greycat.utility.Enforcer;
            asIntWithin(propertyName: string, min: number, max: number): greycat.utility.Enforcer;
            asIntGreaterOrEquals(propertyName: string, min: number): greycat.utility.Enforcer;
            asDoubleArray(propertyName: string): greycat.utility.Enforcer;
            asPositiveInt(propertyName: string): greycat.utility.Enforcer;
            asNonNegativeDouble(propertyName: string): greycat.utility.Enforcer;
            asPositiveDouble(propertyName: string): greycat.utility.Enforcer;
            asNonNegativeOrNanDouble(propertyName: string): greycat.utility.Enforcer;
            asPositiveLong(propertyName: string): greycat.utility.Enforcer;
            declare(propertyName: string, checker: greycat.utility.EnforcerChecker): greycat.utility.Enforcer;
            check(propertyName: string, propertyType: number, propertyValue: any): void;
        }
        interface EnforcerChecker {
            check(inputType: number, input: any): void;
        }
        class HashHelper {
            private static PRIME1;
            private static PRIME2;
            private static PRIME3;
            private static PRIME4;
            private static PRIME5;
            private static len;
            private static byteTable;
            private static HSTART;
            private static HMULT;
            static longHash(number: number, max: number): number;
            static simpleTripleHash(p0: number, p1: number, p2: number, p3: number, max: number): number;
            static tripleHash(p0: number, p1: number, p2: number, p3: number, max: number): number;
            static rand(): number;
            static equals(src: string, other: string): boolean;
            static DOUBLE_MIN_VALUE(): number;
            static DOUBLE_MAX_VALUE(): number;
            static isDefined(param: any): boolean;
            static hash(data: string): number;
            static hashBytes(data: Int8Array): number;
        }
        class KeyHelper {
            static keyToBuffer(buffer: greycat.struct.Buffer, chunkType: number, world: number, time: number, id: number): void;
        }
        class Tuple<A, B> {
            private _left;
            private _right;
            constructor(p_left: A, p_right: B);
            left(): A;
            right(): B;
        }
        class VerboseHook implements greycat.task.TaskHook {
            private ctxIdents;
            start(initialContext: greycat.task.TaskContext): void;
            beforeAction(action: greycat.task.Action, context: greycat.task.TaskContext): void;
            afterAction(action: greycat.task.Action, context: greycat.task.TaskContext): void;
            beforeTask(parentContext: greycat.task.TaskContext, context: greycat.task.TaskContext): void;
            afterTask(context: greycat.task.TaskContext): void;
            end(finalContext: greycat.task.TaskContext): void;
        }
        class VerbosePlugin implements greycat.plugin.Plugin {
            start(graph: greycat.Graph): void;
            stop(): void;
        }
    }
}
