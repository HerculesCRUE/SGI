import { ISgiApiTaskRequest } from './sgi-api-task-request';

export interface ISgiApiInstantTaskRequest extends ISgiApiTaskRequest {
  instant: string;
}
