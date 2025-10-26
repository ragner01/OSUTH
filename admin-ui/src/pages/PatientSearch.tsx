import { useState } from 'react'
import { MagnifyingGlassIcon } from '@heroicons/react/24/outline'

export default function PatientSearch() {
  const [searchQuery, setSearchQuery] = useState('')
  const [patients, setPatients] = useState<any[]>([])
  const [isLoading, setIsLoading] = useState(false)

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    
    // Mock data for demo
    setTimeout(() => {
      setPatients([
        {
          id: '1',
          mrn: 'MRN-001',
          firstName: 'Adebayo',
          lastName: 'Ogunlana',
          dateOfBirth: '1985-03-15',
          gender: 'Male',
          primaryPhone: '+2348021234567',
        },
        {
          id: '2',
          mrn: 'MRN-002',
          firstName: 'Folake',
          lastName: 'Adeyemi',
          dateOfBirth: '1990-07-22',
          gender: 'Female',
          primaryPhone: '+2348134567890',
        },
      ])
      setIsLoading(false)
    }, 500)
  }

  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Patient Search</h1>

      <form onSubmit={handleSearch} className="mb-6">
        <div className="flex gap-2">
          <div className="flex-1 relative">
            <MagnifyingGlassIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search by MRN, NIN, name, or phone..."
              className="input-field pl-10"
            />
          </div>
          <button type="submit" className="btn-primary">
            Search
          </button>
        </div>
      </form>

      {isLoading && (
        <div className="text-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
        </div>
      )}

      {!isLoading && patients.length > 0 && (
        <div className="card">
          <table className="w-full">
            <thead>
              <tr className="border-b border-gray-200">
                <th className="text-left py-3 px-4 font-semibold text-gray-700">MRN</th>
                <th className="text-left py-3 px-4 font-semibold text-gray-700">Name</th>
                <th className="text-left py-3 px-4 font-semibold text-gray-700">DOB</th>
                <th className="text-left py-3 px-4 font-semibold text-gray-700">Gender</th>
                <th className="text-left py-3 px-4 font-semibold text-gray-700">Phone</th>
                <th className="text-left py-3 px-4 font-semibold text-gray-700">Actions</th>
              </tr>
            </thead>
            <tbody>
              {patients.map((patient) => (
                <tr key={patient.id} className="border-b border-gray-100 hover:bg-gray-50">
                  <td className="py-3 px-4 font-mono text-sm">{patient.mrn}</td>
                  <td className="py-3 px-4">{patient.firstName} {patient.lastName}</td>
                  <td className="py-3 px-4">{patient.dateOfBirth}</td>
                  <td className="py-3 px-4">{patient.gender}</td>
                  <td className="py-3 px-4">{patient.primaryPhone}</td>
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
      )}

      {!isLoading && patients.length === 0 && searchQuery && (
        <div className="card text-center py-12">
          <p className="text-gray-500">No patients found matching your search.</p>
        </div>
      )}
    </div>
  )
}

