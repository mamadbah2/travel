import { Routes } from '@angular/router';
import { TravelCatalogPageComponent } from './features/travel-catalog-page/travel-catalog-page.component';
import { TravelDetailPageComponent } from './features/travel-detail-page/travel-detail-page.component';

export const TRAVEL_ROUTES: Routes = [
  { path: '', component: TravelCatalogPageComponent },
  { path: ':travelId', component: TravelDetailPageComponent },
];
