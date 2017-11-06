/*
 * Copyright 2017, OpenSkywalking Organization All rights reserved.
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
 *
 * Project repository: https://github.com/OpenSkywalking/skywalking
 */

package org.skywalking.apm.collector.core.graph;

/**
 * The <code>Node</code> in the graph with explicit INPUT and OUTPUT types.
 *
 * @author peng-yongsheng, wu-sheng
 */
public final class Node<INPUT, OUTPUT> {
    private final NodeProcessor nodeProcessor;
    private final Next<OUTPUT> next;
    private final Graph graph;

    Node(Graph graph, NodeProcessor<INPUT, OUTPUT> nodeProcessor) {
        this.graph = graph;
        this.nodeProcessor = nodeProcessor;
        this.next = new Next<>();
        this.graph.checkForNewNode(this);
    }

    public final <NEXTOUTPUT> Node<OUTPUT, NEXTOUTPUT> addNext(NodeProcessor<OUTPUT, NEXTOUTPUT> nodeProcessor) {
        synchronized (graph) {
            Node<OUTPUT, NEXTOUTPUT> node = new Node<>(graph, nodeProcessor);
            next.addNext(node);
            return node;
        }
    }

    final void execute(INPUT INPUT) {
        nodeProcessor.process(INPUT, next);
    }

    NodeProcessor getHandler() {
        return nodeProcessor;
    }

    Next<OUTPUT> getNext() {
        return next;
    }
}