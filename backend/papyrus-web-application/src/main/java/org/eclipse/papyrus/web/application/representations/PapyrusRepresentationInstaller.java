/*****************************************************************************
 * Copyright (c) 2024, 2025 CEA LIST, Obeo.
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
 *****************************************************************************/
package org.eclipse.papyrus.web.application.representations;

import java.util.Objects;

import org.eclipse.papyrus.web.application.templates.service.api.IUMLProjectCheckerService;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContextProcessor;
import org.eclipse.sirius.web.application.editingcontext.EditingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Set up Papyrus representations in the editing context.
 *
 * @author Arthur Daussy
 */
@Service
public class PapyrusRepresentationInstaller implements IEditingContextProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PapyrusRepresentationInstaller.class);

    private final PapyrusRepresentationDescriptionRegistry papyrusRepresentationRegistry;

    private final IUMLProjectCheckerService umlChecker;

    public PapyrusRepresentationInstaller(PapyrusRepresentationDescriptionRegistry papyrusRepresentationRegistry, IUMLProjectCheckerService umlChecker) {
        super();
        this.papyrusRepresentationRegistry = Objects.requireNonNull(papyrusRepresentationRegistry);
        this.umlChecker = Objects.requireNonNull(umlChecker);
    }

    @Override
    public void preProcess(IEditingContext editingContext) {
        if (editingContext instanceof EditingContext siriusWebEditingContext && this.umlChecker.isPapyrusProject(editingContext.getId())) {
            // Registered in pre-process phase so they will be converted during the editing context loading process
            siriusWebEditingContext.getViews().addAll(papyrusRepresentationRegistry.getPapyrusViews());
        }
    }


}
