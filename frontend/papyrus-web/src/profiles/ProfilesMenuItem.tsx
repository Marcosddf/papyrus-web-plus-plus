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

import MenuItem from '@mui/material/MenuItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import { Link as RouterLink } from 'react-router-dom';
import SvgIcon, { SvgIconProps } from '@mui/material/SvgIcon';

const ProfilesMenuIcon = (props: SvgIconProps) => (
  <SvgIcon {...props} viewBox="0 0 24 24">
    <path d="M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8z" />
    <text
      x="12"
      y="13"
      fill="white"
      textAnchor="middle"
      dominantBaseline="middle"
      fontSize="10"
      fontWeight="700"
      fontFamily="monospace">
      {'<p>'}
    </text>
  </SvgIcon>
);

export const ProfilesMenuItem = () => {
  return (
    <MenuItem component={RouterLink} to="/profiles" data-testid="profiles-link">
      <ListItemIcon>
        <ProfilesMenuIcon />
      </ListItemIcon>
      <ListItemText primary="Profiles" />
    </MenuItem>
  );
};
