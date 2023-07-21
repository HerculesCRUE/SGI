import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ColectivoService } from './colectivo.service';

describe('ColectivoService', () => {
  let service: ColectivoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ],
      providers: [
        SgiAuthService
      ],
    });
    service = TestBed.inject(ColectivoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
