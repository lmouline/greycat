/**
 * Copyright 2017 The GreyCat Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package greycat.memory;

import greycat.chunk.ChunkSpace;
import greycat.chunk.ChunkType;
import greycat.chunk.TimeTreeChunk;
import greycat.internal.chunk.AbstractTimeTreeTest;
import greycat.struct.Buffer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class OffHeapTimeTreeChunkTest extends AbstractTimeTreeTest {

    public OffHeapTimeTreeChunkTest() {
        super(new OffHeapMemoryFactory());
    }

    @After
    public void tearDown() throws Exception {
        if (OffHeapConstants.DEBUG_MODE) {
            Assert.assertEquals(OffHeapConstants.SEGMENTS.size(), 0);
        }
    }

    /*
    @Test
    public void nextTest() {
        ChunkSpace space = factory.newSpace(100, null, false);
        TimeTreeChunk tree = (TimeTreeChunk) space.createAndMark(ChunkType.TIME_TREE_CHUNK, 0, 0, 0);

        Buffer buffer = factory.newBuffer();
        buffer.writeAll("C|Y|A|BA:Y2l0aWVz:CiJIlv:bmFtZQ:ZvUW:aW5kZXg:L9lGk:ZGlzdGFuY2U:iYxfq:Ym91bmRfbWlu:CJHb/f:Ym91bmRfbWF4:CJHcG7:cG9zaXRpb25z:DLgRjU:bnVtYmVy:B7Tklv:cG9zaXRpb24:BZJTJS:YmlrZV9zdGFuZHM:DbSr0L:U0xPVFNfTlVNQkVS:DWDUab:UEVSSU9EX1NJWkU:CRuC9+:c3RhdGlvbl9wcm9maWxl:CBQITF:YXZhaWxhYmxlX2Jpa2Vz:BX0pd4:YXZhaWxhYmxlX2Jpa2Vfc3RhbmRz:BjLLX3:c3RhdHVz:BqZGAd:c3RhdGlvbnM:CdKEt+:Y29udHJhY3RfbmFtZQ:9GgTR:YWRkcmVzcw:CI0MgZ:YmFua2luZw:oLc+1:Ym9udXM:LMkA+:bGF0:0M+:bG5n:0ZK:ZWdyYXBo:Cbi4/v:c3RyYXRlZ3k:DVH0bm:X2ZlYXR1cmVzTmI:BvxlDg:X3RvdGFs:CumYN3:X21pbg:Wasm:X21heA:WalK:X3N1bQ:WdsY:X3N1bVNxdWFyZQ:BT/kxv:dmFsdWU:NWSLi#G;////////9;////////+;C#U#A;A;////////9;EAAAAAC#G|E|ZvUW|QW1pZW5z|a|DLgRjU|C:EAAAAAG|c|CdKEt+|C:Xp4046vu1:EAAAAAI#C;A;////////+;EAAAAAC#C:////////9#C;A;////////9;EAAAAAC#C:////////9#E;A;A;EAAAAAC#C:A:////////9#A;A;////////9;////////8#C|U|L9lGk|C:CiJIlv:EAAAAAE#C;A;////////+;////////8#C:////////9#C;A;////////9;////////8#C:////////9#E;A;A;////////8#C:A:////////9#A;A;////////9;EAAAAAE#C|c|L9lGk|C:xOmUkPQp8:EAAAAAC#C;A;////////+;EAAAAAE#C:////////9#C;A;////////9;EAAAAAE#C:////////9#E;A;A;EAAAAAE#DcBchh|C:A:////////9#A;A;////////9;EAAAAAG#K|I|iYxfq|C|M|CJHb/f|E:wFGg:wGGg|M|CJHcG7|E:QFGg:QGGg|i|Cbi4/v|C$Q%G%A%C%G%E%A%G%C%A%M%G%E:wFGg:wGGg%M%I%E:QFGg:QGGg%e%K%m:QAA:QCA:P/A:QEI8vW9ZxFd:QACXI9DaL42:f/I:f/I:f/I:f/I:f/I:f/I:f/I:f/I:f/I:f/I:f/I:f/I:f/I:f/I%g%M%W:C:Q:C:EAAAAAI:D:D:D:D:D:D:D%G%A%C|I|DVH0bm|A#C;A;////////+;EAAAAAG#C:////////9#C;A;////////9;EAAAAAG#C:////////9#E;A;A;EAAAAAG#Dt/7MZ|C:A:////////9#A;A;////////9;EAAAAAI#O|E|B7Tklv|MDAwMw|a|BZJTJS|C:EAAAAAK|a|DbSr0L|C:EAAAAAM|a|CBQITF|C:EAAAAAO|a|BX0pd4|C:EAAAAAQ|a|BjLLX3|C:EAAAAAS|a|BqZGAd|C:EAAAAAU#C;A;////////+;EAAAAAI#C:////////9#C;A;////////9;EAAAAAI#E:////////9:q76Juyg#E;A;A;EAAAAAI#C:A:////////9#A;A;////////9;EAAAAAK#A#C;A;////////+;EAAAAAK#C:////////9#C;A;////////9;EAAAAAK#E:////////9:q76Juyg#E;A;A;EAAAAAK#C:A:////////9#A;A;////////9;EAAAAAM#A#C;A;////////+;EAAAAAM#C:////////9#C;A;////////9;EAAAAAM#E:////////9:q76Juyg#E;A;A;EAAAAAM#C:A:////////9#A;A;////////9;EAAAAAO#E|I|DWDUab|FQ|G|CRuC9+|BIGQgA#C;A;////////+;EAAAAAO#BIGQgA|C:////////9#C;A;////////9;EAAAAAO#E:////////9:q7mezgA#E;A;A;EAAAAAO#DyNara|C:A:////////9#A;A;////////9;EAAAAAQ#A#C;A;////////+;EAAAAAQ#C:////////9#C;A;////////9;EAAAAAQ#W:////////9:q76Juyg:q7/CVoA:q7/dx+Q:q8APAXQ:q8BNF/w:q8BNdbw:q8BSEWw:q8CrNbw:q8DTxIw:q8D3lng#E;A;A;EAAAAAQ#C:A:////////9#A;A;////////9;EAAAAAS#A#C;A;////////+;EAAAAAS#C:////////9#C;A;////////9;EAAAAAS#W:////////9:q76Juyg:q7/CVoA:q7/dx+Q:q8APAXQ:q8BNF/w:q8BNdbw:q8BSEWw:q8Cq3dg:q8DTxIw:q8D3lng#E;A;A;EAAAAAS#C:A:////////9#A;A;////////9;EAAAAAU#A#C;A;////////+;EAAAAAU#C:////////9#C;A;////////9;EAAAAAU#E:////////9:q76Juyg#E;A;A;EAAAAAU#C:A:////////9#A;A;q76Juyg;EAAAAAK#E|K|0M+|QEI8vW9ZxFd|K|0ZK|QACXI9DaL42#A;A;q76Juyg;EAAAAAQ#C|I|NWSLi|G#A;A;q76Juyg;EAAAAAS#C|I|NWSLi|i#A;A;q76Juyg;EAAAAAU#C|E|NWSLi|T1BFTg#A;A;q76Juyg;EAAAAAM#C|I|NWSLi|o".getBytes());
        tree.load(buffer);

        buffer.free();
        space.free(tree);
        space.freeAll();
    }
    */


}
