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
 * Node state, used to access all stored state element of a particular node
 */
public interface NodeState {

    /**
     * Returns the id of the world this state is attached to.
     *
     * @return the world id
     */
    long world();

    /**
     * Returns the time this state is attached to.
     *
     * @return current resolved time
     */
    long time();

    /**
     * Set the named state element
     *
     * @param index    unique key of element
     * @param elemType type of the element (based on Type definition)
     * @param elem     element to be set
     */
    void set(int index, byte elemType, Object elem);

    /**
     * Set the named state element
     *
     * @param key      unique key of element
     * @param elemType type of the element (based on Type definition)
     * @param elem     element to be set
     */
    void setFromKey(String key, byte elemType, Object elem);

    /**
     * Get the named state element
     *
     * @param index unique key of element
     * @return stored element
     */
    Object get(int index);

    /**
     * Get the named state element
     *
     * @param key unique key of element
     * @return stored element
     */
    Object getFromKey(String key);

    /**
     * Get the named state element
     *
     * @param key          unique key of element
     * @param defaultValue default value in case of null on the previous state
     * @param <A>          The type of the value
     * @return typed stored element
     */
    <A> A getFromKeyWithDefault(String key, A defaultValue);

    /**
     * Get from the state element long
     *
     * @param key          unique long key of element
     * @param defaultValue default value in case of null on the previous state
     * @param <A>          The type of the value
     * @return stored element
     */
    <A> A getWithDefault(int key, A defaultValue);

    /**
     * Atomically get or create an element according to the elemType parameter.
     * This method is particularly handy for map manipulation that have to be initialize by the node state before any usage.
     *
     * @param index    unique key of element
     * @param elemType type of the element (according to Type definition)
     * @return new or previously stored element
     */
    Object getOrCreate(int index, byte elemType);

    /**
     * Atomically get or create an element according to the elemType parameter.
     * This method is particularly handy for map manipulation that have to be initialize by the node state before any usage.
     *
     * @param key      unique key of element
     * @param elemType type of the element (according to Type definition)
     * @return new or previously stored element
     */
    Object getOrCreateFromKey(String key, byte elemType);

    /**
     * Get the type of the stored element, -1 if not found
     *
     * @param index unique key of element
     * @return type currently stored, encoded as a int according the Type defintion
     */
    byte getType(int index);

    /**
     * Get the type of the stored element, -1 if not found
     *
     * @param key unique key of element
     * @return type currently stored, encoded as a int according the Type defintion
     */
    byte getTypeFromKey(String key);


    /**
     * Iterate over NodeState elements
     *
     * @param callBack the method to be called for each state.
     */
    void each(NodeStateCallback callBack);

}
