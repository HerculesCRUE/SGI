/**
 * Recupera el prefijo de los ids de la clasificacion ()
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("get-clasificacion-prefix-query-param-mediator.mediate() - start");

  var path = decodeURI(mc.getProperty('path'));
  var regexEqual = /padreId==([^;,&])*[;,]?/g;
  var regexIn = /padreId=in=([^;&)])*[;,)]?/g;

  var prefix = null;
  var isRoot = false;
  var padreIdQueryParamEqual = path.match(regexEqual);
  if (padreIdQueryParamEqual) {
    var padreIdQueryParam = padreIdQueryParamEqual[0];

    var regexSeparador = /[;,]/g;
    if (regexSeparador.test(padreIdQueryParam)) {
      padreIdQueryParam = padreIdQueryParam.substring(0, padreIdQueryParam.length - 1);
    }
    var padreId = padreIdQueryParam.split("==")[1].replace(/\"/g, '').split("_");
    if (padreId.length > 1) {
      prefix = padreId[0].concat('_');
      isRoot = padreId[1] === 'ROOT';
    }
  } else {
    var padreIdQueryParamIn = path.match(regexIn);
    if (padreIdQueryParamIn) {
      var padreIdsList = padreIdQueryParamIn[0].split("=in=")[1];
      padreIdsList = padreIdsList.substring(1, padreIdsList.length - 1);
      var padreId = padreIdsList.split(",")[0].replace(/\"/g, '').split("_");
      if (padreId.length > 1) {
        prefix = padreId[0].concat('_');
        isRoot = padreId[1] === 'ROOT';
      }
    }
  }

  mc.setProperty('prefix', prefix);
  mc.setProperty('isRoot', isRoot);

  log.info("get-clasificacion-prefix-query-param-mediator.mediate() - end");
}
