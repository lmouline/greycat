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
package org.mwg.internal.scheduler;

import org.mwg.plugin.Job;
import org.mwg.plugin.Scheduler;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * boing,boing,boing....
 */
public class TrampolineScheduler implements Scheduler {

    /**
     * @native ts
     * private queue = new org.mwg.internal.scheduler.JobQueue();
     */
    private final Deque<Job> queue = new ConcurrentLinkedDeque<Job>();
    private final AtomicInteger wip = new AtomicInteger(0);

    @Override
    public void dispatch(final byte affinity, Job job) {
        queue.add(job);
        if (wip.getAndIncrement() == 0) {
            do {
                final Job polled = queue.poll();
                if (polled != null) {
                    polled.run();
                }
            } while (wip.decrementAndGet() > 0);
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {

    }

    @Override
    public int workers() {
        return 1;
    }

}
