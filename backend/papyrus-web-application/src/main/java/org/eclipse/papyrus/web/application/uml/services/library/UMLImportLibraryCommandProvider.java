/*****************************************************************************
 * Copyright (c) 2026 Obeo, CEA LIST.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Vincent LORENZO (CEA LIST) vincent.lorenzo@cea.fr - Initial API and implementation
 *****************************************************************************/
package org.eclipse.papyrus.web.application.uml.services.library;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.papyrus.web.application.uml.services.library.api.IPapyrusCapableEditingContextPredicate;
import org.eclipse.sirius.components.collaborative.omnibox.api.IWorkbenchOmniboxCommandProvider;
import org.eclipse.sirius.components.collaborative.omnibox.dto.OmniboxCommand;
import org.springframework.stereotype.Service;

/**
 * Provides the import studio command in the omnibox.
 *
 * Adapted from StudioImportLibraryCommandProvider
 *
 * @author Vincent LORENZO
 */
@Service
public class UMLImportLibraryCommandProvider implements IWorkbenchOmniboxCommandProvider {

    public static final String IMPORT_LIBRARY_COMMAND_ID = "importUMLLibrary";

    private final IPapyrusCapableEditingContextPredicate papyrusCapableEditingContextPredicate;

    public UMLImportLibraryCommandProvider(IPapyrusCapableEditingContextPredicate papyrusCapableEditingContextPredicate) {
        this.papyrusCapableEditingContextPredicate = Objects.requireNonNull(papyrusCapableEditingContextPredicate);
    }

    @Override
    public List<OmniboxCommand> getCommands(String editingContextId, List<String> selectedObjectIds, String query) {
        List<OmniboxCommand> result = new ArrayList<>();
        if (this.papyrusCapableEditingContextPredicate.test(editingContextId)) {
            result.add(new OmniboxCommand(IMPORT_LIBRARY_COMMAND_ID, "Import UML libraries", List.of("/omnibox/import.svg"), "Import UML libraries in the project"));
        }
        return result;
    }

}
