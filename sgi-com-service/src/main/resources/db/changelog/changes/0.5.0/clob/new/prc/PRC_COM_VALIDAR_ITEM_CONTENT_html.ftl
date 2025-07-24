<#assign data = PRC_COM_VALIDAR_ITEM_DATA?eval />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Le informamos de que dispone de un nuevo ítem que precisa de su validación para poder realizar su baremación:</p>
<p>
- Tipo de ítem: ${sgi.getFieldValue(data.nombreEpigrafe)}<br>
- Título/nombre: ${data.tituloItem}<br>
- Fecha: ${sgi.formatDate(data.fechaItem, "SHORT")}
</p>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear researcher,</p>
<p>Please be advised that you have a new item that needs to be validated in order to carry out your ranking:</p>
<p>
- Type of item: ${sgi.getFieldValue(data.nombreEpigrafe)}<br>
- Title/name: ${data.tituloItem}<br>
- Date: ${sgi.formatDate(data.fechaItem, "SHORT")}
</p>
<p>
Yours sincerely,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Honen bidez jakinarazten dizugu item berri bat dagoela eta balioztatu egin behar duzula, baremazioa egin ahal izateko:</p>
<p>
- Item mota: ${sgi.getFieldValue(data.nombreEpigrafe)}<br>
- Izenburua/izena: ${data.tituloItem}<br>
- Data: ${sgi.formatDate(data.fechaItem, "SHORT")}
</p>
<p>
Jaso agur bero bat.<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>
<hr>
</#if>
</#list>
</body>
</html>