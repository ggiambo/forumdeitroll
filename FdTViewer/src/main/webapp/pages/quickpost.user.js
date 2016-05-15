// ==UserScript==
// @id             quickpost
// @name           quickpost
// @version        1.0
// @namespace     
// @author         achille
// @description    Posta rapidamente un link sul forum dei troll.
// @include        *
// @run-at         document-end
// ==/UserScript==
 
var ID   = "quickpost";
var LBL  = "Posta questa pagina sul fdt!";
 
var body = document.body;
var menu = body.appendChild(document.createElement("menu"));
menu.id = ID;
menu.type = 'context';
var menuitem = menu.appendChild(document.createElement('menuitem'));
menuitem.label = LBL;
menu.addEventListener("click", function(event) {
    var subject = document.title;
    var text = location.href;
    var url = "http://forumdeitroll.com/Messages?action=newMessage&subject=" +
    	encodeURIComponent(subject) + "&text=" +
    	encodeURIComponent(text);
    location.href = url;
}, true);
window.addEventListener('contextmenu', function(event) {}, true);
document.documentElement.setAttribute("contextmenu", ID);
