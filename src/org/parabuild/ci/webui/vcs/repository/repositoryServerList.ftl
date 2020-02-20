<!DOCTYPE html>
<html lang="en">
<head>
    <title>${title}</title>

    <#-- This script tag is what actually loads the GWT module.  The 'nocache.js' file
    (also called a "selection script") is produced by the GWT compiler in the module output
    directory or generated automatically in development mode. -->
    <script src="${base}/server/server.nocache.js" type="text/javascript"></script>

</head>

<body>

<h1>Repository Servers</h1>

<#assign addServerTitle = "Add new repository server"/>
<#assign addServerValue = "Add Server"/>

<div class="form">

    <button type="button" id="add-repository-server-top" title="${addServerTitle}"
            value="${addServerValue}">${addServerValue}</button>

    <table class="standard">

        <thead>
        <tr>
            <th>Server Name</th>
            <th>Server Type</th>
            <th>Server Description</th>
            <#if mode == "admin">
                <th>Commands</th>
            </#if>
        </tr>
        </thead>

        <#list serverList as server>
            <tr>
                <td>${server.name}</td>
                <td>${server.typeAsString}</td>
                <td>${server.description}</td>
                <#if mode == "admin">
                    <td><@s.action name="Edit"/> | <@s.action name="Delete"/></td>
                </#if>
            </tr>
        </#list>

        <#-- Add empty row if the table is empty -->
        <#if !serverList?has_content>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <#if mode == "admin">
                    <td>&nbsp;</td>
                </#if>
            </tr>
        </#if>
    </table>

    <button type="button" id="add-repository-server-bottom" title="${addServerTitle}"
            value="${addServerValue}">${addServerValue}</button>
</div>
</body>
</html>
