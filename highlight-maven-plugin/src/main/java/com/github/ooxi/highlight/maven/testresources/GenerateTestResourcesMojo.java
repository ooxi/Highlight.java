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

import com.github.ooxi.highlight.maven.ResourceMojo;
import com.google.common.collect.ImmutableList;
import java.io.File;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author ooxi
 */
@Mojo(name = "generate-test-resources", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public final class GenerateTestResourcesMojo extends ResourceMojo {
	
	@Parameter(alias = "generated-test-resources-directory", defaultValue="${project.build.directory}/generated-test-resources/highlight", required = true)
	private File resourceDirectory;
	
	public GenerateTestResourcesMojo() {
		super(ImmutableList.of(
			new TestResourceGenerator()
		));
	}

	
	
	@Override
	public void execute() throws MojoExecutionException {
		Resource resource = createResource(resourceDirectory);
		
		resource.addInclude("**/*.plain-example");
		resource.addInclude("**/*.automatically-highlighted-example");
		resource.addInclude("**/*.manually-highlighted-example");

		project.addTestResource(resource);
	}
}
