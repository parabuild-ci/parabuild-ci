<?xml version="1.0" encoding="utf-8" ?>
<xsl:transform version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:test="http://microsoft.com/schemas/VisualStudio/TeamTest/2006">
    <xsl:output method="html"/>

    <xsl:template match="/">      
      <xsl:apply-templates select="cruisecontrol/build/test:TestRun" />
    </xsl:template>
  
    <xsl:template match="test:TestRun">
      <xsl:variable name="outcome" select="test:ResultSummary/@outcome" />
      <xsl:variable name="summary" select="test:ResultSummary/test:Counters" />
      <xsl:variable name="total" select="$summary/@total" />
      <xsl:variable name="passed" select="$summary/@passed" />
      <xsl:variable name="failingTests" select="test:Results/test:UnitTestResult[@outcome != 'Passed']" />
        <table class="section-table" width="100%">
            <tr>
                <td class="sectionheader">
                  Tests
                  <xsl:choose>
                    <xsl:when test="$total = $passed">
                      <span color="darkgreen">SUCCESS</span>
                    </xsl:when>
                    <xsl:otherwise>
                      <span color="red">FAILED</span> (<xsl:value-of select="$outcome"/>)
                    </xsl:otherwise>
                  </xsl:choose>:                  
                      <xsl:value-of select="$total"/> tests,
                      <xsl:value-of select="$passed"/> passed,
                  (<xsl:call-template name="format-percent">
                    <xsl:with-param name="value" select="$passed" />
                    <xsl:with-param name="total" select="$total" />
                  </xsl:call-template> passing)
                </td>
            </tr>
          <xsl:if test="$failingTests">
            <tr>
              <td>(to review the details of this run, import the .trx file into Visual Studio)</td>
            </tr>
            <tr>
                <td>
                    <table width="100%">
                        <tr>
                            <th align="left">Name</th>
                            <th align="left">Outcome</th>
                            <th align="left">Duration</th>
                        </tr>
                        <xsl:for-each select="test:Results/test:UnitTestResult[@outcome != 'Passed']">
                            <tr>
                              <td><xsl:value-of select="@testName"/></td>
                              <td><xsl:value-of select="@outcome"/></td>
                              <td><xsl:value-of select="@duration"/></td>
                           </tr>
                        </xsl:for-each>
                    </table>
                </td>
            </tr>
          </xsl:if>
        </table>
    </xsl:template>

  <xsl:template name="format-percent">
    <xsl:param name="value" />
    <xsl:param name="total"/>
    <xsl:choose>
      <xsl:when test="$total = 0">0 %</xsl:when>
      <xsl:otherwise><xsl:value-of select="format-number(($value div $total) * 100.0, '0.00')" /> %</xsl:otherwise>
    </xsl:choose>
  </xsl:template>  
</xsl:transform>
