import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { toast } from 'react-toastify'
import { profileApi } from '@/api/profile'
import { authApi } from '@/api/auth'
import useAuthStore from '@/store/authStore'
import LoadingSpinner from '@/components/common/LoadingSpinner'

export default function TeacherProfile() {
  const { user, updateUser } = useAuthStore()
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [tab, setTab] = useState('info')

  const { register, handleSubmit, reset, formState: { errors } } = useForm()
  const { register: regPw, handleSubmit: handlePw, reset: resetPw } = useForm()

  useEffect(() => {
    profileApi.getTeacherProfile()
      .then((res) => {
        setProfile(res.data)
        reset(res.data)
      })
      .catch(() => toast.error('Không thể tải hồ sơ'))
      .finally(() => setLoading(false))
  }, [reset])

  const onSaveProfile = async (values) => {
    try {
      setSaving(true)
      const { data } = await profileApi.updateTeacherProfile(values)
      setProfile(data)
      updateUser({ ...user, fullName: data.fullName, avatarUrl: data.avatarUrl })
      toast.success('Cập nhật hồ sơ thành công!')
    } catch {
      toast.error('Cập nhật thất bại')
    } finally {
      setSaving(false)
    }
  }

  const onChangePassword = async (values) => {
    if (values.newPassword !== values.confirmPassword) {
      toast.error('Mật khẩu xác nhận không khớp')
      return
    }
    try {
      await authApi.changePassword({ oldPassword: values.oldPassword, newPassword: values.newPassword })
      toast.success('Đổi mật khẩu thành công!')
      resetPw()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Đổi mật khẩu thất bại')
    }
  }

  if (loading) return <LoadingSpinner />

  return (
    <div className="max-w-2xl mx-auto">
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Hồ sơ cá nhân</h2>

      {/* Avatar */}
      <div className="bg-white rounded-xl shadow-sm p-6 mb-4 flex items-center gap-4">
        <div className="w-20 h-20 rounded-full bg-blue-600 flex items-center justify-center text-white text-3xl font-bold overflow-hidden">
          {profile?.avatarUrl ? (
            <img src={profile.avatarUrl} alt="" className="w-full h-full object-cover" />
          ) : (
            profile?.fullName?.[0]?.toUpperCase()
          )}
        </div>
        <div>
          <p className="text-xl font-semibold text-gray-800">{profile?.fullName}</p>
          <p className="text-sm text-gray-500">{profile?.email}</p>
          <p className="text-sm text-gray-500">{profile?.departmentName}</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-2 mb-4">
        {[{ key: 'info', label: 'Thông tin' }, { key: 'password', label: 'Đổi mật khẩu' }].map((t) => (
          <button key={t.key} onClick={() => setTab(t.key)}
            className={`px-4 py-2 rounded-lg text-sm font-medium ${tab === t.key ? 'bg-blue-600 text-white' : 'bg-white border border-gray-300 text-gray-600 hover:bg-gray-50'}`}>
            {t.label}
          </button>
        ))}
      </div>

      {tab === 'info' && (
        <div className="bg-white rounded-xl shadow-sm p-6">
          <form onSubmit={handleSubmit(onSaveProfile)} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Họ và tên</label>
              <input {...register('fullName')} className="w-full px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input {...register('email')} type="email" disabled className="w-full px-4 py-2 border border-gray-200 rounded-lg text-sm bg-gray-50 text-gray-500 cursor-not-allowed" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Số điện thoại</label>
              <input {...register('phone')} className="w-full px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
            </div>
            <button type="submit" disabled={saving}
              className="px-6 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700 disabled:opacity-60">
              {saving ? 'Đang lưu...' : 'Lưu thay đổi'}
            </button>
          </form>
        </div>
      )}

      {tab === 'password' && (
        <div className="bg-white rounded-xl shadow-sm p-6">
          <form onSubmit={handlePw(onChangePassword)} className="space-y-4">
            {[
              { name: 'oldPassword', label: 'Mật khẩu hiện tại' },
              { name: 'newPassword', label: 'Mật khẩu mới' },
              { name: 'confirmPassword', label: 'Xác nhận mật khẩu mới' },
            ].map((f) => (
              <div key={f.name}>
                <label className="block text-sm font-medium text-gray-700 mb-1">{f.label}</label>
                <input {...regPw(f.name)} type="password"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
            ))}
            <button type="submit" className="px-6 py-2 bg-blue-600 text-white rounded-lg text-sm hover:bg-blue-700">
              Đổi mật khẩu
            </button>
          </form>
        </div>
      )}
    </div>
  )
}
