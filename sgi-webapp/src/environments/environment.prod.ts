import { NgxLoggerLevel, LoggerConfig } from 'ngx-logger';
import { SgiAuthMode, SgiAuthConfig } from '@sgi/framework/auth';
import { version } from '../../package.json';

export const environment = {
  production: true,
  serviceServers: {
    eti: '/api/eti',
    sgp: '/api/sgp',
    csp: '/api/csp',
    usr: '/api/usr',
    sgdoc: '/api/sgdoc',
    sge: '/api/sge',
    sgemp: '/api/sgemp',
    sgepii: '/api/sgepii',
    sgo: '/api/sgo',
    pii: '/api/pii',
    rel: '/api/rel',
  },
  loggerConfig: {
    enableSourceMaps: true, // <-- THIS IS REQUIRED, to make "line-numbers" work in SourceMap Object defition (without evalSourceMap)
    level: NgxLoggerLevel.DEBUG
  } as LoggerConfig,
  authConfig: {
    mode: SgiAuthMode.Keycloak,
    ssoRealm: 'sgi',
    ssoClientId: 'front',
    ssoUrl: '/auth',
    // InMemory  auth config
    inMemoryConfig: {
      userRefId: '',
      authorities: [],
      isInvestigador: false
    },
    protectedResources: [
      /\/api\/eti.*/i,
      /\/api\/csp.*/i,
      /\/api\/usr.*/i,
      /\/api\/sgp.*/i,
      /\/api\/sgemp.*/i,
      /\/api\/sgepii.*/i,
      /\/api\/sgo.*/i,
      /\/api\/sge.*/i,
      /\/api\/pii.*/i,
      /\/api\/sgdoc.*/i,
      /\/api\/rel.*/i,
    ]
  } as SgiAuthConfig,
  version,
  defaultTimeZone: 'Europe/Madrid'
};
