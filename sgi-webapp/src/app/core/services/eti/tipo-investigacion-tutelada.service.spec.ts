import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { TipoInvestigacionTuteladaService } from './tipo-investigacion-tutelada.service';

describe('TipoInvestigacionTuteladaService', () => {
  let service: TipoInvestigacionTuteladaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoInvestigacionTuteladaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
