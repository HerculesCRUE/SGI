import { TestBed } from '@angular/core/testing';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { EstadoEvaluadorPipe } from './estado-evaluador.pipe';

describe('EstadoEvaluadorPipe', () => {
  let pipe: EstadoEvaluadorPipe;
  let translateService: TranslateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TranslateModule.forRoot()
      ],
      providers: [
        TranslateService
      ]
    });

    translateService = TestBed.inject(TranslateService);
    pipe = new EstadoEvaluadorPipe(translateService);
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });
});
