<?xml version="1.0"?>
<!--
  This is a first cut for the XSL template to create partial HTML to display NUnit log
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
  <xsl:param name="h1.style.successful" select="'margin-top: 1em; margin-bottom: 0.5em; font: bold 12px verdana,arial,helvetica; color:#00FF00;'"/>
  <xsl:param name="h2.style" select="'margin-top: 1em; margin-bottom: 0.5em; font: bold 12px verdana,arial,helvetica'"/>
  <xsl:param name="pass.style" select="'font-size: 12px; background-color:#FFFFFF;'"/>
  <xsl:param name="standard.style" select="'font-size: 12px;'"/>
  <xsl:param name="testsuite.style" select="'background-color:#FFFFFF; margin-bottom: 0.5em; font: bold 12px verdana,arial,helvetica'"/>
  <xsl:param name="oddRowStyle" select="'background-color:#FFFFF0;'"/>
  <xsl:param name="oddRowStyleFailed" select="'background-color:#FFD4D4;'"/>
  <xsl:param name="oddRowStyleSuccessful" select="'background-color:#EAFFEB;'"/>
  <xsl:param name="evenRowStyle" select="'background-color:#FFFFFF;'"/>

  <xsl:variable name="testsRoot" select="nunit-testsuites//test-results"/>
  <xsl:variable name="failedTests" select="$testsRoot//test-case[@executed = 'True' and @success = 'False']"/>
  <xsl:variable name="successfulTests" select="$testsRoot//test-case[@executed = 'True' and @success = 'True']"/>
  <xsl:variable name="skippedTests" select="$testsRoot//test-case[@executed = 'False']"/>

  <xsl:variable name="failureCount" select="count($failedTests)"/>
  <xsl:variable name="successCount" select="count($successfulTests)"/>
  <xsl:variable name="skippedCount" select="count($skippedTests)"/>
  <xsl:variable name="testCount" select="$failureCount + $successCount + $skippedCount"/>
  <xsl:variable name="successRate" select="$successCount div ($successCount + $failureCount)"/>

  <xsl:template match="nunit-testsuites">
    <div style="{$body.style}">
      <xsl:call-template name="summary"/>
      <xsl:call-template name="failures"/>
      <xsl:call-template name="successes"/>
      <xsl:call-template name="skipped"/>
    </div>
  </xsl:template>


  <!--
    Template to show summary
  -->
  <xsl:template name="summary">
    <h1 style="{$h1.style}">Summary</h1>
    <table style="{$details.style}" border="0" cellpadding="5" cellspacing="2" width="100%">
      <tr valign="top" style="{$details.style}">
        <th>Total Tests</th>
        <th>Successes</th>
        <th>Failures</th>
        <th>Skipped</th>
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
          <xsl:value-of select="$successCount"/>
        </td>
        <td>
          <xsl:value-of select="$failureCount"/>
        </td>
        <td>
          <xsl:value-of select="$skippedCount"/>
        </td>
        <td>
          <xsl:call-template name="display-percent">
            <xsl:with-param name="value" select="$successRate"/>
          </xsl:call-template>
        </td>
      </tr>
    </table>
  </xsl:template>


  <!--
    Template to show failures
  -->
  <xsl:template name="failures">
    <xsl:if test="$failureCount > 0">
      <h1 style="{$h1.style.failed}">Failed Tests</h1>
      <table style="{$details.style}" border="0" cellpadding="5" cellspacing="2" width="100%">
        <tr  style="{$details.style}">
          <th>Name</th>
          <th>Message</th>
          <th>Time</th>
        </tr>
        <xsl:apply-templates select="$failedTests" mode="failed"/>
      </table>
    </xsl:if>
  </xsl:template>


  <xsl:template match="test-case" mode="failed">
    <tr>
      <xsl:attribute name="style">
        <xsl:choose>
          <xsl:when test="position() mod 2 = 0">
            <xsl:value-of select="$oddRowStyleFailed"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$evenRowStyle"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <td>
        <xsl:value-of select="@name"/>
      </td>
      <td>
        <xsl:value-of select="failure/message"/><br></br>
        <xsl:value-of select="failure/stack-trace"/>
      </td>
      <td>
        <xsl:value-of select="@time"/>
      </td>
    </tr>
  </xsl:template>

  <!--
    Template to show successful tests
  -->
  <xsl:template name="successes">
    <xsl:if test="$successCount > 0">
      <h1 style="{$h1.style.successful}">Successful Tests</h1>
      <table style="{$details.style}" border="0" cellpadding="5" cellspacing="2" width="100%">
        <tr  style="{$details.style}">
          <th>Name</th>
          <th>Time</th>
        </tr>
        <xsl:apply-templates select="$successfulTests" mode="successful"/>
      </table>
    </xsl:if>
  </xsl:template>


  <xsl:template match="test-case" mode="successful">
    <tr>
      <xsl:attribute name="style">
        <xsl:choose>
          <xsl:when test="position() mod 2 = 0">
            <xsl:value-of select="$oddRowStyleSuccessful"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$evenRowStyle"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <td>
        <xsl:value-of select="@name"/>
      </td>
      <td>
        <xsl:value-of select="@time"/>
      </td>
    </tr>
  </xsl:template>

  <!--
    Template to show skipped tests
  -->
  <xsl:template name="skipped">
    <xsl:if test="$skippedCount > 0">
      <h1 style="{$h1.style}">Skipped Tests</h1>
      <table style="{$details.style}" border="0" cellpadding="5" cellspacing="2" width="100%">
        <tr  style="{$details.style}">
          <th>Name</th>
          <th>Reason</th>
        </tr>
        <xsl:apply-templates select="$skippedTests" mode="skipped"/>
      </table>
    </xsl:if>
  </xsl:template>


  <xsl:template match="test-case" mode="skipped">
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
        <xsl:value-of select="@name"/>
      </td>
      <td>
        <xsl:value-of select="reason/message"/>
      </td>
    </tr>
  </xsl:template>

  <!--
    Helper templates
  -->
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

  <xsl:template name="display-percent">
    <xsl:param name="value"/>
    <xsl:value-of select="format-number($value,'0%')"/>
  </xsl:template>

</xsl:stylesheet>

