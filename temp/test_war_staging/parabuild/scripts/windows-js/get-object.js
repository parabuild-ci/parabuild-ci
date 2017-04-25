/*********************************************************************
 * Get an object, this function is cross browser
 * *** Please do not remove this header. ***
 * This code is working on my IE7, IE6, FireFox, Opera and Safari
 * 
 * Usage: 
 * var object = get_object(element_id);
 *
 * @Author Hamid Alipour Codehead @ webmaster-forums.code-head.com  
**/
function get_object(id) {
  var object = null;
  if( document.layers ) {   
    object = document.layers[id];
  } else if( document.all ) {
    object = document.all[id];
  } else if( document.getElementById ) {
    object = document.getElementById(id);
  }
  return object;
}