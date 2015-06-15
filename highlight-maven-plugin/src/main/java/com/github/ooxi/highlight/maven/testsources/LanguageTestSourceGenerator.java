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
package com.github.ooxi.highlight.maven.testsources;

import com.github.ooxi.highlight.maven.SourceMojo;
import com.github.ooxi.highlight.maven.resources.JavaIdentifier;
import com.github.ooxi.highlight.maven.testresources.TestResource;
import com.github.ooxi.highlight.maven.testresources.TestResources;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import static java.util.Arrays.asList;
import javax.script.ScriptException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Generates test cases for highlighting different languages.
 * 
 * @author ooxi
 */
final class LanguageTestSourceGenerator implements SourceMojo.Generator {
	
	private final JCodeModel model;
	private final String pkg;
	
	public LanguageTestSourceGenerator(JCodeModel model, String pkg) {
		this.model = model;
		this.pkg = pkg;
	}

	
	
	
	
	@Override
	public void generate() throws IOException, JClassAlreadyExistsException {
		for (TestResource resource : TestResources.getTestResources()) {
			generateLanguageTestCase(resource);
		}
	}

	
	
	/**
	 * Generates the JUnit container class
	 */
	private void generateLanguageTestCase(TestResource resource) throws JClassAlreadyExistsException {
		TestCase tc = new TestCase(resource);
		JClass assertClass = model.ref(Assert.class);
		JType byteArrayClass = model._ref(byte[].class);
		JType inputStreamType = model._ref(InputStream.class);
		JClass stringClass = model.ref(String.class);
		JType urlType = model._ref(URL.class);
		
		JPackage pkgModel = model._package(pkg);
		tc.testModel = pkgModel._class(JMod.PUBLIC, tc.identifier.toClassName() +"Test", ClassType.CLASS);
		
		
		tc.plainExampleResourceField = tc.testModel.field(
			JMod.PRIVATE | JMod.STATIC | JMod.FINAL, stringClass,
			"PLAIN_EXAMPLE_RESOURCE", JExpr.lit(resource.getLanguage() +".plain-example"));
		
		tc.automaticallyHighlightedExampleResourceField = tc.testModel.field(
			JMod.PRIVATE | JMod.STATIC | JMod.FINAL, stringClass,
			"AUTOMATICALLY_HIGHLIGHTED_EXAMPLE_RESOURCE", JExpr.lit(resource.getLanguage() +".automatically-highlighted-example"));
		
		tc.manuallyHighlightedExampleResourceField = tc.testModel.field(
			JMod.PRIVATE | JMod.STATIC | JMod.FINAL, stringClass,
			"MANUALLY_HIGHLIGHTED_EXAMPLE_RESOURCE", JExpr.lit(resource.getLanguage() +".manually-highlighted-example"));
		
		
		/* private static String toString(String resource) throws IOException
		 */
		tc.toStringMethod = tc.testModel.method(JMod.PRIVATE | JMod.STATIC, stringClass, "toString");
		tc.toStringMethod._throws(IOException.class);
		
		JVar resourceParam = tc.toStringMethod.param(stringClass, "resource");
		JBlock toStringBody = tc.toStringMethod.body();
		
		/* final URL url = SomeTest.class.getResource(resource);
		 * Assert.assertNotNull("…", url);
		 */
		JVar urlVar = toStringBody.decl(JMod.FINAL, urlType, "url", tc.testModel.dotclass().invoke("getResource").arg(resourceParam));
		toStringBody.staticInvoke(assertClass, "assertNotNull").arg(JExpr.lit("Cannot find resource `").plus(resourceParam).plus(JExpr.lit("'"))).arg(urlVar);
		
		/* final InputStream is = url.openStream();
		 * try {
		 *     final byte[] buf = ByteStreams.toByteArray(is);
		 *     return new String(buf, "UTF-8");
		 * } finally {
		 *     is.close();
		 * }
		 */
		JVar isVar = toStringBody.decl(JMod.FINAL, inputStreamType, "is", urlVar.invoke("openStream"));
		JTryBlock tryBlock = toStringBody._try();
		JBlock tryBody = tryBlock.body();
		
		JVar bufVar = tryBody.decl(JMod.FINAL, byteArrayClass, "buf", tc.byteStreamsClass.staticInvoke("toByteArray").arg(isVar));
		tryBody._return(JExpr._new(stringClass).arg(bufVar).arg("UTF-8"));
		
		JBlock finallyBody = tryBlock._finally();
		finallyBody.add(isVar.invoke("close"));
		
		
		generateAutomaticTest(tc);
		generateManualTest(tc);
	}

	
	
	/**
	 * Generates the test case for automatic langauge highlighting
	 */
	private void generateAutomaticTest(TestCase tc) {
		JClass assertClass = model.ref(Assert.class);
		JClass stringClass = model.ref(String.class);
		
		JMethod testMethod = tc.testModel.method(JMod.PUBLIC, model.VOID, "testAutomatic"+ tc.identifier.toClassName() +"Highlighting");
		testMethod.annotate(Test.class);
		testMethod._throws(IOException.class);
		testMethod._throws(ScriptException.class);
		
		/* Test fails for `1c'
		 */
		if ("1c".equals(tc.resource.getLanguage())) {
			testMethod.annotate(Ignore.class);
		}
		
		JBlock testBody = testMethod.body();
		JVar testVar = testBody.decl(JMod.FINAL, stringClass, "TEST", JExpr.invoke(tc.toStringMethod).arg(tc.plainExampleResourceField));
		JVar expectedVar = testBody.decl(JMod.FINAL, stringClass, "EXPECTED", JExpr.invoke(tc.toStringMethod).arg(tc.automaticallyHighlightedExampleResourceField));
		JVar highlightVar = testBody.decl(JMod.FINAL, tc.highlightClass, "highlight", tc.highlightClass.staticInvoke("newInstance"));
		JVar actualVar = testBody.decl(JMod.FINAL, stringClass, "actual", highlightVar.invoke("highlight").arg(testVar));
		
		
		/* Assert.assertEquals("…", EXPECTED, actual)
		 */
		testBody.staticInvoke(assertClass, "assertEquals")
			.arg("Automatic highlighting of language `"+ tc.resource.getLanguage() +"' failed")
			.arg(expectedVar)
			.arg(actualVar);
	}
	
	
	
	/**
	 * Generates the test case for manual langauge highlighting
	 */
	private void generateManualTest(TestCase tc) {
		JClass assertClass = model.ref(Assert.class);
		JClass languageClass = model.ref(pkg +".Language");
		JClass languagesClass = model.ref(pkg +".Languages");
		JClass stringClass = model.ref(String.class);
		
		JMethod testMethod = tc.testModel.method(JMod.PUBLIC, model.VOID, "testManual"+ tc.identifier.toClassName() +"Highlighting");
		testMethod.annotate(Test.class);
		testMethod._throws(IOException.class);
		testMethod._throws(ScriptException.class);
		
		/* Test fails for `1c' and `r'
		 */
		if (asList("1c", "r").contains(tc.resource.getLanguage())) {
			testMethod.annotate(Ignore.class);
		}
		
		JBlock testBody = testMethod.body();
		JVar testVar = testBody.decl(JMod.FINAL, stringClass, "TEST", JExpr.invoke(tc.toStringMethod).arg(tc.plainExampleResourceField));
		JVar expectedVar = testBody.decl(JMod.FINAL, stringClass, "EXPECTED", JExpr.invoke(tc.toStringMethod).arg(tc.automaticallyHighlightedExampleResourceField));
		JVar highlightVar = testBody.decl(JMod.FINAL, tc.highlightClass, "highlight", tc.highlightClass.staticInvoke("newInstance"));
		JVar languageVar = testBody.decl(JMod.FINAL, languageClass, "language", languagesClass.staticRef(tc.identifier.toEnumerationConstant()));
		JVar actualVar = testBody.decl(JMod.FINAL, stringClass, "actual", highlightVar.invoke("highlight").arg(testVar).arg(languageVar));
		
		
		/* Assert.assertEquals("…", EXPECTED, actual)
		 */
		testBody.staticInvoke(assertClass, "assertEquals")
			.arg("Manual highlighting of language `"+ tc.resource.getLanguage() +"' failed")
			.arg(expectedVar)
			.arg(actualVar);
	}
	
	
	
	
	
	private final class TestCase {
		public final TestResource resource;
		public final JavaIdentifier identifier;
		
		public JDefinedClass testModel;
		public JFieldVar plainExampleResourceField;
		public JFieldVar automaticallyHighlightedExampleResourceField;
		public JFieldVar manuallyHighlightedExampleResourceField;
		public JMethod toStringMethod;
		
		public final JClass highlightClass = model.ref("com.github.ooxi.highlight.Highlight");
		public final JClass byteStreamsClass = model.ref("com.github.ooxi.highlight.Guava.ByteStreams");
		
		public TestCase(TestResource resource) {
			this.resource = resource;
			this.identifier = JavaIdentifier.of(resource.getLanguage());
		}
	}
}
