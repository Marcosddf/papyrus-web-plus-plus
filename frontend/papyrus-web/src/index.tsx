/*******************************************************************************
 * Copyright (c) 2019, 2026 CEA LIST, Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *     Titouan BOUETE-GIRAUD (Artal Technologies) - Issue 210
 *     Vincent LORENZO (CEA LIST) vincent.lorenzo@cea.fr - Issue GL-324
 *     Dilan EESHVARAN (CEA LIST) - Issue 323
 *******************************************************************************/
import { loadDevMessages, loadErrorMessages } from '@apollo/client/dev';

import {
  DiagramRepresentationConfiguration,
  footerExtensionPoint,
  navigationBarIconExtensionPoint,
  navigationBarMenuHelpURLExtensionPoint,
  navigationBarMenuEntryExtensionPoint,
  routerExtensionPoint,
  SiriusWebApplication,
  ViewerContextProvider,
} from '@eclipse-sirius/sirius-web-application';
import { httpOrigin, wsOrigin } from './core/URL';

import './ReactFlow.css';
import './fonts.css';
import './portals.css';
import './reset.css';
import './variables.css';
import {
  papyrusWebExtensionRegistry,
  ExtensionRegistryMergeStrategy,
  papyrusNodeTypeRegistryValue,
} from '@eclipse-papyrus/papyrus-web-components';
import { createRoot } from 'react-dom/client';
import { Footer } from './footer/Footer';
import { PapyrusNavigationBarIcon } from './core/PapyrusNavigationBarIcon';
import { papyrusTheme } from './theme/papyrusTheme';
import { ProfilesMenuItem } from './profiles/ProfilesMenuItem';
import { ProfilesPage } from './profiles/ProfilesPage';

if (process.env.NODE_ENV !== 'production') {
  loadDevMessages();
  loadErrorMessages();
}

// Footer contribution
papyrusWebExtensionRegistry.addComponent(footerExtensionPoint, {
  identifier: 'papyrus-footer',
  Component: Footer,
});

// Main icon contribution
papyrusWebExtensionRegistry.addComponent(navigationBarIconExtensionPoint, {
  identifier: 'papyrusweb_navigationbar#icon',
  Component: PapyrusNavigationBarIcon,
});

// Customize help url
papyrusWebExtensionRegistry.putData(navigationBarMenuHelpURLExtensionPoint, {
  identifier: `papyrus_web_doc_${navigationBarMenuHelpURLExtensionPoint.identifier}`,
  data: `${httpOrigin}/doc/index.html`,
});

// Profiles menu item contribution
papyrusWebExtensionRegistry.addComponent(navigationBarMenuEntryExtensionPoint, {
  identifier: 'papyrus-profiles-menu-item',
  Component: ProfilesMenuItem,
});

// Profiles route contribution
papyrusWebExtensionRegistry.putData(routerExtensionPoint, {
  identifier: 'papyrus-profiles-route',
  data: [
    {
      path: '/profiles',
      element: (
        <ViewerContextProvider>
          <ProfilesPage />
        </ViewerContextProvider>
      ),
    },
  ],
});

const container = document.getElementById('root');
const root = createRoot(container!);
root.render(
  <SiriusWebApplication
    httpOrigin={httpOrigin}
    wsOrigin={wsOrigin}
    theme={papyrusTheme}
    extensionRegistry={papyrusWebExtensionRegistry}
    extensionRegistryMergeStrategy={new ExtensionRegistryMergeStrategy()}>
    <DiagramRepresentationConfiguration nodeTypeRegistry={papyrusNodeTypeRegistryValue} />
  </SiriusWebApplication>
);
