import { useState } from 'react'
import { CheckCircleIcon, ClockIcon } from '@heroicons/react/24/outline'

export default function LabResults() {
  const [selectedStatus, setSelectedStatus] = useState('ALL')

  const results = [
    {
      id: '1',
      patientName: 'Adebayo Ogunlana',
      mrn: 'MRN-001',
      testType: 'Complete Blood Count',
      status: 'VERIFIED',
      orderedAt: '2024-01-15 09:30',
      resultedAt: '2024-01-15 14:00',
      verifiedAt: '2024-01-15 16:00',
    },
    {
      id: '2',
      patientName: 'Folake Adeyemi',
      mrn: 'MRN-002',
      testType: 'Liver Function Test',
      status: 'RESULTED',
      orderedAt: '2024-01-15 10:00',
      resultedAt: '2024-01-15 15:30',
      verifiedAt: null,
    },
    {
      id: '3',
      patientName: 'Emeka Okoro',
      mrn: 'MRN-003',
      testType: 'Blood Sugar',
      status: 'IN_PROGRESS',
      orderedAt: '2024-01-15 11:00',
      resultedAt: null,
      verifiedAt: null,
    },
  ]

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'VERIFIED':
        return 'bg-green-100 text-green-800'
      case 'RESULTED':
        return 'bg-blue-100 text-blue-800'
      case 'IN_PROGRESS':
        return 'bg-yellow-100 text-yellow-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Lab Results</h1>
        <select
          value={selectedStatus}
          onChange={(e) => setSelectedStatus(e.target.value)}
          className="input-field"
        >
          <option value="ALL">All Status</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="RESULTED">Resulted</option>
          <option value="VERIFIED">Verified</option>
        </select>
      </div>

      <div className="card">
        <table className="w-full">
          <thead>
            <tr className="border-b border-gray-200">
              <th className="text-left py-3 px-4 font-semibold text-gray-700">Patient</th>
              <th className="text-left py-3 px-4 font-semibold text-gray-700">MRN</th>
              <th className="text-left py-3 px-4 font-semibold text-gray-700">Test</th>
              <th className="text-left py-3 px-4 font-semibold text-gray-700">Status</th>
              <th className="text-left py-3 px-4 font-semibold text-gray-700">Timeline</th>
              <th className="text-left py-3 px-4 font-semibold text-gray-700">Actions</th>
            </tr>
          </thead>
          <tbody>
            {results.map((result) => (
              <tr key={result.id} className="border-b border-gray-100 hover:bg-gray-50">
                <td className="py-3 px-4 font-medium">{result.patientName}</td>
                <td className="py-3 px-4 font-mono text-sm">{result.mrn}</td>
                <td className="py-3 px-4">{result.testType}</td>
                <td className="py-3 px-4">
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(result.status)}`}>
                    {result.status}
                  </span>
                </td>
                <td className="py-3 px-4 text-sm text-gray-600">
                  <div>
                    <div className="flex items-center gap-1 mb-1">
                      <ClockIcon className="h-3 w-3" />
                      <span>Ordered: {result.orderedAt}</span>
                    </div>
                    {result.resultedAt && (
                      <div className="flex items-center gap-1 mb-1">
                        <CheckCircleIcon className="h-3 w-3" />
                        <span>Resulted: {result.resultedAt}</span>
                      </div>
                    )}
                    {result.verifiedAt && (
                      <div className="flex items-center gap-1">
                        <CheckCircleIcon className="h-3 w-3 text-green-500" />
                        <span>Verified: {result.verifiedAt}</span>
                      </div>
                    )}
                  </div>
                </td>
                <td className="py-3 px-4">
                  <button className="text-primary-600 hover:text-primary-700 text-sm font-medium">
                    View Details
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

