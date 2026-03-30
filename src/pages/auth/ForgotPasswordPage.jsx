import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Link } from 'react-router-dom'
import { toast } from 'react-toastify'
import { authApi } from '@/api/auth'

const schema = z.object({
  email: z.string().email('Email không hợp lệ'),
})

export default function ForgotPasswordPage() {
  const [sent, setSent] = useState(false)
  const [loading, setLoading] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({ resolver: zodResolver(schema) })

  const onSubmit = async ({ email }) => {
    try {
      setLoading(true)
      await authApi.forgotPassword(email)
      setSent(true)
      toast.success('Email đặt lại mật khẩu đã được gửi!')
    } catch (err) {
      toast.error(err.response?.data?.message || 'Có lỗi xảy ra, thử lại sau.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-600 to-purple-700">
      <div className="bg-white rounded-2xl shadow-2xl p-8 w-full max-w-md">
        <h2 className="text-2xl font-bold text-gray-900 mb-2">Quên mật khẩu</h2>
        <p className="text-gray-500 mb-6 text-sm">
          Nhập email của bạn và chúng tôi sẽ gửi link đặt lại mật khẩu.
        </p>

        {sent ? (
          <div className="text-center">
            <p className="text-green-600 font-medium mb-4">
              Email đã được gửi! Kiểm tra hộp thư của bạn.
            </p>
            <Link to="/login" className="text-blue-600 hover:underline">
              Quay lại đăng nhập
            </Link>
          </div>
        ) : (
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input
                {...register('email')}
                type="email"
                placeholder="example@email.com"
                className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              {errors.email && (
                <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>
              )}
            </div>
            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition disabled:opacity-60"
            >
              {loading ? 'Đang gửi...' : 'Gửi email'}
            </button>
            <Link to="/login" className="block text-center text-sm text-gray-500 hover:underline mt-2">
              Quay lại đăng nhập
            </Link>
          </form>
        )}
      </div>
    </div>
  )
}
