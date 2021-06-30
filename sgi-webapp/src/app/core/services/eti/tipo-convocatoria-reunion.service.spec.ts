import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgiAuthModule } from '@sgi/framework/auth';

import { TipoConvocatoriaReunionService } from './tipo-convocatoria-reunion.service';


describe('TipoConvocatoriaReunionService', () => {
  let service: TipoConvocatoriaReunionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ]
    });
    service = TestBed.inject(TipoConvocatoriaReunionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
