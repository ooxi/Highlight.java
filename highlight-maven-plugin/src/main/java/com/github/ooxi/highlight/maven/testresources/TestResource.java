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

import com.google.auto.value.AutoValue;

/**
 * Java interface for data generated by `download-test-resources.js'.
 * 
 * @author ooxi
 */
@AutoValue
public abstract class TestResource {
	
	
	public static TestResource of(
				String language,
				String plainExample,
				String automaticallyHighlightedExample,
				String manuallyHighlightedExample
			) {
		
		return new AutoValue_TestResource(
			language,
			plainExample,
			automaticallyHighlightedExample,
			manuallyHighlightedExample
		);
	}
	
	
	public abstract String getLanguage();
	public abstract String getPlainExample();
	public abstract String getAutomaticallyHighlightedExample();
	public abstract String getManuallyHighlightedExample();
	
	
	TestResource() {
	}
}
