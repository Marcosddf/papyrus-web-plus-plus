/*****************************************************************************
 * Copyright (c) 2025 CEA LIST, Obeo.
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
package org.eclipse.papyrus.web.application.configuration;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.components.collaborative.api.ChangeDescription;
import org.eclipse.sirius.components.collaborative.api.IInputPostProcessor;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IInput;
import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.web.application.document.dto.UploadDocumentInput;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Sinks;

/**
 *
 * {@link IInputPostProcessor} in charge of forcing the resolution of proxies after uploading a document.
 *
 * @author Arthur Daussy
 */
@Service
public class UploadPostEditingProcessEvent implements IInputPostProcessor {

    @Override
    public void postProcess(IEditingContext editingContext, IInput input, Sinks.Many<ChangeDescription> changeDescriptionSink) {
        if (editingContext instanceof IEMFEditingContext emfEditingContext && input instanceof UploadDocumentInput uploadDocumentInput) {
            emfEditingContext.getDomain().getResourceSet().getResources().stream()
                    .filter(resource -> resource.eAdapters().stream()
                            .filter(ResourceMetadataAdapter.class::isInstance)
                            .map(ResourceMetadataAdapter.class::cast)
                            .anyMatch(resourceMetadataAdapter -> resourceMetadataAdapter.getName().equals(uploadDocumentInput.file().getName())))
                    .forEach(EcoreUtil::resolveAll);
        }
    }
}
