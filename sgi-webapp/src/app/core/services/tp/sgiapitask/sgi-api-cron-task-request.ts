import { ISgiApiTaskRequest } from './sgi-api-task-request';

export interface ISgiApiCronTaskRequest extends ISgiApiTaskRequest {
  cronExpression: string;
}
