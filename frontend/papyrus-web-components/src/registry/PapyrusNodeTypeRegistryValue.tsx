/*******************************************************************************
 * Copyright (c) 2026 Obeo.
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

import { CuboidNode } from '../nodes/cuboid/CuboidNode';
import { CuboidNodeConverter } from '../nodes/cuboid/CuboidNodeConverter';
import { CuboidNodeLayoutHandler } from '../nodes/cuboid/CuboidNodeLayoutHandler';
import { CuboidNodeListConverter } from '../nodes/cuboid/CuboidNodeListConverter';
import { CuboidNodeListLayoutHandler } from '../nodes/cuboid/CuboidNodeListLayoutHandler';
import { CustomImageNode } from '../nodes/customImage/CustomImageNode';
import { CustomImageNodeConverter } from '../nodes/customImage/CustomImageNodeConverter';
import { CustomImageNodeLayoutHandler } from '../nodes/customImage/CustomImageNodeLayoutHandler';
import { EllipseNode } from '../nodes/ellipse/EllipseNode';
import { EllipseNodeConverter } from '../nodes/ellipse/EllipseNodeConverter';
import { EllipseNodeLayoutHandler } from '../nodes/ellipse/EllipseNodeLayoutHandler';
import { InnerFlagNode } from '../nodes/innerFlag/InnerFlagNode';
import { InnerFlagNodeConverter } from '../nodes/innerFlag/InnerFlagNodeConverter';
import { InnerFlagNodeLayoutHandler } from '../nodes/innerFlag/InnerFlagNodeLayoutHandler';
import { NoteNode } from '../nodes/note/NoteNode';
import { NoteNodeConverter } from '../nodes/note/NoteNodeConverter';
import { NoteNodeLayoutHandler } from '../nodes/note/NoteNodeLayoutHandler';
import { OuterFlagNode } from '../nodes/outerFlag/OuterFlagNode';
import { OuterFlagNodeConverter } from '../nodes/outerFlag/OuterFlagNodeConverter';
import { OuterFlagNodeLayoutHandler } from '../nodes/outerFlag/OuterFlagNodeLayoutHandler';
import { PackageNode } from '../nodes/package/PackageNode';
import { PackageNodeConverter } from '../nodes/package/PackageNodeConverter';
import { PackageNodeLayoutHandler } from '../nodes/package/PackageNodeLayoutHandler';
import { PackageNodeListConverter } from '../nodes/package/PackageNodeListConverter';
import { PackageNodeListLayoutHandler } from '../nodes/package/PackageNodeListLayoutHandler';
import { RectangleWithExternalLabelNode } from '../nodes/rectangleWithExternalLabel/RectangleWithExternalLabelNode';
import { RectangleWithExternalLabelNodeConverter } from '../nodes/rectangleWithExternalLabel/RectangleWithExternalLabelNodeConverter';
import { RectangleWithExternalLabelNodeLayoutHandler } from '../nodes/rectangleWithExternalLabel/RectangleWithExternalLabelNodeLayoutHandler';
import { NodeTypeContribution } from '@eclipse-sirius/sirius-components-diagrams';
import { NodeTypeRegistry } from '@eclipse-sirius/sirius-web-application';
import { NodeProps } from '@xyflow/react';
/*
 * Custom node contribution
 */
export const papyrusNodeTypeRegistryValue: NodeTypeRegistry = {
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
    <NodeTypeContribution component={EllipseNode as unknown as React.FC<NodeProps>} type={'ellipseNode'} />,
    <NodeTypeContribution component={PackageNode as unknown as React.FC<NodeProps>} type={'packageNode'} />,
    <NodeTypeContribution component={PackageNode as unknown as React.FC<NodeProps>} type={'packageNodeList'} />,
    <NodeTypeContribution
      component={RectangleWithExternalLabelNode as unknown as React.FC<NodeProps>}
      type={'rectangleWithExternalLabelNode'}
    />,
    <NodeTypeContribution component={NoteNode as unknown as React.FC<NodeProps>} type={'noteNode'} />,
    <NodeTypeContribution component={InnerFlagNode as unknown as React.FC<NodeProps>} type={'innerFlagNode'} />,
    <NodeTypeContribution component={OuterFlagNode as unknown as React.FC<NodeProps>} type={'outerFlagNode'} />,
    <NodeTypeContribution component={CuboidNode as unknown as React.FC<NodeProps>} type={'cuboidNode'} />,
    <NodeTypeContribution component={CuboidNode as unknown as React.FC<NodeProps>} type={'cuboidNodeList'} />,
    <NodeTypeContribution component={CustomImageNode as unknown as React.FC<NodeProps>} type={'customImageNode'} />,
  ],
};
