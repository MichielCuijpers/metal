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

package io.parsingdata.metal.data.selection;

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.selection.ByToken.getAllRoots;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseItemList;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.token.Token;

public final class ByOffset {

    private ByOffset() {}

    public static boolean hasRootAtOffset(final ParseGraph graph, final Token definition, final long ref) {
        return findItemAtOffset(getAllRoots(graph, definition), ref) != null;
    }

    public static ParseItem findItemAtOffset(final ParseItemList items, final long ref) {
        checkNotNull(items, "items");
        if (items.isEmpty()) { return null; }
        final ParseItem head = items.head;
        if (head.isValue() && head.asValue().getOffset() == ref) {
            return head;
        }
        if (head.isGraph() && containsLocalValue(head.asGraph()) && getLowestOffsetValue(head.asGraph()).getOffset() == ref) {
            return head;
        }
        return findItemAtOffset(items.tail, ref);
    }

    public static ParseValue getLowestOffsetValue(final ParseGraph graph) {
        checkNotNull(graph, "graph");
        if (!containsLocalValue(graph)) {
            throw new IllegalStateException("Cannot determine lowest offset if graph does not contain a value.");
        }
        return getLowestOffsetValueRecursive(graph);
    }

    private static boolean containsLocalValue(final ParseGraph graph) {
        if (graph.isEmpty() || !graph.getDefinition().isLocal()) { return false; }
        if (graph.head.isGraph()) {
            return containsLocalValue(graph.head.asGraph()) || containsLocalValue(graph.tail);
        }
        return graph.head.isValue() || containsLocalValue(graph.tail);
    }

    private static ParseValue getLowestOffsetValueRecursive(final ParseGraph graph) {
        final ParseItem head = graph.head;
        if (head.isValue()) {
            return getLowestOffsetValue(graph.tail, head.asValue());
        }
        if (head.isGraph() && containsLocalValue(head.asGraph())) {
            return getLowestOffsetValue(graph.tail, getLowestOffsetValueRecursive(head.asGraph()));
        }
        return getLowestOffsetValueRecursive(graph.tail);
    }

    private static ParseValue getLowestOffsetValue(final ParseGraph graph, final ParseValue lowest) {
        if (!containsLocalValue(graph)) { return lowest; }
        final ParseItem head = graph.head;
        if (head.isValue()) {
            return getLowestOffsetValue(graph.tail, lowest.getOffset() < head.asValue().getOffset() ? lowest : head.asValue());
        }
        if (head.isGraph()) {
            return getLowestOffsetValue(graph.tail, getLowestOffsetValue(head.asGraph(), lowest));
        }
        return getLowestOffsetValue(graph.tail, lowest);
    }

}
