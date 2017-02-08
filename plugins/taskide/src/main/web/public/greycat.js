var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var java;
(function (java) {
    var lang;
    (function (lang) {
        var System = (function () {
            function System() {
            }
            System.gc = function () {
            };
            System.arraycopy = function (src, srcPos, dest, destPos, numElements) {
                if ((dest instanceof Float64Array || dest instanceof Int32Array)
                    && (src instanceof Float64Array || src instanceof Int32Array)) {
                    if (numElements == src.length) {
                        dest.set(src, destPos);
                    }
                    else {
                        dest.set(src.subarray(srcPos, srcPos + numElements), destPos);
                    }
                }
                else {
                    for (var i = 0; i < numElements; i++) {
                        dest[destPos + i] = src[srcPos + i];
                    }
                }
            };
            return System;
        }());
        lang.System = System;
        var StringBuilder = (function () {
            function StringBuilder() {
                this._buffer = "";
                this.length = 0;
            }
            StringBuilder.prototype.append = function (val) {
                this._buffer = this._buffer + val;
                this.length = this._buffer.length;
                return this;
            };
            StringBuilder.prototype.insert = function (position, val) {
                this._buffer = this._buffer.slice(0, position) + val + this._buffer.slice(position);
                return this;
            };
            StringBuilder.prototype.toString = function () {
                return this._buffer;
            };
            return StringBuilder;
        }());
        lang.StringBuilder = StringBuilder;
        var String = (function () {
            function String() {
            }
            String.valueOf = function (data, offset, count) {
                if (typeof offset === 'undefined' && typeof count === 'undefined') {
                    return data + '';
                }
                else {
                    return data.slice(offset, offset + count);
                }
            };
            String.hashCode = function (str) {
                var h = str['_hashCode'] ? str['_hashCode'] : 0;
                if (h === 0 && str.length > 0) {
                    var val = str;
                    for (var i = 0; i < str.length; i++) {
                        h = 31 * h + str.charCodeAt(i);
                    }
                    str['_hashCode'] = h;
                }
                return h;
            };
            String.isEmpty = function (str) {
                return str.length === 0;
            };
            String.join = function (delimiter, elements) {
                return elements.join(delimiter);
            };
            return String;
        }());
        lang.String = String;
        var Thread = (function () {
            function Thread() {
            }
            Thread.sleep = function (time) {
            };
            return Thread;
        }());
        lang.Thread = Thread;
        var Double = (function () {
            function Double() {
            }
            return Double;
        }());
        Double.MAX_VALUE = Number.MAX_VALUE;
        Double.POSITIVE_INFINITY = Number.POSITIVE_INFINITY;
        Double.NEGATIVE_INFINITY = Number.NEGATIVE_INFINITY;
        Double.NaN = NaN;
        lang.Double = Double;
        var Long = (function () {
            function Long() {
            }
            Long.parseLong = function (d) {
                return parseInt(d);
            };
            return Long;
        }());
        lang.Long = Long;
        var Integer = (function () {
            function Integer() {
            }
            Integer.parseInt = function (d) {
                return parseInt(d);
            };
            return Integer;
        }());
        lang.Integer = Integer;
    })(lang = java.lang || (java.lang = {}));
    var util;
    (function (util) {
        var concurrent;
        (function (concurrent) {
            var atomic;
            (function (atomic) {
                var AtomicIntegerArray = (function () {
                    function AtomicIntegerArray(initialCapacity) {
                        this._internal = new Int32Array(initialCapacity);
                    }
                    AtomicIntegerArray.prototype.set = function (index, newVal) {
                        this._internal[index] = newVal;
                    };
                    AtomicIntegerArray.prototype.get = function (index) {
                        return this._internal[index];
                    };
                    AtomicIntegerArray.prototype.getAndSet = function (index, newVal) {
                        var temp = this._internal[index];
                        this._internal[index] = newVal;
                        return temp;
                    };
                    AtomicIntegerArray.prototype.compareAndSet = function (index, expect, update) {
                        if (this._internal[index] == expect) {
                            this._internal[index] = update;
                            return true;
                        }
                        else {
                            return false;
                        }
                    };
                    return AtomicIntegerArray;
                }());
                atomic.AtomicIntegerArray = AtomicIntegerArray;
                var AtomicLongArray = (function () {
                    function AtomicLongArray(initialCapacity) {
                        this._internal = new Float64Array(initialCapacity);
                    }
                    AtomicLongArray.prototype.set = function (index, newVal) {
                        this._internal[index] = newVal;
                    };
                    AtomicLongArray.prototype.get = function (index) {
                        return this._internal[index];
                    };
                    AtomicLongArray.prototype.getAndSet = function (index, newVal) {
                        var temp = this._internal[index];
                        this._internal[index] = newVal;
                        return temp;
                    };
                    AtomicLongArray.prototype.compareAndSet = function (index, expect, update) {
                        if (this._internal[index] == expect) {
                            this._internal[index] = update;
                            return true;
                        }
                        else {
                            return false;
                        }
                    };
                    AtomicLongArray.prototype.length = function () {
                        return this._internal.length;
                    };
                    return AtomicLongArray;
                }());
                atomic.AtomicLongArray = AtomicLongArray;
                var AtomicReferenceArray = (function () {
                    function AtomicReferenceArray(initialCapacity) {
                        this._internal = new Array();
                    }
                    AtomicReferenceArray.prototype.set = function (index, newVal) {
                        this._internal[index] = newVal;
                    };
                    AtomicReferenceArray.prototype.get = function (index) {
                        return this._internal[index];
                    };
                    AtomicReferenceArray.prototype.getAndSet = function (index, newVal) {
                        var temp = this._internal[index];
                        this._internal[index] = newVal;
                        return temp;
                    };
                    AtomicReferenceArray.prototype.compareAndSet = function (index, expect, update) {
                        if (this._internal[index] == expect) {
                            this._internal[index] = update;
                            return true;
                        }
                        else {
                            return false;
                        }
                    };
                    AtomicReferenceArray.prototype.length = function () {
                        return this._internal.length;
                    };
                    return AtomicReferenceArray;
                }());
                atomic.AtomicReferenceArray = AtomicReferenceArray;
                var AtomicReference = (function () {
                    function AtomicReference() {
                        this._internal = null;
                    }
                    AtomicReference.prototype.compareAndSet = function (expect, update) {
                        if (this._internal == expect) {
                            this._internal = update;
                            return true;
                        }
                        else {
                            return false;
                        }
                    };
                    AtomicReference.prototype.get = function () {
                        return this._internal;
                    };
                    AtomicReference.prototype.set = function (newRef) {
                        this._internal = newRef;
                    };
                    AtomicReference.prototype.getAndSet = function (newVal) {
                        var temp = this._internal;
                        this._internal = newVal;
                        return temp;
                    };
                    return AtomicReference;
                }());
                atomic.AtomicReference = AtomicReference;
                var AtomicLong = (function () {
                    function AtomicLong(init) {
                        this._internal = 0;
                        this._internal = init;
                    }
                    AtomicLong.prototype.compareAndSet = function (expect, update) {
                        if (this._internal == expect) {
                            this._internal = update;
                            return true;
                        }
                        else {
                            return false;
                        }
                    };
                    AtomicLong.prototype.get = function () {
                        return this._internal;
                    };
                    AtomicLong.prototype.incrementAndGet = function () {
                        this._internal++;
                        return this._internal;
                    };
                    AtomicLong.prototype.decrementAndGet = function () {
                        this._internal--;
                        return this._internal;
                    };
                    return AtomicLong;
                }());
                atomic.AtomicLong = AtomicLong;
                var AtomicBoolean = (function () {
                    function AtomicBoolean(init) {
                        this._internal = false;
                        this._internal = init;
                    }
                    AtomicBoolean.prototype.compareAndSet = function (expect, update) {
                        if (this._internal == expect) {
                            this._internal = update;
                            return true;
                        }
                        else {
                            return false;
                        }
                    };
                    AtomicBoolean.prototype.get = function () {
                        return this._internal;
                    };
                    AtomicBoolean.prototype.set = function (newVal) {
                        this._internal = newVal;
                    };
                    return AtomicBoolean;
                }());
                atomic.AtomicBoolean = AtomicBoolean;
                var AtomicInteger = (function () {
                    function AtomicInteger(init) {
                        this._internal = 0;
                        this._internal = init;
                    }
                    AtomicInteger.prototype.compareAndSet = function (expect, update) {
                        if (this._internal == expect) {
                            this._internal = update;
                            return true;
                        }
                        else {
                            return false;
                        }
                    };
                    AtomicInteger.prototype.get = function () {
                        return this._internal;
                    };
                    AtomicInteger.prototype.set = function (newVal) {
                        this._internal = newVal;
                    };
                    AtomicInteger.prototype.getAndSet = function (newVal) {
                        var temp = this._internal;
                        this._internal = newVal;
                        return temp;
                    };
                    AtomicInteger.prototype.incrementAndGet = function () {
                        this._internal++;
                        return this._internal;
                    };
                    AtomicInteger.prototype.decrementAndGet = function () {
                        this._internal--;
                        return this._internal;
                    };
                    AtomicInteger.prototype.getAndIncrement = function () {
                        var temp = this._internal;
                        this._internal++;
                        return temp;
                    };
                    AtomicInteger.prototype.getAndDecrement = function () {
                        var temp = this._internal;
                        this._internal--;
                        return temp;
                    };
                    return AtomicInteger;
                }());
                atomic.AtomicInteger = AtomicInteger;
            })(atomic = concurrent.atomic || (concurrent.atomic = {}));
            var locks;
            (function (locks) {
                var ReentrantLock = (function () {
                    function ReentrantLock() {
                    }
                    ReentrantLock.prototype.lock = function () {
                    };
                    ReentrantLock.prototype.unlock = function () {
                    };
                    return ReentrantLock;
                }());
                locks.ReentrantLock = ReentrantLock;
            })(locks = concurrent.locks || (concurrent.locks = {}));
        })(concurrent = util.concurrent || (util.concurrent = {}));
        var Random = (function () {
            function Random() {
                this.seed = undefined;
                this.haveNextNextGaussian = false;
                this.nextNextGaussian = 0.;
            }
            Random.prototype.nextInt = function (max) {
                if (typeof max === 'undefined') {
                    max = Math.pow(2, 32);
                }
                if (this.seed == undefined) {
                    return Math.floor(Math.random() * max);
                }
                else {
                    return Math.floor(this.nextSeeded(0, max));
                }
            };
            Random.prototype.nextDouble = function () {
                if (this.seed == undefined) {
                    return Math.random();
                }
                else {
                    return this.nextSeeded();
                }
            };
            Random.prototype.nextBoolean = function () {
                if (this.seed == undefined) {
                    return Math.random() >= 0.5;
                }
                else {
                    return this.nextSeeded() >= 0.5;
                }
            };
            Random.prototype.setSeed = function (seed) {
                this.seed = seed;
            };
            Random.prototype.nextSeeded = function (min, max) {
                var max = max || 1;
                var min = min || 0;
                this.seed = (this.seed * 9301 + 49297) % 233280;
                var rnd = this.seed / 233280;
                return min + rnd * (max - min);
            };
            Random.prototype.nextGaussian = function () {
                if (this.haveNextNextGaussian) {
                    this.haveNextNextGaussian = false;
                    return this.nextNextGaussian;
                }
                else {
                    var v1, v2, s;
                    do {
                        v1 = 2 * this.nextDouble() - 1; // between -1 and 1
                        v2 = 2 * this.nextDouble() - 1; // between -1 and 1
                        s = v1 * v1 + v2 * v2;
                    } while (s >= 1 || s == 0);
                    var multiplier = Math.sqrt(-2 * Math.log(s) / s);
                    this.nextNextGaussian = v2 * multiplier;
                    this.haveNextNextGaussian = true;
                    return v1 * multiplier;
                }
            };
            return Random;
        }());
        util.Random = Random;
        var Arrays = (function () {
            function Arrays() {
            }
            Arrays.fill = function (data, begin, nbElem, param) {
                var max = begin + nbElem;
                for (var i = begin; i < max; i++) {
                    data[i] = param;
                }
            };
            Arrays.copyOf = function (original, newLength, ignore) {
                var copy = new Array(newLength);
                lang.System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
                return copy;
            };
            return Arrays;
        }());
        util.Arrays = Arrays;
        var Collections = (function () {
            function Collections() {
            }
            Collections.swap = function (list, i, j) {
                var l = list;
                l.set(i, l.set(j, l.get(i)));
            };
            return Collections;
        }());
        util.Collections = Collections;
        var Itr = (function () {
            function Itr(list) {
                this.cursor = 0;
                this.lastRet = -1;
                this.list = list;
            }
            Itr.prototype.hasNext = function () {
                return this.cursor != this.list.size();
            };
            Itr.prototype.next = function () {
                try {
                    var i = this.cursor;
                    var next = this.list.get(i);
                    this.lastRet = i;
                    this.cursor = i + 1;
                    return next;
                }
                catch ($ex$) {
                    if ($ex$ instanceof Error) {
                        var e = $ex$;
                        throw new Error("no such element exception");
                    }
                    else {
                        throw $ex$;
                    }
                }
            };
            return Itr;
        }());
        util.Itr = Itr;
        var HashSet = (function () {
            function HashSet() {
                this.content = {};
            }
            HashSet.prototype.add = function (val) {
                this.content[val] = val;
            };
            HashSet.prototype.clear = function () {
                this.content = {};
            };
            HashSet.prototype.contains = function (val) {
                return this.content.hasOwnProperty(val);
            };
            HashSet.prototype.containsAll = function (elems) {
                return false;
            };
            HashSet.prototype.addAll = function (vals) {
                var tempArray = vals.toArray(null);
                for (var i = 0; i < tempArray.length; i++) {
                    this.content[tempArray[i]] = tempArray[i];
                }
                return true;
            };
            HashSet.prototype.remove = function (val) {
                var b = false;
                if (this.content[val]) {
                    b = true;
                }
                delete this.content[val];
                return b;
            };
            HashSet.prototype.removeAll = function () {
                return false;
            };
            HashSet.prototype.size = function () {
                return Object.keys(this.content).length;
            };
            HashSet.prototype.isEmpty = function () {
                return this.size() == 0;
            };
            HashSet.prototype.toArray = function (a) {
                var _this = this;
                return Object.keys(this.content).map(function (key) { return _this.content[key]; });
            };
            HashSet.prototype.iterator = function () {
                return new java.util.Itr(this);
            };
            HashSet.prototype.forEach = function (f) {
                for (var p in this.content) {
                    f(this.content[p]);
                }
            };
            HashSet.prototype.get = function (index) {
                return this.content[index];
            };
            return HashSet;
        }());
        util.HashSet = HashSet;
        var AbstractList = (function () {
            function AbstractList() {
                this.content = [];
            }
            AbstractList.prototype.addAll = function (index, vals) {
                var tempArray = vals.toArray(null);
                for (var i = 0; i < tempArray.length; i++) {
                    this.content.push(tempArray[i]);
                }
                return false;
            };
            AbstractList.prototype.clear = function () {
                this.content = [];
            };
            AbstractList.prototype.poll = function () {
                return this.content.shift();
            };
            AbstractList.prototype.remove = function (indexOrElem) {
                this.content.splice(indexOrElem, 1);
                return true;
            };
            AbstractList.prototype.removeAll = function () {
                this.content = [];
                return true;
            };
            AbstractList.prototype.toArray = function (a) {
                return this.content;
            };
            AbstractList.prototype.size = function () {
                return this.content.length;
            };
            AbstractList.prototype.add = function (index, elem) {
                if (typeof elem !== 'undefined') {
                    this.content.splice(index, 0, elem);
                }
                else {
                    this.content.push(index);
                }
            };
            AbstractList.prototype.get = function (index) {
                return this.content[index];
            };
            AbstractList.prototype.contains = function (val) {
                return this.content.indexOf(val) != -1;
            };
            AbstractList.prototype.containsAll = function (elems) {
                return false;
            };
            AbstractList.prototype.isEmpty = function () {
                return this.content.length == 0;
            };
            AbstractList.prototype.set = function (index, element) {
                this.content[index] = element;
                return element;
            };
            AbstractList.prototype.indexOf = function (element) {
                return this.content.indexOf(element);
            };
            AbstractList.prototype.lastIndexOf = function (element) {
                return this.content.lastIndexOf(element);
            };
            AbstractList.prototype.iterator = function () {
                return new Itr(this);
            };
            return AbstractList;
        }());
        util.AbstractList = AbstractList;
        var LinkedList = (function (_super) {
            __extends(LinkedList, _super);
            function LinkedList() {
                return _super !== null && _super.apply(this, arguments) || this;
            }
            return LinkedList;
        }(AbstractList));
        util.LinkedList = LinkedList;
        var ArrayList = (function (_super) {
            __extends(ArrayList, _super);
            function ArrayList() {
                return _super !== null && _super.apply(this, arguments) || this;
            }
            return ArrayList;
        }(AbstractList));
        util.ArrayList = ArrayList;
        var Stack = (function () {
            function Stack() {
                this.content = new Array();
            }
            Stack.prototype.pop = function () {
                return this.content.pop();
            };
            Stack.prototype.push = function (t) {
                this.content.push(t);
            };
            Stack.prototype.isEmpty = function () {
                return this.content.length == 0;
            };
            Stack.prototype.peek = function () {
                return this.content.slice(-1)[0];
            };
            return Stack;
        }());
        util.Stack = Stack;
        var HashMap = (function () {
            function HashMap() {
                this.content = {};
            }
            HashMap.prototype.get = function (key) {
                return this.content[key];
            };
            HashMap.prototype.put = function (key, value) {
                var previous_val = this.content[key];
                this.content[key] = value;
                return previous_val;
            };
            HashMap.prototype.containsKey = function (key) {
                return this.content.hasOwnProperty(key);
            };
            HashMap.prototype.remove = function (key) {
                var tmp = this.content[key];
                delete this.content[key];
                return tmp;
            };
            HashMap.prototype.keySet = function () {
                var result = new HashSet();
                for (var p in this.content) {
                    if (this.content.hasOwnProperty(p)) {
                        result.add(p);
                    }
                }
                return result;
            };
            HashMap.prototype.isEmpty = function () {
                return Object.keys(this.content).length == 0;
            };
            HashMap.prototype.values = function () {
                var result = new HashSet();
                for (var p in this.content) {
                    if (this.content.hasOwnProperty(p)) {
                        result.add(this.content[p]);
                    }
                }
                return result;
            };
            HashMap.prototype.clear = function () {
                this.content = {};
            };
            HashMap.prototype.size = function () {
                return Object.keys(this.content).length;
            };
            return HashMap;
        }());
        util.HashMap = HashMap;
        var ConcurrentHashMap = (function (_super) {
            __extends(ConcurrentHashMap, _super);
            function ConcurrentHashMap() {
                return _super !== null && _super.apply(this, arguments) || this;
            }
            return ConcurrentHashMap;
        }(HashMap));
        util.ConcurrentHashMap = ConcurrentHashMap;
    })(util = java.util || (java.util = {}));
})(java || (java = {}));
function arrayInstanceOf(arr, arg) {
    if (!(arr instanceof Array)) {
        return false;
    }
    else {
        if (arr.length == 0) {
            return true;
        }
        else {
            return (arr[0] instanceof arg);
        }
    }
}
var Long = (function () {
    function Long(low, high, unsigned) {
        /*
         long.js (c) 2013 Daniel Wirtz <dcode@dcode.io>
         Released under the Apache License, Version 2.0
         see: https://github.com/dcodeIO/long.js for details
         */
        this.high = 0;
        this.low = 0;
        this.unsigned = false;
        this.eq = this.equals;
        this.neq = this.notEquals;
        this.lt = this.lessThan;
        this.lte = this.lessThanOrEqual;
        this.gt = this.greaterThan;
        this.gte = this.greaterThanOrEqual;
        this.comp = this.compare;
        this.neg = this.negate;
        this.sub = this.subtract;
        this.mul = this.multiply;
        this.div = this.divide;
        this.mod = this.modulo;
        this.shl = this.shiftLeft;
        this.shr = this.shiftRight;
        this.shru = this.shiftRightUnsigned;
        if (!(high == undefined)) {
            this.high = high;
        }
        if (!(low == undefined)) {
            this.low = low;
        }
        if (!(unsigned == undefined)) {
            this.unsigned = unsigned;
        }
    }
    Long.isLong = function (obj) {
        return (obj && obj["__isLong__"]) === true;
    };
    Long.fromInt = function (value, unsigned) {
        var obj, cachedObj, cache;
        if (unsigned) {
            value >>>= 0;
            if (cache = (0 <= value && value < 256)) {
                cachedObj = Long.UINT_CACHE[value];
                if (cachedObj)
                    return cachedObj;
            }
            obj = Long.fromBits(value, (value | 0) < 0 ? -1 : 0, true);
            if (cache)
                Long.UINT_CACHE[value] = obj;
            return obj;
        }
        else {
            value |= 0;
            if (cache = (-128 <= value && value < 128)) {
                cachedObj = Long.INT_CACHE[value];
                if (cachedObj)
                    return cachedObj;
            }
            obj = Long.fromBits(value, value < 0 ? -1 : 0, false);
            if (cache)
                Long.INT_CACHE[value] = obj;
            return obj;
        }
    };
    Long.fromNumber = function (value, unsigned) {
        if (isNaN(value) || !isFinite(value))
            return unsigned ? Long.UZERO : Long.ZERO;
        if (unsigned) {
            if (value < 0)
                return Long.UZERO;
            if (value >= Long.TWO_PWR_64_DBL)
                return Long.MAX_UNSIGNED_VALUE;
        }
        else {
            if (value <= -Long.TWO_PWR_63_DBL)
                return Long.MIN_VALUE;
            if (value + 1 >= Long.TWO_PWR_63_DBL)
                return Long.MAX_VALUE;
        }
        if (value < 0)
            return Long.fromNumber(-value, unsigned).neg();
        return Long.fromBits((value % Long.TWO_PWR_32_DBL) | 0, (value / Long.TWO_PWR_32_DBL) | 0, unsigned);
    };
    Long.fromBits = function (lowBits, highBits, unsigned) {
        return new Long(lowBits, highBits, unsigned);
    };
    Long.fromString = function (str, radix, unsigned) {
        if (radix === void 0) { radix = 10; }
        if (unsigned === void 0) { unsigned = false; }
        if (str.length === 0)
            throw Error('empty string');
        if (str === "NaN" || str === "Infinity" || str === "+Infinity" || str === "-Infinity")
            return Long.ZERO;
        radix = radix || 10;
        if (radix < 2 || 36 < radix)
            throw RangeError('radix');
        var p;
        if ((p = str.indexOf('-')) > 0)
            throw Error('interior hyphen');
        else if (p === 0) {
            return Long.fromString(str.substring(1), radix, unsigned).neg();
        }
        // Do several (8) digits each time through the loop, so as to
        // minimize the calls to the very expensive emulated div.
        var radixToPower = Long.fromNumber(Long.pow_dbl(radix, 8));
        var result = Long.ZERO;
        for (var i = 0; i < str.length; i += 8) {
            var size = Math.min(8, str.length - i), value = parseInt(str.substring(i, i + size), radix);
            if (size < 8) {
                var power = Long.fromNumber(Long.pow_dbl(radix, size));
                result = result.mul(power).add(Long.fromNumber(value));
            }
            else {
                result = result.mul(radixToPower);
                result = result.add(Long.fromNumber(value));
            }
        }
        result.unsigned = unsigned;
        return result;
    };
    Long.fromValue = function (val) {
        if (val /* is compatible */ instanceof Long)
            return val;
        if (typeof val === 'number')
            return Long.fromNumber(val);
        if (typeof val === 'string')
            return Long.fromString(val);
        // Throws for non-objects, converts non-instanceof Long:
        return Long.fromBits(val.low, val.high, val.unsigned);
    };
    Long.prototype.toInt = function () {
        return this.unsigned ? this.low >>> 0 : this.low;
    };
    ;
    Long.prototype.toNumber = function () {
        if (this.unsigned)
            return ((this.high >>> 0) * Long.TWO_PWR_32_DBL) + (this.low >>> 0);
        return this.high * Long.TWO_PWR_32_DBL + (this.low >>> 0);
    };
    ;
    Long.prototype.toString = function (radix) {
        radix = radix || 10;
        if (radix < 2 || 36 < radix)
            throw RangeError('radix');
        if (this.isZero())
            return '0';
        if (this.isNegative()) {
            if (this.eq(Long.MIN_VALUE)) {
                // We need to change the Long value before it can be negated, so we remove
                // the bottom-most digit in this base and then recurse to do the rest.
                var radixLong = Long.fromNumber(radix), div = this.div(radixLong), rem1 = div.mul(radixLong).sub(this);
                return div.toString(radix) + rem1.toInt().toString(radix);
            }
            else
                return '-' + this.neg().toString(radix);
        }
        // Do several (6) digits each time through the loop, so as to
        // minimize the calls to the very expensive emulated div.
        var radixToPower = Long.fromNumber(Long.pow_dbl(radix, 6), this.unsigned);
        var rem = this;
        var result = '';
        while (true) {
            var remDiv = rem.div(radixToPower);
            var intval = rem.sub(remDiv.mul(radixToPower)).toInt() >>> 0;
            var digits = intval.toString(radix);
            rem = remDiv;
            if (rem.isZero())
                return digits + result;
            else {
                while (digits.length < 6)
                    digits = '0' + digits;
                result = '' + digits + result;
            }
        }
    };
    ;
    Long.prototype.getHighBits = function () {
        return this.high;
    };
    ;
    Long.prototype.getHighBitsUnsigned = function () {
        return this.high >>> 0;
    };
    ;
    Long.prototype.getLowBits = function () {
        return this.low;
    };
    ;
    Long.prototype.getLowBitsUnsigned = function () {
        return this.low >>> 0;
    };
    ;
    Long.prototype.getNumBitsAbs = function () {
        if (this.isNegative())
            return this.eq(Long.MIN_VALUE) ? 64 : this.neg().getNumBitsAbs();
        var val = this.high != 0 ? this.high : this.low;
        for (var bit = 31; bit > 0; bit--)
            if ((val & (1 << bit)) != 0)
                break;
        return this.high != 0 ? bit + 33 : bit + 1;
    };
    ;
    Long.prototype.isZero = function () {
        return this.high === 0 && this.low === 0;
    };
    ;
    Long.prototype.isNegative = function () {
        return !this.unsigned && this.high < 0;
    };
    ;
    Long.prototype.isPositive = function () {
        return this.unsigned || this.high >= 0;
    };
    ;
    Long.prototype.isOdd = function () {
        return (this.low & 1) === 1;
    };
    ;
    Long.prototype.isEven = function () {
        return (this.low & 1) === 0;
    };
    ;
    Long.prototype.equals = function (other) {
        if (!Long.isLong(other))
            other = Long.fromValue(other);
        if (this.unsigned !== other.unsigned && (this.high >>> 31) === 1 && (other.high >>> 31) === 1)
            return false;
        return this.high === other.high && this.low === other.low;
    };
    ;
    Long.prototype.notEquals = function (other) {
        return !this.eq(other);
    };
    ;
    Long.prototype.lessThan = function (other) {
        return this.comp(other) < 0;
    };
    ;
    Long.prototype.lessThanOrEqual = function (other) {
        return this.comp(other) <= 0;
    };
    ;
    Long.prototype.greaterThan = function (other) {
        return this.comp(other) > 0;
    };
    ;
    Long.prototype.greaterThanOrEqual = function (other) {
        return this.comp(other) >= 0;
    };
    ;
    Long.prototype.compare = function (other) {
        if (!Long.isLong(other))
            other = Long.fromValue(other);
        if (this.eq(other))
            return 0;
        var thisNeg = this.isNegative(), otherNeg = other.isNegative();
        if (thisNeg && !otherNeg)
            return -1;
        if (!thisNeg && otherNeg)
            return 1;
        // At this point the sign bits are the same
        if (!this.unsigned)
            return this.sub(other).isNegative() ? -1 : 1;
        // Both are positive if at least one is unsigned
        return (other.high >>> 0) > (this.high >>> 0) || (other.high === this.high && (other.low >>> 0) > (this.low >>> 0)) ? -1 : 1;
    };
    ;
    Long.prototype.negate = function () {
        if (!this.unsigned && this.eq(Long.MIN_VALUE))
            return Long.MIN_VALUE;
        return this.not().add(Long.ONE);
    };
    ;
    Long.prototype.add = function (addend) {
        if (!Long.isLong(addend)) {
            addend = Long.fromValue(addend);
        }
        // Divide each number into 4 chunks of 16 bits, and then sum the chunks.
        var a48 = this.high >>> 16;
        var a32 = this.high & 0xFFFF;
        var a16 = this.low >>> 16;
        var a00 = this.low & 0xFFFF;
        var b48 = addend.high >>> 16;
        var b32 = addend.high & 0xFFFF;
        var b16 = addend.low >>> 16;
        var b00 = addend.low & 0xFFFF;
        var c48 = 0, c32 = 0, c16 = 0, c00 = 0;
        c00 += a00 + b00;
        c16 += c00 >>> 16;
        c00 &= 0xFFFF;
        c16 += a16 + b16;
        c32 += c16 >>> 16;
        c16 &= 0xFFFF;
        c32 += a32 + b32;
        c48 += c32 >>> 16;
        c32 &= 0xFFFF;
        c48 += a48 + b48;
        c48 &= 0xFFFF;
        return Long.fromBits((c16 << 16) | c00, (c48 << 16) | c32, this.unsigned);
    };
    ;
    Long.prototype.subtract = function (subtrahend) {
        if (!Long.isLong(subtrahend))
            subtrahend = Long.fromValue(subtrahend);
        return this.add(subtrahend.neg());
    };
    ;
    Long.prototype.multiply = function (multiplier) {
        if (this.isZero())
            return Long.ZERO;
        if (!Long.isLong(multiplier))
            multiplier = Long.fromValue(multiplier);
        if (multiplier.isZero())
            return Long.ZERO;
        if (this.eq(Long.MIN_VALUE))
            return multiplier.isOdd() ? Long.MIN_VALUE : Long.ZERO;
        if (multiplier.eq(Long.MIN_VALUE))
            return this.isOdd() ? Long.MIN_VALUE : Long.ZERO;
        if (this.isNegative()) {
            if (multiplier.isNegative())
                return this.neg().mul(multiplier.neg());
            else
                return this.neg().mul(multiplier).neg();
        }
        else if (multiplier.isNegative())
            return this.mul(multiplier.neg()).neg();
        // If both longs are small, use float multiplication
        if (this.lt(Long.TWO_PWR_24) && multiplier.lt(Long.TWO_PWR_24))
            return Long.fromNumber(this.toNumber() * multiplier.toNumber(), this.unsigned);
        // Divide each long into 4 chunks of 16 bits, and then add up 4x4 products.
        // We can skip products that would overflow.
        var a48 = this.high >>> 16;
        var a32 = this.high & 0xFFFF;
        var a16 = this.low >>> 16;
        var a00 = this.low & 0xFFFF;
        var b48 = multiplier.high >>> 16;
        var b32 = multiplier.high & 0xFFFF;
        var b16 = multiplier.low >>> 16;
        var b00 = multiplier.low & 0xFFFF;
        var c48 = 0, c32 = 0, c16 = 0, c00 = 0;
        c00 += a00 * b00;
        c16 += c00 >>> 16;
        c00 &= 0xFFFF;
        c16 += a16 * b00;
        c32 += c16 >>> 16;
        c16 &= 0xFFFF;
        c16 += a00 * b16;
        c32 += c16 >>> 16;
        c16 &= 0xFFFF;
        c32 += a32 * b00;
        c48 += c32 >>> 16;
        c32 &= 0xFFFF;
        c32 += a16 * b16;
        c48 += c32 >>> 16;
        c32 &= 0xFFFF;
        c32 += a00 * b32;
        c48 += c32 >>> 16;
        c32 &= 0xFFFF;
        c48 += a48 * b00 + a32 * b16 + a16 * b32 + a00 * b48;
        c48 &= 0xFFFF;
        return Long.fromBits((c16 << 16) | c00, (c48 << 16) | c32, this.unsigned);
    };
    ;
    Long.prototype.divide = function (divisor) {
        if (!Long.isLong(divisor))
            divisor = Long.fromValue(divisor);
        if (divisor.isZero())
            throw Error('division by zero');
        if (this.isZero())
            return this.unsigned ? Long.UZERO : Long.ZERO;
        var approx, rem, res;
        if (!this.unsigned) {
            // This section is only relevant for signed longs and is derived from the
            // closure library as a whole.
            if (this.eq(Long.MIN_VALUE)) {
                if (divisor.eq(Long.ONE) || divisor.eq(Long.NEG_ONE))
                    return Long.MIN_VALUE; // recall that -MIN_VALUE == MIN_VALUE
                else if (divisor.eq(Long.MIN_VALUE))
                    return Long.ONE;
                else {
                    // At this point, we have |other| >= 2, so |this/other| < |MIN_VALUE|.
                    var halfThis = this.shr(1);
                    approx = halfThis.div(divisor).shl(1);
                    if (approx.eq(Long.ZERO)) {
                        return divisor.isNegative() ? Long.ONE : Long.NEG_ONE;
                    }
                    else {
                        rem = this.sub(divisor.mul(approx));
                        res = approx.add(rem.div(divisor));
                        return res;
                    }
                }
            }
            else if (divisor.eq(Long.MIN_VALUE))
                return this.unsigned ? Long.UZERO : Long.ZERO;
            if (this.isNegative()) {
                if (divisor.isNegative())
                    return this.neg().div(divisor.neg());
                return this.neg().div(divisor).neg();
            }
            else if (divisor.isNegative())
                return this.div(divisor.neg()).neg();
            res = Long.ZERO;
        }
        else {
            // The algorithm below has not been made for unsigned longs. It's therefore
            // required to take special care of the MSB prior to running it.
            if (!divisor.unsigned)
                divisor = divisor.toUnsigned();
            if (divisor.gt(this))
                return Long.UZERO;
            if (divisor.gt(this.shru(1)))
                return Long.UONE;
            res = Long.UZERO;
        }
        // Repeat the following until the remainder is less than other:  find a
        // floating-point that approximates remainder / other *from below*, add this
        // into the result, and subtract it from the remainder.  It is critical that
        // the approximate value is less than or equal to the real value so that the
        // remainder never becomes negative.
        rem = this;
        while (rem.gte(divisor)) {
            // Approximate the result of division. This may be a little greater or
            // smaller than the actual value.
            approx = Math.max(1, Math.floor(rem.toNumber() / divisor.toNumber()));
            // We will tweak the approximate result by changing it in the 48-th digit or
            // the smallest non-fractional digit, whichever is larger.
            var log2 = Math.ceil(Math.log(approx) / Math.LN2), delta = (log2 <= 48) ? 1 : Long.pow_dbl(2, log2 - 48), 
            // Decrease the approximation until it is smaller than the remainder.  Note
            // that if it is too large, the product overflows and is negative.
            approxRes = Long.fromNumber(approx), approxRem = approxRes.mul(divisor);
            while (approxRem.isNegative() || approxRem.gt(rem)) {
                approx -= delta;
                approxRes = Long.fromNumber(approx, this.unsigned);
                approxRem = approxRes.mul(divisor);
            }
            // We know the answer can't be zero... and actually, zero would cause
            // infinite recursion since we would make no progress.
            if (approxRes.isZero())
                approxRes = Long.ONE;
            res = res.add(approxRes);
            rem = rem.sub(approxRem);
        }
        return res;
    };
    ;
    Long.prototype.modulo = function (divisor) {
        if (!Long.isLong(divisor))
            divisor = Long.fromValue(divisor);
        return this.sub(this.div(divisor).mul(divisor));
    };
    ;
    Long.prototype.not = function () {
        return Long.fromBits(~this.low, ~this.high, this.unsigned);
    };
    ;
    Long.prototype.and = function (other) {
        if (!Long.isLong(other))
            other = Long.fromValue(other);
        return Long.fromBits(this.low & other.low, this.high & other.high, this.unsigned);
    };
    ;
    Long.prototype.or = function (other) {
        if (!Long.isLong(other))
            other = Long.fromValue(other);
        return Long.fromBits(this.low | other.low, this.high | other.high, this.unsigned);
    };
    ;
    Long.prototype.xor = function (other) {
        if (!Long.isLong(other))
            other = Long.fromValue(other);
        return Long.fromBits(this.low ^ other.low, this.high ^ other.high, this.unsigned);
    };
    ;
    Long.prototype.shiftLeft = function (numBits) {
        if (Long.isLong(numBits))
            numBits = numBits.toInt();
        if ((numBits &= 63) === 0)
            return this;
        else if (numBits < 32)
            return Long.fromBits(this.low << numBits, (this.high << numBits) | (this.low >>> (32 - numBits)), this.unsigned);
        else
            return Long.fromBits(0, this.low << (numBits - 32), this.unsigned);
    };
    ;
    Long.prototype.shiftRight = function (numBits) {
        if (Long.isLong(numBits))
            numBits = numBits.toInt();
        if ((numBits &= 63) === 0)
            return this;
        else if (numBits < 32)
            return Long.fromBits((this.low >>> numBits) | (this.high << (32 - numBits)), this.high >> numBits, this.unsigned);
        else
            return Long.fromBits(this.high >> (numBits - 32), this.high >= 0 ? 0 : -1, this.unsigned);
    };
    ;
    Long.prototype.shiftRightUnsigned = function (numBits) {
        if (Long.isLong(numBits))
            numBits = numBits.toInt();
        numBits &= 63;
        if (numBits === 0)
            return this;
        else {
            var high = this.high;
            if (numBits < 32) {
                var low = this.low;
                return Long.fromBits((low >>> numBits) | (high << (32 - numBits)), high >>> numBits, this.unsigned);
            }
            else if (numBits === 32)
                return Long.fromBits(high, 0, this.unsigned);
            else
                return Long.fromBits(high >>> (numBits - 32), 0, this.unsigned);
        }
    };
    ;
    Long.prototype.toSigned = function () {
        if (!this.unsigned)
            return this;
        return Long.fromBits(this.low, this.high, false);
    };
    ;
    Long.prototype.toUnsigned = function () {
        if (this.unsigned)
            return this;
        return Long.fromBits(this.low, this.high, true);
    };
    ;
    return Long;
}());
Long.INT_CACHE = {};
Long.UINT_CACHE = {};
Long.pow_dbl = Math.pow;
Long.TWO_PWR_16_DBL = 1 << 16;
Long.TWO_PWR_24_DBL = 1 << 24;
Long.TWO_PWR_32_DBL = Long.TWO_PWR_16_DBL * Long.TWO_PWR_16_DBL;
Long.TWO_PWR_64_DBL = Long.TWO_PWR_32_DBL * Long.TWO_PWR_32_DBL;
Long.TWO_PWR_63_DBL = Long.TWO_PWR_64_DBL / 2;
Long.TWO_PWR_24 = Long.fromInt(Long.TWO_PWR_24_DBL);
Long.ZERO = Long.fromInt(0);
Long.UZERO = Long.fromInt(0, true);
Long.ONE = Long.fromInt(1);
Long.UONE = Long.fromInt(1, true);
Long.NEG_ONE = Long.fromInt(-1);
Long.MAX_VALUE = Long.fromBits(0x7FFFFFFF, 0xFFFFFFFF, false);
Long.MAX_UNSIGNED_VALUE = Long.fromBits(0xFFFFFFFF, 0xFFFFFFFF, true);
Long.MIN_VALUE = Long.fromBits(0x80000000, 0, false);
Object.defineProperty(Long.prototype, "__isLong__", {
    value: true,
    enumerable: false,
    configurable: false
});
/// <reference path="./jre.ts" />
var greycat;
(function (greycat) {
    var Constants = (function () {
        function Constants() {
        }
        Constants.isDefined = function (param) {
            return param != null;
        };
        Constants.equals = function (src, other) {
            return src === other;
        };
        Constants.longArrayEquals = function (src, other) {
            if (src.length != other.length) {
                return false;
            }
            for (var i = 0; i < src.length; i++) {
                if (src[i] != other[i]) {
                    return false;
                }
            }
            return true;
        };
        return Constants;
    }());
    Constants.KEY_SIZE = 4;
    Constants.LONG_SIZE = 53;
    Constants.PREFIX_SIZE = 16;
    Constants.BEGINNING_OF_TIME = -0x001FFFFFFFFFFFFE;
    Constants.END_OF_TIME = 0x001FFFFFFFFFFFFE;
    Constants.NULL_LONG = 0x001FFFFFFFFFFFFF;
    Constants.KEY_PREFIX_MASK = 0x0000001FFFFFFFFF;
    Constants.CACHE_MISS_ERROR = "Cache miss error";
    Constants.TASK_PARAM_SEP = ',';
    Constants.TASK_SEP = '.';
    Constants.TASK_PARAM_OPEN = '(';
    Constants.TASK_PARAM_CLOSE = ')';
    Constants.SUB_TASK_OPEN = '{';
    Constants.SUB_TASK_CLOSE = '}';
    Constants.SUB_TASK_DECLR = '#';
    Constants.CHUNK_SEP = "|".charCodeAt(0);
    Constants.CHUNK_ENODE_SEP = "$".charCodeAt(0);
    Constants.CHUNK_ESEP = "%".charCodeAt(0);
    Constants.CHUNK_VAL_SEP = ":".charCodeAt(0);
    Constants.BUFFER_SEP = "#".charCodeAt(0);
    Constants.KEY_SEP = ";".charCodeAt(0);
    Constants.MAP_INITIAL_CAPACITY = 8;
    Constants.BOOL_TRUE = 1;
    Constants.BOOL_FALSE = 0;
    Constants.DEEP_WORLD = true;
    Constants.WIDE_WORLD = false;
    greycat.Constants = Constants;
    var GraphBuilder = (function () {
        function GraphBuilder() {
            this._storage = null;
            this._scheduler = null;
            this._plugins = null;
            this._memorySize = -1;
            this._readOnly = false;
            this._deepPriority = true;
        }
        GraphBuilder.newBuilder = function () {
            return new greycat.GraphBuilder();
        };
        GraphBuilder.prototype.withStorage = function (storage) {
            this._storage = storage;
            return this;
        };
        GraphBuilder.prototype.withReadOnlyStorage = function (storage) {
            this._storage = storage;
            this._readOnly = true;
            return this;
        };
        GraphBuilder.prototype.withMemorySize = function (numberOfElements) {
            this._memorySize = numberOfElements;
            return this;
        };
        GraphBuilder.prototype.withScheduler = function (scheduler) {
            this._scheduler = scheduler;
            return this;
        };
        GraphBuilder.prototype.withPlugin = function (plugin) {
            if (this._plugins == null) {
                this._plugins = new Array(1);
                this._plugins[0] = plugin;
            }
            else {
                var _plugins2 = new Array(this._plugins.length + 1);
                java.lang.System.arraycopy(this._plugins, 0, _plugins2, 0, this._plugins.length);
                _plugins2[this._plugins.length] = plugin;
                this._plugins = _plugins2;
            }
            return this;
        };
        GraphBuilder.prototype.withDeepWorld = function () {
            this._deepPriority = greycat.Constants.DEEP_WORLD;
            return this;
        };
        GraphBuilder.prototype.withWideWorld = function () {
            this._deepPriority = greycat.Constants.WIDE_WORLD;
            return this;
        };
        GraphBuilder.prototype.build = function () {
            if (this._storage == null) {
                this._storage = new greycat.internal.BlackHoleStorage();
            }
            if (this._readOnly) {
                this._storage = new greycat.internal.ReadOnlyStorage(this._storage);
            }
            if (this._scheduler == null) {
                this._scheduler = new greycat.scheduler.TrampolineScheduler();
            }
            if (this._memorySize == -1) {
                this._memorySize = 100000;
            }
            return new greycat.internal.CoreGraph(this._storage, this._memorySize, this._scheduler, this._plugins, this._deepPriority);
        };
        return GraphBuilder;
    }());
    greycat.GraphBuilder = GraphBuilder;
    var Tasks = (function () {
        function Tasks() {
        }
        Tasks.cond = function (mathExpression) {
            return new greycat.internal.task.math.MathConditional(mathExpression).conditional();
        };
        Tasks.newTask = function () {
            return new greycat.internal.task.CoreTask();
        };
        Tasks.emptyResult = function () {
            return new greycat.base.BaseTaskResult(null, false);
        };
        Tasks.then = function (action) {
            return greycat.Tasks.newTask().then(action);
        };
        Tasks.thenDo = function (actionFunction) {
            return greycat.Tasks.newTask().thenDo(actionFunction);
        };
        Tasks.loop = function (from, to, subTask) {
            return greycat.Tasks.newTask().loop(from, to, subTask);
        };
        Tasks.loopPar = function (from, to, subTask) {
            return greycat.Tasks.newTask().loopPar(from, to, subTask);
        };
        Tasks.forEach = function (subTask) {
            return greycat.Tasks.newTask().forEach(subTask);
        };
        Tasks.forEachPar = function (subTask) {
            return greycat.Tasks.newTask().forEachPar(subTask);
        };
        Tasks.map = function (subTask) {
            return greycat.Tasks.newTask().map(subTask);
        };
        Tasks.mapPar = function (subTask) {
            return greycat.Tasks.newTask().mapPar(subTask);
        };
        Tasks.ifThen = function (cond, then) {
            return greycat.Tasks.newTask().ifThen(cond, then);
        };
        Tasks.ifThenScript = function (condScript, then) {
            return greycat.Tasks.newTask().ifThenScript(condScript, then);
        };
        Tasks.ifThenElse = function (cond, thenSub, elseSub) {
            return greycat.Tasks.newTask().ifThenElse(cond, thenSub, elseSub);
        };
        Tasks.ifThenElseScript = function (condScript, thenSub, elseSub) {
            return greycat.Tasks.newTask().ifThenElseScript(condScript, thenSub, elseSub);
        };
        Tasks.doWhile = function (task, cond) {
            return greycat.Tasks.newTask().doWhile(task, cond);
        };
        Tasks.doWhileScript = function (task, condScript) {
            return greycat.Tasks.newTask().doWhileScript(task, condScript);
        };
        Tasks.whileDo = function (cond, task) {
            return greycat.Tasks.newTask().whileDo(cond, task);
        };
        Tasks.whileDoScript = function (condScript, task) {
            return greycat.Tasks.newTask().whileDoScript(condScript, task);
        };
        Tasks.pipe = function () {
            var subTasks = [];
            for (var _i = 0; _i < arguments.length; _i++) {
                subTasks[_i] = arguments[_i];
            }
            return (_a = greycat.Tasks.newTask()).pipe.apply(_a, subTasks);
            var _a;
        };
        Tasks.pipePar = function () {
            var subTasks = [];
            for (var _i = 0; _i < arguments.length; _i++) {
                subTasks[_i] = arguments[_i];
            }
            return (_a = greycat.Tasks.newTask()).pipePar.apply(_a, subTasks);
            var _a;
        };
        Tasks.pipeTo = function (subTask) {
            var vars = [];
            for (var _i = 1; _i < arguments.length; _i++) {
                vars[_i - 1] = arguments[_i];
            }
            return (_a = greycat.Tasks.newTask()).pipeTo.apply(_a, [subTask].concat(vars));
            var _a;
        };
        Tasks.atomic = function (protectedTask) {
            var variablesToLock = [];
            for (var _i = 1; _i < arguments.length; _i++) {
                variablesToLock[_i - 1] = arguments[_i];
            }
            return (_a = greycat.Tasks.newTask()).atomic.apply(_a, [protectedTask].concat(variablesToLock));
            var _a;
        };
        Tasks.parse = function (flat, graph) {
            return greycat.Tasks.newTask().parse(flat, graph);
        };
        return Tasks;
    }());
    greycat.Tasks = Tasks;
    var Type = (function () {
        function Type() {
        }
        Type.typeName = function (p_type) {
            switch (p_type) {
                case greycat.Type.BOOL:
                    return "BOOL";
                case greycat.Type.STRING:
                    return "STRING";
                case greycat.Type.LONG:
                    return "LONG";
                case greycat.Type.INT:
                    return "INT";
                case greycat.Type.DOUBLE:
                    return "DOUBLE";
                case greycat.Type.DOUBLE_ARRAY:
                    return "DOUBLE_ARRAY";
                case greycat.Type.LONG_ARRAY:
                    return "LONG_ARRAY";
                case greycat.Type.INT_ARRAY:
                    return "INT_ARRAY";
                case greycat.Type.STRING_ARRAY:
                    return "STRING_ARRAY";
                case greycat.Type.LONG_TO_LONG_MAP:
                    return "LONG_TO_LONG_MAP";
                case greycat.Type.LONG_TO_LONG_ARRAY_MAP:
                    return "LONG_TO_LONG_ARRAY_MAP";
                case greycat.Type.STRING_TO_INT_MAP:
                    return "STRING_TO_INT_MAP";
                case greycat.Type.RELATION:
                    return "RELATION";
                case greycat.Type.RELATION_INDEXED:
                    return "RELATION_INDEXED";
                case greycat.Type.DMATRIX:
                    return "DMATRIX";
                case greycat.Type.LMATRIX:
                    return "LMATRIX";
                case greycat.Type.EGRAPH:
                    return "EGRAPH";
                case greycat.Type.ENODE:
                    return "ENODE";
                case greycat.Type.ERELATION:
                    return "ERELATION";
                case greycat.Type.TASK:
                    return "TASK";
                case greycat.Type.TASK_ARRAY:
                    return "TASK_ARRAY";
                default:
                    return "unknown";
            }
        };
        Type.typeFromName = function (name) {
            switch (name) {
                case "BOOL":
                    return greycat.Type.BOOL;
                case "STRING":
                    return greycat.Type.STRING;
                case "LONG":
                    return greycat.Type.LONG;
                case "INT":
                    return greycat.Type.INT;
                case "DOUBLE":
                    return greycat.Type.DOUBLE;
                case "DOUBLE_ARRAY":
                    return greycat.Type.DOUBLE_ARRAY;
                case "LONG_ARRAY":
                    return greycat.Type.LONG_ARRAY;
                case "INT_ARRAY":
                    return greycat.Type.INT_ARRAY;
                case "STRING_ARRAY":
                    return greycat.Type.STRING_ARRAY;
                case "LONG_TO_LONG_MAP":
                    return greycat.Type.LONG_TO_LONG_MAP;
                case "LONG_TO_LONG_ARRAY_MAP":
                    return greycat.Type.LONG_TO_LONG_ARRAY_MAP;
                case "STRING_TO_INT_MAP":
                    return greycat.Type.STRING_TO_INT_MAP;
                case "RELATION":
                    return greycat.Type.RELATION;
                case "RELATION_INDEXED":
                    return greycat.Type.RELATION_INDEXED;
                case "DMATRIX":
                    return greycat.Type.DMATRIX;
                case "LMATRIX":
                    return greycat.Type.LMATRIX;
                case "EGRAPH":
                    return greycat.Type.EGRAPH;
                case "ENODE":
                    return greycat.Type.ENODE;
                case "ERELATION":
                    return greycat.Type.ERELATION;
                case "TASK":
                    return greycat.Type.TASK;
                case "TASK_ARRAY":
                    return greycat.Type.TASK_ARRAY;
                default:
                    return -1;
            }
        };
        return Type;
    }());
    Type.BOOL = 1;
    Type.STRING = 2;
    Type.LONG = 3;
    Type.INT = 4;
    Type.DOUBLE = 5;
    Type.DOUBLE_ARRAY = 6;
    Type.LONG_ARRAY = 7;
    Type.INT_ARRAY = 8;
    Type.STRING_ARRAY = 9;
    Type.LONG_TO_LONG_MAP = 10;
    Type.LONG_TO_LONG_ARRAY_MAP = 11;
    Type.STRING_TO_INT_MAP = 12;
    Type.RELATION = 13;
    Type.RELATION_INDEXED = 14;
    Type.DMATRIX = 15;
    Type.LMATRIX = 16;
    Type.EGRAPH = 17;
    Type.ENODE = 18;
    Type.ERELATION = 19;
    Type.TASK = 20;
    Type.TASK_ARRAY = 21;
    greycat.Type = Type;
    var base;
    (function (base) {
        var BaseHook = (function () {
            function BaseHook() {
            }
            BaseHook.prototype.start = function (initialContext) { };
            BaseHook.prototype.beforeAction = function (action, context) { };
            BaseHook.prototype.afterAction = function (action, context) { };
            BaseHook.prototype.beforeTask = function (parentContext, context) { };
            BaseHook.prototype.afterTask = function (context) { };
            BaseHook.prototype.end = function (finalContext) { };
            return BaseHook;
        }());
        base.BaseHook = BaseHook;
        var BaseNode = (function () {
            function BaseNode(p_world, p_time, p_id, p_graph) {
                this._index_worldOrder = -1;
                this._index_superTimeTree = -1;
                this._index_timeTree = -1;
                this._index_stateChunk = -1;
                this._world_magic = -1;
                this._super_time_magic = -1;
                this._time_magic = -1;
                this._dead = false;
                this._world = p_world;
                this._time = p_time;
                this._id = p_id;
                this._graph = p_graph;
                this._resolver = p_graph.resolver();
            }
            BaseNode.prototype.cacheLock = function () {
            };
            BaseNode.prototype.cacheUnlock = function () {
            };
            BaseNode.prototype.init = function () { };
            BaseNode.prototype.nodeTypeName = function () {
                var declaration = this.graph().nodeRegistry().declarationByHash(this._resolver.typeCode(this));
                if (declaration != null) {
                    return declaration.name();
                }
                return null;
            };
            BaseNode.prototype.unphasedState = function () {
                return this._resolver.resolveState(this);
            };
            BaseNode.prototype.phasedState = function () {
                return this._resolver.alignState(this);
            };
            BaseNode.prototype.newState = function (time) {
                return this._resolver.newState(this, this._world, time);
            };
            BaseNode.prototype.graph = function () {
                return this._graph;
            };
            BaseNode.prototype.world = function () {
                return this._world;
            };
            BaseNode.prototype.time = function () {
                return this._time;
            };
            BaseNode.prototype.id = function () {
                return this._id;
            };
            BaseNode.prototype.get = function (name) {
                var resolved = this._resolver.resolveState(this);
                if (resolved != null) {
                    return resolved.get(this._resolver.stringToHash(name, false));
                }
                return null;
            };
            BaseNode.prototype.getAt = function (propIndex) {
                return this._resolver.resolveState(this).get(propIndex);
            };
            BaseNode.prototype.forceSet = function (name, type, value) {
                return this.forceSetAt(this._resolver.stringToHash(name, true), type, value);
            };
            BaseNode.prototype.forceSetAt = function (index, type, value) {
                var preciseState = this._resolver.alignState(this);
                if (preciseState != null) {
                    preciseState.set(index, type, value);
                }
                else {
                    throw new Error(greycat.Constants.CACHE_MISS_ERROR);
                }
                return this;
            };
            BaseNode.prototype.setAt = function (index, type, value) {
                var unPhasedState = this._resolver.resolveState(this);
                var isDiff = (type != unPhasedState.getType(index));
                if (!isDiff) {
                    isDiff = !this.isEquals(unPhasedState.get(index), value, type);
                }
                if (isDiff) {
                    var preciseState = this._resolver.alignState(this);
                    if (preciseState != null) {
                        preciseState.set(index, type, value);
                    }
                    else {
                        throw new Error(greycat.Constants.CACHE_MISS_ERROR);
                    }
                }
                return this;
            };
            BaseNode.prototype.set = function (name, type, value) {
                var hashed = this._resolver.stringToHash(name, true);
                return this.setAt(hashed, type, value);
            };
            BaseNode.prototype.isEquals = function (obj1, obj2, type) {
                switch (type) {
                    case greycat.Type.BOOL:
                        return (obj1 == obj2);
                    case greycat.Type.DOUBLE:
                        return (obj1 == obj2);
                    case greycat.Type.INT:
                        return (obj1 == obj2);
                    case greycat.Type.LONG:
                        return (obj1 == obj2);
                    case greycat.Type.STRING:
                        return (obj1 === obj2);
                    case greycat.Type.DOUBLE_ARRAY:
                        var obj1_ar_d = obj1;
                        var obj2_ar_d = obj2;
                        if (obj1_ar_d.length != obj2_ar_d.length) {
                            return false;
                        }
                        else {
                            for (var i = 0; i < obj1_ar_d.length; i++) {
                                if (obj1_ar_d[i] != obj2_ar_d[i]) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    case greycat.Type.INT_ARRAY:
                        var obj1_ar_i = obj1;
                        var obj2_ar_i = obj2;
                        if (obj1_ar_i.length != obj2_ar_i.length) {
                            return false;
                        }
                        else {
                            for (var i = 0; i < obj1_ar_i.length; i++) {
                                if (obj1_ar_i[i] != obj2_ar_i[i]) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    case greycat.Type.LONG_ARRAY:
                        var obj1_ar_l = obj1;
                        var obj2_ar_l = obj2;
                        if (obj1_ar_l.length != obj2_ar_l.length) {
                            return false;
                        }
                        else {
                            for (var i = 0; i < obj1_ar_l.length; i++) {
                                if (obj1_ar_l[i] != obj2_ar_l[i]) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    case greycat.Type.RELATION:
                    case greycat.Type.RELATION_INDEXED:
                    case greycat.Type.STRING_TO_INT_MAP:
                    case greycat.Type.LONG_TO_LONG_MAP:
                    case greycat.Type.LONG_TO_LONG_ARRAY_MAP: throw new Error("Bad API usage: set can't be used with complex type, please use getOrCreate instead.");
                    default:
                        throw new Error("Not managed type " + type);
                }
            };
            BaseNode.prototype.getOrCreate = function (name, type) {
                return this.getOrCreateAt(this._resolver.stringToHash(name, true), type);
            };
            BaseNode.prototype.getOrCreateAt = function (index, type) {
                var preciseState = this._resolver.alignState(this);
                if (preciseState != null) {
                    return preciseState.getOrCreate(index, type);
                }
                else {
                    throw new Error(greycat.Constants.CACHE_MISS_ERROR);
                }
            };
            BaseNode.prototype.type = function (name) {
                var resolved = this._resolver.resolveState(this);
                if (resolved != null) {
                    return resolved.getType(this._resolver.stringToHash(name, false));
                }
                return -1;
            };
            BaseNode.prototype.typeAt = function (index) {
                var resolved = this._resolver.resolveState(this);
                if (resolved != null) {
                    return resolved.getType(index);
                }
                return -1;
            };
            BaseNode.prototype.remove = function (name) {
                return this.set(name, greycat.Type.INT, null);
            };
            BaseNode.prototype.removeAt = function (index) {
                return this.setAt(index, greycat.Type.INT, null);
            };
            BaseNode.prototype.relation = function (relationName, callback) {
                this.relationAt(this._resolver.stringToHash(relationName, false), callback);
            };
            BaseNode.prototype.relationAt = function (relationIndex, callback) {
                if (callback == null) {
                    return;
                }
                var resolved = this._resolver.resolveState(this);
                if (resolved != null) {
                    switch (resolved.getType(relationIndex)) {
                        case greycat.Type.RELATION:
                            var relation = resolved.get(relationIndex);
                            if (relation == null || relation.size() == 0) {
                                callback(new Array(0));
                            }
                            else {
                                var relSize = relation.size();
                                var ids = new Float64Array(relSize);
                                for (var i = 0; i < relSize; i++) {
                                    ids[i] = relation.get(i);
                                }
                                this._resolver.lookupAll(this._world, this._time, ids, function (result) {
                                    {
                                        callback(result);
                                    }
                                });
                            }
                            break;
                        case greycat.Type.RELATION_INDEXED:
                            var relation_indexed = resolved.get(relationIndex);
                            if (relation_indexed == null || relation_indexed.size() == 0) {
                                callback(new Array(0));
                            }
                            else {
                                this._resolver.lookupAll(this._world, this._time, relation_indexed.all(), function (result) {
                                    {
                                        callback(result);
                                    }
                                });
                            }
                            break;
                        default:
                            callback(new Array(0));
                            break;
                    }
                }
                else {
                    callback(new Array(0));
                }
            };
            BaseNode.prototype.addToRelation = function (relationName, relatedNode) {
                var attributes = [];
                for (var _i = 2; _i < arguments.length; _i++) {
                    attributes[_i - 2] = arguments[_i];
                }
                return this.addToRelationAt.apply(this, [this._resolver.stringToHash(relationName, true), relatedNode].concat(attributes));
            };
            BaseNode.prototype.addToRelationAt = function (relationIndex, relatedNode) {
                var attributes = [];
                for (var _i = 2; _i < arguments.length; _i++) {
                    attributes[_i - 2] = arguments[_i];
                }
                if (relatedNode != null) {
                    var preciseState = this._resolver.alignState(this);
                    if (preciseState != null) {
                        var attributesNotEmpty = (attributes != null && attributes.length > 0);
                        if (attributesNotEmpty) {
                            var indexedRel = preciseState.getOrCreate(relationIndex, greycat.Type.RELATION_INDEXED);
                            indexedRel.add.apply(indexedRel, [relatedNode].concat(attributes));
                        }
                        else {
                            var relationArray = preciseState.getOrCreate(relationIndex, greycat.Type.RELATION);
                            relationArray.add(relatedNode.id());
                        }
                    }
                    else {
                        throw new Error(greycat.Constants.CACHE_MISS_ERROR);
                    }
                }
                return this;
            };
            BaseNode.prototype.removeFromRelation = function (relationName, relatedNode) {
                var attributes = [];
                for (var _i = 2; _i < arguments.length; _i++) {
                    attributes[_i - 2] = arguments[_i];
                }
                return this.removeFromRelationAt.apply(this, [this._resolver.stringToHash(relationName, false), relatedNode].concat(attributes));
            };
            BaseNode.prototype.removeFromRelationAt = function (relationIndex, relatedNode) {
                var attributes = [];
                for (var _i = 2; _i < arguments.length; _i++) {
                    attributes[_i - 2] = arguments[_i];
                }
                if (relatedNode != null) {
                    var preciseState = this._resolver.alignState(this);
                    if (preciseState != null) {
                        var attributesNotEmpty = (attributes != null && attributes.length > 0);
                        if (attributesNotEmpty) {
                            var indexedRel = preciseState.getOrCreate(relationIndex, greycat.Type.RELATION_INDEXED);
                            indexedRel.remove.apply(indexedRel, [relatedNode].concat(attributes));
                        }
                        else {
                            var relationArray = preciseState.getOrCreate(relationIndex, greycat.Type.RELATION);
                            relationArray.remove(relatedNode.id());
                        }
                    }
                    else {
                        throw new Error(greycat.Constants.CACHE_MISS_ERROR);
                    }
                }
                return this;
            };
            BaseNode.prototype.free = function () {
                this._resolver.freeNode(this);
            };
            BaseNode.prototype.timeDephasing = function () {
                var state = this._resolver.resolveState(this);
                if (state != null) {
                    return (this._time - state.time());
                }
                else {
                    throw new Error(greycat.Constants.CACHE_MISS_ERROR);
                }
            };
            BaseNode.prototype.lastModification = function () {
                var state = this._resolver.resolveState(this);
                if (state != null) {
                    return state.time();
                }
                else {
                    throw new Error(greycat.Constants.CACHE_MISS_ERROR);
                }
            };
            BaseNode.prototype.rephase = function () {
                this._resolver.alignState(this);
                return this;
            };
            BaseNode.prototype.timepoints = function (beginningOfSearch, endOfSearch, callback) {
                this._resolver.resolveTimepoints(this, beginningOfSearch, endOfSearch, callback);
            };
            BaseNode.prototype.travelInTime = function (targetTime, callback) {
                this._resolver.lookup(this._world, targetTime, this._id, callback);
            };
            BaseNode.prototype.setTimeSensitivity = function (deltaTime, offset) {
                this._resolver.setTimeSensitivity(this, deltaTime, offset);
                return this;
            };
            BaseNode.prototype.timeSensitivity = function () {
                return this._resolver.getTimeSensitivity(this);
            };
            BaseNode.isNaN = function (toTest) {
                return java.lang.Double.NaN == toTest;
            };
            BaseNode.prototype.toString = function () {
                var _this = this;
                var builder = new java.lang.StringBuilder();
                var isFirst = [true];
                builder.append("{\"world\":");
                builder.append(this.world());
                builder.append(",\"time\":");
                builder.append(this.time());
                builder.append(",\"id\":");
                builder.append(this.id());
                var state = this._resolver.resolveState(this);
                if (state != null) {
                    state.each(function (attributeKey, elemType, elem) {
                        {
                            if (elem != null) {
                                var resolveName = _this._resolver.hashToString(attributeKey);
                                if (resolveName == null) {
                                    resolveName = attributeKey + "";
                                }
                                switch (elemType) {
                                    case greycat.Type.BOOL: {
                                        builder.append(",\"");
                                        builder.append(resolveName);
                                        builder.append("\":");
                                        if (elem) {
                                            builder.append("1");
                                        }
                                        else {
                                            builder.append("0");
                                        }
                                        break;
                                    }
                                    case greycat.Type.STRING: {
                                        builder.append(",\"");
                                        builder.append(resolveName);
                                        builder.append("\":");
                                        builder.append("\"");
                                        builder.append(elem);
                                        builder.append("\"");
                                        break;
                                    }
                                    case greycat.Type.LONG: {
                                        builder.append(",\"");
                                        builder.append(resolveName);
                                        builder.append("\":");
                                        builder.append(elem);
                                        break;
                                    }
                                    case greycat.Type.INT: {
                                        builder.append(",\"");
                                        builder.append(resolveName);
                                        builder.append("\":");
                                        builder.append(elem);
                                        break;
                                    }
                                    case greycat.Type.DOUBLE: {
                                        if (!greycat.base.BaseNode.isNaN(elem)) {
                                            builder.append(",\"");
                                            builder.append(resolveName);
                                            builder.append("\":");
                                            builder.append(elem);
                                        }
                                        break;
                                    }
                                    case greycat.Type.DOUBLE_ARRAY: {
                                        builder.append(",\"");
                                        builder.append(resolveName);
                                        builder.append("\":");
                                        builder.append("[");
                                        var castedArr = elem;
                                        for (var j = 0; j < castedArr.length; j++) {
                                            if (j != 0) {
                                                builder.append(",");
                                            }
                                            builder.append(castedArr[j]);
                                        }
                                        builder.append("]");
                                        break;
                                    }
                                    case greycat.Type.RELATION:
                                        builder.append(",\"");
                                        builder.append(resolveName);
                                        builder.append("\":");
                                        builder.append("[");
                                        var castedRelArr = elem;
                                        for (var j = 0; j < castedRelArr.size(); j++) {
                                            if (j != 0) {
                                                builder.append(",");
                                            }
                                            builder.append(castedRelArr.get(j));
                                        }
                                        builder.append("]");
                                        break;
                                    case greycat.Type.LONG_ARRAY: {
                                        builder.append(",\"");
                                        builder.append(resolveName);
                                        builder.append("\":");
                                        builder.append("[");
                                        var castedArr2 = elem;
                                        for (var j = 0; j < castedArr2.length; j++) {
                                            if (j != 0) {
                                                builder.append(",");
                                            }
                                            builder.append(castedArr2[j]);
                                        }
                                        builder.append("]");
                                        break;
                                    }
                                    case greycat.Type.INT_ARRAY: {
                                        builder.append(",\"");
                                        builder.append(resolveName);
                                        builder.append("\":");
                                        builder.append("[");
                                        var castedArr3 = elem;
                                        for (var j = 0; j < castedArr3.length; j++) {
                                            if (j != 0) {
                                                builder.append(",");
                                            }
                                            builder.append(castedArr3[j]);
                                        }
                                        builder.append("]");
                                        break;
                                    }
                                    case greycat.Type.LONG_TO_LONG_MAP: {
                                        builder.append(",\"");
                                        builder.append(resolveName);
                                        builder.append("\":");
                                        builder.append("{");
                                        var castedMapL2L = elem;
                                        isFirst[0] = true;
                                        castedMapL2L.each(function (key, value) {
                                            {
                                                if (!isFirst[0]) {
                                                    builder.append(",");
                                                }
                                                else {
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
                                    case greycat.Type.RELATION_INDEXED:
                                    case greycat.Type.LONG_TO_LONG_ARRAY_MAP: {
                                        builder.append(",\"");
                                        builder.append(resolveName);
                                        builder.append("\":");
                                        builder.append("{");
                                        var castedMapL2LA = elem;
                                        isFirst[0] = true;
                                        var keys_1 = new java.util.HashSet();
                                        castedMapL2LA.each(function (key, value) {
                                            {
                                                keys_1.add(key);
                                            }
                                        });
                                        var flatKeys = keys_1.toArray(new Array(keys_1.size()));
                                        for (var i = 0; i < flatKeys.length; i++) {
                                            var values = castedMapL2LA.get(flatKeys[i]);
                                            if (!isFirst[0]) {
                                                builder.append(",");
                                            }
                                            else {
                                                isFirst[0] = false;
                                            }
                                            builder.append("\"");
                                            builder.append(flatKeys[i]);
                                            builder.append("\":[");
                                            for (var j = 0; j < values.length; j++) {
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
                                    case greycat.Type.STRING_TO_INT_MAP: {
                                        builder.append(",\"");
                                        builder.append(resolveName);
                                        builder.append("\":");
                                        builder.append("{");
                                        var castedMapS2L = elem;
                                        isFirst[0] = true;
                                        castedMapS2L.each(function (key, value) {
                                            {
                                                if (!isFirst[0]) {
                                                    builder.append(",");
                                                }
                                                else {
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
                    });
                    builder.append("}");
                }
                return builder.toString();
            };
            return BaseNode;
        }());
        base.BaseNode = BaseNode;
        var BaseTaskResult = (function () {
            function BaseTaskResult(toWrap, protect) {
                this._capacity = 0;
                this._size = 0;
                this._exception = null;
                this._output = null;
                if (Array.isArray(toWrap)) {
                    var castedToWrap = toWrap;
                    this._size = toWrap.length;
                    this._capacity = this._size;
                    this._backend = new Array(this._size);
                    if (protect) {
                        for (var i = 0; i < this._size; i++) {
                            var loopObj = castedToWrap[i];
                            if (loopObj instanceof greycat.base.BaseNode) {
                                var loopNode = loopObj;
                                this._backend[i] = loopNode.graph().cloneNode(loopNode);
                            }
                            else {
                                this._backend[i] = loopObj;
                            }
                        }
                    }
                    else {
                        java.lang.System.arraycopy(castedToWrap, 0, this._backend, 0, this._size);
                    }
                }
                else if (toWrap instanceof Float64Array) {
                    var castedOther = toWrap;
                    this._backend = new Array(castedOther.length);
                    for (var i = 0; i < castedOther.length; i++) {
                        this._backend[i] = castedOther[i];
                    }
                    this._capacity = this._backend.length;
                    this._size = this._capacity;
                }
                else if (toWrap instanceof Int32Array) {
                    var castedOther = toWrap;
                    this._backend = new Array(castedOther.length);
                    for (var i = 0; i < castedOther.length; i++) {
                        this._backend[i] = castedOther[i];
                    }
                    this._capacity = this._backend.length;
                    this._size = this._capacity;
                }
                else if (toWrap instanceof Float64Array) {
                    var castedOther = toWrap;
                    this._backend = new Array(castedOther.length);
                    for (var i = 0; i < castedOther.length; i++) {
                        this._backend[i] = castedOther[i];
                    }
                    this._capacity = this._backend.length;
                    this._size = this._capacity;
                }
                else if (toWrap instanceof java.util.ArrayList) {
                    var castedOtherList = toWrap;
                    this._backend = new Array(castedOtherList.size());
                    for (var i = 0; i < castedOtherList.size(); i++) {
                        this._backend[i] = castedOtherList.get(i);
                    }
                    this._capacity = this._backend.length;
                    this._size = this._capacity;
                }
                else if (toWrap instanceof greycat.base.BaseTaskResult) {
                    var other = toWrap;
                    this._size = other._size;
                    this._capacity = other._capacity;
                    if (other._backend != null) {
                        this._backend = new Array(other._backend.length);
                        if (protect) {
                            for (var i = 0; i < this._size; i++) {
                                var loopObj = other._backend[i];
                                if (loopObj instanceof greycat.base.BaseNode) {
                                    var loopNode = loopObj;
                                    this._backend[i] = loopNode.graph().cloneNode(loopNode);
                                }
                                else {
                                    this._backend[i] = loopObj;
                                }
                            }
                        }
                        else {
                            java.lang.System.arraycopy(other._backend, 0, this._backend, 0, this._size);
                        }
                    }
                }
                else {
                    if (toWrap != null) {
                        this._backend = new Array(1);
                        this._capacity = 1;
                        this._size = 1;
                        if (toWrap instanceof greycat.base.BaseNode) {
                            var toWrapNode = toWrap;
                            if (protect) {
                                this._backend[0] = toWrapNode.graph().cloneNode(toWrapNode);
                            }
                            else {
                                this._backend[0] = toWrapNode;
                            }
                        }
                        else {
                            this._backend[0] = toWrap;
                        }
                    }
                }
            }
            BaseTaskResult.prototype.asArray = function () {
                var flat = new Array(this._size);
                if (this._backend != null) {
                    java.lang.System.arraycopy(this._backend, 0, flat, 0, this._size);
                }
                return flat;
            };
            BaseTaskResult.prototype.exception = function () {
                return this._exception;
            };
            BaseTaskResult.prototype.output = function () {
                return this._output;
            };
            BaseTaskResult.prototype.setException = function (e) {
                this._exception = e;
                return this;
            };
            BaseTaskResult.prototype.setOutput = function (output) {
                this._output = output;
                return this;
            };
            BaseTaskResult.prototype.fillWith = function (source) {
                if (source != null) {
                    this._backend = source.asArray();
                    this._capacity = this._backend.length;
                    this._size = this._backend.length;
                }
                return this;
            };
            BaseTaskResult.prototype.iterator = function () {
                return new greycat.base.BaseTaskResultIterator(this._backend);
            };
            BaseTaskResult.prototype.get = function (index) {
                if (index < this._size) {
                    return this._backend[index];
                }
                else {
                    return null;
                }
            };
            BaseTaskResult.prototype.set = function (index, input) {
                if (index >= this._capacity) {
                    this.extendTil(index);
                }
                this._backend[index] = input;
                if (index >= this._size) {
                    this._size++;
                }
                return this;
            };
            BaseTaskResult.prototype.allocate = function (index) {
                if (index >= this._capacity) {
                    if (this._backend == null) {
                        this._backend = new Array(index);
                        this._capacity = index;
                    }
                    else {
                        throw new Error("Not implemented yet!!!");
                    }
                }
                return this;
            };
            BaseTaskResult.prototype.add = function (input) {
                if (this._size >= this._capacity) {
                    this.extendTil(this._size);
                }
                this.set(this._size, input);
                return this;
            };
            BaseTaskResult.prototype.clear = function () {
                this._backend = null;
                this._capacity = 0;
                this._size = 0;
                return this;
            };
            BaseTaskResult.prototype.clone = function () {
                return new greycat.base.BaseTaskResult(this, true);
            };
            BaseTaskResult.prototype.free = function () {
                for (var i = 0; i < this._capacity; i++) {
                    if (this._backend[i] instanceof greycat.base.BaseNode) {
                        this._backend[i].free();
                    }
                }
            };
            BaseTaskResult.prototype.size = function () {
                return this._size;
            };
            BaseTaskResult.prototype.extendTil = function (index) {
                if (this._capacity <= index) {
                    var newCapacity = this._capacity * 2;
                    if (newCapacity <= index) {
                        newCapacity = index + 1;
                    }
                    var extendedBackend = new Array(newCapacity);
                    if (this._backend != null) {
                        java.lang.System.arraycopy(this._backend, 0, extendedBackend, 0, this._size);
                    }
                    this._backend = extendedBackend;
                    this._capacity = newCapacity;
                }
            };
            BaseTaskResult.prototype.toString = function () {
                return this.toJson(true);
            };
            BaseTaskResult.prototype.toJson = function (withContent) {
                var builder = new java.lang.StringBuilder();
                var isFirst = true;
                builder.append("{");
                if (this._exception != null) {
                    isFirst = false;
                    builder.append("\"error\":");
                    greycat.internal.task.TaskHelper.serializeString(this._exception.message, builder, false);
                }
                if (this._output != null) {
                    if (!isFirst) {
                        builder.append(",");
                    }
                    else {
                        isFirst = false;
                    }
                    builder.append("\"output\":");
                    greycat.internal.task.TaskHelper.serializeString(this._output, builder, false);
                }
                if (this._size > 0) {
                    if (!isFirst) {
                        builder.append(",");
                    }
                    builder.append("\"result\":[");
                    for (var i = 0; i < this._size; i++) {
                        if (i != 0) {
                            builder.append(",");
                        }
                        var loop = this._backend[i];
                        if (loop != null) {
                            var saved = loop.toString();
                            if (saved.length > 0) {
                                if (saved.charAt(0) == '{' || saved.charAt(0) == '[') {
                                    builder.append(saved);
                                }
                                else {
                                    greycat.internal.task.TaskHelper.serializeString(saved, builder, false);
                                }
                            }
                        }
                    }
                    builder.append("]");
                }
                builder.append("}");
                return builder.toString();
            };
            return BaseTaskResult;
        }());
        base.BaseTaskResult = BaseTaskResult;
        var BaseTaskResultIterator = (function () {
            function BaseTaskResultIterator(p_backend) {
                this._current = new java.util.concurrent.atomic.AtomicInteger(0);
                if (p_backend != null) {
                    this._backend = p_backend;
                }
                else {
                    this._backend = new Array(0);
                }
                this._size = this._backend.length;
            }
            BaseTaskResultIterator.prototype.next = function () {
                var cursor = this._current.getAndIncrement();
                if (cursor < this._size) {
                    return this._backend[cursor];
                }
                else {
                    return null;
                }
            };
            BaseTaskResultIterator.prototype.nextWithIndex = function () {
                var cursor = this._current.getAndIncrement();
                if (cursor < this._size) {
                    if (this._backend[cursor] != null) {
                        return new greycat.utility.Tuple(cursor, this._backend[cursor]);
                    }
                    else {
                        return null;
                    }
                }
                else {
                    return null;
                }
            };
            return BaseTaskResultIterator;
        }());
        base.BaseTaskResultIterator = BaseTaskResultIterator;
    })(base = greycat.base || (greycat.base = {}));
    var chunk;
    (function (chunk_1) {
        var ChunkType = (function () {
            function ChunkType() {
            }
            return ChunkType;
        }());
        ChunkType.STATE_CHUNK = 0;
        ChunkType.TIME_TREE_CHUNK = 1;
        ChunkType.WORLD_ORDER_CHUNK = 2;
        ChunkType.GEN_CHUNK = 3;
        chunk_1.ChunkType = ChunkType;
    })(chunk = greycat.chunk || (greycat.chunk = {}));
    var internal;
    (function (internal) {
        var BlackHoleStorage = (function () {
            function BlackHoleStorage() {
                this.prefix = 0;
            }
            BlackHoleStorage.prototype.get = function (keys, callback) {
                var result = this._graph.newBuffer();
                var it = keys.iterator();
                var isFirst = true;
                while (it.hasNext()) {
                    var tempView = it.next();
                    if (isFirst) {
                        isFirst = false;
                    }
                    else {
                        result.write(greycat.internal.CoreConstants.BUFFER_SEP);
                    }
                }
                callback(result);
            };
            BlackHoleStorage.prototype.put = function (stream, callback) {
                if (callback != null) {
                    callback(true);
                }
            };
            BlackHoleStorage.prototype.remove = function (keys, callback) {
                callback(true);
            };
            BlackHoleStorage.prototype.connect = function (graph, callback) {
                this._graph = graph;
                callback(true);
            };
            BlackHoleStorage.prototype.lock = function (callback) {
                var buffer = this._graph.newBuffer();
                greycat.utility.Base64.encodeIntToBuffer(this.prefix, buffer);
                this.prefix++;
                callback(buffer);
            };
            BlackHoleStorage.prototype.unlock = function (previousLock, callback) {
                callback(true);
            };
            BlackHoleStorage.prototype.disconnect = function (callback) {
                this._graph = null;
                callback(true);
            };
            return BlackHoleStorage;
        }());
        internal.BlackHoleStorage = BlackHoleStorage;
        var CoreConstants = (function (_super) {
            __extends(CoreConstants, _super);
            function CoreConstants() {
                return _super !== null && _super.apply(this, arguments) || this;
            }
            CoreConstants.fillBooleanArray = function (target, elem) {
                for (var i = 0; i < target.length; i++) {
                    target[i] = elem;
                }
            };
            return CoreConstants;
        }(greycat.Constants));
        CoreConstants.PREFIX_TO_SAVE_SIZE = 2;
        CoreConstants.NULL_KEY = new Float64Array([greycat.Constants.END_OF_TIME, greycat.Constants.END_OF_TIME, greycat.Constants.END_OF_TIME]);
        CoreConstants.GLOBAL_UNIVERSE_KEY = new Float64Array([greycat.Constants.NULL_LONG, greycat.Constants.NULL_LONG, greycat.Constants.NULL_LONG]);
        CoreConstants.GLOBAL_DICTIONARY_KEY = new Float64Array([greycat.Constants.NULL_LONG, 0, 0]);
        CoreConstants.GLOBAL_INDEX_KEY = new Float64Array([greycat.Constants.NULL_LONG, 1, 0]);
        CoreConstants.INDEX_ATTRIBUTE = "index";
        CoreConstants.DISCONNECTED_ERROR = "Please connect your graph, prior to any usage of it";
        CoreConstants.SCALE_1 = 1000;
        CoreConstants.SCALE_2 = 10000;
        CoreConstants.SCALE_3 = 100000;
        CoreConstants.SCALE_4 = 1000000;
        CoreConstants.DEAD_NODE_ERROR = "This Node has been tagged destroyed, please don't use it anymore!";
        internal.CoreConstants = CoreConstants;
        var CoreDeferCounter = (function () {
            function CoreDeferCounter(nb) {
                this._counter = nb;
                this._nb_down = new java.util.concurrent.atomic.AtomicInteger(0);
            }
            CoreDeferCounter.prototype.count = function () {
                var previous;
                var next;
                do {
                    previous = this._nb_down.get();
                    next = previous + 1;
                } while (!this._nb_down.compareAndSet(previous, next));
                if (next == this._counter) {
                    if (this._end != null) {
                        this._end();
                    }
                }
            };
            CoreDeferCounter.prototype.getCount = function () {
                return this._nb_down.get();
            };
            CoreDeferCounter.prototype.then = function (p_callback) {
                this._end = p_callback;
                if (this._nb_down.get() == this._counter) {
                    if (p_callback != null) {
                        p_callback();
                    }
                }
            };
            CoreDeferCounter.prototype.wrap = function () {
                var _this = this;
                return function (result) {
                    {
                        _this.count();
                    }
                };
            };
            return CoreDeferCounter;
        }());
        internal.CoreDeferCounter = CoreDeferCounter;
        var CoreDeferCounterSync = (function () {
            function CoreDeferCounterSync(nb) {
                this._result = null;
                this._counter = nb;
                this._nb_down = new java.util.concurrent.atomic.AtomicInteger(0);
            }
            CoreDeferCounterSync.prototype.count = function () {
                this._nb_down.set(this._nb_down.get() + 1);
                if (this._nb_down.get() == this._counter) {
                    if (this._end != null) {
                        this._end();
                    }
                }
            };
            CoreDeferCounterSync.prototype.getCount = function () {
                return this._nb_down.get();
            };
            CoreDeferCounterSync.prototype.then = function (p_callback) {
                this._end = p_callback;
                if (this._nb_down.get() == this._counter) {
                    if (p_callback != null) {
                        p_callback();
                    }
                }
            };
            CoreDeferCounterSync.prototype.wrap = function () {
                var _this = this;
                return function (result) {
                    {
                        _this._result = result;
                        _this.count();
                    }
                };
            };
            CoreDeferCounterSync.prototype.waitResult = function () {
                while (this._nb_down.get() != this._counter) {
                }
                return this._result;
            };
            return CoreDeferCounterSync;
        }());
        internal.CoreDeferCounterSync = CoreDeferCounterSync;
        var CoreGraph = (function () {
            function CoreGraph(p_storage, memorySize, p_scheduler, p_plugins, deepPriority) {
                this._prefix = null;
                this._nodeKeyCalculator = null;
                this._worldKeyCalculator = null;
                this._actionRegistry = new greycat.internal.task.CoreActionRegistry();
                this._nodeRegistry = new greycat.internal.CoreNodeRegistry();
                this._memoryFactory = new greycat.internal.heap.HeapMemoryFactory();
                this._isConnected = new java.util.concurrent.atomic.AtomicBoolean(false);
                this._lock = new java.util.concurrent.atomic.AtomicBoolean(false);
                this._plugins = p_plugins;
                var selfPointer = this;
                var temp_hooks = new Array(0);
                if (p_plugins != null) {
                    for (var i = 0; i < p_plugins.length; i++) {
                        var loopPlugin = p_plugins[i];
                        loopPlugin.start(this);
                    }
                }
                this._taskHooks = temp_hooks;
                this._storage = p_storage;
                this._space = this._memoryFactory.newSpace(memorySize, selfPointer, deepPriority);
                this._resolver = new greycat.internal.MWGResolver(this._storage, this._space, selfPointer);
                this._scheduler = p_scheduler;
                greycat.internal.task.CoreTask.fillDefault(this._actionRegistry);
                this._nodeRegistry.declaration(greycat.internal.CoreNodeIndex.NAME).setFactory(function (world, time, id, graph) {
                    {
                        return new greycat.internal.CoreNodeIndex(world, time, id, graph);
                    }
                });
            }
            CoreGraph.prototype.fork = function (world) {
                var childWorld = this._worldKeyCalculator.newKey();
                this._resolver.initWorld(world, childWorld);
                return childWorld;
            };
            CoreGraph.prototype.newNode = function (world, time) {
                if (!this._isConnected.get()) {
                    throw new Error(greycat.internal.CoreConstants.DISCONNECTED_ERROR);
                }
                var newNode = new greycat.base.BaseNode(world, time, this._nodeKeyCalculator.newKey(), this);
                this._resolver.initNode(newNode, greycat.Constants.NULL_LONG);
                return newNode;
            };
            CoreGraph.prototype.newTypedNode = function (world, time, nodeType) {
                if (nodeType == null) {
                    throw new Error("nodeType should not be null");
                }
                if (!this._isConnected.get()) {
                    throw new Error(greycat.internal.CoreConstants.DISCONNECTED_ERROR);
                }
                var extraCode = this._resolver.stringToHash(nodeType, false);
                var resolvedFactory = this.factoryByCode(extraCode);
                var newNode;
                if (resolvedFactory == null) {
                    console.log("WARNING: UnKnow NodeType " + nodeType + ", missing plugin configuration in the builder ? Using generic node as a fallback");
                    newNode = new greycat.base.BaseNode(world, time, this._nodeKeyCalculator.newKey(), this);
                }
                else {
                    newNode = resolvedFactory(world, time, this._nodeKeyCalculator.newKey(), this);
                }
                this._resolver.initNode(newNode, extraCode);
                return newNode;
            };
            CoreGraph.prototype.cloneNode = function (origin) {
                if (origin == null) {
                    throw new Error("origin node should not be null");
                }
                if (!this._isConnected.get()) {
                    throw new Error(greycat.internal.CoreConstants.DISCONNECTED_ERROR);
                }
                var casted = origin;
                casted.cacheLock();
                if (casted._dead) {
                    casted.cacheUnlock();
                    throw new Error(greycat.internal.CoreConstants.DEAD_NODE_ERROR + " node id: " + casted.id());
                }
                else {
                    this._space.mark(casted._index_stateChunk);
                    this._space.mark(casted._index_superTimeTree);
                    this._space.mark(casted._index_timeTree);
                    this._space.mark(casted._index_worldOrder);
                    var worldOrderChunk = this._space.get(casted._index_worldOrder);
                    var resolvedFactory = this.factoryByCode(worldOrderChunk.extra());
                    var newNode = void 0;
                    if (resolvedFactory == null) {
                        newNode = new greycat.base.BaseNode(origin.world(), origin.time(), origin.id(), this);
                    }
                    else {
                        newNode = resolvedFactory(origin.world(), origin.time(), origin.id(), this);
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
            };
            CoreGraph.prototype.factoryByCode = function (code) {
                var declaration = this._nodeRegistry.declarationByHash(code);
                if (declaration != null) {
                    return declaration.factory();
                }
                return null;
            };
            CoreGraph.prototype.taskHooks = function () {
                return this._taskHooks;
            };
            CoreGraph.prototype.actionRegistry = function () {
                return this._actionRegistry;
            };
            CoreGraph.prototype.nodeRegistry = function () {
                return this._nodeRegistry;
            };
            CoreGraph.prototype.setMemoryFactory = function (factory) {
                if (this._isConnected.get()) {
                    throw new Error("Memory factory cannot be changed after connection !");
                }
                this._memoryFactory = factory;
                return this;
            };
            CoreGraph.prototype.addGlobalTaskHook = function (newTaskHook) {
                if (this._taskHooks == null) {
                    this._taskHooks = new Array(1);
                    this._taskHooks[0] = newTaskHook;
                }
                else {
                    var temp_temp_hooks = new Array(this._taskHooks.length + 1);
                    java.lang.System.arraycopy(this._taskHooks, 0, temp_temp_hooks, 0, this._taskHooks.length);
                    temp_temp_hooks[this._taskHooks.length] = newTaskHook;
                    this._taskHooks = temp_temp_hooks;
                }
                return this;
            };
            CoreGraph.prototype.lookup = function (world, time, id, callback) {
                if (!this._isConnected.get()) {
                    throw new Error(greycat.internal.CoreConstants.DISCONNECTED_ERROR);
                }
                this._resolver.lookup(world, time, id, callback);
            };
            CoreGraph.prototype.lookupBatch = function (worlds, times, ids, callback) {
                if (!this._isConnected.get()) {
                    throw new Error(greycat.internal.CoreConstants.DISCONNECTED_ERROR);
                }
                this._resolver.lookupBatch(worlds, times, ids, callback);
            };
            CoreGraph.prototype.lookupAll = function (world, time, ids, callback) {
                if (!this._isConnected.get()) {
                    throw new Error(greycat.internal.CoreConstants.DISCONNECTED_ERROR);
                }
                this._resolver.lookupAll(world, time, ids, callback);
            };
            CoreGraph.prototype.lookupTimes = function (world, from, to, id, callback) {
                if (!this._isConnected.get()) {
                    throw new Error(greycat.internal.CoreConstants.DISCONNECTED_ERROR);
                }
                this._resolver.lookupTimes(world, from, to, id, callback);
            };
            CoreGraph.prototype.lookupAllTimes = function (world, from, to, ids, callback) {
                if (!this._isConnected.get()) {
                    throw new Error(greycat.internal.CoreConstants.DISCONNECTED_ERROR);
                }
                this._resolver.lookupAllTimes(world, from, to, ids, callback);
            };
            CoreGraph.prototype.save = function (callback) {
                this._space.save(callback);
            };
            CoreGraph.prototype.connect = function (callback) {
                var _this = this;
                var selfPointer = this;
                while (selfPointer._lock.compareAndSet(false, true))
                    if (this._isConnected.compareAndSet(false, true)) {
                        selfPointer._scheduler.start();
                        selfPointer._storage.connect(selfPointer, function (connection) {
                            {
                                selfPointer._storage.lock(function (prefixBuf) {
                                    {
                                        _this._prefix = greycat.utility.Base64.decodeToIntWithBounds(prefixBuf, 0, prefixBuf.length());
                                        prefixBuf.free();
                                        var connectionKeys_1 = selfPointer.newBuffer();
                                        greycat.utility.KeyHelper.keyToBuffer(connectionKeys_1, greycat.chunk.ChunkType.GEN_CHUNK, greycat.Constants.BEGINNING_OF_TIME, greycat.Constants.NULL_LONG, _this._prefix);
                                        connectionKeys_1.write(greycat.internal.CoreConstants.BUFFER_SEP);
                                        greycat.utility.KeyHelper.keyToBuffer(connectionKeys_1, greycat.chunk.ChunkType.GEN_CHUNK, greycat.Constants.END_OF_TIME, greycat.Constants.NULL_LONG, _this._prefix);
                                        connectionKeys_1.write(greycat.internal.CoreConstants.BUFFER_SEP);
                                        greycat.utility.KeyHelper.keyToBuffer(connectionKeys_1, greycat.chunk.ChunkType.WORLD_ORDER_CHUNK, 0, 0, greycat.Constants.NULL_LONG);
                                        connectionKeys_1.write(greycat.internal.CoreConstants.BUFFER_SEP);
                                        greycat.utility.KeyHelper.keyToBuffer(connectionKeys_1, greycat.chunk.ChunkType.STATE_CHUNK, greycat.internal.CoreConstants.GLOBAL_DICTIONARY_KEY[0], greycat.internal.CoreConstants.GLOBAL_DICTIONARY_KEY[1], greycat.internal.CoreConstants.GLOBAL_DICTIONARY_KEY[2]);
                                        connectionKeys_1.write(greycat.internal.CoreConstants.BUFFER_SEP);
                                        selfPointer._storage.get(connectionKeys_1, function (payloads) {
                                            {
                                                connectionKeys_1.free();
                                                if (payloads != null) {
                                                    var it = payloads.iterator();
                                                    var view1 = it.next();
                                                    var view2 = it.next();
                                                    var view3 = it.next();
                                                    var view4 = it.next();
                                                    var noError = true;
                                                    try {
                                                        var globalWorldOrder = selfPointer._space.createAndMark(greycat.chunk.ChunkType.WORLD_ORDER_CHUNK, 0, 0, greycat.Constants.NULL_LONG);
                                                        if (view3.length() > 0) {
                                                            globalWorldOrder.load(view3);
                                                        }
                                                        var globalDictionaryChunk = selfPointer._space.createAndMark(greycat.chunk.ChunkType.STATE_CHUNK, greycat.internal.CoreConstants.GLOBAL_DICTIONARY_KEY[0], greycat.internal.CoreConstants.GLOBAL_DICTIONARY_KEY[1], greycat.internal.CoreConstants.GLOBAL_DICTIONARY_KEY[2]);
                                                        if (view4.length() > 0) {
                                                            globalDictionaryChunk.load(view4);
                                                        }
                                                        selfPointer._worldKeyCalculator = selfPointer._space.createAndMark(greycat.chunk.ChunkType.GEN_CHUNK, greycat.Constants.END_OF_TIME, greycat.Constants.NULL_LONG, _this._prefix);
                                                        if (view2.length() > 0) {
                                                            selfPointer._worldKeyCalculator.load(view2);
                                                        }
                                                        selfPointer._nodeKeyCalculator = selfPointer._space.createAndMark(greycat.chunk.ChunkType.GEN_CHUNK, greycat.Constants.BEGINNING_OF_TIME, greycat.Constants.NULL_LONG, _this._prefix);
                                                        if (view1.length() > 0) {
                                                            selfPointer._nodeKeyCalculator.load(view1);
                                                        }
                                                        selfPointer._resolver.init();
                                                    }
                                                    catch ($ex$) {
                                                        if ($ex$ instanceof Error) {
                                                            var e = $ex$;
                                                            {
                                                                console.error(e);
                                                                noError = false;
                                                            }
                                                        }
                                                        else {
                                                            throw $ex$;
                                                        }
                                                    }
                                                    payloads.free();
                                                    selfPointer._lock.set(true);
                                                    if (greycat.utility.HashHelper.isDefined(callback)) {
                                                        callback(noError);
                                                    }
                                                }
                                                else {
                                                    selfPointer._lock.set(true);
                                                    if (greycat.utility.HashHelper.isDefined(callback)) {
                                                        callback(false);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                    else {
                        selfPointer._lock.set(true);
                        if (greycat.utility.HashHelper.isDefined(callback)) {
                            callback(null);
                        }
                    }
            };
            CoreGraph.prototype.disconnect = function (callback) {
                var _this = this;
                var _loop_1 = function () {
                    if (this_1._isConnected.compareAndSet(true, false)) {
                        var selfPointer_1 = this_1;
                        selfPointer_1._scheduler.stop();
                        if (this_1._plugins != null) {
                            for (var i = 0; i < this_1._plugins.length; i++) {
                                this_1._plugins[i].stop();
                            }
                        }
                        this_1.save(function (result) {
                            {
                                var space = selfPointer_1._space;
                                space.free(_this._nodeKeyCalculator);
                                space.free(_this._worldKeyCalculator);
                                selfPointer_1._resolver.free();
                                space.freeAll();
                                if (selfPointer_1._storage != null) {
                                    var prefixBuf_1 = selfPointer_1.newBuffer();
                                    greycat.utility.Base64.encodeIntToBuffer(selfPointer_1._prefix, prefixBuf_1);
                                    selfPointer_1._storage.unlock(prefixBuf_1, function (result) {
                                        {
                                            prefixBuf_1.free();
                                            selfPointer_1._storage.disconnect(function (result) {
                                                {
                                                    selfPointer_1._lock.set(true);
                                                    if (greycat.utility.HashHelper.isDefined(callback)) {
                                                        callback(result);
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                                else {
                                    selfPointer_1._lock.set(true);
                                    if (greycat.utility.HashHelper.isDefined(callback)) {
                                        callback(result);
                                    }
                                }
                            }
                        });
                    }
                    else {
                        this_1._lock.set(true);
                        if (greycat.utility.HashHelper.isDefined(callback)) {
                            callback(null);
                        }
                    }
                };
                var this_1 = this;
                while (this._lock.compareAndSet(false, true)) {
                    _loop_1();
                }
            };
            CoreGraph.prototype.newBuffer = function () {
                return this._memoryFactory.newBuffer();
            };
            CoreGraph.prototype.newQuery = function () {
                return new greycat.internal.CoreQuery(this, this._resolver);
            };
            CoreGraph.prototype.index = function (world, time, name, callback) {
                this.internal_index(world, time, name, false, callback);
            };
            CoreGraph.prototype.indexIfExists = function (world, time, name, callback) {
                this.internal_index(world, time, name, true, callback);
            };
            CoreGraph.prototype.internal_index = function (world, time, name, ifExists, callback) {
                var selfPointer = this;
                var indexNameCoded = this._resolver.stringToHash(name, true);
                this._resolver.lookup(world, time, greycat.internal.CoreConstants.END_OF_TIME, function (globalIndexNodeUnsafe) {
                    {
                        if (ifExists && globalIndexNodeUnsafe == null) {
                            callback(null);
                        }
                        else {
                            var globalIndexContent = void 0;
                            if (globalIndexNodeUnsafe == null) {
                                globalIndexNodeUnsafe = new greycat.base.BaseNode(world, time, greycat.internal.CoreConstants.END_OF_TIME, selfPointer);
                                selfPointer._resolver.initNode(globalIndexNodeUnsafe, greycat.internal.CoreConstants.NULL_LONG);
                                globalIndexContent = globalIndexNodeUnsafe.getOrCreate(greycat.internal.CoreConstants.INDEX_ATTRIBUTE, greycat.Type.LONG_TO_LONG_MAP);
                            }
                            else {
                                globalIndexContent = globalIndexNodeUnsafe.get(greycat.internal.CoreConstants.INDEX_ATTRIBUTE);
                            }
                            var indexId = globalIndexContent.get(indexNameCoded);
                            globalIndexNodeUnsafe.free();
                            if (indexId == greycat.internal.CoreConstants.NULL_LONG) {
                                if (ifExists) {
                                    callback(null);
                                }
                                else {
                                    var newIndexNode = selfPointer.newTypedNode(world, time, greycat.internal.CoreNodeIndex.NAME);
                                    indexId = newIndexNode.id();
                                    globalIndexContent.put(indexNameCoded, indexId);
                                    callback(newIndexNode);
                                }
                            }
                            else {
                                selfPointer._resolver.lookup(world, time, indexId, callback);
                            }
                        }
                    }
                });
            };
            CoreGraph.prototype.indexNames = function (world, time, callback) {
                var selfPointer = this;
                this._resolver.lookup(world, time, greycat.internal.CoreConstants.END_OF_TIME, function (globalIndexNodeUnsafe) {
                    {
                        if (globalIndexNodeUnsafe == null) {
                            callback(new Array(0));
                        }
                        else {
                            var globalIndexContent = globalIndexNodeUnsafe.get(greycat.internal.CoreConstants.INDEX_ATTRIBUTE);
                            if (globalIndexContent == null) {
                                globalIndexNodeUnsafe.free();
                                callback(new Array(0));
                            }
                            else {
                                var result_1 = new Array(globalIndexContent.size());
                                var resultIndex_1 = new Int32Array([0]);
                                globalIndexContent.each(function (key, value) {
                                    {
                                        result_1[resultIndex_1[0]] = selfPointer._resolver.hashToString(key);
                                        resultIndex_1[0]++;
                                    }
                                });
                                globalIndexNodeUnsafe.free();
                                callback(result_1);
                            }
                        }
                    }
                });
            };
            CoreGraph.prototype.newCounter = function (expectedCountCalls) {
                return new greycat.internal.CoreDeferCounter(expectedCountCalls);
            };
            CoreGraph.prototype.newSyncCounter = function (expectedCountCalls) {
                return new greycat.internal.CoreDeferCounterSync(expectedCountCalls);
            };
            CoreGraph.prototype.resolver = function () {
                return this._resolver;
            };
            CoreGraph.prototype.scheduler = function () {
                return this._scheduler;
            };
            CoreGraph.prototype.space = function () {
                return this._space;
            };
            CoreGraph.prototype.storage = function () {
                return this._storage;
            };
            CoreGraph.prototype.freeNodes = function (nodes) {
                if (nodes != null) {
                    for (var i = 0; i < nodes.length; i++) {
                        if (nodes[i] != null) {
                            nodes[i].free();
                        }
                    }
                }
            };
            return CoreGraph;
        }());
        internal.CoreGraph = CoreGraph;
        var CoreNodeDeclaration = (function () {
            function CoreNodeDeclaration(name) {
                this._name = name;
            }
            CoreNodeDeclaration.prototype.name = function () {
                return this._name;
            };
            CoreNodeDeclaration.prototype.factory = function () {
                return this._factory;
            };
            CoreNodeDeclaration.prototype.setFactory = function (newFactory) {
                this._factory = newFactory;
            };
            return CoreNodeDeclaration;
        }());
        internal.CoreNodeDeclaration = CoreNodeDeclaration;
        var CoreNodeIndex = (function (_super) {
            __extends(CoreNodeIndex, _super);
            function CoreNodeIndex(p_world, p_time, p_id, p_graph) {
                return _super.call(this, p_world, p_time, p_id, p_graph) || this;
            }
            CoreNodeIndex.prototype.init = function () {
                this.getOrCreate(greycat.internal.CoreConstants.INDEX_ATTRIBUTE, greycat.Type.RELATION_INDEXED);
            };
            CoreNodeIndex.prototype.size = function () {
                return this.get(greycat.internal.CoreConstants.INDEX_ATTRIBUTE).size();
            };
            CoreNodeIndex.prototype.all = function () {
                return this.get(greycat.internal.CoreConstants.INDEX_ATTRIBUTE).all();
            };
            CoreNodeIndex.prototype.addToIndex = function (node) {
                var attributeNames = [];
                for (var _i = 1; _i < arguments.length; _i++) {
                    attributeNames[_i - 1] = arguments[_i];
                }
                (_a = this.get(greycat.internal.CoreConstants.INDEX_ATTRIBUTE)).add.apply(_a, [node].concat(attributeNames));
                return this;
                var _a;
            };
            CoreNodeIndex.prototype.removeFromIndex = function (node) {
                var attributeNames = [];
                for (var _i = 1; _i < arguments.length; _i++) {
                    attributeNames[_i - 1] = arguments[_i];
                }
                (_a = this.get(greycat.internal.CoreConstants.INDEX_ATTRIBUTE)).remove.apply(_a, [node].concat(attributeNames));
                return this;
                var _a;
            };
            CoreNodeIndex.prototype.clear = function () {
                this.get(greycat.internal.CoreConstants.INDEX_ATTRIBUTE).clear();
                return this;
            };
            CoreNodeIndex.prototype.find = function (callback) {
                var query = [];
                for (var _i = 1; _i < arguments.length; _i++) {
                    query[_i - 1] = arguments[_i];
                }
                if (query == null || query.length == 0) {
                    var flat = this.get(greycat.internal.CoreConstants.INDEX_ATTRIBUTE).all();
                    this.graph().lookupAll(this.world(), this.time(), flat, callback);
                }
                else {
                    (_a = this.get(greycat.internal.CoreConstants.INDEX_ATTRIBUTE)).find.apply(_a, [callback, this.world(), this.time()].concat(query));
                }
                var _a;
            };
            CoreNodeIndex.prototype.findByQuery = function (query, callback) {
                this.get(greycat.internal.CoreConstants.INDEX_ATTRIBUTE).findByQuery(query, callback);
            };
            return CoreNodeIndex;
        }(greycat.base.BaseNode));
        CoreNodeIndex.NAME = "NodeIndex";
        internal.CoreNodeIndex = CoreNodeIndex;
        var CoreNodeRegistry = (function () {
            function CoreNodeRegistry() {
                this.backend = new java.util.HashMap();
                this.backend_hash = new java.util.HashMap();
            }
            CoreNodeRegistry.prototype.declaration = function (name) {
                var previous = this.backend.get(name);
                if (previous == null) {
                    previous = new greycat.internal.CoreNodeDeclaration(name);
                    this.backend.put(name, previous);
                    this.backend_hash.put(greycat.utility.HashHelper.hash(name), previous);
                }
                return previous;
            };
            CoreNodeRegistry.prototype.declarationByHash = function (hash) {
                return this.backend_hash.get(hash);
            };
            return CoreNodeRegistry;
        }());
        internal.CoreNodeRegistry = CoreNodeRegistry;
        var CoreQuery = (function () {
            function CoreQuery(graph, p_resolver) {
                this.capacity = 1;
                this._attributes = new Int32Array(this.capacity);
                this._values = new Array(this.capacity);
                this.size = 0;
                this._world = greycat.Constants.NULL_LONG;
                this._time = greycat.Constants.NULL_LONG;
                this._graph = graph;
                this._resolver = p_resolver;
                this._hash = null;
            }
            CoreQuery.prototype.world = function () {
                return this._world;
            };
            CoreQuery.prototype.setWorld = function (p_world) {
                this._world = p_world;
                return this;
            };
            CoreQuery.prototype.time = function () {
                return this._time;
            };
            CoreQuery.prototype.setTime = function (p_time) {
                this._time = p_time;
                return this;
            };
            CoreQuery.prototype.add = function (attributeName, value) {
                this.internal_add(this._resolver.stringToHash(attributeName.trim(), false), value);
                return this;
            };
            CoreQuery.prototype.hash = function () {
                if (this._hash == null) {
                    this.compute();
                }
                return this._hash;
            };
            CoreQuery.prototype.attributes = function () {
                return this._attributes;
            };
            CoreQuery.prototype.values = function () {
                return this._values;
            };
            CoreQuery.prototype.internal_add = function (att, val) {
                if (this.size == this.capacity) {
                    var temp_capacity = this.capacity * 2;
                    var temp_attributes = new Int32Array(temp_capacity);
                    var temp_values = new Array(temp_capacity);
                    java.lang.System.arraycopy(this._attributes, 0, temp_attributes, 0, this.capacity);
                    java.lang.System.arraycopy(this._values, 0, temp_values, 0, this.capacity);
                    this._attributes = temp_attributes;
                    this._values = temp_values;
                    this.capacity = temp_capacity;
                }
                this._attributes[this.size] = att;
                this._values[this.size] = val;
                this.size++;
            };
            CoreQuery.prototype.compute = function () {
                for (var i = (this.size - 1); i >= 0; i--) {
                    for (var j = 1; j <= i; j++) {
                        if (this._attributes[j - 1] > this._attributes[j]) {
                            var tempK = this._attributes[j - 1];
                            var tempV = this._values[j - 1];
                            this._attributes[j - 1] = this._attributes[j];
                            this._values[j - 1] = this._values[j];
                            this._attributes[j] = tempK;
                            this._values[j] = tempV;
                        }
                    }
                }
                var buf = this._graph.newBuffer();
                for (var i = 0; i < this.size; i++) {
                    greycat.utility.Base64.encodeLongToBuffer(this._attributes[i], buf);
                    var loopValue = this._values[i];
                    if (loopValue != null) {
                        greycat.utility.Base64.encodeStringToBuffer(this._values[i], buf);
                    }
                }
                this._hash = greycat.utility.HashHelper.hashBytes(buf.data());
                buf.free();
            };
            return CoreQuery;
        }());
        internal.CoreQuery = CoreQuery;
        var MWGResolver = (function () {
            function MWGResolver(p_storage, p_space, p_graph) {
                this._space = p_space;
                this._storage = p_storage;
                this._graph = p_graph;
            }
            MWGResolver.prototype.init = function () {
                this.dictionary = this._space.getAndMark(greycat.chunk.ChunkType.STATE_CHUNK, greycat.internal.CoreConstants.GLOBAL_DICTIONARY_KEY[0], greycat.internal.CoreConstants.GLOBAL_DICTIONARY_KEY[1], greycat.internal.CoreConstants.GLOBAL_DICTIONARY_KEY[2]);
                this.globalWorldOrderChunk = this._space.getAndMark(greycat.chunk.ChunkType.WORLD_ORDER_CHUNK, 0, 0, greycat.Constants.NULL_LONG);
            };
            MWGResolver.prototype.free = function () {
                if (this.dictionary != null) {
                    this._space.free(this.dictionary);
                }
                if (this.globalWorldOrderChunk != null) {
                    this._space.free(this.globalWorldOrderChunk);
                }
            };
            MWGResolver.prototype.typeCode = function (node) {
                var casted = node;
                var worldOrderChunk = this._space.get(casted._index_worldOrder);
                if (worldOrderChunk == null) {
                    return -1;
                }
                return worldOrderChunk.extra();
            };
            MWGResolver.prototype.initNode = function (node, codeType) {
                var casted = node;
                var cacheEntry = this._space.createAndMark(greycat.chunk.ChunkType.STATE_CHUNK, node.world(), node.time(), node.id());
                this._space.notifyUpdate(cacheEntry.index());
                var superTimeTree = this._space.createAndMark(greycat.chunk.ChunkType.TIME_TREE_CHUNK, node.world(), greycat.Constants.NULL_LONG, node.id());
                superTimeTree.insert(node.time());
                var timeTree = this._space.createAndMark(greycat.chunk.ChunkType.TIME_TREE_CHUNK, node.world(), node.time(), node.id());
                timeTree.insert(node.time());
                var objectWorldOrder = this._space.createAndMark(greycat.chunk.ChunkType.WORLD_ORDER_CHUNK, 0, 0, node.id());
                objectWorldOrder.put(node.world(), node.time());
                if (codeType != greycat.Constants.NULL_LONG) {
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
            };
            MWGResolver.prototype.initWorld = function (parentWorld, childWorld) {
                this.globalWorldOrderChunk.put(childWorld, parentWorld);
            };
            MWGResolver.prototype.freeNode = function (node) {
                var casted = node;
                casted.cacheLock();
                if (!casted._dead) {
                    this._space.unmark(casted._index_stateChunk);
                    this._space.unmark(casted._index_timeTree);
                    this._space.unmark(casted._index_superTimeTree);
                    this._space.unmark(casted._index_worldOrder);
                    casted._dead = true;
                }
                casted.cacheUnlock();
            };
            MWGResolver.prototype.externalLock = function (node) {
                var casted = node;
                var worldOrderChunk = this._space.get(casted._index_worldOrder);
                worldOrderChunk.externalLock();
            };
            MWGResolver.prototype.externalUnlock = function (node) {
                var casted = node;
                var worldOrderChunk = this._space.get(casted._index_worldOrder);
                worldOrderChunk.externalUnlock();
            };
            MWGResolver.prototype.setTimeSensitivity = function (node, deltaTime, offset) {
                var casted = node;
                var superTimeTree = this._space.get(casted._index_superTimeTree);
                superTimeTree.setExtra(deltaTime);
                superTimeTree.setExtra2(offset);
            };
            MWGResolver.prototype.getTimeSensitivity = function (node) {
                var casted = node;
                var result = new Float64Array(2);
                var superTimeTree = this._space.get(casted._index_superTimeTree);
                result[0] = superTimeTree.extra();
                result[1] = superTimeTree.extra2();
                return result;
            };
            MWGResolver.prototype.lookup = function (world, time, id, callback) {
                var _this = this;
                var selfPointer = this;
                selfPointer._space.getOrLoadAndMark(greycat.chunk.ChunkType.WORLD_ORDER_CHUNK, 0, 0, id, function (theNodeWorldOrder) {
                    {
                        if (theNodeWorldOrder == null) {
                            callback(null);
                        }
                        else {
                            var closestWorld_1 = selfPointer.resolve_world(_this.globalWorldOrderChunk, theNodeWorldOrder, time, world);
                            selfPointer._space.getOrLoadAndMark(greycat.chunk.ChunkType.TIME_TREE_CHUNK, closestWorld_1, greycat.Constants.NULL_LONG, id, function (theNodeSuperTimeTree) {
                                {
                                    if (theNodeSuperTimeTree == null) {
                                        selfPointer._space.unmark(theNodeWorldOrder.index());
                                        callback(null);
                                    }
                                    else {
                                        var closestSuperTime = theNodeSuperTimeTree.previousOrEqual(time);
                                        if (closestSuperTime == greycat.Constants.NULL_LONG) {
                                            selfPointer._space.unmark(theNodeSuperTimeTree.index());
                                            selfPointer._space.unmark(theNodeWorldOrder.index());
                                            callback(null);
                                            return;
                                        }
                                        selfPointer._space.getOrLoadAndMark(greycat.chunk.ChunkType.TIME_TREE_CHUNK, closestWorld_1, closestSuperTime, id, function (theNodeTimeTree) {
                                            {
                                                if (theNodeTimeTree == null) {
                                                    selfPointer._space.unmark(theNodeSuperTimeTree.index());
                                                    selfPointer._space.unmark(theNodeWorldOrder.index());
                                                    callback(null);
                                                }
                                                else {
                                                    var closestTime_1 = theNodeTimeTree.previousOrEqual(time);
                                                    if (closestTime_1 == greycat.Constants.NULL_LONG) {
                                                        selfPointer._space.unmark(theNodeTimeTree.index());
                                                        selfPointer._space.unmark(theNodeSuperTimeTree.index());
                                                        selfPointer._space.unmark(theNodeWorldOrder.index());
                                                        callback(null);
                                                        return;
                                                    }
                                                    selfPointer._space.getOrLoadAndMark(greycat.chunk.ChunkType.STATE_CHUNK, closestWorld_1, closestTime_1, id, function (theObjectChunk) {
                                                        {
                                                            if (theObjectChunk == null) {
                                                                selfPointer._space.unmark(theNodeTimeTree.index());
                                                                selfPointer._space.unmark(theNodeSuperTimeTree.index());
                                                                selfPointer._space.unmark(theNodeWorldOrder.index());
                                                                callback(null);
                                                            }
                                                            else {
                                                                var castedNodeWorldOrder = theNodeWorldOrder;
                                                                var extraCode = castedNodeWorldOrder.extra();
                                                                var resolvedFactory = null;
                                                                if (extraCode != -1) {
                                                                    resolvedFactory = selfPointer._graph.factoryByCode(extraCode);
                                                                }
                                                                var resolvedNode = void 0;
                                                                if (resolvedFactory == null) {
                                                                    resolvedNode = new greycat.base.BaseNode(world, time, id, selfPointer._graph);
                                                                }
                                                                else {
                                                                    resolvedNode = resolvedFactory(world, time, id, selfPointer._graph);
                                                                }
                                                                resolvedNode._dead = false;
                                                                resolvedNode._index_stateChunk = theObjectChunk.index();
                                                                resolvedNode._index_superTimeTree = theNodeSuperTimeTree.index();
                                                                resolvedNode._index_timeTree = theNodeTimeTree.index();
                                                                resolvedNode._index_worldOrder = theNodeWorldOrder.index();
                                                                if (closestWorld_1 == world && closestTime_1 == time) {
                                                                    resolvedNode._world_magic = -1;
                                                                    resolvedNode._super_time_magic = -1;
                                                                    resolvedNode._time_magic = -1;
                                                                }
                                                                else {
                                                                    resolvedNode._world_magic = theNodeWorldOrder.magic();
                                                                    resolvedNode._super_time_magic = theNodeSuperTimeTree.magic();
                                                                    resolvedNode._time_magic = theNodeTimeTree.magic();
                                                                }
                                                                if (callback != null) {
                                                                    var casted = resolvedNode;
                                                                    callback(casted);
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            };
            MWGResolver.prototype.lookupBatch = function (worlds, times, ids, callback) {
                var _this = this;
                var idsSize = ids.length;
                if (!(worlds.length == times.length && times.length == idsSize)) {
                    throw new Error("Bad API usage");
                }
                var selfPointer = this;
                var finalResult = new Array(idsSize);
                for (var i = 0; i < idsSize; i++) {
                    finalResult[i] = null;
                }
                var isEmpty = [true];
                var keys = new Float64Array(idsSize * greycat.Constants.KEY_SIZE);
                for (var i = 0; i < idsSize; i++) {
                    isEmpty[0] = false;
                    keys[i * greycat.Constants.KEY_SIZE] = greycat.chunk.ChunkType.WORLD_ORDER_CHUNK;
                    keys[(i * greycat.Constants.KEY_SIZE) + 1] = 0;
                    keys[(i * greycat.Constants.KEY_SIZE) + 2] = 0;
                    keys[(i * greycat.Constants.KEY_SIZE) + 3] = ids[i];
                }
                if (isEmpty[0]) {
                    this.lookupAll_end(finalResult, callback, idsSize, null, null, null, null);
                }
                else {
                    selfPointer._space.getOrLoadAndMarkAll(keys, function (theNodeWorldOrders) {
                        {
                            if (theNodeWorldOrders == null) {
                                _this.lookupAll_end(finalResult, callback, idsSize, null, null, null, null);
                            }
                            else {
                                isEmpty[0] = true;
                                for (var i = 0; i < idsSize; i++) {
                                    if (theNodeWorldOrders[i] != null) {
                                        isEmpty[0] = false;
                                        keys[i * greycat.Constants.KEY_SIZE] = greycat.chunk.ChunkType.TIME_TREE_CHUNK;
                                        keys[(i * greycat.Constants.KEY_SIZE) + 1] = selfPointer.resolve_world(_this.globalWorldOrderChunk, theNodeWorldOrders[i], times[i], worlds[i]);
                                        keys[(i * greycat.Constants.KEY_SIZE) + 2] = greycat.Constants.NULL_LONG;
                                    }
                                    else {
                                        keys[i * greycat.Constants.KEY_SIZE] = -1;
                                    }
                                }
                                if (isEmpty[0]) {
                                    _this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, null, null, null);
                                }
                                else {
                                    selfPointer._space.getOrLoadAndMarkAll(keys, function (theNodeSuperTimeTrees) {
                                        {
                                            if (theNodeSuperTimeTrees == null) {
                                                _this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, null, null, null);
                                            }
                                            else {
                                                isEmpty[0] = true;
                                                for (var i = 0; i < idsSize; i++) {
                                                    if (theNodeSuperTimeTrees[i] != null) {
                                                        var closestSuperTime = theNodeSuperTimeTrees[i].previousOrEqual(times[i]);
                                                        if (closestSuperTime == greycat.Constants.NULL_LONG) {
                                                            keys[i * greycat.Constants.KEY_SIZE] = -1;
                                                        }
                                                        else {
                                                            isEmpty[0] = false;
                                                            keys[(i * greycat.Constants.KEY_SIZE) + 2] = closestSuperTime;
                                                        }
                                                    }
                                                    else {
                                                        keys[i * greycat.Constants.KEY_SIZE] = -1;
                                                    }
                                                }
                                                if (isEmpty[0]) {
                                                    _this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, null, null);
                                                }
                                                else {
                                                    selfPointer._space.getOrLoadAndMarkAll(keys, function (theNodeTimeTrees) {
                                                        {
                                                            if (theNodeTimeTrees == null) {
                                                                _this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, null, null);
                                                            }
                                                            else {
                                                                isEmpty[0] = true;
                                                                for (var i = 0; i < idsSize; i++) {
                                                                    if (theNodeTimeTrees[i] != null) {
                                                                        var closestTime = theNodeTimeTrees[i].previousOrEqual(times[i]);
                                                                        if (closestTime == greycat.Constants.NULL_LONG) {
                                                                            keys[i * greycat.Constants.KEY_SIZE] = -1;
                                                                        }
                                                                        else {
                                                                            isEmpty[0] = false;
                                                                            keys[(i * greycat.Constants.KEY_SIZE)] = greycat.chunk.ChunkType.STATE_CHUNK;
                                                                            keys[(i * greycat.Constants.KEY_SIZE) + 2] = closestTime;
                                                                        }
                                                                    }
                                                                    else {
                                                                        keys[i * greycat.Constants.KEY_SIZE] = -1;
                                                                    }
                                                                }
                                                                if (isEmpty[0]) {
                                                                    _this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, theNodeTimeTrees, null);
                                                                }
                                                                else {
                                                                    selfPointer._space.getOrLoadAndMarkAll(keys, function (theObjectChunks) {
                                                                        {
                                                                            if (theObjectChunks == null) {
                                                                                _this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, theNodeTimeTrees, null);
                                                                            }
                                                                            else {
                                                                                for (var i = 0; i < idsSize; i++) {
                                                                                    if (theObjectChunks[i] != null) {
                                                                                        var castedNodeWorldOrder = theNodeWorldOrders[i];
                                                                                        var extraCode = castedNodeWorldOrder.extra();
                                                                                        var resolvedFactory = null;
                                                                                        if (extraCode != -1) {
                                                                                            resolvedFactory = selfPointer._graph.factoryByCode(extraCode);
                                                                                        }
                                                                                        var resolvedNode = void 0;
                                                                                        if (resolvedFactory == null) {
                                                                                            resolvedNode = new greycat.base.BaseNode(worlds[i], times[i], ids[i], selfPointer._graph);
                                                                                        }
                                                                                        else {
                                                                                            resolvedNode = resolvedFactory(worlds[i], times[i], ids[i], selfPointer._graph);
                                                                                        }
                                                                                        resolvedNode._dead = false;
                                                                                        resolvedNode._index_stateChunk = theObjectChunks[i].index();
                                                                                        resolvedNode._index_superTimeTree = theNodeSuperTimeTrees[i].index();
                                                                                        resolvedNode._index_timeTree = theNodeTimeTrees[i].index();
                                                                                        resolvedNode._index_worldOrder = theNodeWorldOrders[i].index();
                                                                                        if (theObjectChunks[i].world() == worlds[i] && theObjectChunks[i].time() == times[i]) {
                                                                                            resolvedNode._world_magic = -1;
                                                                                            resolvedNode._super_time_magic = -1;
                                                                                            resolvedNode._time_magic = -1;
                                                                                        }
                                                                                        else {
                                                                                            resolvedNode._world_magic = theNodeWorldOrders[i].magic();
                                                                                            resolvedNode._super_time_magic = theNodeSuperTimeTrees[i].magic();
                                                                                            resolvedNode._time_magic = theNodeTimeTrees[i].magic();
                                                                                        }
                                                                                        finalResult[i] = resolvedNode;
                                                                                    }
                                                                                }
                                                                                _this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, theNodeTimeTrees, theObjectChunks);
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            };
            MWGResolver.prototype.lookupTimes = function (world, from, to, id, callback) {
                var selfPointer = this;
                try {
                    selfPointer._space.getOrLoadAndMark(greycat.chunk.ChunkType.WORLD_ORDER_CHUNK, 0, 0, id, function (theNodeWorldOrder) {
                        {
                            if (theNodeWorldOrder == null) {
                                callback(null);
                            }
                            else {
                            }
                        }
                    });
                }
                catch ($ex$) {
                    if ($ex$ instanceof Error) {
                        var e = $ex$;
                        {
                            console.error(e);
                        }
                    }
                    else {
                        throw $ex$;
                    }
                }
            };
            MWGResolver.prototype.lookupAll_end = function (finalResult, callback, sizeIds, worldOrders, superTimes, times, chunks) {
                if (worldOrders != null || superTimes != null || times != null || chunks != null) {
                    for (var i = 0; i < sizeIds; i++) {
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
            };
            MWGResolver.prototype.lookupAll = function (world, time, ids, callback) {
                var _this = this;
                var selfPointer = this;
                var idsSize = ids.length;
                var finalResult = new Array(idsSize);
                for (var i = 0; i < idsSize; i++) {
                    finalResult[i] = null;
                }
                var isEmpty = [true];
                var keys = new Float64Array(idsSize * greycat.Constants.KEY_SIZE);
                for (var i = 0; i < idsSize; i++) {
                    isEmpty[0] = false;
                    keys[i * greycat.Constants.KEY_SIZE] = greycat.chunk.ChunkType.WORLD_ORDER_CHUNK;
                    keys[(i * greycat.Constants.KEY_SIZE) + 1] = 0;
                    keys[(i * greycat.Constants.KEY_SIZE) + 2] = 0;
                    keys[(i * greycat.Constants.KEY_SIZE) + 3] = ids[i];
                }
                if (isEmpty[0]) {
                    this.lookupAll_end(finalResult, callback, idsSize, null, null, null, null);
                }
                else {
                    selfPointer._space.getOrLoadAndMarkAll(keys, function (theNodeWorldOrders) {
                        {
                            if (theNodeWorldOrders == null) {
                                _this.lookupAll_end(finalResult, callback, idsSize, null, null, null, null);
                            }
                            else {
                                isEmpty[0] = true;
                                for (var i = 0; i < idsSize; i++) {
                                    if (theNodeWorldOrders[i] != null) {
                                        isEmpty[0] = false;
                                        keys[i * greycat.Constants.KEY_SIZE] = greycat.chunk.ChunkType.TIME_TREE_CHUNK;
                                        keys[(i * greycat.Constants.KEY_SIZE) + 1] = selfPointer.resolve_world(_this.globalWorldOrderChunk, theNodeWorldOrders[i], time, world);
                                        keys[(i * greycat.Constants.KEY_SIZE) + 2] = greycat.Constants.NULL_LONG;
                                    }
                                    else {
                                        keys[i * greycat.Constants.KEY_SIZE] = -1;
                                    }
                                }
                                if (isEmpty[0]) {
                                    _this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, null, null, null);
                                }
                                else {
                                    selfPointer._space.getOrLoadAndMarkAll(keys, function (theNodeSuperTimeTrees) {
                                        {
                                            if (theNodeSuperTimeTrees == null) {
                                                _this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, null, null, null);
                                            }
                                            else {
                                                isEmpty[0] = true;
                                                for (var i = 0; i < idsSize; i++) {
                                                    if (theNodeSuperTimeTrees[i] != null) {
                                                        var closestSuperTime = theNodeSuperTimeTrees[i].previousOrEqual(time);
                                                        if (closestSuperTime == greycat.Constants.NULL_LONG) {
                                                            keys[i * greycat.Constants.KEY_SIZE] = -1;
                                                        }
                                                        else {
                                                            isEmpty[0] = false;
                                                            keys[(i * greycat.Constants.KEY_SIZE) + 2] = closestSuperTime;
                                                        }
                                                    }
                                                    else {
                                                        keys[i * greycat.Constants.KEY_SIZE] = -1;
                                                    }
                                                }
                                                if (isEmpty[0]) {
                                                    _this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, null, null);
                                                }
                                                else {
                                                    selfPointer._space.getOrLoadAndMarkAll(keys, function (theNodeTimeTrees) {
                                                        {
                                                            if (theNodeTimeTrees == null) {
                                                                _this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, null, null);
                                                            }
                                                            else {
                                                                isEmpty[0] = true;
                                                                for (var i = 0; i < idsSize; i++) {
                                                                    if (theNodeTimeTrees[i] != null) {
                                                                        var closestTime = theNodeTimeTrees[i].previousOrEqual(time);
                                                                        if (closestTime == greycat.Constants.NULL_LONG) {
                                                                            keys[i * greycat.Constants.KEY_SIZE] = -1;
                                                                        }
                                                                        else {
                                                                            isEmpty[0] = false;
                                                                            keys[(i * greycat.Constants.KEY_SIZE)] = greycat.chunk.ChunkType.STATE_CHUNK;
                                                                            keys[(i * greycat.Constants.KEY_SIZE) + 2] = closestTime;
                                                                        }
                                                                    }
                                                                    else {
                                                                        keys[i * greycat.Constants.KEY_SIZE] = -1;
                                                                    }
                                                                }
                                                                if (isEmpty[0]) {
                                                                    _this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, theNodeTimeTrees, null);
                                                                }
                                                                else {
                                                                    selfPointer._space.getOrLoadAndMarkAll(keys, function (theObjectChunks) {
                                                                        {
                                                                            if (theObjectChunks == null) {
                                                                                _this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, theNodeTimeTrees, null);
                                                                            }
                                                                            else {
                                                                                for (var i = 0; i < idsSize; i++) {
                                                                                    if (theObjectChunks[i] != null) {
                                                                                        var castedNodeWorldOrder = theNodeWorldOrders[i];
                                                                                        var extraCode = castedNodeWorldOrder.extra();
                                                                                        var resolvedFactory = null;
                                                                                        if (extraCode != -1) {
                                                                                            resolvedFactory = selfPointer._graph.factoryByCode(extraCode);
                                                                                        }
                                                                                        var resolvedNode = void 0;
                                                                                        if (resolvedFactory == null) {
                                                                                            resolvedNode = new greycat.base.BaseNode(world, time, ids[i], selfPointer._graph);
                                                                                        }
                                                                                        else {
                                                                                            resolvedNode = resolvedFactory(world, time, ids[i], selfPointer._graph);
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
                                                                                        }
                                                                                        else {
                                                                                            resolvedNode._world_magic = theNodeWorldOrders[i].magic();
                                                                                            resolvedNode._super_time_magic = theNodeSuperTimeTrees[i].magic();
                                                                                            resolvedNode._time_magic = theNodeTimeTrees[i].magic();
                                                                                        }
                                                                                        finalResult[i] = resolvedNode;
                                                                                    }
                                                                                }
                                                                                _this.lookupAll_end(finalResult, callback, idsSize, theNodeWorldOrders, theNodeSuperTimeTrees, theNodeTimeTrees, theObjectChunks);
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            };
            MWGResolver.prototype.lookupAllTimes = function (world, from, to, ids, callback) { };
            MWGResolver.prototype.resolve_world = function (globalWorldOrder, nodeWorldOrder, timeToResolve, originWorld) {
                if (globalWorldOrder == null || nodeWorldOrder == null) {
                    return originWorld;
                }
                var currentUniverse = originWorld;
                var previousUniverse = greycat.Constants.NULL_LONG;
                var divergenceTime = nodeWorldOrder.get(currentUniverse);
                while (currentUniverse != previousUniverse) {
                    if (divergenceTime != greycat.Constants.NULL_LONG && divergenceTime <= timeToResolve) {
                        return currentUniverse;
                    }
                    previousUniverse = currentUniverse;
                    currentUniverse = globalWorldOrder.get(currentUniverse);
                    divergenceTime = nodeWorldOrder.get(currentUniverse);
                }
                return originWorld;
            };
            MWGResolver.prototype.getOrLoadAndMarkAll = function (types, keys, callback) {
                var nbKeys = keys.length / MWGResolver.KEY_SIZE;
                var toLoadIndexes = [];
                var nbElem = 0;
                var result = new Array(nbKeys);
                for (var i = 0; i < nbKeys; i++) {
                    if (keys[i * MWGResolver.KEY_SIZE] == greycat.internal.CoreConstants.NULL_KEY[0] && keys[i * MWGResolver.KEY_SIZE + 1] == greycat.internal.CoreConstants.NULL_KEY[1] && keys[i * MWGResolver.KEY_SIZE + 2] == greycat.internal.CoreConstants.NULL_KEY[2]) {
                        toLoadIndexes[i] = false;
                        result[i] = null;
                    }
                    else {
                        result[i] = this._space.getAndMark(types[i], keys[i * MWGResolver.KEY_SIZE], keys[i * MWGResolver.KEY_SIZE + 1], keys[i * MWGResolver.KEY_SIZE + 2]);
                        if (result[i] == null) {
                            toLoadIndexes[i] = true;
                            nbElem++;
                        }
                        else {
                            toLoadIndexes[i] = false;
                        }
                    }
                }
                if (nbElem == 0) {
                    callback(result);
                }
                else {
                    var keysToLoad_1 = this._graph.newBuffer();
                    var reverseIndex_1 = new Int32Array(nbElem);
                    var lastInsertedIndex = 0;
                    for (var i = 0; i < nbKeys; i++) {
                        if (toLoadIndexes[i]) {
                            reverseIndex_1[lastInsertedIndex] = i;
                            if (lastInsertedIndex != 0) {
                                keysToLoad_1.write(greycat.internal.CoreConstants.BUFFER_SEP);
                            }
                            greycat.utility.KeyHelper.keyToBuffer(keysToLoad_1, types[i], keys[i * MWGResolver.KEY_SIZE], keys[i * MWGResolver.KEY_SIZE + 1], keys[i * MWGResolver.KEY_SIZE + 2]);
                            lastInsertedIndex = lastInsertedIndex + 1;
                        }
                    }
                    var selfPointer_2 = this;
                    this._storage.get(keysToLoad_1, function (fromDbBuffers) {
                        {
                            keysToLoad_1.free();
                            var it = fromDbBuffers.iterator();
                            var i = 0;
                            while (it.hasNext()) {
                                var reversedIndex = reverseIndex_1[i];
                                var view = it.next();
                                if (view.length() > 0) {
                                    result[reversedIndex] = selfPointer_2._space.createAndMark(types[reversedIndex], keys[reversedIndex * greycat.internal.MWGResolver.KEY_SIZE], keys[reversedIndex * greycat.internal.MWGResolver.KEY_SIZE + 1], keys[reversedIndex * greycat.internal.MWGResolver.KEY_SIZE + 2]);
                                    result[reversedIndex].load(view);
                                }
                                else {
                                    result[reversedIndex] = null;
                                }
                                i++;
                            }
                            fromDbBuffers.free();
                            callback(result);
                        }
                    });
                }
            };
            MWGResolver.prototype.resolveState = function (node) {
                return this.internal_resolveState(node, true);
            };
            MWGResolver.prototype.internal_resolveState = function (node, safe) {
                var castedNode = node;
                var stateResult = null;
                if (safe) {
                    castedNode.cacheLock();
                }
                if (castedNode._dead) {
                    if (safe) {
                        castedNode.cacheUnlock();
                    }
                    throw new Error(greycat.internal.CoreConstants.DEAD_NODE_ERROR + " node id: " + node.id());
                }
                if (castedNode._world_magic == -1 && castedNode._time_magic == -1 && castedNode._super_time_magic == -1) {
                    stateResult = this._space.get(castedNode._index_stateChunk);
                }
                else {
                    var nodeWorldOrder = this._space.get(castedNode._index_worldOrder);
                    var nodeSuperTimeTree = this._space.get(castedNode._index_superTimeTree);
                    var nodeTimeTree = this._space.get(castedNode._index_timeTree);
                    if (nodeWorldOrder != null && nodeSuperTimeTree != null && nodeTimeTree != null) {
                        if (castedNode._world_magic == nodeWorldOrder.magic() && castedNode._super_time_magic == nodeSuperTimeTree.magic() && castedNode._time_magic == nodeTimeTree.magic()) {
                            stateResult = this._space.get(castedNode._index_stateChunk);
                        }
                        else {
                            if (safe) {
                                nodeWorldOrder.lock();
                            }
                            var nodeTime = castedNode.time();
                            var nodeId = castedNode.id();
                            var nodeWorld = castedNode.world();
                            var resolvedWorld = this.resolve_world(this.globalWorldOrderChunk, nodeWorldOrder, nodeTime, nodeWorld);
                            if (resolvedWorld != nodeSuperTimeTree.world()) {
                                var tempNodeSuperTimeTree = this._space.getAndMark(greycat.chunk.ChunkType.TIME_TREE_CHUNK, resolvedWorld, greycat.internal.CoreConstants.NULL_LONG, nodeId);
                                if (tempNodeSuperTimeTree != null) {
                                    this._space.unmark(nodeSuperTimeTree.index());
                                    nodeSuperTimeTree = tempNodeSuperTimeTree;
                                }
                            }
                            var resolvedSuperTime = nodeSuperTimeTree.previousOrEqual(nodeTime);
                            if (resolvedSuperTime != nodeTimeTree.time()) {
                                var tempNodeTimeTree = this._space.getAndMark(greycat.chunk.ChunkType.TIME_TREE_CHUNK, resolvedWorld, resolvedSuperTime, nodeId);
                                if (tempNodeTimeTree != null) {
                                    this._space.unmark(nodeTimeTree.index());
                                    nodeTimeTree = tempNodeTimeTree;
                                }
                            }
                            var resolvedTime = nodeTimeTree.previousOrEqual(nodeTime);
                            if (resolvedWorld == nodeWorld && resolvedTime == nodeTime) {
                                castedNode._world_magic = -1;
                                castedNode._time_magic = -1;
                                castedNode._super_time_magic = -1;
                            }
                            else {
                                castedNode._world_magic = nodeWorldOrder.magic();
                                castedNode._time_magic = nodeTimeTree.magic();
                                castedNode._super_time_magic = nodeSuperTimeTree.magic();
                                castedNode._index_superTimeTree = nodeSuperTimeTree.index();
                                castedNode._index_timeTree = nodeTimeTree.index();
                            }
                            stateResult = this._space.get(castedNode._index_stateChunk);
                            if (resolvedWorld != stateResult.world() || resolvedTime != stateResult.time()) {
                                var tempNodeState = this._space.getAndMark(greycat.chunk.ChunkType.STATE_CHUNK, resolvedWorld, resolvedTime, nodeId);
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
            };
            MWGResolver.prototype.alignState = function (node) {
                var castedNode = node;
                castedNode.cacheLock();
                if (castedNode._dead) {
                    castedNode.cacheUnlock();
                    throw new Error(greycat.internal.CoreConstants.DEAD_NODE_ERROR + " node id: " + node.id());
                }
                if (castedNode._world_magic == -1 && castedNode._time_magic == -1 && castedNode._super_time_magic == -1) {
                    var currentEntry = this._space.get(castedNode._index_stateChunk);
                    if (currentEntry != null) {
                        castedNode.cacheUnlock();
                        return currentEntry;
                    }
                }
                var nodeWorldOrder = this._space.get(castedNode._index_worldOrder);
                if (nodeWorldOrder == null) {
                    castedNode.cacheUnlock();
                    return null;
                }
                nodeWorldOrder.lock();
                var previouStateChunk = this.internal_resolveState(node, false);
                var previousTime = previouStateChunk.time();
                var previousWorld = previouStateChunk.world();
                if (castedNode._world_magic == -1 && castedNode._time_magic == -1 && castedNode._super_time_magic == -1) {
                    nodeWorldOrder.unlock();
                    castedNode.cacheUnlock();
                    return previouStateChunk;
                }
                var nodeWorld = node.world();
                var nodeTime = node.time();
                var nodeId = node.id();
                var superTimeTree = this._space.get(castedNode._index_superTimeTree);
                var timeSensitivity = superTimeTree.extra();
                if (timeSensitivity != 0 && timeSensitivity != greycat.Constants.NULL_LONG) {
                    if (timeSensitivity < 0) {
                        nodeTime = previousTime;
                    }
                    else {
                        var timeSensitivityOffset = superTimeTree.extra2();
                        if (timeSensitivityOffset == greycat.Constants.NULL_LONG) {
                            timeSensitivityOffset = 0;
                        }
                        nodeTime = nodeTime - (nodeTime % timeSensitivity) + timeSensitivityOffset;
                    }
                }
                var clonedState;
                if (nodeTime != previousTime || nodeWorld != previousWorld) {
                    clonedState = this._space.createAndMark(greycat.chunk.ChunkType.STATE_CHUNK, nodeWorld, nodeTime, nodeId);
                    clonedState.loadFrom(previouStateChunk);
                    castedNode._index_stateChunk = clonedState.index();
                    this._space.unmark(previouStateChunk.index());
                }
                else {
                    clonedState = previouStateChunk;
                }
                castedNode._world_magic = -1;
                castedNode._super_time_magic = -1;
                castedNode._time_magic = -1;
                if (previousWorld == nodeWorld || nodeWorldOrder.get(nodeWorld) != greycat.internal.CoreConstants.NULL_LONG) {
                    var timeTree = this._space.get(castedNode._index_timeTree);
                    var superTreeSize = superTimeTree.size();
                    var threshold = greycat.internal.CoreConstants.SCALE_1 * 2;
                    if (superTreeSize > threshold) {
                        threshold = greycat.internal.CoreConstants.SCALE_2 * 2;
                    }
                    if (superTreeSize > threshold) {
                        threshold = greycat.internal.CoreConstants.SCALE_3 * 2;
                    }
                    if (superTreeSize > threshold) {
                        threshold = greycat.internal.CoreConstants.SCALE_4 * 2;
                    }
                    timeTree.insert(nodeTime);
                    if (timeTree.size() == threshold) {
                        var medianPoint_1 = new Float64Array([-1]);
                        timeTree.range(greycat.internal.CoreConstants.BEGINNING_OF_TIME, greycat.internal.CoreConstants.END_OF_TIME, timeTree.size() / 2, function (t) {
                            {
                                medianPoint_1[0] = t;
                            }
                        });
                        var rightTree = this._space.createAndMark(greycat.chunk.ChunkType.TIME_TREE_CHUNK, nodeWorld, medianPoint_1[0], nodeId);
                        var finalRightTree_1 = rightTree;
                        timeTree.range(greycat.internal.CoreConstants.BEGINNING_OF_TIME, greycat.internal.CoreConstants.END_OF_TIME, timeTree.size() / 2, function (t) {
                            {
                                finalRightTree_1.unsafe_insert(t);
                            }
                        });
                        this._space.notifyUpdate(finalRightTree_1.index());
                        superTimeTree.insert(medianPoint_1[0]);
                        timeTree.clearAt(medianPoint_1[0]);
                        if (nodeTime < medianPoint_1[0]) {
                            this._space.unmark(rightTree.index());
                        }
                        else {
                            castedNode._index_timeTree = finalRightTree_1.index();
                            this._space.unmark(timeTree.index());
                        }
                    }
                }
                else {
                    var newSuperTimeTree = this._space.createAndMark(greycat.chunk.ChunkType.TIME_TREE_CHUNK, nodeWorld, greycat.internal.CoreConstants.NULL_LONG, nodeId);
                    newSuperTimeTree.insert(nodeTime);
                    var newTimeTree = this._space.createAndMark(greycat.chunk.ChunkType.TIME_TREE_CHUNK, nodeWorld, nodeTime, nodeId);
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
            };
            MWGResolver.prototype.newState = function (node, world, time) {
                var castedNode = node;
                var resolved;
                castedNode.cacheLock();
                var fakeNode = new greycat.base.BaseNode(world, time, node.id(), node.graph());
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
            };
            MWGResolver.prototype.resolveTimepoints = function (node, beginningOfSearch, endOfSearch, callback) {
                var selfPointer = this;
                this._space.getOrLoadAndMark(greycat.chunk.ChunkType.WORLD_ORDER_CHUNK, 0, 0, node.id(), function (resolved) {
                    {
                        if (resolved == null) {
                            callback(new Float64Array(0));
                            return;
                        }
                        var objectWorldOrder = resolved;
                        var collectionSize = new Int32Array([greycat.internal.CoreConstants.MAP_INITIAL_CAPACITY]);
                        var collectedWorlds = [new Float64Array(collectionSize[0])];
                        var collectedIndex = 0;
                        var currentWorld = node.world();
                        while (currentWorld != greycat.internal.CoreConstants.NULL_LONG) {
                            var divergenceTimepoint = objectWorldOrder.get(currentWorld);
                            if (divergenceTimepoint != greycat.internal.CoreConstants.NULL_LONG) {
                                if (divergenceTimepoint <= beginningOfSearch) {
                                    collectedWorlds[0][collectedIndex] = currentWorld;
                                    collectedIndex++;
                                    break;
                                }
                                else if (divergenceTimepoint > endOfSearch) {
                                    currentWorld = selfPointer.globalWorldOrderChunk.get(currentWorld);
                                }
                                else {
                                    collectedWorlds[0][collectedIndex] = currentWorld;
                                    collectedIndex++;
                                    if (collectedIndex == collectionSize[0]) {
                                        var temp_collectedWorlds = new Float64Array(collectionSize[0] * 2);
                                        java.lang.System.arraycopy(collectedWorlds[0], 0, temp_collectedWorlds, 0, collectionSize[0]);
                                        collectedWorlds[0] = temp_collectedWorlds;
                                        collectionSize[0] = collectionSize[0] * 2;
                                    }
                                    currentWorld = selfPointer.globalWorldOrderChunk.get(currentWorld);
                                }
                            }
                            else {
                                currentWorld = selfPointer.globalWorldOrderChunk.get(currentWorld);
                            }
                        }
                        selfPointer.resolveTimepointsFromWorlds(objectWorldOrder, node, beginningOfSearch, endOfSearch, collectedWorlds[0], collectedIndex, callback);
                    }
                });
            };
            MWGResolver.prototype.resolveTimepointsFromWorlds = function (objectWorldOrder, node, beginningOfSearch, endOfSearch, collectedWorlds, collectedWorldsSize, callback) {
                var selfPointer = this;
                var timeTreeKeys = new Float64Array(collectedWorldsSize * 3);
                var types = new Int8Array(collectedWorldsSize);
                for (var i = 0; i < collectedWorldsSize; i++) {
                    timeTreeKeys[i * 3] = collectedWorlds[i];
                    timeTreeKeys[i * 3 + 1] = greycat.internal.CoreConstants.NULL_LONG;
                    timeTreeKeys[i * 3 + 2] = node.id();
                    types[i] = greycat.chunk.ChunkType.TIME_TREE_CHUNK;
                }
                this.getOrLoadAndMarkAll(types, timeTreeKeys, function (superTimeTrees) {
                    {
                        if (superTimeTrees == null) {
                            selfPointer._space.unmark(objectWorldOrder.index());
                            callback(new Float64Array(0));
                        }
                        else {
                            var collectedSize_1 = new Int32Array([greycat.internal.CoreConstants.MAP_INITIAL_CAPACITY]);
                            var collectedSuperTimes_1 = [new Float64Array(collectedSize_1[0])];
                            var collectedSuperTimesAssociatedWorlds_1 = [new Float64Array(collectedSize_1[0])];
                            var insert_index_1 = new Int32Array([0]);
                            var previousDivergenceTime = endOfSearch;
                            var _loop_2 = function (i) {
                                var timeTree = superTimeTrees[i];
                                if (timeTree != null) {
                                    var currentDivergenceTime = objectWorldOrder.get(collectedWorlds[i]);
                                    var finalPreviousDivergenceTime_1 = previousDivergenceTime;
                                    timeTree.range(currentDivergenceTime, previousDivergenceTime, greycat.internal.CoreConstants.END_OF_TIME, function (t) {
                                        {
                                            if (t != finalPreviousDivergenceTime_1) {
                                                collectedSuperTimes_1[0][insert_index_1[0]] = t;
                                                collectedSuperTimesAssociatedWorlds_1[0][insert_index_1[0]] = timeTree.world();
                                                insert_index_1[0]++;
                                                if (collectedSize_1[0] == insert_index_1[0]) {
                                                    var temp_collectedSuperTimes = new Float64Array(collectedSize_1[0] * 2);
                                                    var temp_collectedSuperTimesAssociatedWorlds = new Float64Array(collectedSize_1[0] * 2);
                                                    java.lang.System.arraycopy(collectedSuperTimes_1[0], 0, temp_collectedSuperTimes, 0, collectedSize_1[0]);
                                                    java.lang.System.arraycopy(collectedSuperTimesAssociatedWorlds_1[0], 0, temp_collectedSuperTimesAssociatedWorlds, 0, collectedSize_1[0]);
                                                    collectedSuperTimes_1[0] = temp_collectedSuperTimes;
                                                    collectedSuperTimesAssociatedWorlds_1[0] = temp_collectedSuperTimesAssociatedWorlds;
                                                    collectedSize_1[0] = collectedSize_1[0] * 2;
                                                }
                                            }
                                        }
                                    });
                                    previousDivergenceTime = currentDivergenceTime;
                                }
                                selfPointer._space.unmark(timeTree.index());
                            };
                            for (var i = 0; i < collectedWorldsSize; i++) {
                                _loop_2(i);
                            }
                            selfPointer.resolveTimepointsFromSuperTimes(objectWorldOrder, node, beginningOfSearch, endOfSearch, collectedSuperTimesAssociatedWorlds_1[0], collectedSuperTimes_1[0], insert_index_1[0], callback);
                        }
                    }
                });
            };
            MWGResolver.prototype.resolveTimepointsFromSuperTimes = function (objectWorldOrder, node, beginningOfSearch, endOfSearch, collectedWorlds, collectedSuperTimes, collectedSize, callback) {
                var selfPointer = this;
                var timeTreeKeys = new Float64Array(collectedSize * 3);
                var types = new Int8Array(collectedSize);
                for (var i = 0; i < collectedSize; i++) {
                    timeTreeKeys[i * 3] = collectedWorlds[i];
                    timeTreeKeys[i * 3 + 1] = collectedSuperTimes[i];
                    timeTreeKeys[i * 3 + 2] = node.id();
                    types[i] = greycat.chunk.ChunkType.TIME_TREE_CHUNK;
                }
                this.getOrLoadAndMarkAll(types, timeTreeKeys, function (timeTrees) {
                    {
                        if (timeTrees == null) {
                            selfPointer._space.unmark(objectWorldOrder.index());
                            callback(new Float64Array(0));
                        }
                        else {
                            var collectedTimesSize_1 = new Int32Array([greycat.internal.CoreConstants.MAP_INITIAL_CAPACITY]);
                            var collectedTimes_1 = [new Float64Array(collectedTimesSize_1[0])];
                            var insert_index_2 = new Int32Array([0]);
                            var previousDivergenceTime = endOfSearch;
                            var _loop_3 = function (i) {
                                var timeTree = timeTrees[i];
                                if (timeTree != null) {
                                    var currentDivergenceTime = objectWorldOrder.get(collectedWorlds[i]);
                                    if (currentDivergenceTime < beginningOfSearch) {
                                        currentDivergenceTime = beginningOfSearch;
                                    }
                                    var finalPreviousDivergenceTime_2 = previousDivergenceTime;
                                    timeTree.range(currentDivergenceTime, previousDivergenceTime, greycat.internal.CoreConstants.END_OF_TIME, function (t) {
                                        {
                                            if (t != finalPreviousDivergenceTime_2) {
                                                collectedTimes_1[0][insert_index_2[0]] = t;
                                                insert_index_2[0]++;
                                                if (collectedTimesSize_1[0] == insert_index_2[0]) {
                                                    var temp_collectedTimes = new Float64Array(collectedTimesSize_1[0] * 2);
                                                    java.lang.System.arraycopy(collectedTimes_1[0], 0, temp_collectedTimes, 0, collectedTimesSize_1[0]);
                                                    collectedTimes_1[0] = temp_collectedTimes;
                                                    collectedTimesSize_1[0] = collectedTimesSize_1[0] * 2;
                                                }
                                            }
                                        }
                                    });
                                    if (i < collectedSize - 1) {
                                        if (collectedWorlds[i + 1] != collectedWorlds[i]) {
                                            previousDivergenceTime = currentDivergenceTime;
                                        }
                                    }
                                }
                                selfPointer._space.unmark(timeTree.index());
                            };
                            for (var i = 0; i < collectedSize; i++) {
                                _loop_3(i);
                            }
                            if (insert_index_2[0] != collectedTimesSize_1[0]) {
                                var tempTimeline = new Float64Array(insert_index_2[0]);
                                java.lang.System.arraycopy(collectedTimes_1[0], 0, tempTimeline, 0, insert_index_2[0]);
                                collectedTimes_1[0] = tempTimeline;
                            }
                            selfPointer._space.unmark(objectWorldOrder.index());
                            callback(collectedTimes_1[0]);
                        }
                    }
                });
            };
            MWGResolver.prototype.stringToHash = function (name, insertIfNotExists) {
                var hash = greycat.utility.HashHelper.hash(name);
                if (insertIfNotExists) {
                    var dictionaryIndex = this.dictionary.get(0);
                    if (dictionaryIndex == null) {
                        dictionaryIndex = this.dictionary.getOrCreate(0, greycat.Type.STRING_TO_INT_MAP);
                    }
                    if (!dictionaryIndex.containsHash(hash)) {
                        dictionaryIndex.put(name, hash);
                    }
                }
                return hash;
            };
            MWGResolver.prototype.hashToString = function (key) {
                var dictionaryIndex = this.dictionary.get(0);
                if (dictionaryIndex != null) {
                    return dictionaryIndex.getByHash(key);
                }
                return null;
            };
            return MWGResolver;
        }());
        MWGResolver.KEY_SIZE = 3;
        internal.MWGResolver = MWGResolver;
        var ReadOnlyStorage = (function () {
            function ReadOnlyStorage(toWrap) {
                this.wrapped = toWrap;
            }
            ReadOnlyStorage.prototype.get = function (keys, callback) {
                this.wrapped.get(keys, callback);
            };
            ReadOnlyStorage.prototype.put = function (stream, callback) {
                console.error("WARNING: PUT TO A READ ONLY STORAGE");
            };
            ReadOnlyStorage.prototype.remove = function (keys, callback) {
                console.error("WARNING: REMOVE TO A READ ONLY STORAGE");
            };
            ReadOnlyStorage.prototype.connect = function (graph, callback) {
                this.wrapped.connect(graph, callback);
            };
            ReadOnlyStorage.prototype.disconnect = function (callback) {
                this.wrapped.disconnect(callback);
            };
            ReadOnlyStorage.prototype.lock = function (callback) {
                this.wrapped.lock(callback);
            };
            ReadOnlyStorage.prototype.unlock = function (previousLock, callback) {
                this.wrapped.unlock(previousLock, callback);
            };
            return ReadOnlyStorage;
        }());
        internal.ReadOnlyStorage = ReadOnlyStorage;
        var heap;
        (function (heap) {
            var HeapAtomicByteArray = (function () {
                function HeapAtomicByteArray(initialSize) {
                    this._back = new Int8Array(initialSize);
                }
                HeapAtomicByteArray.prototype.get = function (index) {
                    return this._back[index];
                };
                HeapAtomicByteArray.prototype.set = function (index, value) {
                    this._back[index] = value;
                };
                return HeapAtomicByteArray;
            }());
            heap.HeapAtomicByteArray = HeapAtomicByteArray;
            var HeapBuffer = (function () {
                function HeapBuffer() {
                }
                HeapBuffer.prototype.slice = function (initPos, endPos) {
                    var newSize = (endPos - initPos + 1);
                    var newResult = new Int8Array(newSize);
                    java.lang.System.arraycopy(this.buffer, initPos, newResult, 0, newSize);
                    return newResult;
                };
                HeapBuffer.prototype.write = function (b) {
                    if (this.buffer == null) {
                        this.buffer = new Int8Array(greycat.internal.CoreConstants.MAP_INITIAL_CAPACITY);
                        this.buffer[0] = b;
                        this.writeCursor = 1;
                    }
                    else if (this.writeCursor == this.buffer.length) {
                        var temp = new Int8Array(this.buffer.length * 2);
                        java.lang.System.arraycopy(this.buffer, 0, temp, 0, this.buffer.length);
                        temp[this.writeCursor] = b;
                        this.writeCursor++;
                        this.buffer = temp;
                    }
                    else {
                        this.buffer[this.writeCursor] = b;
                        this.writeCursor++;
                    }
                };
                HeapBuffer.prototype.getNewSize = function (old, target) {
                    while (old < target) {
                        old = old * 2;
                    }
                    return old;
                };
                HeapBuffer.prototype.writeAll = function (bytes) {
                    if (this.buffer == null) {
                        var initSize = this.getNewSize(greycat.internal.CoreConstants.MAP_INITIAL_CAPACITY, bytes.length);
                        this.buffer = new Int8Array(initSize);
                        java.lang.System.arraycopy(bytes, 0, this.buffer, 0, bytes.length);
                        this.writeCursor = bytes.length;
                    }
                    else if (this.writeCursor + bytes.length > this.buffer.length) {
                        var newSize = this.getNewSize(this.buffer.length, this.buffer.length + bytes.length);
                        var tmp = new Int8Array(newSize);
                        java.lang.System.arraycopy(this.buffer, 0, tmp, 0, this.buffer.length);
                        java.lang.System.arraycopy(bytes, 0, tmp, this.writeCursor, bytes.length);
                        this.buffer = tmp;
                        this.writeCursor = this.writeCursor + bytes.length;
                    }
                    else {
                        java.lang.System.arraycopy(bytes, 0, this.buffer, this.writeCursor, bytes.length);
                        this.writeCursor = this.writeCursor + bytes.length;
                    }
                };
                HeapBuffer.prototype.read = function (position) {
                    return this.buffer[position];
                };
                HeapBuffer.prototype.data = function () {
                    var copy = new Int8Array(this.writeCursor);
                    if (this.buffer != null) {
                        java.lang.System.arraycopy(this.buffer, 0, copy, 0, this.writeCursor);
                    }
                    return copy;
                };
                HeapBuffer.prototype.length = function () {
                    return this.writeCursor;
                };
                HeapBuffer.prototype.free = function () {
                    this.buffer = null;
                };
                HeapBuffer.prototype.iterator = function () {
                    return new greycat.utility.DefaultBufferIterator(this);
                };
                HeapBuffer.prototype.removeLast = function () {
                    this.writeCursor--;
                };
                HeapBuffer.prototype.toString = function () {
                    return String.fromCharCode.apply(null, this.data());
                };
                return HeapBuffer;
            }());
            heap.HeapBuffer = HeapBuffer;
            var HeapChunkSpace = (function () {
                function HeapChunkSpace(initialCapacity, p_graph, deepWorldPriority) {
                    this._deep_priority = deepWorldPriority;
                    this._graph = p_graph;
                    this._maxEntries = initialCapacity;
                    this._hashEntries = initialCapacity * HeapChunkSpace.HASH_LOAD_FACTOR;
                    this._lru = new greycat.internal.heap.HeapFixedStack(initialCapacity, true);
                    this._dirtiesStack = new greycat.internal.heap.HeapFixedStack(initialCapacity, false);
                    this._hashNext = new java.util.concurrent.atomic.AtomicIntegerArray(initialCapacity);
                    this._hash = new java.util.concurrent.atomic.AtomicIntegerArray(this._hashEntries);
                    for (var i = 0; i < initialCapacity; i++) {
                        this._hashNext.set(i, -1);
                    }
                    for (var i = 0; i < this._hashEntries; i++) {
                        this._hash.set(i, -1);
                    }
                    this._chunkValues = new java.util.concurrent.atomic.AtomicReferenceArray(initialCapacity);
                    this._chunkWorlds = new java.util.concurrent.atomic.AtomicLongArray(this._maxEntries);
                    this._chunkTimes = new java.util.concurrent.atomic.AtomicLongArray(this._maxEntries);
                    this._chunkIds = new java.util.concurrent.atomic.AtomicLongArray(this._maxEntries);
                    this._chunkTypes = new greycat.internal.heap.HeapAtomicByteArray(this._maxEntries);
                    this._chunkMarks = new java.util.concurrent.atomic.AtomicLongArray(this._maxEntries);
                    for (var i = 0; i < this._maxEntries; i++) {
                        this._chunkMarks.set(i, 0);
                    }
                }
                HeapChunkSpace.prototype.graph = function () {
                    return this._graph;
                };
                HeapChunkSpace.prototype.worldByIndex = function (index) {
                    return this._chunkWorlds.get(index);
                };
                HeapChunkSpace.prototype.timeByIndex = function (index) {
                    return this._chunkTimes.get(index);
                };
                HeapChunkSpace.prototype.idByIndex = function (index) {
                    return this._chunkIds.get(index);
                };
                HeapChunkSpace.prototype.getAndMark = function (type, world, time, id) {
                    var index;
                    if (this._deep_priority) {
                        index = greycat.utility.HashHelper.tripleHash(type, world, time, id, this._hashEntries);
                    }
                    else {
                        index = greycat.utility.HashHelper.simpleTripleHash(type, world, time, id, this._hashEntries);
                    }
                    var m = this._hash.get(index);
                    var found = -1;
                    while (m != -1) {
                        if (this._chunkTypes.get(m) == type && this._chunkWorlds.get(m) == world && this._chunkTimes.get(m) == time && this._chunkIds.get(m) == id) {
                            if (this.mark(m) > 0) {
                                found = m;
                            }
                            break;
                        }
                        else {
                            m = this._hashNext.get(m);
                        }
                    }
                    if (found != -1) {
                        return this._chunkValues.get(found);
                    }
                    else {
                        return null;
                    }
                };
                HeapChunkSpace.prototype.get = function (index) {
                    return this._chunkValues.get(index);
                };
                HeapChunkSpace.prototype.getOrLoadAndMark = function (type, world, time, id, callback) {
                    var _this = this;
                    var fromMemory = this.getAndMark(type, world, time, id);
                    if (fromMemory != null) {
                        callback(fromMemory);
                    }
                    else {
                        var keys_2 = this.graph().newBuffer();
                        greycat.utility.KeyHelper.keyToBuffer(keys_2, type, world, time, id);
                        this.graph().storage().get(keys_2, function (result) {
                            {
                                if (result != null && result.length() > 0) {
                                    var loadedChunk = _this.createAndMark(type, world, time, id);
                                    loadedChunk.load(result);
                                    result.free();
                                    callback(loadedChunk);
                                }
                                else {
                                    keys_2.free();
                                    callback(null);
                                }
                            }
                        });
                    }
                };
                HeapChunkSpace.prototype.getOrLoadAndMarkAll = function (keys, callback) {
                    var _this = this;
                    var querySize = keys.length / greycat.Constants.KEY_SIZE;
                    var finalResult = new Array(querySize);
                    var reverse = null;
                    var reverseIndex = 0;
                    var toLoadKeys = null;
                    for (var i = 0; i < querySize; i++) {
                        var offset = i * greycat.Constants.KEY_SIZE;
                        var loopType = keys[offset];
                        if (loopType != -1) {
                            var fromMemory = this.getAndMark(keys[offset], keys[offset + 1], keys[offset + 2], keys[offset + 3]);
                            if (fromMemory != null) {
                                finalResult[i] = fromMemory;
                            }
                            else {
                                if (reverse == null) {
                                    reverse = new Int32Array(querySize);
                                    toLoadKeys = this.graph().newBuffer();
                                }
                                reverse[reverseIndex] = i;
                                if (reverseIndex != 0) {
                                    toLoadKeys.write(greycat.Constants.BUFFER_SEP);
                                }
                                greycat.utility.KeyHelper.keyToBuffer(toLoadKeys, keys[offset], keys[offset + 1], keys[offset + 2], keys[offset + 3]);
                                reverseIndex++;
                            }
                        }
                        else {
                            finalResult[i] = null;
                        }
                    }
                    if (reverse != null) {
                        var finalReverse_1 = reverse;
                        this.graph().storage().get(toLoadKeys, function (loadAllResult) {
                            {
                                var it = loadAllResult.iterator();
                                var i = 0;
                                while (it.hasNext()) {
                                    var view = it.next();
                                    var reversedIndex = finalReverse_1[i];
                                    var reversedOffset = reversedIndex * greycat.Constants.KEY_SIZE;
                                    if (view.length() > 0) {
                                        var loadedChunk = _this.createAndMark(keys[reversedOffset], keys[reversedOffset + 1], keys[reversedOffset + 2], keys[reversedOffset + 3]);
                                        loadedChunk.load(view);
                                        finalResult[reversedIndex] = loadedChunk;
                                    }
                                    else {
                                        finalResult[reversedIndex] = null;
                                    }
                                    i++;
                                }
                                loadAllResult.free();
                                callback(finalResult);
                            }
                        });
                    }
                    else {
                        callback(finalResult);
                    }
                };
                HeapChunkSpace.prototype.mark = function (index) {
                    var castedIndex = index;
                    var before;
                    var after;
                    do {
                        before = this._chunkMarks.get(castedIndex);
                        if (before != -1) {
                            after = before + 1;
                        }
                        else {
                            after = before;
                        }
                    } while (!this._chunkMarks.compareAndSet(castedIndex, before, after));
                    if (before == 0 && after == 1) {
                        this._lru.dequeue(index);
                    }
                    return after;
                };
                HeapChunkSpace.prototype.unmark = function (index) {
                    var castedIndex = index;
                    var before;
                    var after;
                    do {
                        before = this._chunkMarks.get(castedIndex);
                        if (before > 0) {
                            after = before - 1;
                        }
                        else {
                            console.error("WARNING: DOUBLE UNMARK");
                            after = before;
                        }
                    } while (!this._chunkMarks.compareAndSet(castedIndex, before, after));
                    if (before == 1 && after == 0) {
                        this._lru.enqueue(index);
                    }
                };
                HeapChunkSpace.prototype.free = function (chunk) { };
                HeapChunkSpace.prototype.createAndMark = function (type, world, time, id) {
                    var entry = -1;
                    var hashIndex;
                    if (this._deep_priority) {
                        hashIndex = greycat.utility.HashHelper.tripleHash(type, world, time, id, this._hashEntries);
                    }
                    else {
                        hashIndex = greycat.utility.HashHelper.simpleTripleHash(type, world, time, id, this._hashEntries);
                    }
                    var m = this._hash.get(hashIndex);
                    while (m >= 0) {
                        if (type == this._chunkTypes.get(m) && world == this._chunkWorlds.get(m) && time == this._chunkTimes.get(m) && id == this._chunkIds.get(m)) {
                            entry = m;
                            break;
                        }
                        m = this._hashNext.get(m);
                    }
                    if (entry != -1) {
                        var previous = void 0;
                        var after = void 0;
                        do {
                            previous = this._chunkMarks.get(entry);
                            if (previous != -1) {
                                after = previous + 1;
                            }
                            else {
                                after = previous;
                            }
                        } while (!this._chunkMarks.compareAndSet(entry, previous, after));
                        if (after == (previous + 1)) {
                            return this._chunkValues.get(entry);
                        }
                    }
                    var currentVictimIndex = -1;
                    while (currentVictimIndex == -1) {
                        var temp_victim = this._lru.dequeueTail();
                        if (temp_victim == -1) {
                            break;
                        }
                        else {
                            if (this._chunkMarks.compareAndSet(temp_victim, 0, -1)) {
                                currentVictimIndex = temp_victim;
                            }
                        }
                    }
                    if (currentVictimIndex == -1) {
                        throw new Error("mwDB crashed, cache is full, please avoid to much retention of nodes or augment cache capacity! available:" + this.available());
                    }
                    var toInsert = null;
                    switch (type) {
                        case greycat.chunk.ChunkType.STATE_CHUNK:
                            toInsert = new greycat.internal.heap.HeapStateChunk(this, currentVictimIndex);
                            break;
                        case greycat.chunk.ChunkType.WORLD_ORDER_CHUNK:
                            toInsert = new greycat.internal.heap.HeapWorldOrderChunk(this, currentVictimIndex);
                            break;
                        case greycat.chunk.ChunkType.TIME_TREE_CHUNK:
                            toInsert = new greycat.internal.heap.HeapTimeTreeChunk(this, currentVictimIndex);
                            break;
                        case greycat.chunk.ChunkType.GEN_CHUNK:
                            toInsert = new greycat.internal.heap.HeapGenChunk(this, id, currentVictimIndex);
                            break;
                    }
                    if (this._chunkValues.get(currentVictimIndex) != null) {
                        var victimWorld = this._chunkWorlds.get(currentVictimIndex);
                        var victimTime = this._chunkTimes.get(currentVictimIndex);
                        var victimObj = this._chunkIds.get(currentVictimIndex);
                        var victimType = this._chunkTypes.get(currentVictimIndex);
                        var indexVictim = void 0;
                        if (this._deep_priority) {
                            indexVictim = greycat.utility.HashHelper.tripleHash(victimType, victimWorld, victimTime, victimObj, this._hashEntries);
                        }
                        else {
                            indexVictim = greycat.utility.HashHelper.simpleTripleHash(victimType, victimWorld, victimTime, victimObj, this._hashEntries);
                        }
                        m = this._hash.get(indexVictim);
                        var last = -1;
                        while (m >= 0) {
                            if (victimType == this._chunkTypes.get(m) && victimWorld == this._chunkWorlds.get(m) && victimTime == this._chunkTimes.get(m) && victimObj == this._chunkIds.get(m)) {
                                break;
                            }
                            last = m;
                            m = this._hashNext.get(m);
                        }
                        if (last == -1) {
                            var previousNext = this._hashNext.get(m);
                            this._hash.set(indexVictim, previousNext);
                        }
                        else {
                            if (m == -1) {
                                this._hashNext.set(last, -1);
                            }
                            else {
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
                };
                HeapChunkSpace.prototype.notifyUpdate = function (index) {
                    if (this._dirtiesStack.enqueue(index)) {
                        this.mark(index);
                    }
                };
                HeapChunkSpace.prototype.save = function (callback) {
                    var stream = this._graph.newBuffer();
                    var isFirst = true;
                    while (this._dirtiesStack.size() != 0) {
                        var tail = this._dirtiesStack.dequeueTail();
                        var loopChunk = this._chunkValues.get(tail);
                        if (isFirst) {
                            isFirst = false;
                        }
                        else {
                            stream.write(greycat.Constants.BUFFER_SEP);
                        }
                        greycat.utility.KeyHelper.keyToBuffer(stream, this._chunkTypes.get(tail), this._chunkWorlds.get(tail), this._chunkTimes.get(tail), this._chunkIds.get(tail));
                        stream.write(greycat.Constants.BUFFER_SEP);
                        try {
                            loopChunk.save(stream);
                            this.unmark(tail);
                        }
                        catch ($ex$) {
                            if ($ex$ instanceof Error) {
                                var e = $ex$;
                                {
                                    console.error(e);
                                }
                            }
                            else {
                                throw $ex$;
                            }
                        }
                    }
                    this.graph().storage().put(stream, function (result) {
                        {
                            stream.free();
                            if (callback != null) {
                                callback(result);
                            }
                        }
                    });
                };
                HeapChunkSpace.prototype.clear = function () { };
                HeapChunkSpace.prototype.freeAll = function () { };
                HeapChunkSpace.prototype.available = function () {
                    return this._lru.size();
                };
                HeapChunkSpace.prototype.newVolatileGraph = function () {
                    return new greycat.internal.heap.HeapEGraph(null, null, this._graph);
                };
                HeapChunkSpace.prototype.printMarked = function () {
                    for (var i = 0; i < this._chunkValues.length(); i++) {
                        if (this._chunkValues.get(i) != null) {
                            if (this._chunkMarks.get(i) != 0) {
                                switch (this._chunkTypes.get(i)) {
                                    case greycat.chunk.ChunkType.STATE_CHUNK:
                                        console.log("STATE(" + this._chunkWorlds.get(i) + "," + this._chunkTimes.get(i) + "," + this._chunkIds.get(i) + ")->marks->" + this._chunkMarks.get(i));
                                        break;
                                    case greycat.chunk.ChunkType.TIME_TREE_CHUNK:
                                        console.log("TIME_TREE(" + this._chunkWorlds.get(i) + "," + this._chunkTimes.get(i) + "," + this._chunkIds.get(i) + ")->marks->" + this._chunkMarks.get(i));
                                        break;
                                    case greycat.chunk.ChunkType.WORLD_ORDER_CHUNK:
                                        console.log("WORLD_ORDER(" + this._chunkWorlds.get(i) + "," + this._chunkTimes.get(i) + "," + this._chunkIds.get(i) + ")->marks->" + this._chunkMarks.get(i));
                                        break;
                                    case greycat.chunk.ChunkType.GEN_CHUNK:
                                        console.log("GENERATOR(" + this._chunkWorlds.get(i) + "," + this._chunkTimes.get(i) + "," + this._chunkIds.get(i) + ")->marks->" + this._chunkMarks.get(i));
                                        break;
                                }
                            }
                        }
                    }
                };
                return HeapChunkSpace;
            }());
            HeapChunkSpace.HASH_LOAD_FACTOR = 4;
            heap.HeapChunkSpace = HeapChunkSpace;
            var HeapDMatrix = (function () {
                function HeapDMatrix(p_parent, origin) {
                    this.backend = null;
                    this.aligned = true;
                    this.parent = p_parent;
                    if (origin != null) {
                        this.aligned = false;
                        this.backend = origin.backend;
                    }
                }
                HeapDMatrix.prototype.init = function (rows, columns) {
                    {
                        this.internal_init(rows, columns);
                    }
                    this.parent.declareDirty();
                    return this;
                };
                HeapDMatrix.prototype.internal_init = function (rows, columns) {
                    this.backend = new Float64Array(rows * columns + HeapDMatrix.INDEX_OFFSET);
                    this.backend[HeapDMatrix.INDEX_ROWS] = rows;
                    this.backend[HeapDMatrix.INDEX_COLUMNS] = columns;
                    this.backend[HeapDMatrix.INDEX_MAX_COLUMN] = columns;
                    this.aligned = true;
                };
                HeapDMatrix.prototype.appendColumn = function (newColumn) {
                    {
                        this.internal_appendColumn(newColumn);
                        this.parent.declareDirty();
                    }
                    return this;
                };
                HeapDMatrix.prototype.internal_appendColumn = function (newColumn) {
                    var nbRows;
                    var nbColumns;
                    var nbMaxColumn;
                    if (this.backend == null) {
                        nbRows = newColumn.length;
                        nbColumns = greycat.Constants.MAP_INITIAL_CAPACITY;
                        nbMaxColumn = 0;
                        this.backend = new Float64Array(nbRows * nbColumns + HeapDMatrix.INDEX_OFFSET);
                        this.backend[HeapDMatrix.INDEX_ROWS] = nbRows;
                        this.backend[HeapDMatrix.INDEX_COLUMNS] = nbColumns;
                        this.backend[HeapDMatrix.INDEX_MAX_COLUMN] = nbMaxColumn;
                    }
                    else {
                        nbColumns = this.backend[HeapDMatrix.INDEX_COLUMNS];
                        nbRows = this.backend[HeapDMatrix.INDEX_ROWS];
                        nbMaxColumn = this.backend[HeapDMatrix.INDEX_MAX_COLUMN];
                    }
                    if (!this.aligned || nbMaxColumn == nbColumns) {
                        if (nbMaxColumn == nbColumns) {
                            nbColumns = nbColumns * 2;
                            this.backend[HeapDMatrix.INDEX_COLUMNS] = nbColumns;
                            var newLength = nbColumns * nbRows + HeapDMatrix.INDEX_OFFSET;
                            var next_backend = new Float64Array(newLength);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                        else {
                            var next_backend = new Float64Array(this.backend.length);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                    }
                    java.lang.System.arraycopy(newColumn, 0, this.backend, (nbMaxColumn * nbRows) + HeapDMatrix.INDEX_OFFSET, newColumn.length);
                    this.backend[HeapDMatrix.INDEX_MAX_COLUMN] = nbMaxColumn + 1;
                };
                HeapDMatrix.prototype.fill = function (value) {
                    {
                        this.internal_fill(value);
                    }
                    return this;
                };
                HeapDMatrix.prototype.internal_fill = function (value) {
                    if (this.backend != null) {
                        if (!this.aligned) {
                            var next_backend = new Float64Array(this.backend.length);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                        java.util.Arrays.fill(this.backend, HeapDMatrix.INDEX_OFFSET, this.backend.length - HeapDMatrix.INDEX_OFFSET, value);
                        this.backend[HeapDMatrix.INDEX_MAX_COLUMN] = this.backend[HeapDMatrix.INDEX_COLUMNS];
                        this.parent.declareDirty();
                    }
                };
                HeapDMatrix.prototype.fillWith = function (values) {
                    {
                        this.internal_fillWith(values);
                    }
                    return this;
                };
                HeapDMatrix.prototype.fillWithRandom = function (random, min, max) {
                    {
                        if (this.backend != null) {
                            if (!this.aligned) {
                                var next_backend = new Float64Array(this.backend.length);
                                java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                                this.backend = next_backend;
                                this.aligned = true;
                            }
                            for (var i = 0; i < this.backend[HeapDMatrix.INDEX_ROWS] * this.backend[HeapDMatrix.INDEX_COLUMNS]; i++) {
                                this.backend[i + HeapDMatrix.INDEX_OFFSET] = random.nextInt() * (max - min) + min;
                            }
                            this.parent.declareDirty();
                        }
                    }
                    return this;
                };
                HeapDMatrix.prototype.fillWithRandomStd = function (random, std) {
                    {
                        if (this.backend != null) {
                            if (!this.aligned) {
                                var next_backend = new Float64Array(this.backend.length);
                                java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                                this.backend = next_backend;
                                this.aligned = true;
                            }
                            for (var i = 0; i < this.backend[HeapDMatrix.INDEX_ROWS] * this.backend[HeapDMatrix.INDEX_COLUMNS]; i++) {
                                this.backend[i + HeapDMatrix.INDEX_OFFSET] = random.nextGaussian() * std;
                            }
                            this.parent.declareDirty();
                        }
                    }
                    return this;
                };
                HeapDMatrix.prototype.internal_fillWith = function (values) {
                    if (this.backend != null) {
                        if (!this.aligned) {
                            var next_backend = new Float64Array(this.backend.length);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                        java.lang.System.arraycopy(values, 0, this.backend, HeapDMatrix.INDEX_OFFSET, values.length);
                        this.parent.declareDirty();
                    }
                };
                HeapDMatrix.prototype.rows = function () {
                    var result = 0;
                    {
                        if (this.backend != null) {
                            result = this.backend[HeapDMatrix.INDEX_ROWS];
                        }
                    }
                    return result;
                };
                HeapDMatrix.prototype.columns = function () {
                    var result = 0;
                    {
                        if (this.backend != null) {
                            result = this.backend[HeapDMatrix.INDEX_MAX_COLUMN];
                        }
                    }
                    return result;
                };
                HeapDMatrix.prototype.column = function (index) {
                    var result;
                    {
                        var nbRows = this.backend[HeapDMatrix.INDEX_ROWS];
                        result = new Float64Array(nbRows);
                        java.lang.System.arraycopy(this.backend, HeapDMatrix.INDEX_OFFSET + (index * nbRows), result, 0, nbRows);
                    }
                    return result;
                };
                HeapDMatrix.prototype.get = function (rowIndex, columnIndex) {
                    var result = 0;
                    {
                        if (this.backend != null) {
                            var nbRows = this.backend[HeapDMatrix.INDEX_ROWS];
                            result = this.backend[HeapDMatrix.INDEX_OFFSET + rowIndex + columnIndex * nbRows];
                        }
                    }
                    return result;
                };
                HeapDMatrix.prototype.set = function (rowIndex, columnIndex, value) {
                    {
                        this.internal_set(rowIndex, columnIndex, value);
                    }
                    return this;
                };
                HeapDMatrix.prototype.internal_set = function (rowIndex, columnIndex, value) {
                    if (this.backend != null) {
                        if (!this.aligned) {
                            var next_backend = new Float64Array(this.backend.length);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                        var nbRows = this.backend[HeapDMatrix.INDEX_ROWS];
                        this.backend[HeapDMatrix.INDEX_OFFSET + rowIndex + columnIndex * nbRows] = value;
                        this.parent.declareDirty();
                    }
                };
                HeapDMatrix.prototype.add = function (rowIndex, columnIndex, value) {
                    {
                        this.internal_add(rowIndex, columnIndex, value);
                    }
                    return this;
                };
                HeapDMatrix.prototype.internal_add = function (rowIndex, columnIndex, value) {
                    if (this.backend != null) {
                        if (!this.aligned) {
                            var next_backend = new Float64Array(this.backend.length);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                        var nbRows = this.backend[HeapDMatrix.INDEX_ROWS];
                        this.backend[HeapDMatrix.INDEX_OFFSET + rowIndex + columnIndex * nbRows] = value + this.backend[HeapDMatrix.INDEX_OFFSET + rowIndex + columnIndex * nbRows];
                        this.parent.declareDirty();
                    }
                };
                HeapDMatrix.prototype.data = function () {
                    var copy = null;
                    {
                        if (this.backend != null) {
                            copy = new Float64Array(this.backend.length - HeapDMatrix.INDEX_OFFSET);
                            java.lang.System.arraycopy(this.backend, HeapDMatrix.INDEX_OFFSET, copy, 0, this.backend.length - HeapDMatrix.INDEX_OFFSET);
                        }
                    }
                    return copy;
                };
                HeapDMatrix.prototype.leadingDimension = function () {
                    if (this.backend == null) {
                        return 0;
                    }
                    return Math.max(this.backend[HeapDMatrix.INDEX_COLUMNS], this.backend[HeapDMatrix.INDEX_ROWS]);
                };
                HeapDMatrix.prototype.unsafeGet = function (index) {
                    var result = 0;
                    {
                        if (this.backend != null) {
                            result = this.backend[HeapDMatrix.INDEX_OFFSET + index];
                        }
                    }
                    return result;
                };
                HeapDMatrix.prototype.unsafeSet = function (index, value) {
                    {
                        this.internal_unsafeSet(index, value);
                    }
                    return this;
                };
                HeapDMatrix.prototype.internal_unsafeSet = function (index, value) {
                    if (this.backend != null) {
                        if (!this.aligned) {
                            var next_backend = new Float64Array(this.backend.length);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                        this.backend[HeapDMatrix.INDEX_OFFSET + index] = value;
                        this.parent.declareDirty();
                    }
                };
                HeapDMatrix.prototype.unsafe_data = function () {
                    return this.backend;
                };
                HeapDMatrix.prototype.unsafe_init = function (size) {
                    this.backend = new Float64Array(size);
                    this.backend[HeapDMatrix.INDEX_ROWS] = 0;
                    this.backend[HeapDMatrix.INDEX_COLUMNS] = 0;
                    this.aligned = true;
                };
                HeapDMatrix.prototype.unsafe_set = function (index, value) {
                    this.backend[index] = value;
                };
                HeapDMatrix.prototype.load = function (buffer, offset, max) {
                    var cursor = offset;
                    var current = buffer.read(cursor);
                    var isFirst = true;
                    var previous = offset;
                    var elemIndex = 0;
                    while (cursor < max && current != greycat.Constants.CHUNK_SEP && current != greycat.Constants.CHUNK_ENODE_SEP && current != greycat.Constants.CHUNK_ESEP) {
                        if (current == greycat.Constants.CHUNK_VAL_SEP) {
                            if (isFirst) {
                                this.unsafe_init(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                                isFirst = false;
                            }
                            else {
                                this.unsafe_set(elemIndex, greycat.utility.Base64.decodeToDoubleWithBounds(buffer, previous, cursor));
                                elemIndex++;
                            }
                            previous = cursor + 1;
                        }
                        cursor++;
                        if (cursor < max) {
                            current = buffer.read(cursor);
                        }
                    }
                    if (isFirst) {
                        this.unsafe_init(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                    }
                    else {
                        this.unsafe_set(elemIndex, greycat.utility.Base64.decodeToDoubleWithBounds(buffer, previous, cursor));
                    }
                    return cursor;
                };
                return HeapDMatrix;
            }());
            HeapDMatrix.INDEX_ROWS = 0;
            HeapDMatrix.INDEX_COLUMNS = 1;
            HeapDMatrix.INDEX_MAX_COLUMN = 2;
            HeapDMatrix.INDEX_OFFSET = 3;
            heap.HeapDMatrix = HeapDMatrix;
            var HeapEGraph = (function () {
                function HeapEGraph(p_parent, origin, p_graph) {
                    this._nodes = null;
                    this._nodes_capacity = 0;
                    this._nodes_index = 0;
                    this.parent = p_parent;
                    this._graph = p_graph;
                    if (origin != null) {
                        this._nodes_index = origin._nodes_index;
                        this._nodes_capacity = origin._nodes_capacity;
                        this._nodes = new Array(this._nodes_capacity);
                        for (var i = 0; i < this._nodes_index; i++) {
                            this._nodes[i] = new greycat.internal.heap.HeapENode(this, i, origin._nodes[i]);
                        }
                        for (var i = 0; i < this._nodes_index; i++) {
                            this._nodes[i].rebase();
                        }
                    }
                }
                HeapEGraph.prototype.size = function () {
                    return this._nodes_index;
                };
                HeapEGraph.prototype.free = function () {
                    this._nodes = null;
                    this._nodes_capacity = 0;
                    this._nodes_index = 0;
                };
                HeapEGraph.prototype.graph = function () {
                    return this._graph;
                };
                HeapEGraph.prototype.allocate = function (newCapacity) {
                    var closePowerOfTwo = Math.pow(2, Math.ceil(Math.log(newCapacity) / Math.log(2)));
                    if (closePowerOfTwo > this._nodes_capacity) {
                        var new_back = new Array(closePowerOfTwo);
                        if (this._nodes != null) {
                            java.lang.System.arraycopy(this._nodes, 0, new_back, 0, this._nodes_index);
                        }
                        this._nodes = new_back;
                        this._nodes_capacity = closePowerOfTwo;
                    }
                };
                HeapEGraph.prototype.nodeByIndex = function (index, createIfAbsent) {
                    if (index < this._nodes_capacity) {
                        if (index >= this._nodes_index) {
                            this._nodes_index = index + 1;
                        }
                        var elem = this._nodes[index];
                        if (elem == null && createIfAbsent) {
                            elem = new greycat.internal.heap.HeapENode(this, index, null);
                            this._nodes[index] = elem;
                        }
                        return elem;
                    }
                    else {
                        throw new Error("bad API usage");
                    }
                };
                HeapEGraph.prototype.declareDirty = function () {
                    if (!this._dirty) {
                        this._dirty = true;
                        if (this.parent != null) {
                            this.parent.declareDirty();
                        }
                    }
                };
                HeapEGraph.prototype.newNode = function () {
                    if (this._nodes_index == this._nodes_capacity) {
                        var newCapacity = this._nodes_capacity * 2;
                        if (newCapacity == 0) {
                            newCapacity = greycat.Constants.MAP_INITIAL_CAPACITY;
                        }
                        var newNodes = new Array(newCapacity);
                        if (this._nodes != null) {
                            java.lang.System.arraycopy(this._nodes, 0, newNodes, 0, this._nodes_capacity);
                        }
                        this._nodes_capacity = newCapacity;
                        this._nodes = newNodes;
                    }
                    var newNode = new greycat.internal.heap.HeapENode(this, this._nodes_index, null);
                    this._nodes[this._nodes_index] = newNode;
                    this._nodes_index++;
                    return newNode;
                };
                HeapEGraph.prototype.root = function () {
                    if (this._nodes_index > 0) {
                        return this._nodes[0];
                    }
                    return null;
                };
                HeapEGraph.prototype.setRoot = function (eNode) {
                    var casted = eNode;
                    var previousID = casted._id;
                    if (previousID != 0) {
                        var previousRoot = this._nodes[0];
                        this._nodes[previousID] = previousRoot;
                        previousRoot._id = previousID;
                        this._nodes[0] = casted;
                        casted._id = 0;
                    }
                    return this;
                };
                HeapEGraph.prototype.drop = function (eNode) {
                    var casted = eNode;
                    var previousId = casted._id;
                    if (previousId == this._nodes_index - 1) {
                        this._nodes[previousId] = null;
                        this._nodes_index--;
                    }
                    else {
                        this._nodes[previousId] = this._nodes[this._nodes_index - 1];
                        this._nodes[previousId]._id = previousId;
                        this._nodes_index--;
                    }
                    return this;
                };
                HeapEGraph.prototype.toString = function () {
                    var builder = new java.lang.StringBuilder();
                    builder.append("{\"nodes\":[");
                    for (var i = 0; i < this._nodes_index; i++) {
                        if (i != 0) {
                            builder.append(",");
                        }
                        builder.append(this._nodes[i].toString());
                    }
                    builder.append("]}");
                    return builder.toString();
                };
                HeapEGraph.prototype.load = function (buffer, offset, max) {
                    var cursor = offset;
                    var current = buffer.read(cursor);
                    var isFirst = true;
                    var insertIndex = 0;
                    while (cursor < max && current != greycat.Constants.CHUNK_SEP) {
                        if (current == greycat.Constants.CHUNK_ENODE_SEP) {
                            if (isFirst) {
                                this.allocate(greycat.utility.Base64.decodeToIntWithBounds(buffer, offset, cursor));
                                isFirst = false;
                            }
                            cursor++;
                            var eNode = this.nodeByIndex(insertIndex, true);
                            cursor = eNode.load(buffer, cursor, this._graph);
                            insertIndex++;
                        }
                        else {
                            cursor++;
                        }
                        current = buffer.read(cursor);
                    }
                    return cursor;
                };
                return HeapEGraph;
            }());
            heap.HeapEGraph = HeapEGraph;
            var HeapENode = (function () {
                function HeapENode(p_egraph, p_id, origin) {
                    this.egraph = p_egraph;
                    this._id = p_id;
                    if (origin != null) {
                        this._capacity = origin._capacity;
                        this._size = origin._size;
                        if (origin._k != null) {
                            var cloned_k = new Int32Array(this._capacity);
                            java.lang.System.arraycopy(origin._k, 0, cloned_k, 0, this._capacity);
                            this._k = cloned_k;
                        }
                        if (origin._type != null) {
                            var cloned_type = new Int8Array(this._capacity);
                            java.lang.System.arraycopy(origin._type, 0, cloned_type, 0, this._capacity);
                            this._type = cloned_type;
                        }
                        if (origin._next_hash != null) {
                            var cloned_hash = new Int32Array(this._capacity * 3);
                            java.lang.System.arraycopy(origin._next_hash, 0, cloned_hash, 0, this._capacity * 3);
                            this._next_hash = cloned_hash;
                        }
                        if (origin._v != null) {
                            this._v = new Array(this._capacity);
                            for (var i = 0; i < this._size; i++) {
                                switch (origin._type[i]) {
                                    case greycat.Type.LONG_TO_LONG_MAP:
                                        if (origin._v[i] != null) {
                                            this._v[i] = origin._v[i].cloneFor(this);
                                        }
                                        break;
                                    case greycat.Type.RELATION_INDEXED:
                                        if (origin._v[i] != null) {
                                            this._v[i] = origin._v[i].cloneIRelFor(this, this.egraph.graph());
                                        }
                                        break;
                                    case greycat.Type.LONG_TO_LONG_ARRAY_MAP:
                                        if (origin._v[i] != null) {
                                            this._v[i] = origin._v[i].cloneFor(this);
                                        }
                                        break;
                                    case greycat.Type.STRING_TO_INT_MAP:
                                        if (origin._v[i] != null) {
                                            this._v[i] = origin._v[i].cloneFor(this);
                                        }
                                        break;
                                    case greycat.Type.RELATION:
                                        if (origin._v[i] != null) {
                                            this._v[i] = new greycat.internal.heap.HeapRelation(this, origin._v[i]);
                                        }
                                        break;
                                    case greycat.Type.DMATRIX:
                                        if (origin._v[i] != null) {
                                            this._v[i] = new greycat.internal.heap.HeapDMatrix(this, origin._v[i]);
                                        }
                                        break;
                                    case greycat.Type.LMATRIX:
                                        if (origin._v[i] != null) {
                                            this._v[i] = new greycat.internal.heap.HeapLMatrix(this, origin._v[i]);
                                        }
                                        break;
                                    default:
                                        this._v[i] = origin._v[i];
                                        break;
                                }
                            }
                        }
                    }
                    else {
                        this._capacity = 0;
                        this._size = 0;
                    }
                }
                HeapENode.prototype.clear = function () {
                    this._capacity = 0;
                    this._size = 0;
                    this._k = null;
                    this._v = null;
                    this._next_hash = null;
                    this._type = null;
                    return this;
                };
                HeapENode.prototype.declareDirty = function () {
                    if (!this._dirty) {
                        this._dirty = true;
                        this.egraph.declareDirty();
                    }
                };
                HeapENode.prototype.rebase = function () {
                    for (var i = 0; i < this._size; i++) {
                        switch (this._type[i]) {
                            case greycat.Type.ERELATION:
                                var previousERel = this._v[i];
                                previousERel.rebase(this.egraph);
                                break;
                            case greycat.Type.ENODE:
                                var previous = this._v[i];
                                this._v[i] = this.egraph._nodes[previous._id];
                                break;
                        }
                    }
                };
                HeapENode.prototype.allocate = function (newCapacity) {
                    if (newCapacity <= this._capacity) {
                        return;
                    }
                    var ex_k = new Int32Array(newCapacity);
                    if (this._k != null) {
                        java.lang.System.arraycopy(this._k, 0, ex_k, 0, this._capacity);
                    }
                    this._k = ex_k;
                    var ex_v = new Array(newCapacity);
                    if (this._v != null) {
                        java.lang.System.arraycopy(this._v, 0, ex_v, 0, this._capacity);
                    }
                    this._v = ex_v;
                    var ex_type = new Int8Array(newCapacity);
                    if (this._type != null) {
                        java.lang.System.arraycopy(this._type, 0, ex_type, 0, this._capacity);
                    }
                    this._type = ex_type;
                    this._capacity = newCapacity;
                    this._next_hash = new Int32Array(this._capacity * 3);
                    java.util.Arrays.fill(this._next_hash, 0, this._capacity * 3, -1);
                    var double_capacity = this._capacity * 2;
                    for (var i = 0; i < this._size; i++) {
                        var keyHash = this._k[i] % double_capacity;
                        if (keyHash < 0) {
                            keyHash = keyHash * -1;
                        }
                        this._next_hash[i] = this._next_hash[this._capacity + keyHash];
                        this._next_hash[this._capacity + keyHash] = i;
                    }
                };
                HeapENode.prototype.internal_find = function (p_key) {
                    if (this._size == 0) {
                        return -1;
                    }
                    var hashIndex = p_key % (this._capacity * 2);
                    if (hashIndex < 0) {
                        hashIndex = hashIndex * -1;
                    }
                    var m = this._next_hash[this._capacity + hashIndex];
                    while (m >= 0) {
                        if (p_key == this._k[m]) {
                            return m;
                        }
                        else {
                            m = this._next_hash[m];
                        }
                    }
                    return -1;
                };
                HeapENode.prototype.internal_get = function (p_key) {
                    if (this._size == 0) {
                        return null;
                    }
                    var found = this.internal_find(p_key);
                    if (found != -1) {
                        return this._v[found];
                    }
                    return null;
                };
                HeapENode.prototype.internal_set = function (p_key, p_type, p_unsafe_elem, replaceIfPresent, initial) {
                    var param_elem = null;
                    if (p_unsafe_elem != null) {
                        try {
                            switch (p_type) {
                                case greycat.Type.BOOL:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.DOUBLE:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.LONG:
                                    if (p_unsafe_elem instanceof Number) {
                                        var preCasting = p_unsafe_elem;
                                        param_elem = preCasting;
                                    }
                                    else {
                                        param_elem = p_unsafe_elem;
                                    }
                                    break;
                                case greycat.Type.INT:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.STRING:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.DMATRIX:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.LMATRIX:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.RELATION:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.ERELATION:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.ENODE:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.DOUBLE_ARRAY:
                                    var castedParamDouble = p_unsafe_elem;
                                    var clonedDoubleArray = new Float64Array(castedParamDouble.length);
                                    java.lang.System.arraycopy(castedParamDouble, 0, clonedDoubleArray, 0, castedParamDouble.length);
                                    param_elem = clonedDoubleArray;
                                    break;
                                case greycat.Type.LONG_ARRAY:
                                    var castedParamLong = p_unsafe_elem;
                                    var clonedLongArray = new Float64Array(castedParamLong.length);
                                    java.lang.System.arraycopy(castedParamLong, 0, clonedLongArray, 0, castedParamLong.length);
                                    param_elem = clonedLongArray;
                                    break;
                                case greycat.Type.INT_ARRAY:
                                    var castedParamInt = p_unsafe_elem;
                                    var clonedIntArray = new Int32Array(castedParamInt.length);
                                    java.lang.System.arraycopy(castedParamInt, 0, clonedIntArray, 0, castedParamInt.length);
                                    param_elem = clonedIntArray;
                                    break;
                                case greycat.Type.STRING_TO_INT_MAP:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.LONG_TO_LONG_MAP:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.LONG_TO_LONG_ARRAY_MAP:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.RELATION_INDEXED:
                                    param_elem = p_unsafe_elem;
                                    break;
                                default:
                                    throw new Error("Internal Exception, unknown type");
                            }
                        }
                        catch ($ex$) {
                            if ($ex$ instanceof Error) {
                                var e = $ex$;
                                {
                                    throw new Error("mwDB usage error, set method called with type " + greycat.Type.typeName(p_type) + " while param object is " + p_unsafe_elem);
                                }
                            }
                            else {
                                throw $ex$;
                            }
                        }
                    }
                    if (this._k == null) {
                        if (param_elem == null) {
                            return;
                        }
                        this._capacity = greycat.Constants.MAP_INITIAL_CAPACITY;
                        this._k = new Int32Array(this._capacity);
                        this._v = new Array(this._capacity);
                        this._type = new Int8Array(this._capacity);
                        this._next_hash = new Int32Array(this._capacity * 3);
                        java.util.Arrays.fill(this._next_hash, 0, this._capacity * 3, -1);
                        this._k[0] = p_key;
                        this._v[0] = param_elem;
                        this._type[0] = p_type;
                        this._size = 1;
                        var hashIndex_1 = p_key % (this._capacity * 2);
                        if (hashIndex_1 < 0) {
                            hashIndex_1 = hashIndex_1 * -1;
                        }
                        this._next_hash[this._capacity + hashIndex_1] = 0;
                        if (!initial) {
                            this.declareDirty();
                        }
                        return;
                    }
                    var entry = -1;
                    var p_entry = -1;
                    var hashIndex = p_key % (this._capacity * 2);
                    if (hashIndex < 0) {
                        hashIndex = hashIndex * -1;
                    }
                    var m = this._next_hash[this._capacity + hashIndex];
                    while (m != -1) {
                        if (this._k[m] == p_key) {
                            entry = m;
                            break;
                        }
                        p_entry = m;
                        m = this._next_hash[m];
                    }
                    if (entry != -1) {
                        if (replaceIfPresent || (p_type != this._type[entry])) {
                            if (param_elem == null) {
                                if (p_entry != -1) {
                                    this._next_hash[p_entry] = this._next_hash[entry];
                                }
                                else {
                                    this._next_hash[this._capacity + hashIndex] = -1;
                                }
                                var indexVictim = this._size - 1;
                                if (entry == indexVictim) {
                                    this._k[entry] = -1;
                                    this._v[entry] = null;
                                    this._type[entry] = -1;
                                }
                                else {
                                    this._k[entry] = this._k[indexVictim];
                                    this._v[entry] = this._v[indexVictim];
                                    this._type[entry] = this._type[indexVictim];
                                    this._next_hash[entry] = this._next_hash[indexVictim];
                                    var victimHash = this._k[entry] % (this._capacity * 2);
                                    if (victimHash < 0) {
                                        victimHash = victimHash * -1;
                                    }
                                    m = this._next_hash[this._capacity + victimHash];
                                    if (m == indexVictim) {
                                        this._next_hash[this._capacity + victimHash] = entry;
                                    }
                                    else {
                                        while (m != -1) {
                                            if (this._next_hash[m] == indexVictim) {
                                                this._next_hash[m] = entry;
                                                break;
                                            }
                                            m = this._next_hash[m];
                                        }
                                    }
                                    this._k[indexVictim] = -1;
                                    this._v[indexVictim] = null;
                                    this._type[indexVictim] = -1;
                                }
                                this._size--;
                            }
                            else {
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
                        this._next_hash[this._size] = this._next_hash[this._capacity + hashIndex];
                        this._next_hash[this._capacity + hashIndex] = this._size;
                        this._size++;
                        if (!initial) {
                            this.declareDirty();
                        }
                        return;
                    }
                    var newCapacity = this._capacity * 2;
                    var ex_k = new Int32Array(newCapacity);
                    java.lang.System.arraycopy(this._k, 0, ex_k, 0, this._capacity);
                    this._k = ex_k;
                    var ex_v = new Array(newCapacity);
                    java.lang.System.arraycopy(this._v, 0, ex_v, 0, this._capacity);
                    this._v = ex_v;
                    var ex_type = new Int8Array(newCapacity);
                    java.lang.System.arraycopy(this._type, 0, ex_type, 0, this._capacity);
                    this._type = ex_type;
                    this._capacity = newCapacity;
                    this._k[this._size] = p_key;
                    this._v[this._size] = param_elem;
                    this._type[this._size] = p_type;
                    this._size++;
                    this._next_hash = new Int32Array(this._capacity * 3);
                    java.util.Arrays.fill(this._next_hash, 0, this._capacity * 3, -1);
                    var hashCapacity = this._capacity * 2;
                    for (var i = 0; i < this._size; i++) {
                        var keyHash = this._k[i] % hashCapacity;
                        if (keyHash < 0) {
                            keyHash = keyHash * -1;
                        }
                        this._next_hash[i] = this._next_hash[this._capacity + keyHash];
                        this._next_hash[this._capacity + keyHash] = i;
                    }
                    if (!initial) {
                        this.declareDirty();
                    }
                };
                HeapENode.prototype.set = function (name, type, value) {
                    this.internal_set(this.egraph.graph().resolver().stringToHash(name, true), type, value, true, false);
                    return this;
                };
                HeapENode.prototype.setAt = function (key, type, value) {
                    this.internal_set(key, type, value, true, false);
                    return this;
                };
                HeapENode.prototype.get = function (name) {
                    return this.internal_get(this.egraph.graph().resolver().stringToHash(name, false));
                };
                HeapENode.prototype.getAt = function (key) {
                    return this.internal_get(key);
                };
                HeapENode.prototype.getWithDefault = function (key, defaultValue) {
                    var result = this.get(key);
                    if (result == null) {
                        return defaultValue;
                    }
                    else {
                        return result;
                    }
                };
                HeapENode.prototype.getAtWithDefault = function (key, defaultValue) {
                    var result = this.internal_get(key);
                    if (result == null) {
                        return defaultValue;
                    }
                    else {
                        return result;
                    }
                };
                HeapENode.prototype.drop = function () {
                    this.egraph.drop(this);
                };
                HeapENode.prototype.graph = function () {
                    return this.egraph;
                };
                HeapENode.prototype.getOrCreate = function (key, type) {
                    var previous = this.get(key);
                    if (previous != null) {
                        return previous;
                    }
                    else {
                        return this.getOrCreateAt(this.egraph.graph().resolver().stringToHash(key, true), type);
                    }
                };
                HeapENode.prototype.getOrCreateAt = function (key, type) {
                    var found = this.internal_find(key);
                    if (found != -1) {
                        if (this._type[found] == type) {
                            return this._v[found];
                        }
                    }
                    var toSet = null;
                    switch (type) {
                        case greycat.Type.ERELATION:
                            toSet = new greycat.internal.heap.HeapERelation(this, null);
                            break;
                        case greycat.Type.RELATION:
                            toSet = new greycat.internal.heap.HeapRelation(this, null);
                            break;
                        case greycat.Type.RELATION_INDEXED:
                            toSet = new greycat.internal.heap.HeapRelationIndexed(this, this.egraph.graph());
                            break;
                        case greycat.Type.DMATRIX:
                            toSet = new greycat.internal.heap.HeapDMatrix(this, null);
                            break;
                        case greycat.Type.LMATRIX:
                            toSet = new greycat.internal.heap.HeapLMatrix(this, null);
                            break;
                        case greycat.Type.STRING_TO_INT_MAP:
                            toSet = new greycat.internal.heap.HeapStringIntMap(this);
                            break;
                        case greycat.Type.LONG_TO_LONG_MAP:
                            toSet = new greycat.internal.heap.HeapLongLongMap(this);
                            break;
                        case greycat.Type.LONG_TO_LONG_ARRAY_MAP:
                            toSet = new greycat.internal.heap.HeapLongLongArrayMap(this);
                            break;
                    }
                    this.internal_set(key, type, toSet, true, false);
                    return toSet;
                };
                HeapENode.prototype.toString = function () {
                    var builder = new java.lang.StringBuilder();
                    var isFirst = [true];
                    var isFirstField = true;
                    builder.append("{");
                    var _loop_4 = function (i) {
                        var elem = this_2._v[i];
                        var resolver = this_2.egraph.graph().resolver();
                        var attributeKey = this_2._k[i];
                        var elemType = this_2._type[i];
                        if (elem != null) {
                            if (isFirstField) {
                                isFirstField = false;
                            }
                            else {
                                builder.append(",");
                            }
                            var resolveName = resolver.hashToString(attributeKey);
                            if (resolveName == null) {
                                resolveName = attributeKey + "";
                            }
                            switch (elemType) {
                                case greycat.Type.BOOL: {
                                    builder.append("\"");
                                    builder.append(resolveName);
                                    builder.append("\":");
                                    if (elem) {
                                        builder.append("1");
                                    }
                                    else {
                                        builder.append("0");
                                    }
                                    break;
                                }
                                case greycat.Type.STRING: {
                                    builder.append("\"");
                                    builder.append(resolveName);
                                    builder.append("\":");
                                    builder.append("\"");
                                    builder.append(elem);
                                    builder.append("\"");
                                    break;
                                }
                                case greycat.Type.LONG: {
                                    builder.append("\"");
                                    builder.append(resolveName);
                                    builder.append("\":");
                                    builder.append(elem);
                                    break;
                                }
                                case greycat.Type.INT: {
                                    builder.append("\"");
                                    builder.append(resolveName);
                                    builder.append("\":");
                                    builder.append(elem);
                                    break;
                                }
                                case greycat.Type.DOUBLE: {
                                    if (!greycat.base.BaseNode.isNaN(elem)) {
                                        builder.append("\"");
                                        builder.append(resolveName);
                                        builder.append("\":");
                                        builder.append(elem);
                                    }
                                    break;
                                }
                                case greycat.Type.DOUBLE_ARRAY: {
                                    builder.append("\"");
                                    builder.append(resolveName);
                                    builder.append("\":");
                                    builder.append("[");
                                    var castedArr = elem;
                                    for (var j = 0; j < castedArr.length; j++) {
                                        if (j != 0) {
                                            builder.append(",");
                                        }
                                        builder.append(castedArr[j]);
                                    }
                                    builder.append("]");
                                    break;
                                }
                                case greycat.Type.RELATION:
                                    builder.append("\"");
                                    builder.append(resolveName);
                                    builder.append("\":");
                                    builder.append("[");
                                    var castedRelArr = elem;
                                    for (var j = 0; j < castedRelArr.size(); j++) {
                                        if (j != 0) {
                                            builder.append(",");
                                        }
                                        builder.append(castedRelArr.get(j));
                                    }
                                    builder.append("]");
                                    break;
                                case greycat.Type.ERELATION:
                                    builder.append("\"");
                                    builder.append(resolveName);
                                    builder.append("\":");
                                    builder.append("[");
                                    var castedERelArr = elem;
                                    for (var j = 0; j < castedERelArr.size(); j++) {
                                        if (j != 0) {
                                            builder.append(",");
                                        }
                                        builder.append(castedERelArr.node(j)._id);
                                    }
                                    builder.append("]");
                                    break;
                                case greycat.Type.LONG_ARRAY: {
                                    builder.append("\"");
                                    builder.append(resolveName);
                                    builder.append("\":");
                                    builder.append("[");
                                    var castedArr2 = elem;
                                    for (var j = 0; j < castedArr2.length; j++) {
                                        if (j != 0) {
                                            builder.append(",");
                                        }
                                        builder.append(castedArr2[j]);
                                    }
                                    builder.append("]");
                                    break;
                                }
                                case greycat.Type.INT_ARRAY: {
                                    builder.append("\"");
                                    builder.append(resolveName);
                                    builder.append("\":");
                                    builder.append("[");
                                    var castedArr3 = elem;
                                    for (var j = 0; j < castedArr3.length; j++) {
                                        if (j != 0) {
                                            builder.append(",");
                                        }
                                        builder.append(castedArr3[j]);
                                    }
                                    builder.append("]");
                                    break;
                                }
                                case greycat.Type.LONG_TO_LONG_MAP: {
                                    builder.append("\"");
                                    builder.append(resolveName);
                                    builder.append("\":");
                                    builder.append("{");
                                    var castedMapL2L = elem;
                                    isFirst[0] = true;
                                    castedMapL2L.each(function (key, value) {
                                        {
                                            if (!isFirst[0]) {
                                                builder.append(",");
                                            }
                                            else {
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
                                case greycat.Type.RELATION_INDEXED:
                                case greycat.Type.LONG_TO_LONG_ARRAY_MAP: {
                                    builder.append("\"");
                                    builder.append(resolveName);
                                    builder.append("\":");
                                    builder.append("{");
                                    var castedMapL2LA = elem;
                                    isFirst[0] = true;
                                    var keys_3 = new java.util.HashSet();
                                    castedMapL2LA.each(function (key, value) {
                                        {
                                            keys_3.add(key);
                                        }
                                    });
                                    var flatKeys = keys_3.toArray(new Array(keys_3.size()));
                                    for (var k = 0; k < flatKeys.length; k++) {
                                        var values = castedMapL2LA.get(flatKeys[k]);
                                        if (!isFirst[0]) {
                                            builder.append(",");
                                        }
                                        else {
                                            isFirst[0] = false;
                                        }
                                        builder.append("\"");
                                        builder.append(flatKeys[k]);
                                        builder.append("\":[");
                                        for (var j = 0; j < values.length; j++) {
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
                                case greycat.Type.STRING_TO_INT_MAP: {
                                    builder.append("\"");
                                    builder.append(resolveName);
                                    builder.append("\":");
                                    builder.append("{");
                                    var castedMapS2L = elem;
                                    isFirst[0] = true;
                                    castedMapS2L.each(function (key, value) {
                                        {
                                            if (!isFirst[0]) {
                                                builder.append(",");
                                            }
                                            else {
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
                    };
                    var this_2 = this;
                    for (var i = 0; i < this._size; i++) {
                        _loop_4(i);
                    }
                    builder.append("}");
                    return builder.toString();
                };
                HeapENode.prototype.save = function (buffer) {
                    greycat.utility.Base64.encodeIntToBuffer(this._size, buffer);
                    for (var i = 0; i < this._size; i++) {
                        if (this._v[i] != null) {
                            var loopValue = this._v[i];
                            if (loopValue != null) {
                                buffer.write(greycat.internal.CoreConstants.CHUNK_ESEP);
                                greycat.utility.Base64.encodeIntToBuffer(this._type[i], buffer);
                                buffer.write(greycat.internal.CoreConstants.CHUNK_ESEP);
                                greycat.utility.Base64.encodeIntToBuffer(this._k[i], buffer);
                                buffer.write(greycat.internal.CoreConstants.CHUNK_ESEP);
                                switch (this._type[i]) {
                                    case greycat.Type.ENODE:
                                        greycat.utility.Base64.encodeIntToBuffer(loopValue._id, buffer);
                                        break;
                                    case greycat.Type.ERELATION:
                                        var castedLongArrERel = loopValue;
                                        greycat.utility.Base64.encodeIntToBuffer(castedLongArrERel.size(), buffer);
                                        for (var j = 0; j < castedLongArrERel.size(); j++) {
                                            buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                            greycat.utility.Base64.encodeIntToBuffer(castedLongArrERel.node(j)._id, buffer);
                                        }
                                        break;
                                    case greycat.Type.STRING:
                                        greycat.utility.Base64.encodeStringToBuffer(loopValue, buffer);
                                        break;
                                    case greycat.Type.BOOL:
                                        if (this._v[i]) {
                                            greycat.utility.Base64.encodeIntToBuffer(greycat.internal.CoreConstants.BOOL_TRUE, buffer);
                                        }
                                        else {
                                            greycat.utility.Base64.encodeIntToBuffer(greycat.internal.CoreConstants.BOOL_FALSE, buffer);
                                        }
                                        break;
                                    case greycat.Type.LONG:
                                        greycat.utility.Base64.encodeLongToBuffer(loopValue, buffer);
                                        break;
                                    case greycat.Type.DOUBLE:
                                        greycat.utility.Base64.encodeDoubleToBuffer(loopValue, buffer);
                                        break;
                                    case greycat.Type.INT:
                                        greycat.utility.Base64.encodeIntToBuffer(loopValue, buffer);
                                        break;
                                    case greycat.Type.DOUBLE_ARRAY:
                                        var castedDoubleArr = loopValue;
                                        greycat.utility.Base64.encodeIntToBuffer(castedDoubleArr.length, buffer);
                                        for (var j = 0; j < castedDoubleArr.length; j++) {
                                            buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                            greycat.utility.Base64.encodeDoubleToBuffer(castedDoubleArr[j], buffer);
                                        }
                                        break;
                                    case greycat.Type.RELATION:
                                        var castedLongArrRel = loopValue;
                                        greycat.utility.Base64.encodeIntToBuffer(castedLongArrRel.size(), buffer);
                                        for (var j = 0; j < castedLongArrRel.size(); j++) {
                                            buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                            greycat.utility.Base64.encodeLongToBuffer(castedLongArrRel.unsafe_get(j), buffer);
                                        }
                                        break;
                                    case greycat.Type.LONG_ARRAY:
                                        var castedLongArr = loopValue;
                                        greycat.utility.Base64.encodeIntToBuffer(castedLongArr.length, buffer);
                                        for (var j = 0; j < castedLongArr.length; j++) {
                                            buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                            greycat.utility.Base64.encodeLongToBuffer(castedLongArr[j], buffer);
                                        }
                                        break;
                                    case greycat.Type.INT_ARRAY:
                                        var castedIntArr = loopValue;
                                        greycat.utility.Base64.encodeIntToBuffer(castedIntArr.length, buffer);
                                        for (var j = 0; j < castedIntArr.length; j++) {
                                            buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                            greycat.utility.Base64.encodeIntToBuffer(castedIntArr[j], buffer);
                                        }
                                        break;
                                    case greycat.Type.DMATRIX:
                                        var castedMatrix = loopValue;
                                        var unsafeContent = castedMatrix.unsafe_data();
                                        if (unsafeContent != null) {
                                            greycat.utility.Base64.encodeIntToBuffer(unsafeContent.length, buffer);
                                            for (var j = 0; j < unsafeContent.length; j++) {
                                                buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                                greycat.utility.Base64.encodeDoubleToBuffer(unsafeContent[j], buffer);
                                            }
                                        }
                                        break;
                                    case greycat.Type.LMATRIX:
                                        var castedLMatrix = loopValue;
                                        var unsafeLContent = castedLMatrix.unsafe_data();
                                        if (unsafeLContent != null) {
                                            greycat.utility.Base64.encodeIntToBuffer(unsafeLContent.length, buffer);
                                            for (var j = 0; j < unsafeLContent.length; j++) {
                                                buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                                greycat.utility.Base64.encodeLongToBuffer(unsafeLContent[j], buffer);
                                            }
                                        }
                                        break;
                                    case greycat.Type.STRING_TO_INT_MAP:
                                        var castedStringLongMap = loopValue;
                                        greycat.utility.Base64.encodeIntToBuffer(castedStringLongMap.size(), buffer);
                                        castedStringLongMap.unsafe_each(function (key, value) {
                                            {
                                                buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                                greycat.utility.Base64.encodeStringToBuffer(key, buffer);
                                                buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                                greycat.utility.Base64.encodeLongToBuffer(value, buffer);
                                            }
                                        });
                                        break;
                                    case greycat.Type.LONG_TO_LONG_MAP:
                                        var castedLongLongMap = loopValue;
                                        greycat.utility.Base64.encodeIntToBuffer(castedLongLongMap.size(), buffer);
                                        castedLongLongMap.unsafe_each(function (key, value) {
                                            {
                                                buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                                greycat.utility.Base64.encodeLongToBuffer(key, buffer);
                                                buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                                greycat.utility.Base64.encodeLongToBuffer(value, buffer);
                                            }
                                        });
                                        break;
                                    case greycat.Type.RELATION_INDEXED:
                                    case greycat.Type.LONG_TO_LONG_ARRAY_MAP:
                                        var castedLongLongArrayMap = loopValue;
                                        greycat.utility.Base64.encodeIntToBuffer(castedLongLongArrayMap.size(), buffer);
                                        castedLongLongArrayMap.unsafe_each(function (key, value) {
                                            {
                                                buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                                greycat.utility.Base64.encodeLongToBuffer(key, buffer);
                                                buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                                greycat.utility.Base64.encodeLongToBuffer(value, buffer);
                                            }
                                        });
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                    this._dirty = false;
                };
                HeapENode.prototype.load = function (buffer, currentCursor, graph) {
                    var initial = this._k == null;
                    var payloadSize = buffer.length();
                    var cursor = currentCursor;
                    var previous = cursor;
                    var state = HeapENode.LOAD_WAITING_ALLOC;
                    var read_type = -1;
                    var read_key = -1;
                    while (cursor < payloadSize) {
                        var current = buffer.read(cursor);
                        if (current == greycat.Constants.CHUNK_ENODE_SEP || current == greycat.Constants.CHUNK_SEP) {
                            break;
                        }
                        else if (current == greycat.Constants.CHUNK_ESEP) {
                            switch (state) {
                                case HeapENode.LOAD_WAITING_ALLOC:
                                    var closePowerOfTwo = Math.pow(2, Math.ceil(Math.log(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor)) / Math.log(2)));
                                    this.allocate(closePowerOfTwo);
                                    state = HeapENode.LOAD_WAITING_TYPE;
                                    cursor++;
                                    previous = cursor;
                                    break;
                                case HeapENode.LOAD_WAITING_TYPE:
                                    read_type = greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor);
                                    state = HeapENode.LOAD_WAITING_KEY;
                                    cursor++;
                                    previous = cursor;
                                    break;
                                case HeapENode.LOAD_WAITING_KEY:
                                    read_key = greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor);
                                    switch (read_type) {
                                        case greycat.Type.BOOL:
                                        case greycat.Type.INT:
                                        case greycat.Type.DOUBLE:
                                        case greycat.Type.LONG:
                                        case greycat.Type.STRING:
                                        case greycat.Type.ENODE:
                                            state = HeapENode.LOAD_WAITING_VALUE;
                                            cursor++;
                                            previous = cursor;
                                            break;
                                        case greycat.Type.DOUBLE_ARRAY:
                                            var doubleArrayLoaded = null;
                                            var doubleArrayIndex = 0;
                                            cursor++;
                                            previous = cursor;
                                            current = buffer.read(cursor);
                                            while (cursor < payloadSize && current != greycat.Constants.CHUNK_SEP && current != greycat.Constants.CHUNK_ENODE_SEP && current != greycat.Constants.CHUNK_ESEP) {
                                                if (current == greycat.Constants.CHUNK_VAL_SEP) {
                                                    if (doubleArrayLoaded == null) {
                                                        doubleArrayLoaded = new Float64Array(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                                    }
                                                    else {
                                                        doubleArrayLoaded[doubleArrayIndex] = greycat.utility.Base64.decodeToDoubleWithBounds(buffer, previous, cursor);
                                                        doubleArrayIndex++;
                                                    }
                                                    previous = cursor + 1;
                                                }
                                                cursor++;
                                                if (cursor < payloadSize) {
                                                    current = buffer.read(cursor);
                                                }
                                            }
                                            if (doubleArrayLoaded == null) {
                                                doubleArrayLoaded = new Float64Array(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                            }
                                            else {
                                                doubleArrayLoaded[doubleArrayIndex] = greycat.utility.Base64.decodeToDoubleWithBounds(buffer, previous, cursor);
                                            }
                                            this.internal_set(read_key, read_type, doubleArrayLoaded, true, initial);
                                            if (current == greycat.Constants.CHUNK_ESEP && cursor < payloadSize) {
                                                state = HeapENode.LOAD_WAITING_TYPE;
                                                cursor++;
                                                previous = cursor;
                                            }
                                            break;
                                        case greycat.Type.LONG_ARRAY:
                                            var longArrayLoaded = null;
                                            var longArrayIndex = 0;
                                            cursor++;
                                            previous = cursor;
                                            current = buffer.read(cursor);
                                            while (cursor < payloadSize && current != greycat.Constants.CHUNK_SEP && current != greycat.Constants.CHUNK_ENODE_SEP && current != greycat.Constants.CHUNK_ESEP) {
                                                if (current == greycat.Constants.CHUNK_VAL_SEP) {
                                                    if (longArrayLoaded == null) {
                                                        longArrayLoaded = new Float64Array(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                                    }
                                                    else {
                                                        longArrayLoaded[longArrayIndex] = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                                                        longArrayIndex++;
                                                    }
                                                    previous = cursor + 1;
                                                }
                                                cursor++;
                                                if (cursor < payloadSize) {
                                                    current = buffer.read(cursor);
                                                }
                                            }
                                            if (longArrayLoaded == null) {
                                                longArrayLoaded = new Float64Array(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                            }
                                            else {
                                                longArrayLoaded[longArrayIndex] = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                                            }
                                            this.internal_set(read_key, read_type, longArrayLoaded, true, initial);
                                            if (current == greycat.Constants.CHUNK_ESEP && cursor < payloadSize) {
                                                state = HeapENode.LOAD_WAITING_TYPE;
                                                cursor++;
                                                previous = cursor;
                                            }
                                            break;
                                        case greycat.Type.INT_ARRAY:
                                            var intArrayLoaded = null;
                                            var intArrayIndex = 0;
                                            cursor++;
                                            previous = cursor;
                                            current = buffer.read(cursor);
                                            while (cursor < payloadSize && current != greycat.Constants.CHUNK_SEP && current != greycat.Constants.CHUNK_ENODE_SEP && current != greycat.Constants.CHUNK_ESEP) {
                                                if (current == greycat.Constants.CHUNK_VAL_SEP) {
                                                    if (intArrayLoaded == null) {
                                                        intArrayLoaded = new Int32Array(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                                    }
                                                    else {
                                                        intArrayLoaded[intArrayIndex] = greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor);
                                                        intArrayIndex++;
                                                    }
                                                    previous = cursor + 1;
                                                }
                                                cursor++;
                                                if (cursor < payloadSize) {
                                                    current = buffer.read(cursor);
                                                }
                                            }
                                            if (intArrayLoaded == null) {
                                                intArrayLoaded = new Int32Array(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                            }
                                            else {
                                                intArrayLoaded[intArrayIndex] = greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor);
                                            }
                                            this.internal_set(read_key, read_type, intArrayLoaded, true, initial);
                                            if (current == greycat.Constants.CHUNK_ESEP && cursor < payloadSize) {
                                                state = HeapENode.LOAD_WAITING_TYPE;
                                                cursor++;
                                                previous = cursor;
                                            }
                                            break;
                                        case greycat.Type.RELATION:
                                            var relation = new greycat.internal.heap.HeapRelation(this, null);
                                            cursor++;
                                            cursor = relation.load(buffer, cursor, payloadSize);
                                            this.internal_set(read_key, read_type, relation, true, initial);
                                            if (cursor < payloadSize) {
                                                current = buffer.read(cursor);
                                                if (current == greycat.Constants.CHUNK_ESEP && cursor < payloadSize) {
                                                    state = HeapENode.LOAD_WAITING_TYPE;
                                                    cursor++;
                                                    previous = cursor;
                                                }
                                            }
                                            break;
                                        case greycat.Type.DMATRIX:
                                            var matrix = new greycat.internal.heap.HeapDMatrix(this, null);
                                            cursor++;
                                            cursor = matrix.load(buffer, cursor, payloadSize);
                                            this.internal_set(read_key, read_type, matrix, true, initial);
                                            if (cursor < payloadSize) {
                                                current = buffer.read(cursor);
                                                if (current == greycat.Constants.CHUNK_ESEP && cursor < payloadSize) {
                                                    state = HeapENode.LOAD_WAITING_TYPE;
                                                    cursor++;
                                                    previous = cursor;
                                                }
                                            }
                                            break;
                                        case greycat.Type.LMATRIX:
                                            var lmatrix = new greycat.internal.heap.HeapLMatrix(this, null);
                                            cursor++;
                                            cursor = lmatrix.load(buffer, cursor, payloadSize);
                                            this.internal_set(read_key, read_type, lmatrix, true, initial);
                                            if (cursor < payloadSize) {
                                                current = buffer.read(cursor);
                                                if (current == greycat.Constants.CHUNK_ESEP && cursor < payloadSize) {
                                                    state = HeapENode.LOAD_WAITING_TYPE;
                                                    cursor++;
                                                    previous = cursor;
                                                }
                                            }
                                            break;
                                        case greycat.Type.LONG_TO_LONG_MAP:
                                            var l2lmap = new greycat.internal.heap.HeapLongLongMap(this);
                                            cursor++;
                                            cursor = l2lmap.load(buffer, cursor, payloadSize);
                                            this.internal_set(read_key, read_type, l2lmap, true, initial);
                                            if (cursor < payloadSize) {
                                                current = buffer.read(cursor);
                                                if (current == greycat.Constants.CHUNK_ESEP && cursor < payloadSize) {
                                                    state = HeapENode.LOAD_WAITING_TYPE;
                                                    cursor++;
                                                    previous = cursor;
                                                }
                                            }
                                            break;
                                        case greycat.Type.LONG_TO_LONG_ARRAY_MAP:
                                            var l2lrmap = new greycat.internal.heap.HeapLongLongArrayMap(this);
                                            cursor++;
                                            cursor = l2lrmap.load(buffer, cursor, payloadSize);
                                            this.internal_set(read_key, read_type, l2lrmap, true, initial);
                                            if (cursor < payloadSize) {
                                                current = buffer.read(cursor);
                                                if (current == greycat.Constants.CHUNK_ESEP && cursor < payloadSize) {
                                                    state = HeapENode.LOAD_WAITING_TYPE;
                                                    cursor++;
                                                    previous = cursor;
                                                }
                                            }
                                            break;
                                        case greycat.Type.RELATION_INDEXED:
                                            var relationIndexed = new greycat.internal.heap.HeapRelationIndexed(this, graph);
                                            cursor++;
                                            cursor = relationIndexed.load(buffer, cursor, payloadSize);
                                            this.internal_set(read_key, read_type, relationIndexed, true, initial);
                                            if (cursor < payloadSize) {
                                                current = buffer.read(cursor);
                                                if (current == greycat.Constants.CHUNK_ESEP && cursor < payloadSize) {
                                                    state = HeapENode.LOAD_WAITING_TYPE;
                                                    cursor++;
                                                    previous = cursor;
                                                }
                                            }
                                            break;
                                        case greycat.Type.STRING_TO_INT_MAP:
                                            var s2lmap = new greycat.internal.heap.HeapStringIntMap(this);
                                            cursor++;
                                            cursor = s2lmap.load(buffer, cursor, payloadSize);
                                            this.internal_set(read_key, read_type, s2lmap, true, initial);
                                            if (cursor < payloadSize) {
                                                current = buffer.read(cursor);
                                                if (current == greycat.Constants.CHUNK_ESEP && cursor < payloadSize) {
                                                    state = HeapENode.LOAD_WAITING_TYPE;
                                                    cursor++;
                                                    previous = cursor;
                                                }
                                            }
                                            break;
                                        case greycat.Type.ERELATION:
                                            var eRelation = null;
                                            cursor++;
                                            previous = cursor;
                                            current = buffer.read(cursor);
                                            while (cursor < payloadSize && current != greycat.Constants.CHUNK_SEP && current != greycat.Constants.CHUNK_ENODE_SEP && current != greycat.Constants.CHUNK_ESEP) {
                                                if (current == greycat.Constants.CHUNK_VAL_SEP) {
                                                    if (eRelation == null) {
                                                        eRelation = new greycat.internal.heap.HeapERelation(this, null);
                                                        eRelation.allocate(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                                                    }
                                                    else {
                                                        eRelation.add(this.egraph.nodeByIndex(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor), true));
                                                    }
                                                    previous = cursor + 1;
                                                }
                                                cursor++;
                                                if (cursor < payloadSize) {
                                                    current = buffer.read(cursor);
                                                }
                                            }
                                            if (eRelation == null) {
                                                eRelation = new greycat.internal.heap.HeapERelation(this, null);
                                                eRelation.allocate(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                                            }
                                            else {
                                                eRelation.add(this.egraph.nodeByIndex(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor), true));
                                            }
                                            this.internal_set(read_key, read_type, eRelation, true, initial);
                                            if (current == greycat.Constants.CHUNK_ESEP && cursor < payloadSize) {
                                                state = HeapENode.LOAD_WAITING_TYPE;
                                                cursor++;
                                                previous = cursor;
                                            }
                                            break;
                                        default:
                                            throw new Error("Not implemented yet!!!");
                                    }
                                    break;
                                case HeapENode.LOAD_WAITING_VALUE:
                                    this.load_primitive(read_key, read_type, buffer, previous, cursor, initial);
                                    state = HeapENode.LOAD_WAITING_TYPE;
                                    cursor++;
                                    previous = cursor;
                                    break;
                            }
                        }
                        else {
                            cursor++;
                        }
                    }
                    if (state == HeapENode.LOAD_WAITING_VALUE) {
                        this.load_primitive(read_key, read_type, buffer, previous, cursor, initial);
                    }
                    return cursor;
                };
                HeapENode.prototype.load_primitive = function (read_key, read_type, buffer, previous, cursor, initial) {
                    switch (read_type) {
                        case greycat.Type.BOOL:
                            this.internal_set(read_key, read_type, (greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor) == greycat.internal.CoreConstants.BOOL_TRUE), true, initial);
                            break;
                        case greycat.Type.INT:
                            this.internal_set(read_key, read_type, greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor), true, initial);
                            break;
                        case greycat.Type.DOUBLE:
                            this.internal_set(read_key, read_type, greycat.utility.Base64.decodeToDoubleWithBounds(buffer, previous, cursor), true, initial);
                            break;
                        case greycat.Type.LONG:
                            this.internal_set(read_key, read_type, greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor), true, initial);
                            break;
                        case greycat.Type.STRING:
                            this.internal_set(read_key, read_type, greycat.utility.Base64.decodeToStringWithBounds(buffer, previous, cursor), true, initial);
                            break;
                        case greycat.Type.ENODE:
                            this.internal_set(read_key, read_type, this.egraph.nodeByIndex(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor), true), true, initial);
                            break;
                    }
                };
                HeapENode.prototype.each = function (callBack) {
                    for (var i = 0; i < this._size; i++) {
                        if (this._v[i] != null) {
                            callBack(this._k[i], this._type[i], this._v[i]);
                        }
                    }
                };
                return HeapENode;
            }());
            HeapENode.LOAD_WAITING_ALLOC = 0;
            HeapENode.LOAD_WAITING_TYPE = 1;
            HeapENode.LOAD_WAITING_KEY = 2;
            HeapENode.LOAD_WAITING_VALUE = 3;
            heap.HeapENode = HeapENode;
            var HeapERelation = (function () {
                function HeapERelation(p_parent, origin) {
                    this.parent = p_parent;
                    if (origin != null) {
                        this._back = new Array(origin._capacity);
                        java.lang.System.arraycopy(origin._back, 0, this._back, 0, origin._capacity);
                        this._size = origin._size;
                        this._capacity = origin._capacity;
                    }
                    else {
                        this._back = null;
                        this._size = 0;
                        this._capacity = 0;
                    }
                }
                HeapERelation.prototype.rebase = function (newGraph) {
                    for (var i = 0; i < this._size; i++) {
                        var previous = this._back[i];
                        this._back[i] = newGraph._nodes[previous._id];
                    }
                };
                HeapERelation.prototype.size = function () {
                    return this._size;
                };
                HeapERelation.prototype.nodes = function () {
                    var copy = new Array(this._size);
                    java.lang.System.arraycopy(this._back, 0, copy, 0, this._size);
                    return copy;
                };
                HeapERelation.prototype.node = function (index) {
                    return this._back[index];
                };
                HeapERelation.prototype.add = function (eNode) {
                    if (this._capacity == this._size) {
                        if (this._capacity == 0) {
                            this.allocate(greycat.Constants.MAP_INITIAL_CAPACITY);
                        }
                        else {
                            this.allocate(this._capacity * 2);
                        }
                    }
                    this._back[this._size] = eNode;
                    this._size++;
                    if (this.parent != null) {
                        this.parent.declareDirty();
                    }
                    return this;
                };
                HeapERelation.prototype.addAll = function (eNodes) {
                    this.allocate(eNodes.length + this._size);
                    java.lang.System.arraycopy(eNodes, 0, this._back, this._size, eNodes.length);
                    if (this.parent != null) {
                        this.parent.declareDirty();
                    }
                    return this;
                };
                HeapERelation.prototype.clear = function () {
                    this._size = 0;
                    this._back = null;
                    if (this.parent != null) {
                        this.parent.declareDirty();
                    }
                    return this;
                };
                HeapERelation.prototype.toString = function () {
                    var buffer = new java.lang.StringBuilder();
                    buffer.append("[");
                    for (var i = 0; i < this._size; i++) {
                        if (i != 0) {
                            buffer.append(",");
                        }
                        buffer.append(this._back[i]._id);
                    }
                    buffer.append("]");
                    return buffer.toString();
                };
                HeapERelation.prototype.allocate = function (newCapacity) {
                    var closePowerOfTwo = Math.pow(2, Math.ceil(Math.log(newCapacity) / Math.log(2)));
                    if (closePowerOfTwo > this._capacity) {
                        var new_back = new Array(closePowerOfTwo);
                        if (this._back != null) {
                            java.lang.System.arraycopy(this._back, 0, new_back, 0, this._size);
                        }
                        this._back = new_back;
                        this._capacity = closePowerOfTwo;
                    }
                };
                return HeapERelation;
            }());
            heap.HeapERelation = HeapERelation;
            var HeapFixedStack = (function () {
                function HeapFixedStack(capacity, fill) {
                    this._capacity = capacity;
                    this._next = new Int32Array(capacity);
                    this._prev = new Int32Array(capacity);
                    this._first = -1;
                    this._last = -1;
                    java.util.Arrays.fill(this._next, 0, capacity, -1);
                    java.util.Arrays.fill(this._prev, 0, capacity, -1);
                    if (fill) {
                        for (var i = 0; i < capacity; i++) {
                            var l = this._last;
                            this._prev[i] = l;
                            this._last = i;
                            if (this._first == -1) {
                                this._first = i;
                            }
                            else {
                                this._next[l] = i;
                            }
                        }
                        this._count = capacity;
                    }
                    else {
                        this._count = 0;
                    }
                }
                HeapFixedStack.prototype.enqueue = function (index) {
                    if (this._count >= this._capacity) {
                        return false;
                    }
                    var castedIndex = index;
                    if (this._first == castedIndex || this._last == castedIndex) {
                        return false;
                    }
                    if (this._prev[castedIndex] != -1 || this._next[castedIndex] != -1) {
                        return false;
                    }
                    var l = this._last;
                    this._prev[castedIndex] = l;
                    this._last = castedIndex;
                    if (this._first == -1) {
                        this._first = castedIndex;
                    }
                    else {
                        this._next[l] = castedIndex;
                    }
                    this._count++;
                    return true;
                };
                HeapFixedStack.prototype.dequeueTail = function () {
                    var f = this._first;
                    if (f == -1) {
                        return -1;
                    }
                    var n = this._next[f];
                    this._next[f] = -1;
                    this._prev[f] = -1;
                    this._first = n;
                    if (n == -1) {
                        this._last = -1;
                    }
                    else {
                        this._prev[n] = -1;
                    }
                    this._count--;
                    return f;
                };
                HeapFixedStack.prototype.dequeue = function (index) {
                    var castedIndex = index;
                    var p = this._prev[castedIndex];
                    var n = this._next[castedIndex];
                    if (p == -1 && n == -1) {
                        return false;
                    }
                    if (p == -1) {
                        var f = this._first;
                        if (f == -1) {
                            return false;
                        }
                        var n2 = this._next[f];
                        this._next[f] = -1;
                        this._prev[f] = -1;
                        this._first = n2;
                        if (n2 == -1) {
                            this._last = -1;
                        }
                        else {
                            this._prev[n2] = -1;
                        }
                        --this._count;
                    }
                    else if (n == -1) {
                        var l = this._last;
                        if (l == -1) {
                            return false;
                        }
                        var p2 = this._prev[l];
                        this._prev[l] = -1;
                        this._next[l] = -1;
                        this._last = p2;
                        if (p2 == -1) {
                            this._first = -1;
                        }
                        else {
                            this._next[p2] = -1;
                        }
                        --this._count;
                    }
                    else {
                        this._next[p] = n;
                        this._prev[n] = p;
                        this._prev[castedIndex] = -1;
                        this._next[castedIndex] = -1;
                        this._count--;
                    }
                    return true;
                };
                HeapFixedStack.prototype.free = function () { };
                HeapFixedStack.prototype.size = function () {
                    return this._count;
                };
                return HeapFixedStack;
            }());
            heap.HeapFixedStack = HeapFixedStack;
            var HeapGenChunk = (function () {
                function HeapGenChunk(p_space, p_id, p_index) {
                    this._index = p_index;
                    this._space = p_space;
                    this._prefix = Long.fromNumber(p_id).shiftLeft((Constants.LONG_SIZE - Constants.PREFIX_SIZE));
                    this._seed = -1;
                }
                HeapGenChunk.prototype.save = function (buffer) {
                    greycat.utility.Base64.encodeLongToBuffer(this._seed, buffer);
                    this._dirty = false;
                };
                HeapGenChunk.prototype.saveDiff = function (buffer) {
                    greycat.utility.Base64.encodeLongToBuffer(this._seed, buffer);
                    this._dirty = false;
                };
                HeapGenChunk.prototype.load = function (buffer) {
                    this.internal_load(buffer, false);
                };
                HeapGenChunk.prototype.loadDiff = function (buffer) {
                    this.internal_load(buffer, true);
                };
                HeapGenChunk.prototype.internal_load = function (buffer, diff) {
                    if (buffer == null || buffer.length() == 0) {
                        return;
                    }
                    var loaded = greycat.utility.Base64.decodeToLongWithBounds(buffer, 0, buffer.length());
                    var previousSeed = this._seed;
                    this._seed = loaded;
                    if (previousSeed != -1 && previousSeed != this._seed) {
                        if (this._space != null && !this._dirty && diff) {
                            this._dirty = true;
                            this._space.notifyUpdate(this._index);
                        }
                    }
                };
                HeapGenChunk.prototype.newKey = function () {
                    if (this._seed == Constants.KEY_PREFIX_MASK) {
                        throw new Error("Object Index could not be created because it exceeded the capacity of the current prefix. Ask for a new prefix.");
                    }
                    if (this._seed == -1) {
                        this._seed = 0;
                    }
                    this._seed++;
                    var nextIndex = this._seed;
                    if (this._space) {
                        this._space.notifyUpdate(this._index);
                    }
                    var objectKey = this._prefix.add(this._seed).toNumber();
                    if (objectKey >= Constants.NULL_LONG) {
                        throw new Error("Object Index exceeds the maximum JavaScript number capacity. (2^" + Constants.LONG_SIZE + ")");
                    }
                    return objectKey;
                };
                HeapGenChunk.prototype.index = function () {
                    return this._index;
                };
                HeapGenChunk.prototype.world = function () {
                    return this._space.worldByIndex(this._index);
                };
                HeapGenChunk.prototype.time = function () {
                    return this._space.timeByIndex(this._index);
                };
                HeapGenChunk.prototype.id = function () {
                    return this._space.idByIndex(this._index);
                };
                HeapGenChunk.prototype.chunkType = function () {
                    return greycat.chunk.ChunkType.GEN_CHUNK;
                };
                return HeapGenChunk;
            }());
            heap.HeapGenChunk = HeapGenChunk;
            var HeapLMatrix = (function () {
                function HeapLMatrix(p_parent, origin) {
                    this.backend = null;
                    this.aligned = true;
                    this.parent = p_parent;
                    if (origin != null) {
                        this.aligned = false;
                        this.backend = origin.backend;
                    }
                }
                HeapLMatrix.prototype.init = function (rows, columns) {
                    {
                        this.internal_init(rows, columns);
                    }
                    this.parent.declareDirty();
                    return this;
                };
                HeapLMatrix.prototype.internal_init = function (rows, columns) {
                    this.backend = new Float64Array(rows * columns + HeapLMatrix.INDEX_OFFSET);
                    this.backend[HeapLMatrix.INDEX_ROWS] = rows;
                    this.backend[HeapLMatrix.INDEX_COLUMNS] = columns;
                    this.backend[HeapLMatrix.INDEX_MAX_COLUMN] = columns;
                    this.aligned = true;
                };
                HeapLMatrix.prototype.appendColumn = function (newColumn) {
                    {
                        this.internal_appendColumn(newColumn);
                        this.parent.declareDirty();
                    }
                    return this;
                };
                HeapLMatrix.prototype.internal_appendColumn = function (newColumn) {
                    var nbRows;
                    var nbColumns;
                    var nbMaxColumn;
                    if (this.backend == null) {
                        nbRows = newColumn.length;
                        nbColumns = greycat.Constants.MAP_INITIAL_CAPACITY;
                        nbMaxColumn = 0;
                        this.backend = new Float64Array(nbRows * nbColumns + HeapLMatrix.INDEX_OFFSET);
                        this.backend[HeapLMatrix.INDEX_ROWS] = nbRows;
                        this.backend[HeapLMatrix.INDEX_COLUMNS] = nbColumns;
                        this.backend[HeapLMatrix.INDEX_MAX_COLUMN] = nbMaxColumn;
                    }
                    else {
                        nbColumns = this.backend[HeapLMatrix.INDEX_COLUMNS];
                        nbRows = this.backend[HeapLMatrix.INDEX_ROWS];
                        nbMaxColumn = this.backend[HeapLMatrix.INDEX_MAX_COLUMN];
                    }
                    if (!this.aligned || nbMaxColumn == nbColumns) {
                        if (nbMaxColumn == nbColumns) {
                            nbColumns = nbColumns * 2;
                            this.backend[HeapLMatrix.INDEX_COLUMNS] = nbColumns;
                            var newLength = nbColumns * nbRows + HeapLMatrix.INDEX_OFFSET;
                            var next_backend = new Float64Array(newLength);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                        else {
                            var next_backend = new Float64Array(this.backend.length);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                    }
                    java.lang.System.arraycopy(newColumn, 0, this.backend, (nbMaxColumn * nbRows) + HeapLMatrix.INDEX_OFFSET, newColumn.length);
                    this.backend[HeapLMatrix.INDEX_MAX_COLUMN] = nbMaxColumn + 1;
                };
                HeapLMatrix.prototype.fill = function (value) {
                    {
                        this.internal_fill(value);
                    }
                    return this;
                };
                HeapLMatrix.prototype.internal_fill = function (value) {
                    if (this.backend != null) {
                        if (!this.aligned) {
                            var next_backend = new Float64Array(this.backend.length);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                        java.util.Arrays.fill(this.backend, HeapLMatrix.INDEX_OFFSET, this.backend.length - HeapLMatrix.INDEX_OFFSET, value);
                        this.backend[HeapLMatrix.INDEX_MAX_COLUMN] = this.backend[HeapLMatrix.INDEX_COLUMNS];
                        this.parent.declareDirty();
                    }
                };
                HeapLMatrix.prototype.fillWith = function (values) {
                    {
                        this.internal_fillWith(values);
                    }
                    return this;
                };
                HeapLMatrix.prototype.internal_fillWith = function (values) {
                    if (this.backend != null) {
                        if (!this.aligned) {
                            var next_backend = new Float64Array(this.backend.length);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                        java.lang.System.arraycopy(values, 0, this.backend, HeapLMatrix.INDEX_OFFSET, values.length);
                        this.parent.declareDirty();
                    }
                };
                HeapLMatrix.prototype.fillWithRandom = function (min, max, seed) {
                    {
                        this.internal_fillWithRandom(min, max, seed);
                    }
                    return this;
                };
                HeapLMatrix.prototype.internal_fillWithRandom = function (min, max, seed) {
                    var rand = new java.util.Random();
                    rand.setSeed(seed);
                    if (this.backend != null) {
                        if (!this.aligned) {
                            var next_backend = new Float64Array(this.backend.length);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                        for (var i = 0; i < this.backend[HeapLMatrix.INDEX_ROWS] * this.backend[HeapLMatrix.INDEX_COLUMNS]; i++) {
                            this.backend[i + HeapLMatrix.INDEX_OFFSET] = rand.nextInt() * (max - min) + min;
                        }
                        this.parent.declareDirty();
                    }
                };
                HeapLMatrix.prototype.rows = function () {
                    var result = 0;
                    {
                        if (this.backend != null) {
                            result = this.backend[HeapLMatrix.INDEX_ROWS];
                        }
                    }
                    return result;
                };
                HeapLMatrix.prototype.columns = function () {
                    var result = 0;
                    {
                        if (this.backend != null) {
                            result = this.backend[HeapLMatrix.INDEX_MAX_COLUMN];
                        }
                    }
                    return result;
                };
                HeapLMatrix.prototype.column = function (index) {
                    var result;
                    {
                        var nbRows = this.backend[HeapLMatrix.INDEX_ROWS];
                        result = new Float64Array(nbRows);
                        java.lang.System.arraycopy(this.backend, HeapLMatrix.INDEX_OFFSET + (index * nbRows), result, 0, nbRows);
                    }
                    return result;
                };
                HeapLMatrix.prototype.get = function (rowIndex, columnIndex) {
                    var result = 0;
                    {
                        if (this.backend != null) {
                            var nbRows = this.backend[HeapLMatrix.INDEX_ROWS];
                            result = this.backend[HeapLMatrix.INDEX_OFFSET + rowIndex + columnIndex * nbRows];
                        }
                    }
                    return result;
                };
                HeapLMatrix.prototype.set = function (rowIndex, columnIndex, value) {
                    {
                        this.internal_set(rowIndex, columnIndex, value);
                    }
                    return this;
                };
                HeapLMatrix.prototype.internal_set = function (rowIndex, columnIndex, value) {
                    if (this.backend != null) {
                        if (!this.aligned) {
                            var next_backend = new Float64Array(this.backend.length);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                        var nbRows = this.backend[HeapLMatrix.INDEX_ROWS];
                        this.backend[HeapLMatrix.INDEX_OFFSET + rowIndex + columnIndex * nbRows] = value;
                        this.parent.declareDirty();
                    }
                };
                HeapLMatrix.prototype.add = function (rowIndex, columnIndex, value) {
                    {
                        this.internal_add(rowIndex, columnIndex, value);
                    }
                    return this;
                };
                HeapLMatrix.prototype.internal_add = function (rowIndex, columnIndex, value) {
                    if (this.backend != null) {
                        if (!this.aligned) {
                            var next_backend = new Float64Array(this.backend.length);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                        var nbRows = this.backend[HeapLMatrix.INDEX_ROWS];
                        this.backend[HeapLMatrix.INDEX_OFFSET + rowIndex + columnIndex * nbRows] = value + this.backend[HeapLMatrix.INDEX_OFFSET + rowIndex + columnIndex * nbRows];
                        this.parent.declareDirty();
                    }
                };
                HeapLMatrix.prototype.data = function () {
                    var copy = null;
                    {
                        if (this.backend != null) {
                            copy = new Float64Array(this.backend.length - HeapLMatrix.INDEX_OFFSET);
                            java.lang.System.arraycopy(this.backend, HeapLMatrix.INDEX_OFFSET, copy, 0, this.backend.length - HeapLMatrix.INDEX_OFFSET);
                        }
                    }
                    return copy;
                };
                HeapLMatrix.prototype.leadingDimension = function () {
                    if (this.backend == null) {
                        return 0;
                    }
                    return Math.max(this.backend[HeapLMatrix.INDEX_COLUMNS], this.backend[HeapLMatrix.INDEX_ROWS]);
                };
                HeapLMatrix.prototype.unsafeGet = function (index) {
                    var result = 0;
                    {
                        if (this.backend != null) {
                            result = this.backend[HeapLMatrix.INDEX_OFFSET + index];
                        }
                    }
                    return result;
                };
                HeapLMatrix.prototype.unsafeSet = function (index, value) {
                    {
                        this.internal_unsafeSet(index, value);
                    }
                    return this;
                };
                HeapLMatrix.prototype.internal_unsafeSet = function (index, value) {
                    if (this.backend != null) {
                        if (!this.aligned) {
                            var next_backend = new Float64Array(this.backend.length);
                            java.lang.System.arraycopy(this.backend, 0, next_backend, 0, this.backend.length);
                            this.backend = next_backend;
                            this.aligned = true;
                        }
                        this.backend[HeapLMatrix.INDEX_OFFSET + index] = value;
                        this.parent.declareDirty();
                    }
                };
                HeapLMatrix.prototype.unsafe_data = function () {
                    return this.backend;
                };
                HeapLMatrix.prototype.unsafe_init = function (size) {
                    this.backend = new Float64Array(size);
                    this.backend[HeapLMatrix.INDEX_ROWS] = 0;
                    this.backend[HeapLMatrix.INDEX_COLUMNS] = 0;
                    this.aligned = true;
                };
                HeapLMatrix.prototype.unsafe_set = function (index, value) {
                    this.backend[index] = value;
                };
                HeapLMatrix.prototype.load = function (buffer, offset, max) {
                    var cursor = offset;
                    var current = buffer.read(cursor);
                    var isFirst = true;
                    var previous = offset;
                    var elemIndex = 0;
                    while (cursor < max && current != greycat.Constants.CHUNK_SEP && current != greycat.Constants.CHUNK_ENODE_SEP && current != greycat.Constants.CHUNK_ESEP) {
                        if (current == greycat.Constants.CHUNK_VAL_SEP) {
                            if (isFirst) {
                                this.unsafe_init(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                                isFirst = false;
                            }
                            else {
                                this.unsafe_set(elemIndex, greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                elemIndex++;
                            }
                            previous = cursor + 1;
                        }
                        cursor++;
                        if (cursor < max) {
                            current = buffer.read(cursor);
                        }
                    }
                    if (isFirst) {
                        this.unsafe_init(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                    }
                    else {
                        this.unsafe_set(elemIndex, greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                    }
                    return cursor;
                };
                return HeapLMatrix;
            }());
            HeapLMatrix.INDEX_ROWS = 0;
            HeapLMatrix.INDEX_COLUMNS = 1;
            HeapLMatrix.INDEX_MAX_COLUMN = 2;
            HeapLMatrix.INDEX_OFFSET = 3;
            heap.HeapLMatrix = HeapLMatrix;
            var HeapLongLongArrayMap = (function () {
                function HeapLongLongArrayMap(p_listener) {
                    this.mapSize = 0;
                    this.capacity = 0;
                    this.keys = null;
                    this.values = null;
                    this.nexts = null;
                    this.hashs = null;
                    this.parent = p_listener;
                }
                HeapLongLongArrayMap.prototype.key = function (i) {
                    return this.keys[i];
                };
                HeapLongLongArrayMap.prototype.setKey = function (i, newValue) {
                    this.keys[i] = newValue;
                };
                HeapLongLongArrayMap.prototype.value = function (i) {
                    return this.values[i];
                };
                HeapLongLongArrayMap.prototype.setValue = function (i, newValue) {
                    this.values[i] = newValue;
                };
                HeapLongLongArrayMap.prototype.next = function (i) {
                    return this.nexts[i];
                };
                HeapLongLongArrayMap.prototype.setNext = function (i, newValue) {
                    this.nexts[i] = newValue;
                };
                HeapLongLongArrayMap.prototype.hash = function (i) {
                    return this.hashs[i];
                };
                HeapLongLongArrayMap.prototype.setHash = function (i, newValue) {
                    this.hashs[i] = newValue;
                };
                HeapLongLongArrayMap.prototype.reallocate = function (newCapacity) {
                    if (newCapacity > this.capacity) {
                        var new_keys = new Float64Array(newCapacity);
                        if (this.keys != null) {
                            java.lang.System.arraycopy(this.keys, 0, new_keys, 0, this.capacity);
                        }
                        this.keys = new_keys;
                        var new_values = new Float64Array(newCapacity);
                        if (this.values != null) {
                            java.lang.System.arraycopy(this.values, 0, new_values, 0, this.capacity);
                        }
                        this.values = new_values;
                        var new_nexts = new Int32Array(newCapacity);
                        var new_hashes = new Int32Array(newCapacity * 2);
                        java.util.Arrays.fill(new_nexts, 0, newCapacity, -1);
                        java.util.Arrays.fill(new_hashes, 0, (newCapacity * 2), -1);
                        this.hashs = new_hashes;
                        this.nexts = new_nexts;
                        for (var i = 0; i < this.mapSize; i++) {
                            var new_key_hash = greycat.utility.HashHelper.longHash(this.key(i), newCapacity * 2);
                            this.setNext(i, this.hash(new_key_hash));
                            this.setHash(new_key_hash, i);
                        }
                        this.capacity = newCapacity;
                    }
                };
                HeapLongLongArrayMap.prototype.cloneFor = function (newParent) {
                    var cloned = new greycat.internal.heap.HeapLongLongArrayMap(newParent);
                    cloned.mapSize = this.mapSize;
                    cloned.capacity = this.capacity;
                    if (this.keys != null) {
                        var cloned_keys = new Float64Array(this.capacity);
                        java.lang.System.arraycopy(this.keys, 0, cloned_keys, 0, this.capacity);
                        cloned.keys = cloned_keys;
                    }
                    if (this.values != null) {
                        var cloned_values = new Float64Array(this.capacity);
                        java.lang.System.arraycopy(this.values, 0, cloned_values, 0, this.capacity);
                        cloned.values = cloned_values;
                    }
                    if (this.nexts != null) {
                        var cloned_nexts = new Int32Array(this.capacity);
                        java.lang.System.arraycopy(this.nexts, 0, cloned_nexts, 0, this.capacity);
                        cloned.nexts = cloned_nexts;
                    }
                    if (this.hashs != null) {
                        var cloned_hashs = new Int32Array(this.capacity * 2);
                        java.lang.System.arraycopy(this.hashs, 0, cloned_hashs, 0, this.capacity * 2);
                        cloned.hashs = cloned_hashs;
                    }
                    return cloned;
                };
                HeapLongLongArrayMap.prototype.get = function (requestKey) {
                    var result = new Float64Array(0);
                    {
                        if (this.keys != null) {
                            var hashIndex = greycat.utility.HashHelper.longHash(requestKey, this.capacity * 2);
                            var resultCapacity = 0;
                            var resultIndex = 0;
                            var m = this.hash(hashIndex);
                            while (m >= 0) {
                                if (requestKey == this.key(m)) {
                                    if (resultIndex == resultCapacity) {
                                        var newCapacity = void 0;
                                        if (resultCapacity == 0) {
                                            newCapacity = 1;
                                        }
                                        else {
                                            newCapacity = resultCapacity * 2;
                                        }
                                        var tempResult = new Float64Array(newCapacity);
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
                                var shrinkedResult = new Float64Array(resultIndex);
                                java.lang.System.arraycopy(result, 0, shrinkedResult, 0, resultIndex);
                                result = shrinkedResult;
                            }
                        }
                    }
                    return result;
                };
                HeapLongLongArrayMap.prototype.contains = function (requestKey, requestValue) {
                    var result = false;
                    {
                        if (this.keys != null) {
                            var hashIndex = greycat.utility.HashHelper.longHash(requestKey, this.capacity * 2);
                            var m = this.hash(hashIndex);
                            while (m >= 0 && !result) {
                                if (requestKey == this.key(m) && requestValue == this.value(m)) {
                                    result = true;
                                }
                                m = this.next(m);
                            }
                        }
                    }
                    return result;
                };
                HeapLongLongArrayMap.prototype.each = function (callback) {
                    {
                        this.unsafe_each(callback);
                    }
                };
                HeapLongLongArrayMap.prototype.unsafe_each = function (callback) {
                    for (var i = 0; i < this.mapSize; i++) {
                        callback(this.key(i), this.value(i));
                    }
                };
                HeapLongLongArrayMap.prototype.size = function () {
                    var result;
                    {
                        result = this.mapSize;
                    }
                    return result;
                };
                HeapLongLongArrayMap.prototype.delete = function (requestKey, requestValue) {
                    {
                        if (this.keys != null && this.mapSize != 0) {
                            var hashCapacity = this.capacity * 2;
                            var hashIndex = greycat.utility.HashHelper.longHash(requestKey, hashCapacity);
                            var m = this.hash(hashIndex);
                            var found = -1;
                            while (m >= 0) {
                                if (requestKey == this.key(m) && requestValue == this.value(m)) {
                                    found = m;
                                    break;
                                }
                                m = this.next(m);
                            }
                            if (found != -1) {
                                var toRemoveHash = greycat.utility.HashHelper.longHash(requestKey, hashCapacity);
                                m = this.hash(toRemoveHash);
                                if (m == found) {
                                    this.setHash(toRemoveHash, this.next(m));
                                }
                                else {
                                    while (m != -1) {
                                        var next_of_m = this.next(m);
                                        if (next_of_m == found) {
                                            this.setNext(m, this.next(next_of_m));
                                            break;
                                        }
                                        m = next_of_m;
                                    }
                                }
                                var lastIndex = this.mapSize - 1;
                                if (lastIndex == found) {
                                    this.mapSize--;
                                }
                                else {
                                    var lastKey = this.key(lastIndex);
                                    this.setKey(found, lastKey);
                                    this.setValue(found, this.value(lastIndex));
                                    this.setNext(found, this.next(lastIndex));
                                    var victimHash = greycat.utility.HashHelper.longHash(lastKey, hashCapacity);
                                    m = this.hash(victimHash);
                                    if (m == lastIndex) {
                                        this.setHash(victimHash, found);
                                    }
                                    else {
                                        while (m != -1) {
                                            var next_of_m = this.next(m);
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
                    }
                };
                HeapLongLongArrayMap.prototype.put = function (insertKey, insertValue) {
                    {
                        if (this.keys == null) {
                            this.reallocate(greycat.Constants.MAP_INITIAL_CAPACITY);
                            this.setKey(0, insertKey);
                            this.setValue(0, insertValue);
                            this.setHash(greycat.utility.HashHelper.longHash(insertKey, this.capacity * 2), 0);
                            this.setNext(0, -1);
                            this.mapSize++;
                            this.parent.declareDirty();
                        }
                        else {
                            var hashCapacity = this.capacity * 2;
                            var insertKeyHash = greycat.utility.HashHelper.longHash(insertKey, hashCapacity);
                            var currentHash = this.hash(insertKeyHash);
                            var m = currentHash;
                            var found = -1;
                            while (m >= 0) {
                                if (insertKey == this.key(m) && insertValue == this.value(m)) {
                                    found = m;
                                    break;
                                }
                                m = this.next(m);
                            }
                            if (found == -1) {
                                var lastIndex = this.mapSize;
                                if (lastIndex == this.capacity) {
                                    this.reallocate(this.capacity * 2);
                                    hashCapacity = this.capacity * 2;
                                    insertKeyHash = greycat.utility.HashHelper.longHash(insertKey, hashCapacity);
                                    currentHash = this.hash(insertKeyHash);
                                }
                                this.setKey(lastIndex, insertKey);
                                this.setValue(lastIndex, insertValue);
                                this.setHash(greycat.utility.HashHelper.longHash(insertKey, this.capacity * 2), lastIndex);
                                this.setNext(lastIndex, currentHash);
                                this.mapSize++;
                                this.parent.declareDirty();
                            }
                        }
                    }
                };
                HeapLongLongArrayMap.prototype.load = function (buffer, offset, max) {
                    var cursor = offset;
                    var current = buffer.read(cursor);
                    var isFirst = true;
                    var previous = offset;
                    var previousKey = -1;
                    var waitingVal = false;
                    while (cursor < max && current != greycat.Constants.CHUNK_SEP && current != greycat.Constants.CHUNK_ENODE_SEP && current != greycat.Constants.CHUNK_ESEP) {
                        if (current == greycat.Constants.CHUNK_VAL_SEP) {
                            if (isFirst) {
                                this.reallocate(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                                isFirst = false;
                            }
                            else {
                                if (!waitingVal) {
                                    previousKey = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                                    waitingVal = true;
                                }
                                else {
                                    waitingVal = false;
                                    this.put(previousKey, greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                }
                            }
                            previous = cursor + 1;
                        }
                        cursor++;
                        if (cursor < max) {
                            current = buffer.read(cursor);
                        }
                    }
                    if (isFirst) {
                        this.reallocate(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                    }
                    else {
                        if (waitingVal) {
                            this.put(previousKey, greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                        }
                    }
                    return cursor;
                };
                return HeapLongLongArrayMap;
            }());
            heap.HeapLongLongArrayMap = HeapLongLongArrayMap;
            var HeapLongLongMap = (function () {
                function HeapLongLongMap(p_listener) {
                    this.mapSize = 0;
                    this.capacity = 0;
                    this.keys = null;
                    this.values = null;
                    this.nexts = null;
                    this.hashs = null;
                    this.parent = p_listener;
                }
                HeapLongLongMap.prototype.key = function (i) {
                    return this.keys[i];
                };
                HeapLongLongMap.prototype.setKey = function (i, newValue) {
                    this.keys[i] = newValue;
                };
                HeapLongLongMap.prototype.value = function (i) {
                    return this.values[i];
                };
                HeapLongLongMap.prototype.setValue = function (i, newValue) {
                    this.values[i] = newValue;
                };
                HeapLongLongMap.prototype.next = function (i) {
                    return this.nexts[i];
                };
                HeapLongLongMap.prototype.setNext = function (i, newValue) {
                    this.nexts[i] = newValue;
                };
                HeapLongLongMap.prototype.hash = function (i) {
                    return this.hashs[i];
                };
                HeapLongLongMap.prototype.setHash = function (i, newValue) {
                    this.hashs[i] = newValue;
                };
                HeapLongLongMap.prototype.reallocate = function (newCapacity) {
                    if (newCapacity > this.capacity) {
                        var new_keys = new Float64Array(newCapacity);
                        if (this.keys != null) {
                            java.lang.System.arraycopy(this.keys, 0, new_keys, 0, this.capacity);
                        }
                        this.keys = new_keys;
                        var new_values = new Float64Array(newCapacity);
                        if (this.values != null) {
                            java.lang.System.arraycopy(this.values, 0, new_values, 0, this.capacity);
                        }
                        this.values = new_values;
                        var new_nexts = new Int32Array(newCapacity);
                        var new_hashes = new Int32Array(newCapacity * 2);
                        java.util.Arrays.fill(new_nexts, 0, newCapacity, -1);
                        java.util.Arrays.fill(new_hashes, 0, (newCapacity * 2), -1);
                        this.hashs = new_hashes;
                        this.nexts = new_nexts;
                        for (var i = 0; i < this.mapSize; i++) {
                            var new_key_hash = greycat.utility.HashHelper.longHash(this.key(i), newCapacity * 2);
                            this.setNext(i, this.hash(new_key_hash));
                            this.setHash(new_key_hash, i);
                        }
                        this.capacity = newCapacity;
                    }
                };
                HeapLongLongMap.prototype.cloneFor = function (newParent) {
                    var cloned = new greycat.internal.heap.HeapLongLongMap(newParent);
                    cloned.mapSize = this.mapSize;
                    cloned.capacity = this.capacity;
                    if (this.keys != null) {
                        var cloned_keys = new Float64Array(this.capacity);
                        java.lang.System.arraycopy(this.keys, 0, cloned_keys, 0, this.capacity);
                        cloned.keys = cloned_keys;
                    }
                    if (this.values != null) {
                        var cloned_values = new Float64Array(this.capacity);
                        java.lang.System.arraycopy(this.values, 0, cloned_values, 0, this.capacity);
                        cloned.values = cloned_values;
                    }
                    if (this.nexts != null) {
                        var cloned_nexts = new Int32Array(this.capacity);
                        java.lang.System.arraycopy(this.nexts, 0, cloned_nexts, 0, this.capacity);
                        cloned.nexts = cloned_nexts;
                    }
                    if (this.hashs != null) {
                        var cloned_hashs = new Int32Array(this.capacity * 2);
                        java.lang.System.arraycopy(this.hashs, 0, cloned_hashs, 0, this.capacity * 2);
                        cloned.hashs = cloned_hashs;
                    }
                    return cloned;
                };
                HeapLongLongMap.prototype.get = function (requestKey) {
                    var result = greycat.Constants.NULL_LONG;
                    {
                        if (this.keys != null) {
                            var hashIndex = greycat.utility.HashHelper.longHash(requestKey, this.capacity * 2);
                            var m = this.hash(hashIndex);
                            while (m >= 0) {
                                if (requestKey == this.key(m)) {
                                    result = this.value(m);
                                    break;
                                }
                                m = this.next(m);
                            }
                        }
                    }
                    return result;
                };
                HeapLongLongMap.prototype.each = function (callback) {
                    {
                        this.unsafe_each(callback);
                    }
                };
                HeapLongLongMap.prototype.unsafe_each = function (callback) {
                    for (var i = 0; i < this.mapSize; i++) {
                        callback(this.key(i), this.value(i));
                    }
                };
                HeapLongLongMap.prototype.size = function () {
                    var result;
                    {
                        result = this.mapSize;
                    }
                    return result;
                };
                HeapLongLongMap.prototype.remove = function (requestKey) {
                    {
                        if (this.keys != null && this.mapSize != 0) {
                            var hashCapacity = this.capacity * 2;
                            var hashIndex = greycat.utility.HashHelper.longHash(requestKey, hashCapacity);
                            var m = this.hash(hashIndex);
                            var found = -1;
                            while (m >= 0) {
                                if (requestKey == this.key(m)) {
                                    found = m;
                                    break;
                                }
                                m = this.next(m);
                            }
                            if (found != -1) {
                                var toRemoveHash = greycat.utility.HashHelper.longHash(requestKey, hashCapacity);
                                m = this.hash(toRemoveHash);
                                if (m == found) {
                                    this.setHash(toRemoveHash, this.next(m));
                                }
                                else {
                                    while (m != -1) {
                                        var next_of_m = this.next(m);
                                        if (next_of_m == found) {
                                            this.setNext(m, this.next(next_of_m));
                                            break;
                                        }
                                        m = next_of_m;
                                    }
                                }
                                var lastIndex = this.mapSize - 1;
                                if (lastIndex == found) {
                                    this.mapSize--;
                                }
                                else {
                                    var lastKey = this.key(lastIndex);
                                    this.setKey(found, lastKey);
                                    this.setValue(found, this.value(lastIndex));
                                    this.setNext(found, this.next(lastIndex));
                                    var victimHash = greycat.utility.HashHelper.longHash(lastKey, hashCapacity);
                                    m = this.hash(victimHash);
                                    if (m == lastIndex) {
                                        this.setHash(victimHash, found);
                                    }
                                    else {
                                        while (m != -1) {
                                            var next_of_m = this.next(m);
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
                    }
                };
                HeapLongLongMap.prototype.put = function (insertKey, insertValue) {
                    {
                        if (this.keys == null) {
                            this.reallocate(greycat.Constants.MAP_INITIAL_CAPACITY);
                            this.setKey(0, insertKey);
                            this.setValue(0, insertValue);
                            this.setHash(greycat.utility.HashHelper.longHash(insertKey, this.capacity * 2), 0);
                            this.setNext(0, -1);
                            this.mapSize++;
                        }
                        else {
                            var hashCapacity = this.capacity * 2;
                            var insertKeyHash = greycat.utility.HashHelper.longHash(insertKey, hashCapacity);
                            var currentHash = this.hash(insertKeyHash);
                            var m = currentHash;
                            var found = -1;
                            while (m >= 0) {
                                if (insertKey == this.key(m)) {
                                    found = m;
                                    break;
                                }
                                m = this.next(m);
                            }
                            if (found == -1) {
                                var lastIndex = this.mapSize;
                                if (lastIndex == this.capacity) {
                                    this.reallocate(this.capacity * 2);
                                    hashCapacity = this.capacity * 2;
                                    insertKeyHash = greycat.utility.HashHelper.longHash(insertKey, hashCapacity);
                                    currentHash = this.hash(insertKeyHash);
                                }
                                this.setKey(lastIndex, insertKey);
                                this.setValue(lastIndex, insertValue);
                                this.setHash(greycat.utility.HashHelper.longHash(insertKey, this.capacity * 2), lastIndex);
                                this.setNext(lastIndex, currentHash);
                                this.mapSize++;
                                this.parent.declareDirty();
                            }
                            else {
                                if (this.value(found) != insertValue) {
                                    this.setValue(found, insertValue);
                                    this.parent.declareDirty();
                                }
                            }
                        }
                    }
                };
                HeapLongLongMap.prototype.load = function (buffer, offset, max) {
                    var cursor = offset;
                    var current = buffer.read(cursor);
                    var isFirst = true;
                    var previous = offset;
                    var previousKey = -1;
                    var waitingVal = false;
                    while (cursor < max && current != greycat.Constants.CHUNK_SEP && current != greycat.Constants.CHUNK_ENODE_SEP && current != greycat.Constants.CHUNK_ESEP) {
                        if (current == greycat.Constants.CHUNK_VAL_SEP) {
                            if (isFirst) {
                                this.reallocate(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                                isFirst = false;
                            }
                            else {
                                if (!waitingVal) {
                                    previousKey = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                                    waitingVal = true;
                                }
                                else {
                                    waitingVal = false;
                                    this.put(previousKey, greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                }
                            }
                            previous = cursor + 1;
                        }
                        cursor++;
                        if (cursor < max) {
                            current = buffer.read(cursor);
                        }
                    }
                    if (isFirst) {
                        this.reallocate(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                    }
                    else {
                        if (waitingVal) {
                            this.put(previousKey, greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                        }
                    }
                    return cursor;
                };
                return HeapLongLongMap;
            }());
            heap.HeapLongLongMap = HeapLongLongMap;
            var HeapMemoryFactory = (function () {
                function HeapMemoryFactory() {
                }
                HeapMemoryFactory.prototype.newSpace = function (memorySize, graph, deepWorld) {
                    return new greycat.internal.heap.HeapChunkSpace(memorySize, graph, deepWorld);
                };
                HeapMemoryFactory.prototype.newBuffer = function () {
                    return new greycat.internal.heap.HeapBuffer();
                };
                return HeapMemoryFactory;
            }());
            heap.HeapMemoryFactory = HeapMemoryFactory;
            var HeapRelation = (function () {
                function HeapRelation(p_parent, origin) {
                    this.aligned = true;
                    this.parent = p_parent;
                    if (origin != null) {
                        this.aligned = false;
                        this._back = origin._back;
                        this._size = origin._size;
                    }
                    else {
                        this._back = null;
                        this._size = 0;
                    }
                }
                HeapRelation.prototype.allocate = function (_capacity) {
                    var new_back = new Float64Array(_capacity);
                    if (this._back != null) {
                        java.lang.System.arraycopy(this._back, 0, new_back, 0, this._back.length);
                    }
                    this._back = new_back;
                    this.aligned = true;
                };
                HeapRelation.prototype.all = function () {
                    var ids;
                    {
                        if (this._back == null) {
                            ids = new Float64Array(0);
                        }
                        else {
                            var relSize = this._back.length;
                            ids = new Float64Array(relSize);
                            for (var i = 0; i < relSize; i++) {
                                ids[i] = this._back[i];
                            }
                        }
                    }
                    return ids;
                };
                HeapRelation.prototype.size = function () {
                    return this._size;
                };
                HeapRelation.prototype.get = function (index) {
                    var result;
                    {
                        result = this._back[index];
                    }
                    return result;
                };
                HeapRelation.prototype.set = function (index, value) {
                    {
                        this._back[index] = value;
                    }
                };
                HeapRelation.prototype.unsafe_get = function (index) {
                    return this._back[index];
                };
                HeapRelation.prototype.addNode = function (node) {
                    return this.add(node.id());
                };
                HeapRelation.prototype.add = function (newValue) {
                    {
                        if (this._back == null) {
                            this.aligned = true;
                            this._back = new Float64Array(greycat.Constants.MAP_INITIAL_CAPACITY);
                            this._back[0] = newValue;
                            this._size = 1;
                        }
                        else if (this._size == this._back.length) {
                            var ex_back = new Float64Array(this._back.length * 2);
                            java.lang.System.arraycopy(this._back, 0, ex_back, 0, this._size);
                            this._back = ex_back;
                            this._back[this._size] = newValue;
                            this.aligned = true;
                            this._size++;
                        }
                        else {
                            if (!this.aligned) {
                                var temp_back = new Float64Array(this._back.length);
                                java.lang.System.arraycopy(this._back, 0, temp_back, 0, this._back.length);
                                this._back = temp_back;
                                this.aligned = true;
                            }
                            this._back[this._size] = newValue;
                            this._size++;
                        }
                        this.parent.declareDirty();
                    }
                    return this;
                };
                HeapRelation.prototype.addAll = function (newValues) {
                    {
                        var nextSize = newValues.length + this._size;
                        var closePowerOfTwo = Math.pow(2, Math.ceil(Math.log(nextSize) / Math.log(2)));
                        this.allocate(closePowerOfTwo);
                        java.lang.System.arraycopy(newValues, 0, this._back, this._size, newValues.length);
                        this.parent.declareDirty();
                    }
                    return this;
                };
                HeapRelation.prototype.insert = function (targetIndex, newValue) {
                    {
                        if (this._back == null) {
                            if (targetIndex != 0) {
                                throw new Error("Bad API usage ! index out of bounds: " + targetIndex);
                            }
                            this._back = new Float64Array(greycat.Constants.MAP_INITIAL_CAPACITY);
                            this._back[0] = newValue;
                            this._size = 1;
                            this.aligned = true;
                        }
                        else if (this._size == this._back.length) {
                            if (targetIndex > this._size) {
                                throw new Error("Bad API usage ! index out of bounds: " + targetIndex);
                            }
                            var ex_back = new Float64Array(this._back.length * 2);
                            if (this._size == targetIndex) {
                                java.lang.System.arraycopy(this._back, 0, ex_back, 0, this._size);
                                this._back = ex_back;
                                this._back[this._size] = newValue;
                                this._size++;
                            }
                            else {
                                java.lang.System.arraycopy(this._back, 0, ex_back, 0, targetIndex);
                                ex_back[targetIndex] = newValue;
                                java.lang.System.arraycopy(this._back, targetIndex, ex_back, targetIndex + 1, (this._size - targetIndex));
                                this._back = ex_back;
                                this._size++;
                            }
                            this.aligned = true;
                        }
                        else {
                            if (targetIndex > this._size) {
                                throw new Error("Bad API usage ! index out of bounds: " + targetIndex);
                            }
                            if (!this.aligned) {
                                var temp_back = new Float64Array(this._back.length);
                                java.lang.System.arraycopy(this._back, 0, temp_back, 0, this._back.length);
                                this._back = temp_back;
                                this.aligned = true;
                            }
                            var afterIndexSize = this._size - targetIndex;
                            var temp = new Float64Array(afterIndexSize);
                            java.lang.System.arraycopy(this._back, targetIndex, temp, 0, afterIndexSize);
                            this._back[targetIndex] = newValue;
                            java.lang.System.arraycopy(temp, 0, this._back, targetIndex + 1, afterIndexSize);
                            this._size++;
                        }
                        this.parent.declareDirty();
                    }
                    return this;
                };
                HeapRelation.prototype.remove = function (oldValue) {
                    {
                        var indexToRemove = -1;
                        for (var i = 0; i < this._size; i++) {
                            if (this._back[i] == oldValue) {
                                indexToRemove = i;
                                break;
                            }
                        }
                        if (indexToRemove != -1) {
                            if (!this.aligned) {
                                var temp_back = new Float64Array(this._back.length);
                                java.lang.System.arraycopy(this._back, 0, temp_back, 0, this._back.length);
                                this._back = temp_back;
                                this.aligned = true;
                            }
                            java.lang.System.arraycopy(this._back, indexToRemove + 1, this._back, indexToRemove, this._size - indexToRemove - 1);
                            this._size--;
                        }
                    }
                    return this;
                };
                HeapRelation.prototype.delete = function (toRemoveIndex) {
                    {
                        if (toRemoveIndex != -1) {
                            if (!this.aligned) {
                                var temp_back = new Float64Array(this._back.length);
                                java.lang.System.arraycopy(this._back, 0, temp_back, 0, this._back.length);
                                this._back = temp_back;
                                this.aligned = true;
                            }
                            java.lang.System.arraycopy(this._back, toRemoveIndex + 1, this._back, toRemoveIndex, this._size - toRemoveIndex - 1);
                            this._size--;
                        }
                    }
                    return this;
                };
                HeapRelation.prototype.clear = function () {
                    {
                        this._size = 0;
                    }
                    return this;
                };
                HeapRelation.prototype.toString = function () {
                    var buffer = new java.lang.StringBuilder();
                    buffer.append("[");
                    for (var i = 0; i < this._size; i++) {
                        if (i != 0) {
                            buffer.append(",");
                        }
                        buffer.append(this._back[i]);
                    }
                    buffer.append("]");
                    return buffer.toString();
                };
                HeapRelation.prototype.load = function (buffer, offset, max) {
                    var cursor = offset;
                    var current = buffer.read(cursor);
                    var isFirst = true;
                    var previous = offset;
                    while (cursor < max && current != greycat.Constants.CHUNK_SEP && current != greycat.Constants.CHUNK_ENODE_SEP && current != greycat.Constants.CHUNK_ESEP) {
                        if (current == greycat.Constants.CHUNK_VAL_SEP) {
                            if (isFirst) {
                                this.allocate(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                                isFirst = false;
                            }
                            else {
                                this.add(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                            }
                            previous = cursor + 1;
                        }
                        cursor++;
                        if (cursor < max) {
                            current = buffer.read(cursor);
                        }
                    }
                    if (isFirst) {
                        this.allocate(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                    }
                    else {
                        this.add(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                    }
                    return cursor;
                };
                return HeapRelation;
            }());
            heap.HeapRelation = HeapRelation;
            var HeapRelationIndexed = (function (_super) {
                __extends(HeapRelationIndexed, _super);
                function HeapRelationIndexed(p_listener, graph) {
                    var _this = _super.call(this, p_listener) || this;
                    _this._graph = graph;
                    return _this;
                }
                HeapRelationIndexed.prototype.add = function (node) {
                    var attributeNames = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        attributeNames[_i - 1] = arguments[_i];
                    }
                    this.internal_add_remove.apply(this, [true, node].concat(attributeNames));
                    return this;
                };
                HeapRelationIndexed.prototype.remove = function (node) {
                    var attributeNames = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        attributeNames[_i - 1] = arguments[_i];
                    }
                    this.internal_add_remove.apply(this, [false, node].concat(attributeNames));
                    return this;
                };
                HeapRelationIndexed.prototype.internal_add_remove = function (isIndex, node) {
                    var attributeNames = [];
                    for (var _i = 2; _i < arguments.length; _i++) {
                        attributeNames[_i - 2] = arguments[_i];
                    }
                    var flatQuery = node.graph().newQuery();
                    var toIndexNodeState = node.graph().resolver().resolveState(node);
                    for (var i = 0; i < attributeNames.length; i++) {
                        var attKey = attributeNames[i];
                        var attValue = toIndexNodeState.getFromKey(attKey);
                        if (attValue != null) {
                            flatQuery.add(attKey, attValue.toString());
                        }
                        else {
                            flatQuery.add(attKey, null);
                        }
                    }
                    if (isIndex) {
                        this.put(flatQuery.hash(), node.id());
                    }
                    else {
                        this.delete(flatQuery.hash(), node.id());
                    }
                };
                HeapRelationIndexed.prototype.clear = function () {
                    return this;
                };
                HeapRelationIndexed.prototype.find = function (callback, world, time) {
                    var params = [];
                    for (var _i = 3; _i < arguments.length; _i++) {
                        params[_i - 3] = arguments[_i];
                    }
                    var queryObj = this._graph.newQuery();
                    queryObj.setWorld(world);
                    queryObj.setTime(time);
                    var previous = null;
                    for (var i = 0; i < params.length; i++) {
                        if (previous != null) {
                            queryObj.add(previous, params[i]);
                            previous = null;
                        }
                        else {
                            previous = params[i];
                        }
                    }
                    this.findByQuery(queryObj, callback);
                };
                HeapRelationIndexed.prototype.findByQuery = function (query, callback) {
                    var _this = this;
                    var foundIds = this.get(query.hash());
                    if (foundIds == null) {
                        callback(new Array(0));
                    }
                    else {
                        this._graph.resolver().lookupAll(query.world(), query.time(), foundIds, function (resolved) {
                            {
                                var resultSet = new Array(foundIds.length);
                                var resultSetIndex = 0;
                                for (var i = 0; i < resultSet.length; i++) {
                                    var resolvedNode = resolved[i];
                                    if (resolvedNode != null) {
                                        var resolvedState = _this._graph.resolver().resolveState(resolvedNode);
                                        var exact = true;
                                        for (var j = 0; j < query.attributes().length; j++) {
                                            var obj = resolvedState.get(query.attributes()[j]);
                                            if (query.values()[j] == null) {
                                                if (obj != null) {
                                                    exact = false;
                                                    break;
                                                }
                                            }
                                            else {
                                                if (obj == null) {
                                                    exact = false;
                                                    break;
                                                }
                                                else {
                                                    if (obj instanceof Float64Array) {
                                                        if (query.values()[j] instanceof Float64Array) {
                                                            if (!greycat.Constants.longArrayEquals(query.values()[j], obj)) {
                                                                exact = false;
                                                                break;
                                                            }
                                                        }
                                                        else {
                                                            exact = false;
                                                            break;
                                                        }
                                                    }
                                                    else {
                                                        if (!greycat.Constants.equals(query.values()[j].toString(), obj.toString())) {
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
                                }
                                else {
                                    var trimmedResultSet = new Array(resultSetIndex);
                                    java.lang.System.arraycopy(resultSet, 0, trimmedResultSet, 0, resultSetIndex);
                                    callback(trimmedResultSet);
                                }
                            }
                        });
                    }
                };
                HeapRelationIndexed.prototype.all = function () {
                    var flat = new Float64Array(this.size());
                    var i = new Int32Array([0]);
                    this.each(function (key, value) {
                        {
                            flat[i[0]] = value;
                            i[0]++;
                        }
                    });
                    return flat;
                };
                HeapRelationIndexed.prototype.cloneIRelFor = function (newParent, graph) {
                    var cloned = new greycat.internal.heap.HeapRelationIndexed(newParent, graph);
                    cloned.mapSize = this.mapSize;
                    cloned.capacity = this.capacity;
                    if (this.keys != null) {
                        var cloned_keys = new Float64Array(this.capacity);
                        java.lang.System.arraycopy(this.keys, 0, cloned_keys, 0, this.capacity);
                        cloned.keys = cloned_keys;
                    }
                    if (this.values != null) {
                        var cloned_values = new Float64Array(this.capacity);
                        java.lang.System.arraycopy(this.values, 0, cloned_values, 0, this.capacity);
                        cloned.values = cloned_values;
                    }
                    if (this.nexts != null) {
                        var cloned_nexts = new Int32Array(this.capacity);
                        java.lang.System.arraycopy(this.nexts, 0, cloned_nexts, 0, this.capacity);
                        cloned.nexts = cloned_nexts;
                    }
                    if (this.hashs != null) {
                        var cloned_hashs = new Int32Array(this.capacity * 2);
                        java.lang.System.arraycopy(this.hashs, 0, cloned_hashs, 0, this.capacity * 2);
                        cloned.hashs = cloned_hashs;
                    }
                    return cloned;
                };
                return HeapRelationIndexed;
            }(greycat.internal.heap.HeapLongLongArrayMap));
            heap.HeapRelationIndexed = HeapRelationIndexed;
            var HeapStateChunk = (function () {
                function HeapStateChunk(p_space, p_index) {
                    this._space = p_space;
                    this._index = p_index;
                    this.next_and_hash = null;
                    this._type = null;
                    this._size = 0;
                    this._capacity = 0;
                    this._dirty = false;
                }
                HeapStateChunk.prototype.graph = function () {
                    return this._space.graph();
                };
                HeapStateChunk.prototype.world = function () {
                    return this._space.worldByIndex(this._index);
                };
                HeapStateChunk.prototype.time = function () {
                    return this._space.timeByIndex(this._index);
                };
                HeapStateChunk.prototype.id = function () {
                    return this._space.idByIndex(this._index);
                };
                HeapStateChunk.prototype.chunkType = function () {
                    return greycat.chunk.ChunkType.STATE_CHUNK;
                };
                HeapStateChunk.prototype.index = function () {
                    return this._index;
                };
                HeapStateChunk.prototype.get = function (p_key) {
                    return this.internal_get(p_key);
                };
                HeapStateChunk.prototype.internal_find = function (p_key) {
                    if (this._size == 0) {
                        return -1;
                    }
                    else if (this.next_and_hash == null) {
                        for (var i = 0; i < this._size; i++) {
                            if (this._k[i] == p_key) {
                                return i;
                            }
                        }
                        return -1;
                    }
                    else {
                        var hashIndex = p_key % (this._capacity * 2);
                        if (hashIndex < 0) {
                            hashIndex = hashIndex * -1;
                        }
                        var m = this.next_and_hash[this._capacity + hashIndex];
                        while (m >= 0) {
                            if (p_key == this._k[m]) {
                                return m;
                            }
                            else {
                                m = this.next_and_hash[m];
                            }
                        }
                        return -1;
                    }
                };
                HeapStateChunk.prototype.internal_get = function (p_key) {
                    if (this._size == 0) {
                        return null;
                    }
                    var found = this.internal_find(p_key);
                    var result;
                    if (found != -1) {
                        result = this._v[found];
                        if (result != null) {
                            switch (this._type[found]) {
                                case greycat.Type.DOUBLE_ARRAY:
                                    var castedResultD = result;
                                    var copyD = new Float64Array(castedResultD.length);
                                    java.lang.System.arraycopy(castedResultD, 0, copyD, 0, castedResultD.length);
                                    return copyD;
                                case greycat.Type.LONG_ARRAY:
                                    var castedResultL = result;
                                    var copyL = new Float64Array(castedResultL.length);
                                    java.lang.System.arraycopy(castedResultL, 0, copyL, 0, castedResultL.length);
                                    return copyL;
                                case greycat.Type.INT_ARRAY:
                                    var castedResultI = result;
                                    var copyI = new Int32Array(castedResultI.length);
                                    java.lang.System.arraycopy(castedResultI, 0, copyI, 0, castedResultI.length);
                                    return copyI;
                                default:
                                    return result;
                            }
                        }
                    }
                    return null;
                };
                HeapStateChunk.prototype.set = function (p_elementIndex, p_elemType, p_unsafe_elem) {
                    if (p_unsafe_elem != null) {
                        if (p_elemType == Type.STRING) {
                            if (!(typeof p_unsafe_elem === 'string')) {
                                throw new Error("mwDB usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem);
                            }
                        }
                        if (p_elemType == Type.BOOL) {
                            if (!(typeof p_unsafe_elem === 'boolean')) {
                                throw new Error("mwDB usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem);
                            }
                        }
                        if (p_elemType == Type.DOUBLE || p_elemType == Type.LONG || p_elemType == Type.INT) {
                            if (!(typeof p_unsafe_elem === 'number')) {
                                throw new Error("mwDB usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem);
                            }
                        }
                        if (p_elemType == Type.DOUBLE_ARRAY) {
                            if (!(p_unsafe_elem instanceof Float64Array)) {
                                throw new Error("mwDB usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem);
                            }
                        }
                        if (p_elemType == Type.LONG_ARRAY) {
                            if (!(p_unsafe_elem instanceof Float64Array)) {
                                throw new Error("mwDB usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem);
                            }
                        }
                        if (p_elemType == Type.INT_ARRAY) {
                            if (!(p_unsafe_elem instanceof Int32Array)) {
                                throw new Error("mwDB usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem);
                            }
                        }
                        if (p_elemType == Type.STRING_TO_INT_MAP) {
                            if (!(typeof p_unsafe_elem === 'object')) {
                                throw new Error("mwDB usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem);
                            }
                        }
                        if (p_elemType == Type.LONG_TO_LONG_MAP) {
                            if (!(typeof p_unsafe_elem === 'boolean')) {
                                throw new Error("mwDB usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem);
                            }
                        }
                        if (p_elemType == Type.LONG_TO_LONG_ARRAY_MAP) {
                            if (!(typeof p_unsafe_elem === 'boolean')) {
                                throw new Error("mwDB usage error, set method called with type " + Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem);
                            }
                        }
                    }
                    this.internal_set(p_elementIndex, p_elemType, p_unsafe_elem, true, false);
                };
                HeapStateChunk.prototype.setFromKey = function (key, p_elemType, p_unsafe_elem) {
                    this.internal_set(this._space.graph().resolver().stringToHash(key, true), p_elemType, p_unsafe_elem, true, false);
                };
                HeapStateChunk.prototype.getFromKey = function (key) {
                    return this.internal_get(this._space.graph().resolver().stringToHash(key, false));
                };
                HeapStateChunk.prototype.getFromKeyWithDefault = function (key, defaultValue) {
                    var result = this.getFromKey(key);
                    if (result == null) {
                        return defaultValue;
                    }
                    else {
                        return result;
                    }
                };
                HeapStateChunk.prototype.getWithDefault = function (key, defaultValue) {
                    var result = this.get(key);
                    if (result == null) {
                        return defaultValue;
                    }
                    else {
                        return result;
                    }
                };
                HeapStateChunk.prototype.getType = function (p_key) {
                    var found_index = this.internal_find(p_key);
                    if (found_index != -1) {
                        return this._type[found_index];
                    }
                    else {
                        return -1;
                    }
                };
                HeapStateChunk.prototype.getTypeFromKey = function (key) {
                    return this.getType(this._space.graph().resolver().stringToHash(key, false));
                };
                HeapStateChunk.prototype.getOrCreate = function (p_key, p_type) {
                    var found = this.internal_find(p_key);
                    if (found != -1) {
                        if (this._type[found] == p_type) {
                            return this._v[found];
                        }
                    }
                    var toSet = null;
                    switch (p_type) {
                        case greycat.Type.RELATION:
                            toSet = new greycat.internal.heap.HeapRelation(this, null);
                            break;
                        case greycat.Type.RELATION_INDEXED:
                            toSet = new greycat.internal.heap.HeapRelationIndexed(this, this._space.graph());
                            break;
                        case greycat.Type.DMATRIX:
                            toSet = new greycat.internal.heap.HeapDMatrix(this, null);
                            break;
                        case greycat.Type.LMATRIX:
                            toSet = new greycat.internal.heap.HeapLMatrix(this, null);
                            break;
                        case greycat.Type.EGRAPH:
                            toSet = new greycat.internal.heap.HeapEGraph(this, null, this._space.graph());
                            break;
                        case greycat.Type.STRING_TO_INT_MAP:
                            toSet = new greycat.internal.heap.HeapStringIntMap(this);
                            break;
                        case greycat.Type.LONG_TO_LONG_MAP:
                            toSet = new greycat.internal.heap.HeapLongLongMap(this);
                            break;
                        case greycat.Type.LONG_TO_LONG_ARRAY_MAP:
                            toSet = new greycat.internal.heap.HeapLongLongArrayMap(this);
                            break;
                    }
                    this.internal_set(p_key, p_type, toSet, true, false);
                    return toSet;
                };
                HeapStateChunk.prototype.getOrCreateFromKey = function (key, elemType) {
                    return this.getOrCreate(this._space.graph().resolver().stringToHash(key, true), elemType);
                };
                HeapStateChunk.prototype.declareDirty = function () {
                    if (this._space != null && !this._dirty) {
                        this._dirty = true;
                        this._space.notifyUpdate(this._index);
                    }
                };
                HeapStateChunk.prototype.save = function (buffer) {
                    greycat.utility.Base64.encodeIntToBuffer(this._size, buffer);
                    for (var i = 0; i < this._size; i++) {
                        var loopValue = this._v[i];
                        if (loopValue != null) {
                            buffer.write(greycat.internal.CoreConstants.CHUNK_SEP);
                            greycat.utility.Base64.encodeIntToBuffer(this._type[i], buffer);
                            buffer.write(greycat.internal.CoreConstants.CHUNK_SEP);
                            greycat.utility.Base64.encodeIntToBuffer(this._k[i], buffer);
                            buffer.write(greycat.internal.CoreConstants.CHUNK_SEP);
                            switch (this._type[i]) {
                                case greycat.Type.STRING:
                                    greycat.utility.Base64.encodeStringToBuffer(loopValue, buffer);
                                    break;
                                case greycat.Type.BOOL:
                                    if (this._v[i]) {
                                        greycat.utility.Base64.encodeIntToBuffer(greycat.internal.CoreConstants.BOOL_TRUE, buffer);
                                    }
                                    else {
                                        greycat.utility.Base64.encodeIntToBuffer(greycat.internal.CoreConstants.BOOL_FALSE, buffer);
                                    }
                                    break;
                                case greycat.Type.LONG:
                                    greycat.utility.Base64.encodeLongToBuffer(loopValue, buffer);
                                    break;
                                case greycat.Type.DOUBLE:
                                    greycat.utility.Base64.encodeDoubleToBuffer(loopValue, buffer);
                                    break;
                                case greycat.Type.INT:
                                    greycat.utility.Base64.encodeIntToBuffer(loopValue, buffer);
                                    break;
                                case greycat.Type.DOUBLE_ARRAY:
                                    var castedDoubleArr = loopValue;
                                    greycat.utility.Base64.encodeIntToBuffer(castedDoubleArr.length, buffer);
                                    for (var j = 0; j < castedDoubleArr.length; j++) {
                                        buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                        greycat.utility.Base64.encodeDoubleToBuffer(castedDoubleArr[j], buffer);
                                    }
                                    break;
                                case greycat.Type.LONG_ARRAY:
                                    var castedLongArr = loopValue;
                                    greycat.utility.Base64.encodeIntToBuffer(castedLongArr.length, buffer);
                                    for (var j = 0; j < castedLongArr.length; j++) {
                                        buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                        greycat.utility.Base64.encodeLongToBuffer(castedLongArr[j], buffer);
                                    }
                                    break;
                                case greycat.Type.INT_ARRAY:
                                    var castedIntArr = loopValue;
                                    greycat.utility.Base64.encodeIntToBuffer(castedIntArr.length, buffer);
                                    for (var j = 0; j < castedIntArr.length; j++) {
                                        buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                        greycat.utility.Base64.encodeIntToBuffer(castedIntArr[j], buffer);
                                    }
                                    break;
                                case greycat.Type.RELATION:
                                    var castedLongArrRel = loopValue;
                                    greycat.utility.Base64.encodeIntToBuffer(castedLongArrRel.size(), buffer);
                                    for (var j = 0; j < castedLongArrRel.size(); j++) {
                                        buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                        greycat.utility.Base64.encodeLongToBuffer(castedLongArrRel.unsafe_get(j), buffer);
                                    }
                                    break;
                                case greycat.Type.DMATRIX:
                                    var castedMatrix = loopValue;
                                    var unsafeContent = castedMatrix.unsafe_data();
                                    if (unsafeContent != null) {
                                        greycat.utility.Base64.encodeIntToBuffer(unsafeContent.length, buffer);
                                        for (var j = 0; j < unsafeContent.length; j++) {
                                            buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                            greycat.utility.Base64.encodeDoubleToBuffer(unsafeContent[j], buffer);
                                        }
                                    }
                                    break;
                                case greycat.Type.LMATRIX:
                                    var castedLMatrix = loopValue;
                                    var unsafeLContent = castedLMatrix.unsafe_data();
                                    if (unsafeLContent != null) {
                                        greycat.utility.Base64.encodeIntToBuffer(unsafeLContent.length, buffer);
                                        for (var j = 0; j < unsafeLContent.length; j++) {
                                            buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                            greycat.utility.Base64.encodeLongToBuffer(unsafeLContent[j], buffer);
                                        }
                                    }
                                    break;
                                case greycat.Type.STRING_TO_INT_MAP:
                                    var castedStringLongMap = loopValue;
                                    greycat.utility.Base64.encodeIntToBuffer(castedStringLongMap.size(), buffer);
                                    castedStringLongMap.unsafe_each(function (key, value) {
                                        {
                                            buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                            greycat.utility.Base64.encodeStringToBuffer(key, buffer);
                                            buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                            greycat.utility.Base64.encodeLongToBuffer(value, buffer);
                                        }
                                    });
                                    break;
                                case greycat.Type.LONG_TO_LONG_MAP:
                                    var castedLongLongMap = loopValue;
                                    greycat.utility.Base64.encodeIntToBuffer(castedLongLongMap.size(), buffer);
                                    castedLongLongMap.unsafe_each(function (key, value) {
                                        {
                                            buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                            greycat.utility.Base64.encodeLongToBuffer(key, buffer);
                                            buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                            greycat.utility.Base64.encodeLongToBuffer(value, buffer);
                                        }
                                    });
                                    break;
                                case greycat.Type.RELATION_INDEXED:
                                case greycat.Type.LONG_TO_LONG_ARRAY_MAP:
                                    var castedLongLongArrayMap = loopValue;
                                    greycat.utility.Base64.encodeIntToBuffer(castedLongLongArrayMap.size(), buffer);
                                    castedLongLongArrayMap.unsafe_each(function (key, value) {
                                        {
                                            buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                            greycat.utility.Base64.encodeLongToBuffer(key, buffer);
                                            buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                            greycat.utility.Base64.encodeLongToBuffer(value, buffer);
                                        }
                                    });
                                    break;
                                case greycat.Type.EGRAPH:
                                    var castedEGraph = loopValue;
                                    var eNodes = castedEGraph._nodes;
                                    var eGSize = castedEGraph.size();
                                    greycat.utility.Base64.encodeIntToBuffer(eGSize, buffer);
                                    for (var j = 0; j < eGSize; j++) {
                                        buffer.write(greycat.internal.CoreConstants.CHUNK_ENODE_SEP);
                                        eNodes[j].save(buffer);
                                    }
                                    castedEGraph._dirty = false;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    this._dirty = false;
                };
                HeapStateChunk.prototype.saveDiff = function (buffer) { };
                HeapStateChunk.prototype.each = function (callBack) {
                    for (var i = 0; i < this._size; i++) {
                        if (this._v[i] != null) {
                            callBack(this._k[i], this._type[i], this._v[i]);
                        }
                    }
                };
                HeapStateChunk.prototype.loadFrom = function (origin) {
                    if (origin == null) {
                        return;
                    }
                    var casted = origin;
                    this._capacity = casted._capacity;
                    this._size = casted._size;
                    if (casted._k != null) {
                        var cloned_k = new Int32Array(this._capacity);
                        java.lang.System.arraycopy(casted._k, 0, cloned_k, 0, this._capacity);
                        this._k = cloned_k;
                    }
                    if (casted._type != null) {
                        var cloned_type = new Int8Array(this._capacity);
                        java.lang.System.arraycopy(casted._type, 0, cloned_type, 0, this._capacity);
                        this._type = cloned_type;
                    }
                    if (casted.next_and_hash != null) {
                        var cloned_hash = new Int32Array(this._capacity * 3);
                        java.lang.System.arraycopy(casted.next_and_hash, 0, cloned_hash, 0, this._capacity * 3);
                        this.next_and_hash = cloned_hash;
                    }
                    if (casted._v != null) {
                        this._v = new Array(this._capacity);
                        for (var i = 0; i < this._size; i++) {
                            switch (casted._type[i]) {
                                case greycat.Type.LONG_TO_LONG_MAP:
                                    if (casted._v[i] != null) {
                                        this._v[i] = casted._v[i].cloneFor(this);
                                    }
                                    break;
                                case greycat.Type.RELATION_INDEXED:
                                    if (casted._v[i] != null) {
                                        this._v[i] = casted._v[i].cloneIRelFor(this, casted.graph());
                                    }
                                    break;
                                case greycat.Type.LONG_TO_LONG_ARRAY_MAP:
                                    if (casted._v[i] != null) {
                                        this._v[i] = casted._v[i].cloneFor(this);
                                    }
                                    break;
                                case greycat.Type.STRING_TO_INT_MAP:
                                    if (casted._v[i] != null) {
                                        this._v[i] = casted._v[i].cloneFor(this);
                                    }
                                    break;
                                case greycat.Type.RELATION:
                                    if (casted._v[i] != null) {
                                        this._v[i] = new greycat.internal.heap.HeapRelation(this, casted._v[i]);
                                    }
                                    break;
                                case greycat.Type.DMATRIX:
                                    if (casted._v[i] != null) {
                                        this._v[i] = new greycat.internal.heap.HeapDMatrix(this, casted._v[i]);
                                    }
                                    break;
                                case greycat.Type.LMATRIX:
                                    if (casted._v[i] != null) {
                                        this._v[i] = new greycat.internal.heap.HeapLMatrix(this, casted._v[i]);
                                    }
                                    break;
                                case greycat.Type.EGRAPH:
                                    if (casted._v[i] != null) {
                                        this._v[i] = new greycat.internal.heap.HeapEGraph(this, casted._v[i], this._space.graph());
                                    }
                                    break;
                                default:
                                    this._v[i] = casted._v[i];
                                    break;
                            }
                        }
                    }
                };
                HeapStateChunk.prototype.internal_set = function (p_key, p_type, p_unsafe_elem, replaceIfPresent, initial) {
                    var param_elem = null;
                    if (p_unsafe_elem != null) {
                        try {
                            switch (p_type) {
                                case greycat.Type.BOOL:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.INT:
                                    if (p_unsafe_elem instanceof Number) {
                                        param_elem = p_unsafe_elem;
                                    }
                                    else if (p_unsafe_elem instanceof Number) {
                                        var preCasting = p_unsafe_elem;
                                        param_elem = preCasting;
                                    }
                                    else if (p_unsafe_elem instanceof Number) {
                                        var preCastingLong = p_unsafe_elem;
                                        param_elem = preCastingLong;
                                    }
                                    else if (p_unsafe_elem instanceof Number) {
                                        var preCastingLong = p_unsafe_elem;
                                        param_elem = preCastingLong;
                                    }
                                    else if (p_unsafe_elem instanceof Number) {
                                        var preCastingLong = p_unsafe_elem;
                                        param_elem = preCastingLong;
                                    }
                                    else {
                                        param_elem = p_unsafe_elem;
                                    }
                                    break;
                                case greycat.Type.DOUBLE:
                                    if (p_unsafe_elem instanceof Number) {
                                        param_elem = p_unsafe_elem;
                                    }
                                    else if (p_unsafe_elem instanceof Number) {
                                        var preCasting = p_unsafe_elem;
                                        param_elem = preCasting;
                                    }
                                    else if (p_unsafe_elem instanceof Number) {
                                        var preCastingLong = p_unsafe_elem;
                                        param_elem = preCastingLong;
                                    }
                                    else if (p_unsafe_elem instanceof Number) {
                                        var preCastingLong = p_unsafe_elem;
                                        param_elem = preCastingLong;
                                    }
                                    else if (p_unsafe_elem instanceof Number) {
                                        var preCastingLong = p_unsafe_elem;
                                        param_elem = preCastingLong;
                                    }
                                    else {
                                        param_elem = p_unsafe_elem;
                                    }
                                    break;
                                case greycat.Type.LONG:
                                    if (p_unsafe_elem instanceof Number) {
                                        param_elem = p_unsafe_elem;
                                    }
                                    else if (p_unsafe_elem instanceof Number) {
                                        var preCasting = p_unsafe_elem;
                                        param_elem = preCasting;
                                    }
                                    else if (p_unsafe_elem instanceof Number) {
                                        var preCastingLong = p_unsafe_elem;
                                        param_elem = preCastingLong;
                                    }
                                    else if (p_unsafe_elem instanceof Number) {
                                        var preCastingLong = p_unsafe_elem;
                                        param_elem = preCastingLong;
                                    }
                                    else if (p_unsafe_elem instanceof Number) {
                                        var preCastingLong = p_unsafe_elem;
                                        param_elem = preCastingLong;
                                    }
                                    else {
                                        param_elem = p_unsafe_elem;
                                    }
                                    break;
                                case greycat.Type.STRING:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.DMATRIX:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.LMATRIX:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.RELATION:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.DOUBLE_ARRAY:
                                    var castedParamDouble = p_unsafe_elem;
                                    var clonedDoubleArray = new Float64Array(castedParamDouble.length);
                                    java.lang.System.arraycopy(castedParamDouble, 0, clonedDoubleArray, 0, castedParamDouble.length);
                                    param_elem = clonedDoubleArray;
                                    break;
                                case greycat.Type.LONG_ARRAY:
                                    var castedParamLong = p_unsafe_elem;
                                    var clonedLongArray = new Float64Array(castedParamLong.length);
                                    java.lang.System.arraycopy(castedParamLong, 0, clonedLongArray, 0, castedParamLong.length);
                                    param_elem = clonedLongArray;
                                    break;
                                case greycat.Type.INT_ARRAY:
                                    var castedParamInt = p_unsafe_elem;
                                    var clonedIntArray = new Int32Array(castedParamInt.length);
                                    java.lang.System.arraycopy(castedParamInt, 0, clonedIntArray, 0, castedParamInt.length);
                                    param_elem = clonedIntArray;
                                    break;
                                case greycat.Type.STRING_TO_INT_MAP:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.LONG_TO_LONG_MAP:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.LONG_TO_LONG_ARRAY_MAP:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.RELATION_INDEXED:
                                    param_elem = p_unsafe_elem;
                                    break;
                                case greycat.Type.EGRAPH:
                                    param_elem = p_unsafe_elem;
                                    break;
                                default:
                                    throw new Error("Internal Exception, unknown type");
                            }
                        }
                        catch ($ex$) {
                            if ($ex$ instanceof Error) {
                                var e = $ex$;
                                {
                                    throw new Error("mwDB usage error, set method called with type " + greycat.Type.typeName(p_type) + " while param object is " + p_unsafe_elem);
                                }
                            }
                            else {
                                throw $ex$;
                            }
                        }
                    }
                    if (this._k == null) {
                        if (param_elem == null) {
                            return;
                        }
                        this._capacity = greycat.Constants.MAP_INITIAL_CAPACITY;
                        this._k = new Int32Array(this._capacity);
                        this._v = new Array(this._capacity);
                        this._type = new Int8Array(this._capacity);
                        this._k[0] = p_key;
                        this._v[0] = param_elem;
                        this._type[0] = p_type;
                        this._size = 1;
                        if (!initial) {
                            this.declareDirty();
                        }
                        return;
                    }
                    var entry = -1;
                    var p_entry = -1;
                    var hashIndex = -1;
                    if (this.next_and_hash == null) {
                        for (var i = 0; i < this._size; i++) {
                            if (this._k[i] == p_key) {
                                entry = i;
                                break;
                            }
                        }
                    }
                    else {
                        hashIndex = p_key % (this._capacity * 2);
                        if (hashIndex < 0) {
                            hashIndex = hashIndex * -1;
                        }
                        var m = this.next_and_hash[this._capacity + hashIndex];
                        while (m != -1) {
                            if (this._k[m] == p_key) {
                                entry = m;
                                break;
                            }
                            p_entry = m;
                            m = this.next_and_hash[m];
                        }
                    }
                    if (entry != -1) {
                        if (replaceIfPresent || (p_type != this._type[entry])) {
                            if (param_elem == null) {
                                if (this.next_and_hash != null) {
                                    if (p_entry != -1) {
                                        this.next_and_hash[p_entry] = this.next_and_hash[entry];
                                    }
                                    else {
                                        this.next_and_hash[this._capacity + hashIndex] = -1;
                                    }
                                }
                                var indexVictim = this._size - 1;
                                if (entry == indexVictim) {
                                    this._k[entry] = -1;
                                    this._v[entry] = null;
                                    this._type[entry] = -1;
                                }
                                else {
                                    this._k[entry] = this._k[indexVictim];
                                    this._v[entry] = this._v[indexVictim];
                                    this._type[entry] = this._type[indexVictim];
                                    if (this.next_and_hash != null) {
                                        this.next_and_hash[entry] = this.next_and_hash[indexVictim];
                                        var victimHash = this._k[entry] % (this._capacity * 2);
                                        if (victimHash < 0) {
                                            victimHash = victimHash * -1;
                                        }
                                        var m = this.next_and_hash[this._capacity + victimHash];
                                        if (m == indexVictim) {
                                            this.next_and_hash[this._capacity + victimHash] = entry;
                                        }
                                        else {
                                            while (m != -1) {
                                                if (this.next_and_hash[m] == indexVictim) {
                                                    this.next_and_hash[m] = entry;
                                                    break;
                                                }
                                                m = this.next_and_hash[m];
                                            }
                                        }
                                    }
                                    this._k[indexVictim] = -1;
                                    this._v[indexVictim] = null;
                                    this._type[indexVictim] = -1;
                                }
                                this._size--;
                            }
                            else {
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
                        if (this.next_and_hash != null) {
                            this.next_and_hash[this._size] = this.next_and_hash[this._capacity + hashIndex];
                            this.next_and_hash[this._capacity + hashIndex] = this._size;
                        }
                        this._size++;
                        this.declareDirty();
                        return;
                    }
                    var newCapacity = this._capacity * 2;
                    var ex_k = new Int32Array(newCapacity);
                    java.lang.System.arraycopy(this._k, 0, ex_k, 0, this._capacity);
                    this._k = ex_k;
                    var ex_v = new Array(newCapacity);
                    java.lang.System.arraycopy(this._v, 0, ex_v, 0, this._capacity);
                    this._v = ex_v;
                    var ex_type = new Int8Array(newCapacity);
                    java.lang.System.arraycopy(this._type, 0, ex_type, 0, this._capacity);
                    this._type = ex_type;
                    this._capacity = newCapacity;
                    this._k[this._size] = p_key;
                    this._v[this._size] = param_elem;
                    this._type[this._size] = p_type;
                    this._size++;
                    this.next_and_hash = new Int32Array(this._capacity * 3);
                    java.util.Arrays.fill(this.next_and_hash, 0, this._capacity * 3, -1);
                    var double_capacity = this._capacity * 2;
                    for (var i = 0; i < this._size; i++) {
                        var keyHash = this._k[i] % double_capacity;
                        if (keyHash < 0) {
                            keyHash = keyHash * -1;
                        }
                        this.next_and_hash[i] = this.next_and_hash[this._capacity + keyHash];
                        this.next_and_hash[this._capacity + keyHash] = i;
                    }
                    if (!initial) {
                        this.declareDirty();
                    }
                };
                HeapStateChunk.prototype.allocate = function (newCapacity) {
                    if (newCapacity <= this._capacity) {
                        return;
                    }
                    var ex_k = new Int32Array(newCapacity);
                    if (this._k != null) {
                        java.lang.System.arraycopy(this._k, 0, ex_k, 0, this._capacity);
                    }
                    this._k = ex_k;
                    var ex_v = new Array(newCapacity);
                    if (this._v != null) {
                        java.lang.System.arraycopy(this._v, 0, ex_v, 0, this._capacity);
                    }
                    this._v = ex_v;
                    var ex_type = new Int8Array(newCapacity);
                    if (this._type != null) {
                        java.lang.System.arraycopy(this._type, 0, ex_type, 0, this._capacity);
                    }
                    this._type = ex_type;
                    this._capacity = newCapacity;
                    this.next_and_hash = new Int32Array(this._capacity * 3);
                    java.util.Arrays.fill(this.next_and_hash, 0, this._capacity * 3, -1);
                    for (var i = 0; i < this._size; i++) {
                        var keyHash = this._k[i] % (this._capacity * 2);
                        if (keyHash < 0) {
                            keyHash = keyHash * -1;
                        }
                        this.next_and_hash[i] = this.next_and_hash[this._capacity + keyHash];
                        this.next_and_hash[this._capacity + keyHash] = i;
                    }
                };
                HeapStateChunk.prototype.load = function (buffer) {
                    if (buffer != null && buffer.length() > 0) {
                        var initial = this._k == null;
                        var payloadSize = buffer.length();
                        var previous = 0;
                        var cursor = 0;
                        var state = HeapStateChunk.LOAD_WAITING_ALLOC;
                        var read_type = -1;
                        var read_key = -1;
                        while (cursor < payloadSize) {
                            var current = buffer.read(cursor);
                            if (current == greycat.Constants.CHUNK_SEP) {
                                switch (state) {
                                    case HeapStateChunk.LOAD_WAITING_ALLOC:
                                        this.allocate(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                                        state = HeapStateChunk.LOAD_WAITING_TYPE;
                                        cursor++;
                                        previous = cursor;
                                        break;
                                    case HeapStateChunk.LOAD_WAITING_TYPE:
                                        read_type = greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor);
                                        state = HeapStateChunk.LOAD_WAITING_KEY;
                                        cursor++;
                                        previous = cursor;
                                        break;
                                    case HeapStateChunk.LOAD_WAITING_KEY:
                                        read_key = greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor);
                                        switch (read_type) {
                                            case greycat.Type.BOOL:
                                            case greycat.Type.INT:
                                            case greycat.Type.DOUBLE:
                                            case greycat.Type.LONG:
                                            case greycat.Type.STRING:
                                                state = HeapStateChunk.LOAD_WAITING_VALUE;
                                                cursor++;
                                                previous = cursor;
                                                break;
                                            case greycat.Type.DOUBLE_ARRAY:
                                                var doubleArrayLoaded = null;
                                                var doubleArrayIndex = 0;
                                                cursor++;
                                                previous = cursor;
                                                current = buffer.read(cursor);
                                                while (cursor < payloadSize && current != greycat.Constants.CHUNK_SEP) {
                                                    if (current == greycat.Constants.CHUNK_VAL_SEP) {
                                                        if (doubleArrayLoaded == null) {
                                                            doubleArrayLoaded = new Float64Array(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                                        }
                                                        else {
                                                            doubleArrayLoaded[doubleArrayIndex] = greycat.utility.Base64.decodeToDoubleWithBounds(buffer, previous, cursor);
                                                            doubleArrayIndex++;
                                                        }
                                                        previous = cursor + 1;
                                                    }
                                                    cursor++;
                                                    if (cursor < payloadSize) {
                                                        current = buffer.read(cursor);
                                                    }
                                                }
                                                if (doubleArrayLoaded == null) {
                                                    doubleArrayLoaded = new Float64Array(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                                }
                                                else {
                                                    doubleArrayLoaded[doubleArrayIndex] = greycat.utility.Base64.decodeToDoubleWithBounds(buffer, previous, cursor);
                                                }
                                                this.internal_set(read_key, read_type, doubleArrayLoaded, true, initial);
                                                state = HeapStateChunk.LOAD_WAITING_TYPE;
                                                cursor++;
                                                previous = cursor;
                                                break;
                                            case greycat.Type.LONG_ARRAY:
                                                var longArrayLoaded = null;
                                                var longArrayIndex = 0;
                                                cursor++;
                                                previous = cursor;
                                                current = buffer.read(cursor);
                                                while (cursor < payloadSize && current != greycat.Constants.CHUNK_SEP) {
                                                    if (current == greycat.Constants.CHUNK_VAL_SEP) {
                                                        if (longArrayLoaded == null) {
                                                            longArrayLoaded = new Float64Array(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                                        }
                                                        else {
                                                            longArrayLoaded[longArrayIndex] = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                                                            longArrayIndex++;
                                                        }
                                                        previous = cursor + 1;
                                                    }
                                                    cursor++;
                                                    if (cursor < payloadSize) {
                                                        current = buffer.read(cursor);
                                                    }
                                                }
                                                if (longArrayLoaded == null) {
                                                    longArrayLoaded = new Float64Array(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                                }
                                                else {
                                                    longArrayLoaded[longArrayIndex] = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                                                }
                                                this.internal_set(read_key, read_type, longArrayLoaded, true, initial);
                                                state = HeapStateChunk.LOAD_WAITING_TYPE;
                                                cursor++;
                                                previous = cursor;
                                                break;
                                            case greycat.Type.INT_ARRAY:
                                                var intArrayLoaded = null;
                                                var intArrayIndex = 0;
                                                cursor++;
                                                previous = cursor;
                                                current = buffer.read(cursor);
                                                while (cursor < payloadSize && current != greycat.Constants.CHUNK_SEP) {
                                                    if (current == greycat.Constants.CHUNK_VAL_SEP) {
                                                        if (intArrayLoaded == null) {
                                                            intArrayLoaded = new Int32Array(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                                        }
                                                        else {
                                                            intArrayLoaded[intArrayIndex] = greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor);
                                                            intArrayIndex++;
                                                        }
                                                        previous = cursor + 1;
                                                    }
                                                    cursor++;
                                                    if (cursor < payloadSize) {
                                                        current = buffer.read(cursor);
                                                    }
                                                }
                                                if (intArrayLoaded == null) {
                                                    intArrayLoaded = new Int32Array(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor));
                                                }
                                                else {
                                                    intArrayLoaded[intArrayIndex] = greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor);
                                                }
                                                this.internal_set(read_key, read_type, intArrayLoaded, true, initial);
                                                state = HeapStateChunk.LOAD_WAITING_TYPE;
                                                cursor++;
                                                previous = cursor;
                                                break;
                                            case greycat.Type.RELATION:
                                                var relation = new greycat.internal.heap.HeapRelation(this, null);
                                                cursor++;
                                                cursor = relation.load(buffer, cursor, payloadSize);
                                                this.internal_set(read_key, read_type, relation, true, initial);
                                                if (cursor < payloadSize) {
                                                    current = buffer.read(cursor);
                                                    if (current == greycat.Constants.CHUNK_SEP && cursor < payloadSize) {
                                                        state = HeapStateChunk.LOAD_WAITING_TYPE;
                                                        cursor++;
                                                        previous = cursor;
                                                    }
                                                }
                                                break;
                                            case greycat.Type.DMATRIX:
                                                var matrix = new greycat.internal.heap.HeapDMatrix(this, null);
                                                cursor++;
                                                cursor = matrix.load(buffer, cursor, payloadSize);
                                                this.internal_set(read_key, read_type, matrix, true, initial);
                                                if (cursor < payloadSize) {
                                                    current = buffer.read(cursor);
                                                    if (current == greycat.Constants.CHUNK_SEP && cursor < payloadSize) {
                                                        state = HeapStateChunk.LOAD_WAITING_TYPE;
                                                        cursor++;
                                                        previous = cursor;
                                                    }
                                                }
                                                break;
                                            case greycat.Type.LMATRIX:
                                                var lmatrix = new greycat.internal.heap.HeapLMatrix(this, null);
                                                cursor++;
                                                cursor = lmatrix.load(buffer, cursor, payloadSize);
                                                this.internal_set(read_key, read_type, lmatrix, true, initial);
                                                if (cursor < payloadSize) {
                                                    current = buffer.read(cursor);
                                                    if (current == greycat.Constants.CHUNK_SEP && cursor < payloadSize) {
                                                        state = HeapStateChunk.LOAD_WAITING_TYPE;
                                                        cursor++;
                                                        previous = cursor;
                                                    }
                                                }
                                                break;
                                            case greycat.Type.LONG_TO_LONG_MAP:
                                                var l2lmap = new greycat.internal.heap.HeapLongLongMap(this);
                                                cursor++;
                                                cursor = l2lmap.load(buffer, cursor, payloadSize);
                                                this.internal_set(read_key, read_type, l2lmap, true, initial);
                                                if (cursor < payloadSize) {
                                                    current = buffer.read(cursor);
                                                    if (current == greycat.Constants.CHUNK_SEP && cursor < payloadSize) {
                                                        state = HeapStateChunk.LOAD_WAITING_TYPE;
                                                        cursor++;
                                                        previous = cursor;
                                                    }
                                                }
                                                break;
                                            case greycat.Type.LONG_TO_LONG_ARRAY_MAP:
                                                var l2lrmap = new greycat.internal.heap.HeapLongLongArrayMap(this);
                                                cursor++;
                                                cursor = l2lrmap.load(buffer, cursor, payloadSize);
                                                this.internal_set(read_key, read_type, l2lrmap, true, initial);
                                                if (cursor < payloadSize) {
                                                    current = buffer.read(cursor);
                                                    if (current == greycat.Constants.CHUNK_SEP && cursor < payloadSize) {
                                                        state = HeapStateChunk.LOAD_WAITING_TYPE;
                                                        cursor++;
                                                        previous = cursor;
                                                    }
                                                }
                                                break;
                                            case greycat.Type.RELATION_INDEXED:
                                                var relationIndexed = new greycat.internal.heap.HeapRelationIndexed(this, this._space.graph());
                                                cursor++;
                                                cursor = relationIndexed.load(buffer, cursor, payloadSize);
                                                this.internal_set(read_key, read_type, relationIndexed, true, initial);
                                                if (cursor < payloadSize) {
                                                    current = buffer.read(cursor);
                                                    if (current == greycat.Constants.CHUNK_SEP && cursor < payloadSize) {
                                                        state = HeapStateChunk.LOAD_WAITING_TYPE;
                                                        cursor++;
                                                        previous = cursor;
                                                    }
                                                }
                                                break;
                                            case greycat.Type.STRING_TO_INT_MAP:
                                                var s2lmap = new greycat.internal.heap.HeapStringIntMap(this);
                                                cursor++;
                                                cursor = s2lmap.load(buffer, cursor, payloadSize);
                                                this.internal_set(read_key, read_type, s2lmap, true, initial);
                                                if (cursor < payloadSize) {
                                                    current = buffer.read(cursor);
                                                    if (current == greycat.Constants.CHUNK_SEP && cursor < payloadSize) {
                                                        state = HeapStateChunk.LOAD_WAITING_TYPE;
                                                        cursor++;
                                                        previous = cursor;
                                                    }
                                                }
                                                break;
                                            case greycat.Type.EGRAPH:
                                                var eGraph = new greycat.internal.heap.HeapEGraph(this, null, this.graph());
                                                cursor++;
                                                cursor = eGraph.load(buffer, cursor, payloadSize);
                                                this.internal_set(read_key, read_type, eGraph, true, initial);
                                                if (cursor < payloadSize) {
                                                    current = buffer.read(cursor);
                                                    if (current == greycat.Constants.CHUNK_SEP && cursor < payloadSize) {
                                                        state = HeapStateChunk.LOAD_WAITING_TYPE;
                                                        cursor++;
                                                        previous = cursor;
                                                    }
                                                }
                                                break;
                                            default:
                                                throw new Error("Not implemented yet!!!");
                                        }
                                        break;
                                    case HeapStateChunk.LOAD_WAITING_VALUE:
                                        this.load_primitive(read_key, read_type, buffer, previous, cursor, initial);
                                        state = HeapStateChunk.LOAD_WAITING_TYPE;
                                        cursor++;
                                        previous = cursor;
                                        break;
                                }
                            }
                            else {
                                cursor++;
                            }
                        }
                        if (state == HeapStateChunk.LOAD_WAITING_VALUE) {
                            this.load_primitive(read_key, read_type, buffer, previous, cursor, initial);
                        }
                    }
                };
                HeapStateChunk.prototype.load_primitive = function (read_key, read_type, buffer, previous, cursor, initial) {
                    switch (read_type) {
                        case greycat.Type.BOOL:
                            this.internal_set(read_key, read_type, (greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor) == greycat.internal.CoreConstants.BOOL_TRUE), true, initial);
                            break;
                        case greycat.Type.INT:
                            this.internal_set(read_key, read_type, greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor), true, initial);
                            break;
                        case greycat.Type.DOUBLE:
                            this.internal_set(read_key, read_type, greycat.utility.Base64.decodeToDoubleWithBounds(buffer, previous, cursor), true, initial);
                            break;
                        case greycat.Type.LONG:
                            this.internal_set(read_key, read_type, greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor), true, initial);
                            break;
                        case greycat.Type.STRING:
                            this.internal_set(read_key, read_type, greycat.utility.Base64.decodeToStringWithBounds(buffer, previous, cursor), true, initial);
                            break;
                    }
                };
                HeapStateChunk.prototype.loadDiff = function (buffer) { };
                return HeapStateChunk;
            }());
            HeapStateChunk.LOAD_WAITING_ALLOC = 0;
            HeapStateChunk.LOAD_WAITING_TYPE = 1;
            HeapStateChunk.LOAD_WAITING_KEY = 2;
            HeapStateChunk.LOAD_WAITING_VALUE = 3;
            heap.HeapStateChunk = HeapStateChunk;
            var HeapStringIntMap = (function () {
                function HeapStringIntMap(p_parent) {
                    this.mapSize = 0;
                    this.capacity = 0;
                    this.keys = null;
                    this.keysH = null;
                    this.values = null;
                    this.nexts = null;
                    this.hashs = null;
                    this.parent = p_parent;
                }
                HeapStringIntMap.prototype.key = function (i) {
                    return this.keys[i];
                };
                HeapStringIntMap.prototype.setKey = function (i, newValue) {
                    this.keys[i] = newValue;
                };
                HeapStringIntMap.prototype.keyH = function (i) {
                    return this.keysH[i];
                };
                HeapStringIntMap.prototype.setKeyH = function (i, newValue) {
                    this.keysH[i] = newValue;
                };
                HeapStringIntMap.prototype.value = function (i) {
                    return this.values[i];
                };
                HeapStringIntMap.prototype.setValue = function (i, newValue) {
                    this.values[i] = newValue;
                };
                HeapStringIntMap.prototype.next = function (i) {
                    return this.nexts[i];
                };
                HeapStringIntMap.prototype.setNext = function (i, newValue) {
                    this.nexts[i] = newValue;
                };
                HeapStringIntMap.prototype.hash = function (i) {
                    return this.hashs[i];
                };
                HeapStringIntMap.prototype.setHash = function (i, newValue) {
                    this.hashs[i] = newValue;
                };
                HeapStringIntMap.prototype.reallocate = function (newCapacity) {
                    if (newCapacity > this.capacity) {
                        var new_keys = new Array(newCapacity);
                        if (this.keys != null) {
                            java.lang.System.arraycopy(this.keys, 0, new_keys, 0, this.capacity);
                        }
                        this.keys = new_keys;
                        var new_keysH = new Int32Array(newCapacity);
                        if (this.keysH != null) {
                            java.lang.System.arraycopy(this.keysH, 0, new_keysH, 0, this.capacity);
                        }
                        this.keysH = new_keysH;
                        var new_values = new Int32Array(newCapacity);
                        if (this.values != null) {
                            java.lang.System.arraycopy(this.values, 0, new_values, 0, this.capacity);
                        }
                        this.values = new_values;
                        var new_nexts = new Int32Array(newCapacity);
                        var new_hashes = new Int32Array(newCapacity * 2);
                        java.util.Arrays.fill(new_nexts, 0, newCapacity, -1);
                        java.util.Arrays.fill(new_hashes, 0, newCapacity * 2, -1);
                        this.hashs = new_hashes;
                        this.nexts = new_nexts;
                        var double_capacity = this.capacity * 2;
                        for (var i = 0; i < this.mapSize; i++) {
                            var new_key_hash = this.keyH(i) % double_capacity;
                            if (new_key_hash < 0) {
                                new_key_hash = new_key_hash * -1;
                            }
                            this.setNext(i, this.hash(new_key_hash));
                            this.setHash(new_key_hash, i);
                        }
                        this.capacity = newCapacity;
                    }
                };
                HeapStringIntMap.prototype.cloneFor = function (newContainer) {
                    var cloned = new greycat.internal.heap.HeapStringIntMap(newContainer);
                    cloned.mapSize = this.mapSize;
                    cloned.capacity = this.capacity;
                    if (this.keys != null) {
                        var cloned_keys = new Array(this.capacity);
                        java.lang.System.arraycopy(this.keys, 0, cloned_keys, 0, this.capacity);
                        cloned.keys = cloned_keys;
                    }
                    if (this.keysH != null) {
                        var cloned_keysH = new Int32Array(this.capacity);
                        java.lang.System.arraycopy(this.keysH, 0, cloned_keysH, 0, this.capacity);
                        cloned.keysH = cloned_keysH;
                    }
                    if (this.values != null) {
                        var cloned_values = new Int32Array(this.capacity);
                        java.lang.System.arraycopy(this.values, 0, cloned_values, 0, this.capacity);
                        cloned.values = cloned_values;
                    }
                    if (this.nexts != null) {
                        var cloned_nexts = new Int32Array(this.capacity);
                        java.lang.System.arraycopy(this.nexts, 0, cloned_nexts, 0, this.capacity);
                        cloned.nexts = cloned_nexts;
                    }
                    if (this.hashs != null) {
                        var cloned_hashs = new Int32Array(this.capacity * 2);
                        java.lang.System.arraycopy(this.hashs, 0, cloned_hashs, 0, this.capacity * 2);
                        cloned.hashs = cloned_hashs;
                    }
                    return cloned;
                };
                HeapStringIntMap.prototype.getValue = function (requestString) {
                    var result = -1;
                    {
                        if (this.keys != null) {
                            var keyHash = greycat.utility.HashHelper.hash(requestString);
                            var hashIndex = keyHash % (this.capacity * 2);
                            if (hashIndex < 0) {
                                hashIndex = hashIndex * -1;
                            }
                            var m = this.hash(hashIndex);
                            while (m >= 0) {
                                if (keyHash == this.keyH(m)) {
                                    result = this.value(m);
                                    break;
                                }
                                m = this.next(m);
                            }
                        }
                    }
                    return result;
                };
                HeapStringIntMap.prototype.getByHash = function (keyHash) {
                    var result = null;
                    {
                        if (this.keys != null) {
                            var hashIndex = keyHash % (this.capacity * 2);
                            if (hashIndex < 0) {
                                hashIndex = hashIndex * -1;
                            }
                            var m = this.hash(hashIndex);
                            while (m >= 0) {
                                if (keyHash == this.keyH(m)) {
                                    result = this.key(m);
                                    break;
                                }
                                m = this.next(m);
                            }
                        }
                    }
                    return result;
                };
                HeapStringIntMap.prototype.containsHash = function (keyHash) {
                    var result = false;
                    {
                        if (this.keys != null) {
                            var hashIndex = keyHash % (this.capacity * 2);
                            if (hashIndex < 0) {
                                hashIndex = hashIndex * -1;
                            }
                            var m = this.hash(hashIndex);
                            while (m >= 0) {
                                if (keyHash == this.keyH(m)) {
                                    result = true;
                                    break;
                                }
                                m = this.next(m);
                            }
                        }
                    }
                    return result;
                };
                HeapStringIntMap.prototype.each = function (callback) {
                    {
                        this.unsafe_each(callback);
                    }
                };
                HeapStringIntMap.prototype.unsafe_each = function (callback) {
                    for (var i = 0; i < this.mapSize; i++) {
                        callback(this.key(i), this.value(i));
                    }
                };
                HeapStringIntMap.prototype.size = function () {
                    var result;
                    {
                        result = this.mapSize;
                    }
                    return result;
                };
                HeapStringIntMap.prototype.remove = function (requestKey) {
                    {
                        if (this.keys != null && this.mapSize != 0) {
                            var keyHash = greycat.utility.HashHelper.hash(requestKey);
                            var hashCapacity = this.capacity * 2;
                            var hashIndex = keyHash % hashCapacity;
                            if (hashIndex < 0) {
                                hashIndex = hashIndex * -1;
                            }
                            var m = this.hash(hashIndex);
                            var found = -1;
                            while (m >= 0) {
                                if (keyHash == this.keyH(m)) {
                                    found = m;
                                    break;
                                }
                                m = this.next(m);
                            }
                            if (found != -1) {
                                var toRemoveHash = keyHash % hashCapacity;
                                if (toRemoveHash < 0) {
                                    toRemoveHash = toRemoveHash * -1;
                                }
                                m = this.hash(toRemoveHash);
                                if (m == found) {
                                    this.setHash(toRemoveHash, this.next(m));
                                }
                                else {
                                    while (m != -1) {
                                        var next_of_m = this.next(m);
                                        if (next_of_m == found) {
                                            this.setNext(m, this.next(next_of_m));
                                            break;
                                        }
                                        m = next_of_m;
                                    }
                                }
                                var lastIndex = this.mapSize - 1;
                                if (lastIndex == found) {
                                    this.mapSize--;
                                }
                                else {
                                    var lastKey = this.key(lastIndex);
                                    var lastKeyH = this.keyH(lastIndex);
                                    this.setKey(found, lastKey);
                                    this.setKeyH(found, lastKeyH);
                                    this.setValue(found, this.value(lastIndex));
                                    this.setNext(found, this.next(lastIndex));
                                    var victimHash = lastKeyH % hashCapacity;
                                    if (victimHash < 0) {
                                        victimHash = victimHash * -1;
                                    }
                                    m = this.hash(victimHash);
                                    if (m == lastIndex) {
                                        this.setHash(victimHash, found);
                                    }
                                    else {
                                        while (m != -1) {
                                            var next_of_m = this.next(m);
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
                    }
                };
                HeapStringIntMap.prototype.put = function (insertKey, insertValue) {
                    {
                        var keyHash = greycat.utility.HashHelper.hash(insertKey);
                        if (this.keys == null) {
                            this.reallocate(greycat.Constants.MAP_INITIAL_CAPACITY);
                            this.setKey(0, insertKey);
                            this.setKeyH(0, keyHash);
                            this.setValue(0, insertValue);
                            var hashIndex = keyHash % (this.capacity * 2);
                            if (hashIndex < 0) {
                                hashIndex = hashIndex * -1;
                            }
                            this.setHash(hashIndex, 0);
                            this.setNext(0, -1);
                            this.mapSize++;
                        }
                        else {
                            var hashCapacity = this.capacity * 2;
                            var insertKeyHash = keyHash % hashCapacity;
                            if (insertKeyHash < 0) {
                                insertKeyHash = insertKeyHash * -1;
                            }
                            var currentHash = this.hash(insertKeyHash);
                            var m = currentHash;
                            var found = -1;
                            while (m >= 0) {
                                if (keyHash == this.keyH(m)) {
                                    if (!(insertKey === this.key(m))) {
                                        throw new Error("Lotteries Winner !!! hashing conflict between " + this.key(m) + " and " + insertKey);
                                    }
                                    found = m;
                                    break;
                                }
                                m = this.next(m);
                            }
                            if (found == -1) {
                                var lastIndex = this.mapSize;
                                if (lastIndex == this.capacity) {
                                    this.reallocate(this.capacity * 2);
                                }
                                this.setKey(lastIndex, insertKey);
                                this.setKeyH(lastIndex, keyHash);
                                this.setValue(lastIndex, insertValue);
                                var hashIndex = keyHash % (this.capacity * 2);
                                if (hashIndex < 0) {
                                    hashIndex = hashIndex * -1;
                                }
                                this.setHash(hashIndex, lastIndex);
                                this.setNext(lastIndex, currentHash);
                                this.mapSize++;
                                this.parent.declareDirty();
                            }
                            else {
                                if (this.value(found) != insertValue) {
                                    this.setValue(found, insertValue);
                                    this.parent.declareDirty();
                                }
                            }
                        }
                    }
                };
                HeapStringIntMap.prototype.load = function (buffer, offset, max) {
                    var cursor = offset;
                    var current = buffer.read(cursor);
                    var isFirst = true;
                    var previous = offset;
                    var previousKey = null;
                    while (cursor < max && current != greycat.Constants.CHUNK_SEP && current != greycat.Constants.CHUNK_ENODE_SEP && current != greycat.Constants.CHUNK_ESEP) {
                        if (current == greycat.Constants.CHUNK_VAL_SEP) {
                            if (isFirst) {
                                this.reallocate(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                                isFirst = false;
                            }
                            else {
                                if (previousKey == null) {
                                    previousKey = greycat.utility.Base64.decodeToStringWithBounds(buffer, previous, cursor);
                                }
                                else {
                                    this.put(previousKey, greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                                    previousKey = null;
                                }
                            }
                            previous = cursor + 1;
                        }
                        cursor++;
                        if (cursor < max) {
                            current = buffer.read(cursor);
                        }
                    }
                    if (isFirst) {
                        this.reallocate(greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                    }
                    else {
                        if (previousKey != null) {
                            this.put(previousKey, greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor));
                        }
                    }
                    return cursor;
                };
                return HeapStringIntMap;
            }());
            heap.HeapStringIntMap = HeapStringIntMap;
            var HeapTimeTreeChunk = (function () {
                function HeapTimeTreeChunk(p_space, p_index) {
                    this._root = -1;
                    this._size = 0;
                    this._space = p_space;
                    this._index = p_index;
                    this._magic = 0;
                    this._dirty = false;
                    this._extra = 0;
                    this._extra2 = 0;
                }
                HeapTimeTreeChunk.prototype.extra = function () {
                    return this._extra;
                };
                HeapTimeTreeChunk.prototype.setExtra = function (extraValue) {
                    this._extra = extraValue;
                };
                HeapTimeTreeChunk.prototype.extra2 = function () {
                    return this._extra2;
                };
                HeapTimeTreeChunk.prototype.setExtra2 = function (extraValue) {
                    this._extra2 = extraValue;
                };
                HeapTimeTreeChunk.prototype.world = function () {
                    return this._space.worldByIndex(this._index);
                };
                HeapTimeTreeChunk.prototype.time = function () {
                    return this._space.timeByIndex(this._index);
                };
                HeapTimeTreeChunk.prototype.id = function () {
                    return this._space.idByIndex(this._index);
                };
                HeapTimeTreeChunk.prototype.size = function () {
                    return this._size;
                };
                HeapTimeTreeChunk.prototype.range = function (startKey, endKey, maxElements, walker) {
                    var nbElements = 0;
                    var indexEnd = this.internal_previousOrEqual_index(endKey);
                    while (indexEnd != -1 && this.key(indexEnd) >= startKey && nbElements < maxElements) {
                        walker(this.key(indexEnd));
                        nbElements++;
                        indexEnd = this.internal_previous(indexEnd);
                    }
                };
                HeapTimeTreeChunk.prototype.save = function (buffer) {
                    if (this._extra != greycat.internal.CoreConstants.NULL_LONG && this._extra != 0) {
                        greycat.utility.Base64.encodeLongToBuffer(this._extra, buffer);
                        buffer.write(greycat.internal.CoreConstants.CHUNK_SEP);
                    }
                    if (this._extra2 != greycat.internal.CoreConstants.NULL_LONG && this._extra2 != 0) {
                        greycat.utility.Base64.encodeLongToBuffer(this._extra2, buffer);
                        buffer.write(greycat.internal.CoreConstants.CHUNK_SEP);
                    }
                    greycat.utility.Base64.encodeIntToBuffer(this._size, buffer);
                    for (var i = 0; i < this._size; i++) {
                        buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                        greycat.utility.Base64.encodeLongToBuffer(this._k[i], buffer);
                    }
                    this._dirty = false;
                    if (this._diff != null) {
                        greycat.internal.CoreConstants.fillBooleanArray(this._diff, false);
                    }
                };
                HeapTimeTreeChunk.prototype.saveDiff = function (buffer) {
                    if (this._dirty) {
                        if (this._extra != greycat.internal.CoreConstants.NULL_LONG && this._extra != 0) {
                            greycat.utility.Base64.encodeLongToBuffer(this._extra, buffer);
                            buffer.write(greycat.internal.CoreConstants.CHUNK_SEP);
                        }
                        if (this._extra2 != greycat.internal.CoreConstants.NULL_LONG && this._extra2 != 0) {
                            greycat.utility.Base64.encodeLongToBuffer(this._extra2, buffer);
                            buffer.write(greycat.internal.CoreConstants.CHUNK_SEP);
                        }
                        greycat.utility.Base64.encodeIntToBuffer(this._size, buffer);
                        for (var i = 0; i < this._size; i++) {
                            if (this._diff[i]) {
                                buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                greycat.utility.Base64.encodeLongToBuffer(this._k[i], buffer);
                            }
                        }
                        this._dirty = false;
                        greycat.internal.CoreConstants.fillBooleanArray(this._diff, false);
                    }
                };
                HeapTimeTreeChunk.prototype.load = function (buffer) {
                    this.internal_load(buffer, true);
                };
                HeapTimeTreeChunk.prototype.loadDiff = function (buffer) {
                    if (this.internal_load(buffer, false) && !this._dirty) {
                        this._dirty = true;
                        if (this._space != null) {
                            this._space.notifyUpdate(this._index);
                        }
                    }
                };
                HeapTimeTreeChunk.prototype.internal_load = function (buffer, initial) {
                    if (buffer == null || buffer.length() == 0) {
                        return false;
                    }
                    var isDirty = false;
                    var cursor = 0;
                    var previous = 0;
                    var payloadSize = buffer.length();
                    var isFirst = true;
                    var isFirstExtra = true;
                    while (cursor < payloadSize) {
                        var current = buffer.read(cursor);
                        switch (current) {
                            case greycat.Constants.CHUNK_SEP:
                                if (isFirstExtra) {
                                    this._extra = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                                    previous = cursor + 1;
                                    isFirstExtra = false;
                                }
                                else {
                                    this._extra2 = greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor);
                                    previous = cursor + 1;
                                }
                                break;
                            case greycat.Constants.CHUNK_VAL_SEP:
                                if (isFirst) {
                                    var treeSize = greycat.utility.Base64.decodeToIntWithBounds(buffer, previous, cursor);
                                    var closePowerOfTwo = Math.pow(2, Math.ceil(Math.log(treeSize) / Math.log(2)));
                                    this.reallocate(closePowerOfTwo);
                                    previous = cursor + 1;
                                    isFirst = false;
                                }
                                else {
                                    var insertResult_1 = this.internal_insert(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor), initial);
                                    isDirty = isDirty || insertResult_1;
                                    previous = cursor + 1;
                                }
                                break;
                        }
                        cursor++;
                    }
                    var insertResult = this.internal_insert(greycat.utility.Base64.decodeToLongWithBounds(buffer, previous, cursor), initial);
                    isDirty = isDirty || insertResult;
                    return isDirty;
                };
                HeapTimeTreeChunk.prototype.index = function () {
                    return this._index;
                };
                HeapTimeTreeChunk.prototype.previous = function (key) {
                    var resultKey;
                    var result = this.internal_previous_index(key);
                    if (result != -1) {
                        resultKey = this.key(result);
                    }
                    else {
                        resultKey = greycat.internal.CoreConstants.NULL_LONG;
                    }
                    return resultKey;
                };
                HeapTimeTreeChunk.prototype.next = function (key) {
                    var resultKey;
                    var result = this.internal_previousOrEqual_index(key);
                    if (result != -1) {
                        result = this.internal_next(result);
                    }
                    if (result != -1) {
                        resultKey = this.key(result);
                    }
                    else {
                        resultKey = greycat.internal.CoreConstants.NULL_LONG;
                    }
                    return resultKey;
                };
                HeapTimeTreeChunk.prototype.previousOrEqual = function (key) {
                    var resultKey;
                    var result = this.internal_previousOrEqual_index(key);
                    if (result != -1) {
                        resultKey = this.key(result);
                    }
                    else {
                        resultKey = greycat.internal.CoreConstants.NULL_LONG;
                    }
                    return resultKey;
                };
                HeapTimeTreeChunk.prototype.magic = function () {
                    return this._magic;
                };
                HeapTimeTreeChunk.prototype.insert = function (p_key) {
                    if (this.internal_insert(p_key, false)) {
                        this.internal_set_dirty();
                    }
                };
                HeapTimeTreeChunk.prototype.unsafe_insert = function (p_key) {
                    this.internal_insert(p_key, false);
                };
                HeapTimeTreeChunk.prototype.chunkType = function () {
                    return greycat.chunk.ChunkType.TIME_TREE_CHUNK;
                };
                HeapTimeTreeChunk.prototype.clearAt = function (max) {
                    var previousValue = this._k;
                    this._k = new Float64Array(this._k.length);
                    this._back_meta = new Int32Array(this._k.length * HeapTimeTreeChunk.META_SIZE);
                    this._colors = [];
                    this._diff = [];
                    greycat.internal.CoreConstants.fillBooleanArray(this._diff, false);
                    this._root = -1;
                    var _previousSize = this._size;
                    this._size = 0;
                    for (var i = 0; i < _previousSize; i++) {
                        if (previousValue[i] != greycat.internal.CoreConstants.NULL_LONG && previousValue[i] < max) {
                            this.internal_insert(previousValue[i], false);
                        }
                    }
                    this.internal_set_dirty();
                };
                HeapTimeTreeChunk.prototype.reallocate = function (newCapacity) {
                    if (this._k != null && newCapacity <= this._k.length) {
                        return;
                    }
                    var new_back_kv = new Float64Array(newCapacity);
                    if (this._k != null) {
                        java.lang.System.arraycopy(this._k, 0, new_back_kv, 0, this._size);
                    }
                    var new_back_diff = [];
                    greycat.internal.CoreConstants.fillBooleanArray(new_back_diff, false);
                    if (this._diff != null) {
                        java.lang.System.arraycopy(this._diff, 0, new_back_diff, 0, this._size);
                    }
                    var new_back_colors = [];
                    if (this._colors != null) {
                        java.lang.System.arraycopy(this._colors, 0, new_back_colors, 0, this._size);
                        for (var i = this._size; i < newCapacity; i++) {
                            new_back_colors[i] = false;
                        }
                    }
                    var new_back_meta = new Int32Array(newCapacity * HeapTimeTreeChunk.META_SIZE);
                    if (this._back_meta != null) {
                        java.lang.System.arraycopy(this._back_meta, 0, new_back_meta, 0, this._size * HeapTimeTreeChunk.META_SIZE);
                        for (var i = this._size * HeapTimeTreeChunk.META_SIZE; i < newCapacity * HeapTimeTreeChunk.META_SIZE; i++) {
                            new_back_meta[i] = -1;
                        }
                    }
                    this._back_meta = new_back_meta;
                    this._k = new_back_kv;
                    this._colors = new_back_colors;
                    this._diff = new_back_diff;
                };
                HeapTimeTreeChunk.prototype.key = function (p_currentIndex) {
                    if (p_currentIndex == -1) {
                        return -1;
                    }
                    return this._k[p_currentIndex];
                };
                HeapTimeTreeChunk.prototype.setKey = function (p_currentIndex, p_paramIndex, initial) {
                    this._k[p_currentIndex] = p_paramIndex;
                    if (!initial) {
                        this._diff[p_currentIndex] = true;
                    }
                };
                HeapTimeTreeChunk.prototype.left = function (p_currentIndex) {
                    if (p_currentIndex == -1) {
                        return -1;
                    }
                    return this._back_meta[p_currentIndex * HeapTimeTreeChunk.META_SIZE];
                };
                HeapTimeTreeChunk.prototype.setLeft = function (p_currentIndex, p_paramIndex) {
                    this._back_meta[p_currentIndex * HeapTimeTreeChunk.META_SIZE] = p_paramIndex;
                };
                HeapTimeTreeChunk.prototype.right = function (p_currentIndex) {
                    if (p_currentIndex == -1) {
                        return -1;
                    }
                    return this._back_meta[(p_currentIndex * HeapTimeTreeChunk.META_SIZE) + 1];
                };
                HeapTimeTreeChunk.prototype.setRight = function (p_currentIndex, p_paramIndex) {
                    this._back_meta[(p_currentIndex * HeapTimeTreeChunk.META_SIZE) + 1] = p_paramIndex;
                };
                HeapTimeTreeChunk.prototype.parent = function (p_currentIndex) {
                    if (p_currentIndex == -1) {
                        return -1;
                    }
                    return this._back_meta[(p_currentIndex * HeapTimeTreeChunk.META_SIZE) + 2];
                };
                HeapTimeTreeChunk.prototype.setParent = function (p_currentIndex, p_paramIndex) {
                    this._back_meta[(p_currentIndex * HeapTimeTreeChunk.META_SIZE) + 2] = p_paramIndex;
                };
                HeapTimeTreeChunk.prototype.color = function (p_currentIndex) {
                    if (p_currentIndex == -1) {
                        return true;
                    }
                    return this._colors[p_currentIndex];
                };
                HeapTimeTreeChunk.prototype.setColor = function (p_currentIndex, p_paramIndex) {
                    this._colors[p_currentIndex] = p_paramIndex;
                };
                HeapTimeTreeChunk.prototype.grandParent = function (p_currentIndex) {
                    if (p_currentIndex == -1) {
                        return -1;
                    }
                    if (this.parent(p_currentIndex) != -1) {
                        return this.parent(this.parent(p_currentIndex));
                    }
                    else {
                        return -1;
                    }
                };
                HeapTimeTreeChunk.prototype.sibling = function (p_currentIndex) {
                    if (this.parent(p_currentIndex) == -1) {
                        return -1;
                    }
                    else {
                        if (p_currentIndex == this.left(this.parent(p_currentIndex))) {
                            return this.right(this.parent(p_currentIndex));
                        }
                        else {
                            return this.left(this.parent(p_currentIndex));
                        }
                    }
                };
                HeapTimeTreeChunk.prototype.uncle = function (p_currentIndex) {
                    if (this.parent(p_currentIndex) != -1) {
                        return this.sibling(this.parent(p_currentIndex));
                    }
                    else {
                        return -1;
                    }
                };
                HeapTimeTreeChunk.prototype.internal_previous = function (p_index) {
                    var p = p_index;
                    if (this.left(p) != -1) {
                        p = this.left(p);
                        while (this.right(p) != -1) {
                            p = this.right(p);
                        }
                        return p;
                    }
                    else {
                        if (this.parent(p) != -1) {
                            if (p == this.right(this.parent(p))) {
                                return this.parent(p);
                            }
                            else {
                                while (this.parent(p) != -1 && p == this.left(this.parent(p))) {
                                    p = this.parent(p);
                                }
                                return this.parent(p);
                            }
                        }
                        else {
                            return -1;
                        }
                    }
                };
                HeapTimeTreeChunk.prototype.internal_next = function (p_index) {
                    var p = p_index;
                    if (this.right(p) != -1) {
                        p = this.right(p);
                        while (this.left(p) != -1) {
                            p = this.left(p);
                        }
                        return p;
                    }
                    else {
                        if (this.parent(p) != -1) {
                            if (p == this.left(this.parent(p))) {
                                return this.parent(p);
                            }
                            else {
                                while (this.parent(p) != -1 && p == this.right(this.parent(p))) {
                                    p = this.parent(p);
                                }
                                return this.parent(p);
                            }
                        }
                        else {
                            return -1;
                        }
                    }
                };
                HeapTimeTreeChunk.prototype.internal_previousOrEqual_index = function (p_key) {
                    var p = this._root;
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
                            }
                            else {
                                return p;
                            }
                        }
                        else {
                            if (this.left(p) != -1) {
                                p = this.left(p);
                            }
                            else {
                                var parent_1 = this.parent(p);
                                var ch = p;
                                while (parent_1 != -1 && ch == this.left(parent_1)) {
                                    ch = parent_1;
                                    parent_1 = this.parent(parent_1);
                                }
                                return parent_1;
                            }
                        }
                    }
                    return -1;
                };
                HeapTimeTreeChunk.prototype.internal_previous_index = function (p_key) {
                    var p = this._root;
                    if (p == -1) {
                        return p;
                    }
                    while (p != -1) {
                        if (p_key > this.key(p)) {
                            if (this.right(p) != -1) {
                                p = this.right(p);
                            }
                            else {
                                return p;
                            }
                        }
                        else {
                            if (this.left(p) != -1) {
                                p = this.left(p);
                            }
                            else {
                                var parent_2 = this.parent(p);
                                var ch = p;
                                while (parent_2 != -1 && ch == this.left(parent_2)) {
                                    ch = parent_2;
                                    parent_2 = this.parent(parent_2);
                                }
                                return parent_2;
                            }
                        }
                    }
                    return -1;
                };
                HeapTimeTreeChunk.prototype.rotateLeft = function (n) {
                    var r = this.right(n);
                    this.replaceNode(n, r);
                    this.setRight(n, this.left(r));
                    if (this.left(r) != -1) {
                        this.setParent(this.left(r), n);
                    }
                    this.setLeft(r, n);
                    this.setParent(n, r);
                };
                HeapTimeTreeChunk.prototype.rotateRight = function (n) {
                    var l = this.left(n);
                    this.replaceNode(n, l);
                    this.setLeft(n, this.right(l));
                    if (this.right(l) != -1) {
                        this.setParent(this.right(l), n);
                    }
                    this.setRight(l, n);
                    this.setParent(n, l);
                };
                HeapTimeTreeChunk.prototype.replaceNode = function (oldn, newn) {
                    if (this.parent(oldn) == -1) {
                        this._root = newn;
                    }
                    else {
                        if (oldn == this.left(this.parent(oldn))) {
                            this.setLeft(this.parent(oldn), newn);
                        }
                        else {
                            this.setRight(this.parent(oldn), newn);
                        }
                    }
                    if (newn != -1) {
                        this.setParent(newn, this.parent(oldn));
                    }
                };
                HeapTimeTreeChunk.prototype.insertCase1 = function (n) {
                    if (this.parent(n) == -1) {
                        this.setColor(n, true);
                    }
                    else {
                        this.insertCase2(n);
                    }
                };
                HeapTimeTreeChunk.prototype.insertCase2 = function (n) {
                    if (!this.color(this.parent(n))) {
                        this.insertCase3(n);
                    }
                };
                HeapTimeTreeChunk.prototype.insertCase3 = function (n) {
                    if (!this.color(this.uncle(n))) {
                        this.setColor(this.parent(n), true);
                        this.setColor(this.uncle(n), true);
                        this.setColor(this.grandParent(n), false);
                        this.insertCase1(this.grandParent(n));
                    }
                    else {
                        this.insertCase4(n);
                    }
                };
                HeapTimeTreeChunk.prototype.insertCase4 = function (n_n) {
                    var n = n_n;
                    if (n == this.right(this.parent(n)) && this.parent(n) == this.left(this.grandParent(n))) {
                        this.rotateLeft(this.parent(n));
                        n = this.left(n);
                    }
                    else {
                        if (n == this.left(this.parent(n)) && this.parent(n) == this.right(this.grandParent(n))) {
                            this.rotateRight(this.parent(n));
                            n = this.right(n);
                        }
                    }
                    this.insertCase5(n);
                };
                HeapTimeTreeChunk.prototype.insertCase5 = function (n) {
                    this.setColor(this.parent(n), true);
                    this.setColor(this.grandParent(n), false);
                    if (n == this.left(this.parent(n)) && this.parent(n) == this.left(this.grandParent(n))) {
                        this.rotateRight(this.grandParent(n));
                    }
                    else {
                        this.rotateLeft(this.grandParent(n));
                    }
                };
                HeapTimeTreeChunk.prototype.internal_insert = function (p_key, initial) {
                    if (this._k == null || this._k.length == this._size) {
                        var length_1 = this._size;
                        if (length_1 == 0) {
                            length_1 = greycat.Constants.MAP_INITIAL_CAPACITY;
                        }
                        else {
                            length_1 = length_1 * 2;
                        }
                        this.reallocate(length_1);
                    }
                    var newIndex = this._size;
                    if (newIndex == 0) {
                        this.setKey(newIndex, p_key, initial);
                        this.setColor(newIndex, false);
                        this.setLeft(newIndex, -1);
                        this.setRight(newIndex, -1);
                        this.setParent(newIndex, -1);
                        this._root = newIndex;
                        this._size = 1;
                    }
                    else {
                        var n = this._root;
                        while (true) {
                            if (p_key == this.key(n)) {
                                return false;
                            }
                            else if (p_key < this.key(n)) {
                                if (this.left(n) == -1) {
                                    this.setKey(newIndex, p_key, initial);
                                    this.setColor(newIndex, false);
                                    this.setLeft(newIndex, -1);
                                    this.setRight(newIndex, -1);
                                    this.setParent(newIndex, -1);
                                    this.setLeft(n, newIndex);
                                    this._size++;
                                    break;
                                }
                                else {
                                    n = this.left(n);
                                }
                            }
                            else {
                                if (this.right(n) == -1) {
                                    this.setKey(newIndex, p_key, initial);
                                    this.setColor(newIndex, false);
                                    this.setLeft(newIndex, -1);
                                    this.setRight(newIndex, -1);
                                    this.setParent(newIndex, -1);
                                    this.setRight(n, newIndex);
                                    this._size++;
                                    break;
                                }
                                else {
                                    n = this.right(n);
                                }
                            }
                        }
                        this.setParent(newIndex, n);
                    }
                    this.insertCase1(newIndex);
                    return true;
                };
                HeapTimeTreeChunk.prototype.internal_set_dirty = function () {
                    this._magic = this._magic + 1;
                    if (this._space != null && !this._dirty) {
                        this._dirty = true;
                        this._space.notifyUpdate(this._index);
                    }
                };
                return HeapTimeTreeChunk;
            }());
            HeapTimeTreeChunk.META_SIZE = 3;
            heap.HeapTimeTreeChunk = HeapTimeTreeChunk;
            var HeapWorldOrderChunk = (function () {
                function HeapWorldOrderChunk(p_space, p_index) {
                    this._index = p_index;
                    this._space = p_space;
                    this._lock = 0;
                    this._magic = 0;
                    this._extra = greycat.internal.CoreConstants.NULL_LONG;
                    this._size = 0;
                    this._capacity = 0;
                    this._kv = null;
                    this._next = null;
                    this._diff = null;
                    this._hash = null;
                    this._dirty = false;
                }
                HeapWorldOrderChunk.prototype.world = function () {
                    return this._space.worldByIndex(this._index);
                };
                HeapWorldOrderChunk.prototype.time = function () {
                    return this._space.timeByIndex(this._index);
                };
                HeapWorldOrderChunk.prototype.id = function () {
                    return this._space.idByIndex(this._index);
                };
                HeapWorldOrderChunk.prototype.extra = function () {
                    return this._extra;
                };
                HeapWorldOrderChunk.prototype.setExtra = function (extraValue) {
                    this._extra = extraValue;
                };
                HeapWorldOrderChunk.prototype.lock = function () {
                };
                HeapWorldOrderChunk.prototype.unlock = function () {
                };
                HeapWorldOrderChunk.prototype.externalLock = function () {
                };
                HeapWorldOrderChunk.prototype.externalUnlock = function () {
                };
                HeapWorldOrderChunk.prototype.magic = function () {
                    return this._magic;
                };
                HeapWorldOrderChunk.prototype.each = function (callback) {
                    for (var i = 0; i < this._size; i++) {
                        callback(this._kv[i * 2], this._kv[i * 2 + 1]);
                    }
                };
                HeapWorldOrderChunk.prototype.get = function (key) {
                    if (this._size > 0) {
                        var index = greycat.utility.HashHelper.longHash(key, this._capacity * 2);
                        var m = this._hash[index];
                        while (m >= 0) {
                            if (key == this._kv[m * 2]) {
                                return this._kv[(m * 2) + 1];
                            }
                            else {
                                m = this._next[m];
                            }
                        }
                    }
                    return greycat.internal.CoreConstants.NULL_LONG;
                };
                HeapWorldOrderChunk.prototype.put = function (key, value) {
                    this.internal_put(key, value, true);
                };
                HeapWorldOrderChunk.prototype.internal_put = function (key, value, notifyUpdate) {
                    if (this._capacity > 0) {
                        var hashIndex = greycat.utility.HashHelper.longHash(key, this._capacity * 2);
                        var m = this._hash[hashIndex];
                        var found = -1;
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
                                hashIndex = greycat.utility.HashHelper.longHash(key, this._capacity * 2);
                            }
                            this._kv[this._size * 2] = key;
                            this._kv[this._size * 2 + 1] = value;
                            if (notifyUpdate) {
                                this._diff[this._size] = true;
                            }
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
                        }
                        else {
                            if (this._kv[found * 2 + 1] != value) {
                                this._kv[found * 2 + 1] = value;
                                if (notifyUpdate) {
                                    this._diff[found] = true;
                                }
                                this._magic = this._magic + 1;
                                if (notifyUpdate && !this._dirty) {
                                    this._dirty = true;
                                    if (this._space != null) {
                                        this._space.notifyUpdate(this._index);
                                    }
                                }
                            }
                        }
                    }
                    else {
                        this._capacity = greycat.Constants.MAP_INITIAL_CAPACITY;
                        this._next = new Int32Array(this._capacity);
                        java.util.Arrays.fill(this._next, 0, this._capacity, -1);
                        this._diff = [];
                        greycat.internal.CoreConstants.fillBooleanArray(this._diff, false);
                        this._hash = new Int32Array(this._capacity * 2);
                        java.util.Arrays.fill(this._hash, 0, this._capacity * 2, -1);
                        this._kv = new Float64Array(this._capacity * 2);
                        this._size = 1;
                        this._kv[0] = key;
                        this._kv[1] = value;
                        if (notifyUpdate) {
                            this._diff[0] = true;
                        }
                        this._hash[greycat.utility.HashHelper.longHash(key, this._capacity * 2)] = 0;
                        if (notifyUpdate && !this._dirty) {
                            this._dirty = true;
                            if (this._space != null) {
                                this._space.notifyUpdate(this._index);
                            }
                        }
                    }
                };
                HeapWorldOrderChunk.prototype.resize = function (newCapacity) {
                    if (newCapacity > this._capacity) {
                        if (this._kv == null) {
                            this._kv = new Float64Array(newCapacity * 2);
                            this._hash = new Int32Array(newCapacity * 2);
                            this._next = new Int32Array(newCapacity);
                            this._diff = [];
                            this._capacity = newCapacity;
                            java.util.Arrays.fill(this._next, 0, newCapacity, -1);
                            greycat.internal.CoreConstants.fillBooleanArray(this._diff, false);
                            java.util.Arrays.fill(this._hash, 0, newCapacity * 2, -1);
                            return true;
                        }
                        else {
                            var temp_kv = new Float64Array(newCapacity * 2);
                            java.lang.System.arraycopy(this._kv, 0, temp_kv, 0, this._size * 2);
                            var temp_diff = [];
                            greycat.internal.CoreConstants.fillBooleanArray(temp_diff, false);
                            java.lang.System.arraycopy(this._diff, 0, temp_diff, 0, this._size);
                            var temp_next = new Int32Array(newCapacity);
                            var temp_hash = new Int32Array(newCapacity * 2);
                            java.util.Arrays.fill(temp_next, 0, newCapacity, -1);
                            java.util.Arrays.fill(temp_hash, 0, newCapacity * 2, -1);
                            for (var i = 0; i < this._size; i++) {
                                var loopIndex = greycat.utility.HashHelper.longHash(temp_kv[i * 2], newCapacity * 2);
                                temp_next[i] = temp_hash[loopIndex];
                                temp_hash[loopIndex] = i;
                            }
                            this._capacity = newCapacity;
                            this._hash = temp_hash;
                            this._next = temp_next;
                            this._kv = temp_kv;
                            this._diff = temp_diff;
                            return true;
                        }
                    }
                    else {
                        return false;
                    }
                };
                HeapWorldOrderChunk.prototype.load = function (buffer) {
                    this.internal_load(true, buffer);
                };
                HeapWorldOrderChunk.prototype.loadDiff = function (buffer) {
                    this.internal_load(false, buffer);
                };
                HeapWorldOrderChunk.prototype.internal_load = function (initial, buffer) {
                    if (buffer != null && buffer.length() > 0) {
                        var cursor = 0;
                        var bufferSize = buffer.length();
                        var initDone = false;
                        var previousStart = 0;
                        var loopKey = greycat.internal.CoreConstants.NULL_LONG;
                        while (cursor < bufferSize) {
                            var current = buffer.read(cursor);
                            switch (current) {
                                case greycat.Constants.CHUNK_SEP:
                                    this._extra = greycat.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                                    previousStart = cursor + 1;
                                    break;
                                case greycat.Constants.CHUNK_VAL_SEP:
                                    if (!initDone) {
                                        this.resize(greycat.utility.Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                                        initDone = true;
                                    }
                                    else if (loopKey == greycat.internal.CoreConstants.NULL_LONG) {
                                        loopKey = greycat.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                                    }
                                    else {
                                        var loopValue = greycat.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                                        this.internal_put(loopKey, loopValue, !initial);
                                        loopKey = greycat.internal.CoreConstants.NULL_LONG;
                                    }
                                    previousStart = cursor + 1;
                                    break;
                            }
                            cursor++;
                        }
                        if (!initDone) {
                            this.resize(greycat.utility.Base64.decodeToLongWithBounds(buffer, 0, cursor));
                        }
                        else {
                            if (loopKey != greycat.internal.CoreConstants.NULL_LONG) {
                                var loopValue = greycat.utility.Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                                this.internal_put(loopKey, loopValue, !initial);
                            }
                        }
                    }
                };
                HeapWorldOrderChunk.prototype.index = function () {
                    return this._index;
                };
                HeapWorldOrderChunk.prototype.remove = function (key) {
                    throw new Error("Not implemented yet!!!");
                };
                HeapWorldOrderChunk.prototype.size = function () {
                    return this._size;
                };
                HeapWorldOrderChunk.prototype.chunkType = function () {
                    return greycat.chunk.ChunkType.WORLD_ORDER_CHUNK;
                };
                HeapWorldOrderChunk.prototype.save = function (buffer) {
                    if (this._extra != greycat.internal.CoreConstants.NULL_LONG) {
                        greycat.utility.Base64.encodeLongToBuffer(this._extra, buffer);
                        buffer.write(greycat.internal.CoreConstants.CHUNK_SEP);
                    }
                    greycat.utility.Base64.encodeIntToBuffer(this._size, buffer);
                    for (var i = 0; i < this._size; i++) {
                        buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                        greycat.utility.Base64.encodeLongToBuffer(this._kv[i * 2], buffer);
                        buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                        greycat.utility.Base64.encodeLongToBuffer(this._kv[i * 2 + 1], buffer);
                    }
                    this._dirty = false;
                };
                HeapWorldOrderChunk.prototype.saveDiff = function (buffer) {
                    if (this._dirty) {
                        if (this._extra != greycat.internal.CoreConstants.NULL_LONG) {
                            greycat.utility.Base64.encodeLongToBuffer(this._extra, buffer);
                            buffer.write(greycat.internal.CoreConstants.CHUNK_SEP);
                        }
                        greycat.utility.Base64.encodeIntToBuffer(this._size, buffer);
                        for (var i = 0; i < this._size; i++) {
                            if (this._diff[i]) {
                                buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                greycat.utility.Base64.encodeLongToBuffer(this._kv[i * 2], buffer);
                                buffer.write(greycat.internal.CoreConstants.CHUNK_VAL_SEP);
                                greycat.utility.Base64.encodeLongToBuffer(this._kv[i * 2 + 1], buffer);
                            }
                        }
                        this._dirty = false;
                    }
                };
                return HeapWorldOrderChunk;
            }());
            heap.HeapWorldOrderChunk = HeapWorldOrderChunk;
        })(heap = internal.heap || (internal.heap = {}));
        var task;
        (function (task_1) {
            var ActionAddRemoveToGlobalIndex = (function () {
                function ActionAddRemoveToGlobalIndex(remove, timed, name) {
                    var attributes = [];
                    for (var _i = 3; _i < arguments.length; _i++) {
                        attributes[_i - 3] = arguments[_i];
                    }
                    this._name = name;
                    this._timed = timed;
                    this._attributes = attributes;
                    this._remove = remove;
                }
                ActionAddRemoveToGlobalIndex.prototype.eval = function (ctx) {
                    var _this = this;
                    var previousResult = ctx.result();
                    var templatedIndexName = ctx.template(this._name);
                    var templatedAttributes = ctx.templates(this._attributes);
                    var counter = new greycat.internal.CoreDeferCounter(previousResult.size());
                    var _loop_5 = function (i) {
                        var loop = previousResult.get(i);
                        if (loop instanceof greycat.base.BaseNode) {
                            var loopBaseNode_1 = loop;
                            var indexTime = greycat.Constants.BEGINNING_OF_TIME;
                            if (this_3._timed) {
                                indexTime = ctx.time();
                            }
                            ctx.graph().index(loopBaseNode_1.world(), indexTime, templatedIndexName, function (indexNode) {
                                if (_this._remove) {
                                    indexNode.removeFromIndex.apply(indexNode, [loopBaseNode_1].concat(templatedAttributes));
                                }
                                else {
                                    indexNode.addToIndex.apply(indexNode, [loopBaseNode_1].concat(templatedAttributes));
                                }
                                counter.count();
                            });
                        }
                        else {
                            counter.count();
                        }
                    };
                    var this_3 = this;
                    for (var i = 0; i < previousResult.size(); i++) {
                        _loop_5(i);
                    }
                    counter.then(function () {
                        {
                            ctx.continueTask();
                        }
                    });
                };
                ActionAddRemoveToGlobalIndex.prototype.serialize = function (builder) {
                    if (this._timed) {
                        builder.append(greycat.internal.task.CoreActionNames.ADD_TO_GLOBAL_TIMED_INDEX);
                    }
                    else {
                        builder.append(greycat.internal.task.CoreActionNames.ADD_TO_GLOBAL_INDEX);
                    }
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._name, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    greycat.internal.task.TaskHelper.serializeStringParams(this._attributes, builder);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionAddRemoveToGlobalIndex.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionAddRemoveToGlobalIndex;
            }());
            task_1.ActionAddRemoveToGlobalIndex = ActionAddRemoveToGlobalIndex;
            var ActionAddRemoveVarToRelation = (function () {
                function ActionAddRemoveVarToRelation(isAdd, name, varFrom) {
                    var attributes = [];
                    for (var _i = 3; _i < arguments.length; _i++) {
                        attributes[_i - 3] = arguments[_i];
                    }
                    this._isAdd = isAdd;
                    this._name = name;
                    this._varFrom = varFrom;
                    this._attributes = attributes;
                }
                ActionAddRemoveVarToRelation.prototype.eval = function (ctx) {
                    var previousResult = ctx.result();
                    var savedVar = ctx.variable(ctx.template(this._varFrom));
                    var templatedAttributes = ctx.templates(this._attributes);
                    if (previousResult != null && savedVar != null) {
                        var relName = ctx.template(this._name);
                        var previousResultIt = previousResult.iterator();
                        var iter = previousResultIt.next();
                        while (iter != null) {
                            if (iter instanceof greycat.base.BaseNode) {
                                var savedVarIt = savedVar.iterator();
                                var toAddIter = savedVarIt.next();
                                while (toAddIter != null) {
                                    if (toAddIter instanceof greycat.base.BaseNode) {
                                        var castedToAddIter = toAddIter;
                                        var castedIter = iter;
                                        if (this._isAdd) {
                                            castedIter.addToRelation.apply(castedIter, [relName, castedToAddIter].concat(templatedAttributes));
                                        }
                                        else {
                                            castedIter.removeFromRelation.apply(castedIter, [relName, castedToAddIter].concat(templatedAttributes));
                                        }
                                    }
                                    toAddIter = savedVarIt.next();
                                }
                            }
                            iter = previousResultIt.next();
                        }
                    }
                    ctx.continueTask();
                };
                ActionAddRemoveVarToRelation.prototype.serialize = function (builder) {
                    if (this._isAdd) {
                        builder.append(greycat.internal.task.CoreActionNames.ADD_VAR_TO_RELATION);
                    }
                    else {
                        builder.append(greycat.internal.task.CoreActionNames.REMOVE_VAR_TO_RELATION);
                    }
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._name, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    greycat.internal.task.TaskHelper.serializeString(this._varFrom, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    greycat.internal.task.TaskHelper.serializeStringParams(this._attributes, builder);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionAddRemoveVarToRelation.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionAddRemoveVarToRelation;
            }());
            task_1.ActionAddRemoveVarToRelation = ActionAddRemoveVarToRelation;
            var ActionAddToVar = (function () {
                function ActionAddToVar(p_name) {
                    if (p_name == null) {
                        throw new Error("p_name should not be null");
                    }
                    this._name = p_name;
                }
                ActionAddToVar.prototype.eval = function (ctx) {
                    var previousResult = ctx.result();
                    if (ctx.isGlobal(this._name)) {
                        ctx.addToGlobalVariable(ctx.template(this._name), previousResult);
                    }
                    else {
                        ctx.addToVariable(ctx.template(this._name), previousResult);
                    }
                    ctx.continueTask();
                };
                ActionAddToVar.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.ADD_TO_VAR);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._name, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionAddToVar.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionAddToVar;
            }());
            task_1.ActionAddToVar = ActionAddToVar;
            var ActionAttributes = (function () {
                function ActionAttributes(filterType) {
                    if (filterType != null) {
                        this._filter = greycat.Type.typeFromName(filterType);
                    }
                    else {
                        this._filter = -1;
                    }
                }
                ActionAttributes.prototype.eval = function (ctx) {
                    var _this = this;
                    var previous = ctx.result();
                    var result = ctx.newResult();
                    for (var i = 0; i < previous.size(); i++) {
                        if (previous.get(i) instanceof greycat.base.BaseNode) {
                            var n = previous.get(i);
                            var nState = ctx.graph().resolver().resolveState(n);
                            nState.each(function (attributeKey, elemType, elem) {
                                {
                                    if (_this._filter == -1 || elemType == _this._filter) {
                                        var retrieved = ctx.graph().resolver().hashToString(attributeKey);
                                        if (retrieved != null) {
                                            result.add(retrieved);
                                        }
                                        else {
                                            result.add(attributeKey);
                                        }
                                    }
                                }
                            });
                            n.free();
                        }
                    }
                    previous.clear();
                    ctx.continueWith(result);
                };
                ActionAttributes.prototype.serialize = function (builder) {
                    if (this._filter == -1) {
                        builder.append(greycat.internal.task.CoreActionNames.ATTRIBUTES);
                        builder.append(greycat.Constants.TASK_PARAM_OPEN);
                        builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                    }
                    else {
                        builder.append(greycat.internal.task.CoreActionNames.ATTRIBUTES_WITH_TYPE);
                        builder.append(greycat.Constants.TASK_PARAM_OPEN);
                        greycat.internal.task.TaskHelper.serializeType(this._filter, builder);
                        builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                    }
                };
                ActionAttributes.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionAttributes;
            }());
            task_1.ActionAttributes = ActionAttributes;
            var ActionClearResult = (function () {
                function ActionClearResult() {
                }
                ActionClearResult.prototype.eval = function (ctx) {
                    ctx.continueWith(ctx.newResult());
                };
                ActionClearResult.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.CLEAR_RESULT);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionClearResult.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionClearResult;
            }());
            task_1.ActionClearResult = ActionClearResult;
            var ActionCreateNode = (function () {
                function ActionCreateNode(typeNode) {
                    this._typeNode = typeNode;
                }
                ActionCreateNode.prototype.eval = function (ctx) {
                    var newNode;
                    if (this._typeNode == null) {
                        newNode = ctx.graph().newNode(ctx.world(), ctx.time());
                    }
                    else {
                        var templatedType = ctx.template(this._typeNode);
                        newNode = ctx.graph().newTypedNode(ctx.world(), ctx.time(), templatedType);
                    }
                    ctx.continueWith(ctx.wrap(newNode));
                };
                ActionCreateNode.prototype.serialize = function (builder) {
                    if (this._typeNode == null) {
                        builder.append(greycat.internal.task.CoreActionNames.CREATE_NODE);
                        builder.append(greycat.Constants.TASK_PARAM_OPEN);
                        builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                    }
                    else {
                        builder.append(greycat.internal.task.CoreActionNames.CREATE_TYPED_NODE);
                        builder.append(greycat.Constants.TASK_PARAM_OPEN);
                        greycat.internal.task.TaskHelper.serializeString(this._typeNode, builder, true);
                        builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                    }
                };
                ActionCreateNode.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionCreateNode;
            }());
            task_1.ActionCreateNode = ActionCreateNode;
            var ActionDeclareVar = (function () {
                function ActionDeclareVar(isGlobal, p_name) {
                    if (p_name == null) {
                        throw new Error("name should not be null");
                    }
                    this._name = p_name;
                    this._isGlobal = isGlobal;
                }
                ActionDeclareVar.prototype.eval = function (ctx) {
                    if (this._isGlobal) {
                        ctx.setGlobalVariable(ctx.template(this._name), greycat.Tasks.emptyResult());
                    }
                    else {
                        ctx.declareVariable(ctx.template(this._name));
                    }
                    ctx.continueTask();
                };
                ActionDeclareVar.prototype.serialize = function (builder) {
                    if (this._isGlobal) {
                        builder.append(greycat.internal.task.CoreActionNames.DECLARE_GLOBAL_VAR);
                    }
                    else {
                        builder.append(greycat.internal.task.CoreActionNames.DECLARE_VAR);
                    }
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._name, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionDeclareVar.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionDeclareVar;
            }());
            task_1.ActionDeclareVar = ActionDeclareVar;
            var ActionDefineAsVar = (function () {
                function ActionDefineAsVar(p_name, p_global) {
                    if (p_name == null) {
                        throw new Error("name should not be null");
                    }
                    this._name = p_name;
                    this._global = p_global;
                }
                ActionDefineAsVar.prototype.eval = function (ctx) {
                    if (this._global) {
                        ctx.setGlobalVariable(ctx.template(this._name), ctx.result());
                    }
                    else {
                        ctx.defineVariable(ctx.template(this._name), ctx.result());
                    }
                    ctx.continueTask();
                };
                ActionDefineAsVar.prototype.serialize = function (builder) {
                    if (this._global) {
                        builder.append(greycat.internal.task.CoreActionNames.DEFINE_AS_GLOBAL_VAR);
                    }
                    else {
                        builder.append(greycat.internal.task.CoreActionNames.DEFINE_AS_VAR);
                    }
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._name, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionDefineAsVar.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionDefineAsVar;
            }());
            task_1.ActionDefineAsVar = ActionDefineAsVar;
            var ActionExecuteExpression = (function () {
                function ActionExecuteExpression(mathExpression) {
                    this._expression = mathExpression;
                    this._engine = greycat.internal.task.math.CoreMathExpressionEngine.parse(mathExpression);
                }
                ActionExecuteExpression.prototype.eval = function (ctx) {
                    var previous = ctx.result();
                    var next = ctx.newResult();
                    var previousSize = previous.size();
                    for (var i = 0; i < previousSize; i++) {
                        var loop = previous.get(i);
                        var variables = new java.util.HashMap();
                        variables.put("PI", Math.PI);
                        variables.put("TRUE", 1.0);
                        variables.put("FALSE", 0.0);
                        if (loop instanceof greycat.base.BaseNode) {
                            next.add(this._engine.eval(loop, ctx, variables));
                            loop.free();
                        }
                        else {
                            next.add(this._engine.eval(null, ctx, variables));
                        }
                    }
                    previous.clear();
                    ctx.continueWith(next);
                };
                ActionExecuteExpression.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.EXECUTE_EXPRESSION);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._expression, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionExecuteExpression.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionExecuteExpression;
            }());
            task_1.ActionExecuteExpression = ActionExecuteExpression;
            var ActionFlat = (function () {
                function ActionFlat() {
                }
                ActionFlat.prototype.eval = function (ctx) {
                    var result = ctx.result();
                    if (result == null) {
                        ctx.continueTask();
                    }
                    else {
                        var next = ctx.newResult();
                        for (var i = 0; i < result.size(); i++) {
                            var loop = result.get(i);
                            if (loop instanceof greycat.base.BaseTaskResult) {
                                var casted = loop;
                                for (var j = 0; j < casted.size(); j++) {
                                    var resultLoop = casted.get(j);
                                    if (resultLoop != null) {
                                        next.add(resultLoop);
                                    }
                                }
                            }
                            else {
                                if (loop != null) {
                                    next.add(loop);
                                }
                            }
                        }
                        ctx.continueWith(next);
                    }
                };
                ActionFlat.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.FLAT);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionFlat.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionFlat;
            }());
            task_1.ActionFlat = ActionFlat;
            var ActionFlipVar = (function () {
                function ActionFlipVar(name) {
                    if (name == null) {
                        throw new Error("name should not be null");
                    }
                    this._name = name;
                }
                ActionFlipVar.prototype.eval = function (ctx) {
                    var previousGlobalVar = ctx.variable(this._name);
                    var nextResult = previousGlobalVar.clone();
                    previousGlobalVar.fillWith(ctx.result());
                    ctx.continueWith(nextResult);
                };
                ActionFlipVar.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.FLIP_VAR);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._name, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionFlipVar.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionFlipVar;
            }());
            task_1.ActionFlipVar = ActionFlipVar;
            var ActionGlobalIndex = (function () {
                function ActionGlobalIndex(p_indexName) {
                    if (p_indexName == null) {
                        throw new Error("indexName should not be null");
                    }
                    this._name = p_indexName;
                }
                ActionGlobalIndex.prototype.eval = function (ctx) {
                    var name = ctx.template(this._name);
                    ctx.graph().indexIfExists(ctx.world(), ctx.time(), name, function (resolvedIndex) {
                        {
                            if (resolvedIndex != null) {
                                ctx.continueWith(ctx.wrap(resolvedIndex));
                            }
                            else {
                                ctx.continueWith(ctx.newResult());
                            }
                        }
                    });
                };
                ActionGlobalIndex.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.GLOBAL_INDEX);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionGlobalIndex.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionGlobalIndex;
            }());
            task_1.ActionGlobalIndex = ActionGlobalIndex;
            var ActionIndexNames = (function () {
                function ActionIndexNames() {
                }
                ActionIndexNames.prototype.eval = function (ctx) {
                    ctx.graph().indexNames(ctx.world(), ctx.time(), function (result) {
                        {
                            ctx.continueWith(ctx.wrap(result));
                        }
                    });
                };
                ActionIndexNames.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.INDEX_NAMES);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionIndexNames.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionIndexNames;
            }());
            task_1.ActionIndexNames = ActionIndexNames;
            var ActionInject = (function () {
                function ActionInject(value) {
                    if (value == null) {
                        throw new Error("inputValue should not be null");
                    }
                    this._value = value;
                }
                ActionInject.prototype.eval = function (ctx) {
                    ctx.continueWith(ctx.wrap(this._value).clone());
                };
                ActionInject.prototype.serialize = function (builder) {
                    throw new Error("inject remote action not managed yet!");
                };
                ActionInject.prototype.toString = function () {
                    return "inject()";
                };
                return ActionInject;
            }());
            task_1.ActionInject = ActionInject;
            var ActionLog = (function () {
                function ActionLog(p_value) {
                    this._value = p_value;
                }
                ActionLog.prototype.eval = function (ctx) {
                    console.log(ctx.template(this._value));
                    ctx.continueTask();
                };
                ActionLog.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.LOG);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._value, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionLog.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionLog;
            }());
            task_1.ActionLog = ActionLog;
            var ActionLookup = (function () {
                function ActionLookup(p_id) {
                    this._id = p_id;
                }
                ActionLookup.prototype.eval = function (ctx) {
                    var idL = java.lang.Long.parseLong(ctx.template(this._id));
                    ctx.graph().lookup(ctx.world(), ctx.time(), idL, function (result) {
                        {
                            ctx.continueWith(ctx.wrap(result));
                        }
                    });
                };
                ActionLookup.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.LOOKUP);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._id, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionLookup.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionLookup;
            }());
            task_1.ActionLookup = ActionLookup;
            var ActionLookupAll = (function () {
                function ActionLookupAll(p_ids) {
                    this._ids = p_ids;
                }
                ActionLookupAll.prototype.eval = function (ctx) {
                    var afterTemplate = ctx.template(this._ids).trim();
                    if ((afterTemplate.lastIndexOf("[", 0) === 0)) {
                        afterTemplate = afterTemplate.substring(1, afterTemplate.length - 1);
                    }
                    var values = afterTemplate.split(",");
                    var ids = new Float64Array(values.length);
                    for (var i = 0; i < values.length; i++) {
                        ids[i] = java.lang.Long.parseLong(values[i]);
                    }
                    ctx.graph().lookupAll(ctx.world(), ctx.time(), ids, function (result) {
                        {
                            ctx.continueWith(ctx.wrap(result));
                        }
                    });
                };
                ActionLookupAll.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.LOOKUP_ALL);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._ids, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionLookupAll.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionLookupAll;
            }());
            task_1.ActionLookupAll = ActionLookupAll;
            var ActionNamed = (function () {
                function ActionNamed(name) {
                    var params = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        params[_i - 1] = arguments[_i];
                    }
                    this._name = name;
                    this._params = params;
                }
                ActionNamed.prototype.eval = function (ctx) {
                    var subAction = greycat.internal.task.CoreTask.loadAction(ctx.graph().actionRegistry(), this._name, this._params, null);
                    if (subAction != null) {
                        subAction.eval(ctx);
                    }
                    else {
                        ctx.continueTask();
                    }
                };
                ActionNamed.prototype.serialize = function (builder) {
                    builder.append(this._name);
                    greycat.internal.task.TaskHelper.serializeStringParams(this._params, builder);
                };
                ActionNamed.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionNamed;
            }());
            task_1.ActionNamed = ActionNamed;
            var ActionPrint = (function () {
                function ActionPrint(p_name, withLineBreak) {
                    this._name = p_name;
                    this._withLineBreak = withLineBreak;
                }
                ActionPrint.prototype.eval = function (ctx) {
                    if (this._withLineBreak) {
                        ctx.append(ctx.template(this._name) + '\n');
                    }
                    else {
                        ctx.append(ctx.template(this._name));
                    }
                    ctx.continueTask();
                };
                ActionPrint.prototype.serialize = function (builder) {
                    if (this._withLineBreak) {
                        builder.append(greycat.internal.task.CoreActionNames.PRINTLN);
                    }
                    else {
                        builder.append(greycat.internal.task.CoreActionNames.PRINT);
                    }
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._name, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionPrint.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionPrint;
            }());
            task_1.ActionPrint = ActionPrint;
            var ActionReadGlobalIndex = (function () {
                function ActionReadGlobalIndex(p_indexName) {
                    var p_query = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        p_query[_i - 1] = arguments[_i];
                    }
                    if (p_indexName == null) {
                        throw new Error("indexName should not be null");
                    }
                    this._name = p_indexName;
                    this._params = p_query;
                }
                ActionReadGlobalIndex.prototype.eval = function (ctx) {
                    var name = ctx.template(this._name);
                    var query = ctx.templates(this._params);
                    ctx.graph().indexIfExists(ctx.world(), ctx.time(), name, function (resolvedIndex) {
                        {
                            if (resolvedIndex != null) {
                                resolvedIndex.find.apply(resolvedIndex, [function (result) {
                                        {
                                            resolvedIndex.free();
                                            ctx.continueWith(ctx.wrap(result));
                                        }
                                    }].concat(query));
                            }
                            else {
                                ctx.continueWith(ctx.newResult());
                            }
                        }
                    });
                };
                ActionReadGlobalIndex.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.READ_GLOBAL_INDEX);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._name, builder, true);
                    if (this._params != null && this._params.length > 0) {
                        builder.append(greycat.Constants.TASK_PARAM_SEP);
                        greycat.internal.task.TaskHelper.serializeStringParams(this._params, builder);
                    }
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionReadGlobalIndex.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionReadGlobalIndex;
            }());
            task_1.ActionReadGlobalIndex = ActionReadGlobalIndex;
            var ActionReadVar = (function () {
                function ActionReadVar(p_name) {
                    this._origin = p_name;
                    var indexEnd = -1;
                    var indexStart = -1;
                    var cursor = p_name.length - 1;
                    while (cursor > 0) {
                        var c = p_name.charAt(cursor);
                        if (c == ']') {
                            indexEnd = cursor;
                        }
                        else if (c == '[') {
                            indexStart = cursor + 1;
                        }
                        cursor--;
                    }
                    if (indexEnd != -1 && indexStart != -1) {
                        this._index = greycat.internal.task.TaskHelper.parseInt(p_name.substring(indexStart, indexEnd));
                        this._name = p_name.substring(0, indexStart - 1);
                    }
                    else {
                        this._index = -1;
                        this._name = p_name;
                    }
                }
                ActionReadVar.prototype.eval = function (ctx) {
                    var evaluatedName = ctx.template(this._name);
                    var varResult;
                    if (this._index != -1) {
                        varResult = ctx.wrap(ctx.variable(evaluatedName).get(this._index));
                    }
                    else {
                        varResult = ctx.variable(evaluatedName);
                    }
                    if (varResult != null) {
                        varResult = varResult.clone();
                    }
                    ctx.continueWith(varResult);
                };
                ActionReadVar.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.READ_VAR);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._origin, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionReadVar.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionReadVar;
            }());
            task_1.ActionReadVar = ActionReadVar;
            var ActionRemove = (function () {
                function ActionRemove(name) {
                    this._name = name;
                }
                ActionRemove.prototype.eval = function (ctx) {
                    var previousResult = ctx.result();
                    if (previousResult != null) {
                        var flatRelationName = ctx.template(this._name);
                        for (var i = 0; i < previousResult.size(); i++) {
                            var loopObj = previousResult.get(i);
                            if (loopObj instanceof greycat.base.BaseNode) {
                                var loopNode = loopObj;
                                loopNode.remove(flatRelationName);
                            }
                        }
                    }
                    ctx.continueTask();
                };
                ActionRemove.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.REMOVE);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._name, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionRemove.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionRemove;
            }());
            task_1.ActionRemove = ActionRemove;
            var ActionSave = (function () {
                function ActionSave() {
                }
                ActionSave.prototype.eval = function (ctx) {
                    ctx.graph().save(function (result) {
                        {
                            ctx.continueTask();
                        }
                    });
                };
                ActionSave.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.SAVE);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionSave.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionSave;
            }());
            task_1.ActionSave = ActionSave;
            var ActionScript = (function () {
                function ActionScript(script, async) {
                    this._script = script;
                    this._async = async;
                }
                ActionScript.prototype.eval = function (ctx) {
                    var isolation = function (script) {
                        var variables = ctx.variables();
                        for (var i = 0; i < variables.length; i++) {
                            this[variables[i].left()] = variables[i].right();
                        }
                        this['print'] = function (v) { ctx.append(v + '\n'); };
                        this['result'] = ctx.result();
                        return eval(script);
                    };
                    var scriptResult = isolation(this._script);
                    if (!this._async) {
                        if (scriptResult != null && scriptResult != undefined) {
                            ctx.continueWith(ctx.wrap(scriptResult));
                        }
                        else {
                            ctx.continueTask();
                        }
                    }
                };
                ActionScript.prototype.serialize = function (builder) {
                    if (this._async) {
                        builder.append(greycat.internal.task.CoreActionNames.ASYNC_SCRIPT);
                    }
                    else {
                        builder.append(greycat.internal.task.CoreActionNames.SCRIPT);
                    }
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._script, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionScript.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionScript;
            }());
            task_1.ActionScript = ActionScript;
            var ActionSelect = (function () {
                function ActionSelect(script, filter) {
                    this._script = script;
                    this._filter = filter;
                }
                ActionSelect.prototype.eval = function (ctx) {
                    var previous = ctx.result();
                    var next = ctx.newResult();
                    var previousSize = previous.size();
                    for (var i = 0; i < previousSize; i++) {
                        var obj = previous.get(i);
                        if (obj instanceof greycat.base.BaseNode) {
                            var casted = obj;
                            if (this._filter != null && this._filter(casted, ctx)) {
                                next.add(casted);
                            }
                            else if (this._script != null && this.callScript(casted, ctx)) {
                                next.add(casted);
                            }
                            else {
                                casted.free();
                            }
                        }
                        else {
                            next.add(obj);
                        }
                    }
                    previous.clear();
                    ctx.continueWith(next);
                };
                ActionSelect.prototype.callScript = function (node, context) {
                    var print = console.log;
                    return eval(this._script);
                };
                ActionSelect.prototype.serialize = function (builder) {
                    if (this._script == null) {
                        throw new Error("Select remote usage not managed yet, please use SelectScript instead !");
                    }
                    builder.append(greycat.internal.task.CoreActionNames.SELECT);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._script, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionSelect.prototype.toString = function () {
                    if (this._filter != null) {
                        return "select()";
                    }
                    else {
                        var res = new java.lang.StringBuilder();
                        this.serialize(res);
                        return res.toString();
                    }
                };
                return ActionSelect;
            }());
            task_1.ActionSelect = ActionSelect;
            var ActionSelectObject = (function () {
                function ActionSelectObject(filterFunction) {
                    if (filterFunction == null) {
                        throw new Error("filterFunction should not be null");
                    }
                    this._filter = filterFunction;
                }
                ActionSelectObject.prototype.eval = function (ctx) {
                    var previous = ctx.result();
                    var next = ctx.wrap(null);
                    var iterator = previous.iterator();
                    var nextElem = iterator.next();
                    while (nextElem != null) {
                        if (this._filter(nextElem, ctx)) {
                            if (nextElem instanceof greycat.base.BaseNode) {
                                var casted = nextElem;
                                next.add(casted.graph().cloneNode(casted));
                            }
                            else {
                                next.add(nextElem);
                            }
                        }
                        nextElem = iterator.next();
                    }
                    ctx.continueWith(next);
                };
                ActionSelectObject.prototype.serialize = function (builder) {
                    throw new Error("SelectObject remote usage not managed yet, please use SelectScript instead !");
                };
                ActionSelectObject.prototype.toString = function () {
                    return "selectObject()";
                };
                return ActionSelectObject;
            }());
            task_1.ActionSelectObject = ActionSelectObject;
            var ActionSetAsVar = (function () {
                function ActionSetAsVar(p_name) {
                    if (p_name == null) {
                        throw new Error("variableName should not be null");
                    }
                    this._name = p_name;
                }
                ActionSetAsVar.prototype.eval = function (ctx) {
                    var previousResult = ctx.result();
                    if (ctx.isGlobal(this._name)) {
                        ctx.setGlobalVariable(ctx.template(this._name), previousResult);
                    }
                    else {
                        ctx.setVariable(ctx.template(this._name), previousResult);
                    }
                    ctx.continueTask();
                };
                ActionSetAsVar.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.SET_AS_VAR);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    builder.append(this._name);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionSetAsVar.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionSetAsVar;
            }());
            task_1.ActionSetAsVar = ActionSetAsVar;
            var ActionSetAttribute = (function () {
                function ActionSetAttribute(name, propertyType, value, force) {
                    this._name = name;
                    this._value = value;
                    this._propertyType = greycat.Type.typeFromName(propertyType);
                    this._force = force;
                }
                ActionSetAttribute.prototype.eval = function (ctx) {
                    var previousResult = ctx.result();
                    var flatRelationName = ctx.template(this._name);
                    if (previousResult != null) {
                        var toSet = void 0;
                        var valueAfterTemplate = ctx.template(this._value);
                        switch (this._propertyType) {
                            case greycat.Type.BOOL:
                                toSet = this.parseBoolean(valueAfterTemplate);
                                break;
                            case greycat.Type.INT:
                                toSet = greycat.internal.task.TaskHelper.parseInt(valueAfterTemplate);
                                break;
                            case greycat.Type.DOUBLE:
                                toSet = parseFloat(valueAfterTemplate);
                                break;
                            case greycat.Type.LONG:
                                toSet = java.lang.Long.parseLong(valueAfterTemplate);
                                break;
                            case greycat.Type.DOUBLE_ARRAY:
                            case greycat.Type.LONG_ARRAY:
                            case greycat.Type.INT_ARRAY:
                                {
                                    try {
                                        toSet = this.loadArray(valueAfterTemplate, this._propertyType);
                                    }
                                    catch ($ex$) {
                                        if ($ex$ instanceof Error) {
                                            var e = $ex$;
                                            {
                                                toSet = null;
                                                ctx.endTask(null, new Error("Error while parsing array from string:\"" + valueAfterTemplate + "\"\n" + e.toString()));
                                            }
                                        }
                                        else {
                                            throw $ex$;
                                        }
                                    }
                                }
                                break;
                            default:
                                toSet = valueAfterTemplate;
                        }
                        for (var i = 0; i < previousResult.size(); i++) {
                            var loopObj = previousResult.get(i);
                            if (loopObj instanceof greycat.base.BaseNode) {
                                var loopNode = loopObj;
                                if (this._force) {
                                    loopNode.forceSet(flatRelationName, this._propertyType, toSet);
                                }
                                else {
                                    loopNode.set(flatRelationName, this._propertyType, toSet);
                                }
                            }
                        }
                    }
                    ctx.continueTask();
                };
                ActionSetAttribute.prototype.loadArray = function (valueAfterTemplate, type) {
                    var arrayInString = valueAfterTemplate;
                    if (arrayInString.charAt(0) == '[') {
                        arrayInString = arrayInString.substring(1);
                    }
                    if (arrayInString.charAt(arrayInString.length - 1) == ']') {
                        arrayInString = arrayInString.substring(0, arrayInString.length - 1);
                    }
                    var valuesToParse = arrayInString.split(",");
                    switch (type) {
                        case greycat.Type.DOUBLE_ARRAY: {
                            var finalValues = new Float64Array(valuesToParse.length);
                            for (var i = 0; i < valuesToParse.length; i++) {
                                finalValues[i] = parseFloat(valuesToParse[i]);
                            }
                            return finalValues;
                        }
                        case greycat.Type.LONG_ARRAY: {
                            var finalValues = new Float64Array(valuesToParse.length);
                            for (var i = 0; i < valuesToParse.length; i++) {
                                finalValues[i] = java.lang.Long.parseLong(valuesToParse[i]);
                            }
                            return finalValues;
                        }
                        case greycat.Type.INT_ARRAY: {
                            var finalValues = new Int32Array(valuesToParse.length);
                            for (var i = 0; i < valuesToParse.length; i++) {
                                finalValues[i] = java.lang.Integer.parseInt(valuesToParse[i]);
                            }
                            return finalValues;
                        }
                    }
                    return null;
                };
                ActionSetAttribute.prototype.parseBoolean = function (booleanValue) {
                    var lower = booleanValue.toLowerCase();
                    return (lower === "true" || lower === "1");
                };
                ActionSetAttribute.prototype.serialize = function (builder) {
                    if (this._force) {
                        builder.append(greycat.internal.task.CoreActionNames.FORCE_ATTRIBUTE);
                    }
                    else {
                        builder.append(greycat.internal.task.CoreActionNames.SET_ATTRIBUTE);
                    }
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._name, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    greycat.internal.task.TaskHelper.serializeType(this._propertyType, builder);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    greycat.internal.task.TaskHelper.serializeString(this._value, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionSetAttribute.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionSetAttribute;
            }());
            task_1.ActionSetAttribute = ActionSetAttribute;
            var ActionTimeSensitivity = (function () {
                function ActionTimeSensitivity(delta, offset) {
                    this._delta = delta;
                    this._offset = offset;
                }
                ActionTimeSensitivity.prototype.eval = function (ctx) {
                    var previousResult = ctx.result();
                    var parsedDelta = java.lang.Long.parseLong(ctx.template(this._delta));
                    var parsedOffset = java.lang.Long.parseLong(ctx.template(this._offset));
                    for (var i = 0; i < previousResult.size(); i++) {
                        var loopObj = previousResult.get(i);
                        if (loopObj instanceof greycat.base.BaseNode) {
                            var loopNode = loopObj;
                            loopNode.setTimeSensitivity(parsedDelta, parsedOffset);
                        }
                    }
                    ctx.continueTask();
                };
                ActionTimeSensitivity.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.SET_ATTRIBUTE);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    builder.append(this._delta);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    builder.append(this._offset);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionTimeSensitivity.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionTimeSensitivity;
            }());
            task_1.ActionTimeSensitivity = ActionTimeSensitivity;
            var ActionTimepoints = (function () {
                function ActionTimepoints(from, to) {
                    this._from = from;
                    this._to = to;
                }
                ActionTimepoints.prototype.eval = function (ctx) {
                    var previous = ctx.result();
                    var tFrom = ctx.template(this._from);
                    var tTo = ctx.template(this._to);
                    var parsedFrom;
                    var parsedTo;
                    try {
                        parsedFrom = java.lang.Long.parseLong(tFrom);
                    }
                    catch ($ex$) {
                        if ($ex$ instanceof Error) {
                            var t = $ex$;
                            {
                                var d = parseFloat(tFrom);
                                parsedFrom = (d < 0 ? Math.ceil(d) : Math.floor(d));
                            }
                        }
                        else {
                            throw $ex$;
                        }
                    }
                    try {
                        parsedTo = java.lang.Long.parseLong(tTo);
                    }
                    catch ($ex$) {
                        if ($ex$ instanceof Error) {
                            var t = $ex$;
                            {
                                var d = parseFloat(tTo);
                                parsedTo = (d < 0 ? Math.ceil(d) : Math.floor(d));
                            }
                        }
                        else {
                            throw $ex$;
                        }
                    }
                    var next = ctx.newResult();
                    if (previous != null) {
                        var defer_1 = new greycat.internal.CoreDeferCounter(previous.size());
                        var _loop_6 = function (i) {
                            if (previous.get(i) instanceof greycat.base.BaseNode) {
                                var casted_1 = previous.get(i);
                                casted_1.timepoints(parsedFrom, parsedTo, function (result) {
                                    {
                                        for (var i_1 = 0; i_1 < result.length; i_1++) {
                                            next.add(result[i_1]);
                                        }
                                        casted_1.free();
                                        defer_1.count();
                                    }
                                });
                            }
                        };
                        for (var i = 0; i < previous.size(); i++) {
                            _loop_6(i);
                        }
                        defer_1.then(function () {
                            {
                                previous.clear();
                                ctx.continueWith(next);
                            }
                        });
                    }
                    else {
                        ctx.continueWith(next);
                    }
                };
                ActionTimepoints.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.TIMEPOINTS);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionTimepoints.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionTimepoints;
            }());
            task_1.ActionTimepoints = ActionTimepoints;
            var ActionTravelInTime = (function () {
                function ActionTravelInTime(time) {
                    this._time = time;
                }
                ActionTravelInTime.prototype.eval = function (ctx) {
                    var flatTime = ctx.template(this._time);
                    var parsedTime;
                    try {
                        parsedTime = java.lang.Long.parseLong(flatTime);
                    }
                    catch ($ex$) {
                        if ($ex$ instanceof Error) {
                            var t = $ex$;
                            {
                                var d = parseFloat(flatTime);
                                parsedTime = (d < 0 ? Math.ceil(d) : Math.floor(d));
                            }
                        }
                        else {
                            throw $ex$;
                        }
                    }
                    ctx.setTime(parsedTime);
                    var previous = ctx.result();
                    var defer = new greycat.internal.CoreDeferCounter(previous.size());
                    var previousSize = previous.size();
                    var _loop_7 = function (i) {
                        var loopObj = previous.get(i);
                        if (loopObj instanceof greycat.base.BaseNode) {
                            var castedPreviousNode_1 = loopObj;
                            var finalIndex_1 = i;
                            castedPreviousNode_1.travelInTime(parsedTime, function (result) {
                                {
                                    castedPreviousNode_1.free();
                                    previous.set(finalIndex_1, result);
                                    defer.count();
                                }
                            });
                        }
                        else {
                            defer.count();
                        }
                    };
                    for (var i = 0; i < previousSize; i++) {
                        _loop_7(i);
                    }
                    defer.then(function () {
                        {
                            ctx.continueTask();
                        }
                    });
                };
                ActionTravelInTime.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.TRAVEL_IN_TIME);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    builder.append(this._time);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionTravelInTime.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionTravelInTime;
            }());
            task_1.ActionTravelInTime = ActionTravelInTime;
            var ActionTravelInWorld = (function () {
                function ActionTravelInWorld(world) {
                    this._world = world;
                }
                ActionTravelInWorld.prototype.eval = function (ctx) {
                    var flatWorld = ctx.template(this._world);
                    var parsedWorld;
                    try {
                        parsedWorld = java.lang.Long.parseLong(flatWorld);
                    }
                    catch ($ex$) {
                        if ($ex$ instanceof Error) {
                            var t = $ex$;
                            {
                                var d = parseFloat(flatWorld);
                                parsedWorld = (d < 0 ? Math.ceil(d) : Math.floor(d));
                            }
                        }
                        else {
                            throw $ex$;
                        }
                    }
                    ctx.setWorld(parsedWorld);
                    var previous = ctx.result();
                    var defer = new greycat.internal.CoreDeferCounter(previous.size());
                    var previousSize = previous.size();
                    var _loop_8 = function (i) {
                        var loopObj = previous.get(i);
                        if (loopObj instanceof greycat.base.BaseNode) {
                            var castedPreviousNode_2 = loopObj;
                            var finalIndex_2 = i;
                            ctx.graph().lookup(parsedWorld, castedPreviousNode_2.time(), castedPreviousNode_2.id(), function (result) {
                                {
                                    castedPreviousNode_2.free();
                                    previous.set(finalIndex_2, result);
                                    defer.count();
                                }
                            });
                        }
                        else {
                            defer.count();
                        }
                    };
                    for (var i = 0; i < previousSize; i++) {
                        _loop_8(i);
                    }
                    defer.then(function () {
                        {
                            ctx.continueTask();
                        }
                    });
                };
                ActionTravelInWorld.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.TRAVEL_IN_TIME);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    builder.append(this._world);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionTravelInWorld.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionTravelInWorld;
            }());
            task_1.ActionTravelInWorld = ActionTravelInWorld;
            var ActionTraverseOrAttribute = (function () {
                function ActionTraverseOrAttribute(isAttribute, isUnknown, p_name) {
                    var p_params = [];
                    for (var _i = 3; _i < arguments.length; _i++) {
                        p_params[_i - 3] = arguments[_i];
                    }
                    this._name = p_name;
                    this._params = p_params;
                    this._isUnknown = isUnknown;
                    this._isAttribute = isAttribute;
                }
                ActionTraverseOrAttribute.prototype.eval = function (ctx) {
                    var finalResult = ctx.newResult();
                    var flatName = ctx.template(this._name);
                    var previousResult = ctx.result();
                    if (previousResult != null) {
                        var previousSize = previousResult.size();
                        var defer_2 = ctx.graph().newCounter(previousSize);
                        var _loop_9 = function (i) {
                            var loop = previousResult.get(i);
                            if (loop instanceof greycat.base.BaseNode) {
                                var casted_2 = loop;
                                switch (casted_2.type(flatName)) {
                                    case greycat.Type.RELATION:
                                        casted_2.relation(flatName, function (result) {
                                            {
                                                if (result != null) {
                                                    for (var j = 0; j < result.length; j++) {
                                                        finalResult.add(result[j]);
                                                    }
                                                }
                                                casted_2.free();
                                                defer_2.count();
                                            }
                                        });
                                        break;
                                    case greycat.Type.RELATION_INDEXED:
                                        var relationIndexed = casted_2.get(flatName);
                                        if (relationIndexed != null) {
                                            if (this_4._params != null && this_4._params.length > 0) {
                                                var templatedParams = ctx.templates(this_4._params);
                                                var query = ctx.graph().newQuery();
                                                query.setWorld(casted_2.world());
                                                query.setTime(casted_2.time());
                                                var previous = null;
                                                for (var k = 0; k < templatedParams.length; k++) {
                                                    if (previous != null) {
                                                        query.add(previous, templatedParams[k]);
                                                        previous = null;
                                                    }
                                                    else {
                                                        previous = templatedParams[k];
                                                    }
                                                }
                                                relationIndexed.findByQuery(query, function (result) {
                                                    {
                                                        if (result != null) {
                                                            for (var j = 0; j < result.length; j++) {
                                                                if (result[j] != null) {
                                                                    finalResult.add(result[j]);
                                                                }
                                                            }
                                                        }
                                                        casted_2.free();
                                                        defer_2.count();
                                                    }
                                                });
                                            }
                                            else {
                                                casted_2.graph().lookupAll(ctx.world(), ctx.time(), relationIndexed.all(), function (result) {
                                                    {
                                                        if (result != null) {
                                                            for (var j = 0; j < result.length; j++) {
                                                                if (result[j] != null) {
                                                                    finalResult.add(result[j]);
                                                                }
                                                            }
                                                        }
                                                        casted_2.free();
                                                        defer_2.count();
                                                    }
                                                });
                                            }
                                        }
                                        else {
                                            defer_2.count();
                                        }
                                        break;
                                    default:
                                        var resolved = casted_2.get(flatName);
                                        if (resolved != null) {
                                            finalResult.add(resolved);
                                        }
                                        casted_2.free();
                                        defer_2.count();
                                        break;
                                }
                            }
                            else {
                                finalResult.add(loop);
                                defer_2.count();
                            }
                        };
                        var this_4 = this;
                        for (var i = 0; i < previousSize; i++) {
                            _loop_9(i);
                        }
                        defer_2.then(function () {
                            {
                                previousResult.clear();
                                ctx.continueWith(finalResult);
                            }
                        });
                    }
                    else {
                        ctx.continueTask();
                    }
                };
                ActionTraverseOrAttribute.prototype.serialize = function (builder) {
                    if (this._isUnknown) {
                        builder.append(this._name);
                    }
                    else {
                        if (this._isAttribute) {
                            builder.append(greycat.internal.task.CoreActionNames.ATTRIBUTE);
                            builder.append(greycat.Constants.TASK_PARAM_OPEN);
                            builder.append(this._name);
                            builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                        }
                        else {
                            builder.append(greycat.internal.task.CoreActionNames.TRAVERSE);
                            builder.append(greycat.Constants.TASK_PARAM_OPEN);
                            builder.append(this._name);
                            if (this._params != null && this._params.length > 0) {
                                builder.append(greycat.Constants.TASK_PARAM_SEP);
                                greycat.internal.task.TaskHelper.serializeStringParams(this._params, builder);
                            }
                            builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                        }
                    }
                };
                ActionTraverseOrAttribute.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionTraverseOrAttribute;
            }());
            task_1.ActionTraverseOrAttribute = ActionTraverseOrAttribute;
            var ActionWith = (function () {
                function ActionWith(name, stringPattern) {
                    if (name == null) {
                        throw new Error("name should not be null");
                    }
                    if (stringPattern == null) {
                        throw new Error("pattern should not be null");
                    }
                    this._patternTemplate = stringPattern;
                    this._name = name;
                }
                ActionWith.prototype.eval = function (ctx) {
                    var pattern = new RegExp(ctx.template(this._patternTemplate));
                    var previous = ctx.result();
                    var next = ctx.newResult();
                    var previousSize = previous.size();
                    for (var i = 0; i < previousSize; i++) {
                        var obj = previous.get(i);
                        if (obj instanceof greycat.base.BaseNode) {
                            var casted = obj;
                            var currentName = casted.get(this._name);
                            if (currentName != null && pattern.test(currentName.toString())) {
                                next.add(casted.graph().cloneNode(casted));
                            }
                        }
                        else {
                            next.add(obj);
                        }
                    }
                    ctx.continueWith(next);
                };
                ActionWith.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.WITH);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._name, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    greycat.internal.task.TaskHelper.serializeString(this._patternTemplate, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionWith.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionWith;
            }());
            task_1.ActionWith = ActionWith;
            var ActionWithout = (function () {
                function ActionWithout(name, stringPattern) {
                    if (name == null) {
                        throw new Error("name should not be null");
                    }
                    if (stringPattern == null) {
                        throw new Error("pattern should not be null");
                    }
                    this._patternTemplate = stringPattern;
                    this._name = name;
                }
                ActionWithout.prototype.eval = function (ctx) {
                    var pattern = new RegExp(ctx.template(this._patternTemplate));
                    var previous = ctx.result();
                    var next = ctx.newResult();
                    var previousSize = previous.size();
                    for (var i = 0; i < previousSize; i++) {
                        var obj = previous.get(i);
                        if (obj instanceof greycat.base.BaseNode) {
                            var casted = obj;
                            var currentName = casted.get(this._name);
                            if (currentName == null || !pattern.test(currentName.toString())) {
                                next.add(casted.graph().cloneNode(casted));
                            }
                        }
                        else {
                            next.add(obj);
                        }
                    }
                    ctx.continueWith(next);
                };
                ActionWithout.prototype.serialize = function (builder) {
                    builder.append(greycat.internal.task.CoreActionNames.WITHOUT);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._name, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    greycat.internal.task.TaskHelper.serializeString(this._patternTemplate, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                ActionWithout.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.serialize(res);
                    return res.toString();
                };
                return ActionWithout;
            }());
            task_1.ActionWithout = ActionWithout;
            var CF_Action = (function () {
                function CF_Action() {
                }
                CF_Action.prototype.serialize = function (builder) {
                    throw new Error("serialization error !!!");
                };
                CF_Action.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    this.cf_serialize(res, null);
                    return res.toString();
                };
                return CF_Action;
            }());
            task_1.CF_Action = CF_Action;
            var CF_Atomic = (function (_super) {
                __extends(CF_Atomic, _super);
                function CF_Atomic(p_subTask) {
                    var variables = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        variables[_i - 1] = arguments[_i];
                    }
                    var _this = _super.call(this) || this;
                    _this._subTask = p_subTask;
                    _this._variables = variables;
                    return _this;
                }
                CF_Atomic.prototype.eval = function (ctx) {
                    var collected = new java.util.ArrayList();
                    for (var i = 0; i < this._variables.length; i++) {
                        var varName = this._variables[i];
                        var resolved = void 0;
                        if (varName === "result") {
                            resolved = ctx.result();
                        }
                        else {
                            resolved = ctx.variable(varName);
                        }
                        if (resolved != null) {
                            collected.add(resolved);
                        }
                    }
                    var resolver = ctx.graph().resolver();
                    for (var i = 0; i < collected.size(); i++) {
                        var toLock = collected.get(i);
                        for (var j = 0; j < toLock.size(); j++) {
                            var o = toLock.get(j);
                            if (o instanceof greycat.base.BaseNode) {
                                resolver.externalLock(o);
                            }
                        }
                    }
                    this._subTask.executeFrom(ctx, ctx.result(), greycat.plugin.SchedulerAffinity.SAME_THREAD, function (result) {
                        {
                            var exceptionDuringTask = null;
                            if (result != null) {
                                if (result.output() != null) {
                                    ctx.append(result.output());
                                }
                                if (result.exception() != null) {
                                    exceptionDuringTask = result.exception();
                                }
                            }
                            for (var i = 0; i < collected.size(); i++) {
                                var toLock = collected.get(i);
                                for (var j = 0; j < toLock.size(); j++) {
                                    var o = toLock.get(j);
                                    if (o instanceof greycat.base.BaseNode) {
                                        resolver.externalUnlock(o);
                                    }
                                }
                            }
                            if (exceptionDuringTask != null) {
                                ctx.endTask(result, exceptionDuringTask);
                            }
                            else {
                                ctx.continueWith(result);
                            }
                        }
                    });
                };
                CF_Atomic.prototype.children = function () {
                    var children_tasks = new Array(1);
                    children_tasks[0] = this._subTask;
                    return children_tasks;
                };
                CF_Atomic.prototype.cf_serialize = function (builder, dagIDS) {
                    builder.append(greycat.internal.task.CoreActionNames.ATOMIC);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    var castedAction = this._subTask;
                    var castedActionHash = castedAction.hashCode();
                    if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                        builder.append(greycat.Constants.SUB_TASK_OPEN);
                        castedAction.serialize(builder, dagIDS);
                        builder.append(greycat.Constants.SUB_TASK_CLOSE);
                    }
                    else {
                        builder.append("" + dagIDS.get(castedActionHash));
                    }
                    for (var i = 0; i < this._variables.length; i++) {
                        builder.append(greycat.Constants.TASK_PARAM_SEP);
                        greycat.internal.task.TaskHelper.serializeString(this._variables[i], builder, true);
                    }
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return CF_Atomic;
            }(greycat.internal.task.CF_Action));
            task_1.CF_Atomic = CF_Atomic;
            var CF_DoWhile = (function (_super) {
                __extends(CF_DoWhile, _super);
                function CF_DoWhile(p_then, p_cond, conditionalScript) {
                    var _this = _super.call(this) || this;
                    _this._cond = p_cond;
                    _this._then = p_then;
                    _this._conditionalScript = conditionalScript;
                    return _this;
                }
                CF_DoWhile.prototype.eval = function (ctx) {
                    var _this = this;
                    var coreTaskContext = ctx;
                    var selfPointer = this;
                    var recursiveAction = new Array(1);
                    recursiveAction[0] = function (res) {
                        {
                            var previous = coreTaskContext._result;
                            coreTaskContext._result = res;
                            var exceptionDuringTask = null;
                            if (res != null) {
                                if (res.output() != null) {
                                    ctx.append(res.output());
                                }
                                if (res.exception() != null) {
                                    exceptionDuringTask = res.exception();
                                }
                            }
                            if (_this._cond(ctx) && exceptionDuringTask == null) {
                                if (previous != null) {
                                    previous.free();
                                }
                                selfPointer._then.executeFrom(ctx, coreTaskContext._result, greycat.plugin.SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
                            }
                            else {
                                if (previous != null) {
                                    previous.free();
                                }
                                if (exceptionDuringTask != null) {
                                    ctx.endTask(res, exceptionDuringTask);
                                }
                                else {
                                    ctx.continueWith(res);
                                }
                            }
                        }
                    };
                    this._then.executeFrom(ctx, coreTaskContext._result, greycat.plugin.SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
                };
                CF_DoWhile.prototype.children = function () {
                    var children_tasks = new Array(1);
                    children_tasks[0] = this._then;
                    return children_tasks;
                };
                CF_DoWhile.prototype.cf_serialize = function (builder, dagIDS) {
                    if (this._conditionalScript == null) {
                        throw new Error("Closure is not serializable, please use Script version instead!");
                    }
                    builder.append(greycat.internal.task.CoreActionNames.DO_WHILE);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    var castedAction = this._then;
                    var castedActionHash = castedAction.hashCode();
                    if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                        builder.append(greycat.Constants.SUB_TASK_OPEN);
                        castedAction.serialize(builder, dagIDS);
                        builder.append(greycat.Constants.SUB_TASK_CLOSE);
                    }
                    else {
                        builder.append("" + dagIDS.get(castedActionHash));
                    }
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    greycat.internal.task.TaskHelper.serializeString(this._conditionalScript, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return CF_DoWhile;
            }(greycat.internal.task.CF_Action));
            task_1.CF_DoWhile = CF_DoWhile;
            var CF_ForEach = (function (_super) {
                __extends(CF_ForEach, _super);
                function CF_ForEach(p_subTask) {
                    var _this = _super.call(this) || this;
                    _this._subTask = p_subTask;
                    return _this;
                }
                CF_ForEach.prototype.eval = function (ctx) {
                    var selfPointer = this;
                    var previousResult = ctx.result();
                    if (previousResult == null) {
                        ctx.continueTask();
                    }
                    else {
                        var it_1 = previousResult.iterator();
                        var recursiveAction_1 = new Array(1);
                        recursiveAction_1[0] = function (res) {
                            {
                                var exceptionDuringTask = null;
                                if (res != null) {
                                    if (res.output() != null) {
                                        ctx.append(res.output());
                                    }
                                    if (res.exception() != null) {
                                        exceptionDuringTask = res.exception();
                                    }
                                    res.free();
                                }
                                var nextResult_1 = it_1.nextWithIndex();
                                if (nextResult_1 == null || exceptionDuringTask != null) {
                                    if (exceptionDuringTask != null) {
                                        ctx.endTask(null, exceptionDuringTask);
                                    }
                                    else {
                                        ctx.continueTask();
                                    }
                                }
                                else {
                                    selfPointer._subTask.executeFromUsing(ctx, ctx.wrap(nextResult_1.right()), greycat.plugin.SchedulerAffinity.SAME_THREAD, function (result) {
                                        {
                                            result.defineVariable("i", nextResult_1.left());
                                        }
                                    }, recursiveAction_1[0]);
                                }
                            }
                        };
                        var nextRes_1 = it_1.nextWithIndex();
                        if (nextRes_1 != null) {
                            this._subTask.executeFromUsing(ctx, ctx.wrap(nextRes_1.right()), greycat.plugin.SchedulerAffinity.SAME_THREAD, function (result) {
                                {
                                    result.defineVariable("i", nextRes_1.left());
                                }
                            }, recursiveAction_1[0]);
                        }
                        else {
                            ctx.continueTask();
                        }
                    }
                };
                CF_ForEach.prototype.children = function () {
                    var children_tasks = new Array(1);
                    children_tasks[0] = this._subTask;
                    return children_tasks;
                };
                CF_ForEach.prototype.cf_serialize = function (builder, dagIDS) {
                    builder.append(greycat.internal.task.CoreActionNames.FOR_EACH);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    var castedAction = this._subTask;
                    var castedActionHash = castedAction.hashCode();
                    if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                        builder.append(greycat.Constants.SUB_TASK_OPEN);
                        castedAction.serialize(builder, dagIDS);
                        builder.append(greycat.Constants.SUB_TASK_CLOSE);
                    }
                    else {
                        builder.append("" + dagIDS.get(castedActionHash));
                    }
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return CF_ForEach;
            }(greycat.internal.task.CF_Action));
            task_1.CF_ForEach = CF_ForEach;
            var CF_ForEachPar = (function (_super) {
                __extends(CF_ForEachPar, _super);
                function CF_ForEachPar(p_subTask) {
                    var _this = _super.call(this) || this;
                    _this._subTask = p_subTask;
                    return _this;
                }
                CF_ForEachPar.prototype.eval = function (ctx) {
                    var _this = this;
                    var previousResult = ctx.result();
                    var it = previousResult.iterator();
                    var previousSize = previousResult.size();
                    if (previousSize == -1) {
                        throw new Error("Foreach on non array structure are not supported yet!");
                    }
                    var waiter = ctx.graph().newCounter(previousSize);
                    var dequeueJob = new Array(1);
                    var exceptionDuringTask = new Array(1);
                    exceptionDuringTask[0] = null;
                    dequeueJob[0] = function () {
                        {
                            var loop_1 = it.nextWithIndex();
                            if (loop_1 != null) {
                                _this._subTask.executeFromUsing(ctx, ctx.wrap(loop_1.right()), greycat.plugin.SchedulerAffinity.ANY_LOCAL_THREAD, function (result) {
                                    {
                                        result.defineVariable("i", loop_1.left());
                                    }
                                }, function (result) {
                                    {
                                        if (result != null) {
                                            if (result.output() != null) {
                                                ctx.append(result.output());
                                            }
                                            if (result.exception() != null) {
                                                exceptionDuringTask[0] = result.exception();
                                            }
                                            result.free();
                                        }
                                        waiter.count();
                                        dequeueJob[0]();
                                    }
                                });
                            }
                        }
                    };
                    var nbThread = ctx.graph().scheduler().workers();
                    for (var i = 0; i < nbThread; i++) {
                        dequeueJob[0]();
                    }
                    waiter.then(function () {
                        {
                            if (exceptionDuringTask[0] != null) {
                                ctx.endTask(null, exceptionDuringTask[0]);
                            }
                            else {
                                ctx.continueTask();
                            }
                        }
                    });
                };
                CF_ForEachPar.prototype.children = function () {
                    var children_tasks = new Array(1);
                    children_tasks[0] = this._subTask;
                    return children_tasks;
                };
                CF_ForEachPar.prototype.cf_serialize = function (builder, dagIDS) {
                    builder.append(greycat.internal.task.CoreActionNames.FOR_EACH_PAR);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    var castedAction = this._subTask;
                    var castedActionHash = castedAction.hashCode();
                    if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                        builder.append(greycat.Constants.SUB_TASK_OPEN);
                        castedAction.serialize(builder, dagIDS);
                        builder.append(greycat.Constants.SUB_TASK_CLOSE);
                    }
                    else {
                        builder.append("" + dagIDS.get(castedActionHash));
                    }
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return CF_ForEachPar;
            }(greycat.internal.task.CF_Action));
            task_1.CF_ForEachPar = CF_ForEachPar;
            var CF_IfThen = (function (_super) {
                __extends(CF_IfThen, _super);
                function CF_IfThen(cond, action, conditionalScript) {
                    var _this = _super.call(this) || this;
                    if (cond == null) {
                        throw new Error("condition should not be null");
                    }
                    if (action == null) {
                        throw new Error("subTask should not be null");
                    }
                    _this._conditionalScript = conditionalScript;
                    _this._condition = cond;
                    _this._action = action;
                    return _this;
                }
                CF_IfThen.prototype.eval = function (ctx) {
                    if (this._condition(ctx)) {
                        this._action.executeFrom(ctx, ctx.result(), greycat.plugin.SchedulerAffinity.SAME_THREAD, function (res) {
                            {
                                var exceptionDuringTask = null;
                                if (res != null) {
                                    if (res.output() != null) {
                                        ctx.append(res.output());
                                    }
                                    if (res.exception() != null) {
                                        exceptionDuringTask = res.exception();
                                    }
                                }
                                if (exceptionDuringTask != null) {
                                    ctx.endTask(res, exceptionDuringTask);
                                }
                                else {
                                    ctx.continueWith(res);
                                }
                            }
                        });
                    }
                    else {
                        ctx.continueTask();
                    }
                };
                CF_IfThen.prototype.children = function () {
                    var children_tasks = new Array(1);
                    children_tasks[0] = this._action;
                    return children_tasks;
                };
                CF_IfThen.prototype.cf_serialize = function (builder, dagIDS) {
                    if (this._conditionalScript == null) {
                        throw new Error("Closure is not serializable, please use Script version instead!");
                    }
                    builder.append(greycat.internal.task.CoreActionNames.IF_THEN);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._conditionalScript, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    var castedAction = this._action;
                    var castedActionHash = castedAction.hashCode();
                    if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                        builder.append(greycat.Constants.SUB_TASK_OPEN);
                        castedAction.serialize(builder, dagIDS);
                        builder.append(greycat.Constants.SUB_TASK_CLOSE);
                    }
                    else {
                        builder.append("" + dagIDS.get(castedActionHash));
                    }
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return CF_IfThen;
            }(greycat.internal.task.CF_Action));
            task_1.CF_IfThen = CF_IfThen;
            var CF_IfThenElse = (function (_super) {
                __extends(CF_IfThenElse, _super);
                function CF_IfThenElse(cond, p_thenSub, p_elseSub, conditionalScript) {
                    var _this = _super.call(this) || this;
                    if (cond == null) {
                        throw new Error("condition should not be null");
                    }
                    if (p_thenSub == null) {
                        throw new Error("thenSub should not be null");
                    }
                    if (p_elseSub == null) {
                        throw new Error("elseSub should not be null");
                    }
                    _this._conditionalScript = conditionalScript;
                    _this._condition = cond;
                    _this._thenSub = p_thenSub;
                    _this._elseSub = p_elseSub;
                    return _this;
                }
                CF_IfThenElse.prototype.eval = function (ctx) {
                    var selectedNextTask;
                    if (this._condition(ctx)) {
                        selectedNextTask = this._thenSub;
                    }
                    else {
                        selectedNextTask = this._elseSub;
                    }
                    selectedNextTask.executeFrom(ctx, ctx.result(), greycat.plugin.SchedulerAffinity.SAME_THREAD, function (res) {
                        {
                            var exceptionDuringTask = null;
                            if (res != null) {
                                if (res.output() != null) {
                                    ctx.append(res.output());
                                }
                                if (res.exception() != null) {
                                    exceptionDuringTask = res.exception();
                                }
                            }
                            if (exceptionDuringTask != null) {
                                ctx.endTask(res, exceptionDuringTask);
                            }
                            else {
                                ctx.continueWith(res);
                            }
                        }
                    });
                };
                CF_IfThenElse.prototype.children = function () {
                    var children_tasks = new Array(2);
                    children_tasks[0] = this._thenSub;
                    children_tasks[1] = this._elseSub;
                    return children_tasks;
                };
                CF_IfThenElse.prototype.cf_serialize = function (builder, dagIDS) {
                    if (this._conditionalScript == null) {
                        throw new Error("Closure is not serializable, please use Script version instead!");
                    }
                    builder.append(greycat.internal.task.CoreActionNames.IF_THEN_ELSE);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._conditionalScript, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    var castedSubThen = this._thenSub;
                    var castedSubThenHash = castedSubThen.hashCode();
                    if (dagIDS == null || !dagIDS.containsKey(castedSubThenHash)) {
                        builder.append(greycat.Constants.SUB_TASK_OPEN);
                        castedSubThen.serialize(builder, dagIDS);
                        builder.append(greycat.Constants.SUB_TASK_CLOSE);
                    }
                    else {
                        builder.append("" + dagIDS.get(castedSubThenHash));
                    }
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    var castedSubElse = this._elseSub;
                    var castedSubElseHash = castedSubElse.hashCode();
                    if (dagIDS == null || !dagIDS.containsKey(castedSubElseHash)) {
                        builder.append(greycat.Constants.SUB_TASK_OPEN);
                        castedSubElse.serialize(builder, dagIDS);
                        builder.append(greycat.Constants.SUB_TASK_CLOSE);
                    }
                    else {
                        builder.append("" + dagIDS.get(castedSubElseHash));
                    }
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return CF_IfThenElse;
            }(greycat.internal.task.CF_Action));
            task_1.CF_IfThenElse = CF_IfThenElse;
            var CF_Loop = (function (_super) {
                __extends(CF_Loop, _super);
                function CF_Loop(p_lower, p_upper, p_subTask) {
                    var _this = _super.call(this) || this;
                    _this._subTask = p_subTask;
                    _this._lower = p_lower;
                    _this._upper = p_upper;
                    return _this;
                }
                CF_Loop.prototype.eval = function (ctx) {
                    var lowerString = ctx.template(this._lower);
                    var upperString = ctx.template(this._upper);
                    var lower = parseFloat(ctx.template(lowerString));
                    var upper = parseFloat(ctx.template(upperString));
                    var previous = ctx.result();
                    var selfPointer = this;
                    var cursor = new java.util.concurrent.atomic.AtomicInteger(lower);
                    if ((upper - lower) >= 0) {
                        var recursiveAction_2 = new Array(1);
                        recursiveAction_2[0] = function (res) {
                            {
                                var current_1 = cursor.getAndIncrement();
                                var exceptionDuringTask = null;
                                if (res != null) {
                                    if (res.output() != null) {
                                        ctx.append(res.output());
                                    }
                                    if (res.exception() != null) {
                                        exceptionDuringTask = res.exception();
                                    }
                                    res.free();
                                }
                                if (current_1 > upper || exceptionDuringTask != null) {
                                    if (exceptionDuringTask != null) {
                                        ctx.endTask(null, exceptionDuringTask);
                                    }
                                    else {
                                        ctx.continueTask();
                                    }
                                }
                                else {
                                    selfPointer._subTask.executeFromUsing(ctx, previous, greycat.plugin.SchedulerAffinity.SAME_THREAD, function (result) {
                                        {
                                            result.defineVariable("i", current_1);
                                        }
                                    }, recursiveAction_2[0]);
                                }
                            }
                        };
                        this._subTask.executeFromUsing(ctx, previous, greycat.plugin.SchedulerAffinity.SAME_THREAD, function (result) {
                            {
                                result.defineVariable("i", cursor.getAndIncrement());
                            }
                        }, recursiveAction_2[0]);
                    }
                    else {
                        ctx.continueTask();
                    }
                };
                CF_Loop.prototype.children = function () {
                    var children_tasks = new Array(1);
                    children_tasks[0] = this._subTask;
                    return children_tasks;
                };
                CF_Loop.prototype.cf_serialize = function (builder, dagIDS) {
                    builder.append(greycat.internal.task.CoreActionNames.LOOP);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._lower, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    greycat.internal.task.TaskHelper.serializeString(this._upper, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    var castedAction = this._subTask;
                    var castedActionHash = castedAction.hashCode();
                    if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                        builder.append(greycat.Constants.SUB_TASK_OPEN);
                        castedAction.serialize(builder, dagIDS);
                        builder.append(greycat.Constants.SUB_TASK_CLOSE);
                    }
                    else {
                        builder.append("" + dagIDS.get(castedActionHash));
                    }
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return CF_Loop;
            }(greycat.internal.task.CF_Action));
            task_1.CF_Loop = CF_Loop;
            var CF_LoopPar = (function (_super) {
                __extends(CF_LoopPar, _super);
                function CF_LoopPar(p_lower, p_upper, p_subTask) {
                    var _this = _super.call(this) || this;
                    _this._subTask = p_subTask;
                    _this._lower = p_lower;
                    _this._upper = p_upper;
                    return _this;
                }
                CF_LoopPar.prototype.eval = function (ctx) {
                    var lowerString = ctx.template(this._lower);
                    var upperString = ctx.template(this._upper);
                    var lower = parseFloat(ctx.template(lowerString));
                    var upper = parseFloat(ctx.template(upperString));
                    var previous = ctx.result();
                    var exceptionDuringTask = new Array(1);
                    exceptionDuringTask[0] = null;
                    if ((upper - lower) > 0) {
                        var waiter_1 = ctx.graph().newCounter((upper - lower) + 1);
                        var _loop_10 = function (i) {
                            var finalI = i;
                            this_5._subTask.executeFromUsing(ctx, previous, greycat.plugin.SchedulerAffinity.ANY_LOCAL_THREAD, function (result) {
                                {
                                    result.defineVariable("i", finalI);
                                }
                            }, function (result) {
                                {
                                    if (result != null) {
                                        if (result.output() != null) {
                                            ctx.append(result.output());
                                        }
                                        if (result.exception() != null) {
                                            exceptionDuringTask[0] = result.exception();
                                        }
                                        result.free();
                                    }
                                    waiter_1.count();
                                }
                            });
                        };
                        var this_5 = this;
                        for (var i = lower; i <= upper; i++) {
                            _loop_10(i);
                        }
                        waiter_1.then(function () {
                            {
                                if (exceptionDuringTask[0] != null) {
                                    ctx.endTask(null, exceptionDuringTask[0]);
                                }
                                else {
                                    ctx.continueTask();
                                }
                            }
                        });
                    }
                    else {
                        ctx.continueTask();
                    }
                };
                CF_LoopPar.prototype.children = function () {
                    var children_tasks = new Array(1);
                    children_tasks[0] = this._subTask;
                    return children_tasks;
                };
                CF_LoopPar.prototype.cf_serialize = function (builder, dagIDS) {
                    builder.append(greycat.internal.task.CoreActionNames.LOOP_PAR);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._lower, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    greycat.internal.task.TaskHelper.serializeString(this._upper, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    var castedAction = this._subTask;
                    var castedActionHash = castedAction.hashCode();
                    if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                        builder.append(greycat.Constants.SUB_TASK_OPEN);
                        castedAction.serialize(builder, dagIDS);
                        builder.append(greycat.Constants.SUB_TASK_CLOSE);
                    }
                    else {
                        builder.append("" + dagIDS.get(castedActionHash));
                    }
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return CF_LoopPar;
            }(greycat.internal.task.CF_Action));
            task_1.CF_LoopPar = CF_LoopPar;
            var CF_Map = (function (_super) {
                __extends(CF_Map, _super);
                function CF_Map(p_subTask) {
                    var _this = _super.call(this) || this;
                    _this._subTask = p_subTask;
                    return _this;
                }
                CF_Map.prototype.eval = function (ctx) {
                    var _this = this;
                    var selfPointer = this;
                    var previousResult = ctx.result();
                    if (previousResult == null) {
                        ctx.continueTask();
                    }
                    else {
                        var it_2 = previousResult.iterator();
                        var finalResult_1 = ctx.newResult();
                        finalResult_1.allocate(previousResult.size());
                        var recursiveAction_3 = new Array(1);
                        var loopRes_1 = new Array(1);
                        recursiveAction_3[0] = function (res) {
                            {
                                var exceptionDuringTask = null;
                                if (res != null) {
                                    finalResult_1.add(res);
                                    if (res.output() != null) {
                                        ctx.append(res.output());
                                    }
                                    if (res.exception() != null) {
                                        exceptionDuringTask = res.exception();
                                    }
                                }
                                loopRes_1[0].free();
                                var nextResult_2 = it_2.nextWithIndex();
                                if (nextResult_2 != null) {
                                    loopRes_1[0] = ctx.wrap(nextResult_2.right());
                                }
                                else {
                                    loopRes_1[0] = null;
                                }
                                if (nextResult_2 == null || exceptionDuringTask != null) {
                                    if (exceptionDuringTask != null) {
                                        ctx.endTask(finalResult_1, exceptionDuringTask);
                                    }
                                    else {
                                        ctx.continueWith(finalResult_1);
                                    }
                                }
                                else {
                                    selfPointer._subTask.executeFromUsing(ctx, loopRes_1[0], greycat.plugin.SchedulerAffinity.SAME_THREAD, function (result) {
                                        {
                                            result.defineVariable("i", nextResult_2.left());
                                        }
                                    }, recursiveAction_3[0]);
                                }
                            }
                        };
                        var nextRes_2 = it_2.nextWithIndex();
                        if (nextRes_2 != null) {
                            loopRes_1[0] = ctx.wrap(nextRes_2.right());
                            ctx.graph().scheduler().dispatch(greycat.plugin.SchedulerAffinity.SAME_THREAD, function () {
                                {
                                    _this._subTask.executeFromUsing(ctx, loopRes_1[0], greycat.plugin.SchedulerAffinity.SAME_THREAD, function (result) {
                                        {
                                            result.defineVariable("i", nextRes_2.left());
                                        }
                                    }, recursiveAction_3[0]);
                                }
                            });
                        }
                        else {
                            ctx.continueWith(finalResult_1);
                        }
                    }
                };
                CF_Map.prototype.children = function () {
                    var children_tasks = new Array(1);
                    children_tasks[0] = this._subTask;
                    return children_tasks;
                };
                CF_Map.prototype.cf_serialize = function (builder, dagIDS) {
                    builder.append(greycat.internal.task.CoreActionNames.MAP);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    var castedAction = this._subTask;
                    var castedActionHash = castedAction.hashCode();
                    if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                        builder.append(greycat.Constants.SUB_TASK_OPEN);
                        castedAction.serialize(builder, dagIDS);
                        builder.append(greycat.Constants.SUB_TASK_CLOSE);
                    }
                    else {
                        builder.append("" + dagIDS.get(castedActionHash));
                    }
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return CF_Map;
            }(greycat.internal.task.CF_Action));
            task_1.CF_Map = CF_Map;
            var CF_MapPar = (function (_super) {
                __extends(CF_MapPar, _super);
                function CF_MapPar(p_subTask) {
                    var _this = _super.call(this) || this;
                    _this._subTask = p_subTask;
                    return _this;
                }
                CF_MapPar.prototype.eval = function (ctx) {
                    var _this = this;
                    var previousResult = ctx.result();
                    var finalResult = ctx.wrap(null);
                    var it = previousResult.iterator();
                    var previousSize = previousResult.size();
                    if (previousSize == -1) {
                        throw new Error("Foreach on non array structure are not supported yet!");
                    }
                    finalResult.allocate(previousSize);
                    var waiter = ctx.graph().newCounter(previousSize);
                    var dequeueJob = new Array(1);
                    var exceptionDuringTask = new Array(1);
                    exceptionDuringTask[0] = null;
                    dequeueJob[0] = function () {
                        {
                            var loop_2 = it.nextWithIndex();
                            if (loop_2 != null) {
                                _this._subTask.executeFromUsing(ctx, ctx.wrap(loop_2.right()), greycat.plugin.SchedulerAffinity.ANY_LOCAL_THREAD, function (result) {
                                    {
                                        result.defineVariable("i", loop_2.left());
                                    }
                                }, function (result) {
                                    {
                                        if (result != null) {
                                            finalResult.add(result);
                                            if (result.output() != null) {
                                                ctx.append(result.output());
                                            }
                                            if (result.exception() != null) {
                                                exceptionDuringTask[0] = result.exception();
                                            }
                                        }
                                        waiter.count();
                                        dequeueJob[0]();
                                    }
                                });
                            }
                        }
                    };
                    var nbThread = ctx.graph().scheduler().workers();
                    for (var i = 0; i < nbThread; i++) {
                        dequeueJob[0]();
                    }
                    waiter.then(function () {
                        {
                            if (exceptionDuringTask[0] != null) {
                                ctx.endTask(finalResult, exceptionDuringTask[0]);
                            }
                            else {
                                ctx.continueWith(finalResult);
                            }
                        }
                    });
                };
                CF_MapPar.prototype.children = function () {
                    var children_tasks = new Array(1);
                    children_tasks[0] = this._subTask;
                    return children_tasks;
                };
                CF_MapPar.prototype.cf_serialize = function (builder, dagIDS) {
                    builder.append(greycat.internal.task.CoreActionNames.MAP_PAR);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    var castedAction = this._subTask;
                    var castedActionHash = castedAction.hashCode();
                    if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                        builder.append(greycat.Constants.SUB_TASK_OPEN);
                        castedAction.serialize(builder, dagIDS);
                        builder.append(greycat.Constants.SUB_TASK_CLOSE);
                    }
                    else {
                        builder.append("" + dagIDS.get(castedActionHash));
                    }
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return CF_MapPar;
            }(greycat.internal.task.CF_Action));
            task_1.CF_MapPar = CF_MapPar;
            var CF_Pipe = (function (_super) {
                __extends(CF_Pipe, _super);
                function CF_Pipe() {
                    var p_subTasks = [];
                    for (var _i = 0; _i < arguments.length; _i++) {
                        p_subTasks[_i] = arguments[_i];
                    }
                    var _this = _super.call(this) || this;
                    _this._subTasks = p_subTasks;
                    return _this;
                }
                CF_Pipe.prototype.eval = function (ctx) {
                    var _this = this;
                    var previous = ctx.result();
                    var cursor = new java.util.concurrent.atomic.AtomicInteger(0);
                    var tasksSize = this._subTasks.length;
                    var next;
                    if (tasksSize > 1) {
                        next = ctx.newResult();
                        next.allocate(tasksSize);
                    }
                    else {
                        next = null;
                    }
                    var loopcb = new Array(1);
                    loopcb[0] = function (result) {
                        {
                            var exceptionDuringTask = null;
                            var current_2 = cursor.getAndIncrement();
                            if (result != null) {
                                if (tasksSize > 1) {
                                    next.add(result);
                                }
                                if (result.output() != null) {
                                    ctx.append(result.output());
                                }
                                if (result.exception() != null) {
                                    exceptionDuringTask = result.exception();
                                }
                            }
                            if (current_2 < tasksSize && exceptionDuringTask == null) {
                                _this._subTasks[current_2].executeFrom(ctx, previous, greycat.plugin.SchedulerAffinity.SAME_THREAD, loopcb[0]);
                            }
                            else {
                                var nextResult = void 0;
                                if (tasksSize > 1) {
                                    nextResult = next;
                                }
                                else {
                                    nextResult = result;
                                }
                                if (exceptionDuringTask != null) {
                                    ctx.endTask(nextResult, exceptionDuringTask);
                                }
                                else {
                                    ctx.continueWith(nextResult);
                                }
                            }
                        }
                    };
                    var current = cursor.getAndIncrement();
                    if (current < tasksSize) {
                        this._subTasks[current].executeFrom(ctx, previous, greycat.plugin.SchedulerAffinity.SAME_THREAD, loopcb[0]);
                    }
                    else {
                        ctx.continueWith(next);
                    }
                };
                CF_Pipe.prototype.children = function () {
                    return this._subTasks;
                };
                CF_Pipe.prototype.cf_serialize = function (builder, dagIDS) {
                    builder.append(greycat.internal.task.CoreActionNames.PIPE);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    for (var i = 0; i < this._subTasks.length; i++) {
                        if (i != 0) {
                            builder.append(greycat.Constants.TASK_PARAM_SEP);
                        }
                        var castedAction = this._subTasks[i];
                        var castedActionHash = castedAction.hashCode();
                        if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                            builder.append(greycat.Constants.SUB_TASK_OPEN);
                            castedAction.serialize(builder, dagIDS);
                            builder.append(greycat.Constants.SUB_TASK_CLOSE);
                        }
                        else {
                            builder.append("" + dagIDS.get(castedActionHash));
                        }
                    }
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return CF_Pipe;
            }(greycat.internal.task.CF_Action));
            task_1.CF_Pipe = CF_Pipe;
            var CF_PipePar = (function (_super) {
                __extends(CF_PipePar, _super);
                function CF_PipePar() {
                    var p_subTasks = [];
                    for (var _i = 0; _i < arguments.length; _i++) {
                        p_subTasks[_i] = arguments[_i];
                    }
                    var _this = _super.call(this) || this;
                    _this._subTasks = p_subTasks;
                    return _this;
                }
                CF_PipePar.prototype.eval = function (ctx) {
                    var previous = ctx.result();
                    var subTasksSize = this._subTasks.length;
                    var next = ctx.newResult();
                    next.allocate(subTasksSize);
                    var waiter = ctx.graph().newCounter(subTasksSize);
                    var exceptionDuringTask = new Array(1);
                    exceptionDuringTask[0] = null;
                    var _loop_11 = function (i) {
                        var finalI = i;
                        this_6._subTasks[i].executeFrom(ctx, previous, greycat.plugin.SchedulerAffinity.ANY_LOCAL_THREAD, function (subTaskResult) {
                            {
                                if (subTaskResult != null) {
                                    if (subTaskResult.output() != null) {
                                        ctx.append(subTaskResult.output());
                                    }
                                    if (subTaskResult.exception() != null) {
                                        exceptionDuringTask[0] = subTaskResult.exception();
                                    }
                                }
                                next.set(finalI, subTaskResult);
                                waiter.count();
                            }
                        });
                    };
                    var this_6 = this;
                    for (var i = 0; i < subTasksSize; i++) {
                        _loop_11(i);
                    }
                    waiter.then(function () {
                        {
                            var endResult = next;
                            if (subTasksSize == 1) {
                                endResult = next.get(0);
                            }
                            if (exceptionDuringTask[0] != null) {
                                ctx.endTask(endResult, exceptionDuringTask[0]);
                            }
                            else {
                                ctx.continueWith(endResult);
                            }
                        }
                    });
                };
                CF_PipePar.prototype.children = function () {
                    return this._subTasks;
                };
                CF_PipePar.prototype.cf_serialize = function (builder, dagIDS) {
                    builder.append(greycat.internal.task.CoreActionNames.PIPE_PAR);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    for (var i = 0; i < this._subTasks.length; i++) {
                        if (i != 0) {
                            builder.append(greycat.Constants.TASK_PARAM_SEP);
                        }
                        var castedAction = this._subTasks[i];
                        var castedActionHash = castedAction.hashCode();
                        if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                            builder.append(greycat.Constants.SUB_TASK_OPEN);
                            castedAction.serialize(builder, dagIDS);
                            builder.append(greycat.Constants.SUB_TASK_CLOSE);
                        }
                        else {
                            builder.append("" + dagIDS.get(castedActionHash));
                        }
                    }
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return CF_PipePar;
            }(greycat.internal.task.CF_Action));
            task_1.CF_PipePar = CF_PipePar;
            var CF_PipeTo = (function (_super) {
                __extends(CF_PipeTo, _super);
                function CF_PipeTo(p_subTask) {
                    var p_targets = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        p_targets[_i - 1] = arguments[_i];
                    }
                    var _this = _super.call(this) || this;
                    if (p_subTask == null) {
                        throw new Error("subTask should not be null");
                    }
                    _this._subTask = p_subTask;
                    _this._targets = p_targets;
                    return _this;
                }
                CF_PipeTo.prototype.eval = function (ctx) {
                    var _this = this;
                    var previous = ctx.result();
                    this._subTask.executeFrom(ctx, previous, greycat.plugin.SchedulerAffinity.SAME_THREAD, function (result) {
                        {
                            var exceptionDuringTask = null;
                            if (result != null) {
                                if (result.output() != null) {
                                    ctx.append(result.output());
                                }
                                if (result.exception() != null) {
                                    exceptionDuringTask = result.exception();
                                }
                                if (_this._targets != null && _this._targets.length > 0) {
                                    for (var i = 0; i < _this._targets.length; i++) {
                                        ctx.setVariable(_this._targets[i], result);
                                    }
                                }
                                else {
                                    result.free();
                                }
                            }
                            if (exceptionDuringTask != null) {
                                ctx.endTask(previous, exceptionDuringTask);
                            }
                            else {
                                ctx.continueWith(previous);
                            }
                        }
                    });
                };
                CF_PipeTo.prototype.children = function () {
                    var children_tasks = new Array(1);
                    children_tasks[0] = this._subTask;
                    return children_tasks;
                };
                CF_PipeTo.prototype.cf_serialize = function (builder, dagIDS) {
                    builder.append(greycat.internal.task.CoreActionNames.PIPE_TO);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    var castedAction = this._subTask;
                    var castedActionHash = castedAction.hashCode();
                    if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                        builder.append(greycat.Constants.SUB_TASK_OPEN);
                        castedAction.serialize(builder, dagIDS);
                        builder.append(greycat.Constants.SUB_TASK_CLOSE);
                    }
                    else {
                        builder.append("" + dagIDS.get(castedActionHash));
                    }
                    if (this._targets != null && this._targets.length > 0) {
                        builder.append(greycat.Constants.TASK_PARAM_SEP);
                        greycat.internal.task.TaskHelper.serializeStringParams(this._targets, builder);
                    }
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return CF_PipeTo;
            }(greycat.internal.task.CF_Action));
            task_1.CF_PipeTo = CF_PipeTo;
            var CF_ThenDo = (function () {
                function CF_ThenDo(p_wrapped) {
                    if (p_wrapped == null) {
                        throw new Error("action should not be null");
                    }
                    this._wrapped = p_wrapped;
                }
                CF_ThenDo.prototype.eval = function (ctx) {
                    this._wrapped(ctx);
                };
                CF_ThenDo.prototype.toString = function () {
                    return "then()";
                };
                CF_ThenDo.prototype.serialize = function (builder) {
                    throw new Error("Not managed yet!");
                };
                return CF_ThenDo;
            }());
            task_1.CF_ThenDo = CF_ThenDo;
            var CF_WhileDo = (function (_super) {
                __extends(CF_WhileDo, _super);
                function CF_WhileDo(p_cond, p_then, conditionalScript) {
                    var _this = _super.call(this) || this;
                    _this._cond = p_cond;
                    _this._then = p_then;
                    _this._conditionalScript = conditionalScript;
                    return _this;
                }
                CF_WhileDo.prototype.eval = function (ctx) {
                    var _this = this;
                    var coreTaskContext = ctx;
                    var selfPointer = this;
                    var recursiveAction = new Array(1);
                    recursiveAction[0] = function (res) {
                        {
                            var foundException = null;
                            var previous = coreTaskContext._result;
                            coreTaskContext._result = res;
                            if (res != null) {
                                if (res.output() != null) {
                                    ctx.append(res.output());
                                }
                                if (res.exception() != null) {
                                    foundException = res.exception();
                                }
                            }
                            if (_this._cond(ctx) && foundException == null) {
                                if (previous != null) {
                                    previous.free();
                                }
                                selfPointer._then.executeFrom(ctx, coreTaskContext._result, greycat.plugin.SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
                            }
                            else {
                                if (previous != null) {
                                    previous.free();
                                }
                                if (foundException != null) {
                                    ctx.endTask(res, foundException);
                                }
                                else {
                                    ctx.continueWith(res);
                                }
                            }
                        }
                    };
                    if (this._cond(ctx)) {
                        this._then.executeFrom(ctx, coreTaskContext._result, greycat.plugin.SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
                    }
                    else {
                        ctx.continueTask();
                    }
                };
                CF_WhileDo.prototype.children = function () {
                    var children_tasks = new Array(1);
                    children_tasks[0] = this._then;
                    return children_tasks;
                };
                CF_WhileDo.prototype.cf_serialize = function (builder, dagIDS) {
                    if (this._conditionalScript == null) {
                        throw new Error("Closure is not serializable, please use Script version instead!");
                    }
                    builder.append(greycat.internal.task.CoreActionNames.WHILE_DO);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeString(this._conditionalScript, builder, true);
                    builder.append(greycat.Constants.TASK_PARAM_SEP);
                    var castedAction = this._then;
                    var castedActionHash = castedAction.hashCode();
                    if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                        builder.append(greycat.Constants.SUB_TASK_OPEN);
                        castedAction.serialize(builder, dagIDS);
                        builder.append(greycat.Constants.SUB_TASK_CLOSE);
                    }
                    else {
                        builder.append("" + dagIDS.get(castedActionHash));
                    }
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return CF_WhileDo;
            }(greycat.internal.task.CF_Action));
            task_1.CF_WhileDo = CF_WhileDo;
            var CoreActionDeclaration = (function () {
                function CoreActionDeclaration(name) {
                    this._factory = null;
                    this._params = null;
                    this._description = null;
                    this._name = name;
                }
                CoreActionDeclaration.prototype.factory = function () {
                    return this._factory;
                };
                CoreActionDeclaration.prototype.setFactory = function (factory) {
                    this._factory = factory;
                    return this;
                };
                CoreActionDeclaration.prototype.params = function () {
                    return this._params;
                };
                CoreActionDeclaration.prototype.setParams = function () {
                    var params = [];
                    for (var _i = 0; _i < arguments.length; _i++) {
                        params[_i] = arguments[_i];
                    }
                    this._params = Int8Array.from(params);
                    return this;
                };
                CoreActionDeclaration.prototype.description = function () {
                    return this._description;
                };
                CoreActionDeclaration.prototype.setDescription = function (description) {
                    this._description = description;
                    return this;
                };
                CoreActionDeclaration.prototype.name = function () {
                    return this._name;
                };
                return CoreActionDeclaration;
            }());
            task_1.CoreActionDeclaration = CoreActionDeclaration;
            var CoreActionNames = (function () {
                function CoreActionNames() {
                }
                return CoreActionNames;
            }());
            CoreActionNames.ADD_VAR_TO_RELATION = "addVarToRelation";
            CoreActionNames.REMOVE_VAR_TO_RELATION = "removeVarToRelation";
            CoreActionNames.ADD_TO_GLOBAL_INDEX = "addToGlobalIndex";
            CoreActionNames.ADD_TO_GLOBAL_TIMED_INDEX = "addToGlobalTimedIndex";
            CoreActionNames.ADD_TO_VAR = "addToVar";
            CoreActionNames.ATTRIBUTES = "attributes";
            CoreActionNames.ATTRIBUTES_WITH_TYPE = "attributesWithType";
            CoreActionNames.CLEAR_RESULT = "clearResult";
            CoreActionNames.CREATE_NODE = "createNode";
            CoreActionNames.CREATE_TYPED_NODE = "createTypedNode";
            CoreActionNames.DECLARE_GLOBAL_VAR = "declareGlobalVar";
            CoreActionNames.DECLARE_VAR = "declareVar";
            CoreActionNames.FLIP_VAR = "flipVar";
            CoreActionNames.DEFINE_AS_GLOBAL_VAR = "defineAsGlobalVar";
            CoreActionNames.DEFINE_AS_VAR = "defineAsVar";
            CoreActionNames.EXECUTE_EXPRESSION = "executeExpression";
            CoreActionNames.INDEX_NAMES = "indexNames";
            CoreActionNames.LOOKUP = "lookup";
            CoreActionNames.LOOKUP_ALL = "lookupAll";
            CoreActionNames.LOG = "log";
            CoreActionNames.PRINT = "print";
            CoreActionNames.PRINTLN = "println";
            CoreActionNames.READ_GLOBAL_INDEX = "readGlobalIndex";
            CoreActionNames.GLOBAL_INDEX = "globalIndex";
            CoreActionNames.READ_VAR = "readVar";
            CoreActionNames.REMOVE = "remove";
            CoreActionNames.REMOVE_FROM_GLOBAL_INDEX = "removeFromGlobalIndex";
            CoreActionNames.SAVE = "save";
            CoreActionNames.SCRIPT = "script";
            CoreActionNames.ASYNC_SCRIPT = "asyncScript";
            CoreActionNames.SELECT = "select";
            CoreActionNames.SET_AS_VAR = "setAsVar";
            CoreActionNames.FORCE_ATTRIBUTE = "forceAttribute";
            CoreActionNames.SET_ATTRIBUTE = "setAttribute";
            CoreActionNames.TIME_SENSITIVITY = "timeSensitivity";
            CoreActionNames.TIMEPOINTS = "timepoints";
            CoreActionNames.TRAVEL_IN_TIME = "travelInTime";
            CoreActionNames.TRAVEL_IN_WORLD = "travelInWorld";
            CoreActionNames.WITH = "with";
            CoreActionNames.WITHOUT = "without";
            CoreActionNames.TRAVERSE = "traverse";
            CoreActionNames.ATTRIBUTE = "attribute";
            CoreActionNames.LOOP = "loop";
            CoreActionNames.LOOP_PAR = "loopPar";
            CoreActionNames.FOR_EACH = "forEach";
            CoreActionNames.FOR_EACH_PAR = "forEachPar";
            CoreActionNames.MAP = "map";
            CoreActionNames.MAP_PAR = "mapPar";
            CoreActionNames.PIPE = "pipe";
            CoreActionNames.PIPE_PAR = "pipePar";
            CoreActionNames.PIPE_TO = "pipeTo";
            CoreActionNames.DO_WHILE = "doWhile";
            CoreActionNames.WHILE_DO = "whileDo";
            CoreActionNames.IF_THEN = "ifThen";
            CoreActionNames.IF_THEN_ELSE = "ifThenElse";
            CoreActionNames.ATOMIC = "atomic";
            CoreActionNames.FLAT = "flat";
            task_1.CoreActionNames = CoreActionNames;
            var CoreActionRegistry = (function () {
                function CoreActionRegistry() {
                    this.backend = new java.util.HashMap();
                }
                CoreActionRegistry.prototype.declaration = function (name) {
                    var previous = this.backend.get(name);
                    if (previous == null) {
                        previous = new greycat.internal.task.CoreActionDeclaration(name);
                        this.backend.put(name, previous);
                    }
                    return previous;
                };
                CoreActionRegistry.prototype.declarations = function () {
                    var result = this.backend.values().toArray(new Array(this.backend.size()));
                    return result;
                };
                return CoreActionRegistry;
            }());
            task_1.CoreActionRegistry = CoreActionRegistry;
            var CoreActions = (function () {
                function CoreActions() {
                }
                CoreActions.flat = function () {
                    return new greycat.internal.task.ActionFlat();
                };
                CoreActions.travelInWorld = function (world) {
                    return new greycat.internal.task.ActionTravelInWorld(world);
                };
                CoreActions.travelInTime = function (time) {
                    return new greycat.internal.task.ActionTravelInTime(time);
                };
                CoreActions.inject = function (input) {
                    return new greycat.internal.task.ActionInject(input);
                };
                CoreActions.defineAsGlobalVar = function (name) {
                    return new greycat.internal.task.ActionDefineAsVar(name, true);
                };
                CoreActions.defineAsVar = function (name) {
                    return new greycat.internal.task.ActionDefineAsVar(name, false);
                };
                CoreActions.declareGlobalVar = function (name) {
                    return new greycat.internal.task.ActionDeclareVar(true, name);
                };
                CoreActions.declareVar = function (name) {
                    return new greycat.internal.task.ActionDeclareVar(false, name);
                };
                CoreActions.readVar = function (name) {
                    return new greycat.internal.task.ActionReadVar(name);
                };
                CoreActions.flipVar = function (name) {
                    return new greycat.internal.task.ActionFlipVar(name);
                };
                CoreActions.setAsVar = function (name) {
                    return new greycat.internal.task.ActionSetAsVar(name);
                };
                CoreActions.addToVar = function (name) {
                    return new greycat.internal.task.ActionAddToVar(name);
                };
                CoreActions.setAttribute = function (name, type, value) {
                    return new greycat.internal.task.ActionSetAttribute(name, greycat.Type.typeName(type), value, false);
                };
                CoreActions.timeSensitivity = function (delta, offset) {
                    return new greycat.internal.task.ActionTimeSensitivity(delta, offset);
                };
                CoreActions.forceAttribute = function (name, type, value) {
                    return new greycat.internal.task.ActionSetAttribute(name, greycat.Type.typeName(type), value, true);
                };
                CoreActions.remove = function (name) {
                    return new greycat.internal.task.ActionRemove(name);
                };
                CoreActions.attributes = function () {
                    return new greycat.internal.task.ActionAttributes(null);
                };
                CoreActions.attributesWithTypes = function (filterType) {
                    return new greycat.internal.task.ActionAttributes(greycat.Type.typeName(filterType));
                };
                CoreActions.addVarToRelation = function (relName, varName) {
                    var attributes = [];
                    for (var _i = 2; _i < arguments.length; _i++) {
                        attributes[_i - 2] = arguments[_i];
                    }
                    return new ((_a = greycat.internal.task.ActionAddRemoveVarToRelation).bind.apply(_a, [void 0, true, relName, varName].concat(attributes)))();
                    var _a;
                };
                CoreActions.removeVarFromRelation = function (relName, varFrom) {
                    var attributes = [];
                    for (var _i = 2; _i < arguments.length; _i++) {
                        attributes[_i - 2] = arguments[_i];
                    }
                    return new ((_a = greycat.internal.task.ActionAddRemoveVarToRelation).bind.apply(_a, [void 0, false, relName, varFrom].concat(attributes)))();
                    var _a;
                };
                CoreActions.traverse = function (name) {
                    var params = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        params[_i - 1] = arguments[_i];
                    }
                    return new ((_a = greycat.internal.task.ActionTraverseOrAttribute).bind.apply(_a, [void 0, false, false, name].concat(params)))();
                    var _a;
                };
                CoreActions.attribute = function (name) {
                    var params = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        params[_i - 1] = arguments[_i];
                    }
                    return new ((_a = greycat.internal.task.ActionTraverseOrAttribute).bind.apply(_a, [void 0, true, false, name].concat(params)))();
                    var _a;
                };
                CoreActions.readGlobalIndex = function (indexName) {
                    var query = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        query[_i - 1] = arguments[_i];
                    }
                    return new ((_a = greycat.internal.task.ActionReadGlobalIndex).bind.apply(_a, [void 0, indexName].concat(query)))();
                    var _a;
                };
                CoreActions.globalIndex = function (indexName) {
                    return new greycat.internal.task.ActionGlobalIndex(indexName);
                };
                CoreActions.addToGlobalIndex = function (name) {
                    var attributes = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        attributes[_i - 1] = arguments[_i];
                    }
                    return new ((_a = greycat.internal.task.ActionAddRemoveToGlobalIndex).bind.apply(_a, [void 0, false, false, name].concat(attributes)))();
                    var _a;
                };
                CoreActions.addToGlobalTimedIndex = function (name) {
                    var attributes = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        attributes[_i - 1] = arguments[_i];
                    }
                    return new ((_a = greycat.internal.task.ActionAddRemoveToGlobalIndex).bind.apply(_a, [void 0, false, true, name].concat(attributes)))();
                    var _a;
                };
                CoreActions.removeFromGlobalIndex = function (name) {
                    var attributes = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        attributes[_i - 1] = arguments[_i];
                    }
                    return new ((_a = greycat.internal.task.ActionAddRemoveToGlobalIndex).bind.apply(_a, [void 0, true, false, name].concat(attributes)))();
                    var _a;
                };
                CoreActions.removeFromGlobalTimedIndex = function (name) {
                    var attributes = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        attributes[_i - 1] = arguments[_i];
                    }
                    return new ((_a = greycat.internal.task.ActionAddRemoveToGlobalIndex).bind.apply(_a, [void 0, true, true, name].concat(attributes)))();
                    var _a;
                };
                CoreActions.indexNames = function () {
                    return new greycat.internal.task.ActionIndexNames();
                };
                CoreActions.selectWith = function (name, pattern) {
                    return new greycat.internal.task.ActionWith(name, pattern);
                };
                CoreActions.selectWithout = function (name, pattern) {
                    return new greycat.internal.task.ActionWithout(name, pattern);
                };
                CoreActions.select = function (filterFunction) {
                    return new greycat.internal.task.ActionSelect(null, filterFunction);
                };
                CoreActions.selectObject = function (filterFunction) {
                    return new greycat.internal.task.ActionSelectObject(filterFunction);
                };
                CoreActions.selectScript = function (script) {
                    return new greycat.internal.task.ActionSelect(script, null);
                };
                CoreActions.log = function (value) {
                    return new greycat.internal.task.ActionLog(value);
                };
                CoreActions.print = function (name) {
                    return new greycat.internal.task.ActionPrint(name, false);
                };
                CoreActions.println = function (name) {
                    return new greycat.internal.task.ActionPrint(name, true);
                };
                CoreActions.executeExpression = function (expression) {
                    return new greycat.internal.task.ActionExecuteExpression(expression);
                };
                CoreActions.action = function (name) {
                    var params = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        params[_i - 1] = arguments[_i];
                    }
                    return new ((_a = greycat.internal.task.ActionNamed).bind.apply(_a, [void 0, name].concat(params)))();
                    var _a;
                };
                CoreActions.createNode = function () {
                    return new greycat.internal.task.ActionCreateNode(null);
                };
                CoreActions.createTypedNode = function (type) {
                    return new greycat.internal.task.ActionCreateNode(type);
                };
                CoreActions.save = function () {
                    return new greycat.internal.task.ActionSave();
                };
                CoreActions.script = function (script) {
                    return new greycat.internal.task.ActionScript(script, false);
                };
                CoreActions.asyncScript = function (script) {
                    return new greycat.internal.task.ActionScript(script, true);
                };
                CoreActions.lookup = function (nodeId) {
                    return new greycat.internal.task.ActionLookup(nodeId);
                };
                CoreActions.lookupAll = function (nodeIds) {
                    return new greycat.internal.task.ActionLookupAll(nodeIds);
                };
                CoreActions.timepoints = function (from, to) {
                    return new greycat.internal.task.ActionTimepoints(from, to);
                };
                CoreActions.clearResult = function () {
                    return new greycat.internal.task.ActionClearResult();
                };
                return CoreActions;
            }());
            task_1.CoreActions = CoreActions;
            var CoreTask = (function () {
                function CoreTask() {
                    this.insertCapacity = greycat.Constants.MAP_INITIAL_CAPACITY;
                    this.actions = new Array(this.insertCapacity);
                    this.insertCursor = 0;
                    this._hooks = null;
                }
                CoreTask.prototype.addHook = function (p_hook) {
                    if (this._hooks == null) {
                        this._hooks = new Array(1);
                        this._hooks[0] = p_hook;
                    }
                    else {
                        var new_hooks = new Array(this._hooks.length + 1);
                        java.lang.System.arraycopy(this._hooks, 0, new_hooks, 0, this._hooks.length);
                        new_hooks[this._hooks.length] = p_hook;
                        this._hooks = new_hooks;
                    }
                    return this;
                };
                CoreTask.prototype.then = function (nextAction) {
                    if (this.insertCapacity == this.insertCursor) {
                        var new_actions = new Array(this.insertCapacity * 2);
                        java.lang.System.arraycopy(this.actions, 0, new_actions, 0, this.insertCapacity);
                        this.actions = new_actions;
                        this.insertCapacity = this.insertCapacity * 2;
                    }
                    this.actions[this.insertCursor] = nextAction;
                    this.insertCursor++;
                    return this;
                };
                CoreTask.prototype.thenDo = function (nextActionFunction) {
                    return this.then(new greycat.internal.task.CF_ThenDo(nextActionFunction));
                };
                CoreTask.prototype.doWhile = function (task, cond) {
                    return this.then(new greycat.internal.task.CF_DoWhile(task, cond, null));
                };
                CoreTask.prototype.doWhileScript = function (task, condScript) {
                    return this.then(new greycat.internal.task.CF_DoWhile(task, greycat.internal.task.CoreTask.condFromScript(condScript), condScript));
                };
                CoreTask.prototype.loop = function (from, to, subTask) {
                    return this.then(new greycat.internal.task.CF_Loop(from, to, subTask));
                };
                CoreTask.prototype.loopPar = function (from, to, subTask) {
                    return this.then(new greycat.internal.task.CF_LoopPar(from, to, subTask));
                };
                CoreTask.prototype.forEach = function (subTask) {
                    return this.then(new greycat.internal.task.CF_ForEach(subTask));
                };
                CoreTask.prototype.forEachPar = function (subTask) {
                    return this.then(new greycat.internal.task.CF_ForEachPar(subTask));
                };
                CoreTask.prototype.map = function (subTask) {
                    return this.then(new greycat.internal.task.CF_Map(subTask));
                };
                CoreTask.prototype.mapPar = function (subTask) {
                    return this.then(new greycat.internal.task.CF_MapPar(subTask));
                };
                CoreTask.prototype.ifThen = function (cond, then) {
                    return this.then(new greycat.internal.task.CF_IfThen(cond, then, null));
                };
                CoreTask.prototype.ifThenScript = function (condScript, then) {
                    return this.then(new greycat.internal.task.CF_IfThen(greycat.internal.task.CoreTask.condFromScript(condScript), then, condScript));
                };
                CoreTask.prototype.ifThenElse = function (cond, thenSub, elseSub) {
                    return this.then(new greycat.internal.task.CF_IfThenElse(cond, thenSub, elseSub, null));
                };
                CoreTask.prototype.ifThenElseScript = function (condScript, thenSub, elseSub) {
                    return this.then(new greycat.internal.task.CF_IfThenElse(greycat.internal.task.CoreTask.condFromScript(condScript), thenSub, elseSub, condScript));
                };
                CoreTask.prototype.whileDo = function (cond, task) {
                    return this.then(new greycat.internal.task.CF_WhileDo(cond, task, null));
                };
                CoreTask.prototype.whileDoScript = function (condScript, task) {
                    return this.then(new greycat.internal.task.CF_WhileDo(greycat.internal.task.CoreTask.condFromScript(condScript), task, condScript));
                };
                CoreTask.prototype.pipe = function () {
                    var subTasks = [];
                    for (var _i = 0; _i < arguments.length; _i++) {
                        subTasks[_i] = arguments[_i];
                    }
                    return this.then(new ((_a = greycat.internal.task.CF_Pipe).bind.apply(_a, [void 0].concat(subTasks)))());
                    var _a;
                };
                CoreTask.prototype.pipePar = function () {
                    var subTasks = [];
                    for (var _i = 0; _i < arguments.length; _i++) {
                        subTasks[_i] = arguments[_i];
                    }
                    return this.then(new ((_a = greycat.internal.task.CF_PipePar).bind.apply(_a, [void 0].concat(subTasks)))());
                    var _a;
                };
                CoreTask.prototype.pipeTo = function (subTask) {
                    var vars = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        vars[_i - 1] = arguments[_i];
                    }
                    return this.then(new ((_a = greycat.internal.task.CF_PipeTo).bind.apply(_a, [void 0, subTask].concat(vars)))());
                    var _a;
                };
                CoreTask.prototype.atomic = function (protectedTask) {
                    var variablesToLock = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        variablesToLock[_i - 1] = arguments[_i];
                    }
                    return this.then(new ((_a = greycat.internal.task.CF_Atomic).bind.apply(_a, [void 0, protectedTask].concat(variablesToLock)))());
                    var _a;
                };
                CoreTask.prototype.execute = function (graph, callback) {
                    this.executeWith(graph, null, callback);
                };
                CoreTask.prototype.executeSync = function (graph) {
                    var waiter = graph.newSyncCounter(1);
                    this.executeWith(graph, null, waiter.wrap());
                    return waiter.waitResult();
                };
                CoreTask.prototype.executeWith = function (graph, initial, callback) {
                    if (this.insertCursor > 0) {
                        var initalRes = void 0;
                        if (initial instanceof greycat.base.BaseTaskResult) {
                            initalRes = initial.clone();
                        }
                        else {
                            initalRes = new greycat.base.BaseTaskResult(initial, true);
                        }
                        var context_1 = new greycat.internal.task.CoreTaskContext(this, this._hooks, null, initalRes, graph, callback);
                        graph.scheduler().dispatch(greycat.plugin.SchedulerAffinity.SAME_THREAD, function () {
                            {
                                context_1.execute();
                            }
                        });
                    }
                    else {
                        if (callback != null) {
                            callback(greycat.Tasks.emptyResult());
                        }
                    }
                };
                CoreTask.prototype.prepare = function (graph, initial, callback) {
                    var initalRes;
                    if (initial instanceof greycat.base.BaseTaskResult) {
                        initalRes = initial.clone();
                    }
                    else {
                        initalRes = new greycat.base.BaseTaskResult(initial, true);
                    }
                    return new greycat.internal.task.CoreTaskContext(this, this._hooks, null, initalRes, graph, callback);
                };
                CoreTask.prototype.executeUsing = function (preparedContext) {
                    if (this.insertCursor > 0) {
                        preparedContext.graph().scheduler().dispatch(greycat.plugin.SchedulerAffinity.SAME_THREAD, function () {
                            {
                                preparedContext.execute();
                            }
                        });
                    }
                    else {
                        var casted = preparedContext;
                        if (casted._callback != null) {
                            casted._callback(greycat.Tasks.emptyResult());
                        }
                    }
                };
                CoreTask.prototype.executeFrom = function (parentContext, initial, affinity, callback) {
                    if (this.insertCursor > 0) {
                        var aggregatedHooks = null;
                        if (parentContext != null) {
                            aggregatedHooks = parentContext._hooks;
                        }
                        if (this._hooks != null) {
                            if (aggregatedHooks == null) {
                                aggregatedHooks = this._hooks;
                            }
                            else {
                                var temp_hooks = new Array(aggregatedHooks.length + this._hooks.length);
                                java.lang.System.arraycopy(aggregatedHooks, 0, temp_hooks, 0, aggregatedHooks.length);
                                java.lang.System.arraycopy(this._hooks, 0, temp_hooks, aggregatedHooks.length, this._hooks.length);
                                aggregatedHooks = temp_hooks;
                            }
                        }
                        var context_2 = new greycat.internal.task.CoreTaskContext(this, aggregatedHooks, parentContext, initial.clone(), parentContext.graph(), callback);
                        parentContext.graph().scheduler().dispatch(affinity, function () {
                            {
                                context_2.execute();
                            }
                        });
                    }
                    else {
                        if (callback != null) {
                            callback(greycat.Tasks.emptyResult());
                        }
                    }
                };
                CoreTask.prototype.executeFromUsing = function (parentContext, initial, affinity, contextInitializer, callback) {
                    if (this.insertCursor > 0) {
                        var aggregatedHooks = null;
                        if (parentContext != null) {
                            aggregatedHooks = parentContext._hooks;
                        }
                        if (this._hooks != null) {
                            if (aggregatedHooks == null) {
                                aggregatedHooks = this._hooks;
                            }
                            else {
                                var temp_hooks = new Array(aggregatedHooks.length + this._hooks.length);
                                java.lang.System.arraycopy(aggregatedHooks, 0, temp_hooks, 0, aggregatedHooks.length);
                                java.lang.System.arraycopy(this._hooks, 0, temp_hooks, aggregatedHooks.length, this._hooks.length);
                                aggregatedHooks = temp_hooks;
                            }
                        }
                        var context_3 = new greycat.internal.task.CoreTaskContext(this, aggregatedHooks, parentContext, initial.clone(), parentContext.graph(), callback);
                        if (contextInitializer != null) {
                            contextInitializer(context_3);
                        }
                        parentContext.graph().scheduler().dispatch(affinity, function () {
                            {
                                context_3.execute();
                            }
                        });
                    }
                    else {
                        if (callback != null) {
                            callback(greycat.Tasks.emptyResult());
                        }
                    }
                };
                CoreTask.prototype.loadFromBuffer = function (buffer, graph) {
                    return this.parse(greycat.utility.Base64.decodeToStringWithBounds(buffer, 0, buffer.length()), graph);
                };
                CoreTask.prototype.saveToBuffer = function (buffer) {
                    var saved = this.toString();
                    greycat.utility.Base64.encodeStringToBuffer(saved, buffer);
                    return this;
                };
                CoreTask.prototype.parse = function (flat, graph) {
                    if (flat == null) {
                        throw new Error("flat should not be null");
                    }
                    var contextTasks = new java.util.HashMap();
                    this.sub_parse(new greycat.internal.task.CoreTaskReader(flat, 0), graph, contextTasks);
                    return this;
                };
                CoreTask.prototype.sub_parse = function (reader, graph, contextTasks) {
                    var registry = graph.actionRegistry();
                    var cursor = 0;
                    var flatSize = reader.available();
                    var previous = 0;
                    var actionName = null;
                    var isClosed = false;
                    var isEscaped = false;
                    var paramsCapacity = 0;
                    var params = null;
                    var paramsIndex = 0;
                    var previousTaskId = null;
                    var subTaskMode = false;
                    while (cursor < flatSize) {
                        var current = reader.charAt(cursor);
                        switch (current) {
                            case '\"':
                            case '\'':
                                isEscaped = true;
                                cursor++;
                                var previousBackS = false;
                                while (cursor < flatSize) {
                                    var loopChar = reader.charAt(cursor);
                                    if (loopChar == '\\') {
                                        previousBackS = true;
                                    }
                                    else if (current == loopChar && !previousBackS) {
                                        break;
                                    }
                                    else {
                                        previousBackS = false;
                                    }
                                    cursor++;
                                }
                                break;
                            case greycat.Constants.TASK_SEP:
                                if (!isClosed) {
                                    var getName = reader.extract(previous, cursor).trim();
                                    this.then(new greycat.internal.task.ActionTraverseOrAttribute(false, true, getName));
                                }
                                actionName = null;
                                isEscaped = false;
                                previous = cursor + 1;
                                paramsCapacity = 0;
                                params = null;
                                paramsIndex = 0;
                                isClosed = false;
                                break;
                            case greycat.Constants.SUB_TASK_DECLR:
                                if (!isClosed) {
                                    var getName = reader.extract(previous, cursor).trim();
                                    this.then(new greycat.internal.task.ActionTraverseOrAttribute(false, true, getName));
                                }
                                subTaskMode = true;
                                actionName = null;
                                isEscaped = false;
                                previous = cursor + 1;
                                paramsCapacity = 0;
                                params = null;
                                paramsIndex = 0;
                                break;
                            case greycat.Constants.TASK_PARAM_OPEN:
                                actionName = reader.extract(previous, cursor).trim();
                                previous = cursor + 1;
                                break;
                            case greycat.Constants.TASK_PARAM_CLOSE:
                                var lastParamExtracted = void 0;
                                if (previousTaskId != null) {
                                    lastParamExtracted = previousTaskId;
                                    previousTaskId = null;
                                }
                                else {
                                    if (isEscaped) {
                                        lastParamExtracted = reader.extract(previous + 1, cursor - 1);
                                    }
                                    else {
                                        lastParamExtracted = reader.extract(previous, cursor);
                                    }
                                }
                                if (lastParamExtracted.length > 0) {
                                    if ((paramsIndex + 1) != paramsCapacity) {
                                        var newParams = new Array(paramsIndex + 1);
                                        if (params != null) {
                                            java.lang.System.arraycopy(params, 0, newParams, 0, paramsIndex);
                                        }
                                        params = newParams;
                                        paramsCapacity = paramsIndex + 1;
                                    }
                                    params[paramsIndex] = lastParamExtracted;
                                    paramsIndex++;
                                }
                                else {
                                    if (paramsIndex < paramsCapacity) {
                                        var shrinked = new Array(paramsIndex);
                                        java.lang.System.arraycopy(params, 0, shrinked, 0, paramsIndex);
                                        params = shrinked;
                                    }
                                }
                                if (graph == null) {
                                    this.then(new ((_a = greycat.internal.task.ActionNamed).bind.apply(_a, [void 0, actionName].concat(params)))());
                                }
                                else {
                                    this.then(greycat.internal.task.CoreTask.loadAction(registry, actionName, params, contextTasks));
                                }
                                actionName = null;
                                previous = cursor + 1;
                                isClosed = true;
                                break;
                            case greycat.Constants.TASK_PARAM_SEP:
                                var paramExtracted = void 0;
                                if (previousTaskId != null) {
                                    paramExtracted = previousTaskId;
                                    previousTaskId = null;
                                }
                                else {
                                    if (isEscaped) {
                                        paramExtracted = reader.extract(previous + 1, cursor - 1);
                                    }
                                    else {
                                        paramExtracted = reader.extract(previous, cursor);
                                    }
                                }
                                if (paramExtracted.length > 0) {
                                    if (paramsIndex >= paramsCapacity) {
                                        var newParamsCapacity = paramsCapacity * 2;
                                        if (newParamsCapacity == 0) {
                                            newParamsCapacity = greycat.internal.CoreConstants.MAP_INITIAL_CAPACITY;
                                        }
                                        var newParams = new Array(newParamsCapacity);
                                        if (params != null) {
                                            java.lang.System.arraycopy(params, 0, newParams, 0, paramsCapacity);
                                        }
                                        params = newParams;
                                        paramsCapacity = newParamsCapacity;
                                    }
                                    params[paramsIndex] = paramExtracted;
                                    paramsIndex++;
                                }
                                previous = cursor + 1;
                                isEscaped = false;
                                break;
                            case greycat.Constants.SUB_TASK_OPEN:
                                if (cursor > 0 && cursor + 1 < flatSize && reader.charAt(cursor + 1) != greycat.Constants.SUB_TASK_OPEN && reader.charAt(cursor - 1) != greycat.Constants.SUB_TASK_OPEN) {
                                    var subReader = reader.slice(cursor + 1);
                                    var subTask = void 0;
                                    if (subTaskMode) {
                                        var subTaskName = reader.extract(previous, cursor).trim();
                                        var subTaskID = greycat.internal.task.TaskHelper.parseInt(subTaskName);
                                        subTask = contextTasks.get(subTaskID);
                                    }
                                    else {
                                        subTask = new greycat.internal.task.CoreTask();
                                    }
                                    subTask.sub_parse(subReader, graph, contextTasks);
                                    cursor = cursor + subReader.end() + 1;
                                    previous = cursor + 1;
                                    var hash = subTask.hashCode();
                                    contextTasks.put(hash, subTask);
                                    previousTaskId = hash + "";
                                }
                                break;
                            case greycat.Constants.SUB_TASK_CLOSE:
                                if (cursor > 0 && cursor + 1 < flatSize && reader.charAt(cursor + 1) != greycat.Constants.SUB_TASK_CLOSE && reader.charAt(cursor - 1) != greycat.Constants.SUB_TASK_CLOSE) {
                                    reader.markend(cursor);
                                    return;
                                }
                                break;
                        }
                        cursor++;
                    }
                    if (!isClosed) {
                        var getName = reader.extract(previous, flatSize);
                        if (getName.length > 0) {
                            if (actionName != null) {
                                if (graph == null) {
                                    this.then(new ((_b = greycat.internal.task.ActionNamed).bind.apply(_b, [void 0, actionName].concat(params)))());
                                }
                                else {
                                    var singleParam = new Array(1);
                                    singleParam[0] = getName;
                                    this.then(greycat.internal.task.CoreTask.loadAction(registry, actionName, singleParam, contextTasks));
                                }
                            }
                            else {
                                this.then(new greycat.internal.task.ActionTraverseOrAttribute(false, true, getName.trim()));
                            }
                        }
                    }
                    var _a, _b;
                };
                CoreTask.loadAction = function (registry, actionName, params, contextTasks) {
                    var declaration = registry.declaration(actionName);
                    if (declaration == null || declaration.factory() == null) {
                        var varargs = params;
                        return new ((_a = greycat.internal.task.ActionNamed).bind.apply(_a, [void 0, actionName].concat(varargs)))();
                    }
                    else {
                        var factory = declaration.factory();
                        var declaredParams = declaration.params();
                        if (declaredParams != null && params != null) {
                            var resultSize = declaredParams.length;
                            var parsedParams = new Array(resultSize);
                            var varargs_index = 0;
                            for (var i = 0; i < params.length; i++) {
                                var correspondingType = void 0;
                                if (i < resultSize) {
                                    correspondingType = declaredParams[i];
                                }
                                else {
                                    correspondingType = declaredParams[resultSize - 1];
                                }
                                switch (correspondingType) {
                                    case greycat.Type.STRING:
                                        parsedParams[i] = params[i];
                                        break;
                                    case greycat.Type.LONG:
                                        parsedParams[i] = java.lang.Long.parseLong(params[i]);
                                        break;
                                    case greycat.Type.DOUBLE:
                                        parsedParams[i] = parseFloat(params[i]);
                                        break;
                                    case greycat.Type.TASK:
                                        parsedParams[i] = greycat.internal.task.CoreTask.getOrCreate(contextTasks, params[i]);
                                        break;
                                    case greycat.Type.STRING_ARRAY:
                                        if (varargs_index == 0) {
                                            var parsedSubParam = new Array(resultSize - i);
                                            parsedSubParam[varargs_index] = params[i];
                                            varargs_index = 1;
                                            parsedParams[i] = parsedSubParam;
                                        }
                                        else {
                                            parsedParams[resultSize - 1][varargs_index] = params[i];
                                            varargs_index++;
                                        }
                                        break;
                                    case greycat.Type.DOUBLE_ARRAY:
                                        if (varargs_index == 0) {
                                            var parsedSubParam = new Float64Array(resultSize - i);
                                            parsedSubParam[varargs_index] = parseFloat(params[i]);
                                            varargs_index = 1;
                                            parsedParams[i] = parsedSubParam;
                                        }
                                        else {
                                            parsedParams[resultSize - 1][varargs_index] = parseFloat(params[i]);
                                            varargs_index++;
                                        }
                                        break;
                                    case greycat.Type.TASK_ARRAY:
                                        if (varargs_index == 0) {
                                            var parsedSubParamTask = new Array(resultSize - i);
                                            parsedSubParamTask[varargs_index] = greycat.internal.task.CoreTask.getOrCreate(contextTasks, params[i]);
                                            varargs_index = 1;
                                            parsedParams[i] = parsedSubParamTask;
                                        }
                                        else {
                                            parsedParams[resultSize - 1][varargs_index] = greycat.internal.task.CoreTask.getOrCreate(contextTasks, params[i]);
                                            varargs_index++;
                                        }
                                        break;
                                }
                            }
                            if (resultSize > params.length) {
                                for (var i = params.length; i < resultSize; i++) {
                                    parsedParams[i] = null;
                                }
                            }
                            return factory(parsedParams);
                        }
                        else {
                            return factory(new Array(0));
                        }
                    }
                    var _a;
                };
                CoreTask.condFromScript = function (script) {
                    return function (ctx) {
                        {
                            return greycat.internal.task.CoreTask.executeScript(script, ctx);
                        }
                    };
                };
                CoreTask.executeScript = function (script, context) {
                    var print = console.log;
                    var println = console.log;
                    var ctx = context;
                    return eval(script);
                };
                CoreTask.fillDefault = function (registry) {
                    registry.declaration(greycat.internal.task.CoreActionNames.TRAVEL_IN_WORLD).setParams(greycat.Type.STRING).setDescription("Sets the task context to a particular world. Every nodes in current result will be switch ot new world.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionTravelInWorld(params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.TRAVEL_IN_TIME).setParams(greycat.Type.STRING).setDescription("Switches the time of the task context, i.e. travels the task context in time. Every nodes in current result will be switch ot new time.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionTravelInTime(params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.DEFINE_AS_GLOBAL_VAR).setParams(greycat.Type.STRING).setDescription("Stores the task result as a global variable in the task context and starts a new scope (for sub tasks).").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionDefineAsVar(params[0], true);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.DEFINE_AS_VAR).setParams(greycat.Type.STRING).setDescription("Stores the task result as a local variable in the task context and starts a new scope (for sub tasks).").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionDefineAsVar(params[0], false);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.DECLARE_GLOBAL_VAR).setParams(greycat.Type.STRING).setDescription("Stores the task result as a global variable in the task context and starts a new scope (for sub tasks).").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionDeclareVar(true, params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.DECLARE_VAR).setParams(greycat.Type.STRING).setDescription("Stores the task result as a local variable in the task context and starts a new scope (for sub tasks).").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionDeclareVar(false, params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.READ_VAR).setParams(greycat.Type.STRING).setDescription("Retrieves a stored variable. To reach a particular index, a default array notation can be used. Therefore, A[B] will be interpreted as: extract value stored at index B from the variable A.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionReadVar(params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.SET_AS_VAR).setParams(greycat.Type.STRING).setDescription("Stores the current task result into a named variable without starting a new scope.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionSetAsVar(params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.ADD_TO_VAR).setParams(greycat.Type.STRING).setDescription("Adds the current task result to the named variable.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionAddToVar(params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.TRAVERSE).setParams(greycat.Type.STRING, greycat.Type.STRING_ARRAY).setDescription("Retrieves any nodes contained in a relations of the nodes present in the current result.").setFactory(function (params) {
                        {
                            var varrags = params[1];
                            if (varrags != null) {
                                return new ((_a = greycat.internal.task.ActionTraverseOrAttribute).bind.apply(_a, [void 0, false, false, params[0]].concat(varrags)))();
                            }
                            else {
                                return new greycat.internal.task.ActionTraverseOrAttribute(false, false, params[0]);
                            }
                        }
                        var _a;
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.ATTRIBUTE).setParams(greycat.Type.STRING).setDescription("Retrieves any attribute(s) contained in the nodes present in the current result.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionTraverseOrAttribute(false, false, params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.WITH).setParams(greycat.Type.STRING, greycat.Type.STRING).setDescription("Filters the previous result to keep nodes, which named attribute has a specific value.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionWith(params[0], params[1]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.WITHOUT).setParams(greycat.Type.STRING, greycat.Type.STRING).setDescription("Filters the previous result to keep nodes, which named attribute does not have a given value.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionWithout(params[0], params[1]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.SCRIPT).setParams(greycat.Type.STRING).setDescription("Execute a JS script; Current context is automatically injected as ctx variables. Other variables are directly reachable as JS vars. Execution is synchronous").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionScript(params[0], false);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.ASYNC_SCRIPT).setParams(greycat.Type.STRING).setDescription("Execute a JS script; Current context is automatically injected as ctx variables. Other variables are directly reachable as JS vars. Execution is asynchronous and script must contains a ctx.continueTask(); or ctx.continueWith(newResult).").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionScript(params[0], true);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.CREATE_NODE).setParams().setDescription("Creates a new node in the [world,time] of the current context.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionCreateNode(null);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.CREATE_TYPED_NODE).setParams(greycat.Type.STRING).setDescription("Creates a new typed node in the [world,time] of the current context.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionCreateNode(params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.PRINT).setParams(greycat.Type.STRING).setDescription("Prints the action in a human readable format (without line breaks).").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionPrint(params[0], false);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.LOG).setParams(greycat.Type.STRING).setDescription("Prints the action in a human readable format (without line breaks).").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionLog(params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.PRINTLN).setParams(greycat.Type.STRING).setDescription("Prints the action in a human readable format (with line breaks).").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionPrint(params[0], true);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.ATTRIBUTES).setParams().setDescription("Retrieves all attribute names of nodes present in the previous task result.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionAttributes(null);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.ATTRIBUTES).setParams().setDescription("Retrieves all attribute names of nodes present in the previous task result.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionAttributes(null);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.ATTRIBUTES_WITH_TYPE).setParams(greycat.Type.STRING).setDescription("Gets and filters all attribute names of nodes present in the previous result.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionAttributes(params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.FLAT).setParams().setDescription("Flat a TaskResult containing TaskResult to a flat TaskResult.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionFlat();
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.EXECUTE_EXPRESSION).setParams(greycat.Type.STRING).setDescription("Executes an expression on all nodes given from the previous step.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionExecuteExpression(params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.READ_GLOBAL_INDEX).setParams(greycat.Type.STRING, greycat.Type.STRING_ARRAY).setDescription("Retrieves indexed nodes matching the query.").setFactory(function (params) {
                        {
                            var varargs = params[1];
                            if (varargs != null) {
                                return new ((_a = greycat.internal.task.ActionReadGlobalIndex).bind.apply(_a, [void 0, params[0]].concat(varargs)))();
                            }
                            else {
                                return new greycat.internal.task.ActionReadGlobalIndex(params[0]);
                            }
                        }
                        var _a;
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.GLOBAL_INDEX).setParams(greycat.Type.STRING).setDescription("Retrieve global index node").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionGlobalIndex(params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.SELECT).setParams(greycat.Type.STRING).setDescription("Use a JS script to filter nodes. The task context is inject in the variable 'context'. The current node is inject in the variable 'node'.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionSelect(params[0], null);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.TIME_SENSITIVITY).setParams(greycat.Type.STRING, greycat.Type.STRING).setDescription("Adjust the time sensitivity of nodes present in current result.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionTimeSensitivity(params[0], params[1]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.SET_ATTRIBUTE).setParams(greycat.Type.STRING, greycat.Type.STRING, greycat.Type.STRING).setDescription("Sets the value of an attribute for all nodes present in the current result. If value is similar to the previously stored one, nodes will remain unmodified.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionSetAttribute(params[0], params[1], params[2], true);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.FORCE_ATTRIBUTE).setParams(greycat.Type.STRING, greycat.Type.STRING, greycat.Type.STRING).setDescription("Forces the value of an attribute for all nodes present in the current result. If value is similar to the previously stored one, nodes will still be modified and their timeline will be affected.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.ActionSetAttribute(params[0], params[1], params[2], true);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.LOOP).setParams(greycat.Type.STRING, greycat.Type.STRING, greycat.Type.TASK).setDescription("Executes a task in a range.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.CF_Loop(params[0], params[1], params[2]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.LOOP_PAR).setParams(greycat.Type.STRING, greycat.Type.STRING, greycat.Type.TASK).setDescription("Parallel version of loop(String, String, Task). Executes a task in a range. Steps can be executed in parallel. Creates as many threads as elements in the collection.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.CF_LoopPar(params[0], params[1], params[2]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.FOR_EACH).setParams(greycat.Type.TASK).setDescription("Iterates through a collection and calls the sub task for each element.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.CF_ForEach(params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.FOR_EACH_PAR).setParams(greycat.Type.TASK).setDescription("Parallel version of forEach(Task). All sub tasks can be called in parallel. Creates as many threads as elements in the collection.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.CF_ForEachPar(params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.MAP).setParams(greycat.Type.TASK).setDescription("Iterates through a collection and calls the sub task for each element in parallel and then aggregates all results in an array of array manner.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.CF_Map(params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.MAP_PAR).setParams(greycat.Type.TASK).setDescription("Parallel version of map(Task). Iterates through a collection and calls the sub task for each element in parallel and then aggregates all results in an array of array manner.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.CF_MapPar(params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.PIPE).setParams(greycat.Type.TASK_ARRAY).setDescription("Executes and waits for a number of given sub tasks. The result of these sub tasks is immediately enqueued and available in the next sub task in a array of array manner.").setFactory(function (params) {
                        {
                            var varargs = params[0];
                            return new ((_a = greycat.internal.task.CF_Pipe).bind.apply(_a, [void 0].concat(varargs)))();
                        }
                        var _a;
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.PIPE_PAR).setParams(greycat.Type.TASK_ARRAY).setDescription("Parallel version of pipe(Tasks...). Executes and waits a number of given sub tasks. The result of these sub tasks is immediately enqueued and available in the next sub task in a array of array manner.").setFactory(function (params) {
                        {
                            var varargs = params[0];
                            return new ((_a = greycat.internal.task.CF_PipePar).bind.apply(_a, [void 0].concat(varargs)))();
                        }
                        var _a;
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.DO_WHILE).setParams(greycat.Type.STRING, greycat.Type.TASK).setDescription("Executes a give task until a given condition evaluates to true.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.CF_DoWhile(params[1], greycat.internal.task.CoreTask.condFromScript(params[0]), params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.WHILE_DO).setParams(greycat.Type.STRING, greycat.Type.TASK).setDescription("Similar to doWhile(Task, ConditionalExpression) but the task is at least executed once.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.CF_WhileDo(greycat.internal.task.CoreTask.condFromScript(params[0]), params[1], params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.PIPE_TO).setParams(greycat.Type.TASK, greycat.Type.STRING_ARRAY).setDescription("Executes a given sub task in an isolated environment and store result as variables.").setFactory(function (params) {
                        {
                            var varargs = params[1];
                            if (varargs != null) {
                                return new ((_a = greycat.internal.task.CF_PipeTo).bind.apply(_a, [void 0, params[0]].concat(varargs)))();
                            }
                            else {
                                return new greycat.internal.task.CF_PipeTo(params[0]);
                            }
                        }
                        var _a;
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.ATOMIC).setParams(greycat.Type.TASK, greycat.Type.STRING_ARRAY).setDescription("Atomically execute a subTask while blocking on nodes present in named variables").setFactory(function (params) {
                        {
                            var varargs = params[1];
                            if (varargs != null) {
                                return new ((_a = greycat.internal.task.CF_Atomic).bind.apply(_a, [void 0, params[0]].concat(varargs)))();
                            }
                            else {
                                return new greycat.internal.task.CF_Atomic(params[0]);
                            }
                        }
                        var _a;
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.IF_THEN).setParams(greycat.Type.STRING, greycat.Type.TASK).setDescription("Executes a sub task if a given condition is evaluated to true.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.CF_IfThen(greycat.internal.task.CoreTask.condFromScript(params[0]), params[1], params[0]);
                        }
                    });
                    registry.declaration(greycat.internal.task.CoreActionNames.IF_THEN_ELSE).setParams(greycat.Type.STRING, greycat.Type.TASK, greycat.Type.TASK).setDescription("Executes a sub task if a given condition is evaluated to true, another one otherwise.").setFactory(function (params) {
                        {
                            return new greycat.internal.task.CF_IfThenElse(greycat.internal.task.CoreTask.condFromScript(params[0]), params[1], params[2], params[0]);
                        }
                    });
                };
                CoreTask.getOrCreate = function (contextTasks, param) {
                    var taskId = greycat.internal.task.TaskHelper.parseInt(param);
                    var previous = contextTasks.get(taskId);
                    if (previous == null) {
                        previous = new greycat.internal.task.CoreTask();
                        contextTasks.put(taskId, previous);
                    }
                    return previous;
                };
                CoreTask.prototype.hashCode = function () {
                    if (this['hashCodeCache'] === undefined) {
                        this['hashCodeCache'] = Math.floor((Math.random() * 1000000000) + 1);
                    }
                    return this['hashCodeCache'];
                };
                CoreTask.prototype.toString = function () {
                    var res = new java.lang.StringBuilder();
                    var dagCounters = new java.util.HashMap();
                    var dagCollector = new java.util.HashMap();
                    greycat.internal.task.CoreTask.deep_analyze(this, dagCounters, dagCollector);
                    var keys = dagCounters.keySet();
                    var flatKeys = keys.toArray(new Array(keys.size()));
                    var dagIDS = new java.util.HashMap();
                    for (var i = 0; i < flatKeys.length; i++) {
                        var key = flatKeys[i];
                        var counter = dagCounters.get(key);
                        if (counter != null && counter > 1) {
                            dagIDS.put(key, dagIDS.size());
                        }
                    }
                    this.serialize(res, dagIDS);
                    var set_dagIDS = dagIDS.keySet();
                    var flatDagIDS = set_dagIDS.toArray(new Array(set_dagIDS.size()));
                    for (var i = 0; i < flatDagIDS.length; i++) {
                        var key = flatDagIDS[i];
                        var index = dagIDS.get(key);
                        var dagTask = dagCollector.get(key);
                        res.append(greycat.Constants.SUB_TASK_DECLR);
                        res.append("" + index);
                        res.append(greycat.Constants.SUB_TASK_OPEN);
                        dagTask.serialize(res, dagIDS);
                        res.append(greycat.Constants.SUB_TASK_CLOSE);
                    }
                    return res.toString();
                };
                CoreTask.prototype.serialize = function (builder, dagCounters) {
                    for (var i = 0; i < this.insertCursor; i++) {
                        if (i != 0) {
                            builder.append(greycat.Constants.TASK_SEP);
                        }
                        if (this.actions[i] instanceof greycat.internal.task.CF_Action) {
                            this.actions[i].cf_serialize(builder, dagCounters);
                        }
                        else {
                            this.actions[i].serialize(builder);
                        }
                    }
                };
                CoreTask.deep_analyze = function (t, counters, dagCollector) {
                    var tHash = t.hashCode();
                    var previous = counters.get(tHash);
                    if (previous == null) {
                        counters.put(tHash, 1);
                        dagCollector.put(tHash, t);
                        for (var i = 0; i < t.insertCursor; i++) {
                            if (t.actions[i] instanceof greycat.internal.task.CF_Action) {
                                var children = t.actions[i].children();
                                for (var j = 0; j < children.length; j++) {
                                    greycat.internal.task.CoreTask.deep_analyze(children[j], counters, dagCollector);
                                }
                            }
                        }
                    }
                    else {
                        counters.put(tHash, previous + 1);
                    }
                };
                CoreTask.prototype.travelInWorld = function (world) {
                    return this.then(greycat.internal.task.CoreActions.travelInWorld(world));
                };
                CoreTask.prototype.travelInTime = function (time) {
                    return this.then(greycat.internal.task.CoreActions.travelInTime(time));
                };
                CoreTask.prototype.inject = function (input) {
                    return this.then(greycat.internal.task.CoreActions.inject(input));
                };
                CoreTask.prototype.defineAsGlobalVar = function (name) {
                    return this.then(greycat.internal.task.CoreActions.defineAsGlobalVar(name));
                };
                CoreTask.prototype.defineAsVar = function (name) {
                    return this.then(greycat.internal.task.CoreActions.defineAsVar(name));
                };
                CoreTask.prototype.declareGlobalVar = function (name) {
                    return this.then(greycat.internal.task.CoreActions.declareGlobalVar(name));
                };
                CoreTask.prototype.declareVar = function (name) {
                    return this.then(greycat.internal.task.CoreActions.declareVar(name));
                };
                CoreTask.prototype.readVar = function (name) {
                    return this.then(greycat.internal.task.CoreActions.readVar(name));
                };
                CoreTask.prototype.setAsVar = function (name) {
                    return this.then(greycat.internal.task.CoreActions.setAsVar(name));
                };
                CoreTask.prototype.addToVar = function (name) {
                    return this.then(greycat.internal.task.CoreActions.addToVar(name));
                };
                CoreTask.prototype.setAttribute = function (name, type, value) {
                    return this.then(greycat.internal.task.CoreActions.setAttribute(name, type, value));
                };
                CoreTask.prototype.timeSensitivity = function (delta, offset) {
                    return this.then(greycat.internal.task.CoreActions.timeSensitivity(delta, offset));
                };
                CoreTask.prototype.forceAttribute = function (name, type, value) {
                    return this.then(greycat.internal.task.CoreActions.forceAttribute(name, type, value));
                };
                CoreTask.prototype.remove = function (name) {
                    return this.then(greycat.internal.task.CoreActions.remove(name));
                };
                CoreTask.prototype.attributes = function () {
                    return this.then(greycat.internal.task.CoreActions.attributes());
                };
                CoreTask.prototype.timepoints = function (from, to) {
                    return this.then(greycat.internal.task.CoreActions.timepoints(from, to));
                };
                CoreTask.prototype.attributesWithType = function (filterType) {
                    return this.then(greycat.internal.task.CoreActions.attributesWithTypes(filterType));
                };
                CoreTask.prototype.addVarToRelation = function (relName, varName) {
                    var attributes = [];
                    for (var _i = 2; _i < arguments.length; _i++) {
                        attributes[_i - 2] = arguments[_i];
                    }
                    return this.then((_a = greycat.internal.task.CoreActions).addVarToRelation.apply(_a, [relName, varName].concat(attributes)));
                    var _a;
                };
                CoreTask.prototype.removeVarFromRelation = function (relName, varFrom) {
                    var attributes = [];
                    for (var _i = 2; _i < arguments.length; _i++) {
                        attributes[_i - 2] = arguments[_i];
                    }
                    return this.then((_a = greycat.internal.task.CoreActions).removeVarFromRelation.apply(_a, [relName, varFrom].concat(attributes)));
                    var _a;
                };
                CoreTask.prototype.traverse = function (name) {
                    var params = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        params[_i - 1] = arguments[_i];
                    }
                    return this.then((_a = greycat.internal.task.CoreActions).traverse.apply(_a, [name].concat(params)));
                    var _a;
                };
                CoreTask.prototype.attribute = function (name) {
                    var params = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        params[_i - 1] = arguments[_i];
                    }
                    return this.then((_a = greycat.internal.task.CoreActions).attribute.apply(_a, [name].concat(params)));
                    var _a;
                };
                CoreTask.prototype.readGlobalIndex = function (name) {
                    var query = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        query[_i - 1] = arguments[_i];
                    }
                    return this.then((_a = greycat.internal.task.CoreActions).readGlobalIndex.apply(_a, [name].concat(query)));
                    var _a;
                };
                CoreTask.prototype.globalIndex = function (indexName) {
                    return this.then(greycat.internal.task.CoreActions.globalIndex(indexName));
                };
                CoreTask.prototype.addToGlobalIndex = function (name) {
                    var attributes = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        attributes[_i - 1] = arguments[_i];
                    }
                    return this.then((_a = greycat.internal.task.CoreActions).addToGlobalIndex.apply(_a, [name].concat(attributes)));
                    var _a;
                };
                CoreTask.prototype.addToGlobalTimedIndex = function (name) {
                    var attributes = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        attributes[_i - 1] = arguments[_i];
                    }
                    return this.then((_a = greycat.internal.task.CoreActions).addToGlobalTimedIndex.apply(_a, [name].concat(attributes)));
                    var _a;
                };
                CoreTask.prototype.removeFromGlobalIndex = function (name) {
                    var attributes = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        attributes[_i - 1] = arguments[_i];
                    }
                    return this.then((_a = greycat.internal.task.CoreActions).removeFromGlobalIndex.apply(_a, [name].concat(attributes)));
                    var _a;
                };
                CoreTask.prototype.removeFromGlobalTimedIndex = function (name) {
                    var attributes = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        attributes[_i - 1] = arguments[_i];
                    }
                    return this.then((_a = greycat.internal.task.CoreActions).removeFromGlobalTimedIndex.apply(_a, [name].concat(attributes)));
                    var _a;
                };
                CoreTask.prototype.indexNames = function () {
                    return this.then(greycat.internal.task.CoreActions.indexNames());
                };
                CoreTask.prototype.selectWith = function (name, pattern) {
                    return this.then(greycat.internal.task.CoreActions.selectWith(name, pattern));
                };
                CoreTask.prototype.selectWithout = function (name, pattern) {
                    return this.then(greycat.internal.task.CoreActions.selectWithout(name, pattern));
                };
                CoreTask.prototype.select = function (filterFunction) {
                    return this.then(greycat.internal.task.CoreActions.select(filterFunction));
                };
                CoreTask.prototype.selectObject = function (filterFunction) {
                    return this.then(greycat.internal.task.CoreActions.selectObject(filterFunction));
                };
                CoreTask.prototype.log = function (name) {
                    return this.then(greycat.internal.task.CoreActions.log(name));
                };
                CoreTask.prototype.selectScript = function (script) {
                    return this.then(greycat.internal.task.CoreActions.selectScript(script));
                };
                CoreTask.prototype.print = function (name) {
                    return this.then(greycat.internal.task.CoreActions.print(name));
                };
                CoreTask.prototype.println = function (name) {
                    return this.then(greycat.internal.task.CoreActions.println(name));
                };
                CoreTask.prototype.executeExpression = function (expression) {
                    return this.then(greycat.internal.task.CoreActions.executeExpression(expression));
                };
                CoreTask.prototype.createNode = function () {
                    return this.then(greycat.internal.task.CoreActions.createNode());
                };
                CoreTask.prototype.createTypedNode = function (type) {
                    return this.then(greycat.internal.task.CoreActions.createTypedNode(type));
                };
                CoreTask.prototype.save = function () {
                    return this.then(greycat.internal.task.CoreActions.save());
                };
                CoreTask.prototype.script = function (script) {
                    return this.then(greycat.internal.task.CoreActions.script(script));
                };
                CoreTask.prototype.asyncScript = function (ascript) {
                    return this.then(greycat.internal.task.CoreActions.asyncScript(ascript));
                };
                CoreTask.prototype.lookup = function (nodeId) {
                    return this.then(greycat.internal.task.CoreActions.lookup(nodeId));
                };
                CoreTask.prototype.lookupAll = function (nodeIds) {
                    return this.then(greycat.internal.task.CoreActions.lookupAll(nodeIds));
                };
                CoreTask.prototype.clearResult = function () {
                    return this.then(greycat.internal.task.CoreActions.clearResult());
                };
                CoreTask.prototype.action = function (name) {
                    var params = [];
                    for (var _i = 1; _i < arguments.length; _i++) {
                        params[_i - 1] = arguments[_i];
                    }
                    return this.then((_a = greycat.internal.task.CoreActions).action.apply(_a, [name].concat(params)));
                    var _a;
                };
                CoreTask.prototype.flipVar = function (name) {
                    return this.then(greycat.internal.task.CoreActions.flipVar(name));
                };
                CoreTask.prototype.flat = function () {
                    return this.then(greycat.internal.task.CoreActions.flat());
                };
                return CoreTask;
            }());
            task_1.CoreTask = CoreTask;
            var CoreTaskContext = (function () {
                function CoreTaskContext(origin, p_hooks, parentContext, initial, p_graph, p_callback) {
                    this._localVariables = null;
                    this._nextVariables = null;
                    this.cursor = 0;
                    this._output = null;
                    this._origin = origin;
                    this._hooks = p_hooks;
                    if (parentContext != null) {
                        this._time = parentContext.time();
                        this._world = parentContext.world();
                    }
                    else {
                        this._world = 0;
                        this._time = greycat.Constants.BEGINNING_OF_TIME;
                    }
                    this._graph = p_graph;
                    this._parent = parentContext;
                    var castedParentContext = parentContext;
                    if (parentContext == null) {
                        this._globalVariables = new java.util.ConcurrentHashMap();
                    }
                    else {
                        this._globalVariables = castedParentContext.globalVariables();
                    }
                    this._result = initial;
                    this._callback = p_callback;
                }
                CoreTaskContext.prototype.graph = function () {
                    return this._graph;
                };
                CoreTaskContext.prototype.world = function () {
                    return this._world;
                };
                CoreTaskContext.prototype.setWorld = function (p_world) {
                    this._world = p_world;
                    return this;
                };
                CoreTaskContext.prototype.time = function () {
                    return this._time;
                };
                CoreTaskContext.prototype.setTime = function (p_time) {
                    this._time = p_time;
                    return this;
                };
                CoreTaskContext.prototype.variables = function () {
                    var collected = new java.util.HashMap();
                    var globalKeys = this._globalVariables.keySet().toArray(new Array(this._globalVariables.size()));
                    for (var i = 0; i < globalKeys.length; i++) {
                        collected.put(globalKeys[i], this._globalVariables.get(globalKeys[i]));
                    }
                    var collectedKeys = collected.keySet().toArray(new Array(collected.size()));
                    var result = new Array(collectedKeys.length);
                    for (var i = 0; i < collectedKeys.length; i++) {
                        result[i] = new greycat.utility.Tuple(collectedKeys[i], collected.get(collectedKeys[i]));
                    }
                    return result;
                };
                CoreTaskContext.prototype.recursive_collect = function (ctx, collector) {
                    var localVariables = ctx.localVariables();
                    if (localVariables != null) {
                        var localKeys = localVariables.keySet().toArray(new Array(localVariables.size()));
                        for (var i = 0; i < localKeys.length; i++) {
                            if (!collector.containsKey(localKeys[i])) {
                                collector.put(localKeys[i], localVariables.get(localKeys[i]));
                            }
                        }
                    }
                    if (ctx._parent != null) {
                        this.recursive_collect(ctx._parent, collector);
                    }
                };
                CoreTaskContext.prototype.variable = function (name) {
                    var resolved = this._globalVariables.get(name);
                    if (resolved == null) {
                        resolved = this.internal_deep_resolve(name);
                    }
                    return resolved;
                };
                CoreTaskContext.prototype.isGlobal = function (name) {
                    return this._globalVariables.containsKey(name);
                };
                CoreTaskContext.prototype.internal_deep_resolve = function (name) {
                    var resolved = null;
                    if (this._localVariables != null) {
                        resolved = this._localVariables.get(name);
                    }
                    if (resolved == null && this._parent != null) {
                        var castedParent = this._parent;
                        if (castedParent._nextVariables != null) {
                            resolved = castedParent._nextVariables.get(name);
                            if (resolved != null) {
                                return resolved;
                            }
                        }
                        return castedParent.internal_deep_resolve(name);
                    }
                    else {
                        return resolved;
                    }
                };
                CoreTaskContext.prototype.wrap = function (input) {
                    return new greycat.base.BaseTaskResult(input, false);
                };
                CoreTaskContext.prototype.wrapClone = function (input) {
                    return new greycat.base.BaseTaskResult(input, true);
                };
                CoreTaskContext.prototype.newResult = function () {
                    return new greycat.base.BaseTaskResult(null, false);
                };
                CoreTaskContext.prototype.declareVariable = function (name) {
                    if (this._localVariables == null) {
                        this._localVariables = new java.util.HashMap();
                    }
                    this._localVariables.put(name, new greycat.base.BaseTaskResult(null, false));
                    return this;
                };
                CoreTaskContext.prototype.lazyWrap = function (input) {
                    if (input instanceof greycat.base.BaseTaskResult) {
                        return input;
                    }
                    else {
                        return this.wrap(input);
                    }
                };
                CoreTaskContext.prototype.defineVariable = function (name, initialResult) {
                    if (this._localVariables == null) {
                        this._localVariables = new java.util.HashMap();
                    }
                    this._localVariables.put(name, this.lazyWrap(initialResult).clone());
                    return this;
                };
                CoreTaskContext.prototype.defineVariableForSubTask = function (name, initialResult) {
                    if (this._nextVariables == null) {
                        this._nextVariables = new java.util.HashMap();
                    }
                    this._nextVariables.put(name, this.lazyWrap(initialResult).clone());
                    return this;
                };
                CoreTaskContext.prototype.setGlobalVariable = function (name, value) {
                    var previous = this._globalVariables.put(name, this.lazyWrap(value).clone());
                    if (previous != null) {
                        previous.free();
                    }
                    return this;
                };
                CoreTaskContext.prototype.setVariable = function (name, value) {
                    var target = this.internal_deep_resolve_map(name);
                    if (target == null) {
                        if (this._localVariables == null) {
                            this._localVariables = new java.util.HashMap();
                        }
                        target = this._localVariables;
                    }
                    var previous = target.put(name, this.lazyWrap(value).clone());
                    if (previous != null) {
                        previous.free();
                    }
                    return this;
                };
                CoreTaskContext.prototype.internal_deep_resolve_map = function (name) {
                    if (this._localVariables != null) {
                        var resolved = this._localVariables.get(name);
                        if (resolved != null) {
                            return this._localVariables;
                        }
                    }
                    if (this._parent != null) {
                        var castedParent = this._parent;
                        if (castedParent._nextVariables != null) {
                            var resolved = castedParent._nextVariables.get(name);
                            if (resolved != null) {
                                return this._localVariables;
                            }
                        }
                        return this._parent.internal_deep_resolve_map(name);
                    }
                    else {
                        return null;
                    }
                };
                CoreTaskContext.prototype.addToGlobalVariable = function (name, value) {
                    var previous = this._globalVariables.get(name);
                    if (previous == null) {
                        previous = new greycat.base.BaseTaskResult(null, false);
                        this._globalVariables.put(name, previous);
                    }
                    if (value != null) {
                        if (value instanceof greycat.base.BaseTaskResult) {
                            var casted = value;
                            for (var i = 0; i < casted.size(); i++) {
                                var loop = casted.get(i);
                                if (loop instanceof greycat.base.BaseNode) {
                                    var castedNode = loop;
                                    previous.add(castedNode.graph().cloneNode(castedNode));
                                }
                                else {
                                    previous.add(loop);
                                }
                            }
                        }
                        else if (value instanceof greycat.base.BaseNode) {
                            var castedNode = value;
                            previous.add(castedNode.graph().cloneNode(castedNode));
                        }
                        else {
                            previous.add(value);
                        }
                    }
                    return this;
                };
                CoreTaskContext.prototype.addToVariable = function (name, value) {
                    var target = this.internal_deep_resolve_map(name);
                    if (target == null) {
                        if (this._localVariables == null) {
                            this._localVariables = new java.util.HashMap();
                        }
                        target = this._localVariables;
                    }
                    var previous = target.get(name);
                    if (previous == null) {
                        previous = new greycat.base.BaseTaskResult(null, false);
                        target.put(name, previous);
                    }
                    if (value != null) {
                        if (value instanceof greycat.base.BaseTaskResult) {
                            var casted = value;
                            for (var i = 0; i < casted.size(); i++) {
                                var loop = casted.get(i);
                                if (loop instanceof greycat.base.BaseNode) {
                                    var castedNode = loop;
                                    previous.add(castedNode.graph().cloneNode(castedNode));
                                }
                                else {
                                    previous.add(loop);
                                }
                            }
                        }
                        else if (value instanceof greycat.base.BaseNode) {
                            var castedNode = value;
                            previous.add(castedNode.graph().cloneNode(castedNode));
                        }
                        else {
                            previous.add(value);
                        }
                    }
                    return this;
                };
                CoreTaskContext.prototype.globalVariables = function () {
                    return this._globalVariables;
                };
                CoreTaskContext.prototype.localVariables = function () {
                    return this._localVariables;
                };
                CoreTaskContext.prototype.result = function () {
                    return this._result;
                };
                CoreTaskContext.prototype.resultAsNodes = function () {
                    return this._result;
                };
                CoreTaskContext.prototype.resultAsStrings = function () {
                    return this._result;
                };
                CoreTaskContext.prototype.continueWith = function (nextResult) {
                    var previousResult = this._result;
                    if (previousResult != null && previousResult != nextResult) {
                        previousResult.free();
                    }
                    this._result = nextResult;
                    this.continueTask();
                };
                CoreTaskContext.prototype.continueTask = function () {
                    var globalHooks = this._graph.taskHooks();
                    var currentAction = this._origin.actions[this.cursor];
                    if (this._hooks != null) {
                        for (var i = 0; i < this._hooks.length; i++) {
                            this._hooks[i].afterAction(currentAction, this);
                        }
                    }
                    if (globalHooks != null) {
                        for (var i = 0; i < globalHooks.length; i++) {
                            globalHooks[i].afterAction(currentAction, this);
                        }
                    }
                    this.cursor++;
                    var nextAction;
                    if (this.cursor == this._origin.insertCursor) {
                        nextAction = null;
                    }
                    else {
                        nextAction = this._origin.actions[this.cursor];
                    }
                    if (nextAction == null) {
                        this.endTask(null, null);
                    }
                    else {
                        if (this._hooks != null) {
                            for (var i = 0; i < this._hooks.length; i++) {
                                this._hooks[i].beforeAction(nextAction, this);
                            }
                        }
                        if (globalHooks != null) {
                            for (var i = 0; i < globalHooks.length; i++) {
                                globalHooks[i].beforeAction(nextAction, this);
                            }
                        }
                        var previousCursot = this.cursor;
                        try {
                            nextAction.eval(this);
                        }
                        catch ($ex$) {
                            if ($ex$ instanceof Error) {
                                var e = $ex$;
                                {
                                    if (this.cursor == previousCursot) {
                                        this.endTask(null, e);
                                    }
                                    else {
                                        console.error(e);
                                    }
                                }
                            }
                            else {
                                throw $ex$;
                            }
                        }
                    }
                };
                CoreTaskContext.prototype.endTask = function (preFinalResult, e) {
                    if (preFinalResult != null) {
                        if (this._result != null) {
                            this._result.free();
                        }
                        this._result = preFinalResult;
                    }
                    var globalHooks = this._graph.taskHooks();
                    if (this._localVariables != null) {
                        var localValues = this._localVariables.keySet();
                        var flatLocalValues = localValues.toArray(new Array(localValues.size()));
                        for (var i = 0; i < flatLocalValues.length; i++) {
                            this._localVariables.get(flatLocalValues[i]).free();
                        }
                    }
                    if (this._nextVariables != null) {
                        var nextValues = this._nextVariables.keySet();
                        var flatNextValues = nextValues.toArray(new Array(nextValues.size()));
                        for (var i = 0; i < flatNextValues.length; i++) {
                            this._nextVariables.get(flatNextValues[i]).free();
                        }
                    }
                    if (this._parent == null) {
                        var globalValues = this._globalVariables.keySet();
                        var globalFlatValues = globalValues.toArray(new Array(globalValues.size()));
                        for (var i = 0; i < globalFlatValues.length; i++) {
                            this._globalVariables.get(globalFlatValues[i]).free();
                        }
                    }
                    if (this._hooks != null) {
                        for (var i = 0; i < this._hooks.length; i++) {
                            if (this._parent == null) {
                                this._hooks[i].end(this);
                            }
                            else {
                                this._hooks[i].afterTask(this);
                            }
                        }
                    }
                    if (globalHooks != null) {
                        for (var i = 0; i < globalHooks.length; i++) {
                            if (this._parent == null) {
                                globalHooks[i].end(this);
                            }
                            else {
                                globalHooks[i].afterTask(this);
                            }
                        }
                    }
                    if (this._callback != null) {
                        if (e != null) {
                            if (this._result == null) {
                                this._result = new greycat.base.BaseTaskResult(null, false);
                            }
                            this._result.setException(e);
                        }
                        if (this._output != null) {
                            if (this._result == null) {
                                this._result = new greycat.base.BaseTaskResult(null, false);
                            }
                            this._result.setOutput(this._output.toString());
                        }
                        this._callback(this._result);
                    }
                    else {
                        if (e != null) {
                            console.error(e);
                        }
                        if (this._output != null) {
                            console.log(this._output.toString());
                        }
                        if (this._result != null) {
                            this._result.free();
                        }
                    }
                };
                CoreTaskContext.prototype.execute = function () {
                    var current = this._origin.actions[this.cursor];
                    if (this._hooks != null) {
                        for (var i = 0; i < this._hooks.length; i++) {
                            if (this._parent == null) {
                                this._hooks[i].start(this);
                            }
                            else {
                                this._hooks[i].beforeTask(this._parent, this);
                            }
                            this._hooks[i].beforeAction(current, this);
                        }
                    }
                    var globalHooks = this._graph.taskHooks();
                    if (globalHooks != null) {
                        for (var i = 0; i < globalHooks.length; i++) {
                            if (this._parent == null) {
                                globalHooks[i].start(this);
                            }
                            else {
                                globalHooks[i].beforeTask(this._parent, this);
                            }
                            globalHooks[i].beforeAction(current, this);
                        }
                    }
                    try {
                        current.eval(this);
                    }
                    catch ($ex$) {
                        if ($ex$ instanceof Error) {
                            var e = $ex$;
                            {
                                if (this.cursor == 0) {
                                    this.endTask(null, e);
                                }
                                else {
                                    console.error(e);
                                }
                            }
                        }
                        else {
                            throw $ex$;
                        }
                    }
                };
                CoreTaskContext.prototype.template = function (input) {
                    if (input == null) {
                        return null;
                    }
                    var cursor = 0;
                    var buffer = null;
                    var previousPos = -1;
                    while (cursor < input.length) {
                        var currentChar = input.charAt(cursor);
                        var previousChar = '0';
                        var nextChar = '0';
                        if (cursor > 0) {
                            previousChar = input.charAt(cursor - 1);
                        }
                        if (cursor + 1 < input.length) {
                            nextChar = input.charAt(cursor + 1);
                        }
                        if (currentChar == '{' && previousChar == '{') {
                            previousPos = cursor + 1;
                        }
                        else if (previousPos != -1 && currentChar == '}' && previousChar == '}') {
                            if (buffer == null) {
                                buffer = new java.lang.StringBuilder();
                                buffer.append(input.substring(0, previousPos - 2));
                            }
                            var contextKey = input.substring(previousPos, cursor - 1).trim();
                            if (contextKey.length > 0 && contextKey.charAt(0) == '=') {
                                var mathEngine = greycat.internal.task.math.CoreMathExpressionEngine.parse(contextKey.substring(1));
                                var value = mathEngine.eval(null, this, new java.util.HashMap());
                                var valueStr = value + "";
                                for (var i = valueStr.length - 1; i >= 0; i--) {
                                    if (valueStr.charAt(i) == '.') {
                                        valueStr = valueStr.substring(0, i);
                                        break;
                                    }
                                    else if (valueStr.charAt(i) != '0') {
                                        break;
                                    }
                                }
                                buffer.append(valueStr);
                            }
                            else {
                                var indexArray = -1;
                                if (contextKey.charAt(contextKey.length - 1) == ']') {
                                    var indexStart = -1;
                                    for (var i = contextKey.length - 3; i >= 0; i--) {
                                        if (contextKey.charAt(i) == '[') {
                                            indexStart = i + 1;
                                            break;
                                        }
                                    }
                                    if (indexStart != -1) {
                                        indexArray = greycat.internal.task.TaskHelper.parseInt(contextKey.substring(indexStart, contextKey.length - 1));
                                        contextKey = contextKey.substring(0, indexStart - 1);
                                        if (indexArray < 0) {
                                            throw new Error("Array index out of range: " + indexArray);
                                        }
                                    }
                                }
                                var foundVar = this.variable(contextKey);
                                if (foundVar == null) {
                                    switch (contextKey) {
                                        case "result":
                                            {
                                                foundVar = this.result();
                                            }
                                            break;
                                        case "time":
                                            {
                                                foundVar = this.wrap(this._time);
                                            }
                                            break;
                                        case "world":
                                            {
                                                foundVar = this.wrap(this._world);
                                            }
                                            break;
                                    }
                                }
                                if (foundVar != null) {
                                    if (foundVar.size() == 1 || indexArray != -1) {
                                        var toShow = null;
                                        if (indexArray == -1) {
                                            toShow = foundVar.get(0);
                                        }
                                        else {
                                            toShow = foundVar.get(indexArray);
                                        }
                                        buffer.append(toShow);
                                    }
                                    else {
                                        var it = foundVar.iterator();
                                        buffer.append("[");
                                        var isFirst = true;
                                        var next = it.next();
                                        while (next != null) {
                                            if (isFirst) {
                                                isFirst = false;
                                            }
                                            else {
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
                        }
                        else {
                            if (previousPos == -1 && buffer != null) {
                                if (currentChar == '{' && nextChar == '{') {
                                }
                                else {
                                    buffer.append(input.charAt(cursor));
                                }
                            }
                        }
                        cursor++;
                    }
                    if (buffer == null) {
                        return input;
                    }
                    else {
                        return buffer.toString();
                    }
                };
                CoreTaskContext.prototype.templates = function (inputs) {
                    if (inputs == null) {
                        return null;
                    }
                    var result = new Array(inputs.length);
                    for (var i = 0; i < inputs.length; i++) {
                        result[i] = this.template(inputs[i]);
                    }
                    return result;
                };
                CoreTaskContext.prototype.append = function (additionalOutput) {
                    if (this._output == null) {
                        this._output = new java.lang.StringBuilder();
                    }
                    this._output.append(additionalOutput);
                };
                CoreTaskContext.prototype.toString = function () {
                    return "{result:" + this._result.toString() + "}";
                };
                return CoreTaskContext;
            }());
            task_1.CoreTaskContext = CoreTaskContext;
            var CoreTaskReader = (function () {
                function CoreTaskReader(p_flat, p_offset) {
                    this._end = -1;
                    this.flat = p_flat;
                    this.offset = p_offset;
                }
                CoreTaskReader.prototype.available = function () {
                    return this.flat.length - this.offset;
                };
                CoreTaskReader.prototype.charAt = function (cursor) {
                    return this.flat.charAt(this.offset + cursor);
                };
                CoreTaskReader.prototype.extract = function (begin, end) {
                    return this.flat.substring(this.offset + begin, this.offset + end);
                };
                CoreTaskReader.prototype.markend = function (p_end) {
                    this._end = p_end;
                };
                CoreTaskReader.prototype.end = function () {
                    if (this._end == -1) {
                        return this.available();
                    }
                    else {
                        return this._end;
                    }
                };
                CoreTaskReader.prototype.slice = function (cursor) {
                    return new greycat.internal.task.CoreTaskReader(this.flat, cursor);
                };
                return CoreTaskReader;
            }());
            task_1.CoreTaskReader = CoreTaskReader;
            var CoreTaskResultIterator = (function () {
                function CoreTaskResultIterator(p_backend) {
                    this._current = new java.util.concurrent.atomic.AtomicInteger(0);
                    if (p_backend != null) {
                        this._backend = p_backend;
                    }
                    else {
                        this._backend = new Array(0);
                    }
                    this._size = this._backend.length;
                }
                CoreTaskResultIterator.prototype.next = function () {
                    var cursor = this._current.getAndIncrement();
                    if (cursor < this._size) {
                        return this._backend[cursor];
                    }
                    else {
                        return null;
                    }
                };
                CoreTaskResultIterator.prototype.nextWithIndex = function () {
                    var cursor = this._current.getAndIncrement();
                    if (cursor < this._size) {
                        if (this._backend[cursor] != null) {
                            return new greycat.utility.Tuple(cursor, this._backend[cursor]);
                        }
                        else {
                            return null;
                        }
                    }
                    else {
                        return null;
                    }
                };
                return CoreTaskResultIterator;
            }());
            task_1.CoreTaskResultIterator = CoreTaskResultIterator;
            var TaskHelper = (function () {
                function TaskHelper() {
                }
                TaskHelper.flatNodes = function (toFLat, strict) {
                    if (toFLat instanceof greycat.base.BaseNode) {
                        return [toFLat];
                    }
                    if (Array.isArray(toFLat)) {
                        var resAsArray = toFLat;
                        var nodes = new Array(0);
                        for (var i = 0; i < resAsArray.length; i++) {
                            if (resAsArray[i] instanceof greycat.base.BaseNode) {
                                var tmp = new Array(nodes.length + 1);
                                java.lang.System.arraycopy(nodes, 0, tmp, 0, nodes.length);
                                tmp[nodes.length] = resAsArray[i];
                                nodes = tmp;
                            }
                            else if (Array.isArray(resAsArray[i])) {
                                var innerNodes = greycat.internal.task.TaskHelper.flatNodes(resAsArray[i], strict);
                                var tmp = new Array(nodes.length + innerNodes.length);
                                java.lang.System.arraycopy(nodes, 0, tmp, 0, nodes.length);
                                java.lang.System.arraycopy(innerNodes, 0, tmp, nodes.length, innerNodes.length);
                                nodes = tmp;
                            }
                            else if (strict) {
                                throw new Error("[ActionAddRemoveToGlobalIndex] The array in result contains an element with wrong type. " + "Expected type: BaseNode. Actual type: " + resAsArray[i]);
                            }
                        }
                        return nodes;
                    }
                    else if (strict) {
                        throw new Error("[ActionAddRemoveToGlobalIndex] Wrong type of result. Expected type is BaseNode or an array of BaseNode." + "Actual type is " + toFLat);
                    }
                    return new Array(0);
                };
                TaskHelper.parseInt = function (s) {
                    return parseInt(s);
                };
                TaskHelper.serializeString = function (param, builder, singleQuote) {
                    if (singleQuote) {
                        builder.append("\'");
                    }
                    else {
                        builder.append("\"");
                    }
                    var escapteActivated = false;
                    var previousIsEscape = false;
                    if (param != null) {
                        for (var i = 0; i < param.length; i++) {
                            var current = param.charAt(i);
                            if (current == '\r' || current == '\n') {
                                if (!escapteActivated) {
                                    escapteActivated = true;
                                    builder.append(param.substring(0, i));
                                }
                            }
                            else if ((singleQuote && current == '\'') || (!singleQuote && current == '\"')) {
                                if (!escapteActivated) {
                                    escapteActivated = true;
                                    builder.append(param.substring(0, i));
                                }
                                if (!previousIsEscape) {
                                    builder.append('\\');
                                }
                                builder.append(param.charAt(i));
                            }
                            else {
                                if (escapteActivated) {
                                    builder.append(param.charAt(i));
                                }
                            }
                            previousIsEscape = (current == '\\');
                        }
                    }
                    if (!escapteActivated) {
                        builder.append(param);
                    }
                    if (singleQuote) {
                        builder.append("\'");
                    }
                    else {
                        builder.append("\"");
                    }
                };
                TaskHelper.serializeType = function (type, builder) {
                    builder.append(greycat.Type.typeName(type));
                };
                TaskHelper.serializeStringParams = function (params, builder) {
                    for (var i = 0; i < params.length; i++) {
                        if (i != 0) {
                            builder.append(greycat.Constants.TASK_PARAM_SEP);
                        }
                        greycat.internal.task.TaskHelper.serializeString(params[i], builder, true);
                    }
                };
                TaskHelper.serializeNameAndStringParams = function (name, params, builder) {
                    builder.append(name);
                    builder.append(greycat.Constants.TASK_PARAM_OPEN);
                    greycat.internal.task.TaskHelper.serializeStringParams(params, builder);
                    builder.append(greycat.Constants.TASK_PARAM_CLOSE);
                };
                return TaskHelper;
            }());
            task_1.TaskHelper = TaskHelper;
            var math;
            (function (math) {
                var CoreMathExpressionEngine = (function () {
                    function CoreMathExpressionEngine(expression) {
                        this._cacheAST = this.buildAST(this.shuntingYard(expression));
                    }
                    CoreMathExpressionEngine.parse = function (p_expression) {
                        return new greycat.internal.task.math.CoreMathExpressionEngine(p_expression);
                    };
                    CoreMathExpressionEngine.isNumber = function (st) {
                        return !isNaN(+st);
                    };
                    CoreMathExpressionEngine.isDigit = function (c) {
                        var cc = c.charCodeAt(0);
                        if (cc >= 0x30 && cc <= 0x39) {
                            return true;
                        }
                        return false;
                    };
                    CoreMathExpressionEngine.isLetter = function (c) {
                        var cc = c.charCodeAt(0);
                        if ((cc >= 0x41 && cc <= 0x5A) || (cc >= 0x61 && cc <= 0x7A)) {
                            return true;
                        }
                        return false;
                    };
                    CoreMathExpressionEngine.isWhitespace = function (c) {
                        var cc = c.charCodeAt(0);
                        if ((cc >= 0x0009 && cc <= 0x000D) || (cc == 0x0020) || (cc == 0x0085) || (cc == 0x00A0)) {
                            return true;
                        }
                        return false;
                    };
                    CoreMathExpressionEngine.prototype.shuntingYard = function (expression) {
                        var outputQueue = new java.util.ArrayList();
                        var stack = new java.util.Stack();
                        var tokenizer = new greycat.internal.task.math.MathExpressionTokenizer(expression);
                        var lastFunction = null;
                        var previousToken = null;
                        while (tokenizer.hasNext()) {
                            var token = tokenizer.next();
                            if (greycat.internal.task.math.MathEntities.getINSTANCE().functions.keySet().contains(token.toUpperCase())) {
                                stack.push(token);
                                lastFunction = token;
                            }
                            else if ("," === token) {
                                while (!stack.isEmpty() && !("(" === stack.peek())) {
                                    outputQueue.add(stack.pop());
                                }
                                if (stack.isEmpty()) {
                                    throw new Error("Parse error for function '" + lastFunction + "'");
                                }
                            }
                            else if (greycat.internal.task.math.MathEntities.getINSTANCE().operators.keySet().contains(token)) {
                                var o1 = greycat.internal.task.math.MathEntities.getINSTANCE().operators.get(token);
                                var token2 = stack.isEmpty() ? null : stack.peek();
                                while (greycat.internal.task.math.MathEntities.getINSTANCE().operators.keySet().contains(token2) && ((o1.isLeftAssoc() && o1.getPrecedence() <= greycat.internal.task.math.MathEntities.getINSTANCE().operators.get(token2).getPrecedence()) || (o1.getPrecedence() < greycat.internal.task.math.MathEntities.getINSTANCE().operators.get(token2).getPrecedence()))) {
                                    outputQueue.add(stack.pop());
                                    token2 = stack.isEmpty() ? null : stack.peek();
                                }
                                stack.push(token);
                            }
                            else if ("(" === token) {
                                if (previousToken != null) {
                                    if (greycat.internal.task.math.CoreMathExpressionEngine.isNumber(previousToken)) {
                                        throw new Error("Missing operator at character position " + tokenizer.getPos());
                                    }
                                }
                                stack.push(token);
                            }
                            else if (")" === token) {
                                while (!stack.isEmpty() && !("(" === stack.peek())) {
                                    outputQueue.add(stack.pop());
                                }
                                if (stack.isEmpty()) {
                                    throw new Error("Mismatched parentheses");
                                }
                                stack.pop();
                                if (!stack.isEmpty() && greycat.internal.task.math.MathEntities.getINSTANCE().functions.keySet().contains(stack.peek().toUpperCase())) {
                                    outputQueue.add(stack.pop());
                                }
                            }
                            else {
                                outputQueue.add(token);
                            }
                            previousToken = token;
                        }
                        while (!stack.isEmpty()) {
                            var element = stack.pop();
                            if ("(" === element || ")" === element) {
                                throw new Error("Mismatched parentheses");
                            }
                            outputQueue.add(element);
                        }
                        return outputQueue;
                    };
                    CoreMathExpressionEngine.prototype.eval = function (context, taskContext, variables) {
                        if (this._cacheAST == null) {
                            throw new Error("Call parse before");
                        }
                        var stack = new java.util.Stack();
                        for (var ii = 0; ii < this._cacheAST.length; ii++) {
                            var mathToken = this._cacheAST[ii];
                            switch (mathToken.type()) {
                                case 0:
                                    var v1 = stack.pop();
                                    var v2 = stack.pop();
                                    var castedOp = mathToken;
                                    stack.push(castedOp.eval(v2, v1));
                                    break;
                                case 1:
                                    var castedFunction = mathToken;
                                    var p = new Float64Array(castedFunction.getNumParams());
                                    for (var i = castedFunction.getNumParams() - 1; i >= 0; i--) {
                                        p[i] = stack.pop();
                                    }
                                    stack.push(castedFunction.eval(p));
                                    break;
                                case 2:
                                    var castedDouble = mathToken;
                                    stack.push(castedDouble.content());
                                    break;
                                case 3:
                                    var castedFreeToken = mathToken;
                                    var resolvedVar = null;
                                    if (variables != null) {
                                        resolvedVar = variables.get(castedFreeToken.content());
                                    }
                                    if (resolvedVar != null) {
                                        stack.push(resolvedVar);
                                    }
                                    else {
                                        if ("TIME" === castedFreeToken.content()) {
                                            stack.push(context.time());
                                        }
                                        else {
                                            var tokenName = castedFreeToken.content().trim();
                                            var resolved = null;
                                            var cleanName = null;
                                            if (context != null) {
                                                if (tokenName.length > 0 && tokenName.charAt(0) == '{' && tokenName.charAt(tokenName.length - 1) == '}') {
                                                    resolved = context.get(castedFreeToken.content().substring(1, tokenName.length - 1));
                                                    cleanName = castedFreeToken.content().substring(1, tokenName.length - 1);
                                                }
                                                else {
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
                                                        var indexStart = -1;
                                                        var indexArray = -1;
                                                        for (var i = tokenName.length - 3; i >= 0; i--) {
                                                            if (tokenName.charAt(i) == '[') {
                                                                indexStart = i + 1;
                                                                break;
                                                            }
                                                        }
                                                        if (indexStart != -1) {
                                                            indexArray = this.parseInt(tokenName.substring(indexStart, tokenName.length - 1));
                                                            tokenName = tokenName.substring(0, indexStart - 1);
                                                        }
                                                        var varRes = taskContext.variable(tokenName);
                                                        if (varRes == null && tokenName === "result") {
                                                            varRes = taskContext.result();
                                                        }
                                                        if (varRes != null && varRes.size() > indexArray) {
                                                            resolved = varRes.get(indexArray);
                                                        }
                                                    }
                                                    else {
                                                        var varRes = taskContext.variable(tokenName);
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
                                                var resultAsDouble = this.parseDouble(resolved.toString());
                                                variables.put(cleanName, resultAsDouble);
                                                var valueString = resolved.toString();
                                                if (valueString === "true") {
                                                    stack.push(1.0);
                                                }
                                                else if (valueString === "false") {
                                                    stack.push(0.0);
                                                }
                                                else {
                                                    try {
                                                        stack.push(resultAsDouble);
                                                    }
                                                    catch ($ex$) {
                                                        if ($ex$ instanceof Error) {
                                                            var e = $ex$;
                                                            {
                                                            }
                                                        }
                                                        else {
                                                            throw $ex$;
                                                        }
                                                    }
                                                }
                                            }
                                            else {
                                                throw new Error("Unknow variable for name " + castedFreeToken.content());
                                            }
                                        }
                                    }
                                    break;
                            }
                        }
                        var result = stack.pop();
                        if (result == null) {
                            return 0;
                        }
                        else {
                            return result;
                        }
                    };
                    CoreMathExpressionEngine.prototype.buildAST = function (rpn) {
                        var result = new Array(rpn.size());
                        for (var ii = 0; ii < rpn.size(); ii++) {
                            var token = rpn.get(ii);
                            if (greycat.internal.task.math.MathEntities.getINSTANCE().operators.keySet().contains(token)) {
                                result[ii] = greycat.internal.task.math.MathEntities.getINSTANCE().operators.get(token);
                            }
                            else if (greycat.internal.task.math.MathEntities.getINSTANCE().functions.keySet().contains(token.toUpperCase())) {
                                result[ii] = greycat.internal.task.math.MathEntities.getINSTANCE().functions.get(token.toUpperCase());
                            }
                            else {
                                if (token.length > 0 && greycat.internal.task.math.CoreMathExpressionEngine.isLetter(token.charAt(0))) {
                                    result[ii] = new greycat.internal.task.math.MathFreeToken(token);
                                }
                                else {
                                    try {
                                        var parsed = this.parseDouble(token);
                                        result[ii] = new greycat.internal.task.math.MathDoubleToken(parsed);
                                    }
                                    catch ($ex$) {
                                        if ($ex$ instanceof Error) {
                                            var e = $ex$;
                                            {
                                                result[ii] = new greycat.internal.task.math.MathFreeToken(token);
                                            }
                                        }
                                        else {
                                            throw $ex$;
                                        }
                                    }
                                }
                            }
                        }
                        return result;
                    };
                    CoreMathExpressionEngine.prototype.parseDouble = function (val) {
                        return parseFloat(val);
                    };
                    CoreMathExpressionEngine.prototype.parseInt = function (val) {
                        return parseInt(val);
                    };
                    return CoreMathExpressionEngine;
                }());
                CoreMathExpressionEngine.decimalSeparator = '.';
                CoreMathExpressionEngine.minusSign = '-';
                math.CoreMathExpressionEngine = CoreMathExpressionEngine;
                var MathConditional = (function () {
                    function MathConditional(mathExpression) {
                        this._expression = mathExpression;
                        this._engine = greycat.internal.task.math.CoreMathExpressionEngine.parse(mathExpression);
                    }
                    MathConditional.prototype.conditional = function () {
                        var _this = this;
                        return function (ctx) {
                            {
                                var variables = new java.util.HashMap();
                                variables.put("PI", Math.PI);
                                variables.put("TRUE", 1.0);
                                variables.put("FALSE", 0.0);
                                return (_this._engine.eval(null, ctx, variables) >= 0.5);
                            }
                        };
                    };
                    MathConditional.prototype.toString = function () {
                        return "cond(\'" + this._expression + "\')";
                    };
                    return MathConditional;
                }());
                math.MathConditional = MathConditional;
                var MathDoubleToken = (function () {
                    function MathDoubleToken(_content) {
                        this._content = _content;
                    }
                    MathDoubleToken.prototype.type = function () {
                        return 2;
                    };
                    MathDoubleToken.prototype.content = function () {
                        return this._content;
                    };
                    return MathDoubleToken;
                }());
                math.MathDoubleToken = MathDoubleToken;
                var MathEntities = (function () {
                    function MathEntities() {
                        this.operators = new java.util.HashMap();
                        this.operators.put("+", new greycat.internal.task.math.MathOperation("+", 20, true));
                        this.operators.put("-", new greycat.internal.task.math.MathOperation("-", 20, true));
                        this.operators.put("*", new greycat.internal.task.math.MathOperation("*", 30, true));
                        this.operators.put("/", new greycat.internal.task.math.MathOperation("/", 30, true));
                        this.operators.put("%", new greycat.internal.task.math.MathOperation("%", 30, true));
                        this.operators.put("^", new greycat.internal.task.math.MathOperation("^", 40, false));
                        this.operators.put("&&", new greycat.internal.task.math.MathOperation("&&", 4, false));
                        this.operators.put("||", new greycat.internal.task.math.MathOperation("||", 2, false));
                        this.operators.put(">", new greycat.internal.task.math.MathOperation(">", 10, false));
                        this.operators.put(">=", new greycat.internal.task.math.MathOperation(">=", 10, false));
                        this.operators.put("<", new greycat.internal.task.math.MathOperation("<", 10, false));
                        this.operators.put("<=", new greycat.internal.task.math.MathOperation("<=", 10, false));
                        this.operators.put("==", new greycat.internal.task.math.MathOperation("==", 7, false));
                        this.operators.put("!=", new greycat.internal.task.math.MathOperation("!=", 7, false));
                        this.functions = new java.util.HashMap();
                        this.functions.put("NOT", new greycat.internal.task.math.MathFunction("NOT", 1));
                        this.functions.put("IF", new greycat.internal.task.math.MathFunction("IF", 3));
                        this.functions.put("RAND", new greycat.internal.task.math.MathFunction("RAND", 0));
                        this.functions.put("SIN", new greycat.internal.task.math.MathFunction("SIN", 1));
                        this.functions.put("COS", new greycat.internal.task.math.MathFunction("COS", 1));
                        this.functions.put("TAN", new greycat.internal.task.math.MathFunction("TAN", 1));
                        this.functions.put("ASIN", new greycat.internal.task.math.MathFunction("ASIN", 1));
                        this.functions.put("ACOS", new greycat.internal.task.math.MathFunction("ACOS", 1));
                        this.functions.put("ATAN", new greycat.internal.task.math.MathFunction("ATAN", 1));
                        this.functions.put("MAX", new greycat.internal.task.math.MathFunction("MAX", 2));
                        this.functions.put("MIN", new greycat.internal.task.math.MathFunction("MIN", 2));
                        this.functions.put("ABS", new greycat.internal.task.math.MathFunction("ABS", 1));
                        this.functions.put("LOG", new greycat.internal.task.math.MathFunction("LOG", 1));
                        this.functions.put("ROUND", new greycat.internal.task.math.MathFunction("ROUND", 2));
                        this.functions.put("FLOOR", new greycat.internal.task.math.MathFunction("FLOOR", 1));
                        this.functions.put("CEILING", new greycat.internal.task.math.MathFunction("CEILING", 1));
                        this.functions.put("SQRT", new greycat.internal.task.math.MathFunction("SQRT", 1));
                        this.functions.put("SECONDS", new greycat.internal.task.math.MathFunction("SECONDS", 1));
                        this.functions.put("MINUTES", new greycat.internal.task.math.MathFunction("MINUTES", 1));
                        this.functions.put("HOURS", new greycat.internal.task.math.MathFunction("HOURS", 1));
                        this.functions.put("DAY", new greycat.internal.task.math.MathFunction("DAY", 1));
                        this.functions.put("MONTH", new greycat.internal.task.math.MathFunction("MONTH", 1));
                        this.functions.put("YEAR", new greycat.internal.task.math.MathFunction("YEAR", 1));
                        this.functions.put("DAYOFWEEK", new greycat.internal.task.math.MathFunction("DAYOFWEEK", 1));
                    }
                    MathEntities.getINSTANCE = function () {
                        if (MathEntities.INSTANCE == null) {
                            MathEntities.INSTANCE = new greycat.internal.task.math.MathEntities();
                        }
                        return MathEntities.INSTANCE;
                    };
                    return MathEntities;
                }());
                MathEntities.INSTANCE = null;
                math.MathEntities = MathEntities;
                var MathExpressionTokenizer = (function () {
                    function MathExpressionTokenizer(input) {
                        this.pos = 0;
                        this.input = input.trim();
                    }
                    MathExpressionTokenizer.prototype.hasNext = function () {
                        return (this.pos < this.input.length);
                    };
                    MathExpressionTokenizer.prototype.peekNextChar = function () {
                        if (this.pos < (this.input.length - 1)) {
                            return this.input.charAt(this.pos + 1);
                        }
                        else {
                            return '\0';
                        }
                    };
                    MathExpressionTokenizer.prototype.next = function () {
                        var token = new java.lang.StringBuilder();
                        if (this.pos >= this.input.length) {
                            return this.previousToken = null;
                        }
                        var ch = this.input.charAt(this.pos);
                        while (greycat.internal.task.math.CoreMathExpressionEngine.isWhitespace(ch) && this.pos < this.input.length) {
                            ch = this.input.charAt(++this.pos);
                        }
                        if (greycat.internal.task.math.CoreMathExpressionEngine.isDigit(ch)) {
                            while ((greycat.internal.task.math.CoreMathExpressionEngine.isDigit(ch) || ch == greycat.internal.task.math.CoreMathExpressionEngine.decimalSeparator) && (this.pos < this.input.length)) {
                                token.append(this.input.charAt(this.pos++));
                                ch = this.pos == this.input.length ? '\0' : this.input.charAt(this.pos);
                            }
                        }
                        else if (ch == greycat.internal.task.math.CoreMathExpressionEngine.minusSign && greycat.internal.task.math.CoreMathExpressionEngine.isDigit(this.peekNextChar()) && ("(" === this.previousToken || "," === this.previousToken || this.previousToken == null || greycat.internal.task.math.MathEntities.getINSTANCE().operators.keySet().contains(this.previousToken))) {
                            token.append(greycat.internal.task.math.CoreMathExpressionEngine.minusSign);
                            this.pos++;
                            token.append(this.next());
                        }
                        else if (greycat.internal.task.math.CoreMathExpressionEngine.isLetter(ch) || (ch == '_') || (ch == '{') || (ch == '}') || (ch == '$')) {
                            while ((greycat.internal.task.math.CoreMathExpressionEngine.isLetter(ch) || greycat.internal.task.math.CoreMathExpressionEngine.isDigit(ch) || (ch == '_') || (ch == '{') || (ch == '}') || (ch == '$')) && (this.pos < this.input.length)) {
                                token.append(this.input.charAt(this.pos++));
                                ch = this.pos == this.input.length ? '\0' : this.input.charAt(this.pos);
                            }
                            if (this.pos < this.input.length) {
                                if (this.input.charAt(this.pos) == '[') {
                                    token.append(this.input.charAt(this.pos++));
                                    ch = this.pos == this.input.length ? '\0' : this.input.charAt(this.pos);
                                    while (greycat.internal.task.math.CoreMathExpressionEngine.isDigit(ch) && this.pos < this.input.length) {
                                        token.append(this.input.charAt(this.pos++));
                                        ch = this.pos == this.input.length ? '\0' : this.input.charAt(this.pos);
                                    }
                                    if (this.input.charAt(this.pos) != ']') {
                                        throw new Error("Error in array definition '" + token + "' at position " + (this.pos - token.length + 1));
                                    }
                                    else {
                                        token.append(this.input.charAt(this.pos++));
                                    }
                                }
                            }
                        }
                        else if (ch == '(' || ch == ')' || ch == ',') {
                            token.append(ch);
                            this.pos++;
                        }
                        else {
                            while (!greycat.internal.task.math.CoreMathExpressionEngine.isLetter(ch) && !greycat.internal.task.math.CoreMathExpressionEngine.isDigit(ch) && ch != '_' && !greycat.internal.task.math.CoreMathExpressionEngine.isWhitespace(ch) && ch != '(' && ch != ')' && ch != ',' && (ch != '{') && (ch != '}') && (ch != '$') && (this.pos < this.input.length)) {
                                token.append(this.input.charAt(this.pos));
                                this.pos++;
                                ch = this.pos == this.input.length ? '\0' : this.input.charAt(this.pos);
                                if (ch == greycat.internal.task.math.CoreMathExpressionEngine.minusSign) {
                                    break;
                                }
                            }
                            if (!greycat.internal.task.math.MathEntities.getINSTANCE().operators.keySet().contains(token.toString())) {
                                throw new Error("Unknown operator '" + token + "' at position " + (this.pos - token.length + 1));
                            }
                        }
                        return this.previousToken = token.toString();
                    };
                    MathExpressionTokenizer.prototype.getPos = function () {
                        return this.pos;
                    };
                    return MathExpressionTokenizer;
                }());
                math.MathExpressionTokenizer = MathExpressionTokenizer;
                var MathFreeToken = (function () {
                    function MathFreeToken(content) {
                        this._content = content;
                    }
                    MathFreeToken.prototype.content = function () {
                        return this._content;
                    };
                    MathFreeToken.prototype.type = function () {
                        return 3;
                    };
                    return MathFreeToken;
                }());
                math.MathFreeToken = MathFreeToken;
                var MathFunction = (function () {
                    function MathFunction(name, numParams) {
                        this.name = name.toUpperCase();
                        this.numParams = numParams;
                    }
                    MathFunction.prototype.getName = function () {
                        return this.name;
                    };
                    MathFunction.prototype.getNumParams = function () {
                        return this.numParams;
                    };
                    MathFunction.prototype.eval = function (p) {
                        if (this.name === "NOT") {
                            return (p[0] == 0) ? 1 : 0;
                        }
                        else if (this.name === "IF") {
                            return !(p[0] == 0) ? p[1] : p[2];
                        }
                        else if (this.name === "RAND") {
                            return Math.random();
                        }
                        else if (this.name === "SIN") {
                            return Math.sin(p[0]);
                        }
                        else if (this.name === "COS") {
                            return Math.cos(p[0]);
                        }
                        else if (this.name === "TAN") {
                            return Math.tan(p[0]);
                        }
                        else if (this.name === "ASIN") {
                            return Math.asin(p[0]);
                        }
                        else if (this.name === "ACOS") {
                            return Math.acos(p[0]);
                        }
                        else if (this.name === "ATAN") {
                            return Math.atan(p[0]);
                        }
                        else if (this.name === "MAX") {
                            return p[0] > p[1] ? p[0] : p[1];
                        }
                        else if (this.name === "MIN") {
                            return p[0] < p[1] ? p[0] : p[1];
                        }
                        else if (this.name === "ABS") {
                            return Math.abs(p[0]);
                        }
                        else if (this.name === "LOG") {
                            return Math.log(p[0]);
                        }
                        else if (this.name === "ROUND") {
                            var factor = Math.pow(10, p[1]);
                            var value = p[0] * factor;
                            var tmp = Math.round(value);
                            return tmp / factor;
                        }
                        else if (this.name === "FLOOR") {
                            return Math.floor(p[0]);
                        }
                        else if (this.name === "CEILING") {
                            return Math.ceil(p[0]);
                        }
                        else if (this.name === "SQRT") {
                            return Math.sqrt(p[0]);
                        }
                        else if (this.name === "SECONDS") {
                            return this.date_to_seconds(p[0]);
                        }
                        else if (this.name === "MINUTES") {
                            return this.date_to_minutes(p[0]);
                        }
                        else if (this.name === "HOURS") {
                            return this.date_to_hours(p[0]);
                        }
                        else if (this.name === "DAY") {
                            return this.date_to_days(p[0]);
                        }
                        else if (this.name === "MONTH") {
                            return this.date_to_months(p[0]);
                        }
                        else if (this.name === "YEAR") {
                            return this.date_to_year(p[0]);
                        }
                        else if (this.name === "DAYOFWEEK") {
                            return this.date_to_dayofweek(p[0]);
                        }
                        return 0;
                    };
                    MathFunction.prototype.date_to_seconds = function (value) {
                        var date = new Date(value);
                        return date.getSeconds();
                    };
                    MathFunction.prototype.date_to_minutes = function (value) {
                        var date = new Date(value);
                        return date.getMinutes();
                    };
                    MathFunction.prototype.date_to_hours = function (value) {
                        var date = new Date(value);
                        return date.getHours();
                    };
                    MathFunction.prototype.date_to_days = function (value) {
                        var date = new Date(value);
                        return date.getDate();
                    };
                    MathFunction.prototype.date_to_months = function (value) {
                        var date = new Date(value);
                        return date.getMonth();
                    };
                    MathFunction.prototype.date_to_year = function (value) {
                        var date = new Date(value);
                        return date.getFullYear();
                    };
                    MathFunction.prototype.date_to_dayofweek = function (value) {
                        var date = new Date(value);
                        return date.getDay();
                    };
                    MathFunction.prototype.type = function () {
                        return 1;
                    };
                    return MathFunction;
                }());
                math.MathFunction = MathFunction;
                var MathOperation = (function () {
                    function MathOperation(oper, precedence, leftAssoc) {
                        this.oper = oper;
                        this.precedence = precedence;
                        this.leftAssoc = leftAssoc;
                    }
                    MathOperation.prototype.getOper = function () {
                        return this.oper;
                    };
                    MathOperation.prototype.getPrecedence = function () {
                        return this.precedence;
                    };
                    MathOperation.prototype.isLeftAssoc = function () {
                        return this.leftAssoc;
                    };
                    MathOperation.prototype.eval = function (v1, v2) {
                        if (this.oper === "+") {
                            return v1 + v2;
                        }
                        else if (this.oper === "-") {
                            return v1 - v2;
                        }
                        else if (this.oper === "*") {
                            return v1 * v2;
                        }
                        else if (this.oper === "/") {
                            return v1 / v2;
                        }
                        else if (this.oper === "%") {
                            return v1 % v2;
                        }
                        else if (this.oper === "^") {
                            return Math.pow(v1, v2);
                        }
                        else if (this.oper === "&&") {
                            var b1 = !(v1 == 0);
                            var b2 = !(v2 == 0);
                            return b1 && b2 ? 1 : 0;
                        }
                        else if (this.oper === "||") {
                            var b1 = !(v1 == 0);
                            var b2 = !(v2 == 0);
                            return b1 || b2 ? 1 : 0;
                        }
                        else if (this.oper === ">") {
                            return v1 > v2 ? 1 : 0;
                        }
                        else if (this.oper === ">=") {
                            return v1 >= v2 ? 1 : 0;
                        }
                        else if (this.oper === "<") {
                            return v1 < v2 ? 1 : 0;
                        }
                        else if (this.oper === "<=") {
                            return v1 <= v2 ? 1 : 0;
                        }
                        else if (this.oper === "==") {
                            return v1 == v2 ? 1 : 0;
                        }
                        else if (this.oper === "!=") {
                            return v1 != v2 ? 1 : 0;
                        }
                        return 0;
                    };
                    MathOperation.prototype.type = function () {
                        return 0;
                    };
                    return MathOperation;
                }());
                math.MathOperation = MathOperation;
            })(math = task_1.math || (task_1.math = {}));
        })(task = internal.task || (internal.task = {}));
    })(internal = greycat.internal || (greycat.internal = {}));
    var plugin;
    (function (plugin) {
        var SchedulerAffinity = (function () {
            function SchedulerAffinity() {
            }
            return SchedulerAffinity;
        }());
        SchedulerAffinity.SAME_THREAD = 0;
        SchedulerAffinity.ANY_LOCAL_THREAD = 1;
        SchedulerAffinity.OTHER_LOCAL_THREAD = 2;
        SchedulerAffinity.ANY_REMOTE_THREAD = 3;
        plugin.SchedulerAffinity = SchedulerAffinity;
    })(plugin = greycat.plugin || (greycat.plugin = {}));
    var scheduler;
    (function (scheduler) {
        var JobQueue = (function () {
            function JobQueue() {
                this.first = null;
                this.last = null;
            }
            JobQueue.prototype.add = function (item) {
                var elem = new greycat.scheduler.JobQueue.JobQueueElem(item, null);
                if (this.first == null) {
                    this.first = elem;
                    this.last = elem;
                }
                else {
                    this.last._next = elem;
                    this.last = elem;
                }
            };
            JobQueue.prototype.poll = function () {
                var value = this.first;
                this.first = this.first._next;
                return value._ptr;
            };
            return JobQueue;
        }());
        scheduler.JobQueue = JobQueue;
        (function (JobQueue) {
            var JobQueueElem = (function () {
                function JobQueueElem(ptr, next) {
                    this._ptr = ptr;
                    this._next = next;
                }
                return JobQueueElem;
            }());
            JobQueue.JobQueueElem = JobQueueElem;
        })(JobQueue = scheduler.JobQueue || (scheduler.JobQueue = {}));
        var NoopScheduler = (function () {
            function NoopScheduler() {
            }
            NoopScheduler.prototype.dispatch = function (affinity, job) {
                job();
            };
            NoopScheduler.prototype.start = function () { };
            NoopScheduler.prototype.stop = function () { };
            NoopScheduler.prototype.workers = function () {
                return 1;
            };
            return NoopScheduler;
        }());
        scheduler.NoopScheduler = NoopScheduler;
        var TrampolineScheduler = (function () {
            function TrampolineScheduler() {
                this.queue = new JobQueue();
                this.wip = new java.util.concurrent.atomic.AtomicInteger(0);
            }
            TrampolineScheduler.prototype.dispatch = function (affinity, job) {
                this.queue.add(job);
                if (this.wip.getAndIncrement() == 0) {
                    do {
                        var polled = this.queue.poll();
                        if (polled != null) {
                            polled();
                        }
                    } while (this.wip.decrementAndGet() > 0);
                }
            };
            TrampolineScheduler.prototype.start = function () { };
            TrampolineScheduler.prototype.stop = function () { };
            TrampolineScheduler.prototype.workers = function () {
                return 1;
            };
            return TrampolineScheduler;
        }());
        scheduler.TrampolineScheduler = TrampolineScheduler;
    })(scheduler = greycat.scheduler || (greycat.scheduler = {}));
    var utility;
    (function (utility) {
        var Base64 = (function () {
            function Base64() {
            }
            Base64.encodeLongToBuffer = function (l, buffer) {
                var empty = true;
                var tmp = l;
                if (l < 0) {
                    tmp = -tmp;
                }
                for (var i = 47; i >= 5; i -= 6) {
                    if (!(empty && ((tmp / Base64.powTwo[i]) & 0x3F) == 0)) {
                        empty = false;
                        buffer.write(Base64.dictionary[(tmp / Base64.powTwo[i]) & 0x3F]);
                    }
                }
                buffer.write(Base64.dictionary[(tmp & 0x1F) * 2 + (l < 0 ? 1 : 0)]);
            };
            Base64.encodeIntToBuffer = function (l, buffer) {
                var empty = true;
                var tmp = l;
                if (l < 0) {
                    tmp = -tmp;
                }
                for (var i = 29; i >= 5; i -= 6) {
                    if (!(empty && ((tmp / Base64.powTwo[i]) & 0x3F) == 0)) {
                        empty = false;
                        buffer.write(Base64.dictionary[(tmp / Base64.powTwo[i]) & 0x3F]);
                    }
                }
                buffer.write(Base64.dictionary[(tmp & 0x1F) * 2 + (l < 0 ? 1 : 0)]);
            };
            Base64.decodeToLong = function (s) {
                return Base64.decodeToLongWithBounds(s, 0, s.length());
            };
            Base64.decodeToLongWithBounds = function (s, offsetBegin, offsetEnd) {
                var result = Long.ZERO;
                result = result.add(Base64.longIndexes[Base64.dictionary.indexOf(s.read((offsetEnd - 1))) & 0xFF].shiftRightUnsigned(1));
                for (var i = 1; i < (offsetEnd - offsetBegin); i++) {
                    result = result.add(Base64.longIndexes[Base64.dictionary.indexOf(s.read((offsetEnd - 1) - i)) & 0xFF].shiftLeft((6 * i) - 1));
                }
                if (((Base64.dictionary.indexOf(s.read((offsetEnd - 1))) & 0xFF) & 0x1) != 0) {
                    result = result.mul(-1);
                }
                return result.toNumber();
            };
            Base64.decodeToInt = function (s) {
                return Base64.decodeToIntWithBounds(s, 0, s.length());
            };
            Base64.decodeToIntWithBounds = function (s, offsetBegin, offsetEnd) {
                var result = 0;
                result += (Base64.dictionary.indexOf(s.read((offsetEnd - 1))) & 0xFF) / 2;
                for (var i = 1; i < (offsetEnd - offsetBegin); i++) {
                    result += (Base64.dictionary.indexOf(s.read((offsetEnd - 1) - i)) & 0xFF) * Base64.powTwo[(6 * i) - 1];
                }
                if (((Base64.dictionary.indexOf(s.read((offsetEnd - 1))) & 0xFF) & 0x1) != 0) {
                    result = -result;
                }
                return result;
            };
            Base64.encodeDoubleToBuffer = function (d, buffer) {
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
            };
            Base64.decodeToDouble = function (s) {
                return Base64.decodeToDoubleWithBounds(s, 0, s.length());
            };
            Base64.decodeToDoubleWithBounds = function (s, offsetBegin, offsetEnd) {
                var signAndExp = ((Base64.dictionary.indexOf(s.read(offsetBegin)) & 0xFF) * 64) + (Base64.dictionary.indexOf(s.read(offsetBegin + 1)) & 0xFF);
                var sign = ((signAndExp & 0x800) != 0 ? -1 : 1);
                var exp = signAndExp & 0x7FF;
                //Mantisse
                var mantissaBits = 0;
                for (var i = 2; i < (offsetEnd - offsetBegin); i++) {
                    mantissaBits += (Base64.dictionary.indexOf(s.read(offsetBegin + i)) & 0xFF) * Base64.powTwo[48 - (6 * (i - 2))];
                }
                return (exp != 0) ? sign * Math.pow(2, exp - 1023) * (1 + (mantissaBits / Math.pow(2, 52))) : sign * Math.pow(2, -1022) * (0 + (mantissaBits / Math.pow(2, 52)));
            };
            Base64.encodeBoolArrayToBuffer = function (boolArr, buffer) {
                var tmpVal = 0;
                for (var i = 0; i < boolArr.length; i++) {
                    tmpVal = tmpVal | ((boolArr[i] ? 1 : 0) * Base64.powTwo[i % 6]);
                    if (i % 6 == 5 || i == boolArr.length - 1) {
                        buffer.write(Base64.dictionary[tmpVal]);
                        tmpVal = 0;
                    }
                }
            };
            Base64.decodeBoolArray = function (s, arraySize) {
                return Base64.decodeToBoolArrayWithBounds(s, 0, s.length(), arraySize);
            };
            Base64.decodeToBoolArrayWithBounds = function (s, offsetBegin, offsetEnd, arraySize) {
                var resultTmp = [];
                for (var i = 0; i < (offsetEnd - offsetBegin); i++) {
                    var bitarray = Base64.dictionary.indexOf(s.read(offsetBegin + i)) & 0xFF;
                    for (var bit_i = 0; bit_i < 6; bit_i++) {
                        if ((6 * i) + bit_i < arraySize) {
                            resultTmp[(6 * i) + bit_i] = (bitarray & (1 * Base64.powTwo[bit_i])) != 0;
                        }
                        else {
                            break;
                        }
                    }
                }
                return resultTmp;
            };
            Base64.encodeStringToBuffer = function (s, buffer) {
                var sLength = s.length;
                var currentSourceChar;
                var currentEncodedChar = 0;
                var freeBitsInCurrentChar = 6;
                for (var charIdx = 0; charIdx < sLength; charIdx++) {
                    currentSourceChar = s.charCodeAt(charIdx);
                    if (freeBitsInCurrentChar == 6) {
                        buffer.write(Base64.dictionary[(currentSourceChar / 4) & 0x3F]);
                        currentEncodedChar = (currentSourceChar & 0x3) * 16;
                        freeBitsInCurrentChar = 4;
                    }
                    else if (freeBitsInCurrentChar == 4) {
                        buffer.write(Base64.dictionary[(currentEncodedChar | ((currentSourceChar / 16) & 0xF)) & 0x3F]);
                        currentEncodedChar = (currentSourceChar & 0xF) * 4;
                        freeBitsInCurrentChar = 2;
                    }
                    else if (freeBitsInCurrentChar == 2) {
                        buffer.write(Base64.dictionary[(currentEncodedChar | ((currentSourceChar / 64) & 0x3)) & 0x3F]);
                        buffer.write(Base64.dictionary[currentSourceChar & 0x3F]);
                        freeBitsInCurrentChar = 6;
                    }
                }
                if (freeBitsInCurrentChar != 6) {
                    buffer.write(Base64.dictionary[currentEncodedChar]);
                }
            };
            Base64.decodeString = function (s) {
                return Base64.decodeToStringWithBounds(s, 0, s.length());
            };
            Base64.decodeToStringWithBounds = function (s, offsetBegin, offsetEnd) {
                var result = "";
                var currentSourceChar;
                var currentDecodedChar = 0;
                var freeBitsInCurrentChar = 8;
                for (var charIdx = offsetBegin; charIdx < offsetEnd; charIdx++) {
                    currentSourceChar = Base64.dictionary.indexOf(s.read(charIdx));
                    if (freeBitsInCurrentChar == 8) {
                        currentDecodedChar = currentSourceChar * 4;
                        freeBitsInCurrentChar = 2;
                    }
                    else if (freeBitsInCurrentChar == 2) {
                        result += String.fromCharCode(currentDecodedChar | (currentSourceChar / 16));
                        currentDecodedChar = (currentSourceChar & 0xF) * 16;
                        freeBitsInCurrentChar = 4;
                    }
                    else if (freeBitsInCurrentChar == 4) {
                        result += String.fromCharCode(currentDecodedChar | (currentSourceChar / 4));
                        currentDecodedChar = (currentSourceChar & 0x3) * 64;
                        freeBitsInCurrentChar = 6;
                    }
                    else if (freeBitsInCurrentChar == 6) {
                        result += String.fromCharCode(currentDecodedChar | currentSourceChar);
                        freeBitsInCurrentChar = 8;
                    }
                }
                return result;
            };
            return Base64;
        }());
        Base64.dictionary = ['A'.charCodeAt(0), 'B'.charCodeAt(0), 'C'.charCodeAt(0), 'D'.charCodeAt(0), 'E'.charCodeAt(0), 'F'.charCodeAt(0), 'G'.charCodeAt(0), 'H'.charCodeAt(0), 'I'.charCodeAt(0), 'J'.charCodeAt(0), 'K'.charCodeAt(0), 'L'.charCodeAt(0), 'M'.charCodeAt(0), 'N'.charCodeAt(0), 'O'.charCodeAt(0), 'P'.charCodeAt(0), 'Q'.charCodeAt(0), 'R'.charCodeAt(0), 'S'.charCodeAt(0), 'T'.charCodeAt(0), 'U'.charCodeAt(0), 'V'.charCodeAt(0), 'W'.charCodeAt(0), 'X'.charCodeAt(0), 'Y'.charCodeAt(0), 'Z'.charCodeAt(0), 'a'.charCodeAt(0), 'b'.charCodeAt(0), 'c'.charCodeAt(0), 'd'.charCodeAt(0), 'e'.charCodeAt(0), 'f'.charCodeAt(0), 'g'.charCodeAt(0), 'h'.charCodeAt(0), 'i'.charCodeAt(0), 'j'.charCodeAt(0), 'k'.charCodeAt(0), 'l'.charCodeAt(0), 'm'.charCodeAt(0), 'n'.charCodeAt(0), 'o'.charCodeAt(0), 'p'.charCodeAt(0), 'q'.charCodeAt(0), 'r'.charCodeAt(0), 's'.charCodeAt(0), 't'.charCodeAt(0), 'u'.charCodeAt(0), 'v'.charCodeAt(0), 'w'.charCodeAt(0), 'x'.charCodeAt(0), 'y'.charCodeAt(0), 'z'.charCodeAt(0), '0'.charCodeAt(0), '1'.charCodeAt(0), '2'.charCodeAt(0), '3'.charCodeAt(0), '4'.charCodeAt(0), '5'.charCodeAt(0), '6'.charCodeAt(0), '7'.charCodeAt(0), '8'.charCodeAt(0), '9'.charCodeAt(0), '+'.charCodeAt(0), '/'.charCodeAt(0)];
        Base64.powTwo = { 0: 1, 1: 2, 2: 4, 3: 8, 4: 16, 5: 32, 6: 64, 7: 128, 8: 256, 9: 512, 10: 1024, 11: 2048, 12: 4096, 13: 8192, 14: 16384, 15: 32768, 16: 65536, 17: 131072, 18: 262144, 19: 524288, 20: 1048576, 21: 2097152, 22: 4194304, 23: 8388608, 24: 16777216, 25: 33554432, 26: 67108864, 27: 134217728, 28: 268435456, 29: 536870912, 30: 1073741824, 31: 2147483648, 32: 4294967296, 33: 8589934592, 34: 17179869184, 35: 34359738368, 36: 68719476736, 37: 137438953472, 38: 274877906944, 39: 549755813888, 40: 1099511627776, 41: 2199023255552, 42: 4398046511104, 43: 8796093022208, 44: 17592186044416, 45: 35184372088832, 46: 70368744177664, 47: 140737488355328, 48: 281474976710656, 49: 562949953421312, 50: 1125899906842624, 51: 2251799813685248, 52: 4503599627370496, 53: 9007199254740992 };
        Base64.longIndexes = [Long.fromNumber(0), Long.fromNumber(1), Long.fromNumber(2), Long.fromNumber(3), Long.fromNumber(4), Long.fromNumber(5), Long.fromNumber(6), Long.fromNumber(7), Long.fromNumber(8), Long.fromNumber(9), Long.fromNumber(10), Long.fromNumber(11), Long.fromNumber(12), Long.fromNumber(13), Long.fromNumber(14), Long.fromNumber(15), Long.fromNumber(16), Long.fromNumber(17), Long.fromNumber(18), Long.fromNumber(19), Long.fromNumber(20), Long.fromNumber(21), Long.fromNumber(22), Long.fromNumber(23), Long.fromNumber(24), Long.fromNumber(25), Long.fromNumber(26), Long.fromNumber(27), Long.fromNumber(28), Long.fromNumber(29), Long.fromNumber(30), Long.fromNumber(31), Long.fromNumber(32), Long.fromNumber(33), Long.fromNumber(34), Long.fromNumber(35), Long.fromNumber(36), Long.fromNumber(37), Long.fromNumber(38), Long.fromNumber(39), Long.fromNumber(40), Long.fromNumber(41), Long.fromNumber(42), Long.fromNumber(43), Long.fromNumber(44), Long.fromNumber(45), Long.fromNumber(46), Long.fromNumber(47), Long.fromNumber(48), Long.fromNumber(49), Long.fromNumber(50), Long.fromNumber(51), Long.fromNumber(52), Long.fromNumber(53), Long.fromNumber(54), Long.fromNumber(55), Long.fromNumber(56), Long.fromNumber(57), Long.fromNumber(58), Long.fromNumber(59), Long.fromNumber(60), Long.fromNumber(61), Long.fromNumber(62), Long.fromNumber(63)];
        utility.Base64 = Base64;
        var BufferView = (function () {
            function BufferView(p_origin, p_initPos, p_endPos) {
                this._origin = p_origin;
                this._initPos = p_initPos;
                this._endPos = p_endPos;
            }
            BufferView.prototype.write = function (b) {
                throw new Error("Write operation forbidden during iteration");
            };
            BufferView.prototype.writeAll = function (bytes) {
                throw new Error("Write operation forbidden during iteration");
            };
            BufferView.prototype.read = function (position) {
                if (this._initPos + position > this._endPos) {
                    throw new Error("" + position);
                }
                return this._origin.read(this._initPos + position);
            };
            BufferView.prototype.data = function () {
                return this._origin.slice(this._initPos, this._endPos);
            };
            BufferView.prototype.length = function () {
                return this._endPos - this._initPos + 1;
            };
            BufferView.prototype.free = function () {
                throw new Error("Free operation forbidden during iteration");
            };
            BufferView.prototype.iterator = function () {
                throw new Error("iterator creation forbidden forbidden during iteration");
            };
            BufferView.prototype.removeLast = function () {
                throw new Error("Write operation forbidden during iteration");
            };
            BufferView.prototype.slice = function (initPos, endPos) {
                throw new Error("Write operation forbidden during iteration");
            };
            return BufferView;
        }());
        utility.BufferView = BufferView;
        var DefaultBufferIterator = (function () {
            function DefaultBufferIterator(p_origin) {
                this._cursor = -1;
                this._origin = p_origin;
                this._originSize = p_origin.length();
            }
            DefaultBufferIterator.prototype.hasNext = function () {
                return this._originSize > 0 && (this._cursor + 1) < this._originSize;
            };
            DefaultBufferIterator.prototype.next = function () {
                var previousCursor = this._cursor;
                while ((this._cursor + 1) < this._originSize) {
                    this._cursor++;
                    var current = this._origin.read(this._cursor);
                    if (current == greycat.Constants.BUFFER_SEP) {
                        return new greycat.utility.BufferView(this._origin, previousCursor + 1, this._cursor - 1);
                    }
                }
                if (previousCursor < this._originSize) {
                    return new greycat.utility.BufferView(this._origin, previousCursor + 1, this._cursor);
                }
                return null;
            };
            return DefaultBufferIterator;
        }());
        utility.DefaultBufferIterator = DefaultBufferIterator;
        var Enforcer = (function () {
            function Enforcer() {
                this.checkers = new java.util.HashMap();
            }
            Enforcer.prototype.asBool = function (propertyName) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            if (input != null && inputType != greycat.Type.BOOL) {
                                throw new Error("Property " + propertyName + " should be Boolean value, currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.asString = function (propertyName) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            if (input != null && inputType != greycat.Type.STRING) {
                                throw new Error("Property " + propertyName + " should be String value, currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.asLong = function (propertyName) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            if (input != null && inputType != greycat.Type.LONG && inputType != greycat.Type.INT) {
                                throw new Error("Property " + propertyName + " should be long value, currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.asLongWithin = function (propertyName, min, max) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            var inputDouble = input;
                            if (input != null && ((inputType != greycat.Type.LONG && inputType != greycat.Type.INT) || inputDouble < min || inputDouble > max)) {
                                throw new Error("Property " + propertyName + " should be long value [" + min + "," + max + "], currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.asDouble = function (propertyName) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            if (input != null && (inputType != greycat.Type.DOUBLE && inputType != greycat.Type.INT && inputType != greycat.Type.LONG)) {
                                throw new Error("Property " + propertyName + " should be double value, currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.asDoubleWithin = function (propertyName, min, max) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            var inputDouble = void 0;
                            if (input instanceof Number) {
                                inputDouble = input;
                            }
                            else if (input instanceof Number) {
                                inputDouble = input;
                            }
                            else {
                                inputDouble = input;
                            }
                            if (input != null && ((inputType != greycat.Type.DOUBLE && inputType != greycat.Type.INT && inputType != greycat.Type.LONG) || inputDouble < min || inputDouble > max)) {
                                throw new Error("Property " + propertyName + " should be double value [" + min + "," + max + "], currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.asInt = function (propertyName) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            if (input != null && inputType != greycat.Type.INT && inputType != greycat.Type.LONG) {
                                throw new Error("Property " + propertyName + " should be integer value, currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.asIntWithin = function (propertyName, min, max) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            var inputInt = input;
                            if (input != null && ((inputType != greycat.Type.INT && inputType != greycat.Type.LONG) || inputInt < min || inputInt > max)) {
                                throw new Error("Property " + propertyName + " should be integer value [" + min + "," + max + "], currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.asIntGreaterOrEquals = function (propertyName, min) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            var inputInt = input;
                            if (input != null && ((inputType != greycat.Type.INT && inputType != greycat.Type.LONG) || inputInt < min)) {
                                throw new Error("Property " + propertyName + " should be integer value >=" + min + ", currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.asDoubleArray = function (propertyName) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            if (input != null && inputType != greycat.Type.DOUBLE_ARRAY) {
                                throw new Error("Property " + propertyName + " should be doubleArray value, currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.asPositiveInt = function (propertyName) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            var inputInt = input;
                            if ((input != null && inputType != greycat.Type.INT) || inputInt <= 0) {
                                throw new Error("Property " + propertyName + " should be a positive integer, currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.asNonNegativeDouble = function (propertyName) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            var inputDouble = void 0;
                            if (input instanceof Number) {
                                inputDouble = input;
                            }
                            else if (input instanceof Number) {
                                inputDouble = input;
                            }
                            else {
                                inputDouble = input;
                            }
                            if (input != null && ((inputType != greycat.Type.DOUBLE && inputType != greycat.Type.INT && inputType != greycat.Type.LONG) || !(inputDouble >= 0))) {
                                throw new Error("Property " + propertyName + " should be a non-negative double, currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.asPositiveDouble = function (propertyName) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            var inputDouble = void 0;
                            if (input instanceof Number) {
                                inputDouble = input;
                            }
                            else if (input instanceof Number) {
                                inputDouble = input;
                            }
                            else {
                                inputDouble = input;
                            }
                            if (input != null && ((inputType != greycat.Type.DOUBLE && inputType != greycat.Type.INT && inputType != greycat.Type.LONG) || !(inputDouble > 0))) {
                                throw new Error("Property " + propertyName + " should be a positive double, currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.asNonNegativeOrNanDouble = function (propertyName) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            var inputDouble = void 0;
                            if (input instanceof Number) {
                                inputDouble = input;
                            }
                            else if (input instanceof Number) {
                                inputDouble = input;
                            }
                            else {
                                inputDouble = input;
                            }
                            if (input != null && ((inputType != greycat.Type.DOUBLE && inputType != greycat.Type.INT && inputType != greycat.Type.LONG) || inputDouble < 0)) {
                                throw new Error("Property " + propertyName + " should be a positive double, currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.asPositiveLong = function (propertyName) {
                return this.declare(propertyName, {
                    check: function (inputType, input) {
                        {
                            var inputLong = void 0;
                            if (input instanceof Number) {
                                inputLong = input;
                            }
                            else {
                                inputLong = input;
                            }
                            if (input != null && ((inputType != greycat.Type.LONG && inputType != greycat.Type.INT) || inputLong <= 0)) {
                                throw new Error("Property " + propertyName + " should be a positive long, currently " + input);
                            }
                        }
                    }
                });
            };
            Enforcer.prototype.declare = function (propertyName, checker) {
                this.checkers.put(propertyName, checker);
                return this;
            };
            Enforcer.prototype.check = function (propertyName, propertyType, propertyValue) {
                var checker = this.checkers.get(propertyName);
                if (checker != null) {
                    checker.check(propertyType, propertyValue);
                }
            };
            return Enforcer;
        }());
        utility.Enforcer = Enforcer;
        var HashHelper = (function () {
            function HashHelper() {
            }
            HashHelper.longHash = function (number, max) {
                var hash = number % max;
                return hash < 0 ? hash * -1 : hash;
            };
            HashHelper.simpleTripleHash = function (p0, p1, p2, p3, max) {
                var hash = (p0 ^ p1 ^ p2 ^ p3) % max;
                if (hash < 0) {
                    hash = hash * -1;
                }
                return hash;
            };
            HashHelper.tripleHash = function (p0, p1, p2, p3, max) {
                if (max <= 0) {
                    throw new Error("Max must be > 0");
                }
                var v1 = HashHelper.PRIME5;
                var v2 = v1 * HashHelper.PRIME2 + HashHelper.len;
                var v3 = v2 * HashHelper.PRIME3;
                var v4 = v3 * HashHelper.PRIME4;
                var crc;
                v1 = ((v1 << 13) | (v1 >>> 51)) + p1;
                v2 = ((v2 << 11) | (v2 >>> 53)) + p2;
                v3 = ((v3 << 17) | (v3 >>> 47)) + p3;
                v4 = ((v4 << 19) | (v4 >>> 45)) + p0;
                v1 += ((v1 << 17) | (v1 >>> 47));
                v2 += ((v2 << 19) | (v2 >>> 45));
                v3 += ((v3 << 13) | (v3 >>> 51));
                v4 += ((v4 << 11) | (v4 >>> 53));
                v1 *= HashHelper.PRIME1;
                v2 *= HashHelper.PRIME1;
                v3 *= HashHelper.PRIME1;
                v4 *= HashHelper.PRIME1;
                v1 += p1;
                v2 += p2;
                v3 += p3;
                v4 += HashHelper.PRIME5;
                v1 *= HashHelper.PRIME2;
                v2 *= HashHelper.PRIME2;
                v3 *= HashHelper.PRIME2;
                v4 *= HashHelper.PRIME2;
                v1 += ((v1 << 11) | (v1 >>> 53));
                v2 += ((v2 << 17) | (v2 >>> 47));
                v3 += ((v3 << 19) | (v3 >>> 45));
                v4 += ((v4 << 13) | (v4 >>> 51));
                v1 *= HashHelper.PRIME3;
                v2 *= HashHelper.PRIME3;
                v3 *= HashHelper.PRIME3;
                v4 *= HashHelper.PRIME3;
                crc = v1 + ((v2 << 3) | (v2 >>> 61)) + ((v3 << 6) | (v3 >>> 58)) + ((v4 << 9) | (v4 >>> 55));
                crc ^= crc >>> 11;
                crc += (HashHelper.PRIME4 + HashHelper.len) * HashHelper.PRIME1;
                crc ^= crc >>> 15;
                crc *= HashHelper.PRIME2;
                crc ^= crc >>> 13;
                crc = (crc < 0 ? crc * -1 : crc);
                crc = crc % max;
                return crc;
            };
            HashHelper.rand = function () {
                return Math.random() * 1000000;
            };
            HashHelper.equals = function (src, other) {
                return src === other;
            };
            HashHelper.DOUBLE_MIN_VALUE = function () {
                return Number.MIN_VALUE;
            };
            HashHelper.DOUBLE_MAX_VALUE = function () {
                return Number.MAX_VALUE;
            };
            HashHelper.isDefined = function (param) {
                return param != undefined && param != null;
            };
            HashHelper.hash = function (data) {
                var hash = 0, i, chr, len;
                if (data.length === 0)
                    return hash;
                for (i = 0, len = data.length; i < len; i++) {
                    chr = data.charCodeAt(i);
                    hash = ((hash << 5) - hash) + chr;
                    hash |= 0; // Convert to 32bit integer
                }
                return hash;
            };
            HashHelper.hashBytes = function (data) {
                var h = HashHelper.HSTART;
                var dataLength = data.length;
                for (var i = 0; i < dataLength; i++) {
                    h = h.mul(HashHelper.HMULT).xor(HashHelper.byteTable[data[i] & 0xff]);
                }
                return h.mod(greycat.internal.CoreConstants.END_OF_TIME).toNumber();
            };
            return HashHelper;
        }());
        HashHelper.PRIME1 = 2654435761;
        HashHelper.PRIME2 = 2246822519;
        HashHelper.PRIME3 = 3266489917;
        HashHelper.PRIME4 = 668265263;
        HashHelper.PRIME5 = 0x165667b1;
        HashHelper.len = 24;
        HashHelper.byteTable = function () {
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
        HashHelper.HSTART = Long.fromBits(0xA205B064, 0xBB40E64D);
        HashHelper.HMULT = Long.fromBits(0xE116586D, 0x6A5D39EA);
        utility.HashHelper = HashHelper;
        var KeyHelper = (function () {
            function KeyHelper() {
            }
            KeyHelper.keyToBuffer = function (buffer, chunkType, world, time, id) {
                greycat.utility.Base64.encodeIntToBuffer(chunkType, buffer);
                buffer.write(greycat.Constants.KEY_SEP);
                greycat.utility.Base64.encodeLongToBuffer(world, buffer);
                buffer.write(greycat.Constants.KEY_SEP);
                greycat.utility.Base64.encodeLongToBuffer(time, buffer);
                buffer.write(greycat.Constants.KEY_SEP);
                greycat.utility.Base64.encodeLongToBuffer(id, buffer);
            };
            return KeyHelper;
        }());
        utility.KeyHelper = KeyHelper;
        var Tuple = (function () {
            function Tuple(p_left, p_right) {
                this._left = p_left;
                this._right = p_right;
            }
            Tuple.prototype.left = function () {
                return this._left;
            };
            Tuple.prototype.right = function () {
                return this._right;
            };
            return Tuple;
        }());
        utility.Tuple = Tuple;
        var VerboseHook = (function () {
            function VerboseHook() {
                this.ctxIdents = new java.util.HashMap();
            }
            VerboseHook.prototype.start = function (initialContext) {
                this.ctxIdents.put(initialContext, 0);
                console.log("StartTask:" + initialContext);
            };
            VerboseHook.prototype.beforeAction = function (action, context) {
                var currentPrefix = this.ctxIdents.get(context);
                for (var i = 0; i < currentPrefix; i++) {
                    console.log("\t");
                }
                var taskName = action.toString();
                console.log(context.template(taskName));
            };
            VerboseHook.prototype.afterAction = function (action, context) { };
            VerboseHook.prototype.beforeTask = function (parentContext, context) {
                var currentPrefix = this.ctxIdents.get(parentContext);
                this.ctxIdents.put(context, currentPrefix + 1);
            };
            VerboseHook.prototype.afterTask = function (context) {
                this.ctxIdents.remove(context);
            };
            VerboseHook.prototype.end = function (finalContext) {
                console.log("EndTask:" + finalContext.toString());
            };
            return VerboseHook;
        }());
        utility.VerboseHook = VerboseHook;
        var VerbosePlugin = (function () {
            function VerbosePlugin() {
            }
            VerbosePlugin.prototype.start = function (graph) {
                graph.addGlobalTaskHook(new greycat.utility.VerboseHook());
            };
            VerbosePlugin.prototype.stop = function () { };
            return VerbosePlugin;
        }());
        utility.VerbosePlugin = VerbosePlugin;
    })(utility = greycat.utility || (greycat.utility = {}));
})(greycat || (greycat = {}));
//# sourceMappingURL=greycat.js.map