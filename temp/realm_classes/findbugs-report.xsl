<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:param name="messagefile" select="'file:///D:/Java/findbugs-0.6.5/etc/messages.xml'"/>
  <xsl:variable name="messagedoc" select="document($messagefile)"/>
  <xsl:template match="/">
    <h2>Summary</h2>
    <table border="0" id="summary" class="details">
      <tr>
        <th colspan="3">Summary</th>
      </tr>
      <tr>
        <th/>
        <th>Count</th>
        <th>Bugs</th>
      </tr>
      <tr>
        <th>Outer Classes :</th>
        <td>
          <xsl:value-of select="count(descendant::AppClass[ not(contains(text(),'$')) and not(@interface) ])"/>
        </td>
        <td>
          <xsl:value-of select="count(descendant::BugInstance/Class[ not(contains(@classname,'$')) and not(@interface) ])"/>
        </td>
      </tr>
      <tr>
        <th>Inner Classes :</th>
        <td>
          <xsl:value-of select="count(descendant::AppClass[contains(text(), '$') and not(@interface) ])"/>
        </td>
        <td>
          <xsl:value-of select="count(descendant::BugInstance/Class[contains(@classname,'$') and not(@interface)])"/>
        </td>
      </tr>
      <tr>
        <th>Interfaces :</th>
        <td>
          <xsl:value-of select="count(descendant::AppClass/@interface)"/>
        </td>
        <td>
          <!--xsl:value-of select="count(descendant::BugInstance/Class[contains(@classname,'$')])"/-->
        </td>
      </tr>
      <tr>
        <th>Total :</th>
        <td>
          <xsl:number level="any" value="count(descendant::AppClass)"/>
        </td>
        <td>
          <xsl:number level="any" value="count(descendant::BugInstance)"/>
        </td>
      </tr>
    </table>
    <h2>Bug Details</h2>
    <table id="bugInstance" class="details" width="100%">
      <xsl:for-each select="//BugInstance">
        <tr>
          <th colspan="2" class="bugClassname">
            <xsl:element name="span">
              <xsl:attribute name="class">priority
                <xsl:value-of select="@priority"/>
              </xsl:attribute>
              <!--xsl:value-of select="@priority"/-->&nbsp;
            </xsl:element>&nbsp;
            <xsl:value-of select="Class/@classname"/>
          </th>
        </tr>
        <tr>
          <th>Type :</th>
          <td>
            <xsl:value-of select="substring-before(@type, '_')"/> -
            <xsl:variable name="type" select="@type"/>
            <xsl:value-of select="$messagedoc//BugPattern[@type = $type]/ShortDescription"/>
          </td>
        </tr>
        <xsl:for-each select="Method">
          <tr>
            <th>Method :</th>
            <td>
              <xsl:value-of select="@name"/>
            </td>
          </tr>
        </xsl:for-each>
        <xsl:if test="Field">
          <tr>
            <th>Field :</th>
            <td>
              <xsl:value-of select="Field/@name"/>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="SourceLine">
          <tr>
            <th>Source :</th>
            <td>
              <xsl:value-of select="SourceLine/@sourcefile"/>
              <xsl:if test="SourceLine[not(contains(@start, '-1'))]">,
                line
                <xsl:value-of select="SourceLine/@start"/>
              </xsl:if>
            </td>
          </tr>
        </xsl:if>
        <tr>
          <td class="description" colspan="2">
            <xsl:variable name="type" select="@type"/>
            <xsl:value-of disable-output-escaping="yes" select="$messagedoc//BugPattern[@type = $type]/Details"/>
          </td>
        </tr>
        <tr>
          <td style="height:1em;background:White;"/>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>
</xsl:stylesheet>

