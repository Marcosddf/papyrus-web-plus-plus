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
package org.eclipse.papyrus.web.custom.widgets;

import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.ecore.util.Switch;
import org.eclipse.sirius.components.core.api.IFeedbackMessageService;
import org.eclipse.sirius.components.core.api.IObjectService;
import org.eclipse.sirius.components.emf.services.api.IEMFKindService;
import org.eclipse.sirius.components.forms.description.AbstractWidgetDescription;
import org.eclipse.sirius.components.interpreter.AQLInterpreter;
import org.eclipse.sirius.components.view.emf.form.IFormIdProvider;
import org.eclipse.sirius.components.view.emf.form.IWidgetConverterProvider;
import org.eclipse.sirius.components.view.emf.operations.api.IOperationExecutor;
import org.springframework.stereotype.Service;

/**
 * Provides the widget converter needed for the language expression widget.
 *
 * @author Jerome Gout
 */
@Service
public class PapyrusWidgetsConverterProvider implements IWidgetConverterProvider {

    private final IObjectService objectService;

    private final IOperationExecutor operationExecutor;

    private final IFeedbackMessageService feedbackMessageService;

    private final IFormIdProvider formIdProvider;

    private final IEMFKindService emfKindService;


    public PapyrusWidgetsConverterProvider(IObjectService objectService, IOperationExecutor operationExecutor, IFeedbackMessageService feedbackMessageService, IFormIdProvider formIdProvider, IEMFKindService emfKindService) {
        this.objectService = Objects.requireNonNull(objectService);
        this.operationExecutor = Objects.requireNonNull(operationExecutor);
        this.feedbackMessageService =  Objects.requireNonNull(feedbackMessageService);
        this.formIdProvider = Objects.requireNonNull(formIdProvider);
        this.emfKindService = Objects.requireNonNull(emfKindService);
    }

    @Override
    public Switch<Optional<AbstractWidgetDescription>> getWidgetConverter(AQLInterpreter interpreter) {
        return new PapyrusWidgetsConverterSwitch(interpreter, this.objectService, this.operationExecutor, this.feedbackMessageService,  this.formIdProvider, this.emfKindService);
    }

}
