<?xml version="1.0" encoding="ISO-8859-1" ?>
<!--

Appends after last first-level element of document at URL $main all first-level elements of input document .

Copyright J.M. Vanel 2001 - under GNU public licence
jmvanel@free.fr

Worldwide Botanical Knowledge Base
http://wwbota.free.fr/


  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output indent="yes" doctype-public="-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN" doctype-system="http://java.sun.com/dtd/web-app_2_3.dtd"/>
<!--  <xsl:output indent="yes"/>-->

  <xsl:param name="main"/>
  <xsl:variable name="main_doc" select="document($main)"/>
  <xsl:variable name="append" select="/"/>
  <xsl:template match="/">
    <xsl:for-each select="$main_doc/*">
      <xsl:copy>
        <xsl:copy-of select="$main_doc/*/*"/>
        <xsl:copy-of select="$append/*/*"/>
      </xsl:copy>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>