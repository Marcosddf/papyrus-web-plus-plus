/*******************************************************************************
 * Copyright (c) 2026 CEA LIST.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Dilan EESHVARAN (CEA LIST) - Initial API and implementation
 *******************************************************************************/
import { gql, useQuery } from '@apollo/client';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import {
  RepresentationMetadata,
  RepresentationPathContext,
  SelectionContextProvider,
  Workbench,
} from '@eclipse-sirius/sirius-components-core';
import { NavigationBar, useInitialWorkbenchConfiguration } from '@eclipse-sirius/sirius-web-application';
import { useState } from 'react';
import { useParams } from 'react-router-dom';

interface GQLEditingContext {
  id: string;
}

interface GQLProfileMetadata {
  label: string;
  currentEditingContext: GQLEditingContext | null;
}

interface GQLViewer {
  profileMetadatas: GQLProfileMetadata[];
}

interface GQLGetProfileMetadatasData {
  viewer: GQLViewer;
}

const getProfileLabelQuery = gql`
  query getUMLProfileMetadatasForDisplay {
    viewer {
      profileMetadatas {
        label
        currentEditingContext {
          id
        }
      }
    }
  }
`;

export const DisplayProfileView = () => {
  const { editingContextId } = useParams<{ editingContextId: string }>();
  const { workbenchConfiguration } = useInitialWorkbenchConfiguration(editingContextId ?? null);
  const [representation, setRepresentation] = useState<RepresentationMetadata | null>(null);

  const { data } = useQuery<GQLGetProfileMetadatasData>(getProfileLabelQuery);

  const profileLabel =
    data?.viewer?.profileMetadatas?.find((profile) => profile.currentEditingContext?.id === editingContextId)?.label ??
    '';

  const getRepresentationPath = (_representationId: string): string => `/profiles/${editingContextId}`;

  const onRepresentationSelected = (representationMetadata: RepresentationMetadata | null) => {
    setRepresentation(representationMetadata);
  };

  if (!editingContextId) {
    return null;
  }

  return (
    <Box
      sx={{
        display: 'grid',
        gridTemplateRows: 'min-content minmax(0, 1fr)',
        gridTemplateColumns: '1fr',
        height: '100vh',
        width: '100vw',
      }}>
      <RepresentationPathContext.Provider value={{ getRepresentationPath }}>
        <SelectionContextProvider initialSelection={{ entries: [] }}>
          <NavigationBar>
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
              <Box sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
                <Typography
                  variant="h6"
                  noWrap
                  sx={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', maxWidth: '100ch' }}
                  data-testid="navbar-title">
                  {profileLabel || 'Profile'}
                </Typography>
              </Box>
            </Box>
          </NavigationBar>
          {workbenchConfiguration ? (
            <Workbench
              editingContextId={editingContextId}
              initialRepresentationSelected={representation}
              onRepresentationSelected={onRepresentationSelected}
              readOnly={true}
              initialWorkbenchConfiguration={workbenchConfiguration}
            />
          ) : null}
        </SelectionContextProvider>
      </RepresentationPathContext.Provider>
    </Box>
  );
};
