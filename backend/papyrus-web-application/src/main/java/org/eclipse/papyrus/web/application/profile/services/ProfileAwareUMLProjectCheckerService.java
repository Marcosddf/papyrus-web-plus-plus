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

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.papyrus.web.application.profile.dto.UMLProfileMetadata;
import org.eclipse.papyrus.web.application.profile.services.api.IUMLProfileProvider;
import org.eclipse.papyrus.web.application.templates.service.UMLProjectCheckerService;
import org.eclipse.papyrus.web.application.templates.service.api.IUMLProjectCheckerService;
import org.eclipse.papyrus.web.domain.boundedcontext.profile.service.api.IProfileSearchService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * primary implementation of IUMLProjectCheckerService to make it recognise profile editing context id as
 * papyrus projects, so that all uml configurers (PathmapURIHandler, CrossRefAdapter, etc) are applied to profile
 * viewing contexts.
 *
 * @author Dilan EESHVARAN
 */
@Primary
@Service
public class ProfileAwareUMLProjectCheckerService implements IUMLProjectCheckerService {

    private final UMLProjectCheckerService delegate;

    private final IProfileSearchService profileSearchService;

    // save ids of the statically registered profiles to avoid hitting db everytime
    private final Set<String> staticProfileContextIds;

    public ProfileAwareUMLProjectCheckerService(UMLProjectCheckerService delegate, IProfileSearchService profileSearchService, List<IUMLProfileProvider> umlProfileProviders) {
        this.delegate = Objects.requireNonNull(delegate);
        this.profileSearchService = Objects.requireNonNull(profileSearchService);
        this.staticProfileContextIds = Objects.requireNonNull(umlProfileProviders).stream()
                .flatMap(provider -> provider.getUMLProfiles().stream())
                .map(UMLProfileMetadata::getUriPath)
                .filter(ProfileEditingContextIdProvider::isStaticProfileUri)
                .map(ProfileEditingContextIdProvider::computeEditingContextId)
                .collect(Collectors.toSet());
    }

    // its a papyrus context if it is a normal papyrus project OR if it is a profile viewing context
    @Override
    public boolean isPapyrusProject(String editingContextId) {
        return this.delegate.isPapyrusProject(editingContextId) || this.isProfileContext(editingContextId);
    }

    // returns true if the given id identifies a profile viewing context, either statically or dynamic profile
    public boolean isProfileContext(String id) {
        // check static first
        boolean result = this.staticProfileContextIds.contains(id);
        if (!result) {
            try {
                UUID uuid = UUID.fromString(id);
                // check dynamic profile
                result = this.profileSearchService.existsById(uuid);
            } catch (IllegalArgumentException e) {
                // not a profile
                result = false;
            }
        }
        return result;
    }
}
