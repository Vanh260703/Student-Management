import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import { gradesApi } from '@/api/grades'
import LoadingSpinner from '@/components/common/LoadingSpinner'

export default function StudentGrades() {
  const [grades, setGrades] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    gradesApi.getMyGrades()
      .then((res) => setGrades(res.data?.content ?? res.data ?? []))
      .catch(() => toast.error('Không thể tải điểm số'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <LoadingSpinner />

  const totalPassed = grades.filter((g) => (g.finalGrade ?? g.grade?.finalGrade) >= 5).length
  const totalFailed = grades.filter((g) => (g.finalGrade ?? g.grade?.finalGrade) < 5 && (g.finalGrade ?? g.grade?.finalGrade) != null).length
  const avgGPA = grades.length
    ? (grades.reduce((acc, g) => acc + (g.finalGrade ?? g.grade?.finalGrade ?? 0), 0) / grades.length).toFixed(2)
    : '—'

  return (
    <div>
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Bảng điểm</h2>

      {/* Summary */}
      <div className="grid grid-cols-3 gap-4 mb-6">
        <div className="bg-white rounded-xl shadow-sm p-4 text-center">
          <p className="text-2xl font-bold text-blue-600">{avgGPA}</p>
          <p className="text-sm text-gray-500 mt-1">GPA trung bình</p>
        </div>
        <div className="bg-white rounded-xl shadow-sm p-4 text-center">
          <p className="text-2xl font-bold text-green-600">{totalPassed}</p>
          <p className="text-sm text-gray-500 mt-1">Môn đạt</p>
        </div>
        <div className="bg-white rounded-xl shadow-sm p-4 text-center">
          <p className="text-2xl font-bold text-red-500">{totalFailed}</p>
          <p className="text-sm text-gray-500 mt-1">Môn trượt</p>
        </div>
      </div>

      {/* Table */}
      <div className="bg-white rounded-xl shadow-sm overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-gray-600 uppercase text-xs">
            <tr>
              <th className="px-4 py-3 text-left">Môn học / Lớp</th>
              <th className="px-4 py-3 text-center">Giữa kỳ</th>
              <th className="px-4 py-3 text-center">Cuối kỳ</th>
              <th className="px-4 py-3 text-center">Thực hành</th>
              <th className="px-4 py-3 text-center">Tổng kết</th>
              <th className="px-4 py-3 text-center">Kết quả</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {grades.length === 0 ? (
              <tr><td colSpan={6} className="text-center py-10 text-gray-400">Chưa có điểm</td></tr>
            ) : (
              grades.map((g, i) => {
                const final = g.finalGrade ?? g.grade?.finalGrade
                const passed = final != null && final >= 5
                return (
                  <tr key={i} className="hover:bg-gray-50">
                    <td className="px-4 py-3">
                      <p className="font-medium text-gray-800">{g.subjectName ?? g.className}</p>
                      <p className="text-xs text-gray-400">{g.className ?? g.semester}</p>
                    </td>
                    <td className="px-4 py-3 text-center">{g.midtermScore ?? g.grade?.midtermScore ?? '—'}</td>
                    <td className="px-4 py-3 text-center">{g.finalScore ?? g.grade?.finalScore ?? '—'}</td>
                    <td className="px-4 py-3 text-center">{g.practicalScore ?? g.grade?.practicalScore ?? '—'}</td>
                    <td className="px-4 py-3 text-center font-bold">
                      {final != null ? (
                        <span className={passed ? 'text-green-600' : 'text-red-500'}>{final}</span>
                      ) : '—'}
                    </td>
                    <td className="px-4 py-3 text-center">
                      {final != null ? (
                        <span className={`px-2 py-0.5 rounded-full text-xs font-semibold ${passed ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                          {passed ? 'Đạt' : 'Trượt'}
                        </span>
                      ) : (
                        <span className="text-gray-400 text-xs">Chưa có</span>
                      )}
                    </td>
                  </tr>
                )
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
