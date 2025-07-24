package org.crue.hercules.sgi.com.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.crue.hercules.sgi.com.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.framework.i18n.I18nConfigProvider;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CnfI18nConfigProvider implements I18nConfigProvider {

  private final SgiApiCnfService cnfService;

  private List<Language> enabledLanguages = new ArrayList<>();
  private List<Language> languagePriorities = new ArrayList<>();

  public CnfI18nConfigProvider(SgiApiCnfService cnfService) {
    this.cnfService = cnfService;
  }

  private void updateData() {
    try {
      List<String> response = this.cnfService.findStringListByName(I18nConfigProvider.CNF_ENABLED_LANGUAGES_KEY);
      this.enabledLanguages = response.stream().map(Language::fromCode).toList();
    } catch (Exception ex) {
      log.error("Error obteniendo idiomas habilidatdo", ex);
    }
    try {
      List<String> response = this.cnfService.findStringListByName(I18nConfigProvider.CNF_LANGUAGE_PRIORITIES_KEY);
      this.languagePriorities = response.stream().map(Language::fromCode).toList();
    } catch (Exception ex) {
      log.error("Error obteniendo priorizacion de idiomas", ex);
    }
  }

  @Override
  @Scheduled(fixedDelayString = "#{@'sgi-org.crue.hercules.sgi.com.config.SgiConfigProperties'.i18nConfigRefreshInterval}", timeUnit = TimeUnit.SECONDS)
  public void refresh() {
    updateData();
  }

  @Override
  public List<Language> getEnabledLanguages() {
    return enabledLanguages;
  }

  @Override
  public List<Language> getLanguagePriorities() {
    return languagePriorities;
  }

}
