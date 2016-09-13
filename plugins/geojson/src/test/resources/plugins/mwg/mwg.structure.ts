/// <reference path="mwg.d.ts" />

module org {
  export module mwg {
    export module structure {
      export interface NTree {
        nearestN(keys: Float64Array, nbElem: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
        nearestWithinRadius(keys: Float64Array, radius: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
        nearestNWithinRadius(keys: Float64Array, nbElem: number, radius: number, callback: org.mwg.Callback<org.mwg.Node[]>): void;
        insertWith(keys: Float64Array, value: org.mwg.Node, callback: org.mwg.Callback<boolean>): void;
        insert(value: org.mwg.Node, callback: org.mwg.Callback<boolean>): void;
        size(): number;
        setDistance(distanceType: number): void;
        setFrom(extractor: string): void;
      }
      export class StructureActions {
        public static nTreeInsertTo(path: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().action(org.mwg.structure.action.NTreeInsertTo.NAME, path);
        }
        public static nTreeNearestN(pathOrVar: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().action(org.mwg.structure.action.NTreeNearestN.NAME, pathOrVar);
        }
        public static nTreeNearestWithinRadius(pathOrVar: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().action(org.mwg.structure.action.NTreeNearestWithinRadius.NAME, pathOrVar);
        }
        public static nTreeNearestNWithinRadius(pathOrVar: string): org.mwg.task.Task {
          return org.mwg.task.Actions.newTask().action(org.mwg.structure.action.NTreeNearestNWithinRadius.NAME, pathOrVar);
        }
      }
      export class StructurePlugin extends org.mwg.plugin.AbstractPlugin {
        constructor() {
          super();
          this.declareNodeType(org.mwg.structure.tree.KDTree.NAME, (world : number, time : number, id : number, graph : org.mwg.Graph) => {
{
              return new org.mwg.structure.tree.KDTree(world, time, id, graph);
            }          });
          this.declareNodeType(org.mwg.structure.tree.NDTree.NAME, (world : number, time : number, id : number, graph : org.mwg.Graph) => {
{
              return new org.mwg.structure.tree.NDTree(world, time, id, graph);
            }          });
          this.declareTaskAction(org.mwg.structure.action.NTreeInsertTo.NAME, (params : string[]) => {
{
              if (params.length != 1) {
                throw new Error("Bad param number!");
              }
              return new org.mwg.structure.action.NTreeInsertTo(params[0]);
            }          });
          this.declareTaskAction(org.mwg.structure.action.TraverseById.NAME, (params : string[]) => {
{
              if (params.length != 1) {
                throw new Error("Bad param number!");
              }
              return new org.mwg.structure.action.TraverseById(params[0]);
            }          });
          this.declareTaskAction(org.mwg.structure.action.NTreeNearestN.NAME, (params : string[]) => {
{
              if (params.length < 2) {
                throw new Error("Bad param number!");
              }
              let n: number = java.lang.Integer.parseInt(params[params.length - 1]);
              let key: Float64Array = new Float64Array(params.length - 1);
              for (let i: number = 0; i < params.length - 1; i++) {
                key[i] = parseFloat(params[i]);
              }
              return new org.mwg.structure.action.NTreeNearestN(key, n);
            }          });
          this.declareTaskAction(org.mwg.structure.action.NTreeNearestWithinRadius.NAME, (params : string[]) => {
{
              if (params.length < 2) {
                throw new Error("Bad param number!");
              }
              let radius: number = parseFloat(params[params.length - 1]);
              let key: Float64Array = new Float64Array(params.length - 1);
              for (let i: number = 0; i < params.length - 1; i++) {
                key[i] = parseFloat(params[i]);
              }
              return new org.mwg.structure.action.NTreeNearestWithinRadius(key, radius);
            }          });
          this.declareTaskAction(org.mwg.structure.action.NTreeNearestNWithinRadius.NAME, (params : string[]) => {
{
              if (params.length < 3) {
                throw new Error("Bad param number!");
              }
              let radius: number = parseFloat(params[params.length - 1]);
              let n: number = java.lang.Integer.parseInt(params[params.length - 2]);
              let key: Float64Array = new Float64Array(params.length - 2);
              for (let i: number = 0; i < params.length - 2; i++) {
                key[i] = parseFloat(params[i]);
              }
              return new org.mwg.structure.action.NTreeNearestNWithinRadius(key, n, radius);
            }          });
        }
      }
      export module action {
        export class NTreeInsertTo extends org.mwg.plugin.AbstractTaskAction {
          public static NAME: string = "nTreeInsertTo";
          private _variableName: string;
          constructor(variableName: string) {
            super();
            this._variableName = variableName;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            let savedVar: org.mwg.task.TaskResult<any> = context.variable(context.template(this._variableName));
            if (previousResult != null && savedVar != null) {
              let defer: org.mwg.DeferCounter = context.graph().newCounter(previousResult.size());
              let previousResultIt: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
              let iter: any = previousResultIt.next();
              while (iter != null) {
                if (iter instanceof org.mwg.plugin.AbstractNode) {
                  let savedVarIt: org.mwg.task.TaskResultIterator<any> = savedVar.iterator();
                  let toAddIter: any = savedVarIt.next();
                  while (toAddIter != null) {
                    if (toAddIter instanceof org.mwg.structure.tree.KDTree) {
                      (<org.mwg.structure.NTree>toAddIter).insert(<org.mwg.Node>iter, (result : boolean) => {
{
                          defer.count();
                        }                      });
                    } else {
                      defer.count();
                    }
                    toAddIter = savedVarIt.next();
                  }
                } else {
                  defer.count();
                }
                iter = previousResultIt.next();
              }
              defer.then(() => {
{
                  context.continueTask();
                }              });
            } else {
              context.continueTask();
            }
          }
          public toString(): string {
            return "nTreeInsertTo(\'" + this._variableName + "\')";
          }
        }
        export class NTreeNearestN extends org.mwg.plugin.AbstractTaskAction {
          public static NAME: string = "nTreeNearestN";
          private _key: Float64Array;
          private _n: number;
          constructor(key: Float64Array, n: number) {
            super();
            this._key = key;
            this._n = n;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            let nextResult: org.mwg.task.TaskResult<any> = context.newResult();
            if (previousResult != null) {
              let defer: org.mwg.DeferCounter = context.graph().newCounter(previousResult.size());
              let previousResultIt: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
              let iter: any = previousResultIt.next();
              while (iter != null) {
                if (iter instanceof org.mwg.structure.tree.KDTree) {
                  (<org.mwg.structure.NTree>iter).nearestN(this._key, this._n, (result : org.mwg.Node[]) => {
{
                      for (let i: number = 0; i < result.length; i++) {
                        nextResult.add(result[i]);
                      }
                      defer.count();
                    }                  });
                } else {
                  defer.count();
                }
                iter = previousResultIt.next();
              }
              defer.then(() => {
{
                  context.continueWith(nextResult);
                }              });
            } else {
              context.continueWith(nextResult);
            }
          }
          public toString(): string {
            return "nTreeNearestN(\'" + "\')";
          }
        }
        export class NTreeNearestNWithinRadius extends org.mwg.plugin.AbstractTaskAction {
          public static NAME: string = "nTreeNearestNWithinRadius";
          private _key: Float64Array;
          private _n: number;
          private _radius: number;
          constructor(key: Float64Array, n: number, radius: number) {
            super();
            this._key = key;
            this._n = n;
            this._radius = radius;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            let nextResult: org.mwg.task.TaskResult<org.mwg.Node> = context.newResult();
            if (previousResult != null) {
              let defer: org.mwg.DeferCounter = context.graph().newCounter(previousResult.size());
              let previousResultIt: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
              let iter: any = previousResultIt.next();
              while (iter != null) {
                if (iter instanceof org.mwg.structure.tree.KDTree) {
                  (<org.mwg.structure.NTree>iter).nearestNWithinRadius(this._key, this._n, this._radius, (result : org.mwg.Node[]) => {
{
                      for (let i: number = 0; i < result.length; i++) {
                        nextResult.add(result[i]);
                      }
                      defer.count();
                    }                  });
                } else {
                  defer.count();
                }
                iter = previousResultIt.next();
              }
              defer.then(() => {
{
                  context.continueWith(nextResult);
                }              });
            } else {
              context.continueWith(nextResult);
            }
          }
          public toString(): string {
            return "nTreeNearestNWithinRadius(\'" + "\')";
          }
        }
        export class NTreeNearestWithinRadius extends org.mwg.plugin.AbstractTaskAction {
          public static NAME: string = "nTreeNearestWithinRadius";
          private _key: Float64Array;
          private _radius: number;
          constructor(key: Float64Array, radius: number) {
            super();
            this._key = key;
            this._radius = radius;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            let nextResult: org.mwg.task.TaskResult<org.mwg.Node> = context.newResult();
            if (previousResult != null) {
              let defer: org.mwg.DeferCounter = context.graph().newCounter(previousResult.size());
              let previousResultIt: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
              let iter: any = previousResultIt.next();
              while (iter != null) {
                if (iter instanceof org.mwg.structure.tree.KDTree) {
                  (<org.mwg.structure.NTree>iter).nearestWithinRadius(this._key, this._radius, (result : org.mwg.Node[]) => {
{
                      for (let i: number = 0; i < result.length; i++) {
                        nextResult.add(result[i]);
                      }
                      defer.count();
                    }                  });
                } else {
                  defer.count();
                }
                iter = previousResultIt.next();
              }
              defer.then(() => {
{
                  context.continueWith(nextResult);
                }              });
            } else {
              context.continueWith(nextResult);
            }
          }
          public toString(): string {
            return "nTreeNearestWithinRadius(\'" + "\')";
          }
        }
        export class TraverseById extends org.mwg.plugin.AbstractTaskAction {
          public static NAME: string = "traverseById";
          private _name: string;
          constructor(p_name: string) {
            super();
            this._name = p_name;
          }
          public eval(context: org.mwg.task.TaskContext): void {
            let finalResult: org.mwg.task.TaskResult<any> = context.wrap(null);
            let flatlongName: number = java.lang.Long.parseLong(context.template(this._name));
            let previousResult: org.mwg.task.TaskResult<any> = context.result();
            if (previousResult != null) {
              let previousSize: number = previousResult.size();
              let defer: org.mwg.DeferCounter = context.graph().newCounter(previousSize);
              for (let i: number = 0; i < previousSize; i++) {
                let loop: any = previousResult.get(i);
                if (loop instanceof org.mwg.plugin.AbstractNode) {
                  let casted: org.mwg.Node = <org.mwg.Node>loop;
                  casted.relByIndex(flatlongName, (result : org.mwg.Node[]) => {
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
            return "traverseById(\'" + this._name + "\')";
          }
        }
      }
      export module distance {
        export class CosineDistance implements org.mwg.structure.distance.Distance {
          private static static_instance: org.mwg.structure.distance.CosineDistance = null;
          public static instance(): org.mwg.structure.distance.CosineDistance {
            if (CosineDistance.static_instance == null) {
              CosineDistance.static_instance = new org.mwg.structure.distance.CosineDistance();
            }
            return CosineDistance.static_instance;
          }
          constructor() {}
          public measure(x: Float64Array, y: Float64Array): number {
            let sumTop: number = 0;
            let sumOne: number = 0;
            let sumTwo: number = 0;
            for (let i: number = 0; i < x.length; i++) {
              sumTop += x[i] * y[i];
              sumOne += x[i] * x[i];
              sumTwo += y[i] * y[i];
            }
            let cosSim: number = sumTop / (Math.sqrt(sumOne) * Math.sqrt(sumTwo));
            if (cosSim < 0) {
              cosSim = 0;
            }
            return 1 - cosSim;
          }
          public compare(x: number, y: number): boolean {
            return x < y;
          }
          public getMinValue(): number {
            return 0;
          }
          public getMaxValue(): number {
            return java.lang.Double.MAX_VALUE;
          }
        }
        export interface Distance {
          measure(x: Float64Array, y: Float64Array): number;
          compare(x: number, y: number): boolean;
          getMinValue(): number;
          getMaxValue(): number;
        }
        export class Distances {
          public static EUCLIDEAN: number = 0;
          public static GEODISTANCE: number = 1;
          public static COSINE: number = 2;
        }
        export class EuclideanDistance implements org.mwg.structure.distance.Distance {
          private static static_instance: org.mwg.structure.distance.EuclideanDistance = null;
          public static instance(): org.mwg.structure.distance.EuclideanDistance {
            if (EuclideanDistance.static_instance == null) {
              EuclideanDistance.static_instance = new org.mwg.structure.distance.EuclideanDistance();
            }
            return EuclideanDistance.static_instance;
          }
          constructor() {}
          public measure(x: Float64Array, y: Float64Array): number {
            let value: number = 0;
            for (let i: number = 0; i < x.length; i++) {
              value = value + (x[i] - y[i]) * (x[i] - y[i]);
            }
            return Math.sqrt(value);
          }
          public compare(x: number, y: number): boolean {
            return x < y;
          }
          public getMinValue(): number {
            return 0;
          }
          public getMaxValue(): number {
            return java.lang.Double.MAX_VALUE;
          }
        }
        export class GeoDistance implements org.mwg.structure.distance.Distance {
          private static static_instance: org.mwg.structure.distance.GeoDistance = null;
          public static instance(): org.mwg.structure.distance.GeoDistance {
            if (GeoDistance.static_instance == null) {
              GeoDistance.static_instance = new org.mwg.structure.distance.GeoDistance();
            }
            return GeoDistance.static_instance;
          }
          constructor() {}
          public measure(x: Float64Array, y: Float64Array): number {
            let earthRadius: number = 6371000;
            let dLat: number = org.mwg.structure.distance.GeoDistance.toRadians(y[0] - x[0]);
            let dLng: number = org.mwg.structure.distance.GeoDistance.toRadians(y[1] - x[1]);
            let a: number = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(org.mwg.structure.distance.GeoDistance.toRadians(x[0])) * Math.cos(org.mwg.structure.distance.GeoDistance.toRadians(y[0])) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
            let c: number = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return earthRadius * c;
          }
          private static toRadians(angledeg: number): number {
            return angledeg * Math.PI / 180;
          }
          public compare(x: number, y: number): boolean {
            return x < y;
          }
          public getMinValue(): number {
            return 0;
          }
          public getMaxValue(): number {
            return java.lang.Double.MAX_VALUE;
          }
        }
        export class PearsonDistance implements org.mwg.structure.distance.Distance {
          private static static_instance: org.mwg.structure.distance.PearsonDistance = null;
          public static instance(): org.mwg.structure.distance.PearsonDistance {
            if (PearsonDistance.static_instance == null) {
              PearsonDistance.static_instance = new org.mwg.structure.distance.PearsonDistance();
            }
            return PearsonDistance.static_instance;
          }
          constructor() {}
          public measure(a: Float64Array, b: Float64Array): number {
            let xy: number = 0, x: number = 0, x2: number = 0, y: number = 0, y2: number = 0;
            for (let i: number = 0; i < a.length; i++) {
              xy += a[i] * b[i];
              x += a[i];
              y += b[i];
              x2 += a[i] * a[i];
              y2 += b[i] * b[i];
            }
            let n: number = a.length;
            return (xy - (x * y) / n) / Math.sqrt((x2 - (x * x) / n) * (y2 - (y * y) / n));
          }
          public compare(x: number, y: number): boolean {
            return Math.abs(x) > Math.abs(y);
          }
          public getMinValue(): number {
            return 1;
          }
          public getMaxValue(): number {
            return 0;
          }
        }
      }
      export module tree {
        export class KDTree extends org.mwg.plugin.AbstractNode implements org.mwg.structure.NTree {
          public static NAME: string = "KDTree";
          public static FROM: string = "from";
          public static LEFT: string = "left";
          public static RIGHT: string = "right";
          public static KEY: string = "key";
          public static VALUE: string = "value";
          public static SIZE: string = "size";
          public static DIMENSIONS: string = "dimensions";
          public static DISTANCE: string = "distance";
          public static DISTANCE_THRESHOLD: string = "threshold";
          public static DISTANCE_THRESHOLD_DEF: number = 1e-10;
          public static DISTANCE_TYPE_DEF: number = 0;
          private static insert: org.mwg.task.Task = org.mwg.task.Actions.whileDo((context : org.mwg.task.TaskContext) => {
{
              let current: org.mwg.Node = context.resultAsNodes().get(0);
              let nodeKey: Float64Array = <Float64Array>current.get(org.mwg.structure.tree.KDTree.KEY);
              let dim: number = <number>context.variable("dim").get(0);
              let keyToInsert: Float64Array = <Float64Array>context.variable("key").get(0);
              let valueToInsert: org.mwg.Node = <org.mwg.Node>context.variable("value").get(0);
              let root: org.mwg.Node = <org.mwg.Node>context.variable("root").get(0);
              let distance: org.mwg.structure.distance.Distance = <org.mwg.structure.distance.Distance>context.variable("distance").get(0);
              let err: number = <number>context.variable("err").get(0);
              let lev: number = <number>context.variable("lev").get(0);
              if (nodeKey == null) {
                current.setProperty(org.mwg.structure.tree.KDTree.DIMENSIONS, org.mwg.Type.INT, dim);
                current.setProperty(org.mwg.structure.tree.KDTree.KEY, org.mwg.Type.DOUBLE_ARRAY, keyToInsert);
                current.getOrCreateRel(org.mwg.structure.tree.KDTree.VALUE).clear().add(valueToInsert.id());
                current.setProperty(org.mwg.structure.tree.KDTree.SIZE, org.mwg.Type.INT, 1);
                return false;
              } else if (distance.measure(keyToInsert, nodeKey) < err) {
                current.getOrCreateRel(org.mwg.structure.tree.KDTree.VALUE).clear().add(valueToInsert.id());
                return false;
              } else {
                let child: org.mwg.struct.Relationship;
                let nextRel: string;
                if (keyToInsert[lev] > nodeKey[lev]) {
                  child = <org.mwg.struct.Relationship>current.get(org.mwg.structure.tree.KDTree.RIGHT);
                  nextRel = org.mwg.structure.tree.KDTree.RIGHT;
                } else {
                  child = <org.mwg.struct.Relationship>current.get(org.mwg.structure.tree.KDTree.LEFT);
                  nextRel = org.mwg.structure.tree.KDTree.LEFT;
                }
                if (child == null || child.size() == 0) {
                  let childNode: org.mwg.structure.tree.KDTree = <org.mwg.structure.tree.KDTree>context.graph().newTypedNode(current.world(), current.time(), org.mwg.structure.tree.KDTree.NAME);
                  childNode.setProperty(org.mwg.structure.tree.KDTree.KEY, org.mwg.Type.DOUBLE_ARRAY, keyToInsert);
                  childNode.getOrCreateRel(org.mwg.structure.tree.KDTree.VALUE).clear().add(valueToInsert.id());
                  current.getOrCreateRel(nextRel).clear().add(childNode.id());
                  root.setProperty(org.mwg.structure.tree.KDTree.SIZE, org.mwg.Type.INT, <number>root.get(org.mwg.structure.tree.KDTree.SIZE) + 1);
                  childNode.free();
                  return false;
                } else {
                  context.setGlobalVariable("next", nextRel);
                  context.setGlobalVariable("lev", (lev + 1) % dim);
                  return true;
                }
              }

            }          }, org.mwg.task.Actions.traverse("{{next}}"));
          private static nearestTask: org.mwg.task.Task = KDTree.initFindNear();
          private static nearestRadiusTask: org.mwg.task.Task = KDTree.initFindRadius();
          private static enforcer: org.mwg.utility.Enforcer = new org.mwg.utility.Enforcer().asPositiveDouble(KDTree.DISTANCE_THRESHOLD);
          constructor(p_world: number, p_time: number, p_id: number, p_graph: org.mwg.Graph) {
            super(p_world, p_time, p_id, p_graph);
          }
          private static initFindNear(): org.mwg.task.Task {
            let reccursiveDown: org.mwg.task.Task = org.mwg.task.Actions.newTask();
            reccursiveDown.then((context : org.mwg.task.TaskContext) => {
{
                let node: org.mwg.Node = context.resultAsNodes().get(0);
                if (node == null) {
                  context.continueTask();
                  return;
                }
                let pivot: Float64Array = <Float64Array>node.get(org.mwg.structure.tree.KDTree.KEY);
                let dim: number = <number>context.variable("dim").get(0);
                let target: Float64Array = <Float64Array>context.variable("key").get(0);
                let distance: org.mwg.structure.distance.Distance = <org.mwg.structure.distance.Distance>context.variable("distance").get(0);
                let lev: number = <number>context.variable("lev").get(0);
                let hr: org.mwg.structure.util.HRect = <org.mwg.structure.util.HRect>context.variable("hr").get(0);
                let max_dist_sqd: number = <number>context.variable("max_dist_sqd").get(0);
                let s: number = lev % dim;
                let pivot_to_target: number = distance.measure(pivot, target);
                let left_hr: org.mwg.structure.util.HRect = hr;
                let right_hr: org.mwg.structure.util.HRect = <org.mwg.structure.util.HRect>hr.clone();
                left_hr.max[s] = pivot[s];
                right_hr.min[s] = pivot[s];
                let target_in_left: boolean = target[s] < pivot[s];
                let nearer_kd: org.mwg.struct.Relationship;
                let nearer_hr: org.mwg.structure.util.HRect;
                let further_kd: org.mwg.struct.Relationship;
                let further_hr: org.mwg.structure.util.HRect;
                let nearer_st: string;
                let farther_st: string;
                if (target_in_left) {
                  nearer_kd = <org.mwg.struct.Relationship>node.get(org.mwg.structure.tree.KDTree.LEFT);
                  nearer_st = org.mwg.structure.tree.KDTree.LEFT;
                  nearer_hr = left_hr;
                  further_kd = <org.mwg.struct.Relationship>node.get(org.mwg.structure.tree.KDTree.RIGHT);
                  further_hr = right_hr;
                  farther_st = org.mwg.structure.tree.KDTree.RIGHT;
                } else {
                  nearer_kd = <org.mwg.struct.Relationship>node.get(org.mwg.structure.tree.KDTree.RIGHT);
                  nearer_hr = right_hr;
                  nearer_st = org.mwg.structure.tree.KDTree.RIGHT;
                  further_kd = <org.mwg.struct.Relationship>node.get(org.mwg.structure.tree.KDTree.LEFT);
                  further_hr = left_hr;
                  farther_st = org.mwg.structure.tree.KDTree.LEFT;
                }
                context.defineVariable("further_hr", further_hr);
                context.defineVariable("pivot_to_target", pivot_to_target);
                if (nearer_kd != null && nearer_kd.size() != 0) {
                  context.defineVariable("near", nearer_st);
                  context.defineVariableForSubTask("hr", nearer_hr);
                  context.defineVariableForSubTask("max_dist_sqd", max_dist_sqd);
                  context.defineVariableForSubTask("lev", lev + 1);
                } else {
                  context.defineVariableForSubTask("near", context.newResult());
                }
                if (further_kd != null && further_kd.size() != 0) {
                  context.defineVariableForSubTask("far", farther_st);
                } else {
                  context.defineVariableForSubTask("far", context.newResult());
                }
                context.continueTask();
              }            }).isolate(org.mwg.task.Actions.ifThen((context : org.mwg.task.TaskContext) => {
{
                return context.variable("near").size() > 0;
              }            }, org.mwg.task.Actions.traverse("{{near}}").isolate(reccursiveDown))).then((context : org.mwg.task.TaskContext) => {
{
                let nnl: org.mwg.structure.util.NearestNeighborList = <org.mwg.structure.util.NearestNeighborList>context.variable("nnl").get(0);
                let target: Float64Array = <Float64Array>context.variable("key").get(0);
                let distance: org.mwg.structure.distance.Distance = <org.mwg.structure.distance.Distance>context.variable("distance").get(0);
                let max_dist_sqd: number = <number>context.variable("max_dist_sqd").get(0);
                let further_hr: org.mwg.structure.util.HRect = <org.mwg.structure.util.HRect>context.variable("further_hr").get(0);
                let pivot_to_target: number = <number>context.variable("pivot_to_target").get(0);
                let lev: number = <number>context.variable("lev").get(0);
                let node: org.mwg.Node = context.resultAsNodes().get(0);
                let dist_sqd: number;
                if (!nnl.isCapacityReached()) {
                  dist_sqd = java.lang.Double.MAX_VALUE;
                } else {
                  dist_sqd = nnl.getMaxPriority();
                }
                let max_dist_sqd2: number = Math.min(max_dist_sqd, dist_sqd);
                let closest: Float64Array = further_hr.closest(target);
                if (distance.measure(closest, target) < max_dist_sqd) {
                  if (pivot_to_target < dist_sqd) {
                    dist_sqd = pivot_to_target;
                    nnl.insert((<org.mwg.struct.Relationship>(node.get(org.mwg.structure.tree.KDTree.VALUE))).get(0), dist_sqd);
                    let pivot: Float64Array = <Float64Array>node.get(org.mwg.structure.tree.KDTree.KEY);
                    if (nnl.isCapacityReached()) {
                      max_dist_sqd2 = nnl.getMaxPriority();
                    } else {
                      max_dist_sqd2 = java.lang.Double.MAX_VALUE;
                    }
                  }
                  context.defineVariableForSubTask("hr", further_hr);
                  context.defineVariableForSubTask("max_dist_sqd", max_dist_sqd2);
                  context.defineVariableForSubTask("lev", lev + 1);
                  context.defineVariable("continueFar", true);
                } else {
                  context.defineVariable("continueFar", false);
                }
                context.continueTask();
              }            }).isolate(org.mwg.task.Actions.ifThen((context : org.mwg.task.TaskContext) => {
{
                return (<boolean>context.variable("continueFar").get(0) && context.variable("far").size() > 0);
              }            }, org.mwg.task.Actions.traverse("{{far}}").isolate(reccursiveDown)));
            return reccursiveDown;
          }
          private static initFindRadius(): org.mwg.task.Task {
            let reccursiveDown: org.mwg.task.Task = org.mwg.task.Actions.newTask();
            reccursiveDown.then((context : org.mwg.task.TaskContext) => {
{
                let node: org.mwg.Node = context.resultAsNodes().get(0);
                if (node == null) {
                  context.continueTask();
                  return;
                }
                let pivot: Float64Array = <Float64Array>node.get(org.mwg.structure.tree.KDTree.KEY);
                let dim: number = <number>context.variable("dim").get(0);
                let target: Float64Array = <Float64Array>context.variable("key").get(0);
                let distance: org.mwg.structure.distance.Distance = <org.mwg.structure.distance.Distance>context.variable("distance").get(0);
                let lev: number = <number>context.variable("lev").get(0);
                let hr: org.mwg.structure.util.HRect = <org.mwg.structure.util.HRect>context.variable("hr").get(0);
                let max_dist_sqd: number = <number>context.variable("max_dist_sqd").get(0);
                let s: number = lev % dim;
                let pivot_to_target: number = distance.measure(pivot, target);
                let left_hr: org.mwg.structure.util.HRect = hr;
                let right_hr: org.mwg.structure.util.HRect = <org.mwg.structure.util.HRect>hr.clone();
                left_hr.max[s] = pivot[s];
                right_hr.min[s] = pivot[s];
                let target_in_left: boolean = target[s] < pivot[s];
                let nearer_kd: org.mwg.struct.Relationship;
                let nearer_hr: org.mwg.structure.util.HRect;
                let further_kd: org.mwg.struct.Relationship;
                let further_hr: org.mwg.structure.util.HRect;
                let nearer_st: string;
                let farther_st: string;
                if (target_in_left) {
                  nearer_kd = <org.mwg.struct.Relationship>node.get(org.mwg.structure.tree.KDTree.LEFT);
                  nearer_st = org.mwg.structure.tree.KDTree.LEFT;
                  nearer_hr = left_hr;
                  further_kd = <org.mwg.struct.Relationship>node.get(org.mwg.structure.tree.KDTree.RIGHT);
                  further_hr = right_hr;
                  farther_st = org.mwg.structure.tree.KDTree.RIGHT;
                } else {
                  nearer_kd = <org.mwg.struct.Relationship>node.get(org.mwg.structure.tree.KDTree.RIGHT);
                  nearer_hr = right_hr;
                  nearer_st = org.mwg.structure.tree.KDTree.RIGHT;
                  further_kd = <org.mwg.struct.Relationship>node.get(org.mwg.structure.tree.KDTree.LEFT);
                  further_hr = left_hr;
                  farther_st = org.mwg.structure.tree.KDTree.LEFT;
                }
                context.defineVariable("further_hr", further_hr);
                context.defineVariable("pivot_to_target", pivot_to_target);
                if (nearer_kd != null && nearer_kd.size() != 0) {
                  context.defineVariable("near", nearer_st);
                  context.defineVariableForSubTask("hr", nearer_hr);
                  context.defineVariableForSubTask("max_dist_sqd", max_dist_sqd);
                  context.defineVariableForSubTask("lev", lev + 1);
                } else {
                  context.defineVariableForSubTask("near", context.newResult());
                }
                if (further_kd != null && further_kd.size() != 0) {
                  context.defineVariableForSubTask("far", farther_st);
                } else {
                  context.defineVariableForSubTask("far", context.newResult());
                }
                context.continueTask();
              }            }).isolate(org.mwg.task.Actions.ifThen((context : org.mwg.task.TaskContext) => {
{
                return context.variable("near").size() > 0;
              }            }, org.mwg.task.Actions.traverse("{{near}}").isolate(reccursiveDown))).then((context : org.mwg.task.TaskContext) => {
{
                let nnl: org.mwg.structure.util.NearestNeighborArrayList = <org.mwg.structure.util.NearestNeighborArrayList>context.variable("nnl").get(0);
                let target: Float64Array = <Float64Array>context.variable("key").get(0);
                let distance: org.mwg.structure.distance.Distance = <org.mwg.structure.distance.Distance>context.variable("distance").get(0);
                let radius: number = <number>context.variable("radius").get(0);
                let max_dist_sqd: number = <number>context.variable("max_dist_sqd").get(0);
                let further_hr: org.mwg.structure.util.HRect = <org.mwg.structure.util.HRect>context.variable("further_hr").get(0);
                let pivot_to_target: number = <number>context.variable("pivot_to_target").get(0);
                let lev: number = <number>context.variable("lev").get(0);
                let node: org.mwg.Node = context.resultAsNodes().get(0);
                let dist_sqd: number = java.lang.Double.MAX_VALUE;
                let max_dist_sqd2: number = Math.min(max_dist_sqd, dist_sqd);
                let closest: Float64Array = further_hr.closest(target);
                if (distance.measure(closest, target) < max_dist_sqd) {
                  if (pivot_to_target < dist_sqd) {
                    dist_sqd = pivot_to_target;
                    if (dist_sqd <= radius) {
                      nnl.insert((<org.mwg.struct.Relationship>(node.get(org.mwg.structure.tree.KDTree.VALUE))).get(0), dist_sqd);
                    }
                    max_dist_sqd2 = java.lang.Double.MAX_VALUE;
                  }
                  context.defineVariableForSubTask("hr", further_hr);
                  context.defineVariableForSubTask("max_dist_sqd", max_dist_sqd2);
                  context.defineVariableForSubTask("lev", lev + 1);
                  context.defineVariable("continueFar", true);
                } else {
                  context.defineVariable("continueFar", false);
                }
                context.continueTask();
              }            }).isolate(org.mwg.task.Actions.ifThen((context : org.mwg.task.TaskContext) => {
{
                return (<boolean>context.variable("continueFar").get(0) && context.variable("far").size() > 0);
              }            }, org.mwg.task.Actions.traverse("{{far}}").isolate(reccursiveDown)));
            return reccursiveDown;
          }
          public setProperty(propertyName: string, propertyType: number, propertyValue: any): void {
            KDTree.enforcer.check(propertyName, propertyType, propertyValue);
            super.setProperty(propertyName, propertyType, propertyValue);
          }
          public insert(value: org.mwg.Node, callback: org.mwg.Callback<boolean>): void {
            this.extractFeatures(value, (result : Float64Array) => {
{
                this.insertWith(result, value, callback);
              }            });
          }
          public insertWith(key: Float64Array, value: org.mwg.Node, callback: org.mwg.Callback<boolean>): void {
            let state: org.mwg.plugin.NodeState = this.unphasedState();
            let dim: number = state.getFromKeyWithDefault(KDTree.DIMENSIONS, key.length);
            let err: number = state.getFromKeyWithDefault(KDTree.DISTANCE_THRESHOLD, KDTree.DISTANCE_THRESHOLD_DEF);
            if (key.length != dim) {
              throw new Error("Key size should always be the same");
            }
            if (value == null) {
              throw new Error("To index node should not be null");
            }
            let distance: org.mwg.structure.distance.Distance = this.getDistance(state);
            let tc: org.mwg.task.TaskContext = KDTree.insert.prepareWith(this.graph(), this, (result : org.mwg.task.TaskResult<any>) => {
{
                result.free();
                if (callback != null) {
                  callback(true);
                }
              }            });
            let res: org.mwg.task.TaskResult<any> = tc.newResult();
            res.add(key);
            tc.setGlobalVariable("key", res);
            tc.setGlobalVariable("value", value);
            tc.setGlobalVariable("root", this);
            tc.setGlobalVariable("distance", distance);
            tc.setGlobalVariable("dim", dim);
            tc.setGlobalVariable("err", err);
            tc.defineVariable("lev", 0);
            KDTree.insert.executeUsing(tc);
          }
          public size(): number {
            return this.graph().resolver().resolveState(this).getFromKeyWithDefault(KDTree.SIZE, 0);
          }
          public setDistance(distanceType: number): void {
            this.set(KDTree.DISTANCE, distanceType);
          }
          public setFrom(extractor: string): void {
            this.set(KDTree.FROM, extractor);
          }
          public nearestNWithinRadius(key: Float64Array, n: number, radius: number, callback: org.mwg.Callback<org.mwg.Node[]>): void {
            let state: org.mwg.plugin.NodeState = this.unphasedState();
            let dim: number = state.getFromKeyWithDefault(KDTree.DIMENSIONS, key.length);
            if (key.length != dim) {
              throw new Error("Key size should always be the same");
            }
            let hr: org.mwg.structure.util.HRect = org.mwg.structure.util.HRect.infiniteHRect(key.length);
            let max_dist_sqd: number = java.lang.Double.MAX_VALUE;
            let nnl: org.mwg.structure.util.NearestNeighborList = new org.mwg.structure.util.NearestNeighborList(n);
            let distance: org.mwg.structure.distance.Distance = this.getDistance(state);
            let tc: org.mwg.task.TaskContext = KDTree.nearestTask.prepareWith(this.graph(), this, (result : org.mwg.task.TaskResult<any>) => {
{
                let res: Float64Array = nnl.getAllNodesWithin(radius);
                let lookupall: org.mwg.task.Task = org.mwg.task.Actions.setWorld(java.lang.String.valueOf(this.world())).setTime(java.lang.String.valueOf(this.time())).fromVar("res").flatmap(org.mwg.task.Actions.lookup("{{result}}"));
                let tc: org.mwg.task.TaskContext = lookupall.prepareWith(this.graph(), null, (result : org.mwg.task.TaskResult<any>) => {
{
                    let finalres: org.mwg.Node[] = new Array<org.mwg.Node>(result.size());
                    for (let i: number = 0; i < result.size(); i++) {
                      finalres[i] = <org.mwg.Node>result.get(i);
                    }
                    callback(finalres);
                  }                });
                let tr: org.mwg.task.TaskResult<any> = tc.wrap(res);
                tc.addToGlobalVariable("res", tr);
                lookupall.executeUsing(tc);
              }            });
            let res: org.mwg.task.TaskResult<any> = tc.newResult();
            res.add(key);
            tc.setGlobalVariable("key", res);
            tc.setGlobalVariable("distance", distance);
            tc.setGlobalVariable("dim", dim);
            tc.setGlobalVariable("nnl", nnl);
            tc.defineVariable("lev", 0);
            tc.defineVariable("hr", hr);
            tc.defineVariable("max_dist_sqd", max_dist_sqd);
            KDTree.nearestTask.executeUsing(tc);
          }
          public nearestWithinRadius(key: Float64Array, radius: number, callback: org.mwg.Callback<org.mwg.Node[]>): void {
            let state: org.mwg.plugin.NodeState = this.unphasedState();
            let dim: number = state.getFromKeyWithDefault(KDTree.DIMENSIONS, key.length);
            if (key.length != dim) {
              throw new Error("Key size should always be the same");
            }
            let hr: org.mwg.structure.util.HRect = org.mwg.structure.util.HRect.infiniteHRect(key.length);
            let max_dist_sqd: number = java.lang.Double.MAX_VALUE;
            let nnl: org.mwg.structure.util.NearestNeighborArrayList = new org.mwg.structure.util.NearestNeighborArrayList();
            let distance: org.mwg.structure.distance.Distance = this.getDistance(state);
            let tc: org.mwg.task.TaskContext = KDTree.nearestRadiusTask.prepareWith(this.graph(), this, (result : org.mwg.task.TaskResult<any>) => {
{
                let res: Float64Array = nnl.getAllNodes();
                let lookupall: org.mwg.task.Task = org.mwg.task.Actions.setWorld(java.lang.String.valueOf(this.world())).setTime(java.lang.String.valueOf(this.time())).fromVar("res").flatmap(org.mwg.task.Actions.lookup("{{result}}"));
                let tc: org.mwg.task.TaskContext = lookupall.prepareWith(this.graph(), null, (result : org.mwg.task.TaskResult<any>) => {
{
                    let finalres: org.mwg.Node[] = new Array<org.mwg.Node>(result.size());
                    for (let i: number = 0; i < result.size(); i++) {
                      finalres[i] = <org.mwg.Node>result.get(i);
                    }
                    callback(finalres);
                  }                });
                let tr: org.mwg.task.TaskResult<any> = tc.wrap(res);
                tc.addToGlobalVariable("res", tr);
                lookupall.executeUsing(tc);
              }            });
            let res: org.mwg.task.TaskResult<any> = tc.newResult();
            res.add(key);
            tc.setGlobalVariable("key", res);
            tc.setGlobalVariable("distance", distance);
            tc.setGlobalVariable("dim", dim);
            tc.setGlobalVariable("nnl", nnl);
            tc.setGlobalVariable("radius", radius);
            tc.defineVariable("lev", 0);
            tc.defineVariable("hr", hr);
            tc.defineVariable("max_dist_sqd", max_dist_sqd);
            KDTree.nearestRadiusTask.executeUsing(tc);
          }
          public nearestN(key: Float64Array, n: number, callback: org.mwg.Callback<org.mwg.Node[]>): void {
            let state: org.mwg.plugin.NodeState = this.unphasedState();
            let dim: number = state.getFromKeyWithDefault(KDTree.DIMENSIONS, key.length);
            if (key.length != dim) {
              throw new Error("Key size should always be the same");
            }
            let hr: org.mwg.structure.util.HRect = org.mwg.structure.util.HRect.infiniteHRect(key.length);
            let max_dist_sqd: number = java.lang.Double.MAX_VALUE;
            let nnl: org.mwg.structure.util.NearestNeighborList = new org.mwg.structure.util.NearestNeighborList(n);
            let distance: org.mwg.structure.distance.Distance = this.getDistance(state);
            let tc: org.mwg.task.TaskContext = KDTree.nearestTask.prepareWith(this.graph(), this, (result : org.mwg.task.TaskResult<any>) => {
{
                let res: Float64Array = nnl.getAllNodes();
                let lookupall: org.mwg.task.Task = org.mwg.task.Actions.setWorld(java.lang.String.valueOf(this.world())).setTime(java.lang.String.valueOf(this.time())).fromVar("res").flatmap(org.mwg.task.Actions.lookup("{{result}}"));
                let tc: org.mwg.task.TaskContext = lookupall.prepareWith(this.graph(), null, (result : org.mwg.task.TaskResult<any>) => {
{
                    let finalres: org.mwg.Node[] = new Array<org.mwg.Node>(result.size());
                    for (let i: number = 0; i < result.size(); i++) {
                      finalres[i] = <org.mwg.Node>result.get(i);
                    }
                    callback(finalres);
                  }                });
                let tr: org.mwg.task.TaskResult<any> = tc.wrap(res);
                tc.addToGlobalVariable("res", tr);
                lookupall.executeUsing(tc);
              }            });
            let res: org.mwg.task.TaskResult<any> = tc.newResult();
            res.add(key);
            tc.setGlobalVariable("key", res);
            tc.setGlobalVariable("distance", distance);
            tc.setGlobalVariable("dim", dim);
            tc.setGlobalVariable("nnl", nnl);
            tc.defineVariable("lev", 0);
            tc.defineVariable("hr", hr);
            tc.defineVariable("max_dist_sqd", max_dist_sqd);
            KDTree.nearestTask.executeUsing(tc);
          }
          public getDistance(state: org.mwg.plugin.NodeState): org.mwg.structure.distance.Distance {
            let d: number = state.getFromKeyWithDefault(KDTree.DISTANCE, KDTree.DISTANCE_TYPE_DEF);
            let distance: org.mwg.structure.distance.Distance;
            if (d == org.mwg.structure.distance.Distances.EUCLIDEAN) {
              distance = org.mwg.structure.distance.EuclideanDistance.instance();
            } else if (d == org.mwg.structure.distance.Distances.GEODISTANCE) {
              distance = org.mwg.structure.distance.GeoDistance.instance();
            } else {
              throw new Error("Unknown distance code metric");
            }

            return distance;
          }
          public extractFeatures(current: org.mwg.Node, callback: org.mwg.Callback<Float64Array>): void {
            let query: string = <string>super.get(KDTree.FROM);
            if (query != null) {
              let split: string[] = query.split(",");
              let tasks: org.mwg.task.Task[] = new Array<org.mwg.task.Task>(split.length);
              for (let i: number = 0; i < split.length; i++) {
                let t: org.mwg.task.Task = org.mwg.task.Actions.setWorld("" + this.world());
                t.setTime(this.time() + "");
                t.parse(split[i].trim());
                tasks[i] = t;
              }
              let result: Float64Array = new Float64Array(tasks.length);
              let waiter: org.mwg.DeferCounter = this.graph().newCounter(tasks.length);
              for (let i: number = 0; i < split.length; i++) {
                let initial: org.mwg.task.TaskResult<any> = org.mwg.task.Actions.newTask().emptyResult();
                initial.add(current);
                let capsule: org.mwg.Callback<number> = (i : number) => {
{
                    tasks[i].executeWith(this.graph(), initial, (currentResult : org.mwg.task.TaskResult<any>) => {
{
                        if (currentResult == null) {
                          result[i] = org.mwg.Constants.NULL_LONG;
                        } else {
                          result[i] = parseFloat(currentResult.get(0).toString());
                          currentResult.free();
                        }
                        waiter.count();
                      }                    });
                  }                };
                capsule(i);
              }
              waiter.then(() => {
{
                  callback(result);
                }              });
            } else {
              callback(null);
            }
          }
        }
        export class NDTree extends org.mwg.plugin.AbstractNode {
          public static NAME: string = "NDTree";
          private static _STAT: number = 0;
          private static _TOTAL: number = 1;
          private static _SUM: number = 2;
          private static _SUMSQ: number = 3;
          private static _BOUNDMIN: number = 6;
          private static _BOUNDMAX: number = 7;
          private static _VALUES: number = 8;
          private static _VALUES_STR: string = "8";
          private static _KEYS: number = 9;
          private static _KEYS_STR: string = "9";
          private static _PRECISION: number = 10;
          private static _NUMNODES: number = 11;
          private static _DIM: number = 12;
          public static STAT_DEF: boolean = false;
          public static BOUNDMIN: string = "boundmin";
          public static BOUNDMAX: string = "boundmax";
          public static PRECISION: string = "precision";
          public static _RELCONST: number = 16;
          private static insert: org.mwg.task.Task = org.mwg.task.Actions.whileDo((context : org.mwg.task.TaskContext) => {
{
              let root: org.mwg.Node = <org.mwg.Node>context.variable("root").get(0);
              let current: org.mwg.Node = context.resultAsNodes().get(0);
              let state: org.mwg.plugin.NodeState = current.graph().resolver().resolveState(current);
              let updateStat: boolean = <boolean>context.variable("updatestat").get(0);
              let boundMax: Float64Array = <Float64Array>state.get(org.mwg.structure.tree.NDTree._BOUNDMAX);
              let boundMin: Float64Array = <Float64Array>state.get(org.mwg.structure.tree.NDTree._BOUNDMIN);
              let centerKey: Float64Array = new Float64Array(boundMax.length);
              for (let i: number = 0; i < centerKey.length; i++) {
                centerKey[i] = (boundMax[i] + boundMin[i]) / 2;
              }
              let keyToInsert: Float64Array = <Float64Array>context.variable("key").get(0);
              let precision: Float64Array = <Float64Array>context.variable("precision").get(0);
              let dim: number = keyToInsert.length;
              if (updateStat) {
                org.mwg.structure.tree.NDTree.updateGaussian(state, keyToInsert);
              }
              let continueNavigation: boolean = false;
              for (let i: number = 0; i < dim; i++) {
                if (boundMax[i] - boundMin[i] > precision[i]) {
                  continueNavigation = true;
break;
                }
              }
              if (continueNavigation) {
                let traverseId: number = org.mwg.structure.tree.NDTree.getRelationId(centerKey, keyToInsert);
                if (state.get(traverseId) == null) {
                  let newBoundMin: Float64Array = new Float64Array(dim);
                  let newBoundMax: Float64Array = new Float64Array(dim);
                  for (let i: number = 0; i < centerKey.length; i++) {
                    if (keyToInsert[i] <= centerKey[i]) {
                      newBoundMin[i] = boundMin[i];
                      newBoundMax[i] = Math.max(centerKey[i] - boundMin[i], precision[i]) + boundMin[i];
                    } else {
                      newBoundMin[i] = boundMax[i] - Math.max(boundMax[i] - centerKey[i], precision[i]);
                      newBoundMax[i] = boundMax[i];
                    }
                  }
                  let newChild: org.mwg.structure.tree.NDTree = <org.mwg.structure.tree.NDTree>current.graph().newTypedNode(current.world(), current.time(), org.mwg.structure.tree.NDTree.NAME);
                  let newState: org.mwg.plugin.NodeState = newChild.graph().resolver().resolveState(newChild);
                  newState.set(org.mwg.structure.tree.NDTree._BOUNDMIN, org.mwg.Type.DOUBLE_ARRAY, newBoundMin);
                  newState.set(org.mwg.structure.tree.NDTree._BOUNDMAX, org.mwg.Type.DOUBLE_ARRAY, newBoundMax);
                  let relChild: org.mwg.struct.Relationship = <org.mwg.struct.Relationship>state.getOrCreate(traverseId, org.mwg.Type.RELATION);
                  relChild.add(newChild.id());
                  newChild.free();
                  if (root.getByIndex(org.mwg.structure.tree.NDTree._NUMNODES) != null) {
                    let count: number = <number>root.getByIndex(org.mwg.structure.tree.NDTree._NUMNODES);
                    count++;
                    root.setPropertyByIndex(org.mwg.structure.tree.NDTree._NUMNODES, org.mwg.Type.INT, count);
                  } else {
                    root.setPropertyByIndex(org.mwg.structure.tree.NDTree._NUMNODES, org.mwg.Type.INT, 2);
                  }
                }
                context.setVariable("next", traverseId);
              } else {
                let valueToInsert: org.mwg.Node = <org.mwg.Node>context.variable("value").get(0);
                let rel: org.mwg.struct.Relationship = <org.mwg.struct.Relationship>state.getOrCreate(org.mwg.structure.tree.NDTree._VALUES, org.mwg.Type.RELATION);
                rel.add(valueToInsert.id());
                let keys: Float64Array = <Float64Array>state.get(org.mwg.structure.tree.NDTree._KEYS);
                if (keys != null) {
                  let newkeys: Float64Array = new Float64Array(keys.length + dim);
                  java.lang.System.arraycopy(keys, 0, newkeys, 0, keys.length);
                  java.lang.System.arraycopy(keyToInsert, 0, newkeys, keys.length, dim);
                  state.set(org.mwg.structure.tree.NDTree._KEYS, org.mwg.Type.DOUBLE_ARRAY, newkeys);
                } else {
                  state.set(org.mwg.structure.tree.NDTree._KEYS, org.mwg.Type.DOUBLE_ARRAY, keyToInsert);
                }
              }
              return continueNavigation;
            }          }, org.mwg.task.Actions.action(org.mwg.structure.action.TraverseById.NAME, "{{next}}"));
          private static nearestTask: org.mwg.task.Task = NDTree.initNearestTask();
          constructor(p_world: number, p_time: number, p_id: number, p_graph: org.mwg.Graph) {
            super(p_world, p_time, p_id, p_graph);
          }
          public setUpdateStat(value: boolean): void {
            let state: org.mwg.plugin.NodeState = this.unphasedState();
            state.set(NDTree._STAT, org.mwg.Type.BOOL, value);
          }
          private static updateGaussian(state: org.mwg.plugin.NodeState, key: Float64Array): void {
            let total: number = 0;
            let x: number = <number>state.get(NDTree._TOTAL);
            if (x != null) {
              total = x;
            }
            if (total == 0) {
              state.set(NDTree._TOTAL, org.mwg.Type.INT, 1);
              state.set(NDTree._SUM, org.mwg.Type.DOUBLE_ARRAY, key);
            } else {
              let features: number = key.length;
              let sum: Float64Array;
              let sumsquares: Float64Array;
              if (total == 1) {
                sum = <Float64Array>state.get(NDTree._SUM);
                sumsquares = new Float64Array(features * (features + 1) / 2);
                let count: number = 0;
                for (let i: number = 0; i < features; i++) {
                  for (let j: number = i; j < features; j++) {
                    sumsquares[count] = sum[i] * sum[j];
                    count++;
                  }
                }
              } else {
                sum = <Float64Array>state.get(NDTree._SUM);
                sumsquares = <Float64Array>state.get(NDTree._SUMSQ);
              }
              for (let i: number = 0; i < features; i++) {
                sum[i] += key[i];
              }
              let count: number = 0;
              for (let i: number = 0; i < features; i++) {
                for (let j: number = i; j < features; j++) {
                  sumsquares[count] += key[i] * key[j];
                  count++;
                }
              }
              total++;
              state.set(NDTree._TOTAL, org.mwg.Type.INT, total);
              state.set(NDTree._SUM, org.mwg.Type.DOUBLE_ARRAY, sum);
              state.set(NDTree._SUMSQ, org.mwg.Type.DOUBLE_ARRAY, sumsquares);
            }
          }
          public getTotal(): number {
            let x: number = <number>super.getByIndex(NDTree._TOTAL);
            if (x == null) {
              return 0;
            } else {
              return x;
            }
          }
          public getAvg(): Float64Array {
            let total: number = this.getTotal();
            if (total == 0) {
              return null;
            }
            if (total == 1) {
              return <Float64Array>super.getByIndex(NDTree._SUM);
            } else {
              let avg: Float64Array = <Float64Array>super.getByIndex(NDTree._SUM);
              for (let i: number = 0; i < avg.length; i++) {
                avg[i] = avg[i] / total;
              }
              return avg;
            }
          }
          public getCovarianceArray(avg: Float64Array, err: Float64Array): Float64Array {
            if (avg == null) {
              let errClone: Float64Array = new Float64Array(err.length);
              java.lang.System.arraycopy(err, 0, errClone, 0, err.length);
              return errClone;
            }
            if (err == null) {
              err = new Float64Array(avg.length);
            }
            let features: number = avg.length;
            let total: number = this.getTotal();
            if (total == 0) {
              return null;
            }
            if (total > 1) {
              let covariances: Float64Array = new Float64Array(features);
              let sumsquares: Float64Array = <Float64Array>super.getByIndex(NDTree._SUMSQ);
              let correction: number = total;
              correction = correction / (total - 1);
              let count: number = 0;
              for (let i: number = 0; i < features; i++) {
                covariances[i] = (sumsquares[count] / total - avg[i] * avg[i]) * correction;
                if (covariances[i] < err[i]) {
                  covariances[i] = err[i];
                }
                count += features - i;
              }
              return covariances;
            } else {
              let errClone: Float64Array = new Float64Array(err.length);
              java.lang.System.arraycopy(err, 0, errClone, 0, err.length);
              return errClone;
            }
          }
          public static getRelationId(centerKey: Float64Array, keyToInsert: Float64Array): number {
            var result = Long.UZERO;
            for(var i = 0; i < centerKey.length; i++) {
            if(i!=0){
            result = result.shiftLeft(1);
            }
            if (keyToInsert[i] > centerKey[i]) {
            result = result.add(Long.ONE);
            }
            }
            return result.add(Long.fromNumber(org.mwg.structure.tree.NDTree._RELCONST, true)).toNumber();
          }
          public static binaryFromLong(value: number, dim: number): boolean[] {
            let tempvalue: number = value - NDTree._RELCONST;
            let shiftvalue: number = tempvalue >> 1;
            let res: boolean[] = [];
            for (let i: number = 0; i < dim; i++) {
              res[dim - i - 1] = ((tempvalue - (shiftvalue << 1)) == 1);
              tempvalue = shiftvalue;
              shiftvalue = tempvalue >> 1;
            }
            return res;
          }
          public setProperty(propertyName: string, propertyType: number, propertyValue: any): void {
            if (propertyName === NDTree.BOUNDMIN) {
              let state: org.mwg.plugin.NodeState = this.unphasedState();
              state.set(NDTree._BOUNDMIN, org.mwg.Type.DOUBLE_ARRAY, propertyValue);
            } else if (propertyName === NDTree.BOUNDMAX) {
              let state: org.mwg.plugin.NodeState = this.unphasedState();
              state.set(NDTree._BOUNDMAX, org.mwg.Type.DOUBLE_ARRAY, propertyValue);
            } else if (propertyName === NDTree.PRECISION) {
              let state: org.mwg.plugin.NodeState = this.unphasedState();
              state.set(NDTree._PRECISION, org.mwg.Type.DOUBLE_ARRAY, propertyValue);
            } else {
              super.setProperty(propertyName, propertyType, propertyValue);
            }


          }
          public insert(key: Float64Array, value: org.mwg.Node, callback: org.mwg.Callback<boolean>): void {
            let state: org.mwg.plugin.NodeState = this.unphasedState();
            let precisions: Float64Array = <Float64Array>state.get(NDTree._PRECISION);
            if (state.get(NDTree._DIM) == null) {
              state.set(NDTree._DIM, org.mwg.Type.INT, key.length);
            }
            let dim: number = state.getWithDefault(NDTree._DIM, key.length);
            if (key.length != dim) {
              throw new Error("Key size should always be the same");
            }
            let tc: org.mwg.task.TaskContext = NDTree.insert.prepareWith(this.graph(), this, (result : org.mwg.task.TaskResult<any>) => {
{
                result.free();
                if (callback != null) {
                  callback(true);
                }
              }            });
            let res: org.mwg.task.TaskResult<any> = tc.newResult();
            res.add(key);
            tc.setGlobalVariable("key", res);
            if (value != null) {
              tc.setGlobalVariable("value", value);
            }
            let updateStat: boolean = <boolean>state.getWithDefault(NDTree._STAT, NDTree.STAT_DEF);
            tc.setGlobalVariable("updatestat", updateStat);
            tc.setGlobalVariable("root", this);
            let resPres: org.mwg.task.TaskResult<any> = tc.newResult();
            resPres.add(precisions);
            tc.setGlobalVariable("precision", resPres);
            NDTree.insert.executeUsing(tc);
          }
          public nearestN(key: Float64Array, n: number, callback: org.mwg.Callback<org.mwg.Node[]>): void {
            let state: org.mwg.plugin.NodeState = this.unphasedState();
            let dim: number;
            let tdim: any = state.get(NDTree._DIM);
            if (tdim == null) {
              callback(null);
              return;
            } else {
              dim = <number>tdim;
              if (key.length != dim) {
                throw new Error("Key size should always be the same");
              }
            }
            let nnl: org.mwg.structure.util.NearestNeighborList = new org.mwg.structure.util.NearestNeighborList(n);
            let distance: org.mwg.structure.distance.Distance = org.mwg.structure.distance.EuclideanDistance.instance();
            let tc: org.mwg.task.TaskContext = NDTree.nearestTask.prepareWith(this.graph(), this, (result : org.mwg.task.TaskResult<any>) => {
{
                let res: Float64Array = nnl.getAllNodes();
                let lookupall: org.mwg.task.Task = org.mwg.task.Actions.setWorld(java.lang.String.valueOf(this.world())).setTime(java.lang.String.valueOf(this.time())).fromVar("res").flatmap(org.mwg.task.Actions.lookup("{{result}}"));
                let tc: org.mwg.task.TaskContext = lookupall.prepareWith(this.graph(), null, (result : org.mwg.task.TaskResult<any>) => {
{
                    let finalres: org.mwg.Node[] = new Array<org.mwg.Node>(result.size());
                    callback(finalres);
                  }                });
                let tr: org.mwg.task.TaskResult<any> = tc.wrap(res);
                tc.addToGlobalVariable("res", tr);
                lookupall.executeUsing(tc);
              }            });
            let res: org.mwg.task.TaskResult<any> = tc.newResult();
            res.add(key);
            tc.setGlobalVariable("key", res);
            tc.setGlobalVariable("distance", distance);
            tc.setGlobalVariable("dim", dim);
            tc.defineVariable("lev", 0);
            NDTree.nearestTask.executeUsing(tc);
          }
          public static convertToDistance(attributeKey: number, target: Float64Array, center: Float64Array, boundMin: Float64Array, boundMax: Float64Array, precision: Float64Array, distance: org.mwg.structure.distance.Distance): number {
            let childCenter: Float64Array = new Float64Array(center.length);
            let minchild: number = 0;
            let maxchild: number = 0;
            let binaries: boolean[] = org.mwg.structure.tree.NDTree.binaryFromLong(attributeKey, center.length);
            for (let i: number = 0; i < childCenter.length; i++) {
              if (!binaries[i]) {
                minchild = boundMin[i];
                maxchild = Math.max(center[i] - boundMin[i], precision[i]) + boundMin[i];
              } else {
                minchild = boundMax[i] - Math.max(boundMax[i] - center[i], precision[i]);
                maxchild = boundMax[i];
              }
              childCenter[i] = (minchild + maxchild) / 2;
            }
            return distance.measure(childCenter, target);
          }
          private static initNearestTask(): org.mwg.task.Task {
            let reccursiveDown: org.mwg.task.Task = org.mwg.task.Actions.newTask();
            reccursiveDown.then((context : org.mwg.task.TaskContext) => {
{
                let current: org.mwg.structure.tree.NDTree = <org.mwg.structure.tree.NDTree>context.result().get(0);
                let state: org.mwg.plugin.NodeState = current.graph().resolver().resolveState(current);
                let values: org.mwg.struct.Relationship = <org.mwg.struct.Relationship>state.get(org.mwg.structure.tree.NDTree._VALUES);
                if (values != null) {
                  let dim: number = <number>context.variable("dim").get(0);
                  let k: Float64Array = new Float64Array(dim);
                  let keys: Float64Array = <Float64Array>state.get(org.mwg.structure.tree.NDTree._KEYS);
                  let target: Float64Array = <Float64Array>context.variable("key").get(0);
                  let distance: org.mwg.structure.distance.Distance = <org.mwg.structure.distance.Distance>context.variable("distance").get(0);
                  let nnl: org.mwg.structure.util.NearestNeighborList = <org.mwg.structure.util.NearestNeighborList>context.variable("nnl").get(0);
                  for (let i: number = 0; i < values.size(); i++) {
                    for (let j: number = 0; j < dim; j++) {
                      k[j] = keys[i * dim + j];
                    }
                    nnl.insert(values.get(i), distance.measure(k, target));
                  }
                  context.continueWith(null);
                } else {
                  let boundMax: Float64Array = <Float64Array>state.get(org.mwg.structure.tree.NDTree._BOUNDMAX);
                  let boundMin: Float64Array = <Float64Array>state.get(org.mwg.structure.tree.NDTree._BOUNDMIN);
                  let target: Float64Array = <Float64Array>context.variable("key").get(0);
                  let distance: org.mwg.structure.distance.Distance = <org.mwg.structure.distance.Distance>context.variable("distance").get(0);
                }
              }            });
            return reccursiveDown;
          }
          public getNumNodes(): number {
            if (this.getByIndex(NDTree._NUMNODES) != null) {
              return <number>this.getByIndex(NDTree._NUMNODES);
            } else {
              return 1;
            }
          }
        }
      }
      export module util {
        export class HRect {
          public min: Float64Array;
          public max: Float64Array;
          constructor(vmin: Float64Array, vmax: Float64Array) {
            this.min = new Float64Array(vmin.length);
            this.max = new Float64Array(vmax.length);
            java.lang.System.arraycopy(vmin, 0, this.min, 0, vmin.length);
            java.lang.System.arraycopy(vmax, 0, this.max, 0, vmax.length);
          }
          public clone(): any {
            return new org.mwg.structure.util.HRect(this.min, this.max);
          }
          public closest(t: Float64Array): Float64Array {
            let p: Float64Array = new Float64Array(t.length);
            for (let i: number = 0; i < t.length; ++i) {
              if (t[i] <= this.min[i]) {
                p[i] = this.min[i];
              } else if (t[i] >= this.max[i]) {
                p[i] = this.max[i];
              } else {
                p[i] = t[i];
              }

            }
            return p;
          }
          public static infiniteHRect(d: number): org.mwg.structure.util.HRect {
            let vmin: Float64Array = new Float64Array(d);
            let vmax: Float64Array = new Float64Array(d);
            for (let i: number = 0; i < d; ++i) {
              vmin[i] = java.lang.Double.NEGATIVE_INFINITY;
              vmax[i] = java.lang.Double.POSITIVE_INFINITY;
            }
            return new org.mwg.structure.util.HRect(vmin, vmax);
          }
          public intersection(r: org.mwg.structure.util.HRect): org.mwg.structure.util.HRect {
            let newmin: Float64Array = new Float64Array(this.min.length);
            let newmax: Float64Array = new Float64Array(this.min.length);
            for (let i: number = 0; i < this.min.length; ++i) {
              newmin[i] = Math.max(this.min[i], r.min[i]);
              newmax[i] = Math.min(this.max[i], r.max[i]);
              if (newmin[i] >= newmax[i])
                return null;

            }
            return new org.mwg.structure.util.HRect(newmin, newmax);
          }
          public area(): number {
            let a: number = 1;
            for (let i: number = 0; i < this.min.length; ++i) {
              a *= (this.max[i] - this.min[i]);
            }
            return a;
          }
          public toString(): string {
            return this.min + "\n" + this.max + "\n";
          }
        }
        export class NDResult {
          public parent: org.mwg.structure.tree.NDTree;
          public relation: number;
          public distance: number;
        }
        export class NearestNeighborArrayList {
          private maxPriority: number = java.lang.Double.MAX_VALUE;
          private data: java.util.ArrayList<number>;
          private value: java.util.ArrayList<number>;
          private count: number;
          constructor() {
            this.count = 0;
            this.data = new java.util.ArrayList<number>();
            this.value = new java.util.ArrayList<number>();
            this.value.add(this.maxPriority);
            this.data.add(-1);
          }
          public getMaxPriority(): number {
            if (this.count == 0) {
              return java.lang.Double.POSITIVE_INFINITY;
            }
            return this.value.get(1);
          }
          public insert(node: number, priority: number): boolean {
            this.count++;
            this.value.add(priority);
            this.data.add(node);
            this.bubbleUp(this.count);
            return true;
          }
          public getAllNodes(): Float64Array {
            let size: number = this.count;
            let nbrs: Float64Array = new Float64Array(this.count);
            for (let i: number = 0; i < size; ++i) {
              nbrs[size - i - 1] = this.remove();
            }
            return nbrs;
          }
          public getHighest(): number {
            return this.data.get(1);
          }
          public getBestDistance(): number {
            return this.value.get(1);
          }
          public isEmpty(): boolean {
            return this.count == 0;
          }
          public getSize(): number {
            return this.count;
          }
          private remove(): number {
            if (this.count == 0)
              return 0;

            let element: number = this.data.get(1);
            this.data.set(1, this.data.get(this.count));
            this.value.set(1, this.value.get(this.count));
            this.data.set(this.count, 0);
            this.value.set(this.count, 0);
            this.count--;
            this.bubbleDown(1);
            return element;
          }
          private bubbleDown(pos: number): void {
            let element: number = this.data.get(pos);
            let priority: number = this.value.get(pos);
            let child: number;
            for (; pos * 2 <= this.count; pos = child) {
              child = pos * 2;
              if (child != this.count)
                if (this.value.get(child) < this.value.get(child + 1))
                  child++;


              if (priority < this.value.get(child)) {
                this.value.set(pos, this.value.get(child));
                this.data.set(pos, this.data.get(child));
              } else {
break;
              }
            }
            this.value.set(pos, priority);
            this.data.set(pos, element);
          }
          private bubbleUp(pos: number): void {
            let element: number = this.data.get(pos);
            let priority: number = this.value.get(pos);
            let halfpos: number = <number>Math.floor(pos / 2);
            while (this.value.get(halfpos) < priority) {
              this.value.set(pos, this.value.get(halfpos));
              this.data.set(pos, this.data.get(halfpos));
              pos = <number>Math.floor(pos / 2);
              halfpos = <number>Math.floor(pos / 2);
            }
            this.value.set(pos, priority);
            this.data.set(pos, element);
          }
        }
        export class NearestNeighborList {
          private maxPriority: number = java.lang.Double.MAX_VALUE;
          private data: Float64Array;
          private value: Float64Array;
          private count: number;
          private capacity: number;
          constructor(capacity: number) {
            this.count = 0;
            this.capacity = capacity;
            this.data = new Float64Array(capacity + 1);
            this.value = new Float64Array(capacity + 1);
            this.value[0] = this.maxPriority;
          }
          public getMaxPriority(): number {
            if (this.count == 0) {
              return java.lang.Double.POSITIVE_INFINITY;
            }
            return this.value[1];
          }
          public removeNode(node: number): boolean {
            let pos: number = -1;
            for (let i: number = 1; i < this.capacity + 1; i++) {
              if (this.data[i] == node) {
                pos = i;
break;
              }
            }
            if (pos == -1) {
              return false;
            } else if (pos == 1) {
              this.remove();
            } else if (pos == this.capacity + 1) {
              this.data[pos] = 0;
              this.value[pos] = 0;
              this.count--;
            } else {
              for (let i: number = pos; i < this.capacity; i++) {
                this.data[i] = this.data[i + 1];
                this.value[i] = this.value[i + 1];
              }
              this.count--;
            }


            return true;
          }
          public insert(node: number, priority: number): boolean {
            if (this.count < this.capacity) {
              this.add(node, priority);
              return true;
            }
            if (priority > this.getMaxPriority()) {
              return false;
            }
            this.remove();
            this.add(node, priority);
            return true;
          }
          private print(): void {
            console.log(" ");
            console.log("keys: ");
            for (let i: number = 0; i < this.data.length; i++) {
              console.log(this.data[i]);
            }
            console.log(" ");
            console.log("dist: ");
            for (let i: number = 0; i < this.value.length; i++) {
              console.log(this.value[i]);
            }
            console.log(" ");
          }
          public getAllNodes(): Float64Array {
            let size: number = Math.min(this.capacity, this.count);
            let nbrs: Float64Array = new Float64Array(size);
            for (let i: number = 0; i < size; ++i) {
              nbrs[size - i - 1] = this.remove();
            }
            return nbrs;
          }
          public isCapacityReached(): boolean {
            return this.count >= this.capacity;
          }
          public getHighest(): number {
            return this.data[1];
          }
          public getWorstDistance(): number {
            return this.value[1];
          }
          public isEmpty(): boolean {
            return this.count == 0;
          }
          public getSize(): number {
            return this.count;
          }
          private add(element: number, priority: number): void {
            if (this.count++ >= this.capacity) {
              this.expandCapacity();
            }
            this.value[this.count] = priority;
            this.data[this.count] = element;
            this.bubbleUp(this.count);
          }
          private remove(): number {
            if (this.count == 0)
              return 0;

            let element: number = this.data[1];
            this.data[1] = this.data[this.count];
            this.value[1] = this.value[this.count];
            this.data[this.count] = 0;
            this.value[this.count] = 0;
            this.count--;
            this.bubbleDown(1);
            return element;
          }
          private bubbleDown(pos: number): void {
            let element: number = this.data[pos];
            let priority: number = this.value[pos];
            let child: number;
            for (; pos * 2 <= this.count; pos = child) {
              child = pos * 2;
              if (child != this.count)
                if (this.value[child] < this.value[child + 1])
                  child++;


              if (priority < this.value[child]) {
                this.value[pos] = this.value[child];
                this.data[pos] = this.data[child];
              } else {
break;
              }
            }
            this.value[pos] = priority;
            this.data[pos] = element;
          }
          private bubbleUp(pos: number): void {
            let element: number = this.data[pos];
            let priority: number = this.value[pos];
            let halfpos: number = <number>Math.floor(pos / 2);
            while (this.value[halfpos] < priority) {
              this.value[pos] = this.value[halfpos];
              this.data[pos] = this.data[halfpos];
              pos = <number>Math.floor(pos / 2);
              halfpos = <number>Math.floor(pos / 2);
            }
            this.value[pos] = priority;
            this.data[pos] = element;
          }
          private expandCapacity(): void {
            this.capacity = this.count * 2;
            let elements: Float64Array = new Float64Array(this.capacity + 1);
            let prioritys: Float64Array = new Float64Array(this.capacity + 1);
            java.lang.System.arraycopy(this.data, 0, elements, 0, this.data.length);
            java.lang.System.arraycopy(this.value, 0, prioritys, 0, this.data.length);
            this.data = elements;
            this.value = prioritys;
          }
          public getAllNodesWithin(radius: number): Float64Array {
            let size: number = Math.min(this.capacity, this.count);
            let nbrs: Float64Array = new Float64Array(size);
            let cc: number = 0;
            for (let i: number = 0; i < size; ++i) {
              if (this.getMaxPriority() <= radius) {
                nbrs[size - cc - 1] = this.remove();
                cc++;
              } else {
                this.remove();
              }
            }
            let trimnbrs: Float64Array = new Float64Array(cc);
            java.lang.System.arraycopy(nbrs, size - cc, trimnbrs, 0, cc);
            return trimnbrs;
          }
        }
      }
    }
  }
}
