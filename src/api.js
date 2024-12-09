// src/api.js

import axios from 'axios';

// API calls for User and Reservation
export const createUser = (userData) => {
    return axios.post('http://localhost:8080/api/users', userData);
};

export const createReservation = (reservationData) => {
    return axios.post('http://localhost:8080/api/reservations', reservationData);
};

// API calls for Payment and Invoice
export const createPayment = (paymentData) => {
    return axios.post('http://localhost:8080/api/payments', paymentData);
};

export const createInvoice = (invoiceData) => {
    return axios.post('http://localhost:8080/api/invoices', invoiceData);
};

// API call to fetch reward for a user
export const getReward = (userId) => {
    return axios.get(`http://localhost:8080/api/rewards/${userId}`);
};
