<?xml version="1.0"?><!--
  XSL template to create partial HTML to display JUnit log
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" indent="no" encoding="US-ASCII"/>
  <xsl:decimal-format decimal-separator="." grouping-separator=","/>

  <!-- appearance attributes -->
  <xsl:param name="a.style" select="'font-size: 12px; color:blue'"/>
  <xsl:param name="body.style" select="'color:#000000; font:normal 12px verdana,arial,helvetica; width: 100%'"/>
  <xsl:param name="details.style"
             select="'font-size: 12px; font-weight: normal; text-align:left; background:#E3E3E3;'"/>
  <xsl:param name="error.style" select="'font-size: 12px; background-color:#F8C4C2;'"/>
  <xsl:param name="failure.style" select="'font-size: 12px; background-color:#EEE0B7;'"/>
  <xsl:param name="h1.style" select="'margin-top: 1em; margin-bottom: 0.5em; font: bold 12px verdana,arial,helvetica'"/>
  <xsl:param name="h1.style.failed"
             select="'margin-top: 1em; margin-bottom: 0.5em; font: bold 12px verdana,arial,helvetica; color:#FF0000;'"/>
  <xsl:param name="h2.style" select="'margin-top: 1em; margin-bottom: 0.5em; font: bold 12px verdana,arial,helvetica'"/>
  <xsl:param name="pass.style" select="'font-size: 12px; background-color:#FFFFFF;'"/>
  <xsl:param name="standard.style" select="'font-size: 12px;'"/>
  <xsl:param name="testsuite.style"
             select="'background-color:#FFFFFF; margin-bottom: 0.5em; font: bold 12px verdana,arial,helvetica'"/>
  <xsl:param name="oddRowStyle" select="'background-color:#FFFFF0;'"/>
  <xsl:param name="evenRowStyle" select="'background-color:#FFFFFF;'"/>
  <xsl:param name="messageStyle" select="'position: relative; left: 20px;'"/>

  <xsl:variable name="failedTests" select="/testlogs/TestLog/TestSuite/TestCase/Error"/>
  <xsl:variable name="exceptionTests" select="/testlogs/TestLog/TestSuite/TestCase/Exception"/>
  <xsl:variable name="successfulTests" select="/testlogs/TestLog/TestSuite/TestCase/Info"/>
  <xsl:variable name="failureCount" select="count($failedTests)"/>
  <xsl:variable name="exceptionCount" select="count($exceptionTests)"/>
  <xsl:variable name="successCount" select="count($successfulTests)"/>
  <xsl:variable name="testCount" select="$failureCount + $successCount + $exceptionCount"/>
  <xsl:variable name="successRate" select="($testCount - $failureCount - $exceptionCount) div $testCount"/>

  <xsl:template match="testlogs">
    <div style="{$body.style}">
      <xsl:call-template name="summary"/>
      <xsl:call-template name="testSuites"/>
    </div>
  </xsl:template>


  <xsl:template name="summary">
    <h1 style="{$h1.style}">Summary</h1>
    <table style="{$details.style}" border="0" cellpadding="5" cellspacing="2" width="100%">
      <tr valign="top" style="{$details.style}">
        <th>Tests</th>
        <th>Failures</th>
        <th>Success Rate</th>
      </tr>
      <tr valign="top">
        <xsl:attribute name="style">
          <xsl:choose>
            <xsl:when test="$failureCount &gt; 0">
              <xsl:value-of select="$failure.style"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$pass.style"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <td>
          <xsl:value-of select="$testCount"/>
        </td>
        <td>
          <xsl:value-of select="$failureCount"/>
        </td>
        <td>
          <xsl:call-template name="display-percent">
            <xsl:with-param name="value" select="$successRate"/>
          </xsl:call-template>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="testSuites">
    <xsl:apply-templates select="/testlogs/TestLog/TestSuite"/>
  </xsl:template>


  <xsl:template match="TestSuite">
    <h3 style="{$testsuite.style}">
      <xsl:value-of select="@name"/>
    </h3>
    <table style="{$details.style}" border="0" cellpadding="5" cellspacing="2" width="100%">
      <tr style="{$details.style}">
        <th>File and Line</th>
        <th>Status</th>
      </tr>
      <xsl:apply-templates select="TestCase"/>
    </table>
  </xsl:template>


  <xsl:template match="TestCase">
    <tr>
      <td height="30" style="font-weight: bold;">
        <xsl:value-of select="@name"/>
      </td>
      <td height="30">
        Testing time:
        <xsl:value-of select="TestingTime"/>
      </td>
    </tr>
    <xsl:apply-templates/>
  </xsl:template>


  <xsl:template match="Info">
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
      <td>
        <xsl:value-of select="@file"/>
        (
        <xsl:value-of select="@line"/>
        ) :
        <xsl:value-of select="."/>
      </td>
      <td>
        Info
      </td>
    </tr>
  </xsl:template>


  <xsl:template match="Message">
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
      <td>
        <xsl:value-of select="@file"/>
        (
        <xsl:value-of select="@line"/>
        ) :
        <xsl:value-of select="."/>
      </td>
      <td>
        Message
      </td>
    </tr>
  </xsl:template>


  <xsl:template match="Error">
    <tr style="{$error.style}">
      <td>
        <xsl:value-of select="@file"/>
        (
        <xsl:value-of select="@line"/>
        ) :
        <xsl:value-of select="."/>
      </td>
      <td>
        Error
      </td>
    </tr>
  </xsl:template>


  <xsl:template match="Exception">
    <tr style="{$error.style}">
      <td>
        <xsl:value-of select="LastCheckpoint/@file"/>
        (
        <xsl:value-of select="LastCheckpoint/@line"/>
        ) :
        <xsl:value-of select="."/>
      </td>
      <td>
        Exception
      </td>
    </tr>
  </xsl:template>


  <xsl:template name="display-time">
    <xsl:param name="value"/>
    <xsl:value-of select="format-number($value,'0.000')"/>
  </xsl:template>


  <xsl:template name="display-percent">
    <xsl:param name="value"/>
    <xsl:value-of select="format-number($value,'0%')"/>
  </xsl:template>

  <xsl:template match="*">
  </xsl:template>


</xsl:stylesheet>
