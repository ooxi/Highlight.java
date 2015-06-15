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

import com.github.ooxi.highlight.maven.ResourceMojo;
import com.google.common.collect.ImmutableList;
import java.io.File;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Writes all required `highlight.js' resources into a desired package.
 * 
 * @author ooxi
 */
@Mojo(name = "generate-resources", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public final class GenerateResourcesMojo extends ResourceMojo {
	
	@Parameter(alias = "generated-resources-directory", defaultValue="${project.build.directory}/generated-resources/highlight", required = true)
	private File resourceDirectory;
	
	public GenerateResourcesMojo() {
		super(ImmutableList.of(
			new JavaScriptResourceGenerator(),
			new StylesheetResourceGenerator()
		));
	}
	
	

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Resource resource = createResource(resourceDirectory);
		
		resource.addInclude("**/*.js");
		resource.addInclude("**/*.css");

		project.addResource(resource);
	}
	
}
