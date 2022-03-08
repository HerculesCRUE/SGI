import { ISgiApiCronTaskRequest } from './sgi-api-cron-task-request';

export interface ISgiApiCronTaskResponse extends ISgiApiCronTaskRequest {
  id: number;
  disabled: boolean;
}
