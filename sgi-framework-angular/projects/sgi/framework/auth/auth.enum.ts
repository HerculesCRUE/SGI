/**
 * Authentication service implementation to use
 */
export enum SgiAuthMode {
  /**
   * Authentication uses static values provided by the implementation.
   */
  InMemory = 'In Memory',
  /**
   * Authentication uses Keycloak as external SSO provider
   */
  Keycloak = 'Keycloak'
}
