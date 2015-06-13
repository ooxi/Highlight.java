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

import static com.github.ooxi.highlight.maven.JavaIdentifierSubjectFactory.JAVA_IDENTIFIER;
import static com.google.common.truth.Truth.assert_;
import java.io.IOException;
import java.util.Collection;
import static java.util.stream.Collectors.toList;
import org.junit.Test;

/**
 * @author ooxi
 */
public class HighlightResourcesTest {
	
	
	@Test
	public void testJavaScriptResources() throws IOException {
		Collection<HighlightResource> resources = HighlightResources.getJavaScriptResources();
		
		assert_().withFailureMessage("Empty `highlight.js' JavaScript resources")
			.that(resources).isNotEmpty();
	}
	
	
	@Test
	public void testLanguageResources() throws IOException {
		Collection<HighlightResource> resources = HighlightResources.getLanguageResources();
		
		Collection<String> identifiers = resources.stream()
			.map(HighlightResource::getJavaIdentifier)
		.collect(toList());
		
		
		assert_().withFailureMessage("Empty `highlight.js' language resources")
			.that(resources).isNotEmpty();
		assert_().in(identifiers).thatEach(JAVA_IDENTIFIER).isJavaIdentifier();
	}
	
	
	@Test
	public void testStylesheetResources() throws IOException {
		Collection<HighlightResource> resources = HighlightResources.getStylesheetResources();
		
		Collection<String> identifiers = resources.stream()
			.map(HighlightResource::getJavaIdentifier)
		.collect(toList());
		
		assert_().withFailureMessage("Empty `highlight.js' Stylesheet resources")
			.that(resources).isNotEmpty();
		assert_().in(identifiers).thatEach(JAVA_IDENTIFIER).isJavaIdentifier();
	}
}
