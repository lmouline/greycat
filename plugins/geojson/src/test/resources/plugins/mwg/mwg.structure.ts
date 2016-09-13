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
            return new org.mwg.structure.tree.KDTree(world, time, id, graph);
          });
          this.declareTaskAction(org.mwg.structure.action.NTreeInsertTo.NAME, (params : string[]) => {
            if (params.length != 1) {
              throw new Error("Bad param number!");
            }
            return new org.mwg.structure.action.NTreeInsertTo(params[0]);
          });
          this.declareTaskAction(org.mwg.structure.action.TraverseById.NAME, (params : string[]) => {
            if (params.length != 1) {
              throw new Error("Bad param number!");
            }
            return new org.mwg.structure.action.TraverseById(params[0]);
          });
          this.declareTaskAction(org.mwg.structure.action.NTreeNearestN.NAME, (params : string[]) => {
            if (params.length < 2) {
              throw new Error("Bad param number!");
            }
            var n: number = java.lang.Integer.parseInt(params[params.length - 1]);
            var key: Float64Array = new Float64Array(params.length - 1);
            for (var i: number = 0; i < params.length - 1; i++) {
              key[i] = parseFloat(params[i]);
            }
            return new org.mwg.structure.action.NTreeNearestN(key, n);
          });
          this.declareTaskAction(org.mwg.structure.action.NTreeNearestWithinRadius.NAME, (params : string[]) => {
            if (params.length < 2) {
              throw new Error("Bad param number!");
            }
            var radius: number = parseFloat(params[params.length - 1]);
            var key: Float64Array = new Float64Array(params.length - 1);
            for (var i: number = 0; i < params.length - 1; i++) {
              key[i] = parseFloat(params[i]);
            }
            return new org.mwg.structure.action.NTreeNearestWithinRadius(key, radius);
          });
          this.declareTaskAction(org.mwg.structure.action.NTreeNearestNWithinRadius.NAME, (params : string[]) => {
            if (params.length < 3) {
              throw new Error("Bad param number!");
            }
            var radius: number = parseFloat(params[params.length - 1]);
            var n: number = java.lang.Integer.parseInt(params[params.length - 2]);
            var key: Float64Array = new Float64Array(params.length - 2);
            for (var i: number = 0; i < params.length - 2; i++) {
              key[i] = parseFloat(params[i]);
            }
            return new org.mwg.structure.action.NTreeNearestNWithinRadius(key, n, radius);
          });
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
            var previousResult: org.mwg.task.TaskResult<any> = context.result();
            var savedVar: org.mwg.task.TaskResult<any> = context.variable(context.template(this._variableName));
            if (previousResult != null && savedVar != null) {
              var defer: org.mwg.DeferCounter = context.graph().newCounter(previousResult.size());
              var previousResultIt: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
              var iter: any = previousResultIt.next();
              while (iter != null){
                if (iter instanceof org.mwg.plugin.AbstractNode) {
                  var savedVarIt: org.mwg.task.TaskResultIterator<any> = savedVar.iterator();
                  var toAddIter: any = savedVarIt.next();
                  while (toAddIter != null){
                    if (toAddIter instanceof org.mwg.structure.tree.KDTree) {
                      (<org.mwg.structure.NTree>toAddIter).insert(<org.mwg.Node>iter, (result : boolean) => {
                        defer.count();
                      });
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
                context.continueTask();
              });
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
            var previousResult: org.mwg.task.TaskResult<any> = context.result();
            var nextResult: org.mwg.task.TaskResult<any> = context.newResult();
            if (previousResult != null) {
              var defer: org.mwg.DeferCounter = context.graph().newCounter(previousResult.size());
              var previousResultIt: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
              var iter: any = previousResultIt.next();
              while (iter != null){
                if (iter instanceof org.mwg.structure.tree.KDTree) {
                  (<org.mwg.structure.NTree>iter).nearestN(this._key, this._n, (result : org.mwg.Node[]) => {
                    for (var i: number = 0; i < result.length; i++) {
                      nextResult.add(result[i]);
                    }
                    defer.count();
                  });
                } else {
                  defer.count();
                }
                iter = previousResultIt.next();
              }
              defer.then(() => {
                context.continueWith(nextResult);
              });
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
            var previousResult: org.mwg.task.TaskResult<any> = context.result();
            var nextResult: org.mwg.task.TaskResult<org.mwg.Node> = context.newResult();
            if (previousResult != null) {
              var defer: org.mwg.DeferCounter = context.graph().newCounter(previousResult.size());
              var previousResultIt: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
              var iter: any = previousResultIt.next();
              while (iter != null){
                if (iter instanceof org.mwg.structure.tree.KDTree) {
                  (<org.mwg.structure.NTree>iter).nearestNWithinRadius(this._key, this._n, this._radius, (result : org.mwg.Node[]) => {
                    for (var i: number = 0; i < result.length; i++) {
                      nextResult.add(result[i]);
                    }
                    defer.count();
                  });
                } else {
                  defer.count();
                }
                iter = previousResultIt.next();
              }
              defer.then(() => {
                context.continueWith(nextResult);
              });
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
            var previousResult: org.mwg.task.TaskResult<any> = context.result();
            var nextResult: org.mwg.task.TaskResult<org.mwg.Node> = context.newResult();
            if (previousResult != null) {
              var defer: org.mwg.DeferCounter = context.graph().newCounter(previousResult.size());
              var previousResultIt: org.mwg.task.TaskResultIterator<any> = previousResult.iterator();
              var iter: any = previousResultIt.next();
              while (iter != null){
                if (iter instanceof org.mwg.structure.tree.KDTree) {
                  (<org.mwg.structure.NTree>iter).nearestWithinRadius(this._key, this._radius, (result : org.mwg.Node[]) => {
                    for (var i: number = 0; i < result.length; i++) {
                      nextResult.add(result[i]);
                    }
                    defer.count();
                  });
                } else {
                  defer.count();
                }
                iter = previousResultIt.next();
              }
              defer.then(() => {
                context.continueWith(nextResult);
              });
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
            var finalResult: org.mwg.task.TaskResult<any> = context.wrap(null);
            var flatlongName: number = java.lang.Long.parseLong(context.template(this._name));
            var previousResult: org.mwg.task.TaskResult<any> = context.result();
            if (previousResult != null) {
              var previousSize: number = previousResult.size();
              var defer: org.mwg.DeferCounter = context.graph().newCounter(previousSize);
              for (var i: number = 0; i < previousSize; i++) {
                var loop: any = previousResult.get(i);
                if (loop instanceof org.mwg.plugin.AbstractNode) {
                  var casted: org.mwg.Node = <org.mwg.Node>loop;
                  casted.relByIndex(flatlongName, (result : org.mwg.Node[]) => {
                    if (result != null) {
                      for (var j: number = 0; j < result.length; j++) {
                        finalResult.add(result[j]);
                      }
                    }
                    casted.free();
                    defer.count();
                  });
                } else {
                  finalResult.add(loop);
                  defer.count();
                }
              }
              defer.then(() => {
                previousResult.clear();
                context.continueWith(finalResult);
              });
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
            var sumTop: number = 0;
            var sumOne: number = 0;
            var sumTwo: number = 0;
            for (var i: number = 0; i < x.length; i++) {
              sumTop += x[i] * y[i];
              sumOne += x[i] * x[i];
              sumTwo += y[i] * y[i];
            }
            var cosSim: number = sumTop / (Math.sqrt(sumOne) * Math.sqrt(sumTwo));
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
            var value: number = 0;
            for (var i: number = 0; i < x.length; i++) {
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
            var earthRadius: number = 6371000;
            var dLat: number = org.mwg.structure.distance.GeoDistance.toRadians(y[0] - x[0]);
            var dLng: number = org.mwg.structure.distance.GeoDistance.toRadians(y[1] - x[1]);
            var a: number = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(org.mwg.structure.distance.GeoDistance.toRadians(x[0])) * Math.cos(org.mwg.structure.distance.GeoDistance.toRadians(y[0])) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
            var c: number = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
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
            var xy: number = 0, x: number = 0, x2: number = 0, y: number = 0, y2: number = 0;
            for (var i: number = 0; i < a.length; i++) {
              xy += a[i] * b[i];
              x += a[i];
              y += b[i];
              x2 += a[i] * a[i];
              y2 += b[i] * b[i];
            }
            var n: number = a.length;
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
            var current: org.mwg.Node = context.resultAsNodes().get(0);
            var nodeKey: Float64Array = <Float64Array>current.get(org.mwg.structure.tree.KDTree.KEY);
            var dim: number = <number>context.variable("dim").get(0);
            var keyToInsert: Float64Array = <Float64Array>context.variable("key").get(0);
            var valueToInsert: org.mwg.Node = <org.mwg.Node>context.variable("value").get(0);
            var root: org.mwg.Node = <org.mwg.Node>context.variable("root").get(0);
            var distance: org.mwg.structure.distance.Distance = <org.mwg.structure.distance.Distance>context.variable("distance").get(0);
            var err: number = <number>context.variable("err").get(0);
            var lev: number = <number>context.variable("lev").get(0);
            if (nodeKey == null) {
              current.setProperty(org.mwg.structure.tree.KDTree.DIMENSIONS, org.mwg.Type.INT, dim);
              current.setProperty(org.mwg.structure.tree.KDTree.KEY, org.mwg.Type.DOUBLE_ARRAY, keyToInsert);
              current.getOrCreateRel(org.mwg.structure.tree.KDTree.VALUE).clear().add(valueToInsert.id());
              current.setProperty(org.mwg.structure.tree.KDTree.SIZE, org.mwg.Type.INT, 1);
              return false;
            } else {
              if (distance.measure(keyToInsert, nodeKey) < err) {
                current.getOrCreateRel(org.mwg.structure.tree.KDTree.VALUE).clear().add(valueToInsert.id());
                return false;
              } else {
                var child: org.mwg.struct.Relationship;
                var nextRel: string;
                if (keyToInsert[lev] > nodeKey[lev]) {
                  child = <org.mwg.struct.Relationship>current.get(org.mwg.structure.tree.KDTree.RIGHT);
                  nextRel = org.mwg.structure.tree.KDTree.RIGHT;
                } else {
                  child = <org.mwg.struct.Relationship>current.get(org.mwg.structure.tree.KDTree.LEFT);
                  nextRel = org.mwg.structure.tree.KDTree.LEFT;
                }
                if (child == null || child.size() == 0) {
                  var childNode: org.mwg.structure.tree.KDTree = <org.mwg.structure.tree.KDTree>context.graph().newTypedNode(current.world(), current.time(), org.mwg.structure.tree.KDTree.NAME);
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
            }
          }, org.mwg.task.Actions.traverse("{{next}}"));
          private static nearestTask: org.mwg.task.Task = KDTree.initFindNear();
          private static nearestRadiusTask: org.mwg.task.Task = KDTree.initFindRadius();
          private static enforcer: org.mwg.utility.Enforcer = new org.mwg.utility.Enforcer().asPositiveDouble(KDTree.DISTANCE_THRESHOLD);
          constructor(p_world: number, p_time: number, p_id: number, p_graph: org.mwg.Graph) {
            super(p_world, p_time, p_id, p_graph);
          }
          private static initFindNear(): org.mwg.task.Task {
            var reccursiveDown: org.mwg.task.Task = org.mwg.task.Actions.newTask();
            reccursiveDown.then((context : org.mwg.task.TaskContext) => {
              var node: org.mwg.Node = context.resultAsNodes().get(0);
              if (node == null) {
                context.continueTask();
                return;
              }
              var pivot: Float64Array = <Float64Array>node.get(org.mwg.structure.tree.KDTree.KEY);
              var dim: number = <number>context.variable("dim").get(0);
              var target: Float64Array = <Float64Array>context.variable("key").get(0);
              var distance: org.mwg.structure.distance.Distance = <org.mwg.structure.distance.Distance>context.variable("distance").get(0);
              var lev: number = <number>context.variable("lev").get(0);
              var hr: org.mwg.structure.util.HRect = <org.mwg.structure.util.HRect>context.variable("hr").get(0);
              var max_dist_sqd: number = <number>context.variable("max_dist_sqd").get(0);
              var s: number = lev % dim;
              var pivot_to_target: number = distance.measure(pivot, target);
              var left_hr: org.mwg.structure.util.HRect = hr;
              var right_hr: org.mwg.structure.util.HRect = <org.mwg.structure.util.HRect>hr.clone();
              left_hr.max[s] = pivot[s];
              right_hr.min[s] = pivot[s];
              var target_in_left: boolean = target[s] < pivot[s];
              var nearer_kd: org.mwg.struct.Relationship;
              var nearer_hr: org.mwg.structure.util.HRect;
              var further_kd: org.mwg.struct.Relationship;
              var further_hr: org.mwg.structure.util.HRect;
              var nearer_st: string;
              var farther_st: string;
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
            }).isolate(org.mwg.task.Actions.ifThen((context : org.mwg.task.TaskContext) => {
              return context.variable("near").size() > 0;
            }, org.mwg.task.Actions.traverse("{{near}}").isolate(reccursiveDown))).then((context : org.mwg.task.TaskContext) => {
              var nnl: org.mwg.structure.util.NearestNeighborList = <org.mwg.structure.util.NearestNeighborList>context.variable("nnl").get(0);
              var target: Float64Array = <Float64Array>context.variable("key").get(0);
              var distance: org.mwg.structure.distance.Distance = <org.mwg.structure.distance.Distance>context.variable("distance").get(0);
              var max_dist_sqd: number = <number>context.variable("max_dist_sqd").get(0);
              var further_hr: org.mwg.structure.util.HRect = <org.mwg.structure.util.HRect>context.variable("further_hr").get(0);
              var pivot_to_target: number = <number>context.variable("pivot_to_target").get(0);
              var lev: number = <number>context.variable("lev").get(0);
              var node: org.mwg.Node = context.resultAsNodes().get(0);
              var dist_sqd: number;
              if (!nnl.isCapacityReached()) {
                dist_sqd = java.lang.Double.MAX_VALUE;
              } else {
                dist_sqd = nnl.getMaxPriority();
              }
              var max_dist_sqd2: number = Math.min(max_dist_sqd, dist_sqd);
              var closest: Float64Array = further_hr.closest(target);
              if (distance.measure(closest, target) < max_dist_sqd) {
                if (pivot_to_target < dist_sqd) {
                  dist_sqd = pivot_to_target;
                  nnl.insert((<org.mwg.struct.Relationship>(node.get(org.mwg.structure.tree.KDTree.VALUE))).get(0), dist_sqd);
                  var pivot: Float64Array = <Float64Array>node.get(org.mwg.structure.tree.KDTree.KEY);
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
            }).isolate(org.mwg.task.Actions.ifThen((context : org.mwg.task.TaskContext) => {
              return (<boolean>context.variable("continueFar").get(0) && context.variable("far").size() > 0);
            }, org.mwg.task.Actions.traverse("{{far}}").isolate(reccursiveDown)));
            return reccursiveDown;
          }
          private static initFindRadius(): org.mwg.task.Task {
            var reccursiveDown: org.mwg.task.Task = org.mwg.task.Actions.newTask();
            reccursiveDown.then((context : org.mwg.task.TaskContext) => {
              var node: org.mwg.Node = context.resultAsNodes().get(0);
              if (node == null) {
                context.continueTask();
                return;
              }
              var pivot: Float64Array = <Float64Array>node.get(org.mwg.structure.tree.KDTree.KEY);
              var dim: number = <number>context.variable("dim").get(0);
              var target: Float64Array = <Float64Array>context.variable("key").get(0);
              var distance: org.mwg.structure.distance.Distance = <org.mwg.structure.distance.Distance>context.variable("distance").get(0);
              var lev: number = <number>context.variable("lev").get(0);
              var hr: org.mwg.structure.util.HRect = <org.mwg.structure.util.HRect>context.variable("hr").get(0);
              var max_dist_sqd: number = <number>context.variable("max_dist_sqd").get(0);
              var s: number = lev % dim;
              var pivot_to_target: number = distance.measure(pivot, target);
              var left_hr: org.mwg.structure.util.HRect = hr;
              var right_hr: org.mwg.structure.util.HRect = <org.mwg.structure.util.HRect>hr.clone();
              left_hr.max[s] = pivot[s];
              right_hr.min[s] = pivot[s];
              var target_in_left: boolean = target[s] < pivot[s];
              var nearer_kd: org.mwg.struct.Relationship;
              var nearer_hr: org.mwg.structure.util.HRect;
              var further_kd: org.mwg.struct.Relationship;
              var further_hr: org.mwg.structure.util.HRect;
              var nearer_st: string;
              var farther_st: string;
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
            }).isolate(org.mwg.task.Actions.ifThen((context : org.mwg.task.TaskContext) => {
              return context.variable("near").size() > 0;
            }, org.mwg.task.Actions.traverse("{{near}}").isolate(reccursiveDown))).then((context : org.mwg.task.TaskContext) => {
              var nnl: org.mwg.structure.util.NearestNeighborArrayList = <org.mwg.structure.util.NearestNeighborArrayList>context.variable("nnl").get(0);
              var target: Float64Array = <Float64Array>context.variable("key").get(0);
              var distance: org.mwg.structure.distance.Distance = <org.mwg.structure.distance.Distance>context.variable("distance").get(0);
              var radius: number = <number>context.variable("radius").get(0);
              var max_dist_sqd: number = <number>context.variable("max_dist_sqd").get(0);
              var further_hr: org.mwg.structure.util.HRect = <org.mwg.structure.util.HRect>context.variable("further_hr").get(0);
              var pivot_to_target: number = <number>context.variable("pivot_to_target").get(0);
              var lev: number = <number>context.variable("lev").get(0);
              var node: org.mwg.Node = context.resultAsNodes().get(0);
              var dist_sqd: number = java.lang.Double.MAX_VALUE;
              var max_dist_sqd2: number = Math.min(max_dist_sqd, dist_sqd);
              var closest: Float64Array = further_hr.closest(target);
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
            }).isolate(org.mwg.task.Actions.ifThen((context : org.mwg.task.TaskContext) => {
              return (<boolean>context.variable("continueFar").get(0) && context.variable("far").size() > 0);
            }, org.mwg.task.Actions.traverse("{{far}}").isolate(reccursiveDown)));
            return reccursiveDown;
          }
          public setProperty(propertyName: string, propertyType: number, propertyValue: any): void {
            KDTree.enforcer.check(propertyName, propertyType, propertyValue);
            super.setProperty(propertyName, propertyType, propertyValue);
          }
          public insert(value: org.mwg.Node, callback: org.mwg.Callback<boolean>): void {
            this.extractFeatures(value, (result : Float64Array) => {
              this.insertWith(result, value, callback);
            });
          }
          public insertWith(key: Float64Array, value: org.mwg.Node, callback: org.mwg.Callback<boolean>): void {
            var state: org.mwg.plugin.NodeState = this.unphasedState();
            var dim: number = state.getFromKeyWithDefault(KDTree.DIMENSIONS, key.length);
            var err: number = state.getFromKeyWithDefault(KDTree.DISTANCE_THRESHOLD, KDTree.DISTANCE_THRESHOLD_DEF);
            if (key.length != dim) {
              throw new Error("Key size should always be the same");
            }
            if (value == null) {
              throw new Error("To index node should not be null");
            }
            var distance: org.mwg.structure.distance.Distance = this.getDistance(state);
            var tc: org.mwg.task.TaskContext = KDTree.insert.prepareWith(this.graph(), this, (result : org.mwg.task.TaskResult<any>) => {
              result.free();
              if (callback != null) {
                callback(true);
              }
            });
            var res: org.mwg.task.TaskResult<any> = tc.newResult();
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
            var state: org.mwg.plugin.NodeState = this.unphasedState();
            var dim: number = state.getFromKeyWithDefault(KDTree.DIMENSIONS, key.length);
            if (key.length != dim) {
              throw new Error("Key size should always be the same");
            }
            var hr: org.mwg.structure.util.HRect = org.mwg.structure.util.HRect.infiniteHRect(key.length);
            var max_dist_sqd: number = java.lang.Double.MAX_VALUE;
            var nnl: org.mwg.structure.util.NearestNeighborList = new org.mwg.structure.util.NearestNeighborList(n);
            var distance: org.mwg.structure.distance.Distance = this.getDistance(state);
            var tc: org.mwg.task.TaskContext = KDTree.nearestTask.prepareWith(this.graph(), this, (result : org.mwg.task.TaskResult<any>) => {
              var res: Float64Array = nnl.getAllNodesWithin(radius);
              var lookupall: org.mwg.task.Task = org.mwg.task.Actions.setWorld(java.lang.String.valueOf(this.world())).setTime(java.lang.String.valueOf(this.time())).fromVar("res").foreach(org.mwg.task.Actions.lookup("{{result}}"));
              var tc: org.mwg.task.TaskContext = lookupall.prepareWith(this.graph(), null, (result : org.mwg.task.TaskResult<any>) => {
                var finalres: org.mwg.Node[] = new Array<org.mwg.Node>(result.size());
                for (var i: number = 0; i < result.size(); i++) {
                  finalres[i] = <org.mwg.Node>result.get(i);
                }
                callback(finalres);
              });
              var tr: org.mwg.task.TaskResult<any> = tc.wrap(res);
              tc.addToGlobalVariable("res", tr);
              lookupall.executeUsing(tc);
            });
            var res: org.mwg.task.TaskResult<any> = tc.newResult();
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
            var state: org.mwg.plugin.NodeState = this.unphasedState();
            var dim: number = state.getFromKeyWithDefault(KDTree.DIMENSIONS, key.length);
            if (key.length != dim) {
              throw new Error("Key size should always be the same");
            }
            var hr: org.mwg.structure.util.HRect = org.mwg.structure.util.HRect.infiniteHRect(key.length);
            var max_dist_sqd: number = java.lang.Double.MAX_VALUE;
            var nnl: org.mwg.structure.util.NearestNeighborArrayList = new org.mwg.structure.util.NearestNeighborArrayList();
            var distance: org.mwg.structure.distance.Distance = this.getDistance(state);
            var tc: org.mwg.task.TaskContext = KDTree.nearestRadiusTask.prepareWith(this.graph(), this, (result : org.mwg.task.TaskResult<any>) => {
              var res: Float64Array = nnl.getAllNodes();
              var lookupall: org.mwg.task.Task = org.mwg.task.Actions.setWorld(java.lang.String.valueOf(this.world())).setTime(java.lang.String.valueOf(this.time())).fromVar("res").foreach(org.mwg.task.Actions.lookup("{{result}}"));
              var tc: org.mwg.task.TaskContext = lookupall.prepareWith(this.graph(), null, (result : org.mwg.task.TaskResult<any>) => {
                var finalres: org.mwg.Node[] = new Array<org.mwg.Node>(result.size());
                for (var i: number = 0; i < result.size(); i++) {
                  finalres[i] = <org.mwg.Node>result.get(i);
                }
                callback(finalres);
              });
              var tr: org.mwg.task.TaskResult<any> = tc.wrap(res);
              tc.addToGlobalVariable("res", tr);
              lookupall.executeUsing(tc);
            });
            var res: org.mwg.task.TaskResult<any> = tc.newResult();
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
            var state: org.mwg.plugin.NodeState = this.unphasedState();
            var dim: number = state.getFromKeyWithDefault(KDTree.DIMENSIONS, key.length);
            if (key.length != dim) {
              throw new Error("Key size should always be the same");
            }
            var hr: org.mwg.structure.util.HRect = org.mwg.structure.util.HRect.infiniteHRect(key.length);
            var max_dist_sqd: number = java.lang.Double.MAX_VALUE;
            var nnl: org.mwg.structure.util.NearestNeighborList = new org.mwg.structure.util.NearestNeighborList(n);
            var distance: org.mwg.structure.distance.Distance = this.getDistance(state);
            var tc: org.mwg.task.TaskContext = KDTree.nearestTask.prepareWith(this.graph(), this, (result : org.mwg.task.TaskResult<any>) => {
              var res: Float64Array = nnl.getAllNodes();
              var lookupall: org.mwg.task.Task = org.mwg.task.Actions.setWorld(java.lang.String.valueOf(this.world())).setTime(java.lang.String.valueOf(this.time())).fromVar("res").foreach(org.mwg.task.Actions.lookup("{{result}}"));
              var tc: org.mwg.task.TaskContext = lookupall.prepareWith(this.graph(), null, (result : org.mwg.task.TaskResult<any>) => {
                var finalres: org.mwg.Node[] = new Array<org.mwg.Node>(result.size());
                for (var i: number = 0; i < result.size(); i++) {
                  finalres[i] = <org.mwg.Node>result.get(i);
                }
                callback(finalres);
              });
              var tr: org.mwg.task.TaskResult<any> = tc.wrap(res);
              tc.addToGlobalVariable("res", tr);
              lookupall.executeUsing(tc);
            });
            var res: org.mwg.task.TaskResult<any> = tc.newResult();
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
            var d: number = state.getFromKeyWithDefault(KDTree.DISTANCE, KDTree.DISTANCE_TYPE_DEF);
            var distance: org.mwg.structure.distance.Distance;
            if (d == org.mwg.structure.distance.Distances.EUCLIDEAN) {
              distance = org.mwg.structure.distance.EuclideanDistance.instance();
            } else {
              if (d == org.mwg.structure.distance.Distances.GEODISTANCE) {
                distance = org.mwg.structure.distance.GeoDistance.instance();
              } else {
                throw new Error("Unknown distance code metric");
              }
            }
            return distance;
          }
          public extractFeatures(current: org.mwg.Node, callback: org.mwg.Callback<Float64Array>): void {
            var query: string = <string>super.get(KDTree.FROM);
            if (query != null) {
              var split: string[] = query.split(",");
              var tasks: org.mwg.task.Task[] = new Array<org.mwg.task.Task>(split.length);
              for (var i: number = 0; i < split.length; i++) {
                var t: org.mwg.task.Task = org.mwg.task.Actions.setWorld("" + this.world());
                t.setTime(this.time() + "");
                t.parse(split[i].trim());
                tasks[i] = t;
              }
              var result: Float64Array = new Float64Array(tasks.length);
              var waiter: org.mwg.DeferCounter = this.graph().newCounter(tasks.length);
              for (var i: number = 0; i < split.length; i++) {
                var initial: org.mwg.task.TaskResult<any> = org.mwg.task.Actions.newTask().emptyResult();
                initial.add(current);
                var capsule: org.mwg.Callback<number> = (i : number) => {
                  tasks[i].executeWith(this.graph(), initial, (currentResult : org.mwg.task.TaskResult<any>) => {
                    if (currentResult == null) {
                      result[i] = org.mwg.Constants.NULL_LONG;
                    } else {
                      result[i] = parseFloat(currentResult.get(0).toString());
                      currentResult.free();
                    }
                    waiter.count();
                  });
                };
                capsule(i);
              }
              waiter.then(() => {
                callback(result);
              });
            } else {
              callback(null);
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
            var p: Float64Array = new Float64Array(t.length);
            for (var i: number = 0; i < t.length; ++i) {
              if (t[i] <= this.min[i]) {
                p[i] = this.min[i];
              } else {
                if (t[i] >= this.max[i]) {
                  p[i] = this.max[i];
                } else {
                  p[i] = t[i];
                }
              }
            }
            return p;
          }
          public static infiniteHRect(d: number): org.mwg.structure.util.HRect {
            var vmin: Float64Array = new Float64Array(d);
            var vmax: Float64Array = new Float64Array(d);
            for (var i: number = 0; i < d; ++i) {
              vmin[i] = java.lang.Double.NEGATIVE_INFINITY;
              vmax[i] = java.lang.Double.POSITIVE_INFINITY;
            }
            return new org.mwg.structure.util.HRect(vmin, vmax);
          }
          public intersection(r: org.mwg.structure.util.HRect): org.mwg.structure.util.HRect {
            var newmin: Float64Array = new Float64Array(this.min.length);
            var newmax: Float64Array = new Float64Array(this.min.length);
            for (var i: number = 0; i < this.min.length; ++i) {
              newmin[i] = Math.max(this.min[i], r.min[i]);
              newmax[i] = Math.min(this.max[i], r.max[i]);
              if (newmin[i] >= newmax[i]) {
                return null;
              }
            }
            return new org.mwg.structure.util.HRect(newmin, newmax);
          }
          public area(): number {
            var a: number = 1;
            for (var i: number = 0; i < this.min.length; ++i) {
              a *= (this.max[i] - this.min[i]);
            }
            return a;
          }
          public toString(): string {
            return this.min + "\n" + this.max + "\n";
          }
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
            var size: number = this.count;
            var nbrs: Float64Array = new Float64Array(this.count);
            for (var i: number = 0; i < size; ++i) {
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
            if (this.count == 0) {
              return 0;
            }
            var element: number = this.data.get(1);
            this.data.set(1, this.data.get(this.count));
            this.value.set(1, this.value.get(this.count));
            this.data.set(this.count, 0);
            this.value.set(this.count, 0);
            this.count--;
            this.bubbleDown(1);
            return element;
          }
          private bubbleDown(pos: number): void {
            var element: number = this.data.get(pos);
            var priority: number = this.value.get(pos);
            var child: number;
            for (; pos * 2 <= this.count; pos = child) {
              child = pos * 2;
              if (child != this.count) {
                if (this.value.get(child) < this.value.get(child + 1)) {
                  child++;
                }
              }
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
            var element: number = this.data.get(pos);
            var priority: number = this.value.get(pos);
            var halfpos: number = <number>Math.floor(pos / 2);
            while (this.value.get(halfpos) < priority){
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
            for (var i: number = 0; i < this.data.length; i++) {
              console.log(this.data[i]);
            }
            console.log(" ");
            console.log("dist: ");
            for (var i: number = 0; i < this.value.length; i++) {
              console.log(this.value[i]);
            }
            console.log(" ");
          }
          public getAllNodes(): Float64Array {
            var size: number = Math.min(this.capacity, this.count);
            var nbrs: Float64Array = new Float64Array(size);
            for (var i: number = 0; i < size; ++i) {
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
          public getBestDistance(): number {
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
            if (this.count == 0) {
              return 0;
            }
            var element: number = this.data[1];
            this.data[1] = this.data[this.count];
            this.value[1] = this.value[this.count];
            this.data[this.count] = 0;
            this.value[this.count] = 0;
            this.count--;
            this.bubbleDown(1);
            return element;
          }
          private bubbleDown(pos: number): void {
            var element: number = this.data[pos];
            var priority: number = this.value[pos];
            var child: number;
            for (; pos * 2 <= this.count; pos = child) {
              child = pos * 2;
              if (child != this.count) {
                if (this.value[child] < this.value[child + 1]) {
                  child++;
                }
              }
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
            var element: number = this.data[pos];
            var priority: number = this.value[pos];
            var halfpos: number = <number>Math.floor(pos / 2);
            while (this.value[halfpos] < priority){
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
            var elements: Float64Array = new Float64Array(this.capacity + 1);
            var prioritys: Float64Array = new Float64Array(this.capacity + 1);
            java.lang.System.arraycopy(this.data, 0, elements, 0, this.data.length);
            java.lang.System.arraycopy(this.value, 0, prioritys, 0, this.data.length);
            this.data = elements;
            this.value = prioritys;
          }
          public getAllNodesWithin(radius: number): Float64Array {
            var size: number = Math.min(this.capacity, this.count);
            var nbrs: Float64Array = new Float64Array(size);
            var cc: number = 0;
            for (var i: number = 0; i < size; ++i) {
              if (this.getMaxPriority() <= radius) {
                nbrs[size - cc - 1] = this.remove();
                cc++;
              } else {
                this.remove();
              }
            }
            var trimnbrs: Float64Array = new Float64Array(cc);
            java.lang.System.arraycopy(nbrs, size - cc, trimnbrs, 0, cc);
            return trimnbrs;
          }
        }
      }
    }
  }
}
