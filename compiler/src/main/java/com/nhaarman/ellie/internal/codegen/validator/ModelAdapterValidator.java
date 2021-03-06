/*
 * Copyright (C) 2014 Michael Pardo
 * Copyright (C) 2014 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nhaarman.ellie.internal.codegen.validator;

import com.nhaarman.ellie.internal.codegen.Registry;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.tools.Diagnostic.Kind.ERROR;

public class ModelAdapterValidator implements Validator {
	private Messager messager;

	public ModelAdapterValidator(Registry registry) {
		this.messager = registry.getMessager();
	}

	@Override
	public boolean validate(Element enclosingElement, Element element) {
		if (!element.getKind().equals(CLASS)) {
			messager.printMessage(ERROR, "@Table applies only to Model classes.", element);
			return false;
		}

		return true;
	}
}
