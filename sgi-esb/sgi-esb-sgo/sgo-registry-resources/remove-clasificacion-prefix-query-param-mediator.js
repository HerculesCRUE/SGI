/**
 * Elimina el prefijo, si el prefijo contiene _ROOT se cambia la busqueda a padreId=na=""
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("remove-clasificacion-prefix-query-param-mediator.mediate() - start");

  var path = decodeURI(mc.getProperty('path'));
  var isRoot = (mc.getProperty('isRoot') == true);
  log.info("remove-clasificacion-prefix-query-param-mediator.mediate() - initial query: " + path);
  var prefix = mc.getProperty('prefix');
  if (prefix) {
    if (isRoot) {
      var regexEqual = /padreId==([^;,&])*[;,]?/g;
      var regexIn = /padreId=in=([^;&)])*[;,)]?/g;
      if (regexEqual.test(path)) {
        path = path.replace(regexEqual, 'padreId=na=""');
      } else if (regexIn.test(path)) {
        path = path.replace(regexIn, 'padreId=na=""');
      }
    } else {
      path = path.replace(new RegExp(prefix, 'g'), '');
    }

    mc.setProperty('path', encodeURI(path));
  }

  log.info("remove-clasificacion-prefix-query-param-mediator.mediate() - final query = " + path);

  log.info("remove-clasificacion-prefix-query-param-mediator.mediate() - end");
}
