<!DOCTYPE html>
<html lang="en">
<head>

    <title>Parabuild CI | ${title?html}</title>

    <#-- Meta -->
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="robots" content="index, follow, noarchive">
    <meta name='gwt:property' content='locale=en_US'>

    <#-- This script tag is what actually loads the GWT module.  The 'nocache.js' file
    (also called a "selection script") is produced by the GWT compiler in the module output
    directory or generated automatically in development mode. -->
    <script src="${base}/repository/repository.nocache.js" type="text/javascript"></script>

    <#-- Link -->
    <link rel='stylesheet' type='text/css' href='https://fonts.googleapis.com/css?family=Lato:400,700'/>
    <link rel="stylesheet" type="text/css" href="${base}/parabuild-ci.css">

    ${head}
</head>

<body>
<div id="wrap">
    <div id="header">
        <div class="logo">
            <@s.a action="index" namespace="/" title="Go to Parabuild CI home">
                <img src="/images/theme/parabuildci_logo.png" alt="Build and Deploy with Pleasure!" height="70"/>
                <img src="/images/theme/parabuildci_byline.png" alt="Build and Deploy with Pleasure!" height="70"/>
            </@s.a>
        </div>

        <#-- Top user menu -->
        <div id="user">

                <#-- Login / Logout -->
                <#if Session.loggedInUser??>
                    <@s.a action="preferences" namespace="/user" title="Edit user preferences for ${Session.loggedInUser.name?html} ">${Session.loggedInUser.name?html}</@s.a>
                    |
                    <@s.a action="logout" namespace="/user" title="Logout from Parabuild">Logout</@s.a>
                <#else>
                    <@s.a action="login" namespace="/" title="Login to Parabuild" >Login</@s.a>
                </#if>
        </div>

        <ul id="nav">
            <#-- Home -->
            <li class="tab home <#if contextMenu?? && contextMenu = "home">selected</#if>">
                <@s.a action="index" namespace="/" title="Home">Home</@s.a>
            </li>

            <#-- Projects -->
            <li class="tab normal <#if contextMenu?? && contextMenu = "projects">selected</#if>">
                <@s.a action="projects" namespace="/" title="Projects">Projects</@s.a>
            </li>

            <#-- Builds -->
            <li class="tab normal <#if contextMenu?? && contextMenu = "builds">selected</#if>">
                <@s.a action="builds" namespace="/" title="Builds">Builds</@s.a>
            </li>

            <#-- Results -->
            <li class="tab normal <#if contextMenu?? && contextMenu = "results">selected</#if>">
                <@s.a action="results" namespace="/" title="Results">Results</@s.a>
            </li>

            <#-- Administration -->
            <#if Session.loggedInUser?? && Session.loggedInUser.admin>
                <li class="tab normal <#if contextMenu?? && contextMenu = "administration">selected</#if>">
                    <@s.a action="administration" namespace="/" title="Manage Parabuild CI">Administration</@s.a>
                    <ul>
                        <li><@s.a action="showSystemParameters" namespace="/" title="%{getText('manage.system.parameters')}">
                                <@s.text name="System.Parameters"/></@s.a></li>
                        <li><@s.a action="listMediaServers" namespace="/" title="%{getText('manage.wowza.servers')}">
                                <@s.text name="Wowza.Servers"/></@s.a></li>
                        <li>
                            <@s.url id="userList" namespace="/" action="userList">
                                <@s.param name="search"></@s.param>
                            </@s.url>
                            <@s.a href="%{userList}" title="%{getText('Manage.Joglet.users')}"><@s.text name="Users"/></@s.a>
                        </li>
                        <li><@s.a action="reports" namespace="/" title="%{getText('View.Reports')}"><@s.text name="Reports"/></@s.a></li>
                        <li><@s.a action="sendEmailToAllUsers" namespace="/" title="%{getText('Send.Email.to.All.Users')}"><@s.text name="Send.Email.to.All.Users"/></@s.a></li>
                        <li><@s.a action="sendWelcomeMessage" namespace="/" title="%{getText('send.welcome.messages')}"><@s.text name="Send.Welcome.Messages"/></@s.a></li>
                        <li><@s.a action="sendFriendJoinedMessage" namespace="/" title="%{getText('Send.friend.joined.messages')}"><@s.text name="Send.Friend.Joined.Messages"/></@s.a></li>
                    </ul>
                </li>
            </#if>


            <#-- Errors -->
            <li class="tab normal <#if contextMenu?? && contextMenu = "errors">selected</#if>">
                <@s.a action="results" namespace="/" title="Errors">Errors</@s.a>
            </li>

            <#-- Search -->
            <li class="tab normal <#if contextMenu?? && contextMenu = "search">selected</#if>">
                <@s.a action="search" namespace="/" title="Search">Search</@s.a>
            </li>

            <#-- Help -->
            <li class="tab last <#if contextMenu?? && contextMenu = "help">selected</#if>">
                <@s.a action="help" namespace="/" title="Get help with Parabuild">Help</@s.a>
                <ul>
                    <li><@s.a action="documentation" namespace="/" title="Parabuld CI Documentation">Documentation</@s.a></li>
                    <li><@s.a action="support" namespace="/" title="Contact Parabuild CI Support">Support</@s.a></li>
                </ul>
            </li>

        </ul>
        <#--
        <ul id="breadcrumb">
           <li><a href="">Home</a></li>
           <li><a href="">Grandparent</a></li>
           <li><a href="">Parent</a></li>
           <li>Current page</li>
        </ul>
        -->
    </div>
    <div id="content">
        ${body}
    </div>

    <div id="footer">
        <ul class="links">
            <li><@s.a action="index" namespace="/" title="Builds">Builds</@s.a></li>
            <li><@s.a action="result/groups" namespace="/" title="Results">Results</@s.a></li>
            <li><@s.a action="search" namespace="/" title="Search">Search</@s.a></li>
            <li><@s.a action="support" namespace="/" title="Support">Support</@s.a></li>
            <li><@s.a action="about" namespace="/" title="About">About</@s.a></li>
        </ul>
        <div class="legal">
            <div class="copyright">Copyright 2004-2019 Parabuild CI. All rights reserved.</div>
        </div>
    </div>
</div>

<#-- Include a GWT history iframe to enable full GWT history support (the id must be exactly as shown) -->
<iframe src="javascript:''" id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>

</body>
</html>