<#function formatDateNow pattern>
  <#return formatDate(.now, pattern)>
</#function>

<#function formatDate value pattern>
  <#if value??>
    <#local date=value>
    <#if value?is_string>
      <#local date=value?datetime.iso?date>
    <#elseif value?is_datetime>
      <#local date=value?date>
    <#elseif value?is_date_only>
      <#local date=value>
    <#else>
      <#return "">
    </#if>
    <#switch pattern>
      <#case "FULL">
        <#return date?string.full>
        <#break>
      <#case "LONG">
        <#return date?string.long>
        <#break>
      <#case "MEDIUM">
        <#return date?string.medium>
        <#break>
      <#case "SHORT">
        <#return date?string.short>
        <#break>
      <#default>
        <#return date?string[pattern]>
    </#switch>
  </#if>
  <#return "">
</#function>

<#function formatTime value pattern>
  <#if value??>
    <#local time=value>
    <#if value?is_string>
      <#local time=value?datetime.iso?time>
    <#elseif value?is_datetime>
      <#local time=value?time>
    <#elseif value?is_time>
      <#local time=value>
    <#else>
      <#return "">
    </#if>
    <#switch pattern>
      <#case "FULL">
        <#return time?string.full>
        <#break>
      <#case "SHORT">
        <#return time?string.short>
        <#break>
      <#default>
        <#return time?string[pattern]>
    </#switch>
  </#if>
  <#return "">
</#function>

<#function isI18nField object>
  <#if object?is_sequence>
    <#if (object?size > 0)>
      <#if object[0]?is_hash>
        <#return object[0].lang?? && object[0].value??>
      </#if>
    </#if>
  </#if>
  <#return false>
</#function>

<#function getFieldValue field>
  <#local fixedField = "">
  <#if field?is_string>
    <#attempt>
      <#local fixedField = field?eval_json>
    <#recover>
      <#return field>
    </#attempt>
  <#elseif isI18nField(field)>
    <#local fixedField = field>
  </#if>
  <#if fixedField?has_content && fixedField?is_sequence>
    <#local match = getFieldValueForLanguage(fixedField, .lang)>
    <#if match?has_content && match.value??>
      <#return match.value>
    <#else>
      <#list languagePriorities as langPriority>
        <#local other = getFieldValueForLanguage(fixedField, langPriority)>
        <#if other?has_content && other.value??>
          <#return other.value>
        </#if>
      </#list>
    </#if>
  </#if>
  <#return field>
</#function>

<#function getFieldValueForLanguage field language>
  <#if field?has_content && field?is_sequence>
    <#list field as f>
      <#if f?is_hash && f.lang?has_content && f.lang == language>
        <#return f>
      </#if>
    </#list>
  </#if>
  <#return "">
</#function>
