/**
 * Recupera el colectivo que coincide con el id indicado
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("colectivo-findbyId-response-converter-mediator.mediate() - start");

  var colectivoId = decodeURI(mc.getProperty('colectivoId'));

  var colectivos = mc.getPayloadJSON();
  var colectivo = null;
  for (var i = 0; i < colectivos.length; i++) {
    if (colectivos[i].id == colectivoId) {
      colectivo = colectivos[i];
    }
  }

  mc.setPayloadJSON(colectivo);

  log.info("colectivo-findbyId-response-converter-mediator.mediate() - end");
}
