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

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;

/**
 * @author ooxi
 */
public class JavaIdentifierSubject extends Subject<JavaIdentifierSubject, String> {

	public JavaIdentifierSubject(FailureStrategy fs, String subject) {
		super(fs, subject);
	}
	
	
	
	public void isJavaIdentifier() {
		isNotNull();
		final CharSequence identifier = getSubject();
		
		if (0 == identifier.length()) {
			fail("is not empty");
		}
		
		
		if (!Character.isJavaIdentifierStart(identifier.charAt(0))) {
			fail("does start with a java identifier");
		}
		for (int i = 1; i < identifier.length(); ++i) {
			if (!Character.isJavaIdentifierPart(identifier.charAt(i))) {
				fail("is a java identifier at character "+ i);
			}
		}
	}
}
