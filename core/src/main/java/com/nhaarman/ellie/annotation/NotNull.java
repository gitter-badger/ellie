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

package com.nhaarman.ellie.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * <p>
 * An annotation that indicates a member should define its SQLite column using the NOT NULL constraint. A conflict
 * clause may be defined, but there is none by default. Must be used in conjunction with
 * {@link Column}.
 * </p>
 * <p>
 * <a href="http://www.sqlite.org/lang_createtable.html#notnullconst">
 * http://www.sqlite.org/lang_createtable.html#notnullconst
 * </a>
 * <a href="http://www.sqlite.org/syntaxdiagrams.html#column-constraint">
 * http://www.sqlite.org/syntaxdiagrams.html#column-constraint
 * </a>
 * </p>
 */
@Target(FIELD)
@Retention(CLASS)
public @interface NotNull {

    /**
     * Returns a behaviour when the operation encounters a conflict.
     *
     * @return The conflict clause.
     */
    public ConflictClause value() default ConflictClause.NONE;
}