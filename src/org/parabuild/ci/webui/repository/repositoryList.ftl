<!DOCTYPE>
<html lang="en">
<head>
    <title>${title}</title>
</head>

<body>

<table class="default">

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
</table>
</body>
</html>
