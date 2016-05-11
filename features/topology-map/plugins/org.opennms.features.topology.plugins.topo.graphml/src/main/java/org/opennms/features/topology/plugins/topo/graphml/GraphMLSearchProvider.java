/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.topology.plugins.topo.graphml;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.opennms.features.topology.api.GraphContainer;
import org.opennms.features.topology.api.topo.SearchQuery;
import org.opennms.features.topology.api.topo.SimpleSearchProvider;
import org.opennms.features.topology.api.topo.VertexRef;

/**
 * {@link org.opennms.features.topology.api.topo.SearchProvider} for GraphML definitions.
 * The provider searches for matching vertices in ALL graphs, not only the current visible one.
 */
public class GraphMLSearchProvider extends SimpleSearchProvider {

    private final GraphMLTopologyProvider graphMLTopologyProvider;

    public GraphMLSearchProvider(GraphMLTopologyProvider graphMLTopologyProvider) {
        this.graphMLTopologyProvider = Objects.requireNonNull(graphMLTopologyProvider);
    }

    @Override
    public String getSearchProviderNamespace() {
        return graphMLTopologyProvider.getVertexNamespace();
    }

    @Override
    public List<? extends VertexRef> queryVertices(SearchQuery searchQuery, GraphContainer container) {
        final List<GraphMLVertex> matchingVertices = new ArrayList<>();
        graphMLTopologyProvider.getVertices().stream()
            .map(v -> (GraphMLVertex) v)
            .filter(v -> matches(searchQuery, v))
            .sorted((v1, v2) -> v1.getId().compareTo(v2.getId()))
            .forEach(v -> matchingVertices.add(v));
        return matchingVertices;
    }

    /**
     * Returns true if either if either the graph node's id, or the values of any
     * of the graph node's properties contain the query string.
     */
    private static boolean matches(SearchQuery searchQuery, GraphMLVertex graphMLVertex) {
        final String qs = searchQuery.getQueryString().toLowerCase();
        for (Object propValue : graphMLVertex.getProperties().values()) {
            final String value = propValue != null ? propValue.toString() : "";
            if (value.toLowerCase().contains(qs)) {
                return true;
            }
        }
        return graphMLVertex.getId().toLowerCase().contains(qs);
    }
}
