<#assign horaSegunda = ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA />
<#assign fechaEvaluacion = ETI_CONVOCATORIA_REUNION_FECHA_EVALUACION />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>En nombre del/de la Sr/a. Secretario/a del/de la ${ETI_COMITE_NOMBRE_INVESTIGACION}, le informo de que, como miembro del citado / de la citada ${ETI_COMITE}, se le convoca a la reuni&oacute;n que se celebrar&aacute; por videoconferencia el pr&oacute;ximo ${fechaEvaluacion?datetime("dd/MM/yyyy")?string("EEEE")?capitalize}, ${fechaEvaluacion?datetime("dd/MM/yyyy")?string("dd")} de ${fechaEvaluacion?datetime("dd/MM/yyyy")?string("MMMM")?capitalize}, a las ${ETI_CONVOCATORIA_REUNION_HORA_INICIO}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO} horas en primera convocatoria<#if horaSegunda!="null"> y a las ${ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO_SEGUNDA} horas en segunda convocatoria</#if>, con el siguiente orden del d&iacute;a:</p>
    <p>${ETI_CONVOCATORIA_REUNION_ORDEN_DEL_DIA}</p>
    <p>Unirse a la reuni&oacute;n Zoom.</p>
    <p>NOTA: La videoconferencia se realizar&aacute; a trav&eacute;s de la aplicaci&oacute;n ZOOM.</p>
    <a href="https://umurcia.zoom.us/j/00000000000" target="_blank">https://umurcia.zoom.us/j/00000000000</a>
    <p>Por favor, se ruega confirme su asistencia.</p>
    <p>Un saludo.</p>
  </body>
</html>