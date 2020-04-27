---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### PMD adopts Contributor Code of Conduct

To facilitate healthy and constructive community behavior PMD adopts
[Contributor Convenant](https://www.contributor-covenant.org/) as its code of
conduct.

Please note that this project is released with a Contributor Code of Conduct.
By participating in this project you agree to abide by its terms.

You can find the code of conduct in the file [code_of_conduct.md](https://github.com/pmd/pmd/blob/master/code_of_conduct.md)
in our repository.

#### Performance improvements for XPath 2.0 rules

XPath rules written with XPath 2.0 now support conversion to a rulechain rule, which
improves their performance. The rulechain is a mechanism that allows several rules
to be executed in a single tree traversal. Conversion to the rulechain is possible if
your XPath expression looks like `//someNode/... | //someOtherNode/...  | ...`, that
is, a union of one or more path expressions that start with `//`. Instead of traversing
the whole tree once per path expression (and per rule), a single traversal executes all
rules in your ruleset as needed.

This conversion is performed automatically and cannot be disabled. *The conversion should
not change the result of your rules*, if it does, please report a bug at https://github.com/pmd/pmd/issues

Note that XPath 1.0 support, the default XPath version, is deprecated since PMD 6.22.0.
**We highly recommend that you upgrade your rules to XPath 2.0**. Please refer to the [migration guide](https://pmd.github.io/latest/pmd_userdocs_extending_writing_xpath_rules.html#migrating-from-10-to-20).

#### Javascript improvements for ES6

PMD uses the [Rhino](https://github.com/mozilla/rhino) library to parse Javascript.
The default version has been set to `ES6`, so that some ECMAScript 2015 features are
supported. E.g. `let` statements and `for-of` loops are now parsed. However Rhino does
not support all features.

#### New JSON renderer

PMD now supports a JSON renderer (use it with `-f json` on the CLI).
See [the documentation and example](https://pmd.github.io/latest/pmd_userdocs_report_formats.html#json)

#### New Rules

*   The new Apex rule {% rule "apex/codestyle/FieldDeclarationsShouldBeAtStart" %} (`apex-codestyle`)
    helps to ensure that field declarations are always at the beginning of a class.

*   The new Apex rule {% rule "apex/bestpractices/UnusedLocalVariable" %} (`apex-bestpractices`) detects unused
    local variables.

### Fixed Issues

*   apex-design
    *   [#2358](https://github.com/pmd/pmd/issues/2358): \[apex] Invalid Apex in Cognitive Complexity tests
*   apex-security
    *   [#2210](https://github.com/pmd/pmd/issues/2210): \[apex] ApexCRUDViolation: Support WITH SECURITY_ENFORCED
    *   [#2399](https://github.com/pmd/pmd/issues/2399): \[apex] ApexCRUDViolation: false positive with security enforced with line break
*   core
    *   [#1286](https://github.com/pmd/pmd/issues/1286): \[core] Export Supporting JSON Format
    *   [#2019](https://github.com/pmd/pmd/issues/2019): \[core] Insufficient deprecation warnings for XPath attributes
    *   [#2357](https://github.com/pmd/pmd/issues/2357): Add code of conduct: Contributor Covenant
    *   [#2426](https://github.com/pmd/pmd/issues/2426): \[core] CodeClimate renderer links are dead
    *   [#2432](https://github.com/pmd/pmd/pull/2432): \[core] Close ZIP data sources even if a runtime exception or error is thrown
*   doc
    *   [#2355](https://github.com/pmd/pmd/issues/2355): \[doc] Improve documentation about incremental analysis
    *   [#2356](https://github.com/pmd/pmd/issues/2356): \[doc] Add missing doc about pmd.github.io
    *   [#2412](https://github.com/pmd/pmd/issues/2412): \[core] HTMLRenderer doesn't render links to source files
    *   [#2413](https://github.com/pmd/pmd/issues/2413): \[doc] Improve documentation about the available renderers (PMD/CPD)
*   java
    *   [#2378](https://github.com/pmd/pmd/issues/2378): \[java] AbstractJUnitRule has bad performance on large code bases
*   java-bestpractices
    *   [#2398](https://github.com/pmd/pmd/issues/2398): \[java] AbstractClassWithoutAbstractMethod false negative with inner abstract classes
*   java-codestyle
    *   [#1164](https://github.com/pmd/pmd/issues/1164): \[java] ClassNamingConventions suggests to add Util for class containing only static constants
    *   [#1723](https://github.com/pmd/pmd/issues/1723): \[java] UseDiamondOperator false-positive inside lambda
*   java-design
    *   [#2390](https://github.com/pmd/pmd/issues/2390): \[java] AbstractClassWithoutAnyMethod: missing violation for nested classes
*   java-errorprone
    *   [#2402](https://github.com/pmd/pmd/issues/2402): \[java] CloseResource possible false positive with Primitive Streams
*   java-multithreading
    *   [#2313](https://github.com/pmd/pmd/issues/2313): \[java] Documenation for DoNotUseThreads is outdated
*   javascript
    *   [#1235](https://github.com/pmd/pmd/issues/1235): \[javascript] Use of let results in an Empty Statement in the AST
    *   [#2379](https://github.com/pmd/pmd/issues/2379): \[javascript] Support for-of loop
*   javascript-errorprone
    *   [#384](https://github.com/pmd/pmd/issues/384): \[javascript] Trailing commas not detected on French default locale

### API Changes

#### Deprecated APIs

##### Internal API

Those APIs are not intended to be used by clients, and will be hidden or removed with PMD 7.0.0.
You can identify them with the `@InternalApi` annotation. You'll also get a deprecation warning.

*   {% jdoc core::lang.rule.xpath.AbstractXPathRuleQuery %}
*   {% jdoc core::lang.rule.xpath.JaxenXPathRuleQuery %}
*   {% jdoc core::lang.rule.xpath.SaxonXPathRuleQuery %}
*   {% jdoc core::lang.rule.xpath.XPathRuleQuery %}

##### In ASTs

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

##### For removal

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

### External Contributions

*   [#2312](https://github.com/pmd/pmd/pull/2312): \[apex] Update ApexCRUDViolation Rule - [Joshua S Arquilevich](https://github.com/jarquile)
*   [#2314](https://github.com/pmd/pmd/pull/2314): \[doc] maven integration - Add version to plugin - [Pham Hai Trung](https://github.com/gpbp)
*   [#2353](https://github.com/pmd/pmd/pull/2353): \[plsql] xmlforest with optional AS - [Piotr Szymanski](https://github.com/szyman23)
*   [#2383](https://github.com/pmd/pmd/pull/2383): \[apex] Fix invalid apex in documentation - [Gwilym Kuiper](https://github.com/gwilymatgearset)
*   [#2395](https://github.com/pmd/pmd/pull/2395): \[apex] New Rule: Unused local variables - [Gwilym Kuiper](https://github.com/gwilymatgearset)
*   [#2396](https://github.com/pmd/pmd/pull/2396): \[apex] New rule: field declarations should be at start - [Gwilym Kuiper](https://github.com/gwilymatgearset)
*   [#2397](https://github.com/pmd/pmd/pull/2397): \[apex] fixed WITH SECURITY_ENFORCED regex to recognise line break characters - [Kieran Black](https://github.com/kieranlblack)
*   [#2401](https://github.com/pmd/pmd/pull/2401): \[doc] Update DoNotUseThreads rule documentation - [Saikat Sengupta](https://github.com/s4ik4t)
*   [#2403](https://github.com/pmd/pmd/pull/2403): \[java] #2402 fix false-positives on Primitive Streams - [Bernd Farka](https://github.com/BerndFarkaDyna)
*   [#2409](https://github.com/pmd/pmd/pull/2409): \[java] ClassNamingConventions suggests to add Util for class containing only static constants, fixes #1164 - [Binu R J](https://github.com/binu-r)
*   [#2411](https://github.com/pmd/pmd/pull/2411): \[java] Fix UseAssertEqualsInsteadOfAssertTrue Example - [Moritz Scheve](https://github.com/Blightbuster)
*   [#2423](https://github.com/pmd/pmd/pull/2423): \[core] Fix Checkstyle OperatorWrap in AbstractTokenizer - [Harsh Kukreja](https://github.com/harsh-kukreja)
*   [#2432](https://github.com/pmd/pmd/pull/2432): \[core] Close ZIP data sources even if a runtime exception or error is thrown - [Gonzalo Exequiel Ibars Ingman](https://github.com/gibarsin)

### Stats
* 237 commits
* 64 closed tickets & PRs
* Days since last release: 42

{% endtocmaker %}

