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
package com.github.ooxi.highlight.maven.resources;

import static com.github.ooxi.highlight.maven.FileSubjectFactory.FILES;
import com.google.common.io.Files;
import static com.google.common.truth.Truth.assert_;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;
import org.junit.Test;

/**
 * @author ooxi
 */
public class ResourceGeneratorTest {
	
	
	@Test
	public void testResourceGenerator() throws IOException {
		
		/* Prepare resources and resource generator
		 */
		final HighlightResource first = HighlightResource.of(
			ResourceGeneratorTest.class.getResource("first-resource")::openStream,
			"directory/first"
		);
		final HighlightResource second = HighlightResource.of(
			ResourceGeneratorTest.class.getResource("second-resource")::openStream,
			"second"
		);
		
		final ResourceGenerator generator = new ResourceGenerator() {

			@Override
			protected String getBasePath() {
				return "com/github/ooxi/highlight/maven/resource/";
			}

			@Override
			protected Stream<HighlightResource> getResources() {
				return Stream.of(first, second);
			}
			
		};
		
		
		/* Write resources to temporary directory
		 */
		File directory = Files.createTempDir();
		generator.generate(directory);
		
		
		/* Test existence and content of generated resource files
		 */
		assert_().about(FILES).that(new File(directory, "directory/first")).hasContent("AB", "UTF-8");
		assert_().about(FILES).that(new File(directory, "second")).hasContent("CD", "UTF-8");
	}
}
