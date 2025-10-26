import { Link } from 'react-router-dom'
import {
  UserGroupIcon,
  CalendarIcon,
  BeakerIcon,
  ClipboardDocumentCheckIcon,
  BanknotesIcon,
} from '@heroicons/react/24/outline'

const stats = [
  { name: 'Patients Today', value: '247', icon: UserGroupIcon, color: 'bg-blue-500', href: '/patients' },
  { name: 'Appointments Today', value: '89', icon: CalendarIcon, color: 'bg-green-500', href: '/appointments' },
  { name: 'Lab Results Pending', value: '12', icon: BeakerIcon, color: 'bg-yellow-500', href: '/lab-results' },
  { name: 'Pharmacy Dispense', value: '34', icon: ClipboardDocumentCheckIcon, color: 'bg-purple-500', href: '/pharmacy' },
  { name: 'Billing Checkout', value: '18', icon: BanknotesIcon, color: 'bg-red-500', href: '/billing' },
]

export default function Dashboard() {
  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Dashboard</h1>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {stats.map((stat) => (
          <Link key={stat.name} to={stat.href}>
            <div className="card hover:shadow-lg transition-shadow">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">{stat.name}</p>
                  <p className="text-3xl font-bold text-gray-900">{stat.value}</p>
                </div>
                <div className={`${stat.color} p-4 rounded-full`}>
                  <stat.icon className="h-8 w-8 text-white" />
                </div>
              </div>
            </div>
          </Link>
        ))}
      </div>

      <div className="mt-8 grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Recent Activity</h2>
          <div className="space-y-3">
            <div className="flex items-center justify-between py-2 border-b border-gray-100">
              <div>
                <p className="font-medium">Patient registered</p>
                <p className="text-sm text-gray-500">2 minutes ago</p>
              </div>
            </div>
            <div className="flex items-center justify-between py-2 border-b border-gray-100">
              <div>
                <p className="font-medium">Appointment booked</p>
                <p className="text-sm text-gray-500">5 minutes ago</p>
              </div>
            </div>
            <div className="flex items-center justify-between py-2 border-b border-gray-100">
              <div>
                <p className="font-medium">Lab result verified</p>
                <p className="text-sm text-gray-500">10 minutes ago</p>
              </div>
            </div>
          </div>
        </div>

        <div className="card">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Quick Actions</h2>
          <div className="space-y-2">
            <Link to="/patients" className="block btn-primary text-center">Search Patient</Link>
            <Link to="/appointments" className="block btn-secondary text-center">Book Appointment</Link>
            <Link to="/lab-results" className="block btn-secondary text-center">View Lab Results</Link>
          </div>
        </div>
      </div>
    </div>
  )
}

