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

package com.nhaarman.ellie;

import android.content.ContentProvider;

/**
 * <p>
 * Default implementation of a <a href="https://developer.android.com/reference/android/content/ContentProvider.html">
 * ContentProvider</a>. As with any content provider, this class must be
 * <a href="https://developer.android.com/guide/topics/manifest/provider-element.html">declared in the manifest.</a>.
 * When using this content provider, manual initialization is not required as the content provider does this for you.
 * This is not a feature of the provider, but rather a requirement due to the behavior of content providers.
 * </p>
 * Content Uris are built in a restful manner using your package name and tables names. For example:
 * <ul>
 * <li>content://com.example.notes/notes</li>
 * <li>content://com.example.notes/notes/1</li>
 * </ul>
 */
public abstract class EllieProvider extends ContentProvider {
//	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
//	private static final SparseArray<Class<? extends Model>> TYPE_CODES = new SparseArray<Class<? extends Model>>();
//
//	private static boolean sIsImplemented = false;
//	private static String sAuthority;
//	private static SparseArray<String> sMimeTypeCache = new SparseArray<String>();
//
//	/**
//	 * Returns whether the provider has been implemented.
//	 *
//	 * @return Whether the provider has been implemented.
//	 */
//	public static boolean isImplemented() {
//		return sIsImplemented;
//	}
//
//	/**
//	 * Create a Uri for a model table.
//	 *
//	 * @param type The model type.
//	 * @return The Uri for the model table.
//	 */
//	public static Uri createUri(Class<? extends Model> type) {
//		return createUri(type, null);
//	}
//
//	/**
//	 * Create a Uri for a model row.
//	 *
//	 * @param type The model type.
//	 * @param id   The row Id.
//	 * @return The Uri for the model row.
//	 */
//	public static Uri createUri(Class<? extends Model> type, Long id) {
//		final StringBuilder uri = new StringBuilder();
//		uri.append("content://");
//		uri.append(sAuthority);
//		uri.append("/");
//		uri.append(Ellie.getTableName(type).toLowerCase());
//
//		if (id != null) {
//			uri.append("/");
//			uri.append(id.toString());
//		}
//
//		return Uri.parse(uri.toString());
//	}
//
//	@Override
//	public boolean onCreate() {
//		Ellie.init(getContext(), getDatabaseName(), getDatabaseVersion(), getCacheSize(), getLogLevel());
//		sAuthority = getAuthority();
//		sIsImplemented = true;
//
//		int i = 0;
//		for (ModelAdapter modelAdapter : Ellie.getModelAdapters()) {
//			final int tableKey = (i * 2) + 1;
//			final int itemKey = (i * 2) + 2;
//
//			// content://<authority>/<table>
//			URI_MATCHER.addURI(sAuthority, modelAdapter.getTableName().toLowerCase(), tableKey);
//			TYPE_CODES.put(tableKey, modelAdapter.getModelType());
//
//			// content://<authority>/<table>/<id>
//			URI_MATCHER.addURI(sAuthority, modelAdapter.getTableName().toLowerCase() + "/#", itemKey);
//			TYPE_CODES.put(itemKey, modelAdapter.getModelType());
//
//			i++;
//		}
//
//		return true;
//	}
//
//	@Override
//	public String getType(Uri uri) {
//		final int match = URI_MATCHER.match(uri);
//
//		String cachedMimeType = sMimeTypeCache.get(match);
//		if (cachedMimeType != null) {
//			return cachedMimeType;
//		}
//
//		final Class<? extends Model> type = getModelType(uri);
//		final boolean single = ((match % 2) == 0);
//
//		StringBuilder mimeType = new StringBuilder();
//		mimeType.append("vnd");
//		mimeType.append(".");
//		mimeType.append(sAuthority);
//		mimeType.append(".");
//		mimeType.append(single ? "item" : "dir");
//		mimeType.append("/");
//		mimeType.append("vnd");
//		mimeType.append(".");
//		mimeType.append(sAuthority);
//		mimeType.append(".");
//		mimeType.append(Ellie.getTableName(type));
//
//		sMimeTypeCache.append(match, mimeType.toString());
//
//		return mimeType.toString();
//	}
//
//	@Override
//	public Uri insert(Uri uri, ContentValues values) {
//		final Class<? extends Model> type = getModelType(uri);
//		final Long id = Ellie.getDatabase().insert(Ellie.getTableName(type), null, values);
//
//		if (id != null && id > 0) {
//			Uri retUri = createUri(type, id);
//			getContext().getContentResolver().notifyChange(uri, null);
//			return retUri;
//		}
//
//		return null;
//	}
//
//	@Override
//	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//		final int count = Ellie.getDatabase().update(
//				Ellie.getTableName(getModelType(uri)),
//				values,
//				selection,
//				selectionArgs);
//
//		getContext().getContentResolver().notifyChange(uri, null);
//
//		return count;
//	}
//
//	@Override
//	public int delete(Uri uri, String selection, String[] selectionArgs) {
//		final int count = Ellie.getDatabase().delete(
//				Ellie.getTableName(getModelType(uri)),
//				selection,
//				selectionArgs);
//
//		getContext().getContentResolver().notifyChange(uri, null);
//
//		return count;
//	}
//
//	@Override
//	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//		final Cursor cursor = Ellie.getDatabase().query(
//				Ellie.getTableName(getModelType(uri)),
//				projection,
//				selection,
//				selectionArgs,
//				null,
//				null,
//				sortOrder);
//
//		cursor.setNotificationUri(getContext().getContentResolver(), uri);
//
//		return cursor;
//	}
//
//	/**
//	 * Returns the database name.
//	 *
//	 * @return The database name.
//	 */
//	protected abstract String getDatabaseName();
//
//	/**
//	 * Returns the database version.
//	 *
//	 * @return The database version.
//	 */
//	protected abstract int getDatabaseVersion();
//
//	/**
//	 * Returns the package name as the default Uri authority. Override to provide your own Uri authority.
//	 *
//	 * @return The Uri authority.
//	 */
//	protected String getAuthority() {
//		return getContext().getPackageName();
//	}
//
//	/**
//	 * Returns the default cache size. Override to provide your own cache size.
//	 *
//	 * @return The cache size.
//	 */
//	protected int getCacheSize() {
//		return Ellie.DEFAULT_CACHE_SIZE;
//	}
//
//	/**
//	 * Returns the default log level of NONE. Override to provide your own log level.
//	 *
//	 * @return The log level.
//	 */
//	protected LogLevel getLogLevel() {
//		return LogLevel.NONE;
//	}
//
//	private Class<? extends Model> getModelType(Uri uri) {
//		final int code = URI_MATCHER.match(uri);
//		if (code != UriMatcher.NO_MATCH) {
//			return TYPE_CODES.get(code);
//		}
//		return null;
//	}
}
