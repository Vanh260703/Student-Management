import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import dayjs from 'dayjs'
import { paymentsApi } from '@/api/payments'
import LoadingSpinner from '@/components/common/LoadingSpinner'

const statusBadge = {
  PAID: 'bg-green-100 text-green-700',
  PENDING: 'bg-yellow-100 text-yellow-700',
  OVERDUE: 'bg-red-100 text-red-700',
}
const statusLabel = { PAID: 'Đã thanh toán', PENDING: 'Chờ thanh toán', OVERDUE: 'Quá hạn' }

export default function StudentPayments() {
  const [tuitions, setTuitions] = useState([])
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(true)
  const [paying, setPaying] = useState(null)
  const [tab, setTab] = useState('tuitions')

  useEffect(() => {
    Promise.all([paymentsApi.getMyTuitions(), paymentsApi.getPaymentHistory()])
      .then(([t, h]) => {
        setTuitions(t.data?.content ?? t.data ?? [])
        setHistory(h.data?.content ?? h.data ?? [])
      })
      .catch(() => toast.error('Không thể tải dữ liệu học phí'))
      .finally(() => setLoading(false))
  }, [])

  const handlePay = async (tuitionId) => {
    try {
      setPaying(tuitionId)
      const { data } = await paymentsApi.createMomoPayment(tuitionId)
      if (data.payUrl) {
        window.location.href = data.payUrl
      } else {
        toast.success('Thanh toán thành công!')
      }
    } catch (err) {
      toast.error(err.response?.data?.message || 'Thanh toán thất bại')
    } finally {
      setPaying(null)
    }
  }

  if (loading) return <LoadingSpinner />

  const totalPending = tuitions
    .filter((t) => t.status !== 'PAID')
    .reduce((acc, t) => acc + (t.amount ?? 0), 0)

  return (
    <div>
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Học phí</h2>

      {/* Summary */}
      {totalPending > 0 && (
        <div className="bg-orange-50 border border-orange-200 rounded-xl p-4 mb-6 flex items-center gap-3">
          <span className="text-2xl">⚠️</span>
          <div>
            <p className="font-semibold text-orange-700">Cần thanh toán</p>
            <p className="text-sm text-orange-600">
              Tổng còn lại: <strong>{totalPending.toLocaleString('vi-VN')} đ</strong>
            </p>
          </div>
        </div>
      )}

      {/* Tabs */}
      <div className="flex gap-2 mb-4">
        {[
          { key: 'tuitions', label: 'Học phí' },
          { key: 'history', label: 'Lịch sử thanh toán' },
        ].map((t) => (
          <button
            key={t.key}
            onClick={() => setTab(t.key)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition ${tab === t.key ? 'bg-blue-600 text-white' : 'bg-white text-gray-600 border border-gray-300 hover:bg-gray-50'}`}
          >
            {t.label}
          </button>
        ))}
      </div>

      {tab === 'tuitions' && (
        <div className="space-y-3">
          {tuitions.length === 0 ? (
            <div className="text-center py-16 text-gray-400">Không có học phí</div>
          ) : (
            tuitions.map((t) => (
              <div key={t.tuitionId ?? t.id} className="bg-white rounded-xl shadow-sm p-5 flex items-center justify-between">
                <div>
                  <p className="font-semibold text-gray-800">{t.semesterName ?? t.description}</p>
                  <p className="text-sm text-gray-500 mt-0.5">
                    Hạn: {t.dueDate ? dayjs(t.dueDate).format('DD/MM/YYYY') : '—'}
                  </p>
                  <span className={`inline-block mt-1.5 px-2 py-0.5 rounded-full text-xs font-semibold ${statusBadge[t.status] ?? 'bg-gray-100 text-gray-600'}`}>
                    {statusLabel[t.status] ?? t.status}
                  </span>
                </div>
                <div className="text-right">
                  <p className="text-xl font-bold text-gray-800">
                    {(t.amount ?? 0).toLocaleString('vi-VN')} đ
                  </p>
                  {t.status !== 'PAID' && (
                    <button
                      onClick={() => handlePay(t.tuitionId ?? t.id)}
                      disabled={paying === (t.tuitionId ?? t.id)}
                      className="mt-2 px-4 py-1.5 bg-pink-500 text-white text-sm rounded-lg hover:bg-pink-600 disabled:opacity-60"
                    >
                      {paying === (t.tuitionId ?? t.id) ? 'Đang xử lý...' : '💳 Thanh toán MoMo'}
                    </button>
                  )}
                </div>
              </div>
            ))
          )}
        </div>
      )}

      {tab === 'history' && (
        <div className="bg-white rounded-xl shadow-sm overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-600 uppercase text-xs">
              <tr>
                <th className="px-4 py-3 text-left">Mô tả</th>
                <th className="px-4 py-3 text-right">Số tiền</th>
                <th className="px-4 py-3 text-center">Phương thức</th>
                <th className="px-4 py-3 text-right">Ngày</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {history.length === 0 ? (
                <tr><td colSpan={4} className="text-center py-10 text-gray-400">Chưa có lịch sử</td></tr>
              ) : (
                history.map((h, i) => (
                  <tr key={i} className="hover:bg-gray-50">
                    <td className="px-4 py-3 text-gray-800">{h.description ?? h.semesterName}</td>
                    <td className="px-4 py-3 text-right font-semibold text-green-600">
                      {(h.amount ?? 0).toLocaleString('vi-VN')} đ
                    </td>
                    <td className="px-4 py-3 text-center text-gray-600">{h.paymentMethod ?? 'MoMo'}</td>
                    <td className="px-4 py-3 text-right text-gray-500">
                      {h.paidAt ? dayjs(h.paidAt).format('HH:mm DD/MM/YYYY') : '—'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
