/*****************************************************************************
 * Copyright (c) 2024, 2025 CEA LIST.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Obeo - Initial API and implementation
 *****************************************************************************/
package org.eclipse.papyrus.web.application.explorer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.papyrus.web.application.templates.service.api.IUMLProjectCheckerService;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.trees.description.TreeDescription;
import org.eclipse.sirius.web.application.editingcontext.EditingContext;
import org.eclipse.sirius.web.application.views.explorer.services.api.IExplorerTreeDescriptionProvider;
import org.springframework.stereotype.Service;

/**
 * Explorer to be used inside an Papyrus UML Editing context.
 *
 * @author Arthur Daussy
 */
@Service
public class UMLDefaultTreeExplorerProvider implements IExplorerTreeDescriptionProvider {
    private final IUMLProjectCheckerService umlChecker;

    private final UMLDefaultTreeExplorerInstaller treeExplorerInstaller;

    public UMLDefaultTreeExplorerProvider(IUMLProjectCheckerService umlChecker, UMLDefaultTreeExplorerInstaller treeExplorerInstaller) {
        this.umlChecker = Objects.requireNonNull(umlChecker);
        this.treeExplorerInstaller = Objects.requireNonNull(treeExplorerInstaller);
    }

    @Override
    public List<TreeDescription> getDescriptions(IEditingContext editingContext) {
        if (this.umlChecker.isPapyrusProject(editingContext.getId())) {
            var optionalUMLExplorerDescription = this.getUMLExplorerTreeDescription(editingContext);

            if (optionalUMLExplorerDescription.isPresent()) {
                return List.of(optionalUMLExplorerDescription.get());
            }
        }
        return List.of();
    }

    private Optional<TreeDescription> getUMLExplorerTreeDescription(IEditingContext editingContext) {
        if (editingContext instanceof EditingContext siriusEditingContext) {
            return Optional.ofNullable((TreeDescription) siriusEditingContext.getRepresentationDescriptions().get(treeExplorerInstaller.getDescriptionId()));
        }
        return Optional.empty();
    }
}
