/**
 * Copyright (c) 2015 ooxi. All rights reserved.
 *     https://github.com/ooxi/Highlight.java
 *     violetland@mail.ru
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software in a
 *     product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 * 
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 * 
 *  3. This notice may not be removed or altered from any source distribution.
 */
package com.github.ooxi.highlight.maven;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue.ValueType;

/**
 * Provides access to `highlight.js' resources.
 * 
 * @author ooxi
 */
public final class HighlightResources {
	
	private static final URL hljsJavaScript = HighlightResources.class.getResource("/hljs-javascript.json");
	private static final URL hljsLanguage = HighlightResources.class.getResource("/hljs-language.json");
	private static final URL hljsStylesheet = HighlightResources.class.getResource("/hljs-stylesheet.json");
	
	
	
	
	public static ImmutableSet<HighlightResource> getJavaScriptResources() throws IOException {
		checkNotNull(hljsJavaScript, "Cannot find JavaScript resource list");
		return getResources(hljsJavaScript);
	}
	
	
	public static ImmutableSet<HighlightResource> getLanguageResources() throws IOException {
		checkNotNull(hljsLanguage, "Cannot find language resource list");
		return getResources(hljsLanguage);
	}
	
	
	public static ImmutableSet<HighlightResource> getStylesheetResources() throws IOException {
		checkNotNull(hljsStylesheet, "Cannot find Stylesheet resource list");
		return getResources(hljsStylesheet);
	}
	
	
	
	
	
	/**
	 * @param resourceJson Contains a JSON array of resource paths
	 * @return URLs of resources described by `resourceJson'
	 * 
	 * @throws IOException If reading the resource json went wrong
	 */
	private static ImmutableSet<HighlightResource> getResources(URL resourceJson) throws IOException {
		ImmutableSet.Builder<HighlightResource> resources = ImmutableSet.<HighlightResource>builder();
		
		try (	Reader reader = new InputStreamReader(resourceJson.openStream(), "UTF-8");
			JsonReader json = Json.createReader(reader);
		) {
			json.readArray().forEach(element -> {
				checkState(element.getValueType().equals(ValueType.OBJECT), "Type of path `"+ element +"' should be `"+ ValueType.OBJECT +"' but is `"+ element.getValueType() +"'");
				checkState(element instanceof JsonObject);
				
				JsonObject container = (JsonObject)element;
				JsonString absolute = container.getJsonString("absolute");
				JsonString relative = container.getJsonString("relative");
				
				final URL url = HighlightResources.class.getResource("/"+ absolute.getString());
				final String path = relative.getString();
				
				resources.add(HighlightResource.of(url, path));
			});
		}
		
		return resources.build();
	}

}
