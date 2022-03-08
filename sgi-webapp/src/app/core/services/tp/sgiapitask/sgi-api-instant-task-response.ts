import { ISgiApiInstantTaskRequest } from './sgi-api-instant-task-request';

export interface ISgiApiInstantTaskResponse extends ISgiApiInstantTaskRequest {
  id: number;
  disabled: boolean;
}
