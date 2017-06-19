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
import greycat.struct.EGraph;

public class GPSPosition {

    private EGraph backend;

    public GPSPosition(final EGraph e) {
        this.backend = e;
        if(e.size() == 0){
            this.backend.newNode().set("lat", Type.DOUBLE, 1.5d).set("lng", Type.DOUBLE, 1.5d);
        }
    }

    public final double lat() {
        return (double) backend.node(0).get("lat");
    }

    public final double lng() {
        return (double) backend.node(0).get("lng");
    }

    public final void setPosition(double lat, double lng) {
        this.backend.node(0).set("lat", Type.DOUBLE, lat).set("lng", Type.DOUBLE, lng);
    }

    @Override
    public String toString() {
        return "position(" + lat() + "," + lng() + ")";
    }
}
