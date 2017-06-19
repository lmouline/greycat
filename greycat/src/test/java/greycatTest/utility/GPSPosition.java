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
package greycatTest.utility;

import greycat.Type;
import greycat.base.BaseCustomTypeSingle;
import greycat.struct.EGraph;
import greycat.utility.HashHelper;

public class GPSPosition extends BaseCustomTypeSingle {

    private static final String LAT = "lat";
    private static final int LAT_HASH = HashHelper.hash(LAT);
    private static final String LNG = "lng";
    private static final int LNG_HASH = HashHelper.hash(LNG);

    private static final int DEF_NODE = 0;

    public GPSPosition(final EGraph e) {
        super(e);
        if(this._backend.node(DEF_NODE).typeAt(LAT_HASH) != Type.DOUBLE){
            this._backend.node(DEF_NODE).setAt(LAT_HASH, Type.DOUBLE, 1.5d).setAt(LNG_HASH, Type.DOUBLE, 1.5d);
        }
    }

    public final double lat() {
        return (double) getAt(LAT_HASH);
    }

    public final double lng() {
        return (double) getAt(LNG_HASH);
    }

    public final void setPosition(double lat, double lng) {
        this._backend.node(DEF_NODE).setAt(LAT_HASH, Type.DOUBLE, lat).setAt(LNG_HASH, Type.DOUBLE, lng);
    }

    @Override
    public String toString() {
        return "position(" + lat() + "," + lng() + ")";
    }



}
