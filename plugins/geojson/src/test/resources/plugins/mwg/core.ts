/// <reference path="api.ts" />

module org {
  export module mwg {
    export module core {
      export class BlackHoleStorage implements org.mwg.plugin.Storage {
        private _graph: org.mwg.Graph;
        private prefix: number = 0;
        public get(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<org.mwg.struct.Buffer>): void {
          let result: org.mwg.struct.Buffer = this._graph.newBuffer();
          let it: org.mwg.struct.BufferIterator = keys.iterator();
          let isFirst: boolean = true;
          while (it.hasNext()) {
            let tempView: org.mwg.struct.Buffer = it.next();
            if (isFirst) {
              isFirst = false;
            } else {
              result.write(org.mwg.core.CoreConstants.BUFFER_SEP);
            }
          }
          callback(result);
        }
        public put(stream: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void {
          if (callback != null) {
            callback(true);
          }
        }
        public remove(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void {
          callback(true);
        }
        public connect(graph: org.mwg.Graph, callback: org.mwg.Callback<boolean>): void {
          this._graph = graph;
          callback(true);
        }
        public lock(callback: org.mwg.Callback<org.mwg.struct.Buffer>): void {
          let buffer: org.mwg.struct.Buffer = this._graph.newBuffer();
          org.mwg.utility.Base64.encodeIntToBuffer(this.prefix, buffer);
          this.prefix++;
          callback(buffer);
        }
        public unlock(previousLock: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void {
          callback(true);
        }
        public disconnect(callback: org.mwg.Callback<boolean>): void {
          this._graph = null;
          callback(true);
        }
      }
      export class Builder implements org.mwg.GraphBuilder.InternalBuilder {
        public newGraph(p_storage: org.mwg.plugin.Storage, p_readOnly: boolean, p_scheduler: org.mwg.plugin.Scheduler, p_plugins: org.mwg.plugin.Plugin[], p_memorySize: number): org.mwg.Graph {
          let storage: org.mwg.plugin.Storage = p_storage;
          if (storage == null) {
            storage = new org.mwg.core.BlackHoleStorage();
          }
          if (p_readOnly) {
            storage = new org.mwg.core.utility.ReadOnlyStorage(storage);
          }
          let scheduler: org.mwg.plugin.Scheduler = p_scheduler;
          if (scheduler == null) {
            scheduler = new org.mwg.core.scheduler.TrampolineScheduler();
          }
          let memorySize: number = p_memorySize;
          if (memorySize == -1) {
            memorySize = 100000;
          }
          return new org.mwg.core.CoreGraph(storage, memorySize, scheduler, p_plugins);
        }
        public newTask(): org.mwg.task.Task {
          return new org.mwg.core.task.CoreTask();
        }
      }
      export class CoreConstants extends org.mwg.Constants {
        public static PREFIX_TO_SAVE_SIZE: number = 2;
        public static NULL_KEY: Float64Array = new Float64Array([org.mwg.Constants.END_OF_TIME, org.mwg.Constants.END_OF_TIME, org.mwg.Constants.END_OF_TIME]);
        public static GLOBAL_UNIVERSE_KEY: Float64Array = new Float64Array([org.mwg.Constants.NULL_LONG, org.mwg.Constants.NULL_LONG, org.mwg.Constants.NULL_LONG]);
        public static GLOBAL_DICTIONARY_KEY: Float64Array = new Float64Array([org.mwg.Constants.NULL_LONG, 0, 0]);
        public static GLOBAL_INDEX_KEY: Float64Array = new Float64Array([org.mwg.Constants.NULL_LONG, 1, 0]);
        public static INDEX_ATTRIBUTE: string = "index";
        public static DISCONNECTED_ERROR: string = "Please connect your graph, prior to any usage of it";
        public static SCALE_1: number = 1000;
        public static SCALE_2: number = 10000;
        public static SCALE_3: number = 100000;
        public static SCALE_4: number = 1000000;
        public static DEAD_NODE_ERROR: string = "This Node has been tagged destroyed, please don't use it anymore!";
      }
      export class CoreGraph implements org.mwg.Graph {
        private _storage: org.mwg.plugin.Storage;
        private _space: org.mwg.chunk.ChunkSpace;
        private _scheduler: org.mwg.plugin.Scheduler;
        private _resolver: org.mwg.plugin.Resolver;
        private _nodeTypes: java.util.Map<number, org.mwg.plugin.NodeFactory>;
        private _taskActions: java.util.Map<string, org.mwg.task.TaskActionFactory>;
        private _isConnected: java.util.concurrent.atomic.AtomicBoolean;
        private _lock: java.util.concurrent.atomic.AtomicBoolean;
        private _plugins: org.mwg.plugin.Plugin[];
        private _memoryFactory: org.mwg.plugin.MemoryFactory;
        private _hookFactory: org.mwg.task.TaskHookFactory;
        private _prefix: number = null;
        private _nodeKeyCalculator: org.mwg.chunk.GenChunk = null;
        private _worldKeyCalculator: org.mwg.chunk.GenChunk = null;
        constructor(p_storage: org.mwg.plugin.Storage, memorySize: number, p_scheduler: org.mwg.plugin.Scheduler, p_plugins: org.mwg.plugin.Plugin[]) {
          let selfPointer: org.mwg.Graph = this;
          let memoryFactory: org.mwg.plugin.MemoryFactory = null;
          let resolverFactory: org.mwg.plugin.ResolverFactory = null;
          let hookFactory: org.mwg.task.TaskHookFactory = null;
          if (p_plugins != null) {
            for (let i: number = 0; i < p_plugins.length; i++) {
              let loopPlugin: org.mwg.plugin.Plugin = p_plugins[i];
              let loopMF: org.mwg.plugin.MemoryFactory = loopPlugin.memoryFactory();
              let loopHF: org.mwg.task.TaskHookFactory = loopPlugin.hookFactory();
              if (loopMF != null) {
                memoryFactory = loopMF;
              }
              let loopRF: org.mwg.plugin.ResolverFactory = loopPlugin.resolverFactory();
              if (loopRF != null) {
                resolverFactory = loopRF;
              }
              if (loopHF != null) {
                hookFactory = loopHF;
              }
            }
          }
          if (memoryFactory == null) {
            memoryFactory = new org.mwg.core.memory.HeapMemoryFactory();
          }
          if (resolverFactory == null) {
            resolverFactory = {
              newResolver: function (storage: org.mwg.plugin.Storage, space: org.mwg.chunk.ChunkSpace) {
{
                  return new org.mwg.core.MWGResolver(storage, space, selfPointer);
                }              }
            };
          }
          this._hookFactory = hookFactory;
          this._storage = p_storage;
          this._memoryFactory = memoryFactory;
          this._space = memoryFactory.newSpace(memorySize, selfPointer);
          this._resolver = resolverFactory.newResolver(this._storage, this._space);
          this._scheduler = p_scheduler;
          this._taskActions = new java.util.HashMap<string, org.mwg.task.TaskActionFactory>();
          org.mwg.core.task.CoreTask.fillDefault(this._taskActions);
          if (p_plugins != null) {
            this._nodeTypes = new java.util.HashMap<number, org.mwg.plugin.NodeFactory>();
            for (let i: number = 0; i < p_plugins.length; i++) {
              let loopPlugin: org.mwg.plugin.Plugin = p_plugins[i];
              let plugin_names: string[] = loopPlugin.nodeTypes();
              for (let j: number = 0; j < plugin_names.length; j++) {
                let plugin_name: string = plugin_names[j];
                this._nodeTypes.put(this._resolver.stringToHash(plugin_name, false), loopPlugin.nodeType(plugin_name));
              }
              let task_names: string[] = loopPlugin.taskActionTypes();
              for (let j: number = 0; j < task_names.length; j++) {
                let task_name: string = task_names[j];
                this._taskActions.put(task_name, loopPlugin.taskActionType(task_name));
              }
            }
          } else {
            this._nodeTypes = null;
          }
          this._isConnected = new java.util.concurrent.atomic.AtomicBoolean(false);
          this._lock = new java.util.concurrent.atomic.AtomicBoolean(false);
          this._plugins = p_plugins;
        }
        public fork(world: number): number {
          let childWorld: number = this._worldKeyCalculator.newKey();
          this._resolver.initWorld(world, childWorld);
          return childWorld;
        }
        public newNode(world: number, time: number): org.mwg.Node {
          if (!this._isConnected.get()) {
            throw new Error(org.mwg.core.CoreConstants.DISCONNECTED_ERROR);
          }
          let newNode: org.mwg.Node = new org.mwg.core.CoreNode(world, time, this._nodeKeyCalculator.newKey(), this);
          this._resolver.initNode(newNode, org.mwg.Constants.NULL_LONG);
          return newNode;
        }
        public newTypedNode(world: number, time: number, nodeType: string): org.mwg.Node {
          if (nodeType == null) {
            throw new Error("nodeType should not be null");
          }
          if (!this._isConnected.get()) {
            throw new Error(org.mwg.core.CoreConstants.DISCONNECTED_ERROR);
          }
          let extraCode: number = this._resolver.stringToHash(nodeType, false);
          let resolvedFactory: org.mwg.plugin.NodeFactory = this.factoryByCode(extraCode);
          let newNode: org.mwg.plugin.AbstractNode;
          if (resolvedFactory == null) {
            console.log("WARNING: UnKnow NodeType " + nodeType + ", missing plugin configuration in the builder ? Using generic node as a fallback");
            newNode = new org.mwg.core.CoreNode(world, time, this._nodeKeyCalculator.newKey(), this);
          } else {
            newNode = <org.mwg.plugin.AbstractNode>resolvedFactory(world, time, this._nodeKeyCalculator.newKey(), this);
          }
          this._resolver.initNode(newNode, extraCode);
          return newNode;
        }
        public cloneNode(origin: org.mwg.Node): org.mwg.Node {
          if (origin == null) {
            throw new Error("origin node should not be null");
          }
          if (!this._isConnected.get()) {
            throw new Error(org.mwg.core.CoreConstants.DISCONNECTED_ERROR);
          }
          let casted: org.mwg.plugin.AbstractNode = <org.mwg.plugin.AbstractNode>origin;
          casted.cacheLock();
          if (casted._dead) {
            casted.cacheUnlock();
            throw new Error(org.mwg.core.CoreConstants.DEAD_NODE_ERROR + " node id: " + casted.id());
          } else {
            this._space.mark(casted._index_stateChunk);
            this._space.mark(casted._index_superTimeTree);
            this._space.mark(casted._index_timeTree);
            this._space.mark(casted._index_worldOrder);
            let worldOrderChunk: org.mwg.chunk.WorldOrderChunk = <org.mwg.chunk.WorldOrderChunk>this._space.get(casted._index_worldOrder);
            let resolvedFactory: org.mwg.plugin.NodeFactory = this.factoryByCode(worldOrderChunk.extra());
            let newNode: org.mwg.plugin.AbstractNode;
            if (resolvedFactory == null) {
              newNode = new org.mwg.core.CoreNode(origin.world(), origin.time(), origin.id(), this);
            } else {
              newNode = <org.mwg.plugin.AbstractNode>resolvedFactory(origin.world(), origin.time(), origin.id(), this);
            }
            newNode._index_stateChunk = casted._index_stateChunk;
            newNode._index_timeTree = casted._index_timeTree;
            newNode._index_superTimeTree = casted._index_superTimeTree;
            newNode._index_worldOrder = casted._index_worldOrder;
            newNode._world_magic = casted._world_magic;
            newNode._super_time_magic = casted._super_time_magic;
            newNode._time_magic = casted._time_magic;
            casted.cacheUnlock();
            return newNode;
          }
        }
        public factoryByCode(code: number): org.mwg.plugin.NodeFactory {
          if (this._nodeTypes != null && code != org.mwg.Constants.NULL_LONG) {
            return this._nodeTypes.get(code);
          } else {
            return null;
          }
        }
        public taskAction(taskActionName: string): org.mwg.task.TaskActionFactory {
          if (this._taskActions != null && taskActionName != null) {
            return this._taskActions.get(taskActionName);
          } else {
            return null;
          }
        }
        public taskHookFactory(): org.mwg.task.TaskHookFactory {
          return this._hookFactory;
        }
        public lookup<A extends org.mwg.Node>(world: number, time: number, id: number, callback: org.mwg.Callback<A>): void {
          if (!this._isConnected.get()) {
            throw new Error(org.mwg.core.CoreConstants.DISCONNECTED_ERROR);
          }
          this._resolver.lookup(world, time, id, callback);
        }
        public lookupAll(world: number, time: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void {
          if (!this._isConnected.get()) {
            throw new Error(org.mwg.core.CoreConstants.DISCONNECTED_ERROR);
          }
          this._resolver.lookupAll(world, time, ids, callback);
        }
        public save(callback: org.mwg.Callback<boolean>): void {
          this._space.save(callback);
        }
        public connect(callback: org.mwg.Callback<boolean>): void {
          let selfPointer: org.mwg.core.CoreGraph = this;
          while (selfPointer._lock.compareAndSet(false, true)) 
          if (this._isConnected.compareAndSet(false, true)) {
            selfPointer._scheduler.start();
            selfPointer._storage.connect(selfPointer, (connection : boolean) => {
{
                selfPointer._storage.lock((prefixBuf : org.mwg.struct.Buffer) => {
{
                    this._prefix = <number>org.mwg.utility.Base64.decodeToIntWithBounds(prefixBuf, 0, prefixBuf.length());
                    prefixBuf.free();
                    let connectionKeys: org.mwg.struct.Buffer = selfPointer.newBuffer();
                    org.mwg.utility.KeyHelper.keyToBuffer(connectionKeys, org.mwg.chunk.ChunkType.GEN_CHUNK, org.mwg.Constants.BEGINNING_OF_TIME, org.mwg.Constants.NULL_LONG, this._prefix);
                    connectionKeys.write(org.mwg.core.CoreConstants.BUFFER_SEP);
                    org.mwg.utility.KeyHelper.keyToBuffer(connectionKeys, org.mwg.chunk.ChunkType.GEN_CHUNK, org.mwg.Constants.END_OF_TIME, org.mwg.Constants.NULL_LONG, this._prefix);
                    connectionKeys.write(org.mwg.core.CoreConstants.BUFFER_SEP);
                    org.mwg.utility.KeyHelper.keyToBuffer(connectionKeys, org.mwg.chunk.ChunkType.WORLD_ORDER_CHUNK, 0, 0, org.mwg.Constants.NULL_LONG);
                    connectionKeys.write(org.mwg.core.CoreConstants.BUFFER_SEP);
                    org.mwg.utility.KeyHelper.keyToBuffer(connectionKeys, org.mwg.chunk.ChunkType.STATE_CHUNK, org.mwg.core.CoreConstants.GLOBAL_DICTIONARY_KEY[0], org.mwg.core.CoreConstants.GLOBAL_DICTIONARY_KEY[1], org.mwg.core.CoreConstants.GLOBAL_DICTIONARY_KEY[2]);
                    connectionKeys.write(org.mwg.core.CoreConstants.BUFFER_SEP);
                    selfPointer._storage.get(connectionKeys, (payloads : org.mwg.struct.Buffer) => {
{
                        connectionKeys.free();
                        if (payloads != null) {
                          let it: org.mwg.struct.BufferIterator = payloads.iterator();
                          let view1: org.mwg.struct.Buffer = it.next();
                          let view2: org.mwg.struct.Buffer = it.next();
                          let view3: org.mwg.struct.Buffer = it.next();
                          let view4: org.mwg.struct.Buffer = it.next();
                          let noError: boolean = true;
                          try {
                            let globalWorldOrder: org.mwg.chunk.WorldOrderChunk = <org.mwg.chunk.WorldOrderChunk>selfPointer._space.createAndMark(org.mwg.chunk.ChunkType.WORLD_ORDER_CHUNK, 0, 0, org.mwg.Constants.NULL_LONG);
                            if (view3.length() > 0) {
                              globalWorldOrder.load(view3);
                            }
                            let globalDictionaryChunk: org.mwg.chunk.StateChunk = <org.mwg.chunk.StateChunk>selfPointer._space.createAndMark(org.mwg.chunk.ChunkType.STATE_CHUNK, org.mwg.core.CoreConstants.GLOBAL_DICTIONARY_KEY[0], org.mwg.core.CoreConstants.GLOBAL_DICTIONARY_KEY[1], org.mwg.core.CoreConstants.GLOBAL_DICTIONARY_KEY[2]);
                            if (view4.length() > 0) {
                              globalDictionaryChunk.load(view4);
                            }
                            selfPointer._worldKeyCalculator = <org.mwg.chunk.GenChunk>selfPointer._space.createAndMark(org.mwg.chunk.ChunkType.GEN_CHUNK, org.mwg.Constants.END_OF_TIME, org.mwg.Constants.NULL_LONG, this._prefix);
                            if (view2.length() > 0) {
                              selfPointer._worldKeyCalculator.load(view2);
                            }
                            selfPointer._nodeKeyCalculator = <org.mwg.chunk.GenChunk>selfPointer._space.createAndMark(org.mwg.chunk.ChunkType.GEN_CHUNK, org.mwg.Constants.BEGINNING_OF_TIME, org.mwg.Constants.NULL_LONG, this._prefix);
                            if (view1.length() > 0) {
                              selfPointer._nodeKeyCalculator.load(view1);
                            }
                            selfPointer._resolver.init();
                            if (this._plugins != null) {
                              for (let i: number = 0; i < this._plugins.length; i++) {
                                let nodeTypes: string[] = this._plugins[i].nodeTypes();
                                if (nodeTypes != null) {
                                  for (let j: number = 0; j < nodeTypes.length; j++) {
                                    let pluginName: string = nodeTypes[j];
                                    selfPointer._resolver.stringToHash(pluginName, true);
                                  }
                                }
                              }
                            }
                          } catch ($ex$) {
                            if ($ex$ instanceof Error) {
                              var e: Error = <Error>$ex$;
{
                                console.error(e);
                                noError = false;
                              }                            } else {
                              throw $ex$;
                            }
                          }
                          payloads.free();
                          selfPointer._lock.set(true);
                          if (org.mwg.utility.HashHelper.isDefined(callback)) {
                            callback(noError);
                          }
                        } else {
                          selfPointer._lock.set(true);
                          if (org.mwg.utility.HashHelper.isDefined(callback)) {
                            callback(false);
                          }
                        }
                      }                    });
                  }                });
              }            });
          } else {
            selfPointer._lock.set(true);
            if (org.mwg.utility.HashHelper.isDefined(callback)) {
              callback(null);
            }
          }
        }
        public disconnect(callback: org.mwg.Callback<any>): void {
          while (this._lock.compareAndSet(false, true)) 
          if (this._isConnected.compareAndSet(true, false)) {
            let selfPointer: org.mwg.core.CoreGraph = this;
            selfPointer._scheduler.stop();
            this.save((result : boolean) => {
{
                selfPointer._space.freeAll();
                if (selfPointer._storage != null) {
                  let prefixBuf: org.mwg.struct.Buffer = selfPointer.newBuffer();
                  org.mwg.utility.Base64.encodeIntToBuffer(selfPointer._prefix, prefixBuf);
                  selfPointer._storage.unlock(prefixBuf, (result : boolean) => {
{
                      prefixBuf.free();
                      selfPointer._storage.disconnect((result : boolean) => {
{
                          selfPointer._lock.set(true);
                          if (org.mwg.utility.HashHelper.isDefined(callback)) {
                            callback(result);
                          }
                        }                      });
                    }                  });
                } else {
                  selfPointer._lock.set(true);
                  if (org.mwg.utility.HashHelper.isDefined(callback)) {
                    callback(result);
                  }
                }
              }            });
          } else {
            this._lock.set(true);
            if (org.mwg.utility.HashHelper.isDefined(callback)) {
              callback(null);
            }
          }
        }
        public newBuffer(): org.mwg.struct.Buffer {
          return this._memoryFactory.newBuffer();
        }
        public newQuery(): org.mwg.Query {
          return new org.mwg.core.CoreQuery(this, this._resolver);
        }
        public index(indexName: string, toIndexNode: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void {
          this.indexAt(toIndexNode.world(), toIndexNode.time(), indexName, toIndexNode, flatKeyAttributes, callback);
        }
        public indexAt(world: number, time: number, indexName: string, nodeToIndex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void {
          if (indexName == null) {
            throw new Error("indexName should not be null");
          }
          if (nodeToIndex == null) {
            throw new Error("toIndexNode should not be null");
          }
          if (flatKeyAttributes == null) {
            throw new Error("flatKeyAttributes should not be null");
          }
          this.getIndexOrCreate(world, time, indexName, true, (foundIndex : org.mwg.Node) => {
{
              if (foundIndex == null) {
                throw new Error("Index creation failed, cache is probably full !!!");
              }
              foundIndex.index(org.mwg.core.CoreConstants.INDEX_ATTRIBUTE, nodeToIndex, flatKeyAttributes, (result : boolean) => {
{
                  foundIndex.free();
                  if (org.mwg.utility.HashHelper.isDefined(callback)) {
                    callback(result);
                  }
                }              });
            }          });
        }
        public unindex(indexName: string, nodeToUnindex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void {
          this.unindexAt(nodeToUnindex.world(), nodeToUnindex.time(), indexName, nodeToUnindex, flatKeyAttributes, callback);
        }
        public unindexAt(world: number, time: number, indexName: string, nodeToUnindex: org.mwg.Node, flatKeyAttributes: string, callback: org.mwg.Callback<boolean>): void {
          if (indexName == null) {
            throw new Error("indexName should not be null");
          }
          if (nodeToUnindex == null) {
            throw new Error("toIndexNode should not be null");
          }
          if (flatKeyAttributes == null) {
            throw new Error("flatKeyAttributes should not be null");
          }
          this.getIndexOrCreate(world, time, indexName, false, (foundIndex : org.mwg.Node) => {
{
              if (foundIndex != null) {
                foundIndex.unindex(org.mwg.core.CoreConstants.INDEX_ATTRIBUTE, nodeToUnindex, flatKeyAttributes, (result : boolean) => {
{
                    foundIndex.free();
                    if (org.mwg.utility.HashHelper.isDefined(callback)) {
                      callback(result);
                    }
                  }                });
              } else {
                if (org.mwg.utility.HashHelper.isDefined(callback)) {
                  callback(false);
                }
              }
            }          });
        }
        public indexes(world: number, time: number, callback: org.mwg.Callback<string[]>): void {
          let selfPointer: org.mwg.core.CoreGraph = this;
          this._resolver.lookup(world, time, org.mwg.core.CoreConstants.END_OF_TIME, (globalIndexNodeUnsafe : org.mwg.Node) => {
{
              if (globalIndexNodeUnsafe == null) {
                callback(new Array<string>(0));
              } else {
                let globalIndexContent: org.mwg.struct.LongLongMap = <org.mwg.struct.LongLongMap>globalIndexNodeUnsafe.get(org.mwg.core.CoreConstants.INDEX_ATTRIBUTE);
                if (globalIndexContent == null) {
                  globalIndexNodeUnsafe.free();
                  callback(new Array<string>(0));
                } else {
                  let result: string[] = new Array<string>(<number>globalIndexContent.size());
                  let resultIndex: Int32Array = new Int32Array([0]);
                  globalIndexContent.each((key : number, value : number) => {
{
                      result[resultIndex[0]] = selfPointer._resolver.hashToString(key);
                      resultIndex[0]++;
                    }                  });
                  globalIndexNodeUnsafe.free();
                  callback(result);
                }
              }
            }          });
        }
        public find(world: number, time: number, indexName: string, query: string, callback: org.mwg.Callback<org.mwg.Node[]>): void {
          if (indexName == null) {
            throw new Error("indexName should not be null");
          }
          if (query == null) {
            throw new Error("query should not be null");
          }
          this.getIndexOrCreate(world, time, indexName, false, (foundIndex : org.mwg.Node) => {
{
              if (foundIndex == null) {
                if (org.mwg.utility.HashHelper.isDefined(callback)) {
                  callback(new Array<org.mwg.Node>(0));
                }
              } else {
                foundIndex.find(org.mwg.core.CoreConstants.INDEX_ATTRIBUTE, query, (collectedNodes : org.mwg.Node[]) => {
{
                    foundIndex.free();
                    if (org.mwg.utility.HashHelper.isDefined(callback)) {
                      callback(collectedNodes);
                    }
                  }                });
              }
            }          });
        }
        public findByQuery(query: org.mwg.Query, callback: org.mwg.Callback<org.mwg.Node[]>): void {
          if (query == null) {
            throw new Error("query should not be null");
          }
          if (query.world() == org.mwg.Constants.NULL_LONG) {
            throw new Error("Please fill world parameter in query before first usage!");
          }
          if (query.time() == org.mwg.Constants.NULL_LONG) {
            throw new Error("Please fill time parameter in query before first usage!");
          }
          if (query.indexName() == null) {
            throw new Error("Please fill indexName parameter in query before first usage!");
          }
          this.getIndexOrCreate(query.world(), query.time(), query.indexName(), false, (foundIndex : org.mwg.Node) => {
{
              if (foundIndex == null) {
                if (org.mwg.utility.HashHelper.isDefined(callback)) {
                  callback(new Array<org.mwg.Node>(0));
                }
              } else {
                query.setIndexName(org.mwg.core.CoreConstants.INDEX_ATTRIBUTE);
                foundIndex.findByQuery(query, (collectedNodes : org.mwg.Node[]) => {
{
                    foundIndex.free();
                    if (org.mwg.utility.HashHelper.isDefined(callback)) {
                      callback(collectedNodes);
                    }
                  }                });
              }
            }          });
        }
        public findAll(world: number, time: number, indexName: string, callback: org.mwg.Callback<org.mwg.Node[]>): void {
          if (indexName == null) {
            throw new Error("indexName should not be null");
          }
          this.getIndexOrCreate(world, time, indexName, false, (foundIndex : org.mwg.Node) => {
{
              if (foundIndex == null) {
                if (org.mwg.utility.HashHelper.isDefined(callback)) {
                  callback(new Array<org.mwg.Node>(0));
                }
              } else {
                foundIndex.findAll(org.mwg.core.CoreConstants.INDEX_ATTRIBUTE, (collectedNodes : org.mwg.Node[]) => {
{
                    foundIndex.free();
                    if (org.mwg.utility.HashHelper.isDefined(callback)) {
                      callback(collectedNodes);
                    }
                  }                });
              }
            }          });
        }
        public getIndexNode(world: number, time: number, indexName: string, callback: org.mwg.Callback<org.mwg.Node>): void {
          if (indexName == null) {
            throw new Error("indexName should not be null");
          }
          this.getIndexOrCreate(world, time, indexName, false, callback);
        }
        private getIndexOrCreate(world: number, time: number, indexName: string, createIfNull: boolean, callback: org.mwg.Callback<org.mwg.Node>): void {
          let selfPointer: org.mwg.core.CoreGraph = this;
          let indexNameCoded: number = this._resolver.stringToHash(indexName, createIfNull);
          this._resolver.lookup(world, time, org.mwg.core.CoreConstants.END_OF_TIME, (globalIndexNodeUnsafe : org.mwg.Node) => {
{
              if (globalIndexNodeUnsafe == null && !createIfNull) {
                callback(null);
              } else {
                let globalIndexContent: org.mwg.struct.LongLongMap;
                if (globalIndexNodeUnsafe == null) {
                  globalIndexNodeUnsafe = new org.mwg.core.CoreNode(world, time, org.mwg.core.CoreConstants.END_OF_TIME, selfPointer);
                  selfPointer._resolver.initNode(globalIndexNodeUnsafe, org.mwg.core.CoreConstants.NULL_LONG);
                  globalIndexContent = <org.mwg.struct.LongLongMap>globalIndexNodeUnsafe.getOrCreate(org.mwg.core.CoreConstants.INDEX_ATTRIBUTE, org.mwg.Type.LONG_TO_LONG_MAP);
                } else {
                  globalIndexContent = <org.mwg.struct.LongLongMap>globalIndexNodeUnsafe.get(org.mwg.core.CoreConstants.INDEX_ATTRIBUTE);
                }
                let indexId: number = globalIndexContent.get(indexNameCoded);
                globalIndexNodeUnsafe.free();
                if (indexId == org.mwg.core.CoreConstants.NULL_LONG) {
                  if (createIfNull) {
                    let newIndexNode: org.mwg.Node = selfPointer.newNode(world, time);
                    newIndexNode.getOrCreate(org.mwg.core.CoreConstants.INDEX_ATTRIBUTE, org.mwg.Type.LONG_TO_LONG_ARRAY_MAP);
                    indexId = newIndexNode.id();
                    globalIndexContent.put(indexNameCoded, indexId);
                    callback(newIndexNode);
                  } else {
                    callback(null);
                  }
                } else {
                  selfPointer._resolver.lookup(world, time, indexId, callback);
                }
              }
            }          });
        }
        public newCounter(expectedCountCalls: number): org.mwg.DeferCounter {
          return new org.mwg.core.utility.CoreDeferCounter(expectedCountCalls);
        }
        public newSyncCounter(expectedCountCalls: number): org.mwg.DeferCounterSync {
          return new org.mwg.core.utility.CoreDeferCounterSync(expectedCountCalls);
        }
        public resolver(): org.mwg.plugin.Resolver {
          return this._resolver;
        }
        public scheduler(): org.mwg.plugin.Scheduler {
          return this._scheduler;
        }
        public space(): org.mwg.chunk.ChunkSpace {
          return this._space;
        }
        public storage(): org.mwg.plugin.Storage {
          return this._storage;
        }
        public freeNodes(nodes: org.mwg.Node[]): void {
          if (nodes != null) {
            for (let i: number = 0; i < nodes.length; i++) {
              if (nodes[i] != null) {
                nodes[i].free();
              }
            }
          }
        }
      }
      export class CoreNode extends org.mwg.plugin.AbstractNode {
        constructor(p_world: number, p_time: number, p_id: number, p_graph: org.mwg.Graph) {
          super(p_world, p_time, p_id, p_graph);
        }
      }
      export class CoreQuery implements org.mwg.Query {
        private _resolver: org.mwg.plugin.Resolver;
        private _graph: org.mwg.Graph;
        private capacity: number = 1;
        private _attributes: Float64Array = new Float64Array(this.capacity);
        private _values: string[] = new Array<string>(this.capacity);
        private size: number = 0;
        private _hash: number;
        private _world: number = org.mwg.Constants.NULL_LONG;
        private _time: number = org.mwg.Constants.NULL_LONG;
        private _indexName: string = null;
        constructor(graph: org.mwg.Graph, p_resolver: org.mwg.plugin.Resolver) {
          this._graph = graph;
          this._resolver = p_resolver;
          this._hash = null;
        }
        public parse(flatQuery: string): org.mwg.Query {
          let cursor: number = 0;
          let currentKey: number = org.mwg.Constants.NULL_LONG;
          let lastElemStart: number = 0;
          while (cursor < flatQuery.length) {
            if (flatQuery.charAt(cursor) == org.mwg.Constants.QUERY_KV_SEP) {
              if (lastElemStart != -1) {
                currentKey = this._resolver.stringToHash(flatQuery.substring(lastElemStart, cursor).trim(), false);
              }
              lastElemStart = cursor + 1;
            } else if (flatQuery.charAt(cursor) == org.mwg.Constants.QUERY_SEP) {
              if (currentKey != org.mwg.Constants.NULL_LONG) {
                this.internal_add(currentKey, flatQuery.substring(lastElemStart, cursor).trim());
              }
              currentKey = org.mwg.Constants.NULL_LONG;
              lastElemStart = cursor + 1;
            }

            cursor++;
          }
          if (currentKey != org.mwg.Constants.NULL_LONG) {
            this.internal_add(currentKey, flatQuery.substring(lastElemStart, cursor).trim());
          }
          return this;
        }
        public add(attributeName: string, value: string): org.mwg.Query {
          this.internal_add(this._resolver.stringToHash(attributeName.trim(), false), value);
          return this;
        }
        public setWorld(initialWorld: number): org.mwg.Query {
          this._world = initialWorld;
          return this;
        }
        public world(): number {
          return this._world;
        }
        public setTime(initialTime: number): org.mwg.Query {
          this._time = initialTime;
          return this;
        }
        public time(): number {
          return this._time;
        }
        public setIndexName(indexName: string): org.mwg.Query {
          this._indexName = indexName;
          return this;
        }
        public indexName(): string {
          return this._indexName;
        }
        public hash(): number {
          if (this._hash == null) {
            this.compute();
          }
          return this._hash;
        }
        public attributes(): Float64Array {
          return this._attributes;
        }
        public values(): any[] {
          return this._values;
        }
        private internal_add(att: number, val: string): void {
          if (this.size == this.capacity) {
            let temp_capacity: number = this.capacity * 2;
            let temp_attributes: Float64Array = new Float64Array(temp_capacity);
            let temp_values: string[] = new Array<string>(temp_capacity);
            java.lang.System.arraycopy(this._attributes, 0, temp_attributes, 0, this.capacity);
            java.lang.System.arraycopy(this._values, 0, temp_values, 0, this.capacity);
            this._attributes = temp_attributes;
            this._values = temp_values;
            this.capacity = temp_capacity;
          }
          this._attributes[this.size] = att;
          this._values[this.size] = val;
          this.size++;
        }
        private compute(): void {
          for (let i: number = (this.size - 1); i >= 0; i--) {
            for (let j: number = 1; j <= i; j++) {
              if (this._attributes[j - 1] > this._attributes[j]) {
                let tempK: number = this._attributes[j - 1];
                let tempV: string = this._values[j - 1];
                this._attributes[j - 1] = this._attributes[j];
                this._values[j - 1] = this._values[j];
                this._attributes[j] = tempK;
                this._values[j] = tempV;
              }
            }
          }
          let buf: org.mwg.struct.Buffer = this._graph.newBuffer();
          for (let i: number = 0; i < this.size; i++) {
            org.mwg.utility.Base64.encodeLongToBuffer(this._attributes[i], buf);
            let loopValue: any = this._values[i];
            if (loopValue != null) {
              org.mwg.utility.Base64.encodeStringToBuffer(this._values[i], buf);
            }
          }
          this._hash = org.mwg.utility.HashHelper.hashBytes(buf.data());
          buf.free();
        }
      }
      export class MWGResolver implements org.mwg.plugin.Resolver {
        private _storage: org.mwg.plugin.Storage;
        private _space: org.mwg.chunk.ChunkSpace;
        private _graph: org.mwg.Graph;
        private dictionary: org.mwg.chunk.StateChunk;
        private globalWorldOrderChunk: org.mwg.chunk.WorldOrderChunk;
        private static KEY_SIZE: number = 3;
        constructor(p_storage: org.mwg.plugin.Storage, p_space: org.mwg.chunk.ChunkSpace, p_graph: org.mwg.Graph) {
          this._space = p_space;
          this._storage = p_storage;
          this._graph = p_graph;
        }
        public init(): void {
          this.dictionary = <org.mwg.chunk.StateChunk>this._space.getAndMark(org.mwg.chunk.ChunkType.STATE_CHUNK, org.mwg.core.CoreConstants.GLOBAL_DICTIONARY_KEY[0], org.mwg.core.CoreConstants.GLOBAL_DICTIONARY_KEY[1], org.mwg.core.CoreConstants.GLOBAL_DICTIONARY_KEY[2]);
          this.globalWorldOrderChunk = <org.mwg.chunk.WorldOrderChunk>this._space.getAndMark(org.mwg.chunk.ChunkType.WORLD_ORDER_CHUNK, 0, 0, org.mwg.Constants.NULL_LONG);
        }
        public typeName(node: org.mwg.Node): string {
          return this.hashToString(this.typeCode(node));
        }
        public typeCode(node: org.mwg.Node): number {
          let casted: org.mwg.plugin.AbstractNode = <org.mwg.plugin.AbstractNode>node;
          let worldOrderChunk: org.mwg.chunk.WorldOrderChunk = <org.mwg.chunk.WorldOrderChunk>this._space.get(casted._index_worldOrder);
          if (worldOrderChunk == null) {
            return org.mwg.Constants.NULL_LONG;
          }
          return worldOrderChunk.extra();
        }
        public initNode(node: org.mwg.Node, codeType: number): void {
          let casted: org.mwg.plugin.AbstractNode = <org.mwg.plugin.AbstractNode>node;
          let cacheEntry: org.mwg.chunk.StateChunk = <org.mwg.chunk.StateChunk>this._space.createAndMark(org.mwg.chunk.ChunkType.STATE_CHUNK, node.world(), node.time(), node.id());
          this._space.notifyUpdate(cacheEntry.index());
          let superTimeTree: org.mwg.chunk.TimeTreeChunk = <org.mwg.chunk.TimeTreeChunk>this._space.createAndMark(org.mwg.chunk.ChunkType.TIME_TREE_CHUNK, node.world(), org.mwg.Constants.NULL_LONG, node.id());
          superTimeTree.insert(node.time());
          let timeTree: org.mwg.chunk.TimeTreeChunk = <org.mwg.chunk.TimeTreeChunk>this._space.createAndMark(org.mwg.chunk.ChunkType.TIME_TREE_CHUNK, node.world(), node.time(), node.id());
          timeTree.insert(node.time());
          let objectWorldOrder: org.mwg.chunk.WorldOrderChunk = <org.mwg.chunk.WorldOrderChunk>this._space.createAndMark(org.mwg.chunk.ChunkType.WORLD_ORDER_CHUNK, 0, 0, node.id());
          objectWorldOrder.put(node.world(), node.time());
          if (codeType != org.mwg.Constants.NULL_LONG) {
            objectWorldOrder.setExtra(codeType);
          }
          casted._index_stateChunk = cacheEntry.index();
          casted._index_timeTree = timeTree.index();
          casted._index_superTimeTree = superTimeTree.index();
          casted._index_worldOrder = objectWorldOrder.index();
          casted._world_magic = -1;
          casted._super_time_magic = -1;
          casted._time_magic = -1;
          casted.init();
        }
        public initWorld(parentWorld: number, childWorld: number): void {
          this.globalWorldOrderChunk.put(childWorld, parentWorld);
        }
        public freeNode(node: org.mwg.Node): void {
          let casted: org.mwg.plugin.AbstractNode = <org.mwg.plugin.AbstractNode>node;
          casted.cacheLock();
          if (!casted._dead) {
            this._space.unmark(casted._index_stateChunk);
            this._space.unmark(casted._index_timeTree);
            this._space.unmark(casted._index_superTimeTree);
            this._space.unmark(casted._index_worldOrder);
            casted._dead = true;
          }
          casted.cacheUnlock();
        }
        public lookup<A extends org.mwg.Node>(world: number, time: number, id: number, callback: org.mwg.Callback<A>): void {
          let selfPointer: org.mwg.core.MWGResolver = this;
          try {
            selfPointer._space.getOrLoadAndMark(org.mwg.chunk.ChunkType.WORLD_ORDER_CHUNK, 0, 0, id, (theNodeWorldOrder : org.mwg.chunk.Chunk) => {
{
                if (theNodeWorldOrder == null) {
                  callback(null);
                } else {
                  let closestWorld: number = selfPointer.resolve_world(this.globalWorldOrderChunk, <org.mwg.chunk.WorldOrderChunk>theNodeWorldOrder, time, world);
                  selfPointer._space.getOrLoadAndMark(org.mwg.chunk.ChunkType.TIME_TREE_CHUNK, closestWorld, org.mwg.Constants.NULL_LONG, id, (theNodeSuperTimeTree : org.mwg.chunk.Chunk) => {
{
                      if (theNodeSuperTimeTree == null) {
                        selfPointer._space.unmark(theNodeWorldOrder.index());
                        callback(null);
                      } else {
                        let closestSuperTime: number = (<org.mwg.chunk.TimeTreeChunk>theNodeSuperTimeTree).previousOrEqual(time);
                        if (closestSuperTime == org.mwg.Constants.NULL_LONG) {
                          selfPointer._space.unmark(theNodeSuperTimeTree.index());
                          selfPointer._space.unmark(theNodeWorldOrder.index());
                          callback(null);
                          return;
                        }
                        selfPointer._space.getOrLoadAndMark(org.mwg.chunk.ChunkType.TIME_TREE_CHUNK, closestWorld, closestSuperTime, id, (theNodeTimeTree : org.mwg.chunk.Chunk) => {
{
                            if (theNodeTimeTree == null) {
                              selfPointer._space.unmark(theNodeSuperTimeTree.index());
                              selfPointer._space.unmark(theNodeWorldOrder.index());
                              callback(null);
                            } else {
                              let closestTime: number = (<org.mwg.chunk.TimeTreeChunk>theNodeTimeTree).previousOrEqual(time);
                              if (closestTime == org.mwg.Constants.NULL_LONG) {
                                selfPointer._space.unmark(theNodeTimeTree.index());
                                selfPointer._space.unmark(theNodeSuperTimeTree.index());
                                selfPointer._space.unmark(theNodeWorldOrder.index());
                                callback(null);
                                return;
                              }
                              selfPointer._space.getOrLoadAndMark(org.mwg.chunk.ChunkType.STATE_CHUNK, closestWorld, closestTime, id, (theObjectChunk : org.mwg.chunk.Chunk) => {
{
                                  if (theObjectChunk == null) {
                                    selfPointer._space.unmark(theNodeTimeTree.index());
                                    selfPointer._space.unmark(theNodeSuperTimeTree.index());
                                    selfPointer._space.unmark(theNodeWorldOrder.index());
                                    callback(null);
                                  } else {
                                    let castedNodeWorldOrder: org.mwg.chunk.WorldOrderChunk = <org.mwg.chunk.WorldOrderChunk>theNodeWorldOrder;
                                    let extraCode: number = castedNodeWorldOrder.extra();
                                    let resolvedFactory: org.mwg.plugin.NodeFactory = null;
                                    if (extraCode != org.mwg.Constants.NULL_LONG) {
                                      resolvedFactory = (<org.mwg.core.CoreGraph>selfPointer._graph).factoryByCode(extraCode);
                                    }
                                    let resolvedNode: org.mwg.plugin.AbstractNode;
                                    if (resolvedFactory == null) {
                                      resolvedNode = new org.mwg.core.CoreNode(world, time, id, selfPointer._graph);
                                    } else {
                                      resolvedNode = <org.mwg.plugin.AbstractNode>resolvedFactory(world, time, id, selfPointer._graph);
                                    }
                                    resolvedNode._dead = false;
                                    resolvedNode._index_stateChunk = theObjectChunk.index();
                                    resolvedNode._index_superTimeTree = theNodeSuperTimeTree.index();
                                    resolvedNode._index_timeTree = theNodeTimeTree.index();
                                    resolvedNode._index_worldOrder = theNodeWorldOrder.index();
                                    if (closestWorld == world && closestTime == time) {
                                      resolvedNode._world_magic = -1;
                                      resolvedNode._super_time_magic = -1;
                                      resolvedNode._time_magic = -1;
                                    } else {
                                      resolvedNode._world_magic = (<org.mwg.chunk.WorldOrderChunk>theNodeWorldOrder).magic();
                                      resolvedNode._super_time_magic = (<org.mwg.chunk.TimeTreeChunk>theNodeSuperTimeTree).magic();
                                      resolvedNode._time_magic = (<org.mwg.chunk.TimeTreeChunk>theNodeTimeTree).magic();
                                    }
                                    if (callback != null) {
                                      let casted: org.mwg.Node = resolvedNode;
                                      callback(<A>casted);
                                    }
                                  }
                                }                              });
                            }
                          }                        });
                      }
                    }                  });
                }
              }            });
          } catch ($ex$) {
            if ($ex$ instanceof Error) {
              var e: Error = <Error>$ex$;
{
                console.error(e);
              }            } else {
              throw $ex$;
            }
          }
        }
        private lookupAll_end(finalResult: org.mwg.Node[], callback: org.mwg.Callback<org.mwg.Node[]>, sizeIds: number, worldOrders: org.mwg.chunk.Chunk[], superTimes: org.mwg.chunk.Chunk[], times: org.mwg.chunk.Chunk[], chunks: org.mwg.chunk.Chunk[]): void {
          if (worldOrders != null || superTimes != null || times != null || chunks != null) {
            for (let i: number = 0; i < sizeIds; i++) {
              if (finalResult[i] == null) {
                if (worldOrders != null && worldOrders[i] != null) {
                  this._space.unmark(worldOrders[i].index());
                }
                if (superTimes != null && superTimes[i] != null) {
                  this._space.unmark(superTimes[i].index());
                }
                if (times != null && times[i] != null) {
                  this._space.unmark(times[i].index());
                }
                if (chunks != null && chunks[i] != null) {
                  this._space.unmark(chunks[i].index());
                }
              }
            }
          }
          callback(finalResult);
        }
        public lookupAll(world: number, time: number, ids: Float64Array, callback: org.mwg.Callback<org.mwg.Node[]>): void {
          let selfPointer: org.mwg.core.MWGResolver = this;
          let idsSize: number = ids.length;
          let finalResult: org.mwg.Node[] = new Array<org.mwg.Node>(idsSize);
          for (let i: number = 0; i < idsSize; i++) {
            finalResult[i] = null;
          }
          let isEmpty: boolean[] = [true];
          let keys: Float64Array = new Float64Array(idsSize * org.mwg.Constants.KEY_SIZE);
          for (let i: number = 0; i < idsSize; i++) {
            isEmpty[0] = false;
            keys[i * org.mwg.Constants.KEY_SIZE] = org.mwg.chunk.ChunkType.WORLD_ORDER_CHUNK;
            keys[(i * org.mwg.Constants.KEY_SIZE) + 1] = 0;
            keys[(i * org.mwg.Constants.KEY_SIZE) + 2] = 0;
            keys[(i * org.mwg.Constants.KEY_SIZE) + 3] = ids[i];
          }
          if (isEmpty[0]) {
            this.lookupAll_end(finalResult, callback, idsSize, null, null, null, null);
          } else {
            selfPointer._space.getOrLoadAndMarkAll(keys, (theNodeWorldOrders : org.mwg.chunk.Chunk[]) => {
{
                if (theNodeWorldOrders == null) {
                  this.lookupAll_end(finalResult, callback, idsSize, null, null, null, null);
                } else {
                  isEmpty[0] = true;
                  for (let i: number = 0; i < idsSize; i++) {
                    if (theNodeWorldOrders[i] != null) {
                      isEmpty[0] = false;
                      keys[i * org.mwg.Constants.KEY_SIZE] = org.mwg.chunk.ChunkType.TIME_TREE_CHUNK;
                      keys[(i * org.mwg.Constants.KEY_SIZE) + 1] = selfPointer.resolve_world(this.globalWorldOrderChunk, <org.mwg.chunk.WorldOrderChunk>theNodeWorldOrders[i], time, world);
                      keys[(i * org.mwg.Constants.KEY_SIZE) + 2] = org.mwg.Constants.NULL_LONG;
                    } else {
                      keys[i * org.mwg.Constants.KEY_SIZE] = -1;
                    }
                  }
                  if (isEmpty[0]) {
                    this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, null, null, null);
                  } else {
                    selfPointer._space.getOrLoadAndMarkAll(keys, (theNodeSuperTimeTrees : org.mwg.chunk.Chunk[]) => {
{
                        if (theNodeSuperTimeTrees == null) {
                          this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, null, null, null);
                        } else {
                          isEmpty[0] = true;
                          for (let i: number = 0; i < idsSize; i++) {
                            if (theNodeSuperTimeTrees[i] != null) {
                              let closestSuperTime: number = (<org.mwg.chunk.TimeTreeChunk>theNodeSuperTimeTrees[i]).previousOrEqual(time);
                              if (closestSuperTime == org.mwg.Constants.NULL_LONG) {
                                keys[i * org.mwg.Constants.KEY_SIZE] = -1;
                              } else {
                                isEmpty[0] = false;
                                keys[(i * org.mwg.Constants.KEY_SIZE) + 2] = closestSuperTime;
                              }
                            } else {
                              keys[i * org.mwg.Constants.KEY_SIZE] = -1;
                            }
                          }
                          if (isEmpty[0]) {
                            this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, null, null);
                          } else {
                            selfPointer._space.getOrLoadAndMarkAll(keys, (theNodeTimeTrees : org.mwg.chunk.Chunk[]) => {
{
                                if (theNodeTimeTrees == null) {
                                  this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, null, null);
                                } else {
                                  isEmpty[0] = true;
                                  for (let i: number = 0; i < idsSize; i++) {
                                    if (theNodeTimeTrees[i] != null) {
                                      let closestTime: number = (<org.mwg.chunk.TimeTreeChunk>theNodeTimeTrees[i]).previousOrEqual(time);
                                      if (closestTime == org.mwg.Constants.NULL_LONG) {
                                        keys[i * org.mwg.Constants.KEY_SIZE] = -1;
                                      } else {
                                        isEmpty[0] = false;
                                        keys[(i * org.mwg.Constants.KEY_SIZE)] = org.mwg.chunk.ChunkType.STATE_CHUNK;
                                        keys[(i * org.mwg.Constants.KEY_SIZE) + 2] = closestTime;
                                      }
                                    } else {
                                      keys[i * org.mwg.Constants.KEY_SIZE] = -1;
                                    }
                                  }
                                  if (isEmpty[0]) {
                                    this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, theNodeTimeTrees, null);
                                  } else {
                                    selfPointer._space.getOrLoadAndMarkAll(keys, (theObjectChunks : org.mwg.chunk.Chunk[]) => {
{
                                        if (theObjectChunks == null) {
                                          this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, theNodeTimeTrees, null);
                                        } else {
                                          for (let i: number = 0; i < idsSize; i++) {
                                            if (theObjectChunks[i] != null) {
                                              let castedNodeWorldOrder: org.mwg.chunk.WorldOrderChunk = <org.mwg.chunk.WorldOrderChunk>theNodeWorldOrders[i];
                                              let extraCode: number = castedNodeWorldOrder.extra();
                                              let resolvedFactory: org.mwg.plugin.NodeFactory = null;
                                              if (extraCode != org.mwg.Constants.NULL_LONG) {
                                                resolvedFactory = (<org.mwg.core.CoreGraph>selfPointer._graph).factoryByCode(extraCode);
                                              }
                                              let resolvedNode: org.mwg.plugin.AbstractNode;
                                              if (resolvedFactory == null) {
                                                resolvedNode = new org.mwg.core.CoreNode(world, time, ids[i], selfPointer._graph);
                                              } else {
                                                resolvedNode = <org.mwg.plugin.AbstractNode>resolvedFactory(world, time, ids[i], selfPointer._graph);
                                              }
                                              resolvedNode._dead = false;
                                              resolvedNode._index_stateChunk = theObjectChunks[i].index();
                                              resolvedNode._index_superTimeTree = theNodeSuperTimeTrees[i].index();
                                              resolvedNode._index_timeTree = theNodeTimeTrees[i].index();
                                              resolvedNode._index_worldOrder = theNodeWorldOrders[i].index();
                                              if (theObjectChunks[i].world() == world && theObjectChunks[i].time() == time) {
                                                resolvedNode._world_magic = -1;
                                                resolvedNode._super_time_magic = -1;
                                                resolvedNode._time_magic = -1;
                                              } else {
                                                resolvedNode._world_magic = (<org.mwg.chunk.WorldOrderChunk>theNodeWorldOrders[i]).magic();
                                                resolvedNode._super_time_magic = (<org.mwg.chunk.TimeTreeChunk>theNodeSuperTimeTrees[i]).magic();
                                                resolvedNode._time_magic = (<org.mwg.chunk.TimeTreeChunk>theNodeTimeTrees[i]).magic();
                                              }
                                              finalResult[i] = resolvedNode;
                                            }
                                          }
                                          this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, theNodeTimeTrees, theObjectChunks);
                                        }
                                      }                                    });
                                  }
                                }
                              }                            });
                          }
                        }
                      }                    });
                  }
                }
              }            });
          }
        }
        private resolve_world(globalWorldOrder: org.mwg.struct.LongLongMap, nodeWorldOrder: org.mwg.struct.LongLongMap, timeToResolve: number, originWorld: number): number {
          if (globalWorldOrder == null || nodeWorldOrder == null) {
            return originWorld;
          }
          let currentUniverse: number = originWorld;
          let previousUniverse: number = org.mwg.Constants.NULL_LONG;
          let divergenceTime: number = nodeWorldOrder.get(currentUniverse);
          while (currentUniverse != previousUniverse) {
            if (divergenceTime != org.mwg.Constants.NULL_LONG && divergenceTime <= timeToResolve) {
              return currentUniverse;
            }
            previousUniverse = currentUniverse;
            currentUniverse = globalWorldOrder.get(currentUniverse);
            divergenceTime = nodeWorldOrder.get(currentUniverse);
          }
          return originWorld;
        }
        private getOrLoadAndMarkAll(types: Int8Array, keys: Float64Array, callback: org.mwg.Callback<org.mwg.chunk.Chunk[]>): void {
          let nbKeys: number = keys.length / MWGResolver.KEY_SIZE;
          let toLoadIndexes: boolean[] = [];
          let nbElem: number = 0;
          let result: org.mwg.chunk.Chunk[] = new Array<org.mwg.chunk.Chunk>(nbKeys);
          for (let i: number = 0; i < nbKeys; i++) {
            if (keys[i * MWGResolver.KEY_SIZE] == org.mwg.core.CoreConstants.NULL_KEY[0] && keys[i * MWGResolver.KEY_SIZE + 1] == org.mwg.core.CoreConstants.NULL_KEY[1] && keys[i * MWGResolver.KEY_SIZE + 2] == org.mwg.core.CoreConstants.NULL_KEY[2]) {
              toLoadIndexes[i] = false;
              result[i] = null;
            } else {
              result[i] = this._space.getAndMark(types[i], keys[i * MWGResolver.KEY_SIZE], keys[i * MWGResolver.KEY_SIZE + 1], keys[i * MWGResolver.KEY_SIZE + 2]);
              if (result[i] == null) {
                toLoadIndexes[i] = true;
                nbElem++;
              } else {
                toLoadIndexes[i] = false;
              }
            }
          }
          if (nbElem == 0) {
            callback(result);
          } else {
            let keysToLoad: org.mwg.struct.Buffer = this._graph.newBuffer();
            let reverseIndex: Int32Array = new Int32Array(nbElem);
            let lastInsertedIndex: number = 0;
            for (let i: number = 0; i < nbKeys; i++) {
              if (toLoadIndexes[i]) {
                reverseIndex[lastInsertedIndex] = i;
                if (lastInsertedIndex != 0) {
                  keysToLoad.write(org.mwg.core.CoreConstants.BUFFER_SEP);
                }
                org.mwg.utility.KeyHelper.keyToBuffer(keysToLoad, types[i], keys[i * MWGResolver.KEY_SIZE], keys[i * MWGResolver.KEY_SIZE + 1], keys[i * MWGResolver.KEY_SIZE + 2]);
                lastInsertedIndex = lastInsertedIndex + 1;
              }
            }
            let selfPointer: org.mwg.core.MWGResolver = this;
            this._storage.get(keysToLoad, (fromDbBuffers : org.mwg.struct.Buffer) => {
{
                keysToLoad.free();
                let it: org.mwg.struct.BufferIterator = fromDbBuffers.iterator();
                let i: number = 0;
                while (it.hasNext()) {
                  let reversedIndex: number = reverseIndex[i];
                  let view: org.mwg.struct.Buffer = it.next();
                  if (view.length() > 0) {
                    result[reversedIndex] = selfPointer._space.createAndMark(types[reversedIndex], keys[reversedIndex * org.mwg.core.MWGResolver.KEY_SIZE], keys[reversedIndex * org.mwg.core.MWGResolver.KEY_SIZE + 1], keys[reversedIndex * org.mwg.core.MWGResolver.KEY_SIZE + 2]);
                    result[reversedIndex].load(view);
                  } else {
                    result[reversedIndex] = null;
                  }
                  i++;
                }
                fromDbBuffers.free();
                callback(result);
              }            });
          }
        }
        public resolveState(node: org.mwg.Node): org.mwg.plugin.NodeState {
          return this.internal_resolveState(node, true);
        }
        private internal_resolveState(node: org.mwg.Node, safe: boolean): org.mwg.chunk.StateChunk {
          let castedNode: org.mwg.plugin.AbstractNode = <org.mwg.plugin.AbstractNode>node;
          let stateResult: org.mwg.chunk.StateChunk = null;
          if (safe) {
            castedNode.cacheLock();
          }
          if (castedNode._dead) {
            if (safe) {
              castedNode.cacheUnlock();
            }
            throw new Error(org.mwg.core.CoreConstants.DEAD_NODE_ERROR + " node id: " + node.id());
          }
          if (castedNode._world_magic == -1 && castedNode._time_magic == -1 && castedNode._super_time_magic == -1) {
            stateResult = <org.mwg.chunk.StateChunk>this._space.get(castedNode._index_stateChunk);
          } else {
            let nodeWorldOrder: org.mwg.chunk.WorldOrderChunk = <org.mwg.chunk.WorldOrderChunk>this._space.get(castedNode._index_worldOrder);
            let nodeSuperTimeTree: org.mwg.chunk.TimeTreeChunk = <org.mwg.chunk.TimeTreeChunk>this._space.get(castedNode._index_superTimeTree);
            let nodeTimeTree: org.mwg.chunk.TimeTreeChunk = <org.mwg.chunk.TimeTreeChunk>this._space.get(castedNode._index_timeTree);
            if (nodeWorldOrder != null && nodeSuperTimeTree != null && nodeTimeTree != null) {
              if (castedNode._world_magic == nodeWorldOrder.magic() && castedNode._super_time_magic == nodeSuperTimeTree.magic() && castedNode._time_magic == nodeTimeTree.magic()) {
                stateResult = <org.mwg.chunk.StateChunk>this._space.get(castedNode._index_stateChunk);
              } else {
                if (safe) {
                  nodeWorldOrder.lock();
                }
                let nodeTime: number = castedNode.time();
                let nodeId: number = castedNode.id();
                let nodeWorld: number = castedNode.world();
                let resolvedWorld: number = this.resolve_world(this.globalWorldOrderChunk, nodeWorldOrder, nodeTime, nodeWorld);
                if (resolvedWorld != nodeSuperTimeTree.world()) {
                  let tempNodeSuperTimeTree: org.mwg.chunk.TimeTreeChunk = <org.mwg.chunk.TimeTreeChunk>this._space.getAndMark(org.mwg.chunk.ChunkType.TIME_TREE_CHUNK, resolvedWorld, org.mwg.core.CoreConstants.NULL_LONG, nodeId);
                  if (tempNodeSuperTimeTree != null) {
                    this._space.unmark(nodeSuperTimeTree.index());
                    nodeSuperTimeTree = tempNodeSuperTimeTree;
                  }
                }
                let resolvedSuperTime: number = nodeSuperTimeTree.previousOrEqual(nodeTime);
                if (resolvedSuperTime != nodeTimeTree.time()) {
                  let tempNodeTimeTree: org.mwg.chunk.TimeTreeChunk = <org.mwg.chunk.TimeTreeChunk>this._space.getAndMark(org.mwg.chunk.ChunkType.TIME_TREE_CHUNK, resolvedWorld, resolvedSuperTime, nodeId);
                  if (tempNodeTimeTree != null) {
                    this._space.unmark(nodeTimeTree.index());
                    nodeTimeTree = tempNodeTimeTree;
                  }
                }
                let resolvedTime: number = nodeTimeTree.previousOrEqual(nodeTime);
                if (resolvedWorld == nodeWorld && resolvedTime == nodeTime) {
                  castedNode._world_magic = -1;
                  castedNode._time_magic = -1;
                  castedNode._super_time_magic = -1;
                } else {
                  castedNode._world_magic = nodeWorldOrder.magic();
                  castedNode._time_magic = nodeTimeTree.magic();
                  castedNode._super_time_magic = nodeSuperTimeTree.magic();
                  castedNode._index_superTimeTree = nodeSuperTimeTree.index();
                  castedNode._index_timeTree = nodeTimeTree.index();
                }
                stateResult = <org.mwg.chunk.StateChunk>this._space.get(castedNode._index_stateChunk);
                if (resolvedWorld != stateResult.world() || resolvedTime != stateResult.time()) {
                  let tempNodeState: org.mwg.chunk.StateChunk = <org.mwg.chunk.StateChunk>this._space.getAndMark(org.mwg.chunk.ChunkType.STATE_CHUNK, resolvedWorld, resolvedTime, nodeId);
                  if (tempNodeState != null) {
                    this._space.unmark(stateResult.index());
                    stateResult = tempNodeState;
                  }
                }
                castedNode._index_stateChunk = stateResult.index();
                if (safe) {
                  nodeWorldOrder.unlock();
                }
              }
            }
          }
          if (safe) {
            castedNode.cacheUnlock();
          }
          return stateResult;
        }
        public alignState(node: org.mwg.Node): org.mwg.plugin.NodeState {
          let castedNode: org.mwg.plugin.AbstractNode = <org.mwg.plugin.AbstractNode>node;
          castedNode.cacheLock();
          if (castedNode._dead) {
            castedNode.cacheUnlock();
            throw new Error(org.mwg.core.CoreConstants.DEAD_NODE_ERROR + " node id: " + node.id());
          }
          if (castedNode._world_magic == -1 && castedNode._time_magic == -1 && castedNode._super_time_magic == -1) {
            let currentEntry: org.mwg.chunk.StateChunk = <org.mwg.chunk.StateChunk>this._space.get(castedNode._index_stateChunk);
            if (currentEntry != null) {
              castedNode.cacheUnlock();
              return currentEntry;
            }
          }
          let nodeWorldOrder: org.mwg.chunk.WorldOrderChunk = <org.mwg.chunk.WorldOrderChunk>this._space.get(castedNode._index_worldOrder);
          if (nodeWorldOrder == null) {
            castedNode.cacheUnlock();
            return null;
          }
          nodeWorldOrder.lock();
          let nodeWorld: number = node.world();
          let nodeTime: number = node.time();
          let nodeId: number = node.id();
          let previouStateChunk: org.mwg.chunk.StateChunk = this.internal_resolveState(node, false);
          if (castedNode._world_magic == -1 && castedNode._time_magic == -1 && castedNode._super_time_magic == -1) {
            nodeWorldOrder.unlock();
            castedNode.cacheUnlock();
            return previouStateChunk;
          }
          let clonedState: org.mwg.chunk.StateChunk = <org.mwg.chunk.StateChunk>this._space.createAndMark(org.mwg.chunk.ChunkType.STATE_CHUNK, nodeWorld, nodeTime, nodeId);
          clonedState.loadFrom(previouStateChunk);
          castedNode._world_magic = -1;
          castedNode._super_time_magic = -1;
          castedNode._time_magic = -1;
          castedNode._index_stateChunk = clonedState.index();
          this._space.unmark(previouStateChunk.index());
          if (previouStateChunk.world() == nodeWorld || nodeWorldOrder.get(nodeWorld) != org.mwg.core.CoreConstants.NULL_LONG) {
            let superTimeTree: org.mwg.chunk.TimeTreeChunk = <org.mwg.chunk.TimeTreeChunk>this._space.get(castedNode._index_superTimeTree);
            let timeTree: org.mwg.chunk.TimeTreeChunk = <org.mwg.chunk.TimeTreeChunk>this._space.get(castedNode._index_timeTree);
            let superTreeSize: number = superTimeTree.size();
            let threshold: number = org.mwg.core.CoreConstants.SCALE_1 * 2;
            if (superTreeSize > threshold) {
              threshold = org.mwg.core.CoreConstants.SCALE_2 * 2;
            }
            if (superTreeSize > threshold) {
              threshold = org.mwg.core.CoreConstants.SCALE_3 * 2;
            }
            if (superTreeSize > threshold) {
              threshold = org.mwg.core.CoreConstants.SCALE_4 * 2;
            }
            timeTree.insert(nodeTime);
            if (timeTree.size() == threshold) {
              let medianPoint: Float64Array = new Float64Array([-1]);
              timeTree.range(org.mwg.core.CoreConstants.BEGINNING_OF_TIME, org.mwg.core.CoreConstants.END_OF_TIME, timeTree.size() / 2, (t : number) => {
{
                  medianPoint[0] = t;
                }              });
              let rightTree: org.mwg.chunk.TimeTreeChunk = <org.mwg.chunk.TimeTreeChunk>this._space.createAndMark(org.mwg.chunk.ChunkType.TIME_TREE_CHUNK, nodeWorld, medianPoint[0], nodeId);
              let finalRightTree: org.mwg.chunk.TimeTreeChunk = rightTree;
              timeTree.range(org.mwg.core.CoreConstants.BEGINNING_OF_TIME, org.mwg.core.CoreConstants.END_OF_TIME, timeTree.size() / 2, (t : number) => {
{
                  finalRightTree.unsafe_insert(t);
                }              });
              this._space.notifyUpdate(finalRightTree.index());
              superTimeTree.insert(medianPoint[0]);
              timeTree.clearAt(medianPoint[0]);
              if (nodeTime < medianPoint[0]) {
                this._space.unmark(rightTree.index());
              } else {
                castedNode._index_timeTree = finalRightTree.index();
                this._space.unmark(timeTree.index());
              }
            }
          } else {
            let newSuperTimeTree: org.mwg.chunk.TimeTreeChunk = <org.mwg.chunk.TimeTreeChunk>this._space.createAndMark(org.mwg.chunk.ChunkType.TIME_TREE_CHUNK, nodeWorld, org.mwg.core.CoreConstants.NULL_LONG, nodeId);
            newSuperTimeTree.insert(nodeTime);
            let newTimeTree: org.mwg.chunk.TimeTreeChunk = <org.mwg.chunk.TimeTreeChunk>this._space.createAndMark(org.mwg.chunk.ChunkType.TIME_TREE_CHUNK, nodeWorld, nodeTime, nodeId);
            newTimeTree.insert(nodeTime);
            nodeWorldOrder.put(nodeWorld, nodeTime);
            this._space.unmark(castedNode._index_timeTree);
            this._space.unmark(castedNode._index_superTimeTree);
            castedNode._index_timeTree = newTimeTree.index();
            castedNode._index_superTimeTree = newSuperTimeTree.index();
          }
          nodeWorldOrder.unlock();
          castedNode.cacheUnlock();
          return clonedState;
        }
        public newState(node: org.mwg.Node, world: number, time: number): org.mwg.plugin.NodeState {
          let castedNode: org.mwg.plugin.AbstractNode = <org.mwg.plugin.AbstractNode>node;
          let resolved: org.mwg.plugin.NodeState;
          castedNode.cacheLock();
          let fakeNode: org.mwg.plugin.AbstractNode = new org.mwg.core.CoreNode(world, time, node.id(), node.graph());
          fakeNode._index_worldOrder = castedNode._index_worldOrder;
          fakeNode._index_superTimeTree = castedNode._index_superTimeTree;
          fakeNode._index_timeTree = castedNode._index_timeTree;
          fakeNode._index_stateChunk = castedNode._index_stateChunk;
          fakeNode._time_magic = castedNode._time_magic;
          fakeNode._super_time_magic = castedNode._super_time_magic;
          fakeNode._world_magic = castedNode._world_magic;
          resolved = this.alignState(fakeNode);
          castedNode._index_worldOrder = fakeNode._index_worldOrder;
          castedNode._index_superTimeTree = fakeNode._index_superTimeTree;
          castedNode._index_timeTree = fakeNode._index_timeTree;
          castedNode._index_stateChunk = fakeNode._index_stateChunk;
          castedNode._time_magic = fakeNode._time_magic;
          castedNode._super_time_magic = fakeNode._super_time_magic;
          castedNode._world_magic = fakeNode._world_magic;
          castedNode.cacheUnlock();
          return resolved;
        }
        public resolveTimepoints(node: org.mwg.Node, beginningOfSearch: number, endOfSearch: number, callback: org.mwg.Callback<Float64Array>): void {
          let selfPointer: org.mwg.core.MWGResolver = this;
          this._space.getOrLoadAndMark(org.mwg.chunk.ChunkType.WORLD_ORDER_CHUNK, 0, 0, node.id(), (resolved : org.mwg.chunk.Chunk) => {
{
              if (resolved == null) {
                callback(new Float64Array(0));
                return;
              }
              let objectWorldOrder: org.mwg.chunk.WorldOrderChunk = <org.mwg.chunk.WorldOrderChunk>resolved;
              let collectionSize: Int32Array = new Int32Array([org.mwg.core.CoreConstants.MAP_INITIAL_CAPACITY]);
              let collectedWorlds: Array<Float64Array> = [new Float64Array(collectionSize[0])];
              let collectedIndex: number = 0;
              let currentWorld: number = node.world();
              while (currentWorld != org.mwg.core.CoreConstants.NULL_LONG) {
                let divergenceTimepoint: number = objectWorldOrder.get(currentWorld);
                if (divergenceTimepoint != org.mwg.core.CoreConstants.NULL_LONG) {
                  if (divergenceTimepoint <= beginningOfSearch) {
                    collectedWorlds[0][collectedIndex] = currentWorld;
                    collectedIndex++;
break;
                  } else if (divergenceTimepoint > endOfSearch) {
                    currentWorld = selfPointer.globalWorldOrderChunk.get(currentWorld);
                  } else {
                    collectedWorlds[0][collectedIndex] = currentWorld;
                    collectedIndex++;
                    if (collectedIndex == collectionSize[0]) {
                      let temp_collectedWorlds: Float64Array = new Float64Array(collectionSize[0] * 2);
                      java.lang.System.arraycopy(collectedWorlds[0], 0, temp_collectedWorlds, 0, collectionSize[0]);
                      collectedWorlds[0] = temp_collectedWorlds;
                      collectionSize[0] = collectionSize[0] * 2;
                    }
                    currentWorld = selfPointer.globalWorldOrderChunk.get(currentWorld);
                  }

                } else {
                  currentWorld = selfPointer.globalWorldOrderChunk.get(currentWorld);
                }
              }
              selfPointer.resolveTimepointsFromWorlds(selfPointer.globalWorldOrderChunk, objectWorldOrder, node, beginningOfSearch, endOfSearch, collectedWorlds[0], collectedIndex, callback);
            }          });
        }
        private resolveTimepointsFromWorlds(globalWorldOrder: org.mwg.chunk.WorldOrderChunk, objectWorldOrder: org.mwg.chunk.WorldOrderChunk, node: org.mwg.Node, beginningOfSearch: number, endOfSearch: number, collectedWorlds: Float64Array, collectedWorldsSize: number, callback: org.mwg.Callback<Float64Array>): void {
          let selfPointer: org.mwg.core.MWGResolver = this;
          let timeTreeKeys: Float64Array = new Float64Array(collectedWorldsSize * 3);
          let types: Int8Array = new Int8Array(collectedWorldsSize);
          for (let i: number = 0; i < collectedWorldsSize; i++) {
            timeTreeKeys[i * 3] = collectedWorlds[i];
            timeTreeKeys[i * 3 + 1] = org.mwg.core.CoreConstants.NULL_LONG;
            timeTreeKeys[i * 3 + 2] = node.id();
            types[i] = org.mwg.chunk.ChunkType.TIME_TREE_CHUNK;
          }
          this.getOrLoadAndMarkAll(types, timeTreeKeys, (superTimeTrees : org.mwg.chunk.Chunk[]) => {
{
              if (superTimeTrees == null) {
                selfPointer._space.unmark(objectWorldOrder.index());
                callback(new Float64Array(0));
              } else {
                let collectedSize: Int32Array = new Int32Array([org.mwg.core.CoreConstants.MAP_INITIAL_CAPACITY]);
                let collectedSuperTimes: Array<Float64Array> = [new Float64Array(collectedSize[0])];
                let collectedSuperTimesAssociatedWorlds: Array<Float64Array> = [new Float64Array(collectedSize[0])];
                let insert_index: Int32Array = new Int32Array([0]);
                let previousDivergenceTime: number = endOfSearch;
                for (let i: number = 0; i < collectedWorldsSize; i++) {
                  let timeTree: org.mwg.chunk.TimeTreeChunk = <org.mwg.chunk.TimeTreeChunk>superTimeTrees[i];
                  if (timeTree != null) {
                    let currentDivergenceTime: number = objectWorldOrder.get(collectedWorlds[i]);
                    let finalPreviousDivergenceTime: number = previousDivergenceTime;
                    timeTree.range(currentDivergenceTime, previousDivergenceTime, org.mwg.core.CoreConstants.END_OF_TIME, (t : number) => {
{
                        if (t != finalPreviousDivergenceTime) {
                          collectedSuperTimes[0][insert_index[0]] = t;
                          collectedSuperTimesAssociatedWorlds[0][insert_index[0]] = timeTree.world();
                          insert_index[0]++;
                          if (collectedSize[0] == insert_index[0]) {
                            let temp_collectedSuperTimes: Float64Array = new Float64Array(collectedSize[0] * 2);
                            let temp_collectedSuperTimesAssociatedWorlds: Float64Array = new Float64Array(collectedSize[0] * 2);
                            java.lang.System.arraycopy(collectedSuperTimes[0], 0, temp_collectedSuperTimes, 0, collectedSize[0]);
                            java.lang.System.arraycopy(collectedSuperTimesAssociatedWorlds[0], 0, temp_collectedSuperTimesAssociatedWorlds, 0, collectedSize[0]);
                            collectedSuperTimes[0] = temp_collectedSuperTimes;
                            collectedSuperTimesAssociatedWorlds[0] = temp_collectedSuperTimesAssociatedWorlds;
                            collectedSize[0] = collectedSize[0] * 2;
                          }
                        }
                      }                    });
                    previousDivergenceTime = currentDivergenceTime;
                  }
                  selfPointer._space.unmark(timeTree.index());
                }
                selfPointer.resolveTimepointsFromSuperTimes(objectWorldOrder, node, beginningOfSearch, endOfSearch, collectedSuperTimesAssociatedWorlds[0], collectedSuperTimes[0], insert_index[0], callback);
              }
            }          });
        }
        private resolveTimepointsFromSuperTimes(objectWorldOrder: org.mwg.chunk.WorldOrderChunk, node: org.mwg.Node, beginningOfSearch: number, endOfSearch: number, collectedWorlds: Float64Array, collectedSuperTimes: Float64Array, collectedSize: number, callback: org.mwg.Callback<Float64Array>): void {
          let selfPointer: org.mwg.core.MWGResolver = this;
          let timeTreeKeys: Float64Array = new Float64Array(collectedSize * 3);
          let types: Int8Array = new Int8Array(collectedSize);
          for (let i: number = 0; i < collectedSize; i++) {
            timeTreeKeys[i * 3] = collectedWorlds[i];
            timeTreeKeys[i * 3 + 1] = collectedSuperTimes[i];
            timeTreeKeys[i * 3 + 2] = node.id();
            types[i] = org.mwg.chunk.ChunkType.TIME_TREE_CHUNK;
          }
          this.getOrLoadAndMarkAll(types, timeTreeKeys, (timeTrees : org.mwg.chunk.Chunk[]) => {
{
              if (timeTrees == null) {
                selfPointer._space.unmark(objectWorldOrder.index());
                callback(new Float64Array(0));
              } else {
                let collectedTimesSize: Int32Array = new Int32Array([org.mwg.core.CoreConstants.MAP_INITIAL_CAPACITY]);
                let collectedTimes: Array<Float64Array> = [new Float64Array(collectedTimesSize[0])];
                let insert_index: Int32Array = new Int32Array([0]);
                let previousDivergenceTime: number = endOfSearch;
                for (let i: number = 0; i < collectedSize; i++) {
                  let timeTree: org.mwg.chunk.TimeTreeChunk = <org.mwg.chunk.TimeTreeChunk>timeTrees[i];
                  if (timeTree != null) {
                    let currentDivergenceTime: number = objectWorldOrder.get(collectedWorlds[i]);
                    if (currentDivergenceTime < beginningOfSearch) {
                      currentDivergenceTime = beginningOfSearch;
                    }
                    let finalPreviousDivergenceTime: number = previousDivergenceTime;
                    timeTree.range(currentDivergenceTime, previousDivergenceTime, org.mwg.core.CoreConstants.END_OF_TIME, (t : number) => {
{
                        if (t != finalPreviousDivergenceTime) {
                          collectedTimes[0][insert_index[0]] = t;
                          insert_index[0]++;
                          if (collectedTimesSize[0] == insert_index[0]) {
                            let temp_collectedTimes: Float64Array = new Float64Array(collectedTimesSize[0] * 2);
                            java.lang.System.arraycopy(collectedTimes[0], 0, temp_collectedTimes, 0, collectedTimesSize[0]);
                            collectedTimes[0] = temp_collectedTimes;
                            collectedTimesSize[0] = collectedTimesSize[0] * 2;
                          }
                        }
                      }                    });
                    if (i < collectedSize - 1) {
                      if (collectedWorlds[i + 1] != collectedWorlds[i]) {
                        previousDivergenceTime = currentDivergenceTime;
                      }
                    }
                  }
                  selfPointer._space.unmark(timeTree.index());
                }
                if (insert_index[0] != collectedTimesSize[0]) {
                  let tempTimeline: Float64Array = new Float64Array(insert_index[0]);
                  java.lang.System.arraycopy(collectedTimes[0], 0, tempTimeline, 0, insert_index[0]);
                  collectedTimes[0] = tempTimeline;
                }
                selfPointer._space.unmark(objectWorldOrder.index());
                callback(collectedTimes[0]);
              }
            }          });
        }
        public stringToHash(name: string, insertIfNotExists: boolean): number {
          let hash: number = org.mwg.utility.HashHelper.hash(name);
          if (insertIfNotExists) {
            let dictionaryIndex: org.mwg.struct.StringLongMap = <org.mwg.struct.StringLongMap>this.dictionary.get(0);
            if (dictionaryIndex == null) {
              dictionaryIndex = <org.mwg.struct.StringLongMap>this.dictionary.getOrCreate(0, org.mwg.Type.STRING_TO_LONG_MAP);
            }
            if (!dictionaryIndex.containsHash(hash)) {
              dictionaryIndex.put(name, hash);
            }
          }
          return hash;
        }
        public hashToString(key: number): string {
          let dictionaryIndex: org.mwg.struct.StringLongMap = <org.mwg.struct.StringLongMap>this.dictionary.get(0);
          if (dictionaryIndex != null) {
            return dictionaryIndex.getByHash(key);
          }
          return null;
        }
      }
      export module chunk {
        export module heap {
          export class HeapAtomicByteArray {
            private _back: Int8Array;
            constructor(initialSize: number) {
              this._back = new Int8Array(initialSize);
            }
            public get(index: number): number {
              return this._back[index];
            }
            public set(index: number, value: number): void {
              this._back[index] = value;
            }
          }
          export class HeapChunkSpace implements org.mwg.chunk.ChunkSpace {
            private static HASH_LOAD_FACTOR: number = 4;
            private _maxEntries: number;
            private _hashEntries: number;
            private _lru: org.mwg.chunk.Stack;
            private _dirtiesStack: org.mwg.chunk.Stack;
            private _hashNext: java.util.concurrent.atomic.AtomicIntegerArray;
            private _hash: java.util.concurrent.atomic.AtomicIntegerArray;
            private _chunkWorlds: java.util.concurrent.atomic.AtomicLongArray;
            private _chunkTimes: java.util.concurrent.atomic.AtomicLongArray;
            private _chunkIds: java.util.concurrent.atomic.AtomicLongArray;
            private _chunkTypes: org.mwg.core.chunk.heap.HeapAtomicByteArray;
            private _chunkValues: java.util.concurrent.atomic.AtomicReferenceArray<org.mwg.chunk.Chunk>;
            private _chunkMarks: java.util.concurrent.atomic.AtomicLongArray;
            private _graph: org.mwg.Graph;
            public graph(): org.mwg.Graph {
              return this._graph;
            }
            public worldByIndex(index: number): number {
              return this._chunkWorlds.get(<number>index);
            }
            public timeByIndex(index: number): number {
              return this._chunkTimes.get(<number>index);
            }
            public idByIndex(index: number): number {
              return this._chunkIds.get(<number>index);
            }
            constructor(initialCapacity: number, p_graph: org.mwg.Graph) {
              this._graph = p_graph;
              this._maxEntries = initialCapacity;
              this._hashEntries = initialCapacity * HeapChunkSpace.HASH_LOAD_FACTOR;
              this._lru = new org.mwg.core.chunk.heap.HeapFixedStack(initialCapacity, true);
              this._dirtiesStack = new org.mwg.core.chunk.heap.HeapFixedStack(initialCapacity, false);
              this._hashNext = new java.util.concurrent.atomic.AtomicIntegerArray(initialCapacity);
              this._hash = new java.util.concurrent.atomic.AtomicIntegerArray(this._hashEntries);
              for (let i: number = 0; i < initialCapacity; i++) {
                this._hashNext.set(i, -1);
              }
              for (let i: number = 0; i < this._hashEntries; i++) {
                this._hash.set(i, -1);
              }
              this._chunkValues = new java.util.concurrent.atomic.AtomicReferenceArray<org.mwg.chunk.Chunk>(initialCapacity);
              this._chunkWorlds = new java.util.concurrent.atomic.AtomicLongArray(this._maxEntries);
              this._chunkTimes = new java.util.concurrent.atomic.AtomicLongArray(this._maxEntries);
              this._chunkIds = new java.util.concurrent.atomic.AtomicLongArray(this._maxEntries);
              this._chunkTypes = new org.mwg.core.chunk.heap.HeapAtomicByteArray(this._maxEntries);
              this._chunkMarks = new java.util.concurrent.atomic.AtomicLongArray(this._maxEntries);
              for (let i: number = 0; i < this._maxEntries; i++) {
                this._chunkMarks.set(i, 0);
              }
            }
            public getAndMark(type: number, world: number, time: number, id: number): org.mwg.chunk.Chunk {
              let index: number = <number>org.mwg.utility.HashHelper.tripleHash(type, world, time, id, this._hashEntries);
              let m: number = this._hash.get(index);
              let found: number = -1;
              while (m != -1) {
                if (this._chunkTypes.get(m) == type && this._chunkWorlds.get(m) == world && this._chunkTimes.get(m) == time && this._chunkIds.get(m) == id) {
                  if (this.mark(m) > 0) {
                    found = m;
                  }
break;
                } else {
                  m = this._hashNext.get(m);
                }
              }
              if (found != -1) {
                return this._chunkValues.get(found);
              } else {
                return null;
              }
            }
            public get(index: number): org.mwg.chunk.Chunk {
              return this._chunkValues.get(<number>index);
            }
            public getOrLoadAndMark(type: number, world: number, time: number, id: number, callback: org.mwg.Callback<org.mwg.chunk.Chunk>): void {
              let fromMemory: org.mwg.chunk.Chunk = this.getAndMark(type, world, time, id);
              if (fromMemory != null) {
                callback(fromMemory);
              } else {
                let keys: org.mwg.struct.Buffer = this.graph().newBuffer();
                org.mwg.utility.KeyHelper.keyToBuffer(keys, type, world, time, id);
                this.graph().storage().get(keys, (result : org.mwg.struct.Buffer) => {
{
                    if (result != null && result.length() > 0) {
                      let loadedChunk: org.mwg.chunk.Chunk = this.createAndMark(type, world, time, id);
                      loadedChunk.load(result);
                      result.free();
                      callback(loadedChunk);
                    } else {
                      keys.free();
                      callback(null);
                    }
                  }                });
              }
            }
            public getOrLoadAndMarkAll(keys: Float64Array, callback: org.mwg.Callback<org.mwg.chunk.Chunk[]>): void {
              let querySize: number = keys.length / org.mwg.Constants.KEY_SIZE;
              let finalResult: org.mwg.chunk.Chunk[] = new Array<org.mwg.chunk.Chunk>(querySize);
              let reverse: Int32Array = null;
              let reverseIndex: number = 0;
              let toLoadKeys: org.mwg.struct.Buffer = null;
              for (let i: number = 0; i < querySize; i++) {
                let offset: number = i * org.mwg.Constants.KEY_SIZE;
                let loopType: number = <number>keys[offset];
                if (loopType != -1) {
                  let fromMemory: org.mwg.chunk.Chunk = this.getAndMark(<number>keys[offset], keys[offset + 1], keys[offset + 2], keys[offset + 3]);
                  if (fromMemory != null) {
                    finalResult[i] = fromMemory;
                  } else {
                    if (reverse == null) {
                      reverse = new Int32Array(querySize);
                      toLoadKeys = this.graph().newBuffer();
                    }
                    reverse[i] = reverseIndex;
                    if (reverseIndex != 0) {
                      toLoadKeys.write(org.mwg.Constants.BUFFER_SEP);
                    }
                    org.mwg.utility.KeyHelper.keyToBuffer(toLoadKeys, <number>keys[offset], keys[offset + 1], keys[offset + 2], keys[offset + 3]);
                    reverseIndex++;
                  }
                } else {
                  finalResult[i] = null;
                }
              }
              if (reverse != null) {
                let finalReverse: Int32Array = reverse;
                this.graph().storage().get(toLoadKeys, (loadAllResult : org.mwg.struct.Buffer) => {
{
                    let it: org.mwg.struct.BufferIterator = loadAllResult.iterator();
                    let i: number = 0;
                    while (it.hasNext()) {
                      let view: org.mwg.struct.Buffer = it.next();
                      let reversedIndex: number = finalReverse[i];
                      let reversedOffset: number = reversedIndex * org.mwg.Constants.KEY_SIZE;
                      if (view.length() > 0) {
                        let loadedChunk: org.mwg.chunk.Chunk = this.createAndMark(<number>keys[reversedOffset], keys[reversedOffset + 1], keys[reversedOffset + 2], keys[reversedOffset + 3]);
                        loadedChunk.load(view);
                        finalResult[reversedIndex] = loadedChunk;
                      } else {
                        finalResult[reversedIndex] = null;
                      }
                      i++;
                    }
                    loadAllResult.free();
                    callback(finalResult);
                  }                });
              } else {
                callback(finalResult);
              }
            }
            public mark(index: number): number {
              let castedIndex: number = <number>index;
              let before: number;
              let after: number;
              do {
                before = this._chunkMarks.get(castedIndex);
                if (before != -1) {
                  after = before + 1;
                } else {
                  after = before;
                }
              } while (!this._chunkMarks.compareAndSet(castedIndex, before, after));
              if (before == 0 && after == 1) {
                this._lru.dequeue(index);
              }
              return after;
            }
            public unmark(index: number): void {
              let castedIndex: number = <number>index;
              let before: number;
              let after: number;
              do {
                before = this._chunkMarks.get(castedIndex);
                if (before > 0) {
                  after = before - 1;
                } else {
                  console.error("WARNING: DOUBLE UNMARK");
                  after = before;
                }
              } while (!this._chunkMarks.compareAndSet(castedIndex, before, after));
              if (before == 1 && after == 0) {
                this._lru.enqueue(index);
              }
            }
            public free(chunk: org.mwg.chunk.Chunk): void {}
            public createAndMark(type: number, world: number, time: number, id: number): org.mwg.chunk.Chunk {
              let entry: number = -1;
              let hashIndex: number = <number>org.mwg.utility.HashHelper.tripleHash(type, world, time, id, this._hashEntries);
              let m: number = this._hash.get(hashIndex);
              while (m >= 0) {
                if (type == this._chunkTypes.get(m) && world == this._chunkWorlds.get(m) && time == this._chunkTimes.get(m) && id == this._chunkIds.get(m)) {
                  entry = m;
break;
                }
                m = this._hashNext.get(m);
              }
              if (entry != -1) {
                let previous: number;
                let after: number;
                do {
                  previous = this._chunkMarks.get(entry);
                  if (previous != -1) {
                    after = previous + 1;
                  } else {
                    after = previous;
                  }
                } while (!this._chunkMarks.compareAndSet(entry, previous, after));
                if (after == (previous + 1)) {
                  return this._chunkValues.get(entry);
                }
              }
              let currentVictimIndex: number = -1;
              while (currentVictimIndex == -1) {
                let temp_victim: number = <number>this._lru.dequeueTail();
                if (temp_victim == -1) {
break;
                } else {
                  if (this._chunkMarks.compareAndSet(temp_victim, 0, -1)) {
                    currentVictimIndex = temp_victim;
                  }
                }
              }
              if (currentVictimIndex == -1) {
                throw new Error("mwDB crashed, cache is full, please avoid to much retention of nodes or augment cache capacity! available:" + this.available());
              }
              let toInsert: org.mwg.chunk.Chunk = null;
              switch (type) {
                case org.mwg.chunk.ChunkType.STATE_CHUNK:
                  toInsert = new org.mwg.core.chunk.heap.HeapStateChunk(this, currentVictimIndex);
break;
                case org.mwg.chunk.ChunkType.WORLD_ORDER_CHUNK:
                  toInsert = new org.mwg.core.chunk.heap.HeapWorldOrderChunk(this, currentVictimIndex);
break;
                case org.mwg.chunk.ChunkType.TIME_TREE_CHUNK:
                  toInsert = new org.mwg.core.chunk.heap.HeapTimeTreeChunk(this, currentVictimIndex);
break;
                case org.mwg.chunk.ChunkType.GEN_CHUNK:
                  toInsert = new org.mwg.core.chunk.heap.HeapGenChunk(this, id, currentVictimIndex);
break;
              }
              if (this._chunkValues.get(currentVictimIndex) != null) {
                let victimWorld: number = this._chunkWorlds.get(currentVictimIndex);
                let victimTime: number = this._chunkTimes.get(currentVictimIndex);
                let victimObj: number = this._chunkIds.get(currentVictimIndex);
                let victimType: number = this._chunkTypes.get(currentVictimIndex);
                let indexVictim: number = <number>org.mwg.utility.HashHelper.tripleHash(victimType, victimWorld, victimTime, victimObj, this._hashEntries);
                m = this._hash.get(indexVictim);
                let last: number = -1;
                while (m >= 0) {
                  if (victimType == this._chunkTypes.get(m) && victimWorld == this._chunkWorlds.get(m) && victimTime == this._chunkTimes.get(m) && victimObj == this._chunkIds.get(m)) {
break;
                  }
                  last = m;
                  m = this._hashNext.get(m);
                }
                if (last == -1) {
                  let previousNext: number = this._hashNext.get(m);
                  this._hash.set(indexVictim, previousNext);
                } else {
                  if (m == -1) {
                    this._hashNext.set(last, -1);
                  } else {
                    this._hashNext.set(last, this._hashNext.get(m));
                  }
                }
                this._hashNext.set(m, -1);
              }
              this._chunkValues.set(currentVictimIndex, toInsert);
              this._chunkMarks.set(currentVictimIndex, 1);
              this._chunkTypes.set(currentVictimIndex, type);
              this._chunkWorlds.set(currentVictimIndex, world);
              this._chunkTimes.set(currentVictimIndex, time);
              this._chunkIds.set(currentVictimIndex, id);
              this._hashNext.set(currentVictimIndex, this._hash.get(hashIndex));
              this._hash.set(hashIndex, currentVictimIndex);
              return toInsert;
            }
            public notifyUpdate(index: number): void {
              if (this._dirtiesStack.enqueue(index)) {
                this.mark(index);
              }
            }
            public save(callback: org.mwg.Callback<boolean>): void {
              let stream: org.mwg.struct.Buffer = this._graph.newBuffer();
              let isFirst: boolean = true;
              while (this._dirtiesStack.size() != 0) {
                let tail: number = <number>this._dirtiesStack.dequeueTail();
                let loopChunk: org.mwg.chunk.Chunk = this._chunkValues.get(tail);
                if (isFirst) {
                  isFirst = false;
                } else {
                  stream.write(org.mwg.Constants.BUFFER_SEP);
                }
                org.mwg.utility.KeyHelper.keyToBuffer(stream, this._chunkTypes.get(tail), this._chunkWorlds.get(tail), this._chunkTimes.get(tail), this._chunkIds.get(tail));
                stream.write(org.mwg.Constants.BUFFER_SEP);
                try {
                  loopChunk.save(stream);
                  this.unmark(tail);
                } catch ($ex$) {
                  if ($ex$ instanceof Error) {
                    var e: Error = <Error>$ex$;
{
                      console.error(e);
                    }                  } else {
                    throw $ex$;
                  }
                }
              }
              this.graph().storage().put(stream, (result : boolean) => {
{
                  stream.free();
                  if (callback != null) {
                    callback(result);
                  }
                }              });
            }
            public clear(): void {}
            public freeAll(): void {}
            public available(): number {
              return this._lru.size();
            }
            public printMarked(): void {
              for (let i: number = 0; i < this._chunkValues.length(); i++) {
                if (this._chunkValues.get(i) != null) {
                  if (this._chunkMarks.get(i) != 0) {
                    switch (this._chunkTypes.get(i)) {
                      case org.mwg.chunk.ChunkType.STATE_CHUNK:
                        console.log("STATE(" + this._chunkWorlds.get(i) + "," + this._chunkTimes.get(i) + "," + this._chunkIds.get(i) + ")->marks->" + this._chunkMarks.get(i));
break;
                      case org.mwg.chunk.ChunkType.TIME_TREE_CHUNK:
                        console.log("TIME_TREE(" + this._chunkWorlds.get(i) + "," + this._chunkTimes.get(i) + "," + this._chunkIds.get(i) + ")->marks->" + this._chunkMarks.get(i));
break;
                      case org.mwg.chunk.ChunkType.WORLD_ORDER_CHUNK:
                        console.log("WORLD_ORDER(" + this._chunkWorlds.get(i) + "," + this._chunkTimes.get(i) + "," + this._chunkIds.get(i) + ")->marks->" + this._chunkMarks.get(i));
break;
                      case org.mwg.chunk.ChunkType.GEN_CHUNK:
                        console.log("GENERATOR(" + this._chunkWorlds.get(i) + "," + this._chunkTimes.get(i) + "," + this._chunkIds.get(i) + ")->marks->" + this._chunkMarks.get(i));
break;
                    }
                  }
                }
              }
            }
          }
          export class HeapFixedStack implements org.mwg.chunk.Stack {
            private _next: Int32Array;
            private _prev: Int32Array;
            private _capacity: number;
            private _first: number;
            private _last: number;
            private _count: number;
            constructor(capacity: number, fill: boolean) {
              this._capacity = capacity;
              this._next = new Int32Array(capacity);
              this._prev = new Int32Array(capacity);
              this._first = -1;
              this._last = -1;
              java.util.Arrays.fill(this._next, 0, capacity, -1);
              java.util.Arrays.fill(this._prev, 0, capacity, -1);
              if (fill) {
                for (let i: number = 0; i < capacity; i++) {
                  let l: number = this._last;
                  this._prev[i] = l;
                  this._last = i;
                  if (this._first == -1) {
                    this._first = i;
                  } else {
                    this._next[l] = i;
                  }
                }
                this._count = capacity;
              } else {
                this._count = 0;
              }
            }
            public enqueue(index: number): boolean {
              if (this._count >= this._capacity) {
                return false;
              }
              let castedIndex: number = <number>index;
              if (this._first == castedIndex || this._last == castedIndex) {
                return false;
              }
              if (this._prev[castedIndex] != -1 || this._next[castedIndex] != -1) {
                return false;
              }
              let l: number = this._last;
              this._prev[castedIndex] = l;
              this._last = castedIndex;
              if (this._first == -1) {
                this._first = castedIndex;
              } else {
                this._next[l] = castedIndex;
              }
              this._count++;
              return true;
            }
            public dequeueTail(): number {
              let f: number = this._first;
              if (f == -1) {
                return -1;
              }
              let n: number = this._next[f];
              this._next[f] = -1;
              this._prev[f] = -1;
              this._first = n;
              if (n == -1) {
                this._last = -1;
              } else {
                this._prev[n] = -1;
              }
              this._count--;
              return f;
            }
            public dequeue(index: number): boolean {
              let castedIndex: number = <number>index;
              let p: number = this._prev[castedIndex];
              let n: number = this._next[castedIndex];
              if (p == -1 && n == -1) {
                return false;
              }
              if (p == -1) {
                let f: number = this._first;
                if (f == -1) {
                  return false;
                }
                let n2: number = this._next[f];
                this._next[f] = -1;
                this._prev[f] = -1;
                this._first = n2;
                if (n2 == -1) {
                  this._last = -1;
                } else {
                  this._prev[n2] = -1;
                }
                --this._count;
              } else if (n == -1) {
                let l: number = this._last;
                if (l == -1) {
                  return false;
                }
                let p2: number = this._prev[l];
                this._prev[l] = -1;
                this._next[l] = -1;
                this._last = p2;
                if (p2 == -1) {
                  this._first = -1;
                } else {
                  this._next[p2] = -1;
                }
                --this._count;
              } else {
                this._next[p] = n;
                this._prev[n] = p;
                this._prev[castedIndex] = -1;
                this._next[castedIndex] = -1;
                this._count--;
              }

              return true;
            }
            public free(): void {}
            public size(): number {
              return this._count;
            }
          }
          export class HeapGenChunk implements org.mwg.chunk.GenChunk {
            private _space: org.mwg.core.chunk.heap.HeapChunkSpace;
            private _index: number;
            private _prefix: Long;
            private _seed: number;
            private _dirty: boolean;
            constructor(p_space: org.mwg.core.chunk.heap.HeapChunkSpace, p_id: number, p_index: number) {
              this._index = p_index;
              this._space = p_space;
              this._prefix = Long.fromNumber(p_id).shiftLeft((org.mwg.Constants.LONG_SIZE - org.mwg.Constants.PREFIX_SIZE));
              this._seed = -1;
            }
            public save(buffer: org.mwg.struct.Buffer): void {
              org.mwg.utility.Base64.encodeLongToBuffer(this._seed, buffer);
              this._dirty = false;
            }
            public load(buffer: org.mwg.struct.Buffer): void {
              if (buffer == null || buffer.length() == 0) {
                return;
              }
              let loaded: number = org.mwg.utility.Base64.decodeToLongWithBounds(buffer, 0, buffer.length());
              let previousSeed: number = this._seed;
              this._seed = loaded;
              if (previousSeed != -1 && previousSeed != this._seed) {
                if (this._space != null && !this._dirty) {
                  this._dirty = true;
                  this._space.notifyUpdate(this._index);
                }
              }
            }
            public newKey(): number {
              if (this._seed == org.mwg.Constants.KEY_PREFIX_MASK) {
              throw new Error("Object Index could not be created because it exceeded the capacity of the current prefix. Ask for a new prefix.");
              }
              if(this._seed == -1){
              this._seed = 0;
              }
              this._seed++;
              var nextIndex = this._seed;
              if(this._space){
              this._space.notifyUpdate(this._index);
              }
              var objectKey = this._prefix.add(this._seed).toNumber();
              if (objectKey >= org.mwg.Constants.NULL_LONG) {
              throw new Error("Object Index exceeds the maximum JavaScript number capacity. (2^"+org.mwg.Constants.LONG_SIZE+")");
              }
              return objectKey;
            }
            public index(): number {
              return this._index;
            }
            public world(): number {
              return this._space.worldByIndex(this._index);
            }
            public time(): number {
              return this._space.timeByIndex(this._index);
            }
            public id(): number {
              return this._space.idByIndex(this._index);
            }
            public chunkType(): number {
              return org.mwg.chunk.ChunkType.GEN_CHUNK;
            }
          }
          export class HeapLongLongArrayMap implements org.mwg.struct.LongLongArrayMap {
            private parent: org.mwg.core.chunk.heap.HeapStateChunk;
            private mapSize: number = 0;
            private capacity: number = 0;
            private keys: Float64Array = null;
            private values: Float64Array = null;
            private nexts: Int32Array = null;
            private hashs: Int32Array = null;
            constructor(p_listener: org.mwg.core.chunk.heap.HeapStateChunk) {
              this.parent = p_listener;
            }
            private key(i: number): number {
              return this.keys[i];
            }
            private setKey(i: number, newValue: number): void {
              this.keys[i] = newValue;
            }
            private value(i: number): number {
              return this.values[i];
            }
            private setValue(i: number, newValue: number): void {
              this.values[i] = newValue;
            }
            private next(i: number): number {
              return this.nexts[i];
            }
            private setNext(i: number, newValue: number): void {
              this.nexts[i] = newValue;
            }
            private hash(i: number): number {
              return this.hashs[i];
            }
            private setHash(i: number, newValue: number): void {
              this.hashs[i] = newValue;
            }
            public reallocate(newCapacity: number): void {
              if (newCapacity > this.capacity) {
                let new_keys: Float64Array = new Float64Array(newCapacity);
                if (this.keys != null) {
                  java.lang.System.arraycopy(this.keys, 0, new_keys, 0, this.capacity);
                }
                this.keys = new_keys;
                let new_values: Float64Array = new Float64Array(newCapacity);
                if (this.values != null) {
                  java.lang.System.arraycopy(this.values, 0, new_values, 0, this.capacity);
                }
                this.values = new_values;
                let new_nexts: Int32Array = new Int32Array(newCapacity);
                let new_hashes: Int32Array = new Int32Array(newCapacity * 2);
                java.util.Arrays.fill(new_nexts, 0, newCapacity, -1);
                java.util.Arrays.fill(new_hashes, 0, newCapacity * 2, -1);
                this.hashs = new_hashes;
                this.nexts = new_nexts;
                for (let i: number = 0; i < this.mapSize; i++) {
                  let new_key_hash: number = <number>org.mwg.utility.HashHelper.longHash(this.key(i), newCapacity * 2);
                  this.setNext(i, this.hash(new_key_hash));
                  this.setHash(new_key_hash, i);
                }
                this.capacity = newCapacity;
              }
            }
            public cloneFor(newParent: org.mwg.core.chunk.heap.HeapStateChunk): org.mwg.core.chunk.heap.HeapLongLongArrayMap {
              let cloned: org.mwg.core.chunk.heap.HeapLongLongArrayMap = new org.mwg.core.chunk.heap.HeapLongLongArrayMap(newParent);
              cloned.mapSize = this.mapSize;
              cloned.capacity = this.capacity;
              if (this.keys != null) {
                let cloned_keys: Float64Array = new Float64Array(this.capacity);
                java.lang.System.arraycopy(this.keys, 0, cloned_keys, 0, this.capacity);
                cloned.keys = cloned_keys;
              }
              if (this.values != null) {
                let cloned_values: Float64Array = new Float64Array(this.capacity);
                java.lang.System.arraycopy(this.values, 0, cloned_values, 0, this.capacity);
                cloned.values = cloned_values;
              }
              if (this.nexts != null) {
                let cloned_nexts: Int32Array = new Int32Array(this.capacity);
                java.lang.System.arraycopy(this.nexts, 0, cloned_nexts, 0, this.capacity);
                cloned.nexts = cloned_nexts;
              }
              if (this.hashs != null) {
                let cloned_hashs: Int32Array = new Int32Array(this.capacity * 2);
                java.lang.System.arraycopy(this.hashs, 0, cloned_hashs, 0, this.capacity * 2);
                cloned.hashs = cloned_hashs;
              }
              return cloned;
            }
            public get(requestKey: number): Float64Array {
              let result: Float64Array = new Float64Array(0);
{
                if (this.keys != null) {
                  let hashIndex: number = <number>org.mwg.utility.HashHelper.longHash(requestKey, this.capacity * 2);
                  let resultCapacity: number = 0;
                  let resultIndex: number = 0;
                  let m: number = this.hash(hashIndex);
                  while (m >= 0) {
                    if (requestKey == this.key(m)) {
                      if (resultIndex == resultCapacity) {
                        let newCapacity: number;
                        if (resultCapacity == 0) {
                          newCapacity = 1;
                        } else {
                          newCapacity = resultCapacity * 2;
                        }
                        let tempResult: Float64Array = new Float64Array(newCapacity);
                        java.lang.System.arraycopy(result, 0, tempResult, 0, result.length);
                        result = tempResult;
                        resultCapacity = newCapacity;
                      }
                      result[resultIndex] = this.value(m);
                      resultIndex++;
                    }
                    m = this.next(m);
                  }
                  if (resultIndex != resultCapacity) {
                    let shrinkedResult: Float64Array = new Float64Array(resultIndex);
                    java.lang.System.arraycopy(result, 0, shrinkedResult, 0, resultIndex);
                    result = shrinkedResult;
                  }
                }
              }              return result;
            }
            public contains(requestKey: number, requestValue: number): boolean {
              let result: boolean = false;
{
                if (this.keys != null) {
                  let hashIndex: number = <number>org.mwg.utility.HashHelper.longHash(requestKey, this.capacity * 2);
                  let m: number = this.hash(hashIndex);
                  while (m >= 0 && !result) {
                    if (requestKey == this.key(m) && requestValue == this.value(m)) {
                      result = true;
                    }
                    m = this.next(m);
                  }
                }
              }              return result;
            }
            public each(callback: org.mwg.struct.LongLongArrayMapCallBack): void {
{
                this.unsafe_each(callback);
              }            }
            public unsafe_each(callback: org.mwg.struct.LongLongArrayMapCallBack): void {
              for (let i: number = 0; i < this.mapSize; i++) {
                callback(this.key(i), this.value(i));
              }
            }
            public size(): number {
              let result: number;
{
                result = this.mapSize;
              }              return result;
            }
            public remove(requestKey: number, requestValue: number): void {
{
                if (this.keys != null && this.mapSize != 0) {
                  let hashCapacity: number = this.capacity * 2;
                  let hashIndex: number = <number>org.mwg.utility.HashHelper.longHash(requestKey, hashCapacity);
                  let m: number = this.hash(hashIndex);
                  let found: number = -1;
                  while (m >= 0) {
                    if (requestKey == this.key(m) && requestValue == this.value(m)) {
                      found = m;
break;
                    }
                    m = this.next(m);
                  }
                  if (found != -1) {
                    let toRemoveHash: number = <number>org.mwg.utility.HashHelper.longHash(requestKey, hashCapacity);
                    m = this.hash(toRemoveHash);
                    if (m == found) {
                      this.setHash(toRemoveHash, this.next(m));
                    } else {
                      while (m != -1) {
                        let next_of_m: number = this.next(m);
                        if (next_of_m == found) {
                          this.setNext(m, this.next(next_of_m));
break;
                        }
                        m = next_of_m;
                      }
                    }
                    let lastIndex: number = this.mapSize - 1;
                    if (lastIndex == found) {
                      this.mapSize--;
                    } else {
                      let lastKey: number = this.key(lastIndex);
                      this.setKey(found, lastKey);
                      this.setValue(found, this.value(lastIndex));
                      this.setNext(found, this.next(lastIndex));
                      let victimHash: number = <number>org.mwg.utility.HashHelper.longHash(lastKey, hashCapacity);
                      m = this.hash(victimHash);
                      if (m == lastIndex) {
                        this.setHash(victimHash, found);
                      } else {
                        while (m != -1) {
                          let next_of_m: number = this.next(m);
                          if (next_of_m == lastIndex) {
                            this.setNext(m, found);
break;
                          }
                          m = next_of_m;
                        }
                      }
                      this.mapSize--;
                    }
                    this.parent.declareDirty();
                  }
                }
              }            }
            public put(insertKey: number, insertValue: number): void {
{
                if (this.keys == null) {
                  this.reallocate(org.mwg.Constants.MAP_INITIAL_CAPACITY);
                  this.setKey(0, insertKey);
                  this.setValue(0, insertValue);
                  this.setHash(<number>org.mwg.utility.HashHelper.longHash(insertKey, this.capacity * 2), 0);
                  this.setNext(0, -1);
                  this.mapSize++;
                  this.parent.declareDirty();
                } else {
                  let hashCapacity: number = this.capacity * 2;
                  let insertKeyHash: number = <number>org.mwg.utility.HashHelper.longHash(insertKey, hashCapacity);
                  let currentHash: number = this.hash(insertKeyHash);
                  let m: number = currentHash;
                  let found: number = -1;
                  while (m >= 0) {
                    if (insertKey == this.key(m) && insertValue == this.value(m)) {
                      found = m;
break;
                    }
                    m = this.next(m);
                  }
                  if (found == -1) {
                    let lastIndex: number = this.mapSize;
                    if (lastIndex == this.capacity) {
                      this.reallocate(this.capacity * 2);
                    }
                    this.setKey(lastIndex, insertKey);
                    this.setValue(lastIndex, insertValue);
                    this.setHash(<number>org.mwg.utility.HashHelper.longHash(insertKey, this.capacity * 2), lastIndex);
                    this.setNext(lastIndex, currentHash);
                    this.mapSize++;
                    this.parent.declareDirty();
                  }
                }
              }            }
          }
          export class HeapLongLongMap implements org.mwg.struct.LongLongMap {
            private parent: org.mwg.core.chunk.heap.HeapStateChunk;
            private mapSize: number = 0;
            private capacity: number = 0;
            private keys: Float64Array = null;
            private values: Float64Array = null;
            private nexts: Int32Array = null;
            private hashs: Int32Array = null;
            constructor(p_listener: org.mwg.core.chunk.heap.HeapStateChunk) {
              this.parent = p_listener;
            }
            private key(i: number): number {
              return this.keys[i];
            }
            private setKey(i: number, newValue: number): void {
              this.keys[i] = newValue;
            }
            private value(i: number): number {
              return this.values[i];
            }
            private setValue(i: number, newValue: number): void {
              this.values[i] = newValue;
            }
            private next(i: number): number {
              return this.nexts[i];
            }
            private setNext(i: number, newValue: number): void {
              this.nexts[i] = newValue;
            }
            private hash(i: number): number {
              return this.hashs[i];
            }
            private setHash(i: number, newValue: number): void {
              this.hashs[i] = newValue;
            }
            public reallocate(newCapacity: number): void {
              if (newCapacity > this.capacity) {
                let new_keys: Float64Array = new Float64Array(newCapacity);
                if (this.keys != null) {
                  java.lang.System.arraycopy(this.keys, 0, new_keys, 0, this.capacity);
                }
                this.keys = new_keys;
                let new_values: Float64Array = new Float64Array(newCapacity);
                if (this.values != null) {
                  java.lang.System.arraycopy(this.values, 0, new_values, 0, this.capacity);
                }
                this.values = new_values;
                let new_nexts: Int32Array = new Int32Array(newCapacity);
                let new_hashes: Int32Array = new Int32Array(newCapacity * 2);
                java.util.Arrays.fill(new_nexts, 0, newCapacity, -1);
                java.util.Arrays.fill(new_hashes, 0, newCapacity * 2, -1);
                this.hashs = new_hashes;
                this.nexts = new_nexts;
                for (let i: number = 0; i < this.mapSize; i++) {
                  let new_key_hash: number = <number>org.mwg.utility.HashHelper.longHash(this.key(i), newCapacity * 2);
                  this.setNext(i, this.hash(new_key_hash));
                  this.setHash(new_key_hash, i);
                }
                this.capacity = newCapacity;
              }
            }
            public cloneFor(newParent: org.mwg.core.chunk.heap.HeapStateChunk): org.mwg.core.chunk.heap.HeapLongLongMap {
              let cloned: org.mwg.core.chunk.heap.HeapLongLongMap = new org.mwg.core.chunk.heap.HeapLongLongMap(newParent);
              cloned.mapSize = this.mapSize;
              cloned.capacity = this.capacity;
              if (this.keys != null) {
                let cloned_keys: Float64Array = new Float64Array(this.capacity);
                java.lang.System.arraycopy(this.keys, 0, cloned_keys, 0, this.capacity);
                cloned.keys = cloned_keys;
              }
              if (this.values != null) {
                let cloned_values: Float64Array = new Float64Array(this.capacity);
                java.lang.System.arraycopy(this.values, 0, cloned_values, 0, this.capacity);
                cloned.values = cloned_values;
              }
              if (this.nexts != null) {
                let cloned_nexts: Int32Array = new Int32Array(this.capacity);
                java.lang.System.arraycopy(this.nexts, 0, cloned_nexts, 0, this.capacity);
                cloned.nexts = cloned_nexts;
              }
              if (this.hashs != null) {
                let cloned_hashs: Int32Array = new Int32Array(this.capacity * 2);
                java.lang.System.arraycopy(this.hashs, 0, cloned_hashs, 0, this.capacity * 2);
                cloned.hashs = cloned_hashs;
              }
              return cloned;
            }
            public get(requestKey: number): number {
              let result: number = org.mwg.Constants.NULL_LONG;
{
                if (this.keys != null) {
                  let hashIndex: number = <number>org.mwg.utility.HashHelper.longHash(requestKey, this.capacity * 2);
                  let m: number = this.hash(hashIndex);
                  while (m >= 0) {
                    if (requestKey == this.key(m)) {
                      result = this.value(m);
break;
                    }
                    m = this.next(m);
                  }
                }
              }              return result;
            }
            public each(callback: org.mwg.struct.LongLongMapCallBack): void {
{
                this.unsafe_each(callback);
              }            }
            public unsafe_each(callback: org.mwg.struct.LongLongMapCallBack): void {
              for (let i: number = 0; i < this.mapSize; i++) {
                callback(this.key(i), this.value(i));
              }
            }
            public size(): number {
              let result: number;
{
                result = this.mapSize;
              }              return result;
            }
            public remove(requestKey: number): void {
{
                if (this.keys != null && this.mapSize != 0) {
                  let hashCapacity: number = this.capacity * 2;
                  let hashIndex: number = <number>org.mwg.utility.HashHelper.longHash(requestKey, hashCapacity);
                  let m: number = this.hash(hashIndex);
                  let found: number = -1;
                  while (m >= 0) {
                    if (requestKey == this.key(m)) {
                      found = m;
break;
                    }
                    m = this.next(m);
                  }
                  if (found != -1) {
                    let toRemoveHash: number = <number>org.mwg.utility.HashHelper.longHash(requestKey, hashCapacity);
                    m = this.hash(toRemoveHash);
                    if (m == found) {
                      this.setHash(toRemoveHash, this.next(m));
                    } else {
                      while (m != -1) {
                        let next_of_m: number = this.next(m);
                        if (next_of_m == found) {
                          this.setNext(m, this.next(next_of_m));
break;
                        }
                        m = next_of_m;
                      }
                    }
                    let lastIndex: number = this.mapSize - 1;
                    if (lastIndex == found) {
                      this.mapSize--;
                    } else {
                      let lastKey: number = this.key(lastIndex);
                      this.setKey(found, lastKey);
                      this.setValue(found, this.value(lastIndex));
                      this.setNext(found, this.next(lastIndex));
                      let victimHash: number = <number>org.mwg.utility.HashHelper.longHash(lastKey, hashCapacity);
                      m = this.hash(victimHash);
                      if (m == lastIndex) {
                        this.setHash(victimHash, found);
                      } else {
                        while (m != -1) {
                          let next_of_m: number = this.next(m);
                          if (next_of_m == lastIndex) {
                            this.setNext(m, found);
break;
                          }
                          m = next_of_m;
                        }
                      }
                      this.mapSize--;
                    }
                    this.parent.declareDirty();
                  }
                }
              }            }
            public put(insertKey: number, insertValue: number): void {
{
                if (this.keys == null) {
                  this.reallocate(org.mwg.Constants.MAP_INITIAL_CAPACITY);
                  this.setKey(0, insertKey);
                  this.setValue(0, insertValue);
                  this.setHash(<number>org.mwg.utility.HashHelper.longHash(insertKey, this.capacity * 2), 0);
                  this.setNext(0, -1);
                  this.mapSize++;
                } else {
                  let hashCapacity: number = this.capacity * 2;
                  let insertKeyHash: number = <number>org.mwg.utility.HashHelper.longHash(insertKey, hashCapacity);
                  let currentHash: number = this.hash(insertKeyHash);
                  let m: number = currentHash;
                  let found: number = -1;
                  while (m >= 0) {
                    if (insertKey == this.key(m)) {
                      found = m;
break;
                    }
                    m = this.next(m);
                  }
                  if (found == -1) {
                    let lastIndex: number = this.mapSize;
                    if (lastIndex == this.capacity) {
                      this.reallocate(this.capacity * 2);
                    }
                    this.setKey(lastIndex, insertKey);
                    this.setValue(lastIndex, insertValue);
                    this.setHash(<number>org.mwg.utility.HashHelper.longHash(insertKey, this.capacity * 2), lastIndex);
                    this.setNext(lastIndex, currentHash);
                    this.mapSize++;
                    this.parent.declareDirty();
                  } else {
                    if (this.value(found) != insertValue) {
                      this.setValue(found, insertValue);
                      this.parent.declareDirty();
                    }
                  }
                }
              }            }
          }
          export class HeapRelationship implements org.mwg.struct.Relationship {
            private _back: Float64Array;
            private _size: number;
            private parent: org.mwg.core.chunk.heap.HeapStateChunk;
            private aligned: boolean = true;
            constructor(p_listener: org.mwg.core.chunk.heap.HeapStateChunk, origin: org.mwg.core.chunk.heap.HeapRelationship) {
              this.parent = p_listener;
              if (origin != null) {
                this.aligned = false;
                this._back = origin._back;
                this._size = origin._size;
              } else {
                this._back = null;
                this._size = 0;
              }
            }
            public allocate(_capacity: number): void {
              let new_back: Float64Array = new Float64Array(_capacity);
              if (this._back != null) {
                java.lang.System.arraycopy(this._back, 0, new_back, 0, this._back.length);
              }
              this._back = new_back;
            }
            public size(): number {
              return this._size;
            }
            public get(index: number): number {
              let result: number;
{
                result = this._back[index];
              }              return result;
            }
            public unsafe_get(index: number): number {
              return this._back[index];
            }
            public add(newValue: number): org.mwg.struct.Relationship {
{
                if (!this.aligned) {
                  let temp_back: Float64Array = new Float64Array(this._back.length);
                  java.lang.System.arraycopy(this._back, 0, temp_back, 0, this._back.length);
                  this._back = temp_back;
                  this.aligned = true;
                }
                if (this._back == null) {
                  this._back = new Float64Array(org.mwg.Constants.MAP_INITIAL_CAPACITY);
                  this._back[0] = newValue;
                  this._size = 1;
                } else if (this._size == this._back.length) {
                  let ex_back: Float64Array = new Float64Array(this._back.length * 2);
                  java.lang.System.arraycopy(this._back, 0, ex_back, 0, this._size);
                  this._back = ex_back;
                  this._back[this._size] = newValue;
                  this._size++;
                } else {
                  this._back[this._size] = newValue;
                  this._size++;
                }

                this.parent.declareDirty();
              }              return this;
            }
            public remove(oldValue: number): org.mwg.struct.Relationship {
{
                if (!this.aligned) {
                  let temp_back: Float64Array = new Float64Array(this._back.length);
                  java.lang.System.arraycopy(this._back, 0, temp_back, 0, this._back.length);
                  this._back = temp_back;
                  this.aligned = true;
                }
                let indexToRemove: number = -1;
                for (let i: number = 0; i < this._size; i++) {
                  if (this._back[i] == oldValue) {
                    indexToRemove = i;
break;
                  }
                }
                if (indexToRemove != -1) {
                  if ((this._size - 1) == 0) {
                    this._back = null;
                    this._size = 0;
                  } else {
                    let red_back: Float64Array = new Float64Array(this._size - 1);
                    java.lang.System.arraycopy(this._back, 0, red_back, 0, indexToRemove);
                    java.lang.System.arraycopy(this._back, indexToRemove + 1, red_back, indexToRemove, this._size - indexToRemove - 1);
                    this._back = red_back;
                    this._size--;
                  }
                }
              }              return this;
            }
            public clear(): org.mwg.struct.Relationship {
{
                this._size = 0;
                if (!this.aligned) {
                  this._back = null;
                  this.aligned = true;
                } else {
                  if (this._back != null) {
                    java.util.Arrays.fill(this._back, 0, this._back.length, -1);
                  }
                }
              }              return this;
            }
            public toString(): string {
              let buffer: java.lang.StringBuilder = new java.lang.StringBuilder();
              buffer.append("[");
              for (let i: number = 0; i < this._size; i++) {
                if (i != 0) {
                  buffer.append(",");
                }
                buffer.append(this._back[i]);
              }
              buffer.append("]");
              return buffer.toString();
            }
          }
          export class HeapStateChunk implements org.mwg.chunk.StateChunk {
            private _index: number;
            private _space: org.mwg.core.chunk.heap.HeapChunkSpace;
            private _capacity: number;
            private _size: number;
            private _k: Float64Array;
            private _v: any[];
            private _next: Int32Array;
            private _hash: Int32Array;
            private _type: Int8Array;
            private _dirty: boolean;
            constructor(p_space: org.mwg.core.chunk.heap.HeapChunkSpace, p_index: number) {
              this._space = p_space;
              this._index = p_index;
              this._next = null;
              this._hash = null;
              this._type = null;
              this._size = 0;
              this._capacity = 0;
              this._dirty = false;
            }
            public world(): number {
              return this._space.worldByIndex(this._index);
            }
            public time(): number {
              return this._space.timeByIndex(this._index);
            }
            public id(): number {
              return this._space.idByIndex(this._index);
            }
            public chunkType(): number {
              return org.mwg.chunk.ChunkType.STATE_CHUNK;
            }
            public index(): number {
              return this._index;
            }
            public get(p_key: number): any {
              return this.internal_get(p_key);
            }
            private internal_find(p_key: number): number {
              if (this._size == 0) {
                return -1;
              } else if (this._hash == null) {
                for (let i: number = 0; i < this._size; i++) {
                  if (this._k[i] == p_key) {
                    return i;
                  }
                }
                return -1;
              } else {
                let hashIndex: number = <number>org.mwg.utility.HashHelper.longHash(p_key, this._capacity * 2);
                let m: number = this._hash[hashIndex];
                while (m >= 0) {
                  if (p_key == this._k[m]) {
                    return m;
                  } else {
                    m = this._next[m];
                  }
                }
                return -1;
              }

            }
            private internal_get(p_key: number): any {
              if (this._size == 0) {
                return null;
              }
              let found: number = this.internal_find(p_key);
              let result: any;
              if (found != -1) {
                result = this._v[found];
                if (result != null) {
                  switch (this._type[found]) {
                    case org.mwg.Type.DOUBLE_ARRAY:                    let castedResultD: Float64Array = <Float64Array>result;
                    let copyD: Float64Array = new Float64Array(castedResultD.length);

                      java.lang.System.arraycopy(castedResultD, 0, copyD, 0, castedResultD.length);

                      return copyD;
                    case org.mwg.Type.LONG_ARRAY:                    let castedResultL: Float64Array = <Float64Array>result;
                    let copyL: Float64Array = new Float64Array(castedResultL.length);

                      java.lang.System.arraycopy(castedResultL, 0, copyL, 0, castedResultL.length);

                      return copyL;
                    case org.mwg.Type.INT_ARRAY:                    let castedResultI: Int32Array = <Int32Array>result;
                    let copyI: Int32Array = new Int32Array(castedResultI.length);

                      java.lang.System.arraycopy(castedResultI, 0, copyI, 0, castedResultI.length);

                      return copyI;
                    default: 

                      return result;
                  }
                }
              }
              return null;
            }
            public set(p_elementIndex: number, p_elemType: number, p_unsafe_elem: any): void {
              if(p_unsafe_elem != null){
              if(p_elemType == org.mwg.Type.STRING){ if(!(typeof p_unsafe_elem === 'string')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
              if(p_elemType == org.mwg.Type.BOOL){ if(!(typeof p_unsafe_elem === 'boolean')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
              if(p_elemType == org.mwg.Type.DOUBLE || p_elemType == org.mwg.Type.LONG || p_elemType == org.mwg.Type.INT){ if(!(typeof p_unsafe_elem === 'number')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
              if(p_elemType == org.mwg.Type.DOUBLE_ARRAY){ if(!(p_unsafe_elem instanceof Float64Array)){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
              if(p_elemType == org.mwg.Type.LONG_ARRAY){ if(!(p_unsafe_elem instanceof Float64Array)){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
              if(p_elemType == org.mwg.Type.INT_ARRAY){ if(!(p_unsafe_elem instanceof Int32Array)){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
              if(p_elemType == org.mwg.Type.STRING_TO_LONG_MAP){ if(!(typeof p_unsafe_elem === 'object')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
              if(p_elemType == org.mwg.Type.LONG_TO_LONG_MAP){ if(!(typeof p_unsafe_elem === 'boolean')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
              if(p_elemType == org.mwg.Type.LONG_TO_LONG_ARRAY_MAP){ if(!(typeof p_unsafe_elem === 'boolean')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
              }
              this.internal_set(p_elementIndex, p_elemType, p_unsafe_elem, true, false);
            }
            public setFromKey(key: string, p_elemType: number, p_unsafe_elem: any): void {
              this.internal_set(this._space.graph().resolver().stringToHash(key, true), p_elemType, p_unsafe_elem, true, false);
            }
            public getFromKey(key: string): any {
              return this.internal_get(this._space.graph().resolver().stringToHash(key, false));
            }
            public getFromKeyWithDefault<A>(key: string, defaultValue: A): A {
              let result: any = this.getFromKey(key);
              if (result == null) {
                return defaultValue;
              } else {
                return <A>result;
              }
            }
            public getWithDefault<A>(key: number, defaultValue: A): A {
              let result: any = this.get(key);
              if (result == null) {
                return defaultValue;
              } else {
                return <A>result;
              }
            }
            public getType(p_key: number): number {
              if (this._size == 0) {
                return -1;
              }
              if (this._hash == null) {
                for (let i: number = 0; i < this._capacity; i++) {
                  if (this._k[i] == p_key) {
                    return this._type[i];
                  }
                }
              } else {
                let hashIndex: number = <number>org.mwg.utility.HashHelper.longHash(p_key, this._capacity * 2);
                let m: number = this._hash[hashIndex];
                while (m >= 0) {
                  if (p_key == this._k[m]) {
                    return this._type[m];
                  } else {
                    m = this._next[m];
                  }
                }
              }
              return -1;
            }
            public getTypeFromKey(key: string): number {
              return this.getType(this._space.graph().resolver().stringToHash(key, false));
            }
            public getOrCreate(p_key: number, p_type: number): any {
              let found: number = this.internal_find(p_key);
              if (found != -1) {
                if (this._type[found] == p_type) {
                  return this._v[found];
                }
              }
              let toSet: any = null;
              switch (p_type) {
                case org.mwg.Type.RELATION:
                  toSet = new org.mwg.core.chunk.heap.HeapRelationship(this, null);
break;
                case org.mwg.Type.STRING_TO_LONG_MAP:
                  toSet = new org.mwg.core.chunk.heap.HeapStringLongMap(this);
break;
                case org.mwg.Type.LONG_TO_LONG_MAP:
                  toSet = new org.mwg.core.chunk.heap.HeapLongLongMap(this);
break;
                case org.mwg.Type.LONG_TO_LONG_ARRAY_MAP:
                  toSet = new org.mwg.core.chunk.heap.HeapLongLongArrayMap(this);
break;
              }
              this.internal_set(p_key, p_type, toSet, true, false);
              return toSet;
            }
            public getOrCreateFromKey(key: string, elemType: number): any {
              return this.getOrCreate(this._space.graph().resolver().stringToHash(key, true), elemType);
            }
            public declareDirty(): void {
              if (this._space != null && !this._dirty) {
                this._dirty = true;
                this._space.notifyUpdate(this._index);
              }
            }
            public save(buffer: org.mwg.struct.Buffer): void {
              org.mwg.utility.Base64.encodeIntToBuffer(this._size, buffer);
              for (let i: number = 0; i < this._size; i++) {
                if (this._v[i] != null) {
                  let loopValue: any = this._v[i];
                  if (loopValue != null) {
                    buffer.write(org.mwg.core.CoreConstants.CHUNK_SEP);
                    org.mwg.utility.Base64.encodeLongToBuffer(this._k[i], buffer);
                    buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SEP);
                    org.mwg.utility.Base64.encodeIntToBuffer(this._type[i], buffer);
                    buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SEP);
                    switch (this._type[i]) {
                      case org.mwg.Type.STRING:
                        org.mwg.utility.Base64.encodeStringToBuffer(<string>loopValue, buffer);
break;
                      case org.mwg.Type.BOOL:                      if (<boolean>this._v[i]) {
                        buffer.write(org.mwg.core.CoreConstants.BOOL_TRUE);
                      } else {
                        buffer.write(org.mwg.core.CoreConstants.BOOL_FALSE);
                      }
break;
                      case org.mwg.Type.LONG:
                        org.mwg.utility.Base64.encodeLongToBuffer(<number>loopValue, buffer);
break;
                      case org.mwg.Type.DOUBLE:
                        org.mwg.utility.Base64.encodeDoubleToBuffer(<number>loopValue, buffer);
break;
                      case org.mwg.Type.INT:
                        org.mwg.utility.Base64.encodeIntToBuffer(<number>loopValue, buffer);
break;
                      case org.mwg.Type.DOUBLE_ARRAY:                      let castedDoubleArr: Float64Array = <Float64Array>loopValue;

                        org.mwg.utility.Base64.encodeIntToBuffer(castedDoubleArr.length, buffer);
                      for (let j: number = 0; j < castedDoubleArr.length; j++) {
                        buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SUB_SEP);
                        org.mwg.utility.Base64.encodeDoubleToBuffer(castedDoubleArr[j], buffer);
                      }
break;
                      case org.mwg.Type.RELATION:                      let castedLongArrRel: org.mwg.core.chunk.heap.HeapRelationship = <org.mwg.core.chunk.heap.HeapRelationship>loopValue;

                        org.mwg.utility.Base64.encodeIntToBuffer(castedLongArrRel.size(), buffer);
                      for (let j: number = 0; j < castedLongArrRel.size(); j++) {
                        buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SUB_SEP);
                        org.mwg.utility.Base64.encodeLongToBuffer(castedLongArrRel.unsafe_get(j), buffer);
                      }
break;
                      case org.mwg.Type.LONG_ARRAY:                      let castedLongArr: Float64Array = <Float64Array>loopValue;

                        org.mwg.utility.Base64.encodeIntToBuffer(castedLongArr.length, buffer);
                      for (let j: number = 0; j < castedLongArr.length; j++) {
                        buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SUB_SEP);
                        org.mwg.utility.Base64.encodeLongToBuffer(castedLongArr[j], buffer);
                      }
break;
                      case org.mwg.Type.INT_ARRAY:                      let castedIntArr: Int32Array = <Int32Array>loopValue;

                        org.mwg.utility.Base64.encodeIntToBuffer(castedIntArr.length, buffer);
                      for (let j: number = 0; j < castedIntArr.length; j++) {
                        buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SUB_SEP);
                        org.mwg.utility.Base64.encodeIntToBuffer(castedIntArr[j], buffer);
                      }
break;
                      case org.mwg.Type.STRING_TO_LONG_MAP:                      let castedStringLongMap: org.mwg.core.chunk.heap.HeapStringLongMap = <org.mwg.core.chunk.heap.HeapStringLongMap>loopValue;

                        org.mwg.utility.Base64.encodeLongToBuffer(castedStringLongMap.size(), buffer);

                        castedStringLongMap.unsafe_each((key : string, value : number) => {
{
                            buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SUB_SEP);
                            org.mwg.utility.Base64.encodeStringToBuffer(key, buffer);
                            buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SUB_SUB_SEP);
                            org.mwg.utility.Base64.encodeLongToBuffer(value, buffer);
                          }                        });
break;
                      case org.mwg.Type.LONG_TO_LONG_MAP:                      let castedLongLongMap: org.mwg.core.chunk.heap.HeapLongLongMap = <org.mwg.core.chunk.heap.HeapLongLongMap>loopValue;

                        org.mwg.utility.Base64.encodeLongToBuffer(castedLongLongMap.size(), buffer);

                        castedLongLongMap.unsafe_each((key : number, value : number) => {
{
                            buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SUB_SEP);
                            org.mwg.utility.Base64.encodeLongToBuffer(key, buffer);
                            buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SUB_SUB_SEP);
                            org.mwg.utility.Base64.encodeLongToBuffer(value, buffer);
                          }                        });
break;
                      case org.mwg.Type.LONG_TO_LONG_ARRAY_MAP:                      let castedLongLongArrayMap: org.mwg.core.chunk.heap.HeapLongLongArrayMap = <org.mwg.core.chunk.heap.HeapLongLongArrayMap>loopValue;

                        org.mwg.utility.Base64.encodeLongToBuffer(castedLongLongArrayMap.size(), buffer);

                        castedLongLongArrayMap.unsafe_each((key : number, value : number) => {
{
                            buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SUB_SEP);
                            org.mwg.utility.Base64.encodeLongToBuffer(key, buffer);
                            buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SUB_SUB_SEP);
                            org.mwg.utility.Base64.encodeLongToBuffer(value, buffer);
                          }                        });
break;
                      default: 
break;
                    }
                  }
                }
              }
              this._dirty = false;
            }
            public each(callBack: org.mwg.plugin.NodeStateCallback): void {
              for (let i: number = 0; i < this._size; i++) {
                if (this._v[i] != null) {
                  callBack(this._k[i], this._type[i], this._v[i]);
                }
              }
            }
            public loadFrom(origin: org.mwg.chunk.StateChunk): void {
              if (origin == null) {
                return;
              }
              let casted: org.mwg.core.chunk.heap.HeapStateChunk = <org.mwg.core.chunk.heap.HeapStateChunk>origin;
              this._capacity = casted._capacity;
              this._size = casted._size;
              if (casted._k != null) {
                let cloned_k: Float64Array = new Float64Array(this._capacity);
                java.lang.System.arraycopy(casted._k, 0, cloned_k, 0, this._capacity);
                this._k = cloned_k;
              }
              if (casted._type != null) {
                let cloned_type: Int8Array = new Int8Array(this._capacity);
                java.lang.System.arraycopy(casted._type, 0, cloned_type, 0, this._capacity);
                this._type = cloned_type;
              }
              if (casted._next != null) {
                let cloned_next: Int32Array = new Int32Array(this._capacity);
                java.lang.System.arraycopy(casted._next, 0, cloned_next, 0, this._capacity);
                this._next = cloned_next;
              }
              if (casted._hash != null) {
                let cloned_hash: Int32Array = new Int32Array(this._capacity * 2);
                java.lang.System.arraycopy(casted._hash, 0, cloned_hash, 0, this._capacity * 2);
                this._hash = cloned_hash;
              }
              if (casted._v != null) {
                this._v = new Array<any>(this._capacity);
                for (let i: number = 0; i < this._size; i++) {
                  switch (casted._type[i]) {
                    case org.mwg.Type.LONG_TO_LONG_MAP:                    if (casted._v[i] != null) {
                      this._v[i] = (<org.mwg.core.chunk.heap.HeapLongLongMap>casted._v[i]).cloneFor(this);
                    }
break;
                    case org.mwg.Type.LONG_TO_LONG_ARRAY_MAP:                    if (casted._v[i] != null) {
                      this._v[i] = (<org.mwg.core.chunk.heap.HeapLongLongArrayMap>casted._v[i]).cloneFor(this);
                    }
break;
                    case org.mwg.Type.STRING_TO_LONG_MAP:                    if (casted._v[i] != null) {
                      this._v[i] = (<org.mwg.core.chunk.heap.HeapStringLongMap>casted._v[i]).cloneFor(this);
                    }
break;
                    case org.mwg.Type.RELATION:                    if (casted._v[i] != null) {
                      this._v[i] = new org.mwg.core.chunk.heap.HeapRelationship(this, <org.mwg.core.chunk.heap.HeapRelationship>casted._v[i]);
                    }
break;
                    default: 

                      this._v[i] = casted._v[i];
break;
                  }
                }
              }
            }
            private internal_set(p_key: number, p_type: number, p_unsafe_elem: any, replaceIfPresent: boolean, initial: boolean): void {
              let param_elem: any = null;
              if (p_unsafe_elem != null) {
                try {
                  switch (p_type) {
                    case org.mwg.Type.BOOL:
                      param_elem = <boolean>p_unsafe_elem;
break;
                    case org.mwg.Type.DOUBLE:
                      param_elem = <number>p_unsafe_elem;
break;
                    case org.mwg.Type.LONG:                    if (p_unsafe_elem instanceof Number) {
                      let preCasting: number = <number>p_unsafe_elem;
                      param_elem = <number>preCasting;
                    } else {
                      param_elem = <number>p_unsafe_elem;
                    }
break;
                    case org.mwg.Type.INT:
                      param_elem = <number>p_unsafe_elem;
break;
                    case org.mwg.Type.STRING:
                      param_elem = <string>p_unsafe_elem;
break;
                    case org.mwg.Type.RELATION:
                      param_elem = <org.mwg.struct.Relationship>p_unsafe_elem;
break;
                    case org.mwg.Type.DOUBLE_ARRAY:                    let castedParamDouble: Float64Array = <Float64Array>p_unsafe_elem;
                    let clonedDoubleArray: Float64Array = new Float64Array(castedParamDouble.length);

                      java.lang.System.arraycopy(castedParamDouble, 0, clonedDoubleArray, 0, castedParamDouble.length);

                      param_elem = clonedDoubleArray;
break;
                    case org.mwg.Type.LONG_ARRAY:                    let castedParamLong: Float64Array = <Float64Array>p_unsafe_elem;
                    let clonedLongArray: Float64Array = new Float64Array(castedParamLong.length);

                      java.lang.System.arraycopy(castedParamLong, 0, clonedLongArray, 0, castedParamLong.length);

                      param_elem = clonedLongArray;
break;
                    case org.mwg.Type.INT_ARRAY:                    let castedParamInt: Int32Array = <Int32Array>p_unsafe_elem;
                    let clonedIntArray: Int32Array = new Int32Array(castedParamInt.length);

                      java.lang.System.arraycopy(castedParamInt, 0, clonedIntArray, 0, castedParamInt.length);

                      param_elem = clonedIntArray;
break;
                    case org.mwg.Type.STRING_TO_LONG_MAP:
                      param_elem = <org.mwg.struct.StringLongMap>p_unsafe_elem;
break;
                    case org.mwg.Type.LONG_TO_LONG_MAP:
                      param_elem = <org.mwg.struct.LongLongMap>p_unsafe_elem;
break;
                    case org.mwg.Type.LONG_TO_LONG_ARRAY_MAP:
                      param_elem = <org.mwg.struct.LongLongArrayMap>p_unsafe_elem;
break;
                    default: 
                    throw new Error("Internal Exception, unknown type");
                  }
                } catch ($ex$) {
                  if ($ex$ instanceof Error) {
                    var e: Error = <Error>$ex$;
{
                      throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_type) + " while param object is " + p_unsafe_elem);
                    }                  } else {
                    throw $ex$;
                  }
                }
              }
              if (this._k == null) {
                if (param_elem == null) {
                  return;
                }
                this._capacity = org.mwg.Constants.MAP_INITIAL_CAPACITY;
                this._k = new Float64Array(this._capacity);
                this._v = new Array<any>(this._capacity);
                this._type = new Int8Array(this._capacity);
                this._k[0] = p_key;
                this._v[0] = param_elem;
                this._type[0] = p_type;
                this._size = 1;
                return;
              }
              let entry: number = -1;
              let p_entry: number = -1;
              let hashIndex: number = -1;
              if (this._hash == null) {
                for (let i: number = 0; i < this._size; i++) {
                  if (this._k[i] == p_key) {
                    entry = i;
break;
                  }
                }
              } else {
                hashIndex = <number>org.mwg.utility.HashHelper.longHash(p_key, this._capacity * 2);
                let m: number = this._hash[hashIndex];
                while (m != -1) {
                  if (this._k[m] == p_key) {
                    entry = m;
break;
                  }
                  p_entry = m;
                  m = this._next[m];
                }
              }
              if (entry != -1) {
                if (replaceIfPresent || (p_type != this._type[entry])) {
                  if (param_elem == null) {
                    if (this._hash != null) {
                      if (p_entry != -1) {
                        this._next[p_entry] = this._next[entry];
                      } else {
                        this._hash[hashIndex] = -1;
                      }
                    }
                    let indexVictim: number = this._size - 1;
                    if (entry == indexVictim) {
                      this._k[entry] = -1;
                      this._v[entry] = null;
                      this._type[entry] = -1;
                    } else {
                      this._k[entry] = this._k[indexVictim];
                      this._v[entry] = this._v[indexVictim];
                      this._type[entry] = this._type[indexVictim];
                      if (this._hash != null) {
                        this._next[entry] = this._next[indexVictim];
                        let victimHash: number = <number>org.mwg.utility.HashHelper.longHash(this._k[entry], this._capacity * 2);
                        let m: number = this._hash[victimHash];
                        if (m == indexVictim) {
                          this._hash[victimHash] = entry;
                        } else {
                          while (m != -1) {
                            if (this._next[m] == indexVictim) {
                              this._next[m] = entry;
break;
                            }
                            m = this._next[m];
                          }
                        }
                      }
                    }
                    this._size--;
                  } else {
                    this._v[entry] = param_elem;
                    if (this._type[entry] != p_type) {
                      this._type[entry] = p_type;
                    }
                  }
                }
                if (!initial) {
                  this.declareDirty();
                }
                return;
              }
              if (this._size < this._capacity) {
                this._k[this._size] = p_key;
                this._v[this._size] = param_elem;
                this._type[this._size] = p_type;
                if (this._hash != null) {
                  this._next[this._size] = this._hash[hashIndex];
                  this._hash[hashIndex] = this._size;
                }
                this._size++;
                this.declareDirty();
                return;
              }
              let newCapacity: number = this._capacity * 2;
              let ex_k: Float64Array = new Float64Array(newCapacity);
              java.lang.System.arraycopy(this._k, 0, ex_k, 0, this._capacity);
              this._k = ex_k;
              let ex_v: any[] = new Array<any>(newCapacity);
              java.lang.System.arraycopy(this._v, 0, ex_v, 0, this._capacity);
              this._v = ex_v;
              let ex_type: Int8Array = new Int8Array(newCapacity);
              java.lang.System.arraycopy(this._type, 0, ex_type, 0, this._capacity);
              this._type = ex_type;
              this._capacity = newCapacity;
              this._k[this._size] = p_key;
              this._v[this._size] = param_elem;
              this._type[this._size] = p_type;
              this._size++;
              this._hash = new Int32Array(this._capacity * 2);
              java.util.Arrays.fill(this._hash, 0, this._capacity * 2, -1);
              this._next = new Int32Array(this._capacity);
              java.util.Arrays.fill(this._next, 0, this._capacity, -1);
              for (let i: number = 0; i < this._size; i++) {
                let keyHash: number = <number>org.mwg.utility.HashHelper.longHash(this._k[i], this._capacity * 2);
                this._next[i] = this._hash[keyHash];
                this._hash[keyHash] = i;
              }
              if (!initial) {
                this.declareDirty();
              }
            }
            private allocate(newCapacity: number): void {
              if (newCapacity <= this._capacity) {
                return;
              }
              let ex_k: Float64Array = new Float64Array(newCapacity);
              if (this._k != null) {
                java.lang.System.arraycopy(this._k, 0, ex_k, 0, this._capacity);
              }
              this._k = ex_k;
              let ex_v: any[] = new Array<any>(newCapacity);
              if (this._v != null) {
                java.lang.System.arraycopy(this._v, 0, ex_v, 0, this._capacity);
              }
              this._v = ex_v;
              let ex_type: Int8Array = new Int8Array(newCapacity);
              if (this._type != null) {
                java.lang.System.arraycopy(this._type, 0, ex_type, 0, this._capacity);
              }
              this._type = ex_type;
              this._capacity = newCapacity;
              this._hash = new Int32Array(this._capacity * 2);
              java.util.Arrays.fill(this._hash, 0, this._capacity * 2, -1);
              this._next = new Int32Array(this._capacity);
              java.util.Arrays.fill(this._next, 0, this._capacity, -1);
              for (let i: number = 0; i < this._size; i++) {
                let keyHash: number = <number>org.mwg.utility.HashHelper.longHash(this._k[i], this._capacity * 2);
                this._next[i] = this._hash[keyHash];
                this._hash[keyHash] = i;
              }
            }
            public load(buffer: org.mwg.struct.Buffer): void {
              if (buffer == null || buffer.length() == 0) {
                return;
              }
              let initial: boolean = this._k == null;
              let cursor: number = 0;
              let payloadSize: number = buffer.length();
              let previousStart: number = -1;
              let currentChunkElemKey: number = org.mwg.core.CoreConstants.NULL_LONG;
              let currentChunkElemType: number = -1;
              let isFirstElem: boolean = true;
              let currentDoubleArr: Float64Array = null;
              let currentLongArr: Float64Array = null;
              let currentIntArr: Int32Array = null;
              let currentRelation: org.mwg.core.chunk.heap.HeapRelationship = null;
              let currentStringLongMap: org.mwg.core.chunk.heap.HeapStringLongMap = null;
              let currentLongLongMap: org.mwg.core.chunk.heap.HeapLongLongMap = null;
              let currentLongLongArrayMap: org.mwg.core.chunk.heap.HeapLongLongArrayMap = null;
              let currentSubSize: number = -1;
              let currentSubIndex: number = 0;
              let currentMapLongKey: number = org.mwg.core.CoreConstants.NULL_LONG;
              let currentMapStringKey: string = null;
              while (cursor < payloadSize) {
                let current: number = buffer.read(cursor);
                if (current == org.mwg.core.CoreConstants.CHUNK_SEP) {
                  if (isFirstElem) {
                    isFirstElem = false;
                    let stateChunkSize: number = org.mwg.utility.Base64.decodeToIntWithBounds(buffer, 0, cursor);
                    let closePowerOfTwo: number = <number>Math.pow(2, Math.ceil(Math.log(stateChunkSize) / Math.log(2)));
                    this.allocate(closePowerOfTwo);
                    previousStart = cursor + 1;
                  } else {
                    if (currentChunkElemType != -1) {
                      let toInsert: any = null;
                      switch (currentChunkElemType) {
                        case org.mwg.Type.BOOL:                        if (buffer.read(previousStart) == org.mwg.core.CoreConstants.BOOL_FALSE) {
                          toInsert = false;
                        } else if (buffer.read(previousStart) == org.mwg.core.CoreConstants.BOOL_TRUE) {
                          toInsert = true;
                        }

break;
                        case org.mwg.Type.STRING:
                          toInsert = org.mwg.utility.Base64.decodeToStringWithBounds(buffer, previousStart, cursor);
break;
                        case org.mwg.Type.DOUBLE:
                          toInsert = org.mwg.utility.Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
break;
                        case org.mwg.Type.LONG:
                          toInsert = org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
break;
                        case org.mwg.Type.INT:
                          toInsert = org.mwg.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
break;
                        case org.mwg.Type.DOUBLE_ARRAY:                        if (currentDoubleArr == null) {
                          currentDoubleArr = new Float64Array(org.mwg.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                        } else {
                          currentDoubleArr[currentSubIndex] = org.mwg.utility.Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
                        }

                          toInsert = currentDoubleArr;
break;
                        case org.mwg.Type.LONG_ARRAY:                        if (currentLongArr == null) {
                          currentLongArr = new Float64Array(org.mwg.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                        } else {
                          currentLongArr[currentSubIndex] = org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        }

                          toInsert = currentLongArr;
break;
                        case org.mwg.Type.INT_ARRAY:                        if (currentIntArr == null) {
                          currentIntArr = new Int32Array(org.mwg.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                        } else {
                          currentIntArr[currentSubIndex] = org.mwg.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                        }

                          toInsert = currentIntArr;
break;
                        case org.mwg.Type.RELATION:                        if (currentRelation == null) {
                          currentRelation = new org.mwg.core.chunk.heap.HeapRelationship(this, null);
                          currentRelation.allocate(org.mwg.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                        } else {
                          currentRelation.add(org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                        }

                          toInsert = currentRelation;
break;
                        case org.mwg.Type.STRING_TO_LONG_MAP:                        if (currentMapStringKey != null) {
                          currentStringLongMap.put(currentMapStringKey, org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                        }

                          toInsert = currentStringLongMap;
break;
                        case org.mwg.Type.LONG_TO_LONG_MAP:                        if (currentMapLongKey != org.mwg.core.CoreConstants.NULL_LONG) {
                          currentLongLongMap.put(currentMapLongKey, org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                        }

                          toInsert = currentLongLongMap;
break;
                        case org.mwg.Type.LONG_TO_LONG_ARRAY_MAP:                        if (currentMapLongKey != org.mwg.core.CoreConstants.NULL_LONG) {
                          currentLongLongArrayMap.put(currentMapLongKey, org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                        }

                          toInsert = currentLongLongArrayMap;
break;
                      }
                      if (toInsert != null) {
                        this.internal_set(currentChunkElemKey, currentChunkElemType, toInsert, true, initial);
                      }
                    }
                    previousStart = cursor + 1;
                    currentChunkElemKey = org.mwg.core.CoreConstants.NULL_LONG;
                    currentChunkElemType = -1;
                    currentSubSize = -1;
                    currentSubIndex = 0;
                    currentMapLongKey = org.mwg.core.CoreConstants.NULL_LONG;
                    currentMapStringKey = null;
                  }
                } else if (current == org.mwg.core.CoreConstants.CHUNK_SUB_SEP) {
                  if (currentChunkElemKey == org.mwg.core.CoreConstants.NULL_LONG) {
                    currentChunkElemKey = org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    previousStart = cursor + 1;
                  } else if (currentChunkElemType == -1) {
                    currentChunkElemType = <number>org.mwg.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                    previousStart = cursor + 1;
                  }

                } else if (current == org.mwg.core.CoreConstants.CHUNK_SUB_SUB_SEP) {
                  if (currentSubSize == -1) {
                    currentSubSize = org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    switch (currentChunkElemType) {
                      case org.mwg.Type.DOUBLE_ARRAY:
                        currentDoubleArr = new Float64Array(<number>currentSubSize);
break;
                      case org.mwg.Type.LONG_ARRAY:
                        currentLongArr = new Float64Array(<number>currentSubSize);
break;
                      case org.mwg.Type.INT_ARRAY:
                        currentIntArr = new Int32Array(<number>currentSubSize);
break;
                      case org.mwg.Type.RELATION:
                        currentRelation = new org.mwg.core.chunk.heap.HeapRelationship(this, null);

                        currentRelation.allocate(<number>currentSubSize);
break;
                      case org.mwg.Type.STRING_TO_LONG_MAP:
                        currentStringLongMap = new org.mwg.core.chunk.heap.HeapStringLongMap(this);

                        currentStringLongMap.reallocate(<number>currentSubSize);
break;
                      case org.mwg.Type.LONG_TO_LONG_MAP:
                        currentLongLongMap = new org.mwg.core.chunk.heap.HeapLongLongMap(this);

                        currentLongLongMap.reallocate(<number>currentSubSize);
break;
                      case org.mwg.Type.LONG_TO_LONG_ARRAY_MAP:
                        currentLongLongArrayMap = new org.mwg.core.chunk.heap.HeapLongLongArrayMap(this);

                        currentLongLongArrayMap.reallocate(<number>currentSubSize);
break;
                    }
                  } else {
                    switch (currentChunkElemType) {
                      case org.mwg.Type.DOUBLE_ARRAY:
                        currentDoubleArr[currentSubIndex] = org.mwg.utility.Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);

                        currentSubIndex++;
break;
                      case org.mwg.Type.RELATION:
                        currentRelation.add(org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
break;
                      case org.mwg.Type.LONG_ARRAY:
                        currentLongArr[currentSubIndex] = org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);

                        currentSubIndex++;
break;
                      case org.mwg.Type.INT_ARRAY:
                        currentIntArr[currentSubIndex] = org.mwg.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor);

                        currentSubIndex++;
break;
                      case org.mwg.Type.STRING_TO_LONG_MAP:                      if (currentMapStringKey != null) {
                        currentStringLongMap.put(currentMapStringKey, org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                        currentMapStringKey = null;
                      }
break;
                      case org.mwg.Type.LONG_TO_LONG_MAP:                      if (currentMapLongKey != org.mwg.core.CoreConstants.NULL_LONG) {
                        currentLongLongMap.put(currentMapLongKey, org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                        currentMapLongKey = org.mwg.core.CoreConstants.NULL_LONG;
                      }
break;
                      case org.mwg.Type.LONG_TO_LONG_ARRAY_MAP:                      if (currentMapLongKey != org.mwg.core.CoreConstants.NULL_LONG) {
                        currentLongLongArrayMap.put(currentMapLongKey, org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                        currentMapLongKey = org.mwg.core.CoreConstants.NULL_LONG;
                      }
break;
                    }
                  }
                  previousStart = cursor + 1;
                } else if (current == org.mwg.core.CoreConstants.CHUNK_SUB_SUB_SUB_SEP) {
                  switch (currentChunkElemType) {
                    case org.mwg.Type.STRING_TO_LONG_MAP:                    if (currentMapStringKey == null) {
                      currentMapStringKey = org.mwg.utility.Base64.decodeToStringWithBounds(buffer, previousStart, cursor);
                    } else {
                      currentStringLongMap.put(currentMapStringKey, org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                      currentMapStringKey = null;
                    }
break;
                    case org.mwg.Type.LONG_TO_LONG_MAP:                    if (currentMapLongKey == org.mwg.core.CoreConstants.NULL_LONG) {
                      currentMapLongKey = org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    } else {
                      currentLongLongMap.put(currentMapLongKey, org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                      currentMapLongKey = org.mwg.core.CoreConstants.NULL_LONG;
                    }
break;
                    case org.mwg.Type.LONG_TO_LONG_ARRAY_MAP:                    if (currentMapLongKey == org.mwg.core.CoreConstants.NULL_LONG) {
                      currentMapLongKey = org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    } else {
                      currentLongLongArrayMap.put(currentMapLongKey, org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                      currentMapLongKey = org.mwg.core.CoreConstants.NULL_LONG;
                    }
break;
                  }
                  previousStart = cursor + 1;
                }



                cursor++;
              }
              if (currentChunkElemType != -1) {
                let toInsert: any = null;
                switch (currentChunkElemType) {
                  case org.mwg.Type.BOOL:                  if (buffer.read(previousStart) == org.mwg.core.CoreConstants.BOOL_FALSE) {
                    toInsert = false;
                  } else if (buffer.read(previousStart) == org.mwg.core.CoreConstants.BOOL_TRUE) {
                    toInsert = true;
                  }

break;
                  case org.mwg.Type.STRING:
                    toInsert = org.mwg.utility.Base64.decodeToStringWithBounds(buffer, previousStart, cursor);
break;
                  case org.mwg.Type.DOUBLE:
                    toInsert = org.mwg.utility.Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
break;
                  case org.mwg.Type.LONG:
                    toInsert = org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
break;
                  case org.mwg.Type.INT:
                    toInsert = org.mwg.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
break;
                  case org.mwg.Type.DOUBLE_ARRAY:                  if (currentDoubleArr == null) {
                    currentDoubleArr = new Float64Array(org.mwg.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                  } else {
                    currentDoubleArr[currentSubIndex] = org.mwg.utility.Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
                  }

                    toInsert = currentDoubleArr;
break;
                  case org.mwg.Type.LONG_ARRAY:                  if (currentLongArr == null) {
                    currentLongArr = new Float64Array(org.mwg.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                  } else {
                    currentLongArr[currentSubIndex] = org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                  }

                    toInsert = currentLongArr;
break;
                  case org.mwg.Type.INT_ARRAY:                  if (currentIntArr == null) {
                    currentIntArr = new Int32Array(org.mwg.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                  } else {
                    currentIntArr[currentSubIndex] = org.mwg.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                  }

                    toInsert = currentIntArr;
break;
                  case org.mwg.Type.RELATION:                  if (currentRelation != null) {
                    currentRelation.add(org.mwg.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                  }

                    toInsert = currentRelation;
break;
                  case org.mwg.Type.STRING_TO_LONG_MAP:                  if (currentMapStringKey != null) {
                    currentStringLongMap.put(currentMapStringKey, org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                  }

                    toInsert = currentStringLongMap;
break;
                  case org.mwg.Type.LONG_TO_LONG_MAP:                  if (currentMapLongKey != org.mwg.core.CoreConstants.NULL_LONG) {
                    currentLongLongMap.put(currentMapLongKey, org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                  }

                    toInsert = currentLongLongMap;
break;
                  case org.mwg.Type.LONG_TO_LONG_ARRAY_MAP:                  if (currentMapLongKey != org.mwg.core.CoreConstants.NULL_LONG) {
                    currentLongLongArrayMap.put(currentMapLongKey, org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                  }

                    toInsert = currentLongLongArrayMap;
break;
                }
                if (toInsert != null) {
                  this.internal_set(currentChunkElemKey, currentChunkElemType, toInsert, true, initial);
                }
              }
            }
          }
          export class HeapStringLongMap implements org.mwg.struct.StringLongMap {
            private parent: org.mwg.core.chunk.heap.HeapStateChunk;
            private mapSize: number = 0;
            private capacity: number = 0;
            private keys: string[] = null;
            private keysH: Float64Array = null;
            private values: Float64Array = null;
            private nexts: Int32Array = null;
            private hashs: Int32Array = null;
            constructor(p_listener: org.mwg.core.chunk.heap.HeapStateChunk) {
              this.parent = p_listener;
            }
            private key(i: number): string {
              return this.keys[i];
            }
            private setKey(i: number, newValue: string): void {
              this.keys[i] = newValue;
            }
            private keyH(i: number): number {
              return this.keysH[i];
            }
            private setKeyH(i: number, newValue: number): void {
              this.keysH[i] = newValue;
            }
            private value(i: number): number {
              return this.values[i];
            }
            private setValue(i: number, newValue: number): void {
              this.values[i] = newValue;
            }
            private next(i: number): number {
              return this.nexts[i];
            }
            private setNext(i: number, newValue: number): void {
              this.nexts[i] = newValue;
            }
            private hash(i: number): number {
              return this.hashs[i];
            }
            private setHash(i: number, newValue: number): void {
              this.hashs[i] = newValue;
            }
            public reallocate(newCapacity: number): void {
              if (newCapacity > this.capacity) {
                let new_keys: string[] = new Array<string>(newCapacity);
                if (this.keys != null) {
                  java.lang.System.arraycopy(this.keys, 0, new_keys, 0, this.capacity);
                }
                this.keys = new_keys;
                let new_keysH: Float64Array = new Float64Array(newCapacity);
                if (this.keysH != null) {
                  java.lang.System.arraycopy(this.keysH, 0, new_keysH, 0, this.capacity);
                }
                this.keysH = new_keysH;
                let new_values: Float64Array = new Float64Array(newCapacity);
                if (this.values != null) {
                  java.lang.System.arraycopy(this.values, 0, new_values, 0, this.capacity);
                }
                this.values = new_values;
                let new_nexts: Int32Array = new Int32Array(newCapacity);
                let new_hashes: Int32Array = new Int32Array(newCapacity * 2);
                java.util.Arrays.fill(new_nexts, 0, newCapacity, -1);
                java.util.Arrays.fill(new_hashes, 0, newCapacity * 2, -1);
                this.hashs = new_hashes;
                this.nexts = new_nexts;
                for (let i: number = 0; i < this.mapSize; i++) {
                  let new_key_hash: number = <number>org.mwg.utility.HashHelper.longHash(this.keyH(i), newCapacity * 2);
                  this.setNext(i, this.hash(new_key_hash));
                  this.setHash(new_key_hash, i);
                }
                this.capacity = newCapacity;
              }
            }
            public cloneFor(newParent: org.mwg.core.chunk.heap.HeapStateChunk): org.mwg.core.chunk.heap.HeapStringLongMap {
              let cloned: org.mwg.core.chunk.heap.HeapStringLongMap = new org.mwg.core.chunk.heap.HeapStringLongMap(newParent);
              cloned.mapSize = this.mapSize;
              cloned.capacity = this.capacity;
              if (this.keys != null) {
                let cloned_keys: string[] = new Array<string>(this.capacity);
                java.lang.System.arraycopy(this.keys, 0, cloned_keys, 0, this.capacity);
                cloned.keys = cloned_keys;
              }
              if (this.keysH != null) {
                let cloned_keysH: Float64Array = new Float64Array(this.capacity);
                java.lang.System.arraycopy(this.keysH, 0, cloned_keysH, 0, this.capacity);
                cloned.keysH = cloned_keysH;
              }
              if (this.values != null) {
                let cloned_values: Float64Array = new Float64Array(this.capacity);
                java.lang.System.arraycopy(this.values, 0, cloned_values, 0, this.capacity);
                cloned.values = cloned_values;
              }
              if (this.nexts != null) {
                let cloned_nexts: Int32Array = new Int32Array(this.capacity);
                java.lang.System.arraycopy(this.nexts, 0, cloned_nexts, 0, this.capacity);
                cloned.nexts = cloned_nexts;
              }
              if (this.hashs != null) {
                let cloned_hashs: Int32Array = new Int32Array(this.capacity * 2);
                java.lang.System.arraycopy(this.hashs, 0, cloned_hashs, 0, this.capacity * 2);
                cloned.hashs = cloned_hashs;
              }
              return cloned;
            }
            public getValue(requestString: string): number {
              let result: number = org.mwg.Constants.NULL_LONG;
{
                if (this.keys != null) {
                  let keyHash: number = org.mwg.utility.HashHelper.hash(requestString);
                  let hashIndex: number = <number>org.mwg.utility.HashHelper.longHash(keyHash, this.capacity * 2);
                  let m: number = this.hash(hashIndex);
                  while (m >= 0) {
                    if (keyHash == this.keyH(m)) {
                      result = this.value(m);
break;
                    }
                    m = this.next(m);
                  }
                }
              }              return result;
            }
            public getByHash(keyHash: number): string {
              let result: string = null;
{
                if (this.keys != null) {
                  let hashIndex: number = <number>org.mwg.utility.HashHelper.longHash(keyHash, this.capacity * 2);
                  let m: number = this.hash(hashIndex);
                  while (m >= 0) {
                    if (keyHash == this.keyH(m)) {
                      result = this.key(m);
break;
                    }
                    m = this.next(m);
                  }
                }
              }              return result;
            }
            public containsHash(keyHash: number): boolean {
              let result: boolean = false;
{
                if (this.keys != null) {
                  let hashIndex: number = <number>org.mwg.utility.HashHelper.longHash(keyHash, this.capacity * 2);
                  let m: number = this.hash(hashIndex);
                  while (m >= 0) {
                    if (keyHash == this.keyH(m)) {
                      result = true;
break;
                    }
                    m = this.next(m);
                  }
                }
              }              return result;
            }
            public each(callback: org.mwg.struct.StringLongMapCallBack): void {
{
                this.unsafe_each(callback);
              }            }
            public unsafe_each(callback: org.mwg.struct.StringLongMapCallBack): void {
              for (let i: number = 0; i < this.mapSize; i++) {
                callback(this.key(i), this.value(i));
              }
            }
            public size(): number {
              let result: number;
{
                result = this.mapSize;
              }              return result;
            }
            public remove(requestKey: string): void {
{
                if (this.keys != null && this.mapSize != 0) {
                  let keyHash: number = org.mwg.utility.HashHelper.hash(requestKey);
                  let hashCapacity: number = this.capacity * 2;
                  let hashIndex: number = <number>org.mwg.utility.HashHelper.longHash(keyHash, hashCapacity);
                  let m: number = this.hash(hashIndex);
                  let found: number = -1;
                  while (m >= 0) {
                    if (requestKey == this.key(m)) {
                      found = m;
break;
                    }
                    m = this.next(m);
                  }
                  if (found != -1) {
                    let toRemoveHash: number = <number>org.mwg.utility.HashHelper.longHash(keyHash, hashCapacity);
                    m = this.hash(toRemoveHash);
                    if (m == found) {
                      this.setHash(toRemoveHash, this.next(m));
                    } else {
                      while (m != -1) {
                        let next_of_m: number = this.next(m);
                        if (next_of_m == found) {
                          this.setNext(m, this.next(next_of_m));
break;
                        }
                        m = next_of_m;
                      }
                    }
                    let lastIndex: number = this.mapSize - 1;
                    if (lastIndex == found) {
                      this.mapSize--;
                    } else {
                      let lastKey: string = this.key(lastIndex);
                      let lastKeyH: number = this.keyH(lastIndex);
                      this.setKey(found, lastKey);
                      this.setKeyH(found, lastKeyH);
                      this.setValue(found, this.value(lastIndex));
                      this.setNext(found, this.next(lastIndex));
                      let victimHash: number = <number>org.mwg.utility.HashHelper.longHash(lastKeyH, hashCapacity);
                      m = this.hash(victimHash);
                      if (m == lastIndex) {
                        this.setHash(victimHash, found);
                      } else {
                        while (m != -1) {
                          let next_of_m: number = this.next(m);
                          if (next_of_m == lastIndex) {
                            this.setNext(m, found);
break;
                          }
                          m = next_of_m;
                        }
                      }
                      this.mapSize--;
                    }
                    this.parent.declareDirty();
                  }
                }
              }            }
            public put(insertKey: string, insertValue: number): void {
{
                let keyHash: number = org.mwg.utility.HashHelper.hash(insertKey);
                if (this.keys == null) {
                  this.reallocate(org.mwg.Constants.MAP_INITIAL_CAPACITY);
                  this.setKey(0, insertKey);
                  this.setKeyH(0, keyHash);
                  this.setValue(0, insertValue);
                  this.setHash(<number>org.mwg.utility.HashHelper.longHash(keyHash, this.capacity * 2), 0);
                  this.setNext(0, -1);
                  this.mapSize++;
                } else {
                  let hashCapacity: number = this.capacity * 2;
                  let insertKeyHash: number = <number>org.mwg.utility.HashHelper.longHash(keyHash, hashCapacity);
                  let currentHash: number = this.hash(insertKeyHash);
                  let m: number = currentHash;
                  let found: number = -1;
                  while (m >= 0) {
                    if (insertKey == this.key(m)) {
                      found = m;
break;
                    }
                    m = this.next(m);
                  }
                  if (found == -1) {
                    let lastIndex: number = this.mapSize;
                    if (lastIndex == this.capacity) {
                      this.reallocate(this.capacity * 2);
                    }
                    this.setKey(lastIndex, insertKey);
                    this.setKeyH(lastIndex, keyHash);
                    this.setValue(lastIndex, insertValue);
                    this.setHash(<number>org.mwg.utility.HashHelper.longHash(keyHash, this.capacity * 2), lastIndex);
                    this.setNext(lastIndex, currentHash);
                    this.mapSize++;
                    this.parent.declareDirty();
                  } else {
                    if (this.value(found) != insertValue) {
                      this.setValue(found, insertValue);
                      this.parent.declareDirty();
                    }
                  }
                }
              }            }
          }
          export class HeapTimeTreeChunk implements org.mwg.chunk.TimeTreeChunk {
            private static META_SIZE: number = 3;
            private _index: number;
            private _space: org.mwg.core.chunk.heap.HeapChunkSpace;
            private _root: number = -1;
            private _back_meta: Int32Array;
            private _k: Float64Array;
            private _colors: boolean[];
            private _magic: number;
            private _size: number = 0;
            private _dirty: boolean;
            constructor(p_space: org.mwg.core.chunk.heap.HeapChunkSpace, p_index: number) {
              this._space = p_space;
              this._index = p_index;
              this._magic = 0;
              this._dirty = false;
            }
            public world(): number {
              return this._space.worldByIndex(this._index);
            }
            public time(): number {
              return this._space.timeByIndex(this._index);
            }
            public id(): number {
              return this._space.idByIndex(this._index);
            }
            public size(): number {
              return this._size;
            }
            public range(startKey: number, endKey: number, maxElements: number, walker: org.mwg.chunk.TreeWalker): void {
              let nbElements: number = 0;
              let indexEnd: number = this.internal_previousOrEqual_index(endKey);
              while (indexEnd != -1 && this.key(indexEnd) >= startKey && nbElements < maxElements) {
                walker(this.key(indexEnd));
                nbElements++;
                indexEnd = this.previous(indexEnd);
              }
            }
            public save(buffer: org.mwg.struct.Buffer): void {
              org.mwg.utility.Base64.encodeLongToBuffer(this._size, buffer);
              buffer.write(org.mwg.core.CoreConstants.CHUNK_SEP);
              let isFirst: boolean = true;
              for (let i: number = 0; i < this._size; i++) {
                if (!isFirst) {
                  buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SEP);
                } else {
                  isFirst = false;
                }
                org.mwg.utility.Base64.encodeLongToBuffer(this._k[i], buffer);
              }
              this._dirty = false;
            }
            public load(buffer: org.mwg.struct.Buffer): void {
              if (buffer == null || buffer.length() == 0) {
                return;
              }
              let initial: boolean = this._k == null;
              let isDirty: boolean = false;
              let cursor: number = 0;
              let previous: number = 0;
              let payloadSize: number = buffer.length();
              while (cursor < payloadSize) {
                let current: number = buffer.read(cursor);
                if (current == org.mwg.core.CoreConstants.CHUNK_SUB_SEP) {
                  let insertResult: boolean = this.internal_insert(org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                  isDirty = isDirty || insertResult;
                  previous = cursor + 1;
                } else if (current == org.mwg.core.CoreConstants.CHUNK_SEP) {
                  this.reallocate(<number>org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                  previous = cursor + 1;
                }

                cursor++;
              }
              let insertResult: boolean = this.internal_insert(org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
              isDirty = isDirty || insertResult;
              if (isDirty && !initial && !this._dirty) {
                this._dirty = true;
                if (this._space != null) {
                  this._space.notifyUpdate(this._index);
                }
              }
            }
            public index(): number {
              return this._index;
            }
            public previousOrEqual(key: number): number {
              let resultKey: number;
              let result: number = this.internal_previousOrEqual_index(key);
              if (result != -1) {
                resultKey = this.key(result);
              } else {
                resultKey = org.mwg.core.CoreConstants.NULL_LONG;
              }
              return resultKey;
            }
            public magic(): number {
              return this._magic;
            }
            public insert(p_key: number): void {
              if (this.internal_insert(p_key)) {
                this.internal_set_dirty();
              }
            }
            public unsafe_insert(p_key: number): void {
              this.internal_insert(p_key);
            }
            public chunkType(): number {
              return org.mwg.chunk.ChunkType.TIME_TREE_CHUNK;
            }
            public clearAt(max: number): void {
              let previousValue: Float64Array = this._k;
              this._k = new Float64Array(this._k.length);
              this._back_meta = new Int32Array(this._k.length * HeapTimeTreeChunk.META_SIZE);
              this._colors = [];
              this._root = -1;
              let _previousSize: number = this._size;
              this._size = 0;
              for (let i: number = 0; i < _previousSize; i++) {
                if (previousValue[i] != org.mwg.core.CoreConstants.NULL_LONG && previousValue[i] < max) {
                  this.internal_insert(previousValue[i]);
                }
              }
              this.internal_set_dirty();
            }
            private reallocate(newCapacity: number): void {
              let new_back_kv: Float64Array = new Float64Array(newCapacity);
              if (this._k != null) {
                java.lang.System.arraycopy(this._k, 0, new_back_kv, 0, this._size);
              }
              let new_back_colors: boolean[] = [];
              if (this._colors != null) {
                java.lang.System.arraycopy(this._colors, 0, new_back_colors, 0, this._size);
                for (let i: number = this._size; i < newCapacity; i++) {
                  new_back_colors[i] = false;
                }
              }
              let new_back_meta: Int32Array = new Int32Array(newCapacity * HeapTimeTreeChunk.META_SIZE);
              if (this._back_meta != null) {
                java.lang.System.arraycopy(this._back_meta, 0, new_back_meta, 0, this._size * HeapTimeTreeChunk.META_SIZE);
                for (let i: number = this._size * HeapTimeTreeChunk.META_SIZE; i < newCapacity * HeapTimeTreeChunk.META_SIZE; i++) {
                  new_back_meta[i] = -1;
                }
              }
              this._back_meta = new_back_meta;
              this._k = new_back_kv;
              this._colors = new_back_colors;
            }
            private key(p_currentIndex: number): number {
              if (p_currentIndex == -1) {
                return -1;
              }
              return this._k[p_currentIndex];
            }
            private setKey(p_currentIndex: number, p_paramIndex: number): void {
              this._k[p_currentIndex] = p_paramIndex;
            }
            private left(p_currentIndex: number): number {
              if (p_currentIndex == -1) {
                return -1;
              }
              return this._back_meta[p_currentIndex * HeapTimeTreeChunk.META_SIZE];
            }
            private setLeft(p_currentIndex: number, p_paramIndex: number): void {
              this._back_meta[p_currentIndex * HeapTimeTreeChunk.META_SIZE] = p_paramIndex;
            }
            private right(p_currentIndex: number): number {
              if (p_currentIndex == -1) {
                return -1;
              }
              return this._back_meta[(p_currentIndex * HeapTimeTreeChunk.META_SIZE) + 1];
            }
            private setRight(p_currentIndex: number, p_paramIndex: number): void {
              this._back_meta[(p_currentIndex * HeapTimeTreeChunk.META_SIZE) + 1] = p_paramIndex;
            }
            private parent(p_currentIndex: number): number {
              if (p_currentIndex == -1) {
                return -1;
              }
              return this._back_meta[(p_currentIndex * HeapTimeTreeChunk.META_SIZE) + 2];
            }
            private setParent(p_currentIndex: number, p_paramIndex: number): void {
              this._back_meta[(p_currentIndex * HeapTimeTreeChunk.META_SIZE) + 2] = p_paramIndex;
            }
            private color(p_currentIndex: number): boolean {
              if (p_currentIndex == -1) {
                return true;
              }
              return this._colors[p_currentIndex];
            }
            private setColor(p_currentIndex: number, p_paramIndex: boolean): void {
              this._colors[p_currentIndex] = p_paramIndex;
            }
            private grandParent(p_currentIndex: number): number {
              if (p_currentIndex == -1) {
                return -1;
              }
              if (this.parent(p_currentIndex) != -1) {
                return this.parent(this.parent(p_currentIndex));
              } else {
                return -1;
              }
            }
            private sibling(p_currentIndex: number): number {
              if (this.parent(p_currentIndex) == -1) {
                return -1;
              } else {
                if (p_currentIndex == this.left(this.parent(p_currentIndex))) {
                  return this.right(this.parent(p_currentIndex));
                } else {
                  return this.left(this.parent(p_currentIndex));
                }
              }
            }
            private uncle(p_currentIndex: number): number {
              if (this.parent(p_currentIndex) != -1) {
                return this.sibling(this.parent(p_currentIndex));
              } else {
                return -1;
              }
            }
            private previous(p_index: number): number {
              let p: number = p_index;
              if (this.left(p) != -1) {
                p = this.left(p);
                while (this.right(p) != -1) {
                  p = this.right(p);
                }
                return p;
              } else {
                if (this.parent(p) != -1) {
                  if (p == this.right(this.parent(p))) {
                    return this.parent(p);
                  } else {
                    while (this.parent(p) != -1 && p == this.left(this.parent(p))) {
                      p = this.parent(p);
                    }
                    return this.parent(p);
                  }
                } else {
                  return -1;
                }
              }
            }
            private internal_previousOrEqual_index(p_key: number): number {
              let p: number = this._root;
              if (p == -1) {
                return p;
              }
              while (p != -1) {
                if (p_key == this.key(p)) {
                  return p;
                }
                if (p_key > this.key(p)) {
                  if (this.right(p) != -1) {
                    p = this.right(p);
                  } else {
                    return p;
                  }
                } else {
                  if (this.left(p) != -1) {
                    p = this.left(p);
                  } else {
                    let parent: number = this.parent(p);
                    let ch: number = p;
                    while (parent != -1 && ch == this.left(parent)) {
                      ch = parent;
                      parent = this.parent(parent);
                    }
                    return parent;
                  }
                }
              }
              return -1;
            }
            private rotateLeft(n: number): void {
              let r: number = this.right(n);
              this.replaceNode(n, r);
              this.setRight(n, this.left(r));
              if (this.left(r) != -1) {
                this.setParent(this.left(r), n);
              }
              this.setLeft(r, n);
              this.setParent(n, r);
            }
            private rotateRight(n: number): void {
              let l: number = this.left(n);
              this.replaceNode(n, l);
              this.setLeft(n, this.right(l));
              if (this.right(l) != -1) {
                this.setParent(this.right(l), n);
              }
              this.setRight(l, n);
              this.setParent(n, l);
            }
            private replaceNode(oldn: number, newn: number): void {
              if (this.parent(oldn) == -1) {
                this._root = newn;
              } else {
                if (oldn == this.left(this.parent(oldn))) {
                  this.setLeft(this.parent(oldn), newn);
                } else {
                  this.setRight(this.parent(oldn), newn);
                }
              }
              if (newn != -1) {
                this.setParent(newn, this.parent(oldn));
              }
            }
            private insertCase1(n: number): void {
              if (this.parent(n) == -1) {
                this.setColor(n, true);
              } else {
                this.insertCase2(n);
              }
            }
            private insertCase2(n: number): void {
              if (!this.color(this.parent(n))) {
                this.insertCase3(n);
              }
            }
            private insertCase3(n: number): void {
              if (!this.color(this.uncle(n))) {
                this.setColor(this.parent(n), true);
                this.setColor(this.uncle(n), true);
                this.setColor(this.grandParent(n), false);
                this.insertCase1(this.grandParent(n));
              } else {
                this.insertCase4(n);
              }
            }
            private insertCase4(n_n: number): void {
              let n: number = n_n;
              if (n == this.right(this.parent(n)) && this.parent(n) == this.left(this.grandParent(n))) {
                this.rotateLeft(this.parent(n));
                n = this.left(n);
              } else {
                if (n == this.left(this.parent(n)) && this.parent(n) == this.right(this.grandParent(n))) {
                  this.rotateRight(this.parent(n));
                  n = this.right(n);
                }
              }
              this.insertCase5(n);
            }
            private insertCase5(n: number): void {
              this.setColor(this.parent(n), true);
              this.setColor(this.grandParent(n), false);
              if (n == this.left(this.parent(n)) && this.parent(n) == this.left(this.grandParent(n))) {
                this.rotateRight(this.grandParent(n));
              } else {
                this.rotateLeft(this.grandParent(n));
              }
            }
            private internal_insert(p_key: number): boolean {
              if (this._k == null || this._k.length == this._size) {
                let length: number = this._size;
                if (length == 0) {
                  length = org.mwg.Constants.MAP_INITIAL_CAPACITY;
                } else {
                  length = length * 2;
                }
                this.reallocate(length);
              }
              let newIndex: number = this._size;
              if (newIndex == 0) {
                this.setKey(newIndex, p_key);
                this.setColor(newIndex, false);
                this.setLeft(newIndex, -1);
                this.setRight(newIndex, -1);
                this.setParent(newIndex, -1);
                this._root = newIndex;
                this._size = 1;
              } else {
                let n: number = this._root;
                while (true) {
                  if (p_key == this.key(n)) {
                    return false;
                  } else if (p_key < this.key(n)) {
                    if (this.left(n) == -1) {
                      this.setKey(newIndex, p_key);
                      this.setColor(newIndex, false);
                      this.setLeft(newIndex, -1);
                      this.setRight(newIndex, -1);
                      this.setParent(newIndex, -1);
                      this.setLeft(n, newIndex);
                      this._size++;
break;
                    } else {
                      n = this.left(n);
                    }
                  } else {
                    if (this.right(n) == -1) {
                      this.setKey(newIndex, p_key);
                      this.setColor(newIndex, false);
                      this.setLeft(newIndex, -1);
                      this.setRight(newIndex, -1);
                      this.setParent(newIndex, -1);
                      this.setRight(n, newIndex);
                      this._size++;
break;
                    } else {
                      n = this.right(n);
                    }
                  }

                }
                this.setParent(newIndex, n);
              }
              this.insertCase1(newIndex);
              return true;
            }
            private internal_set_dirty(): void {
              this._magic = this._magic + 1;
              if (this._space != null && !this._dirty) {
                this._dirty = true;
                this._space.notifyUpdate(this._index);
              }
            }
          }
          export class HeapWorldOrderChunk implements org.mwg.chunk.WorldOrderChunk {
            private _space: org.mwg.core.chunk.heap.HeapChunkSpace;
            private _index: number;
            private _lock: number;
            private _magic: number;
            private _extra: number;
            private _size: number;
            private _capacity: number;
            private _kv: Float64Array;
            private _next: Int32Array;
            private _hash: Int32Array;
            private _dirty: boolean;
            constructor(p_space: org.mwg.core.chunk.heap.HeapChunkSpace, p_index: number) {
              this._index = p_index;
              this._space = p_space;
              this._lock = 0;
              this._magic = 0;
              this._extra = org.mwg.core.CoreConstants.NULL_LONG;
              this._size = 0;
              this._capacity = 0;
              this._kv = null;
              this._next = null;
              this._hash = null;
              this._dirty = false;
            }
            public world(): number {
              return this._space.worldByIndex(this._index);
            }
            public time(): number {
              return this._space.timeByIndex(this._index);
            }
            public id(): number {
              return this._space.idByIndex(this._index);
            }
            public extra(): number {
              return this._extra;
            }
            public setExtra(extraValue: number): void {
              this._extra = extraValue;
            }
            public lock(): void {
            }
            public unlock(): void {
            }
            public magic(): number {
              return this._magic;
            }
            public each(callback: org.mwg.struct.LongLongMapCallBack): void {
              for (let i: number = 0; i < this._size; i++) {
                callback(this._kv[i * 2], this._kv[i * 2 + 1]);
              }
            }
            public get(key: number): number {
              if (this._size > 0) {
                let index: number = <number>org.mwg.utility.HashHelper.longHash(key, this._capacity * 2);
                let m: number = this._hash[index];
                while (m >= 0) {
                  if (key == this._kv[m * 2]) {
                    return this._kv[(m * 2) + 1];
                  } else {
                    m = this._next[m];
                  }
                }
              }
              return org.mwg.core.CoreConstants.NULL_LONG;
            }
            public put(key: number, value: number): void {
              this.internal_put(key, value, true);
            }
            private internal_put(key: number, value: number, notifyUpdate: boolean): void {
              if (this._capacity > 0) {
                let hashIndex: number = <number>org.mwg.utility.HashHelper.longHash(key, this._capacity * 2);
                let m: number = this._hash[hashIndex];
                let found: number = -1;
                while (m >= 0) {
                  if (key == this._kv[m * 2]) {
                    found = m;
break;
                  }
                  m = this._next[m];
                }
                if (found == -1) {
                  if (this._capacity == this._size) {
                    this.resize(this._capacity * 2);
                    hashIndex = <number>org.mwg.utility.HashHelper.longHash(key, this._capacity * 2);
                  }
                  this._kv[this._size * 2] = key;
                  this._kv[this._size * 2 + 1] = value;
                  this._next[this._size] = this._hash[hashIndex];
                  this._hash[hashIndex] = this._size;
                  this._size++;
                  this._magic = this._magic + 1;
                  if (notifyUpdate && !this._dirty) {
                    this._dirty = true;
                    if (this._space != null) {
                      this._space.notifyUpdate(this._index);
                    }
                  }
                } else {
                  if (this._kv[found * 2 + 1] != value) {
                    this._kv[found * 2 + 1] = value;
                    this._magic = this._magic + 1;
                    if (notifyUpdate && !this._dirty) {
                      this._dirty = true;
                      if (this._space != null) {
                        this._space.notifyUpdate(this._index);
                      }
                    }
                  }
                }
              } else {
                this._capacity = org.mwg.Constants.MAP_INITIAL_CAPACITY;
                this._next = new Int32Array(this._capacity);
                java.util.Arrays.fill(this._next, 0, this._capacity, -1);
                this._hash = new Int32Array(this._capacity * 2);
                java.util.Arrays.fill(this._hash, 0, this._capacity * 2, -1);
                this._kv = new Float64Array(this._capacity * 2);
                this._size = 1;
                this._kv[0] = key;
                this._kv[1] = value;
                this._hash[<number>org.mwg.utility.HashHelper.longHash(key, this._capacity * 2)] = 0;
                if (notifyUpdate && !this._dirty) {
                  this._dirty = true;
                  if (this._space != null) {
                    this._space.notifyUpdate(this._index);
                  }
                }
              }
            }
            private resize(newCapacity: number): boolean {
              if (newCapacity > this._capacity) {
                if (this._kv == null) {
                  this._kv = new Float64Array(newCapacity * 2);
                  this._hash = new Int32Array(newCapacity * 2);
                  this._next = new Int32Array(newCapacity);
                  this._capacity = newCapacity;
                  java.util.Arrays.fill(this._next, 0, newCapacity, -1);
                  java.util.Arrays.fill(this._hash, 0, newCapacity * 2, -1);
                  return true;
                } else {
                  let temp_kv: Float64Array = new Float64Array(newCapacity * 2);
                  java.lang.System.arraycopy(this._kv, 0, temp_kv, 0, this._size * 2);
                  let temp_next: Int32Array = new Int32Array(newCapacity);
                  let temp_hash: Int32Array = new Int32Array(newCapacity * 2);
                  java.util.Arrays.fill(temp_next, 0, newCapacity, -1);
                  java.util.Arrays.fill(temp_hash, 0, newCapacity * 2, -1);
                  for (let i: number = 0; i < this._size; i++) {
                    let loopIndex: number = <number>org.mwg.utility.HashHelper.longHash(temp_kv[i * 2], newCapacity * 2);
                    temp_next[i] = temp_hash[loopIndex];
                    temp_hash[loopIndex] = i;
                  }
                  this._capacity = newCapacity;
                  this._hash = temp_hash;
                  this._next = temp_next;
                  this._kv = temp_kv;
                  return true;
                }
              } else {
                return false;
              }
            }
            public load(buffer: org.mwg.struct.Buffer): void {
              if (buffer == null || buffer.length() == 0) {
                return;
              }
              let isInitial: boolean = this._kv == null;
              let cursor: number = 0;
              let bufferSize: number = buffer.length();
              let initDone: boolean = false;
              let previousStart: number = 0;
              let loopKey: number = org.mwg.core.CoreConstants.NULL_LONG;
              while (cursor < bufferSize) {
                if (buffer.read(cursor) == org.mwg.core.CoreConstants.CHUNK_SEP) {
                  if (!initDone) {
                    this.resize(<number>org.mwg.utility.Base64.decodeToLongWithBounds(buffer, 0, cursor));
                    initDone = true;
                  } else {
                    this._extra = org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                  }
                  previousStart = cursor + 1;
                } else if (buffer.read(cursor) == org.mwg.core.CoreConstants.CHUNK_SUB_SEP) {
                  if (loopKey != org.mwg.core.CoreConstants.NULL_LONG) {
                    let loopValue: number = org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    this.internal_put(loopKey, loopValue, !isInitial);
                    loopKey = org.mwg.core.CoreConstants.NULL_LONG;
                  }
                  previousStart = cursor + 1;
                } else if (buffer.read(cursor) == org.mwg.core.CoreConstants.CHUNK_SUB_SUB_SEP) {
                  loopKey = org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                  previousStart = cursor + 1;
                }


                cursor++;
              }
              if (loopKey != org.mwg.core.CoreConstants.NULL_LONG) {
                let loopValue: number = org.mwg.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                this.internal_put(loopKey, loopValue, !isInitial);
              }
            }
            public index(): number {
              return this._index;
            }
            public remove(key: number): void {
              throw new Error("Not implemented yet!!!");
            }
            public size(): number {
              return this._size;
            }
            public chunkType(): number {
              return org.mwg.chunk.ChunkType.WORLD_ORDER_CHUNK;
            }
            public save(buffer: org.mwg.struct.Buffer): void {
              org.mwg.utility.Base64.encodeLongToBuffer(this._size, buffer);
              buffer.write(org.mwg.core.CoreConstants.CHUNK_SEP);
              if (this._extra != org.mwg.core.CoreConstants.NULL_LONG) {
                org.mwg.utility.Base64.encodeLongToBuffer(this._extra, buffer);
                buffer.write(org.mwg.core.CoreConstants.CHUNK_SEP);
              }
              let isFirst: boolean = true;
              for (let i: number = 0; i < this._size; i++) {
                if (!isFirst) {
                  buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SEP);
                }
                isFirst = false;
                org.mwg.utility.Base64.encodeLongToBuffer(this._kv[i * 2], buffer);
                buffer.write(org.mwg.core.CoreConstants.CHUNK_SUB_SUB_SEP);
                org.mwg.utility.Base64.encodeLongToBuffer(this._kv[i * 2 + 1], buffer);
              }
              this._dirty = false;
            }
          }
        }
      }
      export module memory {
        export class HeapBuffer implements org.mwg.struct.Buffer {
          private buffer: Int8Array;
          private writeCursor: number;
          public slice(initPos: number, endPos: number): Int8Array {
            let newSize: number = <number>(endPos - initPos + 1);
            let newResult: Int8Array = new Int8Array(newSize);
            java.lang.System.arraycopy(this.buffer, <number>initPos, newResult, 0, newSize);
            return newResult;
          }
          public write(b: number): void {
            if (this.buffer == null) {
              this.buffer = new Int8Array(org.mwg.core.CoreConstants.MAP_INITIAL_CAPACITY);
              this.buffer[0] = b;
              this.writeCursor = 1;
            } else if (this.writeCursor == this.buffer.length) {
              let temp: Int8Array = new Int8Array(this.buffer.length * 2);
              java.lang.System.arraycopy(this.buffer, 0, temp, 0, this.buffer.length);
              temp[this.writeCursor] = b;
              this.writeCursor++;
              this.buffer = temp;
            } else {
              this.buffer[this.writeCursor] = b;
              this.writeCursor++;
            }

          }
          private getNewSize(old: number, target: number): number {
            while (old < target) {
              old = old * 2;
            }
            return old;
          }
          public writeAll(bytes: Int8Array): void {
            if (this.buffer == null) {
              let initSize: number = <number>this.getNewSize(org.mwg.core.CoreConstants.MAP_INITIAL_CAPACITY, bytes.length);
              this.buffer = new Int8Array(initSize);
              java.lang.System.arraycopy(bytes, 0, this.buffer, 0, bytes.length);
              this.writeCursor = bytes.length;
            } else if (this.writeCursor + bytes.length > this.buffer.length) {
              let newSize: number = <number>this.getNewSize(this.buffer.length, this.buffer.length + bytes.length);
              let tmp: Int8Array = new Int8Array(newSize);
              java.lang.System.arraycopy(this.buffer, 0, tmp, 0, this.buffer.length);
              java.lang.System.arraycopy(bytes, 0, tmp, this.writeCursor, bytes.length);
              this.buffer = tmp;
              this.writeCursor = this.writeCursor + bytes.length;
            } else {
              java.lang.System.arraycopy(bytes, 0, this.buffer, this.writeCursor, bytes.length);
              this.writeCursor = this.writeCursor + bytes.length;
            }

          }
          public read(position: number): number {
            return this.buffer[<number>position];
          }
          public data(): Int8Array {
            let copy: Int8Array = new Int8Array(this.writeCursor);
            if (this.buffer != null) {
              java.lang.System.arraycopy(this.buffer, 0, copy, 0, this.writeCursor);
            }
            return copy;
          }
          public length(): number {
            return this.writeCursor;
          }
          public free(): void {
            this.buffer = null;
          }
          public iterator(): org.mwg.struct.BufferIterator {
            return new org.mwg.utility.DefaultBufferIterator(this);
          }
          public removeLast(): void {
            this.writeCursor--;
          }
          public toString(): string {
            return String.fromCharCode.apply(null,this.data());
          }
        }
        export class HeapMemoryFactory implements org.mwg.plugin.MemoryFactory {
          public newSpace(memorySize: number, graph: org.mwg.Graph): org.mwg.chunk.ChunkSpace {
            return new org.mwg.core.chunk.heap.HeapChunkSpace(<number>memorySize, graph);
          }
          public newBuffer(): org.mwg.struct.Buffer {
            return new org.mwg.core.memory.HeapBuffer();
          }
        }
      }
      export module scheduler {
        export class JobQueue {
          private first: org.mwg.core.scheduler.JobQueue.JobQueueElem = null;
          private last: org.mwg.core.scheduler.JobQueue.JobQueueElem = null;
          public add(item: org.mwg.plugin.Job): void {
            let elem: org.mwg.core.scheduler.JobQueue.JobQueueElem = new org.mwg.core.scheduler.JobQueue.JobQueueElem(item, null);
            if (this.first == null) {
              this.first = elem;
              this.last = elem;
            } else {
              this.last._next = elem;
              this.last = elem;
            }
          }
          public poll(): org.mwg.plugin.Job {
            let value: org.mwg.core.scheduler.JobQueue.JobQueueElem = this.first;
            this.first = this.first._next;
            return value._ptr;
          }
        }
        export module JobQueue {
          export class JobQueueElem {
            public _ptr: org.mwg.plugin.Job;
            public _next: org.mwg.core.scheduler.JobQueue.JobQueueElem;
            constructor(ptr: org.mwg.plugin.Job, next: org.mwg.core.scheduler.JobQueue.JobQueueElem) {
              this._ptr = ptr;
              this._next = next;
            }
          }
        }
        export class NoopScheduler implements org.mwg.plugin.Scheduler {
          public dispatch(affinity: number, job: org.mwg.plugin.Job): void {
            job();
          }
          public start(): void {}
          public stop(): void {}
        }
        export class TrampolineScheduler implements org.mwg.plugin.Scheduler {
          private queue = new org.mwg.core.scheduler.JobQueue();
          private wip: java.util.concurrent.atomic.AtomicInteger = new java.util.concurrent.atomic.AtomicInteger(0);
          public dispatch(affinity: number, job: org.mwg.plugin.Job): void {
            this.queue.add(job);
            if (this.wip.getAndIncrement() == 0) {
              do {
                let polled: org.mwg.plugin.Job = this.queue.poll();
                if (polled != null) {
                  polled();
                }
              } while (this.wip.decrementAndGet() > 0);
            }
          }
          public start(): void {}
          public stop(): void {}
        }
      }
      export module task {
        export class ActionAdd extends org.mwg.plugin.AbstractTaskAction {
          private _relationName: string;
          private _variableNameToAdd: string;
          constructor(relationName: string, variableNameToAdd: string) {
            super();
            this._relationName = relationName;
            this._variableNameToAdd = variableNameToAdd;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            let savedVar: org.mwg.task.TaskResult<any> = context.variable(context.template(this._variableNameToAdd));
            if (previousResult != null && savedVar != null) {
              let relName: string = context.template(this._relationName);
              let previousResultIt: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
              let iter: any = previousResultIt.next();
              while (iter != null) {
                if (iter instanceof org.mwg.plugin.AbstractNode) {
                  let savedVarIt: org.mwg.task.TaskResultIterator<any> = savedVar.iterator();
                  let toAddIter: any = savedVarIt.next();
                  while (toAddIter != null) {
                    if (toAddIter instanceof org.mwg.plugin.AbstractNode) {
                      (<org.mwg.Node>iter).add(relName, <org.mwg.Node>toAddIter);
                    }
                    toAddIter = savedVarIt.next();
                  }
                }
                iter = previousResultIt.next();
              }
            }
            context.continueTask();
          }
          public toString(): string {
            return "add(\'" + this._relationName + "\'" + org.mwg.Constants.QUERY_SEP + "\'" + this._variableNameToAdd + "\')";
          }
        }
        export class ActionAddTo extends org.mwg.plugin.AbstractTaskAction {
          private _relationName: string;
          private _variableNameTarget: string;
          constructor(relationName: string, variableNameTarget: string) {
            super();
            this._relationName = relationName;
            this._variableNameTarget = variableNameTarget;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            let savedVar: org.mwg.task.TaskResult<any> = context.variable(context.template(this._variableNameTarget));
            if (previousResult != null && savedVar != null) {
              let relName: string = context.template(this._relationName);
              let previousResultIt: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
              let iter: any = previousResultIt.next();
              while (iter != null) {
                if (iter instanceof org.mwg.plugin.AbstractNode) {
                  let savedVarIt: org.mwg.task.TaskResultIterator<any> = savedVar.iterator();
                  let toAddIter: any = savedVarIt.next();
                  while (toAddIter != null) {
                    if (toAddIter instanceof org.mwg.plugin.AbstractNode) {
                      (<org.mwg.plugin.AbstractNode>toAddIter).add(relName, <org.mwg.Node>iter);
                    }
                    toAddIter = savedVarIt.next();
                  }
                }
                iter = previousResultIt.next();
              }
            }
            context.continueTask();
          }
          public toString(): string {
            return "addTo(\'" + this._relationName + "\'" + org.mwg.Constants.QUERY_SEP + "\'" + this._variableNameTarget + "\')";
          }
        }
        export class ActionAddToVar extends org.mwg.plugin.AbstractTaskAction {
          private _name: string;
          private _global: boolean;
          constructor(p_name: string, p_global: boolean) {
            super();
            this._name = p_name;
            this._global = p_global;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            if (this._global) {
              context.addToGlobalVariable(context.template(this._name), previousResult);
            } else {
              context.addToVariable(context.template(this._name), previousResult);
            }
            context.continueTask();
          }
          public toString(): string {
            if (this._global) {
              return "addToGlobalVar(\'" + this._name + "\')";
            } else {
              return "addToVar(\'" + this._name + "\')";
            }
          }
        }
        export class ActionAsVar extends org.mwg.plugin.AbstractTaskAction {
          private _name: string;
          private _global: boolean;
          constructor(p_name: string, p_global: boolean) {
            super();
            this._name = p_name;
            this._global = p_global;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            if (this._global) {
              context.setGlobalVariable(context.template(this._name), previousResult);
            } else {
              context.setVariable(context.template(this._name), previousResult);
            }
            context.continueTask();
          }
          public toString(): string {
            if (this._global) {
              return "asGlobalVar(\'" + this._name + "\')";
            } else {
              return "asVar(\'" + this._name + "\')";
            }
          }
        }
        export class ActionClear extends org.mwg.plugin.AbstractTaskAction {
          public eval(context: org.mwg.task.TaskContext): void {
            context.continueWith(context.newResult());
          }
          public toString(): string {
            return "clear()";
          }
        }
        export class ActionDefineVar extends org.mwg.plugin.AbstractTaskAction {
          private _name: string;
          constructor(p_name: string) {
            super();
            this._name = p_name;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            context.defineVariable(context.template(this._name), previousResult);
            context.continueTask();
          }
          public toString(): string {
            return "defineVar(\'" + this._name + "\')";
          }
        }
        export class ActionDoWhile extends org.mwg.plugin.AbstractTaskAction {
          private _cond: org.mwg.task.TaskFunctionConditional;
          private _then: org.mwg.task.Task;
          constructor(p_then: org.mwg.task.Task, p_cond: org.mwg.task.TaskFunctionConditional) {
            super();
            this._cond = p_cond;
            this._then = p_then;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let coreTaskContext: org.mwg.core.task.CoreTaskContext = <org.mwg.core.task.CoreTaskContext>context;
            let selfPointer: org.mwg.core.task.ActionDoWhile = this;
            let recursiveAction: org.mwg.Callback<any>[] = new Array<org.mwg.Callback<any>>(1);
            recursiveAction[0] = (res : org.mwg.task.TaskResult<any>) => {
{
                let previous: org.mwg.task.TaskResult<any> = coreTaskContext._result;
                coreTaskContext._result = res;
                if (this._cond(context)) {
                  if (previous != null) {
                    previous.free();
                  }
                  selfPointer._then.executeFrom(context, (<org.mwg.core.task.CoreTaskContext>context)._result, org.mwg.plugin.SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
                } else {
                  if (previous != null) {
                    previous.free();
                  }
                  context.continueWith(res);
                }
              }            };
            this._then.executeFrom(context, coreTaskContext._result, org.mwg.plugin.SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
          }
          public toString(): string {
            return "doWhile()";
          }
        }
        export class ActionFlatmap extends org.mwg.plugin.AbstractTaskAction {
          private _subTask: org.mwg.task.Task;
          constructor(p_subTask: org.mwg.task.Task) {
            super();
            this._subTask = p_subTask;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let selfPointer: org.mwg.core.task.ActionFlatmap = this;
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            if (previousResult == null) {
              context.continueTask();
            } else {
              let it: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
              let finalResult: org.mwg.task.TaskResult<any> = context.newResult();
              finalResult.allocate(previousResult.size());
              let recursiveAction: org.mwg.Callback<any>[] = new Array<org.mwg.Callback<any>>(1);
              let loopRes: org.mwg.task.TaskResult<any>[] = new Array<org.mwg.task.TaskResult<any>>(1);
              recursiveAction[0] = (res : org.mwg.task.TaskResult<any>) => {
{
                  if (res != null) {
                    for (let i: number = 0; i < res.size(); i++) {
                      finalResult.add(res.get(i));
                    }
                  }
                  loopRes[0].free();
                  let nextResult: any = it.next();
                  if (nextResult != null) {
                    loopRes[0] = context.wrap(nextResult);
                  } else {
                    loopRes[0] = null;
                  }
                  if (nextResult == null) {
                    context.continueWith(finalResult);
                  } else {
                    selfPointer._subTask.executeFrom(context, loopRes[0], org.mwg.plugin.SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
                  }
                }              };
              let nextRes: any = it.next();
              loopRes[0] = context.wrap(nextRes);
              if (nextRes != null) {
                context.graph().scheduler().dispatch(org.mwg.plugin.SchedulerAffinity.SAME_THREAD, () => {
{
                    this._subTask.executeFrom(context, context.wrap(loopRes[0]), org.mwg.plugin.SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
                  }                });
              } else {
                context.continueWith(finalResult);
              }
            }
          }
          public toString(): string {
            return "flatmap()";
          }
        }
        export class ActionFlatmapPar extends org.mwg.plugin.AbstractTaskAction {
          private _subTask: org.mwg.task.Task;
          constructor(p_subTask: org.mwg.task.Task) {
            super();
            this._subTask = p_subTask;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            let finalResult: org.mwg.task.TaskResult<any> = context.wrap(null);
            let it: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
            let previousSize: number = previousResult.size();
            if (previousSize == -1) {
              throw new Error("Foreach on non array structure are not supported yet!");
            }
            finalResult.allocate(previousSize);
            let waiter: org.mwg.DeferCounter = context.graph().newCounter(previousSize);
            let loop: any = it.next();
            while (loop != null) {
              let loopResult: org.mwg.task.TaskResult<any> = context.wrap(loop);
              this._subTask.executeFrom(context, loopResult, org.mwg.plugin.SchedulerAffinity.ANY_LOCAL_THREAD, (result : org.mwg.task.TaskResult<any>) => {
{
                  if (result != null) {
                    for (let i: number = 0; i < result.size(); i++) {
                      finalResult.add(result.get(i));
                    }
                  }
                  loopResult.free();
                  waiter.count();
                }              });
              loop = it.next();
            }
            waiter.then(() => {
{
                context.continueWith(finalResult);
              }            });
          }
          public toString(): string {
            return "flatmapPar()";
          }
        }
        export class ActionForeach extends org.mwg.plugin.AbstractTaskAction {
          private _subTask: org.mwg.task.Task;
          constructor(p_subTask: org.mwg.task.Task) {
            super();
            this._subTask = p_subTask;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let selfPointer: org.mwg.core.task.ActionForeach = this;
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            if (previousResult == null) {
              context.continueTask();
            } else {
              let it: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
              let recursiveAction: org.mwg.Callback<any>[] = new Array<org.mwg.Callback<any>>(1);
              recursiveAction[0] = (res : org.mwg.task.TaskResult<any>) => {
{
                  if (res != null) {
                    res.free();
                  }
                  let nextResult: any = it.next();
                  if (nextResult == null) {
                    context.continueTask();
                  } else {
                    selfPointer._subTask.executeFrom(context, context.wrap(nextResult), org.mwg.plugin.SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
                  }
                }              };
              let nextRes: any = it.next();
              if (nextRes != null) {
                context.graph().scheduler().dispatch(org.mwg.plugin.SchedulerAffinity.SAME_THREAD, () => {
{
                    this._subTask.executeFrom(context, context.wrap(nextRes), org.mwg.plugin.SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
                  }                });
              } else {
                context.continueTask();
              }
            }
          }
          public toString(): string {
            return "foreach()";
          }
        }
        export class ActionForeachPar extends org.mwg.plugin.AbstractTaskAction {
          private _subTask: org.mwg.task.Task;
          constructor(p_subTask: org.mwg.task.Task) {
            super();
            this._subTask = p_subTask;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            let it: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
            let previousSize: number = previousResult.size();
            if (previousSize == -1) {
              throw new Error("Foreach on non array structure are not supported yet!");
            }
            let waiter: org.mwg.DeferCounter = context.graph().newCounter(previousSize);
            let loop: any = it.next();
            while (loop != null) {
              this._subTask.executeFrom(context, context.wrap(loop), org.mwg.plugin.SchedulerAffinity.ANY_LOCAL_THREAD, (result : org.mwg.task.TaskResult<any>) => {
{
                  if (result != null) {
                    result.free();
                  }
                  waiter.count();
                }              });
              loop = it.next();
            }
            waiter.then(() => {
{
                context.continueTask();
              }            });
          }
          public toString(): string {
            return "foreachPar()";
          }
        }
        export class ActionFromIndex extends org.mwg.plugin.AbstractTaskAction {
          private _indexName: string;
          private _query: string;
          constructor(p_indexName: string, p_query: string) {
            super();
            this._indexName = p_indexName;
            this._query = p_query;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let flatIndexName: string = context.template(this._indexName);
            let flatQuery: string = context.template(this._query);
            context.graph().find(context.world(), context.time(), flatIndexName, flatQuery, (result : org.mwg.Node[]) => {
{
                context.continueWith(context.wrap(result));
              }            });
          }
          public toString(): string {
            return "fromIndex(\'" + this._indexName + "\'" + org.mwg.Constants.QUERY_SEP + "\'" + this._query + "\')";
          }
        }
        export class ActionFromIndexAll extends org.mwg.plugin.AbstractTaskAction {
          private _indexName: string;
          constructor(p_indexName: string) {
            super();
            this._indexName = p_indexName;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            context.graph().findAll(context.world(), context.time(), this._indexName, (result : org.mwg.Node[]) => {
{
                context.continueWith(context.wrap(result));
              }            });
          }
          public toString(): string {
            return "fromIndexAll(\'" + this._indexName + "\')";
          }
        }
        export class ActionFromVar extends org.mwg.plugin.AbstractTaskAction {
          private _name: string;
          private _index: number;
          constructor(p_name: string, p_index: number) {
            super();
            this._name = p_name;
            this._index = p_index;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let evaluatedName: string = context.template(this._name);
            let varResult: org.mwg.task.TaskResult<any>;
            if (this._index != -1) {
              varResult = context.wrap(context.variable(evaluatedName).get(this._index));
            } else {
              varResult = context.variable(evaluatedName);
            }
            if (varResult != null) {
              varResult = varResult.clone();
            }
            context.continueWith(varResult);
          }
          public toString(): string {
            return "fromVar(\'" + this._name + "\')";
          }
        }
        export class ActionGet extends org.mwg.plugin.AbstractTaskAction {
          private _name: string;
          constructor(p_name: string) {
            super();
            this._name = p_name;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let finalResult: org.mwg.task.TaskResult<any> = context.newResult();
            let flatName: string = context.template(this._name);
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            if (previousResult != null) {
              let previousSize: number = previousResult.size();
              let defer: org.mwg.DeferCounter = context.graph().newCounter(previousSize);
              for (let i: number = 0; i < previousSize; i++) {
                let loop: any = previousResult.get(i);
                if (loop instanceof org.mwg.plugin.AbstractNode) {
                  let casted: org.mwg.Node = <org.mwg.Node>loop;
                  if (casted.type(flatName) == org.mwg.Type.RELATION) {
                    casted.rel(flatName, (result : org.mwg.Node[]) => {
{
                        if (result != null) {
                          for (let j: number = 0; j < result.length; j++) {
                            finalResult.add(result[j]);
                          }
                        }
                        casted.free();
                        defer.count();
                      }                    });
                  } else {
                    let resolved: any = casted.get(flatName);
                    if (resolved != null) {
                      finalResult.add(resolved);
                    }
                    casted.free();
                    defer.count();
                  }
                } else {
                  finalResult.add(loop);
                  defer.count();
                }
              }
              defer.then(() => {
{
                  previousResult.clear();
                  context.continueWith(finalResult);
                }              });
            } else {
              context.continueTask();
            }
          }
          public toString(): string {
            return "get(\'" + this._name + "\')";
          }
        }
        export class ActionIfThen extends org.mwg.plugin.AbstractTaskAction {
          private _condition: org.mwg.task.TaskFunctionConditional;
          private _action: org.mwg.task.Task;
          constructor(cond: org.mwg.task.TaskFunctionConditional, action: org.mwg.task.Task) {
            super();
            this._condition = cond;
            this._action = action;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            if (this._condition(context)) {
              this._action.executeFrom(context, context.result(), org.mwg.plugin.SchedulerAffinity.SAME_THREAD, (res : org.mwg.task.TaskResult<any>) => {
{
                  context.continueWith(res);
                }              });
            } else {
              context.continueTask();
            }
          }
          public toString(): string {
            return "ifThen()";
          }
        }
        export class ActionIfThenElse extends org.mwg.plugin.AbstractTaskAction {
          private _condition: org.mwg.task.TaskFunctionConditional;
          private _thenSub: org.mwg.task.Task;
          private _elseSub: org.mwg.task.Task;
          constructor(cond: org.mwg.task.TaskFunctionConditional, p_thenSub: org.mwg.task.Task, p_elseSub: org.mwg.task.Task) {
            super();
            this._condition = cond;
            this._thenSub = p_thenSub;
            this._elseSub = p_elseSub;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            if (this._condition(context)) {
              this._thenSub.executeFrom(context, context.result(), org.mwg.plugin.SchedulerAffinity.SAME_THREAD, (res : org.mwg.task.TaskResult<any>) => {
{
                  context.continueWith(res);
                }              });
            } else {
              this._elseSub.executeFrom(context, context.result(), org.mwg.plugin.SchedulerAffinity.SAME_THREAD, (res : org.mwg.task.TaskResult<any>) => {
{
                  context.continueWith(res);
                }              });
            }
          }
          public toString(): string {
            return "ifThen()";
          }
        }
        export class ActionIndexOrUnindexNode extends org.mwg.plugin.AbstractTaskAction {
          private _indexName: string;
          private _flatKeyAttributes: string;
          private _isIndexation: boolean;
          constructor(indexName: string, flatKeyAttributes: string, isIndexation: boolean) {
            super();
            this._indexName = indexName;
            this._flatKeyAttributes = flatKeyAttributes;
            this._isIndexation = isIndexation;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            let templatedIndexName: string = context.template(this._indexName);
            let templatedKeyAttributes: string = context.template(this._flatKeyAttributes);
            let counter: org.mwg.DeferCounter = new org.mwg.core.utility.CoreDeferCounter(previousResult.size());
            let end: org.mwg.Callback<boolean> = (succeed : boolean) => {
{
                if (succeed) {
                  counter.count();
                } else {
                  throw new Error("Error during indexation of node with id " + (<org.mwg.Node>previousResult.get(0)).id());
                }
              }            };
            for (let i: number = 0; i < previousResult.size(); i++) {
              let loop: any = previousResult.get(i);
              if (loop instanceof org.mwg.plugin.AbstractNode) {
                if (this._isIndexation) {
                  context.graph().index(templatedIndexName, <org.mwg.Node>loop, templatedKeyAttributes, end);
                } else {
                  context.graph().unindex(templatedIndexName, <org.mwg.Node>loop, templatedKeyAttributes, end);
                }
              } else {
                counter.count();
              }
            }
            counter.then(() => {
{
                context.continueTask();
              }            });
          }
          public toString(): string {
            if (this._isIndexation) {
              return "indexNode('" + this._indexName + "','" + this._flatKeyAttributes + "')";
            } else {
              return "unindexNode('" + this._indexName + "','" + this._flatKeyAttributes + "')";
            }
          }
        }
        export class ActionIndexOrUnindexNodeAt extends org.mwg.plugin.AbstractTaskAction {
          private _indexName: string;
          private _flatKeyAttributes: string;
          private _isIndexation: boolean;
          private _world: string;
          private _time: string;
          constructor(world: string, time: string, indexName: string, flatKeyAttributes: string, isIndexation: boolean) {
            super();
            this._indexName = indexName;
            this._flatKeyAttributes = flatKeyAttributes;
            this._isIndexation = isIndexation;
            this._world = world;
            this._time = time;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            let templatedWorld: number = java.lang.Long.parseLong(context.template(this._world));
            let templatedTime: number = java.lang.Long.parseLong(context.template(this._time));
            let templatedIndexName: string = context.template(this._indexName);
            let templatedKeyAttributes: string = context.template(this._flatKeyAttributes);
            let counter: org.mwg.DeferCounter = new org.mwg.core.utility.CoreDeferCounter(previousResult.size());
            let end: org.mwg.Callback<boolean> = (succeed : boolean) => {
{
                if (succeed) {
                  counter.count();
                } else {
                  throw new Error("Error during indexation of node with id " + (<org.mwg.Node>previousResult.get(0)).id());
                }
              }            };
            for (let i: number = 0; i < previousResult.size(); i++) {
              let loop: any = previousResult.get(i);
              if (loop instanceof org.mwg.plugin.AbstractNode) {
                if (this._isIndexation) {
                  context.graph().indexAt(templatedWorld, templatedTime, templatedIndexName, <org.mwg.Node>loop, templatedKeyAttributes, end);
                } else {
                  context.graph().unindexAt(templatedWorld, templatedTime, templatedIndexName, <org.mwg.Node>loop, templatedKeyAttributes, end);
                }
              } else {
                counter.count();
              }
            }
            counter.then(() => {
{
                context.continueTask();
              }            });
          }
          public toString(): string {
            if (this._isIndexation) {
              return "indexNodeAt('" + this._world + "','" + this._time + "','" + "'" + this._indexName + "','" + this._flatKeyAttributes + "')";
            } else {
              return "unindexNodeAt('" + this._world + "','" + this._time + "','" + "'" + this._indexName + "','" + this._flatKeyAttributes + "')";
            }
          }
        }
        export class ActionInject extends org.mwg.plugin.AbstractTaskAction {
          private _value: any;
          constructor(value: any) {
            super();
            this._value = value;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            context.continueWith(context.wrap(this._value).clone());
          }
          public toString(): string {
            return "inject()";
          }
        }
        export class ActionIsolate extends org.mwg.plugin.AbstractTaskAction {
          private _subTask: org.mwg.task.Task;
          constructor(p_subTask: org.mwg.task.Task) {
            super();
            this._subTask = p_subTask;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previous: org.mwg.task.TaskResult<any> = context.result();
            this._subTask.executeFrom(context, previous, org.mwg.plugin.SchedulerAffinity.SAME_THREAD, (subTaskResult : org.mwg.task.TaskResult<any>) => {
{
                if (subTaskResult != null) {
                  subTaskResult.free();
                }
                context.continueWith(previous);
              }            });
          }
          public toString(): string {
            return "subTask()";
          }
        }
        export class ActionJump extends org.mwg.plugin.AbstractTaskAction {
          private _time: string;
          constructor(time: string) {
            super();
            this._time = time;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let flatTime: string = context.template(this._time);
            let parsedTime: number = java.lang.Long.parseLong(flatTime);
            let previous: org.mwg.task.TaskResult<any> = context.result();
            let defer: org.mwg.DeferCounter = new org.mwg.core.utility.CoreDeferCounter(previous.size());
            let previousSize: number = previous.size();
            for (let i: number = 0; i < previousSize; i++) {
              let loopObj: any = previous.get(i);
              if (loopObj instanceof org.mwg.plugin.AbstractNode) {
                let castedPreviousNode: org.mwg.Node = <org.mwg.Node>loopObj;
                let finalIndex: number = i;
                castedPreviousNode.jump(parsedTime, (result : org.mwg.Node) => {
{
                    castedPreviousNode.free();
                    previous.set(finalIndex, result);
                    defer.count();
                  }                });
              } else {
                defer.count();
              }
            }
            defer.then(() => {
{
                context.continueTask();
              }            });
          }
          public toString(): string {
            return "jump(\'" + this._time + "\')";
          }
        }
        export class ActionLocalIndexOrUnindex extends org.mwg.plugin.AbstractTaskAction {
          private _indexedRelation: string;
          private _flatKeyAttributes: string;
          private _isIndexation: boolean;
          private _varNodeToAdd: string;
          constructor(indexedRelation: string, flatKeyAttributes: string, varNodeToAdd: string, _isIndexation: boolean) {
            super();
            this._indexedRelation = indexedRelation;
            this._flatKeyAttributes = flatKeyAttributes;
            this._isIndexation = _isIndexation;
            this._varNodeToAdd = varNodeToAdd;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            let templatedIndexName: string = context.template(this._indexedRelation);
            let templatedKeyAttributes: string = context.template(this._flatKeyAttributes);
            let toAdd: org.mwg.task.TaskResult<any> = context.variable(this._varNodeToAdd);
            if (toAdd.size() == 0) {
              throw new Error("Error while adding a new node in a local index: '" + this._varNodeToAdd + "' does not contain any element.");
            }
            let counter: org.mwg.DeferCounter = new org.mwg.core.utility.CoreDeferCounter(previousResult.size() * toAdd.size());
            let end: org.mwg.Callback<boolean> = (succeed : boolean) => {
{
                if (succeed) {
                  counter.count();
                } else {
                  throw new Error("Error during indexation of node with id " + (<org.mwg.Node>previousResult.get(0)).id());
                }
              }            };
            for (let srcNodeIdx: number = 0; srcNodeIdx < previousResult.size(); srcNodeIdx++) {
              let srcNode: any = previousResult.get(srcNodeIdx);
              for (let targetNodeIdx: number = 0; targetNodeIdx < toAdd.size(); targetNodeIdx++) {
                let targetNode: any = toAdd.get(targetNodeIdx);
                if (targetNode instanceof org.mwg.plugin.AbstractNode && srcNode instanceof org.mwg.plugin.AbstractNode) {
                  if (this._isIndexation) {
                    (<org.mwg.plugin.AbstractNode>srcNode).index(templatedIndexName, <org.mwg.plugin.AbstractNode>targetNode, templatedKeyAttributes, end);
                  } else {
                    (<org.mwg.plugin.AbstractNode>srcNode).unindex(templatedIndexName, <org.mwg.plugin.AbstractNode>targetNode, templatedKeyAttributes, end);
                  }
                } else {
                  counter.count();
                }
              }
            }
            counter.then(() => {
{
                context.continueTask();
              }            });
          }
        }
        export class ActionLookup extends org.mwg.plugin.AbstractTaskAction {
          private _id: string;
          constructor(p_id: string) {
            super();
            this._id = p_id;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let idL: number = java.lang.Long.parseLong(context.template(this._id));
            context.graph().lookup(context.world(), context.time(), idL, (result : org.mwg.Node) => {
{
                context.continueWith(context.wrap(result));
              }            });
          }
          public toString(): string {
            return "lookup(\'" + this._id + "\")";
          }
        }
        export class ActionLookupAll extends org.mwg.plugin.AbstractTaskAction {
          private _ids: string;
          constructor(p_ids: string) {
            super();
            this._ids = p_ids;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let afterTemplate: string = context.template(this._ids).trim();
            if ((afterTemplate.lastIndexOf("[", 0) === 0)) {
              afterTemplate = afterTemplate.substring(1, afterTemplate.length - 1);
            }
            let values: string[] = afterTemplate.split(",");
            let ids: Float64Array = new Float64Array(values.length);
            for (let i: number = 0; i < values.length; i++) {
              ids[i] = java.lang.Long.parseLong(values[i]);
            }
            context.graph().lookupAll(context.world(), context.time(), ids, (result : org.mwg.Node[]) => {
{
                context.continueWith(context.wrap(result));
              }            });
          }
          public toString(): string {
            return "lookup(\'" + this._ids + "\")";
          }
        }
        export class ActionLoop extends org.mwg.plugin.AbstractTaskAction {
          private _subTask: org.mwg.task.Task;
          private _lower: string;
          private _upper: string;
          constructor(p_lower: string, p_upper: string, p_subTask: org.mwg.task.Task) {
            super();
            this._subTask = p_subTask;
            this._lower = p_lower;
            this._upper = p_upper;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let lower: number = org.mwg.core.task.TaskHelper.parseInt(context.template(this._lower));
            let upper: number = org.mwg.core.task.TaskHelper.parseInt(context.template(this._upper));
            let previous: org.mwg.task.TaskResult<any> = context.result();
            let selfPointer: org.mwg.core.task.ActionLoop = this;
            let cursor: java.util.concurrent.atomic.AtomicInteger = new java.util.concurrent.atomic.AtomicInteger(lower);
            if ((upper - lower) >= 0) {
              let recursiveAction: org.mwg.Callback<any>[] = new Array<org.mwg.Callback<any>>(1);
              recursiveAction[0] = (res : org.mwg.task.TaskResult<any>) => {
{
                  let current: number = cursor.getAndIncrement();
                  if (res != null) {
                    res.free();
                  }
                  if (current > upper) {
                    context.continueTask();
                  } else {
                    selfPointer._subTask.executeFromUsing(context, previous, org.mwg.plugin.SchedulerAffinity.SAME_THREAD, (result : org.mwg.task.TaskContext) => {
{
                        result.defineVariable("i", current);
                      }                    }, recursiveAction[0]);
                  }
                }              };
              this._subTask.executeFromUsing(context, previous, org.mwg.plugin.SchedulerAffinity.SAME_THREAD, (result : org.mwg.task.TaskContext) => {
{
                  result.defineVariable("i", cursor.getAndIncrement());
                }              }, recursiveAction[0]);
            } else {
              context.continueTask();
            }
          }
          public toString(): string {
            return "loop(\'" + this._lower + "\',\'" + this._upper + "\')";
          }
        }
        export class ActionLoopPar extends org.mwg.plugin.AbstractTaskAction {
          private _subTask: org.mwg.task.Task;
          private _lower: string;
          private _upper: string;
          constructor(p_lower: string, p_upper: string, p_subTask: org.mwg.task.Task) {
            super();
            this._subTask = p_subTask;
            this._lower = p_lower;
            this._upper = p_upper;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let lower: number = org.mwg.core.task.TaskHelper.parseInt(context.template(this._lower));
            let upper: number = org.mwg.core.task.TaskHelper.parseInt(context.template(this._upper));
            let previous: org.mwg.task.TaskResult<any> = context.result();
            let next: org.mwg.task.TaskResult<any> = context.newResult();
            if ((upper - lower) > 0) {
              let waiter: org.mwg.DeferCounter = context.graph().newCounter((upper - lower) + 1);
              for (let i: number = lower; i <= upper; i++) {
                let finalI: number = i;
                this._subTask.executeFromUsing(context, previous, org.mwg.plugin.SchedulerAffinity.ANY_LOCAL_THREAD, (result : org.mwg.task.TaskContext) => {
{
                    result.defineVariable("i", finalI);
                  }                }, (result : org.mwg.task.TaskResult<any>) => {
{
                    if (result != null && result.size() > 0) {
                      for (let i: number = 0; i < result.size(); i++) {
                        next.add(result.get(i));
                      }
                    }
                    waiter.count();
                  }                });
              }
              waiter.then(() => {
{
                  context.continueWith(next);
                }              });
            } else {
              context.continueWith(next);
            }
          }
          public toString(): string {
            return "loopPar(\'" + this._lower + "\',\'" + this._upper + "\')";
          }
        }
        export class ActionMap extends org.mwg.plugin.AbstractTaskAction {
          private _map: org.mwg.task.TaskFunctionMap<any, any>;
          constructor(p_map: org.mwg.task.TaskFunctionMap<any, any>) {
            super();
            this._map = p_map;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previous: org.mwg.task.TaskResult<any> = context.result();
            let next: org.mwg.task.TaskResult<any> = context.wrap(null);
            let previousSize: number = previous.size();
            for (let i: number = 0; i < previousSize; i++) {
              next.add(this._map(previous.get(i)));
            }
            context.continueWith(next);
          }
          public toString(): string {
            return "map()";
          }
        }
        export class ActionMath extends org.mwg.plugin.AbstractTaskAction {
          private _engine: org.mwg.core.task.math.MathExpressionEngine;
          private _expression: string;
          constructor(mathExpression: string) {
            super();
            this._expression = mathExpression;
            this._engine = org.mwg.core.task.math.CoreMathExpressionEngine.parse(mathExpression);
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previous: org.mwg.task.TaskResult<any> = context.result();
            let next: org.mwg.task.TaskResult<number> = context.newResult();
            let previousSize: number = previous.size();
            for (let i: number = 0; i < previousSize; i++) {
              let loop: any = previous.get(i);
              let variables: java.util.Map<string, number> = new java.util.HashMap<string, number>();
              variables.put("PI", Math.PI);
              variables.put("TRUE", 1.0);
              variables.put("FALSE", 0.0);
              if (loop instanceof org.mwg.plugin.AbstractNode) {
                next.add(this._engine.eval(<org.mwg.Node>loop, context, variables));
                (<org.mwg.plugin.AbstractNode>loop).free();
              } else {
                next.add(this._engine.eval(null, context, variables));
              }
            }
            previous.clear();
            context.continueWith(next);
          }
          public toString(): string {
            return "math(\'" + this._expression + "\')";
          }
        }
        export class ActionNewNode extends org.mwg.plugin.AbstractTaskAction {
          private _typeNode: string;
          constructor(typeNode: string) {
            super();
            this._typeNode = typeNode;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let newNode: org.mwg.Node;
            if (this._typeNode == null) {
              newNode = context.graph().newNode(context.world(), context.time());
            } else {
              let templatedType: string = context.template(this._typeNode);
              newNode = context.graph().newTypedNode(context.world(), context.time(), templatedType);
            }
            context.continueWith(context.wrap(newNode));
          }
          public toString(): string {
            if (this._typeNode != null) {
              return "newTypedNode(\'" + this._typeNode + "\')";
            } else {
              return "newNode()";
            }
          }
        }
        export class ActionPlugin extends org.mwg.plugin.AbstractTaskAction {
          private _actionName: string;
          private _flatParams: string;
          private subAction: org.mwg.task.TaskAction = null;
          constructor(actionName: string, flatParams: string) {
            super();
            this._actionName = actionName;
            this._flatParams = flatParams;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let templatedName: string = context.template(this._actionName);
            let templatedParams: string = context.template(this._flatParams);
            let actionFactory: org.mwg.task.TaskActionFactory = context.graph().taskAction(templatedName);
            if (actionFactory == null) {
              throw new Error("Unknown task action: " + templatedName);
            }
            let paramsCapacity: number = org.mwg.core.CoreConstants.MAP_INITIAL_CAPACITY;
            let params: string[] = new Array<string>(paramsCapacity);
            let paramsIndex: number = 0;
            let cursor: number = 0;
            let flatSize: number = templatedParams.length;
            let previous: number = 0;
            while (cursor < flatSize) {
              let current: string = templatedParams.charAt(cursor);
              if (current == org.mwg.Constants.QUERY_SEP) {
                let param: string = templatedParams.substring(previous, cursor);
                if (param.length > 0) {
                  if (paramsIndex >= paramsCapacity) {
                    let newParamsCapacity: number = paramsCapacity * 2;
                    let newParams: string[] = new Array<string>(newParamsCapacity);
                    java.lang.System.arraycopy(params, 0, newParams, 0, paramsCapacity);
                    params = newParams;
                    paramsCapacity = newParamsCapacity;
                  }
                  params[paramsIndex] = param;
                  paramsIndex++;
                }
                previous = cursor + 1;
              }
              cursor++;
            }
            let param: string = templatedParams.substring(previous, cursor);
            if (param.length > 0) {
              if (paramsIndex >= paramsCapacity) {
                let newParamsCapacity: number = paramsCapacity * 2;
                let newParams: string[] = new Array<string>(newParamsCapacity);
                java.lang.System.arraycopy(params, 0, newParams, 0, paramsCapacity);
                params = newParams;
                paramsCapacity = newParamsCapacity;
              }
              params[paramsIndex] = param;
              paramsIndex++;
            }
            if (paramsIndex < params.length) {
              let shrinked: string[] = new Array<string>(paramsIndex);
              java.lang.System.arraycopy(params, 0, shrinked, 0, paramsIndex);
              params = shrinked;
            }
            this.subAction = actionFactory(params);
            if (this.subAction != null) {
              this.subAction.eval(context);
            } else {
              context.continueTask();
            }
          }
          public toString(): string {
            return this._actionName + "(" + this._flatParams + ")";
          }
        }
        export class ActionPrint extends org.mwg.plugin.AbstractTaskAction {
          private _name: string;
          constructor(p_name: string) {
            super();
            this._name = p_name;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            console.log(context.template(this._name));
            context.continueTask();
          }
          public toString(): string {
            return "print(\'" + this._name + "\')";
          }
        }
        export class ActionProperties extends org.mwg.plugin.AbstractTaskAction {
          private _filter: number;
          constructor(filterType: number) {
            super();
            this._filter = filterType;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previous: org.mwg.task.TaskResult<any> = context.result();
            let result: org.mwg.task.TaskResult<any> = context.newResult();
            for (let i: number = 0; i < previous.size(); i++) {
              if (previous.get(i) instanceof org.mwg.plugin.AbstractNode) {
                let n: org.mwg.Node = <org.mwg.Node>previous.get(i);
                let nState: org.mwg.plugin.NodeState = context.graph().resolver().resolveState(n);
                nState.each((attributeKey : number, elemType : number, elem : any) => {
{
                    if (this._filter == -1 || elemType == this._filter) {
                      let retrieved: string = context.graph().resolver().hashToString(attributeKey);
                      if (retrieved != null) {
                        result.add(retrieved);
                      } else {
                        result.add(attributeKey);
                      }
                    }
                  }                });
                n.free();
              }
            }
            previous.clear();
            context.continueWith(result);
          }
        }
        export class ActionRemove extends org.mwg.plugin.AbstractTaskAction {
          private _relationName: string;
          private _variableNameToRemove: string;
          constructor(relationName: string, variableNameToRemove: string) {
            super();
            this._relationName = relationName;
            this._variableNameToRemove = variableNameToRemove;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            let savedVar: org.mwg.task.TaskResult<any> = context.variable(context.template(this._variableNameToRemove));
            if (previousResult != null && savedVar != null) {
              let relName: string = context.template(this._relationName);
              let previousResultIt: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
              let iter: any = previousResultIt.next();
              while (iter != null) {
                if (iter instanceof org.mwg.plugin.AbstractNode) {
                  let savedVarIt: org.mwg.task.TaskResultIterator<any> = savedVar.iterator();
                  let toRemoveIter: any = savedVarIt.next();
                  while (toRemoveIter != null) {
                    if (toRemoveIter instanceof org.mwg.plugin.AbstractNode) {
                      (<org.mwg.Node>iter).remove(relName, <org.mwg.Node>toRemoveIter);
                    }
                    toRemoveIter = savedVarIt.next();
                  }
                }
                iter = previousResultIt.next();
              }
            }
            context.continueTask();
          }
          public toString(): string {
            return "remove(\'" + this._relationName + "\'" + org.mwg.Constants.QUERY_SEP + "\'" + this._variableNameToRemove + "\')";
          }
        }
        export class ActionRemoveProperty extends org.mwg.plugin.AbstractTaskAction {
          private _propertyName: string;
          constructor(propertyName: string) {
            super();
            this._propertyName = propertyName;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            if (previousResult != null) {
              let flatRelationName: string = context.template(this._propertyName);
              for (let i: number = 0; i < previousResult.size(); i++) {
                let loopObj: any = previousResult.get(i);
                if (loopObj instanceof org.mwg.plugin.AbstractNode) {
                  let loopNode: org.mwg.Node = <org.mwg.Node>loopObj;
                  loopNode.removeProperty(flatRelationName);
                }
              }
            }
            context.continueTask();
          }
          public toString(): string {
            return "removeProperty(\'" + this._propertyName + "\')";
          }
        }
        export class ActionSave extends org.mwg.plugin.AbstractTaskAction {
          public eval(context: org.mwg.task.TaskContext): void {
            context.graph().save((result : boolean) => {
{
                context.continueTask();
              }            });
          }
          public toString(): string {
            return "save()";
          }
        }
        export class ActionSelect extends org.mwg.plugin.AbstractTaskAction {
          private _filter: org.mwg.task.TaskFunctionSelect;
          constructor(p_filter: org.mwg.task.TaskFunctionSelect) {
            super();
            this._filter = p_filter;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previous: org.mwg.task.TaskResult<any> = context.result();
            let next: org.mwg.task.TaskResult<any> = context.newResult();
            let previousSize: number = previous.size();
            for (let i: number = 0; i < previousSize; i++) {
              let obj: any = previous.get(i);
              if (obj instanceof org.mwg.plugin.AbstractNode) {
                let casted: org.mwg.Node = <org.mwg.Node>obj;
                if (this._filter(casted)) {
                  next.add(casted);
                } else {
                  casted.free();
                }
              } else {
                next.add(obj);
              }
            }
            previous.clear();
            context.continueWith(next);
          }
          public toString(): string {
            return "select()";
          }
        }
        export class ActionSelectObject extends org.mwg.plugin.AbstractTaskAction {
          private _filter: org.mwg.task.TaskFunctionSelectObject;
          constructor(filterFunction: org.mwg.task.TaskFunctionSelectObject) {
            super();
            this._filter = filterFunction;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previous: org.mwg.task.TaskResult<any> = context.result();
            let next: org.mwg.task.TaskResult<any> = context.wrap(null);
            let iterator: org.mwg.task.TaskResultIterator<any> = previous.iterator();
            let nextElem: any = iterator.next();
            while (nextElem != null) {
              if (this._filter(nextElem, context)) {
                if (nextElem instanceof org.mwg.plugin.AbstractNode) {
                  let casted: org.mwg.Node = <org.mwg.Node>nextElem;
                  next.add(casted.graph().cloneNode(casted));
                } else {
                  next.add(nextElem);
                }
              }
              nextElem = iterator.next();
            }
            context.continueWith(next);
          }
          public toString(): string {
            return "selectObject()";
          }
        }
        export class ActionSetProperty extends org.mwg.plugin.AbstractTaskAction {
          private _relationName: string;
          private _variableNameToSet: string;
          private _propertyType: number;
          private _force: boolean;
          constructor(relationName: string, propertyType: number, variableNameToSet: string, force: boolean) {
            super();
            this._relationName = relationName;
            this._variableNameToSet = variableNameToSet;
            this._propertyType = propertyType;
            this._force = force;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            let flatRelationName: string = context.template(this._relationName);
            if (previousResult != null) {
              let toSet: any;
              let templateBased: any = context.template(this._variableNameToSet);
              switch (this._propertyType) {
                case org.mwg.Type.BOOL:
                  toSet = this.parseBoolean(templateBased.toString());
break;
                case org.mwg.Type.INT:
                  toSet = org.mwg.core.task.TaskHelper.parseInt(templateBased.toString());
break;
                case org.mwg.Type.DOUBLE:
                  toSet = parseFloat(templateBased.toString());
break;
                case org.mwg.Type.LONG:
                  toSet = java.lang.Long.parseLong(templateBased.toString());
break;
                default: 

                  toSet = templateBased;
              }
              for (let i: number = 0; i < previousResult.size(); i++) {
                let loopObj: any = previousResult.get(i);
                if (loopObj instanceof org.mwg.plugin.AbstractNode) {
                  let loopNode: org.mwg.Node = <org.mwg.Node>loopObj;
                  if (this._force) {
                    loopNode.forceProperty(flatRelationName, this._propertyType, toSet);
                  } else {
                    loopNode.setProperty(flatRelationName, this._propertyType, toSet);
                  }
                }
              }
            }
            context.continueTask();
          }
          public toString(): string {
            return "setProperty(\'" + this._relationName + "\'" + org.mwg.Constants.QUERY_SEP + "\'" + this._propertyType + "\'" + org.mwg.Constants.QUERY_SEP + "\'" + this._variableNameToSet + "\')";
          }
          private parseBoolean(booleanValue: string): boolean {
            let lower: string = booleanValue.toLowerCase();
            return (lower === "true" || lower === "1");
          }
        }
        export class ActionSplit extends org.mwg.plugin.AbstractTaskAction {
          private _splitPattern: string;
          constructor(p_splitPattern: string) {
            super();
            this._splitPattern = p_splitPattern;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let splitPattern: string = context.template(this._splitPattern);
            let previous: org.mwg.task.TaskResult<any> = context.result();
            let next: org.mwg.task.TaskResult<any> = context.wrap(null);
            for (let i: number = 0; i < previous.size(); i++) {
              let loop: any = previous.get(0);
              if (loop instanceof String) {
                let splitted: string[] = (<string>loop).split(splitPattern);
                if (previous.size() == 1) {
                  for (let j: number = 0; j < splitted.length; j++) {
                    next.add(splitted[j]);
                  }
                } else {
                  next.add(splitted);
                }
              }
            }
            context.continueWith(next);
          }
          public toString(): string {
            return "split(\'" + this._splitPattern + "\')";
          }
        }
        export class ActionSubTask extends org.mwg.plugin.AbstractTaskAction {
          private _subTask: org.mwg.task.Task;
          constructor(p_subTask: org.mwg.task.Task) {
            super();
            this._subTask = p_subTask;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previous: org.mwg.task.TaskResult<any> = context.result();
            this._subTask.executeFrom(context, previous, org.mwg.plugin.SchedulerAffinity.SAME_THREAD, (subTaskResult : org.mwg.task.TaskResult<any>) => {
{
                context.continueWith(subTaskResult);
              }            });
          }
          public toString(): string {
            return "subTask()";
          }
        }
        export class ActionSubTasks extends org.mwg.plugin.AbstractTaskAction {
          private _subTasks: org.mwg.task.Task[];
          constructor(p_subTasks: org.mwg.task.Task[]) {
            super();
            this._subTasks = p_subTasks;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previous: org.mwg.task.TaskResult<any> = context.result();
            let cursor: java.util.concurrent.atomic.AtomicInteger = new java.util.concurrent.atomic.AtomicInteger(0);
            let tasksSize: number = this._subTasks.length;
            let next: org.mwg.task.TaskResult<any> = context.newResult();
            let loopcb: org.mwg.Callback<org.mwg.task.TaskResult<any>>[] = new Array<org.mwg.Callback<any>>(1);
            loopcb[0] = (result : org.mwg.task.TaskResult<any>) => {
{
                let current: number = cursor.getAndIncrement();
                if (result != null) {
                  for (let i: number = 0; i < result.size(); i++) {
                    let loop: any = result.get(i);
                    if (loop != null) {
                      next.add(loop);
                    }
                  }
                }
                if (current < tasksSize) {
                  this._subTasks[current].executeFrom(context, previous, org.mwg.plugin.SchedulerAffinity.SAME_THREAD, loopcb[0]);
                } else {
                  context.continueWith(next);
                }
              }            };
            let current: number = cursor.getAndIncrement();
            if (current < tasksSize) {
              this._subTasks[current].executeFrom(context, previous, org.mwg.plugin.SchedulerAffinity.SAME_THREAD, loopcb[0]);
            } else {
              context.continueWith(next);
            }
          }
          public toString(): string {
            return "subTasks()";
          }
        }
        export class ActionSubTasksPar extends org.mwg.plugin.AbstractTaskAction {
          private _subTasks: org.mwg.task.Task[];
          constructor(p_subTasks: org.mwg.task.Task[]) {
            super();
            this._subTasks = p_subTasks;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previous: org.mwg.task.TaskResult<any> = context.result();
            let next: org.mwg.task.TaskResult<any> = context.newResult();
            let subTasksSize: number = this._subTasks.length;
            next.allocate(subTasksSize);
            let waiter: org.mwg.DeferCounter = context.graph().newCounter(subTasksSize);
            for (let i: number = 0; i < subTasksSize; i++) {
              let finalI: number = i;
              this._subTasks[i].executeFrom(context, previous, org.mwg.plugin.SchedulerAffinity.ANY_LOCAL_THREAD, (subTaskResult : org.mwg.task.TaskResult<any>) => {
{
                  next.set(finalI, subTaskResult);
                  waiter.count();
                }              });
            }
            waiter.then(() => {
{
                context.continueWith(next);
              }            });
          }
          public toString(): string {
            return "subTasksPar()";
          }
        }
        export class ActionTime extends org.mwg.plugin.AbstractTaskAction {
          private _varName: string;
          constructor(p_varName: string) {
            super();
            this._varName = p_varName;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let flat: string = context.template(this._varName);
            context.setTime(java.lang.Long.parseLong(flat));
            context.continueTask();
          }
          public toString(): string {
            return "setTime(\'" + this._varName + "\')";
          }
        }
        export class ActionTraverse extends org.mwg.plugin.AbstractTaskAction {
          private _name: string;
          constructor(p_name: string) {
            super();
            this._name = p_name;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let finalResult: org.mwg.task.TaskResult<any> = context.wrap(null);
            let flatName: string = context.template(this._name);
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            if (previousResult != null) {
              let previousSize: number = previousResult.size();
              let defer: org.mwg.DeferCounter = context.graph().newCounter(previousSize);
              for (let i: number = 0; i < previousSize; i++) {
                let loop: any = previousResult.get(i);
                if (loop instanceof org.mwg.plugin.AbstractNode) {
                  let casted: org.mwg.Node = <org.mwg.Node>loop;
                  casted.rel(flatName, (result : org.mwg.Node[]) => {
{
                      if (result != null) {
                        for (let j: number = 0; j < result.length; j++) {
                          finalResult.add(result[j]);
                        }
                      }
                      casted.free();
                      defer.count();
                    }                  });
                } else {
                  finalResult.add(loop);
                  defer.count();
                }
              }
              defer.then(() => {
{
                  previousResult.clear();
                  context.continueWith(finalResult);
                }              });
            } else {
              context.continueTask();
            }
          }
          public toString(): string {
            return "traverse(\'" + this._name + "\')";
          }
        }
        export class ActionTraverseIndex extends org.mwg.plugin.AbstractTaskAction {
          private _indexName: string;
          private _queryParams: string[];
          private _resolvedQueryParams: string[];
          constructor(indexName: string, ...queryParams: string[]) {
            super();
            this._queryParams = queryParams;
            this._indexName = indexName;
            this._resolvedQueryParams = new Array<string>(queryParams.length);
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let finalResult: org.mwg.task.TaskResult<any> = context.wrap(null);
            let flatName: string = context.template(this._indexName);
            for (let i: number = 0; i < this._queryParams.length; i++) {
              this._resolvedQueryParams[i] = context.template(this._queryParams[i]);
            }
            let query: org.mwg.Query = context.graph().newQuery();
            query.setWorld(context.world());
            query.setTime(context.time());
            query.setIndexName(flatName);
            for (let i: number = 0; i < this._resolvedQueryParams.length; i = i + 2) {
              query.add(this._resolvedQueryParams[i], this._resolvedQueryParams[i + 1]);
            }
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            if (previousResult != null) {
              let previousSize: number = previousResult.size();
              let defer: org.mwg.DeferCounter = context.graph().newCounter(previousSize);
              for (let i: number = 0; i < previousSize; i++) {
                let loop: any = previousResult.get(i);
                if (loop instanceof org.mwg.plugin.AbstractNode) {
                  let casted: org.mwg.Node = <org.mwg.Node>loop;
                  casted.findByQuery(query, (result : org.mwg.Node[]) => {
{
                      if (result != null) {
                        for (let j: number = 0; j < result.length; j++) {
                          if (result[j] != null) {
                            finalResult.add(result[j]);
                          }
                        }
                      }
                      casted.free();
                      defer.count();
                    }                  });
                } else {
                  finalResult.add(loop);
                  defer.count();
                }
              }
              defer.then(() => {
{
                  previousResult.clear();
                  context.continueWith(finalResult);
                }              });
            } else {
              context.continueTask();
            }
          }
          public toString(): string {
            return "traverseIndex(\'" + this._indexName + org.mwg.core.CoreConstants.QUERY_SEP + java.lang.String.join(",", this._resolvedQueryParams) + "\')";
          }
        }
        export class ActionTraverseIndexAll extends org.mwg.plugin.AbstractTaskAction {
          private _indexName: string;
          constructor(indexName: string) {
            super();
            this._indexName = indexName;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let finalResult: org.mwg.task.TaskResult<any> = context.wrap(null);
            let flatName: string = context.template(this._indexName);
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            if (previousResult != null) {
              let previousSize: number = previousResult.size();
              let defer: org.mwg.DeferCounter = context.graph().newCounter(previousSize);
              for (let i: number = 0; i < previousSize; i++) {
                let loop: any = previousResult.get(i);
                if (loop instanceof org.mwg.plugin.AbstractNode) {
                  let casted: org.mwg.Node = <org.mwg.Node>loop;
                  casted.findAll(flatName, (result : org.mwg.Node[]) => {
{
                      if (result != null) {
                        for (let j: number = 0; j < result.length; j++) {
                          if (result[j] != null) {
                            finalResult.add(result[j]);
                          }
                        }
                      }
                      casted.free();
                      defer.count();
                    }                  });
                } else {
                  finalResult.add(loop);
                  defer.count();
                }
              }
              defer.then(() => {
{
                  previousResult.clear();
                  context.continueWith(finalResult);
                }              });
            } else {
              context.continueTask();
            }
          }
          public toString(): string {
            return "traverseIndexAll(\'" + this._indexName + "\')";
          }
        }
        export class ActionTraverseOrKeep extends org.mwg.plugin.AbstractTaskAction {
          private _name: string;
          constructor(p_name: string) {
            super();
            this._name = p_name;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let flatName: string = context.template(this._name);
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            if (previousResult != null) {
              let finalResult: org.mwg.task.TaskResult<any> = context.newResult();
              let previousSize: number = previousResult.size();
              let defer: org.mwg.DeferCounter = context.graph().newCounter(previousSize);
              for (let i: number = 0; i < previousSize; i++) {
                let loop: any = previousResult.get(i);
                if (loop instanceof org.mwg.plugin.AbstractNode) {
                  let casted: org.mwg.Node = <org.mwg.Node>loop;
                  if (casted.type(flatName) == org.mwg.Type.RELATION) {
                    casted.rel(flatName, (result : org.mwg.Node[]) => {
{
                        if (result != null) {
                          for (let j: number = 0; j < result.length; j++) {
                            finalResult.add(result[j]);
                          }
                        }
                        defer.count();
                      }                    });
                  } else {
                    finalResult.add(casted.graph().cloneNode(casted));
                    defer.count();
                  }
                } else {
                  finalResult.add(loop);
                  defer.count();
                }
              }
              defer.then(() => {
{
                  context.continueWith(finalResult);
                }              });
            } else {
              context.continueTask();
            }
          }
          public toString(): string {
            return "traverseOrKeep(\'" + this._name + "\')";
          }
        }
        export class ActionWhileDo extends org.mwg.plugin.AbstractTaskAction {
          private _cond: org.mwg.task.TaskFunctionConditional;
          private _then: org.mwg.task.Task;
          constructor(p_cond: org.mwg.task.TaskFunctionConditional, p_then: org.mwg.task.Task) {
            super();
            this._cond = p_cond;
            this._then = p_then;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let coreTaskContext: org.mwg.core.task.CoreTaskContext = <org.mwg.core.task.CoreTaskContext>context;
            let selfPointer: org.mwg.core.task.ActionWhileDo = this;
            let recursiveAction: org.mwg.Callback<any>[] = new Array<org.mwg.Callback<any>>(1);
            recursiveAction[0] = (res : org.mwg.task.TaskResult<any>) => {
{
                let previous: org.mwg.task.TaskResult<any> = coreTaskContext._result;
                coreTaskContext._result = res;
                if (this._cond(context)) {
                  if (previous != null) {
                    previous.free();
                  }
                  selfPointer._then.executeFrom(context, (<org.mwg.core.task.CoreTaskContext>context)._result, org.mwg.plugin.SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
                } else {
                  if (previous != null) {
                    previous.free();
                  }
                  context.continueWith(res);
                }
              }            };
            if (this._cond(context)) {
              this._then.executeFrom(context, coreTaskContext._result, org.mwg.plugin.SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
            } else {
              context.continueTask();
            }
          }
          public toString(): string {
            return "whileDo()";
          }
        }
        export class ActionWith extends org.mwg.plugin.AbstractTaskAction {
          private _patternTemplate: string;
          private _name: string;
          constructor(name: string, stringPattern: string) {
            super();
            this._patternTemplate = stringPattern;
            this._name = name;
          }
          public toString(): string {
            return "with(\'" + this._name + "\'" + org.mwg.Constants.QUERY_SEP + "\'" + this._patternTemplate + "\')";
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let pattern: RegExp = new RegExp(context.template(this._patternTemplate));
            let previous: org.mwg.task.TaskResult<any> = context.result();
            let next: org.mwg.task.TaskResult<any> = context.newResult();
            let previousSize: number = previous.size();
            for (let i: number = 0; i < previousSize; i++) {
              let obj: any = previous.get(i);
              if (obj instanceof org.mwg.plugin.AbstractNode) {
                let casted: org.mwg.Node = <org.mwg.Node>obj;
                let currentName: any = casted.get(this._name);
                if (currentName != null && pattern.test(currentName.toString())) {
                  next.add(casted.graph().cloneNode(casted));
                }
              } else {
                next.add(obj);
              }
            }
            context.continueWith(next);
          }
        }
        export class ActionWithout extends org.mwg.plugin.AbstractTaskAction {
          private _patternTemplate: string;
          private _name: string;
          constructor(name: string, stringPattern: string) {
            super();
            this._patternTemplate = stringPattern;
            this._name = name;
          }
          public toString(): string {
            return "without(\'" + this._name + "\'" + org.mwg.Constants.QUERY_SEP + "\'" + this._patternTemplate + "\')";
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let pattern: RegExp = new RegExp(context.template(this._patternTemplate));
            let previous: org.mwg.task.TaskResult<any> = context.result();
            let next: org.mwg.task.TaskResult<any> = context.newResult();
            let previousSize: number = previous.size();
            for (let i: number = 0; i < previousSize; i++) {
              let obj: any = previous.get(i);
              if (obj instanceof org.mwg.plugin.AbstractNode) {
                let casted: org.mwg.Node = <org.mwg.Node>obj;
                let currentName: any = casted.get(this._name);
                if (currentName == null || !pattern.test(currentName.toString())) {
                  next.add(casted.graph().cloneNode(casted));
                }
              } else {
                next.add(obj);
              }
            }
            context.continueWith(next);
          }
        }
        export class ActionWorld extends org.mwg.plugin.AbstractTaskAction {
          private _varName: string;
          constructor(p_varName: string) {
            super();
            this._varName = p_varName;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let flat: string = context.template(this._varName);
            context.setWorld(java.lang.Long.parseLong(flat));
            context.continueTask();
          }
          public toString(): string {
            return "setWorld(\'" + this._varName + "\')";
          }
        }
        export class ActionWrapper extends org.mwg.plugin.AbstractTaskAction {
          private _wrapped: org.mwg.task.Action;
          constructor(p_wrapped: org.mwg.task.Action) {
            super();
            this._wrapped = p_wrapped;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            this._wrapped(context);
          }
          public toString(): string {
            return "then()";
          }
        }
        export class CoreTask implements org.mwg.task.Task {
          private _first: org.mwg.plugin.AbstractTaskAction = null;
          private _last: org.mwg.plugin.AbstractTaskAction = null;
          private _hookFactory: org.mwg.task.TaskHookFactory = null;
          private addAction(nextAction: org.mwg.plugin.AbstractTaskAction): void {
            if (this._first == null) {
              this._first = nextAction;
              this._last = this._first;
            } else {
              this._last.setNext(nextAction);
              this._last = nextAction;
            }
          }
          public setWorld(template: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionWorld(template));
            return this;
          }
          public setTime(template: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionTime(template));
            return this;
          }
          public fromIndex(indexName: string, query: string): org.mwg.task.Task {
            if (indexName == null) {
              throw new Error("indexName should not be null");
            }
            if (query == null) {
              throw new Error("query should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionFromIndex(indexName, query));
            return this;
          }
          public fromIndexAll(indexName: string): org.mwg.task.Task {
            if (indexName == null) {
              throw new Error("indexName should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionFromIndexAll(indexName));
            return this;
          }
          public indexNode(indexName: string, flatKeyAttributes: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionIndexOrUnindexNode(indexName, flatKeyAttributes, true));
            return this;
          }
          public indexNodeAt(world: string, time: string, indexName: string, flatKeyAttributes: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionIndexOrUnindexNodeAt(world, time, indexName, flatKeyAttributes, true));
            return this;
          }
          public localIndex(indexedRelation: string, flatKeyAttributes: string, varNodeToAdd: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionLocalIndexOrUnindex(indexedRelation, flatKeyAttributes, varNodeToAdd, true));
            return this;
          }
          public unindexNodeAt(world: string, time: string, indexName: string, flatKeyAttributes: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionIndexOrUnindexNodeAt(world, time, indexName, flatKeyAttributes, true));
            return this;
          }
          public unindexNode(indexName: string, flatKeyAttributes: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionIndexOrUnindexNode(indexName, flatKeyAttributes, false));
            return this;
          }
          public localUnindex(indexedRelation: string, flatKeyAttributes: string, varNodeToAdd: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionLocalIndexOrUnindex(indexedRelation, flatKeyAttributes, varNodeToAdd, false));
            return this;
          }
          public selectWith(name: string, pattern: string): org.mwg.task.Task {
            if (pattern == null) {
              throw new Error("pattern should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionWith(name, pattern));
            return this;
          }
          public selectWithout(name: string, pattern: string): org.mwg.task.Task {
            if (pattern == null) {
              throw new Error("pattern should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionWithout(name, pattern));
            return this;
          }
          public asGlobalVar(variableName: string): org.mwg.task.Task {
            if (variableName == null) {
              throw new Error("variableName should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionAsVar(variableName, true));
            return this;
          }
          public asVar(variableName: string): org.mwg.task.Task {
            if (variableName == null) {
              throw new Error("variableName should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionAsVar(variableName, false));
            return this;
          }
          public defineVar(variableName: string): org.mwg.task.Task {
            if (variableName == null) {
              throw new Error("variableName should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionDefineVar(variableName));
            return this;
          }
          public addToGlobalVar(variableName: string): org.mwg.task.Task {
            if (variableName == null) {
              throw new Error("variableName should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionAddToVar(variableName, true));
            return this;
          }
          public addToVar(variableName: string): org.mwg.task.Task {
            if (variableName == null) {
              throw new Error("variableName should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionAddToVar(variableName, false));
            return this;
          }
          public fromVar(variableName: string): org.mwg.task.Task {
            if (variableName == null) {
              throw new Error("variableName should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionFromVar(variableName, -1));
            return this;
          }
          public fromVarAt(variableName: string, index: number): org.mwg.task.Task {
            if (variableName == null) {
              throw new Error("variableName should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionFromVar(variableName, index));
            return this;
          }
          public select(filter: org.mwg.task.TaskFunctionSelect): org.mwg.task.Task {
            if (filter == null) {
              throw new Error("filter should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionSelect(filter));
            return this;
          }
          public selectObject(filterFunction: org.mwg.task.TaskFunctionSelectObject): org.mwg.task.Task {
            if (filterFunction == null) {
              throw new Error("filterFunction should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionSelectObject(filterFunction));
            return this;
          }
          public selectWhere(subTask: org.mwg.task.Task): org.mwg.task.Task {
            throw new Error("Not implemented yet");
          }
          public get(name: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionGet(name));
            return this;
          }
          public traverse(relationName: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionTraverse(relationName));
            return this;
          }
          public traverseOrKeep(relationName: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionTraverseOrKeep(relationName));
            return this;
          }
          public traverseIndex(indexName: string, ...queryParams: string[]): org.mwg.task.Task {
            if (indexName == null) {
              throw new Error("indexName should not be null");
            }
            if (queryParams.length % 2 != 0) {
              throw new Error("The number of arguments in the queryParams MUST be even, because it should be a sequence of \"key\",\"value\". Current size: " + queryParams.length);
            }
            this.addAction(new org.mwg.core.task.ActionTraverseIndex(indexName, ...queryParams));
            return this;
          }
          public traverseIndexAll(indexName: string): org.mwg.task.Task {
            if (indexName == null) {
              throw new Error("indexName should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionTraverseIndexAll(indexName));
            return this;
          }
          public map(mapFunction: org.mwg.task.TaskFunctionMap<any, any>): org.mwg.task.Task {
            if (mapFunction == null) {
              throw new Error("mapFunction should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionMap(mapFunction));
            return this;
          }
          public group(groupFunction: org.mwg.task.TaskFunctionGroup): org.mwg.task.Task {
            throw new Error("Not implemented yet");
          }
          public groupWhere(groupSubTask: org.mwg.task.Task): org.mwg.task.Task {
            throw new Error("Not implemented yet");
          }
          public inject(inputValue: any): org.mwg.task.Task {
            if (inputValue == null) {
              throw new Error("inputValue should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionInject(inputValue));
            return this;
          }
          public subTask(subTask: org.mwg.task.Task): org.mwg.task.Task {
            if (subTask == null) {
              throw new Error("subTask should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionSubTask(subTask));
            return this;
          }
          public isolate(subTask: org.mwg.task.Task): org.mwg.task.Task {
            if (subTask == null) {
              throw new Error("subTask should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionIsolate(subTask));
            return this;
          }
          public subTasks(subTasks: org.mwg.task.Task[]): org.mwg.task.Task {
            if (subTasks == null) {
              throw new Error("subTask should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionSubTasks(subTasks));
            return this;
          }
          public subTasksPar(subTasks: org.mwg.task.Task[]): org.mwg.task.Task {
            if (subTasks == null) {
              throw new Error("subTask should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionSubTasksPar(subTasks));
            return this;
          }
          public ifThen(cond: org.mwg.task.TaskFunctionConditional, then: org.mwg.task.Task): org.mwg.task.Task {
            if (cond == null) {
              throw new Error("condition should not be null");
            }
            if (then == null) {
              throw new Error("subTask should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionIfThen(cond, then));
            return this;
          }
          public ifThenElse(cond: org.mwg.task.TaskFunctionConditional, thenSub: org.mwg.task.Task, elseSub: org.mwg.task.Task): org.mwg.task.Task {
            if (cond == null) {
              throw new Error("condition should not be null");
            }
            if (thenSub == null) {
              throw new Error("thenSub should not be null");
            }
            if (elseSub == null) {
              throw new Error("elseSub should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionIfThenElse(cond, thenSub, elseSub));
            return this;
          }
          public whileDo(cond: org.mwg.task.TaskFunctionConditional, then: org.mwg.task.Task): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionWhileDo(cond, then));
            return this;
          }
          public doWhile(then: org.mwg.task.Task, cond: org.mwg.task.TaskFunctionConditional): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionDoWhile(then, cond));
            return this;
          }
          public then(p_action: org.mwg.task.Action): org.mwg.task.Task {
            if (p_action == null) {
              throw new Error("action should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionWrapper(p_action));
            return this;
          }
          public foreach(subTask: org.mwg.task.Task): org.mwg.task.Task {
            if (subTask == null) {
              throw new Error("subTask should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionForeach(subTask));
            return this;
          }
          public flatmap(subTask: org.mwg.task.Task): org.mwg.task.Task {
            if (subTask == null) {
              throw new Error("subTask should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionFlatmap(subTask));
            return this;
          }
          public foreachPar(subTask: org.mwg.task.Task): org.mwg.task.Task {
            if (subTask == null) {
              throw new Error("subTask should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionForeachPar(subTask));
            return this;
          }
          public flatmapPar(subTask: org.mwg.task.Task): org.mwg.task.Task {
            if (subTask == null) {
              throw new Error("subTask should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionFlatmapPar(subTask));
            return this;
          }
          public save(): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionSave());
            return this;
          }
          public clear(): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionClear());
            return this;
          }
          public lookup(nodeId: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionLookup(nodeId));
            return this;
          }
          public lookupAll(nodeId: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionLookupAll(nodeId));
            return this;
          }
          public execute(graph: org.mwg.Graph, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void {
            this.executeWith(graph, null, callback);
          }
          public executeSync(graph: org.mwg.Graph): org.mwg.task.TaskResult<any> {
            let waiter: org.mwg.DeferCounterSync = graph.newSyncCounter(1);
            this.executeWith(graph, null, waiter.wrap());
            return <org.mwg.task.TaskResult<any>>waiter.waitResult();
          }
          public executeWith(graph: org.mwg.Graph, initial: any, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void {
            if (this._first != null) {
              let initalRes: org.mwg.task.TaskResult<any>;
              if (initial instanceof org.mwg.core.task.CoreTaskResult) {
                initalRes = (<org.mwg.task.TaskResult<any>>initial).clone();
              } else {
                initalRes = new org.mwg.core.task.CoreTaskResult<any>(initial, true);
              }
              let hook: org.mwg.task.TaskHook = null;
              if (this._hookFactory != null) {
                hook = this._hookFactory.newHook();
              } else if (graph.taskHookFactory() != null) {
                hook = graph.taskHookFactory().newHook();
              }

              let context: org.mwg.core.task.CoreTaskContext = new org.mwg.core.task.CoreTaskContext(null, initalRes, graph, hook, callback);
              graph.scheduler().dispatch(org.mwg.plugin.SchedulerAffinity.SAME_THREAD, () => {
{
                  context.execute(this._first);
                }              });
            } else {
              if (callback != null) {
                callback(this.emptyResult());
              }
            }
          }
          public prepareWith(graph: org.mwg.Graph, initial: any, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): org.mwg.task.TaskContext {
            let initalRes: org.mwg.task.TaskResult<any>;
            if (initial instanceof org.mwg.core.task.CoreTaskResult) {
              initalRes = (<org.mwg.task.TaskResult<any>>initial).clone();
            } else {
              initalRes = new org.mwg.core.task.CoreTaskResult<any>(initial, true);
            }
            let hook: org.mwg.task.TaskHook = null;
            if (this._hookFactory != null) {
              hook = this._hookFactory.newHook();
            } else if (graph.taskHookFactory() != null) {
              hook = graph.taskHookFactory().newHook();
            }

            return new org.mwg.core.task.CoreTaskContext(null, initalRes, graph, hook, callback);
          }
          public executeUsing(preparedContext: org.mwg.task.TaskContext): void {
            if (this._first != null) {
              preparedContext.graph().scheduler().dispatch(org.mwg.plugin.SchedulerAffinity.SAME_THREAD, () => {
{
                  (<org.mwg.core.task.CoreTaskContext>preparedContext).execute(this._first);
                }              });
            } else {
              let casted: org.mwg.core.task.CoreTaskContext = <org.mwg.core.task.CoreTaskContext>preparedContext;
              if (casted._callback != null) {
                casted._callback(this.emptyResult());
              }
            }
          }
          public executeFrom(parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, affinity: number, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void {
            if (this._first != null) {
              let context: org.mwg.core.task.CoreTaskContext = new org.mwg.core.task.CoreTaskContext(parentContext, initial.clone(), parentContext.graph(), parentContext.hook(), callback);
              parentContext.graph().scheduler().dispatch(affinity, () => {
{
                  context.execute(this._first);
                }              });
            } else {
              if (callback != null) {
                callback(this.emptyResult());
              }
            }
          }
          public executeFromUsing(parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, affinity: number, contextInitializer: org.mwg.Callback<org.mwg.task.TaskContext>, callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>): void {
            if (this._first != null) {
              let context: org.mwg.core.task.CoreTaskContext = new org.mwg.core.task.CoreTaskContext(parentContext, initial.clone(), parentContext.graph(), parentContext.hook(), callback);
              if (contextInitializer != null) {
                contextInitializer(context);
              }
              parentContext.graph().scheduler().dispatch(affinity, () => {
{
                  context.execute(this._first);
                }              });
            } else {
              if (callback != null) {
                callback(this.emptyResult());
              }
            }
          }
          public action(name: string, flatParams: string): org.mwg.task.Task {
            if (name == null) {
              throw new Error("name should not be null");
            }
            if (flatParams == null) {
              throw new Error("flatParams should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionPlugin(name, flatParams));
            return this;
          }
          public parse(flat: string): org.mwg.task.Task {
            if (flat == null) {
              throw new Error("flat should not be null");
            }
            let cursor: number = 0;
            let flatSize: number = flat.length;
            let previous: number = 0;
            let actionName: string = null;
            let isClosed: boolean = false;
            let isEscaped: boolean = false;
            while (cursor < flatSize) {
              let current: string = flat.charAt(cursor);
              switch (current) {
                case '\'':
                  isEscaped = true;
                while (cursor < flatSize) {
                  if (flat.charAt(cursor) == '\'') {
break;
                  }
                  cursor++;
                }
break;
                case org.mwg.Constants.TASK_SEP:                if (!isClosed) {
                  let getName: string = flat.substring(previous, cursor);
                  this.action("get", getName);
                }

                  actionName = null;

                  isEscaped = false;

                  previous = cursor + 1;
break;
                case org.mwg.Constants.TASK_PARAM_OPEN:
                  actionName = flat.substring(previous, cursor);

                  previous = cursor + 1;
break;
                case org.mwg.Constants.TASK_PARAM_CLOSE:                let extracted: string;
                if (isEscaped) {
                  extracted = flat.substring(previous + 1, cursor - 1);
                } else {
                  extracted = flat.substring(previous, cursor);
                }

                  this.action(actionName, extracted);

                  actionName = null;

                  previous = cursor + 1;

                  isClosed = true;
break;
              }
              cursor++;
            }
            if (!isClosed) {
              let getName: string = flat.substring(previous, cursor);
              if (getName.length > 0) {
                this.action("get", getName);
              }
            }
            return this;
          }
          public newNode(): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionNewNode(null));
            return this;
          }
          public newTypedNode(typeNode: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionNewNode(typeNode));
            return this;
          }
          public setProperty(propertyName: string, propertyType: number, variableNameToSet: string): org.mwg.task.Task {
            if (propertyName == null) {
              throw new Error("propertyName should not be null");
            }
            if (variableNameToSet == null) {
              throw new Error("variableNameToSet should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionSetProperty(propertyName, propertyType, variableNameToSet, false));
            return this;
          }
          public forceProperty(propertyName: string, propertyType: number, variableNameToSet: string): org.mwg.task.Task {
            if (propertyName == null) {
              throw new Error("propertyName should not be null");
            }
            if (variableNameToSet == null) {
              throw new Error("variableNameToSet should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionSetProperty(propertyName, propertyType, variableNameToSet, true));
            return this;
          }
          public removeProperty(propertyName: string): org.mwg.task.Task {
            if (propertyName == null) {
              throw new Error("propertyName should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionRemoveProperty(propertyName));
            return this;
          }
          public add(relationName: string, variableNameToAdd: string): org.mwg.task.Task {
            if (relationName == null) {
              throw new Error("relationName should not be null");
            }
            if (variableNameToAdd == null) {
              throw new Error("variableNameToAdd should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionAdd(relationName, variableNameToAdd));
            return this;
          }
          public addTo(relationName: string, variableNameTarget: string): org.mwg.task.Task {
            if (relationName == null) {
              throw new Error("relationName should not be null");
            }
            if (variableNameTarget == null) {
              throw new Error("variableNameTarget should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionAddTo(relationName, variableNameTarget));
            return this;
          }
          public propertiesWithTypes(filter: number): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionProperties(filter));
            return this;
          }
          public properties(): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionProperties(<number>-1));
            return this;
          }
          public remove(relationName: string, variableNameToRemove: string): org.mwg.task.Task {
            if (relationName == null) {
              throw new Error("relationName should not be null");
            }
            if (variableNameToRemove == null) {
              throw new Error("variableNameToRemove should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionRemove(relationName, variableNameToRemove));
            return this;
          }
          public jump(time: string): org.mwg.task.Task {
            if (time == null) {
              throw new Error("time should not be null");
            }
            this.addAction(new org.mwg.core.task.ActionJump(time));
            return this;
          }
          public math(expression: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionMath(expression));
            return this;
          }
          public split(splitPattern: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionSplit(splitPattern));
            return this;
          }
          public loop(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionLoop(from, to, subTask));
            return this;
          }
          public loopPar(from: string, to: string, subTask: org.mwg.task.Task): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionLoopPar(from, to, subTask));
            return this;
          }
          public print(name: string): org.mwg.task.Task {
            this.addAction(new org.mwg.core.task.ActionPrint(name));
            return this;
          }
          public hook(p_hookFactory: org.mwg.task.TaskHookFactory): org.mwg.task.Task {
            this._hookFactory = p_hookFactory;
            return this;
          }
          public emptyResult(): org.mwg.task.TaskResult<any> {
            return new org.mwg.core.task.CoreTaskResult<any>(null, false);
          }
          public mathConditional(mathExpression: string): org.mwg.task.TaskFunctionConditional {
            return new org.mwg.core.task.math.MathConditional(mathExpression).conditional();
          }
          public static fillDefault(registry: java.util.Map<string, org.mwg.task.TaskActionFactory>): void {
            registry.put("get", (params : string[]) => {
{
                if (params.length != 1) {
                  throw new Error("get action need one parameter");
                }
                return new org.mwg.core.task.ActionGet(params[0]);
              }            });
            registry.put("math", (params : string[]) => {
{
                if (params.length != 1) {
                  throw new Error("math action need one parameter");
                }
                return new org.mwg.core.task.ActionMath(params[0]);
              }            });
            registry.put("traverse", (params : string[]) => {
{
                if (params.length != 1) {
                  throw new Error("traverse action need one parameter");
                }
                return new org.mwg.core.task.ActionTraverse(params[0]);
              }            });
            registry.put("traverseOrKeep", (params : string[]) => {
{
                if (params.length != 1) {
                  throw new Error("traverseOrKeep action need one parameter");
                }
                return new org.mwg.core.task.ActionTraverseOrKeep(params[0]);
              }            });
            registry.put("fromIndexAll", (params : string[]) => {
{
                if (params.length != 1) {
                  throw new Error("fromIndexAll action need one parameter");
                }
                return new org.mwg.core.task.ActionFromIndexAll(params[0]);
              }            });
            registry.put("fromIndex", (params : string[]) => {
{
                if (params.length != 2) {
                  throw new Error("fromIndex action need two parameter");
                }
                return new org.mwg.core.task.ActionFromIndex(params[0], params[1]);
              }            });
            registry.put("with", (params : string[]) => {
{
                if (params.length != 2) {
                  throw new Error("with action need two parameter");
                }
                return new org.mwg.core.task.ActionWith(params[0], params[1]);
              }            });
            registry.put("without", (params : string[]) => {
{
                if (params.length != 2) {
                  throw new Error("without action need two parameter");
                }
                return new org.mwg.core.task.ActionWithout(params[0], params[1]);
              }            });
          }
        }
        export class CoreTaskContext implements org.mwg.task.TaskContext {
          private _globalVariables: java.util.Map<string, org.mwg.task.TaskResult<any>>;
          private _parent: org.mwg.task.TaskContext;
          private _graph: org.mwg.Graph;
          public _callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>;
          private _localVariables: java.util.Map<string, org.mwg.task.TaskResult<any>> = null;
          private _nextVariables: java.util.Map<string, org.mwg.task.TaskResult<any>> = null;
          private _current: org.mwg.plugin.AbstractTaskAction;
          public _result: org.mwg.task.TaskResult<any>;
          private _world: number;
          private _time: number;
          private _hook: org.mwg.task.TaskHook;
          constructor(parentContext: org.mwg.task.TaskContext, initial: org.mwg.task.TaskResult<any>, p_graph: org.mwg.Graph, p_hook: org.mwg.task.TaskHook, p_callback: org.mwg.Callback<org.mwg.task.TaskResult<any>>) {
            this._hook = p_hook;
            if (parentContext != null) {
              this._time = parentContext.time();
              this._world = parentContext.world();
            } else {
              this._world = 0;
              this._time = 0;
            }
            this._graph = p_graph;
            this._parent = parentContext;
            let castedParentContext: org.mwg.core.task.CoreTaskContext = <org.mwg.core.task.CoreTaskContext>parentContext;
            if (parentContext == null) {
              this._globalVariables = new java.util.ConcurrentHashMap<string, org.mwg.task.TaskResult<any>>();
            } else {
              this._globalVariables = castedParentContext.globalVariables();
            }
            this._result = initial;
            this._callback = p_callback;
          }
          public graph(): org.mwg.Graph {
            return this._graph;
          }
          public world(): number {
            return this._world;
          }
          public setWorld(p_world: number): void {
            this._world = p_world;
          }
          public time(): number {
            return this._time;
          }
          public setTime(p_time: number): void {
            this._time = p_time;
          }
          public variable(name: string): org.mwg.task.TaskResult<any> {
            let resolved: org.mwg.task.TaskResult<any> = this._globalVariables.get(name);
            if (resolved == null) {
              resolved = this.internal_deep_resolve(name);
            }
            return resolved;
          }
          private internal_deep_resolve(name: string): org.mwg.task.TaskResult<any> {
            let resolved: org.mwg.task.TaskResult<any> = null;
            if (this._localVariables != null) {
              resolved = this._localVariables.get(name);
            }
            if (resolved == null && this._parent != null) {
              let castedParent: org.mwg.core.task.CoreTaskContext = <org.mwg.core.task.CoreTaskContext>this._parent;
              if (castedParent._nextVariables != null) {
                resolved = castedParent._nextVariables.get(name);
                if (resolved != null) {
                  return resolved;
                }
              }
              return castedParent.internal_deep_resolve(name);
            } else {
              return resolved;
            }
          }
          public wrap(input: any): org.mwg.task.TaskResult<any> {
            return new org.mwg.core.task.CoreTaskResult<any>(input, false);
          }
          public wrapClone(input: any): org.mwg.task.TaskResult<any> {
            return new org.mwg.core.task.CoreTaskResult<any>(input, true);
          }
          public newResult(): org.mwg.task.TaskResult<any> {
            return new org.mwg.core.task.CoreTaskResult<any>(null, false);
          }
          public declareVariable(name: string): void {
            if (this._localVariables == null) {
              this._localVariables = new java.util.HashMap<string, org.mwg.task.TaskResult<any>>();
            }
            this._localVariables.put(name, new org.mwg.core.task.CoreTaskResult<any>(null, false));
          }
          private lazyWrap(input: any): org.mwg.task.TaskResult<any> {
            if (input instanceof org.mwg.core.task.CoreTaskResult) {
              return <org.mwg.task.TaskResult<any>>input;
            } else {
              return this.wrap(input);
            }
          }
          public defineVariable(name: string, initialResult: any): void {
            if (this._localVariables == null) {
              this._localVariables = new java.util.HashMap<string, org.mwg.task.TaskResult<any>>();
            }
            this._localVariables.put(name, this.lazyWrap(initialResult).clone());
          }
          public defineVariableForSubTask(name: string, initialResult: any): void {
            if (this._nextVariables == null) {
              this._nextVariables = new java.util.HashMap<string, org.mwg.task.TaskResult<any>>();
            }
            this._nextVariables.put(name, this.lazyWrap(initialResult).clone());
          }
          public setGlobalVariable(name: string, value: any): void {
            let previous: org.mwg.task.TaskResult<any> = this._globalVariables.put(name, this.lazyWrap(value).clone());
            if (previous != null) {
              previous.free();
            }
          }
          public setVariable(name: string, value: any): void {
            let target: java.util.Map<string, org.mwg.task.TaskResult<any>> = this.internal_deep_resolve_map(name);
            if (target == null) {
              if (this._localVariables == null) {
                this._localVariables = new java.util.HashMap<string, org.mwg.task.TaskResult<any>>();
              }
              target = this._localVariables;
            }
            let previous: org.mwg.task.TaskResult<any> = target.put(name, this.lazyWrap(value).clone());
            if (previous != null) {
              previous.free();
            }
          }
          private internal_deep_resolve_map(name: string): java.util.Map<string, org.mwg.task.TaskResult<any>> {
            if (this._localVariables != null) {
              let resolved: org.mwg.task.TaskResult<any> = this._localVariables.get(name);
              if (resolved != null) {
                return this._localVariables;
              }
            }
            if (this._parent != null) {
              let castedParent: org.mwg.core.task.CoreTaskContext = <org.mwg.core.task.CoreTaskContext>this._parent;
              if (castedParent._nextVariables != null) {
                let resolved: org.mwg.task.TaskResult<any> = castedParent._nextVariables.get(name);
                if (resolved != null) {
                  return this._localVariables;
                }
              }
              return (<org.mwg.core.task.CoreTaskContext>this._parent).internal_deep_resolve_map(name);
            } else {
              return null;
            }
          }
          public addToGlobalVariable(name: string, value: any): void {
            let previous: org.mwg.task.TaskResult<any> = this._globalVariables.get(name);
            if (previous == null) {
              previous = new org.mwg.core.task.CoreTaskResult<any>(null, false);
              this._globalVariables.put(name, previous);
            }
            if (value != null) {
              if (value instanceof org.mwg.core.task.CoreTaskResult) {
                let casted: org.mwg.task.TaskResult<any> = <org.mwg.task.TaskResult<any>>value;
                for (let i: number = 0; i < casted.size(); i++) {
                  let loop: any = casted.get(i);
                  if (loop instanceof org.mwg.plugin.AbstractNode) {
                    let castedNode: org.mwg.Node = <org.mwg.Node>loop;
                    previous.add(castedNode.graph().cloneNode(castedNode));
                  } else {
                    previous.add(loop);
                  }
                }
              } else if (value instanceof org.mwg.plugin.AbstractNode) {
                let castedNode: org.mwg.Node = <org.mwg.Node>value;
                previous.add(castedNode.graph().cloneNode(castedNode));
              } else {
                previous.add(value);
              }

            }
          }
          public addToVariable(name: string, value: any): void {
            let target: java.util.Map<string, org.mwg.task.TaskResult<any>> = this.internal_deep_resolve_map(name);
            if (target == null) {
              if (this._localVariables == null) {
                this._localVariables = new java.util.HashMap<string, org.mwg.task.TaskResult<any>>();
              }
              target = this._localVariables;
            }
            let previous: org.mwg.task.TaskResult<any> = target.get(name);
            if (previous == null) {
              previous = new org.mwg.core.task.CoreTaskResult<any>(null, false);
              target.put(name, previous);
            }
            if (value != null) {
              if (value instanceof org.mwg.core.task.CoreTaskResult) {
                let casted: org.mwg.task.TaskResult<any> = <org.mwg.task.TaskResult<any>>value;
                for (let i: number = 0; i < casted.size(); i++) {
                  let loop: any = casted.get(i);
                  if (loop instanceof org.mwg.plugin.AbstractNode) {
                    let castedNode: org.mwg.Node = <org.mwg.Node>loop;
                    previous.add(castedNode.graph().cloneNode(castedNode));
                  } else {
                    previous.add(loop);
                  }
                }
              } else if (value instanceof org.mwg.plugin.AbstractNode) {
                let castedNode: org.mwg.Node = <org.mwg.Node>value;
                previous.add(castedNode.graph().cloneNode(castedNode));
              } else {
                previous.add(value);
              }

            }
          }
          public globalVariables(): java.util.Map<string, org.mwg.task.TaskResult<any>> {
            return this._globalVariables;
          }
          public nextVariables(): java.util.Map<string, org.mwg.task.TaskResult<any>> {
            return this._globalVariables;
          }
          public variables(): java.util.Map<string, org.mwg.task.TaskResult<any>> {
            return this._localVariables;
          }
          public result(): org.mwg.task.TaskResult<any> {
            return this._result;
          }
          public resultAsNodes(): org.mwg.task.TaskResult<org.mwg.Node> {
            return <org.mwg.task.TaskResult<org.mwg.Node>>this._result;
          }
          public resultAsStrings(): org.mwg.task.TaskResult<string> {
            return <org.mwg.task.TaskResult<string>>this._result;
          }
          public continueWith(nextResult: org.mwg.task.TaskResult<any>): void {
            let previousResult: org.mwg.task.TaskResult<any> = this._result;
            if (previousResult != null && previousResult != nextResult) {
              previousResult.free();
            }
            this._result = nextResult;
            this.continueTask();
          }
          public continueTask(): void {
            if (this._hook != null) {
              this._hook.afterAction(this._current, this);
            }
            let nextAction: org.mwg.plugin.AbstractTaskAction = this._current.next();
            this._current = nextAction;
            if (nextAction == null) {
              if (this._localVariables != null) {
                let localValues: java.util.Set<string> = this._localVariables.keySet();
                let flatLocalValues: string[] = localValues.toArray(new Array<string>(localValues.size()));
                for (let i: number = 0; i < flatLocalValues.length; i++) {
                  this._localVariables.get(flatLocalValues[i]).free();
                }
              }
              if (this._nextVariables != null) {
                let nextValues: java.util.Set<string> = this._nextVariables.keySet();
                let flatNextValues: string[] = nextValues.toArray(new Array<string>(nextValues.size()));
                for (let i: number = 0; i < flatNextValues.length; i++) {
                  this._nextVariables.get(flatNextValues[i]).free();
                }
              }
              if (this._parent == null) {
                let globalValues: java.util.Set<string> = this._globalVariables.keySet();
                let globalFlatValues: string[] = globalValues.toArray(new Array<string>(globalValues.size()));
                for (let i: number = 0; i < globalFlatValues.length; i++) {
                  this._globalVariables.get(globalFlatValues[i]).free();
                }
              }
              if (this._hook != null) {
                if (this._parent == null) {
                  this._hook.end(this);
                } else {
                  this._hook.afterTask(this);
                }
              }
              if (this._callback != null) {
                this._callback(this._result);
              } else {
                if (this._result != null) {
                  this._result.free();
                }
              }
            } else {
              if (this._hook != null) {
                this._hook.beforeAction(nextAction, this);
              }
              nextAction.eval(this);
            }
          }
          public execute(initialTaskAction: org.mwg.plugin.AbstractTaskAction): void {
            this._current = initialTaskAction;
            if (this._hook != null) {
              if (this._parent == null) {
                this._hook.start(this);
              } else {
                this._hook.beforeTask(this._parent, this);
              }
              this._hook.beforeAction(this._current, this);
            }
            this._current.eval(this);
          }
          public template(input: string): string {
            if (input == null) {
              return null;
            }
            let cursor: number = 0;
            let buffer: java.lang.StringBuilder = null;
            let previousPos: number = -1;
            while (cursor < input.length) {
              let currentChar: string = input.charAt(cursor);
              let previousChar: string = '0';
              let nextChar: string = '0';
              if (cursor > 0) {
                previousChar = input.charAt(cursor - 1);
              }
              if (cursor + 1 < input.length) {
                nextChar = input.charAt(cursor + 1);
              }
              if (currentChar == '{' && previousChar == '{') {
                previousPos = cursor + 1;
              } else if (previousPos != -1 && currentChar == '}' && previousChar == '}') {
                if (buffer == null) {
                  buffer = new java.lang.StringBuilder();
                  buffer.append(input.substring(0, previousPos - 2));
                }
                let contextKey: string = input.substring(previousPos, cursor - 1).trim();
                if (contextKey.length > 0 && contextKey.charAt(0) == '=') {
                  let mathEngine: org.mwg.core.task.math.MathExpressionEngine = org.mwg.core.task.math.CoreMathExpressionEngine.parse(contextKey.substring(1));
                  let value: number = mathEngine.eval(null, this, new java.util.HashMap<string, number>());
                  let valueStr: string = value + "";
                  for (let i: number = valueStr.length - 1; i >= 0; i--) {
                    if (valueStr.charAt(i) == '.') {
                      valueStr = valueStr.substring(0, i);
break;
                    } else if (valueStr.charAt(i) != '0') {
break;
                    }

                  }
                  buffer.append(valueStr);
                } else {
                  let indexArray: number = -1;
                  if (contextKey.charAt(contextKey.length - 1) == ']') {
                    let indexStart: number = -1;
                    for (let i: number = contextKey.length - 3; i >= 0; i--) {
                      if (contextKey.charAt(i) == '[') {
                        indexStart = i + 1;
break;
                      }
                    }
                    if (indexStart != -1) {
                      indexArray = org.mwg.core.task.TaskHelper.parseInt(contextKey.substring(indexStart, contextKey.length - 1));
                      contextKey = contextKey.substring(0, indexStart - 1);
                      if (indexArray < 0) {
                        throw new Error("Array index out of range: " + indexArray);
                      }
                    }
                  }
                  let foundVar: org.mwg.task.TaskResult<any> = this.variable(contextKey);
                  if (foundVar == null && contextKey === "result") {
                    foundVar = this.result();
                  }
                  if (foundVar != null) {
                    if (foundVar.size() == 1 || indexArray != -1) {
                      let toShow: any = null;
                      if (indexArray == -1) {
                        toShow = foundVar.get(0);
                      } else {
                        toShow = foundVar.get(indexArray);
                      }
                      buffer.append(toShow);
                    } else {
                      let it: org.mwg.task.TaskResultIterator<any> = foundVar.iterator();
                      buffer.append("[");
                      let isFirst: boolean = true;
                      let next: any = it.next();
                      while (next != null) {
                        if (isFirst) {
                          isFirst = false;
                        } else {
                          buffer.append(",");
                        }
                        buffer.append(next);
                        next = it.next();
                      }
                      buffer.append("]");
                    }
                  }
                }
                previousPos = -1;
              } else {
                if (previousPos == -1 && buffer != null) {
                  if (currentChar == '{' && nextChar == '{') {
                  } else {
                    buffer.append(input.charAt(cursor));
                  }
                }
              }

              cursor++;
            }
            if (buffer == null) {
              return input;
            } else {
              return buffer.toString();
            }
          }
          public hook(): org.mwg.task.TaskHook {
            return this._hook;
          }
          public toString(): string {
            return "{result:" + this._result.toString() + "}";
          }
        }
        export class CoreTaskResult<A> implements org.mwg.task.TaskResult<A> {
          private _backend: any[];
          private _capacity: number = 0;
          private _size: number = 0;
          public asArray(): any[] {
            let flat: any[] = new Array<any>(this._size);
            if (this._backend != null) {
              java.lang.System.arraycopy(this._backend, 0, flat, 0, this._size);
            }
            return flat;
          }
          constructor(toWrap: any, protect: boolean) {
            if (Array.isArray(toWrap)) {
              let castedToWrap: any[] = <any[]>toWrap;
              this._size = (<any[]>toWrap).length;
              this._capacity = this._size;
              this._backend = new Array<any>(this._size);
              if (protect) {
                for (let i: number = 0; i < this._size; i++) {
                  let loopObj: any = castedToWrap[i];
                  if (loopObj instanceof org.mwg.plugin.AbstractNode) {
                    let loopNode: org.mwg.Node = <org.mwg.Node>loopObj;
                    this._backend[i] = loopNode.graph().cloneNode(loopNode);
                  } else {
                    this._backend[i] = loopObj;
                  }
                }
              } else {
                java.lang.System.arraycopy(castedToWrap, 0, this._backend, 0, this._size);
              }
            } else if (toWrap instanceof Float64Array) {
              let castedOther: Float64Array = <Float64Array>toWrap;
              this._backend = new Array<any>(castedOther.length);
              for (let i: number = 0; i < castedOther.length; i++) {
                this._backend[i] = castedOther[i];
              }
              this._capacity = this._backend.length;
              this._size = this._capacity;
            } else if (toWrap instanceof Int32Array) {
              let castedOther: Int32Array = <Int32Array>toWrap;
              this._backend = new Array<any>(castedOther.length);
              for (let i: number = 0; i < castedOther.length; i++) {
                this._backend[i] = castedOther[i];
              }
              this._capacity = this._backend.length;
              this._size = this._capacity;
            } else if (toWrap instanceof Float64Array) {
              let castedOther: Float64Array = <Float64Array>toWrap;
              this._backend = new Array<any>(castedOther.length);
              for (let i: number = 0; i < castedOther.length; i++) {
                this._backend[i] = castedOther[i];
              }
              this._capacity = this._backend.length;
              this._size = this._capacity;
            } else if (toWrap instanceof java.util.ArrayList) {
              let castedOtherList: java.util.ArrayList<any> = <java.util.ArrayList<any>>toWrap;
              this._backend = new Array<any>(castedOtherList.size());
              for (let i: number = 0; i < castedOtherList.size(); i++) {
                this._backend[i] = castedOtherList.get(i);
              }
              this._capacity = this._backend.length;
              this._size = this._capacity;
            } else if (toWrap instanceof org.mwg.core.task.CoreTaskResult) {
              let other: org.mwg.core.task.CoreTaskResult<any> = <org.mwg.core.task.CoreTaskResult<any>>toWrap;
              this._size = other._size;
              this._capacity = other._capacity;
              if (other._backend != null) {
                this._backend = new Array<any>(other._backend.length);
                if (protect) {
                  for (let i: number = 0; i < this._size; i++) {
                    let loopObj: any = other._backend[i];
                    if (loopObj instanceof org.mwg.plugin.AbstractNode) {
                      let loopNode: org.mwg.Node = <org.mwg.Node>loopObj;
                      this._backend[i] = loopNode.graph().cloneNode(loopNode);
                    } else {
                      this._backend[i] = loopObj;
                    }
                  }
                } else {
                  java.lang.System.arraycopy(other._backend, 0, this._backend, 0, this._size);
                }
              }
            } else {
              if (toWrap != null) {
                this._backend = new Array<any>(1);
                this._capacity = 1;
                this._size = 1;
                if (toWrap instanceof org.mwg.plugin.AbstractNode) {
                  let toWrapNode: org.mwg.Node = <org.mwg.Node>toWrap;
                  if (protect) {
                    this._backend[0] = toWrapNode.graph().cloneNode(toWrapNode);
                  } else {
                    this._backend[0] = toWrapNode;
                  }
                } else {
                  this._backend[0] = toWrap;
                }
              }
            }





          }
          public iterator(): org.mwg.task.TaskResultIterator<any> {
            return new org.mwg.core.task.CoreTaskResultIterator<any>(this._backend);
          }
          public get(index: number): A {
            if (index < this._size) {
              return <A>this._backend[index];
            } else {
              return null;
            }
          }
          public set(index: number, input: A): void {
            if (index >= this._capacity) {
              this.extendTil(index);
            }
            this._backend[index] = input;
            if (index >= this._size) {
              this._size++;
            }
          }
          public allocate(index: number): void {
            if (index >= this._capacity) {
              if (this._backend == null) {
                this._backend = new Array<any>(index);
                this._capacity = index;
              } else {
                throw new Error("Not implemented yet!!!");
              }
            }
          }
          public add(input: A): void {
            if (this._size >= this._capacity) {
              this.extendTil(this._size);
            }
            this.set(this._size, input);
          }
          public clear(): void {
            this._backend = null;
            this._capacity = 0;
            this._size = 0;
          }
          public clone(): org.mwg.task.TaskResult<A> {
            return new org.mwg.core.task.CoreTaskResult<A>(this, true);
          }
          public free(): void {
            for (let i: number = 0; i < this._capacity; i++) {
              if (this._backend[i] instanceof org.mwg.plugin.AbstractNode) {
                (<org.mwg.Node>this._backend[i]).free();
              }
            }
          }
          public size(): number {
            return this._size;
          }
          private extendTil(index: number): void {
            if (this._capacity <= index) {
              let newCapacity: number = this._capacity * 2;
              if (newCapacity <= index) {
                newCapacity = index + 1;
              }
              let extendedBackend: any[] = new Array<any>(newCapacity);
              if (this._backend != null) {
                java.lang.System.arraycopy(this._backend, 0, extendedBackend, 0, this._size);
              }
              this._backend = extendedBackend;
              this._capacity = newCapacity;
            }
          }
          public toString(): string {
            return this.toJson(true);
          }
          private toJson(withContent: boolean): string {
            let builder: java.lang.StringBuilder = new java.lang.StringBuilder();
            builder.append("[");
            for (let i: number = 0; i < this._size; i++) {
              if (i != 0) {
                builder.append(",");
              }
              let loop: any = this._backend[i];
              if (loop != null) {
                builder.append(loop.toString());
              }
            }
            builder.append("]");
            return builder.toString();
          }
        }
        export class CoreTaskResultIterator<A> implements org.mwg.task.TaskResultIterator<A> {
          private _backend: any[];
          private _size: number;
          private _current: number = 0;
          constructor(p_backend: any[]) {
            if (p_backend != null) {
              this._backend = p_backend;
            } else {
              this._backend = new Array<any>(0);
            }
            this._size = this._backend.length;
          }
          public next(): A {
            if (this._current < this._size) {
              let result: any = this._backend[this._current];
              this._current++;
              return <A>result;
            } else {
              return null;
            }
          }
        }
        export class TaskHelper {
          public static flatNodes(toFLat: any, strict: boolean): org.mwg.Node[] {
            if (toFLat instanceof org.mwg.plugin.AbstractNode) {
              return [<org.mwg.Node>toFLat];
            }
            if (Array.isArray(toFLat)) {
              let resAsArray: any[] = <any[]>toFLat;
              let nodes: org.mwg.Node[] = new Array<org.mwg.Node>(0);
              for (let i: number = 0; i < resAsArray.length; i++) {
                if (resAsArray[i] instanceof org.mwg.plugin.AbstractNode) {
                  let tmp: org.mwg.Node[] = new Array<org.mwg.Node>(nodes.length + 1);
                  java.lang.System.arraycopy(nodes, 0, tmp, 0, nodes.length);
                  tmp[nodes.length] = <org.mwg.plugin.AbstractNode>resAsArray[i];
                  nodes = tmp;
                } else if (Array.isArray(resAsArray[i])) {
                  let innerNodes: org.mwg.Node[] = org.mwg.core.task.TaskHelper.flatNodes(resAsArray[i], strict);
                  let tmp: org.mwg.Node[] = new Array<org.mwg.Node>(nodes.length + innerNodes.length);
                  java.lang.System.arraycopy(nodes, 0, tmp, 0, nodes.length);
                  java.lang.System.arraycopy(innerNodes, 0, tmp, nodes.length, innerNodes.length);
                  nodes = tmp;
                } else if (strict) {
                  throw new Error("[ActionIndexOrUnindexNode] The array in result contains an element with wrong type. " + "Expected type: AbstractNode. Actual type: " + resAsArray[i]);
                }


              }
              return nodes;
            } else if (strict) {
              throw new Error("[ActionIndexOrUnindexNode] Wrong type of result. Expected type is AbstractNode or an array of AbstractNode." + "Actual type is " + toFLat);
            }

            return new Array<org.mwg.Node>(0);
          }
          public static parseInt(s: string): number {
            return parseInt(s);
          }
        }
        export module math {
          export class CoreMathExpressionEngine implements org.mwg.core.task.math.MathExpressionEngine {
            public static decimalSeparator: string = '.';
            public static minusSign: string = '-';
            private _cacheAST: org.mwg.core.task.math.MathToken[];
            constructor(expression: string) {
              this._cacheAST = this.buildAST(this.shuntingYard(expression));
            }
            public static parse(p_expression: string): org.mwg.core.task.math.MathExpressionEngine {
              return new org.mwg.core.task.math.CoreMathExpressionEngine(p_expression);
            }
            public static isNumber(st: string): boolean {
              return !isNaN(+st);
            }
            public static isDigit(c: string): boolean {
              var cc = c.charCodeAt(0);
              if ( cc >= 0x30 && cc <= 0x39 ){
              return true ;
              }
              return false ;
            }
            public static isLetter(c: string): boolean {
              var cc = c.charCodeAt(0);
              if ( ( cc >= 0x41 && cc <= 0x5A ) || ( cc >= 0x61 && cc <= 0x7A ) ){
              return true ;
              }
              return false ;
            }
            public static isWhitespace(c: string): boolean {
              var cc = c.charCodeAt(0);
              if ( ( cc >= 0x0009 && cc <= 0x000D ) || ( cc == 0x0020 ) || ( cc == 0x0085 ) || ( cc == 0x00A0 ) ){
              return true ;
              }
              return false ;
            }
            private shuntingYard(expression: string): java.util.List<string> {
              let outputQueue: java.util.List<string> = new java.util.ArrayList<string>();
              let stack: java.util.Stack<string> = new java.util.Stack<string>();
              let tokenizer: org.mwg.core.task.math.MathExpressionTokenizer = new org.mwg.core.task.math.MathExpressionTokenizer(expression);
              let lastFunction: string = null;
              let previousToken: string = null;
              while (tokenizer.hasNext()) {
                let token: string = tokenizer.next();
                if (org.mwg.core.task.math.MathEntities.getINSTANCE().functions.keySet().contains(token.toUpperCase())) {
                  stack.push(token);
                  lastFunction = token;
                } else if ("," === token) {
                  while (!stack.isEmpty() && !("(" === stack.peek())) {
                    outputQueue.add(stack.pop());
                  }
                  if (stack.isEmpty()) {
                    throw new Error("Parse error for function '" + lastFunction + "'");
                  }
                } else if (org.mwg.core.task.math.MathEntities.getINSTANCE().operators.keySet().contains(token)) {
                  let o1: org.mwg.core.task.math.MathOperation = org.mwg.core.task.math.MathEntities.getINSTANCE().operators.get(token);
                  let token2: string = stack.isEmpty() ? null : stack.peek();
                  while (org.mwg.core.task.math.MathEntities.getINSTANCE().operators.keySet().contains(token2) && ((o1.isLeftAssoc() && o1.getPrecedence() <= org.mwg.core.task.math.MathEntities.getINSTANCE().operators.get(token2).getPrecedence()) || (o1.getPrecedence() < org.mwg.core.task.math.MathEntities.getINSTANCE().operators.get(token2).getPrecedence()))) {
                    outputQueue.add(stack.pop());
                    token2 = stack.isEmpty() ? null : stack.peek();
                  }
                  stack.push(token);
                } else if ("(" === token) {
                  if (previousToken != null) {
                    if (org.mwg.core.task.math.CoreMathExpressionEngine.isNumber(previousToken)) {
                      throw new Error("Missing operator at character position " + tokenizer.getPos());
                    }
                  }
                  stack.push(token);
                } else if (")" === token) {
                  while (!stack.isEmpty() && !("(" === stack.peek())) {
                    outputQueue.add(stack.pop());
                  }
                  if (stack.isEmpty()) {
                    throw new Error("Mismatched parentheses");
                  }
                  stack.pop();
                  if (!stack.isEmpty() && org.mwg.core.task.math.MathEntities.getINSTANCE().functions.keySet().contains(stack.peek().toUpperCase())) {
                    outputQueue.add(stack.pop());
                  }
                } else {
                  outputQueue.add(token);
                }




                previousToken = token;
              }
              while (!stack.isEmpty()) {
                let element: string = stack.pop();
                if ("(" === element || ")" === element) {
                  throw new Error("Mismatched parentheses");
                }
                outputQueue.add(element);
              }
              return outputQueue;
            }
            public eval(context: org.mwg.Node, taskContext: org.mwg.task.TaskContext, variables: java.util.Map<string, number>): number {
              if (this._cacheAST == null) {
                throw new Error("Call parse before");
              }
              let stack: java.util.Stack<number> = new java.util.Stack<number>();
              for (let ii: number = 0; ii < this._cacheAST.length; ii++) {
                let mathToken: org.mwg.core.task.math.MathToken = this._cacheAST[ii];
                switch (mathToken.type()) {
                  case 0:                  let v1: number = stack.pop();
                  let v2: number = stack.pop();
                  let castedOp: org.mwg.core.task.math.MathOperation = <org.mwg.core.task.math.MathOperation>mathToken;

                    stack.push(castedOp.eval(v2, v1));
break;
                  case 1:                  let castedFunction: org.mwg.core.task.math.MathFunction = <org.mwg.core.task.math.MathFunction>mathToken;
                  let p: Float64Array = new Float64Array(castedFunction.getNumParams());
                  for (let i: number = castedFunction.getNumParams() - 1; i >= 0; i--) {
                    p[i] = stack.pop();
                  }

                    stack.push(castedFunction.eval(p));
break;
                  case 2:                  let castedDouble: org.mwg.core.task.math.MathDoubleToken = <org.mwg.core.task.math.MathDoubleToken>mathToken;

                    stack.push(castedDouble.content());
break;
                  case 3:                  let castedFreeToken: org.mwg.core.task.math.MathFreeToken = <org.mwg.core.task.math.MathFreeToken>mathToken;
                  let resolvedVar: number = null;
                  if (variables != null) {
                    resolvedVar = variables.get(castedFreeToken.content());
                  }
                  if (resolvedVar != null) {
                    stack.push(resolvedVar);
                  } else {
                    if ("TIME" === castedFreeToken.content()) {
                      stack.push(<number>context.time());
                    } else {
                      let tokenName: string = castedFreeToken.content().trim();
                      let resolved: any = null;
                      let cleanName: string = null;
                      if (context != null) {
                        if (tokenName.length > 0 && tokenName.charAt(0) == '{' && tokenName.charAt(tokenName.length - 1) == '}') {
                          resolved = context.get(castedFreeToken.content().substring(1, tokenName.length - 1));
                          cleanName = castedFreeToken.content().substring(1, tokenName.length - 1);
                        } else {
                          resolved = context.get(castedFreeToken.content());
                          cleanName = castedFreeToken.content();
                        }
                        if (cleanName.length > 0 && cleanName.charAt(0) == '$') {
                          cleanName = cleanName.substring(1);
                        }
                      }
                      if (taskContext != null) {
                        if (resolved == null) {
                          if (tokenName.charAt(tokenName.length - 1) == ']') {
                            let indexStart: number = -1;
                            let indexArray: number = -1;
                            for (let i: number = tokenName.length - 3; i >= 0; i--) {
                              if (tokenName.charAt(i) == '[') {
                                indexStart = i + 1;
break;
                              }
                            }
                            if (indexStart != -1) {
                              indexArray = this.parseInt(tokenName.substring(indexStart, tokenName.length - 1));
                              tokenName = tokenName.substring(0, indexStart - 1);
                            }
                            let varRes: org.mwg.task.TaskResult<any> = taskContext.variable(tokenName);
                            if (varRes == null && tokenName === "result") {
                              varRes = taskContext.result();
                            }
                            if (varRes != null && varRes.size() > indexArray) {
                              resolved = varRes.get(indexArray);
                            }
                          } else {
                            let varRes: org.mwg.task.TaskResult<any> = taskContext.variable(tokenName);
                            if (varRes == null && tokenName === "result") {
                              varRes = taskContext.result();
                            }
                            if (varRes != null) {
                              resolved = varRes.get(0);
                            }
                          }
                        }
                      }
                      if (resolved != null) {
                        let resultAsDouble: number = this.parseDouble(resolved.toString());
                        variables.put(cleanName, resultAsDouble);
                        let valueString: string = resolved.toString();
                        if (valueString === "true") {
                          stack.push(1.0);
                        } else if (valueString === "false") {
                          stack.push(0.0);
                        } else {
                          try {
                            stack.push(resultAsDouble);
                          } catch ($ex$) {
                            if ($ex$ instanceof Error) {
                              var e: Error = <Error>$ex$;
{
                              }                            } else {
                              throw $ex$;
                            }
                          }
                        }

                      } else {
                        throw new Error("Unknow variable for name " + castedFreeToken.content());
                      }
                    }
                  }
break;
                }
              }
              let result: number = stack.pop();
              if (result == null) {
                return 0;
              } else {
                return result;
              }
            }
            private buildAST(rpn: java.util.List<string>): org.mwg.core.task.math.MathToken[] {
              let result: org.mwg.core.task.math.MathToken[] = new Array<org.mwg.core.task.math.MathToken>(rpn.size());
              for (let ii: number = 0; ii < rpn.size(); ii++) {
                let token: string = rpn.get(ii);
                if (org.mwg.core.task.math.MathEntities.getINSTANCE().operators.keySet().contains(token)) {
                  result[ii] = org.mwg.core.task.math.MathEntities.getINSTANCE().operators.get(token);
                } else if (org.mwg.core.task.math.MathEntities.getINSTANCE().functions.keySet().contains(token.toUpperCase())) {
                  result[ii] = org.mwg.core.task.math.MathEntities.getINSTANCE().functions.get(token.toUpperCase());
                } else {
                  if (token.length > 0 && org.mwg.core.task.math.CoreMathExpressionEngine.isLetter(token.charAt(0))) {
                    result[ii] = new org.mwg.core.task.math.MathFreeToken(token);
                  } else {
                    try {
                      let parsed: number = this.parseDouble(token);
                      result[ii] = new org.mwg.core.task.math.MathDoubleToken(parsed);
                    } catch ($ex$) {
                      if ($ex$ instanceof Error) {
                        var e: Error = <Error>$ex$;
{
                          result[ii] = new org.mwg.core.task.math.MathFreeToken(token);
                        }                      } else {
                        throw $ex$;
                      }
                    }
                  }
                }

              }
              return result;
            }
            private parseDouble(val: string): number {
              return parseFloat(val);
            }
            private parseInt(val: string): number {
              return parseInt(val);
            }
          }
          export class MathConditional {
            private _engine: org.mwg.core.task.math.MathExpressionEngine;
            private _expression: string;
            constructor(mathExpression: string) {
              this._expression = mathExpression;
              this._engine = org.mwg.core.task.math.CoreMathExpressionEngine.parse(mathExpression);
            }
            public conditional(): org.mwg.task.TaskFunctionConditional {
              return (context : org.mwg.task.TaskContext) => {
{
                  let variables: java.util.Map<string, number> = new java.util.HashMap<string, number>();
                  variables.put("PI", Math.PI);
                  variables.put("TRUE", 1.0);
                  variables.put("FALSE", 0.0);
                  return (this._engine.eval(null, context, variables) >= 0.5);
                }              };
            }
            public toString(): string {
              return "cond(\'" + this._expression + "\')";
            }
          }
          export class MathDoubleToken implements org.mwg.core.task.math.MathToken {
            private _content: number;
            constructor(_content: number) {
              this._content = _content;
            }
            public type(): number {
              return 2;
            }
            public content(): number {
              return this._content;
            }
          }
          export class MathEntities {
            private static INSTANCE: org.mwg.core.task.math.MathEntities = null;
            public operators: java.util.HashMap<string, org.mwg.core.task.math.MathOperation>;
            public functions: java.util.HashMap<string, org.mwg.core.task.math.MathFunction>;
            public static getINSTANCE(): org.mwg.core.task.math.MathEntities {
              if (MathEntities.INSTANCE == null) {
                MathEntities.INSTANCE = new org.mwg.core.task.math.MathEntities();
              }
              return MathEntities.INSTANCE;
            }
            constructor() {
              this.operators = new java.util.HashMap<string, org.mwg.core.task.math.MathOperation>();
              this.operators.put("+", new org.mwg.core.task.math.MathOperation("+", 20, true));
              this.operators.put("-", new org.mwg.core.task.math.MathOperation("-", 20, true));
              this.operators.put("*", new org.mwg.core.task.math.MathOperation("*", 30, true));
              this.operators.put("/", new org.mwg.core.task.math.MathOperation("/", 30, true));
              this.operators.put("%", new org.mwg.core.task.math.MathOperation("%", 30, true));
              this.operators.put("^", new org.mwg.core.task.math.MathOperation("^", 40, false));
              this.operators.put("&&", new org.mwg.core.task.math.MathOperation("&&", 4, false));
              this.operators.put("||", new org.mwg.core.task.math.MathOperation("||", 2, false));
              this.operators.put(">", new org.mwg.core.task.math.MathOperation(">", 10, false));
              this.operators.put(">=", new org.mwg.core.task.math.MathOperation(">=", 10, false));
              this.operators.put("<", new org.mwg.core.task.math.MathOperation("<", 10, false));
              this.operators.put("<=", new org.mwg.core.task.math.MathOperation("<=", 10, false));
              this.operators.put("==", new org.mwg.core.task.math.MathOperation("==", 7, false));
              this.operators.put("!=", new org.mwg.core.task.math.MathOperation("!=", 7, false));
              this.functions = new java.util.HashMap<string, org.mwg.core.task.math.MathFunction>();
              this.functions.put("NOT", new org.mwg.core.task.math.MathFunction("NOT", 1));
              this.functions.put("IF", new org.mwg.core.task.math.MathFunction("IF", 3));
              this.functions.put("RAND", new org.mwg.core.task.math.MathFunction("RAND", 0));
              this.functions.put("SIN", new org.mwg.core.task.math.MathFunction("SIN", 1));
              this.functions.put("COS", new org.mwg.core.task.math.MathFunction("COS", 1));
              this.functions.put("TAN", new org.mwg.core.task.math.MathFunction("TAN", 1));
              this.functions.put("ASIN", new org.mwg.core.task.math.MathFunction("ASIN", 1));
              this.functions.put("ACOS", new org.mwg.core.task.math.MathFunction("ACOS", 1));
              this.functions.put("ATAN", new org.mwg.core.task.math.MathFunction("ATAN", 1));
              this.functions.put("MAX", new org.mwg.core.task.math.MathFunction("MAX", 2));
              this.functions.put("MIN", new org.mwg.core.task.math.MathFunction("MIN", 2));
              this.functions.put("ABS", new org.mwg.core.task.math.MathFunction("ABS", 1));
              this.functions.put("LOG", new org.mwg.core.task.math.MathFunction("LOG", 1));
              this.functions.put("ROUND", new org.mwg.core.task.math.MathFunction("ROUND", 2));
              this.functions.put("FLOOR", new org.mwg.core.task.math.MathFunction("FLOOR", 1));
              this.functions.put("CEILING", new org.mwg.core.task.math.MathFunction("CEILING", 1));
              this.functions.put("SQRT", new org.mwg.core.task.math.MathFunction("SQRT", 1));
              this.functions.put("SECONDS", new org.mwg.core.task.math.MathFunction("SECONDS", 1));
              this.functions.put("MINUTES", new org.mwg.core.task.math.MathFunction("MINUTES", 1));
              this.functions.put("HOURS", new org.mwg.core.task.math.MathFunction("HOURS", 1));
              this.functions.put("DAY", new org.mwg.core.task.math.MathFunction("DAY", 1));
              this.functions.put("MONTH", new org.mwg.core.task.math.MathFunction("MONTH", 1));
              this.functions.put("YEAR", new org.mwg.core.task.math.MathFunction("YEAR", 1));
              this.functions.put("DAYOFWEEK", new org.mwg.core.task.math.MathFunction("DAYOFWEEK", 1));
            }
          }
          export interface MathExpressionEngine {
            eval(context: org.mwg.Node, taskContext: org.mwg.task.TaskContext, variables: java.util.Map<string, number>): number;
          }
          export class MathExpressionTokenizer {
            private pos: number = 0;
            private input: string;
            private previousToken: string;
            constructor(input: string) {
              this.input = input.trim();
            }
            public hasNext(): boolean {
              return (this.pos < this.input.length);
            }
            private peekNextChar(): string {
              if (this.pos < (this.input.length - 1)) {
                return this.input.charAt(this.pos + 1);
              } else {
                return '\0';
              }
            }
            public next(): string {
              let token: java.lang.StringBuilder = new java.lang.StringBuilder();
              if (this.pos >= this.input.length) {
                return this.previousToken = null;
              }
              let ch: string = this.input.charAt(this.pos);
              while (org.mwg.core.task.math.CoreMathExpressionEngine.isWhitespace(ch) && this.pos < this.input.length) {
                ch = this.input.charAt(++this.pos);
              }
              if (org.mwg.core.task.math.CoreMathExpressionEngine.isDigit(ch)) {
                while ((org.mwg.core.task.math.CoreMathExpressionEngine.isDigit(ch) || ch == org.mwg.core.task.math.CoreMathExpressionEngine.decimalSeparator) && (this.pos < this.input.length)) {
                  token.append(this.input.charAt(this.pos++));
                  ch = this.pos == this.input.length ? '\0' : this.input.charAt(this.pos);
                }
              } else if (ch == org.mwg.core.task.math.CoreMathExpressionEngine.minusSign && org.mwg.core.task.math.CoreMathExpressionEngine.isDigit(this.peekNextChar()) && ("(" === this.previousToken || "," === this.previousToken || this.previousToken == null || org.mwg.core.task.math.MathEntities.getINSTANCE().operators.keySet().contains(this.previousToken))) {
                token.append(org.mwg.core.task.math.CoreMathExpressionEngine.minusSign);
                this.pos++;
                token.append(this.next());
              } else if (org.mwg.core.task.math.CoreMathExpressionEngine.isLetter(ch) || (ch == '_') || (ch == '{') || (ch == '}') || (ch == '$')) {
                while ((org.mwg.core.task.math.CoreMathExpressionEngine.isLetter(ch) || org.mwg.core.task.math.CoreMathExpressionEngine.isDigit(ch) || (ch == '_') || (ch == '{') || (ch == '}') || (ch == '$')) && (this.pos < this.input.length)) {
                  token.append(this.input.charAt(this.pos++));
                  ch = this.pos == this.input.length ? '\0' : this.input.charAt(this.pos);
                }
                if (this.pos < this.input.length) {
                  if (this.input.charAt(this.pos) == '[') {
                    token.append(this.input.charAt(this.pos++));
                    ch = this.pos == this.input.length ? '\0' : this.input.charAt(this.pos);
                    while (org.mwg.core.task.math.CoreMathExpressionEngine.isDigit(ch) && this.pos < this.input.length) {
                      token.append(this.input.charAt(this.pos++));
                      ch = this.pos == this.input.length ? '\0' : this.input.charAt(this.pos);
                    }
                    if (this.input.charAt(this.pos) != ']') {
                      throw new Error("Error in array definition '" + token + "' at position " + (this.pos - token.length + 1));
                    } else {
                      token.append(this.input.charAt(this.pos++));
                    }
                  }
                }
              } else if (ch == '(' || ch == ')' || ch == ',') {
                token.append(ch);
                this.pos++;
              } else {
                while (!org.mwg.core.task.math.CoreMathExpressionEngine.isLetter(ch) && !org.mwg.core.task.math.CoreMathExpressionEngine.isDigit(ch) && ch != '_' && !org.mwg.core.task.math.CoreMathExpressionEngine.isWhitespace(ch) && ch != '(' && ch != ')' && ch != ',' && (ch != '{') && (ch != '}') && (ch != '$') && (this.pos < this.input.length)) {
                  token.append(this.input.charAt(this.pos));
                  this.pos++;
                  ch = this.pos == this.input.length ? '\0' : this.input.charAt(this.pos);
                  if (ch == org.mwg.core.task.math.CoreMathExpressionEngine.minusSign) {
break;
                  }
                }
                if (!org.mwg.core.task.math.MathEntities.getINSTANCE().operators.keySet().contains(token.toString())) {
                  throw new Error("Unknown operator '" + token + "' at position " + (this.pos - token.length + 1));
                }
              }



              return this.previousToken = token.toString();
            }
            public getPos(): number {
              return this.pos;
            }
          }
          export class MathFreeToken implements org.mwg.core.task.math.MathToken {
            private _content: string;
            constructor(content: string) {
              this._content = content;
            }
            public content(): string {
              return this._content;
            }
            public type(): number {
              return 3;
            }
          }
          export class MathFunction implements org.mwg.core.task.math.MathToken {
            private name: string;
            private numParams: number;
            constructor(name: string, numParams: number) {
              this.name = name.toUpperCase();
              this.numParams = numParams;
            }
            public getName(): string {
              return this.name;
            }
            public getNumParams(): number {
              return this.numParams;
            }
            public eval(p: Float64Array): number {
              if (this.name === "NOT") {
                return (p[0] == 0) ? 1 : 0;
              } else if (this.name === "IF") {
                return !(p[0] == 0) ? p[1] : p[2];
              } else if (this.name === "RAND") {
                return Math.random();
              } else if (this.name === "SIN") {
                return Math.sin(p[0]);
              } else if (this.name === "COS") {
                return Math.cos(p[0]);
              } else if (this.name === "TAN") {
                return Math.tan(p[0]);
              } else if (this.name === "ASIN") {
                return Math.asin(p[0]);
              } else if (this.name === "ACOS") {
                return Math.acos(p[0]);
              } else if (this.name === "ATAN") {
                return Math.atan(p[0]);
              } else if (this.name === "MAX") {
                return p[0] > p[1] ? p[0] : p[1];
              } else if (this.name === "MIN") {
                return p[0] < p[1] ? p[0] : p[1];
              } else if (this.name === "ABS") {
                return Math.abs(p[0]);
              } else if (this.name === "LOG") {
                return Math.log(p[0]);
              } else if (this.name === "ROUND") {
                let factor: number = <number>Math.pow(10, p[1]);
                let value: number = p[0] * factor;
                let tmp: number = Math.round(value);
                return <number>tmp / factor;
              } else if (this.name === "FLOOR") {
                return Math.floor(p[0]);
              } else if (this.name === "CEILING") {
                return Math.ceil(p[0]);
              } else if (this.name === "SQRT") {
                return Math.sqrt(p[0]);
              } else if (this.name === "SECONDS") {
                return this.date_to_seconds(p[0]);
              } else if (this.name === "MINUTES") {
                return this.date_to_minutes(p[0]);
              } else if (this.name === "HOURS") {
                return this.date_to_hours(p[0]);
              } else if (this.name === "DAY") {
                return this.date_to_days(p[0]);
              } else if (this.name === "MONTH") {
                return this.date_to_months(p[0]);
              } else if (this.name === "YEAR") {
                return this.date_to_year(p[0]);
              } else if (this.name === "DAYOFWEEK") {
                return this.date_to_dayofweek(p[0]);
              }























              return 0;
            }
            private date_to_seconds(value: number): number {
              var date = new Date(value);
              return date.getSeconds();
            }
            private date_to_minutes(value: number): number {
              var date = new Date(value);
              return date.getMinutes();
            }
            private date_to_hours(value: number): number {
              var date = new Date(value);
              return date.getHours();
            }
            private date_to_days(value: number): number {
              var date = new Date(value);
              return date.getDate();
            }
            private date_to_months(value: number): number {
              var date = new Date(value);
              return date.getMonth();
            }
            private date_to_year(value: number): number {
              var date = new Date(value);
              return date.getFullYear();
            }
            private date_to_dayofweek(value: number): number {
              var date = new Date(value);
              return date.getDay();
            }
            public type(): number {
              return 1;
            }
          }
          export class MathOperation implements org.mwg.core.task.math.MathToken {
            private oper: string;
            private precedence: number;
            private leftAssoc: boolean;
            constructor(oper: string, precedence: number, leftAssoc: boolean) {
              this.oper = oper;
              this.precedence = precedence;
              this.leftAssoc = leftAssoc;
            }
            public getOper(): string {
              return this.oper;
            }
            public getPrecedence(): number {
              return this.precedence;
            }
            public isLeftAssoc(): boolean {
              return this.leftAssoc;
            }
            public eval(v1: number, v2: number): number {
              if (this.oper === "+") {
                return v1 + v2;
              } else if (this.oper === "-") {
                return v1 - v2;
              } else if (this.oper === "*") {
                return v1 * v2;
              } else if (this.oper === "/") {
                return v1 / v2;
              } else if (this.oper === "%") {
                return v1 % v2;
              } else if (this.oper === "^") {
                return Math.pow(v1, v2);
              } else if (this.oper === "&&") {
                let b1: boolean = !(v1 == 0);
                let b2: boolean = !(v2 == 0);
                return b1 && b2 ? 1 : 0;
              } else if (this.oper === "||") {
                let b1: boolean = !(v1 == 0);
                let b2: boolean = !(v2 == 0);
                return b1 || b2 ? 1 : 0;
              } else if (this.oper === ">") {
                return v1 > v2 ? 1 : 0;
              } else if (this.oper === ">=") {
                return v1 >= v2 ? 1 : 0;
              } else if (this.oper === "<") {
                return v1 < v2 ? 1 : 0;
              } else if (this.oper === "<=") {
                return v1 <= v2 ? 1 : 0;
              } else if (this.oper === "==") {
                return v1 == v2 ? 1 : 0;
              } else if (this.oper === "!=") {
                return v1 != v2 ? 1 : 0;
              }













              return 0;
            }
            public type(): number {
              return 0;
            }
          }
          export interface MathToken {
            type(): number;
          }
        }
      }
      export module utility {
        export class CoreDeferCounter implements org.mwg.DeferCounter {
          private _nb_down: java.util.concurrent.atomic.AtomicInteger;
          private _counter: number;
          private _end: org.mwg.plugin.Job;
          constructor(nb: number) {
            this._counter = nb;
            this._nb_down = new java.util.concurrent.atomic.AtomicInteger(0);
          }
          public count(): void {
            let previous: number;
            let next: number;
            do {
              previous = this._nb_down.get();
              next = previous + 1;
            } while (!this._nb_down.compareAndSet(previous, next));
            if (next == this._counter) {
              if (this._end != null) {
                this._end();
              }
            }
          }
          public getCount(): number {
            return this._nb_down.get();
          }
          public then(p_callback: org.mwg.plugin.Job): void {
            this._end = p_callback;
            if (this._nb_down.get() == this._counter) {
              if (p_callback != null) {
                p_callback();
              }
            }
          }
          public wrap(): org.mwg.Callback<any> {
            return (result : any) => {
{
                this.count();
              }            };
          }
        }
        export class CoreDeferCounterSync implements org.mwg.DeferCounterSync {
          private _nb_down: java.util.concurrent.atomic.AtomicInteger;
          private _counter: number;
          private _end: org.mwg.plugin.Job;
          private _result: any = null;
          constructor(nb: number) {
            this._counter = nb;
            this._nb_down = new java.util.concurrent.atomic.AtomicInteger(0);
          }
          public count(): void {
            this._nb_down.set(this._nb_down.get()+1);
            if(this._nb_down.get() == this._counter){
            if (this._end != null) {this._end();}
            }
          }
          public getCount(): number {
            return this._nb_down.get();
          }
          public then(p_callback: org.mwg.plugin.Job): void {
            this._end = p_callback;
            if (this._nb_down.get() == this._counter) {
              if (p_callback != null) {
                p_callback();
              }
            }
          }
          public wrap(): org.mwg.Callback<any> {
            return (result : any) => {
{
                this._result = result;
                this.count();
              }            };
          }
          public waitResult(): any {
            while (this._nb_down.get() != this._counter) {
            //loop
            }
            return this._result;
          }
        }
        export class ReadOnlyStorage implements org.mwg.plugin.Storage {
          private wrapped: org.mwg.plugin.Storage;
          constructor(toWrap: org.mwg.plugin.Storage) {
            this.wrapped = toWrap;
          }
          public get(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<org.mwg.struct.Buffer>): void {
            this.wrapped.get(keys, callback);
          }
          public put(stream: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void {
            console.error("WARNING: PUT TO A READ ONLY STORAGE");
          }
          public remove(keys: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void {
            console.error("WARNING: REMOVE TO A READ ONLY STORAGE");
          }
          public connect(graph: org.mwg.Graph, callback: org.mwg.Callback<boolean>): void {
            this.wrapped.connect(graph, callback);
          }
          public disconnect(callback: org.mwg.Callback<boolean>): void {
            this.wrapped.disconnect(callback);
          }
          public lock(callback: org.mwg.Callback<org.mwg.struct.Buffer>): void {
            this.wrapped.lock(callback);
          }
          public unlock(previousLock: org.mwg.struct.Buffer, callback: org.mwg.Callback<boolean>): void {
            this.wrapped.unlock(previousLock, callback);
          }
        }
      }
    }
  }
}
