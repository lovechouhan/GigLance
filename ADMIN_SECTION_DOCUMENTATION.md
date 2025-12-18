# Admin Section Documentation

## Overview
A complete admin panel has been created to manage all payment flows and contracts in the application. Only users with `ROLE_ADMIN` can access this section.

## Project Structure

### Directory
```
src/main/resources/templates/admin/
├── dashboard.html          # Main admin dashboard with statistics
├── payments.html           # All payments/escrow management
├── payment-details.html    # Individual payment details and status update
├── contracts.html          # All contracts overview
└── contract-details.html   # Individual contract details
```

## Controller: AdminController
**Location:** `src/main/java/com/minor/freelancing/Controller/AdminController.java`

### Features:
- **Authentication Check:** All endpoints verify user is authenticated
- **Authorization Check:** All endpoints verify user has `ROLE_ADMIN`
- **Auto Redirect:** Non-authenticated users → `/login`
- **Auto Redirect:** Non-admin users → `/unauthorized`

### Endpoints:

1. **GET `/admin/dashboard`**
   - Main dashboard with payment statistics
   - Shows: Total payments, pending payments, completed payments, total contracts
   - Recent payments preview
   - Quick action links

2. **GET `/admin/payments`**
   - List all escrow/payment records
   - Shows: Payment ID, Client, Freelancer, Project, Amount, Status, Date
   - View button to see payment details

3. **GET `/admin/payment/{id}`**
   - Detailed view of a specific payment
   - Shows: Client info, Freelancer info, Project info
   - Status update form with dropdown (PENDING, COMPLETED, FAILED, REFUNDED)
   - Add remarks functionality

4. **POST `/admin/payment/{id}/update-status`**
   - Update payment status in database
   - Accepts: status, remarks
   - Redirects back to payment details

5. **GET `/admin/contracts`**
   - List all contracts
   - Shows: Contract ID, Client, Freelancer, Project, Amount, Status, Payment Status
   - View button to see contract details

6. **GET `/admin/contract/{id}`**
   - Detailed view of a specific contract
   - Shows: All contract, client, freelancer, and project details
   - Timeline view
   - Status summary

## Features Implemented

### Security
- ✅ Role-based access control (ROLE_ADMIN only)
- ✅ Automatic authentication checks
- ✅ Automatic authorization checks
- ✅ Redirect to unauthorized page for non-admins

### Payment Management
- ✅ View all payments/escrows
- ✅ View payment details with client/freelancer/project info
- ✅ Update payment status (PENDING, COMPLETED, FAILED, REFUNDED)
- ✅ Add remarks to payments
- ✅ Payment statistics on dashboard

### Contract Management
- ✅ View all contracts
- ✅ View contract details
- ✅ Contract status tracking
- ✅ Payment status tracking
- ✅ Timeline view

### Dashboard
- ✅ Total payments amount
- ✅ Pending payments count
- ✅ Completed payments count
- ✅ Total contracts count
- ✅ Recent payments preview
- ✅ Quick action buttons

## How to Access

1. **User must have `ROLE_ADMIN` role** (set in database)
2. **Navigate to:** `http://localhost:8080/admin/dashboard`
3. **Automatically redirected to login if not authenticated**
4. **Automatically redirected to unauthorized if not admin**

## Setting Up Admin User

To make a user an admin, update the `user` table:
```sql
UPDATE user SET role = 'ROLE_ADMIN' WHERE email = 'admin@example.com';
```

Or during user registration in code:
```java
user.setRole("ROLE_ADMIN");
```

## UI/UX Features

### Dashboard
- 📊 Statistics cards with icons and color coding
- 📋 Recent payments preview (last 5)
- 🎯 Quick action buttons
- 📈 Payment statistics

### Payments Page
- 📑 Responsive table with all payments
- 🎨 Status badges (color-coded)
- 📅 Date formatting
- 🔍 View button for each payment

### Payment Details Page
- 💰 Payment amount display
- 👤 Client information
- 👨‍💼 Freelancer information
- 🏢 Project information
- ✅ Status update form
- 📝 Remarks field
- 📊 Payment summary

### Contracts Page
- 📋 All contracts table
- 🎨 Status badges
- 💵 Amount display
- 🔍 View button for each contract

### Contract Details Page
- 📄 Complete contract information
- 👤 Client details
- 👨‍💼 Freelancer details
- 🏢 Project details
- 📊 Status summary
- ⏱️ Timeline view

## Payment Flow Management

The admin can:
1. **View** all payments and contracts
2. **Monitor** payment status (PENDING, COMPLETED, FAILED, REFUNDED)
3. **Update** payment status manually
4. **Add remarks** for documentation
5. **Track** complete payment flow from escrow creation to completion
6. **Verify** contract information

## Integration Points

The admin section integrates with:
- **EscrowServices:** For payment/escrow data
- **ContractService:** For contract data
- **UserService:** For user authentication
- **CommonMethodService:** For authentication checks

## Styling

All pages use:
- **Tailwind CSS** for responsive design
- **Color-coded status badges** for quick visual reference
- **Gradient headers** for professional appearance
- **Card-based layout** for better organization
- **Mobile-responsive** design (works on all devices)

## Future Enhancements

Possible additions:
- 📊 Advanced payment analytics/charts
- 🔍 Payment search and filtering
- 📈 Payment reports export (PDF/CSV)
- 🔔 Payment notifications
- 💳 Refund processing system
- 📱 Mobile app admin panel
- 🔐 Two-factor authentication for admin
- 📜 Audit logs for all admin actions

## Notes

- Only users with explicit `ROLE_ADMIN` can access
- All payment updates are logged
- User cannot access if not authenticated
- User cannot access if role is not ROLE_ADMIN
- All data is pulled from database in real-time
- Payment status updates are saved to database
