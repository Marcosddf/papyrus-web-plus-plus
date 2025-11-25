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
import java.util.Objects;
import java.util.Optional;

import org.eclipse.sirius.components.view.diagram.DiagramDescription;
import org.eclipse.sirius.components.view.diagram.EdgeDescription;
import org.eclipse.sirius.components.view.diagram.NodeDescription;
import org.eclipse.sirius.components.view.emf.IViewConversionData;
import org.eclipse.sirius.components.view.emf.diagram.IDiagramIdProvider;
import org.eclipse.sirius.components.view.emf.diagram.ViewDiagramConversionData;
import org.eclipse.sirius.web.application.editingcontext.EditingContext;
import org.springframework.stereotype.Service;

/**
 * {@link IDiagramConvertedElementProvider} used for Papyrus Web diagrams.
 *
 * @author Arthur Daussy
 */
@Service
public class PapyrusDiagramConvertedElementProvider implements IDiagramConvertedElementProvider {


    private final PapyrusRepresentationDescriptionRegistry papyrusRepresentationDescriptionRegistry;

    private final IDiagramIdProvider diagramIdProvider;


    public PapyrusDiagramConvertedElementProvider(PapyrusRepresentationDescriptionRegistry papyrusRepresentationDescriptionRegistry, IDiagramIdProvider diagramIdProvider) {
        this.papyrusRepresentationDescriptionRegistry = Objects.requireNonNull(papyrusRepresentationDescriptionRegistry);
        this.diagramIdProvider = Objects.requireNonNull(diagramIdProvider);
    }

    @Override
    public Map<NodeDescription, org.eclipse.sirius.components.diagrams.description.NodeDescription> getConvertedNode(String diagramName, EditingContext editingContext) {
        return getConversionData(diagramName, editingContext).map(ViewDiagramConversionData::convertedNodes).orElse(Map.of());
    }

    @Override
    public Map<EdgeDescription, org.eclipse.sirius.components.diagrams.description.EdgeDescription> getConvertedEdges(String diagramName, EditingContext editingContext) {
        return getConversionData(diagramName, editingContext).map(ViewDiagramConversionData::convertedEdges).orElse(Map.of());
    }

    private Optional<ViewDiagramConversionData> getConversionData(String representationName, EditingContext editingContext) {
        // First look for registered papyrus diagrams
        String descriptionId = papyrusRepresentationDescriptionRegistry.getDiagramDescriptionIdByName(representationName);

        if (descriptionId == null) {
            // Otherwise fallback on generic mechanism
            descriptionId = editingContext.getViews().stream()
                    .flatMap(view -> view.getDescriptions().stream())
                    .filter(DiagramDescription.class::isInstance)
                    .map(DiagramDescription.class::cast)
                    .filter(diagramDescription -> representationName.equals(diagramDescription.getName()))
                    .findFirst()
                    .map(diagramIdProvider::getId)
                    .orElse(null);
        }

        if (descriptionId != null) {
            IViewConversionData conversionData = editingContext.getViewConversionData().get(descriptionId);
            if (conversionData instanceof ViewDiagramConversionData diagramConversionData) {
                return Optional.of(diagramConversionData);
            }
        }
        return Optional.empty();

    }
}
