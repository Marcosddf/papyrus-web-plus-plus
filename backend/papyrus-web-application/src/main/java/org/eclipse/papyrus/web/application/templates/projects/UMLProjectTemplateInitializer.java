/*****************************************************************************
 * Copyright (c) 2022, 2023, 2025 CEA LIST, Obeo.
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
 *  Dilan EESHVARAN (CEA LIST) dilan.eeshvaran@cea.fr - https://gitlab.eclipse.org/eclipse/papyrus/org.eclipse.papyrus-web/-/issues/125
 *****************************************************************************/
package org.eclipse.papyrus.web.application.templates.projects;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.sirius.components.core.RepresentationMetadata;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.events.ICause;
import org.eclipse.sirius.web.application.project.services.api.IProjectTemplateInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Initializes the contents of projects created from a UML project template.
 *
 * @author pcdavid
 */
@Configuration
public class UMLProjectTemplateInitializer implements IProjectTemplateInitializer {
    private static final String UML_MODEL_TITLE = "Model.uml";

    private final Logger logger = LoggerFactory.getLogger(UMLProjectTemplateInitializer.class);

    private final TemplateInitializer initializerHelper;

    public UMLProjectTemplateInitializer(TemplateInitializer initializerHelper) {
        this.initializerHelper = Objects.requireNonNull(initializerHelper);
    }

    @Override
    public boolean canHandle(String templateId) {
        return List.of(UMLProjectTemplateProvider.UML_WITH_PRIMITIVES_TEMPLATE_ID, UMLProjectTemplateProvider.EMPTY_UML_TEMPLATE).contains(templateId);
    }

    @Override
    public Optional<RepresentationMetadata> handle(ICause cause, String templateId, IEditingContext editingContext) {
        Optional<RepresentationMetadata> result = Optional.empty();
        if (UMLProjectTemplateProvider.UML_WITH_PRIMITIVES_TEMPLATE_ID.equals(templateId)) {
            result = this.initializeUMLWithPrimitivesProjectContents(editingContext, cause);
        } else if (UMLProjectTemplateProvider.EMPTY_UML_TEMPLATE.equals(templateId)) {
            result = Optional.empty();
        }
        return result;
    }

    private Optional<RepresentationMetadata> initializeUMLWithPrimitivesProjectContents(IEditingContext editingContext, ICause cause) {
        try {
            this.initializerHelper.initializeResourceFromClasspathFile(editingContext, UML_MODEL_TITLE, "DefaultUMLWithPrimitive.uml", cause);
        } catch (IOException e) {
            this.logger.error("Error while creating template", e);
        }
        return Optional.empty();
    }

}
