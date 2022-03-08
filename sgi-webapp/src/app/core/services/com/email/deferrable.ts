import { ServiceType } from './service-type';

export interface IDeferrable {
  type: ServiceType;
  url: string;
  method: 'GET' | 'HEAD' | 'POST' | 'PUT' | 'PATCH' | 'DELETE' | 'OPTIONS' | 'TRACE';
}
