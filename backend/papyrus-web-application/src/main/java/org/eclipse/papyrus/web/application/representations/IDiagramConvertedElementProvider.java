/*******************************************************************************
 * Copyright (c) 2025 CEA List, Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.papyrus.web.application.representations;

import java.util.Map;

import org.eclipse.sirius.components.view.diagram.EdgeDescription;
import org.eclipse.sirius.components.view.diagram.NodeDescription;
import org.eclipse.sirius.web.application.editingcontext.EditingContext;

/**
 * Service that retrieves the converted nodes and edges from the name of a representation.
 *
 * @author Arthur Daussy
 */
public interface IDiagramConvertedElementProvider {

    /**
     * Get the maps of converted node from the name of the diagram description.
     *
     * @param diagramName
     *         the name of the diagram description (view)
     * @param editingContext
     *         the editing context
     * @return a map or null unable to find the diagram description
     */
    Map<NodeDescription, org.eclipse.sirius.components.diagrams.description.NodeDescription> getConvertedNode(String diagramName, EditingContext editingContext);

    /**
     * Get the maps of converted edges from the name of the diagram description.
     *
     * @param diagramName
     *         the name of the diagram description (view)
     * @param editingContext
     *         the editing context
     * @return a map or null unable to find the diagram description
     */
    Map<EdgeDescription, org.eclipse.sirius.components.diagrams.description.EdgeDescription> getConvertedEdges(String diagramName, EditingContext editingContext);

    /**
     * Empty implementation.
     *
     * @author Arthur Daussy
     */
    class NoOp implements IDiagramConvertedElementProvider {

        @Override
        public Map<NodeDescription, org.eclipse.sirius.components.diagrams.description.NodeDescription> getConvertedNode(String diagramName, EditingContext editingContext) {
            return Map.of();
        }

        @Override
        public Map<EdgeDescription, org.eclipse.sirius.components.diagrams.description.EdgeDescription> getConvertedEdges(String diagramName, EditingContext editingContext) {
            return Map.of();
        }
    }
}
