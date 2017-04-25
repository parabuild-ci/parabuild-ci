<?xml version="1.0"?><!--
  XSL template to create partial HTML to display JUnit log
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" indent="no" encoding="US-ASCII"/>
  <xsl:decimal-format decimal-separator="." grouping-separator=","/>

  <!-- appearance attributes -->
  <xsl:param name="h1.style" select="'margin: 0px 0px 5px; font: 11px verdana,arial,helvetica'"/>
  <xsl:param name="h2.style" select="'margin-top: 1em; margin-bottom: 0.5em; font: bold 11px verdana,arial,helvetica'"/>
  <xsl:param name="testsuite.style" select="'background-color:#FFFFFF; margin-bottom: 0.5em; font: bold 12px verdana,arial,helvetica'"/>
  <xsl:param name="p.style" select="'line-height:1.5em; margin-top:0.5em; margin-bottom:1.0em;'"/>
  <xsl:param name="error.style" select="'font-size: 11px; background-color:#F8C4C2;'"/>
  <xsl:param name="failure.style" select="'font-size: 11px; background-color:#EEE0B7;'"/>
  <xsl:param name="pass.style" select="'font-size: 11px; background-color:#FFFFFF;'"/>
  <xsl:param name="body.style" select="'color:#000000; font:normal 11px verdana,arial,helvetica; '"/>
  <xsl:param name="details.style" select="'font-size: 11px; font-weight: normal; text-align:left; background:#E3E3E3;'"/>
  <xsl:param name="standard.style" select="'font-size: 11px;'"/>

  <xsl:template match="testsuites">
    <div style="{$body.style}">
      <xsl:call-template name="summary"/>
      <xsl:call-template name="classes"/>
    </div>
  </xsl:template>


  <xsl:template name="summary">
    <!--<h2 style="{$h1.style}">Summary</h2>-->
    <xsl:variable name="testCount" select="sum(testsuite/@tests)"/>
    <xsl:variable name="errorCount" select="sum(testsuite/@errors)"/>
    <xsl:variable name="failureCount" select="sum(testsuite/@failures)"/>
    <xsl:variable name="timeCount" select="sum(testsuite/@time)"/>
    <xsl:variable name="successRate" select="($testCount - $failureCount - $errorCount) div $testCount"/>
    <table style="{$details.style}" border="0" cellpadding="5" cellspacing="2" width="100%">
      <tr valign="top" style="{$details.style}">
        <th>Tests</th>
        <th>Failures</th>
        <th>Errors</th>
        <th>Success rate</th>
        <th>Time</th>
      </tr>
      <tr valign="top">
        <xsl:attribute name="style">
          <xsl:choose>
            <xsl:when test="$failureCount &gt; 0">
              <xsl:value-of select="$failure.style"/>
            </xsl:when>
            <xsl:when test="$errorCount &gt; 0">
              <xsl:value-of select="$error.style"/>
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
          <xsl:value-of select="$errorCount"/>
        </td>
        <td>
          <xsl:call-template name="display-percent">
            <xsl:with-param name="value" select="$successRate"/>
          </xsl:call-template>
        </td>
        <td>
          <xsl:call-template name="display-time">
            <xsl:with-param name="value" select="$timeCount"/>
          </xsl:call-template>
        </td>
      </tr>
    </table>
  </xsl:template>


  <!--
    Shows all classes
  -->
  <xsl:template name="classes">
    <xsl:for-each select="testsuite">
      <!-- Show testsuite name as as header -->
      <h3 style="{$testsuite.style}">
        <xsl:value-of select="@name"/>
      </h3>

      <table style="{$details.style}" border="0" cellpadding="5" cellspacing="2" width="100%">
        <tr valign="top" style="{$details.style}">
          <th>Name</th>
          <th>Status</th>
          <th width="80%">Message</th>
          <th nowrap="nowrap">Time,
            <br/>
            seconds
          </th>
        </tr>
        <xsl:if test="./error">
          <tr style="{$error.style}">
            <td colspan="4">
              <xsl:apply-templates select="./error"/>
            </td>
          </tr>
        </xsl:if>
        <xsl:apply-templates select="./testcase"/>
      </table>
      <p style="{$p.style}"/>
    </xsl:for-each>
  </xsl:template>


  <xsl:template match="testsuite">
    <tr valign="top">
      <xsl:attribute name="style">
        <xsl:choose>
          <xsl:when test="@failures[.&gt; 0]">
            <xsl:value-of select="$failure.style"/>
          </xsl:when>
          <xsl:when test="@errors[.&gt; 0]">
            <xsl:value-of select="$error.style"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$pass.style"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <!-- print testsuite information -->
      <td>
        <xsl:value-of select="@tests"/>
      </td>
      <td>
        <xsl:value-of select="@errors"/>
      </td>
      <td>
        <xsl:value-of select="@failures"/>
      </td>
      <td>
        <xsl:call-template name="display-time">
          <xsl:with-param name="value" select="@time"/>
        </xsl:call-template>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="testcase">
    <tr valign="top" style="{$details.style}">
      <xsl:attribute name="style">
        <xsl:choose>
          <xsl:when test="error">
            <xsl:value-of select="$error.style"/>
          </xsl:when>
          <xsl:when test="failure">
            <xsl:value-of select="$failure.style"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$pass.style"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <td>
        <xsl:value-of select="@name"/>
      </td>
      <xsl:choose>
        <xsl:when test="failure">
          <td>Failure</td>
          <td>
            <xsl:apply-templates select="failure"/>
          </td>
        </xsl:when>
        <xsl:when test="error">
          <td>Error</td>
          <td>
            <xsl:apply-templates select="error"/>
          </td>
        </xsl:when>
        <xsl:otherwise>
          <td>Success</td>
          <td></td>
        </xsl:otherwise>
      </xsl:choose>
      <td>
        <xsl:call-template name="display-time">
          <xsl:with-param name="value" select="@time"/>
        </xsl:call-template>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="failure">
    <xsl:call-template name="display-failures"/>
  </xsl:template>

  <xsl:template match="error">
    <xsl:call-template name="display-failures"/>
  </xsl:template>

  <xsl:template name="display-failures">
    <xsl:choose>
      <xsl:when test="not(@message)">N/A</xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="@message"/>
      </xsl:otherwise>
    </xsl:choose>
    <!-- display the stacktrace -->
    <code>
      <p style="{$p.style}"/>
      <xsl:call-template name="br-replace">
        <xsl:with-param name="word" select="."/>
      </xsl:call-template>
    </code>
  </xsl:template>

  <xsl:template name="br-replace">
    <xsl:param name="word"/>
    <xsl:choose>
      <xsl:when test="contains($word,'&#xA;')">
        <xsl:value-of select="substring-before($word,'&#xA;')"/>
        <br/>
        <xsl:call-template name="br-replace">
          <xsl:with-param name="word" select="substring-after($word,'&#xA;')"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$word"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="display-time">
    <xsl:param name="value"/>
    <xsl:value-of select="format-number($value,'0.000')"/>
  </xsl:template>

  <xsl:template name="display-percent">
    <xsl:param name="value"/>
    <xsl:value-of select="format-number($value,'0%')"/>
  </xsl:template>

</xsl:stylesheet>

