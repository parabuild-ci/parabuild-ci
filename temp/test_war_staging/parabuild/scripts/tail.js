/*
* Build ID
*/
//var activeBuildID = 0;

/*
* Max number of lines to show.
*/
//var MAX_LINE_COUNT = 100;

/*
* Max number of lines to show.
*/
//var TAIL_REFRESH_INTERVAL = 1000;


/*
* Timer to tail the log.
*/
var tailTimer;

/*
* Last time we checked for new lines.
*/
//var lastTimeStamp = 0;

/*
* Current number of lines shown.
*/
//var currentLineCount = 0;

/*
* Starts log tail timer.
*/
function startTail() {
  tailTimer = window.setInterval(requestNewLines, TAIL_REFRESH_INTERVAL);
}


/*
* Stops log tail timer.
*/
function stopTail() {
  window.clearInterval(tailTimer);
}


/*
* Requests new log lines if any.
*/
function requestNewLines() {
  Tail.getUpdate(activeBuildID, lastTimeStamp, updateTail);
}


/*
* Updates log tail with new log lines.
*/
function updateTail(tailUpdate) {
  // check if there is work to do
  if (tailUpdate == null) return;

  // go over list of new lines
  body = document.getElementById("loglines");
  if (body == null) return;

  // check if there is new data
  if (tailUpdate.timeStamp == lastTimeStamp) return;
  lastTimeStamp = tailUpdate.timeStamp;
  // go over lines
  for (var i = 0; i < tailUpdate.logLines.length; i++) {
    // add new row
    row = document.createElement("tr");
    cell = document.createElement("td");
    cell.setAttribute("class", "logLine")
    textnode = document.createTextNode(tailUpdate.logLines[i]);
    cell.appendChild(textnode);
    row.appendChild(cell);
    body.appendChild(row);
    // scroll
    if (body.childNodes.length >= MAX_LINE_COUNT) {
      body.removeChild(body.firstChild);
    }
  }
}