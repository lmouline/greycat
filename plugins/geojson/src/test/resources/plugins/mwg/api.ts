/// <reference path="./jre.ts" />

module org {
  export module mwg {
    export interface Callback<A> {
      (result: A): void;
    }
    export class Constants {
      public static KEY_SIZE: number = 4;
      public static LONG_SIZE: number = 53;
      public static PREFIX_SIZE: number = 16;
      public static BEGINNING_OF_TIME: number = -0x001FFFFFFFFFFFFE;
      public static END_OF_TIME: number = 0x001FFFFFFFFFFFFE;
      public static NULL_LONG: number = 0x001FFFFFFFFFFFFF;
      public static KEY_PREFIX_MASK: number = 0x0000001FFFFFFFFF;
      public static CACHE_MISS_ERROR: string = "Cache miss error";
      public static QUERY_SEP: string = ',';
      public static QUERY_KV_SEP: string = '=';
      public static TASK_SEP: string = '.';
      public static TASK_PARAM_OPEN: string = '(';
      public static TASK_PARAM_CLOSE: string = ')';
      public static CHUNK_SEP : number = "|".charCodeAt(0);
      public static CHUNK_SUB_SEP : number = ",".charCodeAt(0);
      public static CHUNK_SUB_SUB_SEP : number = ":".charCodeAt(0);
      public static CHUNK_SUB_SUB_SUB_SEP : number = "%".charCodeAt(0);
      public static BUFFER_SEP : number = "#".charCodeAt(0);
      public static KEY_SEP : number = ";".charCodeAt(0);
      public static MAP_INITIAL_CAPACITY: number = 8;
      public static MAP_LOAD_FACTOR: number = (<number>75 / <number>100);
      public static BOOL_TRUE : number = "1".charCodeAt(0);
      public static BOOL_FALSE : number = "0".charCodeAt(0);
      public static isDefined(param: any): boolean {
        return param != null;
      }
      public static equals(src: string, other: string): boolean {
        return src === other;
      }
      public static longArrayEquals(src: Float64Array, other: Float64Array): boolean {
        if (src.length != other.length) {
          return false;
        }
        for (let i: number = 0; i < src.length; i++) {
          if (src[i] != other[i]) {
            return false;
          }
        }
        return true;
      }
    }
    export interface DeferCounter {
      count(): void;
      getCount(): number;
      then(job: org.mwg.plugin.Job): void;
      wrap(): org.mwg.Callback<any>;
    }
    export interface DeferCounterSync extends org.mwg.DeferCounter {
      waitResult(): any;
    }
    export interface Graph {
      newNode(world: number, time: number): org.mwg.Node;
      newTypedNode(world: number, time: number, nodeType: string): org.mwg.Node;
      cloneNode(origin: org.mwg.Node): org.mwg.Node;
      lookup<A extends org.mwg.Node>(world: number, time: number, id: number, callback: org.mwg.Callback<A>): void;
      lookupAll(world: number, time: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
      fork(world: number): number;
      save(callback: org.mwg.Callback<boolean>): void;
      connect(callback: org.mwg.Callback<boolean>): void;
      disconnect(callback: org.mwg.Callback<boolean>): void;
      index(indexName: string, nodeToIndex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
      indexAt(world: number, time: number, indexName: string, nodeToIndex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
      unindex(indexName: string, nodeToUnindex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
      unindexAt(world: number, time: number, indexName: string, nodeToUnindex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
      indexes(world: number, time: number, callback: org.mwg.Callback<string[]>): void;
      find(world: number, time: number, indexName: string, query: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
      findByQuery(query: org.mwg.Query, callback: org.mwg.Callback<org.mwg.Node[]>): void;
      findAll(world: number, time: number, indexName: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
      getIndexNode(world: number, time: number, indexName: string, callback: org.mwg.Callback<org.mwg.Node>): void;
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
      taskHookFactory(): org.mwg.task.TaskHookFactory;
    }
    export class GraphBuilder {
      private _storage: org.mwg.plugin.Storage = null;
      private _scheduler: org.mwg.plugin.Scheduler = null;
      private _plugins: org.mwg.plugin.Plugin[] = null;
      private _memorySize: number = -1;
      private _readOnly: boolean = false;
      private static _internalBuilder: org.mwg.GraphBuilder.InternalBuilder = null;
      public withStorage(storage: org.mwg.plugin.Storage): org.mwg.GraphBuilder {
        this._storage = storage;
        return this;
      }
      public withReadOnlyStorage(storage: org.mwg.plugin.Storage): org.mwg.GraphBuilder {
        this._storage = storage;
        this._readOnly = true;
        return this;
      }
      public withMemorySize(numberOfElements: number): org.mwg.GraphBuilder {
        this._memorySize = numberOfElements;
        return this;
      }
      public withScheduler(scheduler: org.mwg.plugin.Scheduler): org.mwg.GraphBuilder {
        this._scheduler = scheduler;
        return this;
      }
      public withPlugin(plugin: org.mwg.plugin.Plugin): org.mwg.GraphBuilder {
        if (this._plugins == null) {
          this._plugins = new Array<org.mwg.plugin.Plugin>(1);
          this._plugins[0] = plugin;
        } else {
          let _plugins2: org.mwg.plugin.Plugin[] = new Array<org.mwg.plugin.Plugin>(this._plugins.length + 1);
          java.lang.System.arraycopy(this._plugins, 0, _plugins2, 0, this._plugins.length);
          _plugins2[this._plugins.length] = plugin;
          this._plugins = _plugins2;
        }
        return this;
      }
      public build(): org.mwg.Graph {
        if (org.mwg.GraphBuilder._internalBuilder == null) {
        org.mwg.GraphBuilder._internalBuilder = new org.mwg.core.Builder();
        }
        return org.mwg.GraphBuilder._internalBuilder.newGraph(this._storage, this._readOnly, this._scheduler, this._plugins, this._memorySize);
      }
    }
    export module GraphBuilder {
      export interface InternalBuilder {
        newGraph(storage: org.mwg.plugin.Storage, readOnly: boolean, scheduler: org.mwg.plugin.Scheduler, plugins: org.mwg.plugin.Plugin[], memorySize: number): org.mwg.Graph;
        newTask(): org.mwg.task.Task;
      }
    }
    export interface Node {
      world(): number;
      time(): number;
      id(): number;
      get(propertyName: string): any;
      getByIndex(propIndex: number): any;
      type(propertyName: string): number;
      nodeTypeName(): string;
      set(propertyName: string, propertyValue: any): void;
      setProperty(propertyName: string, propertyType: number, propertyValue: any): void;
      forceProperty(propertyName: string, propertyType: number, propertyValue: any): void;
      setPropertyByIndex(propIndex: number, propertyType: number, propertyValue: any): void;
      getOrCreate(propertyName: string, propertyType: number): any;
      getOrCreateRel(propertyName: string): org.mwg.struct.Relationship;
      removeProperty(propertyName: string): void;
      rel(relationName: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
      relByIndex(relationIndex: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
      add(relationName: string, relatedNode: org.mwg.Node): void;
      remove(relationName: string, relatedNode: org.mwg.Node): void;
      index(indexName: string, nodeToIndex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
      unindex(indexName: string, nodeToIndex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void;
      find(indexName: string, query: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
      findByQuery(query: org.mwg.Query, callback: org.mwg.Callback<org.mwg.Node[]>): void;
      findAll(indexName: string, callback: org.mwg.Callback<org.mwg.Node[]>): void;
      timeDephasing(): number;
      lastModification(): number;
      rephase(): void;
      timepoints(beginningOfSearch: number, endOfSearch: number, callback: org.mwg.Callback<Float64Array>): void;
      free(): void;
      graph(): org.mwg.Graph;
      jump<A extends org.mwg.Node>(targetTime: number, callback: org.mwg.Callback<A>): void;
    }
    export interface Query {
      parse(flatQuery: string): org.mwg.Query;
      add(attributeName: string, value: string): org.mwg.Query;
      setWorld(initialWorld: number): org.mwg.Query;
      world(): number;
      setTime(initialTime: number): org.mwg.Query;
      time(): number;
      setIndexName(indexName: string): org.mwg.Query;
      indexName(): string;
      hash(): number;
      attributes(): Float64Array;
      values(): any[];
    }
    export class Type {
      public static BOOL: number = 1;
      public static STRING: number = 2;
      public static LONG: number = 3;
      public static INT: number = 4;
      public static DOUBLE: number = 5;
      public static DOUBLE_ARRAY: number = 6;
      public static LONG_ARRAY: number = 7;
      public static INT_ARRAY: number = 8;
      public static LONG_TO_LONG_MAP: number = 9;
      public static LONG_TO_LONG_ARRAY_MAP: number = 10;
      public static STRING_TO_LONG_MAP: number = 11;
      public static RELATION: number = 12;
      public static typeName(p_type: number): string {
        switch (p_type) {
          case org.mwg.Type.BOOL:
            return "boolean";
          case org.mwg.Type.STRING:
            return "string";
          case org.mwg.Type.LONG:
            return "long";
          case org.mwg.Type.INT:
            return "int";
          case org.mwg.Type.DOUBLE:
            return "double";
          case org.mwg.Type.DOUBLE_ARRAY:
            return "double[]";
          case org.mwg.Type.LONG_ARRAY:
            return "long[]";
          case org.mwg.Type.INT_ARRAY:
            return "int[]";
          case org.mwg.Type.LONG_TO_LONG_MAP:
            return "map(long->long)";
          case org.mwg.Type.LONG_TO_LONG_ARRAY_MAP:
            return "map(long->long[])";
          case org.mwg.Type.STRING_TO_LONG_MAP:
            return "map(string->long)";
          case org.mwg.Type.RELATION:
            return "relation";
          default: 

            return "unknown";
        }
      }
    }
    export module chunk {
      export interface Chunk {
        world(): number;
        time(): number;
        id(): number;
        chunkType(): number;
        index(): number;
        save(buffer: org.mwg.struct.Buffer): void;
        load(buffer: org.mwg.struct.Buffer): void;
      }
      export interface ChunkSpace {
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
      }
      export class ChunkType {
        public static STATE_CHUNK: number = 0;
        public static TIME_TREE_CHUNK: number = 1;
        public static WORLD_ORDER_CHUNK: number = 2;
        public static GEN_CHUNK: number = 3;
      }
      export interface GenChunk extends org.mwg.chunk.Chunk {
        newKey(): number;
      }
      export interface Stack {
        enqueue(index: number): boolean;
        dequeueTail(): number;
        dequeue(index: number): boolean;
        free(): void;
        size(): number;
      }
      export interface StateChunk extends org.mwg.chunk.Chunk, org.mwg.plugin.NodeState {
        loadFrom(origin: org.mwg.chunk.StateChunk): void;
      }
      export interface TimeTreeChunk extends org.mwg.chunk.Chunk {
        insert(key: number): void;
        unsafe_insert(key: number): void;
        previousOrEqual(key: number): number;
        clearAt(max: number): void;
        range(startKey: number, endKey: number, maxElements: number, walker: org.mwg.chunk.TreeWalker): void;
        magic(): number;
        size(): number;
      }
      export interface TreeWalker {
        (t: number): void;
      }
      export interface WorldOrderChunk extends org.mwg.chunk.Chunk, org.mwg.struct.LongLongMap {
        magic(): number;
        lock(): void;
        unlock(): void;
        extra(): number;
        setExtra(extraValue: number): void;
      }
    }
    export module plugin {
      export abstract class AbstractNode implements org.mwg.Node {
        private _world: number;
        private _time: number;
        private _id: number;
        private _graph: org.mwg.Graph;
        public _resolver: org.mwg.plugin.Resolver;
        public _index_worldOrder: number = -1;
        public _index_superTimeTree: number = -1;
        public _index_timeTree: number = -1;
        public _index_stateChunk: number = -1;
        public _world_magic: number = -1;
        public _super_time_magic: number = -1;
        public _time_magic: number = -1;
        public _dead: boolean = false;
        private _lock: number;
        constructor(p_world: number, p_time: number, p_id: number, p_graph: org.mwg.Graph) {
          this._world = p_world;
          this._time = p_time;
          this._id = p_id;
          this._graph = p_graph;
          this._resolver = p_graph.resolver();
        }
        public cacheLock(): void {
        }
        public cacheUnlock(): void {
        }
        public init(): void {}
        public nodeTypeName(): string {
          return this._resolver.typeName(this);
        }
        public unphasedState(): org.mwg.plugin.NodeState {
          return this._resolver.resolveState(this);
        }
        public phasedState(): org.mwg.plugin.NodeState {
          return this._resolver.alignState(this);
        }
        public newState(time: number): org.mwg.plugin.NodeState {
          return this._resolver.newState(this, this._world, time);
        }
        public graph(): org.mwg.Graph {
          return this._graph;
        }
        public world(): number {
          return this._world;
        }
        public time(): number {
          return this._time;
        }
        public id(): number {
          return this._id;
        }
        public get(propertyName: string): any {
          let resolved: org.mwg.plugin.NodeState = this._resolver.resolveState(this);
          if (resolved != null) {
            return resolved.get(this._resolver.stringToHash(propertyName, false));
          }
          return null;
        }
        public set(propertyName: string, propertyValue: any): void {
          if (typeof propertyValue === 'string' || propertyValue instanceof String) {
          this.setProperty(propertyName, org.mwg.Type.STRING, propertyValue);
          } else if(typeof propertyValue === 'number' || propertyValue instanceof Number) {
          if(propertyValue % 1 != 0) {
          this.setProperty(propertyName, org.mwg.Type.DOUBLE, propertyValue);
          } else {
          this.setProperty(propertyName, org.mwg.Type.LONG, propertyValue);
          }
          } else if(typeof propertyValue === 'boolean' || propertyValue instanceof Boolean) {
          this.setProperty(propertyName, org.mwg.Type.BOOL, propertyValue);
          } else if (propertyValue instanceof Int32Array) {
          this.setProperty(propertyName, org.mwg.Type.LONG_ARRAY, propertyValue);
          } else if (propertyValue instanceof Float64Array) {
          this.setProperty(propertyName, org.mwg.Type.DOUBLE_ARRAY, propertyValue);
          } else {
          throw new Error("Invalid property type: " + propertyValue + ", please use a Type listed in org.mwg.Type");
          }
        }
        public forceProperty(propertyName: string, propertyType: number, propertyValue: any): void {
          let hashed: number = this._resolver.stringToHash(propertyName, true);
          let preciseState: org.mwg.plugin.NodeState = this._resolver.alignState(this);
          if (preciseState != null) {
            preciseState.set(hashed, propertyType, propertyValue);
          } else {
            throw new Error(org.mwg.Constants.CACHE_MISS_ERROR);
          }
        }
        public setProperty(propertyName: string, propertyType: number, propertyValue: any): void {
          let hashed: number = this._resolver.stringToHash(propertyName, true);
          let unPhasedState: org.mwg.plugin.NodeState = this._resolver.resolveState(this);
          let isDiff: boolean = (propertyType != unPhasedState.getType(hashed));
          if (!isDiff) {
            isDiff = !this.isEquals(unPhasedState.get(hashed), propertyValue, propertyType);
          }
          if (isDiff) {
            let preciseState: org.mwg.plugin.NodeState = this._resolver.alignState(this);
            if (preciseState != null) {
              preciseState.set(hashed, propertyType, propertyValue);
            } else {
              throw new Error(org.mwg.Constants.CACHE_MISS_ERROR);
            }
          }
        }
        private isEquals(obj1: any, obj2: any, type: number): boolean {
          switch (type) {
            case org.mwg.Type.BOOL:
              return ((<boolean>obj1) == (<boolean>obj2));
            case org.mwg.Type.DOUBLE:
              return ((<number>obj1) == (<number>obj2));
            case org.mwg.Type.INT:
              return ((<number>obj1) == (<number>obj2));
            case org.mwg.Type.LONG:
              return ((<number>obj1) == (<number>obj2));
            case org.mwg.Type.STRING:
              return ((<string>obj1) === <string>obj2);
            case org.mwg.Type.DOUBLE_ARRAY:            let obj1_ar_d: Float64Array = <Float64Array>obj1;
            let obj2_ar_d: Float64Array = <Float64Array>obj2;
            if (obj1_ar_d.length != obj2_ar_d.length) {
              return false;
            } else {
              for (let i: number = 0; i < obj1_ar_d.length; i++) {
                if (obj1_ar_d[i] != obj2_ar_d[i]) {
                  return false;
                }
              }
            }

              return true;
            case org.mwg.Type.INT_ARRAY:            let obj1_ar_i: Int32Array = <Int32Array>obj1;
            let obj2_ar_i: Int32Array = <Int32Array>obj2;
            if (obj1_ar_i.length != obj2_ar_i.length) {
              return false;
            } else {
              for (let i: number = 0; i < obj1_ar_i.length; i++) {
                if (obj1_ar_i[i] != obj2_ar_i[i]) {
                  return false;
                }
              }
            }

              return true;
            case org.mwg.Type.LONG_ARRAY:            let obj1_ar_l: Float64Array = <Float64Array>obj1;
            let obj2_ar_l: Float64Array = <Float64Array>obj2;
            if (obj1_ar_l.length != obj2_ar_l.length) {
              return false;
            } else {
              for (let i: number = 0; i < obj1_ar_l.length; i++) {
                if (obj1_ar_l[i] != obj2_ar_l[i]) {
                  return false;
                }
              }
            }

              return true;
            case org.mwg.Type.RELATION:
            case org.mwg.Type.STRING_TO_LONG_MAP:
            case org.mwg.Type.LONG_TO_LONG_MAP:
            case org.mwg.Type.LONG_TO_LONG_ARRAY_MAP:            throw new Error("Bad API usage: set can't be used with complex type, please use getOrCreate instead.");
            default: 
            throw new Error("Not managed type " + type);
          }
        }
        public getOrCreate(propertyName: string, propertyType: number): any {
          let preciseState: org.mwg.plugin.NodeState = this._resolver.alignState(this);
          if (preciseState != null) {
            return preciseState.getOrCreate(this._resolver.stringToHash(propertyName, true), propertyType);
          } else {
            throw new Error(org.mwg.Constants.CACHE_MISS_ERROR);
          }
        }
        public type(propertyName: string): number {
          let resolved: org.mwg.plugin.NodeState = this._resolver.resolveState(this);
          if (resolved != null) {
            return resolved.getType(this._resolver.stringToHash(propertyName, false));
          }
          return -1;
        }
        public removeProperty(attributeName: string): void {
          this.setProperty(attributeName, org.mwg.Type.INT, null);
        }
        public rel(relationName: string, callback: org.mwg.Callback<org.mwg.Node[]>): void {
          this.relByIndex(this._resolver.stringToHash(relationName, false), callback);
        }
        public relByIndex(relationIndex: number, callback: org.mwg.Callback<org.mwg.Node[]>): void {
          if (callback == null) {
            return;
          }
          let resolved: org.mwg.plugin.NodeState = this._resolver.resolveState(this);
          if (resolved != null) {
            let relationArray: org.mwg.struct.Relationship = <org.mwg.struct.Relationship>resolved.get(relationIndex);
            if (relationArray == null || relationArray.size() == 0) {
              callback(new Array<org.mwg.Node>(0));
            } else {
              let relSize: number = relationArray.size();
              let ids: Float64Array = new Float64Array(relSize);
              for (let i: number = 0; i < relSize; i++) {
                ids[i] = relationArray.get(i);
              }
              this._resolver.lookupAll(this._world, this._time, ids, (result : org.mwg.Node[]) => {
{
                  callback(result);
                }              });
            }
          } else {
            callback(new Array<org.mwg.Node>(0));
          }
        }
        public add(relationName: string, relatedNode: org.mwg.Node): void {
          if (relatedNode != null) {
            let preciseState: org.mwg.plugin.NodeState = this._resolver.alignState(this);
            let relHash: number = this._resolver.stringToHash(relationName, true);
            if (preciseState != null) {
              let relationArray: org.mwg.struct.Relationship = <org.mwg.struct.Relationship>preciseState.getOrCreate(relHash, org.mwg.Type.RELATION);
              relationArray.add(relatedNode.id());
            } else {
              throw new Error(org.mwg.Constants.CACHE_MISS_ERROR);
            }
          }
        }
        public remove(relationName: string, relatedNode: org.mwg.Node): void {
          if (relatedNode != null) {
            let preciseState: org.mwg.plugin.NodeState = this._resolver.alignState(this);
            let relHash: number = this._resolver.stringToHash(relationName, false);
            if (preciseState != null) {
              let relationArray: org.mwg.struct.Relationship = <org.mwg.struct.Relationship>preciseState.get(relHash);
              if (relationArray != null) {
                relationArray.remove(relatedNode.id());
              }
            } else {
              throw new Error(org.mwg.Constants.CACHE_MISS_ERROR);
            }
          }
        }
        public free(): void {
          this._resolver.freeNode(this);
        }
        public timeDephasing(): number {
          let state: org.mwg.plugin.NodeState = this._resolver.resolveState(this);
          if (state != null) {
            return (this._time - state.time());
          } else {
            throw new Error(org.mwg.Constants.CACHE_MISS_ERROR);
          }
        }
        public lastModification(): number {
          let state: org.mwg.plugin.NodeState = this._resolver.resolveState(this);
          if (state != null) {
            return state.time();
          } else {
            throw new Error(org.mwg.Constants.CACHE_MISS_ERROR);
          }
        }
        public rephase(): void {
          this._resolver.alignState(this);
        }
        public timepoints(beginningOfSearch: number, endOfSearch: number, callback: org.mwg.Callback<Float64Array>): void {
          this._resolver.resolveTimepoints(this, beginningOfSearch, endOfSearch, callback);
        }
        public jump<A extends org.mwg.Node>(targetTime: number, callback: org.mwg.Callback<A>): void {
          this._resolver.lookup(this._world, targetTime, this._id, callback);
        }
        public findByQuery(query: org.mwg.Query, callback: org.mwg.Callback<org.mwg.Node[]>): void {
          let currentNodeState: org.mwg.plugin.NodeState = this._resolver.resolveState(this);
          if (currentNodeState == null) {
            throw new Error(org.mwg.Constants.CACHE_MISS_ERROR);
          }
          let indexName: string = query.indexName();
          if (indexName == null) {
            throw new Error("Please specify indexName in query before first use!");
          }
          let queryWorld: number = query.world();
          if (queryWorld == org.mwg.Constants.NULL_LONG) {
            queryWorld = this.world();
          }
          let queryTime: number = query.time();
          if (queryTime == org.mwg.Constants.NULL_LONG) {
            queryTime = this.time();
          }
          let indexMap: org.mwg.struct.LongLongArrayMap = <org.mwg.struct.LongLongArrayMap>currentNodeState.get(this._resolver.stringToHash(indexName, false));
          if (indexMap != null) {
            let selfPointer: org.mwg.plugin.AbstractNode = this;
            let foundIds: Float64Array = indexMap.get(query.hash());
            if (foundIds == null) {
              callback(new Array<org.mwg.plugin.AbstractNode>(0));
              return;
            }
            selfPointer._resolver.lookupAll(queryWorld, queryTime, foundIds, (resolved : org.mwg.Node[]) => {
{
                let resultSet: org.mwg.Node[] = new Array<org.mwg.plugin.AbstractNode>(foundIds.length);
                let resultSetIndex: number = 0;
                for (let i: number = 0; i < resultSet.length; i++) {
                  let resolvedNode: org.mwg.Node = resolved[i];
                  if (resolvedNode != null) {
                    let resolvedState: org.mwg.plugin.NodeState = selfPointer._resolver.resolveState(resolvedNode);
                    let exact: boolean = true;
                    for (let j: number = 0; j < query.attributes().length; j++) {
                      let obj: any = resolvedState.get(query.attributes()[j]);
                      if (query.values()[j] == null) {
                        if (obj != null) {
                          exact = false;
break;
                        }
                      } else {
                        if (obj == null) {
                          exact = false;
break;
                        } else {
                          if (obj instanceof Float64Array) {
                            if (query.values()[j] instanceof Float64Array) {
                              if (!org.mwg.Constants.longArrayEquals(<Float64Array>query.values()[j], <Float64Array>obj)) {
                                exact = false;
break;
                              }
                            } else {
                              exact = false;
break;
                            }
                          } else {
                            if (!org.mwg.Constants.equals(query.values()[j].toString(), obj.toString())) {
                              exact = false;
break;
                            }
                          }
                        }
                      }
                    }
                    if (exact) {
                      resultSet[resultSetIndex] = resolvedNode;
                      resultSetIndex++;
                    }
                  }
                }
                if (resultSet.length == resultSetIndex) {
                  callback(resultSet);
                } else {
                  let trimmedResultSet: org.mwg.Node[] = new Array<org.mwg.plugin.AbstractNode>(resultSetIndex);
                  java.lang.System.arraycopy(resultSet, 0, trimmedResultSet, 0, resultSetIndex);
                  callback(trimmedResultSet);
                }
              }            });
          } else {
            callback(new Array<org.mwg.plugin.AbstractNode>(0));
          }
        }
        public find(indexName: string, query: string, callback: org.mwg.Callback<org.mwg.Node[]>): void {
          let queryObj: org.mwg.Query = this._graph.newQuery();
          queryObj.setWorld(this.world());
          queryObj.setTime(this.time());
          queryObj.setIndexName(indexName);
          queryObj.parse(query);
          this.findByQuery(queryObj, callback);
        }
        public findAll(indexName: string, callback: org.mwg.Callback<org.mwg.Node[]>): void {
          let currentNodeState: org.mwg.plugin.NodeState = this._resolver.resolveState(this);
          if (currentNodeState == null) {
            throw new Error(org.mwg.Constants.CACHE_MISS_ERROR);
          }
          let indexMap: org.mwg.struct.LongLongArrayMap = <org.mwg.struct.LongLongArrayMap>currentNodeState.get(this._resolver.stringToHash(indexName, false));
          if (indexMap != null) {
            let ids: Float64Array = new Float64Array(<number>indexMap.size());
            let idIndex: Int32Array = new Int32Array([0]);
            indexMap.each((hash : number, nodeId : number) => {
{
                ids[idIndex[0]] = nodeId;
                idIndex[0]++;
              }            });
            this._resolver.lookupAll(this.world(), this.time(), ids, (result : org.mwg.Node[]) => {
{
                callback(result);
              }            });
          } else {
            callback(new Array<org.mwg.plugin.AbstractNode>(0));
          }
        }
        public index(indexName: string, nodeToIndex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void {
          let keyAttributes: string[] = flatKeyAttributes.split(org.mwg.Constants.QUERY_SEP + "");
          let hashName: number = this._resolver.stringToHash(indexName, true);
          let flatQuery: org.mwg.Query = this._graph.newQuery();
          let toIndexNodeState: org.mwg.plugin.NodeState = this._resolver.resolveState(nodeToIndex);
          for (let i: number = 0; i < keyAttributes.length; i++) {
            let attKey: string = keyAttributes[i];
            let attValue: any = toIndexNodeState.getFromKey(attKey);
            if (attValue != null) {
              flatQuery.add(attKey, attValue.toString());
            } else {
              flatQuery.add(attKey, null);
            }
          }
          let alreadyIndexed: boolean = false;
          let previousState: org.mwg.plugin.NodeState = this._resolver.resolveState(this);
          if (previousState != null) {
            let previousMap: org.mwg.struct.LongLongArrayMap = <org.mwg.struct.LongLongArrayMap>previousState.get(hashName);
            if (previousMap != null) {
              alreadyIndexed = previousMap.contains(flatQuery.hash(), nodeToIndex.id());
            }
          }
          if (!alreadyIndexed) {
            let currentNodeState: org.mwg.plugin.NodeState = this._resolver.alignState(this);
            if (currentNodeState == null) {
              throw new Error(org.mwg.Constants.CACHE_MISS_ERROR);
            }
            let indexMap: org.mwg.struct.LongLongArrayMap = <org.mwg.struct.LongLongArrayMap>currentNodeState.getOrCreate(hashName, org.mwg.Type.LONG_TO_LONG_ARRAY_MAP);
            indexMap.put(flatQuery.hash(), nodeToIndex.id());
          }
          if (org.mwg.Constants.isDefined(callback)) {
            callback(true);
          }
        }
        public unindex(indexName: string, nodeToIndex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void {
          let keyAttributes: string[] = flatKeyAttributes.split(org.mwg.Constants.QUERY_SEP + "");
          let currentNodeState: org.mwg.plugin.NodeState = this._resolver.alignState(this);
          if (currentNodeState == null) {
            throw new Error(org.mwg.Constants.CACHE_MISS_ERROR);
          }
          let indexMap: org.mwg.struct.LongLongArrayMap = <org.mwg.struct.LongLongArrayMap>currentNodeState.get(this._resolver.stringToHash(indexName, false));
          if (indexMap != null) {
            let flatQuery: org.mwg.Query = this._graph.newQuery();
            let toIndexNodeState: org.mwg.plugin.NodeState = this._resolver.resolveState(nodeToIndex);
            for (let i: number = 0; i < keyAttributes.length; i++) {
              let attKey: string = keyAttributes[i];
              let attValue: any = toIndexNodeState.getFromKey(attKey);
              if (attValue != null) {
                flatQuery.add(attKey, attValue.toString());
              } else {
                flatQuery.add(attKey, null);
              }
            }
            indexMap.remove(flatQuery.hash(), nodeToIndex.id());
          }
          if (org.mwg.Constants.isDefined(callback)) {
            callback(true);
          }
        }
        private isNaN(toTest: number): boolean {
          return isNaN(toTest);
        }
        public toString(): string {
          let builder: java.lang.StringBuilder = new java.lang.StringBuilder();
          let isFirst: boolean[] = [true];
          builder.append("{\"world\":");
          builder.append(this.world());
          builder.append(",\"time\":");
          builder.append(this.time());
          builder.append(",\"id\":");
          builder.append(this.id());
          let state: org.mwg.plugin.NodeState = this._resolver.resolveState(this);
          if (state != null) {
            state.each((attributeKey : number, elemType : number, elem : any) => {
{
                if (elem != null) {
                  switch (elemType) {
                    case org.mwg.Type.BOOL: {
                      builder.append(",\"");
                      builder.append(this._resolver.hashToString(attributeKey));
                      builder.append("\":");
                      if (<boolean>elem) {
                        builder.append("0");
                      } else {
                        builder.append("1");
                      }
break;
                    }
                    case org.mwg.Type.STRING: {
                      builder.append(",\"");
                      builder.append(this._resolver.hashToString(attributeKey));
                      builder.append("\":");
                      builder.append("\"");
                      builder.append(elem);
                      builder.append("\"");
break;
                    }
                    case org.mwg.Type.LONG: {
                      builder.append(",\"");
                      builder.append(this._resolver.hashToString(attributeKey));
                      builder.append("\":");
                      builder.append(elem);
break;
                    }
                    case org.mwg.Type.INT: {
                      builder.append(",\"");
                      builder.append(this._resolver.hashToString(attributeKey));
                      builder.append("\":");
                      builder.append(elem);
break;
                    }
                    case org.mwg.Type.DOUBLE: {
                      if (!this.isNaN(<number>elem)) {
                        builder.append(",\"");
                        builder.append(this._resolver.hashToString(attributeKey));
                        builder.append("\":");
                        builder.append(elem);
                      }
break;
                    }
                    case org.mwg.Type.DOUBLE_ARRAY: {
                      builder.append(",\"");
                      builder.append(this._resolver.hashToString(attributeKey));
                      builder.append("\":");
                      builder.append("[");
                      let castedArr: Float64Array = <Float64Array>elem;
                      for (let j: number = 0; j < castedArr.length; j++) {
                        if (j != 0) {
                          builder.append(",");
                        }
                        builder.append(castedArr[j]);
                      }
                      builder.append("]");
break;
                    }
                    case org.mwg.Type.RELATION:
                      builder.append(",\"");

                      builder.append(this._resolver.hashToString(attributeKey));

                      builder.append("\":");

                      builder.append("[");
                    let castedRelArr: org.mwg.struct.Relationship = <org.mwg.struct.Relationship>elem;
                    for (let j: number = 0; j < castedRelArr.size(); j++) {
                      if (j != 0) {
                        builder.append(",");
                      }
                      builder.append(castedRelArr.get(j));
                    }

                      builder.append("]");
break;
                    case org.mwg.Type.LONG_ARRAY: {
                      builder.append(",\"");
                      builder.append(this._resolver.hashToString(attributeKey));
                      builder.append("\":");
                      builder.append("[");
                      let castedArr2: Float64Array = <Float64Array>elem;
                      for (let j: number = 0; j < castedArr2.length; j++) {
                        if (j != 0) {
                          builder.append(",");
                        }
                        builder.append(castedArr2[j]);
                      }
                      builder.append("]");
break;
                    }
                    case org.mwg.Type.INT_ARRAY: {
                      builder.append(",\"");
                      builder.append(this._resolver.hashToString(attributeKey));
                      builder.append("\":");
                      builder.append("[");
                      let castedArr3: Int32Array = <Int32Array>elem;
                      for (let j: number = 0; j < castedArr3.length; j++) {
                        if (j != 0) {
                          builder.append(",");
                        }
                        builder.append(castedArr3[j]);
                      }
                      builder.append("]");
break;
                    }
                    case org.mwg.Type.LONG_TO_LONG_MAP: {
                      builder.append(",\"");
                      builder.append(this._resolver.hashToString(attributeKey));
                      builder.append("\":");
                      builder.append("{");
                      let castedMapL2L: org.mwg.struct.LongLongMap = <org.mwg.struct.LongLongMap>elem;
                      isFirst[0] = true;
                      castedMapL2L.each((key : number, value : number) => {
{
                          if (!isFirst[0]) {
                            builder.append(",");
                          } else {
                            isFirst[0] = false;
                          }
                          builder.append("\"");
                          builder.append(key);
                          builder.append("\":");
                          builder.append(value);
                        }                      });
                      builder.append("}");
break;
                    }
                    case org.mwg.Type.LONG_TO_LONG_ARRAY_MAP: {
                      builder.append(",\"");
                      builder.append(this._resolver.hashToString(attributeKey));
                      builder.append("\":");
                      builder.append("{");
                      let castedMapL2LA: org.mwg.struct.LongLongArrayMap = <org.mwg.struct.LongLongArrayMap>elem;
                      isFirst[0] = true;
                      let keys: java.util.Set<number> = new java.util.HashSet<number>();
                      castedMapL2LA.each((key : number, value : number) => {
{
                          keys.add(key);
                        }                      });
                      let flatKeys: number[] = keys.toArray(new Array<number>(keys.size()));
                      for (let i: number = 0; i < flatKeys.length; i++) {
                        let values: Float64Array = castedMapL2LA.get(flatKeys[i]);
                        if (!isFirst[0]) {
                          builder.append(",");
                        } else {
                          isFirst[0] = false;
                        }
                        builder.append("\"");
                        builder.append(flatKeys[i]);
                        builder.append("\":[");
                        for (let j: number = 0; j < values.length; j++) {
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
                    case org.mwg.Type.STRING_TO_LONG_MAP: {
                      builder.append(",\"");
                      builder.append(this._resolver.hashToString(attributeKey));
                      builder.append("\":");
                      builder.append("{");
                      let castedMapS2L: org.mwg.struct.StringLongMap = <org.mwg.struct.StringLongMap>elem;
                      isFirst[0] = true;
                      castedMapS2L.each((key : string, value : number) => {
{
                          if (!isFirst[0]) {
                            builder.append(",");
                          } else {
                            isFirst[0] = false;
                          }
                          builder.append("\"");
                          builder.append(key);
                          builder.append("\":");
                          builder.append(value);
                        }                      });
                      builder.append("}");
break;
                    }                  }
                }
              }            });
            builder.append("}");
          }
          return builder.toString();
        }
        public getOrCreateRel(propertyName: string): org.mwg.struct.Relationship {
          return <org.mwg.struct.Relationship>this.getOrCreate(propertyName, org.mwg.Type.RELATION);
        }
        public getByIndex(propIndex: number): any {
          return this._resolver.resolveState(this).get(propIndex);
        }
        public setPropertyByIndex(propIndex: number, propertyType: number, propertyValue: any): void {
          this._resolver.alignState(this).set(propIndex, propertyType, propertyValue);
        }
      }
      export class AbstractPlugin implements org.mwg.plugin.Plugin {
        private _nodeTypes: java.util.Map<string, org.mwg.plugin.NodeFactory> = new java.util.HashMap<string, org.mwg.plugin.NodeFactory>();
        private _taskActions: java.util.Map<string, org.mwg.task.TaskActionFactory> = new java.util.HashMap<string, org.mwg.task.TaskActionFactory>();
        private _memoryFactory: org.mwg.plugin.MemoryFactory;
        private _resolverFactory: org.mwg.plugin.ResolverFactory;
        private _hookFactory: org.mwg.task.TaskHookFactory;
        public declareNodeType(name: string, factory: org.mwg.plugin.NodeFactory): org.mwg.plugin.Plugin {
          this._nodeTypes.put(name, factory);
          return this;
        }
        public declareTaskAction(name: string, factory: org.mwg.task.TaskActionFactory): org.mwg.plugin.Plugin {
          this._taskActions.put(name, factory);
          return this;
        }
        public declareMemoryFactory(factory: org.mwg.plugin.MemoryFactory): org.mwg.plugin.Plugin {
          this._memoryFactory = factory;
          return this;
        }
        public declareResolverFactory(factory: org.mwg.plugin.ResolverFactory): org.mwg.plugin.Plugin {
          this._resolverFactory = factory;
          return this;
        }
        public hookFactory(): org.mwg.task.TaskHookFactory {
          return this._hookFactory;
        }
        public declareTaskHookFactory(factory: org.mwg.task.TaskHookFactory): org.mwg.plugin.Plugin {
          this._hookFactory = factory;
          return this;
        }
        public nodeTypes(): string[] {
          return this._nodeTypes.keySet().toArray(new Array<string>(this._nodeTypes.size()));
        }
        public nodeType(nodeTypeName: string): org.mwg.plugin.NodeFactory {
          return this._nodeTypes.get(nodeTypeName);
        }
        public taskActionTypes(): string[] {
          return this._taskActions.keySet().toArray(new Array<string>(this._taskActions.size()));
        }
        public taskActionType(taskTypeName: string): org.mwg.task.TaskActionFactory {
          return this._taskActions.get(taskTypeName);
        }
        public memoryFactory(): org.mwg.plugin.MemoryFactory {
          return this._memoryFactory;
        }
        public resolverFactory(): org.mwg.plugin.ResolverFactory {
          return this._resolverFactory;
        }
      }
      export abstract class AbstractTaskAction implements org.mwg.task.TaskAction {
        private _next: org.mwg.plugin.AbstractTaskAction = null;
        public setNext(p_next: org.mwg.plugin.AbstractTaskAction): void {
          this._next = p_next;
        }
        public next(): org.mwg.plugin.AbstractTaskAction {
          return this._next;
        }
        public abstract eval(context: org.mwg.task.TaskContext): void;
      }
      export interface Job {
        (): void;
      }
      export interface MemoryFactory {
        newSpace(memorySize: number, graph: org.mwg.Graph): org.mwg.chunk.ChunkSpace;
        newBuffer(): org.mwg.struct.Buffer;
      }
      export interface NodeFactory {
        (world: number, time: number, id: number, graph: org.mwg.Graph): org.mwg.Node;
      }
      export interface NodeState {
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
      export interface NodeStateCallback {
        (attributeKey: number, elemType: number, elem: any): void;
      }
      export interface Plugin {
        declareNodeType(name: string, factory: org.mwg.plugin.NodeFactory): org.mwg.plugin.Plugin;
        declareTaskAction(name: string, factory: org.mwg.task.TaskActionFactory): org.mwg.plugin.Plugin;
        declareMemoryFactory(factory: org.mwg.plugin.MemoryFactory): org.mwg.plugin.Plugin;
        declareTaskHookFactory(factory: org.mwg.task.TaskHookFactory): org.mwg.plugin.Plugin;
        declareResolverFactory(factory: org.mwg.plugin.ResolverFactory): org.mwg.plugin.Plugin;
        hookFactory(): org.mwg.task.TaskHookFactory;
        nodeTypes(): string[];
        nodeType(nodeTypeName: string): org.mwg.plugin.NodeFactory;
        taskActionTypes(): string[];
        taskActionType(taskTypeName: string): org.mwg.task.TaskActionFactory;
        memoryFactory(): org.mwg.plugin.MemoryFactory;
        resolverFactory(): org.mwg.plugin.ResolverFactory;
      }
      export interface Resolver {
        init(): void;
        initNode(node: org.mwg.Node, typeCode: number): void;
        initWorld(parentWorld: number, childWorld: number): void;
        freeNode(node: org.mwg.Node): void;
        typeName(node: org.mwg.Node): string;
        typeCode(node: org.mwg.Node): number;
        lookup<A extends org.mwg.Node>(world: number, time: number, id: number, callback: org.mwg.Callback<A>): void;
        lookupAll(world: number, time: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void;
        resolveState(node: org.mwg.Node): org.mwg.plugin.NodeState;
        alignState(node: org.mwg.Node): org.mwg.plugin.NodeState;
        newState(node: org.mwg.Node, world: number, time: number): org.mwg.plugin.NodeState;
        resolveTimepoints(node: org.mwg.Node, beginningOfSearch: number, endOfSearch: number, callback: org.mwg.Callback<Float64Array>): void;
        stringToHash(name: string, insertIfNotExists: boolean): number;
        hashToString(key: number): string;
      }
      export interface ResolverFactory {
        newResolver(storage: org.mwg.plugin.Storage, space: org.mwg.chunk.ChunkSpace): org.mwg.plugin.Resolver;
      }
      export interface Scheduler {
        dispatch(affinity: number, job: org.mwg.plugin.Job): void;
        start(): void;
        stop(): void;
      }
      export class SchedulerAffinity {
        public static SAME_THREAD: number = 0;
        public static ANY_LOCAL_THREAD: number = 1;
        public static OTHER_LOCAL_THREAD: number = 2;
        public static ANY_REMOTE_THREAD: number = 3;
      }
      export interface Storage {
        get(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
        put(stream: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
        remove(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
        connect(graph: org.mwg.Graph, callback: org.mwg.Callback<boolean>): void;
        lock(callback: org.mwg.Callback<org.mwg.struct.Buffer>): void;
        unlock(previousLock: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void;
        disconnect(callback: org.mwg.Callback<boolean>): void;
      }
    }
    export module struct {
      export interface Buffer {
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
      export interface BufferIterator {
        hasNext(): boolean;
        next(): org.mwg.struct.Buffer;
      }
      export interface LongLongArrayMap extends org.mwg.struct.Map {
        get(key: number): Float64Array;
        put(key: number, value: number): void;
        remove(key: number, value: number): void;
        each(callback: org.mwg.struct.LongLongArrayMapCallBack): void;
        contains(key: number, value: number): boolean;
      }
      export interface LongLongArrayMapCallBack {
        (key: number, value: number): void;
      }
      export interface LongLongMap extends org.mwg.struct.Map {
        get(key: number): number;
        put(key: number, value: number): void;
        remove(key: number): void;
        each(callback: org.mwg.struct.LongLongMapCallBack): void;
      }
      export interface LongLongMapCallBack {
        (key: number, value: number): void;
      }
      export interface Map {
        size(): number;
      }
      export interface Relationship {
        size(): number;
        get(index: number): number;
        add(newValue: number): org.mwg.struct.Relationship;
        remove(oldValue: number): org.mwg.struct.Relationship;
        clear(): org.mwg.struct.Relationship;
      }
      export interface StringLongMap extends org.mwg.struct.Map {
        getValue(key: string): number;
        getByHash(index: number): string;
        containsHash(index: number): boolean;
        put(key: string, value: number): void;
        remove(key: string): void;
        each(callback: org.mwg.struct.StringLongMapCallBack): void;
      }
      export interface StringLongMapCallBack {
        (key: string, value: number): void;
      }
    }
    export module task {
      export interface Action {
        (context: org.mwg.task.TaskContext): void;
      }
      export class Actions {
        private static _internalBuilder: org.mwg.GraphBuilder.InternalBuilder = null;
        public static newTask(): org.mwg.task.Task {
          if (org.mwg.task.Actions._internalBuilder == null) {
          org.mwg.task.Actions._internalBuilder = new org.mwg.core.Builder();
          }
          return org.mwg.task.Actions._internalBuilder.newTask();
        }
        public static setWorld(variableName: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().setWorld(variableName);
        }
        public static setTime(variableName: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().setTime(variableName);
        }
        public static then(action: org.mwg.task.Action): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().then(action);
        }
        public static inject(input: any): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().inject(input);
        }
        public static fromVar(variableName: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().fromVar(variableName);
        }
        public static fromVarAt(variableName: string, index: number): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().fromVarAt(variableName, index);
        }
        public static fromIndexAll(indexName: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().fromIndexAll(indexName);
        }
        public static fromIndex(indexName: string, query: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().fromIndex(indexName, query);
        }
        public static indexNode(indexName: string, flatKeyAttributes: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().indexNode(indexName, flatKeyAttributes);
        }
        public static unindexNode(indexName: string, flatKeyAttributes: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().unindexNode(indexName, flatKeyAttributes);
        }
        public static localIndex(indexedRelation: string, flatKeyAttributes: string, varNodeToAdd: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().localIndex(indexedRelation, flatKeyAttributes, varNodeToAdd);
        }
        public static localUnindex(indexedRelation: string, flatKeyAttributes: string, varNodeToAdd: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().localUnindex(indexedRelation, flatKeyAttributes, varNodeToAdd);
        }
        public static parse(flatTask: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().parse(flatTask);
        }
        public static asGlobalVar(variableName: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().asGlobalVar(variableName);
        }
        public static addToGlobalVar(variableName: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().addToGlobalVar(variableName);
        }
        public static asVar(variableName: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().asGlobalVar(variableName);
        }
        public static defineVar(variableName: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().defineVar(variableName);
        }
        public static addToVar(variableName: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().addToVar(variableName);
        }
        public static map(mapFunction: org.mwg.task.TaskFunctionMap<any, any>): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().map(mapFunction);
        }
        public static selectWith(name: string, pattern: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().selectWith(name, pattern);
        }
        public static selectWithout(name: string, pattern: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().selectWithout(name, pattern);
        }
        public static select(filterFunction: org.mwg.task.TaskFunctionSelect): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().select(filterFunction);
        }
        public static selectObject(filterFunction: org.mwg.task.TaskFunctionSelectObject): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().selectObject(filterFunction);
        }
        public static traverse(relationName: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().traverse(relationName);
        }
        public static get(name: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().get(name);
        }
        public static traverseIndex(indexName: string, ...queryParams: string[]): org.mwg.task.Task {
          let t: org.mwg.task.Task = org.mwg.task.Actions.newTask().traverseIndex(indexName, ...queryParams);
          return t;
        }
        public static traverseOrKeep(relationName: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().traverseOrKeep(relationName);
        }
        public static traverseIndexAll(indexName: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().traverseIndexAll(indexName);
        }
        public static loop(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().loop(from, to, subTask);
        }
        public static loopPar(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().loopPar(from, to, subTask);
        }
        public static print(name: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().print(name);
        }
        public static setProperty(propertyName: string, propertyType: number, variableNameToSet: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().setProperty(propertyName, propertyType, variableNameToSet);
        }
        public static selectWhere(subTask: org.mwg.task.Task): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().selectWhere(subTask);
        }
        public static foreach(subTask: org.mwg.task.Task): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().foreach(subTask);
        }
        public static foreachPar(subTask: org.mwg.task.Task): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().foreachPar(subTask);
        }
        public static flatmap(subTask: org.mwg.task.Task): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().flatmap(subTask);
        }
        public static flatmapPar(subTask: org.mwg.task.Task): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().flatmapPar(subTask);
        }
        public static math(expression: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().math(expression);
        }
        public static action(name: string, params: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().action(name, params);
        }
        public static remove(relationName: string, variableNameToRemove: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().remove(relationName, variableNameToRemove);
        }
        public static add(relationName: string, variableNameToAdd: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().add(relationName, variableNameToAdd);
        }
        public static properties(): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().properties();
        }
        public static propertiesWithTypes(filter: number): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().propertiesWithTypes(filter);
        }
        public static jump(time: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().jump(time);
        }
        public static removeProperty(propertyName: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().removeProperty(propertyName);
        }
        public static newNode(): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().newNode();
        }
        public static newTypedNode(nodeType: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().newTypedNode(nodeType);
        }
        public static save(): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().save();
        }
        public static ifThen(cond: org.mwg.task.TaskFunctionConditional, then: org.mwg.task.Task): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().ifThen(cond, then);
        }
        public static ifThenElse(cond: org.mwg.task.TaskFunctionConditional, thenSub: org.mwg.task.Task, elseSub: org.mwg.task.Task): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().ifThenElse(cond, thenSub, elseSub);
        }
        public static whileDo(cond: org.mwg.task.TaskFunctionConditional, then: org.mwg.task.Task): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().whileDo(cond, then);
        }
        public static doWhile(then: org.mwg.task.Task, cond: org.mwg.task.TaskFunctionConditional): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().doWhile(then, cond);
        }
        public static split(splitPattern: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().split(splitPattern);
        }
        public static lookup(nodeId: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().lookup(nodeId);
        }
        public static hook(fact: org.mwg.task.TaskHookFactory): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().hook(fact);
        }
        public static clear(): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().clear();
        }
        public static subTask(subTask: org.mwg.task.Task): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().subTask(subTask);
        }
        public static isolate(subTask: org.mwg.task.Task): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().isolate(subTask);
        }
        public static subTasks(subTasks: org.mwg.task.Task[]): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().subTasks(subTasks);
        }
        public static subTasksPar(subTasks: org.mwg.task.Task[]): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().subTasksPar(subTasks);
        }
        public static cond(mathExpression: string): org.mwg.task.TaskFunctionConditional {
          return org.mwg.task.Actions.newTask().mathConditional(mathExpression);
        }
      }
      export interface Task {
        setWorld(template: string): org.mwg.task.Task;
        setTime(template: string): org.mwg.task.Task;
        defineVar(variableName: string): org.mwg.task.Task;
        asGlobalVar(variableName: string): org.mwg.task.Task;
        addToGlobalVar(variableName: string): org.mwg.task.Task;
        asVar(variableName: string): org.mwg.task.Task;
        addToVar(variableName: string): org.mwg.task.Task;
        fromVar(variableName: string): org.mwg.task.Task;
        fromVarAt(variableName: string, index: number): org.mwg.task.Task;
        inject(inputValue: any): org.mwg.task.Task;
        fromIndex(indexName: string, query: string): org.mwg.task.Task;
        fromIndexAll(indexName: string): org.mwg.task.Task;
        indexNode(indexName: string, flatKeyAttributes: string): org.mwg.task.Task;
        indexNodeAt(world: string, time: string, indexName: string, flatKeyAttributes: string): org.mwg.task.Task;
        localIndex(indexedRelation: string, flatKeyAttributes: string, varNodeToAdd: string): org.mwg.task.Task;
        unindexNodeAt(world: string, time: string, indexName: string, flatKeyAttributes: string): org.mwg.task.Task;
        unindexNode(indexName: string, flatKeyAttributes: string): org.mwg.task.Task;
        localUnindex(indexedRelation: string, flatKeyAttributes: string, varNodeToAdd: string): org.mwg.task.Task;
        selectWith(name: string, pattern: string): org.mwg.task.Task;
        selectWithout(name: string, pattern: string): org.mwg.task.Task;
        select(filterFunction: org.mwg.task.TaskFunctionSelect): org.mwg.task.Task;
        selectObject(filterFunction: org.mwg.task.TaskFunctionSelectObject): org.mwg.task.Task;
        selectWhere(subTask: org.mwg.task.Task): org.mwg.task.Task;
        traverse(relationName: string): org.mwg.task.Task;
        get(name: string): org.mwg.task.Task;
        traverseOrKeep(relationName: string): org.mwg.task.Task;
        traverseIndex(indexName: string, ...queryArgs: string[]): org.mwg.task.Task;
        traverseIndexAll(indexName: string): org.mwg.task.Task;
        map(mapFunction: org.mwg.task.TaskFunctionMap<any, any>): org.mwg.task.Task;
        group(groupFunction: org.mwg.task.TaskFunctionGroup): org.mwg.task.Task;
        groupWhere(groupSubTask: org.mwg.task.Task): org.mwg.task.Task;
        foreach(subTask: org.mwg.task.Task): org.mwg.task.Task;
        flatmap(subTask: org.mwg.task.Task): org.mwg.task.Task;
        foreachPar(subTask: org.mwg.task.Task): org.mwg.task.Task;
        flatmapPar(subTask: org.mwg.task.Task): org.mwg.task.Task;
        subTask(subTask: org.mwg.task.Task): org.mwg.task.Task;
        isolate(subTask: org.mwg.task.Task): org.mwg.task.Task;
        subTasks(subTasks: org.mwg.task.Task[]): org.mwg.task.Task;
        subTasksPar(subTasks: org.mwg.task.Task[]): org.mwg.task.Task;
        ifThen(cond: org.mwg.task.TaskFunctionConditional, then: org.mwg.task.Task): org.mwg.task.Task;
        ifThenElse(cond: org.mwg.task.TaskFunctionConditional, thenSub: org.mwg.task.Task, elseSub: org.mwg.task.Task): org.mwg.task.Task;
        whileDo(cond: org.mwg.task.TaskFunctionConditional, then: org.mwg.task.Task): org.mwg.task.Task;
        doWhile(then: org.mwg.task.Task, conditional: org.mwg.task.TaskFunctionConditional): org.mwg.task.Task;
        then(action: org.mwg.task.Action): org.mwg.task.Task;
        save(): org.mwg.task.Task;
        clear(): org.mwg.task.Task;
        newNode(): org.mwg.task.Task;
        newTypedNode(typeNode: string): org.mwg.task.Task;
        setProperty(propertyName: string, propertyType: number, variableNameToSet: string): org.mwg.task.Task;
        forceProperty(propertyName: string, propertyType: number, variableNameToSet: string): org.mwg.task.Task;
        removeProperty(propertyName: string): org.mwg.task.Task;
        add(relationName: string, variableToAdd: string): org.mwg.task.Task;
        addTo(relationName: string, variableTarget: string): org.mwg.task.Task;
        properties(): org.mwg.task.Task;
        propertiesWithTypes(filterType: number): org.mwg.task.Task;
        remove(relationName: string, variableNameToRemove: string): org.mwg.task.Task;
        jump(time: string): org.mwg.task.Task;
        parse(flat: string): org.mwg.task.Task;
        action(name: string, params: string): org.mwg.task.Task;
        split(splitPattern: string): org.mwg.task.Task;
        lookup(nodeId: string): org.mwg.task.Task;
        lookupAll(nodeId: string): org.mwg.task.Task;
        math(expression: string): org.mwg.task.Task;
        loop(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task;
        loopPar(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task;
        print(name: string): org.mwg.task.Task;
        hook(hookFactory: org.mwg.task.TaskHookFactory): org.mwg.task.Task;
        execute(graph: org.mwg.Graph, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
        executeSync(graph: org.mwg.Graph): org.mwg.task.TaskResult<any>;
        executeWith(graph: org.mwg.Graph, initial: any, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
        prepareWith(graph: org.mwg.Graph, initial: any, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): org.mwg.task.TaskContext;
        executeUsing(preparedContext: org.mwg.task.TaskContext): void;
        executeFrom(parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, affinity: number, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
        executeFromUsing(parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, affinity: number, contextInitializer: org.mwg.Callback<org.mwg.task.TaskContext>, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void;
        emptyResult(): org.mwg.task.TaskResult<any>;
        mathConditional(mathExpression: string): org.mwg.task.TaskFunctionConditional;
      }
      export interface TaskAction {
        eval(context: org.mwg.task.TaskContext): void;
      }
      export interface TaskActionFactory {
        (params: string[]): org.mwg.task.TaskAction;
      }
      export interface TaskContext {
        graph(): org.mwg.Graph;
        world(): number;
        setWorld(world: number): void;
        time(): number;
        setTime(time: number): void;
        variable(name: string): org.mwg.task.TaskResult<any>;
        wrap(input: any): org.mwg.task.TaskResult<any>;
        wrapClone(input: any): org.mwg.task.TaskResult<any>;
        newResult(): org.mwg.task.TaskResult<any>;
        declareVariable(name: string): void;
        defineVariable(name: string, initialResult: any): void;
        defineVariableForSubTask(name: string, initialResult: any): void;
        setGlobalVariable(name: string, value: any): void;
        setVariable(name: string, value: any): void;
        addToGlobalVariable(name: string, value: any): void;
        addToVariable(name: string, value: any): void;
        result(): org.mwg.task.TaskResult<any>;
        resultAsNodes(): org.mwg.task.TaskResult<org.mwg.Node>;
        resultAsStrings(): org.mwg.task.TaskResult<string>;
        continueTask(): void;
        continueWith(nextResult: org.mwg.task.TaskResult<any>): void;
        template(input: string): string;
        hook(): org.mwg.task.TaskHook;
      }
      export interface TaskFunctionConditional {
        (context: org.mwg.task.TaskContext): boolean;
      }
      export interface TaskFunctionGroup {
        (nodes: org.mwg.Node): number;
      }
      export interface TaskFunctionMap<A, B> {
        (node: A): B;
      }
      export interface TaskFunctionSelect {
        (node: org.mwg.Node): boolean;
      }
      export interface TaskFunctionSelectObject {
        (object: any, context: org.mwg.task.TaskContext): boolean;
      }
      export interface TaskHook {
        start(initialContext: org.mwg.task.TaskContext): void;
        beforeAction(action: org.mwg.task.TaskAction, context: org.mwg.task.TaskContext): void;
        afterAction(action: org.mwg.task.TaskAction, context: org.mwg.task.TaskContext): void;
        beforeTask(parentContext: org.mwg.task.TaskContext, context: org.mwg.task.TaskContext): void;
        afterTask(context: org.mwg.task.TaskContext): void;
        end(finalContext: org.mwg.task.TaskContext): void;
      }
      export interface TaskHookFactory {
        newHook(): org.mwg.task.TaskHook;
      }
      export interface TaskResult<A> {
        iterator(): org.mwg.task.TaskResultIterator<any>;
        get(index: number): A;
        set(index: number, input: A): void;
        allocate(index: number): void;
        add(input: A): void;
        clear(): void;
        clone(): org.mwg.task.TaskResult<A>;
        free(): void;
        size(): number;
        asArray(): any[];
      }
      export interface TaskResultIterator<A> {
        next(): A;
      }
    }
    export module utility {
      export class Base64 {
        private static dictionary : number[] = ['A'.charCodeAt(0), 'B'.charCodeAt(0), 'C'.charCodeAt(0), 'D'.charCodeAt(0), 'E'.charCodeAt(0), 'F'.charCodeAt(0), 'G'.charCodeAt(0), 'H'.charCodeAt(0), 'I'.charCodeAt(0), 'J'.charCodeAt(0), 'K'.charCodeAt(0), 'L'.charCodeAt(0), 'M'.charCodeAt(0), 'N'.charCodeAt(0), 'O'.charCodeAt(0), 'P'.charCodeAt(0), 'Q'.charCodeAt(0), 'R'.charCodeAt(0), 'S'.charCodeAt(0), 'T'.charCodeAt(0), 'U'.charCodeAt(0), 'V'.charCodeAt(0), 'W'.charCodeAt(0), 'X'.charCodeAt(0), 'Y'.charCodeAt(0), 'Z'.charCodeAt(0), 'a'.charCodeAt(0), 'b'.charCodeAt(0), 'c'.charCodeAt(0), 'd'.charCodeAt(0), 'e'.charCodeAt(0), 'f'.charCodeAt(0), 'g'.charCodeAt(0), 'h'.charCodeAt(0), 'i'.charCodeAt(0), 'j'.charCodeAt(0), 'k'.charCodeAt(0), 'l'.charCodeAt(0), 'm'.charCodeAt(0), 'n'.charCodeAt(0), 'o'.charCodeAt(0), 'p'.charCodeAt(0), 'q'.charCodeAt(0), 'r'.charCodeAt(0), 's'.charCodeAt(0), 't'.charCodeAt(0), 'u'.charCodeAt(0), 'v'.charCodeAt(0), 'w'.charCodeAt(0), 'x'.charCodeAt(0), 'y'.charCodeAt(0), 'z'.charCodeAt(0), '0'.charCodeAt(0), '1'.charCodeAt(0), '2'.charCodeAt(0), '3'.charCodeAt(0), '4'.charCodeAt(0), '5'.charCodeAt(0), '6'.charCodeAt(0), '7'.charCodeAt(0), '8'.charCodeAt(0), '9'.charCodeAt(0), '+'.charCodeAt(0), '/'.charCodeAt(0)];
        private static powTwo = {0:1,1:2,2:4,3:8,4:16,5:32,6:64,7:128,8:256,9:512,10:1024,11:2048,12:4096,13:8192,14:16384,15:32768,16:65536,17:131072,18:262144,19:524288,20:1048576,21:2097152,22:4194304,23:8388608,24:16777216,25:33554432,26:67108864,27:134217728,28:268435456,29:536870912,30:1073741824,31:2147483648,32:4294967296,33:8589934592,34:17179869184,35:34359738368,36:68719476736,37:137438953472,38:274877906944,39:549755813888,40:1099511627776,41:2199023255552,42:4398046511104,43:8796093022208,44:17592186044416,45:35184372088832,46:70368744177664,47:140737488355328,48:281474976710656,49:562949953421312,50:1125899906842624,51:2251799813685248,52:4503599627370496,53:9007199254740992};
        private static longIndexes : Long[] = [Long.fromNumber(0), Long.fromNumber(1), Long.fromNumber(2), Long.fromNumber(3), Long.fromNumber(4), Long.fromNumber(5), Long.fromNumber(6), Long.fromNumber(7), Long.fromNumber(8), Long.fromNumber(9), Long.fromNumber(10), Long.fromNumber(11), Long.fromNumber(12), Long.fromNumber(13), Long.fromNumber(14), Long.fromNumber(15), Long.fromNumber(16), Long.fromNumber(17), Long.fromNumber(18), Long.fromNumber(19), Long.fromNumber(20), Long.fromNumber(21), Long.fromNumber(22), Long.fromNumber(23), Long.fromNumber(24), Long.fromNumber(25), Long.fromNumber(26), Long.fromNumber(27), Long.fromNumber(28), Long.fromNumber(29), Long.fromNumber(30), Long.fromNumber(31), Long.fromNumber(32), Long.fromNumber(33), Long.fromNumber(34), Long.fromNumber(35), Long.fromNumber(36), Long.fromNumber(37), Long.fromNumber(38), Long.fromNumber(39), Long.fromNumber(40), Long.fromNumber(41), Long.fromNumber(42), Long.fromNumber(43), Long.fromNumber(44), Long.fromNumber(45), Long.fromNumber(46), Long.fromNumber(47), Long.fromNumber(48), Long.fromNumber(49), Long.fromNumber(50), Long.fromNumber(51), Long.fromNumber(52), Long.fromNumber(53), Long.fromNumber(54), Long.fromNumber(55), Long.fromNumber(56), Long.fromNumber(57), Long.fromNumber(58), Long.fromNumber(59), Long.fromNumber(60), Long.fromNumber(61), Long.fromNumber(62), Long.fromNumber(63)];
        public static encodeLongToBuffer(l:number, buffer:org.mwg.struct.Buffer) {
        var empty=true;
        var tmp = l;
        if(l < 0) {
        tmp = -tmp;
        }
        for (var i = 47; i >= 5; i -= 6) {
        if (!(empty && ((tmp / Base64.powTwo[i]) & 0x3F) == 0)) {
        empty = false;
        buffer.write(Base64.dictionary[(tmp / Base64.powTwo[i]) & 0x3F]);
        }
        }
        buffer.write(Base64.dictionary[(tmp & 0x1F)*2 + (l<0?1:0)]);
        }
        public static encodeIntToBuffer(l:number, buffer:org.mwg.struct.Buffer) {
        var empty=true;
        var tmp = l;
        if(l < 0) {
        tmp = -tmp;
        }
        for (var i = 29; i >= 5; i -= 6) {
        if (!(empty && ((tmp / Base64.powTwo[i]) & 0x3F) == 0)) {
        empty = false;
        buffer.write(Base64.dictionary[(tmp / Base64.powTwo[i]) & 0x3F]);
        }
        }
        buffer.write(Base64.dictionary[(tmp & 0x1F)*2 + (l<0?1:0)]);
        }
        public static decodeToLong(s : org.mwg.struct.Buffer) {
        return Base64.decodeToLongWithBounds(s, 0, s.length());
        }
        public static decodeToLongWithBounds(s:org.mwg.struct.Buffer, offsetBegin:number, offsetEnd:number) {
        var result = Long.ZERO;
        result = result.add(Base64.longIndexes[Base64.dictionary.indexOf(s.read((offsetEnd - 1))) & 0xFF].shiftRightUnsigned(1));
        for (var i = 1; i < (offsetEnd - offsetBegin); i++) {
        result = result.add(Base64.longIndexes[Base64.dictionary.indexOf(s.read((offsetEnd - 1) - i)) & 0xFF].shiftLeft((6 * i)-1));
        }
        if (((Base64.dictionary.indexOf(s.read((offsetEnd - 1))) & 0xFF) & 0x1) != 0) {
        result = result.mul(-1);
        }
        return result.toNumber();
        }
        public static decodeToInt(s : org.mwg.struct.Buffer) {
        return Base64.decodeToIntWithBounds(s, 0, s.length());
        }
        public static decodeToIntWithBounds(s:org.mwg.struct.Buffer, offsetBegin:number, offsetEnd:number) {
        var result = 0;
        result += (Base64.dictionary.indexOf(s.read((offsetEnd - 1))) & 0xFF) / 2;
        for (var i = 1; i < (offsetEnd - offsetBegin); i++) {
        result += (Base64.dictionary.indexOf(s.read((offsetEnd - 1) - i)) & 0xFF) * Base64.powTwo[(6 * i)-1];
        }
        if (((Base64.dictionary.indexOf(s.read((offsetEnd - 1))) & 0xFF) & 0x1) != 0) {
        result = -result;
        }
        return result;
        }
        public static encodeDoubleToBuffer(d : number, buffer : org.mwg.struct.Buffer) {
        var result = [];
        var floatArr = new Float64Array(1);
        var bytes = new Uint8Array(floatArr.buffer);
        floatArr[0] = d;
        var exponent = (((bytes[7] & 0x7f) * 16) | bytes[6] / 16) - 0x3ff;
        var signAndExp = (((bytes[7] / 128) & 0x1) * 2048) + (exponent + 1023);
        //encode sign + exp
        result.push(Base64.dictionary[(signAndExp / 64) & 0x3F]);
        result.push(Base64.dictionary[signAndExp & 0x3F]);
        result.push(Base64.dictionary[bytes[6] & 0x0F]);
        result.push(Base64.dictionary[(bytes[5] / 4) & 0x3F]);
        result.push(Base64.dictionary[((bytes[5] & 0x3) * 16) | (bytes[4] / 16)]);
        result.push(Base64.dictionary[((bytes[4] & 0x0F) * 4) | (bytes[3] / 64)]);
        result.push(Base64.dictionary[(bytes[3] & 0x3F)]);
        result.push(Base64.dictionary[(bytes[2] / 4) & 0x3F]);
        result.push(Base64.dictionary[((bytes[2] & 0x3) * 16) | (bytes[1] / 16)]);
        result.push(Base64.dictionary[((bytes[1] & 0x0F) * 4) | (bytes[0] / 64)]);
        result.push(Base64.dictionary[(bytes[0] & 0x3F)]);
        var indexMax = result.length;
        while (indexMax >= 3 && result[i] == 65) {
        indexMax--;
        }
        for (var i = 0; i < indexMax; i++) {
        buffer.write(result[i]);
        }
        }
        public static decodeToDouble(s : org.mwg.struct.Buffer) {
        return Base64.decodeToDoubleWithBounds(s, 0, s.length());
        }
        public static decodeToDoubleWithBounds(s : org.mwg.struct.Buffer, offsetBegin : number, offsetEnd : number) {
        var signAndExp = ((Base64.dictionary.indexOf(s.read(offsetBegin)) & 0xFF) * 64) + (Base64.dictionary.indexOf(s.read(offsetBegin + 1)) & 0xFF);
        var sign = ((signAndExp & 0x800) != 0 ? -1 : 1);
        var exp = signAndExp & 0x7FF;
        //Mantisse
        var mantissaBits = 0;
        for (var i = 2; i < (offsetEnd - offsetBegin); i++) {
        mantissaBits += (Base64.dictionary.indexOf(s.read(offsetBegin + i)) & 0xFF) * Base64.powTwo[48 - (6 * (i-2))];
        }
        return (exp != 0) ? sign * Math.pow(2, exp - 1023) * (1 + (mantissaBits / Math.pow(2, 52))) : sign * Math.pow(2, -1022) * (0 + (mantissaBits / Math.pow(2, 52)));
        }
        public static encodeBoolArrayToBuffer(boolArr : Array<boolean>, buffer : org.mwg.struct.Buffer) {
        var tmpVal = 0;
        for (var i = 0; i < boolArr.length; i++) {
        tmpVal = tmpVal | ((boolArr[i] ? 1 : 0) * Base64.powTwo[i % 6]);
        if (i % 6 == 5 || i == boolArr.length - 1) {
        buffer.write(Base64.dictionary[tmpVal]);
        tmpVal = 0;
        }
        }
        }
        public static decodeBoolArray(s : org.mwg.struct.Buffer, arraySize : number) {
        return Base64.decodeToBoolArrayWithBounds(s, 0, s.length(), arraySize);
        }
        public static decodeToBoolArrayWithBounds(s : org.mwg.struct.Buffer, offsetBegin : number, offsetEnd : number, arraySize : number) {
        var resultTmp : any[] = [];
        for (var i = 0; i < (offsetEnd - offsetBegin); i++) {
        var bitarray = Base64.dictionary.indexOf(s.read(offsetBegin + i)) & 0xFF;
        for (var bit_i = 0; bit_i < 6; bit_i++) {
        if ((6 * i) + bit_i < arraySize) {
        resultTmp[(6 * i) + bit_i] = (bitarray & (1 * Base64.powTwo[bit_i])) != 0;
        } else {
        break;
        }
        }
        }
        return resultTmp;
        }
        public static encodeStringToBuffer(s : string, buffer : org.mwg.struct.Buffer) {
        var sLength = s.length;
        var currentSourceChar : number;
        var currentEncodedChar = 0;
        var freeBitsInCurrentChar = 6;
        for(var charIdx = 0; charIdx < sLength; charIdx++) {
        currentSourceChar = s.charCodeAt(charIdx);
        if(freeBitsInCurrentChar == 6) {
        buffer.write(Base64.dictionary[(currentSourceChar / 4) & 0x3F]);
        currentEncodedChar = (currentSourceChar & 0x3) * 16;
        freeBitsInCurrentChar = 4;
        } else if(freeBitsInCurrentChar == 4) {
        buffer.write(Base64.dictionary[(currentEncodedChar | ((currentSourceChar / 16) & 0xF)) & 0x3F]);
        currentEncodedChar = (currentSourceChar & 0xF) * 4;
        freeBitsInCurrentChar = 2;
        } else if(freeBitsInCurrentChar == 2) {
        buffer.write(Base64.dictionary[(currentEncodedChar | ((currentSourceChar / 64) & 0x3)) & 0x3F]);
        buffer.write(Base64.dictionary[currentSourceChar & 0x3F]);
        freeBitsInCurrentChar = 6;
        }
        }
        if(freeBitsInCurrentChar != 6) {
        buffer.write(Base64.dictionary[currentEncodedChar]);
        }
        }
        public static decodeString(s : org.mwg.struct.Buffer) {
        return Base64.decodeToStringWithBounds(s, 0, s.length());
        }
        public static decodeToStringWithBounds(s : org.mwg.struct.Buffer, offsetBegin : number, offsetEnd : number) {
        var result = "";
        var currentSourceChar : number;
        var currentDecodedChar = 0;
        var freeBitsInCurrentChar = 8;
        for(var charIdx = offsetBegin; charIdx < offsetEnd; charIdx++) {
        currentSourceChar = Base64.dictionary.indexOf(s.read(charIdx));
        if(freeBitsInCurrentChar == 8) {
        currentDecodedChar = currentSourceChar * 4;
        freeBitsInCurrentChar = 2;
        } else if(freeBitsInCurrentChar == 2) {
        result += String.fromCharCode(currentDecodedChar | (currentSourceChar / 16));
        currentDecodedChar = (currentSourceChar & 0xF) * 16;
        freeBitsInCurrentChar = 4;
        } else if(freeBitsInCurrentChar == 4) {
        result += String.fromCharCode(currentDecodedChar | (currentSourceChar / 4));
        currentDecodedChar = (currentSourceChar & 0x3) * 64;
        freeBitsInCurrentChar = 6;
        } else if(freeBitsInCurrentChar == 6) {
        result += String.fromCharCode(currentDecodedChar | currentSourceChar);
        freeBitsInCurrentChar = 8;
        }
        }
        return result;
        }
      }
      export class BufferView implements org.mwg.struct.Buffer {
        private _origin: org.mwg.struct.Buffer;
        private _initPos: number;
        private _endPos: number;
        constructor(p_origin: org.mwg.struct.Buffer, p_initPos: number, p_endPos: number) {
          this._origin = p_origin;
          this._initPos = p_initPos;
          this._endPos = p_endPos;
        }
        public write(b: number): void {
          throw new Error("Write operation forbidden during iteration");
        }
        public writeAll(bytes: Int8Array): void {
          throw new Error("Write operation forbidden during iteration");
        }
        public read(position: number): number {
          if (this._initPos + position > this._endPos) {
            throw new Error("" + position);
          }
          return this._origin.read(this._initPos + position);
        }
        public data(): Int8Array {
          return this._origin.slice(this._initPos, this._endPos);
        }
        public length(): number {
          return this._endPos - this._initPos + 1;
        }
        public free(): void {
          throw new Error("Free operation forbidden during iteration");
        }
        public iterator(): org.mwg.struct.BufferIterator {
          throw new Error("iterator creation forbidden forbidden during iteration");
        }
        public removeLast(): void {
          throw new Error("Write operation forbidden during iteration");
        }
        public slice(initPos: number, endPos: number): Int8Array {
          throw new Error("Write operation forbidden during iteration");
        }
      }
      export class DefaultBufferIterator implements org.mwg.struct.BufferIterator {
        private _origin: org.mwg.struct.Buffer;
        private _originSize: number;
        private _cursor: number = -1;
        constructor(p_origin: org.mwg.struct.Buffer) {
          this._origin = p_origin;
          this._originSize = p_origin.length();
        }
        public hasNext(): boolean {
          return this._originSize > 0 && (this._cursor + 1) < this._originSize;
        }
        public next(): org.mwg.struct.Buffer {
          let previousCursor: number = this._cursor;
          while ((this._cursor + 1) < this._originSize) {
            this._cursor++;
            let current: number = this._origin.read(this._cursor);
            if (current == org.mwg.Constants.BUFFER_SEP) {
              return new org.mwg.utility.BufferView(this._origin, previousCursor + 1, this._cursor - 1);
            }
          }
          if (previousCursor < this._originSize) {
            return new org.mwg.utility.BufferView(this._origin, previousCursor + 1, this._cursor);
          }
          return null;
        }
      }
      export class Enforcer {
        private checkers: java.util.Map<string, org.mwg.utility.EnforcerChecker> = new java.util.HashMap<string, org.mwg.utility.EnforcerChecker>();
        public asBool(propertyName: string): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                if (input != null && inputType != org.mwg.Type.BOOL) {
                  throw new Error("Property " + propertyName + " should be Boolean value, currently " + input);
                }
              }            }
          });
        }
        public asString(propertyName: string): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                if (input != null && inputType != org.mwg.Type.STRING) {
                  throw new Error("Property " + propertyName + " should be String value, currently " + input);
                }
              }            }
          });
        }
        public asLong(propertyName: string): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                if (input != null && inputType != org.mwg.Type.LONG && inputType != org.mwg.Type.INT) {
                  throw new Error("Property " + propertyName + " should be long value, currently " + input);
                }
              }            }
          });
        }
        public asLongWithin(propertyName: string, min: number, max: number): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                let inputDouble: number = <number>input;
                if (input != null && ((inputType != org.mwg.Type.LONG && inputType != org.mwg.Type.INT) || inputDouble < min || inputDouble > max)) {
                  throw new Error("Property " + propertyName + " should be long value [" + min + "," + max + "], currently " + input);
                }
              }            }
          });
        }
        public asDouble(propertyName: string): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                if (input != null && (inputType != org.mwg.Type.DOUBLE && inputType != org.mwg.Type.INT && inputType != org.mwg.Type.LONG)) {
                  throw new Error("Property " + propertyName + " should be double value, currently " + input);
                }
              }            }
          });
        }
        public asDoubleWithin(propertyName: string, min: number, max: number): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                let inputDouble: number;
                if (input instanceof Number) {
                  inputDouble = <number><number>input;
                } else if (input instanceof Number) {
                  inputDouble = <number><number>input;
                } else {
                  inputDouble = <number>input;
                }

                if (input != null && ((inputType != org.mwg.Type.DOUBLE && inputType != org.mwg.Type.INT && inputType != org.mwg.Type.LONG) || inputDouble < min || inputDouble > max)) {
                  throw new Error("Property " + propertyName + " should be double value [" + min + "," + max + "], currently " + input);
                }
              }            }
          });
        }
        public asInt(propertyName: string): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                if (input != null && inputType != org.mwg.Type.INT && inputType != org.mwg.Type.LONG) {
                  throw new Error("Property " + propertyName + " should be integer value, currently " + input);
                }
              }            }
          });
        }
        public asIntWithin(propertyName: string, min: number, max: number): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                let inputInt: number = <number>input;
                if (input != null && ((inputType != org.mwg.Type.INT && inputType != org.mwg.Type.LONG) || inputInt < min || inputInt > max)) {
                  throw new Error("Property " + propertyName + " should be integer value [" + min + "," + max + "], currently " + input);
                }
              }            }
          });
        }
        public asIntGreaterOrEquals(propertyName: string, min: number): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                let inputInt: number = <number>input;
                if (input != null && ((inputType != org.mwg.Type.INT && inputType != org.mwg.Type.LONG) || inputInt < min)) {
                  throw new Error("Property " + propertyName + " should be integer value >=" + min + ", currently " + input);
                }
              }            }
          });
        }
        public asDoubleArray(propertyName: string): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                if (input != null && inputType != org.mwg.Type.DOUBLE_ARRAY) {
                  throw new Error("Property " + propertyName + " should be doubleArray value, currently " + input);
                }
              }            }
          });
        }
        public asPositiveInt(propertyName: string): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                let inputInt: number = <number>input;
                if ((input != null && inputType != org.mwg.Type.INT) || inputInt <= 0) {
                  throw new Error("Property " + propertyName + " should be a positive integer, currently " + input);
                }
              }            }
          });
        }
        public asNonNegativeDouble(propertyName: string): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                let inputDouble: number;
                if (input instanceof Number) {
                  inputDouble = <number><number>input;
                } else if (input instanceof Number) {
                  inputDouble = <number><number>input;
                } else {
                  inputDouble = <number>input;
                }

                if (input != null && ((inputType != org.mwg.Type.DOUBLE && inputType != org.mwg.Type.INT && inputType != org.mwg.Type.LONG) || !(inputDouble >= 0))) {
                  throw new Error("Property " + propertyName + " should be a non-negative double, currently " + input);
                }
              }            }
          });
        }
        public asPositiveDouble(propertyName: string): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                let inputDouble: number;
                if (input instanceof Number) {
                  inputDouble = <number><number>input;
                } else if (input instanceof Number) {
                  inputDouble = <number><number>input;
                } else {
                  inputDouble = <number>input;
                }

                if (input != null && ((inputType != org.mwg.Type.DOUBLE && inputType != org.mwg.Type.INT && inputType != org.mwg.Type.LONG) || !(inputDouble > 0))) {
                  throw new Error("Property " + propertyName + " should be a positive double, currently " + input);
                }
              }            }
          });
        }
        public asNonNegativeOrNanDouble(propertyName: string): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                let inputDouble: number;
                if (input instanceof Number) {
                  inputDouble = <number><number>input;
                } else if (input instanceof Number) {
                  inputDouble = <number><number>input;
                } else {
                  inputDouble = <number>input;
                }

                if (input != null && ((inputType != org.mwg.Type.DOUBLE && inputType != org.mwg.Type.INT && inputType != org.mwg.Type.LONG) || inputDouble < 0)) {
                  throw new Error("Property " + propertyName + " should be a positive double, currently " + input);
                }
              }            }
          });
        }
        public asPositiveLong(propertyName: string): org.mwg.utility.Enforcer {
          return this.declare(propertyName, {
            check: function (inputType: number, input: any) {
{
                let inputLong: number;
                if (input instanceof Number) {
                  inputLong = <number><number>input;
                } else {
                  inputLong = <number>input;
                }
                if (input != null && ((inputType != org.mwg.Type.LONG && inputType != org.mwg.Type.INT) || inputLong <= 0)) {
                  throw new Error("Property " + propertyName + " should be a positive long, currently " + input);
                }
              }            }
          });
        }
        public declare(propertyName: string, checker: org.mwg.utility.EnforcerChecker): org.mwg.utility.Enforcer {
          this.checkers.put(propertyName, checker);
          return this;
        }
        public check(propertyName: string, propertyType: number, propertyValue: any): void {
          let checker: org.mwg.utility.EnforcerChecker = this.checkers.get(propertyName);
          if (checker != null) {
            checker.check(propertyType, propertyValue);
          }
        }
      }
      export interface EnforcerChecker {
        check(inputType: number, input: any): void;
      }
      export class HashHelper {
        public static PRIME1 : Long = Long.fromNumber(2654435761, false);
        public static PRIME2 : Long = Long.fromNumber(2246822519, false);
        public static PRIME3 : Long = Long.fromNumber(3266489917, false);
        public static PRIME4 : Long = Long.fromNumber(668265263, false);
        public static PRIME5 : Long = Long.fromNumber(0x165667b1, false);
        private static len: number = 24;
        private static byteTable = function(){
        var table = [];
        var h = Long.fromBits(0xCAAF1684, 0x544B2FBA);
        for (var i = 0; i < 256; i++) {
        for (var j = 0; j < 31; j++) {
        h = h.shiftRightUnsigned(7).xor(h);
        h = h.shiftLeft(11).xor(h);
        h = h.shiftRightUnsigned(10).xor(h);
        }
        table[i] = h.toSigned();
        }
        return table;
        }();
        private static HSTART : Long = Long.fromBits(0xA205B064, 0xBB40E64D);
        private static HMULT : Long = Long.fromBits(0xE116586D,0x6A5D39EA);
        public static longHash(number: number, max: number): number {
          if (max <= 0) {
          throw new Error("Max must be > 0");
          }
          var crc = org.mwg.utility.HashHelper.PRIME5;
          crc = crc.add(number);
          crc = crc.add(crc.shiftLeft(17));
          crc = crc.mul(org.mwg.utility.HashHelper.PRIME4);
          crc = crc.mul(org.mwg.utility.HashHelper.PRIME1);
          crc = crc.add(number);
          crc = crc.add(crc.shiftLeft(17));
          crc = crc.mul(org.mwg.utility.HashHelper.PRIME4);
          crc = crc.mul(org.mwg.utility.HashHelper.PRIME1);
          crc = crc.add(org.mwg.utility.HashHelper.len);
          crc = crc.xor(crc.shiftRightUnsigned(15));
          crc = crc.mul(org.mwg.utility.HashHelper.PRIME2);
          crc = crc.add(number);
          crc = crc.xor(crc.shiftRightUnsigned(13));
          crc = crc.mul(org.mwg.utility.HashHelper.PRIME3);
          crc = crc.xor(crc.shiftRightUnsigned(16));
          crc = (crc.isNegative()?crc.mul(-1):crc);
          crc = crc.mod(max);
          return crc.toNumber();
        }
        public static tripleHash(p0: number, p1: number, p2: number, p3: number, max: number): number {
          if (max <= 0) {
          throw new Error("Max must be > 0");
          }
          var v1 = org.mwg.utility.HashHelper.PRIME5;
          var v2 = v1.mul(org.mwg.utility.HashHelper.PRIME2).add(org.mwg.utility.HashHelper.len);
          var v3 = v2.mul(org.mwg.utility.HashHelper.PRIME3);
          var v4 = v3.mul(org.mwg.utility.HashHelper.PRIME4);
          v1 = v1.shiftLeft(13).or(v1.shiftRightUnsigned(51)).add(Long.fromNumber(p1, false));
          v2 = v2.shiftLeft(11).or(v2.shiftRightUnsigned(53)).add(Long.fromNumber(p2, false));
          v3 = v3.shiftLeft(17).or(v3.shiftRightUnsigned(47)).add(Long.fromNumber(p3, false));
          v4 = v4.shiftLeft(19).or(v4.shiftRightUnsigned(45)).add(Long.fromNumber(p0, false));
          v1 = v1.add(v1.shiftLeft(17).or(v1.shiftRightUnsigned(47)));
          v2 = v2.add(v2.shiftLeft(19).or(v2.shiftRightUnsigned(45)));
          v3 = v3.add(v3.shiftLeft(13).or(v3.shiftRightUnsigned(51)));
          v4 = v4.add(v4.shiftLeft(11).or(v4.shiftRightUnsigned(53)));
          v1 = v1.mul(org.mwg.utility.HashHelper.PRIME1).add(Long.fromNumber(p1, false));
          v2 = v2.mul(org.mwg.utility.HashHelper.PRIME1).add(Long.fromNumber(p2, false));
          v3 = v3.mul(org.mwg.utility.HashHelper.PRIME1).add(Long.fromNumber(p3, false));
          v4 = v4.mul(org.mwg.utility.HashHelper.PRIME1).add(org.mwg.utility.HashHelper.PRIME5);
          v1 = v1.mul(org.mwg.utility.HashHelper.PRIME2);
          v2 = v2.mul(org.mwg.utility.HashHelper.PRIME2);
          v3 = v3.mul(org.mwg.utility.HashHelper.PRIME2);
          v4 = v4.mul(org.mwg.utility.HashHelper.PRIME2);
          v1 = v1.add(v1.shiftLeft(11).or(v1.shiftRightUnsigned(53)));
          v2 = v2.add(v2.shiftLeft(17).or(v2.shiftRightUnsigned(47)));
          v3 = v3.add(v3.shiftLeft(19).or(v3.shiftRightUnsigned(45)));
          v4 = v4.add(v4.shiftLeft(13).or(v4.shiftRightUnsigned(51)));
          v1 = v1.mul(org.mwg.utility.HashHelper.PRIME3);
          v2 = v2.mul(org.mwg.utility.HashHelper.PRIME3);
          v3 = v3.mul(org.mwg.utility.HashHelper.PRIME3);
          v4 = v4.mul(org.mwg.utility.HashHelper.PRIME3);
          var crc = v1;
          crc = crc.add(v2.shiftLeft(3).or(v2.shiftRightUnsigned(61)));
          crc = crc.add(v3.shiftLeft(6).or(v3.shiftRightUnsigned(58)));
          crc = crc.add(v4.shiftLeft(9).or(v4.shiftRightUnsigned(55)));
          crc = crc.xor(crc.shiftRightUnsigned(11));
          crc = crc.add(org.mwg.utility.HashHelper.PRIME4.add(org.mwg.utility.HashHelper.len).mul(org.mwg.utility.HashHelper.PRIME1));
          crc = crc.xor(crc.shiftRightUnsigned(15));
          crc = crc.mul(org.mwg.utility.HashHelper.PRIME2);
          crc = crc.xor(crc.shiftRightUnsigned(13));
          crc = (crc.isNegative()?crc.mul(-1):crc);
          crc = crc.mod(max);
          return crc.toNumber();
        }
        public static rand(): number {
          return Math.random() * 1000000
        }
        public static equals(src: string, other: string): boolean {
          return src === other
        }
        public static DOUBLE_MIN_VALUE(): number {
          return Number.MIN_VALUE;
        }
        public static DOUBLE_MAX_VALUE(): number {
          return Number.MAX_VALUE;
        }
        public static isDefined(param: any): boolean {
          return param != undefined && param != null;
        }
        public static hash(data: string): number {
          var h = org.mwg.utility.HashHelper.HSTART;
          var dataLength = data.length;
          for (var i = 0; i < dataLength; i++) {
          h = h.mul(org.mwg.utility.HashHelper.HMULT).xor(org.mwg.utility.HashHelper.byteTable[data.charCodeAt(i) & 0xff]);
          }
          return h.mod(org.mwg.core.CoreConstants.END_OF_TIME).toNumber();
        }
        public static hashBytes(data: Int8Array): number {
          var h = org.mwg.utility.HashHelper.HSTART;
          var dataLength = data.length;
          for (var i = 0; i < dataLength; i++) {
          h = h.mul(org.mwg.utility.HashHelper.HMULT).xor(org.mwg.utility.HashHelper.byteTable[data[i] & 0xff]);
          }
          return h.mod(org.mwg.core.CoreConstants.END_OF_TIME).toNumber();
        }
      }
      export class KeyHelper {
        public static keyToBuffer(buffer: org.mwg.struct.Buffer, chunkType: number, world: number, time: number, id: number): void {
          buffer.write(chunkType);
          buffer.write(org.mwg.Constants.KEY_SEP);
          org.mwg.utility.Base64.encodeLongToBuffer(world, buffer);
          buffer.write(org.mwg.Constants.KEY_SEP);
          org.mwg.utility.Base64.encodeLongToBuffer(time, buffer);
          buffer.write(org.mwg.Constants.KEY_SEP);
          org.mwg.utility.Base64.encodeLongToBuffer(id, buffer);
        }
      }
      export class VerboseHook implements org.mwg.task.TaskHook {
        private ctxIdents: java.util.Map<org.mwg.task.TaskContext, number> = new java.util.HashMap<org.mwg.task.TaskContext, number>();
        public start(initialContext: org.mwg.task.TaskContext): void {
          this.ctxIdents.put(initialContext, 0);
          console.log("StartTask:" + initialContext);
        }
        public beforeAction(action: org.mwg.task.TaskAction, context: org.mwg.task.TaskContext): void {
          let currentPrefix: number = this.ctxIdents.get(context);
          for (let i: number = 0; i < currentPrefix; i++) {
            console.log("\t");
          }
          let taskName: string = action.toString();
          console.log(context.template(taskName));
        }
        public afterAction(action: org.mwg.task.TaskAction, context: org.mwg.task.TaskContext): void {}
        public beforeTask(parentContext: org.mwg.task.TaskContext, context: org.mwg.task.TaskContext): void {
          let currentPrefix: number = this.ctxIdents.get(parentContext);
          this.ctxIdents.put(context, currentPrefix + 1);
        }
        public afterTask(context: org.mwg.task.TaskContext): void {
          this.ctxIdents.remove(context);
        }
        public end(finalContext: org.mwg.task.TaskContext): void {
          console.log("EndTask:" + finalContext.toString());
        }
      }
      export class VerboseHookFactory implements org.mwg.task.TaskHookFactory {
        public newHook(): org.mwg.task.TaskHook {
          return new org.mwg.utility.VerboseHook();
        }
      }
      export class VerbosePlugin extends org.mwg.plugin.AbstractPlugin {
        constructor() {
          super();
          this.declareTaskHookFactory(new org.mwg.utility.VerboseHookFactory());
        }
      }
    }
  }
}
