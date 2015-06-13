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

import com.google.auto.value.AutoValue;
import java.net.URL;
import org.apache.commons.io.FilenameUtils;

/**
 * @author ooix
 */
@AutoValue
public abstract class HighlightResource {
	
	
	public static HighlightResource of(URL url, String path) {
		return new AutoValue_HighlightResource(url, path);
	}
	
	
	public abstract URL getUrl();
	public abstract String getPath();
	
	public final String getBasename() {
		return FilenameUtils.getBaseName(getUrl().toString());
	}
	
	public final String getJavaIdentifier() {
		
		/* @see HighlightResourcesTest
		 */
		final String identifier = getBasename()
			.toUpperCase()
			.replace('-', '_')
			.replace('.', '_');
		
		if ("1C".equals(identifier)) {
			return "ONE_C";
		}
		
		return identifier;
	}
	
	
	HighlightResource() {
	}
}
