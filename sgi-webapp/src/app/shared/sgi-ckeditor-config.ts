import Editor from "ckeditor5-custom-build/build/ckeditor";

export class SgiCkEditorConfig {

  public static defaultConfig = {
    ...Editor.defaultConfig,
    link: {
      defaultProtocol: 'https://'
    }
  };

}


