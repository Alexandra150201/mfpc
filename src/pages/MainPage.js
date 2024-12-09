import React, { useState } from 'react';
import axios from 'axios';
import './MainPage.css';

const MainPage = () => {
    const [userName, setUserName] = useState('');
    const [userId, setUserId] = useState(null);
    const [loyaltyPoints, setLoyaltyPoints] = useState(null);
    const [message, setMessage] = useState('');
    const [roomNumber, setRoomNumber] = useState('');
    const [checkInDate, setCheckInDate] = useState('');
    const [checkOutDate, setCheckOutDate] = useState('');
    const [reservations, setReservations] = useState([]);
    const [selectedReservationId, setSelectedReservationId] = useState(null);
    const [invoices, setInvoices] = useState([]);
    const [paymentAmount, setPaymentAmount] = useState('');
    const [feedbackComment, setFeedbackComment] = useState('');
    const [rating, setRating] = useState(0); // Rating state to store the number of stars


    // Handle user connection
    const handleConnectUser = async () => {
        if (!userName.trim()) {
            alert('Please enter a username');
            return;
        }
        try {
            const response = await axios.post('http://localhost:8080/api/user', { name: userName });

            // Update state with the returned user data
            const user = response.data;

            setUserId(user.id); // Ensure userId matches the response structure
            setLoyaltyPoints(user.loyaltyPoints);
            setMessage(`Hello ${user.name}`); // Use the name from the response for consistency

        } catch (error) {
            if (error.response && error.response.status === 404) {
                alert('User not found!');
            } else {
                alert('Error connecting user!');
            }
        }
    };

    // Create reservation
    const handleCreateReservation = async () => {
        if (!userId) {
            alert('Please connect a user first.');
            return;
        }
        try {
            await axios.post('http://localhost:8080/api/reservation', {
                userId:userId,
                name:userName,
                loyaltyPoints:loyaltyPoints,
                roomNumber:roomNumber,
                checkInDate:checkInDate,
                checkOutDate:checkOutDate
            });
            alert('Reservation created successfully!');
        } catch (error) {
            alert('Error creating reservation!');
        }
    };

    // Fetch invoices
    const handleGetInvoices = async () => {
        if (!userId) {
            alert('Please connect a user first.');
            return;
        }
        try {
            const response = await axios.get(`http://localhost:8080/api/invoices/user/${userId}`);
            setInvoices(response.data);
        } catch (error) {
            setInvoices([]);
            alert('No invoices');
        }
    };

    // Fetch reservations with invoices
    const handleGetReservationsWithInvoices = async () => {
        if (!userId) {
            alert('Please connect a user first.');
            return;
        }
        try {
            const response = await axios.get(`http://localhost:8080/api/reservation/with-invoices/${userId}`);
            setReservations(response.data);
        } catch (error) {
            alert('Error fetching reservations!');
        }
    };

    // Create payment
    const handleCreatePayment = async () => {
        if (!selectedReservationId || !paymentAmount) {
            alert('Please select a reservation and enter an amount.');
            return;
        }
        try {
            await axios.post(`http://localhost:8080/api/payment/${selectedReservationId}/${parseFloat(paymentAmount)}`);
            alert('Payment created successfully!');
        } catch (error) {
            alert('Payment amount is bigger than the invoice amount');
        }
    };

    // Create feedback
    const handleCreateFeedback = async () => {
        if (!userId) {
            alert('Please connect a user first.');
            return;
        }
        else if (feedbackComment.trim() === '') {
            alert('Please enter your feedback');
            return;
        }
        else if (rating === 0) {
            alert('Please select a rating');
            return;
        }
        try {
            await axios.post('http://localhost:8080/api/feedbacks', {
                userId,
                comments: feedbackComment,
                rating: rating
            });
            alert('Feedback submitted successfully!');
            setFeedbackComment('');
        } catch (error) {
            alert('Error submitting feedback!');
        }
    };

    return (
        <div className="main-page">
            <div className="left-side">
                {/* User Connection */}
                <div>
                    <input
                        type="text"
                        placeholder="Enter username"
                        value={userName}
                        onChange={(e) => setUserName(e.target.value)}
                    />
                    <button onClick={handleConnectUser}>Connect User</button>
                    {userId && <h2>{message}</h2>}
                </div>

                {/* Reservation Creation */}
                <div>
                    <h3>Create Reservation</h3>
                    <input
                        type="text"
                        placeholder="Room Number"
                        value={roomNumber}
                        onChange={(e) => setRoomNumber(e.target.value)}
                    />
                    <input
                        type="date"
                        value={checkInDate}
                        onChange={(e) => setCheckInDate(e.target.value)}
                    />
                    <input
                        type="date"
                        value={checkOutDate}
                        onChange={(e) => setCheckOutDate(e.target.value)}
                    />
                    <button onClick={handleCreateReservation}>Create Reservation</button>
                </div>
            </div>

            <div className="right-side">
                {/* View Invoices */}
                <div>
                    <h3>Invoices</h3>
                    <button onClick={handleGetInvoices}>Get Invoices</button>
                    <ul>
                        {invoices.map((invoice) => (
                            <li key={invoice.id}>
                                Invoice ID: {invoice.id}, Amount: {invoice.details}, Invoice Date: {invoice.issueDate}, For reservationId: {invoice.reservationId}
                            </li>
                        ))}
                    </ul>
                </div>

                {/* Manage Reservations and Payments */}
                <div>
                    <h3>Reservations & Payments</h3>
                    <button onClick={handleGetReservationsWithInvoices}>Get Reservations</button>
                    {reservations.length > 0 && (
                        <>
                            <select
                                value={selectedReservationId || ''}
                                onChange={(e) => setSelectedReservationId(e.target.value)}
                            >
                                <option value="">Select Reservation</option>
                                {reservations.map((reservation) => (
                                    <option key={reservation.id} value={reservation.id}>
                                        Reservation ID: {reservation.id}
                                    </option>
                                ))}
                            </select>
                            <input
                                type="number"
                                placeholder="Payment Amount"
                                value={paymentAmount}
                                onChange={(e) => setPaymentAmount(e.target.value)}
                            />
                            <button onClick={handleCreatePayment}>Create Payment</button>
                        </>
                    )}
                </div>

                {/* Feedback Section */}
                <div>
                    <h3>Provide Feedback</h3>
                    {/* Star Rating */}
                    <div>
                        <p>Rating:</p>
                        {[1, 2, 3, 4, 5].map((star) => (
                            <span
                                key={star}
                                style={{
                                    fontSize: '24px',
                                    cursor: 'pointer',
                                    color: star <= rating ? 'gold' : 'gray',
                                }}
                                onClick={() => setRating(star)}
                            >
                                ★
                            </span>
                        ))}
                    </div>

                    {/* Feedback Comment */}
                    <div>
                        <textarea
                            placeholder="Enter your feedback"
                            value={feedbackComment}
                            onChange={(e) => setFeedbackComment(e.target.value)}
                        />
                    </div>

                    {/* Submit Button */}
                    <button onClick={handleCreateFeedback}>Submit Feedback</button>
                </div>
            </div>
        </div>
    );
};

export default MainPage;
