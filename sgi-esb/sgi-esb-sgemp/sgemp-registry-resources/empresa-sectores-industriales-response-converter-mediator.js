/**
 * Agrega el prefijo CNAE_ a los idenfigicadores de los sectores industriales reibidos
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("datos-contacto-response-converter-mediator.mediate() - start");

  var empresa = mc.getPayloadJSON();
  for (var i = 0; i < empresa.sectoresIndustriales.length; i++) {
    empresa.sectoresIndustriales[i] = "CNAE_".concat(empresa.sectoresIndustriales[i]);
  }

  mc.setPayloadJSON(empresa);

  log.info("datos-contacto-response-converter-mediator.mediate() - end");
}

