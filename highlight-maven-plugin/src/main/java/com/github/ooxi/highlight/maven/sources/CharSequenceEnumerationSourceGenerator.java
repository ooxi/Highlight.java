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

import com.github.ooxi.highlight.maven.SourceMojo;
import com.github.ooxi.highlight.maven.resources.HighlightResource;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCase;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JSwitch;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import java.io.IOException;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * Generates an enumeration vor HighlightResources.
 * 
 * @author ooxi
 */
abstract class CharSequenceEnumerationSourceGenerator implements SourceMojo.Generator {
	
	private final JCodeModel model;
	private final String pkg;
	
	protected CharSequenceEnumerationSourceGenerator(JCodeModel model, String pkg) {
		this.model = model;
		this.pkg = pkg;
	}
	
	
	
	protected abstract String getSimpleName();
	protected abstract Collection<HighlightResource> getResources();

	
		
	
	
	private JDefinedClass generateInterface() throws JClassAlreadyExistsException {
		JPackage pkgModel = model._package(pkg);
		JDefinedClass interfaceModel = pkgModel._class(JMod.PUBLIC, getSimpleName(), ClassType.INTERFACE);
		
		/* Extends CharSequence
		 */
		interfaceModel._extends(CharSequence.class);
		
		return interfaceModel;
	}
	
	
	
	private JMethod generateImplementation(
				JDefinedClass interfaceModel
			) throws JClassAlreadyExistsException {
		
		JPackage pkgModel = model._package(pkg);
		JDefinedClass implementationModel = pkgModel._class(JMod.FINAL, getSimpleName() +"Impl", ClassType.CLASS);
		
		
		/* Implements interface
		 */
		implementationModel._implements(interfaceModel);
		generateCharSequenceMethods(implementationModel, JMod.PUBLIC);
		
		
		/* Add factory method to interface
		 */
		JMethod fromNameMethod = interfaceModel.method(JMod.PUBLIC | JMod.STATIC, interfaceModel, "fromName");
		JVar nameParam = fromNameMethod.param(String.class, "name");
		fromNameMethod.body()._return(JExpr._new(implementationModel).arg(nameParam));
		
		
		/* Return access to factory method
		 */
		return fromNameMethod;
	}
	
	
	
	/**
	 * Java 5 enumerations cannot be used since `Enum.equals' as well as
	 * `Enum.hashCode' are declared final and thus cannot be overriden to
	 * conform with `CharSequence.equals' and `CharSequence.hashCode'.
	 * 
	 * @param pkgName Fully quallified package name in which the enumeration
	 *     class should be generated
	 * @param interfaceClass Type of enumeration entities
	 * @param factory Factory method which can be used to create enumeration
	 *     entities
	 * 
	 * @throws IOException iff the enumeration entities cannot be loaded
	 * @throws JClassAlreadyExistsException iff the enumeration class
	 *     already exists
	 */
	private void generateEnumeration(
				JClass interfaceClass,
				JMethod factory
			) throws IOException, JClassAlreadyExistsException {
		
		JPackage pkgModel = model._package(pkg);
		JDefinedClass enumerationModel = pkgModel._class(JMod.PUBLIC | JMod.FINAL, getSimpleName() +"s", ClassType.CLASS);
		
		
		/* Resolve references
		 */
		JClass assertionErrorClass = model.ref(AssertionError.class);
		JClass noSuchElementExceptionClass = model.ref(NoSuchElementException.class);
		JClass stringClass = model.ref(String.class);
		
		
		/* public static Language valueOf(CharSequence name)
		 */
		JMethod valueOfMethod = enumerationModel.method(JMod.PUBLIC | JMod.STATIC, interfaceClass, "valueOf");
		JVar nameParam = valueOfMethod.param(CharSequence.class, "name");
		JBlock valueOfBody = valueOfMethod.body();
		
		JVar nameVar = valueOfBody.decl(JMod.FINAL, stringClass, "namе", nameParam.invoke("toString"));
		JSwitch nameSwitch =  valueOfBody._switch(nameVar);
		
		valueOfBody._throw(JExpr._new(noSuchElementExceptionClass).arg(
			JExpr.lit("Cannot find "+ getSimpleName() +" named `")
			.plus(nameVar)
			.plus(JExpr.lit("'"))
		));
		
		
		/* One field per entity
		 */
		getResources().forEach(resource -> {
			
			/* public static final Language ONE_C = Language.fromName("1C");
			 */
			JFieldVar entityField = enumerationModel.field(
				JMod.PUBLIC | JMod.STATIC | JMod.FINAL, interfaceClass,
				resource.toJavaIdentifier().toEnumerationConstant(),
				interfaceClass.staticInvoke(factory).arg(resource.getBasename())
			);
			
			/* case "1C":
			 *     return ONE_C;
			 */
			JCase basenameCase = nameSwitch._case(JExpr.lit(resource.getBasename()));
			basenameCase.body()._return(entityField);
			
			/* case "ONE_C":
			 *     return ONE_C;
			 */
			JCase identifierCase = nameSwitch._case(JExpr.lit(resource.toJavaIdentifier().toEnumerationConstant()));
			identifierCase.body()._return(entityField);
		});
		
		
		/* No instances of enumeration class allowed
		 */
		JMethod constructor = enumerationModel.constructor(JMod.PRIVATE);
		constructor.body()._throw(JExpr._new(assertionErrorClass));
	}
	
	
	
	private void generateCharSequenceMethods(JDefinedClass model, int constructorMods) {
		JFieldVar nameField = model.field(JMod.PRIVATE | JMod.FINAL, String.class, "name");
		
		
		/* Constructor
		 */
		JMethod constructorMethod = model.constructor(constructorMods);
		JVar nameParam = constructorMethod.param(CharSequence.class, "name");
		
		constructorMethod.body().assign(
			JExpr._this().ref(nameField),
			nameParam.invoke("toString")
		);
		
		
		/* int length()
		 */
		JMethod lengthMethod = model.method(JMod.PUBLIC, int.class, "length");
		lengthMethod.annotate(Override.class);
		lengthMethod.body()._return(nameField.invoke("length"));
		
		
		/* char charAt(int i)
		 */
		JMethod charAtMethod = model.method(JMod.PUBLIC, char.class, "charAt");
		charAtMethod.annotate(Override.class);
		JVar positionParam = charAtMethod.param(int.class, "position");
		charAtMethod.body()._return(nameField.invoke("charAt").arg(positionParam));
		
		
		/* CharSequence subSequence(int start, int end)
		 */
		JMethod subSequenceMethod = model.method(JMod.PUBLIC, CharSequence.class, "subSequence");
		subSequenceMethod.annotate(Override.class);
		JVar startParam = subSequenceMethod.param(int.class, "start");
		JVar endParam = subSequenceMethod.param(int.class, "end");
		subSequenceMethod.body()._return(nameField.invoke("subSequence").arg(startParam).arg(endParam));
		
		
		/* boolean equals(Object o)
		 */
		JMethod equalsMethod = model.method(JMod.PUBLIC, boolean.class, "equals");
		equalsMethod.annotate(Override.class);
		JVar objectParam = equalsMethod.param(Object.class, "object");
		JBlock equalsBody = equalsMethod.body();
		
		/* if (null == object) {
		 *     return false;
		 * }
		 */
		equalsBody._if(JExpr._null().eq(objectParam))._then()._return(JExpr.FALSE);
		
		/* if (this == object) {
		 *     return true;
		 * }
		 */
		equalsBody._if(JExpr._this().eq(objectParam))._then()._return(JExpr.TRUE);
		
		/* if (!(object instanceof CharSequence)) {
		 *     return false;
		 * }
		 */
		JType charSequenceType = this.model._ref(CharSequence.class);
		equalsBody._if(objectParam._instanceof(charSequenceType).not())._then()._return(JExpr.FALSE);
		
		/* CharSequence other = (CharSequence)object;
		 */
		JVar otherVar = equalsBody.decl(charSequenceType, "other", JExpr.cast(charSequenceType, objectParam));
		
		/* return name.equals(other.toString());
		 */
		equalsBody._return(nameField.invoke("equals").arg(otherVar.invoke("toString")));
		
		
		/* int hashCode()
		 */
		JMethod hashCodeMethod = model.method(JMod.PUBLIC, int.class, "hashCode");
		hashCodeMethod.annotate(Override.class);
		hashCodeMethod.body()._return(nameField.invoke("hashCode"));
		
		
		/* String toString()
		 */
		JMethod toStringMethod = model.method(JMod.PUBLIC, String.class, "toString");
		toStringMethod.annotate(Override.class);
		toStringMethod.body()._return(nameField);
	}
	
	
	
	
	
	@Override
	public final void generate() throws IOException, JClassAlreadyExistsException {
		JDefinedClass interfaceModel = generateInterface();
		JMethod factory = generateImplementation(interfaceModel);
		generateEnumeration(interfaceModel, factory);
	}
}
