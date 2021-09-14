/**
 * Conversion del objeto Invencion del SGI al de la UM
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("invenciones-response-converter-mediator.mediate() - start");

  var invenciones = mc.getPayloadJSON();
  if (invenciones) {
    var invencionesResponse = new Array();

    for (var i = 0; i < invenciones.length; i++) {
      var invencion = invenciones[i];
      invencionesResponse[i] = { id: invencion.id, titulo: invencion.titulo };
    }

    mc.setPayloadJSON(invencionesResponse);
  }

  log.info("invenciones-response-converter-mediator.mediate() - end");
}
