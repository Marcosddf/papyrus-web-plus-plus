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

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.web.application.editingcontext.services.api.IEditingContextPersistenceFilter;
import org.springframework.stereotype.Service;

/**
 * prevent the read only resources from being saved back in the db.
 *
 * @author Dilan EESHVARAN
 */
@Service
public class ProfileEditingContextPersistenceFilter implements IEditingContextPersistenceFilter {

    // if uri is pathmap then dont save it, because pathmap:// are used for preinstalled or readonly resources
    @Override
    public boolean shouldPersist(Resource resource) {
        if (resource != null && resource.getURI() != null) {
            return !"pathmap".equals(resource.getURI().scheme());
        }
        return true;
    }
}
