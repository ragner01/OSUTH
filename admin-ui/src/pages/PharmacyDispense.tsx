import { useState } from 'react'
import { TruckIcon, CheckCircleIcon } from '@heroicons/react/24/outline'

export default function PharmacyDispense() {
  const [selectedType, setSelectedType] = useState('PENDING')

  const prescriptions = [
    {
      id: '1',
      patientName: 'Adebayo Ogunlana',
      mrn: 'MRN-001',
      medication: 'Paracetamol 500mg',
      quantity: 20,
      status: 'READY_TO_DISPENSE',
      prescribedAt: '2024-01-15 10:00',
    },
    {
      id: '2',
      patientName: 'Folake Adeyemi',
      mrn: 'MRN-002',
      medication: 'Amoxicillin 250mg',
      quantity: 14,
      status: 'DISPENSED',
      prescribedAt: '2024-01-15 09:30',
      dispensedAt: '2024-01-15 14:30',
    },
    {
      id: '3',
      patientName: 'Emeka Okoro',
      mrn: 'MRN-003',
      medication: 'Ibuprofen 400mg',
      quantity: 30,
      status: 'READY_TO_DISPENSE',
      prescribedAt: '2024-01-15 11:00',
    },
  ]

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'DISPENSED':
        return 'bg-green-100 text-green-800'
      case 'READY_TO_DISPENSE':
        return 'bg-blue-100 text-blue-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Pharmacy Dispense</h1>
        <select
          value={selectedType}
          onChange={(e) => setSelectedType(e.target.value)}
          className="input-field"
        >
          <option value="PENDING">Pending</option>
          <option value="READY_TO_DISPENSE">Ready to Dispense</option>
          <option value="DISPENSED">Dispensed</option>
        </select>
      </div>

      <div className="card">
        <table className="w-full">
          <thead>
            <tr className="border-b border-gray-200">
              <th className="text-left py-3 px-4 font-semibold text-gray-700">Patient</th>
              <th className="text-left py-3 px-4 font-semibold text-gray-700">MRN</th>
              <th className="text-left py-3 px-4 font-semibold text-gray-700">Medication</th>
              <th className="text-left py-3 px-4 font-semibold text-gray-700">Quantity</th>
              <th className="text-left py-3 px-4 font-semibold text-gray-700">Status</th>
              <th className="text-left py-3 px-4 font-semibold text-gray-700">Actions</th>
            </tr>
          </thead>
          <tbody>
            {prescriptions.map((prescription) => (
              <tr key={prescription.id} className="border-b border-gray-100 hover:bg-gray-50">
                <td className="py-3 px-4 font-medium">{prescription.patientName}</td>
                <td className="py-3 px-4 font-mono text-sm">{prescription.mrn}</td>
                <td className="py-3 px-4">{prescription.medication}</td>
                <td className="py-3 px-4">{prescription.quantity}</td>
                <td className="py-3 px-4">
                  <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(prescription.status)}`}>
                    {prescription.status.replace(/_/g, ' ')}
                  </span>
                </td>
                <td className="py-3 px-4">
                  {prescription.status === 'READY_TO_DISPENSE' ? (
                    <button className="btn-primary text-sm">Dispense</button>
                  ) : (
                    <div className="flex items-center gap-1 text-sm text-gray-500">
                      <CheckCircleIcon className="h-4 w-4" />
                      Dispersed
                    </div>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

