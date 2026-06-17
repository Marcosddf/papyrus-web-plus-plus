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

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

/**
 * Utility to compute the editing context identifier associated with a uml profile uri.
 *
 * @author Dilan EESHVARAN
 */
public final class ProfileEditingContextIdProvider {

    private ProfileEditingContextIdProvider() {
        // no instantiation
    }

    /**
     * compute a editing context id for a profile identified by its uri path.
     * for dynamic profiles : the uuid is already in the uri path (pathmap://WEB_DYNAMIC_PROFILE/{uuid}#fragment) so its directly used as the editing context id.
     * for static profiles : a name based uuid (uuid) derived from the full uri path string (deterministic : same uri = same uuid)
     */
    public static String computeEditingContextId(String uriPath) {
        String nonNullUriPath = Objects.requireNonNull(uriPath);
        String result;
        if (isDynamicProfileUri(nonNullUriPath)) {
            String afterPrefix = nonNullUriPath.substring(UMLProfileService.WEB_DYNAMIC_PROFILE_RESOURCE_PREFIX.length());
            int hashIdx = afterPrefix.indexOf('#');
            if (hashIdx >= 0) {
                result = afterPrefix.substring(0, hashIdx);
            } else {
                result = afterPrefix;
            }
        } else {
            result = UUID.nameUUIDFromBytes(nonNullUriPath.getBytes(StandardCharsets.UTF_8)).toString();
        }
        return result;
    }

    // returns whether a profile uri path identifies a statically registered profile.
    public static boolean isStaticProfileUri(String uriPath) {
        return !isDynamicProfileUri(uriPath);
    }

    // returns whether a profile uri path identifies a dynamic published profile.
    public static boolean isDynamicProfileUri(String uriPath) {
        return Objects.requireNonNull(uriPath).startsWith(UMLProfileService.WEB_DYNAMIC_PROFILE_RESOURCE_PREFIX);
    }
}
