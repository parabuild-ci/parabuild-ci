<?xml version="1.0"?><!--
  XSL template to create partial HTML to display JUnit log
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" indent="no" encoding="US-ASCII"/>
  <xsl:decimal-format decimal-separator="." grouping-separator=","/>

  <!-- appearance attributes -->
  <xsl:param name="a.style" select="'font-size: 12px; color:blue'"/>
  <xsl:param name="body.style" select="'color:#000000; font:normal 12px verdana,arial,helvetica; width: 100%'"/>
  <xsl:param name="details.style" select="'font-size: 12px; font-weight: normal; text-align:left; background:#E3E3E3;'"/>
  <xsl:param name="error.style" select="'font-size: 12px; background-color:#F8C4C2;'"/>
  <xsl:param name="failure.style" select="'font-size: 12px; background-color:#EEE0B7;'"/>
  <xsl:param name="h1.style" select="'margin-top: 1em; margin-bottom: 0.5em; font: bold 12px verdana,arial,helvetica'"/>
  <xsl:param name="h1.style.failed" select="'margin-top: 1em; margin-bottom: 0.5em; font: bold 12px verdana,arial,helvetica; color:#FF0000;'"/>
  <xsl:param name="h2.style" select="'margin-top: 1em; margin-bottom: 0.5em; font: bold 12px verdana,arial,helvetica'"/>
  <xsl:param name="pass.style" select="'font-size: 12px; background-color:#FFFFFF;'"/>
  <xsl:param name="standard.style" select="'font-size: 12px;'"/>
  <xsl:param name="testsuite.style" select="'background-color:#FFFFFF; margin-bottom: 0.5em; font: bold 12px verdana,arial,helvetica'"/>
  <xsl:param name="oddRowStyle" select="'background-color:#FFFFF0;'"/>
  <xsl:param name="evenRowStyle" select="'background-color:#FFFFFF;'"/>
  <xsl:param name="messageStyle" select="'position: relative; left: 20px;'"/>

  <xsl:variable name="fatals" select="/SquishReport/summary/@fatals"/>
  <xsl:variable name="testcases" select="/SquishReport/summary/@testcases"/>
  <xsl:variable name="expected_fails" select="/SquishReport/summary/@expected_fails"/>
  <xsl:variable name="unexpected_passes" select="/SquishReport/summary/@unexpected_passes"/>
  <xsl:variable name="warnings" select="/SquishReport/summary/@warnings"/>
  <xsl:variable name="tests" select="/SquishReport/summary/@tests"/>
  <xsl:variable name="errors" select="/SquishReport/summary/@errors"/>
  <xsl:variable name="fails" select="/SquishReport/summary/@fails"/>
  <xsl:variable name="passes" select="/SquishReport/summary/@passes"/>

  <xsl:template match="/SquishReport">
    <div style="{$body.style}">
      <xsl:call-template name="summary"/>
      <p></p>
      <xsl:call-template name="testResults"/>
    </div>
  </xsl:template>


  <xsl:template name="summary">
    <h1 style="{$h1.style}">Summary</h1>
    <table style="{$details.style}" border="0" cellpadding="5" cellspacing="2" width="100%">
      <tr valign="top" style="{$details.style}">
        <th>Fatals</th>
        <th>Testcases</th>
        <th>Expected fails</th>
        <th>Unexpected passes</th>
        <th>Warnings</th>
        <th>Tests</th>
        <th>Errors</th>
        <th>Fails</th>
        <th>Passes</th>
      </tr>
      <tr valign="top">
        <xsl:attribute name="style">
          <xsl:choose>
            <xsl:when test="($fatals + $errors + $fails) &gt; 0">
              <xsl:value-of select="$failure.style"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$pass.style"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <td>
          <xsl:value-of select="$fatals"/>
        </td>
        <td>
          <xsl:value-of select="$testcases"/>
        </td>
        <td>
          <xsl:value-of select="$expected_fails"/>
        </td>
        <td>
          <xsl:value-of select="$unexpected_passes"/>
        </td>
        <td>
          <xsl:value-of select="$warnings"/>
        </td>
        <td>
          <xsl:value-of select="$tests"/>
        </td>
        <td>
          <xsl:value-of select="$errors"/>
        </td>
        <td>
          <xsl:value-of select="$fails"/>
        </td>
        <td>
          <xsl:value-of select="$passes"/>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="testResults">
    <table style="{$details.style}" border="0" cellpadding="5" cellspacing="2" width="100%">
      <tr style="{$details.style}">
        <th>Line</th>
        <th>Message</th>
        <th>Result</th>
        <th>Time</th>
        <th>Description</th>
      </tr>
      <xsl:apply-templates select="testresult"/>
    </table>
  </xsl:template>


  <xsl:template match="testresult">
    <tr>
      <xsl:attribute name="style">
        <xsl:choose>
          <xsl:when test="@result='FAIL' or @result='FATAL' or @result='ERROR'">
            <xsl:value-of select="$failure.style"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$pass.style"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <td height="30">
        <xsl:value-of select="@line"/>
      </td>
      <td height="30">
        <xsl:value-of select="@message"/>
      </td>
      <td height="30">
        <xsl:value-of select="@result"/>
      </td>
      <td height="30">
        <xsl:value-of select="@time"/>
      </td>
      <td height="30">
        <xsl:value-of select="."/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="*">
  </xsl:template>


</xsl:stylesheet>
