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

import java.util.Objects;
import java.util.Optional;

import org.eclipse.sirius.web.application.library.services.LibraryEditingContextService;
import org.eclipse.sirius.web.application.library.services.api.ILibraryEditingContextService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Sirius web checks access to normal projects and libraries only and a profile viewer is not a project. so we mark it
 * as "library like" to expose profile viewing contexts with read only access
 *
 * @author Dilan EESHVARAN
 */
@Primary
@Service
public class ProfileAwareLibraryEditingContextService implements ILibraryEditingContextService {

    // identifier to trick access control into treating profile contexts as "library like"
    private static final String PROFILE_LIBRARY_IDENTIFIER_PREFIX = "profile:";

    // delegate to avoid override original method
    private final LibraryEditingContextService delegate;

    private final ProfileAwareUMLProjectCheckerService profileChecker;

    public ProfileAwareLibraryEditingContextService(LibraryEditingContextService delegate, ProfileAwareUMLProjectCheckerService profileChecker) {
        this.delegate = Objects.requireNonNull(delegate);
        this.profileChecker = Objects.requireNonNull(profileChecker);
    }

    // ask original library identifier if its a library
    // if not, return a profile library identifier if its a profile viewing context to allow read only access
    @Override
    public Optional<String> getLibraryIdentifier(String editingContextId) {
        Optional<String> optionalLibraryIdentifier = this.delegate.getLibraryIdentifier(editingContextId);
        if (optionalLibraryIdentifier.isEmpty() && this.profileChecker.isProfileContext(editingContextId)) {
            optionalLibraryIdentifier = Optional.of(PROFILE_LIBRARY_IDENTIFIER_PREFIX + editingContextId);
        }
        return optionalLibraryIdentifier;
    }
}
