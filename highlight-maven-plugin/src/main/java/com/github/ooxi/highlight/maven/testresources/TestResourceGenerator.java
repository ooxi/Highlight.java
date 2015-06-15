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
import com.github.ooxi.highlight.maven.resources.ResourceGeneratorException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Writes test resources to project.
 * 
 * @author ooxi
 */
final class TestResourceGenerator implements ResourceMojo.Generator {

	@Override
	public void generate(File directory) {
		TestResources.getTestResources().forEach(resource -> {
			File plain = new File(directory, resource.getLanguage() +".plain-example");
			File automatically = new File(directory, resource.getLanguage() +".automatically-highlighted-example");
			File manually = new File(directory, resource.getLanguage() +".manually-highlighted-example");
			
			try (	Writer pw = new OutputStreamWriter(new FileOutputStream(plain), "UTF-8");
				Writer aw = new OutputStreamWriter(new FileOutputStream(automatically), "UTF-8");
				Writer mw = new OutputStreamWriter(new FileOutputStream(manually), "UTF-8");
			) {
				pw.write(resource.getPlainExample());
				aw.write(resource.getAutomaticallyHighlightedExample());
				mw.write(resource.getManuallyHighlightedExample());
				
			} catch (IOException e) {
				throw new ResourceGeneratorException("Cannot write test resource `"+ resource +"'", e);
			}
		});
	}
	
}
