/**
 * Elimina el prefijo CNAE_ a los idenfigicadores de los sectores industriales reibidos
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("datos-contacto-response-converter-mediator.mediate() - start");

  var empresa = mc.getPayloadJSON();
  for (var i = 0; i < empresa.sectoresIndustriales.length; i++) {
    empresa.sectoresIndustriales[i] = empresa.sectoresIndustriales[i].replace("CNAE_", "");
  }

  mc.setPayloadJSON(empresa);

  log.info("datos-contacto-response-converter-mediator.mediate() - end");
}

