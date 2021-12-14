document.onreadystatechange = function(){
    var jdynaFields = document.getElementsByClassName("dynaFieldValue");
    var array_size = jdynaFields.length;
    var searchText = "(Seleccione un valor de la lista)";
    for (var i = 0; i < array_size; i++){
        if(jdynaFields[i].textContent.trim() == searchText){
            jdynaFields[i].parentNode.style.display = 'none';
        }        
    }
    // alert(jdynaFields.length);
}