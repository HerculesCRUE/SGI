import { InjectionToken } from "@angular/core";


export interface CkEditorConfig {
  toolbar: {
    items: string[];
  };
  link: {
    defaultProtocol: string;
  };
  language: string;
  table: {
    contentToolbar: string[];
  };
}

/** InjectionToken for CKEditor to configure options. */
export const CKEDITOR_CONFIG = new InjectionToken<CkEditorConfig>(
  'CKEDITOR_OPTIONS', {
  factory: CKEDITOR_CONFIG_FACTORY
}
);

export function CKEDITOR_CONFIG_FACTORY(): CkEditorConfig {
  return DEFAULT_CKEDITOR_CONFIG;
}

export const DEFAULT_CKEDITOR_CONFIG: CkEditorConfig = {
  toolbar: {
    items: [
      'heading',
      '|',
      'bold',
      'italic',
      'underline',
      'link',
      '|',
      'fontBackgroundColor',
      'fontColor',
      'fontFamily',
      'fontSize',
      '|',
      'bulletedList',
      'numberedList',
      'alignment',
      'insertTable',
      '|',
      'undo',
      'redo'
    ]
  },
  link: {
    defaultProtocol: 'https://'
  },
  language: 'es',
  table: {
    contentToolbar: [
      'tableColumn',
      'tableRow',
      'mergeTableCells'
    ]
  }
};