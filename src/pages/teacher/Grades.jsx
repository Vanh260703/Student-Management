import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { toast } from 'react-toastify'
import { profileApi } from '@/api/profile'
import { gradesApi } from '@/api/grades'
import Modal from '@/components/common/Modal'
import LoadingSpinner from '@/components/common/LoadingSpinner'

export default function TeacherGrades() {
  const [classes, setClasses] = useState([])
  const [selectedClass, setSelectedClass] = useState(null)
  const [grades, setGrades] = useState([])
  const [loadingClasses, setLoadingClasses] = useState(true)
  const [loadingGrades, setLoadingGrades] = useState(false)
  const [gradeModal, setGradeModal] = useState(null) // { enrollmentId, gradeId, studentName, existing }

  const { register, handleSubmit, reset } = useForm()

  useEffect(() => {
    profileApi.getTeacherClasses()
      .then((res) => setClasses(res.data?.content ?? res.data ?? []))
      .catch(() => toast.error('Không thể tải lớp học'))
      .finally(() => setLoadingClasses(false))
  }, [])

  const loadGrades = async (cls) => {
    setSelectedClass(cls)
    setLoadingGrades(true)
    try {
      const { data } = await gradesApi.getClassGrades(cls.classId ?? cls.id)
      setGrades(data?.enrollments ?? data ?? [])
    } catch {
      toast.error('Không thể tải điểm')
    } finally {
      setLoadingGrades(false)
    }
  }

  const openGradeModal = (enrollment) => {
    const existing = enrollment.grade
    setGradeModal({ enrollmentId: enrollment.enrollmentId, gradeId: existing?.id, studentName: enrollment.studentName, existing })
    reset({
      midtermScore: existing?.midtermScore ?? '',
      finalScore: existing?.finalScore ?? '',
      practicalScore: existing?.practicalScore ?? '',
    })
  }

  const onSubmitGrade = async (values) => {
    try {
      const payload = {
        midtermScore: parseFloat(values.midtermScore) || 0,
        finalScore: parseFloat(values.finalScore) || 0,
        practicalScore: values.practicalScore ? parseFloat(values.practicalScore) : undefined,
      }
      if (gradeModal.gradeId) {
        await gradesApi.updateGrade(gradeModal.gradeId, payload)
        toast.success('Cập nhật điểm thành công!')
      } else {
        await gradesApi.enterGrade(gradeModal.enrollmentId, payload)
        toast.success('Nhập điểm thành công!')
      }
      setGradeModal(null)
      loadGrades(selectedClass)
    } catch (err) {
      toast.error(err.response?.data?.message || 'Có lỗi xảy ra')
    }
  }

  const handlePublish = async (gradeId) => {
    if (!window.confirm('Xác nhận công bố điểm cho sinh viên?')) return
    try {
      await gradesApi.publishGrade(gradeId)
      toast.success('Đã công bố điểm!')
      loadGrades(selectedClass)
    } catch {
      toast.error('Công bố điểm thất bại')
    }
  }

  if (loadingClasses) return <LoadingSpinner />

  return (
    <div>
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Quản lý điểm số</h2>

      {!selectedClass ? (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {classes.length === 0 ? (
            <div className="col-span-3 text-center py-16 text-gray-400">Không có lớp học</div>
          ) : (
            classes.map((c) => (
              <button
                key={c.classId ?? c.id}
                onClick={() => loadGrades(c)}
                className="bg-white rounded-xl shadow-sm p-5 text-left hover:shadow-md transition hover:border-blue-300 border-2 border-transparent"
              >
                <h3 className="font-semibold text-gray-800">{c.className ?? c.name}</h3>
                <p className="text-sm text-gray-500 mt-1">{c.subjectName}</p>
                <p className="text-sm text-gray-500">{c.currentStudents ?? 0} sinh viên</p>
              </button>
            ))
          )}
        </div>
      ) : (
        <div>
          <button
            onClick={() => setSelectedClass(null)}
            className="mb-4 text-sm text-blue-600 hover:underline flex items-center gap-1"
          >
            ← Quay lại danh sách lớp
          </button>
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-800">
              {selectedClass.className} — {selectedClass.subjectName}
            </h3>
          </div>

          {loadingGrades ? (
            <LoadingSpinner />
          ) : (
            <div className="bg-white rounded-xl shadow-sm overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-gray-50 text-gray-600 uppercase text-xs">
                  <tr>
                    <th className="px-4 py-3 text-left">Sinh viên</th>
                    <th className="px-4 py-3 text-center">Giữa kỳ</th>
                    <th className="px-4 py-3 text-center">Cuối kỳ</th>
                    <th className="px-4 py-3 text-center">Thực hành</th>
                    <th className="px-4 py-3 text-center">Tổng kết</th>
                    <th className="px-4 py-3 text-center">Trạng thái</th>
                    <th className="px-4 py-3 text-right">Thao tác</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {grades.length === 0 ? (
                    <tr><td colSpan={7} className="text-center py-10 text-gray-400">Chưa có dữ liệu điểm</td></tr>
                  ) : (
                    grades.map((e) => (
                      <tr key={e.enrollmentId} className="hover:bg-gray-50">
                        <td className="px-4 py-3 font-medium text-gray-800">{e.studentName}</td>
                        <td className="px-4 py-3 text-center">{e.grade?.midtermScore ?? '—'}</td>
                        <td className="px-4 py-3 text-center">{e.grade?.finalScore ?? '—'}</td>
                        <td className="px-4 py-3 text-center">{e.grade?.practicalScore ?? '—'}</td>
                        <td className="px-4 py-3 text-center font-semibold">
                          {e.grade?.finalGrade != null ? (
                            <span className={e.grade.finalGrade >= 5 ? 'text-green-600' : 'text-red-500'}>
                              {e.grade.finalGrade}
                            </span>
                          ) : '—'}
                        </td>
                        <td className="px-4 py-3 text-center">
                          {e.grade ? (
                            <span className={`px-2 py-0.5 rounded-full text-xs font-semibold ${e.grade.isPublished ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'}`}>
                              {e.grade.isPublished ? 'Đã công bố' : 'Chưa công bố'}
                            </span>
                          ) : (
                            <span className="text-gray-400 text-xs">Chưa nhập</span>
                          )}
                        </td>
                        <td className="px-4 py-3">
                          <div className="flex items-center justify-end gap-2">
                            <button
                              onClick={() => openGradeModal(e)}
                              className="px-3 py-1 text-xs bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100"
                            >
                              {e.grade ? 'Sửa' : 'Nhập điểm'}
                            </button>
                            {e.grade && !e.grade.isPublished && (
                              <button
                                onClick={() => handlePublish(e.grade.id)}
                                className="px-3 py-1 text-xs bg-green-50 text-green-600 rounded-lg hover:bg-green-100"
                              >
                                Công bố
                              </button>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Grade Modal */}
      <Modal open={!!gradeModal} onClose={() => setGradeModal(null)} title={`Nhập điểm — ${gradeModal?.studentName}`} size="sm">
        <form onSubmit={handleSubmit(onSubmitGrade)} className="space-y-4">
          {[
            { name: 'midtermScore', label: 'Điểm giữa kỳ' },
            { name: 'finalScore', label: 'Điểm cuối kỳ' },
            { name: 'practicalScore', label: 'Điểm thực hành (tuỳ chọn)' },
          ].map((f) => (
            <div key={f.name}>
              <label className="block text-sm font-medium text-gray-700 mb-1">{f.label}</label>
              <input
                {...register(f.name)}
                type="number"
                step="0.1"
                min="0"
                max="10"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
              />
            </div>
          ))}
          <div className="flex justify-end gap-3 pt-2">
            <button type="button" onClick={() => setGradeModal(null)} className="px-4 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50">Hủy</button>
            <button type="submit" className="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700">Lưu điểm</button>
          </div>
        </form>
      </Modal>
    </div>
  )
}
