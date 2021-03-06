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

package com.nhaarman.ellie.internal;

import com.nhaarman.ellie.Model;
import com.nhaarman.ellie.ModelRepository;

import java.util.List;

/**
 * Used internally to create and store ModelRepository instances.
 */
public interface RepositoryHolder {

    String IMPL_CLASS_PACKAGE = "com.nhaarman.ellie";
    String IMPL_CLASS_NAME = "RepositoryHolderImpl";
    String IMPL_CLASS_FQCN = IMPL_CLASS_PACKAGE + "." + IMPL_CLASS_NAME;

    <T extends Model> ModelRepository<T> getModelRepository(Class<? extends Model> cls);

    List<? extends ModelRepository> getModelRepositories();

}
