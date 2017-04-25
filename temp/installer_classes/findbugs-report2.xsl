<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>

  <xsl:template match="/" mode="findbugs">
    <xsl:apply-templates select="cruisecontrol/BugCollection" mode="findbugs"/>
  </xsl:template>

  <xsl:template match="BugCollection" mode="findbugs">
    <xsl:variable name="foundbugs.count" select="count(BugInstance)"/>
    <table cellspacing="0" cellpadding="2" border="0" width="100%">
      <tr>
        <td colspan="3">FindBugs bugs:</td>
        <td colspan="2">
          <xsl:value-of select="$foundbugs.count"/>
        </td>
      </tr>
      <xsl:for-each select="BugInstance/LongMessage">
        <tr>
          <!-- Select row color -->
          <xsl:choose>
            <xsl:when test="position() mod 2 = 1">
              <xsl:attribute name="style">background-color: white; border-width: 1</xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute name="style">background-color: yellow; border-width: 1</xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
          <td>
            <xsl:value-of select="../Class/ #&lt;at#&gt; classname"/>
          </td>
          <td>
            <xsl:value-of select="../SourceLine/ #&lt;at&gt; start"/>
          </td>
          <td>
            <xsl:value-of select="substring-after(.,':')"/>
          </td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template match="/">
    <xsl:apply-templates select="." mode="findbugs"/>
  </xsl:template>
</xsl:stylesheet>
