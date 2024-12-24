import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AcceuilClientDashboardComponent } from './acceuil-client-dashboard.component';

describe('AcceuilClientDashboardComponent', () => {
  let component: AcceuilClientDashboardComponent;
  let fixture: ComponentFixture<AcceuilClientDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AcceuilClientDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AcceuilClientDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
