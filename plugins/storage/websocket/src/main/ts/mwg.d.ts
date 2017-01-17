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
declare module org {
    module mwg {
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
            static isDefined(param: any): boolean;
            static equals(src: string, other: string): boolean;
            static longArrayEquals(src: Float64Array, other: Float64Array): boolean;
        }
        interface DeferCounter {
            count(): void;
            getCount(): number;
            then(job: org.mwg.plugin.Job): void;
            wrap(): org.mwg.Callback<any>;
        }
        interface DeferCounterSync extends org.mwg.DeferCounter {
            waitResult(): any;
        }
        interface Graph {
            newNode(world: number, time: number): org.mwg.Node;
            newTypedNode(world: number, time: number, nodeType: string): org.mwg.Node;
            cloneNode(origin: org.mwg.Node): org.mwg.Node;
            lookup<A extends org.mwg.Node>(world: number, time: number, id: number, callback: org.mwg.Callback<A>): void;
            lookupBatch(worlds: Float64Array, times: Float64Array, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            lookupAll(world: number, time: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            lookupTimes(world: number, from: number, to: number, id: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            lookupAllTimes(world: number, from: number, to: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            fork(world: number): number;
            save(callback: org.mwg.Callback<boolean>): void;
            connect(callback: org.mwg.Callback<boolean>): void;
            disconnect(callback: org.mwg.Callback<boolean>): void;
            index(world: number, time: number, name: string, callback: org.mwg.Callback<org.mwg.NodeIndex>): void;
            indexIfExists(world: number, time: number, name: string, callback: org.mwg.Callback<org.mwg.NodeIndex>): void;
            indexNames(world: number, time: number, callback: org.mwg.Callback<string[]>): void;
            newCounter(expectedEventsCount: number): org.mwg.DeferCounter;
            newSyncCounter(expectedEventsCount: number): org.mwg.DeferCounterSync;
            resolver(): org.mwg.plugin.Resolver;
            scheduler(): org.mwg.plugin.Scheduler;
            space(): org.mwg.chunk.ChunkSpace;
            storage(): org.mwg.plugin.Storage;
            newBuffer(): org.mwg.struct.Buffer;
            newQuery(): org.mwg.Query;
            freeNodes(nodes: org.mwg.Node[]): void;
            taskAction(name: string): org.mwg.task.TaskActionFactory;
            taskHooks(): org.mwg.task.TaskHook[];
        }
        class GraphBuilder {
            private _storage;
            private _scheduler;
            private _plugins;
            private _memorySize;
            private _readOnly;
            static newBuilder(): org.mwg.GraphBuilder;
            withStorage(storage: org.mwg.plugin.Storage): org.mwg.GraphBuilder;
            withReadOnlyStorage(storage: org.mwg.plugin.Storage): org.mwg.GraphBuilder;
            withMemorySize(numberOfElements: number): org.mwg.GraphBuilder;
            withScheduler(scheduler: org.mwg.plugin.Scheduler): org.mwg.GraphBuilder;
            withPlugin(plugin: org.mwg.plugin.Plugin): org.mwg.GraphBuilder;
            build(): org.mwg.Graph;
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
            set(name: string, type: number, value: any): org.mwg.Node;
            setAt(index: number, type: number, value: any): org.mwg.Node;
            forceSet(name: string, type: number, value: any): org.mwg.Node;
            forceSetAt(index: number, type: number, value: any): org.mwg.Node;
            remove(name: string): org.mwg.Node;
            removeAt(index: number): org.mwg.Node;
            getOrCreate(name: string, type: number, ...params: string[]): any;
            getOrCreateAt(index: number, type: number, ...params: string[]): any;
            relation(relationName: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            relationAt(relationIndex: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            addToRelation(relationName: string, relatedNode: org.mwg.Node, ...indexedAttributes: string[]): org.mwg.Node;
            addToRelationAt(relationIndex: number, relatedNode: org.mwg.Node, ...indexedAttributes: string[]): org.mwg.Node;
            removeFromRelation(relationName: string, relatedNode: org.mwg.Node, ...indexedAttributes: string[]): org.mwg.Node;
            removeFromRelationAt(relationIndex: number, relatedNode: org.mwg.Node, ...indexedAttributes: string[]): org.mwg.Node;
            timeDephasing(): number;
            lastModification(): number;
            rephase(): org.mwg.Node;
            timepoints(beginningOfSearch: number, endOfSearch: number, callback: org.mwg.Callback<Float64Array>): void;
            free(): void;
            graph(): org.mwg.Graph;
            travelInTime<A extends org.mwg.Node>(targetTime: number, callback: org.mwg.Callback<A>): void;
            setTimeSensitivity(deltaTime: number, offset: number): org.mwg.Node;
            timeSensitivity(): Float64Array;
        }
        interface NodeIndex extends org.mwg.Node {
            size(): number;
            all(): Float64Array;
            addToIndex(node: org.mwg.Node, ...attributeNames: string[]): org.mwg.NodeIndex;
            removeFromIndex(node: org.mwg.Node, ...attributeNames: string[]): org.mwg.NodeIndex;
            clear(): org.mwg.NodeIndex;
            find(callback: org.mwg.Callback<org.mwg.Node[]>, ...params: string[]): void;
            findByQuery(query: org.mwg.Query, callback: org.mwg.Callback<org.mwg.Node[]>): void;
        }
        interface Query {
            world(): number;
            setWorld(world: number): org.mwg.Query;
            time(): number;
            setTime(time: number): org.mwg.Query;
            add(attributeName: string, value: string): org.mwg.Query;
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
            static typeName(p_type: number): string;
            static typeFromName(name: string): number;
        }
        module base {
            class BaseHook implements org.mwg.task.TaskHook {
                start(initialContext: org.mwg.task.TaskContext): void;
                beforeAction(action: org.mwg.task.Action, context: org.mwg.task.TaskContext): void;
                afterAction(action: org.mwg.task.Action, context: org.mwg.task.TaskContext): void;
                beforeTask(parentContext: org.mwg.task.TaskContext, context: org.mwg.task.TaskContext): void;
                afterTask(context: org.mwg.task.TaskContext): void;
                end(finalContext: org.mwg.task.TaskContext): void;
            }
            class BaseNode implements org.mwg.Node {
                private _world;
                private _time;
                private _id;
                private _graph;
                _resolver: org.mwg.plugin.Resolver;
                _index_worldOrder: number;
                _index_superTimeTree: number;
                _index_timeTree: number;
                _index_stateChunk: number;
                _world_magic: number;
                _super_time_magic: number;
                _time_magic: number;
                _dead: boolean;
                private _lock;
                constructor(p_world: number, p_time: number, p_id: number, p_graph: org.mwg.Graph);
                cacheLock(): void;
                cacheUnlock(): void;
                init(): void;
                nodeTypeName(): string;
                unphasedState(): org.mwg.plugin.NodeState;
                phasedState(): org.mwg.plugin.NodeState;
                newState(time: number): org.mwg.plugin.NodeState;
                graph(): org.mwg.Graph;
                world(): number;
                time(): number;
                id(): number;
                get(name: string): any;
                getAt(propIndex: number): any;
                forceSet(name: string, type: number, value: any): org.mwg.Node;
                forceSetAt(index: number, type: number, value: any): org.mwg.Node;
                setAt(index: number, type: number, value: any): org.mwg.Node;
                set(name: string, type: number, value: any): org.mwg.Node;
                private isEquals(obj1, obj2, type);
                getOrCreate(name: string, type: number, ...params: string[]): any;
                getOrCreateAt(index: number, type: number, ...params: string[]): any;
                type(name: string): number;
                typeAt(index: number): number;
                remove(name: string): org.mwg.Node;
                removeAt(index: number): org.mwg.Node;
                relation(relationName: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                relationAt(relationIndex: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                addToRelation(relationName: string, relatedNode: org.mwg.Node, ...attributes: string[]): org.mwg.Node;
                addToRelationAt(relationIndex: number, relatedNode: org.mwg.Node, ...attributes: string[]): org.mwg.Node;
                removeFromRelation(relationName: string, relatedNode: org.mwg.Node, ...attributes: string[]): org.mwg.Node;
                removeFromRelationAt(relationIndex: number, relatedNode: org.mwg.Node, ...attributes: string[]): org.mwg.Node;
                free(): void;
                timeDephasing(): number;
                lastModification(): number;
                rephase(): org.mwg.Node;
                timepoints(beginningOfSearch: number, endOfSearch: number, callback: org.mwg.Callback<Float64Array>): void;
                travelInTime<A extends org.mwg.Node>(targetTime: number, callback: org.mwg.Callback<A>): void;
                setTimeSensitivity(deltaTime: number, offset: number): org.mwg.Node;
                timeSensitivity(): Float64Array;
                static isNaN(toTest: number): boolean;
                toString(): string;
            }
            class BasePlugin implements org.mwg.plugin.Plugin {
                private _nodeTypes;
                private _taskActions;
                private _memoryFactory;
                private _resolverFactory;
                private _taskHooks;
                declareNodeType(name: string, factory: org.mwg.plugin.NodeFactory): org.mwg.plugin.Plugin;
                declareTaskAction(name: string, factory: org.mwg.task.TaskActionFactory): org.mwg.plugin.Plugin;
                declareMemoryFactory(factory: org.mwg.plugin.MemoryFactory): org.mwg.plugin.Plugin;
                declareResolverFactory(factory: org.mwg.plugin.ResolverFactory): org.mwg.plugin.Plugin;
                taskHooks(): org.mwg.task.TaskHook[];
                declareTaskHook(hook: org.mwg.task.TaskHook): org.mwg.plugin.Plugin;
                nodeTypes(): string[];
                nodeType(nodeTypeName: string): org.mwg.plugin.NodeFactory;
                taskActionTypes(): string[];
                taskActionType(taskTypeName: string): org.mwg.task.TaskActionFactory;
                memoryFactory(): org.mwg.plugin.MemoryFactory;
                resolverFactory(): org.mwg.plugin.ResolverFactory;
                stop(): void;
            }
        }
        module chunk {
            interface Chunk {
                world(): number;
                time(): number;
                id(): number;
                chunkType(): number;
                index(): number;
                save(buffer: org.mwg.struct.Buffer): void;
                saveDiff(buffer: org.mwg.struct.Buffer): void;
                load(buffer: org.mwg.struct.Buffer): void;
                loadDiff(buffer: org.mwg.struct.Buffer): void;
            }
            interface ChunkSpace {
                createAndMark(type: number, world: number, time: number, id: number): org.mwg.chunk.Chunk;
                getAndMark(type: number, world: number, time: number, id: number): org.mwg.chunk.Chunk;
                getOrLoadAndMark(type: number, world: number, time: number, id: number, callback: org.mwg.Callback<org.mwg.chunk.Chunk>): void;
                getOrLoadAndMarkAll(keys: Float64Array, callback: org.mwg.Callback<org.mwg.chunk.Chunk[]>): void;
                get(index: number): org.mwg.chunk.Chunk;
                unmark(index: number): void;
                mark(index: number): number;
                free(chunk: org.mwg.chunk.Chunk): void;
                notifyUpdate(index: number): void;
                graph(): org.mwg.Graph;
                save(callback: org.mwg.Callback<boolean>): void;
                clear(): void;
                freeAll(): void;
                available(): number;
                newVolatileGraph(): org.mwg.struct.EGraph;
            }
            class ChunkType {
                static STATE_CHUNK: number;
                static TIME_TREE_CHUNK: number;
                static WORLD_ORDER_CHUNK: number;
                static GEN_CHUNK: number;
            }
            interface GenChunk extends org.mwg.chunk.Chunk {
                newKey(): number;
            }
            interface Stack {
                enqueue(index: number): boolean;
                dequeueTail(): number;
                dequeue(index: number): boolean;
                free(): void;
                size(): number;
            }
            interface StateChunk extends org.mwg.chunk.Chunk, org.mwg.plugin.NodeState {
                loadFrom(origin: org.mwg.chunk.StateChunk): void;
            }
            interface TimeTreeChunk extends org.mwg.chunk.Chunk {
                insert(key: number): void;
                unsafe_insert(key: number): void;
                previousOrEqual(key: number): number;
                clearAt(max: number): void;
                range(startKey: number, endKey: number, maxElements: number, walker: org.mwg.chunk.TreeWalker): void;
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
            interface WorldOrderChunk extends org.mwg.chunk.Chunk, org.mwg.struct.LongLongMap {
                magic(): number;
                lock(): void;
                unlock(): void;
                externalLock(): void;
                externalUnlock(): void;
                extra(): number;
                setExtra(extraValue: number): void;
            }
        }
        module core {
            class BlackHoleStorage implements org.mwg.plugin.Storage {
                private _graph;
                private prefix;
                get(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
                put(stream: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                remove(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                connect(graph: org.mwg.Graph, callback: org.mwg.Callback<boolean>): void;
                lock(callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
                unlock(previousLock: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                disconnect(callback: org.mwg.Callback<boolean>): void;
            }
            class CoreConstants extends org.mwg.Constants {
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
            class CoreGraph implements org.mwg.Graph {
                private _storage;
                private _space;
                private _scheduler;
                private _resolver;
                private _nodeTypes;
                private _taskActions;
                private _isConnected;
                private _lock;
                private _plugins;
                private _memoryFactory;
                private _taskHooks;
                private _prefix;
                private _nodeKeyCalculator;
                private _worldKeyCalculator;
                constructor(p_storage: org.mwg.plugin.Storage, memorySize: number, p_scheduler: org.mwg.plugin.Scheduler, p_plugins: org.mwg.plugin.Plugin[]);
                fork(world: number): number;
                newNode(world: number, time: number): org.mwg.Node;
                newTypedNode(world: number, time: number, nodeType: string): org.mwg.Node;
                cloneNode(origin: org.mwg.Node): org.mwg.Node;
                factoryByCode(code: number): org.mwg.plugin.NodeFactory;
                taskAction(taskActionName: string): org.mwg.task.TaskActionFactory;
                taskHooks(): org.mwg.task.TaskHook[];
                lookup<A extends org.mwg.Node>(world: number, time: number, id: number, callback: org.mwg.Callback<A>): void;
                lookupBatch(worlds: Float64Array, times: Float64Array, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                lookupAll(world: number, time: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                lookupTimes(world: number, from: number, to: number, id: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                lookupAllTimes(world: number, from: number, to: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                save(callback: org.mwg.Callback<boolean>): void;
                connect(callback: org.mwg.Callback<boolean>): void;
                disconnect(callback: org.mwg.Callback<any>): void;
                newBuffer(): org.mwg.struct.Buffer;
                newQuery(): org.mwg.Query;
                index(world: number, time: number, name: string, callback: org.mwg.Callback<org.mwg.NodeIndex>): void;
                indexIfExists(world: number, time: number, name: string, callback: org.mwg.Callback<org.mwg.NodeIndex>): void;
                private internal_index(world, time, name, ifExists, callback);
                indexNames(world: number, time: number, callback: org.mwg.Callback<string[]>): void;
                newCounter(expectedCountCalls: number): org.mwg.DeferCounter;
                newSyncCounter(expectedCountCalls: number): org.mwg.DeferCounterSync;
                resolver(): org.mwg.plugin.Resolver;
                scheduler(): org.mwg.plugin.Scheduler;
                space(): org.mwg.chunk.ChunkSpace;
                storage(): org.mwg.plugin.Storage;
                freeNodes(nodes: org.mwg.Node[]): void;
            }
            class CoreNodeIndex extends org.mwg.base.BaseNode implements org.mwg.NodeIndex {
                static NAME: string;
                constructor(p_world: number, p_time: number, p_id: number, p_graph: org.mwg.Graph);
                init(): void;
                size(): number;
                all(): Float64Array;
                addToIndex(node: org.mwg.Node, ...attributeNames: string[]): org.mwg.NodeIndex;
                removeFromIndex(node: org.mwg.Node, ...attributeNames: string[]): org.mwg.NodeIndex;
                clear(): org.mwg.NodeIndex;
                find(callback: org.mwg.Callback<org.mwg.Node[]>, ...query: string[]): void;
                findByQuery(query: org.mwg.Query, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            }
            class CoreQuery implements org.mwg.Query {
                private _resolver;
                private _graph;
                private capacity;
                private _attributes;
                private _values;
                private size;
                private _hash;
                private _world;
                private _time;
                constructor(graph: org.mwg.Graph, p_resolver: org.mwg.plugin.Resolver);
                world(): number;
                setWorld(p_world: number): org.mwg.Query;
                time(): number;
                setTime(p_time: number): org.mwg.Query;
                add(attributeName: string, value: string): org.mwg.Query;
                hash(): number;
                attributes(): Int32Array;
                values(): any[];
                private internal_add(att, val);
                private compute();
            }
            class MWGResolver implements org.mwg.plugin.Resolver {
                private _storage;
                private _space;
                private _graph;
                private dictionary;
                private globalWorldOrderChunk;
                private static KEY_SIZE;
                constructor(p_storage: org.mwg.plugin.Storage, p_space: org.mwg.chunk.ChunkSpace, p_graph: org.mwg.Graph);
                init(): void;
                typeName(node: org.mwg.Node): string;
                typeCode(node: org.mwg.Node): number;
                initNode(node: org.mwg.Node, codeType: number): void;
                initWorld(parentWorld: number, childWorld: number): void;
                freeNode(node: org.mwg.Node): void;
                externalLock(node: org.mwg.Node): void;
                externalUnlock(node: org.mwg.Node): void;
                setTimeSensitivity(node: org.mwg.Node, deltaTime: number, offset: number): void;
                getTimeSensitivity(node: org.mwg.Node): Float64Array;
                lookup<A extends org.mwg.Node>(world: number, time: number, id: number, callback: org.mwg.Callback<A>): void;
                lookupBatch(worlds: Float64Array, times: Float64Array, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                lookupTimes(world: number, from: number, to: number, id: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                private lookupAll_end(finalResult, callback, sizeIds, worldOrders, superTimes, times, chunks);
                lookupAll(world: number, time: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                lookupAllTimes(world: number, from: number, to: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                private resolve_world(globalWorldOrder, nodeWorldOrder, timeToResolve, originWorld);
                private getOrLoadAndMarkAll(types, keys, callback);
                resolveState(node: org.mwg.Node): org.mwg.plugin.NodeState;
                private internal_resolveState(node, safe);
                alignState(node: org.mwg.Node): org.mwg.plugin.NodeState;
                newState(node: org.mwg.Node, world: number, time: number): org.mwg.plugin.NodeState;
                resolveTimepoints(node: org.mwg.Node, beginningOfSearch: number, endOfSearch: number, callback: org.mwg.Callback<Float64Array>): void;
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
                    class HeapChunkSpace implements org.mwg.chunk.ChunkSpace {
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
                        graph(): org.mwg.Graph;
                        worldByIndex(index: number): number;
                        timeByIndex(index: number): number;
                        idByIndex(index: number): number;
                        constructor(initialCapacity: number, p_graph: org.mwg.Graph);
                        getAndMark(type: number, world: number, time: number, id: number): org.mwg.chunk.Chunk;
                        get(index: number): org.mwg.chunk.Chunk;
                        getOrLoadAndMark(type: number, world: number, time: number, id: number, callback: org.mwg.Callback<org.mwg.chunk.Chunk>): void;
                        getOrLoadAndMarkAll(keys: Float64Array, callback: org.mwg.Callback<org.mwg.chunk.Chunk[]>): void;
                        mark(index: number): number;
                        unmark(index: number): void;
                        free(chunk: org.mwg.chunk.Chunk): void;
                        createAndMark(type: number, world: number, time: number, id: number): org.mwg.chunk.Chunk;
                        notifyUpdate(index: number): void;
                        save(callback: org.mwg.Callback<boolean>): void;
                        clear(): void;
                        freeAll(): void;
                        available(): number;
                        newVolatileGraph(): org.mwg.struct.EGraph;
                        printMarked(): void;
                    }
                    interface HeapContainer {
                        declareDirty(): void;
                    }
                    class HeapDMatrix implements org.mwg.struct.DMatrix {
                        private static INDEX_ROWS;
                        private static INDEX_COLUMNS;
                        private static INDEX_MAX_COLUMN;
                        private static INDEX_OFFSET;
                        private parent;
                        private backend;
                        private aligned;
                        constructor(p_parent: org.mwg.core.chunk.heap.HeapContainer, origin: org.mwg.core.chunk.heap.HeapDMatrix);
                        init(rows: number, columns: number): org.mwg.struct.DMatrix;
                        private internal_init(rows, columns);
                        appendColumn(newColumn: Float64Array): org.mwg.struct.DMatrix;
                        private internal_appendColumn(newColumn);
                        fill(value: number): org.mwg.struct.DMatrix;
                        private internal_fill(value);
                        fillWith(values: Float64Array): org.mwg.struct.DMatrix;
                        private internal_fillWith(values);
                        fillWithRandom(min: number, max: number, seed: number): org.mwg.struct.DMatrix;
                        private internal_fillWithRandom(min, max, seed);
                        rows(): number;
                        columns(): number;
                        column(index: number): Float64Array;
                        get(rowIndex: number, columnIndex: number): number;
                        set(rowIndex: number, columnIndex: number, value: number): org.mwg.struct.DMatrix;
                        private internal_set(rowIndex, columnIndex, value);
                        add(rowIndex: number, columnIndex: number, value: number): org.mwg.struct.DMatrix;
                        private internal_add(rowIndex, columnIndex, value);
                        data(): Float64Array;
                        leadingDimension(): number;
                        unsafeGet(index: number): number;
                        unsafeSet(index: number, value: number): org.mwg.struct.DMatrix;
                        private internal_unsafeSet(index, value);
                        unsafe_data(): Float64Array;
                        private unsafe_init(size);
                        private unsafe_set(index, value);
                        load(buffer: org.mwg.struct.Buffer, offset: number, max: number): number;
                    }
                    class HeapEGraph implements org.mwg.struct.EGraph {
                        private _graph;
                        private parent;
                        _dirty: boolean;
                        _nodes: org.mwg.core.chunk.heap.HeapENode[];
                        private _nodes_capacity;
                        private _nodes_index;
                        constructor(p_parent: org.mwg.core.chunk.heap.HeapContainer, origin: org.mwg.core.chunk.heap.HeapEGraph, p_graph: org.mwg.Graph);
                        size(): number;
                        free(): void;
                        graph(): org.mwg.Graph;
                        allocate(newCapacity: number): void;
                        nodeByIndex(index: number, createIfAbsent: boolean): org.mwg.core.chunk.heap.HeapENode;
                        declareDirty(): void;
                        newNode(): org.mwg.struct.ENode;
                        root(): org.mwg.struct.ENode;
                        setRoot(eNode: org.mwg.struct.ENode): org.mwg.struct.EGraph;
                        drop(eNode: org.mwg.struct.ENode): org.mwg.struct.EGraph;
                        toString(): string;
                        load(buffer: org.mwg.struct.Buffer, offset: number, max: number): number;
                    }
                    class HeapENode implements org.mwg.struct.ENode, org.mwg.core.chunk.heap.HeapContainer {
                        private egraph;
                        _id: number;
                        private _capacity;
                        private _size;
                        private _k;
                        private _v;
                        private _next;
                        private _hash;
                        private _type;
                        private _dirty;
                        private static LOAD_WAITING_ALLOC;
                        private static LOAD_WAITING_TYPE;
                        private static LOAD_WAITING_KEY;
                        private static LOAD_WAITING_VALUE;
                        constructor(p_egraph: org.mwg.core.chunk.heap.HeapEGraph, p_id: number, origin: org.mwg.core.chunk.heap.HeapENode);
                        clear(): org.mwg.struct.ENode;
                        declareDirty(): void;
                        rebase(): void;
                        private allocate(newCapacity);
                        private internal_find(p_key);
                        private internal_get(p_key);
                        private internal_set(p_key, p_type, p_unsafe_elem, replaceIfPresent, initial);
                        set(name: string, type: number, value: any): org.mwg.struct.ENode;
                        setAt(key: number, type: number, value: any): org.mwg.struct.ENode;
                        get(name: string): any;
                        getAt(key: number): any;
                        drop(): void;
                        graph(): org.mwg.struct.EGraph;
                        getOrCreate(key: string, type: number): any;
                        getOrCreateAt(key: number, type: number): any;
                        toString(): string;
                        save(buffer: org.mwg.struct.Buffer): void;
                        load(buffer: org.mwg.struct.Buffer, currentCursor: number, nodeParent: org.mwg.core.chunk.heap.HeapContainer, graph: org.mwg.Graph): number;
                        private load_primitive(read_key, read_type, buffer, previous, cursor, initial);
                        each(callBack: org.mwg.plugin.NodeStateCallback): void;
                    }
                    class HeapERelation implements org.mwg.struct.ERelation {
                        private _back;
                        private _size;
                        private _capacity;
                        private parent;
                        constructor(p_parent: org.mwg.core.chunk.heap.HeapContainer, origin: org.mwg.core.chunk.heap.HeapERelation);
                        rebase(newGraph: org.mwg.core.chunk.heap.HeapEGraph): void;
                        size(): number;
                        nodes(): org.mwg.struct.ENode[];
                        node(index: number): org.mwg.struct.ENode;
                        add(eNode: org.mwg.struct.ENode): org.mwg.struct.ERelation;
                        addAll(eNodes: org.mwg.struct.ENode[]): org.mwg.struct.ERelation;
                        clear(): org.mwg.struct.ERelation;
                        toString(): string;
                        allocate(newCapacity: number): void;
                    }
                    class HeapFixedStack implements org.mwg.chunk.Stack {
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
                    class HeapGenChunk implements org.mwg.chunk.GenChunk {
                        private _space;
                        private _index;
                        private _prefix;
                        private _seed;
                        private _dirty;
                        constructor(p_space: org.mwg.core.chunk.heap.HeapChunkSpace, p_id: number, p_index: number);
                        save(buffer: org.mwg.struct.Buffer): void;
                        saveDiff(buffer: org.mwg.struct.Buffer): void;
                        load(buffer: org.mwg.struct.Buffer): void;
                        loadDiff(buffer: org.mwg.struct.Buffer): void;
                        private internal_load(buffer, diff);
                        newKey(): number;
                        index(): number;
                        world(): number;
                        time(): number;
                        id(): number;
                        chunkType(): number;
                    }
                    class HeapLMatrix implements org.mwg.struct.LMatrix {
                        private static INDEX_ROWS;
                        private static INDEX_COLUMNS;
                        private static INDEX_MAX_COLUMN;
                        private static INDEX_OFFSET;
                        private parent;
                        private backend;
                        private aligned;
                        constructor(p_parent: org.mwg.core.chunk.heap.HeapContainer, origin: org.mwg.core.chunk.heap.HeapLMatrix);
                        init(rows: number, columns: number): org.mwg.struct.LMatrix;
                        private internal_init(rows, columns);
                        appendColumn(newColumn: Float64Array): org.mwg.struct.LMatrix;
                        private internal_appendColumn(newColumn);
                        fill(value: number): org.mwg.struct.LMatrix;
                        private internal_fill(value);
                        fillWith(values: Float64Array): org.mwg.struct.LMatrix;
                        private internal_fillWith(values);
                        fillWithRandom(min: number, max: number, seed: number): org.mwg.struct.LMatrix;
                        private internal_fillWithRandom(min, max, seed);
                        rows(): number;
                        columns(): number;
                        column(index: number): Float64Array;
                        get(rowIndex: number, columnIndex: number): number;
                        set(rowIndex: number, columnIndex: number, value: number): org.mwg.struct.LMatrix;
                        private internal_set(rowIndex, columnIndex, value);
                        add(rowIndex: number, columnIndex: number, value: number): org.mwg.struct.LMatrix;
                        private internal_add(rowIndex, columnIndex, value);
                        data(): Float64Array;
                        leadingDimension(): number;
                        unsafeGet(index: number): number;
                        unsafeSet(index: number, value: number): org.mwg.struct.LMatrix;
                        private internal_unsafeSet(index, value);
                        unsafe_data(): Float64Array;
                        unsafe_init(size: number): void;
                        unsafe_set(index: number, value: number): void;
                        load(buffer: org.mwg.struct.Buffer, offset: number, max: number): number;
                    }
                    class HeapLongLongArrayMap implements org.mwg.struct.LongLongArrayMap {
                        parent: org.mwg.core.chunk.heap.HeapContainer;
                        mapSize: number;
                        capacity: number;
                        keys: Float64Array;
                        values: Float64Array;
                        nexts: Int32Array;
                        hashs: Int32Array;
                        constructor(p_listener: org.mwg.core.chunk.heap.HeapContainer);
                        private key(i);
                        private setKey(i, newValue);
                        private value(i);
                        private setValue(i, newValue);
                        private next(i);
                        private setNext(i, newValue);
                        private hash(i);
                        private setHash(i, newValue);
                        reallocate(newCapacity: number): void;
                        cloneFor(newParent: org.mwg.core.chunk.heap.HeapContainer): org.mwg.core.chunk.heap.HeapLongLongArrayMap;
                        get(requestKey: number): Float64Array;
                        contains(requestKey: number, requestValue: number): boolean;
                        each(callback: org.mwg.struct.LongLongArrayMapCallBack): void;
                        unsafe_each(callback: org.mwg.struct.LongLongArrayMapCallBack): void;
                        size(): number;
                        delete(requestKey: number, requestValue: number): void;
                        put(insertKey: number, insertValue: number): void;
                        load(buffer: org.mwg.struct.Buffer, offset: number, max: number): number;
                    }
                    class HeapLongLongMap implements org.mwg.struct.LongLongMap {
                        private parent;
                        private mapSize;
                        private capacity;
                        private keys;
                        private values;
                        private nexts;
                        private hashs;
                        constructor(p_listener: org.mwg.core.chunk.heap.HeapContainer);
                        private key(i);
                        private setKey(i, newValue);
                        private value(i);
                        private setValue(i, newValue);
                        private next(i);
                        private setNext(i, newValue);
                        private hash(i);
                        private setHash(i, newValue);
                        reallocate(newCapacity: number): void;
                        cloneFor(newParent: org.mwg.core.chunk.heap.HeapContainer): org.mwg.core.chunk.heap.HeapLongLongMap;
                        get(requestKey: number): number;
                        each(callback: org.mwg.struct.LongLongMapCallBack): void;
                        unsafe_each(callback: org.mwg.struct.LongLongMapCallBack): void;
                        size(): number;
                        remove(requestKey: number): void;
                        put(insertKey: number, insertValue: number): void;
                        load(buffer: org.mwg.struct.Buffer, offset: number, max: number): number;
                    }
                    class HeapRelation implements org.mwg.struct.Relation {
                        private _back;
                        private _size;
                        private parent;
                        private aligned;
                        constructor(p_parent: org.mwg.core.chunk.heap.HeapContainer, origin: org.mwg.core.chunk.heap.HeapRelation);
                        allocate(_capacity: number): void;
                        all(): Float64Array;
                        size(): number;
                        get(index: number): number;
                        set(index: number, value: number): void;
                        unsafe_get(index: number): number;
                        addNode(node: org.mwg.Node): org.mwg.struct.Relation;
                        add(newValue: number): org.mwg.struct.Relation;
                        addAll(newValues: Float64Array): org.mwg.struct.Relation;
                        insert(targetIndex: number, newValue: number): org.mwg.struct.Relation;
                        remove(oldValue: number): org.mwg.struct.Relation;
                        delete(toRemoveIndex: number): org.mwg.struct.Relation;
                        clear(): org.mwg.struct.Relation;
                        toString(): string;
                        load(buffer: org.mwg.struct.Buffer, offset: number, max: number): number;
                    }
                    class HeapRelationIndexed extends org.mwg.core.chunk.heap.HeapLongLongArrayMap implements org.mwg.struct.RelationIndexed {
                        private _graph;
                        constructor(p_listener: org.mwg.core.chunk.heap.HeapContainer, graph: org.mwg.Graph);
                        add(node: org.mwg.Node, ...attributeNames: string[]): org.mwg.struct.RelationIndexed;
                        remove(node: org.mwg.Node, ...attributeNames: string[]): org.mwg.struct.RelationIndexed;
                        private internal_add_remove(isIndex, node, ...attributeNames);
                        clear(): org.mwg.struct.RelationIndexed;
                        find(callback: org.mwg.Callback<org.mwg.Node[]>, world: number, time: number, ...params: string[]): void;
                        findByQuery(query: org.mwg.Query, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                        all(): Float64Array;
                        cloneIRelFor(newParent: org.mwg.core.chunk.heap.HeapContainer, graph: org.mwg.Graph): org.mwg.core.chunk.heap.HeapRelationIndexed;
                    }
                    class HeapStateChunk implements org.mwg.chunk.StateChunk, org.mwg.core.chunk.heap.HeapContainer {
                        private _index;
                        private _space;
                        private _capacity;
                        private _size;
                        private _k;
                        private _v;
                        private _next;
                        private _hash;
                        private _type;
                        private _dirty;
                        private static LOAD_WAITING_ALLOC;
                        private static LOAD_WAITING_TYPE;
                        private static LOAD_WAITING_KEY;
                        private static LOAD_WAITING_VALUE;
                        graph(): org.mwg.Graph;
                        constructor(p_space: org.mwg.core.chunk.heap.HeapChunkSpace, p_index: number);
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
                        save(buffer: org.mwg.struct.Buffer): void;
                        saveDiff(buffer: org.mwg.struct.Buffer): void;
                        each(callBack: org.mwg.plugin.NodeStateCallback): void;
                        loadFrom(origin: org.mwg.chunk.StateChunk): void;
                        private internal_set(p_key, p_type, p_unsafe_elem, replaceIfPresent, initial);
                        private allocate(newCapacity);
                        load(buffer: org.mwg.struct.Buffer): void;
                        private load_primitive(read_key, read_type, buffer, previous, cursor, initial);
                        loadDiff(buffer: org.mwg.struct.Buffer): void;
                    }
                    class HeapStringIntMap implements org.mwg.struct.StringIntMap {
                        private parent;
                        private mapSize;
                        private capacity;
                        private keys;
                        private keysH;
                        private values;
                        private nexts;
                        private hashs;
                        constructor(p_parent: org.mwg.core.chunk.heap.HeapContainer);
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
                        cloneFor(newContainer: org.mwg.core.chunk.heap.HeapContainer): org.mwg.core.chunk.heap.HeapStringIntMap;
                        getValue(requestString: string): number;
                        getByHash(keyHash: number): string;
                        containsHash(keyHash: number): boolean;
                        each(callback: org.mwg.struct.StringLongMapCallBack): void;
                        unsafe_each(callback: org.mwg.struct.StringLongMapCallBack): void;
                        size(): number;
                        remove(requestKey: string): void;
                        put(insertKey: string, insertValue: number): void;
                        load(buffer: org.mwg.struct.Buffer, offset: number, max: number): number;
                    }
                    class HeapTimeTreeChunk implements org.mwg.chunk.TimeTreeChunk {
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
                        constructor(p_space: org.mwg.core.chunk.heap.HeapChunkSpace, p_index: number);
                        extra(): number;
                        setExtra(extraValue: number): void;
                        extra2(): number;
                        setExtra2(extraValue: number): void;
                        world(): number;
                        time(): number;
                        id(): number;
                        size(): number;
                        range(startKey: number, endKey: number, maxElements: number, walker: org.mwg.chunk.TreeWalker): void;
                        save(buffer: org.mwg.struct.Buffer): void;
                        saveDiff(buffer: org.mwg.struct.Buffer): void;
                        load(buffer: org.mwg.struct.Buffer): void;
                        loadDiff(buffer: org.mwg.struct.Buffer): void;
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
                    class HeapWorldOrderChunk implements org.mwg.chunk.WorldOrderChunk {
                        private _space;
                        private _index;
                        private _lock;
                        private _externalLock;
                        private _magic;
                        private _extra;
                        private _size;
                        private _capacity;
                        private _kv;
                        private _next;
                        private _diff;
                        private _hash;
                        private _dirty;
                        constructor(p_space: org.mwg.core.chunk.heap.HeapChunkSpace, p_index: number);
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
                        each(callback: org.mwg.struct.LongLongMapCallBack): void;
                        get(key: number): number;
                        put(key: number, value: number): void;
                        private internal_put(key, value, notifyUpdate);
                        private resize(newCapacity);
                        load(buffer: org.mwg.struct.Buffer): void;
                        loadDiff(buffer: org.mwg.struct.Buffer): void;
                        private internal_load(initial, buffer);
                        index(): number;
                        remove(key: number): void;
                        size(): number;
                        chunkType(): number;
                        save(buffer: org.mwg.struct.Buffer): void;
                        saveDiff(buffer: org.mwg.struct.Buffer): void;
                    }
                }
            }
            module memory {
                class HeapBuffer implements org.mwg.struct.Buffer {
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
                    iterator(): org.mwg.struct.BufferIterator;
                    removeLast(): void;
                    toString(): string;
                }
                class HeapMemoryFactory implements org.mwg.plugin.MemoryFactory {
                    newSpace(memorySize: number, graph: org.mwg.Graph): org.mwg.chunk.ChunkSpace;
                    newBuffer(): org.mwg.struct.Buffer;
                }
            }
            module scheduler {
                class JobQueue {
                    private first;
                    private last;
                    add(item: org.mwg.plugin.Job): void;
                    poll(): org.mwg.plugin.Job;
                }
                module JobQueue {
                    class JobQueueElem {
                        _ptr: org.mwg.plugin.Job;
                        _next: org.mwg.core.scheduler.JobQueue.JobQueueElem;
                        constructor(ptr: org.mwg.plugin.Job, next: org.mwg.core.scheduler.JobQueue.JobQueueElem);
                    }
                }
                class NoopScheduler implements org.mwg.plugin.Scheduler {
                    dispatch(affinity: number, job: org.mwg.plugin.Job): void;
                    start(): void;
                    stop(): void;
                    workers(): number;
                }
                class TrampolineScheduler implements org.mwg.plugin.Scheduler {
                    private queue;
                    private wip;
                    dispatch(affinity: number, job: org.mwg.plugin.Job): void;
                    start(): void;
                    stop(): void;
                    workers(): number;
                }
            }
            module task {
                class ActionAddRemoveToGlobalIndex implements org.mwg.task.Action {
                    private _name;
                    private _attributes;
                    private _timed;
                    private _remove;
                    constructor(remove: boolean, timed: boolean, name: string, ...attributes: string[]);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionAddRemoveVarToRelation implements org.mwg.task.Action {
                    private _name;
                    private _varFrom;
                    private _attributes;
                    private _isAdd;
                    constructor(isAdd: boolean, name: string, varFrom: string, ...attributes: string[]);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionAddToVar implements org.mwg.task.Action {
                    private _name;
                    constructor(p_name: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionAttributes implements org.mwg.task.Action {
                    private _filter;
                    constructor(filterType: number);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionClearResult implements org.mwg.task.Action {
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionCreateNode implements org.mwg.task.Action {
                    private _typeNode;
                    constructor(typeNode: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionDeclareVar implements org.mwg.task.Action {
                    private _name;
                    private _isGlobal;
                    constructor(isGlobal: boolean, p_name: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionDefineAsVar implements org.mwg.task.Action {
                    private _name;
                    private _global;
                    constructor(p_name: string, p_global: boolean);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionExecuteExpression implements org.mwg.task.Action {
                    private _engine;
                    private _expression;
                    constructor(mathExpression: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionFlat implements org.mwg.task.Action {
                    constructor();
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionFlipVar implements org.mwg.task.Action {
                    private _name;
                    constructor(name: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionIndexNames implements org.mwg.task.Action {
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionInject implements org.mwg.task.Action {
                    private _value;
                    constructor(value: any);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionLookup implements org.mwg.task.Action {
                    private _id;
                    constructor(p_id: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionLookupAll implements org.mwg.task.Action {
                    private _ids;
                    constructor(p_ids: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionNamed implements org.mwg.task.Action {
                    private _name;
                    private _params;
                    constructor(name: string, ...params: string[]);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionNames {
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
                    static PRINT: string;
                    static PRINTLN: string;
                    static READ_GLOBAL_INDEX: string;
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
                    static DO_WHILE: string;
                    static WHILE_DO: string;
                    static ISOLATE: string;
                    static IF_THEN: string;
                    static IF_THEN_ELSE: string;
                    static ATOMIC: string;
                    static FLAT: string;
                }
                class ActionPrint implements org.mwg.task.Action {
                    private _name;
                    private _withLineBreak;
                    constructor(p_name: string, withLineBreak: boolean);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionReadGlobalIndex implements org.mwg.task.Action {
                    private _name;
                    private _params;
                    constructor(p_indexName: string, ...p_query: string[]);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionReadVar implements org.mwg.task.Action {
                    private _origin;
                    private _name;
                    private _index;
                    constructor(p_name: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionRemove implements org.mwg.task.Action {
                    private _name;
                    constructor(name: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionSave implements org.mwg.task.Action {
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionScript implements org.mwg.task.Action {
                    private _script;
                    private _async;
                    constructor(script: string, async: boolean);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionSelect implements org.mwg.task.Action {
                    private _script;
                    private _filter;
                    constructor(script: string, filter: org.mwg.task.TaskFunctionSelect);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    private callScript(node, context);
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionSelectObject implements org.mwg.task.Action {
                    private _filter;
                    constructor(filterFunction: org.mwg.task.TaskFunctionSelectObject);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionSetAsVar implements org.mwg.task.Action {
                    private _name;
                    constructor(p_name: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionSetAttribute implements org.mwg.task.Action {
                    private _name;
                    private _value;
                    private _propertyType;
                    private _force;
                    constructor(name: string, propertyType: number, value: string, force: boolean);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    private loadArray(valueAfterTemplate, type);
                    private parseBoolean(booleanValue);
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionTimepoints implements org.mwg.task.Action {
                    private _from;
                    private _to;
                    constructor(from: string, to: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionTravelInTime implements org.mwg.task.Action {
                    private _time;
                    constructor(time: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionTravelInWorld implements org.mwg.task.Action {
                    private _world;
                    constructor(world: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionTraverseOrAttribute implements org.mwg.task.Action {
                    private _name;
                    private _params;
                    private _isAttribute;
                    private _isUnknown;
                    constructor(isAttribute: boolean, isUnknown: boolean, p_name: string, ...p_params: string[]);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionWith implements org.mwg.task.Action {
                    private _patternTemplate;
                    private _name;
                    constructor(name: string, stringPattern: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class ActionWithout implements org.mwg.task.Action {
                    private _patternTemplate;
                    private _name;
                    constructor(name: string, stringPattern: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class Actions {
                    static flat(): org.mwg.task.Action;
                    static travelInWorld(world: string): org.mwg.task.Action;
                    static travelInTime(time: string): org.mwg.task.Action;
                    static inject(input: any): org.mwg.task.Action;
                    static defineAsGlobalVar(name: string): org.mwg.task.Action;
                    static defineAsVar(name: string): org.mwg.task.Action;
                    static declareGlobalVar(name: string): org.mwg.task.Action;
                    static declareVar(name: string): org.mwg.task.Action;
                    static readVar(name: string): org.mwg.task.Action;
                    static flipVar(name: string): org.mwg.task.Action;
                    static setAsVar(name: string): org.mwg.task.Action;
                    static addToVar(name: string): org.mwg.task.Action;
                    static setAttribute(name: string, type: number, value: string): org.mwg.task.Action;
                    static forceAttribute(name: string, type: number, value: string): org.mwg.task.Action;
                    static remove(name: string): org.mwg.task.Action;
                    static attributes(): org.mwg.task.Action;
                    static attributesWithTypes(filterType: number): org.mwg.task.Action;
                    static addVarToRelation(relName: string, varName: string, ...attributes: string[]): org.mwg.task.Action;
                    static removeVarFromRelation(relName: string, varFrom: string, ...attributes: string[]): org.mwg.task.Action;
                    static traverse(name: string, ...params: string[]): org.mwg.task.Action;
                    static attribute(name: string, ...params: string[]): org.mwg.task.Action;
                    static readGlobalIndex(indexName: string, ...query: string[]): org.mwg.task.Action;
                    static addToGlobalIndex(name: string, ...attributes: string[]): org.mwg.task.Action;
                    static addToGlobalTimedIndex(name: string, ...attributes: string[]): org.mwg.task.Action;
                    static removeFromGlobalIndex(name: string, ...attributes: string[]): org.mwg.task.Action;
                    static removeFromGlobalTimedIndex(name: string, ...attributes: string[]): org.mwg.task.Action;
                    static indexNames(): org.mwg.task.Action;
                    static selectWith(name: string, pattern: string): org.mwg.task.Action;
                    static selectWithout(name: string, pattern: string): org.mwg.task.Action;
                    static select(filterFunction: org.mwg.task.TaskFunctionSelect): org.mwg.task.Action;
                    static selectObject(filterFunction: org.mwg.task.TaskFunctionSelectObject): org.mwg.task.Action;
                    static selectScript(script: string): org.mwg.task.Action;
                    static print(name: string): org.mwg.task.Action;
                    static println(name: string): org.mwg.task.Action;
                    static executeExpression(expression: string): org.mwg.task.Action;
                    static action(name: string, ...params: string[]): org.mwg.task.Action;
                    static createNode(): org.mwg.task.Action;
                    static createTypedNode(type: string): org.mwg.task.Action;
                    static save(): org.mwg.task.Action;
                    static script(script: string): org.mwg.task.Action;
                    static asyncScript(script: string): org.mwg.task.Action;
                    static lookup(nodeId: string): org.mwg.task.Action;
                    static lookupAll(nodeIds: string): org.mwg.task.Action;
                    static timepoints(from: string, to: string): org.mwg.task.Action;
                    static clearResult(): org.mwg.task.Action;
                    static cond(mathExpression: string): org.mwg.task.ConditionalFunction;
                    static newTask(): org.mwg.task.Task;
                    static emptyResult(): org.mwg.task.TaskResult<any>;
                    static then(action: org.mwg.task.Action): org.mwg.task.Task;
                    static thenDo(actionFunction: org.mwg.task.ActionFunction): org.mwg.task.Task;
                    static loop(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task;
                    static loopPar(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task;
                    static forEach(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    static forEachPar(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    static map(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    static mapPar(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    static ifThen(cond: org.mwg.task.ConditionalFunction, then: org.mwg.task.Task): org.mwg.task.Task;
                    static ifThenScript(condScript: string, then: org.mwg.task.Task): org.mwg.task.Task;
                    static ifThenElse(cond: org.mwg.task.ConditionalFunction, thenSub: org.mwg.task.Task, elseSub: org.mwg.task.Task): org.mwg.task.Task;
                    static ifThenElseScript(condScript: string, thenSub: org.mwg.task.Task, elseSub: org.mwg.task.Task): org.mwg.task.Task;
                    static doWhile(task: org.mwg.task.Task, cond: org.mwg.task.ConditionalFunction): org.mwg.task.Task;
                    static doWhileScript(task: org.mwg.task.Task, condScript: string): org.mwg.task.Task;
                    static whileDo(cond: org.mwg.task.ConditionalFunction, task: org.mwg.task.Task): org.mwg.task.Task;
                    static whileDoScript(condScript: string, task: org.mwg.task.Task): org.mwg.task.Task;
                    static pipe(...subTasks: org.mwg.task.Task[]): org.mwg.task.Task;
                    static pipePar(...subTasks: org.mwg.task.Task[]): org.mwg.task.Task;
                    static isolate(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    static atomic(protectedTask: org.mwg.task.Task, ...variablesToLock: string[]): org.mwg.task.Task;
                    static parse(flat: string, graph: org.mwg.Graph): org.mwg.task.Task;
                }
                abstract class CF_Action implements org.mwg.task.Action {
                    abstract children(): org.mwg.task.Task[];
                    abstract cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                    abstract eval(ctx: org.mwg.task.TaskContext): void;
                    serialize(builder: java.lang.StringBuilder): void;
                    toString(): string;
                }
                class CF_ActionAtomic extends org.mwg.core.task.CF_Action {
                    private _variables;
                    private _subTask;
                    constructor(p_subTask: org.mwg.task.Task, ...variables: string[]);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    children(): org.mwg.task.Task[];
                    cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                }
                class CF_ActionDoWhile extends org.mwg.core.task.CF_Action {
                    private _cond;
                    private _then;
                    private _conditionalScript;
                    constructor(p_then: org.mwg.task.Task, p_cond: org.mwg.task.ConditionalFunction, conditionalScript: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    children(): org.mwg.task.Task[];
                    cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                }
                class CF_ActionForEach extends org.mwg.core.task.CF_Action {
                    private _subTask;
                    constructor(p_subTask: org.mwg.task.Task);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    children(): org.mwg.task.Task[];
                    cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                }
                class CF_ActionForEachPar extends org.mwg.core.task.CF_Action {
                    private _subTask;
                    constructor(p_subTask: org.mwg.task.Task);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    children(): org.mwg.task.Task[];
                    cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                }
                class CF_ActionIfThen extends org.mwg.core.task.CF_Action {
                    private _condition;
                    private _action;
                    private _conditionalScript;
                    constructor(cond: org.mwg.task.ConditionalFunction, action: org.mwg.task.Task, conditionalScript: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    children(): org.mwg.task.Task[];
                    cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                }
                class CF_ActionIfThenElse extends org.mwg.core.task.CF_Action {
                    private _condition;
                    private _thenSub;
                    private _elseSub;
                    private _conditionalScript;
                    constructor(cond: org.mwg.task.ConditionalFunction, p_thenSub: org.mwg.task.Task, p_elseSub: org.mwg.task.Task, conditionalScript: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    children(): org.mwg.task.Task[];
                    cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                }
                class CF_ActionIsolate extends org.mwg.core.task.CF_Action {
                    private _subTask;
                    constructor(p_subTask: org.mwg.task.Task);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    children(): org.mwg.task.Task[];
                    cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                }
                class CF_ActionLoop extends org.mwg.core.task.CF_Action {
                    private _lower;
                    private _upper;
                    private _subTask;
                    constructor(p_lower: string, p_upper: string, p_subTask: org.mwg.task.Task);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    children(): org.mwg.task.Task[];
                    cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                }
                class CF_ActionLoopPar extends org.mwg.core.task.CF_Action {
                    private _subTask;
                    private _lower;
                    private _upper;
                    constructor(p_lower: string, p_upper: string, p_subTask: org.mwg.task.Task);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    children(): org.mwg.task.Task[];
                    cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                }
                class CF_ActionMap extends org.mwg.core.task.CF_Action {
                    private _subTask;
                    constructor(p_subTask: org.mwg.task.Task);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    children(): org.mwg.task.Task[];
                    cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                }
                class CF_ActionMapPar extends org.mwg.core.task.CF_Action {
                    private _subTask;
                    constructor(p_subTask: org.mwg.task.Task);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    children(): org.mwg.task.Task[];
                    cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                }
                class CF_ActionPipe extends org.mwg.core.task.CF_Action {
                    private _subTasks;
                    constructor(...p_subTasks: org.mwg.task.Task[]);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    children(): org.mwg.task.Task[];
                    cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                }
                class CF_ActionPipePar extends org.mwg.core.task.CF_Action {
                    private _subTasks;
                    constructor(...p_subTasks: org.mwg.task.Task[]);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    children(): org.mwg.task.Task[];
                    cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                }
                class CF_ActionThenDo implements org.mwg.task.Action {
                    private _wrapped;
                    constructor(p_wrapped: org.mwg.task.ActionFunction);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    toString(): string;
                    serialize(builder: java.lang.StringBuilder): void;
                }
                class CF_ActionWhileDo extends org.mwg.core.task.CF_Action {
                    private _cond;
                    private _then;
                    private _conditionalScript;
                    constructor(p_cond: org.mwg.task.ConditionalFunction, p_then: org.mwg.task.Task, conditionalScript: string);
                    eval(ctx: org.mwg.task.TaskContext): void;
                    children(): org.mwg.task.Task[];
                    cf_serialize(builder: java.lang.StringBuilder, dagIDS: java.util.Map<number, number>): void;
                }
                class CoreTask implements org.mwg.task.Task {
                    private insertCapacity;
                    actions: org.mwg.task.Action[];
                    insertCursor: number;
                    _hooks: org.mwg.task.TaskHook[];
                    addHook(p_hook: org.mwg.task.TaskHook): org.mwg.task.Task;
                    then(nextAction: org.mwg.task.Action): org.mwg.task.Task;
                    thenDo(nextActionFunction: org.mwg.task.ActionFunction): org.mwg.task.Task;
                    doWhile(task: org.mwg.task.Task, cond: org.mwg.task.ConditionalFunction): org.mwg.task.Task;
                    doWhileScript(task: org.mwg.task.Task, condScript: string): org.mwg.task.Task;
                    loop(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task;
                    loopPar(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task;
                    forEach(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    forEachPar(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    map(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    mapPar(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    ifThen(cond: org.mwg.task.ConditionalFunction, then: org.mwg.task.Task): org.mwg.task.Task;
                    ifThenScript(condScript: string, then: org.mwg.task.Task): org.mwg.task.Task;
                    ifThenElse(cond: org.mwg.task.ConditionalFunction, thenSub: org.mwg.task.Task, elseSub: org.mwg.task.Task): org.mwg.task.Task;
                    ifThenElseScript(condScript: string, thenSub: org.mwg.task.Task, elseSub: org.mwg.task.Task): org.mwg.task.Task;
                    whileDo(cond: org.mwg.task.ConditionalFunction, task: org.mwg.task.Task): org.mwg.task.Task;
                    whileDoScript(condScript: string, task: org.mwg.task.Task): org.mwg.task.Task;
                    pipe(...subTasks: org.mwg.task.Task[]): org.mwg.task.Task;
                    pipePar(...subTasks: org.mwg.task.Task[]): org.mwg.task.Task;
                    isolate(subTask: org.mwg.task.Task): org.mwg.task.Task;
                    atomic(protectedTask: org.mwg.task.Task, ...variablesToLock: string[]): org.mwg.task.Task;
                    execute(graph: org.mwg.Graph, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                    executeSync(graph: org.mwg.Graph): org.mwg.task.TaskResult<any>;
                    executeWith(graph: org.mwg.Graph, initial: any, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                    prepare(graph: org.mwg.Graph, initial: any, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): org.mwg.task.TaskContext;
                    executeUsing(preparedContext: org.mwg.task.TaskContext): void;
                    executeFrom(parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, affinity: number, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                    executeFromUsing(parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, affinity: number, contextInitializer: org.mwg.Callback<org.mwg.task.TaskContext>, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                    loadFromBuffer(buffer: org.mwg.struct.Buffer, graph: org.mwg.Graph): org.mwg.task.Task;
                    saveToBuffer(buffer: org.mwg.struct.Buffer): org.mwg.task.Task;
                    parse(flat: string, graph: org.mwg.Graph): org.mwg.task.Task;
                    private sub_parse(reader, graph, contextTasks);
                    private static condFromScript(script);
                    private static executeScript(script, context);
                    static fillDefault(registry: java.util.Map<string, org.mwg.task.TaskActionFactory>): void;
                    private static getOrCreate(contextTasks, param);
                    hashCode(): number;
                    toString(): string;
                    serialize(builder: java.lang.StringBuilder, dagCounters: java.util.Map<number, number>): void;
                    private static deep_analyze(t, counters, dagCollector);
                    travelInWorld(world: string): org.mwg.task.Task;
                    travelInTime(time: string): org.mwg.task.Task;
                    inject(input: any): org.mwg.task.Task;
                    defineAsGlobalVar(name: string): org.mwg.task.Task;
                    defineAsVar(name: string): org.mwg.task.Task;
                    declareGlobalVar(name: string): org.mwg.task.Task;
                    declareVar(name: string): org.mwg.task.Task;
                    readVar(name: string): org.mwg.task.Task;
                    setAsVar(name: string): org.mwg.task.Task;
                    addToVar(name: string): org.mwg.task.Task;
                    setAttribute(name: string, type: number, value: string): org.mwg.task.Task;
                    forceAttribute(name: string, type: number, value: string): org.mwg.task.Task;
                    remove(name: string): org.mwg.task.Task;
                    attributes(): org.mwg.task.Task;
                    timepoints(from: string, to: string): org.mwg.task.Task;
                    attributesWithType(filterType: number): org.mwg.task.Task;
                    addVarToRelation(relName: string, varName: string, ...attributes: string[]): org.mwg.task.Task;
                    removeVarFromRelation(relName: string, varFrom: string, ...attributes: string[]): org.mwg.task.Task;
                    traverse(name: string, ...params: string[]): org.mwg.task.Task;
                    attribute(name: string, ...params: string[]): org.mwg.task.Task;
                    readGlobalIndex(name: string, ...query: string[]): org.mwg.task.Task;
                    addToGlobalIndex(name: string, ...attributes: string[]): org.mwg.task.Task;
                    addToGlobalTimedIndex(name: string, ...attributes: string[]): org.mwg.task.Task;
                    removeFromGlobalIndex(name: string, ...attributes: string[]): org.mwg.task.Task;
                    removeFromGlobalTimedIndex(name: string, ...attributes: string[]): org.mwg.task.Task;
                    indexNames(): org.mwg.task.Task;
                    selectWith(name: string, pattern: string): org.mwg.task.Task;
                    selectWithout(name: string, pattern: string): org.mwg.task.Task;
                    select(filterFunction: org.mwg.task.TaskFunctionSelect): org.mwg.task.Task;
                    selectObject(filterFunction: org.mwg.task.TaskFunctionSelectObject): org.mwg.task.Task;
                    selectScript(script: string): org.mwg.task.Task;
                    print(name: string): org.mwg.task.Task;
                    println(name: string): org.mwg.task.Task;
                    executeExpression(expression: string): org.mwg.task.Task;
                    createNode(): org.mwg.task.Task;
                    createTypedNode(type: string): org.mwg.task.Task;
                    save(): org.mwg.task.Task;
                    script(script: string): org.mwg.task.Task;
                    asyncScript(ascript: string): org.mwg.task.Task;
                    lookup(nodeId: string): org.mwg.task.Task;
                    lookupAll(nodeIds: string): org.mwg.task.Task;
                    clearResult(): org.mwg.task.Task;
                    action(name: string, ...params: string[]): org.mwg.task.Task;
                    flipVar(name: string): org.mwg.task.Task;
                    flat(): org.mwg.task.Task;
                }
                class CoreTaskContext implements org.mwg.task.TaskContext {
                    private _globalVariables;
                    private _parent;
                    private _graph;
                    _callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>;
                    private _localVariables;
                    private _nextVariables;
                    _result: org.mwg.task.TaskResult<any>;
                    private _world;
                    private _time;
                    private _origin;
                    private cursor;
                    _hooks: org.mwg.task.TaskHook[];
                    private _output;
                    constructor(origin: org.mwg.core.task.CoreTask, p_hooks: org.mwg.task.TaskHook[], parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, p_graph: org.mwg.Graph, p_callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>);
                    graph(): org.mwg.Graph;
                    world(): number;
                    setWorld(p_world: number): org.mwg.task.TaskContext;
                    time(): number;
                    setTime(p_time: number): org.mwg.task.TaskContext;
                    variables(): org.mwg.utility.Tuple<string, org.mwg.task.TaskResult<any>>[];
                    private recursive_collect(ctx, collector);
                    variable(name: string): org.mwg.task.TaskResult<any>;
                    isGlobal(name: string): boolean;
                    private internal_deep_resolve(name);
                    wrap(input: any): org.mwg.task.TaskResult<any>;
                    wrapClone(input: any): org.mwg.task.TaskResult<any>;
                    newResult(): org.mwg.task.TaskResult<any>;
                    declareVariable(name: string): org.mwg.task.TaskContext;
                    private lazyWrap(input);
                    defineVariable(name: string, initialResult: any): org.mwg.task.TaskContext;
                    defineVariableForSubTask(name: string, initialResult: any): org.mwg.task.TaskContext;
                    setGlobalVariable(name: string, value: any): org.mwg.task.TaskContext;
                    setVariable(name: string, value: any): org.mwg.task.TaskContext;
                    private internal_deep_resolve_map(name);
                    addToGlobalVariable(name: string, value: any): org.mwg.task.TaskContext;
                    addToVariable(name: string, value: any): org.mwg.task.TaskContext;
                    globalVariables(): java.util.Map<string, org.mwg.task.TaskResult<any>>;
                    localVariables(): java.util.Map<string, org.mwg.task.TaskResult<any>>;
                    result(): org.mwg.task.TaskResult<any>;
                    resultAsNodes(): org.mwg.task.TaskResult<org.mwg.Node>;
                    resultAsStrings(): org.mwg.task.TaskResult<string>;
                    continueWith(nextResult: org.mwg.task.TaskResult<any>): void;
                    continueTask(): void;
                    endTask(preFinalResult: org.mwg.task.TaskResult<any>, e: Error): void;
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
                    slice(cursor: number): org.mwg.core.task.CoreTaskReader;
                }
                class CoreTaskResult<A> implements org.mwg.task.TaskResult<A> {
                    private _backend;
                    private _capacity;
                    private _size;
                    _exception: Error;
                    _output: string;
                    asArray(): any[];
                    exception(): Error;
                    output(): string;
                    setException(e: Error): org.mwg.task.TaskResult<A>;
                    setOutput(output: string): org.mwg.task.TaskResult<A>;
                    fillWith(source: org.mwg.task.TaskResult<A>): org.mwg.task.TaskResult<A>;
                    constructor(toWrap: any, protect: boolean);
                    iterator(): org.mwg.task.TaskResultIterator<any>;
                    get(index: number): A;
                    set(index: number, input: A): org.mwg.task.TaskResult<A>;
                    allocate(index: number): org.mwg.task.TaskResult<A>;
                    add(input: A): org.mwg.task.TaskResult<A>;
                    clear(): org.mwg.task.TaskResult<A>;
                    clone(): org.mwg.task.TaskResult<A>;
                    free(): void;
                    size(): number;
                    private extendTil(index);
                    toString(): string;
                    private toJson(withContent);
                }
                class CoreTaskResultIterator<A> implements org.mwg.task.TaskResultIterator<A> {
                    private _backend;
                    private _size;
                    private _current;
                    constructor(p_backend: any[]);
                    next(): A;
                    nextWithIndex(): org.mwg.utility.Tuple<number, A>;
                }
                class TaskHelper {
                    static flatNodes(toFLat: any, strict: boolean): org.mwg.Node[];
                    static parseInt(s: string): number;
                    static serializeString(param: string, builder: java.lang.StringBuilder, singleQuote: boolean): void;
                    static serializeType(type: number, builder: java.lang.StringBuilder): void;
                    static serializeStringParams(params: string[], builder: java.lang.StringBuilder): void;
                    static serializeNameAndStringParams(name: string, params: string[], builder: java.lang.StringBuilder): void;
                }
                module math {
                    class CoreMathExpressionEngine implements org.mwg.core.task.math.MathExpressionEngine {
                        static decimalSeparator: string;
                        static minusSign: string;
                        private _cacheAST;
                        constructor(expression: string);
                        static parse(p_expression: string): org.mwg.core.task.math.MathExpressionEngine;
                        static isNumber(st: string): boolean;
                        static isDigit(c: string): boolean;
                        static isLetter(c: string): boolean;
                        static isWhitespace(c: string): boolean;
                        private shuntingYard(expression);
                        eval(context: org.mwg.Node, taskContext: org.mwg.task.TaskContext, variables: java.util.Map<string, number>): number;
                        private buildAST(rpn);
                        private parseDouble(val);
                        private parseInt(val);
                    }
                    class MathConditional {
                        private _engine;
                        private _expression;
                        constructor(mathExpression: string);
                        conditional(): org.mwg.task.ConditionalFunction;
                        toString(): string;
                    }
                    class MathDoubleToken implements org.mwg.core.task.math.MathToken {
                        private _content;
                        constructor(_content: number);
                        type(): number;
                        content(): number;
                    }
                    class MathEntities {
                        private static INSTANCE;
                        operators: java.util.HashMap<string, org.mwg.core.task.math.MathOperation>;
                        functions: java.util.HashMap<string, org.mwg.core.task.math.MathFunction>;
                        static getINSTANCE(): org.mwg.core.task.math.MathEntities;
                        constructor();
                    }
                    interface MathExpressionEngine {
                        eval(context: org.mwg.Node, taskContext: org.mwg.task.TaskContext, variables: java.util.Map<string, number>): number;
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
                    class MathFreeToken implements org.mwg.core.task.math.MathToken {
                        private _content;
                        constructor(content: string);
                        content(): string;
                        type(): number;
                    }
                    class MathFunction implements org.mwg.core.task.math.MathToken {
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
                    class MathOperation implements org.mwg.core.task.math.MathToken {
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
                class CoreDeferCounter implements org.mwg.DeferCounter {
                    private _nb_down;
                    private _counter;
                    private _end;
                    constructor(nb: number);
                    count(): void;
                    getCount(): number;
                    then(p_callback: org.mwg.plugin.Job): void;
                    wrap(): org.mwg.Callback<any>;
                }
                class CoreDeferCounterSync implements org.mwg.DeferCounterSync {
                    private _nb_down;
                    private _counter;
                    private _end;
                    private _result;
                    constructor(nb: number);
                    count(): void;
                    getCount(): number;
                    then(p_callback: org.mwg.plugin.Job): void;
                    wrap(): org.mwg.Callback<any>;
                    waitResult(): any;
                }
                class ReadOnlyStorage implements org.mwg.plugin.Storage {
                    private wrapped;
                    constructor(toWrap: org.mwg.plugin.Storage);
                    get(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
                    put(stream: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                    remove(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                    connect(graph: org.mwg.Graph, callback: org.mwg.Callback<boolean>): void;
                    disconnect(callback: org.mwg.Callback<boolean>): void;
                    lock(callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
                    unlock(previousLock: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                }
            }
        }
        module plugin {
            interface Job {
                (): void;
            }
            interface MemoryFactory {
                newSpace(memorySize: number, graph: org.mwg.Graph): org.mwg.chunk.ChunkSpace;
                newBuffer(): org.mwg.struct.Buffer;
            }
            interface NodeFactory {
                (world: number, time: number, id: number, graph: org.mwg.Graph): org.mwg.Node;
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
                each(callBack: org.mwg.plugin.NodeStateCallback): void;
            }
            interface NodeStateCallback {
                (attributeKey: number, elemType: number, elem: any): void;
            }
            interface Plugin {
                declareNodeType(name: string, factory: org.mwg.plugin.NodeFactory): org.mwg.plugin.Plugin;
                declareTaskAction(name: string, factory: org.mwg.task.TaskActionFactory): org.mwg.plugin.Plugin;
                declareMemoryFactory(factory: org.mwg.plugin.MemoryFactory): org.mwg.plugin.Plugin;
                declareTaskHook(hook: org.mwg.task.TaskHook): org.mwg.plugin.Plugin;
                declareResolverFactory(factory: org.mwg.plugin.ResolverFactory): org.mwg.plugin.Plugin;
                nodeTypes(): string[];
                nodeType(nodeTypeName: string): org.mwg.plugin.NodeFactory;
                taskActionTypes(): string[];
                taskActionType(taskTypeName: string): org.mwg.task.TaskActionFactory;
                taskHooks(): org.mwg.task.TaskHook[];
                memoryFactory(): org.mwg.plugin.MemoryFactory;
                resolverFactory(): org.mwg.plugin.ResolverFactory;
                stop(): void;
            }
            interface Resolver {
                init(): void;
                initNode(node: org.mwg.Node, typeCode: number): void;
                initWorld(parentWorld: number, childWorld: number): void;
                freeNode(node: org.mwg.Node): void;
                typeName(node: org.mwg.Node): string;
                typeCode(node: org.mwg.Node): number;
                lookup<A extends org.mwg.Node>(world: number, time: number, id: number, callback: org.mwg.Callback<A>): void;
                lookupBatch(worlds: Float64Array, times: Float64Array, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                lookupTimes(world: number, from: number, to: number, id: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                lookupAll(world: number, time: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                lookupAllTimes(world: number, from: number, to: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                resolveState(node: org.mwg.Node): org.mwg.plugin.NodeState;
                alignState(node: org.mwg.Node): org.mwg.plugin.NodeState;
                newState(node: org.mwg.Node, world: number, time: number): org.mwg.plugin.NodeState;
                resolveTimepoints(node: org.mwg.Node, beginningOfSearch: number, endOfSearch: number, callback: org.mwg.Callback<Float64Array>): void;
                stringToHash(name: string, insertIfNotExists: boolean): number;
                hashToString(key: number): string;
                externalLock(node: org.mwg.Node): void;
                externalUnlock(node: org.mwg.Node): void;
                setTimeSensitivity(node: org.mwg.Node, deltaTime: number, delta: number): void;
                getTimeSensitivity(node: org.mwg.Node): Float64Array;
            }
            interface ResolverFactory {
                newResolver(storage: org.mwg.plugin.Storage, space: org.mwg.chunk.ChunkSpace): org.mwg.plugin.Resolver;
            }
            interface Scheduler {
                dispatch(affinity: number, job: org.mwg.plugin.Job): void;
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
                get(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
                put(stream: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                remove(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                connect(graph: org.mwg.Graph, callback: org.mwg.Callback<boolean>): void;
                lock(callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
                unlock(previousLock: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
                disconnect(callback: org.mwg.Callback<boolean>): void;
            }
            interface TaskExecutor {
                executeTasks(callback: org.mwg.Callback<string[]>, ...tasks: org.mwg.task.Task[]): void;
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
                iterator(): org.mwg.struct.BufferIterator;
                removeLast(): void;
                slice(initPos: number, endPos: number): Int8Array;
            }
            interface BufferIterator {
                hasNext(): boolean;
                next(): org.mwg.struct.Buffer;
            }
            interface DMatrix {
                init(rows: number, columns: number): org.mwg.struct.DMatrix;
                fill(value: number): org.mwg.struct.DMatrix;
                fillWith(values: Float64Array): org.mwg.struct.DMatrix;
                fillWithRandom(min: number, max: number, seed: number): org.mwg.struct.DMatrix;
                rows(): number;
                columns(): number;
                column(i: number): Float64Array;
                get(rowIndex: number, columnIndex: number): number;
                set(rowIndex: number, columnIndex: number, value: number): org.mwg.struct.DMatrix;
                add(rowIndex: number, columnIndex: number, value: number): org.mwg.struct.DMatrix;
                appendColumn(newColumn: Float64Array): org.mwg.struct.DMatrix;
                data(): Float64Array;
                leadingDimension(): number;
                unsafeGet(index: number): number;
                unsafeSet(index: number, value: number): org.mwg.struct.DMatrix;
            }
            interface EGraph {
                root(): org.mwg.struct.ENode;
                newNode(): org.mwg.struct.ENode;
                setRoot(eNode: org.mwg.struct.ENode): org.mwg.struct.EGraph;
                drop(eNode: org.mwg.struct.ENode): org.mwg.struct.EGraph;
                size(): number;
                free(): void;
                graph(): org.mwg.Graph;
            }
            interface ENode {
                set(name: string, type: number, value: any): org.mwg.struct.ENode;
                setAt(key: number, type: number, value: any): org.mwg.struct.ENode;
                get(name: string): any;
                getAt(key: number): any;
                getOrCreate(key: string, type: number): any;
                getOrCreateAt(key: number, type: number): any;
                drop(): void;
                graph(): org.mwg.struct.EGraph;
                each(callBack: org.mwg.plugin.NodeStateCallback): void;
                clear(): org.mwg.struct.ENode;
            }
            interface ERelation {
                nodes(): org.mwg.struct.ENode[];
                node(index: number): org.mwg.struct.ENode;
                size(): number;
                add(eNode: org.mwg.struct.ENode): org.mwg.struct.ERelation;
                addAll(eNodes: org.mwg.struct.ENode[]): org.mwg.struct.ERelation;
                clear(): org.mwg.struct.ERelation;
            }
            interface LMatrix {
                init(rows: number, columns: number): org.mwg.struct.LMatrix;
                fill(value: number): org.mwg.struct.LMatrix;
                fillWith(values: Float64Array): org.mwg.struct.LMatrix;
                fillWithRandom(min: number, max: number, seed: number): org.mwg.struct.LMatrix;
                rows(): number;
                columns(): number;
                column(i: number): Float64Array;
                get(rowIndex: number, columnIndex: number): number;
                set(rowIndex: number, columnIndex: number, value: number): org.mwg.struct.LMatrix;
                add(rowIndex: number, columnIndex: number, value: number): org.mwg.struct.LMatrix;
                appendColumn(newColumn: Float64Array): org.mwg.struct.LMatrix;
                data(): Float64Array;
                leadingDimension(): number;
                unsafeGet(index: number): number;
                unsafeSet(index: number, value: number): org.mwg.struct.LMatrix;
            }
            interface LongLongArrayMap extends org.mwg.struct.Map {
                get(key: number): Float64Array;
                put(key: number, value: number): void;
                delete(key: number, value: number): void;
                each(callback: org.mwg.struct.LongLongArrayMapCallBack): void;
                contains(key: number, value: number): boolean;
            }
            interface LongLongArrayMapCallBack {
                (key: number, value: number): void;
            }
            interface LongLongMap extends org.mwg.struct.Map {
                get(key: number): number;
                put(key: number, value: number): void;
                remove(key: number): void;
                each(callback: org.mwg.struct.LongLongMapCallBack): void;
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
                add(newValue: number): org.mwg.struct.Relation;
                addAll(newValues: Float64Array): org.mwg.struct.Relation;
                addNode(node: org.mwg.Node): org.mwg.struct.Relation;
                insert(index: number, newValue: number): org.mwg.struct.Relation;
                remove(oldValue: number): org.mwg.struct.Relation;
                delete(oldValue: number): org.mwg.struct.Relation;
                clear(): org.mwg.struct.Relation;
            }
            interface RelationIndexed {
                size(): number;
                all(): Float64Array;
                add(node: org.mwg.Node, ...attributeNames: string[]): org.mwg.struct.RelationIndexed;
                remove(node: org.mwg.Node, ...attributeNames: string[]): org.mwg.struct.RelationIndexed;
                clear(): org.mwg.struct.RelationIndexed;
                find(callback: org.mwg.Callback<org.mwg.Node[]>, world: number, time: number, ...params: string[]): void;
                findByQuery(query: org.mwg.Query, callback: org.mwg.Callback<org.mwg.Node[]>): void;
            }
            interface StringIntMap extends org.mwg.struct.Map {
                getValue(key: string): number;
                getByHash(index: number): string;
                containsHash(index: number): boolean;
                put(key: string, value: number): void;
                remove(key: string): void;
                each(callback: org.mwg.struct.StringLongMapCallBack): void;
            }
            interface StringLongMapCallBack {
                (key: string, value: number): void;
            }
        }
        module task {
            interface Action {
                eval(ctx: org.mwg.task.TaskContext): void;
                serialize(builder: java.lang.StringBuilder): void;
            }
            interface ActionFunction {
                (ctx: org.mwg.task.TaskContext): void;
            }
            interface ConditionalFunction {
                (ctx: org.mwg.task.TaskContext): boolean;
            }
            interface Task {
                then(nextAction: org.mwg.task.Action): org.mwg.task.Task;
                thenDo(nextActionFunction: org.mwg.task.ActionFunction): org.mwg.task.Task;
                doWhile(task: org.mwg.task.Task, cond: org.mwg.task.ConditionalFunction): org.mwg.task.Task;
                doWhileScript(task: org.mwg.task.Task, condScript: string): org.mwg.task.Task;
                loop(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task;
                loopPar(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task;
                forEach(subTask: org.mwg.task.Task): org.mwg.task.Task;
                forEachPar(subTask: org.mwg.task.Task): org.mwg.task.Task;
                flat(): org.mwg.task.Task;
                map(subTask: org.mwg.task.Task): org.mwg.task.Task;
                mapPar(subTask: org.mwg.task.Task): org.mwg.task.Task;
                ifThen(cond: org.mwg.task.ConditionalFunction, then: org.mwg.task.Task): org.mwg.task.Task;
                ifThenScript(condScript: string, then: org.mwg.task.Task): org.mwg.task.Task;
                ifThenElse(cond: org.mwg.task.ConditionalFunction, thenSub: org.mwg.task.Task, elseSub: org.mwg.task.Task): org.mwg.task.Task;
                ifThenElseScript(condScript: string, thenSub: org.mwg.task.Task, elseSub: org.mwg.task.Task): org.mwg.task.Task;
                whileDo(cond: org.mwg.task.ConditionalFunction, task: org.mwg.task.Task): org.mwg.task.Task;
                whileDoScript(condScript: string, task: org.mwg.task.Task): org.mwg.task.Task;
                pipe(...subTasks: org.mwg.task.Task[]): org.mwg.task.Task;
                pipePar(...subTasks: org.mwg.task.Task[]): org.mwg.task.Task;
                isolate(subTask: org.mwg.task.Task): org.mwg.task.Task;
                parse(input: string, graph: org.mwg.Graph): org.mwg.task.Task;
                loadFromBuffer(buffer: org.mwg.struct.Buffer, graph: org.mwg.Graph): org.mwg.task.Task;
                saveToBuffer(buffer: org.mwg.struct.Buffer): org.mwg.task.Task;
                addHook(hook: org.mwg.task.TaskHook): org.mwg.task.Task;
                execute(graph: org.mwg.Graph, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                executeSync(graph: org.mwg.Graph): org.mwg.task.TaskResult<any>;
                executeWith(graph: org.mwg.Graph, initial: any, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                prepare(graph: org.mwg.Graph, initial: any, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): org.mwg.task.TaskContext;
                executeUsing(preparedContext: org.mwg.task.TaskContext): void;
                executeFrom(parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, affinity: number, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                executeFromUsing(parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, affinity: number, contextInitializer: org.mwg.Callback<org.mwg.task.TaskContext>, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
                travelInWorld(world: string): org.mwg.task.Task;
                travelInTime(time: string): org.mwg.task.Task;
                inject(input: any): org.mwg.task.Task;
                defineAsGlobalVar(name: string): org.mwg.task.Task;
                defineAsVar(name: string): org.mwg.task.Task;
                declareGlobalVar(name: string): org.mwg.task.Task;
                declareVar(name: string): org.mwg.task.Task;
                readVar(name: string): org.mwg.task.Task;
                setAsVar(name: string): org.mwg.task.Task;
                addToVar(name: string): org.mwg.task.Task;
                setAttribute(name: string, type: number, value: string): org.mwg.task.Task;
                forceAttribute(name: string, type: number, value: string): org.mwg.task.Task;
                remove(name: string): org.mwg.task.Task;
                attributes(): org.mwg.task.Task;
                timepoints(from: string, to: string): org.mwg.task.Task;
                attributesWithType(filterType: number): org.mwg.task.Task;
                addVarToRelation(relName: string, varName: string, ...attributes: string[]): org.mwg.task.Task;
                removeVarFromRelation(relName: string, varFrom: string, ...attributes: string[]): org.mwg.task.Task;
                traverse(name: string, ...params: string[]): org.mwg.task.Task;
                attribute(name: string, ...params: string[]): org.mwg.task.Task;
                readGlobalIndex(indexName: string, ...query: string[]): org.mwg.task.Task;
                addToGlobalIndex(name: string, ...attributes: string[]): org.mwg.task.Task;
                addToGlobalTimedIndex(name: string, ...attributes: string[]): org.mwg.task.Task;
                removeFromGlobalIndex(name: string, ...attributes: string[]): org.mwg.task.Task;
                removeFromGlobalTimedIndex(name: string, ...attributes: string[]): org.mwg.task.Task;
                indexNames(): org.mwg.task.Task;
                selectWith(name: string, pattern: string): org.mwg.task.Task;
                selectWithout(name: string, pattern: string): org.mwg.task.Task;
                select(filterFunction: org.mwg.task.TaskFunctionSelect): org.mwg.task.Task;
                selectScript(script: string): org.mwg.task.Task;
                selectObject(filterFunction: org.mwg.task.TaskFunctionSelectObject): org.mwg.task.Task;
                print(name: string): org.mwg.task.Task;
                println(name: string): org.mwg.task.Task;
                executeExpression(expression: string): org.mwg.task.Task;
                createNode(): org.mwg.task.Task;
                createTypedNode(type: string): org.mwg.task.Task;
                save(): org.mwg.task.Task;
                script(script: string): org.mwg.task.Task;
                asyncScript(ascript: string): org.mwg.task.Task;
                lookup(nodeId: string): org.mwg.task.Task;
                lookupAll(nodeIds: string): org.mwg.task.Task;
                clearResult(): org.mwg.task.Task;
                action(name: string, ...params: string[]): org.mwg.task.Task;
                flipVar(name: string): org.mwg.task.Task;
                atomic(protectedTask: org.mwg.task.Task, ...variablesToLock: string[]): org.mwg.task.Task;
            }
            interface TaskActionFactory {
                (params: string[], contextTasks: java.util.Map<number, org.mwg.task.Task>): org.mwg.task.Action;
            }
            interface TaskContext {
                graph(): org.mwg.Graph;
                world(): number;
                setWorld(world: number): org.mwg.task.TaskContext;
                time(): number;
                setTime(time: number): org.mwg.task.TaskContext;
                variables(): org.mwg.utility.Tuple<string, org.mwg.task.TaskResult<any>>[];
                variable(name: string): org.mwg.task.TaskResult<any>;
                isGlobal(name: string): boolean;
                wrap(input: any): org.mwg.task.TaskResult<any>;
                wrapClone(input: any): org.mwg.task.TaskResult<any>;
                newResult(): org.mwg.task.TaskResult<any>;
                declareVariable(name: string): org.mwg.task.TaskContext;
                defineVariable(name: string, initialResult: any): org.mwg.task.TaskContext;
                defineVariableForSubTask(name: string, initialResult: any): org.mwg.task.TaskContext;
                setGlobalVariable(name: string, value: any): org.mwg.task.TaskContext;
                setVariable(name: string, value: any): org.mwg.task.TaskContext;
                addToGlobalVariable(name: string, value: any): org.mwg.task.TaskContext;
                addToVariable(name: string, value: any): org.mwg.task.TaskContext;
                result(): org.mwg.task.TaskResult<any>;
                resultAsNodes(): org.mwg.task.TaskResult<org.mwg.Node>;
                resultAsStrings(): org.mwg.task.TaskResult<string>;
                continueTask(): void;
                continueWith(nextResult: org.mwg.task.TaskResult<any>): void;
                endTask(nextResult: org.mwg.task.TaskResult<any>, e: Error): void;
                template(input: string): string;
                templates(inputs: string[]): string[];
                append(additionalOutput: string): void;
            }
            interface TaskFunctionSelect {
                (node: org.mwg.Node, context: org.mwg.task.TaskContext): boolean;
            }
            interface TaskFunctionSelectObject {
                (object: any, context: org.mwg.task.TaskContext): boolean;
            }
            interface TaskHook {
                start(initialContext: org.mwg.task.TaskContext): void;
                beforeAction(action: org.mwg.task.Action, context: org.mwg.task.TaskContext): void;
                afterAction(action: org.mwg.task.Action, context: org.mwg.task.TaskContext): void;
                beforeTask(parentContext: org.mwg.task.TaskContext, context: org.mwg.task.TaskContext): void;
                afterTask(context: org.mwg.task.TaskContext): void;
                end(finalContext: org.mwg.task.TaskContext): void;
            }
            interface TaskResult<A> {
                iterator(): org.mwg.task.TaskResultIterator<any>;
                get(index: number): A;
                set(index: number, input: A): org.mwg.task.TaskResult<A>;
                allocate(index: number): org.mwg.task.TaskResult<A>;
                add(input: A): org.mwg.task.TaskResult<A>;
                clear(): org.mwg.task.TaskResult<A>;
                clone(): org.mwg.task.TaskResult<A>;
                free(): void;
                size(): number;
                asArray(): any[];
                exception(): Error;
                output(): string;
                setException(e: Error): org.mwg.task.TaskResult<A>;
                setOutput(output: string): org.mwg.task.TaskResult<A>;
                fillWith(source: org.mwg.task.TaskResult<A>): org.mwg.task.TaskResult<A>;
            }
            interface TaskResultIterator<A> {
                next(): A;
                nextWithIndex(): org.mwg.utility.Tuple<number, A>;
            }
        }
        module utility {
            class Base64 {
                private static dictionary;
                private static powTwo;
                private static longIndexes;
                static encodeLongToBuffer(l: number, buffer: org.mwg.struct.Buffer): void;
                static encodeIntToBuffer(l: number, buffer: org.mwg.struct.Buffer): void;
                static decodeToLong(s: org.mwg.struct.Buffer): number;
                static decodeToLongWithBounds(s: org.mwg.struct.Buffer, offsetBegin: number, offsetEnd: number): number;
                static decodeToInt(s: org.mwg.struct.Buffer): number;
                static decodeToIntWithBounds(s: org.mwg.struct.Buffer, offsetBegin: number, offsetEnd: number): number;
                static encodeDoubleToBuffer(d: number, buffer: org.mwg.struct.Buffer): void;
                static decodeToDouble(s: org.mwg.struct.Buffer): number;
                static decodeToDoubleWithBounds(s: org.mwg.struct.Buffer, offsetBegin: number, offsetEnd: number): number;
                static encodeBoolArrayToBuffer(boolArr: Array<boolean>, buffer: org.mwg.struct.Buffer): void;
                static decodeBoolArray(s: org.mwg.struct.Buffer, arraySize: number): any[];
                static decodeToBoolArrayWithBounds(s: org.mwg.struct.Buffer, offsetBegin: number, offsetEnd: number, arraySize: number): any[];
                static encodeStringToBuffer(s: string, buffer: org.mwg.struct.Buffer): void;
                static decodeString(s: org.mwg.struct.Buffer): string;
                static decodeToStringWithBounds(s: org.mwg.struct.Buffer, offsetBegin: number, offsetEnd: number): string;
            }
            class BufferView implements org.mwg.struct.Buffer {
                private _origin;
                private _initPos;
                private _endPos;
                constructor(p_origin: org.mwg.struct.Buffer, p_initPos: number, p_endPos: number);
                write(b: number): void;
                writeAll(bytes: Int8Array): void;
                read(position: number): number;
                data(): Int8Array;
                length(): number;
                free(): void;
                iterator(): org.mwg.struct.BufferIterator;
                removeLast(): void;
                slice(initPos: number, endPos: number): Int8Array;
            }
            class DefaultBufferIterator implements org.mwg.struct.BufferIterator {
                private _origin;
                private _originSize;
                private _cursor;
                constructor(p_origin: org.mwg.struct.Buffer);
                hasNext(): boolean;
                next(): org.mwg.struct.Buffer;
            }
            class Enforcer {
                private checkers;
                asBool(propertyName: string): org.mwg.utility.Enforcer;
                asString(propertyName: string): org.mwg.utility.Enforcer;
                asLong(propertyName: string): org.mwg.utility.Enforcer;
                asLongWithin(propertyName: string, min: number, max: number): org.mwg.utility.Enforcer;
                asDouble(propertyName: string): org.mwg.utility.Enforcer;
                asDoubleWithin(propertyName: string, min: number, max: number): org.mwg.utility.Enforcer;
                asInt(propertyName: string): org.mwg.utility.Enforcer;
                asIntWithin(propertyName: string, min: number, max: number): org.mwg.utility.Enforcer;
                asIntGreaterOrEquals(propertyName: string, min: number): org.mwg.utility.Enforcer;
                asDoubleArray(propertyName: string): org.mwg.utility.Enforcer;
                asPositiveInt(propertyName: string): org.mwg.utility.Enforcer;
                asNonNegativeDouble(propertyName: string): org.mwg.utility.Enforcer;
                asPositiveDouble(propertyName: string): org.mwg.utility.Enforcer;
                asNonNegativeOrNanDouble(propertyName: string): org.mwg.utility.Enforcer;
                asPositiveLong(propertyName: string): org.mwg.utility.Enforcer;
                declare(propertyName: string, checker: org.mwg.utility.EnforcerChecker): org.mwg.utility.Enforcer;
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
                static keyToBuffer(buffer: org.mwg.struct.Buffer, chunkType: number, world: number, time: number, id: number): void;
            }
            class Tuple<A, B> {
                private _left;
                private _right;
                constructor(p_left: A, p_right: B);
                left(): A;
                right(): B;
            }
            class VerboseHook implements org.mwg.task.TaskHook {
                private ctxIdents;
                start(initialContext: org.mwg.task.TaskContext): void;
                beforeAction(action: org.mwg.task.Action, context: org.mwg.task.TaskContext): void;
                afterAction(action: org.mwg.task.Action, context: org.mwg.task.TaskContext): void;
                beforeTask(parentContext: org.mwg.task.TaskContext, context: org.mwg.task.TaskContext): void;
                afterTask(context: org.mwg.task.TaskContext): void;
                end(finalContext: org.mwg.task.TaskContext): void;
            }
            class VerbosePlugin extends org.mwg.base.BasePlugin {
                constructor();
            }
        }
    }
}
