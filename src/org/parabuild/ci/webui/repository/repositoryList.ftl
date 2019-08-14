<!DOCTYPE html>
<html lang="en">
<head>
    <title>${title}</title>
</head>

<body>

<h1>Repositories</h1>

<#assign addRepositoryTitle = "Add new repository"/>
<#assign addRepositoryValue = "Add repository"/>

<form class="form">

    <input type="submit" id="add-repository-top" title="${addRepositoryTitle}" value="${addRepositoryValue}"/>

    <table class="standard">

        <thead>
        <tr>
            <th>Repository Server</th>
            <th>Repository Path</th>
            <th>Repository Type</th>
            <#if mode == "admin">
                <th>Commands</th>
            </#if>
        </tr>
        </thead>

        <#list repositoryList as repository>
            <tr>
                <td>${repository.name}</td>
                <td>${repository.path}</td>
                <td>${repository.typeAsString}</td>
                <#if mode == "admin">
                    <td><@s.action name="Edit"/> | <@s.action name="Delete"/></td>
                </#if>
            </tr>
        </#list>

        <#-- Add empty row if the table is empty -->
        <#if !repositoryList?has_content>
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

    <input type="submit" id="add-repository-bottom" title="${addRepositoryTitle}" value="${addRepositoryValue}"/>
</form>
</body>
</html>
