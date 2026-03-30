import { environment } from '../../../environments/environment';

const BASE = environment.apiBaseUrl;

export const ApiConfig = {
  auth: {
    register: `${BASE}/api/v1/auth/register`,
    login: `${BASE}/api/v1/auth/login`,
    refresh: `${BASE}/api/v1/auth/refresh`,
    logout: `${BASE}/api/v1/auth/logout`,
  },
  users: {
    me: `${BASE}/api/v1/users/me`,
    changePassword: `${BASE}/api/v1/users/me/change-password`,
    list: `${BASE}/api/v1/users`,
    search: `${BASE}/api/v1/users/search`,
    byId: (id: string) => `${BASE}/api/v1/users/${id}`,
    byRole: (role: string) => `${BASE}/api/v1/users/role/${role}`,
    byStatus: (status: string) => `${BASE}/api/v1/users/status/${status}`,
    ban: (id: string) => `${BASE}/api/v1/users/${id}/ban`,
    unban: (id: string) => `${BASE}/api/v1/users/${id}/unban`,
    performanceScore: (id: string) => `${BASE}/api/v1/users/${id}/performance-score`,
  },
  travels: {
    list: `${BASE}/api/v1/travels`,
    search: `${BASE}/api/v1/travels/search`,
    managerMe: `${BASE}/api/v1/travels/manager/me`,
    byId: (id: string) => `${BASE}/api/v1/travels/${id}`,
    publish: (id: string) => `${BASE}/api/v1/travels/${id}/publish`,
    cancel: (id: string) => `${BASE}/api/v1/travels/${id}/cancel`,
    subscribers: (id: string) => `${BASE}/api/v1/travels/${id}/subscribers`,
    removeSubscriber: (travelId: string, subId: string) =>
      `${BASE}/api/v1/travels/${travelId}/subscribers/${subId}`,
  },
  subscriptions: {
    list: `${BASE}/api/v1/subscriptions`,
    byId: (id: string) => `${BASE}/api/v1/subscriptions/${id}`,
    create: (travelId: string) => `${BASE}/api/v1/subscriptions/travel/${travelId}`,
    cancel: (id: string) => `${BASE}/api/v1/subscriptions/${id}/cancel`,
  },
  payments: {
    list: `${BASE}/api/v1/payments`,
    byId: (id: string) => `${BASE}/api/v1/payments/${id}`,
    bySubscription: (id: string) => `${BASE}/api/v1/payments/subscription/${id}`,
    byTraveler: (id: string) => `${BASE}/api/v1/payments/traveler/${id}`,
    byTravel: (id: string) => `${BASE}/api/v1/payments/travel/${id}`,
  },
  notifications: {
    list: `${BASE}/api/v1/notifications`,
    byId: (id: string) => `${BASE}/api/v1/notifications/${id}`,
    byTraveler: (id: string) => `${BASE}/api/v1/notifications/traveler/${id}`,
    byTravel: (id: string) => `${BASE}/api/v1/notifications/travel/${id}`,
    bySubscription: (id: string) => `${BASE}/api/v1/notifications/subscription/${id}`,
  },
  search: {
    query: `${BASE}/api/v1/search`,
    byId: (id: string) => `${BASE}/api/v1/search/${id}`,
  },
  feedbacks: {
    byTravel: (id: string) => `${BASE}/api/v1/feedbacks/travel/${id}`,
    me: `${BASE}/api/v1/feedbacks/me`,
    create: `${BASE}/api/v1/feedbacks`,
    update: (id: number) => `${BASE}/api/v1/feedbacks/${id}`,
    delete: (id: number) => `${BASE}/api/v1/feedbacks/${id}`,
  },
  reports: {
    create: `${BASE}/api/v1/reports`,
    list: `${BASE}/api/v1/reports`,
    byId: (id: string) => `${BASE}/api/v1/reports/${id}`,
  },
  recommendations: {
    me: `${BASE}/api/v1/recommendations/me`,
  },
} as const;
