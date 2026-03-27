import { Routes } from '@angular/router';
import { SubscriptionListPageComponent } from './features/subscription-list-page/subscription-list-page.component';
import { SubscriptionDetailPageComponent } from './features/subscription-detail-page/subscription-detail-page.component';

export const SUBSCRIPTION_ROUTES: Routes = [
  { path: '', component: SubscriptionListPageComponent },
  { path: ':subscriptionId', component: SubscriptionDetailPageComponent },
];
