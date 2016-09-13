module java {
    export module lang {
        export class System {
            static gc() {
            }

            static arraycopy(src: any[]| Float64Array | Int32Array, srcPos: number, dest: any[]| Float64Array | Int32Array, destPos: number, numElements: number): void {
                if ((dest instanceof Float64Array || dest instanceof Int32Array)
                    && (src instanceof Float64Array || src instanceof Int32Array)) {
                    if (numElements == src.length) {
                        dest.set(src, destPos);
                    } else {
                        dest.set(src.subarray(srcPos, srcPos + numElements), destPos);
                    }
                } else {
                    for (var i = 0; i < numElements; i++) {
                        dest[destPos + i] = src[srcPos + i];
                    }
                }
            }
        }

        export class StringBuilder {
            private _buffer: string = "";
            public length = 0;

            append(val: any): StringBuilder {
                this._buffer = this._buffer + val;
                this.length = this._buffer.length;
                return this;
            }

            insert(position: number, val: any): StringBuilder {
                this._buffer = this._buffer.slice(0, position) + val + this._buffer.slice(position);

                return this;
            }

            toString(): string {
                return this._buffer;
            }
        }

        export class String {
            static valueOf(data: any, offset?: number, count?: number): string {
                if (typeof offset === 'undefined' && typeof count === 'undefined') {
                    return data + '';
                } else {
                    return data.slice(offset, offset + count);
                }
            }

            static hashCode(str: string): number {
                var h: number = str['_hashCode'] ? str['_hashCode'] : 0;
                if (h === 0 && str.length > 0) {
                    var val: string = str;

                    for (var i: number = 0; i < str.length; i++) {
                        h = 31 * h + str.charCodeAt(i);
                    }
                    str['_hashCode'] = h;
                }
                return h;
            }

            static isEmpty(str: string): boolean {
                return str.length === 0;
            }

            static join(delimiter: string, ...elements: string[]): string {
                return elements.join(delimiter);
            }
        }

        export class Thread {
            static sleep(time: number): void {

            }
        }
        export class Double {
            public static MAX_VALUE: number = Number.MAX_VALUE;
            public static POSITIVE_INFINITY: number = Number.POSITIVE_INFINITY;
            public static NEGATIVE_INFINITY: number = Number.NEGATIVE_INFINITY;
            public static NaN = NaN;
        }
        export class Long {
            public static parseLong(d: any) {
                return parseInt(d);
            }
        }
        export class Integer {
            public static parseInt(d: any) {
                return parseInt(d);
            }
        }

    }

    export namespace util {
        export namespace concurrent {
            export namespace atomic {
                export class AtomicIntegerArray {
                    _internal: Int32Array;

                    constructor(initialCapacity: number) {
                        this._internal = new Int32Array(initialCapacity);
                    }

                    set(index: number, newVal: number) {
                        this._internal[index] = newVal;
                    }

                    get(index: number) {
                        return this._internal[index];
                    }

                    getAndSet(index: number, newVal: number) {
                        var temp = this._internal[index];
                        this._internal[index] = newVal;
                        return temp;
                    }

                    compareAndSet(index: number, expect: number, update: number): boolean {
                        if (this._internal[index] == expect) {
                            this._internal[index] = update;
                            return true;
                        } else {
                            return false;
                        }
                    }
                }

                export class AtomicLongArray {
                    _internal: Float64Array;

                    constructor(initialCapacity: number) {
                        this._internal = new Float64Array(initialCapacity);
                    }

                    set(index: number, newVal: number) {
                        this._internal[index] = newVal;
                    }

                    get(index: number) {
                        return this._internal[index];
                    }

                    getAndSet(index: number, newVal: number) {
                        var temp = this._internal[index];
                        this._internal[index] = newVal;
                        return temp;
                    }

                    compareAndSet(index: number, expect: number, update: number): boolean {
                        if (this._internal[index] == expect) {
                            this._internal[index] = update;
                            return true;
                        } else {
                            return false;
                        }
                    }

                    length(): number {
                        return this._internal.length;
                    }
                }

                export class AtomicReferenceArray<A> {
                    _internal: Array<A>;

                    constructor(initialCapacity: number) {
                        this._internal = new Array<A>();
                    }

                    set(index: number, newVal: A) {
                        this._internal[index] = newVal;
                    }

                    get(index: number): A {
                        return this._internal[index];
                    }

                    getAndSet(index: number, newVal: A) {
                        var temp = this._internal[index];
                        this._internal[index] = newVal;
                        return temp;
                    }

                    compareAndSet(index: number, expect: A, update: A): boolean {
                        if (this._internal[index] == expect) {
                            this._internal[index] = update;
                            return true;
                        } else {
                            return false;
                        }
                    }

                    length(): number {
                        return this._internal.length;
                    }
                }

                export class AtomicReference<A> {
                    _internal: A = null;

                    compareAndSet(expect: A, update: A): boolean {
                        if (this._internal == expect) {
                            this._internal = update;
                            return true;
                        } else {
                            return false;
                        }
                    }

                    get(): A {
                        return this._internal
                    }

                    set(newRef: A) {
                        this._internal = newRef;
                    }

                    getAndSet(newVal: A): A {
                        var temp = this._internal;
                        this._internal = newVal;
                        return temp;
                    }
                }

                export class AtomicLong {
                    _internal = 0;

                    constructor(init: number) {
                        this._internal = init;
                    }

                    compareAndSet(expect: number, update: number): boolean {
                        if (this._internal == expect) {
                            this._internal = update;
                            return true;
                        } else {
                            return false;
                        }
                    }

                    get(): number {
                        return this._internal;
                    }

                    incrementAndGet(): number {
                        this._internal++;
                        return this._internal;
                    }

                    decrementAndGet(): number {
                        this._internal--;
                        return this._internal;
                    }
                }

                export class AtomicBoolean {
                    _internal = false;

                    constructor(init: boolean) {
                        this._internal = init;
                    }

                    compareAndSet(expect: boolean, update: boolean): boolean {
                        if (this._internal == expect) {
                            this._internal = update;
                            return true;
                        } else {
                            return false;
                        }
                    }

                    get(): boolean {
                        return this._internal;
                    }

                    set(newVal: boolean) {
                        this._internal = newVal
                    }

                }

                export class AtomicInteger {
                    _internal = 0;

                    constructor(init: number) {
                        this._internal = init;
                    }

                    compareAndSet(expect: number, update: number): boolean {
                        if (this._internal == expect) {
                            this._internal = update;
                            return true;
                        } else {
                            return false;
                        }
                    }

                    get(): number {
                        return this._internal;
                    }

                    set(newVal: number) {
                        this._internal = newVal
                    }

                    getAndSet(newVal: number): number {
                        var temp = this._internal;
                        this._internal = newVal;
                        return temp;
                    }

                    incrementAndGet(): number {
                        this._internal++;
                        return this._internal;
                    }

                    decrementAndGet(): number {
                        this._internal--;
                        return this._internal;
                    }

                    getAndIncrement(): number {
                        var temp = this._internal;
                        this._internal++;
                        return temp;
                    }

                    getAndDecrement(): number {
                        var temp = this._internal;
                        this._internal--;
                        return temp;
                    }
                }
            }
            export namespace locks {
                export class ReentrantLock {
                    public lock(): void {

                    }

                    public unlock(): void {

                    }
                }
            }
        }

        export class Random {
            private seed: number = undefined;

            public nextInt(max?: number): number {
                if (typeof max === 'undefined') {
                    max = Math.pow(2, 32);
                }
                if (this.seed == undefined) {
                    return Math.floor(Math.random() * max);
                } else {
                    return Math.floor(this.nextSeeded(0, max));
                }
            }

            public nextDouble(): number {
                if (this.seed == undefined) {
                    return Math.random();
                } else {
                    return this.nextSeeded(Number.MIN_VALUE, Number.MAX_VALUE);
                }
            }

            public nextBoolean(): boolean {
                if (this.seed == undefined) {
                    return Math.random() >= 0.5;
                } else {
                    return this.nextSeeded() >= 0.5;
                }
            }

            public setSeed(seed: number): void {
                this.seed = seed;
            }

            private nextSeeded(min?: number, max?: number) {
                var max = max || 1;
                var min = min || 0;

                this.seed = (this.seed * 9301 + 49297) % 233280;
                var rnd = this.seed / 233280;

                return min + rnd * (max - min);
            }

        }

        export interface Iterator<E> {
            hasNext(): boolean;
            next(): E;
        }

        export class Arrays {
            public static fill(data: any, begin: number, nbElem: number, param: number): void {
                var max = begin + nbElem;
                for (var i = begin; i < max; i++) {
                    data[i] = param;
                }
            }

            public static copyOf<T>(original: any[], newLength: number, ignore?: any): T[] {
                var copy = new Array<T>(newLength);
                lang.System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
                return copy;
            }
        }

        export class Collections {

            public static swap(list: List<any>, i: number, j: number) {
                const l = list;
                l.set(i, l.set(j, l.get(i)));
            }

        }

        export interface Collection<E> {
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

        export interface List<E> extends Collection<E> {
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

        export interface Set<E> extends Collection<E> {
            forEach(f: (e: any)=>void): void;
        }

        export class Itr<E> implements Iterator<E> {
            public cursor: number = 0;
            public lastRet: number = -1;
            protected list: Collection<E>;

            constructor(list: Collection<E>) {
                this.list = list;
            }

            public hasNext(): boolean {
                return this.cursor != this.list.size();
            }

            public next(): E {
                try {
                    var i: number = this.cursor;
                    var next: E = this.list.get(i);
                    this.lastRet = i;
                    this.cursor = i + 1;
                    return next;
                } catch ($ex$) {
                    if ($ex$ instanceof Error) {
                        var e: Error = <Error>$ex$;
                        throw new Error("no such element exception");
                    } else {
                        throw $ex$;
                    }
                }
            }
        }

        export class HashSet<E> implements Set<E> {
            private content = {};

            add(val: E) {
                this.content[<any>val] = val;
            }

            clear() {
                this.content = {};
            }

            contains(val: E): boolean {
                return this.content.hasOwnProperty(<any>val);
            }

            containsAll(elems: Collection<E>): boolean {
                return false;
            }

            addAll(vals: Collection<E>): boolean {
                var tempArray = vals.toArray(null);
                for (var i = 0; i < tempArray.length; i++) {
                    this.content[<any>tempArray[i]] = tempArray[i];
                }
                return true;
            }

            remove(val: E): boolean {
                var b = false;
                if (this.content[<any>val]) {
                    b = true;
                }
                delete this.content[<any>val];
                return b;
            }

            removeAll(): boolean {
                return false;
            }

            size(): number {
                return Object.keys(this.content).length;
            }

            isEmpty(): boolean {
                return this.size() == 0;
            }

            toArray<E>(a: Array<E>): E[] {
                return <E[]><any>Object.keys(this.content).map(key => this.content[key]);
            }

            iterator(): Iterator<E> {
                return new java.util.Itr(this);
            }

            forEach(f: (e: any)=>void): void {
                for (var p in this.content) {
                    f(this.content[p]);
                }
            }

            get(index: number): E {
                return this.content[index];
            }
        }


        export class AbstractList<E> implements List<E> {
            private content: E[] = [];

            addAll(index: any, vals?: any): boolean {
                var tempArray = vals.toArray(null);
                for (var i = 0; i < tempArray.length; i++) {
                    this.content.push(tempArray[i]);
                }
                return false;
            }

            clear() {
                this.content = [];
            }

            poll(): E {
                return this.content.shift();
            }

            remove(indexOrElem: any): any {
                this.content.splice(indexOrElem, 1);
                return true;
            }

            removeAll(): boolean {
                this.content = [];
                return true;
            }

            toArray(a: Array<E>): E[] {
                return this.content;
            }

            size(): number {
                return this.content.length;
            }

            add(index: any, elem?: E) {
                if (typeof elem !== 'undefined') {
                    this.content.splice(index, 0, elem);
                } else {
                    this.content.push(index);
                }
            }

            get(index: number): E {
                return this.content[index];
            }

            contains(val: E): boolean {
                return this.content.indexOf(val) != -1;
            }

            containsAll(elems: Collection<E>): boolean {
                return false;
            }

            isEmpty(): boolean {
                return this.content.length == 0;
            }

            set(index: number, element: E): E {
                this.content[index] = element;
                return element;
            }

            indexOf(element: E): number {
                return this.content.indexOf(element);
            }

            lastIndexOf(element: E): number {
                return this.content.lastIndexOf(element);
            }

            iterator(): Iterator<E> {
                return new Itr(this);
            }

        }

        export class LinkedList<E> extends AbstractList<E> {
        }
        export class ArrayList<E> extends AbstractList<E> {
        }

        export class Stack<E> {
            content = new Array();

            pop(): E {
                return this.content.pop();
            }

            push(t: E): void {
                this.content.push(t);
            }

            isEmpty(): boolean {
                return this.content.length == 0;
            }

            peek(): E {
                return this.content.slice(-1)[0];
            }
        }

        export interface Map<K, V> {
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

        export class HashMap<K, V> implements Map<K, V> {

            private content = {};

            get(key: K): V {
                return this.content[<any>key];
            }

            put(key: K, value: V): V {
                var previous_val = this.content[<any>key];
                this.content[<any>key] = value;
                return previous_val;
            }

            containsKey(key: K): boolean {
                return this.content.hasOwnProperty(<any>key);
            }

            remove(key: K): V {
                var tmp = this.content[<any>key];
                delete this.content[<any>key];
                return tmp;
            }

            keySet(): Set<K> {
                var result = new HashSet<K>();
                for (var p in this.content) {
                    if (this.content.hasOwnProperty(p)) {
                        result.add(<any> p);
                    }
                }
                return <Set<K>> result;
            }

            isEmpty(): boolean {
                return Object.keys(this.content).length == 0;
            }

            values(): Set<V> {
                var result = new HashSet<V>();
                for (var p in this.content) {
                    if (this.content.hasOwnProperty(p)) {
                        result.add(this.content[p]);
                    }
                }
                return <Set<V>> result;
            }

            clear(): void {
                this.content = {};
            }

            size(): number {
                return Object.keys(this.content).length
            }
        }
        export class ConcurrentHashMap<K, V> extends HashMap<K, V> {

        }
    }
}

function arrayInstanceOf(arr: any, arg: Function): boolean {
    if (!(arr instanceof Array)) {
        return false;
    } else {
        if (arr.length == 0) {
            return true;
        } else {
            return (arr[0] instanceof arg);
        }
    }
}


class Long {

    /*
     long.js (c) 2013 Daniel Wirtz <dcode@dcode.io>
     Released under the Apache License, Version 2.0
     see: https://github.com/dcodeIO/long.js for details
     */

    private high: number = 0;
    private low: number = 0;
    private unsigned: boolean = false;

    private static INT_CACHE = {};
    private static UINT_CACHE = {};
    private static pow_dbl = Math.pow;


    private static TWO_PWR_16_DBL = 1 << 16;
    private static TWO_PWR_24_DBL = 1 << 24;
    private static TWO_PWR_32_DBL = Long.TWO_PWR_16_DBL * Long.TWO_PWR_16_DBL;
    private static TWO_PWR_64_DBL = Long.TWO_PWR_32_DBL * Long.TWO_PWR_32_DBL;
    private static TWO_PWR_63_DBL = Long.TWO_PWR_64_DBL / 2;
    private static TWO_PWR_24 = Long.fromInt(Long.TWO_PWR_24_DBL);

    public static ZERO: Long = Long.fromInt(0);
    public static UZERO: Long = Long.fromInt(0, true);
    public static ONE: Long = Long.fromInt(1);
    public static UONE: Long = Long.fromInt(1, true);
    public static NEG_ONE: Long = Long.fromInt(-1);
    public static MAX_VALUE: Long = Long.fromBits(0x7FFFFFFF, 0xFFFFFFFF, false);
    public static MAX_UNSIGNED_VALUE: Long = Long.fromBits(0xFFFFFFFF, 0xFFFFFFFF, true);
    public static MIN_VALUE: Long = Long.fromBits(0x80000000, 0, false);


    constructor(low ?: number, high ?: number, unsigned ?: boolean) {
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

    public static isLong(obj: any): boolean {
        return (obj && obj["__isLong__"]) === true;
    }

    public static fromInt(value: number, unsigned ?: boolean): Long {
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
        } else {
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
    }

    public static fromNumber(value: number, unsigned ?: boolean): Long {
        if (isNaN(value) || !isFinite(value))
            return unsigned ? Long.UZERO : Long.ZERO;
        if (unsigned) {
            if (value < 0)
                return Long.UZERO;
            if (value >= Long.TWO_PWR_64_DBL)
                return Long.MAX_UNSIGNED_VALUE;
        } else {
            if (value <= -Long.TWO_PWR_63_DBL)
                return Long.MIN_VALUE;
            if (value + 1 >= Long.TWO_PWR_63_DBL)
                return Long.MAX_VALUE;
        }
        if (value < 0)
            return Long.fromNumber(-value, unsigned).neg();
        return Long.fromBits((value % Long.TWO_PWR_32_DBL) | 0, (value / Long.TWO_PWR_32_DBL) | 0, unsigned);
    }

    public static fromBits(lowBits ?: number, highBits ?: number, unsigned ?: boolean): Long {
        return new Long(lowBits, highBits, unsigned);
    }

    public static fromString(str: string, radix: number = 10, unsigned: boolean = false): Long {
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
            var size = Math.min(8, str.length - i),
                value = parseInt(str.substring(i, i + size), radix);
            if (size < 8) {
                var power = Long.fromNumber(Long.pow_dbl(radix, size));
                result = result.mul(power).add(Long.fromNumber(value));
            } else {
                result = result.mul(radixToPower);
                result = result.add(Long.fromNumber(value));
            }
        }
        result.unsigned = unsigned;
        return result;
    }

    public static  fromValue(val: any): Long {
        if (val /* is compatible */ instanceof Long)
            return val;
        if (typeof val === 'number')
            return Long.fromNumber(val);
        if (typeof val === 'string')
            return Long.fromString(val);
        // Throws for non-objects, converts non-instanceof Long:
        return Long.fromBits(val.low, val.high, val.unsigned);
    }


    public toInt(): number {
        return this.unsigned ? this.low >>> 0 : this.low;
    };

    public toNumber(): number {
        if (this.unsigned)
            return ((this.high >>> 0) * Long.TWO_PWR_32_DBL) + (this.low >>> 0);
        return this.high * Long.TWO_PWR_32_DBL + (this.low >>> 0);
    };

    public toString(radix: number): string {
        radix = radix || 10;
        if (radix < 2 || 36 < radix)
            throw RangeError('radix');
        if (this.isZero())
            return '0';
        if (this.isNegative()) { // Unsigned Longs are never negative
            if (this.eq(Long.MIN_VALUE)) {
                // We need to change the Long value before it can be negated, so we remove
                // the bottom-most digit in this base and then recurse to do the rest.
                var radixLong = Long.fromNumber(radix),
                    div = this.div(radixLong),
                    rem1 = div.mul(radixLong).sub(this);
                return div.toString(radix) + rem1.toInt().toString(radix);
            } else
                return '-' + this.neg().toString(radix);
        }

        // Do several (6) digits each time through the loop, so as to
        // minimize the calls to the very expensive emulated div.
        var radixToPower = Long.fromNumber(Long.pow_dbl(radix, 6), this.unsigned);
        var rem: Long = this;
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

    public getHighBits(): number {
        return this.high;
    };

    public getHighBitsUnsigned(): number {
        return this.high >>> 0;
    };

    public getLowBits(): number {
        return this.low;
    };

    public getLowBitsUnsigned(): number {
        return this.low >>> 0;
    };

    public getNumBitsAbs(): number {
        if (this.isNegative()) // Unsigned Longs are never negative
            return this.eq(Long.MIN_VALUE) ? 64 : this.neg().getNumBitsAbs();
        var val = this.high != 0 ? this.high : this.low;
        for (var bit = 31; bit > 0; bit--)
            if ((val & (1 << bit)) != 0)
                break;
        return this.high != 0 ? bit + 33 : bit + 1;
    };

    public isZero(): boolean {
        return this.high === 0 && this.low === 0;
    };

    public isNegative(): boolean {
        return !this.unsigned && this.high < 0;
    };

    public isPositive(): boolean {
        return this.unsigned || this.high >= 0;
    };

    public isOdd(): boolean {
        return (this.low & 1) === 1;
    };

    public isEven(): boolean {
        return (this.low & 1) === 0;
    };

    public equals(other: any): boolean {
        if (!Long.isLong(other))
            other = Long.fromValue(other);
        if (this.unsigned !== other.unsigned && (this.high >>> 31) === 1 && (other.high >>> 31) === 1)
            return false;
        return this.high === other.high && this.low === other.low;
    };

    public eq = this.equals;

    public notEquals(other: any): boolean {
        return !this.eq(other);
    };

    public neq = this.notEquals;

    public lessThan(other: any): boolean {
        return this.comp(other) < 0;
    };

    public lt = this.lessThan;

    public lessThanOrEqual(other: any): boolean {
        return this.comp(other) <= 0;
    };

    public lte = this.lessThanOrEqual;

    public greaterThan(other: any): boolean {
        return this.comp(other) > 0;
    };

    public gt = this.greaterThan;

    public greaterThanOrEqual(other: any): boolean {
        return this.comp(other) >= 0;
    };

    public gte = this.greaterThanOrEqual;

    public compare(other: any): number {
        if (!Long.isLong(other))
            other = Long.fromValue(other);
        if (this.eq(other))
            return 0;
        var thisNeg = this.isNegative(),
            otherNeg = other.isNegative();
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

    public comp = this.compare;

    public negate(): Long {
        if (!this.unsigned && this.eq(Long.MIN_VALUE))
            return Long.MIN_VALUE;
        return this.not().add(Long.ONE);
    };

    public neg = this.negate;

    public add(addend: any): Long {
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

    public subtract(subtrahend: any): Long {
        if (!Long.isLong(subtrahend))
            subtrahend = Long.fromValue(subtrahend);
        return this.add(subtrahend.neg());
    };

    public sub = this.subtract;

    public multiply(multiplier: any): Long {
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
        } else if (multiplier.isNegative())
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

    public mul = this.multiply;

    public divide(divisor: any): Long {
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
                    return Long.MIN_VALUE;  // recall that -MIN_VALUE == MIN_VALUE
                else if (divisor.eq(Long.MIN_VALUE))
                    return Long.ONE;
                else {
                    // At this point, we have |other| >= 2, so |this/other| < |MIN_VALUE|.
                    var halfThis = this.shr(1);
                    approx = halfThis.div(divisor).shl(1);
                    if (approx.eq(Long.ZERO)) {
                        return divisor.isNegative() ? Long.ONE : Long.NEG_ONE;
                    } else {
                        rem = this.sub(divisor.mul(approx));
                        res = approx.add(rem.div(divisor));
                        return res;
                    }
                }
            } else if (divisor.eq(Long.MIN_VALUE))
                return this.unsigned ? Long.UZERO : Long.ZERO;
            if (this.isNegative()) {
                if (divisor.isNegative())
                    return this.neg().div(divisor.neg());
                return this.neg().div(divisor).neg();
            } else if (divisor.isNegative())
                return this.div(divisor.neg()).neg();
            res = Long.ZERO;
        } else {
            // The algorithm below has not been made for unsigned longs. It's therefore
            // required to take special care of the MSB prior to running it.
            if (!divisor.unsigned)
                divisor = divisor.toUnsigned();
            if (divisor.gt(this))
                return Long.UZERO;
            if (divisor.gt(this.shru(1))) // 15 >>> 1 = 7 ; with divisor = 8 ; true
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
            var log2 = Math.ceil(Math.log(approx) / Math.LN2),
                delta = (log2 <= 48) ? 1 : Long.pow_dbl(2, log2 - 48),

                // Decrease the approximation until it is smaller than the remainder.  Note
                // that if it is too large, the product overflows and is negative.
                approxRes = Long.fromNumber(approx),
                approxRem = approxRes.mul(divisor);
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

    public div = this.divide;

    public modulo(divisor: any): Long {
        if (!Long.isLong(divisor))
            divisor = Long.fromValue(divisor);
        return this.sub(this.div(divisor).mul(divisor));
    };

    public mod = this.modulo;

    public not(): Long {
        return Long.fromBits(~this.low, ~this.high, this.unsigned);
    };

    public and(other: any): Long {
        if (!Long.isLong(other))
            other = Long.fromValue(other);
        return Long.fromBits(this.low & other.low, this.high & other.high, this.unsigned);
    };

    public or(other: any): Long {
        if (!Long.isLong(other))
            other = Long.fromValue(other);
        return Long.fromBits(this.low | other.low, this.high | other.high, this.unsigned);
    };

    public xor(other: any): Long {
        if (!Long.isLong(other))
            other = Long.fromValue(other);
        return Long.fromBits(this.low ^ other.low, this.high ^ other.high, this.unsigned);
    };

    public shiftLeft(numBits): Long {
        if (Long.isLong(numBits))
            numBits = numBits.toInt();
        if ((numBits &= 63) === 0)
            return this;
        else if (numBits < 32)
            return Long.fromBits(this.low << numBits, (this.high << numBits) | (this.low >>> (32 - numBits)), this.unsigned);
        else
            return Long.fromBits(0, this.low << (numBits - 32), this.unsigned);
    };

    public shl = this.shiftLeft;

    public shiftRight(numBits): Long {
        if (Long.isLong(numBits))
            numBits = numBits.toInt();
        if ((numBits &= 63) === 0)
            return this;
        else if (numBits < 32)
            return Long.fromBits((this.low >>> numBits) | (this.high << (32 - numBits)), this.high >> numBits, this.unsigned);
        else
            return Long.fromBits(this.high >> (numBits - 32), this.high >= 0 ? 0 : -1, this.unsigned);
    };

    public shr = this.shiftRight;

    public shiftRightUnsigned(numBits): Long {
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
            } else if (numBits === 32)
                return Long.fromBits(high, 0, this.unsigned);
            else
                return Long.fromBits(high >>> (numBits - 32), 0, this.unsigned);
        }
    };

    public shru = this.shiftRightUnsigned;

    public toSigned(): Long {
        if (!this.unsigned)
            return this;
        return Long.fromBits(this.low, this.high, false);
    };

    public toUnsigned(): Long {
        if (this.unsigned)
            return this;
        return Long.fromBits(this.low, this.high, true);
    };


}

Object.defineProperty(Long.prototype, "__isLong__", {
    value: true,
    enumerable: false,
    configurable: false
});

