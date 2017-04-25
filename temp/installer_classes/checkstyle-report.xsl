<?xml version="1.0"?><!--
  XSL template to create partial HTML to display PMD log
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" indent="no" encoding="US-ASCII"/>
  <xsl:decimal-format decimal-separator="." grouping-separator=","/>
  <xsl:param name="body.style" select="'color:#000000; font:normal 12px verdana,arial,helvetica; width: 100%'"/>
  <xsl:param name="details.style" select="'font-size: 12px; font-weight: normal; text-align:left; background:#E3E3E3;'"/>
  <xsl:param name="h1.style" select="'margin-top: 1em; margin-bottom: 0.5em; font: bold 12px verdana,arial,helvetica'"/>
  <xsl:param name="error.style" select="'font-size: 12px; background-color:#EEE0B7;'"/>
  <xsl:param name="pass.style" select="'font-size: 12px; background-color:#FFFFFF;'"/>
  <xsl:param name="h1.style.failed" select="'margin-top: 1em; margin-bottom: 0.5em; font: bold 12px verdana,arial,helvetica; color:#FF0000;'"/>
  <xsl:param name="fileRowStyle" select="'background-color:#FFFFFF; font-weight: bold; font-size: 11px'"/>
  <xsl:param name="oddRowStyle" select="'background-color:#FFFFF0;'"/>
  <xsl:param name="evenRowStyle" select="'background-color:#FFFFFF;'"/>

  <xsl:variable name="fileCount" select="count(/checkstyle/file)"/>
  <xsl:variable name="errors" select="checkstyle/file[count(error) > 0]"/>
  <xsl:variable name="errorCount" select="count(checkstyle/file/error)"/>

  <xsl:template match="checkstyle">
    <div style="{$body.style}">

      <!--
        Show summary
      -->
      <h1 style="{$h1.style}">Checkstyle Summary</h1>
      <table style="{$details.style}" border="0" cellpadding="5" cellspacing="2" width="100%">
        <tr valign="top" style="{$details.style}">
          <th>Files</th>
          <th>Errors</th>
        </tr>
        <tr valign="top">
          <xsl:attribute name="style">
            <xsl:choose>
              <xsl:when test="$errorCount &gt; 0">
                <xsl:value-of select="$error.style"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="$pass.style"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
          <td>
            <xsl:value-of select="$fileCount"/>
          </td>
          <td>
            <xsl:value-of select="$errorCount"/>
          </td>
        </tr>
      </table>

      <!--
        Go over errors
      -->
      <xsl:if test="$errorCount > 0">
        <h1 style="{$h1.style.failed}">Checkstyle Errors</h1>
        <table style="{$details.style}" border="0" cellpadding="5" cellspacing="3" width="100%">
          <tr valign="top" style="{$details.style}">
            <th>Line</th>
            <th>Message</th>
            <th>Severity</th>
          </tr>
          <xsl:apply-templates select="$errors"/>
        </table>
      </xsl:if>
    </div>
  </xsl:template>

  <xsl:template match="file">
    <tr style="{$fileRowStyle}">
      <td colspan="3">
        <xsl:value-of select="@name"/>
      </td>
    </tr>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="error">
    <tr>
      <xsl:attribute name="style">
        <xsl:choose>
          <xsl:when test="position() mod 2 = 0">
            <xsl:value-of select="$oddRowStyle"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$evenRowStyle"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <td align="right" width="50">
        <xsl:value-of select="@line"/>
      </td>
      <td>
        <xsl:value-of select="@message"/>
      </td>
      <td>
        <xsl:value-of select="@severity"/>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>

