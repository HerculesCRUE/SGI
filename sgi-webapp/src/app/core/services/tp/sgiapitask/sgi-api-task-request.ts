import { ServiceType } from './service-type';

export interface ISgiApiTaskRequest {
  description: string;
  serviceType: ServiceType;
  relativeUrl: string;
  httpMethod: 'GET' | 'HEAD' | 'POST' | 'PUT' | 'PATCH' | 'DELETE' | 'OPTIONS' | 'TRACE';
}
