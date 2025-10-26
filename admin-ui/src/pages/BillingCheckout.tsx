import { useState } from 'react'
import { BanknotesIcon, CreditCardIcon } from '@heroicons/react/24/outline'

export default function BillingCheckout() {
  const [selectedPatient, setSelectedPatient] = useState('')
  const [items, setItems] = useState([
    { id: '1', description: 'Consultation Fee', amount: 5000 },
    { id: '2', description: 'Lab Test - CBC', amount: 2500 },
  ])
  const [paymentMethod, setPaymentMethod] = useState('CASH')

  const subtotal = items.reduce((sum, item) => sum + item.amount, 0)
  const tax = 0
  const total = subtotal + tax

  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Billing Checkout</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <div className="card mb-6">
            <h2 className="text-xl font-bold text-gray-900 mb-4">Bill Items</h2>
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-200">
                  <th className="text-left py-3 px-4 font-semibold text-gray-700">Description</th>
                  <th className="text-right py-3 px-4 font-semibold text-gray-700">Amount</th>
                  <th className="text-right py-3 px-4 font-semibold text-gray-700">Actions</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item) => (
                  <tr key={item.id} className="border-b border-gray-100">
                    <td className="py-3 px-4">{item.description}</td>
                    <td className="py-3 px-4 text-right font-medium">
                      ₦{item.amount.toLocaleString()}
                    </td>
                    <td className="py-3 px-4 text-right">
                      <button className="text-red-600 hover:text-red-700 text-sm">Remove</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="mt-4">
              <button className="btn-secondary">Add Item</button>
            </div>
          </div>

          <div className="card">
            <h2 className="text-xl font-bold text-gray-900 mb-4">Payment Method</h2>
            <div className="space-y-3">
              <label className="flex items-center gap-3 p-3 border border-gray-300 rounded-lg cursor-pointer hover:bg-gray-50">
                <input
                  type="radio"
                  name="payment"
                  value="CASH"
                  checked={paymentMethod === 'CASH'}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className="form-radio"
                />
                <div className="flex items-center gap-2">
                  <BanknotesIcon className="h-5 w-5 text-gray-500" />
                  <span className="font-medium">Cash</span>
                </div>
              </label>
              <label className="flex items-center gap-3 p-3 border border-gray-300 rounded-lg cursor-pointer hover:bg-gray-50">
                <input
                  type="radio"
                  name="payment"
                  value="CARD"
                  checked={paymentMethod === 'CARD'}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className="form-radio"
                />
                <div className="flex items-center gap-2">
                  <CreditCardIcon className="h-5 w-5 text-gray-500" />
                  <span className="font-medium">Card Payment</span>
                </div>
              </label>
              <label className="flex items-center gap-3 p-3 border border-gray-300 rounded-lg cursor-pointer hover:bg-gray-50">
                <input
                  type="radio"
                  name="payment"
                  value="NHIA"
                  checked={paymentMethod === 'NHIA'}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className="form-radio"
                />
                <span className="font-medium">NHIA Claim</span>
              </label>
            </div>
          </div>
        </div>

        <div className="card h-fit">
          <h2 className="text-xl font-bold text-gray-900 mb-4">Summary</h2>
          <div className="space-y-3 mb-6">
            <div className="flex justify-between text-gray-600">
              <span>Subtotal</span>
              <span className="font-medium">₦{subtotal.toLocaleString()}</span>
            </div>
            <div className="flex justify-between text-gray-600">
              <span>Tax</span>
              <span className="font-medium">₦{tax.toLocaleString()}</span>
            </div>
            <div className="border-t border-gray-200 pt-3">
              <div className="flex justify-between text-lg font-bold">
                <span>Total</span>
                <span>₦{total.toLocaleString()}</span>
              </div>
            </div>
          </div>
          <button className="w-full btn-primary">
            Process Payment
          </button>
        </div>
      </div>
    </div>
  )
}

