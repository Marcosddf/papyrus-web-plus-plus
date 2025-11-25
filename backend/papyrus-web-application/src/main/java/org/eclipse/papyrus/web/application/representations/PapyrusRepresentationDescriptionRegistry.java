/*******************************************************************************
 * Copyright (c) 2022, 2025 CEA LIST, Obeo.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Obeo - Initial API and implementation
 *******************************************************************************/
package org.eclipse.papyrus.web.application.representations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.sirius.components.representations.IRepresentationDescription;
import org.eclipse.sirius.components.view.View;
import org.eclipse.sirius.components.view.diagram.DiagramDescription;
import org.eclipse.sirius.components.view.emf.diagram.IDiagramIdProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry that keeps track of all {@link IRepresentationDescription}s used in Papyrus application.
 *
 * @author Arthur Daussy
 */
public class PapyrusRepresentationDescriptionRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(PapyrusRepresentationDescriptionRegistry.class);

    private final Map<String, DiagramDescription> viewDiagramDescriptionById = new HashMap<>();

    private final List<View> papyrusViews = new ArrayList<>();

    private final Map<String, String> diagramDescriptionNameToId = new HashMap<>();

    private final IDiagramIdProvider diagramIdProvider;

    public PapyrusRepresentationDescriptionRegistry(IDiagramIdProvider diagramIdProvider) {
        this.diagramIdProvider = Objects.requireNonNull(diagramIdProvider);
    }

    /**
     * Register a view containing a Papyrus web representation.
     *
     * @param view a view
     */
    public void registerView(View view) {
        papyrusViews.add(view);

        for (var description : view.getDescriptions()) {
            if (description instanceof DiagramDescription diagramDescription) {
                diagramDescriptionNameToId.put(description.getName(), diagramIdProvider.getId(diagramDescription));
            }
        }
    }

    public String getDiagramDescriptionIdByName(String diagramName) {
        return diagramDescriptionNameToId.get(diagramName);
    }

    public boolean isPapyrusWebDiagramDescriptionId(String id) {
        return diagramDescriptionNameToId.containsValue(id);
    }

    public List<View> getPapyrusViews() {
        return papyrusViews;
    }

}
