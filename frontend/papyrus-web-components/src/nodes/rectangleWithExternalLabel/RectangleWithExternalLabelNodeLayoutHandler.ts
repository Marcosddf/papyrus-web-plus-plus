/*****************************************************************************
 * Copyright (c) 2023, 2025 CEA LIST, Obeo.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Obeo - Initial API and implementation
 *****************************************************************************/
import {
  Diagram,
  DiagramNodeType,
  getBorderNodeExtent,
  ILayoutEngine,
  INodeLayoutHandler,
  NodeData,
  setBorderNodesPosition,
  ForcedDimensions,
  defaultWidth,
  defaultHeight,
} from '@eclipse-sirius/sirius-components-diagrams';
import { Node } from '@xyflow/react';

export class RectangleWithExternalLabelNodeLayoutHandler implements INodeLayoutHandler<NodeData> {
  canHandle(node: Node<NodeData, DiagramNodeType>) {
    return node.type === 'rectangleWithExternalLabelNode';
  }

  handle(
    layoutEngine: ILayoutEngine,
    previousDiagram: Diagram | null,
    node: Node<NodeData>,
    visibleNodes: Node<NodeData, DiagramNodeType>[],
    directChildren: Node<NodeData, DiagramNodeType>[],
    newlyAddedNodes: Node<NodeData, DiagramNodeType>[],
    _forceWidth?: ForcedDimensions
  ) {
    layoutEngine.layoutNodes(previousDiagram, visibleNodes, directChildren, newlyAddedNodes);

    const borderNodes = directChildren.filter((node) => node.data.isBorderNode);

    node.width = node.data.defaultWidth ?? defaultWidth;
    node.height = node.data.defaultHeight ?? defaultHeight;

    // Update border nodes positions
    borderNodes.forEach((borderNode) => {
      borderNode.extent = getBorderNodeExtent(node, borderNode);
    });
    setBorderNodesPosition(borderNodes, node, previousDiagram);
  }
}
