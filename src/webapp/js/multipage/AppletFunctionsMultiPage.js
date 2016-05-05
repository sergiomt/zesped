var mapAmounts = null;
var tabIndex = 4;
var mapDocumentsId = null;

var listaImagenes = new Array();
var imagenesProcesadas = 0;
var documentosProcesados = 0;

var commited = false ;
var idCommitedDeposit ;
var idChequeToParent = "" ;
var confirmDeposit=false;

var REVERSE_CELL_INDEX_thumbnailsTable = 0;
var OBVERSE_CELL_INDEX_thumbnailsTable = 0;
var CHEQUE_INFO_CELL_INDEX_checkInfoTable = 2;

var MAXIMUM_AMOUNT_ALLOWED = 21474836.47;

var CHEQUE_WITHOUT_INCIDENCE = new Number(1);
var CHEQUE_WITH_INCIDENCE = new Number(0);

var checkBalance = 0;
var maxAllowedErrorInCodeline = -1;
var contadorCaracteresInvalidosCodeline = 0;
var contadorCaracteresValidosCodeline = 0;
var longitudCodeline = 0;
var duplicateCodelinesValidation = 0;

var rowDigitalizationTable = "";
var contItem =0;
var numRow=0;

var depositCancelled = "Deposit cancelled";
var depositCommitted = "Deposit committed";
var clientError = "Se ha producido un error: \n {0} \n<br>Solucione el problema y reinicie el escaner.";
var serverError = "Se ha producido un error en el servidor: \n{0} \n<br>Contacte con el administrador para solucionarlo.";
var scannerInitialized = "Escaner inicializado";
var deleteTheDocument = "¿Desea borrar el documento {0}?";
var errorDeleteDocument = "Ha ocurrido un error y no se ha eliminado el documento: \n{0}";
var errorTotalOfCheques = "El total de cheques es {0} pero se han digitalizado {1} correctamente. Reviselo antes de continuar";
var errorNumberOfChequesGreaterThanZero = "El total de cheques de la remesa ha de ser mayor que 0. Reviselo antes de continuar";
var errorAmountDocumentIsEmpty = "El importe del documento con indice {0} esta vacio. Reviselo antes de continuar";
var errorAmountChequeGreaterThanZero = "El importe del documento con indice {0} debe de ser mayor que cero. Reviselo antes de continuar";
var errorTotalAmount = "El Total del importe del documento esta vacio o con un valor erroneo. Reviselo antes de continuar";
var errorSumOfAmounts = "Existe un descuadre de {0} en el importe. Reviselo antes de continuar";
var descuadre = "Descuadre: ";
var operacionesPendientes = "Aun existen tareas pendientes. Espere a que finalicen para continuar."
var rellenarCamposObligatorios = "Rellene antes el campo {0} para iniciar la captura de cheques";
var campoNumeroDeCheques = "Número de Cheques";
var campoImporteTotal = "Importe Total";
var order = "Orden:";
var amountCheque = "Importe:";
var marcaje = "Marcaje:";
var actualAmount = "Importe total actual:";
var codelineEmptyError = "Marcaje Inválido";
var maxAmountPerDayNotPostpone = "Ha superado el máximo importe por día y no podrá seguir digitalizando en el día de hoy.";
var maxAmountPerDepositer = "Ha superado el máximo importe por día, su depósito se procesará en el siguiente día. ¿Desea continuar de todas formas?";
var commitMessageExceedTimeLimit = "Se ha superado el límite de hora, el depósito se procesará en el siguiente día hábil. ¿Está seguro que desea confirmar el depósito?";
var marcajeDuplicado = "Marcaje Duplicado:";
var observations = "Observaciones:";
var currentcodeline = new String("");
var currentDocumentErrorMessage = new String("");

function setCheckBalanceAmount(value) {
    checkBalance = value;
}

function setMaxAllowedErrorInCodeline(maxErrorsCodeline) {
    maxAllowedErrorInCodeline = maxErrorsCodeline;
}

function setDuplicateCodelinesValidation(duplicateCodelinesValidationValue) {
    duplicateCodelinesValidation = duplicateCodelinesValidationValue;
}

function setInitialButtonState() {
    $("#initButton").attr("disabled", "");
    $("#scanButton").attr("disabled", "disabled");
    $("#stopButton").attr("disabled", "disabled");
}

function activateApplet(locale) {
    var applet = document.getElementById('atril-capture-applet');
    if (applet.isActive()) {
        $("#error").html("");
        $("#message").html("");
        $("#initButton").attr("disabled", "disabled");
        $("#stopButton").attr("disabled", "disabled");
        var language = applet.setLanguage(locale);
        var opciones = applet.initScanner();
    } else {
        setTimeout(activateApplet(locale),5000);
    }
}

//---------------------------------------------------------------------------
//           PULSACION DE LOS BOTONES
//---------------------------------------------------------------------------
function setInitButtonClick(locale) {
    try {
        $("#error").html("");
        activateApplet(locale);
    }catch(err) {
        $("#error").html(err);
    }
}

function setRollbackClick(rollbackMessage) {
    if (!document.getElementById('scanButton').disabled) {
        $("#error").html("");
        var applet = document.getElementById('atril-capture-applet');
        var resp = window.confirm(rollbackMessage);
        if (resp) {
            try {
                $("#initButton").attr("disabled", "disabled");
                $("#scanButton").attr("disabled", "");
                $("#stopButton").attr("disabled", "disabled");
                $("#message").html("");
                $("#error").html("");
                applet.rollback();
                $("#message").html(depositCancelled);
                deleteTable();
                imagenesProcesadas = 0;
                listaImagenes.length = 0;
                documentosProcesados = 0;
            } catch(err) {
                $("#error").html(err);
            }
        }
    } else {
        window.alert(operacionesPendientes);
    }
}

function setCommitClick(commitMessage,maxChequesDeposit,accountNumber,timeLimit,workingDays) {
    $("#error").html("");
    var applet = document.getElementById('atril-capture-applet');
    var resp = window.confirm(commitMessage);
    if (resp) {
        try {
            $("#initButton").attr("disabled", "disabled");
            $("#scanButton").attr("disabled", "");
            $("#stopButton").attr("disabled", "disabled");
            $("#message").html("");
            $("#error").html("");
            applet.commit();
            $("#message").html(depositCommitted);
            deleteTable();
            imagenesProcesadas = 0;
            listaImagenes.length = 0;
            documentosProcesados = 0;
        } catch(err) {
            $("#error").html(err);
        }
    } else {
        window.alert(operacionesPendientes);
    }
}

function setScanClick() {
    $("#error").html("");
    var applet = document.getElementById('atril-capture-applet');
    try {
        $("#initButton").attr("disabled", "disabled");
        $("#scanButton").attr("disabled", "disabled");
        $("#stopButton").attr("disabled", "");
        $("#message").html("");
        $("#error").html("");
        applet.scan();
    } catch(err) {
        $("#error").html(err);
    }
}

function setStopClick() {
    $("#error").html("");
    var applet = document.getElementById('atril-capture-applet');
    try {
        $("#initButton").attr("disabled", "disabled");
        $("#scanButton").attr("disabled", "");
        $("#stopButton").attr("disabled", "disabled");
        $("#message").html("");
        $("#error").html("");
        applet.stop();
        comprobarCargaImagenes();
    } catch(err) {
        $("#error").html(err);
    }
}

//---------------------------------------------------------------------------
//           CALLBACK DE LOS EVENTOS LANZADOS DESDE EL APPLET
//---------------------------------------------------------------------------
function errorCallback(needInitialization, error) {
    if(needInitialization) {
        $("#initButton").attr("disabled", "");
        $("#scanButton").attr("disabled", "disabled");
    } else {
        $("#initButton").attr("disabled", "disabled");
        $("#scanButton").attr("disabled", "");
    }
    $("#stopButton").attr("disabled", "disabled");
    $("#error").html(format(clientError, error));
}

function startDocumentProcessCallback() {
    var applet = document.getElementById('atril-capture-applet');
    applet.endorserOff();
}

function serverErrorCallback(error) {
    $("#initButton").attr("disabled", "disabled");
    $("#scanButton").attr("disabled", "disabled");
    $("#stopButton").attr("disabled", "disabled");
    $("#error").html(format(serverError, error));
}

function scannerInitializedCallback() {
    $("#initButton").attr("disabled", "disabled");
    $("#scanButton").attr("disabled", "");
    $("#stopButton").attr("disabled", "disabled");
    $("#message").html(scannerInitialized);
}

function userActionNeededCallback(error) {
    var mensaje = error;
    $("#initButton").attr("disabled", "disabled");
    comprobarCargaImagenes();
    $("#stopButton").attr("disabled", "disabled");
    $("#error").html(mensaje);
}

function depositConfirmedCallback(idDeposit) {
    //$("#message").html(depositCommitted);
    window.location = "deposito.jsp";
}

function depositRolledBackCallback(idDepositRolledBack){
    //$("#scanButton").attr("disabled", "");
    //$("#message").html(depositCancelled);
    window.location = "index.jsp";
}

function depositServerValidations(idDeposit,applet) {
}

function viewCodelineCallback(codeline) {
    $("#message").html(codeline);
}

function documentBeginProcesCallback() {
    var applet = document.getElementById('atril-capture-applet');

    applet.endorserOff();
}

function documentProcessedCallback(documentId, infoDocu) {
    addRowToImagesTable();
    //  addRowToChequeInfoTable(documentId, infoDocu);
    listaImagenes[listaImagenes.length] = "";
}

function pageProcessedCallback(documentId, pageId, pageNumber) {
   // var cell = document.getElementById(documentosProcesados);
    var cell = document.getElementById("cell"+documentosProcesados);

    //    var idRow = getRow(documentId);
    //  var row = table.rows[idRow];
    //   var row = table.rows[idRow];
 
    //  if((pageNumber % 2)==0){
    //        if (pageNumber==2) {
    //        var row = table.rows[documentosProcesados-1];
    //        row.cells[REVERSE_CELL_INDEX_thumbnailsTable].id="row" + pageId;
    //        row.cells[REVERSE_CELL_INDEX_thumbnailsTable].width="300px";
    //        row.cells[REVERSE_CELL_INDEX_thumbnailsTable].height="200px";
    //    } else {
    //        var row = table.rows[documentosProcesados-2];
    //        row.cells[OBVERSE_CELL_INDEX_thumbnailsTable].id="row" + pageId;
    //        row.cells[REVERSE_CELL_INDEX_thumbnailsTable].width="300px";
    //        row.cells[REVERSE_CELL_INDEX_thumbnailsTable].height="200px";
    //    }
    //    var internalDiv = document.getElementById("id").getElemntById("div"+documentosProcesados);
    //  internalDiv.id="row" + pageId;
    //    row.cells[0].width="400px";
    //    row.cells[0].height="650px";
    contItem++;
    createNewItem("img" + pageId);
    getItem("img" + pageId).setRepositoryName("taglib");
    initTagItem("cell"+contItem,"img" + pageId ,pageId, 175, 250, null, "default", 0,false);
    //    initTagItem("span"+documentosProcesados,"img" + pageId ,pageId, 350, 550, null, "default", 0,false);
    // initTagItem("row" + pageId,"img" + pageId ,pageId, 350, 550, null, "default", 0,false);
    
    listaImagenes[imagenesProcesadas] = "img" + pageId;
    imagenesProcesadas++;
}

//---------------------------------------------------------------------------
//                  GENERACION DE LA TABLA DE IMAGENES
//---------------------------------------------------------------------------
function addRowToImagesTable() {
    //    var table = document.getElementById("digitalizationTable");
    //    // Anverso
    //    if (tr.l)
    //    var tr=document.createElement("tr");
    //    documentosProcesados++;
    //    tr.id = "span"+documentosProcesados;
    //    dtriv.appendChild(tr);
    //    
    //    // Reverso
    //    var div2=document.createElement("div");
    //
    //    documentosProcesados++;
    //    div2.id = "span"+documentosProcesados;
    //    div.appendChild(div2);
    //       
    
    var table = document.getElementById("digitalizationTable");
    if ((documentosProcesados % 6)==0){
        rowDigitalizationTable = table.insertRow(numRow);
        rowDigitalizationTable.align="left";
        numRow++
    }
    var td=document.createElement("td");
    documentosProcesados++;
    td.id="cell"+documentosProcesados;
    rowDigitalizationTable.appendChild(td);
   
    
     var td2=document.createElement("td");
     documentosProcesados++;
    td2.id="cell"+documentosProcesados;
    rowDigitalizationTable.appendChild(td2);

    
}

//---------------------------------------------------------------------------
//         GENERACION DE LA TABLA DE INFORMACION DEL CHEQUE
//---------------------------------------------------------------------------
function addRowToChequeInfoTable(documentId, infoDocu) {
    var cellData = rowDigitalizationTable.insertCell(CHEQUE_INFO_CELL_INDEX_checkInfoTable);
    cellData.className="infoCheck_toDeposti_Cell";

    cellData.innerHTML = returnFieldSetInfo(infoDocu,documentId);
    
    //AsignarOnClickABotonDelete(cellData);
   
    asignarTabIndexElementosTabla();
    var Field = document.getElementById(documentId);
    setFieldFocus(Field);
}

function AsignarOnClickABotonDelete(cellData) {
    cellData.childNodes[0].childNodes[cellData.childNodes[0].childNodes.length-1].onclick = deleteRow;
}

function returnFieldSetInfo(infoDocu, documentId) {
    var table = document.getElementById("digitalizationTable");
    infoValidatedDocument = addAttibutesJSON(infoDocu);
    infoValidatedDocument.documentId = documentId;
    chequeAttributes = infoValidatedDocument.paragraphs;

    var paragraphs = addIdDocument(documentId) +
    addCurrentCodeline(infoValidatedDocument) +
    addOrderNumber() +
    addAmount(infoValidatedDocument) +
    chequeAttributes +
    addObservations();
    //+ addTrashButton(table);
    return "<fieldset class='chequeInformationFrame'>" + paragraphs + "</fieldset>"
}

function addIdDocument(documentId) {
    var paragraphs = "<input type='hidden' value='" + documentId + "'/>";

    return paragraphs;
}

function addOrderNumber() {
    var paragraphs = "<p><span class='fieldName'>" + order + "  " + "</span>";
    paragraphs = paragraphs + "<span>" + documentosProcesados + "</span></p>";
   
    return paragraphs;
}

function addAmount(infoValidatedDocument) {
    if((checkBalance) && (esDocumentoSinIncidencia(infoValidatedDocument))){
        var paragraphs = "<p><span class='fieldName'>" + amountCheque + "  " + "</span>";
        paragraphs = paragraphs + "<span><input id='" + infoValidatedDocument.documentId;
        paragraphs = paragraphs + "' type='textbox' class='currency' value='0.00'";
        paragraphs = paragraphs + " onblur=\"getActualAmount(); return validarImporteMaximoNoExcedido('" + infoValidatedDocument.documentId + "')\"" ;     
        paragraphs = paragraphs + " onfocus='this.select()'  onKeyPress='return validarTeclaPulsadaEsImporte(event)'/>";

    }else{
        var paragraphs = "<p><span class='fieldName' style='visibility:hidden'>" + amountCheque + "  " + "</span>";
        paragraphs = paragraphs + "<span><input id='" + infoValidatedDocument.documentId;
        paragraphs = paragraphs + "' type='textbox' class='currency' value='0.00'";
        paragraphs = paragraphs + " style='visibility:hidden' ";
    }
    
    paragraphs = paragraphs + "</span></p>";  
    return paragraphs;     
}

function addAttibutesJSON(infoDocu) {
    var obj = jQuery.parseJSON(infoDocu);
    var paragraphs = "";
    var currentcodeline = "";
    var documentDoesNotHaveIncidence = CHEQUE_WITHOUT_INCIDENCE;
    
    $.each(obj, function(key, value) {
        currentDocumentErrorMessage = "";
        var resp = verificarSiKeyEsCodeline(key);
        if(resp) {
            value = limpiarControlCharactersCodeLine(value);
            currentcodeline = value;
            if (esMarcajeValido()){
                if (esMarcajeDuplicado(value)) {
                    documentDoesNotHaveIncidence = CHEQUE_WITH_INCIDENCE;
                    currentDocumentErrorMessage = marcajeDuplicado;
                }
                paragraphs = paragraphs + "<p><span class='fieldName'>" + marcaje + " </span><span>" + value + "</span></p>"
            } else {
                paragraphs = paragraphs + "<p><span class='fieldName'>" + marcaje + " </span><span>" + value + "</span></p>"
                currentDocumentErrorMessage = codelineEmptyError;
                documentDoesNotHaveIncidence = CHEQUE_WITH_INCIDENCE;
            }   
        }
    });

    return new InfoValidatedDocument("", currentcodeline, documentDoesNotHaveIncidence, paragraphs);
}

function addObservations() {
    var paragraphs = "";
    if (currentDocumentErrorMessage.length == 0) {
        paragraphs = "<p><span class='fieldName'>" + " " + "</span>";
        paragraphs = paragraphs + "<span style='color:red;'>" + " " + "</span></p>";
    } else {
        paragraphs = "<p><span class='fieldName'>" + observations + "</span>";
        paragraphs = paragraphs + "<span style='color:red;'>" + " " + currentDocumentErrorMessage + "</span></p>";
    }
    return paragraphs;
}


function addTrashButton(table){
    var trashId = "a" + table.rows.length;
    var paragraphs = "<a class='trash' id='" + trashId + "' href='#'><span class='borrar'>borrar</span></a>";

    return paragraphs;
}

//---------------------------------------------------------------------------
//                  METODOS DE VALIDACION
//---------------------------------------------------------------------------
function validateReference() {
}

function validateTotalItems() {
    return true;
}

function validateMaxAmountPerDayPerCompanyPerDepositor(accountNumber,maxChequesDeposit,commitMessage){
}

function validateDepositTimeLimit(accountNumber,maxChequesDeposit,commitMessage,timeLimit,workingDays){
}

function validateMaxChequesDeposit(maxChequesDeposit){
}

function getNumberFromCurrency(currency) {
    return currency.asNumber({
        parseType: 'float'
    });
}

function validateValueOfTotalAmount(){
    return false;
}

function validateTotalAmount() {

}

//---------------------------------------------------------------------------
//                  METODOS DE PROPOSITO GENERAL
//---------------------------------------------------------------------------
function getRow(documentId) {
    var table = document.getElementById("digitalizationTable");
    var row = null;
    for (var i = 0; i < table.rows.length; i++) {
        row = table.rows[i];
    //        if (row.cells[CHEQUE_INFO_CELL_INDEX_checkInfoTable].childNodes[0].childNodes[0].value == documentId) {
    //            return i;
    //        }
    }
    return -1;
}

function insertObservation(documentId, observationText) {
    var table = document.getElementById("digitalizationTable");
    var row = null;
    for (var i = 0; i < table.rows.length; i++) {
        row = table.rows[i];
    //        if (row.cells[CHEQUE_INFO_CELL_INDEX_checkInfoTable].childNodes[0].childNodes[0].value == documentId) {
    //            row.cells[CHEQUE_INFO_CELL_INDEX_checkInfoTable].childNodes[0].childNodes[1].value = CHEQUE_WITH_INCIDENCE;
    //            row.cells[CHEQUE_INFO_CELL_INDEX_checkInfoTable].childNodes[0].childNodes[6].childNodes[0].innerHTML = observations;
    //            row.cells[CHEQUE_INFO_CELL_INDEX_checkInfoTable].childNodes[0].childNodes[6].childNodes[1].innerHTML = observationText;
    //            row.cells[CHEQUE_INFO_CELL_INDEX_checkInfoTable].childNodes[0].childNodes[4].childNodes[1].innerHTML = " ";
    //            mapAmounts["A" + documentId] = 0;
    //        }
    }
}

function setFieldFocus(field) {
    field.focus();
    field.select();
}

function isNumeric(sText)
{
    var validChars = "0123456789.";
    var isNumber=true;
    var charac;

    for (i = 0; i < sText.length && isNumber == true; i++){
        charac = sText.charAt(i);
        if (validChars.indexOf(charac) == -1) {
            isNumber = false;
        }
    }
    return isNumber;
}

function convertArrayToJSON() {
    return JSON.stringify(mapAmounts);
}

function addAmounts() {
    var accumulatedAmount = 0;

    for(var clave in mapAmounts) {
        accumulatedAmount += parseFloat(mapAmounts[clave]);
    }
    return accumulatedAmount;
}

function asignarTabIndexElementosTabla() {
    var idDoc;
    var textBox;

    var table = document.getElementById('digitalizationTable');
    for (var i = 0; i < table.rows.length; i++) {
        idDoc = table.rows[i].childNodes[CHEQUE_INFO_CELL_INDEX_checkInfoTable].childNodes[0].childNodes[0].value;
        textBox = document.getElementById(idDoc);
        if (textBox != null) {
            textBox.tabIndex = i + 5;
        }
    }
}

function comprobarCargaImagenes() {
    var resultado = true;
    var imageName = "";

    for (var i = 0; i < listaImagenes.length;i++) {
        imageName = listaImagenes[i];
        if (imageName != "") {
            resultado = document.images[imageName].complete;
            if (!resultado) {
                i = document.images.length;
            }
        }
    }
    if (resultado == false) {
        setTimeout("comprobarCargaImagenes()", 500);
    } else {
        imagenesProcesadas = 0;
        listaImagenes.length = 0;
        
        $("#scanButton").attr("disabled", "");
        $("#message").html("");
    }
}

function verificarSiKeyEsCodeline(keyCodeline) {

    if(keyCodeline == "marcaje" || keyCodeline == "codeline") {
        return true;
    }
    return false;
}

function esMarcajeValido() {
    return true;
}

function deleteTable() {
    var table_digitalizationTable = document.getElementById('digitalizationTable');
    var rowDigitalizationTable = null;

    for(var i = table_digitalizationTable.rows.length; i > 0; i--) {
        rowDigitalizationTable = table_digitalizationTable.rows[i-1];
        deleteRowFromTable(rowDigitalizationTable);
    }
    tabIndex = 5;
}

function deleteRow() {
    var rowCheckInfoTable = this.parentNode.parentNode.parentNode;
    var table = document.getElementById("digitalizationTable");
    var rowThumbnailsTable = table.rows[rowCheckInfoTable.rowIndex];

    if (rowThumbnailsTable.cells[OBVERSE_CELL_INDEX_thumbnailsTable].innerHTML != "" && rowThumbnailsTable.cells[REVERSE_CELL_INDEX_thumbnailsTable].innerHTML != "") {
        confirmDeleteRowWindow(rowThumbnailsTable);
    }
}



function deleteRowFromTable(rowDigitalizationTable) {
    var idRow = rowDigitalizationTable.rowIndex;
    var arrayGetValue = false;
    var arraySetValue = false;

    deleteImagesInListaImagenes(rowDigitalizationTable);
    
    var table_digitalizationTable = document.getElementById('digitalizationTable');
    table_digitalizationTable.deleteRow(idRow);
    
    if(checkBalance){
        getActualAmount();
    }
}

function deleteImagesInListaImagenes(row) {

    var idImagen = row.cells[OBVERSE_CELL_INDEX_thumbnailsTable].childNodes[0].id;
    var index = jQuery.inArray(idImagen, listaImagenes);
    if (index != -1) {
        listaImagenes[index] = "";
    }
    idImagen = row.cells[REVERSE_CELL_INDEX_thumbnailsTable].childNodes[0].id;
    index = jQuery.inArray(idImagen, listaImagenes);
    if (index != -1) {
        listaImagenes[index] = "";
    }
}

function validarTeclaPulsadaEsNumerico(event) {
    var charcode;
    
    if (navigator.appName=="Netscape") {
        charcode = event.which;
        if (charcode == 0) { //Tecla especial
            charcode = event.keyCode;
            return validarTeclaExpecial(charcode);
        }
    } else {
        charcode = event.keyCode;
    }
    
    return validarTeclaCampoNumerico(charcode);
}  

function validarTeclaPulsadaEsImporte(event) {
    var charcode;
    
    if (navigator.appName == "Netscape") {
        charcode = event.which;
        if (charcode == 0){ //Tecla especial
            charcode = event.keyCode;
            return validarTeclaExpecial(charcode);
        }
    } else {
        charcode = event.keyCode;
    }
    
    return validarTeclaCampoImporte(charcode);
} 

function validarTeclaCampoNumerico(keycode) {
    if ((keycode == 8) || (keycode == 13) || (keycode > 47 && keycode < 58)) {
        return true;
    } else {
        return false;
    }
}

function validarTeclaCampoImporte(keycode) {
    if ((keycode == 8) || (keycode == 13) || (keycode == 44) || (keycode == 46) ||
        (keycode > 47 && keycode < 58)) {
        return true;
    } else {
        return false;
    }
}

function validarTeclaExpecial(keycode) {
    if ((keycode == 9) || (keycode == 37) || (keycode == 39) || (keycode == 46)) {
        return true;
    } else {
        return false;
    }
}

function convertArrayToJSONId() {
    return JSON.stringify(mapDocumentsId);
}

function addCurrentCodeline(infoValidatedDocument) {
    var paragraphs = "<input type='hidden' value='" + infoValidatedDocument.codeline + "'/>";
    return paragraphs;
}

function limpiarControlCharactersCodeLine(codeline) {
    var cleanCodeline = "";

    longitudCodeline = codeline.length;
    for (i = 0; i < codeline.length; i++) {
        if(esCaracteresValidosCodeline(codeline.charAt(i)) || esCaracteresControlCodeline(codeline.charAt(i))){
            cleanCodeline += (codeline.charAt(i));
        }
        else {
            cleanCodeline += " ";
        }
    }

    return cleanCodeline;
}

function esMarcajeDuplicado(codeline) {
    return false;
}

function esCaracteresValidosCodeline(caracter) {
    if(caracter >= "0" && caracter <= "9"){
        contadorCaracteresValidosCodeline += 1;
        return true;
    }
    return false;
}

function esCaracteresControlCodeline(caracter) {
    if(caracter == "!" || caracter == "@" || caracter == "?"){
        contadorCaracteresInvalidosCodeline += 1;
        return true;
    }
    return false;
}

function InfoValidatedDocument(documentId,
    codeline,
    documentWithoutIncidence,
    paragraphs) {

    this.documentId = documentId;
    this.codeline = codeline;
    this.documentWithoutIncidence = documentWithoutIncidence;
    this.paragraphs = paragraphs;
}