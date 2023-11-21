/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package freemarker.ext.beans;

import java.lang.reflect.Method;

public class RecordAccessor {

    private final Method RECORD_GET_RECORD_COMPONENTS;
    private final Method RECORD_COMPONENT_GET_ACCESSOR;

    private final static RecordAccessor INSTANCE;
    private final static RuntimeException PROBLEM;

    static {
        RuntimeException prob = null;
        RecordAccessor inst = null;
        try {
            inst = new RecordAccessor();
        } catch (RuntimeException e) {
            prob = e;
        }
        INSTANCE = inst;
        PROBLEM = prob;
    }

    private RecordAccessor() throws RuntimeException {
        try {
            RECORD_GET_RECORD_COMPONENTS = Class.class.getMethod("getRecordComponents");
            Class<?> c = Class.forName("java.lang.reflect.RecordComponent");
            RECORD_COMPONENT_GET_ACCESSOR = c.getMethod("getAccessor");
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Failed to access Methods needed to support `java.lang.Record`: (%s) %s",
                    e.getClass().getName(), e.getMessage()), e);
        }
    }

    public static RecordAccessor instance() {
        if (PROBLEM != null) {
            throw PROBLEM;
        }
        return INSTANCE;
    }

    private Object[] recordComponents(Class<?> recordType) throws IllegalArgumentException {
        try {
            return (Object[]) RECORD_GET_RECORD_COMPONENTS.invoke(recordType);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to access RecordComponents of type "
                    + recordType.getName());
        }
    }

    public Method[] getAccessors(Class<?> recordType) throws IllegalArgumentException {
        final Object[] components = recordComponents(recordType);
        if (components == null) {
            // not a record, or no reflective access on native image
            return null;
        }
        final Method[] accessors = new Method[components.length];
        for (int i = 0; i < components.length; i++) {
            try {
                accessors[i] = (Method) RECORD_COMPONENT_GET_ACCESSOR.invoke(components[i]);
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format(
                        "Failed to access name of field #%d (of %d) of Record type %s",
                        i, components.length, recordType.getName()), e);
            }
        }
        return accessors;
    }

}
