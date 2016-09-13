/// <reference path="mwg.d.ts" />
declare module org {
    module mwg {
        module structure {
            interface NTree {
                nearestN(keys: Float64Array, nbElem: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                nearestWithinRadius(keys: Float64Array, radius: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                nearestNWithinRadius(keys: Float64Array, nbElem: number, radius: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                insertWith(keys: Float64Array, value: org.mwg.Node, callback: org.mwg.Callback<boolean>): void;
                insert(value: org.mwg.Node, callback: org.mwg.Callback<boolean>): void;
                size(): number;
                setDistance(distanceType: number): void;
                setFrom(extractor: string): void;
            }
            class StructureActions {
                static nTreeInsertTo(path: string): org.mwg.task.Task;
                static nTreeNearestN(pathOrVar: string): org.mwg.task.Task;
                static nTreeNearestWithinRadius(pathOrVar: string): org.mwg.task.Task;
                static nTreeNearestNWithinRadius(pathOrVar: string): org.mwg.task.Task;
            }
            class StructurePlugin extends org.mwg.plugin.AbstractPlugin {
                constructor();
            }
            module action {
                class NTreeInsertTo extends org.mwg.plugin.AbstractTaskAction {
                    static NAME: string;
                    private _variableName;
                    constructor(variableName: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class NTreeNearestN extends org.mwg.plugin.AbstractTaskAction {
                    static NAME: string;
                    private _key;
                    private _n;
                    constructor(key: Float64Array, n: number);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class NTreeNearestNWithinRadius extends org.mwg.plugin.AbstractTaskAction {
                    static NAME: string;
                    private _key;
                    private _n;
                    private _radius;
                    constructor(key: Float64Array, n: number, radius: number);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class NTreeNearestWithinRadius extends org.mwg.plugin.AbstractTaskAction {
                    static NAME: string;
                    private _key;
                    private _radius;
                    constructor(key: Float64Array, radius: number);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
                class TraverseById extends org.mwg.plugin.AbstractTaskAction {
                    static NAME: string;
                    private _name;
                    constructor(p_name: string);
                    eval(context: org.mwg.task.TaskContext): void;
                    toString(): string;
                }
            }
            module distance {
                class CosineDistance implements org.mwg.structure.distance.Distance {
                    private static static_instance;
                    static instance(): org.mwg.structure.distance.CosineDistance;
                    constructor();
                    measure(x: Float64Array, y: Float64Array): number;
                    compare(x: number, y: number): boolean;
                    getMinValue(): number;
                    getMaxValue(): number;
                }
                interface Distance {
                    measure(x: Float64Array, y: Float64Array): number;
                    compare(x: number, y: number): boolean;
                    getMinValue(): number;
                    getMaxValue(): number;
                }
                class Distances {
                    static EUCLIDEAN: number;
                    static GEODISTANCE: number;
                    static COSINE: number;
                }
                class EuclideanDistance implements org.mwg.structure.distance.Distance {
                    private static static_instance;
                    static instance(): org.mwg.structure.distance.EuclideanDistance;
                    constructor();
                    measure(x: Float64Array, y: Float64Array): number;
                    compare(x: number, y: number): boolean;
                    getMinValue(): number;
                    getMaxValue(): number;
                }
                class GeoDistance implements org.mwg.structure.distance.Distance {
                    private static static_instance;
                    static instance(): org.mwg.structure.distance.GeoDistance;
                    constructor();
                    measure(x: Float64Array, y: Float64Array): number;
                    private static toRadians(angledeg);
                    compare(x: number, y: number): boolean;
                    getMinValue(): number;
                    getMaxValue(): number;
                }
                class PearsonDistance implements org.mwg.structure.distance.Distance {
                    private static static_instance;
                    static instance(): org.mwg.structure.distance.PearsonDistance;
                    constructor();
                    measure(a: Float64Array, b: Float64Array): number;
                    compare(x: number, y: number): boolean;
                    getMinValue(): number;
                    getMaxValue(): number;
                }
            }
            module tree {
                class KDTree extends org.mwg.plugin.AbstractNode implements org.mwg.structure.NTree {
                    static NAME: string;
                    static FROM: string;
                    static LEFT: string;
                    static RIGHT: string;
                    static KEY: string;
                    static VALUE: string;
                    static SIZE: string;
                    static DIMENSIONS: string;
                    static DISTANCE: string;
                    static DISTANCE_THRESHOLD: string;
                    static DISTANCE_THRESHOLD_DEF: number;
                    static DISTANCE_TYPE_DEF: number;
                    private static insert;
                    private static nearestTask;
                    private static nearestRadiusTask;
                    private static enforcer;
                    constructor(p_world: number, p_time: number, p_id: number, p_graph: org.mwg.Graph);
                    private static initFindNear();
                    private static initFindRadius();
                    setProperty(propertyName: string, propertyType: number, propertyValue: any): void;
                    insert(value: org.mwg.Node, callback: org.mwg.Callback<boolean>): void;
                    insertWith(key: Float64Array, value: org.mwg.Node, callback: org.mwg.Callback<boolean>): void;
                    size(): number;
                    setDistance(distanceType: number): void;
                    setFrom(extractor: string): void;
                    nearestNWithinRadius(key: Float64Array, n: number, radius: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                    nearestWithinRadius(key: Float64Array, radius: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                    nearestN(key: Float64Array, n: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                    getDistance(state: org.mwg.plugin.NodeState): org.mwg.structure.distance.Distance;
                    extractFeatures(current: org.mwg.Node, callback: org.mwg.Callback<Float64Array>): void;
                }
                class NDTree extends org.mwg.plugin.AbstractNode {
                    static NAME: string;
                    private static _STAT;
                    private static _TOTAL;
                    private static _SUM;
                    private static _SUMSQ;
                    private static _BOUNDMIN;
                    private static _BOUNDMAX;
                    private static _VALUES;
                    private static _VALUES_STR;
                    private static _KEYS;
                    private static _KEYS_STR;
                    private static _PRECISION;
                    private static _NUMNODES;
                    private static _DIM;
                    static STAT_DEF: boolean;
                    static BOUNDMIN: string;
                    static BOUNDMAX: string;
                    static PRECISION: string;
                    static _RELCONST: number;
                    private static insert;
                    private static nearestTask;
                    constructor(p_world: number, p_time: number, p_id: number, p_graph: org.mwg.Graph);
                    setUpdateStat(value: boolean): void;
                    private static updateGaussian(state, key);
                    getTotal(): number;
                    getAvg(): Float64Array;
                    getCovarianceArray(avg: Float64Array, err: Float64Array): Float64Array;
                    static getRelationId(centerKey: Float64Array, keyToInsert: Float64Array): number;
                    static binaryFromLong(value: number, dim: number): boolean[];
                    setProperty(propertyName: string, propertyType: number, propertyValue: any): void;
                    insert(key: Float64Array, value: org.mwg.Node, callback: org.mwg.Callback<boolean>): void;
                    nearestN(key: Float64Array, n: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
                    static convertToDistance(attributeKey: number, target: Float64Array, center: Float64Array, boundMin: Float64Array, boundMax: Float64Array, precision: Float64Array, distance: org.mwg.structure.distance.Distance): number;
                    private static initNearestTask();
                    getNumNodes(): number;
                }
            }
            module util {
                class HRect {
                    min: Float64Array;
                    max: Float64Array;
                    constructor(vmin: Float64Array, vmax: Float64Array);
                    clone(): any;
                    closest(t: Float64Array): Float64Array;
                    static infiniteHRect(d: number): org.mwg.structure.util.HRect;
                    intersection(r: org.mwg.structure.util.HRect): org.mwg.structure.util.HRect;
                    area(): number;
                    toString(): string;
                }
                class NDResult {
                    parent: org.mwg.structure.tree.NDTree;
                    relation: number;
                    distance: number;
                }
                class NearestNeighborArrayList {
                    private maxPriority;
                    private data;
                    private value;
                    private count;
                    constructor();
                    getMaxPriority(): number;
                    insert(node: number, priority: number): boolean;
                    getAllNodes(): Float64Array;
                    getHighest(): number;
                    getBestDistance(): number;
                    isEmpty(): boolean;
                    getSize(): number;
                    private remove();
                    private bubbleDown(pos);
                    private bubbleUp(pos);
                }
                class NearestNeighborList {
                    private maxPriority;
                    private data;
                    private value;
                    private count;
                    private capacity;
                    constructor(capacity: number);
                    getMaxPriority(): number;
                    removeNode(node: number): boolean;
                    insert(node: number, priority: number): boolean;
                    private print();
                    getAllNodes(): Float64Array;
                    isCapacityReached(): boolean;
                    getHighest(): number;
                    getWorstDistance(): number;
                    isEmpty(): boolean;
                    getSize(): number;
                    private add(element, priority);
                    private remove();
                    private bubbleDown(pos);
                    private bubbleUp(pos);
                    private expandCapacity();
                    getAllNodesWithin(radius: number): Float64Array;
                }
            }
        }
    }
}
