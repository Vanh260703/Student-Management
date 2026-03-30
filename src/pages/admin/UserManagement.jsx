import { useEffect, useState, useCallback } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'react-toastify'
import { usersApi } from '@/api/users'
import Modal from '@/components/common/Modal'
import Pagination from '@/components/common/Pagination'
import LoadingSpinner from '@/components/common/LoadingSpinner'

const userSchema = z.object({
  fullName: z.string().min(2, 'Họ tên phải ít nhất 2 ký tự'),
  email: z.string().email('Email không hợp lệ'),
  role: z.enum(['ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT']),
  password: z.string().min(6, 'Mật khẩu phải ít nhất 6 ký tự').optional().or(z.literal('')),
})

const roleBadge = {
  ROLE_ADMIN: 'bg-purple-100 text-purple-700',
  ROLE_TEACHER: 'bg-blue-100 text-blue-700',
  ROLE_STUDENT: 'bg-green-100 text-green-700',
}
const roleLabel = {
  ROLE_ADMIN: 'Quản trị',
  ROLE_TEACHER: 'Giảng viên',
  ROLE_STUDENT: 'Sinh viên',
}

export default function UserManagement() {
  const [users, setUsers] = useState([])
  const [totalPages, setTotalPages] = useState(0)
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [roleFilter, setRoleFilter] = useState('')
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [editingUser, setEditingUser] = useState(null)
  const [saving, setSaving] = useState(false)

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: zodResolver(userSchema),
  })

  const fetchUsers = useCallback(async () => {
    try {
      setLoading(true)
      const { data } = await usersApi.getAll({
        page,
        size: 10,
        search: search || undefined,
        role: roleFilter || undefined,
      })
      setUsers(data.content ?? data)
      setTotalPages(data.totalPages ?? 1)
    } catch {
      toast.error('Không thể tải danh sách người dùng')
    } finally {
      setLoading(false)
    }
  }, [page, search, roleFilter])

  useEffect(() => { fetchUsers() }, [fetchUsers])

  const openCreate = () => {
    setEditingUser(null)
    reset({ fullName: '', email: '', role: 'ROLE_STUDENT', password: '' })
    setModalOpen(true)
  }

  const openEdit = (user) => {
    setEditingUser(user)
    reset({ fullName: user.fullName, email: user.email, role: user.role, password: '' })
    setModalOpen(true)
  }

  const onSubmit = async (values) => {
    try {
      setSaving(true)
      const payload = { ...values }
      if (!payload.password) delete payload.password
      if (editingUser) {
        await usersApi.update(editingUser.id, payload)
        toast.success('Cập nhật người dùng thành công!')
      } else {
        await usersApi.create(payload)
        toast.success('Tạo người dùng thành công!')
      }
      setModalOpen(false)
      fetchUsers()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Có lỗi xảy ra')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Bạn chắc chắn muốn xóa người dùng này?')) return
    try {
      await usersApi.delete(id)
      toast.success('Đã xóa người dùng')
      fetchUsers()
    } catch {
      toast.error('Không thể xóa người dùng')
    }
  }

  const handleToggleBlock = async (user) => {
    try {
      if (user.isActive) {
        await usersApi.block(user.id)
        toast.success('Đã khóa tài khoản')
      } else {
        await usersApi.unblock(user.id)
        toast.success('Đã mở khóa tài khoản')
      }
      fetchUsers()
    } catch {
      toast.error('Có lỗi xảy ra')
    }
  }

  return (
    <div>
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Quản lý người dùng</h2>
        <button
          onClick={openCreate}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium text-sm"
        >
          + Thêm người dùng
        </button>
      </div>

      {/* Filters */}
      <div className="flex flex-col sm:flex-row gap-3 mb-4">
        <input
          type="text"
          placeholder="Tìm kiếm theo tên, email..."
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(0) }}
          className="flex-1 px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <select
          value={roleFilter}
          onChange={(e) => { setRoleFilter(e.target.value); setPage(0) }}
          className="px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="">Tất cả vai trò</option>
          <option value="ROLE_ADMIN">Quản trị</option>
          <option value="ROLE_TEACHER">Giảng viên</option>
          <option value="ROLE_STUDENT">Sinh viên</option>
        </select>
      </div>

      {/* Table */}
      <div className="bg-white rounded-xl shadow-sm overflow-x-auto">
        {loading ? (
          <LoadingSpinner />
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-600 uppercase text-xs">
              <tr>
                <th className="px-4 py-3 text-left">Họ tên</th>
                <th className="px-4 py-3 text-left">Email</th>
                <th className="px-4 py-3 text-left">Vai trò</th>
                <th className="px-4 py-3 text-left">Trạng thái</th>
                <th className="px-4 py-3 text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {users.length === 0 ? (
                <tr>
                  <td colSpan={5} className="text-center py-10 text-gray-400">
                    Không có dữ liệu
                  </td>
                </tr>
              ) : (
                users.map((user) => (
                  <tr key={user.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 font-medium text-gray-800">
                      <div className="flex items-center gap-2">
                        <div className="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center text-white text-xs font-semibold flex-shrink-0">
                          {user.fullName?.[0]?.toUpperCase()}
                        </div>
                        {user.fullName}
                      </div>
                    </td>
                    <td className="px-4 py-3 text-gray-600">{user.email}</td>
                    <td className="px-4 py-3">
                      <span className={`px-2 py-0.5 rounded-full text-xs font-semibold ${roleBadge[user.role]}`}>
                        {roleLabel[user.role]}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <span className={`px-2 py-0.5 rounded-full text-xs font-semibold ${user.isActive ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                        {user.isActive ? 'Hoạt động' : 'Bị khóa'}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => openEdit(user)}
                          className="px-3 py-1 text-xs bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100"
                        >
                          Sửa
                        </button>
                        <button
                          onClick={() => handleToggleBlock(user)}
                          className={`px-3 py-1 text-xs rounded-lg ${user.isActive ? 'bg-orange-50 text-orange-600 hover:bg-orange-100' : 'bg-green-50 text-green-600 hover:bg-green-100'}`}
                        >
                          {user.isActive ? 'Khóa' : 'Mở khóa'}
                        </button>
                        <button
                          onClick={() => handleDelete(user.id)}
                          className="px-3 py-1 text-xs bg-red-50 text-red-600 rounded-lg hover:bg-red-100"
                        >
                          Xóa
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        )}
      </div>

      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />

      {/* Modal */}
      <Modal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editingUser ? 'Chỉnh sửa người dùng' : 'Thêm người dùng mới'}
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Họ và tên</label>
            <input
              {...register('fullName')}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            />
            {errors.fullName && <p className="text-red-500 text-xs mt-1">{errors.fullName.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input
              {...register('email')}
              type="email"
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            />
            {errors.email && <p className="text-red-500 text-xs mt-1">{errors.email.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Vai trò</label>
            <select
              {...register('role')}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            >
              <option value="ROLE_STUDENT">Sinh viên</option>
              <option value="ROLE_TEACHER">Giảng viên</option>
              <option value="ROLE_ADMIN">Quản trị</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Mật khẩu {editingUser && <span className="text-gray-400 font-normal">(để trống nếu không đổi)</span>}
            </label>
            <input
              {...register('password')}
              type="password"
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            />
            {errors.password && <p className="text-red-500 text-xs mt-1">{errors.password.message}</p>}
          </div>

          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={() => setModalOpen(false)}
              className="px-4 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50"
            >
              Hủy
            </button>
            <button
              type="submit"
              disabled={saving}
              className="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-60"
            >
              {saving ? 'Đang lưu...' : editingUser ? 'Cập nhật' : 'Tạo mới'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
