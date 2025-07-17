/*******************************************************************************
 * Copyright (c) 2019, 2025 CEA LIST, Obeo.
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
 *******************************************************************************/
import { loadDevMessages, loadErrorMessages } from '@apollo/client/dev';
import { ExtensionRegistry } from '@eclipse-sirius/sirius-components-core';
import {
  DiagramPaletteToolContributionProps,
  diagramPaletteToolExtensionPoint,
  EdgeData,
  NodeData,
  NodeTypeContribution,
} from '@eclipse-sirius/sirius-components-diagrams';
import {
  ApolloClientOptionsConfigurer,
  apolloClientOptionsConfigurersExtensionPoint,
  DefaultExtensionRegistryMergeStrategy,
  DiagramRepresentationConfiguration,
  footerExtensionPoint,
  navigationBarIconExtensionPoint,
  navigationBarMenuHelpURLExtensionPoint,
  NodeTypeRegistry,
  SiriusWebApplication,
} from '@eclipse-sirius/sirius-web-application';
import { forkRegistry } from '@eclipse-sirius/sirius-web-view-fork';
import FormatListBulletedIcon from '@mui/icons-material/FormatListBulleted';
import { httpOrigin, wsOrigin } from './core/URL';
import { CuboidNode } from '@eclipse-papyrus/papyrus-web-components';
import { CuboidNodeConverter } from '@eclipse-papyrus/papyrus-web-components';
import { CuboidNodeLayoutHandler } from '@eclipse-papyrus/papyrus-web-components';
import { CuboidNodeListConverter } from '@eclipse-papyrus/papyrus-web-components';
import { CuboidNodeListLayoutHandler } from '@eclipse-papyrus/papyrus-web-components';
import { CustomImageNode } from '@eclipse-papyrus/papyrus-web-components';
import { CustomImageNodeConverter } from '@eclipse-papyrus/papyrus-web-components';
import { CustomImageNodeLayoutHandler } from '@eclipse-papyrus/papyrus-web-components';
import { EllipseNode } from '@eclipse-papyrus/papyrus-web-components';
import { EllipseNodeConverter } from '@eclipse-papyrus/papyrus-web-components';
import { EllipseNodeLayoutHandler } from '@eclipse-papyrus/papyrus-web-components';
import { InnerFlagNode } from '@eclipse-papyrus/papyrus-web-components';
import { InnerFlagNodeConverter } from '@eclipse-papyrus/papyrus-web-components';
import { InnerFlagNodeLayoutHandler } from '@eclipse-papyrus/papyrus-web-components';
import { NoteNode } from '@eclipse-papyrus/papyrus-web-components';
import { NoteNodeConverter } from '@eclipse-papyrus/papyrus-web-components';
import { NoteNodeLayoutHandler } from '@eclipse-papyrus/papyrus-web-components';
import { OuterFlagNode } from '@eclipse-papyrus/papyrus-web-components';
import { OuterFlagNodeConverter } from '@eclipse-papyrus/papyrus-web-components';
import { OuterFlagNodeLayoutHandler } from '@eclipse-papyrus/papyrus-web-components';
import { PackageNode } from '@eclipse-papyrus/papyrus-web-components';
import { PackageNodeConverter } from '@eclipse-papyrus/papyrus-web-components';
import { PackageNodeLayoutHandler } from '@eclipse-papyrus/papyrus-web-components';
import { PackageNodeListConverter } from '@eclipse-papyrus/papyrus-web-components';
import { PackageNodeListLayoutHandler } from '@eclipse-papyrus/papyrus-web-components';
import { RectangleWithExternalLabelNode } from '@eclipse-papyrus/papyrus-web-components';
import { RectangleWithExternalLabelNodeConverter } from '@eclipse-papyrus/papyrus-web-components';
import { RectangleWithExternalLabelNodeLayoutHandler } from '@eclipse-papyrus/papyrus-web-components';

import {
  GQLWidget,
  PropertySectionComponent,
  widgetContributionExtensionPoint,
} from '@eclipse-sirius/sirius-components-forms';
import { treeItemContextMenuEntryExtensionPoint } from '@eclipse-sirius/sirius-components-trees';
import {
  ReferenceIcon,
  ReferencePreview,
  ReferencePropertySection,
} from '@eclipse-sirius/sirius-components-widget-reference';
import './ReactFlow.css';
import { PapyrusPopupToolContribution } from '@eclipse-papyrus/papyrus-web-components';
import './fonts.css';
import { Footer } from './footer/Footer';
import { nodesStyleDocumentTransform } from '@eclipse-papyrus/papyrus-web-components';
import './portals.css';
import { UMLModelTreeItemContextMenuContribution } from '@eclipse-papyrus/papyrus-web-components';
import { UMLElementTreeItemContextMenuContribution } from '@eclipse-papyrus/papyrus-web-components';
import './reset.css';
import './variables.css';
import { customWidgetsDocumentTransform } from '@eclipse-papyrus/papyrus-web-components';

import { Edge, Node } from '@xyflow/react';
import { ExtensionRegistryMergeStrategy } from './extensions/ExtensionRegistryMergeStrategy';
import { createRoot } from 'react-dom/client';
import { PapyrusNavigationBarIcon } from './core/PapyrusNavigationBarIcon';
import { PublishProfileTreeItemContextMenuContribution } from '@eclipse-papyrus/papyrus-web-components';
import { ContainmentReferenceIcon } from '@eclipse-papyrus/papyrus-web-components';
import { ContainmentReferencePreview } from '@eclipse-papyrus/papyrus-web-components';
import { ContainmentReferenceSection } from '@eclipse-papyrus/papyrus-web-components';
import { CustomImageIcon } from '@eclipse-papyrus/papyrus-web-components';
import { CustomImagePreview } from '@eclipse-papyrus/papyrus-web-components';
import { CustomImageSection } from '@eclipse-papyrus/papyrus-web-components';
import { LanguageExpressionIcon } from '@eclipse-papyrus/papyrus-web-components';
import { LanguageExpressionPreview } from '@eclipse-papyrus/papyrus-web-components';
import { LanguageExpressionSection } from '@eclipse-papyrus/papyrus-web-components';
import { PrimitiveListWidgetPreview } from '@eclipse-papyrus/papyrus-web-components';
import { PrimitiveListSection } from '@eclipse-papyrus/papyrus-web-components';
import { PrimitiveRadioIcon } from '@eclipse-papyrus/papyrus-web-components';
import { PrimitiveRadioPreview } from '@eclipse-papyrus/papyrus-web-components';
import { PrimitiveRadioSection } from '@eclipse-papyrus/papyrus-web-components';

if (process.env.NODE_ENV !== 'production') {
  loadDevMessages();
  loadErrorMessages();
}

const extensionRegistry: ExtensionRegistry = new ExtensionRegistry();

/*
 * Custom node contribution
 */
const nodeTypeRegistryValue: NodeTypeRegistry = {
  nodeLayoutHandlers: [
    new EllipseNodeLayoutHandler(),
    new PackageNodeLayoutHandler(),
    new PackageNodeListLayoutHandler(),
    new RectangleWithExternalLabelNodeLayoutHandler(),
    new NoteNodeLayoutHandler(),
    new InnerFlagNodeLayoutHandler(),
    new OuterFlagNodeLayoutHandler(),
    new CuboidNodeLayoutHandler(),
    new CuboidNodeListLayoutHandler(),
    new CustomImageNodeLayoutHandler(),
  ],
  nodeConverters: [
    new EllipseNodeConverter(),
    new PackageNodeConverter(),
    new PackageNodeListConverter(),
    new RectangleWithExternalLabelNodeConverter(),
    new NoteNodeConverter(),
    new InnerFlagNodeConverter(),
    new OuterFlagNodeConverter(),
    new CuboidNodeConverter(),
    new CuboidNodeListConverter(),
    new CustomImageNodeConverter(),
  ],
  nodeTypeContributions: [
    <NodeTypeContribution component={EllipseNode} type={'ellipseNode'} />,
    <NodeTypeContribution component={PackageNode} type={'packageNode'} />,
    <NodeTypeContribution component={PackageNode} type={'packageNodeList'} />,
    <NodeTypeContribution component={RectangleWithExternalLabelNode} type={'rectangleWithExternalLabelNode'} />,
    <NodeTypeContribution component={NoteNode} type={'noteNode'} />,
    <NodeTypeContribution component={InnerFlagNode} type={'innerFlagNode'} />,
    <NodeTypeContribution component={OuterFlagNode} type={'outerFlagNode'} />,
    <NodeTypeContribution component={CuboidNode} type={'cuboidNode'} />,
    <NodeTypeContribution component={CuboidNode} type={'cuboidNodeList'} />,
    <NodeTypeContribution component={CustomImageNode} type={'customImageNode'} />,
  ],
};

// Contribution to modify GraphQl requests to handle custom node
const nodeApolloClientOptionsConfigurer: ApolloClientOptionsConfigurer = (currentOptions) => {
  const { documentTransform } = currentOptions;

  const newDocumentTransform = documentTransform
    ? documentTransform.concat(nodesStyleDocumentTransform)
    : nodesStyleDocumentTransform;
  return {
    ...currentOptions,
    documentTransform: newDocumentTransform,
  };
};

/*
 * Custom widgets contribution
 */

// Contribution to modify GraphQl requests to handle custom widgets
const widgetsApolloClientOptionsConfigurer: ApolloClientOptionsConfigurer = (currentOptions) => {
  const { documentTransform } = currentOptions;

  const newDocumentTransform = documentTransform
    ? documentTransform.concat(customWidgetsDocumentTransform)
    : customWidgetsDocumentTransform;
  return {
    ...currentOptions,
    documentTransform: newDocumentTransform,
  };
};

extensionRegistry.putData(widgetContributionExtensionPoint, {
  identifier: 'papyrus-custom-widget-primitive-list',
  data: [
    {
      name: 'PrimitiveListWidget',
      icon: <FormatListBulletedIcon />,
      previewComponent: PrimitiveListWidgetPreview,
      component: (widget: GQLWidget): PropertySectionComponent<GQLWidget> | null => {
        if (widget.__typename === 'PrimitiveListWidget') {
          return PrimitiveListSection;
        }
        return null;
      },
    },
    {
      name: 'LanguageExpression',
      icon: <LanguageExpressionIcon />,
      previewComponent: LanguageExpressionPreview,
      component: (widget: GQLWidget): PropertySectionComponent<GQLWidget> | null => {
        if (widget.__typename === 'LanguageExpression') {
          return LanguageExpressionSection;
        }
        return null;
      },
    },
    {
      name: 'ContainmentReferenceWidget',
      icon: <ContainmentReferenceIcon />,
      previewComponent: ContainmentReferencePreview,
      component: (widget: GQLWidget): PropertySectionComponent<GQLWidget> | null => {
        if (widget.__typename === 'ContainmentReferenceWidget') {
          return ContainmentReferenceSection;
        }
        return null;
      },
    },
    {
      name: 'PrimitiveRadio',
      icon: <PrimitiveRadioIcon />,
      previewComponent: PrimitiveRadioPreview,
      component: (widget: GQLWidget): PropertySectionComponent<GQLWidget> | null => {
        if (widget.__typename === 'PrimitiveRadio') {
          return PrimitiveRadioSection;
        }
        return null;
      },
    },
    {
      name: 'ReferenceWidget',
      icon: <ReferenceIcon />,
      previewComponent: ReferencePreview,
      component: (widget: GQLWidget): PropertySectionComponent<GQLWidget> | null => {
        let propertySectionComponent: PropertySectionComponent<GQLWidget> | null = null;

        if (widget.__typename === 'ReferenceWidget') {
          propertySectionComponent = ReferencePropertySection;
        }
        return propertySectionComponent;
      },
    },
    {
      name: 'CustomImageWidget',
      icon: <CustomImageIcon />,
      previewComponent: CustomImagePreview,
      component: (widget: GQLWidget): PropertySectionComponent<GQLWidget> | null => {
        if (widget.__typename === 'CustomImageWidget') {
          return CustomImageSection;
        }
        return null;
      },
    },
  ],
});

// Plug both (widgets and node) graphQl document transformers
extensionRegistry.putData(apolloClientOptionsConfigurersExtensionPoint, {
  identifier: `papyrusweb_${apolloClientOptionsConfigurersExtensionPoint.identifier}`,
  data: [nodeApolloClientOptionsConfigurer, widgetsApolloClientOptionsConfigurer],
});

// Palette tools contribution
const diagramPaletteToolContributions: DiagramPaletteToolContributionProps[] = [
  {
    canHandle: (_: Node<NodeData> | Edge<EdgeData>) => true,
    component: PapyrusPopupToolContribution,
  },
];
extensionRegistry.putData<DiagramPaletteToolContributionProps[]>(diagramPaletteToolExtensionPoint, {
  identifier: 'papyrus-diagram-tools',
  data: diagramPaletteToolContributions,
});

// Tree Item context menu contributions
extensionRegistry.addComponent(treeItemContextMenuEntryExtensionPoint, {
  identifier: 'papyrus-custom-tree-menu-profile',
  Component: UMLModelTreeItemContextMenuContribution,
});
extensionRegistry.addComponent(treeItemContextMenuEntryExtensionPoint, {
  identifier: 'papyrus-custom-tree-menu-stereotype',
  Component: UMLElementTreeItemContextMenuContribution,
});
extensionRegistry.addComponent(treeItemContextMenuEntryExtensionPoint, {
  identifier: 'papyrus-custom-tree-menu-publish-profile',
  Component: PublishProfileTreeItemContextMenuContribution,
});

// Footer contribution
extensionRegistry.addComponent(footerExtensionPoint, {
  identifier: 'papyrus-footer',
  Component: Footer,
});

// Main icon contribution
extensionRegistry.addComponent(navigationBarIconExtensionPoint, {
  identifier: 'papyrusweb_navigationbar#icon',
  Component: PapyrusNavigationBarIcon,
});

// Customize help url
extensionRegistry.putData(navigationBarMenuHelpURLExtensionPoint, {
  identifier: `papyrus_web_doc_${navigationBarMenuHelpURLExtensionPoint.identifier}`,
  data: `${httpOrigin}/doc/index.html`,
});

// Table contribution
extensionRegistry.addAll(forkRegistry, new DefaultExtensionRegistryMergeStrategy());

const container = document.getElementById('root');
const root = createRoot(container!);
root.render(
  <SiriusWebApplication
    httpOrigin={httpOrigin}
    wsOrigin={wsOrigin}
    extensionRegistry={extensionRegistry}
    extensionRegistryMergeStrategy={new ExtensionRegistryMergeStrategy()}>
    <DiagramRepresentationConfiguration nodeTypeRegistry={nodeTypeRegistryValue} />
  </SiriusWebApplication>
);
