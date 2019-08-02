<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <!-- Properties can be specified to influence deferred binding -->
    <meta name='gwt:property' content='locale=en_US'>

    <!-- Stylesheets are optional, but useful -->
    <link rel="stylesheet" href="${base}/parabuild-ci.css">

    <!-- Titles are optional, but useful -->
    <title>${title}</title>

    <!-- This script tag is what actually loads the GWT module.  The -->
    <!-- 'nocache.js' file (also called a "selection script") is     -->
    <!-- produced by the GWT compiler in the module output directory -->
    <!-- or generated automatically in development mode.             -->
    <script src="${base}/repository/repository.nocache.js" type="text/javascript"></script>

    ${head}
</head>

<body>

${body}

<!-- Include a history iframe to enable full GWT history support -->
<!-- (the id must be exactly as shown)                           -->
<iframe src="javascript:''" id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>

</body>
</html>