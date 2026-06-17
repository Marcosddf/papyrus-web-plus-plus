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
package org.eclipse.papyrus.web.application.profile.controllers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.papyrus.web.application.profile.dto.UMLProfileMetadata;
import org.eclipse.papyrus.web.application.profile.services.ProfileEditingContextIdProvider;
import org.eclipse.sirius.components.annotations.spring.graphql.QueryDataFetcher;
import org.eclipse.sirius.components.graphql.api.IDataFetcherWithFieldCoordinates;
import org.eclipse.sirius.components.graphql.api.LocalContextConstants;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;

/**
 * data fetcher for the field UMLProfileMetadata#currentEditingContext.
 * computes a deterministic editing context id for the profile and propagates it in the graphql local context so that
 * nested fields (ex: representation descriptions) can resolve the correct editing context.
 * @author Dilan EESHVARAN
 */
@QueryDataFetcher(type = "UMLProfileMetadata", field = "currentEditingContext")
public class ProfileCurrentEditingContextDataFetcher implements IDataFetcherWithFieldCoordinates<DataFetcherResult<String>> {

    @Override
    public DataFetcherResult<String> get(DataFetchingEnvironment environment) throws Exception {
        UMLProfileMetadata profile = environment.getSource();
        String editingContextId = ProfileEditingContextIdProvider.computeEditingContextId(profile.getUriPath());

        Map<String, Object> localContext = new HashMap<>();
        localContext.put(LocalContextConstants.EDITING_CONTEXT_ID, editingContextId);

        return DataFetcherResult.<String>newResult()
                .data(editingContextId)
                .localContext(localContext)
                .build();
    }

}
