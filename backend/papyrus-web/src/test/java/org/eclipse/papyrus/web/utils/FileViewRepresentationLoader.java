/*****************************************************************************
 * Copyright (c) 2023, 2025 CEA LIST, Obeo.
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
package org.eclipse.papyrus.web.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.sirius.components.emf.services.IDAdapter;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.components.emf.utils.EMFResourceUtils;
import org.eclipse.sirius.components.representations.IRepresentationDescription;
import org.eclipse.sirius.components.view.View;
import org.eclipse.sirius.components.view.ViewPackage;
import org.eclipse.sirius.components.view.emf.IViewConverter;
import org.eclipse.sirius.components.view.emf.ViewConverterResult;
import org.eclipse.sirius.web.application.editingcontext.EditingContext;
import org.springframework.core.io.ClassPathResource;

/**
 * Helper used to load an register a representation defined in view model file into the
 * IRepresentationDescriptionRegistry.
 *
 * @author Arthur Daussy
 */
public class FileViewRepresentationLoader {

    private final IViewConverter viewConverter;

    private final EditingContext siriusEditingContext;

    private final String filePath;

    private final List<View> views = new ArrayList<>();

    public FileViewRepresentationLoader(String filePath, IViewConverter viewConverter, EditingContext siriusEditingContext) {
        this.filePath = filePath;
        this.viewConverter = viewConverter;
        this.siriusEditingContext = siriusEditingContext;
    }

    public List<IRepresentationDescription> loadRepresentations() {
        Optional<View> optionalView = this.load(new ClassPathResource(this.filePath), List.of(ViewPackage.eINSTANCE), View.class);

        if (optionalView.isPresent()) {
            var view = optionalView.get();

            view.eAllContents().forEachRemaining(eObject -> {
                eObject.eAdapters().add(new IDAdapter(UUID.nameUUIDFromBytes(EcoreUtil.getURI(eObject).toString().getBytes())));
            });


            this.views.add(view);
            return this.viewConverter.convert(siriusEditingContext, List.of(view)).stream().map(ViewConverterResult::representationDescription).toList();
        }
        return List.of();
    }

    public List<View> getViews() {
        return this.views;
    }

    private <T> Optional<T> load(ClassPathResource classPathResource, List<EPackage> requiredEPackages, Class<T> rootElementType) {
        Optional<T> result = Optional.empty();

        ResourceSet resourceSet = new ResourceSetImpl();
        EPackageRegistryImpl ePackageRegistry = new EPackageRegistryImpl();
        EPackage.Registry.INSTANCE.forEach(ePackageRegistry::put);
        requiredEPackages.forEach(ePackage -> ePackageRegistry.put(ePackage.getNsURI(), ePackage));
        resourceSet.setPackageRegistry(ePackageRegistry);

        try (var inputStream = classPathResource.getInputStream()) {
            URI uri = URI.createURI(IEMFEditingContext.RESOURCE_SCHEME + ":///" + UUID.nameUUIDFromBytes(classPathResource.getPath().getBytes()));
            Resource resource = new XMIResourceImpl(uri);
            resourceSet.getResources().add(resource);
            resource.load(inputStream, new EMFResourceUtils().getXMILoadOptions());

            return resource.getContents().stream()
                    .filter(rootElementType::isInstance)
                    .map(rootElementType::cast)
                    .findFirst();
        } catch (IOException exception) {
        }
        return result;
    }
}
