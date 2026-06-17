/*******************************************************************************
 * Copyright (c) 2026 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Dilan EESHVARAN (CEA LIST) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.papyrus.web.application.profile.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.papyrus.web.application.profile.dto.UMLProfileMetadata;
import org.eclipse.papyrus.web.application.profile.services.api.IUMLProfileService;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContextProcessor;
import org.eclipse.sirius.components.core.api.IEditingContextRepresentationDescriptionProvider;
import org.eclipse.sirius.components.core.api.IEditingContextSearchService;
import org.eclipse.sirius.web.application.editingcontext.EditingContext;
import org.eclipse.sirius.web.application.editingcontext.services.EditingContextSearchService;
import org.eclipse.sirius.web.application.editingcontext.services.api.IEditingDomainFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * implementation of IEditingContextSearchService that intercepts profile context id, builds a EditingContext using the
 * profile resource. for all other than profiles, it delegates to the standard EditingContextSearchService so projects
 * are opened normally.
 *
 * @author Dilan EESHVARAN
 */
@Primary
@Service
public class ProfileEditingContextSearchService implements IEditingContextSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileEditingContextSearchService.class);

    private final EditingContextSearchService delegate;

    private final IUMLProfileService umlProfileService;

    private final IEditingDomainFactory editingDomainFactory;

    private final List<IEditingContextRepresentationDescriptionProvider> representationDescriptionProviders;

    private final List<IEditingContextProcessor> editingContextProcessors;

    private final ProfileAwareUMLProjectCheckerService profileChecker;

    // CHECKSTYLE:OFF
    public ProfileEditingContextSearchService(
            EditingContextSearchService delegate,
            IUMLProfileService umlProfileService,
            IEditingDomainFactory editingDomainFactory,
            @Lazy List<IEditingContextRepresentationDescriptionProvider> representationDescriptionProviders,
            @Lazy List<IEditingContextProcessor> editingContextProcessors,
            ProfileAwareUMLProjectCheckerService profileChecker) {
        this.delegate = Objects.requireNonNull(delegate);
        this.umlProfileService = Objects.requireNonNull(umlProfileService);
        this.editingDomainFactory = Objects.requireNonNull(editingDomainFactory);
        this.representationDescriptionProviders = Objects.requireNonNull(representationDescriptionProviders);
        this.editingContextProcessors = Objects.requireNonNull(editingContextProcessors);
        this.profileChecker = Objects.requireNonNull(profileChecker);
    }
    // CHECKSTYLE:ON

    @Override
    public boolean existsById(String editingContextId) {
        if (this.profileChecker.isProfileContext(editingContextId)) {
            return true;
        }
        return this.delegate.existsById(editingContextId);
    }

    @Override
    public Optional<IEditingContext> findById(String editingContextId) {
        if (this.profileChecker.isProfileContext(editingContextId)) {
            return this.buildProfileEditingContext(editingContextId);
        }
        return this.delegate.findById(editingContextId);
    }

    private Optional<IEditingContext> buildProfileEditingContext(String editingContextId) {
        // step 1: search all available profile and find matching profile with computed id = requested editingContextId
        Optional<UMLProfileMetadata> profileOpt = this.umlProfileService.getAllUMLProfiles().stream()
                .filter(p -> editingContextId.equals(ProfileEditingContextIdProvider.computeEditingContextId(p.getUriPath())))
                .findFirst();

        if (profileOpt.isEmpty()) {
            return Optional.empty();
        }

        String uriPath = profileOpt.get().getUriPath();

        // step 2: create emf editing domain
        AdapterFactoryEditingDomain domain = this.editingDomainFactory.createEditingDomain(editingContextId);

        // step 3: wrap emf editing domain and create virtual sirius editing context
        EditingContext editingContext = new EditingContext(editingContextId, domain, new HashMap<>(), new ArrayList<>());

        // step 4: run pre processors before loading to setup editing context (PathmapURIHandler)
        this.editingContextProcessors.forEach(p -> p.preProcess(editingContext));

        // step 5: load profile resource from uri into editing context resource set if not already loaded
        int hashIdx = uriPath.indexOf('#');
        String resourceUri;
        if (hashIdx >= 0) {
            resourceUri = uriPath.substring(0, hashIdx);
        } else {
            resourceUri = uriPath;
        }
        try {
            domain.getResourceSet().getResource(URI.createURI(resourceUri), true);
            // CHECKSTYLE:OFF
        } catch (Exception e) {
            // CHECKSTYLE:ON
            LOGGER.error("failed to load profile resource '{}'", resourceUri, e);
        }

        // step 6: give workbench available representation descriptions
        this.representationDescriptionProviders.forEach(provider -> provider.getRepresentationDescriptions(editingContext)
                .forEach(desc -> editingContext.getRepresentationDescriptions().put(desc.getId(), desc)));

        // step 7: run post processors after loading
        this.editingContextProcessors.forEach(p -> p.postProcess(editingContext));

        return Optional.of(editingContext);
    }

}
