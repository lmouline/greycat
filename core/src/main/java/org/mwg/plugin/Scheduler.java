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
package org.mwg.plugin;

/**
 * In charge of the scheduling of tasks in mwDB
 */
public interface Scheduler {

    /**
     * Registers a job for execution.
     *
     * @param affinity The job thread affinity
     * @param job      The new job to execute.
     */
    void dispatch(byte affinity, Job job);

    /**
     * Starts the scheduler (i.e.: the execution of tasks).
     */
    void start();

    /**
     * Terminates the scheduler (i.e.: the execution of tasks).
     */
    void stop();

    /**
     * Return the number of parallel workers
     * @return integer representing the number of current parallel workers
     */
    int workers();

}
