import { Routes } from '@angular/router';
import { ManagerDashboardPageComponent } from './features/manager-dashboard-page/manager-dashboard-page.component';
import { TravelCreatePageComponent } from './features/travel-create-page/travel-create-page.component';
import { TravelEditPageComponent } from './features/travel-edit-page/travel-edit-page.component';
import { TravelManagePageComponent } from './features/travel-manage-page/travel-manage-page.component';
import { ManagerAnalyticsPageComponent } from './features/manager-analytics-page/manager-analytics-page.component';

export const MANAGER_ROUTES: Routes = [
  { path: '', component: ManagerDashboardPageComponent },
  { path: 'analytics', component: ManagerAnalyticsPageComponent },
  { path: 'create', component: TravelCreatePageComponent },
  { path: ':travelId/edit', component: TravelEditPageComponent },
  { path: ':travelId/manage', component: TravelManagePageComponent },
];
