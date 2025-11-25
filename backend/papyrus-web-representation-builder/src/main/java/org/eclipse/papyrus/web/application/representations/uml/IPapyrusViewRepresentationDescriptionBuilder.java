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
package org.eclipse.papyrus.web.application.representations.uml;

import org.eclipse.sirius.components.view.View;

/**
 * Builder of Papyrus Representation.
 *
 * @author Arthur Daussy
 */
public interface IPapyrusViewRepresentationDescriptionBuilder {

    /**
     * Builds a view containing a representation to be registered in Papyrus Web editing contexts.
     *
     * @return a view containing a representation.
     */
    View buildRepresentationDescriptionView();

}
