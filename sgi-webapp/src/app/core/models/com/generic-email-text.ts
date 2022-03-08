import { IRecipient } from './recipient';

export interface IGenericEmailText {
  id: number;
  subject: string;
  content: string;
  recipients: IRecipient[];
}
