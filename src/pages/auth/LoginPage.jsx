import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useNavigate, Link } from 'react-router-dom'
import { toast } from 'react-toastify'
import { authApi } from '@/api/auth'
import useAuthStore from '@/store/authStore'

const schema = z.object({
  email: z.string().email('Email không hợp lệ'),
  password: z.string().min(6, 'Mật khẩu phải ít nhất 6 ký tự'),
})

const roleHome = {
  ROLE_ADMIN: '/admin/dashboard',
  ROLE_TEACHER: '/teacher/dashboard',
  ROLE_STUDENT: '/student/dashboard',
}

export default function LoginPage() {
  const navigate = useNavigate()
  const setAuth = useAuthStore((s) => s.setAuth)
  const [loading, setLoading] = useState(false)
  const [showPassword, setShowPassword] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({ resolver: zodResolver(schema) })

  const onSubmit = async (values) => {
    try {
      setLoading(true)
      const { data } = await authApi.login(values)
      setAuth(data.user, data.accessToken, data.refreshToken)
      toast.success('Đăng nhập thành công!')
      navigate(roleHome[data.user.role] ?? '/')
    } catch (err) {
      toast.error(err.response?.data?.message || 'Email hoặc mật khẩu không đúng')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex">
      {/* ── Left panel – branding ── */}
      <div className="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-blue-600 via-blue-700 to-purple-700 flex-col items-center justify-center p-16 relative overflow-hidden">
        {/* decorative blobs */}
        <div className="absolute -top-24 -left-24 w-96 h-96 bg-white/10 rounded-full blur-3xl" />
        <div className="absolute -bottom-24 -right-24 w-96 h-96 bg-purple-500/30 rounded-full blur-3xl" />

        <div className="relative z-10 text-center">
          {/* icon */}
          <div className="w-24 h-24 bg-white/20 rounded-3xl flex items-center justify-center mx-auto mb-8 backdrop-blur-sm">
            <span className="text-5xl">🎓</span>
          </div>

          <h1 className="text-4xl font-bold text-white leading-tight">
            Hệ Thống<br />Quản Lý Sinh Viên
          </h1>
          <p className="text-blue-100 mt-4 text-lg leading-relaxed max-w-sm">
            Nền tảng quản lý học tập toàn diện dành cho sinh viên, giảng viên và nhà trường.
          </p>

          {/* feature pills */}
          <div className="mt-10 flex flex-col gap-3 items-start max-w-xs mx-auto">
            {[
              { icon: '📊', text: 'Theo dõi điểm số & GPA' },
              { icon: '📅', text: 'Quản lý lịch học & điểm danh' },
              { icon: '💳', text: 'Thanh toán học phí trực tuyến' },
              { icon: '🔔', text: 'Thông báo thời gian thực' },
            ].map((f) => (
              <div key={f.text} className="flex items-center gap-3 text-white/90 text-sm">
                <span className="w-8 h-8 bg-white/15 rounded-lg flex items-center justify-center flex-shrink-0">
                  {f.icon}
                </span>
                {f.text}
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* ── Right panel – form ── */}
      <div className="flex-1 flex flex-col items-center justify-center bg-gray-50 px-6 py-12">
        {/* Mobile logo */}
        <div className="lg:hidden mb-8 text-center">
          <div className="w-16 h-16 bg-blue-600 rounded-2xl flex items-center justify-center mx-auto mb-3">
            <span className="text-3xl">🎓</span>
          </div>
          <h2 className="text-xl font-bold text-gray-800">Quản Lý Sinh Viên</h2>
        </div>

        <div className="w-full max-w-md">
          {/* Heading */}
          <div className="mb-8">
            <h2 className="text-3xl font-bold text-gray-900">Đăng nhập</h2>
            <p className="text-gray-500 mt-2">Chào mừng bạn trở lại! Vui lòng nhập thông tin.</p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            {/* Email */}
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                Email
              </label>
              <div className="relative">
                <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 text-base">
                  ✉️
                </span>
                <input
                  {...register('email')}
                  type="email"
                  placeholder="example@email.com"
                  className={`w-full pl-11 pr-4 py-3 bg-white border rounded-xl text-sm transition focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                    errors.email ? 'border-red-400' : 'border-gray-200'
                  }`}
                />
              </div>
              {errors.email && (
                <p className="text-red-500 text-xs mt-1.5 flex items-center gap-1">
                  <span>⚠</span> {errors.email.message}
                </p>
              )}
            </div>

            {/* Password */}
            <div>
              <div className="flex items-center justify-between mb-2">
                <label className="text-sm font-semibold text-gray-700">Mật khẩu</label>
                <Link
                  to="/forgot-password"
                  className="text-xs text-blue-600 hover:text-blue-700 font-medium"
                >
                  Quên mật khẩu?
                </Link>
              </div>
              <div className="relative">
                <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 text-base">
                  🔒
                </span>
                <input
                  {...register('password')}
                  type={showPassword ? 'text' : 'password'}
                  placeholder="Nhập mật khẩu"
                  className={`w-full pl-11 pr-12 py-3 bg-white border rounded-xl text-sm transition focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
                    errors.password ? 'border-red-400' : 'border-gray-200'
                  }`}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword((p) => !p)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 text-sm select-none"
                  tabIndex={-1}
                >
                  {showPassword ? '🙈' : '👁️'}
                </button>
              </div>
              {errors.password && (
                <p className="text-red-500 text-xs mt-1.5 flex items-center gap-1">
                  <span>⚠</span> {errors.password.message}
                </p>
              )}
            </div>

            {/* Submit */}
            <button
              type="submit"
              disabled={loading}
              className="w-full py-3.5 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white font-semibold rounded-xl transition-all duration-150 disabled:opacity-60 disabled:cursor-not-allowed flex items-center justify-center gap-2 mt-2 shadow-lg shadow-blue-200"
            >
              {loading ? (
                <>
                  <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  Đang đăng nhập...
                </>
              ) : (
                'Đăng nhập'
              )}
            </button>
          </form>

          {/* Footer */}
          <p className="text-center text-xs text-gray-400 mt-10">
            © 2026 Hệ thống Quản lý Sinh viên. All rights reserved.
          </p>
        </div>
      </div>
    </div>
  )
}
