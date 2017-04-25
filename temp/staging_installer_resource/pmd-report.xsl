<?xml version="1.0"?>
<!--
  XSL template to create partial HTML to display PMD log
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" indent="no" encoding="US-ASCII"/>
  <xsl:decimal-format decimal-separator="." grouping-separator=","/>
  <xsl:param name="details.style" select="'background-color:#FFFFFF; font: normal 12px verdana,arial,helvetica'"/>

  <xsl:template match="pmd">
    <table style="{$details.style}" border="0" cellpadding="5" cellspacing="3" width="100%">
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="file">
    <tr style="background-color:#EFEFEF;">
      <td colspan="2">
        <xsl:value-of select="@name"/>
      </td>
      <xsl:apply-templates/>
    </tr>
  </xsl:template>

  <xsl:template match="violation">
    <tr style="background-color:#FFFFF0;">
      <td align="right">
        <xsl:value-of select="@line"/>
      </td>
      <td>
        <xsl:value-of select="."/> (<xsl:value-of select="@rule"/>)
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>

