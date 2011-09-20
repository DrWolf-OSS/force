/**
 * 
 */
var dirty = true;
function goodbye(e) {
	if(dirty){
		if(!e) e = window.event;
		//e.cancelBubble is supported by IE - this will kill the bubbling process.
		e.cancelBubble = true;
		e.returnValue = 'Stai abbandonando la pagina senza aver salvato le informazioni eventualmente inserite!'+
		'\nClicca Salva se vuoi salvare i dati.'; //This is displayed on the dialog

		//e.stopPropagation works in Firefox.
		if (e.stopPropagation) {
			e.stopPropagation();
			e.preventDefault();
		}
	}
}
window.onbeforeunload=goodbye;