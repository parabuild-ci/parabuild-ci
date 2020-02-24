<!DOCTYPE html>
<html lang="en">
<head>
    <#-- This script tag is what actually loads the GWT module.  The 'nocache.js' file
    (also called a "selection script") is produced by the GWT compiler in the module output
    directory or generated automatically in development mode. -->
    <script src="${base}/repository/repository.nocache.js" type="text/javascript"></script>

    <title>${title}</title>
</head>

<body>

<h1>Repositories</h1>

<#assign addRepositoryTitle = "Add new repository"/>
<#assign addRepositoryValue = "Add repository"/>

<form class="form">

    <button type="button" id="add-repository-top" title="${addRepositoryTitle}"
            value="${addRepositoryValue}">${addRepositoryValue}</button>

    <table class="standard">

        <thead>
        <tr>
            <th>Repository Server</th>
            <th>Repository Type</th>
            <th>Repository Name</th>
            <th>Repository Description</th>
            <#if mode == "admin">
                <th>Commands</th>
            </#if>
        </tr>
        </thead>

        <#list repositoryList as repository>
            <tr>
                <td>${repository.serverName}</td>
                <td>${repository.typeAsString}</td>
                <td>${repository.name}</td>
                <td>${repository.description}</td>
                <#if mode == "admin">
                    <td><@s.action name="Edit"/> | <@s.action name="Delete"/></td>
                </#if>
            </tr>
        </#list>

        <#-- Add empty row if the table is empty -->
        <#if !repositoryList?has_content>
            <tr>
                <td>&nbsp</td>
                <td>&nbsp</td>
                <td>&nbsp</td>
                <td>&nbsp</td>
                <#if mode == "admin">
                    <td>&nbsp;</td>
                </#if>
            </tr>
        </#if>
    </table>

    <button type="button" id="add-repository-bottom" title="${addRepositoryTitle}"
            value="${addRepositoryValue}">${addRepositoryValue}</button>
</form>
</body>
</html>
