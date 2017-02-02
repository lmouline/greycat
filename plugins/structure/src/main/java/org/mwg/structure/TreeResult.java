/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.structure;

/**
 * Created by assaad on 05/01/2017.
 */
public interface TreeResult {

    int size();

    boolean insert(double[] key, long value, double distance);

    double[] keys(int index);
    long value(int index);
    double distance(int index);
    double getWorstDistance();

    void free();
    boolean isCapacityReached();
    void sort(boolean ascending);
}
