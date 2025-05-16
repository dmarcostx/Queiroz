import { LogLevel, PassedInitialConfig } from 'angular-auth-oidc-client';

export const authConfig: PassedInitialConfig = {
  config: {
    authority: 'http://keycloak:8180/realms/quarkus-app-realm',
    redirectUrl: window.location.origin + '/callback',
    postLogoutRedirectUri: window.location.origin + '/logout',
    clientId: 'angular-frontend-client',
    scope: 'openid profile',
    responseType: 'code',
    silentRenew: true,
    useRefreshToken: true,
    logLevel: LogLevel.Debug
  }
};
