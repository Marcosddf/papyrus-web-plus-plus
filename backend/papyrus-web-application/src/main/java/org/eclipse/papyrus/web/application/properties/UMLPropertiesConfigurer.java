/*******************************************************************************
 * Copyright (c) 2023, 2024, 2025 CEA LIST, Obeo.
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
package org.eclipse.papyrus.web.application.properties;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.papyrus.web.application.templates.service.api.IUMLProjectCheckerService;
import org.eclipse.sirius.components.collaborative.forms.services.api.IPropertiesDescriptionRegistry;
import org.eclipse.sirius.components.collaborative.forms.services.api.IPropertiesDescriptionRegistryConfigurer;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContextProcessor;
import org.eclipse.sirius.components.emf.services.IDAdapter;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.components.interpreter.AQLInterpreter;
import org.eclipse.sirius.components.view.View;
import org.eclipse.sirius.components.view.emf.IJavaServiceProvider;
import org.eclipse.sirius.components.view.emf.ViewConverterResult;
import org.eclipse.sirius.components.view.emf.form.ViewFormDescriptionConverter;
import org.eclipse.sirius.components.view.form.FormDescription;
import org.eclipse.sirius.web.application.editingcontext.EditingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuration in charge of contributing the UML details view.
 *
 * @author Arthur Daussy
 */
@Configuration
public class UMLPropertiesConfigurer implements IPropertiesDescriptionRegistryConfigurer, IEditingContextProcessor {

    public static final String UML_DETAIL_VIEW_NAME = "UML Detail View";

    private static final UUID NAME_UUID_FROM_BYTES = UUID.nameUUIDFromBytes(UMLPropertiesConfigurer.class.getCanonicalName().getBytes());

    private static final Logger LOGGER = LoggerFactory.getLogger(UMLPropertiesConfigurer.class);

    private final ViewFormDescriptionConverter converter;

    private final Registry globalEPackageRegistry;

    private final AdvancedPropertiesDescriptionProvider defaultPropertyViewProvider;

    private final List<IJavaServiceProvider> javaServiceProviders;

    private final ApplicationContext applicationContext;
    private final IUMLProjectCheckerService umlChecker;

    private View view;

    public UMLPropertiesConfigurer(ViewFormDescriptionConverter converter,
            EPackage.Registry globalEPackageRegistry,
            AdvancedPropertiesDescriptionProvider defaultPropertyViewProvider,
            ApplicationContext applicationContext,
            List<IJavaServiceProvider> javaServiceProviders,
            IUMLProjectCheckerService umlChecker) {
        this.defaultPropertyViewProvider = Objects.requireNonNull(defaultPropertyViewProvider);
        this.globalEPackageRegistry = Objects.requireNonNull(globalEPackageRegistry);
        this.converter = Objects.requireNonNull(converter);
        this.javaServiceProviders = Objects.requireNonNull(javaServiceProviders);
        this.applicationContext = Objects.requireNonNull(applicationContext);
        this.umlChecker = Objects.requireNonNull(umlChecker);
    }

    private List<EPackage> findGlobalEPackages() {
        return this.globalEPackageRegistry.values().stream().filter(EPackage.class::isInstance).map(EPackage.class::cast).toList();
    }


    @PostConstruct
    public void registryUMLProperties() {
        // The FormDescription must be part of View inside a proper EMF Resource to be correctly handled
        URI uri = URI.createURI(IEMFEditingContext.RESOURCE_SCHEME + ":///" + NAME_UUID_FROM_BYTES);
        Resource resource = new JSONResourceFactory().createResource(uri);
        this.view = new UMLDetailViewFromBuilder(UML_DETAIL_VIEW_NAME).build();
        view.eAllContents().forEachRemaining(eObject -> {
            eObject.eAdapters().add(new IDAdapter(UUID.nameUUIDFromBytes(EcoreUtil.getURI(eObject).toString().getBytes())));
        });
        resource.getContents().add(view);

    }

    @Override
    public void postProcess(IEditingContext editingContext) {
        // Add this view in post process so it is not converted to API Form, this part is done in addPropertiesDescriptions
        // We still need to register it in the view of the editing context to be able to access the view model from the several handlers
        if (this.view != null && editingContext instanceof EditingContext siriusWebEditingContext && this.umlChecker.isPapyrusProject(editingContext.getId())) {
            siriusWebEditingContext.getViews().add(this.view);
        }
    }

    @Override
    public void addPropertiesDescriptions(IPropertiesDescriptionRegistry registry) {

        // Convert the view
        view.getDescriptions().stream()
                .filter(FormDescription.class::isInstance)
                .map(FormDescription.class::cast)
                .forEach(formDescription -> {
                    // Convert the View-based FormDescription and register the result into the system
                    // Compute the list of service

                    AutowireCapableBeanFactory beanFactory = this.applicationContext.getAutowireCapableBeanFactory();
                    List<Object> serviceInstances = this.javaServiceProviders.stream()
                            .flatMap(provider -> provider.getServiceClasses(view).stream())
                            .map(serviceClass -> {
                                try {
                                    return beanFactory.createBean(serviceClass);
                                } catch (BeansException beansException) {
                                    LOGGER.warn("Error while trying to instantiate Java service class " + serviceClass.getName(), beansException);
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .map(Object.class::cast)
                            .toList();
                    AQLInterpreter interpreter = new AQLInterpreter(List.of(), serviceInstances, findGlobalEPackages());

                    ViewConverterResult converterResult = this.converter.convert(formDescription, List.of(), interpreter);
                    if (converterResult != null && converterResult.representationDescription() instanceof org.eclipse.sirius.components.forms.description.FormDescription apiFormDescription) {
                        apiFormDescription.getPageDescriptions().forEach(registry::add);
                    }

                    // Register the "Advance Property View"
                    this.defaultPropertyViewProvider.getFormDescription().getPageDescriptions().forEach(registry::add);
                });


    }


}
