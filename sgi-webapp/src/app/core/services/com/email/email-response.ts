import { IRecipient } from '@core/models/com/recipient';
import { IEmailParam } from '../email-tpl/email-param';
import { IDeferrable } from './deferrable';

export interface IEmailResponse {
  id: number;
  template: string;
  recipients: IRecipient[];
  attachments: string[];
  params: IEmailParam[];
  deferrableRecipients: IDeferrable;
  deferrableAttachments: IDeferrable;
  deferrableParams: IDeferrable;
}
