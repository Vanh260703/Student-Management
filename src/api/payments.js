import axiosInstance from './axiosInstance'

export const paymentsApi = {
  getMyTuitions: () => axiosInstance.get('/student/tuitions'),
  getPaymentHistory: () => axiosInstance.get('/student/payment-history'),
  createMomoPayment: (tuitionId) =>
    axiosInstance.post(`/payments/momo/create/${tuitionId}`),
}
