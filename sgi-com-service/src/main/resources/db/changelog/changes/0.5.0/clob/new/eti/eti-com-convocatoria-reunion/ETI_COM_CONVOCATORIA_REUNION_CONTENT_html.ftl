<#macro renderEs>
<#setting locale="es">
<p>Estimado/a miembro del Comité,</p>
<p>Mediante la presente comunicación, se le convoca a la reunión del ${ETI_COMITE}, que se celebrará:</p> 
<p>
- Día: ${sgi.formatDate(ETI_CONVOCATORIA_REUNION_FECHA_EVALUACION, "FULL")}<br>
- Hora: ${ETI_CONVOCATORIA_REUNION_HORA_INICIO}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO} h (primera convocatoria); <#if ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA??>${ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO_SEGUNDA} h (segunda convocatoria)</#if><br>
- Tipo de convocatoria: ${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_TIPO_CONVOCATORIA)}<br>
- Lugar: <#if ETI_CONVOCATORIA_REUNION_VIDEOCONFERENCIA?string == "true">Videoconferencia<#else>${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_LUGAR)}</#if>
</p>
<p>El orden del día de la reunión será el siguiente:</p>
<p>${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_ORDEN_DEL_DIA)}</p>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Committee Member,</p>
<p>You are hereby requested to attend the meeting of the ${ETI_COMITE}, to be held on:</p>
<p>
- Day: ${sgi.formatDate(ETI_CONVOCATORIA_REUNION_FECHA_EVALUACION, "FULL")}<br>
- Time: ${ETI_CONVOCATORIA_REUNION_HORA_INICIO}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO} h (first call); <#if ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA??>${ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO_SEGUNDA} h (second call)</#if><br>
- Type of call: ${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_TIPO_CONVOCATORIA)}<br>
- Location: <#if ETI_CONVOCATORIA_REUNION_VIDEOCONFERENCIA?string == "true">Videoconference<#else>${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_LUGAR)}</#if>
</p>
<p>The agenda of the meeting will be as follows:</p>
<p>${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_ORDEN_DEL_DIA)}</p>
<p>
Yours sincerely,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Batzordekide agurgarria:</p>
<p>Komunikazio honen bidez ${ETI_COMITE}ren bilerarako deialdia helarazten dizugu. Bilerari buruzko datuak:</p>
<p>
- Eguna: ${sgi.formatDate(ETI_CONVOCATORIA_REUNION_FECHA_EVALUACION, "FULL")}<br>
- Ordua: ${ETI_CONVOCATORIA_REUNION_HORA_INICIO}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO} h (lehenengo deialdia); <#if ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA??>${ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO_SEGUNDA} h (bigarren deialdia)</#if><br>
- Deialdi mota: ${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_TIPO_CONVOCATORIA)}<br>
- Lekua: <#if ETI_CONVOCATORIA_REUNION_VIDEOCONFERENCIA?string == "true">Bideokonferentzia<#else>${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_LUGAR)}</#if>
</p>
<p>Hau izango da bilerako gai zerrenda:</p>
<p>${sgi.getFieldValue(ETI_CONVOCATORIA_REUNION_ORDEN_DEL_DIA)}</p>
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