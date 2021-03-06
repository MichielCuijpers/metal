/*
 * Copyright 2013-2016 Netherlands Forensic Institute
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

package io.parsingdata.metal.expression.value;

import java.util.NoSuchElementException;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseValue;

public class OptionalValue {

    private final Value value; // Private because access is explicitly guarded.

    private OptionalValue(final Value value) {
        this.value = value;
    }

    private OptionalValue() {
        this(null);
    }

    public static OptionalValue of(final Value value) {
        return new OptionalValue(value);
    }

    public static OptionalValue empty() {
        return new OptionalValue();
    }

    public static ImmutableList<OptionalValue> wrap(final ImmutableList<ParseValue> list) {
        Util.checkNotNull(list, "list");
        if (list.isEmpty()) { return new ImmutableList<>(); }
        return wrap(list.tail).add(of(list.head));
    }

    public boolean isPresent() {
        return value != null;
    }

    public Value get() {
        if (isPresent()) { return value; }
        else { throw new NoSuchElementException("OptionalValue instance is empty."); }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + (!isPresent() ? "empty" : value) + ")";
    }

}
