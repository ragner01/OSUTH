import { useState } from 'react'
import { CalendarIcon, ClockIcon } from '@heroicons/react/24/outline'

export default function AppointmentBooking() {
  const [selectedDate, setSelectedDate] = useState('')
  const [selectedTime, setSelectedTime] = useState('')
  const [patientId, setPatientId] = useState('')
  const [clinicId, setClinicId] = useState('')

  const clinics = [
    { id: '1', name: 'General Medicine' },
    { id: '2', name: 'Cardiology' },
    { id: '3', name: 'Pediatrics' },
  ]

  const timeSlots = [
    '08:00', '09:00', '10:00', '11:00',
    '13:00', '14:00', '15:00', '16:00',
  ]

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    alert('Appointment booking functionality - backend integration needed')
  }

  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Book Appointment</h1>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Appointment Details</h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Patient ID / Search
              </label>
              <input
                type="text"
                value={patientId}
                onChange={(e) => setPatientId(e.target.value)}
                placeholder="Search patient..."
                className="input-field"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Clinic</label>
              <select
                value={clinicId}
                onChange={(e) => setClinicId(e.target.value)}
                className="input-field"
              >
                <option value="">Select clinic...</option>
                {clinics.map((clinic) => (
                  <option key={clinic.id} value={clinic.id}>
                    {clinic.name}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Date</label>
              <input
                type="date"
                value={selectedDate}
                onChange={(e) => setSelectedDate(e.target.value)}
                min={new Date().toISOString().split('T')[0]}
                className="input-field"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Time</label>
              <div className="grid grid-cols-4 gap-2">
                {timeSlots.map((slot) => (
                  <button
                    key={slot}
                    type="button"
                    onClick={() => setSelectedTime(slot)}
                    className={`py-2 px-3 rounded-lg border transition-colors ${
                      selectedTime === slot
                        ? 'bg-primary-600 text-white border-primary-600'
                        : 'bg-white text-gray-700 border-gray-300 hover:border-primary-500'
                    }`}
                  >
                    {slot}
                  </button>
                ))}
              </div>
            </div>

            <button type="submit" className="w-full btn-primary">
              Book Appointment
            </button>
          </form>
        </div>

        <div className="card">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Today's Schedule</h2>
          <div className="space-y-2">
            {[
              { time: '09:00', patient: 'Adebayo Ogunlana', clinic: 'General Medicine' },
              { time: '10:30', patient: 'Folake Adeyemi', clinic: 'Cardiology' },
              { time: '14:00', patient: 'Emeka Okoro', clinic: 'Pediatrics' },
            ].map((apt, idx) => (
              <div key={idx} className="p-3 bg-gray-50 rounded-lg">
                <div className="flex items-center gap-2 mb-1">
                  <ClockIcon className="h-4 w-4 text-gray-500" />
                  <span className="font-medium text-gray-900">{apt.time}</span>
                </div>
                <p className="text-sm text-gray-700 font-medium">{apt.patient}</p>
                <p className="text-xs text-gray-500">{apt.clinic}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

