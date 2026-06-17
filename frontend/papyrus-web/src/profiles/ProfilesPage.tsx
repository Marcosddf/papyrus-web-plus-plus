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
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import { MaterialReactTable, MRT_ColumnDef, useMaterialReactTable } from 'material-react-table';
import { useMemo } from 'react';
import { Link } from 'react-router-dom';
import { NavigationBar, footerExtensionPoint } from '@eclipse-sirius/sirius-web-application';
import { useComponent } from '@eclipse-sirius/sirius-components-core';

interface GQLEditingContext {
  id: string;
}

interface GQLProfileMetadata {
  label: string;
  uriPath: string;
  version: string | null;
  author: string | null;
  date: string | null;
  comment: string | null;
  copyright: string | null;
  currentEditingContext: GQLEditingContext | null;
}

interface GQLViewer {
  profileMetadatas: GQLProfileMetadata[];
}

interface GQLGetProfileMetadatasData {
  viewer: GQLViewer;
}

const getUMLProfileMetadatasQuery = gql`
  query getUMLProfileMetadatas {
    viewer {
      profileMetadatas {
        label
        uriPath
        version
        author
        date
        comment
        copyright
        currentEditingContext {
          id
        }
      }
    }
  }
`;

export const ProfilesPage = () => {
  const { Component: Footer } = useComponent(footerExtensionPoint);

  const { loading, data, error } = useQuery<GQLGetProfileMetadatasData>(getUMLProfileMetadatasQuery);

  const profiles: GQLProfileMetadata[] = data?.viewer?.profileMetadatas ?? [];

  const columns = useMemo<MRT_ColumnDef<GQLProfileMetadata>[]>(
    () => [
      {
        accessorKey: 'label',
        header: 'Name',
        size: 180,
        Cell: ({ row }) => {
          const id = row.original.currentEditingContext?.id;
          if (id) {
            return (
              <Link to={`/profiles/${id}`} style={{ textDecoration: 'none' }}>
                <Typography noWrap color="primary">
                  {row.original.label}
                </Typography>
              </Link>
            );
          }
          return <Typography noWrap>{row.original.label}</Typography>;
        },
      },
      {
        accessorKey: 'version',
        header: 'Version',
        size: 80,
        Cell: ({ renderedCellValue }) => <Typography noWrap>{renderedCellValue ?? ''}</Typography>,
      },
      {
        accessorKey: 'date',
        header: 'Date',
        size: 100,
        Cell: ({ renderedCellValue }) => <Typography noWrap>{renderedCellValue ?? ''}</Typography>,
      },
      {
        accessorKey: 'author',
        header: 'Author',
        size: 120,
        Cell: ({ renderedCellValue }) => <Typography noWrap>{renderedCellValue ?? ''}</Typography>,
      },
      {
        accessorKey: 'comment',
        header: 'Comments',
        size: 200,
        grow: true,
        Cell: ({ renderedCellValue }) => <Typography noWrap>{renderedCellValue ?? ''}</Typography>,
      },
      {
        accessorKey: 'copyright',
        header: 'Copyright',
        size: 160,
        Cell: ({ renderedCellValue }) => <Typography noWrap>{renderedCellValue ?? ''}</Typography>,
      },
      {
        accessorKey: 'uriPath',
        header: 'URI Path',
        size: 260,
        Cell: ({ renderedCellValue }) => (
          <Typography noWrap sx={{ opacity: '0.6', fontFamily: 'monospace', fontSize: '0.8rem' }}>
            {renderedCellValue}
          </Typography>
        ),
      },
    ],
    []
  );

  const table = useMaterialReactTable({
    columns,
    data: profiles,
    enableColumnActions: false,
    enableColumnFilters: false,
    enableFullScreenToggle: false,
    enableDensityToggle: false,
    enableHiding: false,
    enableRowSelection: false,
    positionPagination: 'bottom',
    state: {
      isLoading: loading,
      showAlertBanner: !!error,
    },
    muiToolbarAlertBannerProps: error
      ? {
          color: 'error',
          children: 'An unexpected error has occurred while loading profiles. Please refresh the page.',
        }
      : undefined,
    muiTablePaperProps: {
      elevation: 0,
      sx: { display: 'flex', flexDirection: 'column' },
    },
    muiTableContainerProps: {
      sx: { maxHeight: 'calc(100vh - 250px)', overflowY: 'auto' },
    },
  });

  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <NavigationBar />
      <Container maxWidth="xl" sx={{ flexGrow: 1, py: 4 }}>
        <Grid container justifyContent="center">
          <Grid size={{ xs: 12, lg: 10 }}>
            <main>
              <Box
                sx={{
                  display: 'flex',
                  flexDirection: 'row',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  pb: 2,
                }}>
                <Typography variant="h4">Profiles</Typography>
              </Box>
              <Box sx={{ width: '100%' }}>
                <MaterialReactTable table={table} />
              </Box>
            </main>
          </Grid>
        </Grid>
      </Container>
      <Footer />
    </Box>
  );
};
