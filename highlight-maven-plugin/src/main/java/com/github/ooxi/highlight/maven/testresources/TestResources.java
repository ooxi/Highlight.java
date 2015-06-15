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
package com.github.ooxi.highlight.maven.testresources;

import com.github.ooxi.highlight.maven.resources.ResourceGeneratorException;
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
import javax.json.JsonValue;

/**
 * @author ooxi
 */
public final class TestResources {
	
	private static final URL hljsTests = TestResources.class.getResource("/hljs-tests.json");
	
	
	public static ImmutableSet<TestResource> getTestResources() {
		checkNotNull(hljsTests, "Cannot find test resource list");
		ImmutableSet.Builder<TestResource> resources = ImmutableSet.<TestResource>builder();
		
		try (	Reader reader = new InputStreamReader(hljsTests.openStream(), "UTF-8");
			JsonReader json = Json.createReader(reader);
		) {
			json.readArray().forEach(element -> {
				checkState(element.getValueType().equals(JsonValue.ValueType.OBJECT), "Type of test resource `"+ element +"' should be `"+ JsonValue.ValueType.OBJECT +"' but is `"+ element.getValueType() +"'");
				checkState(element instanceof JsonObject);
				
				JsonObject container = (JsonObject)element;
				JsonString language = container.getJsonString("language");
				JsonString plainExample = container.getJsonString("plain_example");
				JsonString automaticallyHighlightedExample = container.getJsonString("automatically_highlighted_example");
				JsonString manuallyHighlightedExample = container.getJsonString("manually_highlighted_example");
				
				resources.add(TestResource.of(
					language.getString(),
					plainExample.getString(),
					automaticallyHighlightedExample.getString(),
					manuallyHighlightedExample.getString()
				));
			});
		
		} catch (IOException e) {
			throw new ResourceGeneratorException("Cannot load test resource from `"+ hljsTests +"'", e);
		}
		
		return resources.build();
	}
	
	
	
	
	
	private TestResources() {
	}
}
