/*
 * Copyright (C) 2014 Michael Pardo
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

package com.nhaarman.ellie.internal.codegen.step;

import com.google.common.collect.Sets;
import com.nhaarman.ellie.internal.codegen.Registry;
import com.nhaarman.ellie.internal.codegen.validator.Validator;

import com.nhaarman.ellie.adapter.BooleanAdapter;
import com.nhaarman.ellie.adapter.CalendarAdapter;
import com.nhaarman.ellie.adapter.SqlDateAdapter;
import com.nhaarman.ellie.adapter.UtilDateAdapter;
import com.nhaarman.ellie.internal.TypeAdapter;

import com.nhaarman.ellie.internal.codegen.element.TypeAdapterElement;
import com.nhaarman.ellie.internal.codegen.validator.TypeAdapterValidator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public class TypeAdapterStep implements ProcessingStep {
	private static final Class[] DEFAULT_TYPE_ADAPTERS = new Class[]{
			BooleanAdapter.class,
			CalendarAdapter.class,
			SqlDateAdapter.class,
			UtilDateAdapter.class
	};

	private Registry registry;
	private Validator validator;

	public TypeAdapterStep(Registry registry) {
		this.registry = registry;
		this.validator = new TypeAdapterValidator(registry);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		final Set<Element> elements = Sets.newHashSet(roundEnv.getElementsAnnotatedWith(TypeAdapter.class));
		for (Class cls : DEFAULT_TYPE_ADAPTERS) {
			elements.add(registry.getElements().getTypeElement(cls.getName()));
		}

		for (Element element : elements) {
			if (validator.validate(element.getEnclosingElement(), element)) {
				registry.addTypeAdapterModel(new TypeAdapterElement(
						registry.getTypes(),
						registry.getElements(),
						(TypeElement) element));
			}
		}

		return false;
	}
}
