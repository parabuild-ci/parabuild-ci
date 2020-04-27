---
title: PMD 7.0.0 development
permalink: pmd_next_major_development.html
keywords: changelog, release notes, deprecation, api changes
---

We're excited to bring you the next major version of PMD! Here are the major features and changes we're working on.
To give us feedback or to suggest a new feature, drop us a line on [Gitter](https://gitter.im/pmd/pmd)!

## New Features

TODO

## Java grammar changes

{% include note.html content="Current plans are listed [here](https://github.com/pmd/pmd/labels/in%3Aast) and in particular [here](https://github.com/pmd/pmd/issues/1019)" %}



## New API support guidelines

### What's new

Until now, all released public members and types were implicitly considered part
of PMD's public API, including inheritance-specific members (protected members, abstract methods).
We have maintained those APIs with the goal to preserve full binary compatibility between minor releases,
only breaking those APIs infrequently, for major releases.

In order to allow PMD to move forward at a faster pace, this implicit contract will
be invalidated with PMD 7.0.0. We now introduce more fine-grained distinctions between
the type of compatibility support we guarantee for our libraries, and ways to make
them explicit to clients of PMD.

#### `.internal` packages and `@InternalApi` annotation

*Internal API* is meant for use *only* by the main PMD codebase. Internal types and methods
may be modified in any way, or even removed, at any time.

Any API in a package that contains an `.internal` segment is considered internal.
The `@InternalApi` annotation will be used for APIs that have to live outside of
these packages, e.g. methods of a public type that shouldn't be used outside of PMD (again,
these can be removed anytime).

#### `@ReservedSubclassing`

Types marked with the `@ReservedSubclassing` annotation are only meant to be subclassed
by classes within PMD. As such, we may add new abstract methods, or remove protected methods,
at any time. All published public members remain supported. The annotation is *not* inherited, which
means a reserved interface doesn't prevent its implementors to be subclassed.

#### `@Experimental`

APIs marked with the `@Experimental` annotation at the class or method level are subject to change.
They can be modified in any way, or even removed, at any time. You should not use or rely
 on them in any production code. They are purely to allow broad testing and feedback.

#### `@Deprecated`

APIs marked with the `@Deprecated` annotation at the class or method level will remain supported
until the next major release but it is recommended to stop using them.

### The transition

*All currently supported APIs will remain so until 7.0.0*. All APIs that are to be moved to
`.internal` packages or hidden will be tagged `@InternalApi` before that major release, and
the breaking API changes will be performed in 7.0.0.

## Planned API removals

### List of currently deprecated APIs

{% include warning.html content="This list is not exhaustive. The ultimate reference is whether
an API is tagged as `@Deprecated` or not in the latest minor release. During the development of 7.0.0,
we may decide to remove some APIs that were not tagged as deprecated, though we'll try to avoid it." %}

#### 6.23.0

##### Deprecated APIs

###### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   {% jdoc core::lang.rule.xpath.AbstractXPathRuleQuery %}
*   {% jdoc core::lang.rule.xpath.JaxenXPathRuleQuery %}
*   {% jdoc core::lang.rule.xpath.SaxonXPathRuleQuery %}
*   {% jdoc core::lang.rule.xpath.XPathRuleQuery %}

###### In ASTs

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated in the **Apex**, **Javascript**, **PL/SQL**, **Scala** and **Visualforce** ASTs:

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked {% jdoc core::annotation.InternalApi %}. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
    *   In the meantime you should use interfaces like {% jdoc visualforce::lang.vf.ast.VfNode %} or
        {% jdoc core::lang.ast.Node %}, or the other published interfaces in this package,
        to refer to nodes generically.
    *   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The implementation classes of {% jdoc core::lang.Parser %} (eg {% jdoc visualforce::lang.vf.VfParser %}) are deprecated and should not be used directly.
    Use {% jdoc !!core::lang.LanguageVersionHandler#getParser(ParserOptions) %} instead.
*   The implementation classes of {% jdoc core::lang.TokenManager %} (eg {% jdoc visualforce::lang.vf.VfTokenManager %}) are deprecated and should not be used outside of our implementation.
    **This also affects CPD-only modules**.

These deprecations are added to the following language modules in this release.
Please look at the package documentation to find out the full list of deprecations.
* Apex: **{% jdoc_package apex::lang.apex.ast %}**
* Javascript: **{% jdoc_package javascript::lang.ecmascript.ast %}**
* PL/SQL: **{% jdoc_package plsql::lang.plsql.ast %}**
* Scala: **{% jdoc_package scala::lang.scala.ast %}**
* Visualforce: **{% jdoc_package visualforce::lang.vf.ast %}**

These deprecations have already been rolled out in a previous version for the
following languages:
* Java: {% jdoc_package java::lang.java.ast %}
* Java Server Pages: {% jdoc_package jsp::lang.jsp.ast %}
* Velocity Template Language: {% jdoc_package vm::lang.vm.ast %}

Outside of these packages, these changes also concern the following TokenManager
implementations, and their corresponding Parser if it exists (in the same package):

*   {% jdoc cpp::lang.cpp.CppTokenManager %}
*   {% jdoc java::lang.java.JavaTokenManager %}
*   {% jdoc javascript::lang.ecmascript5.Ecmascript5TokenManager %}
*   {% jdoc jsp::lang.jsp.JspTokenManager %}
*   {% jdoc matlab::lang.matlab.MatlabTokenManager %}
*   {% jdoc modelica::lang.modelica.ModelicaTokenManager %}
*   {% jdoc objectivec::lang.objectivec.ObjectiveCTokenManager %}
*   {% jdoc plsql::lang.plsql.PLSQLTokenManager %}
*   {% jdoc python::lang.python.PythonTokenManager %}
*   {% jdoc visualforce::lang.vf.VfTokenManager %}
*   {% jdoc vm::lang.vm.VmTokenManager %}


In the **Java AST** the following attributes are deprecated and will issue a warning when used in XPath rules:

*   {% jdoc !!java::lang.java.ast.ASTAdditiveExpression#getImage() %} - use `getOperator()` instead
*   {% jdoc !!java::lang.java.ast.ASTVariableDeclaratorId#getImage() %} - use `getName()` instead
*   {% jdoc !!java::lang.java.ast.ASTVariableDeclaratorId#getVariableName() %} - use `getName()` instead

###### For removal

*   {% jdoc !!core::lang.Parser#getTokenManager(java.lang.String,java.io.Reader) %}
*   {% jdoc !!core::lang.TokenManager#setFileName(java.lang.String) %}
*   {% jdoc !!core::lang.ast.AbstractTokenManager#setFileName(java.lang.String) %}
*   {% jdoc !!core::lang.ast.AbstractTokenManager#getFileName(java.lang.String) %}
*   {% jdoc !!core::cpd.token.AntlrToken#getType() %} - use `getKind()` instead.
*   {% jdoc core::lang.rule.ImmutableLanguage %}
*   {% jdoc core::lang.rule.MockRule %}
*   {% jdoc !!core::lang.ast.Node#getFirstParentOfAnyType(java.lang.Class[]) %}
*   {% jdoc !!core::lang.ast.Node#getAsDocument() %}
*   {% jdoc !!core::lang.ast.AbstractNode#hasDescendantOfAnyType(java.lang.Class[]) %}
*   {% jdoc !!java::lang.java.ast.ASTRecordDeclaration#getComponentList() %}
*   Multiple fields, constructors and methods in {% jdoc core::lang.rule.XPathRule %}. See javadoc for details.

#### 6.22.0

##### Deprecated APIs

###### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* {% jdoc java::lang.java.JavaLanguageHandler %}
* {% jdoc java::lang.java.JavaLanguageParser %}
* {% jdoc java::lang.java.JavaDataFlowHandler %}
* Implementations of {% jdoc core::lang.rule.RuleViolationFactory %} in each
  language module, eg {% jdoc java::lang.java.rule.JavaRuleViolationFactory %}.
  See javadoc of {% jdoc core::lang.rule.RuleViolationFactory %}.
* Implementations of {% jdoc core::RuleViolation %} in each language module,
  eg {% jdoc java::lang.java.rule.JavaRuleViolation %}. See javadoc of
  {% jdoc core::RuleViolation %}.

* {% jdoc core::rules.RuleFactory %}
* {% jdoc core::rules.RuleBuilder %}
* Constructors of {% jdoc core::RuleSetFactory %}, use factory methods from {% jdoc core::RulesetsFactoryUtils %} instead
* {% jdoc core::RulesetsFactoryUtils#getRulesetFactory(core::PMDConfiguration, core::util.ResourceLoader) %}

* {% jdoc apex::lang.apex.ast.AbstractApexNode %}
* {% jdoc apex::lang.apex.ast.AbstractApexNodeBase %}, and the related `visit`
methods on {% jdoc apex::lang.apex.ast.ApexParserVisitor %} and its implementations.
 Use {% jdoc apex::lang.apex.ast.ApexNode %} instead, now considers comments too.

###### For removal

* pmd-core
  * {% jdoc core::lang.dfa.DFAGraphRule %} and its implementations
  * {% jdoc core::lang.dfa.DFAGraphMethod %}
  * Many methods on the {% jdoc core::lang.ast.Node %} interface
  and {% jdoc core::lang.ast.AbstractNode %} base class. See their javadoc for details.
  * {% jdoc !!core::lang.ast.Node#isFindBoundary() %} is deprecated for XPath queries.
  * Many APIs of {% jdoc_package core::lang.metrics %}, though most of them were internal and
  probably not used directly outside of PMD. Use {% jdoc core::lang.metrics.MetricsUtil %} as
  a replacement for the language-specific façades too.
  * {% jdoc core::lang.ast.QualifiableNode %}, {% jdoc core::lang.ast.QualifiedName %}
* pmd-java
  * {% jdoc java::lang.java.AbstractJavaParser %}
  * {% jdoc java::lang.java.AbstractJavaHandler %}
  * [`ASTAnyTypeDeclaration.TypeKind`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.TypeKind.html)
  * {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getKind() %}
  * {% jdoc java::lang.java.ast.JavaQualifiedName %}
  * {% jdoc !!java::lang.java.ast.ASTCatchStatement#getBlock() %}
  * {% jdoc !!java::lang.java.ast.ASTCompilationUnit#declarationsAreInDefaultPackage() %}
  * {% jdoc java::lang.java.ast.JavaQualifiableNode %}
    * {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getQualifiedName() %}
    * {% jdoc !!java::lang.java.ast.ASTMethodOrConstructorDeclaration#getQualifiedName() %}
    * {% jdoc !!java::lang.java.ast.ASTLambdaExpression#getQualifiedName() %}
  * {% jdoc_package java::lang.java.qname %} and its contents
  * {% jdoc java::lang.java.ast.MethodLikeNode %}
    * Its methods will also be removed from its implementations,
      {% jdoc java::lang.java.ast.ASTMethodOrConstructorDeclaration %},
      {% jdoc java::lang.java.ast.ASTLambdaExpression %}.
  * {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getImage() %} will be removed. Please use `getSimpleName()`
    instead. This affects {% jdoc !!java::lang.java.ast.ASTAnnotationTypeDeclaration#getImage() %},
    {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#getImage() %}, and
    {% jdoc !!java::lang.java.ast.ASTEnumDeclaration#getImage() %}.
  * Several methods of {% jdoc java::lang.java.ast.ASTTryStatement %}, replacements with other names
    have been added. This includes the XPath attribute `@Finally`, replace it with a test for `child::FinallyStatement`.
  * Several methods named `getGuardExpressionNode` are replaced with `getCondition`. This affects the
    following nodes: WhileStatement, DoStatement, ForStatement, IfStatement, AssertStatement, ConditionalExpression.
  * {% jdoc java::lang.java.ast.ASTYieldStatement %} will not implement {% jdoc java::lang.java.ast.TypeNode %}
    anymore come 7.0.0. Test the type of the expression nested within it.
  * {% jdoc java::lang.java.metrics.JavaMetrics %}, {% jdoc java::lang.java.metrics.JavaMetricsComputer %}
  * {% jdoc !!java::lang.java.ast.ASTArguments#getArgumentCount() %}.
    Use {% jdoc java::lang.java.ast.ASTArguments#size() %} instead.
  * {% jdoc !!java::lang.java.ast.ASTFormalParameters#getParameterCount() %}.
    Use {% jdoc java::lang.java.ast.ASTFormalParameters#size() %} instead.
* pmd-apex
  * {% jdoc apex::lang.apex.metrics.ApexMetrics %}, {% jdoc apex::lang.apex.metrics.ApexMetricsComputer %}

###### In ASTs (JSP)

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the JSP AST** (with other languages to come):

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked {% jdoc core::annotation.InternalApi %}. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
    *   In the meantime you should use interfaces like {% jdoc jsp::lang.jsp.ast.JspNode %} or
        {% jdoc core::lang.ast.Node %}, or the other published interfaces in this package,
        to refer to nodes generically.
    *   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The class {% jdoc jsp::lang.jsp.JspParser %} is deprecated and should not be used directly.
    Use {% jdoc !!core::lang.LanguageVersionHandler#getParser(ParserOptions) %} instead.

Please look at {% jdoc_package jsp::lang.jsp.ast %} to find out the full list of deprecations.

###### In ASTs (Velocity)

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the VM AST** (with other languages to come):

*   Manual instantiation of nodes. **Constructors of node classes are deprecated** and
    marked {% jdoc core::annotation.InternalApi %}. Nodes should only be obtained from the parser,
    which for rules, means that they never need to instantiate node themselves.
    Those constructors will be made package private with 7.0.0.
*   **Subclassing of abstract node classes, or usage of their type**. The base classes are internal API
    and will be hidden in version 7.0.0. You should not couple your code to them.
    *   In the meantime you should use interfaces like {% jdoc vm::lang.vm.ast.VmNode %} or
        {% jdoc core::lang.ast.Node %}, or the other published interfaces in this package,
        to refer to nodes generically.
    *   Concrete node classes will **be made final** with 7.0.0.
*   Setters found in any node class or interface. **Rules should consider the AST immutable**.
    We will make those setters package private with 7.0.0.
*   The package {% jdoc_package vm::lang.vm.directive %} as well as the classes
    {% jdoc vm::lang.vm.util.DirectiveMapper %} and {% jdoc vm::lang.vm.util.LogUtil %} are deprecated
    for removal. They were only used internally during parsing.
*   The class {% jdoc vm::lang.vm.VmParser %} is deprecated and should not be used directly.
    Use {% jdoc !!core::lang.LanguageVersionHandler#getParser(ParserOptions) %} instead.

Please look at {% jdoc_package vm::lang.vm.ast %} to find out the full list of deprecations.

##### PLSQL AST

The production and node `ASTCursorBody` was unnecessary, not used and has been removed. Cursors have been already
parsed as `ASTCursorSpecification`.

#### 6.21.0

##### Deprecated APIs

###### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* {% jdoc java::lang.java.JavaLanguageHandler %}
* {% jdoc java::lang.java.JavaLanguageParser %}
* {% jdoc java::lang.java.JavaDataFlowHandler %}
* Implementations of {% jdoc core::lang.rule.RuleViolationFactory %} in each
  language module, eg {% jdoc java::lang.java.rule.JavaRuleViolationFactory %}.
  See javadoc of {% jdoc core::lang.rule.RuleViolationFactory %}.
* Implementations of {% jdoc core::RuleViolation %} in each language module,
  eg {% jdoc java::lang.java.rule.JavaRuleViolation %}. See javadoc of
  {% jdoc core::RuleViolation %}.

* {% jdoc core::rules.RuleFactory %}
* {% jdoc core::rules.RuleBuilder %}
* Constructors of {% jdoc core::RuleSetFactory %}, use factory methods from {% jdoc core::RulesetsFactoryUtils %} instead
* {% jdoc core::RulesetsFactoryUtils#getRulesetFactory(core::PMDConfiguration, core::util.ResourceLoader) %}

* {% jdoc apex::lang.apex.ast.AbstractApexNode %}
* {% jdoc apex::lang.apex.ast.AbstractApexNodeBase %}, and the related `visit`
methods on {% jdoc apex::lang.apex.ast.ApexParserVisitor %} and its implementations.
 Use {% jdoc apex::lang.apex.ast.ApexNode %} instead, now considers comments too.

* {% jdoc core::lang.ast.CharStream %}, {% jdoc core::lang.ast.JavaCharStream %},
{% jdoc core::lang.ast.SimpleCharStream %}: these are APIs used by our JavaCC
implementations and that will be moved/refactored for PMD 7.0.0. They should not
be used, extended or implemented directly.
* All classes generated by JavaCC, eg {% jdoc java::lang.java.ast.JJTJavaParserState %}.
This includes token classes, which will be replaced with a single implementation, and
subclasses of {% jdoc core::lang.ast.ParseException %}, whose usages will be replaced
by just that superclass.

###### For removal

* pmd-core
  * Many methods on the {% jdoc core::lang.ast.Node %} interface
  and {% jdoc core::lang.ast.AbstractNode %} base class. See their javadoc for details.
  * {% jdoc !!core::lang.ast.Node#isFindBoundary() %} is deprecated for XPath queries.
* pmd-java
  * {% jdoc java::lang.java.AbstractJavaParser %}
  * {% jdoc java::lang.java.AbstractJavaHandler %}
  * [`ASTAnyTypeDeclaration.TypeKind`](https://javadoc.io/page/net.sourceforge.pmd/pmd-java/6.21.0/net/sourceforge/pmd/lang/java/ast/ASTAnyTypeDeclaration.TypeKind.html)
  * {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getKind() %}
  * {% jdoc java::lang.java.ast.JavaQualifiedName %}
  * {% jdoc !!java::lang.java.ast.ASTCatchStatement#getBlock() %}
  * {% jdoc !!java::lang.java.ast.ASTCompilationUnit#declarationsAreInDefaultPackage() %}
  * {% jdoc java::lang.java.ast.JavaQualifiableNode %}
    * {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getQualifiedName() %}
    * {% jdoc !!java::lang.java.ast.ASTMethodOrConstructorDeclaration#getQualifiedName() %}
    * {% jdoc !!java::lang.java.ast.ASTLambdaExpression#getQualifiedName() %}
  * {% jdoc_package java::lang.java.qname %} and its contents
  * {% jdoc java::lang.java.ast.MethodLikeNode %}
    * Its methods will also be removed from its implementations,
      {% jdoc java::lang.java.ast.ASTMethodOrConstructorDeclaration %},
      {% jdoc java::lang.java.ast.ASTLambdaExpression %}.
  * {% jdoc !!java::lang.java.ast.ASTAnyTypeDeclaration#getImage() %} will be removed. Please use `getSimpleName()`
    instead. This affects {% jdoc !!java::lang.java.ast.ASTAnnotationTypeDeclaration#getImage() %},
    {% jdoc !!java::lang.java.ast.ASTClassOrInterfaceDeclaration#getImage() %}, and
    {% jdoc !!java::lang.java.ast.ASTEnumDeclaration#getImage() %}.
  * Several methods of {% jdoc java::lang.java.ast.ASTTryStatement %}, replacements with other names
    have been added. This includes the XPath attribute `@Finally`, replace it with a test for `child::FinallyStatement`.
  * Several methods named `getGuardExpressionNode` are replaced with `getCondition`. This affects the
    following nodes: WhileStatement, DoStatement, ForStatement, IfStatement, AssertStatement, ConditionalExpression.
  * {% jdoc java::lang.java.ast.ASTYieldStatement %} will not implement {% jdoc java::lang.java.ast.TypeNode %}
    anymore come 7.0.0. Test the type of the expression nested within it.

#### 6.20.0

No changes.

#### 6.19.0

##### Deprecated APIs

###### For removal

* pmd-core
  * All the package {% jdoc_package core::dcd %} and its subpackages. See {% jdoc core::dcd.DCD %}.
  * In {% jdoc core::lang.LanguageRegistry %}:
    * {% jdoc core::lang.LanguageRegistry#commaSeparatedTerseNamesForLanguageVersion(List) %}
    * {% jdoc core::lang.LanguageRegistry#commaSeparatedTerseNamesForLanguage(List) %}
    * {% jdoc core::lang.LanguageRegistry#findAllVersions() %}
    * {% jdoc core::lang.LanguageRegistry#findLanguageVersionByTerseName(String) %}
    * {% jdoc core::lang.LanguageRegistry#getInstance() %}
  * {% jdoc !!core::RuleSet#getExcludePatterns() %}. Use the new method {% jdoc core::RuleSet#getFileExclusions() %} instead.
  * {% jdoc !!core::RuleSet#getIncludePatterns() %}. Use the new method {% jdoc core::RuleSet#getFileInclusions() %} instead.
  * {% jdoc !!core::lang.Parser#canParse() %}
  * {% jdoc !!core::lang.Parser#getSuppressMap() %}
  * {% jdoc !!core::rules.RuleBuilder#RuleBuilder(String,String,String) %}. Use the new constructor with the correct ResourceLoader instead.
  * {% jdoc !!core::rules.RuleFactory#RuleFactory() %}. Use the new constructor with the correct ResourceLoader instead.
* pmd-java
  * {% jdoc java::lang.java.ast.CanSuppressWarnings %} and its implementations
  * {% jdoc java::lang.java.rule.AbstractJavaRule#isSuppressed(Node) %}
  * {% jdoc java::lang.java.rule.AbstractJavaRule#getDeclaringType(Node) %}.
  * {% jdoc java::lang.java.rule.JavaRuleViolation#isSupressed(Node,Rule) %}
  * {% jdoc java::lang.java.ast.ASTMethodDeclarator %}
  * {% jdoc java::lang.java.ast.ASTMethodDeclaration#getMethodName() %}
  * {% jdoc java::lang.java.ast.ASTMethodDeclaration#getBlock() %}
  * {% jdoc java::lang.java.ast.ASTConstructorDeclaration#getParameterCount() %}
* pmd-apex
  * {% jdoc apex::lang.apex.ast.CanSuppressWarnings %} and its implementations
  * {% jdoc apex::lang.apex.rule.ApexRuleViolation#isSupressed(Node,Rule) %}

###### Internal APIs

* pmd-core
  * All the package {% jdoc_package core::util %} and its subpackages,
  except {% jdoc_package core::util.datasource %} and {% jdoc_package core::util.database %}.
  * {% jdoc core::cpd.GridBagHelper %}
  * {% jdoc core::renderers.ColumnDescriptor %}


#### 6.18.0

##### Changes to Renderer

*   Each renderer has now a new method {% jdoc !!core::renderers.Renderer#setUseShortNames(List) %} which
    is used for implementing the "shortnames" CLI option. The method is automatically called by PMD, if this
    CLI option is in use. When rendering filenames to the report, the new helper method
    {% jdoc !!core::renderers.AbstractRenderer#determineFileName(String) %} should be used. This will change
    the filename to a short name, if the CLI option "shortnames" is used.
    
    Not adjusting custom renderers will make them render always the full file names and not honoring the
    CLI option "shortnames".

##### Deprecated APIs

###### For removal

*   The methods {% jdoc java::lang.java.ast.ASTImportDeclaration#getImportedNameNode() %} and
    {% jdoc java::lang.java.ast.ASTImportDeclaration#getPackage() %} have been deprecated and
    will be removed with PMD 7.0.0.
*   The method {% jdoc !!core::RuleContext#setSourceCodeFilename(String) %} has been deprecated
    and will be removed. The already existing method {% jdoc !!core::RuleContext#setSourceCodeFile(File) %}
    should be used instead. The method {% jdoc !!core::RuleContext#getSourceCodeFilename() %} still
    exists and returns just the filename without the full path.
*   The method {% jdoc !!core::processor.AbstractPMDProcessor#filenameFrom(DataSource) %} has been
    deprecated. It was used to determine a "short name" of the file being analyzed, so that the report
    can use short names. However, this logic has been moved to the renderers.
*   The method {% jdoc !!core::Report#metrics() %} and {% jdoc core::Report::hasMetrics() %} have
    been deprecated. They were leftovers from a previous deprecation round targeting
    {% jdoc core::lang.rule.stat.StatisticalRule %}.

###### Internal APIs

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0. You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

* pmd-core
  * {% jdoc_package core::cache %}
* pmd-java
  * {% jdoc_package java::lang.java.typeresolution %}: Everything, including
    subpackages, except {% jdoc java::lang.java.typeresolution.TypeHelper %} and
    {% jdoc java::lang.java.typeresolution.typedefinition.JavaTypeDefinition %}.
  * {% jdoc !c!java::lang.java.ast.ASTCompilationUnit#getClassTypeResolver() %}


#### 6.17.0

No changes.

#### 6.16.0

##### Deprecated APIs

> Reminder: Please don't use members marked with the annotation {% jdoc core::annotation.InternalApi %}, as they will likely be removed, hidden, or otherwise intentionally broken with 7.0.0.


###### In ASTs

As part of the changes we'd like to do to AST classes for 7.0.0, we would like to
hide some methods and constructors that rule writers should not have access to.
The following usages are now deprecated **in the Java AST** (with other languages to come):

* Manual instantiation of nodes. **Constructors of node classes are deprecated** and marked {% jdoc core::annotation.InternalApi %}. Nodes should only be obtained from the parser, which for rules, means that never need to instantiate node themselves. Those constructors will be made package private with 7.0.0.
* **Subclassing of abstract node classes, or usage of their type**. Version 7.0.0 will bring a new set of abstractions that will be public API, but the base classes are and will stay internal. You should not couple your code to them.
  * In the meantime you should use interfaces like {% jdoc java::lang.java.ast.JavaNode %} or  {% jdoc core::lang.ast.Node %}, or the other published interfaces in this package, to refer to nodes generically.
  * Concrete node classes will **be made final** with 7.0.0.
* Setters found in any node class or interface. **Rules should consider the AST immutable**. We will make those setters package private with 7.0.0.

Please look at {% jdoc_package java::lang.java.ast %} to find out the full list
of deprecations.


#### 6.15.0

##### Deprecated APIs

###### For removal

*   The `DumpFacades` in all languages, that could be used to transform a AST into a textual representation,
    will be removed with PMD 7. The rule designer is a better way to inspect nodes.
    *   {% jdoc !q!apex::lang.apex.ast.DumpFacade %}
    *   {% jdoc !q!java::lang.java.ast.DumpFacade %}
    *   {% jdoc !q!javascript::lang.ecmascript.ast.DumpFacade %}
    *   {% jdoc !q!jsp::lang.jsp.ast.DumpFacade %}
    *   {% jdoc !q!plsql::lang.plsql.ast.DumpFacade %}
    *   {% jdoc !q!visualforce::lang.vf.ast.DumpFacade %}
    *   {% jdoc !q!vm::lang.vm.ast.AbstractVmNode#dump(String, boolean, Writer) %}
    *   {% jdoc !q!xml::lang.xml.ast.DumpFacade %}
*   The method {% jdoc !c!core::lang.LanguageVersionHandler#getDumpFacade(Writer, String, boolean) %} will be
    removed as well. It is deprecated, along with all its implementations in the subclasses of {% jdoc core::lang.LanguageVersionHandler %}.

#### 6.14.0

No changes.

#### 6.13.0

##### Command Line Interface

The start scripts `run.sh`, `pmd.bat` and `cpd.bat` support the new environment variable `PMD_JAVA_OPTS`.
This can be used to set arbitrary JVM options for running PMD, such as memory settings (e.g. `PMD_JAVA_OPTS=-Xmx512m`)
or enable preview language features (e.g. `PMD_JAVA_OPTS=--enable-preview`).

The previously available variables such as `OPTS` or `HEAPSIZE` are deprecated and will be removed with PMD 7.0.0.

##### Deprecated API

*   {% jdoc core::renderers.CodeClimateRule %} is deprecated in 7.0.0 because it was unused for 2 years and
    created an unwanted dependency.
    Properties "cc_categories", "cc_remediation_points_multiplier", "cc_block_highlighting" will also be removed.
    See [#1702](https://github.com/pmd/pmd/pull/1702) for more.

*   The Apex ruleset `rulesets/apex/ruleset.xml` has been deprecated and will be removed in 7.0.0. Please use the new
    quickstart ruleset `rulesets/apex/quickstart.xml` instead.

#### 6.12.0

No changes.

#### 6.11.0

* {% jdoc core::lang.rule.stat.StatisticalRule %} and the related helper classes and base rule classes
are deprecated for removal in 7.0.0. This includes all of {% jdoc_package core::stat %} and {% jdoc_package core::lang.rule.stat %},
and also {% jdoc java::lang.java.rule.AbstractStatisticalJavaRule %}, {% jdoc apex::lang.apex.rule.AbstractStatisticalApexRule %} and the like.
The methods {% jdoc !c!core::Report#addMetric(core::stat.Metric) %} and {% jdoc core::ThreadSafeReportListener#metricAdded(core::stat.Metric) %}
will also be removed.
* {% jdoc core::properties.PropertySource#setProperty(core::properties.MultiValuePropertyDescriptor, Object[]) %} is deprecated,
because {% jdoc core::properties.MultiValuePropertyDescriptor %} is deprecated as well

#### 6.10.0

##### Properties framework

{% jdoc_nspace :props core::properties %}
{% jdoc_nspace :PDr props::PropertyDescriptor %}
{% jdoc_nspace :PF props::PropertyFactory %}

The properties framework is about to get a lifting, and for that reason, we need to deprecate a lot of APIs
to remove them in 7.0.0. The proposed changes to the API are described [on the wiki](https://github.com/pmd/pmd/wiki/Property-framework-7-0-0)

###### Changes to how you define properties

* Construction of property descriptors has been possible through builders since 6.0.0. The 7.0.0 API will only allow
construction through builders. The builder hierarchy, currently found in the package {% jdoc_package props::builders %},
is being replaced by the simpler {% jdoc props::PropertyBuilder %}. Their APIs enjoy a high degree of source compatibility.

* Concrete property classes like {% jdoc props::IntegerProperty %} and {% jdoc props::StringMultiProperty %} will gradually
all be deprecated until 7.0.0. Their usages should be replaced by direct usage of the {% jdoc props::PropertyDescriptor %}
interface, e.g. `PropertyDescriptor<Integer>` or `PropertyDescriptor<List<String>>`.

* Instead of spreading properties across countless classes, the utility class {% jdoc :PF %} will become
from 7.0.0 on the only provider for property descriptor builders. Each current property type will be replaced
by a corresponding method on `PropertyFactory`:
  * {% jdoc props::IntegerProperty %} is replaced by {% jdoc !c!:PF#intProperty(java.lang.String) %}
    * {% jdoc props::IntegerMultiProperty %} is replaced by {% jdoc !c!:PF#intListProperty(java.lang.String) %}

  * {% jdoc props::FloatProperty %} and {% jdoc props::DoubleProperty %} are both replaced by {% jdoc !c!:PF#doubleProperty(java.lang.String) %}.
    Having a separate property for floats wasn't that useful.
    * Similarly, {% jdoc props::FloatMultiProperty %} and {% jdoc props::DoubleMultiProperty %} are replaced by {% jdoc !c!:PF#doubleListProperty(java.lang.String) %}.

  * {% jdoc props::StringProperty %} is replaced by {% jdoc !c!:PF#stringProperty(java.lang.String) %}
    * {% jdoc props::StringMultiProperty %} is replaced by {% jdoc !c!:PF#stringListProperty(java.lang.String) %}

  * {% jdoc props::RegexProperty %} is replaced by {% jdoc !c!:PF#regexProperty(java.lang.String) %}

  * {% jdoc props::EnumeratedProperty %} is replaced by {% jdoc !c!:PF#enumProperty(java.lang.String,java.util.Map) %}
    * {% jdoc props::EnumeratedProperty %} is replaced by {% jdoc !c!:PF#enumListProperty(java.lang.String,java.util.Map) %}

  * {% jdoc props::BooleanProperty %} is replaced by {% jdoc !c!:PF#booleanProperty(java.lang.String) %}
    * Its multi-valued counterpart, {% jdoc props::BooleanMultiProperty %}, is not replaced, because it doesn't have a use case.

  * {% jdoc props::CharacterProperty %} is replaced by {% jdoc !c!:PF#charProperty(java.lang.String) %}
    * {% jdoc props::CharacterMultiProperty %} is replaced by {% jdoc !c!:PF#charListProperty(java.lang.String) %}

  * {% jdoc props::LongProperty %} is replaced by {% jdoc !c!:PF#longIntProperty(java.lang.String) %}
    * {% jdoc props::LongMultiProperty %} is replaced by {% jdoc !c!:PF#longIntListProperty(java.lang.String) %}

  * {% jdoc props::MethodProperty %}, {% jdoc props::FileProperty %}, {% jdoc props::TypeProperty %} and their multi-valued counterparts
    are discontinued for lack of a use-case, and have no planned replacement in 7.0.0 for now.
    <!-- TODO complete that as we proceed. -->


Here's an example:
```java
// Before 7.0.0, these are equivalent:
IntegerProperty myProperty = new IntegerProperty("score", "Top score value", 1, 100, 40, 3.0f);
IntegerProperty myProperty = IntegerProperty.named("score").desc("Top score value").range(1, 100).defaultValue(40).uiOrder(3.0f);

// They both map to the following in 7.0.0
PropertyDescriptor<Integer> myProperty = PropertyFactory.intProperty("score").desc("Top score value").require(inRange(1, 100)).defaultValue(40);
```

You're highly encouraged to migrate to using this new API as soon as possible, to ease your migration to 7.0.0.



###### Architectural simplifications

* {% jdoc props::EnumeratedPropertyDescriptor %}, {% jdoc props::NumericPropertyDescriptor %}, {% jdoc props::PackagedPropertyDescriptor %},
and the related builders (in {% jdoc_package props::builders %}) will be removed.
These specialized interfaces allowed additional constraints to be enforced on the
value of a property, but made the property class hierarchy very large and impractical
to maintain. Their functionality will be mapped uniformly to {% jdoc props::constraints.PropertyConstraint %}s,
which will allow virtually any constraint to be defined, and improve documentation and error reporting. The
related methods {% jdoc !c!props::PropertyTypeId#isPropertyNumeric() %} and
{% jdoc !c!props::PropertyTypeId#isPropertyPackaged() %} are also deprecated.

* {% jdoc props::MultiValuePropertyDescriptor %} and {% jdoc props::SingleValuePropertyDescriptor %}
are deprecated. 7.0.0 will introduce a new XML syntax which will remove the need for such a divide
between single- and multi-valued properties. The method {% jdoc !c!:PDr#isMultiValue() %} will be removed
accordingly.

###### Changes to the PropertyDescriptor interface

* {% jdoc :PDr#preferredRowCount() %} is deprecated with no intended replacement. It was never implemented, and does not belong
  in this interface. The methods {% jdoc :PDr#uiOrder() %} and `compareTo(PropertyDescriptor)` are deprecated for the
  same reason. These methods mix presentation logic with business logic and are not necessary for PropertyDescriptors to work.
  `PropertyDescriptor` will not extend `Comparable<PropertyDescriptor>` anymore come 7.0.0.
* The method {% jdoc :PDr#propertyErrorFor(core::Rule) %} is deprecated and will be removed with no intended
  replacement. It's really just a shortcut for `prop.errorFor(rule.getProperty(prop))`.
* `T `{% jdoc !a!:PDr#valueFrom(java.lang.String) %} and `String `{% jdoc :PDr#asDelimitedString(java.lang.Object) %}`(T)` are deprecated and will be removed. These were
  used to serialize and deserialize properties to/from a string, but 7.0.0 will introduce a more flexible
  XML syntax which will make them obsolete.
* {% jdoc :PDr#isMultiValue() %} and {% jdoc :PDr#type() %} are deprecated and won't be replaced. The new XML syntax will remove the need
  for a divide between multi- and single-value properties, and will allow arbitrary types to be represented.
  Since arbitrary types may be represented, `type` will become obsolete as it can't represent generic types,
  which will nevertheless be representable with the XML syntax. It was only used for documentation, but a
  new way to document these properties exhaustively will be added with 7.0.0.
* {% jdoc :PDr#errorFor(java.lang.Object) %} is deprecated as its return type will be changed to `Optional<String>` with the shift to Java 8.

##### Deprecated APIs

{% jdoc_nspace :xpath core::lang.ast.xpath %}
{% jdoc_nspace :jast java::lang.java.ast %}
{% jdoc_nspace :rule core::Rule %}
{% jdoc_nspace :lvh core::lang.LanguageVersionHandler %}
{% jdoc_nspace :rset core::RuleSet %}
{% jdoc_nspace :rsets core::RuleSets %}

###### For internalization

*   The implementation of the adapters for the XPath engines Saxon and Jaxen (package {% jdoc_package :xpath %})
    are now deprecated. They'll be moved to an internal package come 7.0.0. Only {% jdoc xpath::Attribute %} remains public API.

*   The classes {% jdoc props::PropertyDescriptorField %}, {% jdoc props::builders.PropertyDescriptorBuilderConversionWrapper %}, and the methods
    {% jdoc !c!:PDr#attributeValuesById %}, {% jdoc !c!:PDr#isDefinedExternally() %} and {% jdoc !c!props::PropertyTypeId#getFactory() %}.
    These were used to read and write properties to and from XML, but were not intended as public API.

*   The class {% jdoc props::ValueParserConstants %} and the interface {% jdoc props::ValueParser %}.

*   All classes from {% jdoc_package java::lang.java.metrics.impl.visitors %} are now considered internal API. They're deprecated
    and will be moved into an internal package with 7.0.0. To implement your own metrics visitors,
    {% jdoc jast::JavaParserVisitorAdapter %} should be directly subclassed.

*   {% jdoc !ac!:lvh#getDataFlowHandler() %}, {% jdoc !ac!:lvh#getDFAGraphRule() %}

*   {% jdoc core::lang.VisitorStarter %}

###### For removal

*   All classes from {% jdoc_package props::modules %} will be removed.

*   The interface {% jdoc jast::Dimensionable %} has been deprecated.
    It gets in the way of a grammar change for 7.0.0 and won't be needed anymore (see [#997](https://github.com/pmd/pmd/issues/997)).

*   Several methods from {% jdoc jast::ASTLocalVariableDeclaration %} and {% jdoc jast::ASTFieldDeclaration %} have
    also been deprecated:

    *   {% jdoc jast::ASTFieldDeclaration %} won't be a {% jdoc jast::TypeNode %} come 7.0.0, so
        {% jdoc jast::ASTFieldDeclaration#getType() %} and
        {% jdoc jast::ASTFieldDeclaration#getTypeDefinition() %} are deprecated.

    *   The method `getVariableName` on those two nodes will be removed, too.

    All these are deprecated because those nodes may declare several variables at once, possibly
    with different types (and obviously with different names). They both implement `Iterator<`{% jdoc jast::ASTVariableDeclaratorId %}`>`
    though, so you should iterate on each declared variable. See [#910](https://github.com/pmd/pmd/issues/910).

*   Visitor decorators are now deprecated and will be removed in PMD 7.0.0. They were originally a way to write
    composable visitors, used in the metrics framework, but they didn't prove cost-effective.

    *   In {% jdoc_package :jast %}: {% jdoc jast::JavaParserDecoratedVisitor %}, {% jdoc jast::JavaParserControllessVisitor %},
        {% jdoc jast::JavaParserControllessVisitorAdapter %}, and {% jdoc jast::JavaParserVisitorDecorator %} are deprecated with no intended replacement.


*   The LanguageModules of several languages, that only support CPD execution, have been deprecated. These languages
    are not fully supported by PMD, so having a language module does not make sense. The functionality of CPD is
    not affected by this change. The following classes have been deprecated and will be removed with PMD 7.0.0:

    *   {% jdoc cpp::lang.cpp.CppHandler %}
    *   {% jdoc cpp::lang.cpp.CppLanguageModule %}
    *   {% jdoc cpp::lang.cpp.CppParser %}
    *   {% jdoc cs::lang.cs.CsLanguageModule %}
    *   {% jdoc fortran::lang.fortran.FortranLanguageModule %}
    *   {% jdoc groovy::lang.groovy.GroovyLanguageModule %}
    *   {% jdoc matlab::lang.matlab.MatlabHandler %}
    *   {% jdoc matlab::lang.matlab.MatlabLanguageModule %}
    *   {% jdoc matlab::lang.matlab.MatlabParser %}
    *   {% jdoc objectivec::lang.objectivec.ObjectiveCHandler %}
    *   {% jdoc objectivec::lang.objectivec.ObjectiveCLanguageModule %}
    *   {% jdoc objectivec::lang.objectivec.ObjectiveCParser %}
    *   {% jdoc php::lang.php.PhpLanguageModule %}
    *   {% jdoc python::lang.python.PythonHandler %}
    *   {% jdoc python::lang.python.PythonLanguageModule %}
    *   {% jdoc python::lang.python.PythonParser %}
    *   {% jdoc ruby::lang.ruby.RubyLanguageModule %}
    *   {% jdoc scala::lang.scala.ScalaLanguageModule %}
    *   {% jdoc swift::lang.swift.SwiftLanguageModule %}


* Optional AST processing stages like symbol table, type resolution or data-flow analysis will be reified
in 7.0.0 to factorise common logic and make them extensible. Further explanations about this change can be
found on [#1426](https://github.com/pmd/pmd/pull/1426). Consequently, the following APIs are deprecated for
removal:
  * In {% jdoc :rule %}: {% jdoc !a!:rule#isDfa() %}, {% jdoc !a!:rule#isTypeResolution() %}, {% jdoc !a!:rule#isMultifile() %} and their
    respective setters.
  * In {% jdoc :rset %}: {% jdoc !a!:rset#usesDFA(core::lang.Language) %}, {% jdoc !a!:rset#usesTypeResolution(core::lang.Language) %}, {% jdoc !a!:rset#usesMultifile(core::lang.Language) %}
  * In {% jdoc :rsets %}: {% jdoc !a!:rsets#usesDFA(core::lang.Language) %}, {% jdoc !a!:rsets#usesTypeResolution(core::lang.Language) %}, {% jdoc !a!:rsets#usesMultifile(core::lang.Language) %}
  * In {% jdoc :lvh %}: {% jdoc !a!:lvh#getDataFlowFacade() %}, {% jdoc !a!:lvh#getSymbolFacade() %}, {% jdoc !a!:lvh#getSymbolFacade(java.lang.ClassLoader) %},
    {% jdoc !a!:lvh#getTypeResolutionFacade(java.lang.ClassLoader) %}, {% jdoc !a!:lvh#getQualifiedNameResolutionFacade(java.lang.ClassLoader) %}

#### 6.9.0

No changes.

#### 6.8.0

*   A couple of methods and fields in `net.sourceforge.pmd.properties.AbstractPropertySource` have been
    deprecated, as they are replaced by already existing functionality or expose internal implementation
    details: `propertyDescriptors`, `propertyValuesByDescriptor`,
    `copyPropertyDescriptors()`, `copyPropertyValues()`, `ignoredProperties()`, `usesDefaultValues()`,
    `useDefaultValueFor()`.

*   Some methods in `net.sourceforge.pmd.properties.PropertySource` have been deprecated as well:
    `usesDefaultValues()`, `useDefaultValueFor()`, `ignoredProperties()`.

*   The class `net.sourceforge.pmd.lang.rule.AbstractDelegateRule` has been deprecated and will
    be removed with PMD 7.0.0. It is internally only in use by RuleReference.

*   The default constructor of `net.sourceforge.pmd.lang.rule.RuleReference` has been deprecated
    and will be removed with PMD 7.0.0. RuleReferences should only be created by providing a Rule and
    a RuleSetReference. Furthermore the following methods are deprecated: `setRuleReference()`,
    `hasOverriddenProperty()`, `usesDefaultValues()`, `useDefaultValueFor()`.

#### 6.7.0

*   All classes in the package `net.sourceforge.pmd.lang.dfa.report` have been deprecated and will be removed
    with PMD 7.0.0. This includes the class `net.sourceforge.pmd.lang.dfa.report.ReportTree`. The reason is,
    that this class is very specific to Java and not suitable for other languages. It has only been used for
    `YAHTMLRenderer`, which has been rewritten to work without these classes.

*   The nodes RUNSIGNEDSHIFT and RSIGNEDSHIFT are deprecated and will be removed from the AST with PMD 7.0.0.
    These represented the operator of ShiftExpression in two cases out of three, but they're not needed and
    make ShiftExpression inconsistent. The operator of a ShiftExpression is now accessible through
    ShiftExpression#getOperator.

#### 6.5.0

*   The utility class `net.sourceforge.pmd.lang.java.ast.CommentUtil` has been deprecated and will be removed
    with PMD 7.0.0. Its methods have been intended to parse javadoc tags. A more useful solution will be added
    around the AST node `FormalComment`, which contains as children `JavadocElement` nodes, which in
    turn provide access to the `JavadocTag`.

    All comment AST nodes (`FormalComment`, `MultiLineComment`, `SingleLineComment`) have a new method
    `getFilteredComment()` which provide access to the comment text without the leading `/*` markers.

*   The method `AbstractCommentRule.tagsIndicesIn()` has been deprecated and will be removed with
    PMD 7.0.0. It is not very useful, since it doesn't extract the information
    in a useful way. You would still need check, which tags have been found, and with which
    data they might be accompanied.

#### 6.4.0

* The following classes in package `net.sourceforge.pmd.benchmark` have been deprecated: `Benchmark`, `Benchmarker`,
  `BenchmarkReport`, `BenchmarkResult`, `RuleDuration`, `StringBuilderCR` and `TextReport`. Their API is not supported anymore
  and is disconnected from the internals of PMD. Use the newer API based around `TimeTracker` instead, which can be found
  in the same package.
* The class `net.sourceforge.pmd.lang.java.xpath.TypeOfFunction` has been deprecated. Use the newer `TypeIsFunction` in the same package.
* The `typeof` methods in `net.sourceforge.pmd.lang.java.xpath.JavaFunctions` have been deprecated.
  Use the newer `typeIs` method in the same class instead..
* The methods `isA`, `isEither` and `isNeither` of `net.sourceforge.pmd.lang.java.typeresolution.TypeHelper`.
  Use the new `isExactlyAny` and `isExactlyNone` methods in the same class instead.

#### 6.2.0

*   The static method `PMDParameters.transformParametersIntoConfiguration(PMDParameters)` is now deprecated,
    for removal in 7.0.0. The new instance method `PMDParameters.toConfiguration()` replaces it.

*   The method `ASTConstructorDeclaration.getParameters()` has been deprecated in favor of the new method
    `getFormalParameters()`. This method is available for both `ASTConstructorDeclaration` and
    `ASTMethodDeclaration`.

#### 6.1.0

* The method `getXPathNodeName` is added to the `Node` interface, which removes the
use of the `toString` of a node to get its XPath element name (see [#569](https://github.com/pmd/pmd/issues/569)).
  * The default implementation provided in  `AbstractNode`, will
  be removed with 7.0.0
  * With 7.0.0, the `Node.toString` method will not necessarily provide its XPath node
  name anymore.

* The interface `net.sourceforge.pmd.cpd.Renderer` has been deprecated. A new interface
`net.sourceforge.pmd.cpd.renderer.CPDRenderer` has been introduced to replace it. The main
difference is that the new interface is meant to render directly to a `java.io.Writer`
rather than to a String. This allows to greatly reduce the memory footprint of CPD, as on
large projects, with many duplications, it was causing `OutOfMemoryError`s (see [#795](https://github.com/pmd/pmd/issues/795)).

  `net.sourceforge.pmd.cpd.FileReporter` has also been deprecated as part of this change, as it's no longer needed.

#### 6.0.1

*   The constant `net.sourceforge.pmd.PMD.VERSION` has been deprecated and will be removed with PMD 7.0.0.
    Please use `net.sourceforge.pmd.PMDVersion.VERSION` instead.

### List of currently deprecated rules

*   The Java rules {% rule java/codestyle/VariableNamingConventions %}, {% rule java/codestyle/MIsLeadingVariableName %},
    {% rule java/codestyle/SuspiciousConstantFieldName %}, and {% rule java/codestyle/AvoidPrefixingMethodParameters %} are
    now deprecated, and will be removed with version 7.0.0. They are replaced by the more general
    {% rule java/codestyle/FieldNamingConventions %}, {% rule java/codestyle/FormalParameterNamingConventions %}, and
    {% rule java/codestyle/LocalVariableNamingConventions %}.

*   The Java rule {% rule java/codestyle/AbstractNaming %} is deprecated
    in favour of {% rule java/codestyle/ClassNamingConventions %}.

*   The Java rules {% rule java/codestyle/WhileLoopsMustUseBraces %}, {% rule java/codestyle/ForLoopsMustUseBraces %}, {% rule java/codestyle/IfStmtsMustUseBraces %}, and {% rule java/codestyle/IfElseStmtsMustUseBraces %}
    are deprecated. They will be replaced by the new rule {% rule java/codestyle/ControlStatementBraces %}.

*   The Java rules {% rule java/design/NcssConstructorCount %}, {% rule java/design/NcssMethodCount %}, and {% rule java/design/NcssTypeCount %} have been
    deprecated. They will be replaced by the new rule {% rule java/design/NcssCount %} in the category `design`.

*   The Java rule `LooseCoupling` in ruleset `java-typeresolution` is deprecated. Use the rule with the same name from category `bestpractices` instead.

*   The Java rule `CloneMethodMustImplementCloneable` in ruleset `java-typeresolution` is deprecated. Use the rule with the same name from category `errorprone` instead.

*   The Java rule `UnusedImports` in ruleset `java-typeresolution` is deprecated. Use the rule with
    the same name from category `bestpractices` instead.

*   The Java rule `SignatureDeclareThrowsException` in ruleset `java-typeresolution` is deprecated. Use the rule with the same name from category `design` instead.

*   The Java rule `EmptyStaticInitializer` in ruleset `java-empty` is deprecated. Use the rule {% rule java/errorprone/EmptyInitializer %}, which covers both static and non-static empty initializers.`

*   The Java rules `GuardDebugLogging` (ruleset `java-logging-jakarta-commons`) and `GuardLogStatementJavaUtil`
    (ruleset `java-logging-java`) have been deprecated. Use the rule {% rule java/bestpractices/GuardLogStatement %}, which covers all cases regardless of the logging framework.

*   The Java rule {% rule "java/multithreading/UnsynchronizedStaticDateFormatter" %} has been deprecated and
    will be removed with PMD 7.0.0. The rule is replaced by the more general
    {% rule "java/multithreading/UnsynchronizedStaticFormatter" %}.












