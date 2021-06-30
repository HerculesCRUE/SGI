import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ConvocatoriaReunionService } from './convocatoria-reunion.service';

describe('ConvocatoriaReunionService', () => {
  let service: ConvocatoriaReunionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ]
    });
    service = TestBed.inject(ConvocatoriaReunionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
