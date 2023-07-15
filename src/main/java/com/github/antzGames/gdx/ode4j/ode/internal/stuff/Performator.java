/*************************************************************************
 *                                                                       *
 * Open Dynamics Engine 4J, Copyright (C) 2009-2014 Tilmann Zaeschke     *
 * All rights reserved.  Email: ode4j@gmx.de   Web: www.ode4j.org        *
 *                                                                       *
 * This library is free software; you can redistribute it and/or         *
 * modify it under the terms of EITHER:                                  *
 *   (1) The GNU Lesser General Public License as published by the Free  *
 *       Software Foundation; either version 2.1 of the License, or (at  *
 *       your option) any later version. The text of the GNU Lesser      *
 *       General Public License is included with this library in the     *
 *       file LICENSE.TXT.                                               *
 *   (2) The BSD-style license that is included with this library in     *
 *       the file ODE4J-LICENSE-BSD.TXT.                                 *
 *                                                                       *
 * This library is distributed in the hope that it will be useful,       *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the files    *
 * LICENSE.TXT and ODE4J-LICENSE-BSD.TXT for more details.               *
 *                                                                       *
 *************************************************************************/
package com.github.antzGames.gdx.ode4j.ode.internal.stuff;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tilmann Zaeschke
 */
public final class Performator {

    private static final ConcurrentHashMap<String, Entry> data =
        new ConcurrentHashMap<String, Entry>();

    /**
     * Start time measurement.
     * @param key key
     */
    public static final void begin(String key) {
        Entry e = data.get(key);
        //funny construct for theoretical thread safety
        if (e == null) {
            e = new Entry();
        	data.putIfAbsent(key, e);
        	e = data.get(key);
        }
        e.begin();
    }

    /**
     * Stop time measurement.
     * @param key key
     */
    public static final void end(String key) {
        data.get(key).end();
    }

    /**
     * Print results.
     */
    public static final void print() {
        List<String> keys = new LinkedList<String>();
        keys.addAll(data.keySet());
        Collections.sort(keys);
        for (String key: keys) {
            System.out.println(data.get(key).print() + " :: " + key);
        }
    }

    private static final class Entry {
        long _total = 0;
        long _nCalls = 0;

        /**
         * @return Returns String representation
         */
        public final String print() {
            StringBuffer b = new StringBuffer();
            b.append("Calls: " + _nCalls);
            while (b.length() < 25) b.append(' ');
            b.append("Time [ms]: " + _total);
            while (b.length() < 50) b.append(' ');
            b.append("T/C [ms]: " + _total/(double)_nCalls);
            return b.toString();
        }

        /**
         *
         */
        public final void end() {
            _total += System.currentTimeMillis();
            _nCalls++;
        }

        /**
         *
         */
        public final void begin() {
            _total -= System.currentTimeMillis();
        }

    }

    /**
     *
     */
    public static final void reset() {
        data.clear();
    }
}
