// ===== Enums =====

export type UserRole = 'ADMIN' | 'MANAGER' | 'TRAVELER';
export type UserStatus = 'ACTIVE' | 'BANNED' | 'PENDING_VERIFICATION';
export type TravelStatus = 'DRAFT' | 'PUBLISHED' | 'CANCELLED' | 'COMPLETED';
export type SubscriptionStatus = 'PENDING_PAYMENT' | 'CONFIRMED' | 'CANCELLED';
export type AccommodationType = 'HOTEL' | 'HOSTEL' | 'RESORT' | 'APARTMENT' | 'CAMPING' | 'GUESTHOUSE' | 'OTHER';
export type TransportationType = 'FLIGHT' | 'BUS' | 'TRAIN' | 'BOAT' | 'CAR' | 'MINIBUS' | 'OTHER';
export type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED';
export type PaymentMethod = 'STRIPE' | 'PAYPAL' | 'WAVE' | 'SIMULATED';
export type NotificationType = 'SUBSCRIPTION_CREATED' | 'PAYMENT_SUCCESS' | 'PAYMENT_FAILED';
export type NotificationStatus = 'PENDING' | 'SENT' | 'FAILED';

// ===== Generic =====

export interface MessageResponse {
  message: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

// ===== Auth / Users =====

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string | null;
  role: UserRole;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface UpdateUserRequest {
  email?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  phoneNumber?: string | null;
  role?: UserRole | null;
  status?: UserStatus | null;
  performanceScore?: number | null;
}

export interface UserResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string | null;
  role: UserRole;
  status: UserStatus;
  performanceScore: number | null;
  createdAt: string;
  lastLoginAt: string | null;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserResponse;
}

// ===== Travels =====

export interface DestinationRequest {
  name: string;
  country: string;
  city?: string | null;
  description?: string | null;
  displayOrder?: number | null;
}

export interface ActivityRequest {
  name: string;
  description?: string | null;
  location?: string | null;
  displayOrder?: number | null;
}

export interface CreateTravelRequest {
  title: string;
  description?: string | null;
  startDate: string;
  endDate: string;
  price: number;
  maxCapacity: number;
  accommodationType?: AccommodationType | null;
  accommodationName?: string | null;
  transportationType?: TransportationType | null;
  transportationDetails?: string | null;
  destinations: DestinationRequest[];
  activities: ActivityRequest[];
}

export interface UpdateTravelRequest {
  title?: string | null;
  description?: string | null;
  startDate?: string | null;
  endDate?: string | null;
  price?: number | null;
  maxCapacity?: number | null;
  status?: TravelStatus | null;
  accommodationType?: AccommodationType | null;
  accommodationName?: string | null;
  transportationType?: TransportationType | null;
  transportationDetails?: string | null;
  destinations?: DestinationRequest[] | null;
  activities?: ActivityRequest[] | null;
}

export interface DestinationResponse {
  id: string;
  name: string;
  country: string;
  city: string | null;
  description: string | null;
  displayOrder: number | null;
}

export interface ActivityResponse {
  id: string;
  name: string;
  description: string | null;
  location: string | null;
  displayOrder: number | null;
}

export interface TravelResponse {
  id: string;
  managerId: string;
  title: string;
  description: string | null;
  startDate: string;
  endDate: string;
  duration: number;
  price: number;
  maxCapacity: number;
  currentBookings: number;
  availableSpots: number;
  status: TravelStatus;
  accommodationType: AccommodationType | null;
  accommodationName: string | null;
  transportationType: TransportationType | null;
  transportationDetails: string | null;
  destinations: DestinationResponse[];
  activities: ActivityResponse[];
  createdAt: string;
  updatedAt: string;
}

// ===== Subscriptions =====

export interface SubscriptionResponse {
  id: string;
  travelerId: string;
  travelId: string;
  travelTitle: string;
  status: SubscriptionStatus;
  createdAt: string;
  updatedAt: string;
}

// ===== Payments =====

export interface PaymentResponse {
  id: string;
  subscriptionId: string;
  travelId: string;
  travelerId: string;
  travelTitle: string;
  amount: number;
  currency: string;
  method: PaymentMethod;
  transactionId: string;
  status: PaymentStatus;
  failureReason: string | null;
  createdAt: string;
  updatedAt: string;
}

// ===== Notifications =====

export interface NotificationResponse {
  id: string;
  travelerId: string;
  travelId: string;
  subscriptionId: string;
  recipientEmail: string;
  subject: string;
  body: string;
  type: NotificationType;
  status: NotificationStatus;
  failureReason: string | null;
  createdAt: string;
  updatedAt: string;
}

// ===== Feedbacks =====

export interface FeedbackResponse {
  id: number;
  travelerId: string;
  travelId: string;
  travelTitle: string;
  rating: number;
  comment: string;
  createdAt: string;
}

export interface CreateFeedbackRequest {
  travelId: string;
  rating: number;
  comment: string;
}

export interface UpdateFeedbackRequest {
  rating?: number;
  comment?: string;
}

// ===== Reports =====

export type ReportStatus = 'OPEN' | 'REVIEWED' | 'DISMISSED';
export type ReportTargetType = 'TRAVEL' | 'REVIEW' | 'USER';

export interface ReportResponse {
  id: string;
  reporterId: string;
  reporterName: string;
  targetType: ReportTargetType;
  targetId: string;
  targetLabel: string;
  reason: string;
  status: ReportStatus;
  createdAt: string;
}

export interface CreateReportRequest {
  targetType: ReportTargetType;
  targetId: string;
  reason: string;
}

// ===== Recommendations =====

export interface RecommendationResponse {
  travelId: string;
  title: string;
  description: string;
  price: number;
  startDate: string;
  endDate: string;
  destinations: string[];
  score: number;
  reason: string;
}

// ===== Search =====

export interface SearchDestination {
  name: string;
  country: string;
  city: string | null;
  description: string | null;
}

export interface SearchActivity {
  name: string;
  description: string | null;
  location: string | null;
}

export interface SearchResultResponse {
  id: string;
  managerId: string;
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  duration: number;
  price: number;
  maxCapacity: number;
  currentBookings: number;
  availableSpots: number;
  status: string;
  accommodationType: string | null;
  accommodationName: string | null;
  transportationType: string | null;
  transportationDetails: string | null;
  destinations: SearchDestination[];
  activities: SearchActivity[];
  createdAt: string;
  updatedAt: string;
}
