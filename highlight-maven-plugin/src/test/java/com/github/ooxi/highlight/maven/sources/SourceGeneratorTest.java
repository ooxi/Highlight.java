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
package com.github.ooxi.highlight.maven.sources;

import static com.google.common.truth.Truth.assert_;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import java.io.IOException;
import org.junit.Test;

/**
 * @author ooxi
 */
public class SourceGeneratorTest {
	
	
	@Test
	public void testLanguagesGenerator() throws IOException, JClassAlreadyExistsException {
		JCodeModel model = new JCodeModel();
		new LanguagesSourceGenerator(model, "com.example").generate();
		
		JDefinedClass interfaceModel = model._getClass("com.example.Language");
		JDefinedClass implementationModel = model._getClass("com.example.LanguageImpl");
		JDefinedClass enumerationModel = model._getClass("com.example.Languages");
		
		assert_().withFailureMessage("Language interface missing")
			.that(interfaceModel).isNotNull();
		assert_().withFailureMessage("Language implementation missing")
			.that(implementationModel).isNotNull();
		assert_().withFailureMessage("Language enumeration missing")
			.that(enumerationModel).isNotNull();
	}
	
	
	
	@Test
	public void testStylesheetsGenerator() throws IOException, JClassAlreadyExistsException {
		JCodeModel model = new JCodeModel();
		new StylesheetsSourceGenerator(model, "com.example").generate();
		
		JDefinedClass interfaceModel = model._getClass("com.example.Stylesheet");
		JDefinedClass implementationModel = model._getClass("com.example.StylesheetImpl");
		JDefinedClass enumerationModel = model._getClass("com.example.Stylesheets");
		
		assert_().withFailureMessage("Stylesheet interface missing")
			.that(interfaceModel).isNotNull();
		assert_().withFailureMessage("Stylesheet implementation missing")
			.that(implementationModel).isNotNull();
		assert_().withFailureMessage("Stylesheet enumeration missing")
			.that(enumerationModel).isNotNull();
	}
}
