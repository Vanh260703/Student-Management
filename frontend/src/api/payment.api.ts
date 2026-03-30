import { axiosPrivate } from './axiosInstance';
import { MomoPaymentResponse } from '../types/tuition.types';

export const paymentApi = {
  createMomoPayment: async (tuitionId: number): Promise<MomoPaymentResponse> => {
    const res = await axiosPrivate.post(`/api/v2/payments/momo/create/${tuitionId}`);
    return res.data.result;
  },
};
