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

import java.util.List;

import org.eclipse.papyrus.web.application.representations.uml.IPapyrusViewRepresentationDescriptionBuilder;
import org.eclipse.sirius.components.view.emf.diagram.IDiagramIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure the {@link PapyrusRepresentationDescriptionRegistry}.
 *
 * @author Arthur Daussy
 */
@Configuration
public class PapyrusRepresentationDescriptionRegistryConfigurer {

    @Bean
    public PapyrusRepresentationDescriptionRegistry buildPapyrusRepresentationDescriptionRegistry(List<IPapyrusViewRepresentationDescriptionBuilder> builders, IDiagramIdProvider diagramIdProvider) {
        PapyrusRepresentationDescriptionRegistry registry = new PapyrusRepresentationDescriptionRegistry(diagramIdProvider);
        for (var builder : builders) {
            registry.registerView(builder.buildRepresentationDescriptionView());
        }
        return registry;
    }
}
